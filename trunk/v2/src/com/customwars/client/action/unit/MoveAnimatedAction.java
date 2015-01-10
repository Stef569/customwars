package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DelayedAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.GameController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Moves the unit animated from the 'from' location to the 'to' location
 * by moving the unit on each tile between those locations.
 */
public class MoveAnimatedAction extends DelayedAction {
  private static final Logger logger = Logger.getLogger(MoveAnimatedAction.class);
  private CursorController cursorControl;
  private GameController gameController;
  private MessageSender messageSender;
  private boolean alreadyRevealed;
  InGameContext inGameContext;
  MoveTraverse moveTraverse;
  Game game;
  Map map;
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

  protected void init(InGameContext inGameContext) {
    if (inGameContext.isTrapped()) {
      setActionCompleted(true);
      return;
    }

    logger.debug(String.format("Moving Animated from %s to %s",
      from.getLocationString(), to.getLocationString()));

    // Reset unit state, if this unit was capturing a city
    if (unit.getUnitState() == UnitState.CAPTURING) {
      unit.setUnitState(UnitState.IDLE);
    }

    this.inGameContext = inGameContext;
    game = inGameContext.getObj(Game.class);
    map = game.getMap();
    moveTraverse = inGameContext.getObj(MoveTraverse.class);
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
    cursorControl = inGameContext.getObj(CursorController.class);
    cursorControl.setCursorLocked(true);

    MapRenderer mapRenderer = inGameContext.getObj(MapRenderer.class);
    mapRenderer.removeZones();
    prepareMove(mapRenderer);
    mapRenderer.showArrows(false);
  }

  private void prepareMove(MapRenderer mapRenderer) {
    List<Location> movePath = mapRenderer.getUnitMovePath();

    // In case of a drop the move path will be empty
    if (movePath.isEmpty()) {
      // Generate a path
      moveTraverse.prepareMove(unit, to);
    } else {
      // Use the user chosen path
      moveTraverse.prepareMove(unit, movePath);
    }
  }

  protected void invokeAction() {
    if (moveTraverse.isPathMoveComplete()) {
      pathMoveComplete();
      setActionCompleted(true);
    } else {
      moveTraverse.update();

      if (inGameContext.isLaunchingUnit()) {
        revealUnit();
      }
    }
  }

  void pathMoveComplete() {
    if (moveTraverse.foundTrapper()) {
      SFX.playSound("trapped");
      inGameContext.setTrapped(true);
    } else {
      inGameContext.setTrapped(false);
    }

    cursorControl.setCursorLocked(false);
    if (App.isMultiplayer()) sendMove();
  }

  private void revealUnit() {
    // At first the from tile contains 2 units the plane and the carrier.
    // Only the plane sprite is visible, if the plane moves then we need to reveal the carrier.
    if (!alreadyRevealed && !from.contains(unit)) {
      revealCarrier(from);
    }
  }

  private void revealCarrier(Location carrierLocation) {
    // Remove and add the carrier to the tile
    // This triggers a show unit event and will reveal the carrier sprite.
    Locatable carrier = carrierLocation.getLastLocatable();
    carrierLocation.remove(carrier);
    carrierLocation.add(carrier);
    logger.debug("Revealed " + carrier);
    alreadyRevealed = true;
  }

  public void undo() {
    unit.setDefaultOrientation();
    gameController.teleport(from, to);
    game.setActiveUnit(null);
  }

  private void sendMove() {
    try {
      messageSender.teleport(from, unit.getLocation());
    } catch (NetworkException ex) {
      logger.warn("Could not send move unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendMove();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(from).add(to).build();
  }
}
