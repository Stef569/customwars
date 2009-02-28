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
 * Represent a player that can participates in the game, can be either
 * a human, AI or neutral -player.
 * Can be allied with another Player
 * Each non neutral player has an unique ID
 * A Player is always in the IDLE state
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
  private List<Unit> army;  // All the units of this player
  private List<City> cities;// All the cities of this player
  private boolean createdFirstUnit;

  private Player() {
    army = new LinkedList<Unit>();
    cities = new LinkedList<City>();
  }

  public Player(int id, Color color, boolean neutral, City hq) {
    this();
    this.id = id;
    this.color = color;
    this.neutral = neutral;
    setHq(hq);
  }

  public Player(int id, Color color, boolean neutral, City hq, String name, int budget, int team, boolean ai) {
    this(id, color, neutral, hq);
    this.name = name;
    this.budget = budget;
    this.team = team;
    this.ai = ai;
  }

  /**
   * Create a Neutral player
   */
  public Player(Color color, City hq, int team) {
    this(Player.NEUTRAL_PLAYER_ID, color, true, hq, "Neutral", 0, team, false);
  }

  public void startTurn() {
    if (neutral) return;
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
   * When a player dies
   * all units are destroyed
   * all cities are owned by the conquerer
   *
   * @param conquerer the player that destroyed the last unit
   */
  public void die(Player conquerer) {
    for (Unit unit : army) {
      unit.setState(GameObjectState.DESTROYED);
    }

    for (City city : cities) {
      city.setOwner(conquerer);
    }
  }

  public void purchase(Unit unit) {
    if (canPurchase(unit)) {
      addToBudget(-unit.getPrice());
    }
  }

  public boolean canPurchase(Unit unit) {
    return budget - unit.getPrice() >= 0;
  }

  public void addToBudget(int amount) {
    int validBudget = Args.getBetweenZeroMax(this.budget + amount, Integer.MAX_VALUE);
    setBudget(validBudget);
  }

  void setBudget(int budget) {
    int oldBudget = this.budget;
    this.budget = budget;
    firePropertyChange("budget", oldBudget, this.budget);
  }

  // ---------------------------------------------------------------------------
  // Handle cities that are owned by this player
  // ---------------------------------------------------------------------------
  public void addCity(City city) {
    if (city == null) throw new IllegalArgumentException("null city cannot be added");
    if (cities.contains(city))
      throw new IllegalArgumentException("cannot add the same city");

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

  public void removeAllCities() {
    cities.clear();
    firePropertyChange("property", null, null);
  }

  public Iterable<City> getAllCities() {
    Iterable<City> it;

    it = new Iterable<City>() {
      public Iterator<City> iterator() {
        return cities.iterator();
      }
    };
    return it;
  }

  // ---------------------------------------------------------------------------
  // Handle Units that are owned by this player
  // ---------------------------------------------------------------------------
  public void addUnit(Unit u) {
    if (u == null) throw new IllegalArgumentException("null unit cannot be added");
    if (army.contains(u)) throw new IllegalArgumentException("army already contains the same unit");

    if (army.size() == 0) createdFirstUnit = true;
    army.add(u);
    u.setOwner(this);
    firePropertyChange("unit", null, u);
  }

  public boolean containsUnit(Unit u) {
    return army.contains(u);
  }

  public void removeUnit(Unit u) {
    army.remove(u);
    firePropertyChange("unit", u, null);
  }

  public void removeAllUnits() {
    army.clear();
    firePropertyChange("unit", null, null);
  }

  public Iterable<Unit> getArmy() {
    Iterable<Unit> it;

    it = new Iterable<Unit>() {
      public Iterator<Unit> iterator() {
        return army.iterator();
      }
    };
    return it;
  }

  public void clear() {
    removeAllUnits();
    removeAllCities();
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
    if (hq != null) hq.setOwner(this);
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

  public boolean isAi() {
    return ai;
  }

  public boolean isNeutral() {
    return neutral;
  }

  public boolean isDestroyed() {
    return createdFirstUnit && army.isEmpty();
  }

  public boolean isAlliedWith(Player p) {
    return p.getTeam() == team;
  }

  public City getHq() {
    return hq;
  }

  @Override
  public String toString() {
    return "[name=" + name + " id=" + id + " color=" + ColorUtil.toString(color) + "]";
  }
}
