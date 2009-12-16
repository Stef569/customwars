package com.customwars.client.controller;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameConfig;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle input in the game rules state
 */
public class GameRulesController {
  private final StateSession stateSession;
  private final StateChanger stateChanger;
  private final GameConfig gameConfig;
  private Page page;

  public GameRulesController(StateChanger stateChanger, StateSession stateSession) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
    this.gameConfig = new GameConfig();
  }

  public void init(Page page) {
    this.page = page;
  }

  /**
   * This method will gather default values initialised in gameObject
   * and show them as the selected value of the cbo
   */
  public void initValues() {
    page.getWidget("fog").setText(gameConfig.isFogOfWarOn() ? "True" : "False");
    page.getWidget("day_limit").setText(gameConfig.getDayLimit() + "");
    page.getWidget("funds").setText(gameConfig.getCityFunds() + "");
    page.getWidget("income").setText(gameConfig.getPlayerIncome() + "");
  }

  public void fundsChange(String newValue) {
    int newFunds = Integer.parseInt(newValue);
    gameConfig.setCityfunds(newFunds);
  }

  public void incomeChange(String newValue) {
    int income = Integer.parseInt(newValue);
    gameConfig.setPlayerIncome(income);
  }

  public void fogChange(Widget fogCbo) {
    String selectedText = fogCbo.getChild(fogCbo.getSelectedIndex()).getText();
    boolean fogON = Boolean.valueOf(selectedText);
    gameConfig.setFogOfWar(fogON);
  }

  public void dayLimitChange(String newValue) {
    int dayLimit = Integer.parseInt(newValue);
    gameConfig.setDayLimit(dayLimit);
  }

  public void back() {
    stateChanger.changeToPrevious();
  }

  public void continueToNextState() {
    Map<Tile> map = stateSession.map;
    List<Player> players = buildGamePlayers(map);
    stateSession.game = new Game(map, players, gameConfig);
    stateChanger.changeTo("IN_GAME");
  }

  /**
   * Create a new game player for each player in the map
   */
  private List<Player> buildGamePlayers(Map<Tile> map) {
    List<Player> players = new ArrayList<Player>();
    int team = 0;
    for (Player player : map.getUniquePlayers()) {
      int id = player.getId();
      Color color = player.getColor();
      String colorName = ColorUtil.toString(color);
      int income = gameConfig.getPlayerIncome();
      Player p = new Player(id, color, colorName, income, team++, false);
      players.add(p);
    }
    return players;
  }
}
