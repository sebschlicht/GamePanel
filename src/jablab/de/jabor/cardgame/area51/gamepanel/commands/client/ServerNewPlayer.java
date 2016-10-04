package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs about a new player joining the game
 * 
 * @author development.Jabor
 * 
 */
public class ServerNewPlayer extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 2;

	/**
	 * client identifier
	 */
	private int clientID;

	/**
	 * player name
	 */
	private String playerName;

	/**
	 * create new new player command
	 * 
	 * @param client
	 *            command target
	 * @param clientID
	 *            client identifier
	 * @param playerName
	 *            player name
	 */
	public ServerNewPlayer(Client client, int clientID, String playerName) {
		super(client);
		this.clientID = clientID;
		this.playerName = playerName;
	}

	@Override
	public void run() {
		this.client.serverNewPlayer(this.clientID, this.playerName);
	}

}