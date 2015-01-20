package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CW Impl of a Turnbased game.
 * <p/>
 * The Players in the map(map players) will be replaced by players from the players list(game players).
 * Map players have an ID, Color and a HQ(if any), all other player values are default or null values.
 * <p/>
 * Usage:
 * Game game = new Game(map,players,gameConfig)
 * game.startGame();
 * game.endTurn();
 * game.endTurn();
 */
public class Game extends TurnBasedGame implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;
  private int weather;            // The current weather in effect
  private int cityFunds;          // The amount of money each City produces each turn
  private final GameStatistics gameStatistics;  // Holds statistics for each player(number of units killed, cities captures,...)

  private Unit activeUnit;        // There can only be one active unit in a game at any time

  public Game(Map map, List<Player> players, GameRules gameRules) {
    super(map, players, gameRules.getDayLimit());
    applyGameConfig(gameRules);
    gameStatistics = new GameStatistics(players);
    init();
  }

  private void applyGameConfig(GameRules gameRules) {
    this.weather = gameRules.getStartWeather();
    this.cityFunds = gameRules.getCityFunds();
    this.map.setFogOfWarOn(gameRules.isFogOfWarOn());
  }

  /**
   * Copy Constructor
   *
   * @param otherGame game to copy
   */
  public Game(Game otherGame) {
    super(otherGame);
    this.weather = otherGame.weather;
    this.cityFunds = otherGame.cityFunds;
    this.gameStatistics = new GameStatistics(otherGame.gameStatistics);

    if (otherGame.activeUnit != null) {
      this.activeUnit = map.getUnitOn(otherGame.activeUnit.getLocation());
    }
    init();
  }

  private void init() {
    replaceMapPlayers();
    initCityFunds();
    new GameStatisticsListener(this, gameStatistics);
  }

  /**
   * Replace mapPlayers with GamePlayers
   * by comparing their ID
   */
  private void replaceMapPlayers() {
    Collection<Player> mapPlayers = map.getUniquePlayers();
    for (Player player : mapPlayers) {
      Player gamePlayer = getPlayerByID(player.getId());
      map.replacePlayer(player, gamePlayer);
    }
  }

  private void initCityFunds() {
    for (Tile t : map.getAllTiles()) {
      City city = map.getCityOn(t);

      if (city != null) {
        city.setFunds(cityFunds);
      }
    }
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

    for (Player player : getAllPlayers()) {
      player.addPropertyChangeListener(this);

      City hq = player.getHq();
      if (hq != null) {
        hq.addPropertyChangeListener(this);
      }
    }
  }

  void startTurn(Player player) {
    player.getCO().dayStart(player);
    destroyUnitsWithoutSupplies(player);
    supplyUnitsAdjacentOfTransport(player);
    super.startTurn(player);
  }

  /**
   * When a unit has 0 supplies:
   * Ships sink and airplanes fall out of the sky
   * Ground units are better off they are just immobilized
   */
  private void destroyUnitsWithoutSupplies(Player player) {
    Collection<Unit> unitsToDestroy = new ArrayList<Unit>();

    for (Unit unit : player.getArmy()) {
      if (!unit.isInTransport()) {
        if (unit.getSupplies() == 0 && isDestroyedWhenOutOfSupplies(unit)) {
          // Don't call unit.destroy() here as it will remove the unit from
          // the player army collection throwing a ConcurrentModificationException
          unitsToDestroy.add(unit);
        }
      }
    }

    for (Unit unit : unitsToDestroy) {
      unit.destroy(true);
    }
  }

  private static boolean isDestroyedWhenOutOfSupplies(Unit unit) {
    return unit.isAir() || unit.isNaval();
  }

  /**
   * Locate transports that transport ground units
   * Check for units that can be supplied around that transport
   */
  private void supplyUnitsAdjacentOfTransport(Player player) {
    for (Unit unit : player.getArmy()) {
      if (!unit.isInTransport()) {
        if (unit.getStats().canTransport() && unit.isLand()) {
          supplyUnitOnAdjacentTile(unit, Direction.NORTH);
          supplyUnitOnAdjacentTile(unit, Direction.EAST);
          supplyUnitOnAdjacentTile(unit, Direction.SOUTH);
          supplyUnitOnAdjacentTile(unit, Direction.WEST);
        }
      }
    }
  }

  private void supplyUnitOnAdjacentTile(Unit supplier, Direction direction) {
    Tile adjacentTile = map.getRelativeTile(supplier.getLocation(), direction);
    if (adjacentTile != null && adjacentTile.getLocatableCount() > 0) {
      Unit unit = map.getUnitOn(adjacentTile);
      supplier.supply(unit);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    if (!isStarted()) return;

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
      setState(GameState.GAME_OVER);
    }
  }

  /**
   * A HQ was captured by newOwner
   * the oldOwner is destroyed
   *
   * @param evt Event send by a hq
   */
  private void hqOwnerChange(PropertyChangeEvent evt) {
    Player oldOwner = (Player) evt.getOldValue();
    Player newOwner = (Player) evt.getNewValue();

    // Only destroy the old hq owner if that player is still playing
    if (oldOwner.isActive()) {
      oldOwner.destroy(newOwner);
      firePropertyChange("hqCaptured", oldOwner, newOwner);
    }
  }

  /**
   * A unit is added or removed from a player
   *
   * @param evt Event send by a player
   */
  private void playerUnitChange(PropertyChangeEvent evt) {
    Player player = (Player) evt.getSource();

    // If the player is still playing and lost all of his units destroy it.
    if (player.isActive() && player.areAllUnitsDestroyed()) {
      player.destroy(map.getNeutralPlayer());
    }
  }

  /**
   * The game is over when
   * the active players(not destroyed, not neutral) are allied or When there is only 1 player left
   */
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

    Player p = activePlayers.get(0);
    for (Player player : activePlayers) {
      if (!player.isAlliedWith(p)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the players that are playing are all AI players.
   *
   * @return if the active players in the game are all AI players
   */
  public boolean isAIOnlyGame() {
    for (Player player : getAllPlayers()) {
      if (player.isActive() && !player.isAi()) {
        return false;
      }
    }

    return true;
  }

  public void setActiveUnit(Unit unit) {
    Unit oldVal = this.activeUnit;
    this.activeUnit = unit;
    firePropertyChange("activeunit", oldVal, this.activeUnit);
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

  public GameStatistics getStats() {
    return gameStatistics;
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    // When a Game is read from a stream,
    // make sure to register the listeners for the statistics.
    new GameStatisticsListener(this, gameStatistics);
  }
}
