package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Select the last unit on selectTile and make it the active unit in the game. This is the first action
 * for a unit.
 *
 * @author stefan
 */
public class SelectAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private CursorController cursorControl;
  private Game game;
  private Location selectTile;

  public SelectAction(Location selectTile) {
    super("Select");
    this.selectTile = selectTile;
  }

  public void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
    this.mapRenderer = context.getMapRenderer();
    this.cursorControl = context.getCursorController();
  }

  protected void invokeAction() {
    Locatable locatable = selectTile.getLastLocatable();
    selectUnit((Unit) locatable);
    SFX.playSound("select");
    context.setInputMode(InGameContext.INPUT_MODE.UNIT_SELECT);
  }

  private void selectUnit(Unit unit) {
    game.setActiveUnit(unit);
    mapRenderer.setActiveUnit(unit);
    mapRenderer.removeZones();
    mapRenderer.showMoveZone();
    mapRenderer.showArrows(true);
  }

  public void undo() {
    deselectActiveUnit();
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
  }

  private void deselectActiveUnit() {
    game.setActiveUnit(null);
    mapRenderer.setActiveUnit(null);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    cursorControl.moveCursor(selectTile);
  }
}