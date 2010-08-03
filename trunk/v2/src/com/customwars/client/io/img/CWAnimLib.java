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

  /**
   * Get a city animation. Colored if the city can be captured. Neutral if not.
   */
  public Animation getCityAnim(City city, Color color) {
    return city.canBeCaptured() ?
      animLib.getCityAnim(city.getImgRowID(), color, "") :
      animLib.getCityAnim(city.getImgRowID() + AnimLib.NEUTRAL_CITY_OFFSET, color, "");
  }

  /**
   * Get the inactive city animation for a city that cannot be captured. Inactive can have different meanings
   * depending on the city:
   * In case of a missile silo an empty launch platform is returned.
   * In case of a pipe seam a destroyed pipe seam is returned.
   * if the city can be captured null is returned.
   */
  public Animation getInActiveCityAnim(City city, Color color) {
    return city.canBeCaptured() ? null :
      animLib.getCityAnim(city.getImgRowID() + AnimLib.NEUTRAL_CITY_OFFSET, color, AnimLib.ANIM_INACTIVE);
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

  /**
   * Get a darker version of a unit animation
   */
  public Animation getInactiveUnitAnim(Unit unit, Color color) {
    return animLib.getUnitAnim(unit.getStats().getImgRowID(), color, AnimLib.ANIM_INACTIVE);
  }
}
