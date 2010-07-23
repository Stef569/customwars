package com.customwars.client.model.fight;

import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Creates a Fight class based on the defender on the defender location.
 */
public class FightFactory {

  public static Fight createFight(Map<Tile> map, Attacker attacker, Location defenderLocation) {
    boolean hasUnit = map.getUnitOn(defenderLocation) != null;
    boolean hasCity = map.getCityOn(defenderLocation) != null;

    Fight fight;
    Defender defender;

    if (hasUnit) {
      defender = map.getUnitOn(defenderLocation);
      fight = new UnitFight(map, attacker, defender);
    } else if (hasCity) {
      defender = map.getCityOn(defenderLocation);
      fight = new UnitVsCityFight(attacker, defender);
    } else {
      throw new IllegalArgumentException("Can't create Fight for defender on " + defenderLocation);
    }

    return fight;
  }
}
