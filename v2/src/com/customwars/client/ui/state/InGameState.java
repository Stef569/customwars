package com.customwars.client.ui.state;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;

public class InGameState extends CWState {
  private MapRenderer mapRenderer;
  private Camera2D camera;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    mapRenderer = new MapRenderer();
    mapRenderer.loadResources(resources);
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    Map<Tile> map = stateSession.map;
    mapRenderer.setMap(map);

    // Create Camera & Scroller
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(container.getWidth(), container.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    mapRenderer.setScroller(new Scroller(camera));

    // Create & add Cursors
    ImageStrip cursor1 = resources.getSlickImgStrip("selectCursor");
    ImageStrip cursor2 = resources.getSlickImgStrip("aimCursor");
    TileSprite selectCursor = new TileSprite(cursor1, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(cursor2, map.getRandomTile(), map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.activateCursor("SELECT");
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    camera.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    g.translate(-camera.getX(), -camera.getY());
    mapRenderer.render(g);
    renderTileInfo(mapRenderer.getCursorLocation().toString(), g, container);
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
    if (cwInput.isSelect(command)) {
      System.out.println("Clicked on " + mapRenderer.getCursorLocation());
    }
  }

  private void moveCursor(Command command, CWInput cwInput) {
    if (cwInput.isUp(command)) {
      mapRenderer.moveCursor(Direction.NORTH);
    }
    if (cwInput.isDown(command)) {
      mapRenderer.moveCursor(Direction.SOUTH);
    }
    if (cwInput.isLeft(command)) {
      mapRenderer.moveCursor(Direction.WEST);
    }
    if (cwInput.isRight(command)) {
      mapRenderer.moveCursor(Direction.EAST);
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
    mapRenderer.moveCursor(newx, newy);
  }

  public int getID() {
    return 10;
  }
}
