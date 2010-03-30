package com.customwars.client.model.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains game statistics for each player. Keyed by a String. The value can be in the form of:
 * <ul>
 * <li>text</li>
 * <li>number</li>
 * <li>array</li>
 * </ul>
 *
 * Example key -> value pairs
 * units_killed -> 6
 * array_units_killed -> {1,0,0,0,0,0,0,0,5} (The array index is the unit ID)
 * cities_captured -> 1
 */
public class GameStatistics implements Serializable {
  private final Map<Integer, Map<String, Object>> playerStatistics;

  public GameStatistics() {
    playerStatistics = new HashMap<Integer, Map<String, Object>>();
  }

  public GameStatistics(List<Player> players) {
    this();

    for (Player player : players) {
      addPlayer(player);
    }
  }

  public void addPlayer(Player player) {
    playerStatistics.put(player.getId(), new HashMap<String, Object>());
  }

  /**
   * Copy constructor
   *
   * @param otherGameStatistics gameStatistics to copy
   */
  public GameStatistics(GameStatistics otherGameStatistics) {
    playerStatistics = new HashMap<Integer, Map<String, Object>>(otherGameStatistics.playerStatistics);
  }

  void setPlayerStat(int playerID, String key, Object value) {
    playerStatistics.get(playerID).put(key, value);
  }

  void addOne(int playerID, String key) {
    int val = getNumericStat(playerID, key) + 1;
    setPlayerStat(playerID, key, val + "");
  }

  public Set<String> getStatKeys(int playerID) {
    return playerStatistics.get(playerID).keySet();
  }

  public String getTextStat(int player, String key) {
    return getStat(player, key) + "";
  }

  public int getNumericStat(int playerID, String key) {
    Object value = getStat(playerID, key);

    if (value == null || value.equals("")) {
      return 0;
    } else {
      return Integer.parseInt(value + "");
    }
  }

  public int[] getArrayStat(int playerID, String key) {
    return (int[]) getStat(playerID, key);
  }

  private Object getStat(int player, String key) {
    return playerStatistics.get(player).get(key);
  }
}
