package com.customwars.loader;

import com.customwars.map.Map;
import com.customwars.state.FileSystemManager;
import com.customwars.util.IOUtil;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads one Map or multiple maps from disk, they are not cached
 * so use loadAllMaps sparingly as it will access the disk on each invocation!
 */
public class MapLoader {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MapLoader.class);
  private static final char MAP_FILE_DEILIMTER = Character.MIN_VALUE;
  private static final byte VERSION = -1;
  private static String defaultMapName;   // Defaults to the fileName
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
   * load a map from a file, when no map name is provided default to the fileName
   *
   * @throws MapFormatException When the map does not end with .map
   */
  public Map loadMap(File mapFile) throws IOException, MapFormatException {
    defaultMapName = mapFile.getName();
    DataInputStream mapReader = null;
    Map map;

    try {
      validateMapFile(mapFile);
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
  private void validateMapFile(File file) throws MapFormatException {
    if (file != null && !file.getName().endsWith(".map")) {
      throw new MapFormatException("file " + file + " doesn't end with .map");
    }
  }

  /**
   * Read a map from a Binary file
   *
   * @throws MapFormatException If the version number is not the first value in the file
   *                            or when the version is not equal to the VERSION cst
   */
  private Map readMap(DataInputStream mapReader) throws IOException, MapFormatException {
    String mapName = "", author = "", description = "";
    int cols, rows, playersCount;
    Map map;

    byte version = (byte) mapReader.readInt();
    if (validVersion(version)) {
      mapName = readString(mapReader, MAP_FILE_DEILIMTER, defaultMapName);
      author = readString(mapReader, MAP_FILE_DEILIMTER, DEFAULT_AUTHOR_NAME);
      description = readString(mapReader, MAP_FILE_DEILIMTER, DEFAULT_DESCRIPTION);
      cols = mapReader.readInt();
      rows = mapReader.readInt();
      playersCount = (int) mapReader.readByte();

      map = new Map(mapName, author, description, playersCount, cols, rows);
    } else {
      throw new MapFormatException("version " + version + " does not equal to " + VERSION);
    }
    return map ;
  }

  /**
   * A valid version is a version that has a value of VERSION
   * todo it would be better if the version of the app was put here, so we can see if this map is supported.
   */
  private boolean validVersion(byte version) {
    return version == VERSION;
  }

  private String readString(DataInputStream mapReader, char mapFileDelimiter, String defaultValue) throws IOException {
    String rawData = readString(mapReader, mapFileDelimiter);
    return containsValue(rawData) ? rawData : defaultValue;
  }

  /**
   * Read characters until endChar is found
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
}
