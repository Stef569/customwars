import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.debug.DebugEnvironment;
import com.customwars.client.ui.state.CWStates;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;
import slick.HardCodedGame;
import slick.TestStates;
import tools.ColorUtil;

import java.util.Enumeration;

/**
 * Starts The client
 * If DEBUG is true a hardcoded testGame will be loaded
 * if DEBUG_GUI is true 2 debug windows will be visible
 *
 * @author stefan
 */
public class Main {
  private static final Logger logger = Logger.getLogger(Main.class);
  private static boolean DEBUG = true;
  private static boolean DEBUG_GUI;
  private static ResourceManager resources;
  private static JConsole console;
  private static Interpreter bsh;
  private static StateSession stateSession;
  private static String startStateName;
  private static Config config;

  public Main() throws SlickException {
    console = initScript();

    StateBasedGame stateBasedGame;
    if (DEBUG) {
      initDebugMode();
      stateBasedGame = new TestStates(startStateName, stateSession, resources, config);
    } else {
      stateBasedGame = new CWStates(startStateName, stateSession, resources, config);
    }

    logger.info("Starting Slick");
    boolean fullScreen = App.getBoolean("user.display.fullscreen", false);
    int displayWidth = App.getInt("user.display.width", 640);
    int displayHeight = App.getInt("user.display.height", 480);

    AppGameContainer appGameContainer = new AppGameContainer(stateBasedGame);
    appGameContainer.setDisplayMode(displayWidth, displayHeight, fullScreen);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setForceExit(false);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
    shutDownHook();
    System.exit(0);
  }

  private JConsole initScript() {
    logger.info("init script");
    JConsole console = new JConsole();
    bsh = new Interpreter(console);
    return console;
  }

  private void initDebugMode() {
    logger.info("Init debug Mode");
    resources.loadModel();
    Game game = HardCodedGame.getGame();

    stateSession = new StateSession();
    stateSession.game = game;
    stateSession.map = game.getMap();  // Later set by a mapSelectState

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

  /**
   * Returns true if it appears that log4j have been previously configured. This code
   * checks to see if there are any appenders defined for log4j which is the
   * definitive way to tell if log4j is already initialized
   */
  private static boolean isLog4JConfigured() {
    Enumeration appenders = Logger.getRoot().getAllAppenders();
    if (appenders.hasMoreElements()) {
      return true;
    } else {
      Enumeration loggers = LogManager.getCurrentLoggers();
      while (loggers.hasMoreElements()) {
        Logger c = (Logger) loggers.nextElement();
        if (c.getAllAppenders().hasMoreElements())
          return true;
      }
    }
    return false;
  }

  private void shutDownHook() {
    logger.info("Shutting down");
    config.storeInputConfig();
    config.storeProperties();
  }

  public static void main(String[] argv) {
    handleArgs(argv);

    try {
      LoadingList.setDeferredLoading(!DEBUG);
      resources = new ResourceManager();
      config = new Config(resources);
      config.configure();
      logger.info("Starting up");
      new Main();
    } catch (Exception e) {
      if (isLog4JConfigured())
        logger.fatal("Failure", e);
      else
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
          startStateName = args[i++];
        else
          startStateName = null;
      }
    }
  }
}
