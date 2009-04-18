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
  private GUIContext guiContext;

  private Game game;
  private Map<Tile> map;

  private ModelEventsRenderer modelEventsRenderer;
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private HUD hud;

  private boolean renderHUD = true;
  private boolean renderEvents = true;
  private ResourceManager resources;

  public GameRenderer(GUIContext guiContext) {
    SpriteManager spriteManager = new SpriteManager();
    this.hud = new HUD(guiContext, spriteManager);
    this.mapRenderer = new MapRenderer(spriteManager);
    this.modelEventsRenderer = new ModelEventsRenderer();
    this.guiContext = guiContext;
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
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

  public void setMap(Map<Tile> map) {
    this.map = map;
    initMapRenderer();

    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation, isOnLeftSide(cursorLocation));

    initCamera();
    mapRenderer.setScroller(new Scroller(camera));
    hud.setCamera(camera);
    BasicComponent.setCamera(camera);
  }

  private void initMapRenderer() {
    initCursors();
    if (game != null) {
      mapRenderer.setNeutralColor(game.getNeutralColor());
    }
    mapRenderer.setMap(map);
  }

  private void initCursors() {
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    ImageStrip aimCursorImgs = resources.getSlickImgStrip("aimCursor");
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, map.getRandomTile(), map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.activateCursor("SELECT");
  }

  private void initCamera() {
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
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
    if (context != null) {
      for (Tile t : context.getDropLocations()) {
        Tile transportLocation = context.getClick(2);
        if (transportLocation != null) {
          Direction dir = game.getMap().getDirectionTo(transportLocation, t);
          mapRenderer.renderArrowHead(g, dir, t);
        }
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

    if (cwInput.isUp(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.NORTH);
    }

    if (cwInput.isDown(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.SOUTH);
    }

    if (cwInput.isLeft(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.WEST);
    }

    if (cwInput.isRight(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.EAST);
    }

    if (cursorMoved(originalCursorLocation)) {
      resources.playSound("maptick");
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
      resources.playSound("maptick");
    }

    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation, isOnLeftSide(cursorLocation));
  }

  public void setRenderHUD(boolean renderHUD) {
    this.renderHUD = renderHUD;
  }

  public void setRenderEvents(boolean renderEvents) {
    this.renderEvents = renderEvents;
  }

  public boolean isOnLeftSide(Location location) {
    return location != null && (location.getCol() < map.getCols() / 2);
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
