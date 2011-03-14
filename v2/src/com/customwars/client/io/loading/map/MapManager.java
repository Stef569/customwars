package com.customwars.client.io.loading.map;

import com.customwars.client.App;
import com.customwars.client.model.map.Map;
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
import java.util.Iterator;
import java.util.List;

/**
 * Keep a reference to Map objects. Maps can be retrieved by map name.
 * Each map is stored into a category.
 */
public class MapManager {
  private static final Logger logger = Logger.getLogger(MapManager.class);
  private final java.util.Map<String, Map> maps = new HashMap<String, Map>();
  private final java.util.Map<String, List<Map>> mapsByCategory = new HashMap<String, List<Map>>();
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
   * <p/>
   * Post:
   * isMapCached(mapName) returns true
   * getMap(mapName) returns a copy of the loaded map
   */
  public Map loadMap(InputStream in) throws IOException {
    Map map = mapParser.readMap(in);
    maps.put(map.getMapName(), new Map(map));
    return map;
  }

  /**
   * Save the map to the $mapDirPath/$category location, The map name is used as the name for the map file.
   * A category contains maps with the same player count. A map with 2 players is saved in the category "2P".
   * The category subdir is created when it does not exists.
   * <p/>
   * Post:
   * isMapCached(mapName) returns true
   * getMap(map.getMapName()) returns a copy of the saved map
   * getAllMapsByCategory($category) includes a copy of the loaded map
   *
   * @param map map to persists
   * @throws IOException When the map could not be saved.
   */
  public void saveMap(Map map) throws IOException {
    String mapDirPath = App.get("home.maps.dir");
    String mapExtension = App.get("map.file.extension");
    String mapFileName = map.getMapName() + mapExtension;
    String category = map.getNumPlayers() + "P";
    File categoryDir = new File(mapDirPath, category);
    File mapFile = new File(categoryDir, mapFileName);
    String currentCategory = getCategory(map);

    if (mapFile.exists()) {
      logger.debug("Overwriting map " + mapFileName);
      deleteMap(map, category);
    }

    if (currentCategory != null && !currentCategory.equals(category)) {
      // The map file to save is in another category.
      // Delete the old map
      logger.debug("New category for " + map.getMapName() + " old=" + currentCategory + " new=" + category);
      deleteMap(map, currentCategory);
    }

    if (categoryDir.mkdir()) {
      logger.debug("New category created: " + categoryDir);
    }

    saveMap(map, category, new FileOutputStream(mapFile));
  }

  private String getCategory(Map map) {
    for (String category : mapsByCategory.keySet()) {
      for (Map aMap : mapsByCategory.get(category)) {
        if (aMap.getMapName().equals(map.getMapName())) {
          return category;
        }
      }
    }
    // the map is not stored in a category
    return null;
  }

  private void deleteMap(Map map, String category) {
    String mapDirPath = App.get("home.maps.dir");
    String mapExtension = App.get("map.file.extension");
    String mapFileName = map.getMapName() + mapExtension;
    File categoryDir = new File(mapDirPath, category);
    File mapFile = new File(categoryDir, mapFileName);
    assert mapFile.delete();

    maps.remove(map.getMapName());
    assert !maps.containsKey(map.getMapName());

    String currentCategory = getCategory(map);
    for (String aCategory : mapsByCategory.keySet()) {
      if (aCategory.equals(currentCategory)) {
        Iterator it = mapsByCategory.get(aCategory).iterator();
        while (it.hasNext()) {
          Map aMap = (Map) it.next();
          if (aMap.getMapName().equals(map.getMapName())) {
            it.remove();
            break;
          }
        }
      }
    }
  }

  private void saveMap(Map map, String category, OutputStream out) throws IOException {
    map.normalise();
    mapParser.writeMap(map, out);
    addMap(category, new Map(map));
  }

  /**
   * Add the map to the mapCache and to the category
   * <p/>
   * Pre:
   * The map name is not already cached
   * <p/>
   * Post:
   * isMapCached(mapName) returns true
   * getAllMapsByCategory(category) contains a copy of the given map
   * getMap(mapName) returns a copy of the given map
   *
   * @param category The name of the category eg. "Versus", "1P", "2P", "Classic",...
   * @param map      map to be cached
   */
  public void addMap(String category, Map map) {
    if (!maps.containsKey(map.getMapName())) {
      maps.put(map.getMapName(), map);
      addMapCategory(category, new Map(map));
    } else {
      logger.warn("Map name " + map.getMapName() + " is already cached");
    }
  }

  private void addMapCategory(String category, Map map) {
    if (mapsByCategory.containsKey(category)) {
      List<Map> categoryMaps = mapsByCategory.get(category);

      if (!categoryMaps.contains(map)) {
        categoryMaps.add(map);
      } else {
        logger.warn("Map name " + map.getMapName() + " in category " + category + " is already cached");
      }
    } else {
      List<Map> categoryMaps = new ArrayList<Map>();
      categoryMaps.add(map);
      mapsByCategory.put(category, categoryMaps);
    }
  }

  /**
   * @param mapName the exact name of the map, case sensitive
   * @return a copy of the Map with mapName
   */
  public Map getMap(String mapName) {
    if (maps.containsKey(mapName)) {
      return new Map(maps.get(mapName));
    } else {
      throw new IllegalArgumentException("no map stored for " + mapName);
    }
  }

  /**
   * Checks if a map has been loaded into memory.
   * When a map is cached {@link #getMap(String)} will return a copy.
   * When a map is not cached it needs to be loaded.
   *
   * @param mapName The name of a map without the extension.
   * @return If the map has been cached.
   */
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
  public Collection<Map> getAllMapsByCategory(String category) {
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
