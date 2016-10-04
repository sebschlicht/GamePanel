package jablab.de.jabor.cardgame.area51.gamepanel.graphics.slotPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Coordinates;

/**
 * slot to play cards to
 * 
 * @author development.Jabor
 * 
 */
public class Slot {
	// TODO: connect Slot to cardgame.Slot

	/**
	 * slot identifier
	 */
	private int id;

	/**
	 * slot's upper left corner
	 */
	private Coordinates coordinates;

	/**
	 * slot enabled flag
	 */
	private boolean enabled;

	/**
	 * slot hovered flag
	 */
	private boolean hovered;

	/**
	 * card set to slot
	 */
	private Card card;

	/**
	 * card defending the slot
	 */
	private Card antiCard;

	/**
	 * create new playing slot
	 * 
	 * @param left
	 *            left screen distance
	 * @param top
	 *            top screen distance
	 */
	public Slot(int id, int left, int top) {
		this.id = id;
		this.coordinates = new Coordinates(left, top);
	}

	/**
	 * identify slot
	 * 
	 * @return slot identifier
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * provide slot bounds for drawing reasons
	 * 
	 * @return left screen distance
	 */
	public float getLeft() {
		return this.coordinates.getX();
	}

	/**
	 * provide slot bounds for drawing reasons
	 * 
	 * @return top screen distance
	 */
	public float getTop() {
		return this.coordinates.getY();
	}

	/**
	 * provide current state for game logic
	 * 
	 * @return enabled flag
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * change slot state
	 * 
	 * @param enabled
	 *            enabled flag
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * provide hovering state for drawing and game logic
	 * 
	 * @return hovered flag
	 */
	public boolean isHovered() {
		return this.hovered;
	}

	/**
	 * change hovering state
	 * 
	 * @param hovered
	 *            hovered flag
	 */
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	/**
	 * provide card for drawing reasons
	 * 
	 * @return card set to slot
	 */
	public Card getCard() {
		return this.card;
	}

	/**
	 * change card
	 * 
	 * @param card
	 *            card set to slot
	 */
	public void setCard(Card card) {
		this.card = card;
	}

	/**
	 * provide anti card for drawing reasons
	 * 
	 * @return card defending the slot
	 */
	public Card getAntiCard() {
		return this.antiCard;
	}

	/**
	 * change anti card
	 * 
	 * @param antiCard
	 *            card defending the slot
	 */
	public void setAntiCard(Card antiCard) {
		this.antiCard = antiCard;
	}

}