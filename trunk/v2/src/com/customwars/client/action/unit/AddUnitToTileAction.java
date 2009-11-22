package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

/**
 * Add unit to tile
 * subtract the unit cost from the unit owner
 */
public class AddUnitToTileAction extends DirectAction {
  private Game game;
  private ControllerManager controllerManager;
  private Unit unit;                    // Unit to be put on tile
  private Tile tile;                    // Tile to add unit to
  private Player unitOwner;

  public AddUnitToTileAction(Unit unit, Tile tile, boolean canUndo) {
    super("Add unit to tile", canUndo);
    this.unit = unit;
    this.tile = tile;
    this.unitOwner = unit.getOwner();
  }

  protected void init(InGameContext context) {
    game = context.getGame();
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    unitOwner.addToBudget(-unit.getStats().getPrice());
    unitOwner.addUnit(unit);

    tile.add(unit);

    game.getMap().buildMovementZone(unit);
    game.getMap().buildAttackZone(unit);

    if (unitOwner.isAi()) {
      controllerManager.addAIUnitController(unit);
    } else {
      controllerManager.addHumanUnitController(unit);
    }
  }
}