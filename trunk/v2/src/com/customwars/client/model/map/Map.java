package com.customwars.client.model.map;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.path.Mover;
import com.customwars.client.model.map.path.PathFinder;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * A Game map extends tileMap adding game specific fields
 * Handles Fog, paths and units
 * Zones: move zone, attack zone
 * Surrounding information: suppliables, enemies in a Range
 *
 * Properties contains game specific text eg:
 * NAME -> the map name
 * VERSION -> for what version is this map made
 * AUTHOR -> who made this map
 * DESCRIPTION -> Small text describing what this map is all about
 *
 * At each start of a turn the map is reset see {@link #resetMap(Player)}
 *
 * @author stefan
 */
public class Map<T extends Tile> extends TileMap<T> implements TurnHandler {
  private static final Logger logger = Logger.getLogger(Map.class);
  private Properties properties;    // The properties of the map
  private int numPlayers;           // Amount of players that can play on this map
  private boolean fogOfWarOn;       // Is fog of war in effect
  private PathFinder pathFinder;    // To builds paths within the map

  public Map(int cols, int rows, int tileSize, int numPlayers, boolean fogOfWar, Terrain startTerrain, Properties properties) {
    this(cols, rows, tileSize, numPlayers, fogOfWar, startTerrain);
    this.properties = properties == null ? new Properties() : properties;
  }

  /**
   * Create a new Map
   *
   * @param cols         width in tiles
   * @param rows         height in tiles
   * @param tileSize     square size of 1 tile in pixels
   * @param numPlayers   Amount of players that can play on this map
   * @param fogOfWar     indicates if fow is on
   * @param startTerrain the terrain that is used to fill the map
   */
  public Map(int cols, int rows, int tileSize, int numPlayers, boolean fogOfWar, Terrain startTerrain) {
    super(cols, rows, tileSize);
    Args.validateBetweenZeroMax(numPlayers, Integer.MAX_VALUE, "numplayers");

    this.numPlayers = numPlayers;
    this.fogOfWarOn = fogOfWar;
    this.pathFinder = new PathFinder(this);
    this.properties = new Properties();
    fillMap(cols, rows, startTerrain);
  }

  @SuppressWarnings("unchecked")
  private void fillMap(int cols, int rows, Terrain terrain) {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Tile t = new Tile(col, row, terrain);
        setTile(col, row, (T) t);
      }
    }
  }

  /**
   * Copy Constructor
   *
   * @param otherMap map to copy
   */
  public Map(Map<Tile> otherMap) {
    this(otherMap.getCols(), otherMap.getRows(), otherMap.getTileSize(), otherMap.numPlayers, otherMap.fogOfWarOn, otherMap.getTile(0, 0).getTerrain());
    this.properties = new Properties(otherMap.properties);
    copyMapData(otherMap);
  }

  @SuppressWarnings("unchecked")
  private void copyMapData(Map<Tile> otherMap) {
    for (Tile t : otherMap.getAllTiles()) {
      int col = t.getCol();
      int row = t.getRow();
      boolean fogged = t.isFogged();

      Tile tileCopy = new Tile(col, row, copyTerrain(t.getTerrain()), fogged);
      copyUnits(t, tileCopy);
      setTile((T) tileCopy);
    }
  }

  private Terrain copyTerrain(Terrain terrain) {
    Terrain terrainCopy;
    if (terrain instanceof City) {
      terrainCopy = new City((City) terrain);
    } else {
      terrainCopy = terrain;
    }
    return terrainCopy;
  }

  private void copyUnits(Tile oldTile, Tile newTile) {
    for (int i = 0; i < oldTile.getLocatableCount(); i++) {
      Unit unitCopy = new Unit(getUnitOn(oldTile));
      newTile.add(unitCopy);
    }
  }

  public void fillWithTerrain(Terrain terrain) {
    for (Tile t : getAllTiles()) {
      t.setTerrain(terrain);
    }
  }

  /**
   * Start the turn by resetting the map to the given player
   */
  public void startTurn(Player player) {
    resetMap(player);
  }

  public void endTurn(Player player) {
  }

  /**
   * Set all units to Idle and the units of this player to active.
   * if fog is enabled we apply the los for each owned unit.
   *
   * @param player The player who's units should be made active and fog applied to.
   */
  public void resetMap(Player player) {
    resetUnits(player);
    if (fogOfWarOn) {
      resetFogMap(player);
    }
  }

  /**
   * For each unit
   * #1 set Game Object State: not owned by player -> IDLE, owned by player -> ACTIVE.
   * #2 Reset the action(Capturing, submerged) the owned unit was performing one turn ago.
   *
   * @param player The player who's units should be reset
   */
  private void resetUnits(Player player) {
    for (Location t : getAllTiles()) {
      Unit unit = getUnitOn(t);

      if (unit != null) {
        if (unit.getOwner() == player) {
          unit.setState(GameObjectState.ACTIVE);
          unit.setUnitState(UnitState.IDLE);
        } else {
          unit.setState(GameObjectState.IDLE);
        }
      }
    }
  }

  /**
   * Retrieve a list of visible units in supply range of the supplier
   *
   * @param supplier The supplier of which we want to retrieve the suppliables units in range for
   * @return units in supply range of supplier that can be supplied and are not fogged
   */
  public List<Unit> getSuppliablesInRange(Unit supplier) {
    List<Unit> units = new ArrayList<Unit>();

    for (Tile t : getSurroundingTiles(supplier.getLocation(), supplier.getSupplyRange())) {
      Unit unitInRange = getUnitOn(t);

      if (unitInRange != null) {
        if (!t.isFogged() && supplier.canSupply(unitInRange)) {
          units.add(unitInRange);
        }
      }
    }
    return units;
  }

  /**
   * Retrieve a list of visible units in attack range of the attacker
   *
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @return units in attackRange of attacker that can be attacked and are not fogged
   */
  public List<Unit> getEnemiesInRangeOf(Attacker attacker) {
    return getEnemiesInRangeOf(attacker, attacker.getLocation());
  }

  /**
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @param center   The center to iterate around
   * @return units in attackRange of attacker that can be attacked and are not fogged
   */
  public List<Unit> getEnemiesInRangeOf(Attacker attacker, Location center) {
    List<Unit> units = new ArrayList<Unit>();

    for (Tile t : getSurroundingTiles(center, attacker.getAttackRange())) {
      Unit unit = getUnitOn(t);

      if (!t.isFogged() && attacker.canAttack(unit)) {
        units.add(unit);
      }
    }
    return units;
  }

  /**
   * Init the move and attack zone of each unit owned by the given player
   */
  public void initUnitZonesForPlayer(Player player) {
    for (Unit unit : player.getArmy()) {
      buildMovementZone(unit);
      buildAttackZone(unit);
    }
  }

  /**
   * Build a zone in which the mover can make a move and set it to the mover
   * If the mover is within a transport then the movezone is null
   * If the mover cannot move then the current mover location is set as the moveZone
   */
  public void buildMovementZone(Mover mover) {
    List<Location> moveZone;

    if (mover.getLocation() instanceof Unit) {
      moveZone = null;
    } else {
      if (mover.canMove()) {
        moveZone = pathFinder.getMovementZone(mover);
      } else {
        moveZone = Arrays.asList(mover.getLocation());
      }
    }
    mover.setMoveZone(moveZone);
  }

  /**
   * Build a zone in which the Attacker can attack and set it to the attacker
   * If the attacker is within a transport then the attackzone is null
   */
  public void buildAttackZone(Attacker attacker) {
    List<Location> attackZone = new ArrayList<Location>();
    Range attackRange = attacker.getAttackRange();

    if (attacker.getLocation() instanceof Unit) {
      attackZone = null;
    } else {
      for (Tile t : getAllTiles()) {
        if (inFireRange(attacker, t, attackRange)) {
          attackZone.add(t);
        }
      }
    }
    attacker.setAttackZone(attackZone);
  }

  /**
   * Determines if a Location is in the attacker's attack range
   */
  public boolean inFireRange(Attacker attacker, Location location, Range attackRange) {
    if (attackRange.getMaxRange() >= 0) {
      int distance = getDistanceBetween(location, attacker.getLocation());
      boolean indirect = attackRange.getMinRange() > 1;

      if (indirect) {
        return attackRange.isInRange(distance);
      } else {
        if (attacker.isWithinMoveZone(location) || isAdjacentOfLocations(location, attacker.getMoveZone())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isAdjacentOfLocations(Location location, List<Location> locations) {
    for (Location loc : locations) {
      if (isAdjacent(location, loc)) return true;
    }
    return false;
  }

  /**
   * Get a path of directions(N,E,S,W) from the mover location to the destination
   * Each Direction in the path is relative to the previous location.
   * In case of illegal data an empty list is returned
   */
  public List<Direction> getDirectionsPath(Mover mover, Location destination) {
    if (mover == null) {
      logger.warn("mover is null");
      return Collections.emptyList();
    }

    return pathFinder.getDirections(mover, destination);
  }

  /**
   * First set all tiles to fogged
   * then set the tiles that are visible for this player and his allies to clear(not fogged).
   *
   * @param player The player and all his allies who's units line of sight aka vision should be visible.
   */
  public void resetFogMap(Player player) {
    if (fogOfWarOn) {
      fillFog(true);
      showLosOn(player);
    }
  }

  /**
   * Completely fill/clear a Map of fog.
   *
   * @param fog if the map should be fogged or cleared
   */
  public void fillFog(boolean fog) {
    for (Tile t : getAllTiles()) {
      t.setFogged(fog);
    }
  }

  /**
   * Resets the map fog of war.
   * This is done by setting all the tiles within a Unit and City Line of sight to fogged=false.
   *
   * @param player The player and his allies to apply the los for
   */
  private void showLosOn(Player player) {
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);
      City city = getCityOn(t);

      if (unit != null && unit.getOwner().isAlliedWith(player)) {
        int visionBonus = getUnitVisionBonus(unit);
        int vision = unit.getVision();
        clearSight(t, vision + visionBonus);
      }

      if (city != null && city.getOwner().isAlliedWith(player)) {
        int vision = city.getVision();
        clearSight(t, vision);
      }
    }
  }

  private int getUnitVisionBonus(Unit unit) {
    int visionBonus = 0;
    Tile t = (Tile) unit.getLocation();

    if (unit.getArmyBranch() == ArmyBranch.LAND) {
      Terrain terrain = t.getTerrain();
      if (terrain.getHeight() >= 2) {
        visionBonus = terrain.getVision();
      }
    }
    return visionBonus;
  }

  /**
   * Clears all visible tiles within a vision range
   *
   * @param baseTile The location tile to clear the los around
   * @param vision   The amount of tiles that have to be cleared in all directions
   */
  public void clearSight(Tile baseTile, int vision) {
    int col = baseTile.getCol();
    int row = baseTile.getRow();

    // Clear baseTile
    baseTile.setFogged(false);

    // Clear all visible tiles within vision range
    for (int i = 1; i <= vision; i++) // for each layer of vision
    {
      for (int j = 0; j < i; j++)     // for each tile within that layer
      {
        clearFog(baseTile, getTile(col + i - j, row + j)); // bottom right sector
        clearFog(baseTile, getTile(col - i + j, row - j)); // top left sector
        clearFog(baseTile, getTile(col - j, row + i - j)); // bottom left sector
        clearFog(baseTile, getTile(col + j, row - i + j)); // top right sector
      }
    }
  }

  private void clearFog(Tile baseTile, Tile tileToBeFogged) {
    if (isValid(tileToBeFogged) && canClearFog(baseTile, tileToBeFogged)) {
      tileToBeFogged.setFogged(false);
    }
  }

  /**
   * If a tile is within the unit los
   * then there are some gameObjects that remain fogged until directly next to it.
   * See the isHidden() function.
   *
   * They can only be made clear if the unit is directly next to it
   * The base and adjacent tiles are always visible.
   *
   * @param tileToBeFogged The tile to check relative to the baseTile
   * @param baseTile       The tile the unit is on
   * @return If the tile can be cleared of fog.
   */
  public boolean canClearFog(Tile baseTile, Tile tileToBeFogged) {
    Unit unit = getUnitOn(tileToBeFogged);
    Terrain terrain = tileToBeFogged.getTerrain();

    boolean hiddenTerrain = terrain.isHidden();

    // If directly next to the tile we can see everything
    boolean adjacent = isAdjacent(tileToBeFogged, baseTile);

    return (!hiddenTerrain) || adjacent;
  }

  public void setFogOfWarOn(boolean fogOfWarOn) {
    boolean oldVal = this.fogOfWarOn;
    this.fogOfWarOn = fogOfWarOn;
    firePropertyChange("fogOfWar", oldVal, fogOfWarOn);
  }

  public void setNumPlayers(int numPlayers) {
    this.numPlayers = numPlayers;
  }

  public void putProperty(String key, String value) {
    properties.put(key, value);
  }

  /**
   * Retrieves the last added unit from location
   * if location doesn't contain a unit <b>NULL</b> is returned
   */
  public Unit getUnitOn(Location location) {
    Locatable locatable = location.getLastLocatable();
    if (locatable instanceof Unit) {
      return (Unit) locatable;
    }
    return null;
  }

  /**
   * Retrieves a city that is on the location
   * if location doesn't contain a city <b>NULL</b> is returned
   */
  public City getCityOn(Location location) {
    Tile t = (Tile) location;
    Terrain terrain = t.getTerrain();
    if (terrain instanceof City) {
      return (City) terrain;
    }
    return null;
  }

  public boolean isFogOfWarOn() {
    return fogOfWarOn;
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public boolean hasProperty(String key) {
    return properties.containsKey(key);
  }

  public Iterable<String> getPropertyKeys() {
    final Iterator it = properties.keySet().iterator();
    return new Iterable<String>() {
      public Iterator<String> iterator() {
        return new Iterator<String>() {
          public boolean hasNext() {
            return it.hasNext();
          }

          public String next() {
            return (String) it.next();
          }

          public void remove() {
            throw new UnsupportedOperationException("Removing properties is not allowed");
          }
        };
      }
    };
  }
}
