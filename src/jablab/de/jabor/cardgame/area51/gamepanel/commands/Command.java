package jablab.de.jabor.cardgame.area51.gamepanel.commands;

/**
 * basic command to be executed in UI context
 * 
 * @author development.Jabor
 * 
 */
public abstract class Command implements Runnable {

	/**
	 * execute the command's specific execution implementation
	 */
	public abstract void run();

}