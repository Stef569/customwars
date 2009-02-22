package test.com.customwars.client.model.map.gameobject;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class CityXStreamTest {
  private XStream xStream = new XStream(new DomDriver());

  @Before
  public void beforeEachTest() {
    // When we find a city tag, create a City object
    // Using Reflection
    xStream.alias("city", City.class);

    // You can use omitField at deserialization time currently only to tell XStream
    // to omit the val tag if your class no longer has the field to omit. Otherwise
    // it would though an exception.
    //
    // As alternative you can declare val as transient member, then the XML tag is
    // ignored automatically.
    // xStream.omitField(GameObject.class, "changeSupport");

    // id and name are read from attributes, not elements
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    CityFactory.clear();
  }

  @Test
  public void cityFromXml() {
    String validCityXml = "<city id='0' name='Factory'>" +
            "  <description>Can produce units</description>" +
            "  <defenseBonus>0</defenseBonus>" +
            "  <height>0</height>" +
            "  <moveCosts>" +
            "    <int>1</int>" +
            "    <int>1</int>" +
            "    <int>1</int>" +
            "    <int>2</int>" +
            "    <int>1</int>" +
            "    <int>127</int>" +
            "  </moveCosts>" +
            "  <vision>1</vision>" +
            "  <maxCapCount>20</maxCapCount>" +
            "  <healRate>1</healRate>" +
            "  <minHealRange>1</minHealRange>" +
            "  <maxHealRange>1</maxHealRange>" +
            "  <capCount>10</capCount>" +
            "  <funds>0</funds>" +
            "</city>";
    City city = (City) xStream.fromXML(validCityXml);
    CityFactory.addCity(city);
    CityFactory.getRandomCity();

    Assert.assertEquals(0, city.getID());
    Assert.assertEquals("Factory", city.getName());
    Assert.assertEquals(0, city.getID());
    Assert.assertEquals(1, city.getVision());
    //Assert.assertEquals(0, city.getCapCountPercentage());
  }

  @Test(expected = ConversionException.class)
  public void inValidcityFromXml() {
    String inValidCityXml = "<city id='0' name='Factory'>" +
            "  <height>999999999999999999999999</height>" +
            "</city>";
    City city = (City) xStream.fromXML(inValidCityXml);
    CityFactory.addCity(city);
    CityFactory.getRandomCity();
    Assert.assertEquals(0, city.getID());
    Assert.assertEquals("Factory", city.getName());
    Assert.assertEquals(1, city.getVision());
  }

  @Test
  public void testCityDefaults() {
    String inValidCityXml = "<city id='0' name='Factory'>" +
            "  <moveCosts/>" +
            "</city>";
    City city = (City) xStream.fromXML(inValidCityXml);
    CityFactory.addCity(city);
    CityFactory.getRandomCity();

    // Default to 1, if not in xml
    Assert.assertEquals(1, city.getMinHealRange());
    Assert.assertEquals(1, city.getMaxHealRange());
  }
}
