package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;

/**
 * CW specific Image getter functions
 * This class hides how single unit/city images are retrieved from a unit/city spritesheet.
 */
public class CWImageLib {
  private final ImageLib lib;

  public CWImageLib(ImageLib lib) {
    this.lib = lib;
  }

  public int getSingleCityImageHeight(Color color) {
    SpriteSheet sheet = getCitySpriteSheet(color);
    return sheet.getSubImage(0, 0).getHeight();
  }

  public Image getCityImage(City city, int colIndex, Color color) {
    SpriteSheet citySheet = getCitySpriteSheet(color);
    return citySheet.getSubImage(colIndex, city.getID());
  }

  public SpriteSheet getCitySpriteSheet(Color color) {
    return getSlickSpriteSheet("CITY", color);
  }

  public SpriteSheet getShadedCitySpriteSheet(Color color) {
    return getSlickSpriteSheet("CITY", color, "darker");
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
    return getSlickSpriteSheet("UNIT", color, "darker");
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
   * Crop a single unit image from a spritesheet that is looking in the given direction.
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
        throw new IllegalArgumentException("Direction " + direction + " is not supported for a unit image spritesheet");
    }
    return unitImg;
  }

  private SpriteSheet getSlickSpriteSheet(String imgName, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName + "_" + suffix);
  }

  private SpriteSheet getSlickSpriteSheet(String imgName, Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName);
  }

  private SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) lib.getSlickImg(imgName);
  }
}
