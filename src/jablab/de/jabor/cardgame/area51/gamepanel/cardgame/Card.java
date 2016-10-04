package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

/**
 * basic card instance
 * 
 * @author development.Jabor
 * 
 */
public class Card {

	/**
	 * card identifier
	 */
	private int id;

	/**
	 * card type (color)
	 */
	private CardType type;

	/**
	 * card value
	 */
	private CardValue value;

	/**
	 * create new card instance
	 * 
	 * @param id
	 *            card identifier
	 */
	public Card(int id) {
		this.id = id;
		this.type = CardType.getTypeById(id);
		this.value = CardValue.getValueById(id);
	}

	@Override
	public String toString() {
		return this.type.toString() + " " + this.value.toString();
	}

	/**
	 * identify card
	 * 
	 * @return card identifier
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * check for trump/matching color
	 * 
	 * @return card type (color)
	 */
	public CardType getType() {
		return this.type;
	}

	/**
	 * check for existing values
	 * 
	 * @return card value
	 */
	public CardValue getValue() {
		return this.value;
	}

}