package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * The unit joins the target
 *
 * @author stefan
 */
public class JoinAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(JoinAction.class);
  private ControllerManager controllerManager;
  private final Unit target, unit;
  private InGameContext context;

  public JoinAction(Unit unit, Unit target) {
    super("Join", false);
    this.unit = unit;
    this.target = target;
  }

  protected void init(InGameContext context) {
    this.context = context;
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    if (!context.isTrapped()) {
      join();
    }
  }

  private void join() {
    logger.debug("Join unit " + unit + " with " + target);

    //Add Money if joining cause the target to go over max HP
    int excessHP = unit.getHp() + target.getHp() - target.getMaxHp();

    if (excessHP > 0) {
      target.getOwner().addToBudget((excessHP * unit.getPrice()) / unit.getMaxHp());
    }

    // add HP, supplies, ammo to target
    target.addHp(unit.getHp());
    target.addSupplies(unit.getSupplies());

    if (unit.getAvailableWeapon() != null) {
      target.addAmmo(unit.getAvailableWeapon().getAmmo());
    }

    target.setUnitState(unit.getUnitState());
    unit.destroy(false);
    controllerManager.removeUnitController(unit);
  }
}
