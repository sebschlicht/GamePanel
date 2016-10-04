package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * version sent when client established connection to server
 * 
 * @author development.Jabor
 * 
 */
public class ServerVersion extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 1;

	/**
	 * server version
	 */
	private String version;

	/**
	 * create new version command
	 * 
	 * @param client
	 *            command target
	 * @param version
	 *            server version
	 */
	public ServerVersion(Client client, String version) {
		super(client);
		this.version = version;
	}

	@Override
	public void run() {
		this.client.serverVersion(this.version);
	}

}