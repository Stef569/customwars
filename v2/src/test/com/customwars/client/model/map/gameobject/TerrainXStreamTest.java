package test.com.customwars.client.model.map.gameobject;

import com.customwars.client.model.map.gameobject.Terrain;
import com.customwars.client.model.map.gameobject.TerrainFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author stefan
 */
public class TerrainXStreamTest {
  private XStream xStream = new XStream(new DomDriver());

  @Before
  public void beforeEachTest() {
    // When we find a terrain tag, create a Terrain object
    // Using Reflection
    xStream.alias("terrain", Terrain.class);
    // id and name are read from attributes, not elements
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
  }

  @Test
  public void testLoadingOfTerrainsFromXml() throws IOException {
    // HardCoded valid xml string
    String terrainXML = "<list>" +
            "  <terrain id='0' name='plain'>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>1</byte>" +
            "        <byte>1</byte>" +
            "        <byte>1</byte>" +
            "        <byte>2</byte>" +
            "        <byte>1</byte>" +
            "        <byte>127</byte>" +
            "    </moveCosts>" +
            "    <hidden>false</hidden>" +
            "  </terrain>" +
            "<terrain id='1' name='forrest'>" +
            "    <description></description>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>1</byte>" +
            "        <byte>1</byte>" +
            "        <byte>1</byte>" +
            "        <byte>2</byte>" +
            "        <byte>1</byte>" +
            "        <byte>127</byte>" +
            "    </moveCosts>" +
            "    <hidden>false</hidden>" +
            "  </terrain>" +
            "</list>";

    // Magic, we retrieve a list of Terrain objects!
    List<Terrain> terrains = (List<Terrain>) xStream.fromXML(terrainXML);
    for (Terrain terrain : terrains) {
      Assert.assertNotNull(terrain);
      TerrainFactory.addTerrain(terrain);
    }

    Terrain firstTerrain = TerrainFactory.getTerrain(0);
    Assert.assertEquals(0, firstTerrain.getID());
    Assert.assertEquals("plain", firstTerrain.getName());

    // Description is not set in the xml, default to ""
    Assert.assertEquals(null, firstTerrain.getDescription());
    Assert.assertEquals(TerrainFactory.countTerrains(), 2);
  }

  /**
   * ConversionException: 1800 is not valid
   */
  @Test(expected = ConversionException.class)
  public void testInvalidTerrainXMLInvalidByteInput() {
    // HardCoded invalid xml string
    // <byte>1800</byte> is not within Byte bounds -127 127
    String invalidTerrainXML = "<list>" +
            "  <terrain id='0' name='plain'>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>1800</byte>" +
            "    </moveCosts>" +
            "  </terrain>" +
            "</list>";

    xStream.fromXML(invalidTerrainXML);
  }

  /**
   * IllegalArgumentException: addTerrain(terrain) will not add the duplicate ID
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTerrainXML() {
    // HardCoded invalid xml string
    // contains duplicate ID
    String invalidTerrainXML = "<list>" +
            "  <terrain id='0' name='plain'>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>5</byte>" +
            "    </moveCosts>" +
            "  </terrain>" +
            "  <terrain id='0' name='plain'>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>5</byte>" +
            "    </moveCosts>" +
            "  </terrain>" +
            "</list>";

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
    // HardCoded invalid xml string
    // <DefenseBonus>0</defenseBonus> upper case D is invalid
    // should match the field in the class
    String invalidTerrainXML = "<list>" +
            "  <terrain id='0' name='plain'>" +
            "    <DefenseBonus>0</DefenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>5</byte>" +
            "    </moveCosts>" +
            "  </terrain>" +
            "  <terrain id='1' name='plain'>" +
            "    <defenseBonus>0</defenseBonus>" +
            "    <height>0</height>" +
            "    <moveCosts>" +
            "        <byte>5</byte>" +
            "    </moveCosts>" +
            "  </terrain>" +
            "</list>";

    List<Terrain> terrainsFromXml = (List<Terrain>) xStream.fromXML(invalidTerrainXML);
    for (Terrain terrain : terrainsFromXml) {
      TerrainFactory.addTerrain(terrain);
    }
  }
}
