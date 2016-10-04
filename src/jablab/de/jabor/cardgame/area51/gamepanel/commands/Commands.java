package jablab.de.jabor.cardgame.area51.gamepanel.commands;

/**
 * network commands, parameters and error codes
 * 
 * @author development.Jabor
 * 
 */
public class Commands {

	/**
	 * server commands and parameters
	 * 
	 * @author development.Jabor
	 * 
	 */
	public static class Server {

		/**
		 * server sends it's version at first<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>version</b>: server version
		 */
		public static final String VERSION = "SVER";

		/**
		 * server informs about error occurred<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>errorCode</b>: error identifier
		 */
		public static final String ERROR = "SERR";

		/**
		 * server sends necessary data<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>maxPlayers</b>: maximum number of players connected<br>
		 * 2. <b>clientID</b>: client identifier<br>
		 * 3. <b>ownIndex</b>: client current index
		 */
		public static final String WELCOME = "SWEL";

		/**
		 * server informs about a new player<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>clientID</b>: client identifier<br>
		 * 2. <b>player name</b>: player name
		 */
		public static final String NEW_PLAYER = "SNWP";

		/**
		 * server informs about a card being drawn<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>clientID</b>: client identifier<br>
		 * 2. <b>cardID</b>: card identifier (zero if client is not drawing the
		 * card itself)
		 */
		public static final String DRAW_CARD = "SDRW";

		/**
		 * server informs that the game has been started<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>trump</b>: trump card type
		 */
		public static final String GAME_START = "SGST";

		/**
		 * server informs about the player roles<br>
		 * <br>
		 * parameters:<br>
		 * <b>3 x 2 = 6</b><br>
		 * 1. <b>clientID</b>: client identifier<br>
		 * 2. <b>role</b>: player role
		 */
		public static final String CRR_ROLES = "SCRR";

		/**
		 * server informs about a player playing a card<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>clientID</b>: client identifier<br>
		 * 2. <b>cardID</b>: card identifier<br>
		 * 3. <b>slotID</b>: slot identifier
		 */
		public static final String PLAYER_PLAYS_CARD = "SPPC";

		/**
		 * server informs that the target aborted it's move
		 */
		public static final String TARGET_ABORTED = "STAB";

		/**
		 * server informs that the round has been finished<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>targetWon</b>: target won flag
		 */
		public static final String ROUND_FINISHED = "SRFN";

		/**
		 * server informs about the game end<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>numClients</b>: number of clients<br>
		 * 2..X. <b>clientID</b>: client identifier
		 */
		public static final String GAME_FINISHED = "SGFN";

	}

	/**
	 * client commands and parameters
	 * 
	 * @author development.Jabor
	 * 
	 */
	public static class Client {

		/**
		 * client tries to login after versions have been compared<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>player name</b>: player name chosen
		 */
		public static final String LOGIN = "CLOG";

		/**
		 * client wants to play a card<br>
		 * <br>
		 * parameters:<br>
		 * 1. <b>cardID</b>: card identifier<br>
		 * 2. <b>slotID</b>: slot identifier, set by target <b>only</b>
		 */
		public static final String PLAY_CARD = "CPLY";

		/**
		 * client wants to abort the round
		 */
		public static final String PLAY_ABORT = "CABT";
	}

	/**
	 * error codes
	 * 
	 * @author development.Jabor
	 * 
	 */
	public static class ErrorCode {

		/**
		 * no error occurred
		 */
		public static final int NO_ERROR = 0;

		/**
		 * client join error codes
		 * 
		 * @author development.Jabor
		 * 
		 */
		public static class Join {

			/**
			 * server is full
			 */
			public static final int SERVER_FULL = 100;

			/**
			 * game is already running
			 */
			public static final int GAME_RUNNING = 101;

		}

		/**
		 * client login error codes
		 * 
		 * @author development.Jabor
		 * 
		 */
		public static class Login {

			/**
			 * name contains invalid characters<br>
			 * <b>not used</b>
			 */
			public static final int INVALID = 200;

			/**
			 * client already logged in
			 */
			public static final int LOGGED_IN = 201;

			/**
			 * name used by another client
			 */
			public static final int IN_USE = 202;

		}

		/**
		 * client play card error codes
		 * 
		 * @author development.Jabor
		 * 
		 */
		public static class Play {

			/**
			 * the game is not running yet
			 */
			public static final int GAME_NOT_RUNNING = 300;

			/**
			 * client is not allowed to play a card
			 */
			public static final int CLIENT_NOT_ALLOWED = 301;

			/**
			 * client has aborted its move before
			 */
			public static final int CLIENT_ABORTED = 302;

