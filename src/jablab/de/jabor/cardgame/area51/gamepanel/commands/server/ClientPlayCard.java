package jablab.de.jabor.cardgame.area51.gamepanel.commands.server;

import jablab.de.jabor.cardgame.area51.gamepanel.server.ServerClient;

/**
 * sent when a client want to play a card
 * 
 * @author development.Jabor
 * 
 */
public class ClientPlayCard extends ServerCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 2;

	/**
	 * card identifier
	 */
	private int cardID;

	/**
	 * slot identifier
	 */
	private int slotID;

	/**
	 * create new play card command
	 * 
	 * @param client
	 *            client the command comes from
	 * @param cardID
	 *            card identifier
	 * @param slotID
	 *            slot identifier
	 */
	public ClientPlayCard(ServerClient client, int cardID, int slotID) {
		super(client);
		this.cardID = cardID;
		this.slotID = slotID;
	}

	@Override
	public void run() {
		SERVER.clientPlayCard(this.client, this.cardID, this.slotID);
	}

}