package slick;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;

public class TestInGameState extends CWState {
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private Scroller scroller;
  private Game game;

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    mapRenderer = new MapRenderer(resources);
    mapRenderer.setTerrainStrip(resources.getSlickImgStrip("terrains"));
  }

  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    game = HardCodedGame.getGame();
    game.init();

    Map<Tile> map = game.getMap();
    mapRenderer.setMap(map);

    game.startGame();

    // Create Camera & scroller
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(container.getWidth(), container.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    scroller = new Scroller(camera);
    mapRenderer.setScroller(scroller);

    // Create & add Cursors
    ImageStrip cursor1 = resources.getSlickImgStrip("selectCursor");
    ImageStrip cursor2 = resources.getSlickImgStrip("aimCursor");
    TileSprite selectCursor = new TileSprite(cursor1, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(cursor2, map.getRandomTile(), map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("AIM", aimCursor);
    mapRenderer.activedCursor("SELECT");
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    camera.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    mapRenderer.render(-camera.getX(), -camera.getY(), g);
    renderTileInfo(mapRenderer.getCursorLocation().toString(), g, container);
    g.drawString("Day:" + game.getDay(), 100, 10);
    g.drawString("Player:" + game.getActivePlayer().getName(), 100, 20);
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

  public void controlPressed(Command command, CWInput cwInput) {
    moveCursor(command, cwInput);
    if (cwInput.isSelectPressed(command)) {
      System.out.println("Clicked on " + mapRenderer.getCursorLocation());
    }
  }

  private void moveCursor(Command command, CWInput cwInput) {
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
    if (key == Input.KEY_3) {
      camera.centerOnTile(0, 0);
    }
    if (key == Input.KEY_4) {
      camera.centerOnTile(5, 5);
    }
    if (key == Input.KEY_5) {
      camera.centerOnTile(9, 9);
    }
    if (key == Input.KEY_R) {
      mapRenderer.setMap(stateSession.getMap());
    }
    if (key == Input.KEY_U) {
      scroller.toggleAutoUpdate();
    }
    if (key == Input.KEY_L) {
      mapRenderer.toggleCursorLock();
    }
    if (key == Input.KEY_J) {
      mapRenderer.setRenderSprites(!mapRenderer.isRenderingSprites());
    }
    if (key == Input.KEY_E) {
      game.endTurn();
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
    mapRenderer.moveCursor(gameX, gameY);
  }

  public int getID() {
    return 3;
  }
}
