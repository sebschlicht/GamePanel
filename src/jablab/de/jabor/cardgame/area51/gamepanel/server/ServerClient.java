package jablab.de.jabor.cardgame.area51.gamepanel.server;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardList;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.RoleTuple;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Commands;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.server.ClientAbort;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.server.ClientLogin;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.server.ClientPlayCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

/**
 * server's client allowing communication
 * 
 * @author development.Jabor
 * 
 */
public class ServerClient implements Runnable {

	/**
	 * thread-safe command queue to stack commands
	 */
	private BlockingQueue<Command> commandQueue;

	/**
	 * socket to client machine
	 */
	private Socket socket;

	/**
	 * incoming communication stream
	 */
	private BufferedReader reader;

	/**
	 * outgoing communication stream
	 */
	private PrintWriter writer;

	/**
	 * client identifier
	 */
	private int id;

	/**
	 * initializing error code
	 */
	private int joinError;

	/**
	 * player name
	 */
	private String username;

	private CardList cards;

	/**
	 * round aborted flag
	 */
	private boolean aborted;

	/**
	 * finished match flag
	 */
	private boolean finished;

	/**
	 * create new client instance wrapping client socket
	 * 
	 * @param commandQueue
	 *            thread-safe command queue to stack commands
	 * @param socket
	 *            socket to client machine
	 * @param id
	 *            client identifier
	 * @param joinError
	 *            initializing error code
	 * @throws IOException
	 */
	public ServerClient(BlockingQueue<Command> commandQueue, Socket socket,
			int id, int joinError) throws IOException {
		// open communication gateways
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream(), true);

		this.commandQueue = commandQueue;
		this.id = id;
		this.joinError = joinError;
		this.cards = new CardList();

