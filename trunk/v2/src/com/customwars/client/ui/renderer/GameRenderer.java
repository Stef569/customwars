package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Dimension;

public class GameRenderer {
  private InGameContext context;
  private Game game;

  private ModelEventsRenderer modelEventsRenderer;
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private HUD hud;

  private boolean renderHUD = true;
  private boolean renderEvents = true;
  private ResourceManager resources;

  public void init(GUIContext guiContext) {
    SpriteManager spriteManager = new SpriteManager();
    this.hud = new HUD(guiContext, spriteManager);
    this.mapRenderer = new MapRenderer(spriteManager);
    this.modelEventsRenderer = new ModelEventsRenderer();
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    mapRenderer.setTerrainStrip(resources.getSlickImgStrip("terrains"));
    mapRenderer.loadResources(resources);
    modelEventsRenderer.loadResources(resources);
  }

  public void setInGameContext(InGameContext inGameContext) {
    this.context = inGameContext;
    game = inGameContext.getGame();

    hud.setGame(game);
    hud.loadResources(resources);
    modelEventsRenderer.setGame(game);
    modelEventsRenderer.setMoveTraverse(inGameContext.getMoveTraverse());

    setMap(game.getMap());
  }

  private void setMap(Map<Tile> map) {
    // Create & add Cursors
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    ImageStrip aimCursorImgs = resources.getSlickImgStrip("aimCursor");
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, map.getRandomTile(), map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.activateCursor("SELECT");
    mapRenderer.setNeutralColor(game.getNeutralColor());
    mapRenderer.setMap(map);

    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation, isOnLeftSide(cursorLocation));

    // Create Camera & scroller
    initCamera(map);
    mapRenderer.setScroller(new Scroller(camera));
    hud.setCamera(camera);
  }

  private void initCamera(Map<Tile> map) {
    GUIContext gui = context.getContainer();
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(gui.getWidth(), gui.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    BasicComponent.setCamera(camera);
  }

  public void render(Graphics g) {
    if (camera != null) {
      int cameraX = camera.getX();
      int cameraY = camera.getY();
      float zoomLvl = camera.getZoomLvl();

      g.scale(zoomLvl, zoomLvl);
      mapRenderer.render(-cameraX, -cameraY, g);
      renderDropLocations(g);
      if (renderEvents) modelEventsRenderer.render(-cameraX, -cameraY, g);
      if (renderHUD) hud.render(g);
    }
  }

  private void renderDropLocations(Graphics g) {
    for (Tile t : context.getDropLocations()) {
      Tile transportLocation = context.getClick(2);
      if (transportLocation != null) {
        Direction dir = game.getMap().getDirectionTo(transportLocation, t);
        mapRenderer.renderArrowHead(g, dir, t);
      }
    }
  }

  public void update(int elapsedTime) {
    mapRenderer.update(elapsedTime);
    camera.update(elapsedTime);
    modelEventsRenderer.update(elapsedTime);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    hud.controlPressed(command, cwInput);
  }

  public void moveCursor(Command command, CWInput cwInput) {
    Location originalCursorLocation = mapRenderer.getCursorLocation();
    boolean traversing = mapRenderer.isTraversing();

    if (cwInput.isUpPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.NORTH);
    }

    if (cwInput.isDownPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.SOUTH);
    }

    if (cwInput.isLeftPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.WEST);
    }

    if (cwInput.isRightPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.EAST);
    }

    if (cursorMoved(originalCursorLocation)) {
      context.playSound("maptick");
    }
    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation, isOnLeftSide(cursorLocation));
  }

  public void mouseMoved(int x, int y) {
    Location originalCursorLocation = mapRenderer.getCursorLocation();
    int gameX = camera.convertToGameX(x);
    int gameY = camera.convertToGameY(y);
    mapRenderer.moveCursor(gameX, gameY);

    if (cursorMoved(originalCursorLocation)) {
      context.playSound("maptick");
    }

    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation, isOnLeftSide(cursorLocation));
  }

  public boolean isOnLeftSide(Location location) {
    return location != null && (location.getCol() < game.getMap().getCols() / 2);
  }

  private boolean cursorMoved(Location oldLocation) {
    return mapRenderer.getCursorLocation() != oldLocation;
  }

  public void zoomIn() {
    camera.zoomIn();
  }

  public void zoomOut() {
    camera.zoomOut();
  }

  public Tile getCursorLocation() {
    return (Tile) mapRenderer.getCursorLocation();
  }

  public HUD getHud() {
    return hud;
  }

  public MapRenderer getMapRenderer() {
    return mapRenderer;
  }
}
