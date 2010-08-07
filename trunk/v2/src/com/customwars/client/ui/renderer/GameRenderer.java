package com.customwars.client.ui.renderer;

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
import org.newdawn.slick.Graphics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Renders the game, The rendering is delegated to the following subComponents:
 * the map     ->  MapRenderer
 * game events ->  EventsRenderer
 * pop up      ->  HUD
 * damage      -> DamageRenderer
 * <p/>
 * The map is scrolled when the cursor is near the edge of the map {@link Scroller}
 */
public class GameRenderer implements Renderable, PropertyChangeListener {
  // Control
  private boolean renderEvents = true;

  // GUI
  private final MapRenderer mapRenderer;
  private final GameEventsRenderer eventsRenderer;
  private DamageRenderer damageRenderer;
  private final SpriteManager spriteManager;
  private final Camera2D camera;
  private final HUD hud;
  private final Scroller scroller;

  // Data
  private final Game game;
  private final Map<Tile> map;

  public GameRenderer(Game game, Camera2D camera, HUD hud, MoveTraverse moveTraverse) {
    this.game = game;
    this.camera = camera;

    this.game.addPropertyChangeListener(this);
    this.map = game.getMap();

    this.scroller = new Scroller(camera);
    this.spriteManager = new SpriteManager(map);
    this.eventsRenderer = new GameEventsRenderer(moveTraverse, game, camera);
    this.mapRenderer = new MapRenderer(map, spriteManager);

    this.hud = hud;
    hud.setSpriteManager(spriteManager);
    hud.setCamera(camera);
    hud.setGame(game);
  }

  public void loadResources(ResourceManager resources) {
    mapRenderer.loadResources(resources);
    eventsRenderer.loadResources(resources);
    hud.loadResources(resources);
    DamageRenderer.setTextFont(resources.getFont("in_game"));
  }

  public void update(int elapsedTime) {
    mapRenderer.update(elapsedTime);
    camera.update(elapsedTime);
    scroller.setCursorLocation(getCursorLocation());
    scroller.update(elapsedTime);
    eventsRenderer.update(elapsedTime);
  }

  public void render(Graphics g) {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    g.translate(-camera.getX(), -camera.getY());
    mapRenderer.render(g);
    if (renderEvents) eventsRenderer.render(g);
    if (damageRenderer != null) damageRenderer.render(g);
    hud.renderTranslated(g);
    g.resetTransform();
  }

  public void setRenderAttackDamage(boolean renderAttackDamage) {
    if (renderAttackDamage) {
      Unit activeUnit = game.getActiveUnit();

      if (damageRenderer == null) {
        this.damageRenderer = new DamageRenderer(map, activeUnit, getCursorLocation());
      } else {
        // Only create a new Damage renderer when the location has changed.
        // If the location has not changed keep using the old damage renderer
        if (!damageRenderer.isShowingDamageFor(getCursorLocation())) {
          this.damageRenderer = new DamageRenderer(map, activeUnit, getCursorLocation());
        }
      }
    } else {
      this.damageRenderer = null;
    }
  }

  public void setRenderEvents(boolean renderEvents) {
    this.renderEvents = renderEvents;
  }

  public Tile getCursorLocation() {
    return mapRenderer.getCursorLocation();
  }

  public MapRenderer getMapRenderer() {
    return mapRenderer;
  }

  public boolean isDyingUnitAnimationCompleted() {
    return spriteManager.isDyingUnitAnimationCompleted();
  }

  public void shakeScreen() {
    camera.shake();
  }

  /**
   * Update the active unit in mapRenderer
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
