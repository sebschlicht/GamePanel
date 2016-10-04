package jablab.de.jabor.cardgame.area51.gamepanel.graphics.handPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Coordinates;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.GamePanel;

import java.util.Vector;

import android.graphics.Canvas;

/**
 * screen area representing the player's card hand
 * 
 * @author development.Jabor
 * 
 */
public class HandPanel {

	/**
	 * margin around card slots
	 */
	private static final int SLOT_MARGIN = 10;

	/**
	 * width of the hand panel
	 */
	private int width;

	/**
	 * height of the hand panel
	 */
	private int height;

	/**
	 * panel's distance to left screen border
	 */
	private int left;

	/**
	 * panel's distance to upper screen border
	 */
	private int top;

	/**
	 * card slots holding the cards
	 */
	private Vector<CardSlot> slots;

	/**
	 * current card slot width
	 */
	private int slotWidth;

	/**
	 * current card slot height
	 */
	private int slotHeight;

	/**
	 * card that is currently moved
	 */
	private Card movedCard;

	/**
	 * create a new hand panel
	 * 
	 * @param width
	 *            panel width
	 * @param height
	 *            panel height
	 * @param left
	 *            distance to left screen border
	 * @param top
	 *            distance to upper screen border
	 */
	public HandPanel(int width, int height, int left, int top) {
		this.width = width;
		this.height = height;
		this.left = left;
		this.top = top;
		this.slots = new Vector<CardSlot>();

		this.slotHeight = this.height - (2 * SLOT_MARGIN);
		this.slotWidth = (int) (this.slotHeight * GamePanel.SLOT_RATIO);
	}

	/**
	 * provide slot width for texture scaling
	 * 
	 * @return slot width
	 */
	public int getSlotWidth() {
		return this.slotWidth;
	}

	/**
	 * provide slot height for texture scaling
	 * 
	 * @return slot height
	 */
	public int getSlotHeight() {
		return this.slotHeight;
	}

	/**
	 * provide moved card to play it
	 * 
	 * @return card being moved
	 */
	public Card getMovedCard() {
		return this.movedCard;
	}

	/**
	 * provide number of cards for slot count
	 * 
	 * @return number of card on the hand
	 */
	public int getNumCards() {
		return this.slots.size();
	}

	/**
	 * draw static content of the hand panel
	 * 
	 * @param canvas
	 *            canvas to draw to
	 */
	public void paint(Canvas canvas) {
		// draw static cards
		Card card;
		for (CardSlot slot : this.slots) {
			card = slot.getCard();
			if (card != this.movedCard) {
				canvas.drawBitmap(card.getTexture(),
						card.getX() - (card.getWidth() / 2), card.getY()
								- (card.getHeight() / 2), null);
			}
		}

		// draw moved card at last if set
		if (this.movedCard != null) {
			card = this.movedCard;
			canvas.drawBitmap(card.getTexture(), card.getX()
					- (card.getWidth() / 2), card.getY()
					- (card.getHeight() / 2), null);
		}
	}

	/**
	 * update position and angle of all slots
	 */
	private void updateSlots() {
		int numSlots = this.slots.size();
		float crrLeft = SLOT_MARGIN;
		CardSlot slot;

		if (((numSlots * (this.slotWidth + SLOT_MARGIN)) + SLOT_MARGIN) <= this.width) {
			// card do not need to overlap
			crrLeft = (this.width - (numSlots * (this.slotWidth + SLOT_MARGIN)) - SLOT_MARGIN) / 2;

			for (int i = 0; i < numSlots; i++) {
				slot = this.slots.get(i);
				slot.setLeft(this.left + crrLeft
						+ (i * (this.slotWidth + SLOT_MARGIN)), this.slotWidth);
				slot.setVisibleWidth(this.slotWidth);
				if (slot.getCard() != this.movedCard) {
					slot.resetCard();
				}
			}
		} else {
			// card overlap
			float availableSpace = this.width - (2 * SLOT_MARGIN)
					- this.slotWidth;
			float perSlot = availableSpace / (numSlots - 1);
			crrLeft = (this.width - ((numSlots - 1) * perSlot) - this.slotWidth) / 2;

			for (int i = 0; i < numSlots; i++) {
				slot = this.slots.get(i);
				slot.setLeft(this.left + crrLeft + (i * perSlot),
						this.slotWidth);
				if (i != (numSlots - 1)) {
					slot.setVisibleWidth(perSlot);
				} else {
					slot.setVisibleWidth(this.slotWidth);
				}
				if (slot.getCard() != this.movedCard) {
					slot.resetCard();
				}
			}
		}
	}

