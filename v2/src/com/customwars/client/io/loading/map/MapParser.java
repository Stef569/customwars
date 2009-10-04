package com.customwars.client.io.loading.map;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for objects that can read/write a Map object from/to a stream
 */
public interface MapParser {
  public static final String MAP_FILE_EXTENSION = ".map";

  /**
   * Read a map object from the inputstream
   *
   * @param in The styream to load the Map from
   * @return The map object loaded from the mapFile
   * @throws IOException        when the stream is not valid
   * @throws MapFormatException thrown when the format of the file does not adhere to the format specified by this parser
   */
  public Map<Tile> readMap(InputStream in) throws IOException, MapFormatException;

  /**
   * Write a map object to the outputstream
   *
   * After writing the map to the outputstream
   * readMap(in) from the same physical location returns an equal map.
   *
   * @param map the map object to save
   * @param out the stream where the map is going to be saved to
   * @throws IOException        when the stream is not valid
   * @throws MapFormatException thrown when the format of the file does not adhere to the format specified by this parser
   */
  public void writeMap(Map<Tile> map, OutputStream out) throws IOException, MapFormatException;
}
