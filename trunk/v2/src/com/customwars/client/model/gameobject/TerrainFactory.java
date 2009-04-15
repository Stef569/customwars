package com.customwars.client.model.gameobject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * A Cache of Terrain Objects
 * when a terrain is requested using getTerrain(terrain ID)
 * We simply return a terrain reference from the terrains cache.
 */
public class TerrainFactory {
  private static HashMap<Integer, Terrain> terrains = new HashMap<Integer, Terrain>();

  public static void addTerrains(Collection<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      addTerrain(terrain);
    }
  }

  /**
   * @param terrain The terrain to add to the cache, using the terrain ID as key
   */
  public static void addTerrain(Terrain terrain) {
    terrain.init();
    int terrainID = terrain.getID();
    if (terrains.containsKey(terrainID)) {
      throw new IllegalArgumentException("Terrain ID " + terrainID + " is already used by " + terrains.get(terrainID));
    }
    terrains.put(terrainID, terrain);
  }

  public static Terrain getTerrain(int terrainID) {
    Terrain terrain;

    if (terrains.isEmpty()) {
      throw new RuntimeException("terrain cache is empty");
    }

    if (terrains.containsKey(terrainID)) {
      terrain = terrains.get(terrainID);
    } else {
      throw new IllegalArgumentException("Terrainid: " + terrainID + " is not in the cache " + terrains.keySet());
    }
    return terrain;
  }

  public static Terrain getTerrain(String terrainID) {
    Integer id = Integer.parseInt(terrainID);
    return getTerrain(id);
  }

  public static Terrain getRandomTerrain() {
    int rand = (int) (Math.random() * terrains.size());
    if (terrains.containsKey(rand)) {
      return getTerrain(rand);
    } else {
      return getTerrain(0);
    }
  }

  public static int countTerrains() {
    return terrains.size();
  }

  public static void clear() {
    terrains.clear();
  }

  public static boolean hasTerrain(int id) {
    return terrains.containsKey(id);
  }

  public static Collection<Terrain> getTerrains() {
    return Collections.unmodifiableCollection(terrains.values());
  }
}
