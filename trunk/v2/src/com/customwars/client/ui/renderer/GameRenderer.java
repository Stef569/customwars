package com.customwars.client.ui.renderer;

import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Color;
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
  private static final Color DAMAGE_PERCENTAGE_BACKGROUND_COLOR = new Color(0, 0, 0, 0.4f);

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
  private Map<Tile> map;
  private Fight fight;

  public GameRenderer(Game game, Camera2D camera, MoveTraverse moveTraverse) {
    this(game, camera, null, moveTraverse);
    renderHUD = false;
  }

  public GameRenderer(Game game, Camera2D camera, HUD hud, MoveTraverse moveTraverse) {
    this.game = game;
    this.camera = camera;
    this.hud = hud;

    this.fight = new UnitFight();
    this.game.addPropertyChangeListener(this);
    this.map = game.getMap();

    this.scroller = new Scroller(camera);
    this.spriteManager = new SpriteManager(map);
    this.eventsRenderer = new ModelEventsRenderer(moveTraverse, game);
    this.mapRenderer = new MapRenderer(map, spriteManager);
    this.gameControl = new GameController(game, this, spriteManager);

    if (hud != null) {
      hud.setSpriteManager(spriteManager);
      hud.setCamera(camera);
      hud.setGame(game);
    }
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

  /**
   * Draw The damage percentage near the cursorlocation
   *
   * @param g Graphics to render the dmg percentage to
   */
  public void renderAttackDamagePercentage(Graphics g) {
    Location cursorLocation = getCursorLocation();
    Unit attacker = game.getActiveUnit();
    Unit defender = map.getUnitOn(cursorLocation);

    if (defender != null) {
      fight.initFight(attacker, defender);
      int tileSize = map.getTileSize();
      int cursorX = cursorLocation.getCol() * tileSize + tileSize / 2;
      int cursorY = cursorLocation.getRow() * tileSize + 5;
      int dmgPercentage = fight.getAttackDamagePercentage();

      String dmgTxt = "Damage:" + dmgPercentage + "%";
      int fontWidth = g.getFont().getWidth(dmgTxt);
      int fontHeight = g.getFont().getHeight(dmgTxt);

      final int BOX_MARGIN = 2;
      final int CURSOR_OFFSET = 64;

      int boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
      int boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
      int totalWidth = fontWidth + BOX_MARGIN * 2;
      int totalHeight = fontHeight + BOX_MARGIN * 2;

      // If the damage percentage does not fit to the gui make sure that it does
      // by positioning the dmg percentage to the opposite quadrant as where the cursor is located.
      // If the cursor is located NORTH EAST in the map show the dmg percentage at SOUTH WEST
      if (!GUI.canFitToScreen(boxX, boxY, totalWidth, totalHeight)) {
        Direction quadrant = map.getQuadrantFor(cursorLocation);
        switch (quadrant) {
          case NORTHEAST:
            boxX = cursorX - CURSOR_OFFSET - BOX_MARGIN;
            boxY = cursorY + CURSOR_OFFSET - BOX_MARGIN;
            break;
          case NORTHWEST:
            boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
            boxY = cursorY + CURSOR_OFFSET - BOX_MARGIN;
            break;
          case SOUTHEAST:
            boxX = cursorX - CURSOR_OFFSET - BOX_MARGIN;
            boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
            break;
          case SOUTHWEST:
            boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
            boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
            break;
        }
      }

      Color prevColor = g.getColor();
      g.setColor(DAMAGE_PERCENTAGE_BACKGROUND_COLOR);
      g.fillRoundRect(boxX, boxY, totalWidth, totalHeight, 2);
      g.setColor(prevColor);
      g.drawString(dmgTxt, boxX + BOX_MARGIN, boxY + BOX_MARGIN);
    }
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
