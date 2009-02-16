package com.customwars.client.io.loading;

import com.customwars.client.model.map.gameobject.Terrain;
import com.customwars.client.model.map.gameobject.TerrainFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.newdawn.slick.util.ResourceLoader;

import java.io.InputStream;
import java.util.Collection;

/**
 * Loads Model classes to their Factories ie
 * Terrain -> TerrainFactory
 *
 * @author stefan
 */
public class ModelLoader {
  private static final XStream xStream = new XStream(new DomDriver());

  public void load() {
    loadTerrains();
  }

  private void loadTerrains() {
    InputStream terrainStream = ResourceLoader.getResourceAsStream("res/data/terrains.xml");
    xStream.alias("terrain", Terrain.class);
    xStream.useAttributeFor(Terrain.class, "id");
    xStream.useAttributeFor(Terrain.class, "name");
    TerrainFactory.addTerrains((Collection<Terrain>) xStream.fromXML(terrainStream));
  }
}
