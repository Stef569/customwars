package com.customwars.client.action;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.renderer.MapRenderer;

/**
 * Moves the active unit to the 2nd tile in the inGameSession
 *
 * @author stefan
 */
public class MoveAnimatedAction extends DelayedAction {
  private static final int MOVE_DELAY = 500;
  private InGameSession inGameSession;
  private MoveTraverse moveTraverse;
  private Game game;
  private MapRenderer mapRenderer;

  public MoveAnimatedAction(Game game, MapRenderer mapRenderer, MoveTraverse moveTraverse, InGameSession inGameSession) {
    super("Move Animated", MOVE_DELAY);
    this.game = game;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
    this.moveTraverse = moveTraverse;
  }

  public void init() {
    Unit activeUnit = game.getActiveUnit();
    Location to = inGameSession.getClick(2);
    inGameSession.setMoving(true);

    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setCursorLocked(true);
    activeUnit.setUnitState(UnitState.MOVING);

    moveTraverse.prepareMove(activeUnit, to);
  }

  /**
   * This method is called by the timer everytime the MOVE_DELAY has passed
   */
  public void doActionImpl() {
    moveTraverse.update();

    if (moveTraverse.isPathMoveComplete()) {
      pathMoveComplete();
      actionCompleted = true;
    }
  }

  private void pathMoveComplete() {
    inGameSession.setTrapped(moveTraverse.foundTrapper());
    Unit activeUnit = game.getActiveUnit();
    activeUnit.setUnitState(UnitState.IDLE);
    mapRenderer.setCursorLocked(false);
  }

  void undoAction() {
    Location from = inGameSession.getClick(2);
    Location to = inGameSession.getClick(1);

    Unit activeUnit = game.getActiveUnit();
    activeUnit.setOrientation(Unit.DEFAULT_ORIENTATION);
    game.getMap().teleport(from, to, activeUnit);
    game.setActiveUnit(null);
  }
}
