package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A database(cache) of cities that can be used in the game. each City is mapped to an unique ID.
 * The Cities in the cache contain values that always remain the same like: description, maxHP,...
 * getCity(id) will create a copy of the City in the cache and return it.
 *
 * @author stefan
 */
public class CityFactory {
  private static HashMap<Integer, City> cities = new HashMap<Integer, City>();

  public static void addCities(Collection<City> cities) {
    for (City city : cities) {
      addCity(city);
    }
  }

  public static void addCity(City city) {
    int cityID = city.getID();
    if (cities.containsKey(cityID)) {
      throw new IllegalArgumentException("City ID " + cityID + " is already used by " + getCity(cityID));
    }
    city.init();
    cities.put(cityID, city);
  }

  public static City getCity(int id) {
    if (!cities.containsKey(id)) {
      throw new IllegalArgumentException("City ID " + id + " is not cached " + cities.keySet());
    }
    City city = new City(cities.get(id));
    city.reset();
    return city;
  }

  public static Collection<City> getAllCities() {
    List<City> cityCopies = new ArrayList<City>();
    for (City unit : cities.values()) {
      cityCopies.add(getCity(unit.getID()));
    }
    return Collections.unmodifiableList(cityCopies);
  }

  public static City getRandomCity() {
    int rand = (int) (Math.random() * cities.size());
    return getCity(rand);
  }

  public static int countCities() {
    return cities.size();
  }

  public static void clear() {
    cities.clear();
  }
}
