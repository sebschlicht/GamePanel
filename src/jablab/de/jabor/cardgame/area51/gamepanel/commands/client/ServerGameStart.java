package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs that the game has been started
 * 
 * @author development.Jabor
 * 
 */
public class ServerGameStart extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 1;

	/**
	 * trump card
	 */
	private Card trump;

	/**
	 * create new game start command
	 * 
	 * @param client
	 *            command target
	 * @param trump
	 *            trump card
	 */
	public ServerGameStart(Client client, Card trump) {
		super(client);
		this.trump = trump;
	}

	@Override
	public void run() {
		this.client.serverGameStart(this.trump);
	}

}