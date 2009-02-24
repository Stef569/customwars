package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.path.Mover;
import com.customwars.client.model.map.path.PathFinder;
import com.customwars.client.model.rules.MapRules;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A Game map, contains game specific fields
 * Handles Fog, paths
 *
 * @author stefan
 */
public class Map<T extends Tile> extends TileMap<T> {
  private static final Logger logger = Logger.getLogger(Map.class);
  private Properties properties = new Properties();
  private int numPlayers;           // Amount of players that can play on this map
  private boolean fogOfWarOn;       // is fog of war in effect
  private PathFinder pathFinder;    // to builds paths within the map
  private MapRules rules;

  public Map(int cols, int rows, int tileSize, int numPlayers, boolean fogOfWarOn) {
    super(cols, rows, tileSize);
    this.numPlayers = numPlayers;
    this.fogOfWarOn = fogOfWarOn;
    pathFinder = new PathFinder(this);
  }

  public Map(int cols, int rows, int tileSize, Properties properties, int numPlayers, boolean fogOfWarOn) {
    this(cols, rows, tileSize, numPlayers, fogOfWarOn);
    this.properties = properties == null ? new Properties() : properties;
  }

  // ---------------------------------------------------------------------------
  // Validate
  // ---------------------------------------------------------------------------
  protected void validateMapState(boolean validateTiles) throws IllegalStateException {
    super.validateMapState(validateTiles);
    Args.validateBetweenZeroMax(numPlayers, Integer.MAX_VALUE);
    for (Tile t : getAllTiles()) {
      if (t.getTerrain() == null) {
        throw new IllegalStateException("Tile " + t + " has no terrain.");
      }
    }

    if (numPlayers <= 0) {
      throw new IllegalStateException("Max amount of Players cannot be <=0");
    }
  }

  // ----------------------------------------------------------------------------
  // Actions
  // ----------------------------------------------------------------------------
  /**
   * Start the turn by resetting the map to the new player
   */
  public void startTurn(Player currentPlayer) {
    resetMap(currentPlayer);
    checkHealAndSupplyConditionsFor(currentPlayer);
  }

  private void checkHealAndSupplyConditionsFor(Player currentPlayer) {
    for (Tile t : getAllTiles()) {
      City city = getCityOn(t);
      Unit unit = getUnitOn(t);

      if (city != null && city.getOwner().isAlliedWith(currentPlayer)) {
        if (city.canSupply(unit)) {
          city.supply(unit);
        }

        if (city.canHeal(unit)) {
          city.heal(unit);
        }
      }
    }
  }

  public void endTurn(Player currentPlayer) {
  }

  /**
   * set all units to Idle and the units of this player to active.
   * if fog is enabled we apply the los for each active unit.
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
   * Reset the map so that the player can control his units by:
   * Making all units Idle
   * except the units of the player
   * They are made Active.
   *
   * @param player The player whos units should be made active
   */
  private void resetUnits(Player player) {
    for (Location location : getAllTiles()) {
      Unit unit = getUnitOn(location);

      if (unit != null) {
        if (unit.getOwner() == player) {
          unit.setState(GameObjectState.ACTIVE);
        } else {
          unit.setState(GameObjectState.IDLE);
        }
      }
    }
  }

  /**
   * @param supplier The Supplier of which we want to retrieve the suppliables in range for
   * @return units in supplyRange of Supplier.
   */
  public List<Unit> getSuppliablesInRange(Unit supplier, Location location) {
    List<Unit> units = new ArrayList<Unit>();
    int minHealSupplyRange = supplier.getMinHealRange();
    int maxHealSupplyRange = supplier.getMaxHealRange();

    for (Tile t : getSurroundingTiles(location, minHealSupplyRange, maxHealSupplyRange)) {
      Unit unit = getUnitOn(t);

      if (supplier.canSupply(unit)) {
        units.add(unit);
      }
    }
    return units;
  }

  /**
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @return units in attackRange of attacker.
   */
  public List<Unit> getEnemiesInRangeOf(Unit attacker, Location location) {
    List<Unit> units = new ArrayList<Unit>();
    int minAttackRange = attacker.getMinAttackRange();
    int maxAttackRange = attacker.getMaxAttackRange();

    for (Tile t : getSurroundingTiles(location, minAttackRange, maxAttackRange)) {
      Unit unit = getUnitOn(t);

      if (attacker.canAttack(unit)) {
        units.add((Unit) t.getLastLocatable());
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
  public void buildAttackZone(Unit attacker) {
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
   * Determines if a mapLocation is in the attacker's firing range
   */
  public boolean inFireRange(Mover mover, Location mapLocation, int minAttackRange, int maxAttackRange) {
    if (maxAttackRange <= 0) return false;
    int t = Math.abs(mapLocation.getRow() - mover.getLocation().getRow()) + Math.abs(mapLocation.getCol() - mover.getLocation().getCol());

    //Indirects
    if (minAttackRange > 1) {
      return t >= minAttackRange && t <= maxAttackRange;
    } else {
      //Directs
      if (mover.isWithinMoveZone(mapLocation)) return true;
      for (Location moveZoneLocation : mover.getMoveZone()) {
        if (isAdjacent(mapLocation, moveZoneLocation)) return true;
      }
    }
    return false;
  }

  /**
   * Build a zone in which the mover can make a move
   */
  public void buildMovementZone(Mover mover) {
    pathFinder.setMoveCost(mover.getMoveStrategy());
    mover.setMoveZone(pathFinder.getMovementZone(mover));
  }

  /**
   * Get a path of compass directions for the Mover to get to the destination
   * Each step in the path has a Direction relative to the previous location.
   * The list never contains null
   */
  public List<Direction> getDirectionsPath(Mover mover, Location destination) {
    if (mover == null) {
      logger.warn("mover is null");
      return new ArrayList<Direction>();
    }

    pathFinder.setMoveCost(mover.getMoveStrategy());
    return pathFinder.getDirections(mover, destination);
  }

  // ---------------------------------------------------------------------------
  // Fog Actions
  // ---------------------------------------------------------------------------
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
      int additionalVision = calcExtraVision(t);

      if (unit != null && unit.getOwner().isAlliedWith(player)) {
        int vision = unit.getVision();
        clearSight(t, vision + additionalVision);
      }

      if (city != null && city.getOwner().isAlliedWith(player)) {
        int vision = city.getVision();
        clearSight(t, vision + additionalVision);
      }
    }
  }

  public int calcExtraVision(Tile t) {
    return 0;
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
    if (isValid(tileToBeFogged) && rules.canClearFog(baseTile, tileToBeFogged))
      tileToBeFogged.setFogged(false);
  }

  public void addProperty(String key, String value) {
    properties.put(key, value);
  }

  public void setRules(MapRules rules) {
    MapRules oldVal = this.rules;
    this.rules = rules;
    firePropertyChange("rules", oldVal, rules);
  }

  public Unit getUnitOn(Location location) {
    Locatable locatable = location.getLastLocatable();
    if (locatable instanceof Unit) {
      return (Unit) locatable;
    }
    return null;
  }

  public City getCityOn(Tile t) {
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
}