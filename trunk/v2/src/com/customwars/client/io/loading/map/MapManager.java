package com.customwars.client.io.loading.map;

import com.customwars.client.App;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.StringUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapManager {
  private static final Logger logger = Logger.getLogger(MapManager.class);
  private final java.util.Map<String, Map<Tile>> maps = new HashMap<String, Map<Tile>>();
  private final java.util.Map<String, List<Map<Tile>>> mapsByCategory = new HashMap<String, List<Map<Tile>>>();
  private final MapParser mapParser;

  /**
   * Create a new MapManager using the given mapParser to read/write maps
   *
   * @param mapParser The MapParser to persist map Objects
   */
  public MapManager(MapParser mapParser) {
    this.mapParser = mapParser;
  }

  /**
   * Load a map from the inputstream, and store the map by name
   * The map is not added to a category
   * The specified stream is closed after this method returns.
   *
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
   * A category contains maps with the same player count. A map with 2 players are saved in the category "2P".
   * The category subdir is created when it does not exists.
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
   * Add the map to the mapCache and to the category
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
}
