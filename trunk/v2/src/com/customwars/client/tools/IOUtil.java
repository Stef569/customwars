package com.customwars.client.tools;

import org.apache.log4j.Logger;

import javax.imageio.stream.ImageInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
   * Helper method for creating dirs
   * When the dir could not be created, a RuntimeException is thrown
   *
   * @param dir    directory to create
   * @param errMsg The message to be included in the exception, if null a default error message will be generated
   * @throws RuntimeException thrown when the dir could not be created
   */
  public static void mkDir(File dir, String errMsg) throws RuntimeException {
    boolean success = mkDir(dir);

    if (!success) {
      String errorMessage = errMsg == null ? "Could not create dir: " + dir : errMsg;
      throw new RuntimeException(errorMessage);
    }
  }

  /**
   * Helper method for creating dirs
   * If the directory doesn't exist, create the dir
   *
   * @param dir directory to create
   * @return true if the directory could be created, false otherwise
   */
  public static boolean mkDir(File dir) {
    boolean success = true;
    if (!dir.exists()) {
      success = !dir.mkdir();
    }
    return success;
  }

  public static void createNewFile(File file) throws RuntimeException {
    createNewFile(file, null);
  }

  /**
   * Helper method for creating a file
   * If the file could not be created a RuntimeException is thrown
   *
   * @param file   file to create
   * @param errMsg The message to be included in the exception, if null a default error message will be generated
   * @throws RuntimeException thrown when the file could not be created
   */
  public static void createNewFile(File file, String errMsg) throws RuntimeException {
    String errorMessage = errMsg == null ? "Could not create file: '" + file + "'" : errMsg;

    try {
      boolean successs = file.createNewFile();
      if (!successs) {
        throw new RuntimeException(errorMessage);
      }
    } catch (IOException e) {
      throw new RuntimeException(errorMessage);
    }
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

  /**
   * Store the properties to the location with no extra comments
   */
  public static void storePropertyFile(Properties properties, String location) throws IOException {
    storePropertyFile(properties, location, "");
  }

  /**
   * Store the properties to the location with comments at the top of the file
   */
  public static void storePropertyFile(Properties properties, String location, String comments) throws IOException {
    FileOutputStream out = new FileOutputStream(location);
    properties.store(out, comments);
    closeStream(out);
  }

  /**
   * Get All non empty lines from an InputStream
   */
  public static List<String> getLinesFromFile(InputStream stream) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
    List<String> lines = new ArrayList<String>();

    try {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() != 0) {
          lines.add(line);
        }
      }
    } finally {
      IOUtil.closeStream(stream);
    }
    return lines;
  }

  /**
   * Class loader that loads resources from classpath and folders
   */
  public static class URLClassLoader extends ClassLoader {
    protected URL findResource(String name) {
      File f = new File(name);

      try {
        return f.toURI().toURL();
      }
      catch (MalformedURLException e) {
        logger.warn("", e);
      }
      return super.findResource(name);
    }
  }
}
