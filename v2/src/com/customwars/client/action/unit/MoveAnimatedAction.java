package com.customwars.client.action.unit;

import com.customwars.client.action.DelayedAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Moves the active unit to the 2nd tile in the inGameSession
 *
 * @author stefan
 */
public class MoveAnimatedAction extends DelayedAction {
  private static final int MOVE_DELAY = 150;
  private InGameSession inGameSession;
  private MoveTraverse moveTraverse;
  private Game game;
  private MapRenderer mapRenderer;

  public MoveAnimatedAction(Game game, MapRenderer mapRenderer, MoveTraverse moveTraverse, InGameSession inGameSession) {
    super("Move Animated", true, MOVE_DELAY);
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
    moveTraverse.prepareMove(activeUnit, to);
  }

  /**
   * This method is called by the timer everytime the MOVE_DELAY has passed
   */
  public void doActionImpl() {
    moveTraverse.update();

    if (moveTraverse.isPathMoveComplete()) {
      pathMoveComplete();
      setActionCompleted(true);
    }
  }

  private void pathMoveComplete() {
    inGameSession.setTrapped(moveTraverse.foundTrapper());
    mapRenderer.setCursorLocked(false);
  }

  public void undoAction() {
    Location from = inGameSession.getClick(2);
    Location to = inGameSession.getClick(1);

    Unit activeUnit = game.getActiveUnit();
    activeUnit.setOrientation(Unit.DEFAULT_ORIENTATION);
    game.getMap().teleport(from, to, activeUnit);
    game.setActiveUnit(null);
  }
}
