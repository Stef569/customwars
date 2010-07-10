package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

public class StartFlareAction extends DirectAction {
  private InGameContext inGameContext;
  private MapRenderer mapRenderer;
  private CursorController cursorController;
  private Game game;

  public StartFlareAction() {
    super("Prepare to flare");
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
    this.cursorController = inGameContext.getObj(CursorController.class);
    this.game = inGameContext.getObj(Game.class);
  }

  @Override
  protected void invokeAction() {
    Unit activeUnit = game.getActiveUnit();
    game.getMap().buildFlareZone(activeUnit);
    mapRenderer.showAttackZone();
    cursorController.activateCursor("SILO");
    cursorController.moveCursor(activeUnit.getAttackZone().get(0));
    inGameContext.setInputMode(InGameContext.INPUT_MODE.UNIT_FLARE);
  }

  @Override
  public void undo() {
    mapRenderer.removeZones();
    cursorController.activateCursor("SELECT");
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
  }
}
