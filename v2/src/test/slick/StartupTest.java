package test.slick;

import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

import java.io.IOException;

/**
 * Configure
 * Load resources
 * It's a stand alone test because I couldn't load resources 2x.
 *
 * @author stefan
 */
public class StartupTest extends BasicGame {
  private static AppGameContainer appGameContainer;
  private ResourceManager resources;
  private Config config;

  // Deferred loading
  private DeferredResource nextResource;
  private boolean loadingComplete;
  private int screenWidth;

  public StartupTest() {
    super("Resource Loading");
  }

  public void init(GameContainer container) throws SlickException {
    LoadingList.setDeferredLoading(true);
    resources = new ResourceManager();
    config = new Config(resources);
    config.configure();
    screenWidth = container.getWidth();

    try {
      resources.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    appGameContainer.setTitle(System.getProperty("game.name"));
  }

  //MapParser map = new MapParser();
  //try {
  //    map.writeMap("res/map/cool.txt","[yeah][its][cool][but]");
  //    map.printMap("res/map/cool.txt");
  //} catch (FileNotFoundException ex) {
  //    ex.printStackTrace();
  //} catch (IOException ex) {
  //    ex.printStackTrace();
  //}

  public void update(GameContainer container, int delta) throws SlickException {
    updateLoadingProgress();
  }

  private void updateLoadingProgress() throws SlickException {
    if (nextResource != null) {
      try {
        nextResource.load();
      } catch (IOException e) {
        throw new SlickException("Failed to load: " + nextResource.getDescription(), e);
      }
      nextResource = null;
    }

    if (LoadingList.get().getRemainingResources() > 0) {
      nextResource = LoadingList.get().getNext();
    } else {
      if (!loadingComplete) {
        loadingComplete = true;
      }
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (loadingComplete) {
      g.setColor(Color.white);
      g.drawString("LOADING COMPLETED  showing all Animations...", 100, 50);
      int line = 0, col = 0;
      for (Animation anim : resources.getAllAnims()) {
        g.drawAnimation(anim, (col * 32), 100 + (line * 40));
        if (++col == 25) {
          col = 0;
          line++;
        }
      }
    } else {
      renderLoadingProgress(g);
    }
  }

  private void renderLoadingProgress(Graphics g) {
    if (LoadingList.isDeferredLoading()) {
      if (nextResource != null) {
        g.drawString("Loading: " + nextResource.getDescription(), 100, 100);
      }

      int total = LoadingList.get().getTotalResources();
      int loaded = LoadingList.get().getTotalResources() - LoadingList.get().getRemainingResources();
      float totalPx = screenWidth - 30;
      float loadedPX = loaded / (float) total;  // 0 .. 1

      g.fillRect(10, 150, loadedPX * totalPx, 20);
      g.drawRect(10, 150, totalPx, 20);
    } else {
      g.drawString("Loading...", 100, 100);
    }
  }

  public static void main(String[] args) throws SlickException {
    appGameContainer = new AppGameContainer(new StartupTest());
    appGameContainer.setDisplayMode(800, 600, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.start();
  }
}
