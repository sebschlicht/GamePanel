package jablab.de.jabor.cardgame.area51.gamepanel.graphics.slotPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.GamePanel;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class SlotPanel {

	/**
	 * paint object for enabled slots
	 */
	private static final Paint PAINT_SLOT_ENABLED = new Paint();

	/**
	 * paint object for hovered slots
	 */
	private static final Paint PAINT_SLOT_HOVERED = new Paint();

	/**
	 * paint object for disabled slots
	 */
	private static final Paint PAINT_SLOT_DISABLED = new Paint();

	/**
	 * margin around playing slots
	 */
	private static final int SLOT_MARGIN = 10;

	/**
	 * slot list
	 */
	private SlotList slotList;

	/**
	 * slot width
	 */
	private int slotWidth;

	/**
	 * slot height
	 */
	private int slotHeight;

	/**
	 * create new slot panel
	 * 
	 * @param width
	 *            width available
	 * @param height
	 *            height available
	 * @param left
	 *            left screen distance
	 * @param top
	 *            top screen distance
	 */
	public SlotPanel(int width, int height, int left, int top) {
		this.slotList = new SlotList();

		// calculate slot dimensions
		this.slotWidth = (width - (7 * SLOT_MARGIN)) / 6;
		this.slotHeight = (int) (this.slotWidth / GamePanel.SLOT_RATIO);

		// initialize brushes
		PAINT_SLOT_ENABLED.setStyle(Style.STROKE);
		PAINT_SLOT_HOVERED.setStyle(Style.STROKE);
		PAINT_SLOT_DISABLED.setStyle(Style.STROKE);

		PAINT_SLOT_ENABLED.setColor(Color.LTGRAY);
		PAINT_SLOT_HOVERED.setColor(Color.YELLOW);
		PAINT_SLOT_DISABLED.setColor(Color.DKGRAY);

		// fill slot list
		int slotTop = top + ((height - this.slotHeight) / 2);
		for (int i = 0; i < 6; i++) {
			this.slotList.add(new Slot(i, left + SLOT_MARGIN
					+ (i * (this.slotWidth + SLOT_MARGIN)), slotTop));
		}
	}

	/**
	 * change slot states to match game logic
	 * 
	 * @param numSlots
	 *            number of active slots
	 */
	public void setNumSlots(int numSlots) {
		this.slotList.setNumSlots(numSlots);
	}

	/**
	 * draw static content of slot panel
	 * 
	 * @param canvas
	 *            canvas to draw to
	 */
	public void paint(Canvas canvas) {
		Paint paint;
		Card card;
		for (Slot slot : this.slotList.getList()) {
			if (slot.isEnabled()) {
				if (!slot.isHovered()) {
					paint = PAINT_SLOT_ENABLED;
				} else {
					paint = PAINT_SLOT_HOVERED;
				}

				card = slot.getCard();
				if (card != null) {
					canvas.drawBitmap(card.getTexture(), slot.getLeft(),
							slot.getTop(), null);

					card = slot.getAntiCard();
					if (card != null) {
						canvas.drawBitmap(card.getTexture(), slot.getLeft(),
								slot.getTop(), null);
					}
				}
			} else {
				paint = PAINT_SLOT_DISABLED;
			}

			canvas.drawRect(slot.getLeft(), slot.getTop(), slot.getLeft()
					+ this.slotWidth, slot.getTop() + this.slotHeight, paint);
		}
	}

	/**
	 * search for slot targeted by movement
	 * 
	 * @param x
	 *            x-axis cursor position
	 * @param y
	 *            y-axis cursor position
	 * @return slot - slot at cursor position<br>
	 *         null - if no slot is targeted
	 */
	public Slot getSlotAtPosition(float x, float y) {
		for (Slot slot : this.slotList.getList()) {
			if (slot.isEnabled() && (slot.getLeft() < x) && (slot.getTop() < y)) {
				if (((slot.getLeft() + this.slotWidth) > x)
						&& ((slot.getTop() + this.slotHeight) > y)) {
					return slot;
				}
			} else {
				break;
			}
		}

		return null;
	}

	/**
	 * search for a specific slot
	 * 
	 * @param slotID
	 *            slot identifier
	 * @return slot - slot with the identifier specified<br>
	 *         null - slot not found
	 */
	private Slot getSlotById(int slotID) {
		for (Slot slot : this.slotList.getList()) {
			if (slot.getId() == slotID) {
				return slot;
			}
		}

		return null;
	}

	public void addCardToSlot(int slotID, Card card, Bitmap rawTexture) {
		Slot slot = this.getSlotById(slotID);
		int cardWidth = this.slotWidth;
		int cardHeight = this.slotHeight;

		if (slot.getCard() != null) {
			cardWidth *= 0.75;
			cardHeight *= 0.75;

			slot.setAntiCard(card);
		} else {
			slot.setCard(card);
		}

		Bitmap texture = Bitmap.createScaledBitmap(rawTexture, cardWidth,
				cardHeight, true);
		card.setTexture(texture);

		card.setX(slot.getLeft() + (this.slotWidth / 2));
		card.setY(slot.getTop() + (this.slotHeight / 2));
	}

	public void reset() {
		for (Slot slot : this.slotList.getList()) {
			slot.setCard(null);
			slot.setAntiCard(null);
		}
	}
}