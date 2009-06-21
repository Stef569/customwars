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
 * @author Kevin, Stefan
 */
public class FileSystemManager {
  private static final String DIR_IGNORE_FILTER = ".";
  private FilenameFilter fileNameFilter;
  private FilenameFilter dirFilter;
  private File PARENT_DIR;

  public FileSystemManager(String parentDir) {
    PARENT_DIR = new File(parentDir);
    fileNameFilter = getFileFilter();
    dirFilter = getDirFilter();
  }

  private FilenameFilter getFileFilter() {
    return new FilenameFilter() {
      public boolean accept(File file, String name) {
        return !name.startsWith(DIR_IGNORE_FILTER) &&
            !new File(file, name).isDirectory();
      }
    };
  }

  private FilenameFilter getDirFilter() {
    return new FilenameFilter() {
      public boolean accept(File file, String name) {
        return !name.startsWith(DIR_IGNORE_FILTER) &&
            !new File(file, name).isFile();
      }
    };
  }

  public boolean createFile(String name) throws IOException {
    File file = new File(PARENT_DIR, name);
    return file.createNewFile();
  }

  public List<File> getDirs() {
    return getDirs(PARENT_DIR);
  }

  public List<File> getDirs(File file) {
    List<File> result;
    File[] list = file.listFiles(dirFilter);

    if (list == null || list.length == 0) {
      result = Collections.emptyList();
    } else {
      result = Arrays.asList(list);
    }

    return result;
  }

  /**
   * @return a list of files under the PARENT_DIR excluding subdirs
   */
  public List<File> getFiles() {
    return getFiles(PARENT_DIR);
  }

  public List<File> getFiles(String dir) {
    return getFiles(new File(PARENT_DIR, dir));
  }

  /**
   * @return a list of files under the dir excluding subdirs
   */
  public List<File> getFiles(File dir) {
    List<File> result;
    File[] list = dir.listFiles(fileNameFilter);

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
  public boolean clearFiles(File dir) {
    for (File file : getFiles(dir)) {
      if (!file.delete()) return false;
    }
    return true;
  }
}
