package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import tools.Args;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * CW Impl of a Turnbase game
 * The Players in the map will be replaced by players from the players list.
 *
 * Usage:
 * Game game = new Game(map,players,neutralPlayer,gameConfig)
 * game.init();
 * game.startGame();
 * game.endTurn();
 * game.endTurn();
 * ...
 *
 * The game is over when
 * The turn or day limit is reached or
 * When the active players(not destroyed, not neutral) are allied or
 * When there is only 1 player left
 */
public class Game extends TurnBasedGame implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(Game.class);
  private Unit activeUnit;        // There can only be one active unit in a game at any time
  private int weather;            // The current weather in effect
  private int cityFunds;          // The amount of money each City produces each turn
  private boolean inited;         // Has this game been inited (map players replaced by game players)

  public Game(Map<Tile> map, List<Player> players, Player neutral, GameConfig gameConfig) {
    super(map, players, neutral, null);
    applyGameConfig(gameConfig);
  }

  public void applyGameConfig(GameConfig gameConfig) {
    this.weather = gameConfig.getStartWeather();
    this.cityFunds = gameConfig.getCityFunds();
    super.turn = new Turn(0, 1, gameConfig.getTurnLimit(), gameConfig.getDayLimit());
  }

  /**
   * replace mapPlayers with GamePlayers
   * Get all cities, Units from the map
   * Read their id,
   * Overwrite the dummy map owner of the city/Unit by
   * comparing their id to a real player in the Game
   * add the unit/city to their army/cities
   */
  public void init() {
    if (!inited) {
      for (Tile t : map.getAllTiles()) {
        City city = map.getCityOn(t);
        Unit unit = map.getUnitOn(t);

        if (city != null) {
          initCity(city);
        }
        if (unit != null) {
          initUnit(unit);
        }
      }
      inited = true;
    }
  }

  private void initCity(City city) {
    Tile t = (Tile) city.getLocation();
    Args.checkForNull(city.getLocation(), "City @ " + t.getLocationString() + " has no location");
    Args.checkForNull(city.getOwner(), "City @ " + t.getLocationString() + " has no owner");

    Player mapPlayer = city.getOwner();
    Player newOwner = getGamePlayer(mapPlayer);

    // Is this city the HQ
    if (mapPlayer.getHq() == city) {
      newOwner.setHq(city);
    }

    newOwner.addCity(city);
    city.setFunds(cityFunds);
  }

  private void initUnit(Unit unit) {
    Tile t = (Tile) unit.getLocation();
    Args.validate(t.getLocatableCount() != 1, "Tile @ " + t.getLocationString() + " contains " + t.getLocatableCount() + " units, limit=1");
    Args.checkForNull(unit.getLocation(), "Unit @ " + t.getLocationString() + " has no location");
    Args.checkForNull(unit.getOwner(), "Unit @ " + t.getLocationString() + " has no owner");

    Player newOwner = getGamePlayer(unit.getOwner());
    newOwner.addUnit(unit);
  }

  private Player getGamePlayer(Player dummy) {
    Player gamePlayer = getPlayerByID(dummy.getId());
    if (gamePlayer == null)
      throw new IllegalArgumentException("No game player found for player id " + dummy.getId());
    return gamePlayer;
  }

  /**
   * Start the game for the first player in the players list
   */
  public void startGame() {
    startGame(getPlayerByID(0));
  }

  /**
   * Start the game, gameStarter will be set to the activePlayer
   *
   * @param gameStarter the player starting this game
   */
  public void startGame(Player gameStarter) {
    super.startGame(gameStarter);
    initZones();

    for (Player player : getAllPlayers()) {
      player.addPropertyChangeListener(this);
      if (player.getHq() != null) {
        player.getHq().addPropertyChangeListener(this);
      }
    }
  }

  /**
   * Create move/Attack zones for each unit of each player.
   * fog of war is reset for each player since fog affects zones.
   */
  public void initZones() {
    for (Player player : getAllPlayers()) {
      map.resetFogMap(player);
      map.initUnitZonesForPlayer(player);
    }
    map.resetFogMap(getActivePlayer());
  }

  void startTurn(Player player) {
    super.startTurn(player);
    checkSupplyConditions(player);
  }

  /**
   * Search for a friendly city with a unit on the same tile.
   * supply that unit if the player can afford the supply cost.
   */
  private void checkSupplyConditions(Player player) {
    for (Unit unit : player.getArmy()) {
      Tile location = (Tile) unit.getLocation();
      City city = map.getCityOn(location);

      if (city != null && city.getOwner().isAlliedWith(player)) {
        if (city.canSupply(unit) || city.canHeal(unit)) {
          int supplyCost = unit.getPrice() / unit.getHp();

          if (player.isWithinBudget(supplyCost)) {
            int oldSupply = unit.getSupplies();
            city.supply(unit);
            city.heal(unit);
            player.addToBudget(-supplyCost);
            logger.debug("Supplied unit on city(" + location.getLocationString() + ") " + oldSupply + " -> " + unit.getSupplies());
          }
        }
      }
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    if (!isActive()) return;

    if (evt.getSource() instanceof City) {
      if (propertyName.equals("owner")) {
        hqOwnerChange(evt);
      }
    } else if (evt.getSource() instanceof Player) {
      if (propertyName.equals("unit")) {
        playerUnitChange(evt);
      }
    }

    if (isTheGameOver()) {
      setState(GameObjectState.DESTROYED);
    }
  }

  private void hqOwnerChange(PropertyChangeEvent evt) {
    Player oldOwner = (Player) evt.getOldValue();
    Player newOwner = (Player) evt.getNewValue();
    oldOwner.destroy(newOwner);
  }

  private void playerUnitChange(PropertyChangeEvent evt) {
    Player player = (Player) evt.getSource();
    if (player.isActive() && player.areAllUnitsDestroyed()) {
      player.destroy(neutralPlayer);
    }
  }

  private boolean isTheGameOver() {
    return getActivePlayerCount() <= 1 || isAlliedVictory();
  }

  /**
   * @return true if all the active players in the game are from the same team
   */
  private boolean isAlliedVictory() {
    List<Player> activePlayers = getActivePlayers();

    if (activePlayers.isEmpty()) {
      return true;
    }

    int team = activePlayers.get(0).getTeam();
    for (Player player : activePlayers) {
      if (player.getTeam() != team) {
        return false;
      }
    }
    return true;
  }

  public void setActiveUnit(Unit unit) {
    Unit oldVal = this.activeUnit;
    this.activeUnit = unit;
    firePropertyChange("activeUnit", oldVal, this.activeUnit);
  }

  public void setWeather(int weather) {
    int oldVal = this.weather;
    this.weather = weather;
    firePropertyChange("weather", oldVal, this.weather);
  }

  public Unit getActiveUnit() {
    return activeUnit;
  }

  public int getWeather() {
    return weather;
  }

  public boolean isInited() {
    return inited;
  }
}
