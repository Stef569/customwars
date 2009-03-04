package com.customwars.client.ui.state;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Allows to pass data between states
 *
 * @author stefan
 */
public class StateSession {
  private Game game;
  private Map<Tile> map;

  public void setMap(Map<Tile> map) {
    this.map = map;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public Map<Tile> getMap() {
    return map;
  }

  public Game getGame() {
    return game;
  }
}
