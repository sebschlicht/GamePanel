package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.RoleTuple;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

import java.util.List;

/**
 * server informs about current roles
 * 
 * @author development.Jabor
 * 
 */
public class ServerCurrentRoles extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 6;

	/**
	 * player roles set
	 */
	private List<RoleTuple> roles;

	/**
	 * create new current roles command
	 * 
	 * @param client
	 *            command target
	 * @param roles
	 *            player roles set
	 */
	public ServerCurrentRoles(Client client, List<RoleTuple> roles) {
		super(client);
		this.roles = roles;
	}

	@Override
	public void run() {
		this.client.serverCurrentRoles(this.roles);
	}

}