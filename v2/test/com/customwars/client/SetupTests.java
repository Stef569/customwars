package com.customwars.client;

import com.customwars.client.model.TestData;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.junit.Test;

/**
 * Store all test data into Factory classes on startup
 */
public class SetupTests {

  static {
    // Ignore any logging messages
    Logger.getRootLogger().addAppender(new NullAppender());
    TestData.storeTestData();
  }

  @Test
  public void dummyMethodsothisClassisaddedtoJunit() {
  }
}
