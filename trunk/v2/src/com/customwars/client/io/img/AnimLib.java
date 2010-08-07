package com.customwars.client.io.img;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.UCaseMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Contains all the Animations, keyed by upper case name
 *
 * @author stefan
 */
public class AnimLib {
  /**
   * The start ID of neutral animations. The first neutral city anim has 101 as ID.
   * This allows to add both neutral cities and cities that can be captured without overlapping the ID's.
   */
  public static final int NEUTRAL_CITY_OFFSET = 100;
  public static final String ANIM_LEFT = "LEFT";
  public static final String ANIM_RIGHT = "RIGHT";
  public static final String ANIM_UP = "UP";
  public static final String ANIM_DOWN = "DOWN";
  public static final String ANIM_INACTIVE_CITY = "INACTIVE";
  public static final String ANIM_INACTIVE_UNIT_LEFT = "INACTIVE_LEFT";
  public static final String ANIM_INACTIVE_UNIT_RIGHT = "INACTIVE_RIGHT";
  private static final int NO_DURATION = 1;
  private final Map<String, Animation> animations;

  public AnimLib() {
    animations = new UCaseMap<Animation>();
  }

  public void addAnim(String animName, Animation anim) {
    if (anim.getFrameCount() > 0) {
      if (!isAnimLoaded(animName)) {
        animations.put(animName, anim);
      }
    }
  }

  public Animation getAnim(String animName) {
    if (!isAnimLoaded(animName)) {
      throw new IllegalArgumentException(
        "Animation cache does not contain " + animName + " " + animations.keySet());
    }
    return animations.get(animName);
  }

  boolean isAnimLoaded(String animName) {
    return animations.containsKey(animName);
  }

  public Collection<Animation> getAllAnimations() {
    return Collections.unmodifiableCollection(animations.values());
  }

  //----------------------------------------------------------------------------
  // City Animation
  //----------------------------------------------------------------------------

  public void createCityAnimations(Color baseColor, Color color, ResourceManager resources) {
    createArmyCityAnimations(baseColor, color, resources);
    createNeutralCityAnimations(resources);
  }

  private void createArmyCityAnimations(Color baseColor, Color color, ResourceManager resources) {
    SpriteSheet citySpriteSheet = resources.getCitySpriteSheet(color);
    boolean cityAnimationsOn = App.getBoolean("display.city.animate");

    // Read frame count and durations from the base animations
    Animation baseAnim = getCityBaseAnimation(baseColor);
    int duration = baseAnim.getDurations()[0];
    int totalFrames = baseAnim.getFrameCount();

    for (int row = 0; row < citySpriteSheet.getVerticalCount(); row++) {
      Animation animActive = createAnim(citySpriteSheet, 0, totalFrames, row, duration);
      animActive.setLooping(cityAnimationsOn);
      addCityAnim(row, color, "", animActive);
    }
  }

  /**
   * Create neutral city animations
   * The neutral city spritesheet image has 2 cols.
   * the first column contains the active city img and
   * the second column contains the inactive city img.
   * <p/>
   * For example a Missile silo would have a platform+rocket as active anim and
   * just a platform w/o rocket as inactive anim.
   */
  private void createNeutralCityAnimations(ResourceManager resources) {
    Color neutralColor = App.getColor("plugin.neutral_color");
    SpriteSheet neutralCitySpriteSheet = resources.getNeutralCitySpriteSheet();

    for (int row = 0; row < neutralCitySpriteSheet.getVerticalCount(); row++) {
      Animation animActive = createAnim(neutralCitySpriteSheet, 0, 1, row, NO_DURATION);
      animActive.setLooping(false);
      addCityAnim(row + NEUTRAL_CITY_OFFSET, neutralColor, "", animActive);

      Animation animInActive = createAnim(neutralCitySpriteSheet, 1, 2, row, NO_DURATION);
      animInActive.setLooping(false);
      addCityAnim(row + NEUTRAL_CITY_OFFSET, neutralColor, ANIM_INACTIVE_CITY, animInActive);
    }
  }

  private Animation getCityBaseAnimation(Color baseColor) {
    String cityAnimName = createCityAnimName(0, baseColor, "");
    return getAnim(cityAnimName);
  }

  private void addCityAnim(int cityID, Color color, String suffix, Animation cityAnim) {
    String cityAnimName = createCityAnimName(cityID, color, suffix);
    addAnim(cityAnimName, cityAnim);
  }

  public Animation getCityAnim(int cityID, Color color, String suffix) {
    String cityAnimName = createCityAnimName(cityID, color, suffix);
    return getAnim(cityAnimName);
  }

  private static String createCityAnimName(int cityID, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return StringUtil.hasContent(suffix) ?
      "city_" + cityID + "_" + colorName + "_" + suffix :
      "city_" + cityID + "_" + colorName;
  }

