package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs that the round has been finished
 * 
 * @author development.Jabor
 * 
 */
public class ServerRoundFinished extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 1;

	/**
	 * target won flag
	 */
	private boolean targetWon;

	/**
	 * create new round finished command
	 * 
	 * @param client
	 *            command target
	 * @param targetWon
	 *            target won flag
	 */
	public ServerRoundFinished(Client client, boolean targetWon) {
		super(client);
		this.targetWon = targetWon;
	}

	@Override
	public void run() {
		this.client.serverRoundFinished(this.targetWon);
	}

}