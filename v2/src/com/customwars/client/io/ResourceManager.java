package com.customwars.client.io;

import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.ResourcesLoader;
import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.io.loading.map.MapParser;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.ui.sprite.TileSprite;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Handles and stores all kind of resources: Images, Sounds, Music, Animations
 * Each of these resources is mapped to a string ie "SELECT_SOUND" -> Sound object
 * Other classes can now ask this class to play the sound
 * ie resourceManager.playSound("SELECT_SOUND")
 *
 * Before loading, the paths where the resources are located should be set
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private MapParser mapParser;
  private int darkPercentage;

  private ImageLib imageLib;
  private AnimLib animLib;

  private final HashMap<String, Sound> sounds = new HashMap<String, Sound>();
  private final HashMap<String, Music> music = new HashMap<String, Music>();
  private final HashMap<String, Map<Tile>> maps = new HashMap<String, Map<Tile>>();
  private final HashMap<String, Font> fonts = new HashMap<String, Font>();
  private final HashMap<String, TileSprite> cursors = new HashMap<String, TileSprite>();
  private final ResourcesLoader resourceLoader;

  public ResourceManager() {
    this(new ImageLib(), new AnimLib());
  }

  /**
   * @param imageLib The cache to load the images to
   * @param animLib  The cache to load the animations to
   */
  public ResourceManager(ImageLib imageLib, AnimLib animLib) {
    this.imageLib = imageLib;
    this.animLib = animLib;
    this.mapParser = new BinaryCW2MapParser();
    this.resourceLoader = new ResourcesLoader(imageLib, animLib, this, mapParser);
  }

  /**
   * Add a path where resources can be loaded from
   * eg img.path = /home/van/the/man/images/
   *
   * @param pathName The path name, used to lookup a loading path
   * @param path     The loading path
   */
  public void putLoadPath(String pathName, String path) {
    resourceLoader.putLoadPath(pathName, path);
  }

  public void loadAll() {
    resourceLoader.loadAll();
  }

  public void addFont(String fontID, Font font) {
    Args.checkForNull(fontID);
    Args.checkForNull(font);
    fonts.put(fontID.toUpperCase(), font);
  }

  public void addCursor(String cursorName, TileSprite cursor) {
    String upperCaseCursorName = cursorName.toUpperCase();
    if (!cursors.containsKey(upperCaseCursorName)) {
      cursors.put(upperCaseCursorName, cursor);
    } else {
      throw new IllegalArgumentException(upperCaseCursorName + " already stored " + cursors);
    }
  }

  public void saveMap(Map<Tile> map, OutputStream out) throws IOException {
    mapParser.writeMap(map, out);
    String mapName = map.getMapName();
    maps.put(mapName, map);
  }

  public void recolor(Color... colors) {
    recolor(Arrays.asList(colors));
  }

  public void recolor(Collection<Color> colors) {
    recolorImages(colors);
    createRecoloredAnimations(colors);
  }

  private void recolorImages(Collection<Color> colors) {
    for (Color color : colors) {
      checkIsColorSupported(color);
      imageLib.recolorImg(color, "unit");
      imageLib.recolorImg(color, "city");
    }

    for (Color color : colors) {
      imageLib.recolorImg(color, "unit", "darker", "unit", darkPercentage);
      imageLib.recolorImg(color, "city", "darker", "city", darkPercentage);
    }
  }

  private void createRecoloredAnimations(Collection<Color> colors) {
    Color unitBaseColor = getBaseColor("unit");
    Color cityBaseColor = getBaseColor("city");

    for (Color color : colors) {
      checkIsColorSupported(color);
      animLib.createUnitAnimations(unitBaseColor, color, this);
      animLib.createCityAnimations(cityBaseColor, color, this);
    }
  }

  private void checkIsColorSupported(Color color) {
    if (!imageLib.getSupportedColors().contains(color)) {
      throw new IllegalArgumentException(
        "Color " + color + " is not supported, add the color");
    }
  }

  public void setDarkPercentage(int darkPercentage) {
    this.darkPercentage = darkPercentage;
  }

  public boolean isSlickImgLoaded(String slickImgName) {
    return imageLib.isSlickImgLoaded(slickImgName);
  }

  public Image getSlickImg(String imgName) {
    return imageLib.getSlickImg(imgName);
  }

  public ImageStrip getSlickImgStrip(String imgName) {
    return (ImageStrip) imageLib.getSlickImg(imgName);
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
    return (SpriteSheet) imageLib.getSlickImg(imgName);
  }

  public int countSlickImages() {
    return imageLib.countSlickImages();
  }

  public Color getBaseColor(String filterName) {
    return imageLib.getBaseColor(filterName);
  }

  public Set<Color> getSupportedColors() {
    return imageLib.getSupportedColors();
  }

  public Animation getAnim(String animName) {
    return animLib.getAnim(animName);
  }

  public Animation getCityAnim(int cityID, Color color) {
    return getCityAnim(cityID, color, "");
  }

  public Animation getCityAnim(int cityID, Color color, String suffix) {
    return animLib.getCityAnim(cityID, color, suffix);
  }

  public Animation getUnitAnim(Unit unit, Color color, String suffix) {
    return animLib.getUnitAnim(unit.getImgRowID(), color, suffix);
  }

  public Collection<Animation> getAllAnims() {
    return animLib.getAllAnimations();
  }

  public Font getFont(String fontName) {
    String key = fontName.toUpperCase();
    if (!fonts.containsKey(key)) {
      throw new IllegalArgumentException("no font stored for " + key);
    }
    return fonts.get(key);
  }

  public Music getMusic(String musicName) {
    return music.get(musicName.toUpperCase());
  }

  public Sound getSound(String soundName) {
    return sounds.get(soundName.toUpperCase());
  }

  /**
   * @param mapName the exact name of the map, case sensitive
   * @return a copy of the Map with mapName
   */
  public Map<Tile> getMap(String mapName) {
    if (!maps.containsKey(mapName)) {
      throw new IllegalArgumentException("no map stored for " + mapName);
    }
    return new Map<Tile>(maps.get(mapName));
  }

  public boolean isMapCached(String mapName) {
    return maps.containsKey(mapName);
  }

  public Map<Tile> loadMap(File file) throws IOException {
    Map<Tile> map = mapParser.readMap(new FileInputStream(file));
    String mapName = map.getMapName();
    addMap(mapName, map);
    return map;
  }

  public void addMap(String mapName, Map<Tile> map) {
    if (maps.containsKey(mapName)) {
      throw new IllegalArgumentException(mapName + " is already stored " + maps);
    } else {
      maps.put(mapName, map);
    }
  }

  public Set<String> getAllMapNames() {
    return Collections.unmodifiableSet(maps.keySet());
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
    int row = unit.getImgRowID();
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
    int row = unit.getImgRowID();
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

  public SpriteSheet getCitySpriteSheet(Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet("CITY_" + colorName);
  }

  public TileSprite getCursor(String cursorName) {
    String upperCaseCursorName = cursorName.toUpperCase();
    if (cursors.containsKey(upperCaseCursorName)) {
      return cursors.get(upperCaseCursorName);
    } else {
      throw new IllegalArgumentException("No cursor stored for " + upperCaseCursorName + cursors.keySet());
    }
  }

  public Collection<TileSprite> getAllCursors() {
    return cursors.values();
  }

  /**
   * Create a new Cursor and place it in the map on a random tile
   */
  public TileSprite createCursor(Map<Tile> map, String cursorName) {
    TileSprite cursor = getCursor(cursorName);
    cursor.setMap(map);
    cursor.setLocation(map.getRandomTile());
    return cursor;
  }

  public Font loadDefaultFont() {
    try {
      return resourceLoader.loadDefaultFont();
    } catch (IOException e) {
      logger.warn("Could not load default font");
    }
    return null;
  }

  public void addSound(String soundName, Sound sound) {
    sounds.put(soundName, sound);
  }

  public void addMusic(String musicName, Music music) {
    this.music.put(musicName, music);
  }
}
