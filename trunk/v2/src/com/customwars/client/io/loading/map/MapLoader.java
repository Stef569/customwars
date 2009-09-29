package com.customwars.client.io.loading.map;

import com.customwars.client.io.FileSystemManager;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Load maps from the map path locations
 * and add them to the resourceManager
 */
public class MapLoader {
  private static final Logger logger = Logger.getLogger(MapLoader.class);
  private List<String> mapPaths;
  private MapParser mapParser;
  private ResourceManager resources;

  public MapLoader(List<String> mapPaths, MapParser mapParser, ResourceManager resources) {
    this.mapPaths = mapPaths;
    this.mapParser = mapParser;
    this.resources = resources;
  }

  /**
   * Load all the maps from the 'map paths locations'
   * using the CW2 binary map parser
   *
   * Maps are searched for in the current map path and all subdirs
   */
  public void loadAllMaps() throws IOException {
    if (LoadingList.isDeferredLoading()) {
      LoadingList.get().add(new DeferredMapLoader());
    } else {
      loadAllMapsNow();
    }
  }

  private void loadAllMapsNow() throws IOException {
    for (String mapPath : mapPaths) {
      FileSystemManager fsm = new FileSystemManager(mapPath);

      // Current dir
      File f = new File(mapPath);
      readMapsFromDir(fsm, f);

      // Subdirs
      for (File category : fsm.getDirs()) {
        readMapsFromDir(fsm, category);
      }
    }
  }

  private void readMapsFromDir(FileSystemManager fsm, File dir) throws IOException {
    for (File mapFile : fsm.getFiles(dir)) {
      if (mapFile.getName().endsWith(".map")) {
        InputStream in = ResourceLoader.getResourceAsStream(mapFile.getPath());
        loadMap(in);
      } else {
        logger.warn("Skipping " + mapFile + " wrong extension expected .map");
      }
    }
  }

  private void loadMap(InputStream in) throws IOException {
    Map<Tile> map = mapParser.readMap(in);
    String mapName = map.getProperty("NAME");
    resources.addMap(mapName, map);
  }

  private class DeferredMapLoader implements DeferredResource {
    public void load() throws IOException {
      loadAllMapsNow();
    }

    public String getDescription() {
      return "Loading maps";
    }
  }
}
