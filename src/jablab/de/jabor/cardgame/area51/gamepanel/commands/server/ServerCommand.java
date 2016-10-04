package jablab.de.jabor.cardgame.area51.gamepanel.commands.server;

import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;
import jablab.de.jabor.cardgame.area51.gamepanel.server.Server;
import jablab.de.jabor.cardgame.area51.gamepanel.server.ServerClient;

/**
 * server specific command to be extended
 * 
 * @author development.Jabor
 * 
 */
public abstract class ServerCommand extends Command {

	/**
	 * server the commands target
	 */
	protected static Server SERVER;

	/**
	 * client the command comes from
	 */
	protected ServerClient client;

	/**
	 * set target server instance
	 * 
	 * @param server
	 *            server targeted in command execution
	 */
	public static void setTargetServer(Server server) {
		SERVER = server;
	}

	/**
	 * basic constructor
	 * 
	 * @param client
	 *            client the command comes from
	 */
	public ServerCommand(ServerClient client) {
		this.client = client;
	}

}