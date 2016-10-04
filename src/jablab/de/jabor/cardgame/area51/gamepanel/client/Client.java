package jablab.de.jabor.cardgame.area51.gamepanel.client;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardList;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.RoleTuple;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.SlotList;
import jablab.de.jabor.cardgame.area51.gamepanel.client.ki.BasicKI;
import jablab.de.jabor.cardgame.area51.gamepanel.client.ki.ClientKI;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Commands;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Worker;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerCurrentRoles;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerDrawCard;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerError;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerGameFinished;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerGameStart;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerNewPlayer;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerPlayerPlaysCard;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerRoundFinished;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerTargetAborted;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerVersion;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.client.ServerWelcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

	/**
	 * client version
	 */
	public static final String VERSION = "1.1";

	/**
	 * user interface additionally handling commands
	 */
	private ClientGUI gui;

	/**
	 * thread-safe command queue
	 */
	private BlockingQueue<Command> commandQueue;

	/**
	 * client socket to connect to server
	 */
	private Socket socket;

	/**
	 * incoming communication stream
	 */
	private BufferedReader reader;

	/**
	 * outgoing communication stream
	 */
	private PrintWriter writer;

	/**
	 * player name
	 */
	private String username;

	/**
	 * client identifier assigned
	 */
	private int id;

	/**
	 * index within server list
	 */
	private int ownIndex;

	/**
	 * player list
	 */
	private PlayerList playerList;

	/**
	 * player's hand
	 */
	private CardList cards;

	/**
	 * target player's client identifier
	 */
	private int target;

	/**
	 * player role
	 */
	private PlayerRole role;

	/**
	 * playing slot list
	 */
	private SlotList slots;

	/**
	 * trump card
	 */
	private Card trump;

	/**
	 * game running flag
	 */
	private boolean gameRunning;

	/**
	 * client ki
	 */
	private ClientKI ki;

	/**
	 * target aborted flag
	 */
	private boolean targetAborted = false;

	public Client(ClientGUI gui, String ipAddress, String username, boolean isKI)
			throws UnknownHostException, IOException {
		// create client socket
		this.socket = new Socket(ipAddress, 61111);

		// open communication gateways
		this.reader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
		this.writer = new PrintWriter(this.socket.getOutputStream(), true);

		this.gui = gui;
		this.commandQueue = new LinkedBlockingQueue<Command>();
		this.username = username;
		this.cards = new CardList();
		this.slots = new SlotList();
		this.trump = null;
		if (isKI) {
			this.ki = new BasicKI(this, this.cards, this.slots);
		}

		// start command handling
		final Thread workerThread = new Thread(new Worker(this.commandQueue,
				this.gui));
		workerThread.start();

		// start listening
		final Thread commThread = new Thread(this);
		commThread.start();
	}

	/**
	 * handle server communication
	 */
	public void run() {
		// continuously read messages, parse to and stack commands
		String commandLine;
		try {
			while ((commandLine = this.reader.readLine()) != null) {
				String[] commandParts = commandLine.split(Commands.SEPARATOR);
				this.parseCommand(commandParts);
			}
		} catch (IOException e) {
			this.gui.showError("failed to read line!\n" + e.getMessage());
		}
	}

	/**
	 * shutdown network communication gateways
	 */
	public void close() {
		try {
			this.socket.shutdownInput();
			this.socket.close();
			this.writer.close();
			// this.reader.close();
		} catch (IOException e) {
			this.gui.log("ServerClient|close",
					"failed to clean up!\n" + e.getMessage());
		}
	}

	public int getId() {
		return this.id;
	}

	public int getTargetId() {
		return this.target;
	}

	public PlayerRole getRole() {
		return this.role;
	}

	/**
	 * parse messages to and stack commands
	 * 
	 * @param commandParts
	 *            message's command parts
	 */
	private void parseCommand(String[] commandParts) {
		String command = commandParts[0];
		int numParams = commandParts.length - 1;

		if ((numParams == ServerVersion.PARAMS)
				&& Commands.Server.VERSION.equals(command)) {
			String version = commandParts[1];

			ServerVersion versionCommand = new ServerVersion(this, version);
			this.commandQueue.add(versionCommand);

		} else if ((numParams == ServerError.PARAMS)
				&& Commands.Server.ERROR.equals(command)) {
			int errorCode = Integer.parseInt(commandParts[1]);

			ServerError errorCommand = new ServerError(this, errorCode);
			this.commandQueue.add(errorCommand);

		} else if ((numParams == ServerWelcome.PARAMS)
				&& Commands.Server.WELCOME.equals(command)) {
			int maxPlayers = Integer.parseInt(commandParts[1]);
			int clientID = Integer.parseInt(commandParts[2]);
			int ownIndex = Integer.parseInt(commandParts[3]);

			ServerWelcome welcomeCommand = new ServerWelcome(this, maxPlayers,
					clientID, ownIndex);
			this.commandQueue.add(welcomeCommand);

		} else if ((numParams == ServerNewPlayer.PARAMS)
				&& Commands.Server.NEW_PLAYER.equals(command)) {
			int clientID = Integer.parseInt(commandParts[1]);
			String playerName = commandParts[2];

			ServerNewPlayer newPlayerCommand = new ServerNewPlayer(this,
					clientID, playerName);
			this.commandQueue.add(newPlayerCommand);
		} else if ((numParams == ServerDrawCard.PARAMS)
				&& Commands.Server.DRAW_CARD.equals(command)) {
			int clientID = Integer.parseInt(commandParts[1]);
			int cardID = Integer.parseInt(commandParts[2]);

			ServerDrawCard drawCardCommand = new ServerDrawCard(this, clientID,
					cardID);
			this.commandQueue.add(drawCardCommand);
		} else if ((numParams == ServerGameStart.PARAMS)
				&& Commands.Server.GAME_START.equals(command)) {
			int cardID = Integer.parseInt(commandParts[1]);
			Card card = new Card(cardID);

			ServerGameStart gameStartCommand = new ServerGameStart(this, card);
			this.commandQueue.add(gameStartCommand);
		} else if ((numParams == ServerCurrentRoles.PARAMS)
				&& Commands.Server.CRR_ROLES.equals(command)) {
			int clientID, value;
			PlayerRole role;
			List<RoleTuple> roles = new ArrayList<RoleTuple>(3);
			for (int i = 0; i < 3; i++) {
				clientID = Integer.parseInt(commandParts[1 + (i * 2)]);
				value = Integer.parseInt(commandParts[2 + (i * 2)]);
				role = PlayerRole.getRoleByValue(value);
				roles.add(new RoleTuple(clientID, role));
			}

			ServerCurrentRoles currentRolesCommand = new ServerCurrentRoles(
					this, roles);
			this.commandQueue.add(currentRolesCommand);
		} else if ((numParams == ServerPlayerPlaysCard.PARAMS)
				&& Commands.Server.PLAYER_PLAYS_CARD.equals(command)) {
			int clientID = Integer.parseInt(commandParts[1]);
			int cardID = Integer.parseInt(commandParts[2]);
			int slotID = Integer.parseInt(commandParts[3]);

			ServerPlayerPlaysCard playerPlaysCardCommand = new ServerPlayerPlaysCard(
					this, clientID, cardID, slotID);
			this.commandQueue.add(playerPlaysCardCommand);
		} else if ((numParams == ServerTargetAborted.PARAMS)
				&& Commands.Server.TARGET_ABORTED.equals(command)) {
			ServerTargetAborted targetAbortedCommand = new ServerTargetAborted(
					this);
			this.commandQueue.add(targetAbortedCommand);
		} else if ((numParams == ServerRoundFinished.PARAMS)
				&& Commands.Server.ROUND_FINISHED.equals(command)) {
			boolean targetWon = Boolean.parseBoolean(commandParts[1]);

			ServerRoundFinished roundFinishedCommand = new ServerRoundFinished(
					this, targetWon);
			this.commandQueue.add(roundFinishedCommand);
		} else if (Commands.Server.GAME_FINISHED.equals(command)) {
			int numClients = Integer.parseInt(commandParts[1]);
			List<Integer> winnerIDs = new ArrayList<Integer>(numClients);

			for (int i = 0; i < numClients; i++) {
				winnerIDs.add(Integer.parseInt(commandParts[2 + i]));
			}

			ServerGameFinished gameFinishedCommand = new ServerGameFinished(
					this, winnerIDs);
			this.commandQueue.add(gameFinishedCommand);
		}

		else {
			this.gui.showError("unknown command received: \"" + command
					+ "\" (length: " + numParams + ")");
		}
	}

	public void serverVersion(String version) {
		if (!version.equals(Client.VERSION)) {
			this.gui.showError("Version mismatch!\nClient (" + Client.VERSION
					+ ") <-> Server (" + version + ")");
			this.close();
		} else {
			this.writer.println(Commands.Client.LOGIN + Commands.SEPARATOR
					+ this.username);
		}
	}

	public void serverError(int errorCode) {
		String message = Commands.ErrorCode.parse(errorCode);
		this.gui.showInfo(message);

		if (this.ki != null) {
			this.ki.start();
		}
	}

	public void serverWelcome(int maxPlayers, int clientID, int ownIndex) {
		this.playerList = new PlayerList(maxPlayers - 1);
		this.id = clientID;
		this.ownIndex = ownIndex;
		this.gui.log("serverWelcome", "maxPlayers: " + maxPlayers + ", id: "
				+ this.id + ", index: " + this.ownIndex);
	}

	public void serverNewPlayer(int clientID, String name) {
		Player player = new Player(clientID, name);
		while (this.playerList == null) {

		}
		this.playerList.add(player);
		this.gui.serverNewPlayer(player);
	}

	public void serverDrawCard(int clientID, int cardID) {
		if (clientID == this.id) {
			Card card = new Card(cardID);
			this.cards.add(card);
			this.gui.serverDrawCard(null, card);
		} else {
			Player player = this.playerList.getPlayerById(clientID);
			player.setNumCards(player.getNumCards() + 1);
			this.gui.serverDrawCard(player, null);
		}
	}

	public void serverGameStart(Card trump) {
		this.gameRunning = true;
		this.trump = trump;

		if (this.ki != null) {
			this.ki.setTrump(trump.getType());
		}

		// reset used slots
		this.slots.reset();

		this.gui.serverGameStart(trump);
	}

	public void serverCurrentRoles(List<RoleTuple> roles) {
		this.target = 0;
		this.role = PlayerRole.NORMAL;

		for (Player player : this.playerList.getList()) {
			player.setRole(PlayerRole.NORMAL);
		}

		for (RoleTuple tuple : roles) {
			if (tuple.getClientId() == this.id) {
				this.role = tuple.getRole();

				// set number of slots available
				if (this.role == PlayerRole.TARGET) {
					this.target = this.id;
					this.slots.setNumSlots(this.cards.size());
				}
			} else {
				Player player = this.playerList.getPlayerById(tuple
						.getClientId());
				player.setRole(tuple.getRole());

				// set number of slots available
				if (player.getRole() == PlayerRole.TARGET) {
					this.target = player.getClientId();
					this.slots.setNumSlots(player.getNumCards());
				}
			}
		}

		this.targetAborted = false;
		this.gui.serverCurrentRole(this.role);

		if (this.ki != null) {
			this.ki.start();
		}
	}

	public int playCard(int cardID, int slotID) {
		Card playedCard = this.cards.getCardById(cardID);

		// check if game is running
		if (!this.gameRunning) {
			return Commands.ErrorCode.Play.GAME_NOT_RUNNING;
		}

		// check if card is owned
		if (playedCard == null) {
			return Commands.ErrorCode.Play.CARD_NOT_OWNED;
		}

		if ((this.role == PlayerRole.ATTACKER)
				|| (this.role == PlayerRole.FIRST_ATTACKER)) {
			// check for free slots in general
			if (this.slots.numFreeSlots() > 0) {
				// only first attacker can start a round
				if ((this.slots.numUsedSlots() == 0)
						&& (this.role == PlayerRole.ATTACKER)) {
					return Commands.ErrorCode.Play.FIRST_ATTACKER_ONLY;
				}

				// every other player/card has to be played already
				else if (((this.slots.numUsedSlots() != 0) || (this.role == PlayerRole.ATTACKER))
						&& !this.slots.containsValue(playedCard.getValue())) {
					return Commands.ErrorCode.Play.CARD_NOT_ALLOWED;
				}
			} else {
				return Commands.ErrorCode.Play.SLOTS_FULL;
			}
		} else if (this.role == PlayerRole.TARGET) {
			Slot slot = this.slots.get(slotID);
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
				else if (playedCard.getType() != this.trump.getType()) {
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
				Player nextPlayer = this.playerList.getNextPlayer();
				if ((nextPlayer != null)
						&& (nextPlayer.getNumCards() < (this.slots
								.numUsedSlots() + 1))) {
					return Commands.ErrorCode.Play.PASSING_NOT_ENOUGH_CARDS;
				}
			}
		} else {
			// role does not match conditions
			return Commands.ErrorCode.Play.CLIENT_NOT_ALLOWED;
		}

		// card played successfully
		this.writer.println(Commands.Client.PLAY_CARD + Commands.SEPARATOR
				+ cardID + Commands.SEPARATOR + slotID);
		return Commands.ErrorCode.NO_ERROR;
	}

	public void serverPlayerPlaysCard(int clientID, int cardID, int slotID) {
		Card playedCard;
		Player player = null;

		if (clientID == this.id) {
			playedCard = this.cards.getCardById(cardID);
			this.cards.remove(playedCard);
		} else {
			playedCard = new Card(cardID);
			player = this.playerList.getPlayerById(clientID);
			player.setNumCards(player.getNumCards() - 1);
		}

		Slot slot = this.slots.get(slotID);
		if (slot.getCard() == null) {
			this.slots.setCard(slotID, playedCard);
		} else {
			this.slots.setAntiCard(slotID, playedCard);
		}

		this.gui.serverPlayerPlaysCard(player, playedCard, slot);

		if (this.ki != null) {
			this.ki.start();
		}
	}

	public void serverTargetAborted() {
		this.targetAborted = true;
		this.gui.serverTargetAborted(this.playerList.getPlayerById(this.target)
				.getName());

		if (this.ki != null) {
			this.ki.start();
		}
	}

	public void abort() {
		this.writer.println(Commands.Client.PLAY_ABORT);
	}

	public void serverRoundFinished(boolean targetWon) {
		if ((this.target != this.id) && !targetWon) {
			// fill target hand with slot cards
			Player target = this.playerList.getPlayerById(this.target);
			target.setNumCards((target.getNumCards() + (this.slots
					.numUsedSlots() * 2)) - this.slots.numUndefendedSlots());
		}
		this.slots.reset();

		this.gui.serverRoundFinished(targetWon);
	}

	public void serverGameFinished(List<Integer> winnerIDs) {
		this.gameRunning = false;

		List<Player> winners = new ArrayList<Player>(winnerIDs.size());
		for (Integer winnerID : winnerIDs) {
			if (winnerID != this.id) {
				winners.add(this.playerList.getPlayerById(winnerID));
			} else {
				winners.add(new Player(this.id, this.username));
			}
		}

		this.gui.serverGameFinished(winners);
	}

	public boolean getTargetAborted() {
		return this.targetAborted;
	}
}