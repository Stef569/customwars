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
  private static final boolean DEBUG = true;
  private static final boolean DEBUG_GUI = true;
  private static ResourceManager resources;

  // Script
  private static JConsole console;
  private static Interpreter bsh;
  private static StateSession stateSession;
  private static int startStateID;

  public static void main(String[] argv) {
    if (argv.length > 0)
      startStateID = Integer.valueOf(argv[0]);

    try {
      LoadingList.setDeferredLoading(false);
      resources = new ResourceManager();

      Config config = new Config(resources);
      config.configure();
      logger.info("Starting up");
      new Main();
    } catch (Exception e) {
      logger.fatal("Startup failure", e);
      System.exit(-1);
    }
  }

  public Main() {
    logger.info("init script");
    console = initScript();

    if (DEBUG) {
      logger.info("Init debug Mode");
      initDebugMode();
    }

    try {
      AppGameContainer appGameContainer = new AppGameContainer(new TestStates(startStateID, stateSession, resources));
      appGameContainer.setDisplayMode(640, 480, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (Exception e) {
      logger.fatal("", e);
      e.printStackTrace();
      System.exit(-1);
    }
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
   * See <a href="http://jadvancedwars.sourceforge.net/forum/viewtopic.php?f=20&t=49&p=79&hilit=beanshell#p79">beanshell howto</a>
   * for more information.
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
