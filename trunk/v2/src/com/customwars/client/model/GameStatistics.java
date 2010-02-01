package com.customwars.client.model;

import com.customwars.client.App;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.TurnBasedGame;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Gather statistics for each player about the given game.
 * Player statistics can be retrieved by calling {@link #getPlayerStats(Player)}
 *
 * @author stefan
 */
public class GameStatistics implements PropertyChangeListener, Serializable {
  private final TurnBasedGame game;
  private final Map<Player, PlayerStats> playerStatistics;

  public GameStatistics(TurnBasedGame game) {
    this.game = game;
    this.playerStatistics = new HashMap<Player, PlayerStats>();

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
      playerStatistics.put(player, new PlayerStats());
    }
  }

  /**
   * Retrieve all statistics for a player
   *
   * @param player The player to retrieve statistics for
   * @return A Map of statistics example: key="units killed" value="4"
   */
  public Map<String, String> getPlayerStats(Player player) {
    if (playerStatistics.containsKey(player)) {
      PlayerStats playerStats = playerStatistics.get(player);
      playerStats.update();
      return playerStats.getStats();
    } else {
      throw new IllegalArgumentException("No statistics for " + player);
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
    PlayerStats playerStats = playerStatistics.get(player);

    if (evt.getOldValue() == null && evt.getNewValue() != null) {
      Unit unit = (Unit) evt.getNewValue();
      playerStats.unitsCreated++;
      playerStats.createdUnitIDs[unit.getStats().getID()]++;
    } else if (evt.getOldValue() != null && evt.getNewValue() == null) {
      playerStats.unitsLost++;

      // Don't record units killing themselves..
      if (activePlayer != player) {
        PlayerStats activePlayerStats = getActivePlayerStats();
        activePlayerStats.unitsKilled++;
      }
    }
  }

  private void cityCaptureChange(PropertyChangeEvent evt) {
    if ((Boolean) evt.getNewValue()) {
      PlayerStats activePlayerStats = getActivePlayerStats();
      activePlayerStats.citiesCaptured++;
    }
  }

  private PlayerStats getActivePlayerStats() {
    return playerStatistics.get(game.getActivePlayer());
  }

  private static class PlayerStats implements Serializable {
    public int unitsCreated, unitsKilled, unitsLost, citiesCaptured;
    public final int[] createdUnitIDs = new int[UnitFactory.countUnits()];
    private transient Map<String, String> stats;

    public PlayerStats() {
      stats = new HashMap<String, String>();
    }

    public void update() {
      stats.put("Units Created", unitsCreated + "");
      stats.put("Units Killed", unitsKilled + "");
      stats.put("Units Lost", unitsLost + "");
      stats.put("Cities Captured", citiesCaptured + "");
      stats.put("Favorite unit", getFavoriteUnit());
    }

    private String getFavoriteUnit() {
      int favoriteUnitID = -1;
      int highestUnitCount = 0;

      for (int unitID = 0; unitID < createdUnitIDs.length; unitID++) {
        int unitCount = createdUnitIDs[unitID];
        if (unitCount > highestUnitCount) {
          highestUnitCount = unitCount;
          favoriteUnitID = unitID;
        }
      }

      if (favoriteUnitID == -1) {
        return "";
      } else {
        Unit favoriteUnit = UnitFactory.getUnit(favoriteUnitID);
        return App.translate(favoriteUnit.getStats().getName());
      }
    }

    public Map<String, String> getStats() {
      return Collections.unmodifiableMap(stats);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      stats = new HashMap<String, String>();
    }
  }
}