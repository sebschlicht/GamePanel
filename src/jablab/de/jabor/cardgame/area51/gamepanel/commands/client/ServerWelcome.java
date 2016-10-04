package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * welcome command sent when client successfully managed to login
 * 
 * @author development.Jabor
 * 
 */
public class ServerWelcome extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 3;

	/**
	 * maximum number of players in game
	 */
	private int maxPlayers;

	/**
	 * client identifier
	 */
	private int clientID;

	/**
	 * client index within server list
	 */
	private int ownIndex;

	/**
	 * create new welcome command
	 * 
	 * @param client
	 *            command target
	 * @param maxPlayers
	 *            maximum number of players in game
	 * @param clientID
	 *            client identifier
	 * @param ownIndex
	 *            client index within server list
	 */
	public ServerWelcome(Client client, int maxPlayers, int clientID,
			int ownIndex) {
		super(client);
		this.maxPlayers = maxPlayers;
		this.clientID = clientID;
		this.ownIndex = ownIndex;
	}

	@Override
	public void run() {
		this.client
				.serverWelcome(this.maxPlayers, this.clientID, this.ownIndex);
	}

}