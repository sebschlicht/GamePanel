package jablab.de.jabor.cardgame.area51.gamepanel.graphics.handPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Coordinates;

/**
 * card slot holding a single card
 * 
 * @author development.Jabor
 * 
 */
public class CardSlot {

	// TODO
	// delete center coordinates using card dimension
	// when changing card movement handling function in hand panel

	/**
	 * slot's upper left corner
	 */
	private Coordinates coordinates;

	/**
	 * slot's center coordinates
	 */
	private Coordinates center;

	/**
	 * card being hold
	 */
	private Card card;

	/**
	 * visible card width<br>
	 * used for overlapping
	 */
	private float visibleWidth;

	/**
	 * create new card slot
	 * 
	 * @param coordinates
	 *            slot's upper left corner
	 * @param center
	 *            slot's center coordinates
	 * @param card
	 *            card to hold
	 */
	public CardSlot(Coordinates coordinates, Coordinates center, Card card) {
		this.coordinates = coordinates;
		this.center = center;
		this.setCard(card);
	}

	/**
	 * provide slot bounds for card movement
	 * 
	 * @return left screen distance
	 */
	public float getLeft() {
		return this.coordinates.getX();
	}

	/**
	 * change slot position
	 * 
	 * @param left
	 *            left screen distance
	 * @param width
	 *            slot width to calculate center coordinates
	 */
	public void setLeft(float left, int width) {
		this.coordinates.setX(left);
		this.center.setX(left + (width / 2));
	}

	/**
	 * provide slot bounds for card movement
	 * 
	 * @return top screen distance
	 */
	public float getTop() {
		return this.coordinates.getY();
	}

	/**
	 * provide center for card reset
	 * 
	 * @return center's left screen distance
	 */
	public float getX() {
		return this.center.getX();
	}

	/**
	 * provide center for card reset
	 * 
	 * @return center's top screen distance
	 */
	public float getY() {
		return this.center.getY();
	}

	/**
	 * provide card for drawing and movement
	 * 
	 * @return card being hold
	 */
	public Card getCard() {
		return this.card;
	}

	/**
	 * change card being hold when user changes card order
	 * 
	 * @param card
	 *            card being hold
	 */
	public void setCard(Card card) {
		this.card = card;
		this.card.setSlot(this);
	}

	/**
	 * reset card bounds to slot bounds
	 */
	public void resetCard() {
		this.card.setX(this.center.getX());
		this.card.setY(this.center.getY());
	}

	/**
	 * provide visible width for drawing reasons
	 * 
	 * @return visible card width
	 */
	public float getVisibleWidth() {
		return this.visibleWidth;
	}

	/**
	 * update visible width when overlapping changes
	 * 
	 * @param visibleWidth
	 *            visible card width
	 */
	public void setVisibleWidth(float visibleWidth) {
		this.visibleWidth = visibleWidth;
	}

}