package com.customwars.client.model.game;

import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.ColorUtil;
import org.apache.log4j.Logger;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represent a player that can participate in a game, can be either a human, AI or neutral player.
 * Can be allied with another Player by being in the same team
 * Each non neutral player has an unique ID, neutral players always have the same NEUTRAL_PLAYER_ID
 * <p/>
 * A Player is equal to another Player by having the same ID and color
 *
 * @author stefan
 */
public class Player extends GameObject {
  private static final Logger logger = Logger.getLogger(Player.class);
  private static final int NEUTRAL_PLAYER_ID = -1;
  private static final int NEUTRAL_TEAM = -1;
  private static final String DUMMY_CO_NAME = "dummy";
  private final int id;           // Unique Number
  private final Color color;      // Unique Color
  private final int team;         // The team this player is in
  private final boolean ai;       // Is this a human or an AI player
  private String name;            // Name for this player
  private City hq;                // Headquarters
  private CO co;                  // Commanding officer
  private Direction unitFacingDirection;  // The direction unit's will face to when idle

  private int budget;               // Amount of money that can be spend
  private final List<Unit> army;    // All the units of this player
  private final List<City> cities;  // All the cities of this player, including the HQ
  private boolean createdFirstUnit; // Has this player created his first unit
  private Collection<Location> coZone;

  /**
   * Create an unnamed human player with a dummy CO
   */
  public Player(int id, Color color) {
    this(id, color, "Unnamed", 0, 0, false, new BasicCO(DUMMY_CO_NAME));
  }

  /**
   * Create a neutral player with a dummy CO
   * Post: isNeutral returns true
   */
  public static Player createNeutralPlayer(Color color) {
    return new Player(NEUTRAL_PLAYER_ID, color, "Neutral", 0, NEUTRAL_TEAM, false, new BasicCO(DUMMY_CO_NAME));
  }

  /**
   * Copy Constructor
   * Note that the units and cities owned by this player are not copied
   *
   * @param otherPlayer player to copy
   */
  public Player(Player otherPlayer) {
    super(otherPlayer);
    this.id = otherPlayer.id;
    this.color = otherPlayer.color;
    this.name = otherPlayer.name;
    this.budget = otherPlayer.budget;
    this.team = otherPlayer.team;
    this.ai = otherPlayer.ai;
    this.army = new LinkedList<Unit>();
    this.cities = new LinkedList<City>();
    this.unitFacingDirection = otherPlayer.unitFacingDirection;
    this.coZone = otherPlayer.coZone;

    String otherPlayerCOName = otherPlayer.co.getName();
    if (otherPlayerCOName.equals(DUMMY_CO_NAME)) {
      this.co = new BasicCO(DUMMY_CO_NAME);
    } else {
      this.co = COFactory.getCO(otherPlayerCOName);
    }
  }

  public Player(int id, Color color, String name, int startBudget, int team, boolean ai) {
    this(id, color, name, startBudget, team, ai, new BasicCO(DUMMY_CO_NAME));
  }

  public Player(int id, Color color, String name, int startBudget, int team, boolean ai, CO co) {
    this.id = id;
    this.color = color;
    this.name = name;
    this.budget = startBudget;
    this.team = team;
    this.ai = ai;
    this.co = co;
    this.army = new LinkedList<Unit>();
    this.cities = new LinkedList<City>();
    this.unitFacingDirection = Unit.DEFAULT_ORIENTATION;
    this.coZone = Collections.emptyList();
  }

  public void startTurn() {
    for (Unit unit : army) {
      unit.startTurn(this);
    }

    for (City city : cities) {
      city.startTurn(this);
    }
  }

  public void endTurn() {
    for (Unit unit : army) {
      unit.endTurn(this);
    }

    for (City city : cities) {
      city.endTurn(this);
    }
  }

  /**
   * Destroy all units of this owner
   * Change the owner of the cities owned by this player to the conqueror player.
   *
   * @param conqueror the player that has conquered this player
   */
  public void destroy(Player conqueror) {
    setState(GameObjectState.DESTROYED);
    destroyAllUnits();
    changeCityOwnersTo(conqueror);
    logger.info(name + " is destroyed. All cities now belong to " + conqueror.name);
  }

  /**
   * Remove all units from army, until there are none left
   * unit.destroy() removes the unit from this player's army.
   */
  private void destroyAllUnits() {
    while (!army.isEmpty()) {
      Unit unit = army.get(army.size() - 1);
      unit.destroy(true);
    }
  }

  /**
   * Change the owner of each city owned by this player to newOwner
   *
   * @param newOwner The new owner of the cities owned by this player
   */
  private void changeCityOwnersTo(Player newOwner) {
    for (City city : cities) {
      newOwner.addCity(city);
    }
    cities.clear();
  }

  public void addToBudget(int amount) {
    setBudget(budget + amount);
  }

