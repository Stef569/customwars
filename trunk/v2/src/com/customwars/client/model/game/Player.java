package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import tools.Args;
import tools.ColorUtil;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represent a player that can participates in a game, can be either
 * a human, AI or neutral player.
 * Can be allied with another Player
 * Each non neutral player has an unique ID, neutral players always have the same NEUTRAL_PLAYER_ID
 *
 * @author stefan
 */
public class Player extends GameObject {
  public static final int NEUTRAL_PLAYER_ID = -1;
  private int id;           // Unique Number
  private String name;      // Name for this player,not unique
  private Color color;      // Unique Color
  private int budget;       // Amount of money that can be spend
  private int team;         // The team this player is in
  private boolean ai;       // Is this a human or an AI player
  private boolean neutral;  // Is this player passive of active
  private City hq;          // Headquarters
  private List<Unit> army;    // All the units of this player
  private List<City> cities;  // All the cities of this player
  private boolean createdFirstUnit; // Has this player created his first unit

  private Player() {
    army = new LinkedList<Unit>();
    cities = new LinkedList<City>();
  }

  public Player(int id, City hq) {
    this();
    this.id = id;
    this.hq = hq;
  }

  public Player(int id, Color color, boolean neutral, City hq) {
    this();
    this.id = id;
    this.color = color;
    this.neutral = neutral;
    this.hq = hq;
  }

  public Player(int id, Color color, boolean neutral, City hq, String name, int budget, int team, boolean ai) {
    this(id, color, neutral, hq);
    this.name = name;
    this.budget = budget;
    this.team = team;
    this.ai = ai;
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
   * Destroy all units
   * Cities are owned by the conquerer
   *
   * @param conquerer the player that has conquered this player
   */
  public void destroy(Player conquerer) {
    destroyAllUnits();
    changeCityOwnersTo(conquerer);
    setState(GameObjectState.DESTROYED);
  }

  /**
   * Keep removing units from army, until there are none left
   * unit.destroy() removes the unit from this player's army.
   */
  public void destroyAllUnits() {
    while (army.size() > 0) {
      Unit unit = army.get(army.size() - 1);
      unit.destroy();
    }
  }

  public void changeCityOwnersTo(Player newOwner) {
    for (City city : getAllCities()) {
      city.setOwner(newOwner);
    }
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
   * Add city to this player, and set this player as owner of the city
   */
  public void addCity(City city) {
    Args.checkForNull(city, "null city cannot be added");
    Args.validate(cities.contains(city), "cannot add the same city");

    cities.add(city);
    city.setOwner(this);
    firePropertyChange("city", null, city);
  }

  public boolean containsCity(City city) {
    return cities.contains(city);
  }

  public void removeCity(City city) {
    cities.remove(city);
    firePropertyChange("city", city, null);
  }

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
    if (army.size() == 0) createdFirstUnit = true;

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

  public void setName(String name) {
    String oldVal = this.name;
    this.name = name;
    firePropertyChange("name", oldVal, name);
  }

  public void setColor(Color color) {
    Color oldVal = this.color;
    this.color = color;
    firePropertyChange("color", oldVal, color);
  }

  public void setTeam(int team) {
    int oldVal = this.team;
    this.team = team;
    firePropertyChange("team", oldVal, team);
  }

  public void setHq(City hq) {
    this.hq = hq;
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

  public int getTeam() {
    return team;
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
    return neutral;
  }

  public boolean areAllUnitsDestroyed() {
    return createdFirstUnit && army.isEmpty();
  }

  public boolean isAlliedWith(Player p) {
    return p.getTeam() == team;
  }

  public City getHq() {
    return hq;
  }

  public boolean isWithinBudget(int price) {
    return budget - price >= 0;
  }

  @Override
  public String toString() {
    return "[name=" + name + " id=" + id + " state=" + getState() + " color=" + ColorUtil.toString(color) +
            " budget=" + budget + " team=" + team + "]";
  }
}