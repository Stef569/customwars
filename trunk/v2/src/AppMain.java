import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.state.CWStates;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;
import tools.Log4JUtil;

/**
 * Starts The client
 * If DEBUG is true a hardcoded testGame will be loaded
 * if DEBUG_GUI is true 2 debug windows will be visible
 *
 * @author stefan
 */
public class AppMain {
  private static final Logger logger = Logger.getLogger(AppMain.class);
  private static ResourceManager resources;
  private static String startStateName;
  private static Config config;

  public AppMain() throws SlickException {
    StateSession stateSession = new StateSession();
    StateBasedGame stateBasedGame = new CWStates(startStateName, stateSession, resources, config);

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

  private void shutDownHook() {
    logger.info("Shutting down");
    config.storeInputConfig();
    config.storePersistenceProperties();
  }

  public static void main(String[] argv) {
    try {
      LoadingList.setDeferredLoading(true);
      resources = new ResourceManager();
      config = new Config(resources);
      config.configure();
      logger.info("Starting up");
      new AppMain();
    } catch (Exception e) {
      if (Log4JUtil.isLog4JConfigured())
        logger.fatal("Failure", e);
      else
        e.printStackTrace();
      System.exit(-1);
    }
  }
}
