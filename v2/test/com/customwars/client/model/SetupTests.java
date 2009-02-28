package com.customwars.client.model;

import com.customwars.client.model.testdata.TestData;
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
