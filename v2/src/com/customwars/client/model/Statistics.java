package com.customwars.client.model;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Gather statistics about the given game
 *
 * @author stefan
 */
public class Statistics implements PropertyChangeListener {
  private Game game;
  private Map<Player, PlayerStatistics> playerStatistics;

  public Statistics(Game game) {
    this.game = game;
    addListeners(game);
    initPlayerStatistics();
  }

  private void addListeners(Game game) {
    for (Player p : game.getAllPlayers()) {
      p.addPropertyChangeListener(this);

      for (City city : p.getAllCities()) {
        city.addPropertyChangeListener(this);
      }
    }

    Player neutralPlayer = game.getPlayerByID(Player.NEUTRAL_PLAYER_ID);
    for (City neutralCity : neutralPlayer.getAllCities()) {
      neutralCity.addPropertyChangeListener(this);
    }
  }

  private void initPlayerStatistics() {
    playerStatistics = new HashMap<Player, PlayerStatistics>();
    for (Player player : game.getAllPlayers()) {
      playerStatistics.put(player, new PlayerStatistics());
    }
  }

  public PlayerStatistics getPlayerStats(Player player) {
    return playerStatistics.get(player);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    // Only gather statistics when the game has started
    if (!game.isActive()) return;
    String propertyName = evt.getPropertyName();

    if (evt.getSource() instanceof Player) {
      if (propertyName.equals("unit")) {
        Player player = (Player) evt.getSource();
        Player activePlayer = game.getActivePlayer();
        PlayerStatistics playerStats = playerStatistics.get(player);

        if (evt.getOldValue() == null && evt.getNewValue() != null) {
          playerStats.unitsCreated++;
        } else if (evt.getOldValue() != null && evt.getNewValue() == null) {
          playerStats.unitsLost++;

          // Don't record units killing themselfs..
          if (activePlayer != player) {
            PlayerStatistics activePlayerStats = getActivePlayerStats();
            activePlayerStats.unitsKilled++;
          }
        }
      }
    } else if (evt.getSource() instanceof City) {
      if (propertyName.equals("captured")) {
        if ((Boolean) evt.getNewValue()) {
          PlayerStatistics activePlayerStats = getActivePlayerStats();
          activePlayerStats.citiesCaptured++;
        }
      }
    }
  }

  private PlayerStatistics getActivePlayerStats() {
    return playerStatistics.get(game.getActivePlayer());
  }

  public class PlayerStatistics {
    public int unitsCreated, unitsKilled, unitsLost, citiesCaptured;
  }
}
