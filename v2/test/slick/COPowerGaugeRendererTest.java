package slick;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.renderer.COPowerGaugeRenderer;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * Render a Power gauge
 */
public class COPowerGaugeRendererTest extends BasicGame {
  private COPowerGaugeRenderer powerGauge;
  private Game game;

  public COPowerGaugeRendererTest() {
    super("CO Power Gauge Test");
  }

  @Override
  public void init(GameContainer container) throws SlickException {
    Font numbersFont = container.getDefaultFont();
    ResourceManager resources = new ResourceManager();
    resources.addFont("numbers", numbersFont);
    resources.addImage("light_bar", new Image("resources/res/plugin/dor/images/co/bar0.png"));
    resources.addImage("dark_bar", new Image("resources/res/plugin/dor/images/co/bar1.png"));
    resources.addImage("cobar0", new Image("resources/res/plugin/dor/images/co/cobar0.png"));
    resources.addImage("cobar1", new Image("resources/res/plugin/dor/images/co/cobar1.png"));
    resources.addImage("cobar2", new Image("resources/res/plugin/dor/images/co/cobar2.png"));
    resources.addImage("cobar3", new Image("resources/res/plugin/dor/images/co/cobar3.png"));
    resources.addImage("cobar4", new Image("resources/res/plugin/dor/images/co/cobar4.png"));
    resources.addImage("CO_STURM", new Image("resources/res/plugin/dor/images/co/sheets/brenner.png"));
    resources.addImage("CO_ANDY", new Image("resources/res/plugin/dor/images/co/sheets/caulder.png"));
    resources.addImage("CO_PENNY", new Image("resources/res/plugin/dor/images/co/sheets/caulder.png"));

    TestData.storeTestData();
    game = HardCodedGame.getGame();
    game.startGame();
    powerGauge = new COPowerGaugeRenderer(game, container.getWidth());
    powerGauge.loadResources(resources);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_L) {
      powerGauge.setRenderLeftToRight(true);
    } else if (key == Input.KEY_R) {
      powerGauge.setRenderLeftToRight(false);
    } else if (key == Input.KEY_A) {
      game.getActivePlayer().chargePowerGauge(1);
    } else if (key == Input.KEY_Z) {
      game.getActivePlayer().resetPowerGauge();
    }
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    powerGauge.render(g);
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new COPowerGaugeRendererTest());
    appGameContainer.setDisplayMode(180, 100, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
  }
}
