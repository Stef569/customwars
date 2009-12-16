package com.customwars.client.io;

import com.customwars.client.App;
import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.CWImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.RecolorManager;
import com.customwars.client.io.loading.ResourcesLoader;
import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.io.loading.map.MapParser;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.UCaseMap;
import com.customwars.client.ui.sprite.TileSprite;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Handles and stores all kind of resources: Images, Sounds, Music, Animations
 * Each of these resources is mapped to a string ie "SELECT_SOUND" -> Sound object
 * Other classes can now ask this class to get the sound
 * ie resourceManager.getSound("SELECT_SOUND")
 *
 * Before loading, the paths where the resources are located should be set
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private final MapParser mapParser;
  private final ImageLib imageLib;
  private final CWImageLib cwImageLib;
  private final AnimLib animLib;
  private int darkPercentage;

  private final java.util.Map<String, Sound> sounds = new UCaseMap<Sound>();
  private final java.util.Map<String, Music> music = new UCaseMap<Music>();
  private final java.util.Map<String, Map<Tile>> maps = new HashMap<String, Map<Tile>>();
  private final java.util.Map<String, List<Map<Tile>>> mapsByCategory = new HashMap<String, List<Map<Tile>>>();
  private final java.util.Map<String, Font> fonts = new UCaseMap<Font>();
  private final java.util.Map<String, TileSprite> cursors = new UCaseMap<TileSprite>();
  private final ResourcesLoader resourceLoader;

  public ResourceManager() {
    this.imageLib = new ImageLib();
    this.cwImageLib = new CWImageLib(imageLib);
    this.animLib = new AnimLib();
    this.mapParser = new BinaryCW2MapParser();
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
    recolorManager.recolor("city", "darker", colors, darkPercentage);
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
  // Images : Getters
  //----------------------------------------------------------------------------

  public Image getSlickImg(String imgRef) {
    return imageLib.getSlickImg(imgRef);
  }

  public ImageStrip getSlickImgStrip(String imgRef) {
    return (ImageStrip) imageLib.getSlickImg(imgRef);
  }

  public SpriteSheet getSlickSpriteSheet(String imgRef) {
    return (SpriteSheet) imageLib.getSlickImg(imgRef);
  }

  public SpriteSheet getSlickSpriteSheet(String imgRef, Color color, String suffix) {
    return cwImageLib.getSlickSpriteSheet(imgRef, color, suffix);
  }

  public SpriteSheet getSlickSpriteSheet(String imgRef, Color color) {
    return cwImageLib.getSlickSpriteSheet(imgRef, color);
  }

  public SpriteSheet getCitySpriteSheet(Color color) {
    return cwImageLib.getCitySpriteSheet(color);
  }

  public SpriteSheet getUnitSpriteSheet(Color color) {
    return cwImageLib.getUnitSpriteSheet(color);
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

  public Image getShadedUnitImg(Unit unit, Direction direction) {
    return cwImageLib.getShadedUnitImg(unit, direction);
  }

  public Image getShadedUnitImg(Unit unit, Color color, Direction direction) {
    return cwImageLib.getShadedUnitImg(unit, color, direction);
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

  public Animation getCityAnim(int cityID, Color color) {
    return getCityAnim(cityID, color, "");
  }

  public Animation getCityAnim(int cityID, Color color, String suffix) {
    return animLib.getCityAnim(cityID, color, suffix);
  }

  public Animation getUnitAnim(Unit unit, Color color, String suffix) {
    return animLib.getUnitAnim(unit.getStats().getImgRowID(), color, suffix);
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
   * Load a map from the inputstream, the map is not added to a category
   * Post:
   * isMapCached(mapName) returns true
   * getMap(mapName) returns a copy of the loaded map
   */
  public Map<Tile> loadMap(InputStream in) throws IOException {
    Map<Tile> map = mapParser.readMap(in);
    maps.put(map.getMapName(), new Map<Tile>(map));
    return map;
  }

  /**
   * Save the map to the $mapDirPath/$category location, The map name is used as the name for the map file.
   * A category subdir contains maps with the same player count.
   * maps with 2 players are saved in "2P".
   * If the category subfolder does not yet exists it is created.
   * By default the CW2 Binary map parser is used to persist the map.
   *
   * Post:
   * isMapCached(mapName) returns true
   * getMap(map.getMapName()) returns a copy of the saved map
   * getAllMapsByCategory($category) includes a copy of the loaded map
   *
   * @param map map to persists
   * @throws IOException When the map could not be saved, or the map already exists
   */
  public void saveMap(Map<Tile> map) throws IOException {
    String mapFileName = StringUtil.appendTrailingSuffix(map.getMapName(), App.get("map.file.extension"));
    String mapDirPath = App.get("home.maps.dir");
    String category = map.getNumPlayers() + "P";
    File mapDir = new File(mapDirPath, category);
    File mapFile = new File(mapDir, mapFileName);

    if (mapFile.exists()) {
      throw new IOException("The map " + mapFileName + " already exists");
    } else {
      boolean categoryCreated = mapDir.mkdir();
      assert categoryCreated : "In case the map category does not exist yet, create it now";
      saveMap(map, category, new FileOutputStream(mapFile));
    }
  }

  private void saveMap(Map<Tile> map, String category, OutputStream out) throws IOException {
    map.normalise();
    mapParser.writeMap(map, out);
    addMap(category, new Map<Tile>(map));
  }

  /**
   * Add the given map to the mapCache
   *
   * Pre:
   * The map name is not already cached
   *
   * Post:
   * isMapCached(mapName) returns true
   * getAllMapsByCategory(category) contains a copy of the given map
   * getMap(mapName) returns a copy of the given map
   *
   * @param category The name of the category eg. "Versus", "1P", "2P", "Classic",...
   * @param map      map to be cached
   */
  public void addMap(String category, Map<Tile> map) {
    if (!maps.containsKey(map.getMapName())) {
      maps.put(map.getMapName(), map);
      addMapCategory(category, new Map<Tile>(map));
    } else {
      logger.warn("Map name " + map.getMapName() + " is already cached");
    }
  }

  private void addMapCategory(String category, Map<Tile> map) {
    if (mapsByCategory.containsKey(category)) {
      List<Map<Tile>> categoryMaps = mapsByCategory.get(category);
      categoryMaps.add(map);
    } else {
      List<Map<Tile>> categoryMaps = new ArrayList<Map<Tile>>();
      categoryMaps.add(map);
      mapsByCategory.put(category, categoryMaps);
    }
  }

  /**
   * @param mapName the exact name of the map, case sensitive
   * @return a copy of the Map with mapName
   */
  public Map<Tile> getMap(String mapName) {
    if (maps.containsKey(mapName)) {
      return new Map<Tile>(maps.get(mapName));
    } else {
      throw new IllegalArgumentException("no map stored for " + mapName);
    }
  }

  public boolean isMapCached(String mapName) {
    return maps.containsKey(mapName);
  }

  /**
   * @return all the cached map names sorted into ascending order
   */
  public Collection<String> getAllMapNames() {
    List<String> allMapNames = new ArrayList<String>(maps.keySet());
    Collections.sort(allMapNames);
    return Collections.unmodifiableCollection(allMapNames);
  }

  /**
   * Retrieve the maps for the given category
   *
   * @param category The category maps are stored under
   * @return A Collections of maps stored under category
   */
  public Collection<Map<Tile>> getAllMapsByCategory(String category) {
    if (mapsByCategory.containsKey(category)) {
      return Collections.unmodifiableCollection(mapsByCategory.get(category));
    } else {
      throw new IllegalArgumentException("No maps stored for " + category + ' ' + mapsByCategory.keySet());
    }
  }

  /**
   * @return all the cached map categories sorted into ascending order
   */
  public List<String> getAllMapCategories() {
    List<String> mapCats = new ArrayList<String>(mapsByCategory.keySet());
    Collections.sort(mapCats);
    return mapCats;
  }

  /**
   * Return true when the given category is a valid map category
   *
   * @return if category is a valid map category
   */
  public boolean isValidMapCategory(String category) {
    return mapsByCategory.containsKey(category);
  }

  //----------------------------------------------------------------------------
  // Cursor
  //----------------------------------------------------------------------------

  /**
   * Create a new Cursor and place it in the map on a random tile
   */
  public TileSprite createCursor(Map<Tile> map, String cursorName) {
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
