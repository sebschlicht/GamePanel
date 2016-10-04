package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

import java.util.List;
import java.util.Vector;

public class CardList {

	private Vector<Card> cards;

	public CardList() {
		this.cards = new Vector<Card>();
	}

	public int size() {
		return this.cards.size();
	}

	public void add(Card card) {
		this.cards.add(card);
	}

	public void remove(Card card) {
		this.cards.remove(card);
	}

	public Card getCardById(int cardID) {
		for (Card card : this.cards) {
			if (card.getId() == cardID) {
				return card;
			}
		}

		return null;
	}

	public List<Card> getList() {
		return this.cards;
	}

}