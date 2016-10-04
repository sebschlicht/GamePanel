package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;

/**
 * enumeration of player roles
 * 
 * @author development.Jabor
 * 
 */
public enum PlayerRole {

	/**
	 * player is not involved yet
	 */
	NORMAL(0, "spectator"),

	/**
	 * player is the target of the other player(s) actions
	 */
	TARGET(1, "target"),

	/**
	 * player can attack the target if it has been attacked before
	 */
	ATTACKER(2, "attacker"),

	/**
	 * player starts attacking the target
	 */
	FIRST_ATTACKER(3, "first attacker");

	/**
	 * integer value
	 */
	private final int value;

	/**
	 * label shown to users
	 */
	private final String label;

	/**
	 * create a new player role
	 * 
	 * @param value
	 *            integer value
	 */
	private PlayerRole(int value, String label) {
		this.value = value;
		this.label = label;
	}

	/**
	 * parse to integer
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
	 * calculate player role by its value
	 * 
	 * @param value
	 *            integer value
	 * @return player role
	 */
	public static PlayerRole getRoleByValue(int value) {
		switch (value) {
		case 0:
			return NORMAL;

		case 1:
			return TARGET;

		case 2:
			return ATTACKER;

		case 3:
			return FIRST_ATTACKER;

		default:
			return null;
		}
	}

}