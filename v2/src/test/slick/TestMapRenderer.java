package test.slick;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.renderer.TileMapRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateLogic;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;
import test.testData.HardCodedGame;

import java.awt.Dimension;

/**
 * renders a hardcoded map
 *
 * @author stefan
 */
public class TestMapRenderer extends CWState {
  private static final Logger logger = Logger.getLogger(TestMapRenderer.class);
  private MapRenderer mapRenderer;
  private TileMapRenderer miniMapRenderer;
  private Camera2D camera;
  private Scroller scroller;

  public TestMapRenderer(CWInput cwInput, StateLogic stateLogic) {
    super(cwInput, stateLogic);
  }

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    ResourceManager resources = new ResourceManager();
    resources.setDataPath("res/data/");
    resources.setImgPath("res/image/");
    resources.loadAll();

    Map<Tile> map = HardCodedGame.getMap();

    // Create Camera & scroller
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(container.getWidth(), container.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    scroller = new Scroller(camera);

    ImageStrip terrainStrip = resources.getSlickImgStrip("terrains");
    ImageStrip cursor1 = resources.getSlickImgStrip("selectCursor");
    ImageStrip cursor2 = resources.getSlickImgStrip("aimCursor");

    TileSprite selectCursor = new TileSprite(cursor1, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(cursor2, map.getRandomTile(), map);

    mapRenderer = new MapRenderer(resources);
    mapRenderer.setTerrainStrip(terrainStrip);
    mapRenderer.setMap(map);
    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("AIM", aimCursor);
    mapRenderer.activedCursor("SELECT");

    // Changse in the map at this point will change the graphics, so let's test that:
    Tile t = map.getTile(6, 6);
    map.getUnitOn(t).setOrientation(Direction.WEST);

    ImageStrip miniMapTerrainStrip = resources.getSlickImgStrip("miniMap");
    miniMapRenderer = new TileMapRenderer(map);
    miniMapRenderer.setTerrainStrip(miniMapTerrainStrip);
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    mapRenderer.render(-camera.getX(), -camera.getY(), g);
    renderTileInfo(mapRenderer.getCursorLocation().toString(), g, container);

    g.drawString("MiniMap", 500, 2);
    miniMapRenderer.render(510, 25, g);
  }

  private void renderTileInfo(String tileInfo, Graphics g, GameContainer container) {
    String line1 = tileInfo, line2 = "";

    int endIndex = tileInfo.length();
    while (g.getFont().getWidth(line1) > container.getWidth() - 20) {
      line1 = tileInfo.substring(0, endIndex--);
    }

    if (endIndex > 0)
      line2 = tileInfo.substring(endIndex);
    g.drawString(line1, 10, container.getHeight() - 40);
    g.drawString(line2, 10, container.getHeight() - 20);

  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    mapRenderer.update(delta);
    scroller.update(delta);
    camera.update(delta);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    moveCursor(command, cwInput);
    if (cwInput.isSelectPressed(command)) {
      System.out.println("Clicked on " + mapRenderer.getCursorLocation());
    }
  }

  private void moveCursor(Command command, CWInput cwInput) {
    scroller.setCursorLocation(mapRenderer.getCursorLocation());

    if (cwInput.isUpPressed(command)) {
      mapRenderer.moveCursor(Direction.NORTH);
    }
    if (cwInput.isDownPressed(command)) {
      mapRenderer.moveCursor(Direction.SOUTH);
    }
    if (cwInput.isLeftPressed(command)) {
      mapRenderer.moveCursor(Direction.WEST);
    }
    if (cwInput.isRightPressed(command)) {
      mapRenderer.moveCursor(Direction.EAST);
    }
  }

  public void keyReleased(int key, char c) {
    if (key == Input.KEY_0) {
      mapRenderer.activedCursor("DOES_NOT_EXISTS");
    }
    if (key == Input.KEY_1) {
      mapRenderer.activedCursor("AIM");
    }
    if (key == Input.KEY_2) {
      mapRenderer.activedCursor("SELECT");
    }
    if (key == Input.KEY_C) {
      camera.centerOnTile(0, 0);
    }
    if (key == Input.KEY_O) {
      camera.centerOnTile(8, 3);
    }
  }

  public void mouseWheelMoved(int newValue) {
    if (newValue > 0) {
      camera.zoomIn();
    } else {
      camera.zoomOut();
    }
  }

  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    int gameX = camera.convertToGameX(newx);
    int gameY = camera.convertToGameY(newy);
    scroller.setCursorLocation(mapRenderer.getCursorLocation());
    mapRenderer.moveCursor(gameX, gameY);
  }

  public int getID() {
    return 1;
  }
}
