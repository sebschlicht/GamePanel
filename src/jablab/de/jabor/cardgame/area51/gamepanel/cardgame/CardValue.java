package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

/**
 * enumeration of card values
 * 
 * @author development.Jabor
 * 
 */
public enum CardValue {

	/**
	 * seven (Sieben)
	 */
	SEVEN("Sieben", 7),

	/**
	 * eight (Acht)
	 */
	EIGHT("Acht", 8),

	/**
	 * nine (Neun)
	 */
	NINE("Neun", 9),

	/**
	 * ten (Zehn)
	 */
	TEN("Zehn", 10),

	/**
	 * knave (Bube)
	 */
	KNAVE("Bube", 11),

	/**
	 * queen (Dame)
	 */
	QUEEN("Dame", 12),

	/**
	 * king (König)
	 */
	KING("König", 13),

	/**
	 * ace (Ass)
	 */
	ACE("Ass", 14);

	/**
	 * integer value
	 */
	private final int value;

	/**
	 * label shown to users
	 */
	private final String label;

	/**
	 * create a new card value
	 * 
	 * @param label
	 *            label shown to users
	 * @param value
	 *            integer value
	 */
	private CardValue(String label, int value) {
		this.label = label;
		this.value = value;
	}

	/**
	 * parse to integer for comparison
	 * 
	 * @return integer value
	 */
	public int getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.label;
	}

	/**
	 * calculate card value by card identifier
	 * 
	 * @param cardID
	 *            card identifier
	 * @return card's value
	 */
	public static CardValue getValueById(int cardID) {
		switch ((cardID - 1) % 8) {

		case 0:
			return SEVEN;

		case 1:
			return EIGHT;

		case 2:
			return NINE;

		case 3:
			return TEN;

		case 4:
			return KNAVE;

		case 5:
			return QUEEN;

		case 6:
			return KING;

		case 7:
			return ACE;

		default:
			return null;

		}
	}

}