package jablab.de.jabor.cardgame.area51.gamepanel.server;

import jablab.de.jabor.cardgame.area51.gamepanel.commands.CommandHandler;

/**
 * server's user interface additionally handling commands
 * 
 * @author development.Jabor
 * 
 */
public interface ServerGUI extends CommandHandler {

	/**
	 * add a log entry
	 * 
	 * @param tag
	 *            log entry title
	 * @param message
	 *            log entry content
	 */
	public void log(String tag, String message);

	/**
	 * show an error occurred
	 * 
	 * @param errorMessage
	 *            error message to be displayed
	 */
	public void showError(String errorMessage);

	/**
	 * show an info text
	 * 
	 * @param message
	 *            info message to be displayed
	 */
	public void showInfo(String message);

}