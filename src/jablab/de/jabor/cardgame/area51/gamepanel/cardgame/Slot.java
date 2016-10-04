package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

/**
 * playing slot
 * 
 * @author development.Jabor
 * 
 */
public class Slot {

	/**
	 * slot identifier
	 */
	private int id;

	/**
	 * card being hold
	 */
	private Card card;

	/**
	 * defending card
	 */
	private Card antiCard;

	public Slot(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public Card getCard() {
		return this.card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Card getAntiCard() {
		return this.antiCard;
	}

	public void setAntiCard(Card antiCard) {
		this.antiCard = antiCard;
	}

}