package com.customwars.client.io.loading.map;

import com.customwars.client.App;
import com.customwars.client.io.FileSystemManager;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.loading.CWResourceLoader;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Load maps from the map path locations
 * and add them to the resourceManager
 */
public class MapLoader implements CWResourceLoader {
  private static final Logger logger = Logger.getLogger(MapLoader.class);
  private final List<String> mapPaths;
  private final MapParser mapParser;
  private final ResourceManager resources;

  public MapLoader(List<String> mapPaths, MapParser mapParser, ResourceManager resources) {
    this.mapPaths = mapPaths;
    this.mapParser = mapParser;
    this.resources = resources;
  }

  /**
   * Load all the maps from the 'map paths locations'
   * Maps are searched for in the current map path and all subdirs
   */
  public void load() throws IOException {
    for (String mapPath : mapPaths) {
      FileSystemManager fsm = new FileSystemManager(mapPath);

      // Current dir
      File mapsDir = new File(mapPath);
      readMapsFromDir(fsm, mapsDir);

      // Subdirs
      for (File category : fsm.getDirs()) {
        readMapsFromDir(fsm, category);
      }
    }
  }

  private void readMapsFromDir(FileSystemManager fsm, File dir) throws IOException {
    String mapExtension = App.get("map.file.extension");
    String parentDir = dir.getName();

    for (File mapFile : fsm.getFiles(dir)) {
      if (mapFile.getName().endsWith(mapExtension)) {
        InputStream in = ResourceLoader.getResourceAsStream(mapFile.getPath());
        logger.info("Loading map " + mapFile);
        loadMap(parentDir, in);
      } else {
        logger.warn("Skipping " + mapFile + " wrong extension expected " + mapExtension);
      }
    }
  }

  private void loadMap(String category, InputStream in) throws IOException {
    Map<Tile> map = mapParser.readMap(in);
    resources.addMap(category, map);
  }
}
