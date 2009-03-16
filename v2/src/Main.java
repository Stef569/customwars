import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.testdata.TestData;
import com.customwars.client.ui.debug.DebugEnvironment;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import slick.HardCodedGame;
import slick.TestStates;
import tools.ColorUtil;

/**
 * Starts The client
 * If DEBUG is on a hardcoded testGame will be loaded
 * if DEBUG_GUI is on 2 debug windows will be visible
 *
 * @author stefan
 */
public class Main {
  private static final Logger logger = Logger.getLogger(Main.class);
  private static boolean DEBUG = true;
  private static boolean DEBUG_GUI;
  private static ResourceManager resources;

  // Script
  private static JConsole console;
  private static Interpreter bsh;
  private static StateSession stateSession;
  private static int startStateID;

  public static void main(String[] argv) {
    handleArgs(argv);

    try {
      LoadingList.setDeferredLoading(false);
      resources = new ResourceManager();

      Config config = new Config(resources);
      config.configure();
      logger.info("Starting up");
      new Main();
    } catch (Exception e) {
      logger.fatal("Startup failure", e);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private static void handleArgs(String[] args) {
    int i = 0;
    String arg;

    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];

      // use this type of check for "wordy" arguments
      if (arg.equals("-showdebuggui")) {
        DEBUG_GUI = true;
      } else if (arg.equals("-debug")) {
        DEBUG = true;
      }

      // use this type of check for arguments that require arguments
      else if (arg.equals("-startstate")) {
        if (i < args.length)
          startStateID = Integer.parseInt(args[i++]);
        else
          startStateID = 0;
      }
    }
  }

  public Main() throws SlickException {
    logger.info("init script");
    console = initScript();

    if (DEBUG) {
      logger.info("Init debug Mode");
      initDebugMode();
    }

    AppGameContainer appGameContainer = new AppGameContainer(new TestStates(startStateID, stateSession, resources));
    appGameContainer.setDisplayMode(640, 480, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.start();
  }

  private JConsole initScript() {
    JConsole console = new JConsole();
    bsh = new Interpreter(console);
    return console;
  }

  private void initDebugMode() {
    TestData.storeTestData();

    logger.info("init hard coded game");
    Game game = HardCodedGame.getGame();

    stateSession = new StateSession();
    stateSession.setGame(game);
    stateSession.setMap(game.getMap());  // Later set by a mapSelectState

    if (DEBUG_GUI) {
      DebugEnvironment debugEnvironment = new DebugEnvironment(console, bsh, game);
      debugEnvironment.show();
    }

    try {
      initScriptObjects(game, resources);
    } catch (EvalError evalError) {
      logger.fatal("Script init error, not all live objects will be available", evalError);
    }
  }

  /**
   * We add various objects to beanshell, accessible by their name
   */
  private void initScriptObjects(Game game, ResourceManager resources) throws EvalError {
    for (Player p : game.getAllPlayers()) {
      bsh.set("p_" + ColorUtil.toString(p.getColor()), p);
    }

    bsh.set("game", game);
    bsh.set("map", game.getMap());
    bsh.set("resources", resources);
  }
}
