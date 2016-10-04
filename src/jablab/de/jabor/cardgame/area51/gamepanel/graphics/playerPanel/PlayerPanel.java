package jablab.de.jabor.cardgame.area51.gamepanel.graphics.playerPanel;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * screen area representing the other players
 * 
 * @author development.Jabor
 * 
 */
public class PlayerPanel {

	// DEBUG
	private static final Paint PAINT_TEXT = new Paint();
	private static final Paint PAINT_RECT = new Paint();

	private static final int PLAYER_MARGIN = 10;

	private int width;

	private int height;

	private int left;

	private int top;

	private Bitmap playerIcon;

	private Vector<PlayerView> players;

	private int playerWidth;

	private int playerHeight;

	public PlayerPanel(int width, int height, int left, int top,
			Bitmap playerIcon, Bitmap cardIcon) {
		this.width = width;
		this.height = height;
		this.left = left;
		this.top = top;

		this.playerHeight = ((height - (3 * PLAYER_MARGIN)) / 4);
		this.playerWidth = this.playerHeight;

		Bitmap playerBitmap = Bitmap.createScaledBitmap(playerIcon,
				this.playerWidth - 13, this.playerHeight - 13, true);
		int cardHeight = playerBitmap.getHeight() / 3;
		Bitmap cardBitmap = Bitmap.createScaledBitmap(cardIcon,
				(int) (cardHeight * 0.7), cardHeight, true);

		int cardLeft = (playerBitmap.getWidth() - cardBitmap.getWidth()) / 2;
		int cardTop = (playerBitmap.getWidth() / 3) * 2;
		Canvas canvas = new Canvas(playerBitmap);
		canvas.drawBitmap(cardBitmap, cardLeft, cardTop, null);
		this.playerIcon = playerBitmap;

		this.players = new Vector<PlayerView>();

		PAINT_RECT.setStyle(Style.STROKE);

		// DEBUG
		PAINT_TEXT.setColor(Color.WHITE);
		PAINT_TEXT.setStyle(Style.STROKE);
	}

	private static void drawTextCentered(final Canvas canvas,
			final int containerLeft, final int containerWidth, final int top,
			final String text) {
		final float textWidth = PAINT_TEXT.measureText(text);
		final int left = containerLeft
				+ (int) ((containerWidth - textWidth) / 2);
		canvas.drawText(text, left, top, PAINT_TEXT);
	}

	public void paint(Canvas canvas) {
		for (PlayerView playerView : this.players) {
			canvas.drawBitmap(this.playerIcon, playerView.getLeft()
					+ ((this.playerWidth - this.playerIcon.getWidth()) / 2),
					playerView.getTop(), null);
			drawTextCentered(canvas, (int) (this.left + playerView.getLeft()),
					this.playerWidth,
					(int) (this.top + playerView.getTop() + this.playerHeight),
					playerView.getPlayerName());
			drawTextCentered(canvas, (int) (this.left + playerView.getLeft()),
					this.playerWidth, (int) (this.top + playerView.getTop()
							+ ((this.playerIcon.getWidth() / 3) * 2) + 13),
					String.valueOf(playerView.getNumCards()));

			switch (playerView.getPlayerRole()) {

			case TARGET:
				PAINT_RECT.setColor(Color.RED);
				canvas.drawRect(playerView.getLeft(), playerView.getTop(),
						playerView.getLeft() + this.playerWidth,
						playerView.getTop() + this.playerHeight, PAINT_RECT);
				break;

			case ATTACKER:
				PAINT_RECT.setColor(Color.BLUE);
				canvas.drawRect(playerView.getLeft(), playerView.getTop(),
						playerView.getLeft() + this.playerWidth,
						playerView.getTop() + this.playerHeight, PAINT_RECT);
				break;

			case FIRST_ATTACKER:
				PAINT_RECT.setColor(Color.GREEN);
				canvas.drawRect(playerView.getLeft(), playerView.getTop(),
						playerView.getLeft() + this.playerWidth,
						playerView.getTop() + this.playerHeight, PAINT_RECT);
				break;

			}
		}
	}

	public int getPlayerViewWidth() {
		return this.playerWidth + PLAYER_MARGIN;
	}

	public int getPlayerViewHeight() {
		return this.playerHeight + PLAYER_MARGIN;
	}

	public PlayerView getTargetPlayerView() {
		for (PlayerView player : this.players) {
			if (player.getPlayerRole() == PlayerRole.TARGET) {
				return player;
			}
		}

		return null;
	}

	private void updatePlayers() {
		int numPlayers = this.players.size();
		int lowerBound = 0;
		int upperBound = numPlayers;
		if (numPlayers > 2) {
			PlayerView playerLeft = this.players.get(0);
			playerLeft.setLeft(this.left);
			playerLeft.setTop(this.top + this.playerHeight
					+ (2 * PLAYER_MARGIN));

			PlayerView playerRight = this.players.get(numPlayers - 1);
			playerRight.setLeft((this.left + this.width) - this.playerWidth);
			playerRight.setTop(playerLeft.getTop());

			lowerBound = 1;
			upperBound = numPlayers - 1;
		}

		int spacer = this.width / ((upperBound - lowerBound) + 1);
		for (int i = lowerBound; i < upperBound; i++) {
			PlayerView player = this.players.get(i);
			player.setLeft((this.left + (((i - lowerBound) + 1) * spacer))
					- (this.playerWidth / 2));
			player.setTop(this.top);
		}
	}

	public void addPlayer(PlayerView player) {
		this.players.add(player);

		// correct player positions
		this.updatePlayers();
	}
}