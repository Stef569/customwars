package com.customwars.client.io.loading;

import com.customwars.client.io.converter.TerrainConverter;
import com.customwars.client.io.converter.UnitWeaponConverter;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.customwars.client.model.map.Range;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.tools.XStreamUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Loads: Damage tables,
 * Model classes to their Factories ie
 * Terrain -> TerrainFactory
 * Weapon -> WeaponFactory
 * Unit -> UnitFactory
 * City -> CityFactory
 *
 * @author stefan
 */
public class ModelLoader implements CWResourceLoader {
  private static final String XML_DATA_TERRAIN_FILE = "baseTerrains.xml";
  private static final String XML_DATA_ALL_TERRAIN_FILE = "terrains.xml";
  private static final String XML_DATA_WEAPONS_FILE = "weapons.xml";
  private static final String XML_DATA_UNITS_FILE = "units.xml";
  private static final String XML_DATA_CITY_FILE = "cities.xml";
  private static final String BASE_DMG_FILE = "baseDMG.txt";
  private static final String ALT_DMG_FILE = "altDMG.txt";
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
    xStream.useAttributeFor(Weapon.class, "id");
    xStream.useAttributeFor(Weapon.class, "name");
    xStream.alias("fireRange", Range.class);
    xStream.alias("armyBranch", ArmyBranch.class);
    InputStream weaponStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_WEAPONS_FILE);
    Collection<Weapon> weapons = (Collection<Weapon>) XStreamUtil.readObject(xStream, weaponStream);
    WeaponFactory.addWeapons(weapons);
  }

  @SuppressWarnings("unchecked")
  private void loadUnits() {
    xStream.registerConverter(new UnitWeaponConverter());
    xStream.alias("unit", Unit.class);
    xStream.alias("primaryWeapon", Weapon.class);
    xStream.alias("secondaryWeapon", Weapon.class);
    xStream.useAttributeFor(Unit.class, "unitID");
    xStream.useAttributeFor(Unit.class, "imgRowID");
    xStream.useAttributeFor(Unit.class, "name");
    xStream.alias("supplyRange", Range.class);
    InputStream unitStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_UNITS_FILE);
    Collection<Unit> units = (Collection<Unit>) XStreamUtil.readObject(xStream, unitStream);
    UnitFactory.addUnits(units);
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
    InputStream baseDamageStream = null, altDamageStream = null;

    try {
      baseDamageStream = ResourceLoader.getResourceAsStream(modelResPath + BASE_DMG_FILE);
      UnitFight.setBaseDMG(loadDamageTable(baseDamageStream));

      altDamageStream = ResourceLoader.getResourceAsStream(modelResPath + ALT_DMG_FILE);
      UnitFight.setAltDMG(loadDamageTable(altDamageStream));
    } finally {
      IOUtil.closeStream(baseDamageStream);
      IOUtil.closeStream(altDamageStream);
    }
  }

  private static int[][] loadDamageTable(InputStream stream) throws IOException {
    DamageParser damageParser = new DamageParser(stream);
    damageParser.load();
    return damageParser.getDmgTable();
  }
}
