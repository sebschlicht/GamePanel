package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

import java.util.ArrayList;
import java.util.List;

/**
 * playing slot list
 * 
 * @author development.Jabor
 * 
 */
public class SlotList {

	private int usedSlots;

	private int numSlots;

	private int defendedSlots;

	private List<Slot> slots;

	public SlotList() {
		this.slots = new ArrayList<Slot>(6);
		for (int i = 0; i < 6; i++) {
			this.slots.add(new Slot(i));
		}
	}

	public Slot get(int index) {
		if ((index >= 0) && (index < this.slots.size())) {
			return this.slots.get(index);
		}

		return null;
	}

	public int numUsedSlots() {
		return this.usedSlots;
	}

	public int numSlots() {
		return this.numSlots;
	}

	public void setNumSlots(int numSlots) {
		this.numSlots = numSlots;
	}

	public int numFreeSlots() {
		return this.numSlots - this.usedSlots;
	}

	public int numUndefendedSlots() {
		return this.usedSlots - this.defendedSlots;
	}

	public boolean containsValue(CardValue value) {
		Card card;
		for (Slot slot : this.slots) {
			card = slot.getCard();
			if (card != null) {
				if ((card.getValue() == value)
						|| (((card = slot.getAntiCard()) != null) && (card
								.getValue() == value))) {
					return true;
				}
			} else {
				break;
			}
		}

		return false;
	}

	public boolean canPass(CardValue value) {
		if (this.usedSlots > 3) {
			return false;
		}

		for (int i = 0; i < 3; i++) {
			if (this.slots.get(i).getCard() != null) {
				if ((this.slots.get(i).getAntiCard() != null)
						|| (this.slots.get(i).getCard().getValue() != value)) {
					return false;
				}
			} else {
				break;
			}
		}

		return true;
	}

	public void setCard(int slotID, Card card) {
		this.usedSlots += 1;
		this.slots.get(slotID).setCard(card);
	}

	public void setAntiCard(int slotID, Card antiCard) {
		this.defendedSlots += 1;
		this.slots.get(slotID).setAntiCard(antiCard);
	}

	public List<Slot> getList() {
		return this.slots;
	}

	public void reset() {
		for (Slot slot : this.slots) {
			slot.setCard(null);
			slot.setAntiCard(null);
		}

		this.usedSlots = 0;
		this.defendedSlots = 0;
	}

}