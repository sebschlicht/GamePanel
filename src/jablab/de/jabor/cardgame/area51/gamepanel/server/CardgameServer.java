package jablab.de.jabor.cardgame.area51.gamepanel.server;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardType;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.RoleTuple;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.SlotList;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class CardgameServer {

	private ClientList clients;

	/**
	 * game running flag
	 */
	private boolean gameRunning;

	/**
	 * index of target client
	 */
	private int target;

	/**
	 * instances of attacking clients
	 */
	private ArrayList<ServerClient> attackingClients;

	/**
	 * instance of first attacking client
	 */
	private ServerClient firstAttacker;

	private ArrayList<Card> cards;

	private SlotList slots;

	private CardType trump;

	private List<Integer> winnerIDs;

	/**
	 * create a new cardgame server
	 * 
	 * @param clients
	 *            client list
	 */
	public CardgameServer(ClientList clients) {
		this.clients = clients;
		this.attackingClients = new ArrayList<ServerClient>(2);
		this.slots = new SlotList();
	}

	/**
	 * get flag to check for joins
	 * 
	 * @return game running flag
	 */
	public boolean isGameRunning() {
		return this.gameRunning;
	}

	private static long getCrrMs() {
		return new Date().getTime();
	}

	/**
	 * start the game initializing all variables
	 */
	public void startGame() {
		this.gameRunning = true;

		int numCards = 32;
		int cardsPerPlayer = 6;

		// initialize winner list
		this.winnerIDs = new ArrayList<Integer>(this.clients.numClients());

		// create sorted card stack
		ArrayList<Card> cards = new ArrayList<Card>(numCards);
		for (int i = 0; i < numCards; i++) {
			cards.add(new Card(i + 1));
		}

		// create randomized card stack
		Random random = new Random();
		this.cards = new ArrayList<Card>(numCards);
		for (int i = cards.size(); i > 0; i--) {
			this.cards.add(cards.remove(random.nextInt(i)));
		}

		// calculate trump
		int numClients = this.clients.numClients();
		int cardsDrawn = numClients * cardsPerPlayer;
		Card trump;

		if (cardsDrawn >= numCards) {
			int cardsLeft = numCards % numClients;
			cardsLeft = (cardsLeft != 0) ? cardsLeft : numClients;
			cardsDrawn = numCards - cardsLeft;
		}
		trump = this.cards.get(cardsDrawn);
		this.trump = trump.getType();

		// deal cards
		int clientID;
		Card card;
		int cardID;
		int crrIndex;
		int lowestTrump = numCards + 1;
		int firstIndex = 0;
		long crrMs;

		for (int i = 0; i < cardsPerPlayer; i++) {
			// check if there are enough cards left
			if (this.cards.size() <= numClients) {
				break;
			}

			crrIndex = 0;
			for (ServerClient client : this.clients.getList()) {
				clientID = client.getId();
				card = this.cards.remove(0);
				cardID = card.getId();

				// check for lowest trump
				if ((cardID < lowestTrump)
						&& (CardType.getTypeById(cardID) == trump.getType())) {
					lowestTrump = cardID;
					firstIndex = crrIndex;
				}

				for (ServerClient informClient : this.clients.getList()) {
					if (informClient != client) {
						informClient.sendDrawCard(clientID, 0);
					} else {
						informClient.addCard(card);
					}
				}

				// sleep for 250ms
				crrMs = CardgameServer.getCrrMs() + 250;
				while (CardgameServer.getCrrMs() < crrMs) {
				}

				crrIndex += 1;
			}
		}

		// publish trump
		for (ServerClient informClient : this.clients.getList()) {
			informClient.sendGameStart(trump);
		}

		// DEBUG
		firstIndex = 0;

		// initialize client pointers
		this.target = this.clients.getNextIndex(firstIndex);
		this.attackingClients.add(this.clients.get(firstIndex));
		int secondAttacker = this.clients.getNextIndex(this.target);
		this.attackingClients.add(this.clients.get(secondAttacker));
		this.firstAttacker = this.attackingClients.get(0);

		// DEBUG
		System.out.println("trump: " + trump);
		System.out.println("lowest trump: " + lowestTrump);
		System.out.println("firstAttacker: #" + this.firstAttacker.getId());

		System.out.println("target: " + this.target + " (#"
				+ this.clients.get(this.target).getId() + ")");
		System.out.println("attackingClients: #"
				+ this.attackingClients.get(0).getId() + ", #"
				+ this.attackingClients.get(1).getId());

		// reset used slots
		this.slots.reset();

		// inform clients about their status
		this.publishPlayerRoles(true);
	}

	/**
	 * step to the next target<br>
	 * <br>
	 * <b>Sets:</b><br>
	 * target, attackingClients, firstAttacker<br>
	 * <br>
	 * <b>Calls:</b><br>
	 * publishPlayerRoles
	 * 
	 * @param targetWon
	 *            previous target won flag
	 */
	private void nextTarget(boolean targetWon, boolean roundFinished) {
		int firstAttacker;
		if (targetWon) {
			firstAttacker = this.clients.thisOrNextIndex(this.target);
		} else {
			firstAttacker = this.clients.getNextIndex(this.target);
		}

		// set first attacker calculated
		this.firstAttacker = this.clients.get(firstAttacker);
		this.attackingClients.set(0, this.firstAttacker);

		// target is first attacker's next client
		this.target = this.clients.getNextIndex(firstAttacker);

		// second attacker is target's next client
		int secondAttacker = this.clients.getNextIndex(this.target);
		this.attackingClients.set(1, this.clients.get(secondAttacker));

		// reset abortion flag
		this.clients.get(this.target).setAborted(false);
		this.attackingClients.get(0).setAborted(false);
		this.attackingClients.get(1).setAborted(false);

		// publish player roles
		this.publishPlayerRoles(roundFinished);
	}

	private void publishPlayerRoles(boolean roundFinished) {
		// update slots available
		ServerClient targetClient = this.clients.get(this.target);
		this.slots.setNumSlots(targetClient.getNumCards());

		List<RoleTuple> roles = new ArrayList<RoleTuple>(3);

		roles.add(new RoleTuple(targetClient.getId(), PlayerRole.TARGET));
		roles.add(new RoleTuple(this.attackingClients.get(1).getId(),
				PlayerRole.ATTACKER));
		roles.add(new RoleTuple(this.firstAttacker.getId(),
				roundFinished ? PlayerRole.FIRST_ATTACKER : PlayerRole.ATTACKER));

		for (ServerClient informClient : this.clients.getList()) {
			informClient.sendCurrentRoles(roles);
		}
	}

	/**
	 * 
	 * @param client
	 */
	public void clientDisconnect(ServerClient client) {
		int clientID = client.getId();

		if (!this.clients.remove(client)) {
			client.sendError(Commands.ErrorCode.Disconnect.NOT_LOGGED_IN);
			return;
		} else if (!this.checkGameState(null)) {
			// game has been finished
			return;
		}

		if (clientID == this.clients.get(this.target).getId()) {
			this.nextTarget(false, true);
		} else {
			int index = this.attackingClients.indexOf(client);

			if (index == 0) {
				int previousClient = this.clients.getPreviousIndex(this.target);
				this.attackingClients.set(0, this.clients.get(previousClient));
				this.firstAttacker = this.attackingClients.get(1);
			} else if (index == 1) {
				int nextClient = this.clients.getNextIndex(this.target);
				this.attackingClients.set(1, this.clients.get(nextClient));
			}
		}
	}

	private int getPlayCardError(ServerClient client, Card playedCard, Slot slot) {
		// check if game is running
		if (!this.gameRunning) {
			return Commands.ErrorCode.Play.GAME_NOT_RUNNING;
		}

		// check if card is owned
		if (playedCard == null) {
			return Commands.ErrorCode.Play.CARD_NOT_OWNED;
		}

		if (this.attackingClients.contains(client)) {
			// check if client aborted
			if (client.hasAborted()) {
				return Commands.ErrorCode.Play.CLIENT_ABORTED;
			}

			// check if slot has been set before
			if (slot != null) {
				return Commands.ErrorCode.Play.SLOT_INVALID;
			}

			// check for free slots in general
			if (this.slots.numFreeSlots() > 0) {
				// only first attacker can start a round
				if ((this.slots.numUsedSlots() == 0)
						&& (this.attackingClients.indexOf(client) == 1)) {
					return Commands.ErrorCode.Play.FIRST_ATTACKER_ONLY;
				}

				// every other card has to be played already
				else if ((this.slots.numUsedSlots() != 0)
						&& !this.slots.containsValue(playedCard.getValue())) {
					return Commands.ErrorCode.Play.CARD_NOT_ALLOWED;
				}
			} else {
				return Commands.ErrorCode.Play.SLOTS_FULL;
			}
		} else if (this.clients.get(this.target) == client) {
			// check if client aborted
			if (client.hasAborted()) {
				return Commands.ErrorCode.Play.CLIENT_ABORTED;
			}

			// TODO: check this only when defending
			// check if slot is not set when attacking

			// check for valid slot
			if (slot == null) {
				return Commands.ErrorCode.Play.SLOT_INVALID;
			}

			Card attackingCard = slot.getCard();

			// defend slot
			if (attackingCard != null) {
				// slot must not have been defended already
				if (slot.getAntiCard() != null) {
					return Commands.ErrorCode.Play.SLOT_DEFENDED;
				}

				// check for matching card types
				if (playedCard.getType() == attackingCard.getType()) {
					// card value has to be higher
					if (playedCard.getValue().getValue() < attackingCard
							.getValue().getValue()) {
						return Commands.ErrorCode.Play.CARD_TOO_LOW;
					}
				}

				// mismatching card types other than trump not allowed
				else if (playedCard.getType() != this.trump) {
					return Commands.ErrorCode.Play.CARD_MISMATCH;
				}
			}

			// attack next player
			else {
				// only first attacker can start a round
				if (this.slots.numUsedSlots() == 0) {
					return Commands.ErrorCode.Play.FIRST_ATTACKER_ONLY;
				}

				// all slots must be undefended and of same value
				if (!this.slots.canPass(playedCard.getValue())) {
					return Commands.ErrorCode.Play.PASSING_NOT_ALLOWED;
				}

				// the next player must have enough cards to pass
				if (this.clients.get(this.clients.getNextIndex(this.target))
						.getNumCards() < (this.slots.numUsedSlots() + 1)) {
					return Commands.ErrorCode.Play.PASSING_NOT_ENOUGH_CARDS;
				}
			}
		} else {
			// role does not match conditions
			return Commands.ErrorCode.Play.CLIENT_NOT_ALLOWED;
		}

		return Commands.ErrorCode.NO_ERROR;
	}

	public void clientPlayCard(ServerClient client, int cardID, int slotID) {
		Card card = client.getCardInHand(cardID);
		Slot slot = this.slots.get(slotID);

		int playCardError = this.getPlayCardError(client, card, slot);
		if (playCardError == Commands.ErrorCode.NO_ERROR) {
			// chose next free slot
			if (slot == null) {
				slotID = this.slots.numUsedSlots();
				slot = this.slots.get(slotID);

				if (slot == null) {
					Log.d("CServer|clientPlayCard", "999!");
					client.sendError(999);
					return;
				}
			}

			for (ServerClient informClient : this.clients.getList()) {
				informClient
						.sendPlayerPlaysCard(client.getId(), cardID, slotID);
			}

			// remove card from player
			client.removeCard(card);

			// check if game still running
			if (!this.checkGameState(client)) {
				// broadcast round finished
				for (ServerClient informClient : this.clients.getList()) {
					informClient.sendRoundFinished(client == this.clients
							.get(this.target));
				}

				// broadcast game finished
				for (ServerClient informClient : this.clients.getList()) {
					informClient.sendGameFinished(this.winnerIDs);
				}

				// reset game server
				this.slots.reset();
				this.gameRunning = false;
				return;
			}

			// play card
			if (slot.getCard() == null) {
				this.slots.setCard(slotID, card);

				if (client == this.clients.get(this.target)) {
					// target passes
					this.nextTarget(true, false);
				} else if ((this.slots.numFreeSlots() == 0)
						&& this.clients.get(this.target).hasAborted()) {
					// slots are full and target does not want to defend
					this.nextRound(false);
				} else {
					// next round if current player played last card and all
					// other players aborted before
					this.checkRoundState();
				}
			} else {
				this.slots.setAntiCard(slotID, card);

				// target defended all slots
				if ((this.slots.numFreeSlots() == 0)
						&& (this.slots.numUndefendedSlots() == 0)) {
					this.nextRound(true);
				}
			}
		} else {
			Log.d("server|playCard", "error: " + playCardError + " by "
					+ client.getId());
			client.sendError(playCardError);
		}
	}

	public void clientAbort(ServerClient client) {
		// filter aborted clients
		if (client.hasAborted()) {
			client.sendError(Commands.ErrorCode.Play.CLIENT_ABORTED);
			return;
		}

		// filter attacking clients that have not initialized the round
		if ((this.attackingClients.get(0) == client)
				&& (this.slots.numUsedSlots() == 0)) {
			client.sendError(Commands.ErrorCode.Play.ROUND_NOT_INITIALIZED);
		}

		// check if client is target client
		if (this.clients.get(this.target) == client) {
			client.setAborted(true);

			// publish target aborted
			for (ServerClient informClient : this.clients.getList()) {
				if (informClient != client) {
					informClient.sendTargetAborted();
				}
			}
			this.checkRoundState();
		} else if (this.attackingClients.contains(client)) {
			client.setAborted(true);
			this.checkRoundState();
		} else {
			client.sendError(Commands.ErrorCode.Play.CLIENT_NOT_ALLOWED);
		}
	}

	private void nextRound(boolean targetWon) {
		// broadcast round finished
		for (ServerClient informClient : this.clients.getList()) {
			informClient.sendRoundFinished(targetWon);
		}

		this.refillClientCards();

		this.slots.reset();
		this.nextTarget(targetWon, true);
	}

	private void refillClientCards() {
		boolean cardsWereLeft = (this.cards.size() > 0);
		int firstAttacker = this.clients.getPreviousIndex(this.target);
		int i = firstAttacker;
		ServerClient client;
		Card card;

		do {
			client = this.clients.get(i);
			for (int numCards = client.getNumCards(); (numCards < 6)
					&& (this.cards.size() > 0); numCards++) {
				card = this.cards.remove(0);
				client.addCard(card);

				for (ServerClient informClient : this.clients.getList()) {
					if (informClient != client) {
						informClient.sendDrawCard(client.getId(), card.getId());
					}
				}
			}

			i = this.clients.getNextIndex(i);
		} while (i != firstAttacker);

		// set player state to finished when they got no cards
		if (cardsWereLeft && (this.cards.size() == 0)) {
			for (ServerClient finishedClient : this.clients.getList()) {
				if (!finishedClient.hasFinished()
						&& (finishedClient.getNumCards() == 0)) {
					finishedClient.setFinished(true);
				}
			}
		}
	}

	private void checkRoundState() {
		// check if all attacking clients aborted the round
		boolean allAttackersAborted = true;
		for (ServerClient attackingClient : this.attackingClients) {
			if (!attackingClient.hasAborted() && !attackingClient.hasFinished()) {
				allAttackersAborted = false;
				break;
			}
		}

		ServerClient target = this.clients.get(this.target);

		// all attacking clients aborted their moves
		if (allAttackersAborted
				|| (target.hasAborted() && (this.slots.numFreeSlots() == 0))) {
			// if target aborted fill target hand
			if (target.hasAborted()) {
				for (Slot slot : this.slots.getList()) {
					Card card = slot.getCard();
					if (card != null) {
						target.addCard(card);

						card = slot.getAntiCard();
						if (card != null) {
							target.addCard(card);
						}
					}
				}
			}

			// next round
			this.nextRound(!target.hasAborted());
		}
	}

	private boolean checkGameState(ServerClient client) {
		if ((client == null)
				|| ((this.cards.size() == 0) && (client.getNumCards() == 0))) {
			if (client != null) {
				// player finished
				client.setFinished(true);
				this.winnerIDs.add(client.getId());
			} else {
				// a player left the game
			}

			// search for last playing player
			ServerClient loser = null;
			for (ServerClient otherClient : this.clients.getList()) {
				if (!otherClient.hasFinished()) {
					if (loser == null) {
						loser = otherClient;
					} else {
						loser = null;
						break;
					}
				}
			}

			// check for game end
			if (loser != null) {
				this.winnerIDs.add(loser.getId());
				return false;
			}
		}

		return true;
	}
}