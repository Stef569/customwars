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
 * when the test is done delete the maps files, so they don't end up on svn.
 *
 * @author stefan
 */
public class FileSystemManagerTest {
  private static String parentDir = "maps/";
  private static String versusDir = "versus/";
  private static String testDir = "test/";

  private String testMap1 = "testMap1.map";
  private String testMap2 = "testMap2.map";
  private String testMap3 = "testMap3.map";
  private String versusMap1 = "versus.map";

  private static FileSystemManager fsm;

  @Before
  public void beforeEachTest() throws IOException {
    fsm = new FileSystemManager(parentDir);
    new File(parentDir, testDir).mkdirs();
    new File(parentDir, versusDir).mkdirs();
  }

  @AfterClass
  public static void afterAllTests() {
    File test = new File(parentDir, testDir);
    fsm.clearFiles(test);
    test.delete();
    File versus = new File(parentDir, versusDir);
    fsm.clearFiles(versus);
    versus.delete();
    new File(parentDir).delete();
  }

  @Test
  public void readAllTestMaps() throws IOException {
    // Create some empty files in the test dir
    Assert.assertTrue(fsm.createFile(testDir + testMap1));
    Assert.assertTrue(fsm.createFile(testDir + testMap2));
    Assert.assertTrue(fsm.createFile(testDir + testMap3));

    // Check if they are really created
    List<File> maps = fsm.getFiles(testDir);
    Assert.assertTrue(maps.contains(new File(parentDir, testDir + testMap1)));
    Assert.assertTrue(maps.contains(new File(parentDir, testDir + testMap2)));
    Assert.assertTrue(maps.contains(new File(parentDir, testDir + testMap3)));
  }

  @Test
  public void readAllVersusMaps() throws IOException {
    // Create some empty files in the versus dir
    Assert.assertTrue(fsm.createFile(versusDir + versusMap1));

    // Check if they are really created
    List<File> maps = fsm.getFiles(versusDir);
    Assert.assertTrue(maps.contains(new File(parentDir + versusDir, versusMap1)));
  }
}
