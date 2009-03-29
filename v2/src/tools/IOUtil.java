package tools;

import org.apache.log4j.Logger;

import javax.imageio.stream.ImageInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simplify java IO methods by reducing them to 1 line.
 *
 * @author Stefan
 */
public final class IOUtil {
  private static final Logger logger = Logger.getLogger(IOUtil.class);

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private IOUtil() {
  }

  /**
   * Close a Stream
   * Ignoring null streams,
   * A warning is logged when the stream could not be closed.
   *
   * @param in The stream to close
   */
  public static void closeStream(Closeable in) {
    if (in == null) return;

    try {
      in.close();
    }
    catch (IOException ioe) {
      logger.warn("Exception closing: " + in + " " + ioe);
    }
  }

  public static void closeImgInputStream(ImageInputStream in) {
    if (in == null) return;

    try {
      in.close();
    }
    catch (IOException ioe) {
      logger.warn("Exception closing: " + in + " " + ioe);
    }
  }

  /**
   * Helper method for checking dirs
   * If the directory doesn't exist it will create it else it will return immediately
   * When it failed to create the dir, it returns false
   *
   * @param dir directory to create
   */
  public static boolean mkDir(File dir) {
    boolean result = true;
    if (!dir.exists()) {
      result = !dir.mkdir();
    }
    return result;
  }

  /**
   * Load Properties from the inputstream without using defaults
   */
  public static Properties loadProperties(InputStream in) throws IOException {
    return loadProperties(in, null);
  }

  /**
   * Load Properties from the inputstream, backed by defaults
   */
  public static Properties loadProperties(InputStream in, Properties defaults) throws IOException {
    Properties properties = new Properties(defaults);
    properties.load(in);
    closeStream(in);
    return properties;
  }
}
