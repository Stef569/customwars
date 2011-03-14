package com.customwars.client.io.loading;

import com.customwars.client.io.converter.COXmlConverter;
import com.customwars.client.io.converter.CityXmlConverter;
import com.customwars.client.io.converter.HexColorConverter;
import com.customwars.client.io.converter.TerrainXmlConverter;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.co.AbstractCO;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.co.COStyle;
import com.customwars.client.model.co.Power;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainConnection;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.TransportStats;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitStats;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.customwars.client.model.map.Range;
import com.customwars.client.tools.XStreamUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Loads: Damage tables and Model classes to their Factories ie
 * Terrain -> TerrainFactory
 * Weapon -> WeaponFactory
 * Unit -> UnitFactory
 * City -> CityFactory
 * Dmg table -> UnitFight
 * CO -> COFactory
 *
 * @author stefan
 */
@SuppressWarnings("unchecked")
public class ModelLoader implements CWResourceLoader {
  private static final String XML_DATA_TERRAIN_FILE = "baseTerrains.xml";
  private static final String XML_DATA_ALL_TERRAIN_FILE = "terrains.xml";
  private static final String XML_DATA_WEAPONS_FILE = "weapons.xml";
  private static final String XML_DATA_UNITS_FILE = "units.xml";
  private static final String XML_DATA_CITY_FILE = "baseCities.xml";
  private static final String XML_DATA_ALL_CITY_FILE = "cities.xml";
  private static final String DMG_XML_FILE = "damage.xml";
  private static final String XML_CO_TEXT_FILE = "coText.xml";
  private static final String XML_CO_STYLE_FILE = "coStyle.xml";
  private static final XStream xStream = new XStream(new DomDriver());
  private final String modelResPath;

  public ModelLoader(String modelResPath) {
    this.modelResPath = modelResPath;
  }

  public void load() throws IOException {
    loadTerrains();
    loadWeapons();
    loadUnits();
    loadCities();
    loadDamageTables();
    loadCOs();
    validateData();
  }