  private void setBudget(int budget) {
    int oldBudget = this.budget;
    this.budget = Args.getBetweenZeroMax(budget, Integer.MAX_VALUE);
    firePropertyChange("budget", oldBudget, this.budget);
  }

  /**
   * Add the given city to this player, and set this player as owner of the city.
   * If the city is a HQ it is added and set as HQ.
   */
  public void addCity(City city) {
    Args.checkForNull(city, "null city cannot be added");
    Args.validate(cities.contains(city), "cannot add the same city");

    cities.add(city);
    city.setOwner(this);
    if (city.isHQ()) hq = city;
    firePropertyChange("city", null, city);
  }

  public boolean containsCity(City city) {
    return cities.contains(city);
  }

  /**
   * Remove the given city from this player.
   * If the city is a HQ it is removed and the hq is set to null.
   */
  public void removeCity(City city) {
    boolean removed = cities.remove(city);

    if (removed) {
      if (city.isHQ()) hq = null;
      firePropertyChange("city", city, null);
    } else {
      logger.warn("Removing city " + city.getName() + " from player " + name + " failed!");
    }
  }

  /**
   * @return All the cities owned by this player including the HQ
   */
  public Iterable<City> getAllCities() {
    return new Iterable<City>() {
      public Iterator<City> iterator() {
        return cities.iterator();
      }
    };
  }

  /**
   * Add unit to this player, and set this player as owner of the unit
   */
  public void addUnit(Unit unit) {
    Args.checkForNull(unit, "null unit cannot be added");
    Args.validate(army.contains(unit), "army already contains the same unit");

    if (army.isEmpty()) {
      createdFirstUnit = true;
    }

    army.add(unit);
    unit.setOwner(this);
    firePropertyChange("unit", null, unit);
  }

  public boolean containsUnit(Unit unit) {
    return army.contains(unit);
  }

  public void removeUnit(Unit unit) {
    army.remove(unit);
    firePropertyChange("unit", unit, null);
  }

  /**
   * @return a list of each unit owned by this player,
   *         note that this includes units inside transports!
   */
  public Iterable<Unit> getArmy() {
    return new Iterable<Unit>() {
      public Iterator<Unit> iterator() {
        return army.iterator();
      }
    };
  }

  /**
   * @see CO#resetPowerGauge();
   */
  public void resetPowerGauge() {
    co.resetPowerGauge();
  }

  /**
   * @see CO#chargePowerGauge(double);
   */
  public void chargePowerGauge(double chargeRate) {
    co.chargePowerGauge(chargeRate);
  }

  public void setName(String name) {
    String oldVal = this.name;
    this.name = name;
    firePropertyChange("name", oldVal, name);
  }

  public void setHq(City hq) {
    this.hq = hq;
  }

  public void setUnitFacingDirection(Direction direction) {
    this.unitFacingDirection = direction;
  }

  public void setCoZone(Collection<Location> coZone) {
    this.coZone = coZone;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public int getBudget() {
    return budget;
  }

  public int getArmyCount() {
    return army.size();
  }

  public int getCityCount() {
    return cities.size();
  }

  public boolean isAi() {
    return ai;
  }

  public boolean isNeutral() {
    return team == NEUTRAL_TEAM;
  }

  public boolean areAllUnitsDestroyed() {
    return createdFirstUnit && army.isEmpty();
  }

  public boolean isAlliedWith(Player p) {
    return p.team == team;
  }

  public City getHq() {
    return hq;
  }

  public boolean isWithinBudget(int price) {
    return budget - price >= 0;
  }

  public CO getCO() {
    return co;
  }

  /**
   * Check if the given location is within the co Zone:
   * <ul>
   * <li>A CO should be loaded into a unit</li>
   * <li>The location should be within the zone of this coUnit</li>
   * </ul>
   */
  public boolean isInCOZone(Location location) {
    return isCOLoaded() && co.isInCOZone(getCOUnit(), location);
  }

  /**
   * @return is the CO loaded into a unit owned by this player
   */
  public boolean isCOLoaded() {
    return getCOUnit() != null;
  }

  /**
   * Get the unit that has a CO on board.
   * Null if the CO is not boarded into a unit.
   */
  private Unit getCOUnit() {
    for (Unit unit : army) {
      if (unit.isCoOnBoard()) {
        return unit;
      }
    }
    return null;
  }

  public Collection<Location> getCoZone() {
    return Collections.unmodifiableCollection(coZone);
  }

  public Direction getUnitFacingDirection() {
    return unitFacingDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Player player = (Player) o;

    if (id != player.id) return false;
    if (color != null ? !color.equals(player.color) : player.color != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("[name=%s id=%s state=%s color=%s budget=%s team=%s]",
      name, id, getState(), ColorUtil.toString(color), budget, team);
  }

  public String printStats() {
    return String.format("%s units(%s) cities(%s) HQ=%s", ColorUtil.toString(color), army.size(), cities.size(), hq != null);
  }
}