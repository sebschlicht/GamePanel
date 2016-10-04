package jablab.de.jabor.cardgame.area51.gamepanel.client;

import java.util.ArrayList;
import java.util.List;

public class PlayerList {

	private ArrayList<Player> players;

	public PlayerList(int maxPlayers) {
		this.players = new ArrayList<Player>(maxPlayers);
	}

	public Player get(int index) {
		return this.players.get(index);
	}

	public void add(Player player) {
		this.players.add(player);
	}

	public Player getPlayerById(int clientID) {
		for (Player player : this.players) {
			if (player.getClientId() == clientID) {
				return player;
			}
		}

		return null;
	}

	public List<Player> getList() {
		return this.players;
	}

	public Player getNextPlayer() {
		for (Player player : this.players) {
			if (player.getNumCards() > 0) {
				return player;
			}
		}

		return null;
	}
}