  private void loadTerrains() {
    xStream.alias("terrain", Terrain.class);
    XStreamUtil.useReflectionFor(xStream, Terrain.class);
    XStreamUtil.useReflectionFor(xStream, TerrainConnection.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    xStream.useAttributeFor(Terrain.class, "type");

    // Load Base Terrains, they contain the terrain stats
    InputStream baseTerrainsStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_TERRAIN_FILE);
    Collection<Terrain> baseTerrains = (Collection<Terrain>) XStreamUtil.readObject(xStream, baseTerrainsStream);

    // Load all terrains, use the type parameter to look up the base terrain, and copy the stats
    InputStream allTerrainStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_ALL_TERRAIN_FILE);
    xStream.registerConverter(new TerrainXmlConverter(baseTerrains));
    Collection<Terrain> terrains = (Collection<Terrain>) XStreamUtil.readObject(xStream, allTerrainStream);
    TerrainFactory.addTerrains(terrains);
    TerrainFactory.addBaseTerrains(baseTerrains);
  }

  private void loadWeapons() {
    xStream.alias("weapon", Weapon.class);
    XStreamUtil.useReflectionFor(xStream, Weapon.class);
    xStream.useAttributeFor(Weapon.class, "name");
    xStream.alias("fireRange", Range.class);
    xStream.alias("armyBranch", ArmyBranch.class);
    InputStream weaponStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_WEAPONS_FILE);
    Collection<Weapon> weapons = (Collection<Weapon>) XStreamUtil.readObject(xStream, weaponStream);
    WeaponFactory.addWeapons(weapons);
  }

  private void loadUnits() {
    xStream.alias("unit", UnitStats.class);
    XStreamUtil.useReflectionFor(xStream, UnitStats.class);
    xStream.useAttributeFor(UnitStats.class, "unitID");
    xStream.useAttributeFor(UnitStats.class, "imgRowID");
    xStream.useAttributeFor(UnitStats.class, "name");
    xStream.useAttributeFor(Range.class, "minRange");
    xStream.useAttributeFor(Range.class, "maxRange");

    xStream.aliasField("transports", UnitStats.class, "transportStats");
    xStream.addImplicitCollection(TransportStats.class, "transports");
    xStream.useAttributeFor(TransportStats.class, "maxTransportCount");
    xStream.aliasAttribute("max", "maxTransportCount");

    xStream.alias("supplyRange", Range.class);
    xStream.alias("unitID", String.class);
    xStream.alias("id", String.class);

    InputStream unitStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_UNITS_FILE);
    Collection<UnitStats> unitStats = (Collection<UnitStats>) XStreamUtil.readObject(xStream, unitStream);
    UnitFactory.addUnits(unitStats);
  }

  private void loadCities() {
    xStream.alias("city", City.class);
    XStreamUtil.useReflectionFor(xStream, City.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    xStream.useAttributeFor(Terrain.class, "type");
    xStream.useAttributeFor(City.class, "imgRowID");
    xStream.alias("armyBranch", ArmyBranch.class);
    xStream.alias("unitID", String.class);
    xStream.aliasField("connect", Terrain.class, "connection");

    // Load Base Cities, they contain the city stats
    InputStream baseCitiesStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_CITY_FILE);
    Collection<City> baseCities = (Collection<City>) XStreamUtil.readObject(xStream, baseCitiesStream);

    // Load all cities, use the type parameter to look up the base city, and copy the stats
    InputStream allCitiesStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_ALL_CITY_FILE);
    xStream.registerConverter(new CityXmlConverter(baseCities));
    Collection<City> cities = (Collection<City>) XStreamUtil.readObject(xStream, allCitiesStream);
    CityFactory.addCities(cities);
    CityFactory.addBaseCities(baseCities);
  }

  private void loadDamageTables() throws IOException {
    InputStream xmlDamageParserStream = ResourceLoader.getResourceAsStream(modelResPath + DMG_XML_FILE);
    XMLDamageParser xmlDamageParser = new XMLDamageParser(xmlDamageParserStream);
    xmlDamageParser.load();
  }

  public void loadCOs() {
    XStreamUtil.useReflectionFor(xStream, BasicCO.class);
    XStreamUtil.useReflectionFor(xStream, AbstractCO.class);
    XStreamUtil.useReflectionFor(xStream, COStyle.class);
    xStream.registerConverter(new HexColorConverter(), XStream.PRIORITY_VERY_HIGH);

    xStream.alias("COStyle", COStyle.class);
    xStream.useAttributeFor(COStyle.class, "name");
    xStream.useAttributeFor(COStyle.class, "id");

    xStream.alias("cos", List.class);
    xStream.alias("co", BasicCO.class);
    xStream.useAttributeFor(AbstractCO.class, "name");
    xStream.useAttributeFor(AbstractCO.class, "style");
    xStream.useAttributeFor(Power.class, "name");

    InputStream CoStyleXmlStream = ResourceLoader.getResourceAsStream(modelResPath + "co/" + XML_CO_STYLE_FILE);
    Collection<COStyle> CoStyles = (Collection<COStyle>) XStreamUtil.readObject(xStream, CoStyleXmlStream);

    xStream.registerConverter(new COXmlConverter(CoStyles));
    InputStream coXmlStream = ResourceLoader.getResourceAsStream(modelResPath + "co/" + XML_CO_TEXT_FILE);
    Collection<CO> cos = (Collection<CO>) XStreamUtil.readObject(xStream, coXmlStream);
    COFactory.addCOStyles(CoStyles);
    COFactory.addCOs(cos);
  }

  private void validateData() {
    for (Unit unit : UnitFactory.getAllUnits()) {
      unit.getStats().validate();
    }
    for (City city : CityFactory.getBaseCities()) {
      city.validate();
    }
  }
}