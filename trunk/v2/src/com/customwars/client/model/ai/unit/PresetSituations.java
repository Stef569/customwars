package com.customwars.client.model.ai.unit;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.map.Map;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Check for preset situations and makes sure that the AI will do the right thing.
 * For example when our hq is being captured, make sure that 1 of our units attack the capturing unit.
 *
 * This is achieved by assigning a unit to an order. Bypassing any order of executing.
 */
public class PresetSituations {
  private final Game game;
  private final Map map;
  private final java.util.Map<Unit, Fuz.UNIT_ORDER> presets;

  public PresetSituations(Game game) {
    this.game = game;
    this.map = game.getMap();
    this.presets = new HashMap<Unit, Fuz.UNIT_ORDER>();
  }

  public java.util.Map<Unit, Fuz.UNIT_ORDER> think() {
    checkOurHQDefense();
    return Collections.unmodifiableMap(presets);
  }

  private void checkOurHQDefense() {
    // An enemy on our HQ is a higher priority.
    City hq = game.getActivePlayer().getHq();

    if (isHqBeingCaptured(hq)) {
      Unit unit = findBestAttackerAgainstUnitOnHQ(map);
      presets.put(unit, Fuz.UNIT_ORDER.ATTACK_UNIT);
    }
  }

  private boolean isHqBeingCaptured(City city) {
    return city != null && city.isHQ() && city.isBeingCaptured();
  }

  private Unit findBestAttackerAgainstUnitOnHQ(Map map) {
    City hq = game.getActivePlayer().getHq();
    Unit enemy = map.getUnitOn(hq.getLocation());
    // Find the unit that can do the most damage against the unit on the hq
    return findBestUnitAgainstEnemyOnHQ(map, hq, enemy);
  }

  private Unit findBestUnitAgainstEnemyOnHQ(Map map, City hq, Unit enemy) {
    int highestDamage = 0;
    Unit bestAttacker = null;
    Unit capturingUnit = map.getUnitOn(hq.getLocation());

    for (Unit myUnit : game.getActivePlayer().getArmy()) {
      if (!myUnit.isInTransport()) {
        List<Defender> enemies = map.getEnemiesInRangeOfIndirect(myUnit);

        if (enemies.contains(capturingUnit)) {
          UnitFight fight = new UnitFight(map, myUnit, enemy);
          int attackDamage = fight.getAttackDamagePercentage();

          if (attackDamage > highestDamage) {
            highestDamage = attackDamage;
            bestAttacker = myUnit;
          }
        }
      }
    }

    return bestAttacker;
  }

  public boolean hasPresetFor(Unit unit) {
    return presets.containsKey(unit);
  }

  public Fuz.UNIT_ORDER getUnitOrder(Unit unit) {
    return presets.get(unit);
  }
}