			/**
			 * round must be started before first attacker can abort
			 */
			public static final int ROUND_NOT_INITIALIZED = 303;

			/**
			 * round can be started by first attacker only
			 */
			public static final int FIRST_ATTACKER_ONLY = 304;

			/**
			 * no free slot available
			 */
			public static final int SLOTS_FULL = 305;

			/**
			 * slot identifier is not valid yet
			 */
			public static final int SLOT_INVALID = 306;

			/**
			 * slot has already been defended
			 */
			public static final int SLOT_DEFENDED = 307;

			/**
			 * card being played is not owned
			 */
			public static final int CARD_NOT_OWNED = 308;

			/**
			 * card being played is not allowed yet
			 */
			public static final int CARD_NOT_ALLOWED = 309;

			/**
			 * card being played does not match attacking type
			 */
			public static final int CARD_MISMATCH = 310;

			/**
			 * card being played is too low to defend the slot
			 */
			public static final int CARD_TOO_LOW = 311;

			/**
			 * target can not pass anymore
			 */
			public static final int PASSING_NOT_ALLOWED = 312;

			/**
			 * target's next player does not have enough cards
			 */
			public static final int PASSING_NOT_ENOUGH_CARDS = 313;

		}

		/**
		 * client disconnect error codes
		 * 
		 * @author development.Jabor
		 * 
		 */
		public static class Disconnect {

			/**
			 * client was not logged in
			 */
			public static final int NOT_LOGGED_IN = 900;

		}

		/**
		 * parse error code to error message
		 * 
		 * @param errorCode
		 *            error code to be parsed
		 * @return error message
		 */
		public static String parse(int errorCode) {
			switch (errorCode) {

			case Commands.ErrorCode.Join.SERVER_FULL:
				return "Der Server ist voll. Sie können zur Zeit nicht beitreten!";

			case Commands.ErrorCode.Join.GAME_RUNNING:
				return "Der Server befindet sich bereits im Spielmodus. Sie können zur Zeit nicht beitreten!";

			case Commands.ErrorCode.Login.INVALID:
				return "Der Benutzername enthält ungültige Zeichen! Bitte versuchen Sie es mit einem anderen.";

			case Commands.ErrorCode.Login.LOGGED_IN:
				return "Sie sind bereits eingeloggt!";

			case Commands.ErrorCode.Login.IN_USE:
				return "Der Benutzername wird bereits verwendet! Bitte versuchen Sie es mit einem anderen.";

			case Commands.ErrorCode.Play.GAME_NOT_RUNNING:
				return "OUT OF SYCN: game not running!";

			case Commands.ErrorCode.Play.CLIENT_ABORTED:
				return "Sie haben Ihren Zug bereits beendet!";

			case Commands.ErrorCode.Play.CLIENT_NOT_ALLOWED:
				return "Sie sind nicht am Zug!";

			case Commands.ErrorCode.Play.ROUND_NOT_INITIALIZED:
				return "Sie müssen die Runde zunächst beginnen!";

			case Commands.ErrorCode.Play.FIRST_ATTACKER_ONLY:
				return "Sie sind nicht am Zug! Warten Sie auf den Zug des ersten Angreifers.";

			case Commands.ErrorCode.Play.SLOTS_FULL:
				return "Es kann keine weitere Karte auf den Spieler gespielt werden!";

			case Commands.ErrorCode.Play.SLOT_INVALID:
				return "OUT OF SYNC: slot identifier invalid!";

			case Commands.ErrorCode.Play.SLOT_DEFENDED:
				return "Sie haben diese Karte bereits abgewehrt!";

			case Commands.ErrorCode.Play.CARD_NOT_OWNED:
				return "OUT OF SYNC: card identifier invalid!";

			case Commands.ErrorCode.Play.CARD_NOT_ALLOWED:
				return "Diese Karte können Sie zur Zeit nicht spielen!";

			case Commands.ErrorCode.Play.CARD_MISMATCH:
				return "Diese Karte hat nicht die richtige Spielfarbe!";

			case Commands.ErrorCode.Play.CARD_TOO_LOW:
				return "Der Wert dieser Karte ist nicht hoch genug!";

			case Commands.ErrorCode.Play.PASSING_NOT_ALLOWED:
				return "Sie können jetzt nicht mehr schieben!";

			case Commands.ErrorCode.Play.PASSING_NOT_ENOUGH_CARDS:
				return "Der nächste Spieler hat nicht genug Karten um zu schieben!";

			default:
				return "Es ist ein unbekannter Fehler aufgetreten: #"
						+ errorCode;

			}
		}

	}

	/**
	 * universal command separator
	 */
	public static final String SEPARATOR = ";";

}