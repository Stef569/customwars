package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

public class CityTest {
  private Map<Tile> map;
  private Player player1, player2;
  private City city;

  @Before
  public void beforeEachTest() {
    city = CityFactory.getCity(TestData.BASE);
    player1 = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 0, false);
    player2 = new Player(1, Color.BLUE, false, null, "JSR", 8500, 1, false);
    Terrain plain = TerrainFactory.getTerrain(TestData.PLAIN);
    map = new Map<Tile>(10, 10, 32, 4, false, plain);
    map.setFogOfWarOn(true);
  }

  @Test
  /**
   * A Unit captures a City, in 2 steps
   * The first time the unit tries to capture the city the capCount will be 50% (10/20)
   * The next attempt will fully capture the city 100% (20/20)
   * player2 is now owning the city,
   *
   * resetCapturing will put the capCount back to 0.
   *
   * The Unit Hp is used to increase the cap count
   */
  public void testValidCaptureCity() {
    city.setOwner(player1);

    Unit inf = UnitFactory.getUnit(TestData.INF);
    inf.setOwner(player2);

    city.capture(inf);
    Assert.assertEquals(50, city.getCapCountPercentage());

    city.capture(inf);
    Assert.assertEquals(100, city.getCapCountPercentage());
    Assert.assertEquals(player2, city.getOwner());

    city.resetCapturing();
    Assert.assertEquals(0, city.getCapCountPercentage());
    Assert.assertEquals(player2, city.getOwner());
  }

  @Test
  /**
   * 1. city owned by p1
   * 2. p2 captures city
   * 3. p1 recaptures the city
   *
   * p1 is now owning the city...
   */
  public void testMultipleCaptureAttempts() {
    map.getTile(0, 0).setTerrain(city);
    city.setOwner(player1);

    Unit capturingUnit = UnitFactory.getUnit(TestData.INF);
    capturingUnit.setOwner(player2);
    city.capture(capturingUnit);
    city.capture(capturingUnit);

    Unit capturingUnit2 = UnitFactory.getUnit(TestData.INF);
    capturingUnit2.setOwner(player1);
    city.capture(capturingUnit2);
    city.capture(capturingUnit2);

    Assert.assertEquals(city.getOwner(), player1);
  }

  @Test
  /**
   * A unit has 5 hp out of 10 max
   * a city with healRate 2 will bring the hp to 7!
   */
  public void testHealFromBuilding() {
    final int UNIT_HP = 5;
    Tile tile = map.getTile(0, 0);

    city.setOwner(player1);
    city.setLocation(tile);

    Unit unit = UnitFactory.getUnit(TestData.INF);
    unit.setHp(UNIT_HP * 10);      // 5/10 (internal 50/100)
    unit.setOwner(player1);
    tile.add(unit);

    Assert.assertEquals(true, city.canHeal(unit));
    city.heal(unit);
    int expectedHp = UNIT_HP + TestData.CITY_HEAL_RATE;
    Assert.assertEquals(expectedHp, unit.getHp());
    Assert.assertEquals(expectedHp * 10, unit.getHpPercentage());
  }

  @Test
  /**
   * A unit has 5 supplies out of 10 max
   * the city will supply the unit to max
   */
  public void testSupplyFromCity() {
    final int UNIT_SUPPLIES = 5;
    Tile tile = map.getTile(0, 0);

    city.setOwner(player1);
    city.setLocation(tile);

    // Create Unit on the city
    Unit unit = UnitFactory.getUnit(TestData.INF);
    unit.setSupplies(UNIT_SUPPLIES); // 5/10
    unit.setOwner(player1);
    tile.add(unit);

    Assert.assertEquals(true, city.canSupply(unit));
    city.supply(unit);
    Assert.assertEquals(unit.getMaxSupplies(), unit.getSupplies());
  }

  @Test
  // The factory produces Ground units
  // our Infantry is armybranch Ground so the factory can build this unit.
  public void testBuildingUnit() {
    City factory = CityFactory.getCity(TestData.FACTORY);
    map.getTile(4, 0).setTerrain(factory);
    factory.setOwner(player1);

    Unit unit = UnitFactory.getUnit(TestData.INF);
    Assert.assertEquals(true, factory.canBuild(unit));
  }
}
