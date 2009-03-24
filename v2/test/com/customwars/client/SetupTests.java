package com.customwars.client;

import com.customwars.client.model.TestData;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

/**
 * Store all test data into Factory classes on startup
 */
public class SetupTests {

  static {
    BasicConfigurator.configure();
    TestData.storeTestData();
  }

  @Test
  public void dummyMethodsothisClassisaddedtoJunit() {
  }
}
