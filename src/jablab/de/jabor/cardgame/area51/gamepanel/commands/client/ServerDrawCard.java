package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs about a card being drawn
 * 
 * @author development.Jabor
 * 
 */
public class ServerDrawCard extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 2;

	/**
	 * client identifier
	 */
	private int clientID;

	/**
	 * card identifier
	 */
	private int cardID;

	/**
	 * create new draw card command
	 * 
	 * @param client
	 *            command target
	 * @param clientID
	 *            client identifier
	 * @param cardID
	 *            card identifier
	 */
	public ServerDrawCard(Client client, int clientID, int cardID) {
		super(client);
		this.clientID = clientID;
		this.cardID = cardID;
	}

	@Override
	public void run() {
		this.client.serverDrawCard(this.clientID, this.cardID);
	}

}