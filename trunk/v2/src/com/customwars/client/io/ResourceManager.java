package com.customwars.client.io;

import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.CWAnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.CWImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.RecolorManager;
import com.customwars.client.io.loading.ResourcesLoader;
import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.io.loading.map.MapManager;
import com.customwars.client.io.loading.map.MapParser;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.UCaseMap;
import com.customwars.client.ui.COSheet;
import com.customwars.client.ui.sprite.TileSprite;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Handles and stores all kind of resources: Images, Sounds, Music, Animations
 * Each of these resources is mapped to a string ie "SELECT_SOUND" -> Sound object
 * Other classes can now ask this class to get the sound
 * ie resourceManager.getSound("SELECT_SOUND")
 * <p/>
 * Before loading, the paths where the resources are located should be set
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private final ImageLib imageLib;
  private final CWImageLib cwImageLib;
  private final AnimLib animLib;
  private final CWAnimLib cwAnimLib;
  private int darkPercentage;

  private final java.util.Map<String, Sound> sounds = new UCaseMap<Sound>();
  private final java.util.Map<String, Music> music = new UCaseMap<Music>();
  private final java.util.Map<String, Font> fonts = new UCaseMap<Font>();
  private final java.util.Map<String, TileSprite> cursors = new UCaseMap<TileSprite>();
  private final ResourcesLoader resourceLoader;
  private final MapManager mapManager;

  public ResourceManager() {
    this.imageLib = new ImageLib();
    this.cwImageLib = new CWImageLib(imageLib);
    this.animLib = new AnimLib();
    this.cwAnimLib = new CWAnimLib(animLib);
    MapParser mapParser = new BinaryCW2MapParser();
    this.mapManager = new MapManager(mapParser);
    this.resourceLoader = new ResourcesLoader(imageLib, animLib, this, mapParser);
  }

  /**
   * Add a path where resources can be loaded from
   * eg img.path = /home/van/the/man/images/
   * The path should end with a '/' char
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

  //----------------------------------------------------------------------------
  // Images : Recoloring
  //----------------------------------------------------------------------------

  public void recolor(Color... colors) {
    recolor(Arrays.asList(colors));
  }

  public void recolor(Collection<Color> colors) {
    recolorImages(colors);
    createRecoloredAnimations(colors);
  }

  private void recolorImages(Collection<Color> colors) {
    RecolorManager recolorManager = imageLib.getRecolorManager();
    recolorManager.recolor("unit", colors, 0);
    recolorManager.recolor("city", colors, 0);
    recolorManager.recolor("unit", "darker", colors, darkPercentage);
  }

  private void createRecoloredAnimations(Collection<Color> colors) {
    Color unitBaseColor = getBaseColor("unit");
    Color cityBaseColor = getBaseColor("city");

    for (Color color : colors) {
      animLib.createUnitAnimations(unitBaseColor, color, this);
      animLib.createCityAnimations(cityBaseColor, color, this);
    }
  }

  public void setDarkPercentage(int darkPercentage) {
    this.darkPercentage = darkPercentage;
  }

  public Color getBaseColor(String filterName) {
    return imageLib.getRecolorManager().getBaseColor(filterName);
  }

  public Set<Color> getSupportedColors() {
    return imageLib.getRecolorManager().getSupportedColors();
  }

  //----------------------------------------------------------------------------
  // Images
  //----------------------------------------------------------------------------

  public void addImage(String imgRef, Image img) {
    imageLib.addSlickImg(imgRef, img);
  }

  public Image getSlickImg(String imgRef) {
    return imageLib.getSlickImg(imgRef);
  }

  public ImageStrip getSlickImgStrip(String imgRef) {
    return (ImageStrip) imageLib.getSlickImg(imgRef);
  }

  public SpriteSheet getSlickSpriteSheet(String imgRef) {
    return (SpriteSheet) imageLib.getSlickImg(imgRef);
  }

  public Image getCityImage(City city, int colIndex, Color color) {
    return cwImageLib.getCityImage(city, colIndex, color);
  }

  public SpriteSheet getCitySpriteSheet(Color color) {
    return cwImageLib.getCitySpriteSheet(color);
  }

  public SpriteSheet getNeutralCitySpriteSheet() {
    return cwImageLib.getNeutralCitySpriteSheet();
  }

  public int getSingleCityImageHeight(Color color) {
    return cwImageLib.getSingleCityImageHeight(color);
  }

  public Image getUnitImg(Unit unit, Direction direction) {
    return cwImageLib.getUnitImg(unit, direction);
  }

  public Image getUnitImg(Unit unit, Color color, Direction direction) {
    return cwImageLib.getUnitImg(unit, color, direction);
  }

  public SpriteSheet getShadedUnitSpriteSheet(Color color) {
    return cwImageLib.getShadedUnitSpriteSheet(color);
  }

  public SpriteSheet getUnitSpriteSheet(Color color) {
    return cwImageLib.getUnitSpriteSheet(color);
  }

  public Image getShadedUnitImg(Unit unit, Direction direction) {
    return cwImageLib.getShadedUnitImg(unit, direction);
  }

  public Image getShadedUnitImg(Unit unit, Color color, Direction direction) {
    return cwImageLib.getShadedUnitImg(unit, color, direction);
  }

  public COSheet getCOSheet(CO co) {
    return cwImageLib.getCoSheet(co.getName());
  }

  public Image getLeftCOBar(CO co) {
    int styleID = co.getStyle().getID();
    return getSlickImg("cobar" + styleID);
  }

  public Image getRightCOBar(CO co) {
    return getLeftCOBar(co).getFlippedCopy(true, false);
  }

  public Image getEndTurnImg(CO co) {
    int styleID = co.getStyle().getID();
    return getSlickImg("day" + styleID);
  }

  public int countSlickImages() {
    return imageLib.countSlickImages();
  }

  //----------------------------------------------------------------------------
  // Animation
  //----------------------------------------------------------------------------

  public Animation getAnim(String animName) {
    return animLib.getAnim(animName);
  }

  public Animation getCityAnim(City city, Color color) {
    return cwAnimLib.getCityAnim(city, color);
  }

  public Animation getInActiveCityAnim(City city, Color color) {
    return cwAnimLib.getInActiveCityAnim(city, color);
  }

  public Animation getUnitAnim(Unit unit, Color color, Direction direction) {
    return cwAnimLib.getUnitAnim(unit, color, direction);
  }

  public Animation getInactiveUnitAnim(Unit unit, Color color, Direction direction) {
    return cwAnimLib.getInactiveUnitAnim(unit, color, direction);
  }

  public Collection<Animation> getAllAnims() {
    return animLib.getAllAnimations();
  }

  //----------------------------------------------------------------------------
  // Fonts
  //----------------------------------------------------------------------------

  public void addFont(String fontName, Font font) {
    fonts.put(fontName, font);
  }

  public Font getFont(String fontName) {
    if (fonts.containsKey(fontName)) {
      return fonts.get(fontName);
    } else {
      throw new IllegalArgumentException("no font stored for " + fontName);
    }
  }

  //----------------------------------------------------------------------------
  // Sound & Music
  //----------------------------------------------------------------------------

  public void addMusic(String musicName, Music music) {
    this.music.put(musicName, music);
  }

  public Music getMusic(String musicName) {
    return music.containsKey(musicName) ? music.get(musicName) : null;
  }

  public void addSound(String soundName, Sound sound) {
    sounds.put(soundName, sound);
  }

  public Sound getSound(String soundName) {
    return sounds.containsKey(soundName) ? sounds.get(soundName) : null;
  }

  //----------------------------------------------------------------------------
  // Maps
  //----------------------------------------------------------------------------

  /**
   * @see MapManager#loadMap(InputStream)
   */
  public Map loadMap(InputStream in) throws IOException {
    return mapManager.loadMap(in);
  }

  /**
   * @see MapManager#saveMap(Map)
   */
  public void saveMap(Map map) throws IOException {
    mapManager.saveMap(map);
  }

  /**
   * @see MapManager#getMap(String)
   */
  public Map getMap(String mapName) {
    return mapManager.getMap(mapName);
  }

  /**
   * @see MapManager#addMap(String, Map)
   */
  public void addMap(String category, Map map) {
    mapManager.addMap(category, map);
  }

  /**
   * @see MapManager#isMapCached(String)
   */
  public boolean isMapCached(String mapName) {
    return mapManager.isMapCached(mapName);
  }

  /**
   * @see MapManager#getAllMapNames()
   */
  public Collection<String> getAllMapNames() {
    return mapManager.getAllMapNames();
  }

  /**
   * @see MapManager#getAllMapsByCategory(String)
   */
  public Collection<Map> getAllMapsByCategory(String category) {
    return mapManager.getAllMapsByCategory(category);
  }

  /**
   * @see MapManager#getAllMapCategories()
   */
  public List<String> getAllMapCategories() {
    return mapManager.getAllMapCategories();
  }

  /**
   * @see MapManager#isValidMapCategory(String)
   */
  public boolean isValidMapCategory(String category) {
    return mapManager.isValidMapCategory(category);
  }

  //----------------------------------------------------------------------------
  // Cursor
  //----------------------------------------------------------------------------

  /**
   * Create a new Cursor and place it in the map on a random tile
   */
  public TileSprite createCursor(Map map, String cursorName) {
    TileSprite cursor = getCursor(cursorName);
    cursor.setMap(map);
    cursor.setLocation(map.getRandomTile());
    return cursor;
  }

  public void addCursor(String cursorName, TileSprite cursor) {
    if (!cursors.containsKey(cursorName)) {
      cursors.put(cursorName, cursor);
    } else {
      throw new IllegalArgumentException(cursorName + " already stored " + cursors);
    }
  }

  public TileSprite getCursor(String cursorName) {
    if (cursors.containsKey(cursorName)) {
      return cursors.get(cursorName);
    } else {
      throw new IllegalArgumentException("No cursor stored for " + cursorName + cursors.keySet());
    }
  }

  public Collection<TileSprite> getAllCursors() {
    return cursors.values();
  }
}