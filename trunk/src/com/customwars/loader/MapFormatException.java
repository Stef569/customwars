package com.customwars.loader;

import java.io.IOException;

/**
 * thrown when a map file does not contain the formatting we expected it to have
 * @author stefan
 */
public class MapFormatException extends Exception{

	private static final long serialVersionUID = 6458011281800385183L;

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
