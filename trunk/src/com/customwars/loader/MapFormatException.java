package com.customwars.loader;

/**
 * thrown when a map file does not contain the formatting we expected it to have
 * @author stefan
 */
public class MapFormatException extends Exception {

  public MapFormatException() {
    super();
  }

  public MapFormatException(String message) {
    super(message);
  }

  public MapFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public MapFormatException(Throwable cause) {
    super(cause);
  }
}
