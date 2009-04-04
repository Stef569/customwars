package com.customwars.client.io;

import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.AnimationParser;
import com.customwars.client.io.loading.ImageFilterParser;
import com.customwars.client.io.loading.ImageParser;
import com.customwars.client.io.loading.MapParser;
import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.SoundParser;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;
import tools.ColorUtil;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Handles and hides all kind of resources through 1 object
 * ie Images, Sound, Music, Animations can be retrieved
 * Each of these resources is mapped to a string like "select sound" -> Sound
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private static final String IMAGE_LOADER_FILE = "imageLoader.txt";
  private static final String ANIM_LOADER_FILE = "animLoader.txt";
  private static final String SOUND_LOADER_FILE = "soundLoader.txt";
  private static final String COLORS_FILE = "colors.xml";
  private static final int DARK_PERCENTAGE = 30;
  private static final ModelLoader modelLoader = new ModelLoader();

  private ImageLib imageLib;
  private AnimLib animLib;
  private MapParser mapParser = new MapParser();
  private HashMap<String, Sound> sounds = new HashMap<String, Sound>();
  private HashMap<String, Music> music = new HashMap<String, Music>();
  private HashMap<String, Map<Tile>> maps = new HashMap<String, Map<Tile>>();
  private String imgPath, soundPath, mapPath, dataPath;

  public ResourceManager() {
    this(new ImageLib(), new AnimLib());
  }

  /**
   * @param imageLib The cache to load the image to
   * @param animLib  The cache to load the animations to
   */
  public ResourceManager(ImageLib imageLib, AnimLib animLib) {
    this.imageLib = imageLib;
    this.animLib = animLib;
    SlickImageFactory.setDeferredLoading(LoadingList.isDeferredLoading());
  }

  public void loadAll() {
    logger.info("Loading resources");
    loadModel();
    try {
      loadResources();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load resource " + e);
    }
  }

  public void loadModel() {
    modelLoader.setModelResPath(dataPath);
    modelLoader.loadModel();
  }

  public void loadResources() throws IOException {
    loadColorsFromFile();
    loadImagesFromFile();
    loadAnimationsFromFile();
    loadSoundFromFile();
    loadAllMaps();
  }

  private void loadColorsFromFile() throws IOException {
    ImageFilterParser imgFilterParser = new ImageFilterParser();
    InputStream in = ResourceLoader.getResourceAsStream(dataPath + COLORS_FILE);
    imgFilterParser.loadConfigFile(in);
    imageLib.buildColorsFromImgFilters();
  }

  private void loadImagesFromFile() throws IOException {
    logger.info("Reading file " + imgPath + IMAGE_LOADER_FILE);
    ImageParser imgParser = new ImageParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + IMAGE_LOADER_FILE);
    imgParser.setImgPath(imgPath);
    imgParser.loadConfigFile(in);
  }

  private void loadAnimationsFromFile() throws IOException {
    AnimationParser animParser = new AnimationParser(imageLib, animLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + ANIM_LOADER_FILE);
    animParser.loadConfigFile(in);
  }

  private void loadSoundFromFile() throws IOException {
    SoundParser soundParser = new SoundParser(sounds, music);
    InputStream in = ResourceLoader.getResourceAsStream(soundPath + SOUND_LOADER_FILE);
    soundParser.setSoundPath(soundPath);
    soundParser.loadConfigFile(in);
  }

  // todo mapParser not accepting file as param?
  private void loadAllMaps() throws IOException {
    FileSystemManager fsm = new FileSystemManager(mapPath);
    for (File mapFile : fsm.getFiles()) {
      Map<Tile> map = mapParser.loadMap("");
      String mapName = map.getProperty("MAP_NAME");
      maps.put(mapName, map);
    }
  }

  public void recolor(Color... colors) {
    recolorImages(colors);
    createRecoloredAnimations(colors);
  }

  private void recolorImages(Color... colors) {
    for (Color color : colors) {
      checkIsColorSupported(color);
      imageLib.recolorImg(color, "unit");
      imageLib.recolorImg(color, "city");
    }

    for (Color color : colors) {
      imageLib.recolorImg(color, "unit", "darker", "unit", DARK_PERCENTAGE);
      imageLib.recolorImg(color, "city", "darker", "city", DARK_PERCENTAGE);
    }
  }

  private void createRecoloredAnimations(Color... colors) {
    Color unitBaseColor = getBaseColor("unit");
    Color cityBaseColor = getBaseColor("city");

    for (Color color : colors) {
      checkIsColorSupported(color);
      animLib.createUnitAnimations(unitBaseColor, this, color);
      animLib.createCityAnimations(cityBaseColor, this, color);
    }
  }

  private void checkIsColorSupported(Color color) {
    if (!imageLib.getSupportedColors().contains(color)) {
      throw new IllegalArgumentException(
              "Color " + color + " is not supported, add the color info to " + COLORS_FILE);
    }
  }

  public void setImgPath(String path) {
    this.imgPath = path;
  }

  public void setDataPath(String path) {
    this.dataPath = path;
  }

  public void setSoundPath(String soundPath) {
    this.soundPath = soundPath;
  }

  public void setMapPath(String mapPath) {
    this.mapPath = mapPath;
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

  public Animation getUnitAnim(int unitID, Color color, String suffix) {
    return animLib.getUnitAnim(unitID, color, suffix);
  }

  public Collection<Animation> getAllAnims() {
    return animLib.getAllAnimations();
  }

  public Music getMusic(String musicName) {
    return music.get(musicName.toUpperCase());
  }

  public Sound getSound(String soundName) {
    return sounds.get(soundName.toUpperCase());
  }

  public void playSound(String soundName) {
    Sound s = getSound(soundName);
    if (s != null) {
      s.play();
    }
  }

  public void playMusic(String musicName) {
    Music music = getMusic(musicName);
    if (music != null) {
      music.play();
    }
  }

  /**
   * @param mapName the exact name of the map, case sensitive
   * @return a copy of the Map with mapName
   */
  public Map<Tile> getMap(String mapName) {
    return new Map<Tile>(maps.get(mapName));
  }

  public Set<String> getAllMapNames() {
    return Collections.unmodifiableSet(maps.keySet());
  }
}
