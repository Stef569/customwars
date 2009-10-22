package com.customwars.client.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles files & folders under a parent dir
 * Simplifies:
 * Creating a File
 * Retrieve all files under a dir or sub dir
 * Retrieve all folders under the parent dir or sub dir
 * Clear all files under a dir or sub dir
 */
public class FileSystemManager {
  private static final String DIR_IGNORE_FILTER = ".";
  private final FilenameFilter fileNameFilter;
  private final FilenameFilter dirFilter;
  private final File PARENT_DIR;

  public FileSystemManager(String PARENTDIR) {
    // If parentDir is a relative path then
    // make sure it is converted to an absolute path
    this.PARENT_DIR = new File(PARENTDIR).getAbsoluteFile();
    this.fileNameFilter = getFileFilter();
    this.dirFilter = getDirFilter();
  }

  private FilenameFilter getFileFilter() {
    return new FilenameFilter() {
      public boolean accept(File file, String name) {
        return !name.startsWith(DIR_IGNORE_FILTER) &&
          new File(file, name).isFile();
      }
    };
  }

  private FilenameFilter getDirFilter() {
    return new FilenameFilter() {
      public boolean accept(File file, String name) {
        return
          !name.startsWith(DIR_IGNORE_FILTER) &&
            new File(file, name).isDirectory();
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

  /**
   * @param subDir The sub dir under the parent dir to retrieve the files from
   * @return a list of files under the PARENT_DIR/subDir/ excluding directories
   */
  public List<File> getFiles(String subDir) {
    return getFiles(new File(PARENT_DIR, subDir));
  }

  /**
   * @param dir The dir to retrieve the files from
   * @return a list of files under the dir excluding directories
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
   * Delete each file in the sub directory
   *
   * @param subDir The dir to delete all files in
   * @return false when a file could not be deleted
   */
  public boolean clearFiles(String subDir) {
    File dirToBeCleared = new File(PARENT_DIR, subDir);
    return clearFiles(dirToBeCleared);
  }

  /**
   * Delete each file in the directory
   *
   * @param dir The directory where all files should be deleted from
   * @return false when a file could not be deleted
   */
  private boolean clearFiles(File dir) {
    for (File file : getFiles(dir)) {
      if (!file.delete()) return false;
    }
    return true;
  }
}
