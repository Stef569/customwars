package com.customwars.client.io.loading;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.loading.map.MapLoader;
import com.customwars.client.io.loading.map.MapParser;
import org.apache.log4j.Logger;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Load all the resources,
 * the paths where the resources can be loaded from are set at runtime
 */
public class ResourcesLoader {
  private static final Logger logger = Logger.getLogger(ResourcesLoader.class);
  private static final String IMAGE_LOADER_FILE = "imageLoader.txt";
  private static final String ANIM_LOADER_FILE = "animLoader.txt";
  private static final String SOUND_LOADER_FILE = "soundLoader.txt";
  private static final String COLORS_FILE = "colors.xml";

  private final CursorLoader cursorLoader;
  private final ModelLoader modelLoader;
  private final ImageLib imageLib;
  private final AnimLib animLib;
  private final ResourceManager resources;
  private final MapParser mapParser;
  private final Map<String, String> paths;

  public ResourcesLoader(ImageLib imageLib, AnimLib animLib, ResourceManager resources, MapParser mapParser) {
    this.imageLib = imageLib;
    this.animLib = animLib;
    this.resources = resources;
    this.mapParser = mapParser;
    modelLoader = new ModelLoader();
    cursorLoader = new CursorLoader(resources);
    paths = new HashMap<String, String>();
  }

  /**
   * Add a path where resources can be loaded from keyed by pathName
   * eg img.path = /home/van/the/man/images/
   *
   * @param pathName The path name, used to lookup a loading path
   * @param path     The loading path
   */
  public void putLoadPath(String pathName, String path) {
    if (!paths.containsKey(pathName)) {
      paths.put(pathName, path);
    } else {
      logger.warn("overwriting loading path " + pathName + " from " + paths.get(pathName) + " to " + path);
      paths.put(pathName, path);
    }
  }

  public void loadAll() {
    loadModel();

    try {
      loadResources();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load resource " + e);
    }
  }

  public void loadModel() {
    logger.info("Loading model");
    String dataPath = getPath("data.path");
    modelLoader.setModelResPath(dataPath);
    modelLoader.loadModel();
  }

  public void loadResources() throws IOException {
    logger.info("Loading resources");
    String cursorImgPath = getPath("cursorimg.path");
    cursorLoader.loadCursors(cursorImgPath);
    loadColorsFromFile();
    loadImagesFromFile();
    loadAnimationsFromFile();
    recolor();
    loadSoundFromFile();
    loadMaps();
    loadFonts();
    releaseUnneededResources();
  }

  private void loadColorsFromFile() throws IOException {
    String dataPath = getPath("data.path");
    ImageFilterParser imgFilterParser = new ImageFilterParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(dataPath + COLORS_FILE);
    imgFilterParser.loadConfigFile(in);
  }

  private void loadImagesFromFile() throws IOException {
    String imgPath = getPath("img.path");

    ImageParser imgParser = new ImageParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + IMAGE_LOADER_FILE);
    imgParser.setImgPath(imgPath);
    imgParser.loadConfigFile(in);
  }

  private void loadAnimationsFromFile() throws IOException {
    String imgPath = getPath("img.path");

    AnimationParser animParser = new AnimationParser(imageLib, animLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + ANIM_LOADER_FILE);
    animParser.loadConfigFile(in);
  }

  private void recolor() {
    Collection<Color> supportedColors = imageLib.getSupportedColors();
    resources.recolor(supportedColors);
  }

  private void loadSoundFromFile() throws IOException {
    String soundPath = getPath("sound.path");

    SoundParser soundParser = new SoundParser(resources);
    InputStream in = ResourceLoader.getResourceAsStream(soundPath + SOUND_LOADER_FILE);
    soundParser.setSoundPath(soundPath);
    soundParser.loadConfigFile(in);
  }

  private void loadMaps() throws IOException {
    List<String> mapPaths = getMultiplePath("map.path");
    MapLoader mapLoader = new MapLoader(mapPaths, mapParser, resources);
    mapLoader.loadAllMaps();
  }

  private void loadFonts() throws IOException {
  }

  public Font loadDefaultFont() throws IOException {
    String fontPath = getPath("font.path");

    Font defaultFont;
    try {
      defaultFont = new AngelCodeFont(fontPath + "default.fnt", fontPath + "default_00.tga");
    } catch (SlickException ex) {
      throw new IOException(ex);
    }
    return defaultFont;
  }

  /**
   * Search for multiple paths that start with the same prefix
   * Starting with prefix1 to prefix9
   */
  private List<String> getMultiplePath(String prefix) {
    List<String> multiplePaths = new ArrayList<String>();
    for (int i = 1; i < 10; i++) {
      String pathName = prefix + i;
      if (paths.containsKey(pathName)) {
        String path = paths.get(pathName);
        multiplePaths.add(path);
      }
    }
    return multiplePaths;
  }

  private String getPath(String key) {
    if (paths.containsKey(key)) {
      return paths.get(key);
    } else {
      throw new IllegalArgumentException("key could not be found in paths " + paths);
    }
  }

  private void releaseUnneededResources() {
    imageLib.clearImageSources();
  }
}
