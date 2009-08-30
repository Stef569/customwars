package com.customwars.client.ui.renderer;

import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Graphics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Renders the game, The rendering is delegated to the following subComponents:
 * the map     ->  MapRenderer
 * game events ->  EventsRenderer
 * hud         ->  HUD(optional)
 *
 * The map is scrolled when the cursor is near the edge of the map {@link Scroller}
 */
public class GameRenderer implements Renderable, PropertyChangeListener {
  // Control
  private boolean renderHUD = true, renderEvents = true;
  private GameController gameControl;
  private Scroller scroller;

  // GUI
  private MapRenderer mapRenderer;
  private ModelEventsRenderer eventsRenderer;
  private SpriteManager spriteManager;
  private Camera2D camera;
  private HUD hud;

  // Data
  private Game game;

  public GameRenderer(Game game, Camera2D camera, MoveTraverse moveTraverse) {
    this(game, camera, null, moveTraverse);
    renderHUD = false;
  }

  public GameRenderer(Game game, Camera2D camera, HUD hud, MoveTraverse moveTraverse) {
    game.addPropertyChangeListener(this);
    this.game = game;
    Map<Tile> map = game.getMap();
    this.camera = camera;
    this.hud = hud;
    this.scroller = new Scroller(camera);
    this.spriteManager = new SpriteManager(map);
    this.eventsRenderer = new ModelEventsRenderer(moveTraverse, game);
    this.mapRenderer = new MapRenderer(map, spriteManager);

    if (hud != null) {
      hud.setSpriteManager(spriteManager);
      hud.setCamera(camera);
      hud.setGame(game);
    }

    gameControl = new GameController(game, this, spriteManager);
  }

  public void loadResources(ResourceManager resources) {
    mapRenderer.loadResources(resources);
    eventsRenderer.loadResources(resources);
    if (hud != null) hud.loadResources(resources);
  }

  public void update(int elapsedTime) {
    mapRenderer.update(elapsedTime);
    camera.update(elapsedTime);
    scroller.setCursorLocation(mapRenderer.getCursorLocation());
    scroller.update(elapsedTime);
    eventsRenderer.update(elapsedTime);
  }

  public void render(Graphics g) {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());

    // Map moves upwards
    g.translate(-camera.getX(), -camera.getY());
    mapRenderer.render(g);
    if (renderEvents) eventsRenderer.render(g);
    hud.renderPopup(g);

    // other components move down
    g.translate(camera.getX(), camera.getY());
    if (renderHUD) hud.render(g);
    g.resetTransform();
  }

  public void addCursor(String cursorName, TileSprite cursorSprite) {
    spriteManager.addCursor(cursorName, cursorSprite);
  }

  public void activateCursor(String cursorName) {
    spriteManager.setActiveCursor(cursorName);
  }

  public Tile getCursorLocation() {
    return mapRenderer.getCursorLocation();
  }

  public GameController getGameControl() {
    return gameControl;
  }

  public MapRenderer getMapRenderer() {
    return mapRenderer;
  }

  public boolean isDyingUnitAnimationCompleted() {
    return spriteManager.isDyingUnitAnimationCompleted();
  }

  /**
   * Update the active unit in mapRenderer to the unit in the game
   * each time the activeunit in the game has changed
   *
   * @param evt An event from the game
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Game) {
      if (evt.getPropertyName().equals("activeunit")) {
        Unit activeUnit = (Unit) evt.getNewValue();
        mapRenderer.setActiveUnit(activeUnit);
      }
    }
  }
}