		// start communication
		Thread commThread = new Thread(this);
		commThread.start();
	}

	/**
	 * handle client communication
	 */
	public void run() {
		// send server version
		this.writer.println(Commands.Server.VERSION + Commands.SEPARATOR
				+ Server.VERSION);

		// send join error
		if (this.joinError != Commands.ErrorCode.NO_ERROR) {
			this.sendError(this.joinError);
			this.close();
		} else {
			// continuously read messages, parse to and stack commands
			String commandLine;

			try {
				while ((commandLine = this.reader.readLine()) != null) {
					String[] commandParts = commandLine
							.split(Commands.SEPARATOR);
					this.parseCommand(commandParts);
				}
			} catch (IOException e) {
				Log.d("ServerClient|run",
						"failed to read line!\n" + e.getMessage());
				this.close();
			}
		}
	}

	/**
	 * identify this specific client
	 * 
	 * @return client identifier
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * get client label
	 * 
	 * @return player name
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * append the player name
	 * 
	 * @param username
	 *            player name used to login
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * provide abortion flag for round abortion
	 * 
	 * @return round aborted flag
	 */
	public boolean hasAborted() {
		return this.aborted;
	}

	/**
	 * change abortion flag
	 * 
	 * @param aborted
	 *            round aborted flag
	 */
	public void setAborted(boolean aborted) {
		this.aborted = aborted;
	}

	/**
	 * provide finished flag for game end
	 * 
	 * @return match finished flag
	 */
	public boolean hasFinished() {
		return this.finished;
	}

	/**
	 * change finished flag
	 * 
	 * @param finished
	 *            match finished flag
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * shutdown network communication gateways
	 */
	public void close() {
		try {
			this.socket.shutdownInput();
			this.socket.close();
			this.writer.close();
			// this.reader.close();
		} catch (IOException e) {
			Log.d("ServerClient|close",
					"failed to clean up!\n" + e.getMessage());
		}
	}

	/**
	 * parse messages to and stack commands
	 * 
	 * @param commandParts
	 *            message's command parts
	 */
	private void parseCommand(String[] commandParts) {
		String command = commandParts[0];
		int numParams = commandParts.length - 1;

		if ((numParams == ClientLogin.PARAMS)
				&& Commands.Client.LOGIN.equals(command)) {
			String username = commandParts[1];

			ClientLogin loginCommand = new ClientLogin(this, username);
			this.commandQueue.add(loginCommand);
		} else if ((numParams == ClientPlayCard.PARAMS)
				&& Commands.Client.PLAY_CARD.equals(command)) {
			int cardID = Integer.parseInt(commandParts[1]);
			int slotID = Integer.parseInt(commandParts[2]);

			ClientPlayCard playCardCommand = new ClientPlayCard(this, cardID,
					slotID);
			this.commandQueue.add(playCardCommand);
		} else if ((numParams == ClientAbort.PARAMS)
				&& Commands.Client.PLAY_ABORT.equals(command)) {
			ClientAbort abortCommand = new ClientAbort(this);
			this.commandQueue.add(abortCommand);
		}
	}

	/**
	 * send error message to client
	 * 
	 * @param errorCode
	 *            error identifier
	 */
	public void sendError(int errorCode) {
		this.writer.println(Commands.Server.ERROR + Commands.SEPARATOR
				+ errorCode);
	}

	public void sendWelcome(int maxPlayers, int ownIndex) {
		this.writer.println(Commands.Server.WELCOME + Commands.SEPARATOR
				+ maxPlayers + Commands.SEPARATOR + this.id
				+ Commands.SEPARATOR + ownIndex);
	}

	public void sendNewPlayer(int id, String username) {
		this.writer.println(Commands.Server.NEW_PLAYER + Commands.SEPARATOR
				+ id + Commands.SEPARATOR + username);
	}

	public void addCard(Card card) {
		this.cards.add(card);
		this.sendDrawCard(this.id, card.getId());
	}

	public void sendDrawCard(int clientID, int cardID) {
		this.writer.println(Commands.Server.DRAW_CARD + Commands.SEPARATOR
				+ clientID + Commands.SEPARATOR + cardID);
	}

	public void sendGameStart(Card trump) {
		this.writer.println(Commands.Server.GAME_START + Commands.SEPARATOR
				+ trump.getId());
	}

	public void sendCurrentRoles(List<RoleTuple> roles) {
		// TODO: build before
		StringBuilder message = new StringBuilder();
		for (RoleTuple role : roles) {
			message.append(role.getClientId());
			message.append(Commands.SEPARATOR);
			message.append(role.getRole().getValue());
			message.append(Commands.SEPARATOR);
		}

		this.writer.println(Commands.Server.CRR_ROLES + Commands.SEPARATOR
				+ message);
	}

	public int getNumCards() {
		return this.cards.size();
	}

	public Card getCardInHand(int cardID) {
		return this.cards.getCardById(cardID);
	}

	public void sendPlayerPlaysCard(int clientID, int cardID, int slotID) {
		this.writer.println(Commands.Server.PLAYER_PLAYS_CARD
				+ Commands.SEPARATOR + clientID + Commands.SEPARATOR + cardID
				+ Commands.SEPARATOR + slotID);
	}

	public void removeCard(Card card) {
		this.cards.remove(card);
	}

	public void sendTargetAborted() {
		this.writer.println(Commands.Server.TARGET_ABORTED);
	}

	public void sendRoundFinished(boolean targetWon) {
		this.writer.println(Commands.Server.ROUND_FINISHED + Commands.SEPARATOR
				+ targetWon);
	}

	public void sendGameFinished(List<Integer> winnerIDs) {
		// TODO: build before
		StringBuilder message = new StringBuilder();
		message.append(winnerIDs.size());
		for (Integer winnerID : winnerIDs) {
			message.append(Commands.SEPARATOR);
			message.append(winnerID);
		}

		this.writer.println(Commands.Server.GAME_FINISHED + Commands.SEPARATOR
				+ message);
	}

}