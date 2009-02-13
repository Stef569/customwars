package tools;

import org.apache.log4j.Logger;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Try to simplify java IO calls by reducing them to 1 line.
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
   * Igores nulls, logs warnings when the stream could not be closed.
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

  public static ImageInputStream getImgResource(String path) throws FileNotFoundException {
    MemoryCacheImageInputStream ims = null;
    InputStream is = getResource(path);
    if (is != null) {
      ims = new MemoryCacheImageInputStream(is);
    }
    return ims;
  }

  /**
   * Load a resource based on a path
   * Note: <b>Jar paths should use the forward slash as path separator!</b>
   *
   * @param path The full path of the file to load
   * @return The stream to the resource
   * @throws FileNotFoundException Indicates a failure to read the resource
   */
  public static InputStream getResource(String path) throws FileNotFoundException {
    // Try to load resource from jar
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    if (is == null) {
      return new FileInputStream(path); // No Jar let's Try the local FileSystem
    } else {
      return is;
    }
  }

  /**
   * Load a resource based on a path
   * When the resource is not found then
   * The exception will be ignored and null will be returned.
   *
   * @param path The full path of the resource to load
   * @return The inputStream
   */
  public static InputStream getOptionalResource(String path) {
    InputStream stream = null;
    try {
      stream = getResource(path);
    } catch (FileNotFoundException e) {
      // Ignore, return null instead
    }
    return stream;
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
}
