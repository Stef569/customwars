package com.customwars.client.ui.state;

import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.network.User;
import com.customwars.client.tools.ColorUtil;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

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

  // The CO selected by the user
  public CO selectedCO;

  // index == playerID
  private CO[] cos;
  private int[] teams;
  private Color[] colors;
  private String[] controllers; // AI or HUMAN

  // Network
  public String serverGameName;
  public User user;

  public void clear() {
    initialGame = null;
    replay = null;
    game = null;
    map = null;
    serverGameName = "";
    selectedCO = null;
    if (cos != null) Arrays.fill(cos, null);
    if (teams != null) Arrays.fill(teams, -1);
    if (colors != null) Arrays.fill(colors, null);
    if (controllers != null) Arrays.fill(controllers, null);
  }

  /**
   * Set player defaults for each player:
   * random CO
   * color equal to the co style
   * unique team
   * human controller
   */
  public void setPlayerDefaults() {
    Collection<Player> mapPlayers = map.getUniquePlayers();
    cos = new CO[mapPlayers.size()];
    teams = new int[mapPlayers.size()];
    colors = new Color[mapPlayers.size()];
    controllers = new String[mapPlayers.size()];

    int team = 1;
    for (Player player : mapPlayers) {
      int playerID = player.getId();
      CO randomCO = COFactory.getRandomCO();

      cos[playerID] = randomCO;
      teams[playerID] = team++;
      String colorName = randomCO.getStyle().getColorName();
      colors[playerID] = ColorUtil.toColor(colorName);
      controllers[playerID] = "HUMAN";
    }
  }

  public void setCO(CO co, int playerID) {
    cos[playerID] = co;
  }

  public void setTeam(int team, int playerID) {
    teams[playerID] = team;
  }

  public void setColor(Color color, int playerID) {
    colors[playerID] = color;
  }

  public void setControllerType(String controllerType, int playerID) {
    controllers[playerID] = controllerType;
  }

  public CO getCO(Player player) {
    return cos[player.getId()];
  }

  public Color getColor(Player player) {
    return colors[player.getId()];
  }

  public Color getColor(int playerID) {
    return colors[playerID];
  }

  public int getTeam(Player player) {
    return teams[player.getId()];
  }

  public int getTeam(int playerID) {
    return teams[playerID];
  }

  public String getControllerType(Player player) {
    return controllers[player.getId()];
  }
}
