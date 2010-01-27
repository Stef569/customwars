import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.tools.Log4JUtil;
import com.customwars.client.ui.slick.CWStateBasedGame;
import com.customwars.client.ui.state.CWStates;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Starts The client
 * Command line arguments:
 * -datafiles path     The path to load the resources from, defaults to the working directory
 *
 * @author stefan
 */
public class MainGame {
  private static final Logger logger = Logger.getLogger(MainGame.class);
  private static CWStateBasedGame cwStates;
  private static Config config;
  private static String resourcesLocation = "";

  public MainGame() throws SlickException {
    ResourceManager resources = new ResourceManager();
    loadConfiguration(resources);

    cwStates = new CWStates("MAIN_MENU", resources, config);
    AppGameContainer container = createContainer(cwStates);
    init(container, resources);
    run(container);
  }

  private void loadConfiguration(ResourceManager resources) {
    config = new Config(resources);
    config.load(resourcesLocation);
  }

  private AppGameContainer createContainer(StateBasedGame stateBasedGame) throws SlickException {
    logger.info("Starting Slick");
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
    SFX.setSoundEffectsVolume((float) App.getDouble("user.sfx.sound_volume"));
    SFX.setMusicVolume((float) App.getDouble("user.sfx.music_volume"));
  }

  private void run(AppGameContainer container) throws SlickException {
    // Blocking method, keeps on looping until container.exit() is invoked
    container.start();
    cwStates.shutDownHook();
    System.exit(0);
  }

  public static void main(String[] argv) {
    handleArgs(argv);

    try {
      new MainGame();
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
    String arg;

    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];

      // use this type of check for arguments that require arguments
      if (arg.equals("-datafiles")) {
        if (i < args.length) {
          resourcesLocation = args[i++];
        } else {
          resourcesLocation = "";
        }
      }
    }
  }
}