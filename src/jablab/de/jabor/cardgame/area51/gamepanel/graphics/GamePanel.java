package jablab.de.jabor.cardgame.area51.gamepanel.graphics;

import jablab.de.jabor.cardgame.area51.gamepanel.R;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;
import jablab.de.jabor.cardgame.area51.gamepanel.client.ClientGUI;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Player;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Commands;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.handPanel.HandPanel;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.playerPanel.PlayerPanel;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.playerPanel.PlayerView;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.slotPanel.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.slotPanel.SlotPanel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

  private static final int[] RESOURCES = { R.drawable.cardback,
      R.drawable.diamond_7, R.drawable.diamond_8, R.drawable.diamond_9,
      R.drawable.diamond_10, R.drawable.diamond_j, R.drawable.diamond_q,
      R.drawable.diamond_k, R.drawable.diamond_a, R.drawable.heart_7,
      R.drawable.heart_8, R.drawable.heart_9, R.drawable.heart_10,
      R.drawable.heart_j, R.drawable.heart_q, R.drawable.heart_k,
      R.drawable.heart_a, R.drawable.spade_7, R.drawable.spade_8,
      R.drawable.spade_9, R.drawable.spade_10, R.drawable.spade_j,
      R.drawable.spade_q, R.drawable.spade_k, R.drawable.spade_a,
      R.drawable.club_7, R.drawable.club_8, R.drawable.club_9,
      R.drawable.club_10, R.drawable.club_j, R.drawable.club_q,
      R.drawable.club_k, R.drawable.club_a, R.drawable.ic_launcher };

  private static final Bitmap[] TEXTURES = new Bitmap[RESOURCES.length];

  public static final float SLOT_RATIO = 0.7f;

  private BlockingQueue<MotionEvent> touchQueue;

  private Thread touchWorker;

  private UpdateThread updateThread;

  private Client client;

  private PlayerPanel playerPanel;

  private SlotPanel slotPanel;

  private HandPanel handPanel;

  private Slot hoveredSlot;

  private ClientGUI gui;

  public GamePanel(Context context, ClientGUI gui) {
    super(context);
    this.gui = gui;

    // start touch handling
    this.touchQueue = new LinkedBlockingQueue<MotionEvent>();
    this.touchWorker = new Thread(new TouchWorker(this, this.touchQueue));
    this.touchWorker.start();

    // add surface callback
    this.getHolder().addCallback(this);

    // create drawing thread
    this.updateThread = new UpdateThread(this.getHolder(), this);

    // surface can capture motion events
    this.setFocusable(true);

    // load textures
    for (int i = 0; i < RESOURCES.length; i++) {
      TEXTURES[i] = BitmapFactory.decodeResource(this.getResources(),
          RESOURCES[i]);
    }
  }

  @Override
  public boolean onTouchEvent(final MotionEvent motionEvent) {
    this.touchQueue.add(motionEvent);
    return true;
  }

  public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    // may not get called unless screen orientation lock released
  }

  public void surfaceCreated(SurfaceHolder arg0) {
    int playerPanelHeight = (int) (this.getHeight() * 0.6);
    this.playerPanel = new PlayerPanel(this.getWidth(), playerPanelHeight, 0,
        0, TEXTURES[TEXTURES.length - 1], TEXTURES[0]);

    int playerViewWidth = this.playerPanel.getPlayerViewWidth();
    int playerViewHeight = this.playerPanel.getPlayerViewHeight();
    this.slotPanel = new SlotPanel(this.getWidth() - (2 * playerViewWidth),
        playerPanelHeight - playerViewHeight, playerViewWidth, playerViewHeight);

    this.handPanel = new HandPanel(this.getWidth(), this.getHeight()
        - playerPanelHeight, 0, playerPanelHeight);

    this.updateThread.setRunning(true);
    this.updateThread.start();
  }

  public void surfaceDestroyed(SurfaceHolder arg0) {
    // stop drawing
    boolean drawing = true;
    this.updateThread.setRunning(false);

    while (drawing) {
      try {
        this.updateThread.join();
        drawing = false;
      } catch (InterruptedException e) {
        // retry
      }
    }
  }

  public void setUpdateState(boolean state) {
    this.updateThread.setRunning(state);
  }

  public void updateGame() {
    // TODO
  }

  public void drawGame(float interpolation, Canvas canvas) {
    // refill screen with background color
    canvas.drawColor(Color.BLACK);

    // draw static content
    this.playerPanel.paint(canvas);
    this.slotPanel.paint(canvas);
    this.handPanel.paint(canvas);

    // draw generic content
    // TODO
  }

  public void handleMotionEvent(MotionEvent motion) {
    float x = motion.getX();
    float y = motion.getY();
    Card card;

    switch (motion.getAction()) {
    case MotionEvent.ACTION_DOWN:
      this.handPanel.prepareCardForMovement(x, y);
      break;

    case MotionEvent.ACTION_MOVE:
      card = this.handPanel.getMovedCard();
      if (card != null) {
        card.setX(x);
        card.setY(y);

        boolean unhover = true;
        if (!this.handPanel.handleMovement(card)) {
          if (this.client.getRole() == PlayerRole.TARGET) {
            // target player can select slot to play card to
            Slot slot = this.slotPanel.getSlotAtPosition(x, y);
            if (slot != null) {
              if ((this.hoveredSlot == null) || (this.hoveredSlot != slot)) {
                if (this.hoveredSlot != null) {
                  this.hoveredSlot.setHovered(false);
                }

                slot.setHovered(true);
                this.hoveredSlot = slot;
              }

              unhover = false;
            }

            if (unhover && (this.hoveredSlot != null)) {
              this.hoveredSlot.setHovered(false);
              this.hoveredSlot = null;
            }
          } else if (this.client.getRole() != PlayerRole.NORMAL) {
            // attacking players play cards when leaving hand panel
            // TODO: mark card as being played
            this.playCard(this.handPanel.getMovedCard(), -1);
            this.handPanel.unprepareCard();
          }
        }
      }
      break;

    case MotionEvent.ACTION_UP:
      if (this.hoveredSlot != null) {
        card = this.handPanel.getMovedCard();
        int slotID = this.hoveredSlot.getId();
        this.playCard(card, slotID);

        this.hoveredSlot.setHovered(false);
        this.hoveredSlot = null;
      }

      this.handPanel.unprepareCard();
      break;
    }
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void addPlayer(Player player) {
    PlayerView playerView = new PlayerView(new Coordinates(0, 0), player);

    synchronized (this.getHolder()) {
      this.playerPanel.addPlayer(playerView);
    }
  }

  public void addCard(int cardID) {
    Bitmap texture = Bitmap.createScaledBitmap(TEXTURES[cardID],
        this.handPanel.getSlotWidth(), this.handPanel.getSlotHeight(), true);
    Card gCard = new Card(cardID, new Coordinates(0, 0));
    gCard.setTexture(texture);

    synchronized (this.getHolder()) {
      this.handPanel.addCard(gCard, true);
    }
  }

  public void setCurrentRole(PlayerRole role) {
    // TODO: show role to user

    // update slots
    synchronized (this.getHolder()) {
      if (this.client.getTargetId() != this.client.getId()) {
        PlayerView targetView = this.playerPanel.getTargetPlayerView();
        if (targetView != null) {
          this.slotPanel.setNumSlots(targetView.getNumCards());
        } else {
          // TODO: ensure this never happens
          Log.d("GamePanel|setCrrRole", "no target view!");
        }
      } else {
        // TODO: enable target to pass even if local slots are full
        // but next player has at least one free
        this.slotPanel.setNumSlots(this.handPanel.getNumCards());
      }
    }

  }

  private void playCard(Card card, int slotID) {
    if (this.client.getTargetId() != this.client.getId()) {
      slotID = -1;
    }

    int playError = this.client.playCard(card.getId(), slotID);
    if (playError != Commands.ErrorCode.NO_ERROR) {
      this.gui.showInfo(Commands.ErrorCode.parse(playError));
    } else {
      // TODO: set card being played -> no movement anymore
    }
  }

  public void playCard(Player player,
      jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card card,
      jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot slot) {

    synchronized (this.getHolder()) {
      if (player == null) {
        this.handPanel.removeCardById(card.getId());
      }

      // TODO: check if not thread-safe
      Card playedCard = new Card(card.getId(), new Coordinates(0, 0));
      this.slotPanel.addCardToSlot(slot.getId(), playedCard,
          TEXTURES[card.getId()]);
    }
  }

  public void roundFinished(boolean targetWon) {
    synchronized (this.getHolder()) {
      // TODO: check if not thread-safe
      this.slotPanel.reset();
    }
  }

  public void reset() {
    this.touchQueue.clear();

    synchronized (this.getHolder()) {
      // check for null pointer when canceling startup before
      // onSurfaceCreated got called
      if (this.handPanel != null) {
        this.handPanel.clear();
      }
      if (this.slotPanel != null) {
        this.slotPanel.reset();
      }
    }
  }

  public void cleanUp() {
    this.reset();
    synchronized (this.getHolder()) {
      // TODO: clear player panel
    }

    this.touchWorker.interrupt();
    // TODO: check if update thread is being destroyed by surfaceDestroyed
  }

}