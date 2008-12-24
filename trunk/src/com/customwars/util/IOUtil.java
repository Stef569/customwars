package com.customwars.util;

import java.io.*;

/**
 * Contains reusable functions to handle common IO tasks
 *
 * @author stefan
 */
public final class IOUtil {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private IOUtil() {
  }

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
