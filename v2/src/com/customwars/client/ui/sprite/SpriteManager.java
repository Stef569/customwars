package com.customwars.client.ui.sprite;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Handles all sprites in the game
 * cursors, units, cities
 *
 * Each unique animation is stored and updated, if we would update each sprite animation
 * there would be a small interval between other sprite animations.
 *
 * This class listens for changes from the model and updates the graphical representations
 *
 * For example when a city is captured a new Player is set.
 * This class receives a PropertyChangeEvent and recolors the city to the new owner color.
 *
 * @author stefan
 */
public class SpriteManager implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(SpriteManager.class);
  private static final Color NEUTRAL_COLOR = App.getColor("plugin.neutral_color");
  private final TileMap<Tile> map;
  private ResourceManager resources;

  private final Map<String, TileSprite> cursorSprites;
  private final Map<Unit, UnitSprite> unitSprites;
  private final Map<City, CitySprite> citySprites;
  private final Set<Animation> uniqueAnimations;
  private ImageStrip unitDecorationStrip;

  private TileSprite activeCursor;
  private Font numbersFont;
  private boolean renderSprites = true;

  public SpriteManager(TileMap<Tile> map) {
    this.map = map;
    this.cursorSprites = new HashMap<String, TileSprite>();
    this.unitSprites = new HashMap<Unit, UnitSprite>();
    this.citySprites = new HashMap<City, CitySprite>();
    this.uniqueAnimations = new HashSet<Animation>();
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    unitDecorationStrip = resources.getSlickImgStrip("unitDecoration");
    numbersFont = resources.getFont("numbers");
  }

  /**
   * Only update animations in the uniqueAnimations collection
   * this is because Animations are shared between sprites
   * updating a sprite will not update the sprite animation
   */
  public void update(int elapsedTime) {
    for (Animation anim : uniqueAnimations) {
      anim.update(elapsedTime);
    }

    for (CitySprite citySprite : citySprites.values()) {
      citySprite.update(elapsedTime);
    }

    Iterator it = unitSprites.values().iterator();
    while (it.hasNext()) {
      UnitSprite sprite = (UnitSprite) it.next();
      sprite.update(elapsedTime);

      if (sprite.canBeRemoved()) {
        logger.debug("Removing UnitSprite");
        it.remove();
      }
    }
  }

  public void renderCursor(Graphics g) {
    if (renderSprites && isCursorSet()) {
      activeCursor.render(g);
    }
  }

  public void renderUnit(Graphics g, Unit unit) {
    if (renderSprites && unit != null && unitSprites.containsKey(unit)) {
      unitSprites.get(unit).render(g);
    }
  }

  public void renderDyingUnits(Graphics g) {
    if (renderSprites) {
      for (UnitSprite sprite : unitSprites.values()) {
        if (sprite.isDying()) {
          sprite.render(g);
        }
      }
    }
  }

  public void renderCity(Graphics g, City city) {
    if (renderSprites && citySprites.containsKey(city)) {
      citySprites.get(city).render(g);
    }
  }

  public void moveCursorTo(Location newLocation) {
    if (isCursorSet()) {
      activeCursor.setLocation(newLocation);
    } else {
      logger.warn("Cannot move cursor, no active cursor set");
    }
  }

  //----------------------------------------------------------------------------
  // load Sprites
  //----------------------------------------------------------------------------

  /**
   * Create and locate each Sprite
   */
  public void loadSprites() {
    if (map != null) {
      uniqueAnimations.clear();
      initCursors();
      initMapSprites();
    }
  }

  /**
   * Move each cursor on a random position in the map
   * to prevent off the map cursor positions.
   *
   * If an activeCursor has been set then add it to the uniqueAnimations
   */
  private void initCursors() {
    for (TileSprite cursor : cursorSprites.values()) {
      cursor.setLocation(map.getRandomTile());
    }

    if (activeCursor != null)
      addUniqueSprite(activeCursor);
  }

  /**
   * Get all the model objects in the map that are visible.
   * Make a Graphical representation for each of them.
   */
  private void initMapSprites() {
    clearAllMapSprites();

    for (Tile t : map.getAllTiles()) {
      Locatable locatable = t.getLastLocatable();
      Terrain terrain = t.getTerrain();
      t.addPropertyChangeListener(this);

      if (locatable instanceof Unit) {
        loadUnitSprite((Unit) locatable);
      }

      if (terrain instanceof City) {
        loadCitySprite((City) terrain);
      }
    }

    logger.info("Init map sprites, units=" + unitSprites.size() + " cities=" + citySprites.size() +
      " tiles=" + map.countTiles() + " tileSize=" + map.getTileSize());
  }

  /**
   * Make a Graphical representation(UnitSprite) for the unit.
   * The Unit should:
   * be owned by a Player to get the color
   * have a location to set the sprite position
   * have an id, this is used to retrieve the image
   *
   * @param unit Unit to make a sprite for
   */
  public void loadUnitSprite(Unit unit) {
    Color unitColor = unit.getOwner().getColor();
    UnitSprite sprite = createUnitSprite(unit);
    sprite.setUpdateAnim(false);
    recolorUnitSprite(sprite, unitColor, unit);
    addUnitSprite(unit, sprite);
    sprite.addPropertyChangeListener(this);
    unit.addPropertyChangeListener(this);
  }

  /**
   * Create A Unitsprite, that is on the same Location as the unit
   * and is filtered so it has the color of the owner.
   */
  private UnitSprite createUnitSprite(Unit unit) {
    UnitSprite unitSprite;

    if (!unitSprites.containsKey(unit)) {
      Animation animDying = resources.getAnim("EXPLOSION_" + unit.getArmyBranch());
      animDying.stopAt(animDying.getFrameCount() - 1);
      unitSprite = new UnitSprite(map, unit, unitDecorationStrip);
      unitSprite.setAnimDying(animDying);
      unitSprite.setFont(numbersFont);
    } else {
      throw new IllegalArgumentException("Unit " + unit + " is already cached.");
    }
    return unitSprite;
  }

  private void recolorUnitSprite(UnitSprite sprite, Color color, Unit unit) {
    Animation animLeft = resources.getUnitAnim(unit, color, Direction.WEST);
    Animation animRight = resources.getUnitAnim(unit, color, Direction.EAST);
    Animation animUp = resources.getUnitAnim(unit, color, Direction.NORTH);
    Animation animDown = resources.getUnitAnim(unit, color, Direction.SOUTH);
    Animation animInactive = resources.getInactiveUnitAnim(unit, color);
    sprite.setAnimLeft(animLeft);
    sprite.setAnimRight(animRight);
    sprite.setAnimUp(animUp);
    sprite.setAnimDown(animDown);
    sprite.setAnimInActive(animInactive);
    sprite.updateAnim();
  }

  private void addUnitSprite(Unit unit, UnitSprite unitSprite) {
    checkTileSprites(unitSprite, unitSprites.values().iterator());
    unitSprites.put(unit, unitSprite);
    addUniqueSprite(unitSprite);
  }

  /**
   * Make a Graphical representation(CitySprite) for the city.
   * The City should:
   * be owned by a Player to get the color
   * have a location to set the sprite position
   * have an id, this is used to retrieve the image
   *
   * @param city City to create a sprite for
   */
  private void loadCitySprite(City city) {
    Color cityColor = city.getOwner().getColor();
    CitySprite sprite = createCitySprite(city);
    recolorCitySprite(city, sprite, cityColor);
    sprite.setUpdateAnim(false);
    addCitySprite(city, sprite);
    sprite.addPropertyChangeListener(this);
    city.addPropertyChangeListener(this);
  }

  private CitySprite createCitySprite(City city) {
    CitySprite citySprite;

    if (!citySprites.containsKey(city)) {
      citySprite = new CitySprite(map, city);
    } else {
      throw new IllegalArgumentException("City " + city + "  is already cached.");
    }
    return citySprite;
  }

  private void recolorCitySprite(City city, CitySprite sprite, Color color) {
    Animation animActive = resources.getCityAnim(city, color);
    Animation animInActive = resources.getInActiveCityAnim(city, NEUTRAL_COLOR);
    Animation animFogged = getFoggedCityAnim(city, color);
    sprite.setAnimActive(animActive);
    sprite.setAnimInActive(animInActive);
    sprite.setAnimFogged(animFogged);
    sprite.updateAnim();
  }

  /**
   * In Fow The HQ is always visible. All other cities are neutral.
   */
  private Animation getFoggedCityAnim(City city, Color color) {
    Animation cachedAnim;
    if (city.isHQ()) {
      cachedAnim = resources.getCityAnim(city, color);
    } else {
      cachedAnim = resources.getCityAnim(city, NEUTRAL_COLOR);
    }

    Animation foggedCityAnim = new Animation();
    for (int i = 0; i < cachedAnim.getFrameCount(); i++) {
      foggedCityAnim.addFrame(cachedAnim.getImage(i), 1);
    }
    foggedCityAnim.setLooping(false);
    return foggedCityAnim;
  }

  private void addCitySprite(City city, CitySprite citySprite) {
    checkTileSprites(citySprite, citySprites.values().iterator());
    citySprites.put(city, citySprite);
    addUniqueSprite(citySprite);
  }

  /**
   * Changes the activeCursor to the cursor mapped by cursorName
   *
   * @param cursorName case insensitive name of the cursor ie 'Select' and 'SELECT' both return the same cursor
   */
  public void setActiveCursor(String cursorName) {
    if (hasCursor(cursorName)) {
      if (activeCursor != null) {
        uniqueAnimations.remove(activeCursor.anim);
      }
      TileSprite cursor = cursorSprites.get(cursorName.toUpperCase());
      cursor.activate();
      addUniqueSprite(cursor);
      this.activeCursor = cursor;
    } else {
      logger.warn(cursorName + " is not available, cursors:" + cursorSprites.keySet());
    }
  }

  private void addUniqueSprite(Sprite sprite) {
    assert sprite.anim != null : "Sprite can not have null anim";
    uniqueAnimations.add(sprite.anim);
  }

  public void setRenderSprites(boolean renderSprites) {
    this.renderSprites = renderSprites;
  }

  public void addCursor(String name, TileSprite cursorSprite) {
    cursorSprite.setRenderInCenter(true);
    this.cursorSprites.put(name.toUpperCase(), cursorSprite);
  }

  public Location getCursorLocation() {
    if (isCursorSet()) {
      return activeCursor.getLocation();
    } else {
      throw new IllegalStateException("no active cursor set");
    }
  }

  public int getCursorEffectRange() {
    return activeCursor.getEffectRange();
  }

  public boolean isCursorSet() {
    return activeCursor != null;
  }

  public boolean hasCursor(String cursorName) {
    return cursorSprites.containsKey(cursorName.toUpperCase());
  }

  /**
   * Checks the new TileSprite to be added for correctness:
   * if the TileSprite location is already used by another TileSprite in the sprite collection then
   * the latter is removed.
   *
   * @param newTileSprite the tileSprite that we want to add to the sprites collection
   * @return True if a sprite was removed
   */
  private boolean checkTileSprites(TileSprite newTileSprite, Iterator<? extends TileSprite> sprites) {
    boolean spriteRemoved = false;

    while (sprites.hasNext()) {
      if (sprites.next().getLocation() == newTileSprite.getLocation()) {
        sprites.remove();
        spriteRemoved = true;
        logger.debug("Removing Sprite on same location " + newTileSprite.getLocation().getLocationString());
      }
    }
    return spriteRemoved;
  }

  private void clearAllMapSprites() {
    unitSprites.clear();
    citySprites.clear();
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() instanceof City) {
      if (propertyName.equals("owner")) {
        cityOwnerChange(evt);
      }
    } else if (evt.getSource() instanceof Unit) {
      if (propertyName.equals("owner")) {
        unitOwnerChange(evt);
      } else if (propertyName.equals("location")) {
        unitLocationchange(evt);
      } else if (propertyName.equals("transport")) {
        transportChange(evt);
      }
    } else if (evt.getSource() instanceof Sprite) {
      if (propertyName.equals("anim")) {
        spriteAnimChange(evt);
      }
    } else if (evt.getSource() instanceof Tile) {
      if (propertyName.equals("locatable")) {
        unitOnTileChange(evt);
      } else if (propertyName.equals("terrain")) {
        terrainOnTileChange(evt);
      }
    }
  }

  private void cityOwnerChange(PropertyChangeEvent evt) {
    Player oldVal = (Player) evt.getOldValue();
    Player newVal = (Player) evt.getNewValue();
    Color oldColor = oldVal.getColor();
    Color newColor = newVal.getColor();

    if (!oldColor.equals(newColor)) {
      City city = (City) evt.getSource();
      CitySprite sprite = citySprites.get(city);
      recolorCitySprite(city, sprite, newColor);
    }
  }

  private void unitOwnerChange(PropertyChangeEvent evt) {
    Player oldVal = (Player) evt.getOldValue();
    Player newVal = (Player) evt.getNewValue();
    if (oldVal == null || newVal == null) return;

    Color oldColor = oldVal.getColor();
    Color newColor = newVal.getColor();

    if (!oldColor.equals(newColor)) {
      Unit unit = (Unit) evt.getSource();
      UnitSprite sprite = unitSprites.get(unit);
      recolorUnitSprite(sprite, newColor, unit);
    }
  }

  /**
   * When the location of a unit changes to null, remove the unit sprite
   */
  private void unitLocationchange(PropertyChangeEvent evt) {
    Unit unit = (Unit) evt.getSource();
    Location newLocation = (Location) evt.getNewValue();

    if (newLocation == null && unitSprites.containsKey(unit)) {
      removeUnitSprite(unit);
    }
  }

  private void transportChange(PropertyChangeEvent evt) {
    Unit transport = (Unit) evt.getSource();
    addUnit(transport);
  }

  private void spriteAnimChange(PropertyChangeEvent evt) {
    animChange((Animation) evt.getOldValue(), (Animation) evt.getNewValue());
  }

  private void animChange(Animation oldAnim, Animation newAnim) {
    // First look if the old Animation can be removed
    if (oldAnim != null && !isAnimInUse(oldAnim)) {
      uniqueAnimations.remove(oldAnim);
    }

    // Add the new Animation
    if (!uniqueAnimations.contains(newAnim) && newAnim != null && newAnim.getFrameCount() > 1) {
      uniqueAnimations.add(newAnim);
    }
  }

  private boolean isAnimInUse(Animation anim) {
    if (anim == null) return false;
    for (Sprite sprite : unitSprites.values()) {
      if (sprite.isRenderingAnim(anim)) {
        return true;
      }
    }
    for (Sprite sprite : citySprites.values()) {
      if (sprite.isRenderingAnim(anim)) {
        return true;
      }
    }
    return true;
  }

  /**
   * When a unit is added to a tile, ie. on each move within a path
   * A new unitSprite is created if it is not already present
   */
  private void unitOnTileChange(PropertyChangeEvent evt) {
    Unit newUnit = (Unit) evt.getNewValue();
    addUnit(newUnit);
  }

  private void addUnit(Unit newUnit) {
    if (newUnit != null && !unitSprites.containsKey(newUnit)) {
      logger.debug("Found 1 new Unit, Creating sprite @ " + newUnit.getLocation().getLocationString());
      loadUnitSprite(newUnit);
    }
  }

  private void terrainOnTileChange(PropertyChangeEvent evt) {
    Location newLocation = (Location) evt.getSource();
    Terrain newTerrain = (Terrain) evt.getNewValue();
    Terrain oldTerrain = (Terrain) evt.getOldValue();

    if (newTerrain instanceof City) {
      City city = (City) newTerrain;
      if (newLocation == null && citySprites.containsKey(city)) {
        removeCitySprite(city);
      } else {
        loadCitySprite(city);
      }
    } else if (oldTerrain instanceof City) {
      City city = (City) oldTerrain;
      removeCitySprite(city);
    }
  }

  public void removeCitySprite(City city) {
    Sprite sprite = citySprites.get(city);
    logger.debug("Removing CitySprite");
    citySprites.remove(city);
    animChange(sprite.anim, null);
  }

  public void removeUnitSprite(Unit unit) {
    Sprite sprite = unitSprites.get(unit);
    logger.debug("Removing UnitSprite");
    unitSprites.remove(unit);
    animChange(sprite.anim, null);
  }

  public CitySprite getCitySprite(City city) {
    return citySprites.get(city);
  }

  public boolean isRenderingSprites() {
    return renderSprites;
  }

  /**
   * @return true if the dying unit animation has been completed
   */
  public boolean isDyingUnitAnimationCompleted() {
    for (UnitSprite unitSprite : unitSprites.values()) {
      if (unitSprite.isDying() && !unitSprite.anim.isStopped()) {
        return false;
      }
    }

    return true;
  }

  public void removeCursorListener(PropertyChangeListener listener) {
    for (TileSprite cursor : cursorSprites.values()) {
      cursor.removePropertyChangeListener(listener);
    }
  }

  public void addCursorListener(PropertyChangeListener listener) {
    for (TileSprite cursor : cursorSprites.values()) {
      cursor.addPropertyChangeListener(listener);
    }
  }
}
