package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

/**
 * The unit joins the target
 *
 * @author stefan
 */
public class JoinAction extends DirectAction {
  private Game game;
  private ControllerManager controllerManager;
  private Unit target, unit;

  public JoinAction(Unit unit, Unit target) {
    super("Join", false);
    this.unit = unit;
    this.target = target;
  }

  protected void init(InGameContext context) {
    game = context.getGame();
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    //Add Money if joining cause the target to go over max HP
    int excessHP = unit.getHp() + target.getHp() - target.getMaxHp();

    if (excessHP > 0)
      game.getActivePlayer().addToBudget((excessHP * unit.getPrice()) / unit.getMaxHp());

    // add HP, supplies, ammo to target
    target.addHp(unit.getHp());
    target.addSupplies(unit.getSupplies());
    target.addAmmo(unit.getAvailableWeapon().getAmmo());

    // todo make the dived state of the unit being moved onto the same as the one moving
    //    target.setDived(activeUnit.isDived());

    // Remove Unit manually, unit.destroy() will trigger an explosion.
    unit.getOwner().removeUnit(unit);
    unit.getLocation().remove(unit);
    unit.setLocation(null);
    controllerManager.removeUnitController(unit);
  }
}
