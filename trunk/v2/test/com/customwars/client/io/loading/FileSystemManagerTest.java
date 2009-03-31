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
 * Test the FileSystemManager by creating some maps in testData/map
 * when the test is done delete the maps files, so they don't end up on svn.
 *
 * @author stefan
 */
public class FileSystemManagerTest {
  private String parentDir = "testData/map/";
  private String testMap1 = "testMap1.map";
  private String testMap2 = "testMap2.map";
  private String testMap3 = "testMap3.map";

  private File expectedTestMap1 = new File(parentDir + testMap1);
  private File expectedTestMap2 = new File(parentDir + testMap2);
  private File expectedTestMap3 = new File(parentDir + testMap3);
  private static FileSystemManager fsm;

  @Before
  public void beforeEachTest() throws IOException {
    fsm = new FileSystemManager(parentDir);
    fsm.createFile(testMap1);
    fsm.createFile(testMap2);
    fsm.createFile(testMap3);
  }

  @AfterClass
  public static void afterAllTests() {
    fsm.clearFiles();
  }

  @Test
  public void readAllMaps() {
    List<File> maps = fsm.getFiles();
    Assert.assertTrue(maps.contains(expectedTestMap1));
    Assert.assertTrue(maps.contains(expectedTestMap2));
    Assert.assertTrue(maps.contains(expectedTestMap3));
  }
}
