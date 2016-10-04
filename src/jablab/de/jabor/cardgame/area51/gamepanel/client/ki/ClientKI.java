package jablab.de.jabor.cardgame.area51.gamepanel.client.ki;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.CardType;

/**
 * KI interface to be implemented by each KI strategy
 * 
 * @author development.Jabor
 * 
 */
public interface ClientKI {

	/**
	 * set trump type
	 * 
	 * @param trump
	 *            current trump type
	 */
	public void setTrump(CardType trump);

	/**
	 * start to play a card if the situation allows
	 */
	public void start();

}