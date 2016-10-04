package jablab.de.jabor.cardgame.area51.gamepanel.cardgame;


/**
 * role tuple containing client and role being set
 * 
 * @author development.Jabor
 * 
 */
public class RoleTuple {

	/**
	 * client identifier
	 */
	private int clientID;

	/**
	 * player role
	 */
	private PlayerRole role;

	/**
	 * create new role tuple
	 * 
	 * @param clientID
	 *            client identifier
	 * @param role
	 *            player role
	 */
	public RoleTuple(int clientID, PlayerRole role) {
		this.clientID = clientID;
		this.role = role;
	}

	public int getClientId() {
		return this.clientID;
	}

	public PlayerRole getRole() {
		return this.role;
	}

}