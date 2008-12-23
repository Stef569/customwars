package com.customwars.loader;

import com.customwars.map.Map;
import com.customwars.state.FileSystemManager;
import com.customwars.util.IOUtil;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Loads one Map or multiple maps from disk, they are not cached
 * so use loadAllMaps sparingly as it will access the disk on each invocation!
 */
public class MapLoader {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MapLoader.class);
  private static final char MAP_FILE_DEILIMTER = Character.MIN_VALUE;
  private static final byte VERSION = -1;
  private static final String DEFAULT_MAP_NAME = "Unnamed Map";
  private static final String DEFAULT_AUTHOR_NAME = "unnamed";
  private static final String DEFAULT_DESCRIPTION = "No information is available for this map";

  /**
   * todo why do we need to get each map file name?
   */
  @Deprecated
  public String[] getFileNames() {
    List<String> mapFileNames = new ArrayList<String>();
    for (File file : getAllMapFiles()) {
      mapFileNames.add(file.getPath());
    }
    return mapFileNames.toArray(new String[]{});
  }

  /**
   * Loads all valid maps from a folder,
   * On Exception: Log and continue with the next map
   */
  public List<Map> loadAllValidMaps() {
    List<Map> maps = new ArrayList<Map>();

    for (File mapFile : getAllMapFiles()) {
      try {
        Map map = loadMap(mapFile);
        if (map != null) maps.add(map);
      } catch (IOException e) {
        logger.warn("Couldn't read MAP file [ " + mapFile.getName() + "]", e);
      } catch (MapFormatException e) {
        logger.warn("Couldn't parse MAP file [ " + mapFile.getName() + "]", e);
      }
    }
    return maps;
  }

  public List<Map> loadAllMaps() throws MapFormatException, IOException {
    List<Map> maps = new ArrayList<Map>();

    for (File mapFile : getAllMapFiles()) {
      try {
        Map map = loadMap(mapFile);
        if (map != null) maps.add(map);
      } catch (IOException e) {
        throw new IOException("Couldn't read MAP file [ " + mapFile.getName() + "]", e);
      } catch (MapFormatException e) {
        throw new MapFormatException("Couldn't parse MAP file [ " + mapFile.getName() + "]", e);
      }
    }
    return maps;
  }

  private File[] getAllMapFiles() {
    return FileSystemManager.getAllAvailableMaps().toArray(new File[0]);
  }

  /**
   * When the map does not end with .map then a null map is returned
   */
  public Map loadMap(File mapFile) throws IOException, MapFormatException {
    DataInputStream mapReader = null;
    Map map;

    try {
      isMapFile(mapFile);
      mapReader = new DataInputStream(new FileInputStream(mapFile));
      map = readMap(mapReader);
    } finally {
      IOUtil.tryToCloseStream(mapReader);
    }
    return map;
  }

  /**
   * @throws MapFormatException if the file is not a valid cw Map file
   */
  private void isMapFile(File file) throws MapFormatException {
    if (file != null && !file.getName().endsWith(".map")) {
      throw new MapFormatException("file " + file + " doesn't end with .map");
    }
  }

  /**
   * Read a map from a Binary file
   *
   * @throws IllegalArgumentException If the version number is not the first value in the file
   *                                  or when the version is not equal to the VERSION cst
   */
  private Map readMap(DataInputStream mapReader) throws IOException, MapFormatException {
    String mapName = "", author = "", description = "";

    byte version = (byte) mapReader.readInt();
    if (validateVersion(version)) {
      mapName = readString(mapReader, MAP_FILE_DEILIMTER, DEFAULT_MAP_NAME);
      author = readString(mapReader, MAP_FILE_DEILIMTER, DEFAULT_AUTHOR_NAME);
      description = readString(mapReader, MAP_FILE_DEILIMTER, DEFAULT_DESCRIPTION);
    } else {
      throw new MapFormatException("version " + version + " does not equal to " + VERSION);
    }
    return new Map(mapName, author, description, 0, 0);
  }

  /**
   * A valid version is a version that has a value of VERSION
   * todo it would be better if the version of the app was put here, so we can see if this map is supported.
   */
  private boolean validateVersion(byte version) {
    return version == VERSION;
  }

  private String readString(DataInputStream mapReader, char mapFileDeilimter, String defaultValue) throws IOException {
    String rawData = readString(mapReader, mapFileDeilimter);
    return containsValue(rawData) ? rawData : defaultValue;
  }

  /**
   * Read bytes until endChar is found
   * todo move to loaderUtil class
   */
  private String readString(InputStream is, char endChar) throws IOException {
    StringBuffer sb = new StringBuffer();
    int i;
    char ch;

    while ((i = is.read()) != -1) {
      ch = (char) i;
      if (ch == endChar) break;
      sb.append(ch);
    }
    return sb.toString();
  }

  /**
   * todo move to StringUtil class
   */
  private boolean containsValue(String s) {
    return !(s == null || s.trim().length() == 0);
  }

  /**
   * if name is "" then do magic
   * unnused, todo why/what is this doing?
   */
  private String cleanName(String name, String fileName) {
    if (name.equals("")) {
      int lastslash = fileName.indexOf('\\');
      if (lastslash == -1) {
        lastslash = fileName.indexOf('/');
        while (fileName.indexOf('/', lastslash + 1) != -1) {
          lastslash = fileName.indexOf('/', lastslash + 1);
        }
      } else {
        while (fileName.indexOf('\\', lastslash + 1) != -1) {
          lastslash = fileName.indexOf('\\', lastslash + 1);
        }
      }
      return fileName.substring(lastslash + 1, fileName.length() - 4);
    } else {
      return name;
    }
  }
}
