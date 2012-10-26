package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Gather statistics for each player in the given game.
 * Player statistics are stored into the GameStatistics class
 *
 * The following statistic keys are created when creating this listener.
 * Note that if the key already exists it will not be overwritten.
 * name - default value
 * array_units_created - new int[]
 * units_created - 0
 * units_killed - 0
 * units_lost - 0
 * cities_captured - 0
 * favorite_unit - "none"
 */
public class GameStatisticsListener implements PropertyChangeListener {
  private final TurnBasedGame game;
  private GameStatistics gameStatistics;
  private final Map<Integer, PlayerStats> playerStatistics;

  public GameStatisticsListener(TurnBasedGame game, GameStatistics gameStatistics) {
    this.game = game;
    this.gameStatistics = gameStatistics;
    this.playerStatistics = new HashMap<Integer, PlayerStats>();

    addListeners(game);
    initPlayerStatistics();
  }

  private void addListeners(TurnBasedGame game) {
    for (Player p : game.getAllPlayers()) {
      p.addPropertyChangeListener(this);

      for (City city : p.getAllCities()) {
        city.addPropertyChangeListener(this);
      }
    }

    Player neutralPlayer = game.getMap().getNeutralPlayer();
    for (City neutralCity : neutralPlayer.getAllCities()) {
      neutralCity.addPropertyChangeListener(this);
    }
  }

  private void initPlayerStatistics() {
    for (Player player : game.getAllPlayers()) {
      playerStatistics.put(player.getId(), new PlayerStats(player.getId(), gameStatistics));
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if (!game.isStarted()) return;

    String propertyName = evt.getPropertyName();
    if (evt.getSource() instanceof Player) {
      if (propertyName.equals("unit")) {
        unitInPlayerChange(evt);
      }
    } else if (evt.getSource() instanceof City) {
      if (propertyName.equals("captured")) {
        cityCaptureChange(evt);
      }
    }
  }

  private void unitInPlayerChange(PropertyChangeEvent evt) {
    Player player = (Player) evt.getSource();
    Player activePlayer = game.getActivePlayer();
    PlayerStats playerStats = playerStatistics.get(player.getId());

    if (evt.getOldValue() == null && evt.getNewValue() != null) {
      Unit unit = (Unit) evt.getNewValue();
      playerStats.unitCreated(unit.getStats().getID());
    } else if (evt.getOldValue() != null && evt.getNewValue() == null) {
      playerStats.unitLost();

      // Don't record units killing themselves..
      if (activePlayer != player) {
        PlayerStats activePlayerStats = getActivePlayerStats();
        activePlayerStats.unitKilled();
      }
    }
  }

  private void cityCaptureChange(PropertyChangeEvent evt) {
    if ((Boolean) evt.getNewValue()) {
      PlayerStats activePlayerStats = getActivePlayerStats();
      activePlayerStats.cityCaptured();
    }
  }

  private PlayerStats getActivePlayerStats() {
    return playerStatistics.get(game.getActivePlayer().getId());
  }

  /**
   * Handles game events and updates the GameStatistics
   */
  private class PlayerStats {
    private final int playerID;
    private final GameStatistics stats;

    public PlayerStats(int playerID, GameStatistics stats) {
      this.playerID = playerID;
      this.stats = stats;
      initStatistics();
    }

    private void initStatistics() {
      createDefaultStat("array_units_created", new int[UnitFactory.countUnits()]);
      createDefaultStat("units_created", 0);
      createDefaultStat("units_killed", 0);
      createDefaultStat("units_lost", 0);
      createDefaultStat("cities_captured", 0);
      createDefaultStat("favorite_unit", "none");
    }

    /**
     * Creates a default statistic name-value pair if one does not exists yet.
     * @param name The name of the statistic
     * @param value The default value of the statistic
     */
    private void createDefaultStat(String name, Object value) {
      if(!stats.hasStatFor(playerID, name)) {
        stats.setPlayerStat(playerID, name, value);
      }
    }

    public void unitCreated(int unitID) {
      stats.addOne(playerID, "units_created");

      int[] createdUnits = stats.getArrayStat(playerID, "array_units_created");
      createdUnits[unitID]++;
      stats.setPlayerStat(playerID, "favorite_unit", getFavoriteUnit(createdUnits));
    }

    public void unitKilled() {
      stats.addOne(playerID, "units_killed");
    }

    public void unitLost() {
      stats.addOne(playerID, "units_lost");
    }

    public void cityCaptured() {
      stats.addOne(playerID, "cities_captured");
    }

    private String getFavoriteUnit(int[] createdUnits) {
      int favoriteUnitID = -1;
      int highestUnitCount = 0;

      for (int unitID = 0; unitID < createdUnits.length; unitID++) {
        int unitCount = createdUnits[unitID];
        if (unitCount > highestUnitCount) {
          highestUnitCount = unitCount;
          favoriteUnitID = unitID;
        }
      }

      if (favoriteUnitID == -1) {
        return "";
      } else {
        Unit favoriteUnit = UnitFactory.getUnit(favoriteUnitID);
        return favoriteUnit.getStats().getName();
      }
    }
  }
}