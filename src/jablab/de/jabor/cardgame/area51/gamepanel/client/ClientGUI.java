package jablab.de.jabor.cardgame.area51.gamepanel.client;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.CommandHandler;

import java.util.List;

/**
 * client's user interface additionally handling commands
 * 
 * @author development.Jabor
 * 
 */
public interface ClientGUI extends CommandHandler {

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

	/**
	 * display errors sent from server
	 * 
	 * @param errorMessage
	 *            error message
	 */
	public void serverError(String errorMessage);

	/**
	 * a player has joined the game
	 * 
	 * @param player
	 *            player joined
	 */
	public void serverNewPlayer(Player player);

	/**
	 * a card has been drawn
	 * 
	 * @param player
	 *            player identifier <b>may be null</b>
	 * @param card
	 *            card drawn <b>may be null</b>
	 */
	public void serverDrawCard(Player player, Card card);

	/**
	 * the game has been started
	 * 
	 * @param trump
	 *            trump card
	 */
	public void serverGameStart(Card trump);

	/**
	 * the player role has changed
	 * 
	 * @param role
	 *            current player role
	 */
	public void serverCurrentRole(PlayerRole role);

	/**
	 * a player has played a card
	 * 
	 * @param player
	 *            player who played the card
	 * @param card
	 *            card played
	 * @param slot
	 *            slot the card has been played to
	 */
	public void serverPlayerPlaysCard(Player player, Card card, Slot slot);

	/**
	 * target player aborted it's move
	 * 
	 * @param targetName
	 *            target player name
	 */
	public void serverTargetAborted(String targetName);

	/**
	 * current round has been finished
	 * 
	 * @param targetWon
	 *            target won flag
	 */
	public void serverRoundFinished(boolean targetWon);

	/**
	 * match has been finished
	 * 
	 * @param winners
	 *            players ordered by winning position
	 */
	public void serverGameFinished(List<Player> winners);

}