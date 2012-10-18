package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.gui.GUIContext;

/**
 * Reset everything in the in game state to default.
 */
public class ClearInGameStateAction extends DirectAction {
  private InGameContext inGameContext;
  private MapRenderer mapRenderer;
  private Game game;
  private HUD hud;
  private CursorController cursorControl;

  public ClearInGameStateAction() {
    super("Clear in game state", false);
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.game = inGameContext.getObj(Game.class);
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
    this.hud = inGameContext.getObj(HUD.class);
    this.cursorControl = inGameContext.getObj(CursorController.class);
  }

  protected void invokeAction() {
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    inGameContext.clearUndoHistory();
    inGameContext.clearClickHistory();
    inGameContext.setTrapped(false);
    inGameContext.setLaunchingUnit(false);
    inGameContext.clearDropHistory();
    inGameContext.getObj(GUIContext.class).getInput().resume();

    Location previousCursorLocation = mapRenderer.getCursorLocation();
    cursorControl.activateCursor("Select");
    cursorControl.moveCursor(previousCursorLocation);
    cursorControl.stopCursorTraversal();
    cursorControl.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setActiveUnit(null);
    mapRenderer.setDropLocations(null, null);

    hud.hidePopup();

    // Avoid unnecessary events to be fired
    if (game.getActiveUnit() != null) {
      game.setActiveUnit(null);
    }
  }
}
