package com.customwars.client.ui.renderer;

import com.customwars.client.SFX;
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
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Dimension;
import java.util.List;

public class GameRenderer {
  private InGameContext context;
  private GUIContext guiContext;

  private Game game;
  private Map<Tile> map;

  private ModelEventsRenderer modelEventsRenderer;
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private HUD hud;
  private Animation explosionAnim;
  private List<Location> explosionArea;

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
    explosionAnim = resources.getAnim("explosion_LAND");
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
    hud.moveOverTile(cursorLocation);

    initCamera();
    mapRenderer.setScroller(new Scroller(camera));
    hud.setCamera(camera);
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
    Image siloCursorImg = resources.getSlickImg("siloCursor");

    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, randomTile, map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, randomTile, map);
    TileSprite siloCursor = new TileSprite(siloCursorImg, randomTile, map);

    // Use the silo cursor Image height to calculate the effect Range ie
    // If the image has a height of 160/32 is 5 tiles high/2 rounded to int becomes 2.
    int effectRange = siloCursorImg.getHeight() / map.getTileSize() / 2;
    siloCursor.setEffectRange(effectRange);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.addCursor("SILO", siloCursor);
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

      // Map moves up
      g.translate(-camera.getX(), -camera.getY());
      mapRenderer.render(g);
      renderDropLocations(g);
      renderExplosionArea();
      if (renderEvents) modelEventsRenderer.render(-cameraX, -cameraY, g);
      if (hud != null) hud.renderPopUp(g);

      // Hud moves down
      g.translate(camera.getX(), camera.getY());
      if (renderHUD) hud.renderComponents(g);
    }
  }

  private void renderExplosionArea() {
    if (context != null && explosionArea != null) {
      if (explosionAnim.isStopped()) {
        explosionArea = null;
      } else {
        for (Location t : explosionArea) {
          explosionAnim.getCurrentFrame().draw(t.getCol() * map.getTileSize(), t.getRow() * map.getTileSize());
        }
      }
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
    explosionAnim.update(elapsedTime);
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
      SFX.playSound("maptick");
    }
    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation);
  }

  public void mouseMoved(int x, int y) {
    Location originalCursorLocation = mapRenderer.getCursorLocation();
    mapRenderer.moveCursor(x, y);

    if (cursorMoved(originalCursorLocation)) {
      SFX.playSound("maptick");
    }

    Tile cursorLocation = getCursorLocation();
    hud.moveOverTile(cursorLocation);
  }

  public void setRenderHUD(boolean renderHUD) {
    this.renderHUD = renderHUD;
  }

  public void setRenderEvents(boolean renderEvents) {
    this.renderEvents = renderEvents;
  }

  public void setExplosionArea(List<Location> explosionArea) {
    this.explosionArea = explosionArea;
    if (explosionAnim.isStopped()) explosionAnim.restart();
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

  public List<Location> getCursorEffectRange() {
    return mapRenderer.getCursorEffectRange();
  }

  public HUD getHud() {
    return hud;
  }

  public MapRenderer getMapRenderer() {
    return mapRenderer;
  }

  public Camera2D getCamera() {
    return camera;
  }
}
