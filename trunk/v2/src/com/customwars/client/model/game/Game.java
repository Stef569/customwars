package com.customwars.client.model.game;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CW Impl of a Turnbased game
 * The Players in the map(map players) will be replaced by players from the players list(game players).
 * map players have an ID, Color and a HQ(if any), all other player values are default or null values.
 *
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

  public Game(Map<Tile> map, List<Player> players, GameRules gameRules) {
    super(map, players, gameRules.getDayLimit());
    applyGameConfig(gameRules);
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

    if (otherGame.activeUnit != null) {
      this.activeUnit = map.getUnitOn(otherGame.activeUnit.getLocation());
    }
    init();
  }

  /**
   * Replace mapPlayers with GamePlayers
   * by comparing their ID
   */
  private void init() {
    Collection<Player> mapPlayers = map.getUniquePlayers();
    for (Player player : mapPlayers) {
      Player gamePlayer = getPlayerByID(player.getId());
      map.replacePlayer(player, gamePlayer);
    }
    initCityFunds();
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
    initZones();

    for (Player player : getAllPlayers()) {
      player.addPropertyChangeListener(this);

      City hq = player.getHq();
      if (hq != null) {
        hq.addPropertyChangeListener(this);
      }
    }
  }

  /**
   * Create move/Attack zones for each unit of each player.
   * The map is temporarily reset for each player since fog and hidden units affects zones.
   */
  public void initZones() {
    Collection<Tile> tileCopies = new ArrayList<Tile>(map.countTiles());

    // Save the col, row and fogged information
    for (Tile t : map.getAllTiles()) {
      Tile tileCopy = new Tile(t.getCol(), t.getRow());
      tileCopy.setFogged(t.isFogged());
      tileCopies.add(tileCopy);
    }

    for (Player player : getAllPlayers()) {
      map.resetFogMap(player);
      map.resetAllHiddenUnits(player);
      map.initUnitZonesForPlayer(player);
    }

    // Put the map back in the state it was before invoking this method
    Player activePlayer = getActivePlayer();
    map.resetFogMap(activePlayer);
    map.resetAllHiddenUnits(activePlayer);

    // Tiles we could see before remain visible
    for (Tile t : tileCopies) {
      if (!t.isFogged()) {
        map.getTile(t).setFogged(false);
      }
    }
  }

  void startTurn(Player player) {
    super.startTurn(player);
    destroyUnitsWithoutSupplies(player);
    supplyUnitsAdjacentOfTransport(player);
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
        if (unit.getSupplies() == 0 && isDestroyedWhenOutOfSupplies(unit.getArmyBranch())) {
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

  private static boolean isDestroyedWhenOutOfSupplies(ArmyBranch armyBranch) {
    return armyBranch == ArmyBranch.AIR || armyBranch == ArmyBranch.NAVAL;
  }

  /**
   * Locate transports that transport ground units
   * Check for units that can be supplied around that transport
   */
  private void supplyUnitsAdjacentOfTransport(Player player) {
    for (Unit unit : player.getArmy()) {
      if (!unit.isInTransport()) {
        if (unit.getStats().canTransport() && unit.getArmyBranch() == ArmyBranch.LAND) {
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
      Unit unit = (Unit) adjacentTile.getLastLocatable();
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
