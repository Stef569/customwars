package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.action.game.LoadGameAction;
import com.customwars.client.action.game.SaveGameAction;
import com.customwars.client.action.unit.StartUnitCycleAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.GUIContext;

/**
 * Handles input in the in game state
 */
public class GameController {
  private static final Logger logger = Logger.getLogger(GameController.class);
  private final GameRenderer gameRenderer;
  private final CursorController cursorControl;
  private InGameContext inGameContext;
  private GUIContext guiContext;
  private final Game game;
  private final Map<Tile> map;

  public GameController(Game game, GameRenderer gameRenderer, SpriteManager spriteManager) {
    this.game = game;
    this.map = game.getMap();
    this.gameRenderer = gameRenderer;
    this.cursorControl = new InGameCursorController(game, game.getMap(), spriteManager);
  }

  public void handleA(Tile cursorLocation) {
    City city = map.getCityOn(cursorLocation);
    Unit unit = getUnit(cursorLocation);

    if (canUnitAct(unit)) {
      inGameContext.handleUnitAPress(unit);
    } else if (isCityPressed(city)) {
      inGameContext.handleCityAPress(city);
    } else if (inGameContext.isDefaultMode()) {
      ShowPopupMenuAction showContextMenuAction = buildContextMenu(cursorLocation);
      inGameContext.doAction(showContextMenuAction);
    } else {
      logger.warn("could not handle A press");
    }
  }

  /**
   * Return true
   * when the given unit is active or
   * when a unit is selected and is going to perform an action
   */
  private boolean canUnitAct(Unit unit) {
    return unit != null && unit.isActive() || inGameContext.isInUnitMode();
  }

  /**
   * Return true
   * when the city is visible, can build a unit
   * and is owned by the active player
   */
  private boolean isCityPressed(City city) {
    if (city == null) return false;
    Tile cityLocation = (Tile) city.getLocation();

    return !cityLocation.isFogged() && cityLocation.getLocatableCount() == 0 &&
      city.isOwnedBy(game.getActivePlayer()) && city.canBuild();
  }

  private ShowPopupMenuAction buildContextMenu(Tile menuLocation) {
    ShowPopupMenuAction showContextMenuAction = ShowPopupMenuAction.createPopupInMap("Game menu", menuLocation);

    MenuItem endTurnMenuItem = new MenuItem(App.translate("end_turn"), guiContext);
    showContextMenuAction.addAction(ActionFactory.buildEndTurnAction(), endTurnMenuItem);

    if (App.isSinglePlayerGame()) {
      MenuItem saveGameMenuItem = new MenuItem(App.translate("save_game"), guiContext);
      showContextMenuAction.addAction(new SaveGameAction(), saveGameMenuItem);

      MenuItem loadGameMenuItem = new MenuItem(App.translate("load_game"), guiContext);
      showContextMenuAction.addAction(new LoadGameAction(), loadGameMenuItem);
    }

    MenuItem endGameMenuItem = new MenuItem(App.translate("end_game"), guiContext);
    showContextMenuAction.addAction(ActionFactory.buildEndGameAction(), endGameMenuItem);

    return showContextMenuAction;
  }

  public void handleB(Tile cursorLocation) {
    Unit selectedUnit = getUnit(cursorLocation);
    if (selectedUnit != null) {
      inGameContext.handleUnitBPress(selectedUnit);
    }
  }

  /**
   * Return the active unit(if set)
   * or the selected unit(if present on the cursor location)
   * or null(both are not set)
   */
  private Unit getUnit(Tile cursorLocation) {
    Unit activeUnit = game.getActiveUnit();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    return activeUnit != null ? activeUnit : selectedUnit;
  }

  public void undo() {
    SFX.playSound("cancel");
    inGameContext.undo();
  }

  /**
   * Start unit cycle mode
   * If already in unit cycle mode, move the cursor to the next unit location
   * If there are no active units nothing happens
   */
  public void startUnitCycle() {
    if (!map.getActiveUnits().isEmpty()) {
      if (!inGameContext.isUnitCycleMode()) {
        inGameContext.doAction(new StartUnitCycleAction());
      } else {
        cursorControl.moveCursorToNextLocation();
      }
    }
  }

  public void endTurn() {
    CWAction endTurn = ActionFactory.buildEndTurnAction();
    inGameContext.doAction(endTurn);
  }

  public void setInGameContext(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.guiContext = inGameContext.getContainer();
  }

  public CursorController getCursorController() {
    return cursorControl;
  }
}
