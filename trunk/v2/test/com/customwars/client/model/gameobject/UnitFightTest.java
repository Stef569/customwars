package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import slick.HardCodedGame;

import java.awt.Color;

public class UnitFightTest {
  private Player p1;
  private Player p2;
  private Map<Tile> map;

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @Before
  public void beforeEachTest() {
    map = HardCodedGame.getMap();
    // 2 Non allied Players
    p1 = new Player(8, Color.GREEN, false, null, "Jos", 0, 0, false);
    p2 = new Player(250, Color.BLUE, false, null, "Bob", 0, 1, false);
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testDirectAttack() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    tank1.setOwner(p1);
    tank1.setHp(80);
    map.getTile(0, 0).add(tank1);

    Unit tank2 = UnitFactory.getUnit(TestData.TANK);
    tank2.setOwner(p2);
    tank2.setHp(40);
    map.getTile(0, 1).add(tank2);

    UnitFight fight = new UnitFight();
    fight.initFight(tank1, tank2);
    fight.startFight();

    Assert.assertTrue(tank2.isDestroyed());
  }

  @Test
  public void testCounterAttack() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 0).add(tank1);
    tank1.setOwner(p1);

    Unit tank2 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 1).add(tank2);
    tank2.setOwner(p2);

    UnitFight fight = new UnitFight();
    fight.initFight(tank1, tank2);
    fight.startFight();

    // Both tanks are damaged, because of counter attack
    Assert.assertNotSame(tank1.getHp(), tank1.getMaxHp());
    Assert.assertNotSame(tank2.getHp(), tank2.getMaxHp());
  }

  /**
   * When an Artillery unit(indirect) attacks a target,
   * then the target cannot counter attack unless directly adjacent.
   */
  @Test
  public void testIndirectAttack() {
    Unit artillery = UnitFactory.getUnit(TestData.ARTILLERY);
    map.getTile(0, 0).add(artillery);
    artillery.setOwner(p1);

    Unit target = UnitFactory.getUnit(TestData.MECH);
    map.getTile(2, 0).add(target);
    target.setOwner(p2);

    int expectedHpPercentage = artillery.getHpPercentage();
    int targetHpPercentage = target.getHpPercentage();

    UnitFight fight = new UnitFight();
    fight.initFight(artillery, target);
    fight.startFight();

    // No counter attack, attacker hp didn't change
    Assert.assertEquals(expectedHpPercentage, artillery.getHpPercentage());

    // The target is damaged, defender hp changed
    Assert.assertNotSame(targetHpPercentage, target.getHpPercentage());
  }

  @Test
  public void testRocketCounterAttacking() {
    Unit rocket = UnitFactory.getUnit(TestData.ROCKETS);
    map.getTile(0, 0).add(rocket);
    rocket.setOwner(p1);

    Unit inf = UnitFactory.getUnit(TestData.INF);

    map.getTile(0, 1).add(inf);
    inf.setOwner(p2);

    // The inf is outside of the attack zone of the rocket
    // and the rocket has a min attack range of 3 It can't counter attack.
    Fight fight = new UnitFight();
    fight.initFight(inf, rocket);
    fight.startFight();

    Assert.assertEquals(inf.getMaxHp(), inf.getHp());
  }

  @Test
  public void testTankWithoutPrimaryAmmo() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 0).add(tank1);
    tank1.setOwner(p1);

    Unit tank2 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 1).add(tank2);
    tank2.setOwner(p2);

    tank1.getPrimaryWeapon().setAmmo(0);

    // tank1 has no primary ammo so it will use it's secondary weapon to attack the tank...
    Fight fight = new UnitFight();
    fight.initFight(tank1, tank2);

    // 6 comes from the alt damage table tank vs tank.
    Assert.assertEquals(6, fight.getAttackDamagePercentage());
  }

  @Test
  /**
   * Defender will not counter attack if it will kill him
   */
  public void testSuicidalCounterAttack() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 1).add(tank1);
    tank1.setOwner(p2);
    tank1.setHp(100);

    Unit mech = UnitFactory.getUnit(TestData.MECH);
    map.getTile(0, 0).add(mech);
    mech.setOwner(p1);
    mech.setHp(90);

    Fight fight = new UnitFight();
    fight.initFight(tank1, mech);
    fight.startFight();

    Assert.assertEquals(tank1.getHp(), tank1.getMaxHp());
  }

  @Ignore
  // todo this test doesn't pass!
  public void completeOwnageTest() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 1).add(tank1);
    tank1.setOwner(p2);

    Unit mech = UnitFactory.getUnit(TestData.MECH);
    map.getTile(0, 0).add(mech);
    mech.setOwner(p1);
    mech.setHp(5);

    Fight fight = new UnitFight();
    fight.initFight(tank1, mech);

    // The fight should return an attack percentage of 100
    //  or more since the tank is going to kill the mech
    Assert.assertTrue(fight.getAttackDamagePercentage() >= 100);
  }
}
