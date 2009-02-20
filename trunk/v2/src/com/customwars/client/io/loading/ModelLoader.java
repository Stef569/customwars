package com.customwars.client.io.loading;

import com.customwars.client.io.converter.UnitWeaponConverter;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Loads Model classes to their Factories ie
 * Terrain -> TerrainFactory
 * Weapon -> WeaponFactory
 * Unit -> UnitFactory
 * City -> CityFactory
 *
 * @author stefan
 */
public class ModelLoader {
  private static final XStream xStream = new XStream(new DomDriver());
  private static final String XML_DATA_TERRAIN_FILE = "terrains.xml";
  private static final String XML_DATA_WEAPONS_FILE = "weapons.xml";
  private static final String XML_DATA_UNITS_FILE = "units.xml";
  private static final String XML_DATA_CITY_FILE = "cities.xml";
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
  }

  private void loadTerrains() {
    xStream.alias("terrain", Terrain.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    InputStream terrainStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_TERRAIN_FILE);
    TerrainFactory.addTerrains((Collection<Terrain>) xStream.fromXML(terrainStream));
  }

  private void loadWeapons() {
    xStream.alias("weapon", Weapon.class);
    xStream.useAttributeFor(Weapon.class, "id");
    xStream.useAttributeFor(Weapon.class, "name");
    InputStream weaponStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_WEAPONS_FILE);
    WeaponFactory.addWeapons((Collection<Weapon>) xStream.fromXML(weaponStream));
  }

  private void loadUnits() {
    xStream.registerConverter(new UnitWeaponConverter());
    xStream.alias("unit", Unit.class);
    xStream.alias("primaryWeapon", Weapon.class);
    xStream.alias("secondaryWeapon", Weapon.class);
    xStream.useAttributeFor(Unit.class, "id");
    xStream.useAttributeFor(Unit.class, "name");
    InputStream unitStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_UNITS_FILE);
    UnitFactory.addUnits((Collection<Unit>) xStream.fromXML(unitStream));
  }

  private void loadCities() {
    xStream.alias("city", City.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    InputStream cityStream = ResourceLoader.getResourceAsStream(modelResPath + XML_DATA_CITY_FILE);
    CityFactory.addCities((Collection<City>) xStream.fromXML(cityStream));
  }

  public void clear() {
    TerrainFactory.clear();
    WeaponFactory.clear();
    UnitFactory.clear();
    CityFactory.clear();
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
