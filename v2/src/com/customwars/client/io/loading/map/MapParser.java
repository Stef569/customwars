package com.customwars.client.io.loading.map;

import com.customwars.client.model.map.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for objects that can read/write a Map object from/to a stream
 */
public interface MapParser {
  /**
   * Read a map object from the inputstream
   * The stream is closed by the parser
   *
   * @param in The stream to load the Map from
   * @return The map object loaded from the mapFile
   * @throws IOException        when the stream is not valid
   * @throws MapFormatException thrown when the format of the file does not adhere to the format specified by this parser
   */
  public Map readMap(InputStream in) throws IOException, MapFormatException;

  /**
   * Write a map object to the outputstream
   * <p/>
   * After writing the map to the outputstream
   * readMap(in) from the same physical location returns an equal map.
   *
   * @param map the map object to save
   * @param out the stream to save the map to
   * @throws IOException        when the stream is not valid
   * @throws MapFormatException thrown when the format of the file does not adhere to the format specified by this parser
   */
  public void writeMap(Map map, OutputStream out) throws IOException, MapFormatException;
}
