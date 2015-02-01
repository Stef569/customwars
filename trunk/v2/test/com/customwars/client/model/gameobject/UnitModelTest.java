package com.customwars.client.model.gameobject;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.model.TestData;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class UnitModelTest {

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testBattleShip() {
    Unit battleShip = UnitFactory.getUnit("BATTLESHIP");

    Assert.assertFalse(battleShip.isDirect());
    Assert.assertTrue(battleShip.isInDirect());
    Assert.assertTrue(battleShip.isBallistic());
  }

  @Test
  public void testLander() {
    Unit lander = UnitFactory.getUnit("LANDER");

    Assert.assertFalse(lander.isDirect());
    Assert.assertFalse(lander.isInDirect());
    Assert.assertFalse(lander.isBallistic());
    Assert.assertFalse(lander.isInTransport());
    Assert.assertFalse(lander.isTransportFull());
    Assert.assertTrue(lander.getStats().canTransport());
    Assert.assertTrue(lander.getStats().canTransport("infantry"));
    Assert.assertFalse(lander.getStats().canBuildCity());
    Assert.assertFalse(lander.getStats().canCapture);
    Assert.assertFalse(lander.getStats().canDive);
  }

  @Test
  public void testCruiser() {
    Unit cruiser = UnitFactory.getUnit("CRUISER");

    Assert.assertTrue(cruiser.isDirect());
    Assert.assertFalse(cruiser.isInDirect());
    Assert.assertFalse(cruiser.isBallistic());
    Assert.assertFalse(cruiser.isTransportFull());
    Assert.assertFalse(cruiser.isInTransport());
    Assert.assertTrue(cruiser.getStats().canTransport());
    Assert.assertFalse(cruiser.getStats().canTransport("infantry"));
    Assert.assertTrue(cruiser.getStats().canTransport("tcopter"));
    Assert.assertFalse(cruiser.getStats().canBuildCity());
    Assert.assertFalse(cruiser.getStats().canCapture);
    Assert.assertFalse(cruiser.getStats().canDive);
  }
}
