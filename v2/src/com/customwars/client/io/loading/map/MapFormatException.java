package com.customwars.client.io.loading.map;

public class MapFormatException extends RuntimeException {
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
