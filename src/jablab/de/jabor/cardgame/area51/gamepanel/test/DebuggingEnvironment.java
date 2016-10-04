package jablab.de.jabor.cardgame.area51.gamepanel.test;

import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.client.ClientGUI;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Player;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;

import java.util.List;

public class DebuggingEnvironment implements ClientGUI {
	private static boolean ENABLED = false;

	public void executeCommand(final Command command) {
		final Thread workerThread = new Thread(command);
		workerThread.start();
		try {
			workerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void log(final String tag, final String message) {
		if (ENABLED) {
			System.out.println(tag + "\t" + message);
		}
	}

	public void showError(final String errorMessage) {
		if (ENABLED) {
			System.err.println(errorMessage);
		}
	}

	public void showInfo(final String message) {
		if (ENABLED) {
			System.out.println(message);
		}
	}

	public void serverError(final String errorMessage) {
		if (ENABLED) {
			System.out.println("serverError: \"" + errorMessage + "\"");
		}
	}

	public void serverNewPlayer(final Player player) {
		if (ENABLED) {
			System.out.println("player joined: \"" + player.getName() + "\" (#"
					+ player.getClientId() + ")");
		}
	}

	public void serverDrawCard(final Player player, final Card card) {
		if (ENABLED) {
			if (card == null) {
				System.out.println("\"" + player.getName() + "\""
						+ " drew a card");
			} else {
				System.out.println("card drawn: " + card + " (" + card.getId()
						+ ")");
			}
		}
	}

	public void serverGameStart(Card trump) {
		if (ENABLED) {
			System.out.println("game has been started! trump: " + trump);
		}
	}

	public void serverCurrentRole(PlayerRole role) {
		if (ENABLED) {
			System.out.println("new role: " + role);
		}
	}

	public void serverPlayerPlaysCard(Player player, Card card, Slot slot) {
		if (ENABLED) {
			if (player != null) {
				System.out.println("\""
						+ player.getName()
						+ "\" played the "
						+ card
						+ ((slot.getAntiCard() == null) ? ("")
								: (" to defend against the " + slot
										.getAntiCard())));
			} else {
				System.out.println(card
						+ " successfully played"
						+ ((slot.getAntiCard() == null) ? ("")
								: (" to defend against the " + slot
										.getAntiCard())));
			}
		}
	}

	public void serverTargetAborted(String targetName) {
		if (ENABLED) {
			System.out.println("\"" + targetName + "\" aborted his move!");
		}
	}

	public void serverRoundFinished(boolean targetWon) {
		if (ENABLED) {
			System.out.println("round has been finished!");
		}
	}

	public void serverGameFinished(List<Player> winners) {
		if (ENABLED) {
			System.out.println("\"" + winners.get(0).getName()
					+ "\" won the game!");
		}
	}

}