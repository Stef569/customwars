package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
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
 *
 * Pre: A map is set in the statesession
 */
public class GameRulesController {
  private final StateSession stateSession;
  private final StateChanger stateChanger;
  private final GameRules gameRules;
  private Page page;

  public GameRulesController(StateChanger stateChanger, StateSession stateSession) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
    this.gameRules = new GameRules();
  }

  public void init(Page page) {
    this.page = page;
  }

  /**
   * This method will gather the default game rule values initialised in the map
   * and show them as the selected value of the cbo
   */
  public void initValues() {
    page.getWidget("fog").setText(gameRules.isFogOfWarOn() ? "True" : "False");
    page.getWidget("day_limit").setText(gameRules.getDayLimit() + "");
    page.getWidget("funds").setText(gameRules.getCityFunds() + "");
    page.getWidget("income").setText(gameRules.getPlayerBudgetStart() + "");
  }

  public void fundsChange(String newValue) {
    int newFunds = Integer.parseInt(newValue);
    gameRules.setCityFunds(newFunds);
  }

  public void incomeChange(String newValue) {
    int startBudget = Integer.parseInt(newValue);
    gameRules.setPlayerBudgetStart(startBudget);
  }

  public void fogChange(Widget fogCbo) {
    String selectedText = fogCbo.getChild(fogCbo.getSelectedIndex()).getText();
    boolean fogON = Boolean.valueOf(selectedText);
    gameRules.setFogOfWar(fogON);
  }

  public void dayLimitChange(String newValue) {
    int dayLimit = Integer.parseInt(newValue);
    gameRules.setDayLimit(dayLimit);
  }

  public void back() {
    stateChanger.changeToPrevious();
  }

  public void continueToNextState() {
    if (App.isSinglePlayerGame()) {
      startSinglePlayerGame();
    } else {
      returnToMultiPlayerState();
    }
  }

  private void startSinglePlayerGame() {
    Map<Tile> map = stateSession.map;
    List<Player> players = buildGamePlayers(map);
    stateSession.game = new Game(map, players, gameRules);
    stateChanger.changeTo("IN_GAME");
  }

  /**
   * Create a new game player for each player in the map
   */
  private List<Player> buildGamePlayers(Map<Tile> map) {
    List<Player> gamePlayers = new ArrayList<Player>();
    int team = 0;
    for (Player mapPlayer : map.getUniquePlayers()) {
      int id = mapPlayer.getId();
      Color color = mapPlayer.getColor();
      String colorName = ColorUtil.toString(color);
      int startBudget = gameRules.getPlayerBudgetStart();
      Player gamePlayer = new Player(id, color, colorName, startBudget, team++, false);

      if (mapPlayer.getHq() != null) {
        gamePlayer.setHq(mapPlayer.getHq());
      }
      gamePlayers.add(gamePlayer);
    }
    return gamePlayers;
  }

  private void returnToMultiPlayerState() {
    // Overwrite default game rules with user chosen rules
    stateSession.map.setDefaultRules(gameRules);

    // Return to the multiplayer state by
    // going back 2 states (map select, game rules)
    stateChanger.changeToPrevious();
    stateChanger.changeToPrevious();
  }
}
