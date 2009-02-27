package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.util.List;

/**
 * CW Impl of a Turnbase game
 * The Players in the map will be replaced by a player from the players list.
 */
public class Game extends TurnBasedGame {
  private Unit activeUnit;        // There can only be one active unit in a game at any time
  private int weather;            // The current weather in effect
  private int cityFunds;          // The amount of money each City produces each turn

  public Game(Map<Tile> map, List<Player> players, GameConfig gameConfig) {
    super(map, players, gameConfig.getRules());
    applyGameConfig(gameConfig);
  }

  public void applyGameConfig(GameConfig gameConfig) {
    this.weather = gameConfig.getStartWeather();
    this.cityFunds = gameConfig.getCityFunds();

    // Start at turn -1, because startGame will end the first turn to allow the first player to get
    // The city funds.
    super.turn = new Turn(-1, gameConfig.getTurnLimit());
  }

  /**
   * Start the game for the first player in the players list
   */
  public void startGame() {
    startGame(getPlayer(0));
  }

  /**
   * Starts a game
   * replacing mapPlayers with GamePlayers
   * Get all cities, Units from the map
   * Read their id,
   * Overwrite the dummy map owner of the city/Unit by
   * comparing their id to a real player in the Game
   * add the unit/city to their army/cities
   */
  public void startGame(Player gameStarter) {
    for (Tile t : map.getAllTiles()) {
      City city = map.getCityOn(t);
      Unit unit = map.getUnitOn(t);

      replaceCityOwner(city);
      replaceUnitOwner(unit);
      initCityFunds(t);
    }

    super.startGame(gameStarter);
    initZones();
  }

  private void replaceCityOwner(City city) {
    if (city != null) {
      Player newOwner = getGamePlayer(city.getOwner());

      // Link Player and city
      newOwner.addCity(city);
      city.setOwner(newOwner);
    }
  }

  private void replaceUnitOwner(Unit unit) {
    if (unit != null) {
      Player newOwner = getGamePlayer(unit.getOwner());

      // Link Player and unit
      newOwner.addUnit(unit);
      unit.setOwner(newOwner);
    }
  }

  private Player getGamePlayer(Player dummy) {
    Player gamePlayer = getPlayer(dummy.getId());
    if (gamePlayer == null)
      throw new IllegalArgumentException("No game player found for player id " + dummy.getId());
    return gamePlayer;
  }

  /**
   * Set city funds to the city on t
   */
  private void initCityFunds(Tile t) {
    City city = map.getCityOn(t);
    city.setFunds(cityFunds);
  }

  /**
   * Create move/Attack zones for each unit of each player.
   */
  public void initZones() {
    for (Player player : getAllPlayers()) {
      map.resetFogMap(player);
      map.initUnitZonesForPlayer(player);
    }
    map.resetFogMap(activePlayer);
  }

  // ---------------------------------------------------------------------------
  // SETTERS
  // --------------------------------------------------------------------------
  public void setActiveUnit(Unit activeUnit) {
    Unit oldVal = this.activeUnit;
    this.activeUnit = activeUnit;
    firePropertyChange("activeUnit", oldVal, this.activeUnit);
  }

  public void setWeather(int weather) {
    int oldVal = this.weather;
    this.weather = weather;
    firePropertyChange("weather", oldVal, this.weather);
  }

  // ---------------------------------------------------------------------------
  // GETTERS
  // ---------------------------------------------------------------------------
  public Unit getActiveUnit() {
    return activeUnit;
  }

  public int getWeather() {
    return weather;
  }

  public int getTurnLimit() {
    return turn.getTurnLimit();
  }
}
