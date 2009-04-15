package com.customwars.client.model.gameobject;

import com.customwars.client.io.converter.UnitWeaponConverter;
import com.customwars.client.model.TestData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests regarding reading unit+weapon data from xml
 *
 * @author stefan
 */
public class UnitXStreamTest {
  private static XStream xStream = new XStream(new DomDriver());

  @BeforeClass
  public static void beforeAllTest() {
    // When we find a unit tag, create a Unit object
    xStream.alias("unit", Unit.class);
    // When we find a Weapon, use our own converter
    xStream.alias("primaryWeapon", Weapon.class);
    xStream.alias("secondaryWeapon", Weapon.class);
    xStream.registerConverter(new UnitWeaponConverter());

    // id and name are read from attributes, not elements
    xStream.useAttributeFor(Unit.class, "id");
    xStream.useAttributeFor(Unit.class, "name");
  }

  @Before
  public void beforeEachTest() {
    UnitFactory.clear();
  }

  @AfterClass
  public static void afterAllTest() {
    TestData.storeTestData();
  }

  @Test
  public void validUnitXml() {
    String unitXml = "<unit id='0' name='Inf'>" +
            "  <description></description>" +
            "  <price>3000</price>" +
            "  <movement>3</movement>" +
            "  <vision>5</vision>" +
            "  <maxHp>20</maxHp>" +
            "  <maxSupplies>20</maxSupplies>" +
            "  <dailyUse>0</dailyUse>" +
            "  <canCapture>true</canCapture>" +
            "  <armyBranch>1</armyBranch>" +
            "  <movementType>0</movementType>" +
            "</unit>";
    Unit unit = (Unit) xStream.fromXML(unitXml);
    UnitFactory.addUnit(unit);
    Unit unitCopy = UnitFactory.getRandomUnit();

    Assert.assertEquals(true, unitCopy.canCapture());
  }

  @Test
  public void unitWithWeapon() {
    String unitXml = "<unit id='0' name='Infantry'>" +
            "<primaryWeapon id='0'>" +
            "<ammo>99</ammo>" +
            "</primaryWeapon>" +
            "</unit>";

    // Magic! the weapon is retrieved from the UnitFactory and added to the unit as primaryWeapon
    Unit unit = (Unit) xStream.fromXML(unitXml);
    Assert.assertEquals(TestData.SMG, unit.getPrimaryWeapon().getID());
    Assert.assertEquals(WeaponFactory.getWeapon(TestData.SMG).getName(), unit.getPrimaryWeapon().getName());
    Assert.assertEquals(99, unit.getPrimaryWeapon().getAmmo());
  }

  @Test
  public void unitWithPrimaryandSecondaryWeapon() {
    String unitXml = "<unit id='0' name='Infantry'>" +
            "<primaryWeapon id='0'>" +
            "<ammo>99</ammo>" +
            "</primaryWeapon>" +
            "<secondaryWeapon id='0'>" +
            "<ammo>12</ammo>" +
            "</secondaryWeapon>" +
            "</unit>";

    // Magic! the weapon is retrieved from the Weaponfactory and added to the unit as primaryWeapon
    Unit unit = (Unit) xStream.fromXML(unitXml);
    UnitFactory.addUnit(unit);
    Unit unitCopy = UnitFactory.getRandomUnit();

    Weapon smg = WeaponFactory.getWeapon(TestData.SMG);
    Assert.assertEquals(TestData.SMG, unitCopy.getPrimaryWeapon().getID());
    Assert.assertEquals(smg.getName(), unitCopy.getPrimaryWeapon().getName());
    Assert.assertEquals(99, unitCopy.getPrimaryWeapon().getAmmo());
    Assert.assertEquals(99, unitCopy.getPrimaryWeapon().getMaxAmmo());

    Assert.assertEquals(smg.getID(), unitCopy.getSecondaryWeapon().getID());
    Assert.assertEquals(smg.getName(), unitCopy.getSecondaryWeapon().getName());
    Assert.assertEquals(12, unitCopy.getSecondaryWeapon().getAmmo());
    Assert.assertEquals(12, unitCopy.getSecondaryWeapon().getMaxAmmo());
  }

  @Test
  /**
   * Xstream defaults to null when description or name is not included in the xml
   * Make sure that when the unit is added to the UnitFactory the name and description are set to "" instead of null
   * to avoid nullPointerExceptions.
   */
  public void unitWithNoInfo() {
    String unitXml = "<unit id='0'>" +
            "</unit>";
    Unit unit = (Unit) xStream.fromXML(unitXml);
    UnitFactory.addUnit(unit);
    Unit unitCopy = UnitFactory.getUnit(0);

    Assert.assertEquals("", unitCopy.getDescription());
    Assert.assertEquals("", unitCopy.getName());
  }
}