  //----------------------------------------------------------------------------
  // Unit Animation
  //----------------------------------------------------------------------------

  /**
   * Retrieve images from each row of the unitSpriteSheet and create animations out of them.
   *
   * @param baseColor base color for a unit
   * @param color     the color of the unitSpriteSheet, used to store the animations ie UNIT_0_BLUE
   * @param resources The resources Manager containing the unit SpriteSheets
   */
  public void createUnitAnimations(Color baseColor, Color color, ResourceManager resources) {
    SpriteSheet unitSpriteSheet = resources.getUnitSpriteSheet(color);
    SpriteSheet inactiveUnitSpriteSheet = resources.getShadedUnitSpriteSheet(color);

    // Read frame count and durations from the base animations
    Animation baseAnimLeft = getUnitBaseAnimation(baseColor, ANIM_LEFT);
    Animation baseAnimRight = getUnitBaseAnimation(baseColor, ANIM_RIGHT);
    Animation baseAnimUp = getUnitBaseAnimation(baseColor, ANIM_UP);
    Animation baseAnimDown = getUnitBaseAnimation(baseColor, ANIM_DOWN);

    int animLeftFrameCount = baseAnimLeft.getFrameCount();
    int animRightFrameCount = baseAnimRight.getFrameCount();
    int animUpFrameCount = baseAnimUp.getFrameCount();
    int animDownFrameCount = baseAnimDown.getFrameCount();
    int animLeftFrameDurations = baseAnimLeft.getDurations()[0];
    int animRightFrameDurations = baseAnimRight.getDurations()[0];
    int animUpFrameDurations = baseAnimUp.getDurations()[0];
    int animDownFrameDurations = baseAnimDown.getDurations()[0];

    // For each row get LEFT, RIGHT, DOWN and UP Images and create Animations out of them.
    // THE ORDER IN WHICH IMAGES ARE READ IS IMPORTANT!
    for (int row = 0; row < unitSpriteSheet.getVerticalCount(); row++) {
      int frame = 0, totalFrames = animLeftFrameCount; // Frames within 1 row

      Animation animLeft = createAnim(unitSpriteSheet, frame, totalFrames, row, animLeftFrameDurations);
      addUnitAnim(row, color, ANIM_LEFT, animLeft);
      frame += animLeftFrameCount;
      totalFrames += animLeftFrameCount;

      Animation animRight = createAnim(unitSpriteSheet, frame, totalFrames, row, animRightFrameDurations);
      addUnitAnim(row, color, ANIM_RIGHT, animRight);
      frame += animRightFrameCount;
      totalFrames += animRightFrameCount;

      Animation animDown = createAnim(unitSpriteSheet, frame, totalFrames, row, animDownFrameDurations);
      addUnitAnim(row, color, ANIM_DOWN, animDown);
      frame += animDownFrameCount;
      totalFrames += animDownFrameCount;

      Animation animUp = createAnim(unitSpriteSheet, frame, totalFrames, row, animUpFrameDurations);
      addUnitAnim(row, color, ANIM_UP, animUp);
      frame += animUpFrameCount;
      totalFrames += animUpFrameCount;

      // Create left and right animation with a single shaded unit image
      Animation animInactiveLeft = new Animation(false);
      Image inactiveImgLeft = inactiveUnitSpriteSheet.getSubImage(0, row);
      animInactiveLeft.addFrame(inactiveImgLeft, NO_DURATION);
      addUnitAnim(row, color, ANIM_INACTIVE_UNIT_LEFT, animInactiveLeft);

      Animation animInactiveRight = new Animation(false);
      Image inactiveImgRight = inactiveUnitSpriteSheet.getSubImage(animLeftFrameCount + 1, row);
      animInactiveRight.addFrame(inactiveImgRight, NO_DURATION);
      addUnitAnim(row, color, ANIM_INACTIVE_UNIT_RIGHT, animInactiveRight);
    }
  }

  private static Animation createAnim(SpriteSheet sheet, int col, int cols, int row, int duration) {
    return new Animation(sheet, col, row, cols - 1, row, true, duration, false);
  }

  private Animation getUnitBaseAnimation(Color baseColor, String suffix) {
    String unitAnimName = createUnitAnimName(0, baseColor, suffix);
    return getAnim(unitAnimName);
  }

  private void addUnitAnim(int rowID, Color color, String suffix, Animation unitAnim) {
    String animName = createUnitAnimName(rowID, color, suffix);
    addAnim(animName, unitAnim);
  }

  public Animation getUnitAnim(int rowID, Color color, String suffix) {
    String unitAnimName = createUnitAnimName(rowID, color, suffix);
    return getAnim(unitAnimName);
  }

  private static String createUnitAnimName(int rowID, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return "unit_" + rowID + "_" + colorName + "_" + suffix;
  }
}
