package jablab.de.jabor.cardgame.area51.gamepanel.server;

import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Commands;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Worker;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.server.ServerCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * game server handling new and existing clients
 * 
 * @author development.Jabor
 * 
 */
public class Server implements Runnable {

	/**
	 * server version
	 */
	public static final String VERSION = "1.1";

	/**
	 * user interface additionally handling commands
	 */
	private ServerGUI gui;

	/**
	 * thread-safe command queue
	 */
	private BlockingQueue<Command> commandQueue;

	/**
	 * server socket to listen for new clients
	 */
	private ServerSocket socket;

	/**
	 * list with clients in game
	 */
	private ClientList clients;

	/**
	 * maximum number of clients in game
	 */
	private final int maxClients;

	/**
	 * game server doing card game logic
	 */
	private CardgameServer gameServer;

	/**
	 * create new listening server
	 * 
	 * @param gui
	 *            user interface and command handler
	 * @param maxClients
	 *            maximum number of clients in game
	 * @throws IOException
	 */
	public Server(ServerGUI gui, int maxClients) throws IOException {
		// create server socket
		this.socket = new ServerSocket(61111);

		this.gui = gui;
		this.commandQueue = new LinkedBlockingQueue<Command>();
		this.clients = new ClientList(maxClients);
		this.maxClients = maxClients;
		this.gameServer = new CardgameServer(this.clients);

		// start command handling
		ServerCommand.setTargetServer(this);
		final Thread workerThread = new Thread(new Worker(this.commandQueue,
				this.gui));
		workerThread.start();

		// start listening
		final Thread listenThread = new Thread(this);
		listenThread.start();
	}

	/**
	 * continuously listen for new clients
	 */
	public void run() {
		// listen for clients
		try {
			int id = 0;

			while (!this.socket.isClosed()) {
				this.gui.log("Server|run",
						"waiting for a new client to connect...");
				Socket clientSocket = this.socket.accept();
				id += 1;

				int joinError = this.getJoinError();
				new ServerClient(this.commandQueue, clientSocket, id, joinError);

				if (joinError == Commands.ErrorCode.NO_ERROR) {
					this.gui.log("Server|run", "client accepted: "
							+ clientSocket.getInetAddress() + "(#" + id + ")");
				}
			}
		} catch (IOException e) {
			this.showError(e, "failed to accept new client!");
		}
	}

	/**
	 * close all network communication gateways
	 */
	public void close() {
		try {
			this.socket.close();

			for (ServerClient client : this.clients.getList()) {
				client.close();
			}
		} catch (IOException e) {
			this.showError(e, "failed to clean up!");
		}
	}

	/**
	 * check for general invalid usernames
	 * 
	 * @param username
	 *            username to be checked
	 * @return login error code
	 */
	private static int getUsernameError(String username) {
		if ((username == null) || username.contains(Commands.SEPARATOR)) {
			return Commands.ErrorCode.Login.INVALID;
		}

		return Commands.ErrorCode.NO_ERROR;
	}

	/**
	 * show error in user interface
	 * 
	 * @param e
	 *            exception occurred
	 * @param message
	 *            additional message to be displayed
	 */
	private void showError(Exception e, String message) {
		this.gui.showError(message + "\n\"" + e.getMessage() + "\"");
	}

	/**
	 * check for join errors
	 * 
	 * @return join error code
	 */
	private int getJoinError() {
		if (this.gameServer.isGameRunning()) {
			return Commands.ErrorCode.Join.GAME_RUNNING;
		} else if (this.maxClients == this.clients.numClients()) {
			return Commands.ErrorCode.Join.SERVER_FULL;
		}

		return Commands.ErrorCode.NO_ERROR;
	}

	/**
	 * check for login errors
	 * 
	 * @param client
	 *            client logging in
	 * @param username
	 *            username specified
	 * @return login error code
	 */
	private int getLoginError(ServerClient client, String username) {
		int loginError;

		if ((loginError = this.getJoinError()) != Commands.ErrorCode.NO_ERROR) {
			return loginError;
		} else if (client.getUsername() != null) {
			return Commands.ErrorCode.Login.LOGGED_IN;
		} else if ((loginError = Server.getUsernameError(username)) != Commands.ErrorCode.NO_ERROR) {
			return loginError;
		} else if (this.clients.containsUsername(username)) {
			return Commands.ErrorCode.Login.IN_USE;
		}

		return Commands.ErrorCode.NO_ERROR;
	}

	public void clientLogin(ServerClient client, String username) {
		int loginError = this.getLoginError(client, username);

		if (loginError == Commands.ErrorCode.NO_ERROR) {
			client.setUsername(username);
			client.sendWelcome(this.maxClients, this.clients.numClients());
			for (ServerClient informClient : this.clients.getList()) {
				informClient.sendNewPlayer(client.getId(), username);
				client.sendNewPlayer(informClient.getId(),
						informClient.getUsername());
			}

			this.clients.add(client);
		} else {
			client.sendError(loginError);
		}
	}

	public void startGame() {
		this.gameServer.startGame();
	}

	public void clientPlayCard(ServerClient client, int cardID, int slotID) {
		this.gameServer.clientPlayCard(client, cardID, slotID);
	}

	public void clientAbort(ServerClient client) {
		this.gameServer.clientAbort(client);
	}

	public boolean isGameRunning() {
		return this.gameServer.isGameRunning();
	}
}