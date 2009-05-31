package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.ui.state.InGameContext;

/**
 * Attack defender with attacker
 *
 * @author stefan
 */
public class AttackAction extends DirectAction {
  private InGameContext context;
  private ControllerManager controllerManager;
  private UnitFight unitFight;
  private Unit attacker, defender;

  public AttackAction(Unit attacker, Unit defender) {
    super("Attack", false);
    this.attacker = attacker;
    this.defender = defender;
  }

  protected void init(InGameContext context) {
    this.context = context;
    unitFight = new UnitFight();
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    if (context.isTrapped()) return;
    attackUnit(attacker, defender);
  }

  /**
   * @param attacker The unit that is attacking
   * @param defender The Unit that is under attack
   */
  public void attackUnit(Unit attacker, Unit defender) {
    unitFight.initFight(attacker, defender);
    unitFight.startFight();

    if (attacker.isDestroyed()) {
      controllerManager.removeUnitController(attacker);
      context.playSound("explode");
    }
    if (defender.isDestroyed()) {
      controllerManager.removeUnitController(defender);
      context.playSound("explode");
    }
  }
}