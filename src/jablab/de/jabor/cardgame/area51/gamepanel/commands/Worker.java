package jablab.de.jabor.cardgame.area51.gamepanel.commands;


import java.util.concurrent.BlockingQueue;

import android.util.Log;

/**
 * command worker executing commands via handler
 * 
 * @author development.Jabor
 * 
 */
public class Worker implements Runnable {

	/**
	 * thread-safe command queue
	 */
	private BlockingQueue<Command> commandQueue;

	/**
	 * command handler executing stacked commands
	 */
	private CommandHandler handler;

	/**
	 * create new command worker
	 * 
	 * @param commandQueue
	 *            thread-safe command queue
	 * @param handler
	 *            command handler executing stacked commands
	 */
	public Worker(BlockingQueue<Command> commandQueue, CommandHandler handler) {
		this.commandQueue = commandQueue;
		this.handler = handler;
	}

	/**
	 * execute command stacked first
	 */
	public void run() {
		try {
			while (true) {
				final Command command = this.commandQueue.take();
				this.handler.executeCommand(command);
			}
		} catch (InterruptedException e) {
			Log.d("Worker|run", "failed to take command!\n" + e.getMessage());
		}
	}

}