package jablab.de.jabor.cardgame.area51.gamepanel.client.ki;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardList;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardType;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardValue;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.SlotList;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * basic KI that may not pass cards
 * 
 * @author development.Jabor
 * 
 */
public class BasicKI implements ClientKI {

	private Timer timer;

	private Client client;
	private CardType trump;
	private CardList cards;
	private SlotList slots;

	private List<Card> tempSet;

	public BasicKI(Client client, CardList cards, SlotList slots) {
		this.client = client;
		this.cards = cards;
		this.slots = slots;
		this.timer = null;
	}

	public void setTrump(CardType trump) {
		this.trump = trump;
	}

	private Card getMinCard(CardValue value) {
		Card minCard = null;
		for (Card card : this.tempSet) {
			// filter value if specified
			if ((value == null) || (card.getValue() == value)) {
				// set first card
				if ((minCard == null)
				// set cards of lower value
						|| ((card.getValue().ordinal() < minCard.getValue()
								.ordinal())
						// that are of same type OR cards that are no trump
						// cards
						&& ((card.getType() == minCard.getType()) || (card
								.getType() != this.trump)))
						// set cards of same value
						|| ((card.getValue() == minCard.getValue())
						// that are of lower type but not trump cards
						&& ((card.getType().ordinal() < minCard.getType()
								.ordinal()) && (card.getType() != this.trump)))) {

					minCard = card;
				}
			}
		}

		return minCard;
	}

	private Card getMinCard(CardValue value, CardType type) {
		Card minCard = null;
		for (Card card : this.tempSet) {
			// set card of same type but higher value that is still lower than
			// current overriding any trump card
			if (((card.getType() == type)
					&& (card.getValue().ordinal() > value.ordinal()) && ((minCard == null)
					|| ((minCard.getType() == this.trump) && (type != this.trump)) || (card
					.getValue().ordinal() < minCard.getValue().ordinal())))

			// set trump cards lower than current trump card if set
					|| (((card.getType() == this.trump) && (type != this.trump) && ((minCard == null) || (card
							.getValue().ordinal() < minCard.getValue()
							.ordinal()))))) {

				minCard = card;

			}
		}

		return minCard;
	}

	private void playCard() {
		// TODO: check if client aborted

		// do not play any card by default
		Card playedCard = null;
		int slotID = -1;
		PlayerRole role = this.client.getRole();

		// create temporary set
		this.tempSet = new ArrayList<Card>(this.cards.size());
		for (Card card : this.cards.getList()) {
			this.tempSet.add(card);
		}

		if ((role == PlayerRole.FIRST_ATTACKER)
				&& (this.slots.numUsedSlots() == 0)) {
			// search for lowest card in hand
			playedCard = this.getMinCard(null);
		} else if ((role == PlayerRole.FIRST_ATTACKER)
				|| ((role == PlayerRole.ATTACKER) && (this.slots.numUsedSlots() != 0))) {
			Card card;

			if (this.slots.numFreeSlots() != 0) {
				for (Slot slot : this.slots.getList()) {
					// search for any card playable
					card = slot.getCard();
					if (card != null) {
						playedCard = this.getMinCard(card.getValue());
						if (playedCard != null) {
							break;
						}

						card = slot.getAntiCard();
						if (card != null) {
							playedCard = this.getMinCard(card.getValue());
							if (playedCard != null) {
								break;
							}
						}
					}
				}
			}
			// TODO: else abort(?)

			// there was no card to play
			if ((playedCard == null)
					&& (this.client.getTargetAborted() || (this.slots
							.numUndefendedSlots() == 0))) {
				// client aborted or all slots defended yet
				this.client.abort();
				return;
			}

		} else if (role == PlayerRole.TARGET) {
			Card card;
			if (this.slots.numUndefendedSlots() > 0) {
				for (Slot slot : this.slots.getList()) {
					if (((card = slot.getCard()) != null)
							&& (slot.getAntiCard() == null)) {

						// search for lowest card beating this undefended slot
						Card minCard = this.getMinCard(card.getValue(),
								card.getType());
						if (playedCard == null) {
							playedCard = minCard;
							slotID = slot.getId();
						}

						// remove this card from temporary set
						this.tempSet.remove(minCard);

						// abort if there is any card that can not be beaten
						if (minCard == null) {
							this.client.abort();
							return;
						}
					}
				}
			}
		}

		if (playedCard != null) {
			this.client.playCard(playedCard.getId(), slotID);
		}
	}

	public void start() {
		if (this.timer == null) {
			this.timer = new Timer();
			this.timer.schedule(new KITask(this), 1000L);
		}
	}

	private void stop() {
		this.timer.cancel();
		this.timer = null;
	}

	private class KITask extends TimerTask {

		private BasicKI ki;

		public KITask(BasicKI ki) {
			super();
			this.ki = ki;
		}

		@Override
		public void run() {
			this.ki.playCard();
			this.ki.stop();
		}

	}

}