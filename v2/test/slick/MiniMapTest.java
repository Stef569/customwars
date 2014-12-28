package slick;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.TestData;
import com.customwars.client.ui.renderer.MiniMapRenderer;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class MiniMapTest extends BasicGame {
  private MiniMapRenderer miniMapRenderer;

  public MiniMapTest() {
    super("Mini map Render test");
  }

  @Override
  public void init(GameContainer container) throws SlickException {
    TestData.storeTestData();
    ImageStrip miniMap = new ImageStrip("testData/miniMap.png", 4, 4);
    miniMapRenderer = new MiniMapRenderer(HardCodedGame.getMap());
    miniMapRenderer.setLocation(0, 0);
    miniMapRenderer.setTerrainMiniMap(miniMap);
    miniMapRenderer.setScale(4.0f);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    miniMapRenderer.update();
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    miniMapRenderer.render(g);
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new MiniMapTest());
    appGameContainer.setDisplayMode(800, 600, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
  }
}
