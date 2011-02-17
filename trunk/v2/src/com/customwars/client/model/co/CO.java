package com.customwars.client.model.co;

import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.GameRenderer;

import java.io.Serializable;

/**
 * Defines a commanding officer
 */
public interface CO extends Serializable {

  void power(Game game, GameRenderer gameRenderer);

  void deActivatePower();

  void superPower(Game game, GameRenderer gameRenderer);

  void deActivateSuperPower();

  void dayStart(Game game);

  int getAttackBonusPercentage(Unit attacker, Unit defender);

  int getDefenseBonusPercentage(Unit attacker, Unit defender);

  int unitMovementHook(Unit mover, int movement);

  int captureRateHook(int captureRate);

  int cityFundsHook(int funds);

  int unitPriceHook(int price);

  int healRateHook(int healRate);

  int terrainDefenseHook(int terrainDefenseBonus);

  int fireRangeHook(int fireRange);

  int visionHook(int vision);

  void unitAttackedHook(Unit attacker, Defender defender);

  String getName();

  COStyle getStyle();

  /**
   * @return The condensed biography
   */
  String getBio();

  String getTitle();

  /**
   * @return What this CO likes
   */
  String getHit();

  /**
   * @return What this CO dislikes
   */
  String getMiss();

  /**
   * @return Describes what the speciality of this CO is.
   */
  String getSkill();

  /**
   * @return Intelligence to the point what advantage(s) does this co have.
   */
  String getIntel();

  boolean canDoPower();

  boolean isPowerActive();

  String getPowerDescription();

  boolean canDoSuperPower();

  boolean isSuperPowerActive();

  String getSuperPowerDescription();

  /**
   * Is the location within the co zone of the given unit
   *
   * @param unit     The unit that contains the CO
   * @param location The location to apply the range check to
   * @return Is the location included in the CO Zone of the given unit
   */
  public boolean isInCOZone(Unit unit, Location location);

  /**
   * Increase the power gauge. When the power gauge is already full nothing happens.
   *
   * @param chargeRate The rate for increasing the power gauge in the range of [0..1]
   */
  public void chargePowerGauge(double chargeRate);

  /**
   * Reset the power gauge back to it's initial state.
   */
  void resetPowerGauge();

  /**
   * @return The number of bars that are active in the power gauge
   */
  int getBars();

  /**
   * @return The maximum number of bars that can be active
   */
  int getMaxBars();

  /**
   * @return a collection of quotes
   */
  String[] getQuotes();

  /**
   * @return a collection of possible victory strings
   */
  String[] getVictory();

  /**
   * @return a collection of possible defeat strings
   */
  String[] getDefeat();

  /**
   * @return The distance in tiles around a unit that covers the co zone..
   */
  int getZoneRange();
}
