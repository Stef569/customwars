package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A Cache of Terrain Objects
 * when a terrain is requested using getTerrain(terrain ID)
 * A terrain reference from the terrains cache is returned.
 */
public class TerrainFactory {
  private static HashMap<Integer, Terrain> terrains = new HashMap<Integer, Terrain>();
  private static List<Terrain> baseTerrains = new ArrayList<Terrain>();
  private static final Comparator<Terrain> SORT_TERRAIN_ON_ID = new Comparator<Terrain>() {
    public int compare(Terrain terrainA, Terrain terrainB) {
      return terrainA.getID() - terrainB.getID();
    }
  };

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

  /**
   * Use the id of each dummy base terrain in the baseTerrainCollection
   * to retrieve the real base terrain from the terrains collection and
   * to add the real base terrain to the baseTerrain collection
   */
  public static void addBaseTerrains(Collection<Terrain> dummyBaseTerrainCollection) {
    for (Terrain dummyBaseTerrain : dummyBaseTerrainCollection) {
      int dummyBaseTerrainID = dummyBaseTerrain.getID();
      Terrain baseTerrain = terrains.get(dummyBaseTerrainID);
      baseTerrains.add(baseTerrain);
    }
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

  public static Terrain getTerrain(String terrainName) {
    for (Terrain terrain : terrains.values()) {
      if (terrain.getName().equalsIgnoreCase(terrainName)) {
        return terrain;
      }
    }
    throw new IllegalArgumentException("Terrain with name: " + terrainName + " is not in the cache");
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

  public static boolean hasTerrainForID(int terrainID) {
    return terrains.containsKey(terrainID);
  }

  /**
   * @return A Collection of all the terrains in this Factory sorted on terrainID
   */
  public static Collection<Terrain> getTerrains() {
    List<Terrain> allTerrains = new ArrayList<Terrain>(terrains.values());
    Collections.sort(allTerrains, SORT_TERRAIN_ON_ID);
    return Collections.unmodifiableCollection(allTerrains);
  }

  /**
   * @return A Collection of all the base terrains in this Factory sorted on terrainID
   */
  public static List<Terrain> getBaseTerrains() {
    Collections.sort(baseTerrains, SORT_TERRAIN_ON_ID);
    return Collections.unmodifiableList(baseTerrains);
  }
}
