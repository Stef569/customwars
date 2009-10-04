package tools;

import java.io.File;

public final class FileUtil {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private FileUtil() {
  }

  public static String getFileNamewithoutExtension(File file) {
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
}
