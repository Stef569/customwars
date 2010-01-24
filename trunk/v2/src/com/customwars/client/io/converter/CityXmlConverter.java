package com.customwars.client.io.converter;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.UCaseMap;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;
import java.util.Map;

/**
 * Read all Cities from an xml file
 * Each city inherits all the statistics from the base city.
 * The base city can be retrieved from baseCities by the 'type' parameter.
 *
 * A 'horizontal pipe seam' with type 'pipe_seam' inherits all the statistics from the 'pipe_seam' base city
 */
public class CityXmlConverter implements Converter {
  private final Map<String, City> baseCities = new UCaseMap<City>();
  private int cityID;
  private String cityName;
  private String cityType;
  private List<Direction> connections;

  public CityXmlConverter(Iterable<City> baseCities) {
    for (City city : baseCities) {
      this.baseCities.put(city.getName(), city);
    }
  }

  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
    throw new UnsupportedOperationException("Cities only have read from xml support");
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    readCity(reader);
    City cityCopy = createCopy(cityType);
    writeFields(cityCopy);
    return cityCopy;
  }

  private void readCity(HierarchicalStreamReader reader) {
    cityID = Integer.parseInt(reader.getAttribute("id"));
    cityName = reader.getAttribute("name");
    cityType = reader.getAttribute("type");
    connections = ConvertUtil.readConnectionNode(reader);
  }

  private City createCopy(String cityType) {
    City baseCity = getBaseCity(cityType);
    baseCity.init();
    return new City(baseCity);
  }

  private City getBaseCity(String type) {
    if (!baseCities.containsKey(type)) {
      throw new IllegalArgumentException(type + " is not a base city");
    }

    return baseCities.get(type);
  }

  private void writeFields(City cityCopy) {
    // Use Reflection to overwrite the following values in the city copy
    // If no name is provided default to the name of the base city
    City baseCity = getBaseCity(cityType);
    ConvertUtil.writeField("id", cityCopy, cityID);
    ConvertUtil.writeField("name", cityCopy, cityName);
    ConvertUtil.writeField("name", cityCopy, cityName == null ? baseCity.getName() : cityName);
    ConvertUtil.writeField("connectedDirections", cityCopy, connections);
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass == City.class;
  }
}