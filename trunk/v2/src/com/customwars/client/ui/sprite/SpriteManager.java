package com.customwars.client.ui.sprite;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
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
 * Each unique animation is stored and updated.
 * Gui classes are not allowed to change the model, They only listen for changes.
 *
 * For example when a city is captured a new Player is set.
 * This class receives a PropertyChangeEvent and recolors the city to the new owner color.
 *
 * @author stefan
 */
public class SpriteManager implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(SpriteManager.class);
  private TileMap<Tile> map;
  private ResourceManager resources;

  private Map<String, TileSprite> cursorSprites;
  private Map<Unit, UnitSprite> unitSprites;
  private Map<City, CitySprite> citySprites;
  private Set<Animation> uniqueAnimations;
  private ImageStrip unitDecorationStrip;

  private TileSprite activeCursor;

  public SpriteManager() {
    cursorSprites = new HashMap<String, TileSprite>();
    unitSprites = new HashMap<Unit, UnitSprite>();
    citySprites = new HashMap<City, CitySprite>();
    uniqueAnimations = new HashSet<Animation>();
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    unitDecorationStrip = resources.getSlickImgStrip("unitDecoration");
  }

  /**
   * Only update animations in uniqueAnimations this is because Animations are shared between sprites
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

  public void render(int x, int y, Graphics g) {
    for (Sprite sprite : citySprites.values()) {
      sprite.render(x, y, g);
    }
    for (Sprite sprite : unitSprites.values()) {
      sprite.render(x, y, g);
    }
    if (isCursorSet()) {
      activeCursor.render(x, y, g);
    }
  }

  public void renderUnit(int x, int y, Graphics g, Unit unit) {
    if (unitSprites.containsKey(unit)) {
      unitSprites.get(unit).render(x, y, g);
    }
  }

  public void renderCity(int x, int y, Graphics g, City city) {
    if (citySprites.containsKey(city)) {
      citySprites.get(city).render(x, y, g);
    }
  }

  public void moveCursorTo(Location location) {
    if (isCursorSet()) {
      activeCursor.setLocation(location);
    }
  }

  public void setMap(TileMap<Tile> map) {
    this.map = map;
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
      initColors();
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
      uniqueAnimations.add(activeCursor.anim);
  }

  /**
   * Search for units and cities,
   * get their Color, recoloring might take a while.
   */
  private void initColors() {
    Set<Color> colorsInMap = new HashSet<Color>(6);
    for (Tile t : map.getAllTiles()) {
      Locatable locatable = t.getLastLocatable();
      Terrain terrain = t.getTerrain();

      if (locatable instanceof Unit) {
        Unit unit = (Unit) locatable;
        colorsInMap.add(unit.getOwner().getColor());
      }

      if (terrain instanceof City) {
        City city = (City) terrain;
        colorsInMap.add(city.getOwner().getColor());
      }
    }

    resources.recolor(colorsInMap.toArray(new Color[]{}));
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

    logger.info("Init map sprites, units=" + unitSprites.size() + " Cities=" + citySprites.size() + " Tiles=" + map.countTiles());
  }

  /**
   * Make a Graphical representation(UnitSprite) for the unit.
   * Preconditions:
   * The Unit should:
   * be owned by a Player to get the color
   * have a location to set the sprite position
   * have an id, this is used to retrieve the image
   */
  public void loadUnitSprite(Unit unit) {
    Color unitColor = unit.getOwner().getColor();
    UnitSprite sprite = createUnitSprite(unit);
    sprite.setUpdateAnim(false);
    recolorUnitSprite(sprite, unitColor, unit.getID());
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

    if (unitSprites.containsKey(unit)) {
      throw new IllegalArgumentException("Unit " + unit + " is already cached...");
    } else {
      Tile t = (Tile) unit.getLocation();
      Animation animDying = resources.getAnim("EXPLOSION_" + unit.getArmyBranch());
      animDying.stopAt(animDying.getFrameCount() - 1);
      unitSprite = new UnitSprite(t, map, unit, unitDecorationStrip);
      unitSprite.setAnimDying(animDying);
    }
    return unitSprite;
  }

  private void recolorUnitSprite(UnitSprite sprite, Color c, int unitID) {
    Animation animLeft = resources.getUnitAnim(unitID, c, AnimLib.ANIM_LEFT);
    Animation animRight = resources.getUnitAnim(unitID, c, AnimLib.ANIM_RIGHT);
    Animation animUp = resources.getUnitAnim(unitID, c, AnimLib.ANIM_UP);
    Animation animDown = resources.getUnitAnim(unitID, c, AnimLib.ANIM_DOWN);
    Animation animInactive = resources.getUnitAnim(unitID, c, AnimLib.ANIM_INACTIVE);
    sprite.setAnimLeft(animLeft);
    sprite.setAnimRight(animRight);
    sprite.setAnimUp(animUp);
    sprite.setAnimDown(animDown);
    sprite.setAnimInActive(animInactive);
    sprite.updateAnim();
  }

  public void addUnitSprite(Unit unit, UnitSprite unitSprite) {
    checkTileSprites(unitSprite, unitSprites.values().iterator());
    unitSprites.put(unit, unitSprite);
    uniqueAnimations.add(unitSprite.anim);
  }

  /**
   * Make a Graphical representation(CitySprite) for the city.
   * The City should:
   * be owned by a Player to get the color
   * have a location to set the sprite position
   * have an id, this is used to retrieve the image
   */
  public void loadCitySprite(City city) {
    Color cityColor = city.getOwner().getColor();
    CitySprite sprite = createCitySprite(city);
    recolorCitySprite(sprite, cityColor, city.getID());
    sprite.setUpdateAnim(false);
    addCitySprite(city, sprite);
    sprite.addPropertyChangeListener(this);
    city.addPropertyChangeListener(this);
  }

  private CitySprite createCitySprite(City city) {
    CitySprite citySprite;

    if (citySprites.containsKey(city)) {
      throw new IllegalArgumentException("City " + city + "  is already cached...");
    } else {
      citySprite = new CitySprite(city.getLocation(), map, city, null);
    }
    return citySprite;
  }

  private void recolorCitySprite(CitySprite sprite, Color c, int cityID) {
    Animation animActive = resources.getCityAnim(cityID, c);
    Animation animFogged = resources.getCityAnim(cityID, c, AnimLib.ANIM_INACTIVE);
    sprite.setAnimActive(animActive);
    sprite.setAnimFogged(animFogged);
    sprite.updateAnim();
  }

  public void addCitySprite(City city, CitySprite citySprite) {
    checkTileSprites(citySprite, citySprites.values().iterator());
    citySprites.put(city, citySprite);
    uniqueAnimations.add(citySprite.anim);
  }

  /**
   * Changes the activeCursor to the cursor mapped by cursorName
   *
   * @param cursorName case insensitive name of the cursor ie 'Select' and 'SELECT' both return the same cursor
   */
  public void setActiveCursor(String cursorName) {
    if (isCursorSet(cursorName)) {
      if (activeCursor != null) {
        uniqueAnimations.remove(activeCursor.anim);
      }
      activeCursor = cursorSprites.get(cursorName.toUpperCase());
      uniqueAnimations.add(activeCursor.anim);
    } else {
      logger.warn(cursorName + " is not available, cursors:" + cursorSprites.keySet());
    }
  }

  public void addCursor(String name, TileSprite cursorSprite) {
    cursorSprite.setRenderInCenter(true);
    this.cursorSprites.put(name.toUpperCase(), cursorSprite);
  }

  public Location getCursorLocation() {
    return activeCursor.getLocation();
  }

  public boolean isCursorSet() {
    return activeCursor != null;
  }

  public boolean isCursorSet(String cursorName) {
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
        logger.debug("Removing Sprite on same location " + newTileSprite.getLocation());
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
      }
    } else if (evt.getSource() instanceof Sprite) {
      if (propertyName.equals("anim")) {
        spriteAnimChange(evt);
      }
    } else if (evt.getSource() instanceof Tile) {
      if (propertyName.equals("locatable")) {
        unitOnTileChanged(evt);
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
      recolorCitySprite(sprite, newColor, city.getID());
    }
  }

  private void unitOwnerChange(PropertyChangeEvent evt) {
    Player oldVal = (Player) evt.getOldValue();
    Player newVal = (Player) evt.getNewValue();
    Color oldColor = oldVal.getColor();
    Color newColor = newVal.getColor();

    if (!oldColor.equals(newColor)) {
      Unit unit = (Unit) evt.getSource();
      UnitSprite sprite = unitSprites.get(unit);
      recolorUnitSprite(sprite, newColor, unit.getID());
    }
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
   * This method is fired when a unit is added to a tile and on each move within a path,
   * thus it can't be used to remove unitsprites.
   */
  private void unitOnTileChanged(PropertyChangeEvent evt) {
    Unit newUnit = (Unit) evt.getNewValue();

    if (newUnit != null && !unitSprites.containsKey(newUnit)) {
      logger.debug("Found 1 new Unit, Creating sprite...");
      loadUnitSprite(newUnit);
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
}
