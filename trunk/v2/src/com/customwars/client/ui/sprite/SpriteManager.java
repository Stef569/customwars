package com.customwars.client.ui.sprite;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.AnimLib;
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
  private TileSprite activeCursor;

  public SpriteManager(ResourceManager resources) {
    this.resources = resources;
    cursorSprites = new HashMap<String, TileSprite>();
    unitSprites = new HashMap<Unit, UnitSprite>();
    citySprites = new HashMap<City, CitySprite>();
    uniqueAnimations = new HashSet<Animation>();
  }

  /**
   * Only update animations in uniqueAnimations this is because we share Animations between sprites
   * If we update each sprite, then we will updated the same animation multiple times.
   */
  public void update(int elapsedTime) {
    for (Animation anim : uniqueAnimations) {
      anim.update(elapsedTime);
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
   * Get all the model objects in the map that are visible.
   * Make a Graphical representation for each of them.
   */
  private void initMapSprites() {
    clearAllMapSprites();

    for (Tile t : map.getAllTiles()) {
      Locatable locatable = t.getLastLocatable();
      Terrain terrain = t.getTerrain();

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
    UnitSprite sprite = createUnitSprite(unit);
    recolorUnitSprite(sprite, unit.getOwner().getColor(), unit.getID());
    addUnitSprite(unit, sprite);
    unit.addPropertyChangeListener(this);
  }

  /**
   * Create A Unitsprite, that is on the same Location as the unit
   * and is filtered so it has the color of the owner.
   */
  private UnitSprite createUnitSprite(Unit unit) {
    UnitSprite unitSprite;

    if (unitSprites.containsKey(unit)) {
      throw new RuntimeException("Unit " + unit + " is already cached...");
    } else {
      Tile t = (Tile) unit.getLocation();
      Animation animDying = null;
      unitSprite = new UnitSprite(t, map, unit, null);
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

  /**
   * Make a Graphical representation(UnitSprite) for the unit.
   * Preconditions:
   * The Unit should be owned by a Player to get the color
   */
  public void addUnitSprite(Unit unit, UnitSprite unitSprite) {
    checkTileSprites(unitSprite, unitSprites.values().iterator());
    unitSprites.put(unit, unitSprite);
    uniqueAnimations.add(unitSprite.anim);
  }

  /**
   * Make a Graphical representation(PropertySprite) for the city.
   * The Property should:
   * be owned by a Player to get the color
   * have a location to set the sprite position
   * have an id, this is used to retrieve the image
   */
  public void loadCitySprite(City city) {
    CitySprite sprite = createCitySprite(city);
    recolorCitySprite(sprite, city.getOwner().getColor(), city.getID());
    addCitySprite(city, sprite);
    city.addPropertyChangeListener(this);
  }

  /**
   * Create A CitySprite, that is on the same Location as the city
   */
  private CitySprite createCitySprite(City city) {
    CitySprite citySprite;

    if (citySprites.containsKey(city)) {
      throw new RuntimeException("City " + city + "  is already cached...");
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
   * changes the activeCursor to the cursor mapped by cursorName
   *
   * @param cursorName case incensitive name of the cursor ie Select, SELECT both return the same cursor
   */
  public void setActiveCursor(String cursorName) {
    if (isCursorSet(cursorName)) {
      if (activeCursor != null) {
        uniqueAnimations.remove(activeCursor.anim);
      }
      activeCursor = cursorSprites.get(cursorName);
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
   * Checks the newTileSprite to be added for correctness:
   * if the newTileSprite location is already used by another TileSprite then
   * that TileSprite is removed.
   *
   * @param newTileSprite the tileSprite that we want to add to the map
   * @return True if a sprite was removed
   */
  private boolean checkTileSprites(TileSprite newTileSprite, Iterator<? extends TileSprite> sprites) {
    boolean spriteRemoved = false;

    while (sprites.hasNext()) {
      if (sprites.next().getLocation() == newTileSprite.getLocation()) {
        sprites.remove();
        spriteRemoved = true;
      }
    }
    return spriteRemoved;
  }

  private void clearAllMapSprites() {
    unitSprites.clear();
    citySprites.clear();
  }

  public void propertyChange(PropertyChangeEvent evt) {
  }
}
