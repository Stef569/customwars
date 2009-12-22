package com.customwars.client.io.loading;

import com.customwars.client.io.converter.TerrainConverter;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
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
  private static final String XML_DATA_CITY_FILE = "cities.xml";
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
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    xStream.useAttributeFor(Terrain.class, "type");
    InputStream basicTerrainStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_TERRAIN_FILE);
    Collection<Terrain> basicTerrains = (Collection<Terrain>) XStreamUtil.readObject(xStream, basicTerrainStream);

    InputStream allTerrainStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_ALL_TERRAIN_FILE);
    xStream.registerConverter(new TerrainConverter(basicTerrains));
    Collection<Terrain> terrains = (Collection<Terrain>) XStreamUtil.readObject(xStream, allTerrainStream);
    TerrainFactory.addTerrains(terrains);
    TerrainFactory.addBaseTerrains(basicTerrains);
  }

  @SuppressWarnings("unchecked")
  private void loadWeapons() {
    xStream.alias("weapon", Weapon.class);
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
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    xStream.useAttributeFor(Terrain.class, "type");
    xStream.alias("armyBranch", ArmyBranch.class);
    InputStream cityStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_CITY_FILE);
    Collection<City> cities = (Collection<City>) XStreamUtil.readObject(xStream, cityStream);
    CityFactory.addCities(cities);
  }

  private void loadDamageTables() throws IOException {
    InputStream xmlDamageParserStream = ResourceLoader.getResourceAsStream(modelResPath + DMG_XML_FILE);
    XMLDamageParser xmlDamageParser = new XMLDamageParser(xmlDamageParserStream);
    xmlDamageParser.load();
  }
}
