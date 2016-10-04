package jablab.de.jabor.cardgame.area51.gamepanel.commands.client;

import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;

import java.util.List;

/**
 * server informs about the game end
 * 
 * @author development.Jabor
 * 
 */
public class ServerGameFinished extends ClientCommand {

	/**
	 * client identifiers ordered by winning position
	 */
	private List<Integer> winnerIDs;

	/**
	 * create new game finished command
	 * 
	 * @param client
	 *            command target
	 * @param winnerIDs
	 *            client identifiers ordered by winning position
	 */
	public ServerGameFinished(Client client, List<Integer> winnerIDs) {
		super(client);
		this.winnerIDs = winnerIDs;
	}

	@Override
	public void run() {
		this.client.serverGameFinished(this.winnerIDs);
	}

}