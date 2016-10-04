package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

/**
 * server sends error codes of errors occurred
 * 
 * @author development.Jabor
 * 
 */
public class ServerError extends ClientCommand {

	/**
	 * number of parameters
	 */
	public static final int PARAMS = 1;

	/**
	 * code of the error occurred
	 */
	private int errorCode;

	/**
	 * create new error command
	 * 
	 * @param client
	 *            command target
	 * @param errorCode
	 *            code of the error occurred
	 */
	public ServerError(Client client, int errorCode) {
		super(client);
		this.errorCode = errorCode;
	}

	@Override
	public void run() {
		this.client.serverError(this.errorCode);
	}

}