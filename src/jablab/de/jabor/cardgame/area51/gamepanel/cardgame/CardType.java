package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

/**
 * enumeration of card types
 * 
 * @author development.Jabor
 * 
 */
public enum CardType {

	/**
	 * diamonds (Karo)
	 */
	DIAMONDS("Karo"),

	/**
	 * hearts (Herz)
	 */
	HEARTS("Herz"),

	/**
	 * spade (Pik)
	 */
	SPADE("Pik"),

	/**
	 * clubs (Kreuz)
	 */
	CLUBS("Kreuz");

	/**
	 * label shown to users
	 */
	private final String label;

	/**
	 * create a new card type
	 * 
	 * @param label
	 *            label shown to users
	 */
	private CardType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	/**
	 * calculate card type by card identifier
	 * 
	 * @param cardID
	 *            card identifier
	 * @return card's type
	 */
	public static CardType getTypeById(int cardID) {
		if ((cardID >= 1) && (cardID < 9)) {
			return DIAMONDS;
		} else if ((cardID >= 9) && (cardID < 17)) {
			return HEARTS;
		} else if ((cardID >= 17) && (cardID < 25)) {
			return SPADE;
		} else if ((cardID >= 25) && (cardID < 33)) {
			return CLUBS;
		} else {
			return null;
		}
	}

}