package com.customwars.client.model.game;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CW Impl of a Turnbase game
 * The Players in the map(map players) will be replaced by players from the players list(game players).
 * Usage:
 * Game game = new Game(map,players,gameConfig)
 * game.startGame();
 * game.endTurn();
 * game.endTurn();
 * ...
 */
public class Game extends TurnBasedGame implements PropertyChangeListener {
  private int weather;            // The current weather in effect
  private int cityFunds;          // The amount of money each City produces each turn

  private Unit activeUnit;        // There can only be one active unit in a game at any time

  public Game(Map<Tile> map, List<Player> players, GameConfig gameConfig) {
    super(map, players, gameConfig.getTurnLimit(), gameConfig.getDayLimit());
    applyGameConfig(gameConfig);
    init();
  }

  private void applyGameConfig(GameConfig gameConfig) {
    this.weather = gameConfig.getStartWeather();
    this.cityFunds = gameConfig.getCityFunds();
  }

  /**
   * Replace mapPlayers with GamePlayers
   * Get all cities, Units from the map
   * Read their id,
   * Overwrite the dummy map owner of the city/Unit by
   * comparing their id to a real player in the Game
   * add the unit/city to their army/cities
   */
  private void init() {
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
  }

  private void initCity(City city) {
    Player mapPlayer = city.getOwner();
    Player gamePlayer = getGamePlayer(mapPlayer);

    if (mapPlayer.getHq() == city) {
      gamePlayer.setHq(city);
    }

    gamePlayer.addCity(city);
    city.setFunds(cityFunds);
  }

  private void initUnit(Unit unit) {
    Player gamePlayer = getGamePlayer(unit.getOwner());
    gamePlayer.addUnit(unit);

    for (int i = 0; i < unit.getLocatableCount(); i++) {
      Unit unitInTransport = (Unit) unit.getLocatable(i);
      gamePlayer.addUnit(unitInTransport);
    }
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
   * Search for a friendly city with a unit on the same tile -> Supply and heal the unit on the city
   * Search for air, naval units that have 0 supplies -> Destroy them
   */
  private void checkSupplyConditions(Player player) {
    Collection<Unit> unitsToDestroy = new ArrayList<Unit>();

    for (Unit unit : player.getArmy()) {
      // Skip units located in a transport
      if (unit.getLocation() instanceof Tile) {
        Location tile = unit.getLocation();
        City city = map.getCityOn(tile);

        if (city != null) {
          if (city.canSupply(unit) && city.canHeal(unit)) {
            city.supply(unit);
            city.heal(unit);
          }
        }

        if (unit.getSupplies() == 0 && isDestroyedWhenOutOfSupplies(unit.getArmyBranch())) {
          // Don't call unit.destroy() here as it will remove the unit from
          // the owning player army collection throwing a ConcurrentModificationException
          unitsToDestroy.add(unit);
        }
      }
    }

    for (Unit unit : unitsToDestroy) {
      unit.destroy();
    }
  }

  private static boolean isDestroyedWhenOutOfSupplies(ArmyBranch armyBranch) {
    return armyBranch == ArmyBranch.AIR || armyBranch == ArmyBranch.NAVAL;
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
  private static void hqOwnerChange(PropertyChangeEvent evt) {
    Player oldOwner = (Player) evt.getOldValue();
    Player newOwner = (Player) evt.getNewValue();

    // Only destroy the old hq owner if that player is still playing
    if (oldOwner.isActive()) {
      oldOwner.destroy(newOwner);
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
      player.destroy(neutralPlayer);
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
}
