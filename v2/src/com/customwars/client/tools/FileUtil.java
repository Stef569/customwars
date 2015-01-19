package com.customwars.client.tools;

import java.io.File;

public final class FileUtil {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private FileUtil() {
  }

  public static String getFileNameWithoutExtension(File file) {
    return StripFileExtension(file.getName());
  }

  public static String StripFileExtension(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf('.'));
  }

  public static String getExtension(File file) {
    return getFileExtension(file.getName());
  }

  public static String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf('.') + 1);
  }

  /**
   * Creates a new directory somewhere beneath the system's temporary directory (as defined by the java.io.tmpdir system property), and returns its name.
   * @return the newly-created directory
   */
  public static File createTempDir() {
    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    String baseName = System.currentTimeMillis() + "-";

    for (int counter = 0; counter < 10; counter++) {
      File tempDir = new File(baseDir, baseName + counter);
      if (tempDir.mkdir()) {
        return tempDir;
      }
    }
    throw new IllegalStateException("Failed to create directory within "
      + 10 + " attempts (tried " + baseName + "0 to " + baseName + (10 - 1) + ')');
  }
}
