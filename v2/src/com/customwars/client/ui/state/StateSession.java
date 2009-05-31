package com.customwars.client.ui.state;

import com.customwars.client.model.Statistics;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Allows to pass data between states
 *
 * @author stefan
 */
public class StateSession {
  public Game game;
  public Map<Tile> map;
  public Statistics stats;
}
