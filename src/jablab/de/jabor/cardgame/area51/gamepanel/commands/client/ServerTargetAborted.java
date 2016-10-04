package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server informs that the target aborted it's move
 * 
 * @author development.Jabor
 * 
 */
public class ServerTargetAborted extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 0;

	/**
	 * create new target aborted command
	 * 
	 * @param client
	 *            command target
	 */
	public ServerTargetAborted(Client client) {
		super(client);
	}

	@Override
	public void run() {
		this.client.serverTargetAborted();
	}

}