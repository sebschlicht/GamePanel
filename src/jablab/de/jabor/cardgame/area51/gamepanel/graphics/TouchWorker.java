package jablab.de.jabor.cardgame.area51.gamepanel.graphics;

import java.util.concurrent.BlockingQueue;

import android.view.MotionEvent;

public class TouchWorker implements Runnable {

	private GamePanel gamePanel;

	private BlockingQueue<MotionEvent> touchQueue;

	public TouchWorker(GamePanel gamePanel,
			BlockingQueue<MotionEvent> touchQueue) {
		this.gamePanel = gamePanel;
		this.touchQueue = touchQueue;
	}

	public void run() {
		try {
			while (true) {
				MotionEvent motionEvent = this.touchQueue.take();
				this.gamePanel.handleMotionEvent(motionEvent);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}