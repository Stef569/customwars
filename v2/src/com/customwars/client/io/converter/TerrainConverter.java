package com.customwars.client.io.converter;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Direction;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Read all terrains
 * Each terrain is based on a baseTerrain which are stored in baseTerrains keyed by name ie
 * Road -> Road terrain
 */
public class TerrainConverter implements Converter {
  Map<String, Terrain> baseTerrains = new HashMap<String, Terrain>();

  public TerrainConverter(Collection<Terrain> baseTerrains) {
    for (Terrain terrain : baseTerrains)
      this.baseTerrains.put(terrain.getName().toUpperCase(), terrain);
  }

  public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
    throw new UnsupportedOperationException("Terrains only have read from xml support");
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    int terrainID = Integer.parseInt(reader.getAttribute("id"));
    String terrainName = reader.getAttribute("name");
    String type = reader.getAttribute("type").toUpperCase();

    // Use the type to get the base terrain ie
    // Horizontal road has as type road
    // Init the base terrain to make sure it does not contain illegal data
    // and make a copy
    Terrain baseTerrain = getBaseTerrain(type);
    baseTerrain.init();
    Terrain terrainCopy = new Terrain(baseTerrain);
    List<Direction> connections = null;

    while (reader.hasMoreChildren()) {
      reader.moveDown();
      final String nodeName = reader.getNodeName();

      if (nodeName.equals("connect")) {
        connections = readDirections(reader.getValue());
        reader.moveUp();
      } else {
        throw new IllegalArgumentException("Unknown child in terrain xml");
      }
    }

    // Use reflection to set the new values to the terrain copy
    writeField("id", terrainCopy, terrainID);
    writeField("name", terrainCopy, terrainName == null ? baseTerrain.getName() : terrainName);
    writeField("type", terrainCopy, baseTerrain.getName());
    writeField("connectedDirections", terrainCopy, connections);
    return terrainCopy;
  }

  private Terrain getBaseTerrain(String type) {
    if (!baseTerrains.containsKey(type)) {
      throw new IllegalArgumentException(type + " is not a base terrain");
    }

    return baseTerrains.get(type);
  }

  private void writeField(String fieldName, Object obj, Object val) {
    Field f = Fields.find(Terrain.class, fieldName);
    Fields.write(f, obj, val);
  }

  public List<Direction> readDirections(String input) {
    List<Direction> connects = new ArrayList<Direction>();
    StringTokenizer tokenizer = new StringTokenizer(input, ",");
    while (tokenizer.hasMoreTokens()) {
      connects.add(Direction.getDirection(tokenizer.nextToken()));
    }
    return connects;
  }

  public boolean canConvert(Class aClass) {
    return aClass == Terrain.class;
  }
}
