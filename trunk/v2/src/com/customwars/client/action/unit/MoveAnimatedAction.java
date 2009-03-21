package com.customwars.client.action.unit;

import com.customwars.client.action.DelayedAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Moves the unit from the 'from' location to the 'to' location
 *
 * @author stefan
 */
public class MoveAnimatedAction extends DelayedAction {
  private static final int MOVE_DELAY = 150;
  InGameContext context;
  MoveTraverse moveTraverse;
  Game game;
  private MapRenderer mapRenderer;
  Location from;
  Location to;
  Unit unit;

  public MoveAnimatedAction(Unit unit, Location from, Location to) {
    super("Move Animated", MOVE_DELAY);
    this.unit = unit;
    this.from = from;
    this.to = to;
  }

  public MoveAnimatedAction(Location from, Location to) {
    this((Unit) from.getLastLocatable(), from, to);
  }

  protected void init(InGameContext context) {
    if (context.isTrapped()) {
      setActionCompleted(true);
      return;
    }

    this.context = context;
    game = context.getGame();
    moveTraverse = context.getMoveTraverse();
    mapRenderer = context.getMapRenderer();
    context.setMoving(true);

    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setCursorLocked(true);
    moveTraverse.prepareMove(unit, to);
  }

  protected void invokeAction() {
    if (moveTraverse.isPathMoveComplete()) {
      pathMoveComplete();
      setActionCompleted(true);
    } else {
      moveTraverse.update();
    }
  }

  void pathMoveComplete() {
    context.setTrapped(moveTraverse.foundTrapper());
    mapRenderer.setCursorLocked(false);
  }

  public void undo() {
    unit.setOrientation(Unit.DEFAULT_ORIENTATION);
    game.getMap().teleport(from, to, unit);
    game.setActiveUnit(null);
  }
}
