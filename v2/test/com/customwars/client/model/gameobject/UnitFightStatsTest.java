package com.customwars.client.model.gameobject;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.XMLDamageParser;
import com.customwars.client.model.TestData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UnitFightStatsTest {

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    ModelLoader modelLoader = new ModelLoader("resources/testData/testGame/");
    modelLoader.load();

    InputStream damageStream = ResourceLoader.getResourceAsStream("resources/testData/testGame/damage.xml");
    new XMLDamageParser(damageStream).load();
  }

  @AfterClass
  public static void afterAllTests() throws IOException {
    TestData.clearTestData();
  }

  @Test
  public void test() {
    UnitFightStats stats = new UnitFightStats();
    stats.generate();

    List<Unit> infEnemies = stats.getTopEnemies("infantry");
    Assert.assertTrue(inCollection(infEnemies, "anti_air"));
    Assert.assertTrue(inCollection(infEnemies, "bomber"));
    Assert.assertTrue(inCollection(infEnemies, "heavy_tank"));
    Assert.assertTrue(inCollection(infEnemies, "rockets"));
    Assert.assertFalse(inCollection(infEnemies, "cruiser"));
    Assert.assertFalse(inCollection(infEnemies, "sub"));
    Assert.assertFalse(inCollection(infEnemies, "lander"));
    Assert.assertFalse(inCollection(infEnemies, "apc"));

    List<Unit> antiTankEnemies = stats.getTopEnemies("anti_tank");
    Assert.assertTrue(inCollection(antiTankEnemies, "bomber"));
    Assert.assertTrue(inCollection(antiTankEnemies, "rockets"));
    Assert.assertTrue(inCollection(antiTankEnemies, "mech"));
    Assert.assertTrue(inCollection(antiTankEnemies, "battleship"));
    Assert.assertFalse(inCollection(antiTankEnemies, "cruiser"));
    Assert.assertFalse(inCollection(antiTankEnemies, "sub"));
    Assert.assertFalse(inCollection(antiTankEnemies, "lander"));
    Assert.assertFalse(inCollection(antiTankEnemies, "apc"));

    List<Unit> bomberEnemies = stats.getTopEnemies("bomber");
    Assert.assertTrue(inCollection(bomberEnemies, "fighter"));
    Assert.assertTrue(inCollection(bomberEnemies, "cruiser"));
    Assert.assertTrue(inCollection(bomberEnemies, "missiles"));
    Assert.assertTrue(inCollection(bomberEnemies, "anti_air"));
    Assert.assertFalse(inCollection(bomberEnemies, "infantry"));
    Assert.assertFalse(inCollection(bomberEnemies, "mech"));
    Assert.assertFalse(inCollection(bomberEnemies, "lander"));
    Assert.assertFalse(inCollection(bomberEnemies, "apc"));

    List<Unit> landerEnemies = stats.getTopEnemies("lander");
    Assert.assertTrue(inCollection(landerEnemies, "sub"));
    Assert.assertTrue(inCollection(landerEnemies, "bomber"));
    Assert.assertFalse(inCollection(landerEnemies, "fighter"));
    Assert.assertFalse(inCollection(landerEnemies, "lander"));
    Assert.assertFalse(inCollection(landerEnemies, "apc"));
  }

  private boolean inCollection(List<Unit> units, String unitName) {
    for (Unit unit : units) {
      if (unit.getStats().getName().equals(unitName)) return true;
    }
    return false;
  }
}
