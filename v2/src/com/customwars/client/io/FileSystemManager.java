package com.customwars.client.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles Files under a parent dir
 *
 * @author Kevin
 * @author Stefan
 */
public class FileSystemManager {
  private static final String DIR_IGNORE_FILTER = ".";
  private FilenameFilter fileNameFilters;
  private File PARENT_DIR;

  public FileSystemManager(String parentDir) {
    PARENT_DIR = new File(parentDir);
    fileNameFilters = getFileFilters();
  }

  private FilenameFilter getFileFilters() {
    return new FilenameFilter() {
      public boolean accept(File file, String name) {
        return !name.startsWith(DIR_IGNORE_FILTER) &&
                !new File(file, name).isDirectory();
      }
    };
  }

  public boolean createFile(String name) throws IOException {
    File file = new File(PARENT_DIR.getCanonicalPath() + "/" + name);
    return file.createNewFile();
  }

  /**
   * @return a list of files under the PARENT_DIR excluding subdirs
   */
  public List<File> getFiles() {
    return getAllFiles(PARENT_DIR);
  }

  private List<File> getAllFiles(File file) {
    List<File> result;
    File[] list = file.listFiles(fileNameFilters);

    if (list == null || list.length == 0) {
      result = Collections.emptyList();
    } else {
      result = Arrays.asList(list);
    }

    return result;
  }

  /**
   * Delete each file in the Parent directory
   *
   * @return false when a file could not be deleted
   */
  public boolean clearFiles() {
    for (File file : getFiles()) {
      if (!file.delete()) return false;
    }
    return true;
  }
}
