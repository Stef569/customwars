package com.customwars.client.model.fight;

import com.customwars.client.model.map.TileMap;
import com.customwars.client.tools.Args;

/**
 * Allows an Attacker to attack a Defender
 * When canCounterAttack returns true the defender will counterAttack the attacker
 * An attack - counter attack sequence can only be performed once.
 */
public abstract class BasicFight implements Fight {
  protected Attacker attacker;
  protected Defender defender;
  private boolean fightComplete;

  protected BasicFight(Attacker attacker, Defender defender) {
    init(attacker, defender);
  }

  /**
   * Start the attack - counter attack sequence
   */
  public void startFight() {
    fightComplete = false;
    startFightSequence(attacker, defender);
  }

  private void init(Attacker attacker, Defender defender) {
    Args.checkForNull(attacker);
    Args.checkForNull(defender);
    this.attacker = attacker;
    this.defender = defender;
  }

  private void startFightSequence(Attacker attacker, Defender defender) {
    if (canAttack()) {
      attacker.attack(defender, this);

      if (canSwapAttackerAndDefender() && defender.canCounterAttack(attacker)) {
        swapAttackerAndDefender();

        if (canCounterAttack()) {
          counterAttack();
        }

        swapAttackerAndDefender();
      }
    }
  }

  protected boolean canAttack() {
    return attacker.canAttack(defender);
  }

  protected boolean canCounterAttack() {
    return !fightComplete && !isSuicidalCounterAttack() &&
      TileMap.isAdjacent(attacker.getLocation(), defender.getLocation());
  }

  private boolean canSwapAttackerAndDefender() {
    return defender instanceof Attacker && attacker instanceof Defender;
  }

  public boolean isSuicidalCounterAttack() {
    return getAttackDamagePercentage() == 0;
  }

  private void counterAttack() {
    fightComplete = true;
    startFightSequence(attacker, defender);
  }

  /**
   * Pre: canSwapAttackerAndDefender() returns true
   */
  private void swapAttackerAndDefender() {
    Attacker attackerCopy = attacker;
    attacker = (Attacker) defender;
    defender = (Defender) attackerCopy;
  }
}
