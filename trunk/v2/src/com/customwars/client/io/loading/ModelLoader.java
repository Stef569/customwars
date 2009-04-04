package com.customwars.client.io.loading;

import com.customwars.client.io.converter.UnitWeaponConverter;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;
import tools.XStreamUtil;

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
public class ModelLoader {
  private static final String XML_DATA_TERRAIN_FILE = "terrains.xml";
  private static final String XML_DATA_WEAPONS_FILE = "weapons.xml";
  private static final String XML_DATA_UNITS_FILE = "units.xml";
  private static final String XML_DATA_CITY_FILE = "cities.xml";
  private static final String BASE_DMG_FILE = "baseDMG.txt";
  private static final String ALT_DMG_FILE = "altDMG.txt";
  private static final XStream xStream = new XStream(new DomDriver());
  private static final DamageParser damageParser = new DamageParser();
  private String modelResPath;

  public void loadModel() {
    if (LoadingList.isDeferredLoading()) {
      LoadingList.get().add(new DeferredModelLoader());
    } else {
      loadModelNow();
    }
  }

  private void loadModelNow() {
    loadTerrains();
    loadWeapons();
    loadUnits();
    loadCities();
    loadDamageTables();
  }

  private void loadTerrains() {
    xStream.alias("terrain", Terrain.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    InputStream terrainStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_TERRAIN_FILE);
    Collection<Terrain> terrains = (Collection<Terrain>) XStreamUtil.readObject(xStream, terrainStream);
    TerrainFactory.addTerrains(terrains);
  }

  private void loadWeapons() {
    xStream.alias("weapon", Weapon.class);
    xStream.useAttributeFor(Weapon.class, "id");
    xStream.useAttributeFor(Weapon.class, "name");
    InputStream weaponStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_WEAPONS_FILE);
    Collection<Weapon> weapons = (Collection<Weapon>) XStreamUtil.readObject(xStream, weaponStream);
    WeaponFactory.addWeapons(weapons);
  }

  private void loadUnits() {
    xStream.registerConverter(new UnitWeaponConverter());
    xStream.alias("unit", Unit.class);
    xStream.alias("primaryWeapon", Weapon.class);
    xStream.alias("secondaryWeapon", Weapon.class);
    xStream.useAttributeFor(Unit.class, "id");
    xStream.useAttributeFor(Unit.class, "name");
    InputStream unitStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_UNITS_FILE);
    Collection<Unit> units = (Collection<Unit>) XStreamUtil.readObject(xStream, unitStream);
    UnitFactory.addUnits(units);
  }

  private void loadCities() {
    xStream.alias("city", City.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    InputStream cityStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_CITY_FILE);
    Collection<City> cities = (Collection<City>) XStreamUtil.readObject(xStream, cityStream);
    CityFactory.addCities(cities);
  }

  private void loadDamageTables() {
    InputStream baseDamageStream = ResourceLoader.getResourceAsStream(modelResPath + BASE_DMG_FILE);
    InputStream altDamageStream = ResourceLoader.getResourceAsStream(modelResPath + ALT_DMG_FILE);

    try {
      UnitFight.setBaseDMG(damageParser.read(baseDamageStream));
      UnitFight.setAltDMG(damageParser.read(altDamageStream));
    } catch (IOException e) {
      throw new RuntimeException("Could not read damage table", e);
    }
  }

  public void setModelResPath(String modelResPath) {
    this.modelResPath = modelResPath;
  }

  private class DeferredModelLoader implements DeferredResource {
    public void load() throws IOException {
      loadModelNow();
    }

    public String getDescription() {
      return "Model data";
    }
  }
}
