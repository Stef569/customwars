package com.customwars.client.io.loading;

import com.customwars.client.io.FileSystemManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test the FileSystemManager by creating some maps in maps/test and maps/versus
 * when the test is done delete the maps folder and the map files, so they don't end up on svn.
 */
public class FileSystemManagerTest {
  private static String parentDir = "maps/";
  private static String versusDir = "versus/";
  private static String testDir = "test/";

  private String testMap1 = "testMap1.map";
  private String testMap2 = "testMap2.map";
  private String testMap3 = "testMap3.map";
  private String versusMap1 = "versus.map";

  private static File parentDirFile, versusDirFile, testDirFile;

  private static FileSystemManager fsm;

  @Before
  public void beforeEachTest() throws IOException {
    File temp = File.createTempFile("temp-file-name", ".tmp");
    String path = temp.getAbsolutePath();

    parentDirFile = new File(path + parentDir);
    parentDirFile.mkdir();

    fsm = new FileSystemManager(parentDir);

    testDirFile = new File(parentDir, testDir);
    testDirFile.mkdirs();

    versusDirFile = new File(parentDir, versusDir);
    versusDirFile.mkdirs();
  }

  @AfterClass
  public static void afterAllTests() {
    Assert.assertTrue(fsm.clearFiles("test"));
    Assert.assertTrue(testDirFile.delete());

    Assert.assertTrue(fsm.clearFiles("versus"));
    Assert.assertTrue(versusDirFile.delete());

    Assert.assertTrue(parentDirFile.delete());
  }

  @Test
  public void readAllTestMaps() throws IOException {
    // Create some empty files in the test dir
    Assert.assertTrue(fsm.createFile(testDir + testMap1));
    Assert.assertTrue(fsm.createFile(testDir + testMap2));
    Assert.assertTrue(fsm.createFile(testDir + testMap3));

    // Check if they are really created
    List<File> testMaps = fsm.getFiles(testDir);
    File testFile1 = new File(parentDir + testDir, testMap1).getAbsoluteFile();
    File testFile2 = new File(parentDir + testDir, testMap2).getAbsoluteFile();
    File testFile3 = new File(parentDir + testDir, testMap3).getAbsoluteFile();
    Assert.assertTrue(testMaps.contains(testFile1));
    Assert.assertTrue(testMaps.contains(testFile2));
    Assert.assertTrue(testMaps.contains(testFile3));
  }

  @Test
  public void readAllVersusMaps() throws IOException {
    // Create some empty files in the versus dir
    Assert.assertTrue(fsm.createFile(versusDir + versusMap1));

    // Check if they are really created
    List<File> versusMaps = fsm.getFiles(versusDir);
    File versusFile1 = new File(parentDir + versusDir, versusMap1).getAbsoluteFile();
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