	/**
	 * add a card to the hand
	 * 
	 * @param card
	 *            card object
	 * @param resetCard
	 *            location reset flag
	 */
	public void addCard(Card card, boolean resetCard) {
		// add new slot
		int slotTop = this.top + SLOT_MARGIN;
		CardSlot slot = new CardSlot(new Coordinates(0, slotTop),
				new Coordinates(this.slotWidth / 2, slotTop
						+ (this.slotHeight / 2)), card);
		this.slots.add(slot);

		// correct slot positions
		this.updateSlots();

		if (resetCard) {
			slot.resetCard();
		}
	}

	/**
	 * remove a card from the hand
	 * 
	 * @param cardID
	 *            card identifier
	 * @return card - card that has been removed<br>
	 *         null - card has not been found
	 */
	public Card removeCardById(int cardID) {
		for (CardSlot slot : this.slots) {
			if (slot.getCard().getId() == cardID) {
				this.slots.remove(slot);
				this.updateSlots();
				return slot.getCard();
			}
		}

		return null;
	}

	/**
	 * load card that can me moved by cursor
	 * 
	 * @param x
	 *            x-axis value of cursor position
	 * @param y
	 *            y-axis value of cursor position
	 */
	public void prepareCardForMovement(float x, float y) {
		y -= this.top;

		if ((y < SLOT_MARGIN) || (y > (this.slotHeight + SLOT_MARGIN))
				|| (x < this.left) || (x > (this.left + this.width))) {
			return;
		}

		for (CardSlot slot : this.slots) {
			if ((x > slot.getLeft())
					&& (x < (slot.getLeft() + slot.getVisibleWidth()))) {
				this.movedCard = slot.getCard();
				return;
			}
		}
	}

	/**
	 * unload card that can be moved by cursor
	 */
	public void unprepareCard() {
		if (this.movedCard != null) {
			this.movedCard.reset();
			this.movedCard = null;
		}
	}

	/**
	 * handle movement of card
	 * 
	 * @param card
	 *            card that has been moved
	 * @return true - card movement has been handled<br>
	 *         false - card out of panel bounds
	 */
	public boolean handleMovement(Card card) {
		if ((card.getX() < this.left) || (card.getY() < this.top)
				|| (card.getX() > (this.left + this.width))
				|| (card.getY() > (this.top + this.height))) {
			return false;
		}

		// select neighbored slots
		final CardSlot slot = card.getSlot();
		final int slotIndex = this.slots.lastIndexOf(slot);
		final int[] neighboredSlots = { slotIndex - 1, slotIndex + 1 };
		if (neighboredSlots[0] == -1) {
			neighboredSlots[0] = this.slots.size() - 1;
		}
		if (neighboredSlots[1] == this.slots.size()) {
			neighboredSlots[1] = 0;
		}

		float minXDiff = Math.abs(slot.getX() - card.getX());
		CardSlot closestSlot = slot;
		float crrXDiff;

		// find closest slot
		for (int neighboredSlot : neighboredSlots) {
			if (neighboredSlot != slotIndex) {
				crrXDiff = Math.abs(this.slots.get(neighboredSlot).getX()
						- card.getX());
				if (crrXDiff < minXDiff) {
					minXDiff = crrXDiff;
					closestSlot = this.slots.get(neighboredSlot);
				}
			}
		}

		// swap card slots if target card is not played
		if (slot != closestSlot) {
			final Card oldCard = closestSlot.getCard();
			closestSlot.setCard(card);
			slot.setCard(oldCard);

			if (oldCard != null) {
				oldCard.reset();
			}
		}

		return true;
	}

	/**
	 * reset hand panel for a new match
	 */
	public void clear() {
		this.slots.clear();
	}

}