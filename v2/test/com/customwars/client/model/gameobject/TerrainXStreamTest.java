package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.customwars.client.tools.XStreamUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TerrainXStreamTest {
  private static final XStream xStream = new XStream(new DomDriver());

  @BeforeClass
  public static void beforeAllTest() {
    // When we find a terrain tag, create a Terrain object
    // Using Reflection
    xStream.alias("terrain", Terrain.class);
    XStreamUtil.useReflectionFor(xStream, Terrain.class);

    // id and name are read from attributes, not elements
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
  }

  @Before
  public void beforeEachTest() {
    TerrainFactory.clear();
  }

  @AfterClass
  public static void afterAllTest() {
    TestData.storeTestData();
  }

  @Test
  public void testLoadingOfTerrainsFromXml() throws IOException {
    // HardCoded valid xml string
    String terrainXML = "<list>" +
      "  <terrain id='0' name='plain'>" +
      "    <description/>" +
      "    <defenseBonus>0</defenseBonus>" +
      "    <height>0</height>" +
      "    <moveCosts>" +
      "        <int>1</int>" +
      "        <int>1</int>" +
      "        <int>1</int>" +
      "        <int>2</int>" +
      "        <int>1</int>" +
      "        <int>127</int>" +
      "    </moveCosts>" +
      "    <hidden>false</hidden>" +
      "  </terrain>" +
      "<terrain id='1' name='forrest'>" +
      "    <description></description>" +
      "    <defenseBonus>0</defenseBonus>" +
      "    <height>0</height>" +
      "    <moveCosts>" +
      "        <int>1</int>" +
      "        <int>1</int>" +
      "        <int>1</int>" +
      "        <int>2</int>" +
      "        <int>1</int>" +
      "        <int>127</int>" +
      "    </moveCosts>" +
      "    <hidden>false</hidden>" +
      "  </terrain>" +
      "</list>";

    // Magic, we retrieve a list of Terrain objects!
    @SuppressWarnings("unchecked")
    List<Terrain> terrains = (List<Terrain>) xStream.fromXML(terrainXML);
    for (Terrain terrain : terrains) {
      Assert.assertNotNull(terrain);
      TerrainFactory.addTerrain(terrain);
    }

    Terrain firstTerrain = TerrainFactory.getTerrain(0);
    Assert.assertEquals(0, firstTerrain.getID());
    Assert.assertEquals("plain", firstTerrain.getName());

    // Description is not set in the xml, default to ""!
    Assert.assertEquals("", firstTerrain.getDescription());
    Assert.assertEquals(2, TerrainFactory.countTerrains());
  }

  /**
   * IllegalArgumentException: 1800 is not valid
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTerrainXMLInvalidMovecostInput_1800() {
    // HardCoded invalid xml string
    // <int>1800</int> is not within min and max movecost
    String invalidTerrainXML =
      "  <terrain id='0' name='plain'>" +
        "    <moveCosts>" +
        "        <int>1800</int>" +
        "    </moveCosts>" +
        "  </terrain>";

    Terrain terrain = (Terrain) xStream.fromXML(invalidTerrainXML);
    TerrainFactory.addTerrain(terrain);
    TerrainFactory.getRandomTerrain();
  }

  /**
   * IllegalArgumentException: 0 is not valid
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTerrainXMLInvalidMovecostInput_0() {
    // HardCoded invalid xml string
    // <int>0</int> is not within min and max movecost
    String invalidTerrainXML =
      "  <terrain id='0' name='plain'>" +
        "    <moveCosts>" +
        "        <int>0</int>" +
        "    </moveCosts>" +
        "  </terrain>";

    Terrain terrain = (Terrain) xStream.fromXML(invalidTerrainXML);
    TerrainFactory.addTerrain(terrain);
    TerrainFactory.getRandomTerrain();
  }

  /**
   * IllegalArgumentException: addTerrain(terrain) will not add the duplicate ID
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTerrainXML() {
    // Contains duplicate ID
    String invalidTerrainXML = "<list>" +
      "  <terrain id='1' name='plain1'>" +
      "    <moveCosts/>" +
      "  </terrain>" +
      "  <terrain id='1' name='plain2'>" +
      "    <moveCosts/>" +
      "  </terrain>" +
      "</list>";

    @SuppressWarnings("unchecked")
    List<Terrain> terrainsFromXml = (List<Terrain>) xStream.fromXML(invalidTerrainXML);
    for (Terrain terrain : terrainsFromXml) {
      TerrainFactory.addTerrain(terrain);
    }
  }

  /**
   * ConversionException: DefenseBonus should be defenseBonus
   */
  @Test(expected = ConversionException.class)
  public void testIllegalFieldCase() {
    // <DefenseBonus>0</DefenseBonus> upper case 'D' is invalid
    // should match the field in the class
    String invalidTerrainXML = "<list>" +
      "  <terrain id='0' name='plain'>" +
      "    <DefenseBonus>0</DefenseBonus>" +
      "    <height>0</height>" +
      "    <moveCosts>" +
      "        <int>5</int>" +
      "    </moveCosts>" +
      "  </terrain>" +
      "  <terrain id='1' name='plain'>" +
      "    <defenseBonus>0</defenseBonus>" +
      "    <height>0</height>" +
      "    <moveCosts>" +
      "        <int>5</int>" +
      "    </moveCosts>" +
      "  </terrain>" +
      "</list>";

    @SuppressWarnings("unchecked")
    List<Terrain> terrainsFromXml = (List<Terrain>) xStream.fromXML(invalidTerrainXML);
    for (Terrain terrain : terrainsFromXml) {
      TerrainFactory.addTerrain(terrain);
    }
  }
}
