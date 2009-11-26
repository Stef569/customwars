package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TestData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test Storing/retrieving weapons in the WeaponFactory
 */
public class WeaponFactoryTest {

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testgetNonExistingWeapon() {
    WeaponFactory.getWeapon("ASZMG");
  }

  @Test
  public void testRetrieveSMGWeapon() {
    // the case doesn't matter, weapons are stored in upper case
    Assert.assertTrue(WeaponFactory.hasWeapon(TestData.SMG));
    Assert.assertTrue(WeaponFactory.hasWeapon("SmG"));
    Assert.assertNotNull(WeaponFactory.getWeapon("Smg"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddingDuplicateWeapons() {
    Weapon weapon = new Weapon(TestData.SMG, "", null, 0, false, Arrays.asList(ArmyBranch.LAND));
    WeaponFactory.addWeapon(weapon);
  }
}
