package com.customwars.client.model.co;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
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

  int getAttackBonusPercentage(Unit attacker, Unit defender);

  int getDefenseBonusPercentage(Unit attacker, Unit defender);

  int unitMovementHook(Unit mover, int movement);

  int captureRateHook(int captureRate);

  int cityFundsHook(int funds);

  int unitPriceHook(int price);

  int healRateHook(int healRate);

  int terrainDefenseHook(int terrainDefenseBonus);

  String getName();

  CoStyle getStyle();

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
}
