package slick;

import com.customwars.client.App;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;

public class TestMapRenderer extends CWState {
  private GUIContext guiContext;
  private MapRenderer mapRenderer;
  private Map<Tile> map;
  private Camera2D camera;
  private Scroller scroller;
  private CursorController cursorControl;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    guiContext = container;
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    this.map = stateSession.map;
    initMap(map);
  }

  public void initMap(Map<Tile> map) {
    SpriteManager spriteManager = new SpriteManager(map);
    mapRenderer = new MapRenderer(map, spriteManager);
    mapRenderer.loadResources(resources);
    cursorControl = new CursorController(map, spriteManager);

    // Create Camera & scroller
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    scroller = new Scroller(camera);

    // Create & add Cursors
    TileSprite selectCursor = resources.createCursor(map, App.get("user.selectcursor"));
    TileSprite attackCursor = resources.createCursor(map, App.get("user.attackcursor"));

    cursorControl.addCursor("SELECT", selectCursor);
    cursorControl.addCursor("ATTACK", attackCursor);
    cursorControl.activateCursor("SELECT");
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    camera.update(delta);
    scroller.update(delta);
    container.getInput().setOffset(camera.getX(), camera.getY());
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (camera != null) {
      g.scale(camera.getZoomLvl(), camera.getZoomLvl());
      g.translate(-camera.getX(), -camera.getY());
      mapRenderer.render(g);

      g.translate(camera.getX(), camera.getY());
      Location cursorLocation = mapRenderer.getCursorLocation();
      renderTileInfo(cursorLocation.toString(), g, container);
      g.drawString(String.format("Camera pos: %s,%s max cols: %s/%s max rows: %s/%s",
        camera.getCol(), camera.getRow(),
        camera.getMaxCols(), map.getCols(),
        camera.getMaxRows(), map.getRows()),
        10, 10);
    }
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

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command.isMoveCommand()) {
      moveCursor(command);
    } else if (command == cwInput.SELECT) {
      System.out.println("Clicked on " + mapRenderer.getCursorLocation());
    }
  }

  private void moveCursor(CWCommand command) {
    switch (command.getEnum()) {
      case UP:
        cursorControl.moveCursor(Direction.NORTH);
        break;
      case DOWN:
        cursorControl.moveCursor(Direction.SOUTH);
        break;
      case LEFT:
        cursorControl.moveCursor(Direction.WEST);
        break;
      case RIGHT:
        cursorControl.moveCursor(Direction.EAST);
        break;
    }
    scroller.setCursorLocation(mapRenderer.getCursorLocation());
  }

  @Override
  public void keyReleased(int key, char c) {
    if (key == Input.KEY_0) {
      cursorControl.activateCursor("DOES_NOT_EXISTS");
    }
    if (key == Input.KEY_1) {
      cursorControl.activateCursor("ATTACK");
    }
    if (key == Input.KEY_2) {
      cursorControl.activateCursor("SELECT");
    }
    if (key == Input.KEY_3) {
      camera.centerOnTile(0, 0);
    }
    if (key == Input.KEY_4) {
      camera.centerOnTile(9, 9);
    }
    if (key == Input.KEY_5) {
      camera.centerOnTile(10, 13);
    }
    if (key == Input.KEY_6) {
      camera.centerOnTile(map.getCols() - 1, map.getRows() - 1);
    }
    if (key == Input.KEY_R) {

    }
    if (key == Input.KEY_U) {
      scroller.setAutoScroll(scroller.isAutoScrollOn());
    }
    if (key == Input.KEY_L) {
      cursorControl.toggleCursorLock();
    }
    if (key == Input.KEY_J) {
      mapRenderer.setRenderSprites(!mapRenderer.isRenderingSprites());
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
    cursorControl.moveCursor(newx, newy);
    scroller.setCursorLocation(mapRenderer.getCursorLocation());
  }

  public int getID() {
    return 1;
  }
}
