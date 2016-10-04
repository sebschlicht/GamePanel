package jablab.de.jabor.cardgame.area51.gamepanel.client;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;

public class Player {

	private int clientID;

	private String name;

	private int numCards;

	private PlayerRole role;

	public Player(int clientID, String name) {
		this.clientID = clientID;
		this.name = name;
		this.numCards = 0;
		this.role = PlayerRole.NORMAL;
	}

	public int getClientId() {
		return this.clientID;
	}

	public String getName() {
		return this.name;
	}

	public int getNumCards() {
		return this.numCards;
	}

	public void setNumCards(int numCards) {
		this.numCards = numCards;
	}

	public PlayerRole getRole() {
		return this.role;
	}

	public void setRole(PlayerRole role) {
		this.role = role;
	}

}