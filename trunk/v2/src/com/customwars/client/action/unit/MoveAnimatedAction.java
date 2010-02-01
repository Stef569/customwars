package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.DelayedAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Moves the unit animated from the 'from' location to the 'to' location
 * by moving the unit on each tile between those locations.
 *
 * @author stefan
 */
public class MoveAnimatedAction extends DelayedAction {
  private static final Logger logger = Logger.getLogger(MoveAnimatedAction.class);
  InGameContext context;
  MoveTraverse moveTraverse;
  Game game;
  private CursorController cursorControl;
  Location from;
  Location to;
  Unit unit;

  public MoveAnimatedAction(Unit unit, Location from, Location to) {
    super("Move Animated", App.getInt("unit.movedelay"));
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

    logger.debug(String.format("Moving Animated from %s to %s",
      from.getLocationString(), to.getLocationString()));

    // Reset unit state, if this unit was capturing a city
    if (unit.getUnitState() == UnitState.CAPTURING) {
      unit.setUnitState(UnitState.IDLE);
    }

    this.context = context;
    game = context.getGame();
    moveTraverse = context.getMoveTraverse();
    cursorControl = context.getCursorController();
    cursorControl.setCursorLocked(true);

    MapRenderer mapRenderer = context.getMapRenderer();
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
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
    if (moveTraverse.foundTrapper()) {
      SFX.playSound("trapped");
      context.setTrapped(true);
    } else {
      context.setTrapped(false);
    }
    cursorControl.setCursorLocked(false);
  }

  public void undo() {
    unit.setOrientation(Unit.DEFAULT_ORIENTATION);
    game.getMap().teleport(from, to, unit);
    game.setActiveUnit(null);
  }
}
