package com.customwars.client.action;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.MapRenderer;

/**
 * The unit is made Inactive(can no longer be controlled)
 * This is the last action for a unit, when the action has been completed
 * reset everything back to default.
 *
 * @author stefan
 */
public class UnitWaitAction extends CWAction {
  private Game game;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;

  public UnitWaitAction(Game game, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Wait", false);
    this.game = game;
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
  }

  public void doActionImpl() {
    wait(game.getActiveUnit());
    inGameSession.discartAllEdits();
    initDefaultMode();
  }

  private void wait(Unit unit) {
    game.getMap().resetFogMap(unit.getOwner());
    game.initZones();
    game.setActiveUnit(null);
    unit.setState(GameObjectState.IDLE);
  }

  /**
   * Init selection mode
   */
  private void initDefaultMode() {
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
    inGameSession.clearClicks();
    mapRenderer.activateCursor("Select");
    //mapRenderer.stopTileSpriteTraversal();
    mapRenderer.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    inGameSession.setMoving(false);
  }

  void undoAction() {
  }
}
