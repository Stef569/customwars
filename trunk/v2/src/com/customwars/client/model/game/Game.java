package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.util.List;

/**
 * CW Impl of a Turnbase game
 * The Players in the map will be replaced by a player from the players list.
 *
 * Usage:
 * Game game = new Game(map,players,gameConfig)
 * game.init();
 * game.startGame();
 * game.endTurn();
 * game.endTurn();
 * ...
 * game.endTurn();
 */
public class Game extends TurnBasedGame {
  private Unit activeUnit;        // There can only be one active unit in a game at any time
  private int weather;            // The current weather in effect
  private int cityFunds;          // The amount of money each City produces each turn

  private boolean inited;         // Has this game been inited (map players replaced by game players)
  private boolean started;        // Has the game been started (in progress)

  public Game(Map<Tile> map, List<Player> players, GameConfig gameConfig) {
    super(map, players);
    applyGameConfig(gameConfig);
  }

  public void applyGameConfig(GameConfig gameConfig) {
    this.weather = gameConfig.getStartWeather();
    this.cityFunds = gameConfig.getCityFunds();
    super.turn = new Turn(0, gameConfig.getTurnLimit());
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
          Player newOwner = getGamePlayer(city.getOwner());
          newOwner.addCity(city);
          city.setFunds(cityFunds);
        }
        if (unit != null) {
          if (t.getLocatableCount() != 1) {
            throw new IllegalStateException("Tile @ " + t.getLocationString() + " contains " + t.getLocatableCount() + " units, limit=1");
          }

          Player newOwner = getGamePlayer(unit.getOwner());
          newOwner.addUnit(unit);
        }

      }
      inited = true;
    }
  }

  private Player getGamePlayer(Player dummy) {
    Player gamePlayer = getPlayer(dummy.getId());
    if (gamePlayer == null)
      throw new IllegalArgumentException("No game player found for player id " + dummy.getId());
    return gamePlayer;
  }

  /**
   * Start the game for the first player in the players list
   */
  public void startGame() {
    startGame(getPlayer(0));
  }

  /**
   * Start the game, gameStarter will be set to the activePlayer
   *
   * @param gameStarter the player starting this game
   */
  public void startGame(Player gameStarter) {
    if (!started) {
      super.startGame(gameStarter);
      initZones();
      started = true;
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

  // ---------------------------------------------------------------------------
  // SETTERS
  // --------------------------------------------------------------------------
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
