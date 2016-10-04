package jablab.de.jabor.cardgame.area51.gamepanel.graphics.playerPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Player;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.Coordinates;

public class PlayerView {

	private Coordinates coordinates;

	private Player player;

	public PlayerView(Coordinates coordinates, Player player) {
		this.coordinates = coordinates;
		this.player = player;
	}

	public float getLeft() {
		return this.coordinates.getX();
	}

	public void setLeft(float left) {
		this.coordinates.setX(left);
	}

	public float getTop() {
		return this.coordinates.getY();
	}

	public void setTop(float top) {
		this.coordinates.setY(top);
	}

	public String getPlayerName() {
		return this.player.getName();
	}

	public int getNumCards() {
		return this.player.getNumCards();
	}

	public PlayerRole getPlayerRole() {
		return this.player.getRole();
	}

}