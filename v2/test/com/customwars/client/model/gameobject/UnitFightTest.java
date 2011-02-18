package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.script.ScriptManager;
import com.customwars.client.script.ScriptedCO;
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
  private Map map;

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @Before
  public void beforeEachTest() {
    map = HardCodedGame.getMap();
    // 2 Non allied Players
    p1 = new Player(8, Color.GREEN, "Jos", 0, 0, false);
    p2 = new Player(250, Color.BLUE, "Bob", 0, 1, false);
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

    UnitFight fight = new UnitFight(map, tank1, tank2);
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

    UnitFight fight = new UnitFight(map, tank1, tank2);
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

    UnitFight fight = new UnitFight(map, artillery, target);
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
    Fight fight = new UnitFight(map, inf, rocket);
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
    Fight fight = new UnitFight(map, tank1, tank2);

    // 6 comes from the alt damage table tank vs tank.
    Assert.assertEquals(6, fight.getAttackDamagePercentage());
  }

  @Ignore
  /**
   * todo Defender will not counter attack if it will kill him
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

    Fight fight = new UnitFight(map, tank1, mech);
    fight.startFight();

    Assert.assertEquals(tank1.getHp(), tank1.getMaxHp());
  }

  @Test
  public void testMechAmmoDecreaseAfterFight() {
    Unit tank1 = UnitFactory.getUnit(TestData.TANK);
    map.getTile(0, 1).add(tank1);
    tank1.setOwner(p2);

    Unit mech = UnitFactory.getUnit(TestData.MECH);
    map.getTile(0, 0).add(mech);
    mech.setOwner(p1);
    mech.setHp(5);

    int startAmmo = mech.getPrimaryWeapon().getAmmo();
    Fight fight = new UnitFight(map, mech, tank1);
    fight.startFight();

    Assert.assertSame(startAmmo - 1, mech.getPrimaryWeapon().getAmmo());
  }

  @Test
  public void testInfVSInf() {
    Unit attInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 0).add(attInf);
    attInf.setOwner(p1);

    Unit defInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 1).add(defInf);
    defInf.setOwner(p2);

    Fight unitFight = new UnitFight(map, attInf, defInf);
    int preFightDmg = unitFight.getAttackDamagePercentage();

    // There are no modifiers (terrain,co)
    // See TestData Alternative damage chart
    Assert.assertEquals(55, preFightDmg);
    unitFight.startFight();
    Assert.assertEquals(7, attInf.getHp());
    Assert.assertEquals(4, defInf.getHp());
  }

  @Test
  public void testInfVSInfOnMountain() {
    Unit attInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 0).add(attInf);
    attInf.setOwner(p1);

    Unit defInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 1).add(defInf);
    map.getTile(0, 1).setTerrain(TerrainFactory.getTerrain("mountain"));
    defInf.setOwner(p2);

    Fight unitFight = new UnitFight(map, attInf, defInf);
    int preFightDmg = unitFight.getAttackDamagePercentage();

    Assert.assertEquals(39, preFightDmg);
    unitFight.startFight();
    Assert.assertEquals(6, attInf.getHp());
    Assert.assertEquals(6, defInf.getHp());
  }

  @Test
  public void testInfVsInfWithCO() {
    ScriptManager scriptManager = new ScriptManager();
    String sturmCOAttackMethod =
      "public int sturm_getAttackBonusPercentage() {" +
        "if (power || superPower) {return 130;} else {return 120;}" +
        "}";

    scriptManager.eval(sturmCOAttackMethod);
    COFactory.setScriptManager(scriptManager);
    CO sturm = new ScriptedCO(COFactory.getCO("sturm"), scriptManager);

    p1 = new Player(8, Color.GREEN, "Jos", 0, 0, false, sturm);
    Unit attInf = UnitFactory.getUnit("infantry");
    Unit defInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 0).add(attInf);
    attInf.setOwner(p1);

    map.getTile(0, 1).add(defInf);
    defInf.setOwner(p2);

    Fight unitFight = new UnitFight(map, attInf, defInf);
    int damagePercentage = unitFight.getAttackDamagePercentage();

    // Attacking infantry has Sturm as CO
    // Adding 20% to the attack value
    Assert.assertEquals(66, damagePercentage);
    unitFight.startFight();
  }

  @Test
  public void testInfVsInfWith2COS() {
    ScriptManager scriptManager = new ScriptManager();
    scriptManager.eval("public int sturm_getAttackBonusPercentage() {" +
      "if (power || superPower) {return 130;} else {return 120;}}");
    scriptManager.eval("public int andy_getDefenseBonusPercentage() {" +
      "if (power || superPower) {return 110;} else {return 100;}}");
    COFactory.setScriptManager(scriptManager);

    CO sturm = new ScriptedCO(COFactory.getCO("sturm"), scriptManager);
    CO andy = new ScriptedCO(COFactory.getCO("andy"), scriptManager);
    p1 = new Player(8, Color.GREEN, "Sturm", 0, 0, false, sturm);
    p2 = new Player(9, Color.BLUE, "Andy", 0, 1, false, andy);

    Unit attInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 0).add(attInf);
    attInf.setOwner(p1);

    Unit defInf = UnitFactory.getUnit("infantry");
    map.getTile(0, 1).add(defInf);
    defInf.setOwner(p2);

    Fight unitFight = new UnitFight(map, attInf, defInf);
    int damagePercentage = unitFight.getAttackDamagePercentage();

    // Attacking infantry has Sturm as CO
    // Adding 20% to the attack value
    // Defending infantry has andy as CO
    // Adding 10% to the defense value
    Assert.assertEquals(66, damagePercentage);
    unitFight.startFight();
    Assert.assertEquals(8, attInf.getHp());
    Assert.assertEquals(3, defInf.getHp());
  }
}
