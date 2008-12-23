package com.customwars.util;

import java.io.*;

/**
 * @author stefan
 */
public final class IOUtil {

  private IOUtil() {}

  /**
   * @param closeable The source or destination of data that should be closed.
   * @post the closeable is closed
   */
  public static void tryToCloseStream(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        // the closeable could not be closed, that's ok
      }
    }
  }
}
