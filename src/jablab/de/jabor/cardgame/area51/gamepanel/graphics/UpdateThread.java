package jablab.de.jabor.cardgame.area51.gamepanel.graphics;

import java.util.Date;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * separate thread continuously updating and drawing the game panel
 * 
 * @author development.Jabor
 * 
 */
public class UpdateThread extends Thread {

  /**
   * game updates per second
   */
  private static final int TICKS_PER_SECOND = 25;

  /**
   * milliseconds to skip between game updates
   */
  private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;

  /**
   * maximum skipped frames before drawing the game
   */
  private static final int MAX_FRAMESKIPS = 5;

  /**
   * surface holder to draw to
   */
  private final SurfaceHolder surfaceHolder;

  /**
   * game panel to be drawn
   */
  private final GamePanel gamePanel;

  /**
   * drawing flag
   */
  private boolean running;

  /**
   * canvas to draw to
   */
  private Canvas canvas;

  /**
   * create a new update thread
   * 
   * @param surfaceHolder
   *          surface holder to draw to
   * @param gamePanel
   *          game panel to be drawn
   */
  public UpdateThread(final SurfaceHolder surfaceHolder,
      final GamePanel gamePanel) {
    this.surfaceHolder = surfaceHolder;
    this.gamePanel = gamePanel;
  }

  /**
   * append new thread state
   * 
   * @param running
   *          true - thread will draw the game panel<br>
   *          false - otherwise
   */
  public void setRunning(final boolean running) {
    this.running = running;
  }

  private long getCrrMs() {
    return new Date().getTime();
  }

  @Override
  public void run() {
    long nextTick = this.getCrrMs();
    int frameSkips = 0;
    float interpolation;

    while (this.running) {
      frameSkips = 0;

      while ((this.getCrrMs() > nextTick)
          && (frameSkips < UpdateThread.MAX_FRAMESKIPS)) {

        synchronized (this.surfaceHolder) {
          // update game
          this.gamePanel.updateGame();
        }

        nextTick += UpdateThread.SKIP_TICKS;
        frameSkips += 1;
      }

      interpolation = (this.getCrrMs() - (nextTick - UpdateThread.SKIP_TICKS))
          / UpdateThread.SKIP_TICKS;
      this.drawGame(interpolation);
    }
  }

  private void drawGame(final float interpolation) {
    // draw the game panel thread-safe
    this.canvas = null;
    try {
      this.canvas = this.surfaceHolder.lockCanvas(null);
      synchronized (this.surfaceHolder) {
        this.gamePanel.drawGame(interpolation, this.canvas);
      }
    } finally {
      if (this.canvas != null) {
        this.surfaceHolder.unlockCanvasAndPost(this.canvas);
      }
    }
  }
}