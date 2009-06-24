package com.customwars.client.io.loading.map;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.io.File;
import java.io.IOException;

public interface MapParser {
  /**
   * Convert a File into a Map object
   *
   * @param mapFile The file to load the Map from
   * @return The map loaded from the mapFile
   * @throws IOException        when the mapFile does not exist
   * @throws MapFormatException if there was a type mismatch eg.: expected byte but was int.
   */
  public Map<Tile> readMap(File mapFile) throws IOException, MapFormatException;

  /**
   * Convert a Map object into a File
   *
   * @param map     the map to save
   * @param mapFile the file where the map is going to be saved to
   * @throws IOException        when the mapFile does not exist
   * @throws MapFormatException if there was a type mismatch eg.: expected byte but was int.
   */
  public void writeMap(Map<Tile> map, File mapFile) throws IOException, MapFormatException;
}
