package com.customwars.client.model.map;

import com.customwars.client.App;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.fight.FightFactory;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.path.Mover;
import com.customwars.client.model.map.path.PathFinder;
import com.customwars.client.tools.Args;
import org.apache.log4j.Logger;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A game Map extends TileMap adding game specific fields
 * Handles Fog, paths and units
 * Zones: move zone, attack zone
 * Surrounding information: suppliables, enemies in a Range
 * <p/>
 * At each start of a turn the map is reset see {@link #resetMap(Player)}
 * <p/>
 * Players in a map are called 'map players'
 * name and funds unknown they are used to link units and cities to a player.
 * They hold the units, cities, hq location, the default color and the player ID.
 */
public class Map extends TileMap<Tile> implements TurnHandler {
  private static final Logger logger = Logger.getLogger(Map.class);
  private static final long serialVersionUID = 1L;
  private String mapName, author, description;    // The properties of the map
  private boolean fogOfWarOn;                     // Is fog of war in effect
  private transient PathFinder pathFinder;        // To builds paths within the map
  private Player neutralPlayer;     // Idle neutral player owner of the neutral cities
  private GameRules defaultRules;   // The default game rules as chosen by the map creator
  private final HashMap<Location, City> citiesUnderConstruction;

  /**
   * Convenient constructor to create an anonymous map. The map name and author are set to anonymous.
   */
  public Map(int cols, int rows, int tileSize, Terrain startTerrain) {
    this("anonymous", "anonymous", "", cols, rows, tileSize, startTerrain);
  }

  /**
   * Create a new named Map. Fog is off.
   *
   * @param mapName      The name of the map
   * @param author       The creator of this map
   * @param description  A short message explaining what this map is all about
   * @param cols         width in tiles
   * @param rows         height in tiles
   * @param tileSize     square size of 1 tile in pixels
   * @param startTerrain the terrain that is used to fill the map
   */
  public Map(String mapName, String author, String description, int cols, int rows, int tileSize, Terrain startTerrain) {
    super(cols, rows, tileSize);
    this.mapName = mapName;
    this.author = author;
    this.description = description;
    this.pathFinder = new PathFinder(this);
    this.neutralPlayer = Player.createNeutralPlayer(App.getColor("plugin.neutral_color"));
    this.defaultRules = new GameRules();
    this.citiesUnderConstruction = new HashMap<Location, City>();
    fillMap(cols, rows, startTerrain);
  }

  @SuppressWarnings("unchecked")
  private void fillMap(int cols, int rows, Terrain terrain) {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Tile t = new Tile(col, row, terrain);
        setTile(col, row, t);
      }
    }
  }

  /**
   * Validate units and cities within this map.
   * A valid unit, city is:
   * <ul>
   * <li>Located in the map</li>
   * <li>Owned by a player</li>
   * </ul>
   * <p/>
   * When an invalid unit or city is found an IllegalArgumentException is thrown.
   */
  public void validate() throws IllegalArgumentException {
    for (Tile t : getAllTiles()) {
      City city = getCityOn(t);
      Unit unit = getUnitOn(t);

      if (city != null) {
        Args.checkForNull(city.getLocation(), "City has no location");
        Args.validate(city.getLocation() != t, "Wrong location for " + city + " Expected " + t.getLocationString());
        Args.checkForNull(city.getOwner(), "City @ " + t.getLocationString() + " has no owner");
      }

      if (unit != null) {
        Args.validate(t.getLocatableCount() != 1, "Tile @ " + t.getLocationString() + " contains " + t.getLocatableCount() + " units, limit=1");
        Args.checkForNull(unit.getLocation(), "Unit has no location");
        Args.validate(unit.getLocation() != t, "Wrong location for " + unit + " Expected " + t.getLocationString());
        Args.checkForNull(unit.getOwner(), "Unit @ " + t.getLocationString() + " has no owner");
      }
    }
  }

  /**
   * Copy Constructor
   *
   * @param otherMap map to copy
   */
  public Map(Map otherMap) {
    this(otherMap.getCols(), otherMap.getRows(), otherMap.getTileSize(), otherMap.getTile(0, 0).getTerrain());
    this.mapName = otherMap.mapName;
    this.author = otherMap.author;
    this.description = otherMap.description;
    this.fogOfWarOn = otherMap.fogOfWarOn;
    this.defaultRules = new GameRules(otherMap.defaultRules);
    copyMapData(otherMap);
    copyPlayers();
  }

  @SuppressWarnings("unchecked")
  private void copyMapData(Map otherMap) {
    for (Tile t : otherMap.getAllTiles()) {
      int col = t.getCol();
      int row = t.getRow();
      boolean fogged = t.isFogged();
      Terrain terrain = t.getTerrain();

      Unit unitCopy = copyUnit(t);
      Terrain terrainCopy = copyTerrain(terrain, unitCopy);
      Tile tileCopy = new Tile(col, row, terrainCopy, fogged);
      copyCityLocation(tileCopy);
      tileCopy.add(unitCopy);
      setTile(tileCopy);
    }
  }

  private Unit copyUnit(Tile t) {
    int unitCount = t.getLocatableCount();

    if (unitCount > 0) {
      return new Unit((Unit) t.getLastLocatable());
    } else {
      return null;
    }
  }

  private Terrain copyTerrain(Terrain terrain, Unit unit) {
    boolean isCity = terrain instanceof City;
    boolean hasUnit = unit != null;

    if (isCity) {
      City otherCity = (City) terrain;

      if (otherCity.isBeingCaptured() && hasUnit) {
        return new City((City) terrain, unit);
      } else {
        return new City((City) terrain);
      }
    } else {
      return terrain;
    }
  }

  private void copyCityLocation(Tile tileCopy) {
    Terrain terrain = tileCopy.getTerrain();

    if (terrain instanceof City) {
      City city = (City) tileCopy.getTerrain();
      city.setLocation(tileCopy);
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
   * Activate the units of the given player so they can be controlled.
   * If fog is enabled the los for each owned and allied unit/city is revealed.
   *
   * @param player The player to reset the map for
   */
  private void resetMap(Player player) {
    resetUnits(player);
    if (fogOfWarOn) {
      resetFogMap(player);
    }
  }

  private void resetUnits(Player player) {
    resetAllUnitStates(player);
    resetAllHiddenUnits(player);
  }

  private void resetAllUnitStates(Player player) {
    for (Location t : getAllTiles()) {
      Unit unit = getUnitOn(t);

      if (unit != null) {
        resetUnitState(unit, player);
      }
    }
  }

  /**
   * Activate all the units owned by the given player.
   * Deactivate all other units.
   *
   * @param unit   The unit to reset
   * @param player The active player
   */
  private void resetUnitState(Unit unit, Player player) {
    if (unit.getOwner() == player) {
      unit.setActive(true);
    } else {
      unit.setActive(false);
    }
  }

  public void resetAllHiddenUnits(Player player) {
    for (Location t : getAllTiles()) {
      Unit unit = getUnitOn(t);

      if (unit != null) {
        resetHiddenUnit(unit, player);
      }
    }
  }

  /**
   * Determine if the given unit should be revealed.
   * A unit is revealed when:
   * <ul>
   * <li>The unit is allied to the active player</li>
   * <li>The unit is adjacent to an allied unit or city</li>
   * <li>The unit is a sub and is at the surface</li>
   * </ul>
   * Note that only units that have the reveal/hide ability can reset their hidden status.
   * All other units are ignored.
   *
   * @param unit   The unit to reset the hidden flag for
   * @param player The active player
   */
  private void resetHiddenUnit(Unit unit, Player player) {
    if (unit.canHide()) {
      boolean allied = unit.isAlliedWith(player);
      boolean hasAdjacentAlliedUnitOrCity = hasAdjacentAlliedUnitOrCity(unit.getLocation(), player);
      boolean surfacedSub = unit.canDive() && !unit.isSubmerged();

      if (allied || hasAdjacentAlliedUnitOrCity || surfacedSub) {
        unit.setHidden(false);
      } else {
        unit.setHidden(true);
      }
    }
  }

  private boolean hasAdjacentAlliedUnitOrCity(Location unitLocation, Player player) {
    for (Tile adjacentTile : getSurroundingTiles(unitLocation, 1, 1)) {
      Unit unit = getUnitOn(adjacentTile);
      City city = getCityOn(adjacentTile);
      boolean hasAdjacentAlliedUnit = unit != null && unit.isAlliedWith(player);
      boolean hasAdjacentAlliedCity = city != null && city.isAlliedWith(player);

      if (hasAdjacentAlliedUnit || hasAdjacentAlliedCity) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieve a list of visible units in supply range of the supplier
   *
   * @param supplier The supplier of which we want to retrieve the suppliables units in range for
   * @return units in supply range of supplier that can be supplied and are not fogged
   */
  public List<Unit> getSuppliablesInRange(Unit supplier) {
    List<Unit> units = new ArrayList<Unit>(4);
    Location supplierLocation = supplier.getLocation();
    Range supplyRange = supplier.getStats().getSupplyRange();

    for (Tile t : getSurroundingTiles(supplierLocation, supplyRange)) {
      Unit unitInRange = getUnitOn(t);

      if (unitInRange != null) {
        if (isUnitVisible(unitInRange) && supplier.canSupply(unitInRange, false)) {
          units.add(unitInRange);
        }
      }
    }
    return units;
  }

  /**
   * Retrieve a list of visible enemies in attack range of the attacker
   *
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @return enemies in attackRange of attacker that can be attacked and are not fogged
   */
  public List<Defender> getEnemiesInRangeOf(Attacker attacker) {
    return getEnemiesInRangeOf(attacker, attacker.getLocation());
  }

  /**
   * @param attacker The Attacker of which we want to retrieve the enemies in range for
   * @param center   The center to iterate around
   * @return enemies in attackRange of attacker that can be attacked and are not fogged
   */
  public List<Defender> getEnemiesInRangeOf(Attacker attacker, Location center) {
    List<Defender> enemies = new ArrayList<Defender>();

    for (Tile t : getSurroundingTiles(center, attacker.getAttackRange())) {
      Unit unit = getUnitOn(t);
      City city = getCityOn(t);
      boolean canAttackCity = city != null && attacker.canAttack(city);
      boolean canAttackUnit = isUnitVisible(unit) && attacker.canAttack(unit);

      if (canAttackUnit || canAttackCity) {
        Fight fight = FightFactory.createFight(this, attacker, t);

        if (fight != null) {
          if (fight.canUsePrimaryWeapon() || fight.canUseSecondaryWeapon()) {
            if (canAttackUnit) {
              enemies.add(unit);
            } else {
              enemies.add(city);
            }
          }
        }
      }
    }
    return enemies;
  }

  /**
   * Get every unit that can be attacked by the given unit.
   * This method is for the AI to create a list of the units it can attack.
   * At least 1 location next to the defender can be reached.
   * This method is for direct units. Indirects should use getEnemiesInRangeOf.
   *
   * A defender can be a City or a Unit.
   */
  public List<Defender> getEnemiesInRangeOfIndirect(Unit unit) {
    List<Defender> allEnemyTargets = new ArrayList<Defender>();

    buildMovementZone(unit);
    buildAttackZone(unit);

    for (Location location : unit.getAttackZone()) {
      List<Defender> enemies = getEnemiesInRangeOf(unit, location);

      for (Defender defender : enemies) {
        if (!allEnemyTargets.contains(defender)) {
          allEnemyTargets.add(defender);
        }
      }
    }

    return allEnemyTargets;
  }

  /**
   * A unit is visible when the tile it is on is not fogged and the unit is not hidden.
   *
   * @param unit the unit to be checked for visibility
   * @return if the unit is visible to the active player
   */
  private boolean isUnitVisible(Unit unit) {
    if (unit != null) {
      Tile t = (Tile) unit.getLocation();
      return !t.isFogged() && !unit.isHidden();
    } else {
      return false;
    }
  }

  /**
   * Build a zone in which the mover can make a move and set it to the mover
   * If the mover is within a transport then the move zone is empty
   * If the mover cannot move then the current mover location is set as the moveZone
   *
   * @param mover The mover to build a move zone for
   */
  public void buildMovementZone(Mover mover) {
    List<Location> moveZone;

    if (mover.isInTransport()) {
      moveZone = Collections.emptyList();
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
   * If the attacker is within a transport then the attack zone is empty
   *
   * @param attacker The attacker to build the attack zone for
   */
  public void buildAttackZone(Attacker attacker) {
    List<Location> attackZone = new ArrayList<Location>(30);
    Range attackRange = attacker.getAttackRange();

    if (attacker.isInTransport()) {
      attackZone = Collections.emptyList();
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
   * Determines if a Location is in the attacker's attack range.
   *
   * For indirect units check if the location is within the attack range.
   * For direct units check if the location is in or adjacent of the move zone.
   *
   * @param attacker    The attacker that wants to fire
   * @param location    The location that the attacker wants to fire on
   * @param attackRange The range of the attacker in which it can fire
   * @return True if the location is within the attack range of the attacker
   */
  public boolean inFireRange(Attacker attacker, Location location, Range attackRange) {
    if (attackRange.getMaxRange() >= 0) {
      int distance = getDistanceBetween(location, attacker.getLocation());
      boolean indirect = attackRange.getMinRange() >= 1 && attackRange.getMaxRange() > 1;

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

  private boolean isAdjacentOfLocations(Location location, Collection<Location> locations) {
    for (Location loc : locations) {
      if (isAdjacent(location, loc)) return true;
    }
    return false;
  }

  /**
   * Build a zone in which the unit can flare and set it to the unit as the attackZone.
   * The flare zone includes the unit location and has a max range equal to the flare weapon range.
   *
   * @param unit The unit to build the flare zone for
   */
  public void buildFlareZone(Unit unit) {
    List<Location> flareZone = new ArrayList<Location>();
    flareZone.add(unit.getLocation());
    for (Tile t : getSurroundingTiles(unit.getLocation(), unit.getPrimaryWeapon().getRange())) {
      flareZone.add(t);
    }
    unit.setAttackZone(flareZone);
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
   * then reveal all the tiles that are visible to this player and his allies.
   *
   * @param player The player and all his allies who's units and cities line of sight should be revealed.
   */
  public void resetFogMap(Player player) {
    if (fogOfWarOn) {
      fillFog(true);
      showLosFor(player);
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
   * Show the line of sight for the given player.
   * This is done by revealing all the tiles within an allied Unit and City vision range.
   *
   * @param player The player and his allies to apply the los for
   */
  public void showLosFor(Player player) {
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);
      City city = getCityOn(t);

      if (unit != null && unit.isAlliedWith(player)) {
        CO co = unit.getOwner().getCO();
        int visionBonus = getUnitVisionBonus(unit);
        int vision = unit.getStats().getVision();
        int coVision = co.unitVisionHook(unit, vision + visionBonus);
        showLos(t, coVision, co.isPiercingVision());
      }

      if (city != null && city.isAlliedWith(player)) {
        CO co = city.getOwner().getCO();
        int vision = city.getVision();
        int coVision = co.cityVisionHook(vision);
        showLos(t, coVision, co.isPiercingVision());
      }
    }
  }

  private int getUnitVisionBonus(Unit unit) {
    int visionBonus = 0;
    Tile t = (Tile) unit.getLocation();

    if (unit.isLand()) {
      Terrain terrain = t.getTerrain();
      if (terrain.isMountain()) {
        visionBonus = terrain.getVision();
      }
    }
    return visionBonus;
  }

  /**
   * Reveals all visible tiles within a vision range including the baseTile
   *
   * @param baseTile       The tile to show the line of sight around
   * @param vision         The amount of tiles that have to be shown around the baseTile in all directions
   * @param piercingVision Can the player see can see through hidden terrains.
   */
  private void showLos(Tile baseTile, int vision, boolean piercingVision) {
    int col = baseTile.getCol();
    int row = baseTile.getRow();

    // Clear baseTile
    baseTile.setFogged(false);

    // Clear all visible tiles within vision range
    for (int i = 1; i <= vision; i++) // for each layer of vision
    {
      for (int j = 0; j < i; j++)     // for each tile within that layer
      {
        clearFog(baseTile, getTile(col + i - j, row + j), piercingVision); // bottom right sector
        clearFog(baseTile, getTile(col - i + j, row - j), piercingVision); // top left sector
        clearFog(baseTile, getTile(col - j, row + i - j), piercingVision); // bottom left sector
        clearFog(baseTile, getTile(col + j, row - i + j), piercingVision); // top right sector
      }
    }
  }

  private void clearFog(Tile baseTile, Tile tile, boolean piercingVision) {
    if (isValid(tile) && canClearFog(baseTile, tile, piercingVision)) {
      tile.setFogged(false);
    }
  }

  /**
   * Can the fog be cleared for the given tile
   * There are some terrains that remain fogged until directly next to the baseTile.
   * Hidden terrains can only be cleared of fog if the terrain is directly next to the baseTile
   *
   * @param baseTile       The center of the vision range that is being cleared
   * @param tile           A tile within vision range
   * @param piercingVision Can the player see through hidden terrains.
   * @return If the tile should be cleared of fog.
   */
  private boolean canClearFog(Location baseTile, Tile tile, boolean piercingVision) {
    Terrain terrain = tile.getTerrain();
    boolean adjacent = isAdjacent(tile, baseTile);

    if (piercingVision && terrain.isHidden()) {
      return true;
    } else
      // If not hidden or directly next to the tile we can see everything
      return !terrain.isHidden() || adjacent;
  }

  /**
   * Normalise this map
   * A normalised maps contains map players with Id's that start with ID 0 and increase by 1
   */
  public void normalise() {
    copyPlayers();
  }

  /**
   * Replace each player in the map with a new Player Copy
   * new players start with ID 0, next players id increases by 1
   */
  private void copyPlayers() {
    Collection<Player> currentPlayers = getUniquePlayers();

    int nextPlayerIndex = 0;
    for (Player currentPlayer : currentPlayers) {
      Player newPlayer = new Player(nextPlayerIndex++, currentPlayer.getColor());
      replacePlayer(currentPlayer, newPlayer);
    }

    Player newNeutralPlayer = Player.createNeutralPlayer(App.getColor("plugin.neutral_color"));
    replacePlayer(neutralPlayer, newNeutralPlayer);
  }

  /**
   * Replace each instance of oldPlayer with newPlayer in the map
   */
  public void replacePlayer(Player oldPlayer, Player newPlayer) {
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);
      if (unit != null && unit.getOwner().equals(oldPlayer)) {
        replaceUnitOwner(newPlayer, unit);
      }

      City city = getCityOn(t);
      if (city != null && city.getOwner().equals(oldPlayer)) {
        newPlayer.addCity(city);
      }
    }
  }

  private void replaceUnitOwner(Player newPlayer, Unit unit) {
    newPlayer.addUnit(unit);

    for (Unit unitInTransport : unit.getUnitsInTransport()) {
      newPlayer.addUnit(unitInTransport);
    }
  }

  public void setFogOfWarOn(boolean fogOfWarOn) {
    boolean oldVal = this.fogOfWarOn;
    this.fogOfWarOn = fogOfWarOn;
    firePropertyChange("fogofwar", oldVal, fogOfWarOn);
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  public void setDefaultRules(GameRules rules) {
    this.defaultRules = rules;
  }

  public void addCityUnderConstruction(Location location, City city) {
    if (!citiesUnderConstruction.containsKey(location)) {
      citiesUnderConstruction.put(location, city);
    }
  }

  /**
   * Remove the cities under construction with no unit on them.
   * This happens when the apc construct a city in 1 turn.
   * But then moves to another location leaving the city in the citiesUnderConstruction collection.
   */
  public void validateConstructingCities() {
    Iterator<Location> iterator = citiesUnderConstruction.keySet().iterator();

    while (iterator.hasNext()) {
      Location location = iterator.next();
      if (!hasUnitOn(location)) {
        iterator.remove();
      }
    }
  }

  public City getCityUnderConstructionAt(Location location) {
    City city = null;
    if (isConstructingCityAt(location)) {
      city = citiesUnderConstruction.get(location);
    }
    return city;
  }

  public boolean isConstructingCityAt(Location location) {
    return citiesUnderConstruction.containsKey(location);
  }

  public void stopConstructingCity(Location location) {
    citiesUnderConstruction.remove(location);
  }

  /**
   * @see #getUnitOn(Location)
   */
  public Unit getUnitOn(int col, int row) {
    Location location = getTile(col, row);
    return getUnitOn(location);
  }

  /**
   * Checks if there is a unit on the given location
   *
   * @param location the location to check for a unit
   * @return true if a unit is present
   * @see #getUnitOn(Location)
   */
  public boolean hasUnitOn(Location location) {
    return getUnitOn(location) != null;
  }

  /**
   * @param location the location in the map to retrieve a unit from
   * @return The last added unit from location
   *         if location doesn't contain a unit <b>NULL</b> is returned
   */
  public Unit getUnitOn(Location location) {
    if (location != null) {
      Tile t = getTile(location);

      if (t != null) {
        Locatable locatable = t.getLastLocatable();

        if (locatable instanceof Unit) {
          return (Unit) locatable;
        }
      }
    }
    return null;
  }

  /**
   * @param location the location in the map to retrieve a unit from
   * @return The unit at the given index on the location
   *         if location doesn't contain a unit <b>NULL</b> is returned
   */
  public Unit getUnitOn(Location location, int index) {
    if (location != null) {
      Tile t = getTile(location);

      if (t != null) {
        Locatable locatable = t.getLocatable(index);

        if (locatable instanceof Unit) {
          return (Unit) locatable;
        }
      }
    }
    return null;
  }

  /**
   * @see #getCityOn(Location)
   */
  public City getCityOn(int col, int row) {
    Location location = getTile(col, row);
    return getCityOn(location);
  }

  /**
   * Checks if there is a city on the given location
   *
   * @param location the location in the map to check for a city
   * @return true if a city is present
   * @see #getCityOn(Location)
   */
  public boolean hasCityOn(Location location) {
    return getCityOn(location) != null;
  }

  /**
   * @param location the location in the map to retrieve a city from
   * @return The city that is on the location
   *         if location doesn't contain a city <b>NULL</b> is returned
   */
  public City getCityOn(Location location) {
    if (location != null) {
      Tile t = getTile(location);

      if (t != null) {
        Terrain terrain = t.getTerrain();
        if (terrain instanceof City) {
          return (City) terrain;
        }
      }
    }
    return null;
  }

  /**
   * Retrieve a list of drop locations where all units can be dropped on.
   * Using the current location of the transport.
   *
   * @param transport The transport that wants to start dropping units
   * @return If all units in the transport can be dropped
   * @see #getFreeDropLocations(Unit, Tile)
   */
  public List<Location> getFreeDropLocations(Unit transport) {
    Tile transportLocation = getTile(transport.getLocation());
    return getFreeDropLocations(transport, transportLocation);
  }

  /**
   * Retrieve a list of drop locations where all units can be dropped on.
   * Drop locations are always adjacent.
   * <p/>
   * The center parameter allows a transport to find out if any units can be dropped
   * around a given center without actually moving to that location.
   *
   * @param transport       The transport where we want to find the free drop locations for.
   * @param moveDestination The location of the transport after the move where the drop locations are located around.
   * @return A list of adjacent locations where units inside the transport can be dropped on.
   * @see #canDropAtLeast1Unit(Unit, Tile, Tile)
   */
  public List<Location> getFreeDropLocations(Unit transport, Tile moveDestination) {
    if (!transport.hasUnitsInTransport()) return Collections.emptyList();

    List<Location> freeDropLocations = new ArrayList<Location>(4);
    for (Tile tile : getSurroundingTiles(moveDestination, 1, 1)) {
      if (canDropAtLeast1Unit(transport, moveDestination, tile)) {
        freeDropLocations.add(tile);
      }
    }
    return freeDropLocations;
  }

  /**
   * Retrieve a list of drop locations where the given unit can be dropped on.
   * Drop locations are always adjacent.
   * <p/>
   * The center parameter allows a transport to find out if any units can be dropped
   * around a given center without actually moving to that location.
   *
   * @param transport       The transport that wants to drop the given unit
   * @param moveDestination The location of the transport after the move where the drop locations are located around.
   * @param unitToBeDropped The unit that wants to be dropped out of the transport into the map
   * @param center          The location to search for free adjacent drop locations
   * @return a list of adjacent locations where units inside the transport can be dropped on
   * @see #isFreeDropLocation(Unit, Tile, Unit, Tile)
   */
  public List<Location> getFreeDropLocations(Unit transport, Location moveDestination, Unit unitToBeDropped, Location center) {
    if (!transport.hasUnitsInTransport()) return Collections.emptyList();

    Tile moveDestinationTile = getTile(moveDestination);

    List<Location> freeDropLocations = new ArrayList<Location>(4);
    for (Tile tile : getSurroundingTiles(center, 1, 1)) {
      if (isFreeDropLocation(transport, moveDestinationTile, unitToBeDropped, tile)) {
        freeDropLocations.add(tile);
      }
    }
    return freeDropLocations;
  }

  /**
   * Determines if at least 1 unit in the transport can be dropped on the drop location.
   * If the transport is empty false is returned.
   *
   * @param dropLocation    The tile that a unit wants to be dropped on
   * @param transporter     The transport unit that attempts to drop a unit to the dropLocation
   * @param moveDestination The location of the transport after the move where the drop locations are located around.
   * @return Can a unit in the transport be dropped to the given drop location
   * @see #isFreeDropLocation(Unit, Tile, Unit, Tile)
   */
  boolean canDropAtLeast1Unit(Unit transporter, Tile moveDestination, Tile dropLocation) {
    if (!transporter.hasUnitsInTransport()) return false;

    for (Unit unit : transporter.getUnitsInTransport()) {
      if (!isFreeDropLocation(transporter, moveDestination, unit, dropLocation)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if the given unit can be dropped on the drop location.
   * A drop location is considered free when one of the following conditions is true:
   * <ul>
   * <li>The drop location is fogged</li>
   * <li>The drop location is not occupied by a unit</li>
   * <li>The drop location is occupied by a hidden unit</li>
   * <li>The unit on the drop location is equal to the transporter</li>
   * <li>At least 1 of the units in the transport can move over the drop location</li>
   * <li>At least 1 of the units in the transport can move over the move destination</li>
   * </ul>
   *
   * @param unit            The unit to be dropped
   * @param dropLocation    The tile that a unit wants to be dropped on
   * @param transporter     The transport unit that attempts to drop a unit to the dropLocation
   * @param moveDestination The location of the transport after the move where the drop locations are located around.
   * @return Can a unit be dropped to the given drop location
   */
  public boolean isFreeDropLocation(Unit transporter, Tile moveDestination, Unit unit, Tile dropLocation) {
    if (!canTraverseTile(unit, dropLocation)) return false;
    if (!canTraverseTile(unit, moveDestination)) return false;

    Unit unitOnDropLocation = getUnitOn(dropLocation);
    return dropLocation.isFogged() || dropLocation.getLocatableCount() == 0 ||
      unitOnDropLocation.isHidden() || unitOnDropLocation == transporter;
  }

  /**
   * Check if the unit can move over a tile
   *
   * @return if the terrain on the tile can be traversed by the given unit
   */
  public boolean canTraverseTile(Unit unit, Location location) {
    int moveType = unit.getMovementType();
    Tile t = getTile(location);
    Terrain terrain = t.getTerrain();
    return terrain.canBeTraverseBy(moveType);
  }

  /**
   * Create a collection of tiles that covers the co zone area around the given unit.
   * When the zone range is 0 an empty collection is returned.
   *
   * @param unit      The unit to retrieve the co zone from
   * @param zoneRange the radius of the spiral that makes the co zone
   * @return The locations that cover the co zone
   */
  public Collection<Location> buildCOZone(Unit unit, int zoneRange) {
    Collection<Location> coZone = new ArrayList<Location>();
    if (zoneRange != 0) {
      coZone.add(unit.getLocation());
      for (Tile t : getSurroundingTiles(unit.getLocation(), 1, zoneRange)) {
        coZone.add(t);
      }
    }
    return coZone;
  }

  /**
   * @return Amount of human players in this map
   */
  public int getNumPlayers() {
    return getUniquePlayers().size();
  }

  /**
   * @return Each unique player in the map excluding the neutral player
   */
  public List<Player> getUniquePlayers() {
    Set<Player> players = new HashSet<Player>();
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);
      City city = getCityOn(t);

      if (unit != null && !unit.getOwner().isNeutral()) {
        players.add(unit.getOwner());
      }

      if (city != null && !city.getOwner().isNeutral()) {
        players.add(city.getOwner());
      }
    }
    return Collections.unmodifiableList(new ArrayList<Player>(players));
  }

  /**
   * Get all the units of the active player that have not performed an action.
   *
   * @return All active units in the map
   */
  public Collection<Unit> getActiveUnits() {
    Collection<Unit> units = new ArrayList<Unit>();
    for (Tile t : getAllTiles()) {
      Unit unit = getUnitOn(t);

      if (unit != null && unit.isActive()) {
        units.add(unit);
      }
    }
    return units;
  }

  public String getMapName() {
    return mapName;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public boolean isFogOfWarOn() {
    return fogOfWarOn;
  }

  public Player getNeutralPlayer() {
    return neutralPlayer;
  }

  public void addListenerToAllTilesUnitsAndCities(PropertyChangeListener listener) {
    for (Tile t : getAllTiles()) {
      t.addPropertyChangeListener(listener);

      Unit unit = getUnitOn(t);
      if (unit != null) unit.addPropertyChangeListener(listener);

      City city = getCityOn(t);
      if (city != null) city.addPropertyChangeListener(listener);
    }
  }

  public void removeListenerFromAllTilesUnitsAndCities(PropertyChangeListener listener) {
    for (Tile t : getAllTiles()) {
      t.removePropertyChangeListener(listener);

      Unit unit = getUnitOn(t);
      if (unit != null) unit.removePropertyChangeListener(listener);

      City city = getCityOn(t);
      if (city != null) city.removePropertyChangeListener(listener);
    }
  }

  /**
   * Counts cities of a given type owned by a given player.
   * If the player argument is null. All cities in the map of a given city type are counted.
   *
   * @param baseCityName The city to count. Eg: 'airport'
   * @param player       The owner of the cities to count
   * @return The amount of cities in this map of a city type owned by player.
   */
  public int getCityCount(String baseCityName, Player player) {
    int cityCount = 0;
    for (Tile t : getAllTiles()) {
      City city = getCityOn(t);

      if (city != null && city.getType().equalsIgnoreCase(baseCityName))
        if (player == null || city.isOwnedBy(player)) {
          cityCount++;
        }
    }
    return cityCount;
  }

  /**
   * Counts all cities of the same city type. For example You can use this to retrieve the amount of ports.
   *
   * @param baseCityName The city to count. Eg: 'airport'
   * @return The amount of cities in this map of a city type.
   */
  public int getCityCount(String baseCityName) {
    return getCityCount(baseCityName, null);
  }

  /**
   * Check if each adjacent tile around the unit is occupied by an enemy unit.
   * Fog and hidden units have no influence since adjacent tiles are always visible.
   *
   * @param unit the unit to check for enemies on each adjacent tile
   * @return If the unit is surrounded by enemies
   */
  public boolean isSurroundedByEnemyUnits(Unit unit) {
    int enemies = 0;
    for (Tile t : getSurroundingTiles(unit.getLocation(), 1, 1)) {
      Unit adjacentUnit = getUnitOn(t);
      if (adjacentUnit != null && !adjacentUnit.isAlliedWith(unit.getOwner())) {
        enemies++;
      }
    }
    return enemies == 4;
  }

  /**
   * Return a map player from this map for the given color
   * If the map already contains a player for the given color then that player is returned.
   * else a new player with an unique ID is returned.
   */
  public Player getPlayer(Color color) {
    boolean createNewPlayer = !hasMapPlayerFor(color);

    if (createNewPlayer) {
      int nextFreeID = getNextFreeMapPlayerID();
      Player mapPlayer = new Player(nextFreeID, color);
      mapPlayer.setName("map player");
      return mapPlayer;
    } else {
      return getMapPlayerFor(color);
    }
  }

  private int getNextFreeMapPlayerID() {
    int id = 0;
    while (isPlayerIDTaken(id)) {
      id++;
    }
    return id;
  }

  private boolean isPlayerIDTaken(int id) {
    for (Player mapPlayer : getUniquePlayers()) {
      if (mapPlayer.getId() == id) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return Does this map contains a player with the given color
   */
  public boolean hasMapPlayerFor(Color color) {
    return getMapPlayerFor(color) != null;
  }

  /**
   * Retrieve a map player with the given color from this map.
   * If the color equals the neutral color the neutral player is returned.
   * <p/>
   * If the color is not used by a player in the map and it's not the neutral player
   * <tt>null</tt> is returned.
   *
   * @param color color to retrieve a player for
   * @return A map player that is defined by the given color
   */
  private Player getMapPlayerFor(Color color) {
    Color neutralColor = App.getColor("plugin.neutral_color");
    for (Player mapPlayer : getUniquePlayers()) {
      if (mapPlayer.getColor().equals(color)) {
        return mapPlayer;
      }
    }

    if (color.equals(neutralColor)) {
      return neutralPlayer;
    } else {
      return null;
    }
  }

  /**
   * @return A copy of the default rules for this map
   */
  public GameRules getDefaultGameRules() {
    return new GameRules(defaultRules);
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    this.pathFinder = new PathFinder(this);
    this.neutralPlayer = Player.createNeutralPlayer(App.getColor("plugin.neutral_color"));
  }
}
