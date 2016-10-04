package jablab.de.jabor.cardgame.area51.gamepanel.activities;

import jablab.de.jabor.cardgame.area51.gamepanel.R;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Card;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.PlayerRole;
import jablab.de.jabor.cardgame.area51.gamepanel.cardgame.Slot;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Client;
import jablab.de.jabor.cardgame.area51.gamepanel.client.ClientGUI;
import jablab.de.jabor.cardgame.area51.gamepanel.client.Player;
import jablab.de.jabor.cardgame.area51.gamepanel.commands.Command;
import jablab.de.jabor.cardgame.area51.gamepanel.graphics.GamePanel;
import jablab.de.jabor.cardgame.area51.gamepanel.server.Server;
import jablab.de.jabor.cardgame.area51.gamepanel.server.ServerGUI;
import jablab.de.jabor.cardgame.area51.gamepanel.test.DebuggingEnvironment;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

/**
 * card game activity
 * 
 * @author development.Jabor
 * 
 */
public class GamePanelView extends Activity implements ServerGUI, ClientGUI {

  /**
   * game client
   */
  private Client client;

  /**
   * game server<br>
   * <b>may be null</b>
   */
  private Server server;

  /**
   * game panel shown
   */
  private GamePanel gamePanel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
          .permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }

    // create game panel
    this.gamePanel = new GamePanel(this, this);
    this.setContentView(this.gamePanel);

    // create server
    try {
      this.server = new Server(this, 5);
    } catch (IOException e) {
      this.showError(e.getMessage());
    }

    // create client
    try {
      this.client = new Client(this, "localhost", "testy", false);
      this.gamePanel.setClient(this.client);
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // DEBUG
    this.addSomeClients();
  }

  private void addSomeClients() {
    final String[] names = { "Eric Eric!", "Glon_MM28", "LowGravity", "MadMax",
        "sry_about_that" };
    final ClientGUI gui = new DebuggingEnvironment();

    final Timer timer = new Timer();
    final TimerTask addClient = new TimerTask() {

      private int i = 0;

      @Override
      public void run() {
        try {
          if (this.i < names.length) {
            new Client(gui, "localhost", names[this.i++], true);
          } else {
            timer.cancel();
            GamePanelView.this.server.startGame();
          }
        } catch (UnknownHostException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    };

    timer.scheduleAtFixedRate(addClient, 1500L, 500L);
  }

  @Override
  public void onResume() {
    super.onResume();

    // restart drawing
    this.gamePanel.setUpdateState(true);
  }

  @Override
  public void onPause() {
    super.onPause();

    // stop drawing
    this.gamePanel.setUpdateState(false);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    this.gamePanel.cleanUp();

    // stop server and client
    this.server.close();
    this.client.close();

    this.server = null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getMenuInflater();
    inflater.inflate(R.menu.menu_game, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    // DEBUG
    // set abort item state
    MenuItem mAbort = menu.getItem(0);
    mAbort.setEnabled(this.client != null);

    // set restart item visibility
    MenuItem mRestart = menu.getItem(1);
    mRestart.setVisible((this.server != null) && !this.server.isGameRunning());

    // set stop item title
    MenuItem mStop = menu.getItem(2);
    if (this.server != null) {
      mStop.setTitle(R.string.mStopServer);
    } else {
      mStop.setTitle(R.string.mDisconnect);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

    // DEBUG
    case R.id.mAbort:
      this.client.abort();
      return true;

    case R.id.mRestart:
      this.server.startGame();
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * show toast from different threads than UI thread
   * 
   * @param message
   *          message to be displayed
   * @param duration
   *          duration flag
   */
  private void showToastThreadSafe(final String message, final int duration) {
    this.runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(GamePanelView.this, message, duration).show();
      }
    });
  }

  public void executeCommand(Command command) {
    this.runOnUiThread(command);
  }

  public void showError(String errorMessage) {
    Log.e("error", "\"" + errorMessage + "\"");
    this.finish();
  }

  public void showInfo(String message) {
    this.showToastThreadSafe(message, Toast.LENGTH_SHORT);
  }

  public void log(String tag, String message) {
    Log.d(tag, message);
  }

  public void serverError(String errorMessage) {
    this.showToastThreadSafe(errorMessage, Toast.LENGTH_LONG);
  }

  public void serverNewPlayer(Player player) {
    this.gamePanel.addPlayer(player);
  }

  public void serverDrawCard(Player player, Card card) {
    // TODO: switch in game panel when adding animations
    if (card != null) {
      this.gamePanel.addCard(card.getId());
    }
  }

  public void serverGameStart(Card trump) {
    this.showInfo("The battle has begun... Trump: " + trump);
  }

  public void serverCurrentRole(PlayerRole role) {
    // TODO: implement this in game panel when info method created
    if (role == PlayerRole.TARGET) {
      this.showInfo("May the force be with you!");
    } else if (role == PlayerRole.ATTACKER) {
      this.showInfo("Get ready! Ready? Hold it...");
    } else if (role == PlayerRole.FIRST_ATTACKER) {
      this.showInfo("Let's kick some asses!");
    }

    this.gamePanel.setCurrentRole(role);
  }

  // TODO: change slot panel to use original slots
  public void serverPlayerPlaysCard(Player player, Card card, Slot slot) {
    // play card and maybe remove card from hand panel
    this.gamePanel.playCard(player, card, slot);
  }

  public void serverTargetAborted(String targetName) {
    if ((this.client.getRole() == PlayerRole.ATTACKER)
        || (this.client.getRole() == PlayerRole.FIRST_ATTACKER)) {
      this.showInfo("\"" + targetName + "\" aborted his move... Get him!");
    }
  }

  // TODO: change GUI logic and let game panel be the GUI instead
  public void serverRoundFinished(boolean targetWon) {
    this.gamePanel.roundFinished(targetWon);
  }

  public void serverGameFinished(List<Player> winners) {
    this.showInfo("\"" + winners.get(0).getName() + "\" won the game!");

    // TODO: delegate to game panel
  }
}