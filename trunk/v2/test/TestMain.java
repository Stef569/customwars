import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;
import slick.HardCodedGame;
import slick.TestStates;
import tools.Log4JUtil;

/**
 * Starts The tests
 *
 * Command line arguments:
 * -startstate startStateName
 * The state to start  the program in, defaults to the main menu
 * see {@link slick.TestStates#mapStateIdsToName()} for a list of state names that can be used.
 *
 * @author stefan
 */
public class TestMain {
  private static final Logger logger = Logger.getLogger(TestMain.class);
  private static ResourceManager resources;
  private static StateSession stateSession;
  private static String startStateName;
  private static Config config;

  public TestMain() throws SlickException {
    StateBasedGame stateBasedGame;
    initTestMode();
    stateBasedGame = new TestStates(startStateName, stateSession, resources, config);

    logger.info("Starting Slick");
    boolean fullScreen = App.getBoolean("user.display.fullscreen", false);
    int displayWidth = App.getInt("user.display.width", 640);
    int displayHeight = App.getInt("user.display.height", 480);

    AppGameContainer appGameContainer = new AppGameContainer(stateBasedGame);
    SFX.setResources(resources);
    SFX.setGameContainer(appGameContainer);

    appGameContainer.setDisplayMode(displayWidth, displayHeight, fullScreen);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setForceExit(false);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
    shutDownHook();
    System.exit(0);
  }

  private void initTestMode() {
    logger.info("Init debug Mode");
    resources.loadModel();
    Game game = HardCodedGame.getGame();

    stateSession = new StateSession();
    stateSession.game = game;
    stateSession.map = game.getMap();
  }

  private void shutDownHook() {
    logger.info("Shutting down");
    config.storeInputConfig();
    config.storePersistenceProperties();
  }

  public static void main(String[] argv) {
    handleArgs(argv);

    try {
      LoadingList.setDeferredLoading(false);
      resources = new ResourceManager();
      config = new Config(resources);
      config.configure();
      logger.info("Starting up");
      new TestMain();
    } catch (Exception e) {
      if (Log4JUtil.isLog4JConfigured()) {
        logger.fatal("Failure", e);
      } else {
        e.printStackTrace();
      }
      System.exit(-1);
    }
  }

  private static void handleArgs(String[] args) {
    int i = 0;
    String arg;

    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];

      // use this type of check for arguments that require arguments
      if (arg.equals("-startstate")) {
        if (i < args.length)
          startStateName = args[i++];
        else
          startStateName = null;
      }
    }
  }
}