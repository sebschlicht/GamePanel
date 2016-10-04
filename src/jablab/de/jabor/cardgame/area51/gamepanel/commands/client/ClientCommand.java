package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;

/**
 * server specific command to be extended
 * 
 * @author development.Jabor
 * 
 */
public abstract class ClientCommand extends Command {

	/**
	 * client the commands target
	 */
	protected Client client;

	/**
	 * create new client command
	 * 
	 * @param client
	 *            client the command targets
	 */
	public ClientCommand(Client client) {
		this.client = client;
	}

	/**
	 * set target client instance
	 * 
	 * @param client
	 *            client targeted in command execution
	 */
	public void setTargetClient(Client client) {
		this.client = client;
	}

}