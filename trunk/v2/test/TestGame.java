import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.tools.Log4JUtil;
import com.customwars.client.ui.slick.CWStateBasedGame;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import slick.TestStates;

/**
 * Starts The tests
 *
 * Command line arguments:
 * -startstate startStateName The state to start the program in, defaults to the main menu,
 * see {@link TestStates#buildStateList()} for a list of state names that can be used.
 *
 * -datafiles path     The path to load the resources from, defaults to the working directory
 *
 * @author stefan
 */
public class TestGame {
  private static final Logger logger = Logger.getLogger(TestGame.class);
  private final CWStateBasedGame testStates;
  private Config config;

  private static String startStateName = "";
  private static String resourcesLocation = "";

  public TestGame() throws SlickException {
    ResourceManager resources = new ResourceManager();
    loadConfiguration(resources);

    testStates = new TestStates(startStateName, resources, config);
    AppGameContainer container = createContainer(testStates);
    init(container, resources);
    run(container);
  }

  private void loadConfiguration(ResourceManager resources) {
    config = new Config(resources);
    config.load(resourcesLocation);
  }

  private AppGameContainer createContainer(StateBasedGame stateBasedGame) throws SlickException {
    boolean fullScreen = App.getBoolean("user.display.fullscreen", false);
    int displayWidth = App.getInt("user.display.width", 640);
    int displayHeight = App.getInt("user.display.height", 480);

    AppGameContainer appGameContainer = new AppGameContainer(stateBasedGame);
    appGameContainer.setDisplayMode(displayWidth, displayHeight, fullScreen);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setForceExit(false);
    appGameContainer.setShowFPS(false);
    return appGameContainer;
  }

  private void init(GameContainer gameContainer, ResourceManager resources) {
    SFX.setResources(resources);
    SFX.setGameContainer(gameContainer);
  }

  private void run(AppGameContainer container) throws SlickException {
    logger.info("Starting Slick");
    container.start();
    testStates.shutDownHook();
    System.exit(0);
  }

  public static void main(String[] args) {
    handleArgs(args);

    try {
      new TestGame();
    } catch (Exception e) {
      logAndExit(e);
    }
  }

  private static void logAndExit(Exception e) {
    if (Log4JUtil.isLog4JConfigured()) {
      logger.fatal("Failure", e);
    } else {
      e.printStackTrace();
    }
    System.exit(-1);
  }

  private static void handleArgs(String[] args) {
    int i = 0;

    while (i < args.length && args[i].startsWith("-")) {
      String arg = args[i++];

      // use this type of check for arguments that require arguments
      if (arg.equals("-startstate")) {
        if (i < args.length) {
          startStateName = args[i++];
        } else {
          startStateName = null;
        }
      } else if (arg.equals("-datafiles")) {
        if (i < args.length) {
          resourcesLocation = args[i++];
        } else {
          resourcesLocation = "";
        }
      }
    }
  }
}
