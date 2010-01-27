package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * The unit attacks another Unit
 *
 * @author stefan
 */
public class AttackUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(AttackUnitAction.class);
  private InGameContext context;
  private ControllerManager controllerManager;
  private final Fight unitFight;
  private final Unit attacker, defender;
  private int damagePercentage, attackerHPPreFight, defenderHPPreFight;

  /**
   * @param attacker The unit that is attacking
   * @param defender The Unit that is under attack
   */
  public AttackUnitAction(Unit attacker, Unit defender) {
    super("Attack", false);
    this.attacker = attacker;
    this.defender = defender;
    this.unitFight = new UnitFight();
  }

  protected void init(InGameContext context) {
    this.context = context;
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    if (!context.isTrapped()) {
      attackUnit();
    }
  }

  public void attackUnit() {
    unitFight.initFight(attacker, defender);
    gatherPreFightStats();
    unitFight.startFight();
    logFightStatistics();

    if (attacker.isDestroyed()) {
      controllerManager.removeUnitController(attacker);
      SFX.playSound("explode");
    }
    if (defender.isDestroyed()) {
      controllerManager.removeUnitController(defender);
      SFX.playSound("explode");
    }
  }

  private void gatherPreFightStats() {
    damagePercentage = unitFight.getAttackDamagePercentage();
    attackerHPPreFight = attacker.getInternalHp();
    defenderHPPreFight = defender.getInternalHp();
  }

  private void logFightStatistics() {
    String attackerHP = attackerHPPreFight + "/" + attacker.getInternalMaxHp();
    String defenderHP = defenderHPPreFight + "/" + defender.getInternalMaxHp();

    logger.debug(String.format("%s(%s) is attacking %s(%s) dmg:%s%% Outcome: attacker(%s) defender(%s)",
      attacker.getStats().getName(), attackerHP, defender.getStats().getName(), defenderHP,
      damagePercentage, attacker.getInternalHp(), defender.getInternalHp()));
  }
}