package com.customwars.client.ui.state;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.network.User;

/**
 * Allows to pass data between states
 *
 * @author stefan
 */
public class StateSession {
  public GameReplay replay;
  public Game initialGame;
  public Game game;
  public Map<Tile> map;

  // Network
  public String serverGameName;
  public User user;

  public void clear() {
    initialGame = null;
    replay = null;
    game = null;
    map = null;
    serverGameName = "";
  }
}
