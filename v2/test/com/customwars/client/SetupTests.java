package com.customwars.client;

import com.customwars.client.model.TestData;
import org.junit.Test;

/**
 * Store all test data into Factory classes on startup
 */
public class SetupTests {

  static {
    TestData.storeTestData();
  }

  @Test
  public void dummyMethodsothisClassisaddedtoJunit() {
  }
}
