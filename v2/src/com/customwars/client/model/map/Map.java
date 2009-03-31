package com.customwars.client.model.map;

import com.customwars.client.model.Attacker;
import com.customwars.client.model.TurnHandler;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * A Game map, contains game specific fields
 * Handles Fog, paths
 *
 * @author stefan
 */
public class Map<T extends Tile> extends TileMap<T> implements TurnHandler {
  private static final Logger logger = Logger.getLogger(Map.class);
  private Properties properties;  // The properties of the map
  private int numPlayers;           // Amount of players that can play on this map
  private boolean fogOfWarOn;       // Is fog of war in effect
  private PathFinder pathFinder;    // To builds paths within the map

  public Map(int cols, int rows, int tileSize, int numPlayers) {
    super(cols, rows, tileSize);
    this.numPlayers = numPlayers;
    this.pathFinder = new PathFinder(this);
    this.properties = new Properties();
  }

  public Map(int cols, int rows, int tileSize, Properties properties, int numPlayers) {
    this(cols, rows, tileSize, numPlayers);
    this.properties = properties == null ? new Properties() : properties;
  }

  /**
   * Copy Constructor
   *
   * @param otherMap map to copy
   */
  public Map(Map<Tile> otherMap) {
    this(otherMap.getCols(), otherMap.getRows(), otherMap.getTileSize(), otherMap.numPlayers);
    this.fogOfWarOn = otherMap.fogOfWarOn;
    this.properties = new Properties(otherMap.properties);
    copyMapData(otherMap);
  }

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

