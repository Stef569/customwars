package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;

/**
 * CW specific Image functions
 */
public class CWImageLib {
  private final ImageLib lib;

  public CWImageLib(ImageLib lib) {
    this.lib = lib;
  }

  public SpriteSheet getCitySpriteSheet(Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet("CITY_" + colorName);
  }

  public SpriteSheet getUnitSpriteSheet(Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet("UNIT_" + colorName);
  }

  public Image getUnitImg(Unit unit, Direction direction) {
    Color playerColor = unit.getOwner().getColor();
    return getUnitImg(unit, playerColor, direction);
  }

  public Image getUnitImg(Unit unit, Color color, Direction direction) {
    SpriteSheet unitSpriteSheet = getUnitSpriteSheet(color);
    int row = unit.getStats().getImgRowID();
    return cropUnitImg(unitSpriteSheet, direction, row);
  }

  public SpriteSheet getShadedUnitSpriteSheet(Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet("UNIT_" + colorName + "_darker");
  }

  public Image getShadedUnitImg(Unit unit, Direction direction) {
    Color playerColor = unit.getOwner().getColor();
    return getShadedUnitImg(unit, playerColor, direction);
  }

  public Image getShadedUnitImg(Unit unit, Color color, Direction direction) {
    SpriteSheet unitSpriteSheet = getShadedUnitSpriteSheet(color);
    int row = unit.getStats().getImgRowID();
    return cropUnitImg(unitSpriteSheet, direction, row);
  }

  /**
   * Crop a unit from a spritesheet
   * that is looking in the given direction
   * Supported directions(N,E,S,W) all other directions will throw an IllegalArgumentException
   */
  private Image cropUnitImg(SpriteSheet unitSpriteSheet, Direction direction, int row) {
    Image unitImg;

    switch (direction) {
      case NORTH:
        unitImg = unitSpriteSheet.getSubImage(10, row);
        break;
      case EAST:
        unitImg = unitSpriteSheet.getSubImage(4, row);
        break;
      case SOUTH:
        unitImg = unitSpriteSheet.getSubImage(7, row);
        break;
      case WEST:
        unitImg = unitSpriteSheet.getSubImage(1, row);
        break;
      default:
        throw new IllegalArgumentException("Direction " + direction + " is not supported for a unit");
    }
    return unitImg;
  }

  public SpriteSheet getSlickSpriteSheet(String imgName, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName + "_" + suffix);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName, Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) lib.getSlickImg(imgName);
  }
}
