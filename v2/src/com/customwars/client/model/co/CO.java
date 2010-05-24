package com.customwars.client.model.co;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.GameRenderer;

import java.io.Serializable;

/**
 * Defines a commanding officer
 */
public interface CO extends Serializable {

  boolean canDoPower();

  void power(Game game, GameRenderer gameRenderer);

  void deActivatePower();

  boolean canDoSuperPower();

  void superPower(Game game, GameRenderer gameRenderer);

  void deActivateSuperPower();

  void dayStart();

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

  String getName();

  COStyle getStyle();

  public String getBio();

  public String getTitle();

  public String getHit();

  public String getMiss();

  public String getSkill();

  public Power getPower();

  public Power getSuperpower();

  public String[] getIntel();

  public String[] getQuotes();

  public String[] getVictory();

  public String[] getDefeat();

  public boolean isInCOZone(Location zoneCenter, Location otherLocation);

  public void chargePowerGauge(double chargeRate);

  void resetPowerGauge();

  int getBars();

  int getMaxBars();
}
