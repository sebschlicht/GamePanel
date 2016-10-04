package jablab.de.jabor.cardgame.area51.gamepanel.graphics;

import jablab.de.jabor.cardgame.area51.gamepanel.graphics.handPanel.CardSlot;
import android.graphics.Bitmap;

public class Card {

	private CardSlot slot;

	private int id;

	private Coordinates center;

	private Bitmap texture;

	public Card(int cardID, Coordinates center) {
		this.id = cardID;
		this.center = center;
	}

	public CardSlot getSlot() {
		return this.slot;
	}

	public void setSlot(CardSlot slot) {
		this.slot = slot;
	}

	public void reset() {
		this.setX(this.slot.getX());
		this.setY(this.slot.getY());
	}

	public int getId() {
		return this.id;
	}

	public float getX() {
		return this.center.getX();
	}

	public void setX(float x) {
		this.center.setX(x);
	}

	public float getY() {
		return this.center.getY();
	}

	public void setY(float y) {
		this.center.setY(y);
	}

	public Bitmap getTexture() {
		return this.texture;
	}

	public void setTexture(Bitmap texture) {
		this.texture = texture;
	}

	public int getWidth() {
		return this.texture.getWidth();
	}

	public int getHeight() {
		return this.texture.getHeight();
	}

}