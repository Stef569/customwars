package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.action.game.LoadGameAction;
import com.customwars.client.action.game.SaveGameAction;
import com.customwars.client.action.game.SaveReplayAction;
import com.customwars.client.action.unit.StartUnitCycleAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.StandardMenuItem;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

/**
 * Handles user input in the in game state
 */
public class UserInGameInputHandler implements InGameInputHandler {
  private static final Logger logger = Logger.getLogger(UserInGameInputHandler.class);
  private final InGameContext inGameContext;
  private final GUIContext guiContext;
  private final Game game;
  private final CursorController cursorController;
  private final Map map;

  public UserInGameInputHandler(InGameContext inGameContext) {
    this.game = inGameContext.getObj(Game.class);
    this.cursorController = inGameContext.getObj(CursorController.class);
    this.map = game.getMap();
    this.inGameContext = inGameContext;
    this.guiContext = inGameContext.getObj(GUIContext.class);
  }

  public void handleA(Tile cursorLocation) {
    City city = map.getCityOn(cursorLocation);
    Unit unit = getUnit(cursorLocation);

    if (canUnitAct(unit)) {
      inGameContext.handleUnitAPress(unit);
    } else if (canCityBuild(city)) {
      inGameContext.handleCityAPress(city);
    } else if (inGameContext.isDefaultMode()) {
      new ClearInGameStateAction().invoke(inGameContext);
      showContextMenu(cursorLocation);
    } else {
      throw new AssertionError("could not handle A press");
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
  private boolean canCityBuild(City city) {
    if (city != null) {
      Tile cityLocation = (Tile) city.getLocation();
      boolean fogged = cityLocation.isFogged();
      boolean noUnitOnCity = cityLocation.getLocatableCount() == 0;
      boolean cityOwnedByActivePlayer = city.isOwnedBy(game.getActivePlayer());

      return !fogged && noUnitOnCity && cityOwnedByActivePlayer && city.canBuild();
    } else {
      return false;
    }
  }

  private void showContextMenu(Tile menuLocation) {
    PopupMenu popupMenu = new PopupMenu(guiContext, "Game menu");

    StandardMenuItem endTurnMenuItem = buildMenuItem(App.translate("end_turn"), ActionFactory.buildEndTurnAction());
    popupMenu.addItem(endTurnMenuItem);

    if (App.isSinglePlayerGame()) {
      StandardMenuItem saveGameMenuItem = buildMenuItem(App.translate("save_game"), new SaveGameAction());
      popupMenu.addItem(saveGameMenuItem);

      StandardMenuItem loadGameMenuItem = buildMenuItem(App.translate("load_game"), new LoadGameAction());
      popupMenu.addItem(loadGameMenuItem);
    }

    StandardMenuItem saveReplayMenuItem = buildMenuItem(App.translate("save_replay"), new SaveReplayAction());
    popupMenu.addItem(saveReplayMenuItem);

    StandardMenuItem endGameMenuItem = buildMenuItem(App.translate("end_game"), ActionFactory.buildEndGameAction());
    popupMenu.addItem(endGameMenuItem);

    inGameContext.doAction(new ShowPopupMenuAction(popupMenu, menuLocation));
  }

  /**
   * Build a menu item backed by a CWAction
   *
   * @param menuItemName The name of the menu item, as shown in the gui
   * @param action       The action to perform when clicked on the menu item
   */
  public StandardMenuItem buildMenuItem(final String menuItemName, final CWAction action) {
    StandardMenuItem menuItem = new StandardMenuItem(menuItemName, guiContext);
    menuItem.addListener(new ComponentListener() {
      public void componentActivated(AbstractComponent source) {
        inGameContext.doAction(action);
      }
    });
    return menuItem;
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
        cursorController.moveCursorToNextLocation();
      }
    }
  }

  public void endTurn() {
    CWAction endTurn = ActionFactory.buildEndTurnAction();
    inGameContext.doAction(endTurn);
  }
}
