package com.customwars.client.io.loading.map;

import java.io.IOException;

/**
 * Thrown when the map file does not have the expected format
 */
public class MapFormatException extends IOException {
  public MapFormatException(Throwable cause) {
    super(cause);
  }

  public MapFormatException() {
  }

  public MapFormatException(String message) {
    super(message);
  }

  public MapFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
