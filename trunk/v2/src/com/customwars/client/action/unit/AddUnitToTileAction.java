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
 * if there is a cost to deploy this unit then
 * subtract the unit cost from the unit owner
 */
public class AddUnitToTileAction extends DirectAction {
  private Game game;
  private ControllerManager controllerManager;
  private Unit unit;                    // Unit to be put on tile
  private Tile tile;                    // Tile to add unit to
  private boolean noCostDeployement;    // Does it cost the newOwner to put the unit on the tile
  private Player unitOwner;

  public AddUnitToTileAction(Unit unit, Tile tile, boolean noCostDeployement, boolean canUndo) {
    super("Add unit to tile", canUndo);
    this.unit = unit;
    this.tile = tile;
    this.unitOwner = unit.getOwner();
    this.noCostDeployement = noCostDeployement;
  }

  protected void init(InGameContext context) {
    game = context.getGame();
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    if (!noCostDeployement) {
      unitOwner.addToBudget(-unit.getPrice());
    }
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