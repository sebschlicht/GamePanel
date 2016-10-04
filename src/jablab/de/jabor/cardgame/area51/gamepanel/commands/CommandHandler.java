package jablab.de.jabor.cardgame.area51.gamepanel.commands;

/**
 * command handler executing commands stacked and taken before
 * 
 * @author development.Jabor
 * 
 */
public interface CommandHandler {

	/**
	 * execute a command at correct thread (UI)
	 * 
	 * @param command
	 *            command to be executed
	 */
	public void executeCommand(Command command);

}