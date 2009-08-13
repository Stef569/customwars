package com.customwars.client.io.img;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.SpriteSheet;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import tools.ColorUtil;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains all the Animations, keyed by upper case name
 *
 * @author stefan
 */
public class AnimLib {
  public static final String ANIM_LEFT = "LEFT";
  public static final String ANIM_RIGHT = "RIGHT";
  public static final String ANIM_UP = "UP";
  public static final String ANIM_DOWN = "DOWN";
  public static final String ANIM_INACTIVE = "INACTIVE";
  public static final String ANIM_FOGGED = "FOGGED";
  private static final int NO_DURATION = 999;

  private static Map<String, Animation> animations = new HashMap<String, Animation>();

  public void addAnim(String animName, Animation anim) {
    String key = animName.toUpperCase();

    if (anim.getFrameCount() > 0) {
      if (!isAnimLoaded(key)) {
        animations.put(key, anim);
      }
    }
  }

  public Animation getAnim(String animName) {
    String key = animName.toUpperCase();
    if (!isAnimLoaded(key)) {
      throw new IllegalArgumentException(
              "Animation cache does not contain " + key + " " + animations.keySet());
    }
    return animations.get(key);
  }

  public boolean isAnimLoaded(String animName) {
    return animations.containsKey(animName.toUpperCase());
  }

  public Collection<Animation> getAllAnimations() {
    return Collections.unmodifiableCollection(animations.values());
  }

  public void createUnitAnimations(Color baseColor, ResourceManager resources, Color color) {
    SpriteSheet unitSpriteSheet = resources.getSlickSpriteSheet("unit", color);
    SpriteSheet darkerSpriteSheet = resources.getSlickSpriteSheet("unit", color, "darker");
    createUnitAnimationsNow(baseColor, unitSpriteSheet, darkerSpriteSheet, color);
  }

  public void createCityAnimations(Color baseColor, ResourceManager resources, Color color) {
    SpriteSheet citySpriteSheet = resources.getSlickSpriteSheet("city", color);
    SpriteSheet darkerCitySpriteSheet = resources.getSlickSpriteSheet("city", color, "darker");
    createCityAnimationsNow(baseColor, citySpriteSheet, darkerCitySpriteSheet, color);
  }

  //----------------------------------------------------------------------------
  // City Animation
  //----------------------------------------------------------------------------
  private void createCityAnimationsNow(Color baseColor, SpriteSheet citySpriteSheet, SpriteSheet darkerCitySpriteSheet, Color color) {
    // Read frame count and durations from the base animations
    Animation baseAnim = getCityBaseAnimation(baseColor);
    int[] durations = baseAnim.getDurations();
    int totalFrames = baseAnim.getFrameCount();

    for (int row = 0; row < citySpriteSheet.getVerticalCount(); row++) {
      Animation animActive = createAnim(citySpriteSheet, 0, totalFrames, row, durations);
      addCityAnim(row, color, "", animActive);

      Animation animFogged = new Animation(false);
      animFogged.addFrame(darkerCitySpriteSheet.getSubImage(0, row), NO_DURATION);
      addCityAnim(row, color, ANIM_FOGGED, animFogged);
    }
  }

  private Animation getCityBaseAnimation(Color baseColor) {
    String cityAnimName = createCityAnimName(0, baseColor);
    return getAnim(cityAnimName);
  }

  private void addCityAnim(int cityID, Color color, String suffix, Animation cityAnim) {
    String cityAnimName = createCityAnimName(cityID, color, suffix);
    addAnim(cityAnimName, cityAnim);
  }

  public String createCityAnimName(int cityID, Color color) {
    String colorName = ColorUtil.toString(color);
    return "city_" + cityID + "_" + colorName;
  }

  public Animation getCityAnim(int cityID, Color color, String suffix) {
    String cityAnimName = createCityAnimName(cityID, color, suffix);
    return getAnim(cityAnimName);
  }

  public String createCityAnimName(int cityID, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    if (suffix != null && suffix.trim().length() > 0) {
      return "city_" + cityID + "_" + colorName + "_" + suffix;
    } else {
      return "city_" + cityID + "_" + colorName;
    }
  }

  //----------------------------------------------------------------------------
  // Unit Animation
  //----------------------------------------------------------------------------
  /**
   * Retrieve images from each row of the unitSpriteSheet and create animations out of them.
   *
   * @param baseColor               base color for a unit
   * @param unitSpriteSheet         the spritesheet to extract the images from
   * @param inactiveUnitSpriteSheet the sprite sheet to extract the inactive images from
   * @param color                   the color of the unitSpriteSheet, used to store the animations ie UNIT_0_BLUE
   */
  private void createUnitAnimationsNow(Color baseColor, SpriteSheet unitSpriteSheet, SpriteSheet inactiveUnitSpriteSheet, Color color) {
    // Read frame count and durations from the base animations
    Animation baseAnimLeft = getUnitBaseAnimation(baseColor, ANIM_LEFT);
    Animation baseAnimRight = getUnitBaseAnimation(baseColor, ANIM_RIGHT);
    Animation baseAnimUp = getUnitBaseAnimation(baseColor, ANIM_UP);
    Animation baseAnimDown = getUnitBaseAnimation(baseColor, ANIM_DOWN);

    int animLeftFrameCount = baseAnimLeft.getFrameCount();
    int animRightFrameCount = baseAnimRight.getFrameCount();
    int animUpFrameCount = baseAnimUp.getFrameCount();
    int animDownFrameCount = baseAnimDown.getFrameCount();
    int[] animLeftFrameDurations = baseAnimLeft.getDurations();
    int[] animRightFrameDurations = baseAnimRight.getDurations();
    int[] animUpFrameDurations = baseAnimUp.getDurations();
    int[] animDownFrameDuractions = baseAnimDown.getDurations();

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

      Animation animDown = createAnim(unitSpriteSheet, frame, totalFrames, row, animDownFrameDuractions);
      addUnitAnim(row, color, ANIM_DOWN, animDown);
      frame += animDownFrameCount;
      totalFrames += animDownFrameCount;

      Animation animUp = createAnim(unitSpriteSheet, frame, totalFrames, row, animUpFrameDurations);
      addUnitAnim(row, color, ANIM_UP, animUp);
      frame += animUpFrameCount;
      totalFrames += animUpFrameCount;

      Animation animInActive = new Animation(false);
      Image img = inactiveUnitSpriteSheet.getSubImage(animLeftFrameCount + 1, row);
      animInActive.addFrame(img, NO_DURATION);
      addUnitAnim(row, color, ANIM_INACTIVE, animInActive);
    }
  }

  private Animation getUnitBaseAnimation(Color baseColor, String suffix) {
    String unitAnimName = createUnitAnimName(0, baseColor, suffix);
    return getAnim(unitAnimName);
  }

  private void addUnitAnim(int unitID, Color color, String suffix, Animation unitAnim) {
    String animName = createUnitAnimName(unitID, color, suffix);
    addAnim(animName, unitAnim);
  }

  public Animation getUnitAnim(int unitID, Color color, String suffix) {
    String unitAnimName = createUnitAnimName(unitID, color, suffix);
    return getAnim(unitAnimName);
  }

  public String createUnitAnimName(int unitID, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return "unit_" + unitID + "_" + colorName + "_" + suffix;
  }

  private Animation createAnim(SpriteSheet sheet, int col, int cols, int row, int[] durations) {
    Animation anim = new Animation();
    int duractionCount = 0;
    while (col < cols) {
      Image frame = sheet.getSubImage(col, row);
      anim.addFrame(frame, durations[duractionCount++]);
      col++;
    }
    return anim;
  }
}