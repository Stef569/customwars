package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TestData;
import com.customwars.client.tools.XStreamUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests regarding reading unit+weapon data from xml
 */
public class UnitXStreamTest {
  private static final XStream xStream = new XStream(new DomDriver());

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();

    // When we find a unit tag, create a UnitStats object
    xStream.alias("unit", UnitStats.class);
    XStreamUtil.useReflectionFor(xStream, UnitStats.class);

    // id, imgRowID and name are read from attributes, not elements
    XStreamUtil.useReflectionFor(xStream, UnitStats.class);
    xStream.useAttributeFor(UnitStats.class, "unitID");
    xStream.useAttributeFor(UnitStats.class, "imgRowID");
    xStream.useAttributeFor(UnitStats.class, "name");

    xStream.alias("unitID", String.class);
    xStream.alias("id", String.class);
  }

  @Before
  public void beforeEachTest() {
    UnitFactory.clear();
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void validUnitXml() {
    String unitXml =
      "<unit unitID='1' name='Inf'>" +
        "  <description></description>" +
        "  <price>3000</price>" +
        "  <movement>3</movement>" +
        "  <vision>5</vision>" +
        "  <maxHp>20</maxHp>" +
        "  <maxSupplies>20</maxSupplies>" +
        "  <suppliesPerTurn>0</suppliesPerTurn>" +
        "  <canCapture>true</canCapture>" +
        "  <armyBranch>LAND</armyBranch>" +
        "  <movementType>0</movementType>" +
        "</unit>";
    UnitStats unitStats = (UnitStats) xStream.fromXML(unitXml);
    UnitFactory.addUnit(new Unit(unitStats));
    Unit unitCopy = UnitFactory.getUnit(1);

    Assert.assertEquals(1, unitCopy.getStats().getID());
    Assert.assertEquals("Inf", unitCopy.getStats().getName());
    Assert.assertTrue(unitCopy.getStats().canCapture());
    Assert.assertSame(unitCopy.getArmyBranch(), ArmyBranch.LAND);
  }

  @Test
  /**
   * The case of:
   * armybranch LAND matters
   * true doesn't matter
   */
  public void unitFromXMLStringCaseCheck() {
    String unitXml =
      "<unit unitID='1' name='Inf'>" +
        "  <canCapture>TrUe</canCapture>" +
        "  <armyBranch>LAND</armyBranch>" +
        "  <description></description>" +
        "</unit>";
    UnitStats unitStats = (UnitStats) xStream.fromXML(unitXml);
    UnitFactory.addUnit(new Unit(unitStats));
    Unit unitCopy = UnitFactory.getUnit(1);

    Assert.assertTrue(unitCopy.getStats().canCapture());
    Assert.assertSame(unitCopy.getArmyBranch(), ArmyBranch.LAND);
  }
}