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
import com.customwars.client.action.game.StartDeleteUnitAction;
import com.customwars.client.action.game.ToggleInGameSoundAction;
import com.customwars.client.action.unit.StartUnitCycleAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.StandardMenuItem;
import com.customwars.client.ui.renderer.MapRenderer;
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
  private final MapRenderer mapRenderer;
  private boolean canDeleteUnit;

  public UserInGameInputHandler(InGameContext inGameContext) {
    this.game = inGameContext.getObj(Game.class);
    this.cursorController = inGameContext.getObj(CursorController.class);
    this.map = game.getMap();
    this.inGameContext = inGameContext;
    this.guiContext = inGameContext.getObj(GUIContext.class);
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
  }

  public void handleA(Tile cursorLocation) {
    City city = map.getCityOn(cursorLocation);
    Unit unit = getUnit(cursorLocation);

    if (inGameContext.isUnitDeleteMode()) {
      tryToDeleteUnit(cursorLocation);
    } else if (canUnitAct(unit)) {
      inGameContext.handleUnitAPress(unit);
    } else if (canCityBuild(city)) {
      inGameContext.handleCityAPress(city);
    } else if (inGameContext.isDefaultMode()) {
      new ClearInGameStateAction().invoke(inGameContext);
      showContextMenu(cursorLocation);
    } else {
      throw new AssertionError("could not handle A press context=" + inGameContext.toString());
    }
  }

  private void tryToDeleteUnit(Tile cursorLocation) {
    if (!canDeleteUnit) {
      inGameContext.doAction(new ClearInGameStateAction());
    } else {
      CWAction deleteUnitAction = ActionFactory.buildDeleteUnitAction(cursorLocation);
      inGameContext.doAction(deleteUnitAction);
      canDeleteUnit = false;
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

    StandardMenuItem deleteUnitMenuItem = buildMenuItem(App.translate("delete_unit"), new StartDeleteUnitAction());
    popupMenu.addItem(deleteUnitMenuItem);

    if (App.isSinglePlayerGame()) {
      StandardMenuItem saveGameMenuItem = buildMenuItem(App.translate("save_game"), new SaveGameAction());
      popupMenu.addItem(saveGameMenuItem);

      StandardMenuItem loadGameMenuItem = buildMenuItem(App.translate("load_game"), new LoadGameAction());
      popupMenu.addItem(loadGameMenuItem);
    }

    StandardMenuItem saveReplayMenuItem = buildMenuItem(App.translate("save_replay"), new SaveReplayAction());
    popupMenu.addItem(saveReplayMenuItem);

    float soundVolume = SFX.getSoundEffectVolume();
    String menuMessage = soundVolume == 0 ? App.translate("turn_sound_on") : App.translate("turn_sound_off");
    StandardMenuItem toggleSoundMenuItem = buildMenuItem(menuMessage, new ToggleInGameSoundAction());
    popupMenu.addItem(toggleSoundMenuItem);

    StandardMenuItem endGameMenuItem = buildMenuItem(App.translate("end_game"), ActionFactory.buildEndGameAction());
    popupMenu.addItem(endGameMenuItem);

    StandardMenuItem endTurnMenuItem = buildMenuItem(App.translate("end_turn"), ActionFactory.buildEndTurnAction());
    popupMenu.addItem(endTurnMenuItem);

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
    if (inGameContext.isUnitDeleteMode()) {
      inGameContext.doAction(new ClearInGameStateAction());
      return;
    }

    Unit selectedUnit = getUnit(cursorLocation);
    if (selectedUnit != null) {
      inGameContext.handleUnitBPress(selectedUnit);
    }
  }

  /**
   * @return The active unit if a unit has already previously been selected.
   * The selected unit if there is an active unit and a unit is present on the cursor location.
   * Null in all other cases.
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

  public void cursorMoved(Location oldLocation, Location newLocation, Direction moveDirection) {
    if (!oldLocation.equals(newLocation)) {
      updateDeleteUnitCursor(newLocation);
      updateAttackUnitCursor(newLocation);
      mapRenderer.cursorMoved(oldLocation, newLocation, moveDirection);
    }
  }

  private void updateDeleteUnitCursor(Location newLocation) {
    if (inGameContext.isUnitDeleteMode()) {
      Unit unit = map.getUnitOn(newLocation);

      if (canDeleteUnit(unit)) {
        cursorController.activateCursor("HAMMER");
        cursorController.moveCursor(newLocation);
        canDeleteUnit = true;
      } else {
        cursorController.activateCursor("CANCEL");
        cursorController.moveCursor(newLocation);
        canDeleteUnit = false;
      }
    }
  }

  private boolean canDeleteUnit(Unit unit) {
    return unit != null && unit.isActive() &&
      unit.getOwner().equals(game.getActivePlayer());
  }

  private void updateAttackUnitCursor(Location newLocation) {
    Unit activeUnit = game.getActiveUnit();

    if (activeUnit != null) {
      boolean validMovePath = mapRenderer.getUnitMovePath() != null;
      Player activePlayer = activeUnit.getOwner();
      boolean hasEnemyUnit = map.hasEnemyUnitOn(activePlayer, newLocation);
      Unit enemyUnit = map.getUnitOn(newLocation);
      boolean canAttack = activeUnit.canAttack(enemyUnit);

      if (activeUnit.isDirect() && validMovePath && hasEnemyUnit && canAttack) {
        cursorController.activateCursor("ATTACK");
      } else {
        cursorController.activateCursor("SELECT");
      }
    }
  }

}