  private void copyUnits(Tile t, Tile newTile) {
    for (int i = 0; i < t.getLocatableCount(); i++) {
      Unit unit = (Unit) t.getLocatable(i);
      Unit unitCopy = new Unit(unit);
      newTile.add(unitCopy);
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

  protected void validateMapState(boolean validateTiles) throws IllegalStateException {
    super.validateMapState(validateTiles);
    Args.validateBetweenZeroMax(numPlayers, Integer.MAX_VALUE, "numplayers");

    if (validateTiles) {
      for (Tile t : getAllTiles()) {
        if (t.getTerrain() == null) {
          throw new IllegalStateException("Tile " + t + " has no terrain.");
        }
      }
    }
  }

  /**
   * Start the turn by resetting the map to the new player
   */
  public void startTurn(Player currentPlayer) {
    resetMap(currentPlayer);
  }

  public void endTurn(Player currentPlayer) {
  }

  /**
   * Set all units to Idle and the units of this player to active.
   * if fog is enabled we apply the los for each active unit.
   *
   * @param player The player who's units should be made active and fog applied to.
   */
  public void resetMap(Player player) {
    validateMapState(true);
    resetUnits(player);
    if (fogOfWarOn) {
      resetFogMap(player);
    }
  }

  /**
   * Reset the map so that the player can
   * control his units by setting the unit Game Object state to ACTIVE
   * unit state is put to IDLE, so that a unit is
   * ready to perform another unit action (ie Capture)
   *
   * @param player The player who's units should be made active
   */
  private void resetUnits(Player player) {
    for (Location t : getAllTiles()) {
      Unit unit = getUnitOn(t);

      if (unit != null) {
        unit.setState(GameObjectState.IDLE);

        if (unit.getOwner() == player) {
          unit.setUnitState(UnitState.IDLE);
          unit.setState(GameObjectState.ACTIVE);
        }
      }
    }
  }

  /**
   * Retrieve a list of units in supply range of the supplier
   *
   * @param supplier The supplier of which we want to retrieve the suppliables units in range for
   * @return units in supply range of supplier that can be supplied
   */
  public List<Unit> getSuppliablesInRange(Unit supplier) {
    List<Unit> units = new ArrayList<Unit>();
    int minHealSupplyRange = supplier.getMinSupplyRange();
    int maxHealSupplyRange = supplier.getMaxSupplyRange();

    for (Tile t : getSurroundingTiles(supplier.getLocation(), minHealSupplyRange, maxHealSupplyRange)) {
      Unit unitInRange = getUnitOn(t);

      if (unitInRange != null) {
        if (supplier.canSupply(unitInRange)) {
          units.add(unitInRange);
        }
      }
    }
    return units;
  }

  /**
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @return units in attackRange of attacker.
   */
  public List<Unit> getEnemiesInRangeOf(Attacker attacker) {
    return getEnemiesInRangeOf(attacker, attacker.getLocation());
  }

  /**
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @param center   The center to iterate around
   * @return units in attackRange of attacker.
   */
  public List<Unit> getEnemiesInRangeOf(Attacker attacker, Location center) {
    List<Unit> units = new ArrayList<Unit>();
    int minAttackRange = attacker.getMinAttackRange();
    int maxAttackRange = attacker.getMaxAttackRange();

    for (Tile t : getSurroundingTiles(center, minAttackRange, maxAttackRange)) {
      Unit unit = getUnitOn(t);

      if (!t.isFogged() && attacker.canAttack(unit)) {
        units.add(unit);
      }
    }
    return units;
  }

  public void initUnitZonesForPlayer(Player player) {
    for (Unit unit : player.getArmy()) {
      buildMovementZone(unit);
      buildAttackZone(unit);
    }
  }

  /**
   * Build a zone in which the Unit can attack
   */
  public void buildAttackZone(Attacker attacker) {
    List<Location> attackZone = new ArrayList<Location>();
    int minAttRange = attacker.getMinAttackRange();
    int maxAttRange = attacker.getMaxAttackRange();

    for (Tile t : getAllTiles()) {
      if (inFireRange(attacker, t, minAttRange, maxAttRange)) {
        attackZone.add(t);
      }
    }
    attacker.setAttackZone(attackZone);
  }

  /**
   * Determines if a Location is in the attacker's firing range
   */
  public boolean inFireRange(Attacker attacker, Location mapLocation, int minAttackRange, int maxAttackRange) {
    if (maxAttackRange <= 0) return false;
    int t = Math.abs(mapLocation.getRow() - attacker.getLocation().getRow()) +
            Math.abs(mapLocation.getCol() - attacker.getLocation().getCol());

    //Indirects
    if (minAttackRange > 1) {
      return t >= minAttackRange && t <= maxAttackRange;
    } else {
      //Directs
      if (attacker.isWithinMoveZone(mapLocation)) return true;
      for (Location moveZoneLocation : attacker.getMoveZone()) {
        if (isAdjacent(mapLocation, moveZoneLocation)) return true;
      }
    }
    return false;
  }

  /**
   * Build a zone in which the mover can make a move
   */
  public void buildMovementZone(Mover mover) {
    mover.setMoveZone(pathFinder.getMovementZone(mover));
  }

  /**
   * Get a path of compass directions for the Mover to get to the destination
   * Each step in the path has a Direction relative to the previous location.
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
   * special vision cases(mountains,...) are handled in the calcExtraVision method
   *
   * @param player The player and his allies to apply the los for
   */
  private void showLosOn(Player player) {
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);
      City city = getCityOn(t);
      int visionBonus = t.getTerrain().getVision();

      if (unit != null && unit.getOwner().isAlliedWith(player)) {
        int vision = unit.getVision();
        clearSight(t, vision + visionBonus);
      }

      if (city != null && city.getOwner().isAlliedWith(player)) {
        int vision = city.getVision();
        clearSight(t, vision + visionBonus);
      }
    }
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
   * then there are some terrains and properties that remain fogged.
   *
   * They can only be made clear if the unit is directly next to it
   * The sameTile and adjacent tile are always visible.
   *
   * @param tileToBeFogged The tile to check relative to the baseTile
   * @param baseTile       The tile the unit is on
   * @return If the tile can be cleared of fog.
   */
  public boolean canClearFog(Tile baseTile, Tile tileToBeFogged) {
    City city = getCityOn(tileToBeFogged);
    Unit unit = getUnitOn(tileToBeFogged);

    // If unit/City is hidden(remain fogged until directly next to it)
    // we cannot clear the fog.
    boolean hiddenUnit = unit != null && unit.isHidden();
    boolean hiddenCity = city != null && city.isHidden();

    // If directly next to the tile we can see everything
    boolean adjacent = isAdjacent(tileToBeFogged, baseTile);

    return (!hiddenUnit && !hiddenCity) || adjacent;
  }

  public void setFogOfWarOn(boolean fogOfWarOn) {
    boolean oldVal = this.fogOfWarOn;
    this.fogOfWarOn = fogOfWarOn;
    firePropertyChange("fogOfWar", oldVal, fogOfWarOn);
  }

  public void addProperty(String key, String value) {
    properties.put(key, value);
  }

  /**
   * Retrieves the last unit from location
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
