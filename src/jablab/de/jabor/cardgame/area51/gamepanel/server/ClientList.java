package jablab.de.jabor.cardgame.area51.gamepanel.server;

import java.util.ArrayList;
import java.util.List;

/**
 * client list with additional functionality
 * 
 * @author development.Jabor
 * 
 */
public class ClientList {

	/**
	 * encapsulated client list
	 */
	private ArrayList<ServerClient> clients;

	/**
	 * create a new empty client list
	 * 
	 * @param maxClients
	 *            maximum number of clients
	 */
	public ClientList(int maxClients) {
		this.clients = new ArrayList<ServerClient>(maxClients);
	}

	/**
	 * 
	 * @return number of clients connected
	 */
	public int numClients() {
		return this.clients.size();
	}

	/**
	 * add a client to the list
	 * 
	 * @param client
	 *            client instance to be added
	 */
	public void add(ServerClient client) {
		this.clients.add(client);
	}

	/**
	 * get a specific client
	 * 
	 * @param index
	 *            index of client instance in list
	 * @return client instance at index specified
	 */
	public ServerClient get(int index) {
		return this.clients.get(index);
	}

	/**
	 * remove a specific client
	 * 
	 * @param client
	 *            client to be removed
	 * @return true - client has been removed<br>
	 *         false - list does not contain this client
	 */
	public boolean remove(ServerClient client) {
		return this.clients.remove(client);
	}

	/**
	 * get client list to iterate over it
	 * 
	 * @return basic client list
	 */
	public List<ServerClient> getList() {
		return this.clients;
	}

	/**
	 * get next client's index
	 * 
	 * @param index
	 *            index of current client
	 * @return index of next client
	 */
	public int getNextIndex(int index) {
		if (++index == this.clients.size()) {
			index = 0;
		}

		if (this.clients.get(index).hasFinished()) {
			return this.getNextIndex(index);
		}

		return index;
	}

	/**
	 * get previous client's index
	 * 
	 * @param index
	 *            index of current client
	 * @return index of previous client
	 */
	public int getPreviousIndex(int index) {
		if (--index < 0) {
			index = this.clients.size() - 1;
		}

		if (this.clients.get(index).hasFinished()) {
			return this.getPreviousIndex(index);
		}

		return index;
	}

	/**
	 * check usage of a username
	 * 
	 * @param username
	 *            username to search for
	 * @return true - username already in use<br>
	 *         false - otherwise
	 */
	public boolean containsUsername(String username) {
		for (ServerClient client : this.clients) {
			if (username.equals(client.getUsername())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * search for a specific client
	 * 
	 * @param clientID
	 *            client identifier
	 * @return client specified if found<br>
	 *         null otherwise
	 */
	public ServerClient getClientById(int clientID) {
		for (ServerClient client : this.clients) {
			if (client.getId() == clientID) {
				return client;
			}
		}

		return null;
	}

	/**
	 * search for the next playing client including this one
	 * 
	 * @param index
	 *            client start index
	 * @return index of next playing client (>=)
	 */
	public int thisOrNextIndex(int index) {
		if (this.clients.get(index).hasFinished()) {
			return this.getNextIndex(index);
		} else {
			return index;
		}
	}

}