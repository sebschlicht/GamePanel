package jablab.de.jabor.cardgame.area51.gamepanel.graphics.slotPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * playing slot list
 * 
 * @author development.Jabor
 * 
 */
public class SlotList {

	/**
	 * encapsulated slots list
	 */
	private List<Slot> slots;

	/**
	 * create new slot list
	 */
	public SlotList() {
		this.slots = new ArrayList<Slot>(6);
	}

	/**
	 * add a slot to the list
	 * 
	 * @param slot
	 *            slot to add
	 */
	public void add(Slot slot) {
		this.slots.add(slot);
	}

	/**
	 * provide list for iteration
	 * 
	 * @return encapsulated slot list
	 */
	public List<Slot> getList() {
		return this.slots;
	}

	/**
	 * change slot states to match game logic
	 * 
	 * @param numSlots
	 *            number of active slots
	 */
	public void setNumSlots(int numSlots) {
		for (Slot slot : this.slots) {
			slot.setEnabled(numSlots > 0);
			numSlots -= 1;
		}
	}

}