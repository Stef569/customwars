package com.customwars.client.ui.state;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Allows to pass data between states
 *
 * @author stefan
 */
public class StateSession {
  private Map<Tile> map;

  public Map<Tile> getMap() {
    return map;
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
  }
}
