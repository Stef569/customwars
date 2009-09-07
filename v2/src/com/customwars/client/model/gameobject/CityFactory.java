package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A database(cache) of cities that can be used in the game. each city is mapped to an unique city ID.
 * The cities in the cache contain values that always remain the same(static values) like: description, maxHP,...
 * getCity(id) will create a deep copy of the city in the cache and return it.
 *
 * When cities are added init is invoked on them, this allows the city to validate it's values before it is used.
 * Each time a city is retrieved from this Factory reset is invoked, this allows the city to
 * put dynamic values to max and put values to default.
 *
 * @author stefan
 */
public class CityFactory {
  private static HashMap<Integer, City> cities = new HashMap<Integer, City>();
  private static final Comparator<City> SORT_CITY_ON_ID = new Comparator<City>() {
    public int compare(City cityA, City cityB) {
      return cityA.getID() - cityB.getID();
    }
  };

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
    List<City> allCities = new ArrayList<City>(cities.size());
    for (City city : cities.values()) {
      int cityID = city.getID();
      allCities.add(getCity(cityID));
    }
    Collections.sort(allCities, SORT_CITY_ON_ID);
    return Collections.unmodifiableList(allCities);
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
