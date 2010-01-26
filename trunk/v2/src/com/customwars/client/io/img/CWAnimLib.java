package com.customwars.client.io.img;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import org.newdawn.slick.Animation;

import java.awt.Color;

/**
 * CW specific Animation getter functions
 * This class hides how unit/city Animations are retrieved from the AnimLib
 */
public class CWAnimLib {
  private final AnimLib animLib;

  public CWAnimLib(AnimLib animLib) {
    this.animLib = animLib;
  }

  public Animation getCityAnim(City city, Color color) {
    return animLib.getCityAnim(city.getImgRowID(), color, "");
  }

  /**
   * Get the inactive city animation for a special neutral city or null.
   */
  public Animation getInActiveCityAnim(City city, Color color) {
    return city.isSpecialNeutralCity() ? animLib.getCityAnim(city.getImgRowID(), color, AnimLib.ANIM_INACTIVE) : null;
  }

  /**
   * Get a single unit animation that is looking in the given direction.
   * Supported directions(N,E,S,W) all other directions will throw an IllegalArgumentException
   */
  public Animation getUnitAnim(Unit unit, Color color, Direction direction) {
    int unitID = unit.getStats().getImgRowID();

    switch (direction) {
      case NORTH:
        return animLib.getUnitAnim(unitID, color, AnimLib.ANIM_UP);
      case EAST:
        return animLib.getUnitAnim(unitID, color, AnimLib.ANIM_RIGHT);
      case SOUTH:
        return animLib.getUnitAnim(unitID, color, AnimLib.ANIM_DOWN);
      case WEST:
        return animLib.getUnitAnim(unitID, color, AnimLib.ANIM_LEFT);
      default:
        throw new IllegalArgumentException("Direction " + direction + " is not supported for a unit animation");
    }
  }

  public Animation getInactiveUnitAnim(Unit unit, Color color) {
    return animLib.getUnitAnim(unit.getStats().getImgRowID(), color, AnimLib.ANIM_INACTIVE);
  }
}
