package jablab.de.jabor.cardgame.area51.gamepanel.commands.server;

import jablab.de.jabor.cardgame.area51.gamepanel.server.ServerClient;

/**
 * login command sent when client tries to join the game
 * 
 * @author development.Jabor
 * 
 */
public class ClientLogin extends ServerCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 1;

	/**
	 * username the client wants to use
	 */
	private String username;

	/**
	 * create new login command
	 * 
	 * @param client
	 *            client the command comes from
	 * @param username
	 *            username chosen
	 */
	public ClientLogin(ServerClient client, String username) {
		super(client);
		this.username = username;
	}

	@Override
	public void run() {
		SERVER.clientLogin(this.client, this.username);
	}

}