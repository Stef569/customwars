package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Attack defender with attacker
 *
 * @author stefan
 */
public class AttackAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(AttackAction.class);
  private InGameContext context;
  private ControllerManager controllerManager;
  private UnitFight unitFight;
  private final Unit attacker, defender;

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
    if (!context.isTrapped()) {
      attackUnit(attacker, defender);
    }
  }

  /**
   * @param attacker The unit that is attacking
   * @param defender The Unit that is under attack
   */
  public void attackUnit(Unit attacker, Unit defender) {
    unitFight.initFight(attacker, defender);

    // Gather debugging data before the fight starts
    int damagePercentage = unitFight.getAttackDamagePercentage();
    int attackerHPPreFight = attacker.getInternalHp();
    int defenderHPPreFight = defender.getInternalHp();

    unitFight.startFight();
    logFightStatistics(attackerHPPreFight, defenderHPPreFight, damagePercentage);

    if (attacker.isDestroyed()) {
      controllerManager.removeUnitController(attacker);
      SFX.playSound("explode");
    }
    if (defender.isDestroyed()) {
      controllerManager.removeUnitController(defender);
      SFX.playSound("explode");
    }
  }

  private void logFightStatistics(int attackerHPPreFight, int defenderHPPreFight, int damagePercentage) {
    String attackerHP = attackerHPPreFight + "/" + attacker.getInternalMaxHp();
    String defenderHP = defenderHPPreFight + "/" + defender.getInternalMaxHp();

    logger.debug(String.format("%s(%s) is attacking %s(%s) dmg:%s%% Outcome: attacker(%s) defender(%s)",
      attacker.getStats().getName(), attackerHP, defender.getStats().getName(), defenderHP,
      damagePercentage, attacker.getInternalHp(), defender.getInternalHp()));
  }
}