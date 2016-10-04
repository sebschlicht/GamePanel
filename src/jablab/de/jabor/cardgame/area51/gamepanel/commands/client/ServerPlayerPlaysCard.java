package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs about a player playing a card
 * 
 * @author development.Jabor
 * 
 */
public class ServerPlayerPlaysCard extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 3;

	/**
	 * client identifier
	 */
	private int clientID;

	/**
	 * card identifier
	 */
	private int cardID;

	/**
	 * slot identifier
	 */
	private int slotID;

	public ServerPlayerPlaysCard(Client client, int clientID, int cardID,
			int slotID) {
		super(client);
		this.clientID = clientID;
		this.cardID = cardID;
		this.slotID = slotID;
	}

	@Override
	public void run() {
		this.client.serverPlayerPlaysCard(this.clientID, this.cardID,
				this.slotID);
	}

}