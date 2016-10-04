package jablab.de.jabor.cardgame.area51.gamepanel.commands.server;

import jablab.de.jabor.cardgame.area51.gamepanel.server.ServerClient;

/**
 * sent when target client aborts the round
 * 
 * @author development.Jabor
 * 
 */
public class ClientAbort extends ServerCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 0;

	/**
	 * create new abort command
	 * 
	 * @param client
	 */
	public ClientAbort(ServerClient client) {
		super(client);
	}

	@Override
	public void run() {
		SERVER.clientAbort(this.client);
	}

}