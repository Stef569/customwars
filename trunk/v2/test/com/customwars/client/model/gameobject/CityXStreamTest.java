package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CityXStreamTest {
    private static XStream xStream = new XStream(new DomDriver());

    @BeforeClass
    public static void beforeAllTest() {
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
    }

    @Before
    public void beforeEachTest() {
        CityFactory.clear();
    }

    @AfterClass
    /**
     * We messed with CityFactory, restore the test data
     */
    public static void afterAllTest() {
        TestData.storeTestData();
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
                "  <capCount>10</capCount>" +
                "  <funds>0</funds>" +
                "</city>";
        // Parse xml data into City object
        City city = (City) xStream.fromXML(validCityXml);
        // Add the city to the factory, invoking init and reset on it
        // cityCopy now contains valid data.
        CityFactory.addCity(city);
        City cityCopy = CityFactory.getRandomCity();

        Assert.assertEquals(0, cityCopy.getID());
        Assert.assertEquals("Factory", cityCopy.getName());
        Assert.assertEquals(0, cityCopy.getID());
        Assert.assertEquals(1, cityCopy.getVision());

        // capCount was set to 10 in the xml, but the city has been validated and
        // capCount is set to 0. The cap percentage is now 0.
        Assert.assertEquals(0, cityCopy.getCapCountPercentage());
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
}
