package com.customwars.client.io.loading;

import com.customwars.client.io.converter.CityXmlConverter;
import com.customwars.client.io.converter.TerrainXmlConverter;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainConnection;
import com.customwars.client.model.gameobject.TerrainFactory;
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

/**
 * Loads: Damage tables and Model classes to their Factories ie
 * Terrain -> TerrainFactory
 * Weapon -> WeaponFactory
 * Unit -> UnitFactory
 * City -> CityFactory
 * Dmg table -> UnitFight
 *
 * @author stefan
 */
public class ModelLoader implements CWResourceLoader {
  private static final String XML_DATA_TERRAIN_FILE = "baseTerrains.xml";
  private static final String XML_DATA_ALL_TERRAIN_FILE = "terrains.xml";
  private static final String XML_DATA_WEAPONS_FILE = "weapons.xml";
  private static final String XML_DATA_UNITS_FILE = "units.xml";
  private static final String XML_DATA_CITY_FILE = "baseCities.xml";
  private static final String XML_DATA_ALL_CITY_FILE = "cities.xml";
  private static final String DMG_XML_FILE = "damage.xml";
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
  }

  @SuppressWarnings("unchecked")
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

  @SuppressWarnings("unchecked")
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

  @SuppressWarnings("unchecked")
  private void loadUnits() {
    xStream.alias("unit", UnitStats.class);
    XStreamUtil.useReflectionFor(xStream, Unit.class);
    xStream.useAttributeFor(UnitStats.class, "unitID");
    xStream.useAttributeFor(UnitStats.class, "imgRowID");
    xStream.useAttributeFor(UnitStats.class, "name");
    xStream.alias("supplyRange", Range.class);
    InputStream unitStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_UNITS_FILE);
    Collection<UnitStats> unitStats = (Collection<UnitStats>) XStreamUtil.readObject(xStream, unitStream);
    UnitFactory.addUnits(unitStats);
  }

  @SuppressWarnings("unchecked")
  private void loadCities() {
    xStream.alias("city", City.class);
    XStreamUtil.useReflectionFor(xStream, City.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    xStream.useAttributeFor(Terrain.class, "type");
    xStream.useAttributeFor(City.class, "imgRowID");
    xStream.alias("armyBranch", ArmyBranch.class);
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
}
