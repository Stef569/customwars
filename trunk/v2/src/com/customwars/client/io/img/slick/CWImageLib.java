package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.tools.UCaseMap;
import com.customwars.client.ui.COSheet;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;
import java.util.Map;

/**
 * CW specific Image getter functions
 * This class hides how single unit/city images are retrieved from a unit/city spritesheet.
 * The imageRowID is used to retrieve an image from a spritesheet.
 */
public class CWImageLib {
  private final ImageLib lib;
  private Map<String, COSheet> coSheets;

  public CWImageLib(ImageLib lib) {
    this.lib = lib;
  }

  public int getSingleCityImageHeight(Color color) {
    SpriteSheet sheet = getCitySpriteSheet(color);
    return sheet.getSubImage(0, 0).getHeight();
  }

  public Image getCityImage(City city, int colIndex, Color color) {
    // There are 2 types of cities.
    // Cities that can be captured/owned and hence recolored.
    // Cities that are neutral and are always displayed in the neutral color.
    if (city.canBeCaptured()) {
      SpriteSheet coloredCitySheet = getCitySpriteSheet(color);
      return coloredCitySheet.getSubImage(colIndex, city.getImgRowID());
    } else {
      SpriteSheet neutralCitySheet = getNeutralCitySpriteSheet();
      return neutralCitySheet.getSubImage(colIndex, city.getImgRowID());
    }
  }

  public SpriteSheet getNeutralCitySpriteSheet() {
    return getSlickSpriteSheet("neutral_cities");
  }

  public SpriteSheet getCitySpriteSheet(Color color) {
    return getSlickSpriteSheet("CITY", color);
  }

  public SpriteSheet getUnitSpriteSheet(Color color) {
    return getSlickSpriteSheet("UNIT", color);
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
   *
   * @param unitSpriteSheet The spritesheet source to crop a Unit image out
   * @param direction       The facing direction of the cropped unit
   * @param row             The row in the spritesheet to re retrieve the unit image from
   * @return 1 cropped unit image facing to direction on the given row
   */
  private static Image cropUnitImg(SpriteSheet unitSpriteSheet, Direction direction, int row) {
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

  public COSheet getCoSheet(String coName) {
    // The co images are not loaded yet when creating this object
    // Load them from the image lib when they are needed for the first time.
    if (coSheets == null) {
      loadCOSheets();
    }
    return coSheets.get(coName);
  }

  private void loadCOSheets() {
    this.coSheets = new UCaseMap<COSheet>();

    for (CO co : COFactory.getAllCOS()) {
      Image coImage = lib.getSlickImg("co_" + co.getName());
      coSheets.put(co.getName(), new COSheet(coImage));
    }
  }
}
