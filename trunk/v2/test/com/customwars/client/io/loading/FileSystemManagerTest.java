package com.customwars.client.io.loading;

import com.customwars.client.io.FileSystemManager;
import com.customwars.client.tools.FileUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test the FileSystemManager by creating some maps in maps/test and maps/versus
 * when the test is done delete the maps folder and the map files.
 */
public class FileSystemManagerTest {
  private static String PARENT_DIR = "maps/";
  private static String VERSUS_DIR = "versus/";
  private static String TEST_DIR = "test/";
  private static String parentPath;

  private static File parentDirFile, versusDirFile, testDirFile;
  private static File tempDir;
  private static FileSystemManager fsm;

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    tempDir = FileUtil.createTempDir();
    String tempPath = tempDir.getCanonicalPath();
    parentPath = tempPath + File.separator + PARENT_DIR;
    parentDirFile = new File(parentPath);
    parentDirFile.mkdir();

    fsm = new FileSystemManager(parentPath);

    testDirFile = new File(parentPath, TEST_DIR);
    testDirFile.mkdirs();

    versusDirFile = new File(parentPath, VERSUS_DIR);
    versusDirFile.mkdirs();
  }

  @AfterClass
  public static void afterAllTests() {
    Assert.assertTrue(fsm.clearFiles("test"));
    Assert.assertTrue(testDirFile.delete());

    Assert.assertTrue(fsm.clearFiles("versus"));
    Assert.assertTrue(versusDirFile.delete());

    Assert.assertTrue(parentDirFile.delete());
    tempDir.delete();
  }

  @Test
  public void readAllTestMaps() throws IOException {
    // Create some empty files in the test dir
    String testMap1 = "testMap1.map";
    Assert.assertTrue(fsm.createFile(TEST_DIR + testMap1));
    String testMap2 = "testMap2.map";
    Assert.assertTrue(fsm.createFile(TEST_DIR + testMap2));
    String testMap3 = "testMap3.map";
    Assert.assertTrue(fsm.createFile(TEST_DIR + testMap3));

    // Check if they are really created
    List<File> testMaps = fsm.getFiles(TEST_DIR);
    File testFile1 = new File(parentPath + TEST_DIR, testMap1).getAbsoluteFile();
    File testFile2 = new File(parentPath + TEST_DIR, testMap2).getAbsoluteFile();
    File testFile3 = new File(parentPath + TEST_DIR, testMap3).getAbsoluteFile();
    Assert.assertTrue(testMaps.contains(testFile1));
    Assert.assertTrue(testMaps.contains(testFile2));
    Assert.assertTrue(testMaps.contains(testFile3));
  }

  @Test
  public void readAllVersusMaps() throws IOException {
    // Create some empty files in the versus dir
    String versusMap1 = "versus.map";
    Assert.assertTrue(fsm.createFile(VERSUS_DIR + versusMap1));

    // Check if they are really created
    List<File> versusMaps = fsm.getFiles(VERSUS_DIR);
    File versusFile1 = new File(parentPath + VERSUS_DIR, versusMap1).getAbsoluteFile();
    Assert.assertTrue(versusMaps.contains(versusFile1));
  }

  @Test
  public void getTestAndVersusDir() {
    // There are 2 dirs under the parent dir
    List<File> dirs = fsm.getDirs();
    Assert.assertTrue(dirs.size() == 2);
  }

  @Test
  public void getFilesInParentDir() {
    // There are 2 dirs under the parent dir
    // But no files
    List<File> files = fsm.getFiles();
    Assert.assertTrue(files.size() == 0);
  }
}
