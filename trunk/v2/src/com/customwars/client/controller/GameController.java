package com.customwars.client.controller;

import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.GUIContext;

/**
 * Handles input in the in game state
 */
public class GameController {
  private static final Logger logger = Logger.getLogger(GameController.class);
  private GameRenderer gameRenderer;
  private CursorController cursorController;
  private InGameContext context;
  private GUIContext guiContext;
  private Game game;
  private StateChanger stateChanger;

  public GameController(Game game, GameRenderer gameRenderer, SpriteManager spriteManager) {
    this.game = game;
    this.gameRenderer = gameRenderer;
    this.cursorController = new CursorController(game.getMap(), spriteManager);
  }

  public CursorController getCursorController() {
    return cursorController;
  }

  public void undo() {
    SFX.playSound("cancel");
    context.undo();
  }

  public void handleA(Unit unit, City city, Tile cursorLocation) {
    if (unit != null && unit.isActive() || context.isInUnitMode()) {
      context.handleUnitAPress(unit);
    } else if (!cursorLocation.isFogged() && cursorLocation.getLocatableCount() == 0 &&
      city != null && city.getOwner() == game.getActivePlayer() && city.canBuild()) {
      context.handleCityAPress(city);
    } else if (context.isDefaultMode()) {
      new ClearInGameStateAction().invoke(context);
      ShowPopupMenuAction showContextMenuAction = buildContextMenu();
      showContextMenuAction.setLocation(cursorLocation);
      context.doAction(showContextMenuAction);
    } else {
      logger.warn("could not handle A press");
    }
  }

  private ShowPopupMenuAction buildContextMenu() {
    ShowPopupMenuAction showContextMenuAction = new ShowPopupMenuAction("Context menu");
    MenuItem endTurnMenuItem = new MenuItem("End turn", guiContext);
    showContextMenuAction.addAction(ActionFactory.buildEndTurnAction(stateChanger), endTurnMenuItem);
    return showContextMenuAction;
  }

  public void handleB(Unit activeUnit, Unit selectedUnit) {
    if (selectedUnit != null) {
      context.handleUnitBPress(selectedUnit);
    }
  }

  public void setInGameContext(InGameContext inGameContext) {
    this.context = inGameContext;
    this.guiContext = inGameContext.getContainer();
  }

  public void setStateChanger(StateChanger stateChanger) {
    this.stateChanger = stateChanger;
  }

  public void endTurn(StateChanger stateChanger) {
    CWAction endTurn = ActionFactory.buildEndTurnAction(stateChanger);
    context.doAction(endTurn);
  }
}
