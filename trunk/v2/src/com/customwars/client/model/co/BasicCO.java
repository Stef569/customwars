package com.customwars.client.model.co;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.GameRenderer;

/**
 * This CO has hooks called from within the game. It returns default or when applicable the parameter value.
 * It doesn't modify the game in any way.
 * To add functionality other CO's should extend this class and overwrite the functions of interest.
 */
public class BasicCO extends AbstractCO {

  public BasicCO(String name) {
    super(name);
  }

  /**
   * Create a CO with all required values
   */
  public BasicCO(String name, COStyle style, String bio, String title, int coZone,
                 String hit, String miss, String skill,
                 Power power, Power superPower,
                 String[] intel, String[] defeat, String[] victory, String[] quotes) {
    super(name, style, bio, title, coZone,
      hit, miss, skill, power, superPower, intel, defeat, victory, quotes);
  }

  public BasicCO(AbstractCO co) {
    super(co);
  }

  // ------------------------------------------------
  // These methods are hooks within the model
  // They are called every time the action is made
  // By Default the parameter is returned, Co's can overwrite these functions and return
  // a multiplier, +1, whatever. The Idea is to keep co specific stuff inside the co classes
  // to prevent a mess.
  // ------------------------------------------------

  @Override
  public int unitMovementHook(Unit mover, int movement) {
    return movement;
  }

  @Override
  public void power(Game game, GameRenderer gameRenderer) {
  }

  @Override
  public void deActivatePower() {
  }

  @Override
  public void superPower(Game game, GameRenderer gameRenderer) {
  }

  @Override
  public void deActivateSuperPower() {
  }

  @Override
  public void dayStart() {
  }

  public int getAttackBonusPercentage(Unit attacker, Unit defender) {
    return 100;
  }

  @Override
  public int getDefenseBonusPercentage(Unit attacker, Unit defender) {
    return 100;
  }

  public int captureRateHook(int captureRate) {
    return captureRate;
  }

  @Override
  public int cityFundsHook(int funds) {
    return funds;
  }

  @Override
  public int unitPriceHook(int price) {
    return price;
  }

  @Override
  public int healRateHook(int healRate) {
    return healRate;
  }

  @Override
  public int terrainDefenseHook(int terrainDefenseBonus) {
    return terrainDefenseBonus;
  }

  @Override
  public int fireRangeHook(int fireRange) {
    return fireRange;
  }

  @Override
  public int visionHook(int vision) {
    return vision;
  }
}