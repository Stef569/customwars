package com.customwars.client.model.co;

import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.script.ScriptManager;
import com.customwars.client.script.ScriptedCO;
import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import slick.HardCodedGame;

public class ScriptedCOTest {
  private static CO scriptedAndyCO;
  private static ScriptManager scriptManager;

  @BeforeClass
  public static void beforeAllTests() {
    BasicConfigurator.configure();
    TestData.storeTestData();
    scriptManager = new ScriptManager();
    scriptManager.init("resources/testData/coScript.bsh");

    CO andy = new BasicCO("andy");
    scriptedAndyCO = new ScriptedCO(andy, scriptManager);
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testScriptMethodReturnValues() {
    Assert.assertEquals("andy", scriptedAndyCO.getName());
    // Expected numbers come from the script methods
    // see resources/testData/coScript.bsh
    Assert.assertEquals(350, scriptedAndyCO.getAttackBonusPercentage(null, null));
    Assert.assertEquals(200, scriptedAndyCO.getDefenseBonusPercentage(null, null));
    Assert.assertEquals(8, scriptedAndyCO.unitMovementHook(null, 6));
  }

  @Test
  public void testScriptMethodsInScriptFile() {
    Assert.assertTrue(scriptManager.isMethod("andy_getAttackBonusPercentage"));
    Assert.assertTrue(scriptManager.isMethod("andy_getDefenseBonusPercentage"));
    Assert.assertTrue(scriptManager.isMethod("andy_unitMovementHook"));
    Assert.assertTrue(scriptManager.isMethod("andy_power"));
    Assert.assertTrue(scriptManager.isMethod("andy_superPower"));
    Assert.assertFalse(scriptManager.isMethod("unitPriceHook"));
  }

  @Test
  public void testNotExistingScriptMethod() {
    // Not in script, returns the input value
    Assert.assertEquals(5, scriptedAndyCO.unitPriceHook(5));
  }

  @Test
  public void testPowerScriptMethod() {
    Game game = HardCodedGame.getGame();
    Unit unit = UnitFactory.getUnit("mech");
    game.setActiveUnit(unit);
    // mech is the active unit in the game
    // game is passed to the scripted co method
    scriptedAndyCO.power(game);

    // The power decreased the inf hp -2
    Assert.assertEquals(8, unit.getHp());
  }

  @Test
  public void testSuperPowerScriptMethod() {
    Game game = HardCodedGame.getGame();
    Unit unit = UnitFactory.getUnit("mech");
    game.setActiveUnit(unit);
    // mech is the active unit in the game
    // game is passed to the scripted co method
    scriptedAndyCO.superPower(game);
    // 10-4=6
    Assert.assertEquals(6, unit.getHp());

    Assert.assertEquals("When the super power is active 400 is the att bonus", 400, scriptedAndyCO.getAttackBonusPercentage(null, null));
    scriptedAndyCO.deActivateSuperPower();
    Assert.assertEquals("If super power off, 350 is the att bonus", 350, scriptedAndyCO.getAttackBonusPercentage(null, null));
  }

  @Test
  public void testGreyField() {
    Game game = HardCodedGame.getGame();
    CO scriptedGreyfieldCO = new ScriptedCO(new BasicCO("greyfield"), scriptManager);
    Unit attacker = UnitFactory.getUnit("mech");
    game.getPlayerByID(0).addUnit(attacker);

    Unit defender = UnitFactory.getUnit("mech");
    game.getPlayerByID(1).addUnit(defender);

    Assert.assertEquals(100, scriptedGreyfieldCO.getAttackBonusPercentage(attacker, defender));
    scriptedGreyfieldCO.power(game);
    Assert.assertTrue(scriptedGreyfieldCO.isPowerActive());
    Assert.assertFalse(scriptedGreyfieldCO.isSuperPowerActive());
    Assert.assertEquals(110, scriptedGreyfieldCO.getAttackBonusPercentage(attacker, defender));
  }
}

