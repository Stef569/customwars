package com.customwars.client.model.gameobject;

import com.customwars.client.tools.UCaseMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database(cache) of cities that can be used in the game. each city is mapped to an unique city ID.
 * The cities in the cache contain values that always remain the same(static values) like: description, maxHP,...
 * getCity(id) will create a deep copy of the city in the cache and return it.
 *
 * When cities are added init is invoked on them, this allows the city to validate it's values before it is used.
 * Each time a city is retrieved from this Factory reset is invoked, this allows the city to
 * put dynamic values to max and put values to default.
 *
 * The name of the city stored in this Factory is always in upper case.
 */
public class CityFactory {
  private static final Map<Integer, City> citiesById = new HashMap<Integer, City>();
  private static final Map<String, City> citiesByName = new UCaseMap<City>();
  private static final Map<String, City> baseCitiesByName = new UCaseMap<City>();
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
    String cityName = city.getName();
    searchForDuplicates(cityName, cityID);
    addCity(cityName, cityID, city);
  }

  private static void searchForDuplicates(String cityName, int cityID) {
    if (citiesById.containsKey(cityID)) {
      City duplicateCity = getCity(cityID);
      throw new IllegalArgumentException("City ID " + cityID + " is already used by " + duplicateCity);
    }

    if (citiesByName.containsKey(cityName)) {
      City duplicateCity = getCity(cityName);
      throw new IllegalArgumentException("City name " + cityName + " is already used by " + duplicateCity);
    }
  }

  private static void addCity(String cityName, int cityID, City city) {
    city.init();
    citiesById.put(cityID, city);
    citiesByName.put(cityName, city);
  }

  public static void addBaseCities(Collection<City> baseCityCollection) {
    for (City baseCity : baseCityCollection) {
      String cityName = baseCity.getName();
      City baseCityCopy = new City(baseCity);
      baseCitiesByName.put(cityName, baseCityCopy);
    }
  }

  public static City getCity(int id) {
    if (!citiesById.containsKey(id)) {
      throw new IllegalArgumentException("City ID " + id + " is not cached " + citiesById.keySet());
    }
    City city = new City(citiesById.get(id));
    city.reset();
    return city;
  }

  public static City getCity(String cityName) {
    if (!citiesByName.containsKey(cityName)) {
      throw new IllegalArgumentException("City name " + cityName + " is not cached " + citiesByName.keySet());
    }
    City city = new City(citiesByName.get(cityName));
    city.reset();
    return city;
  }

  public static City getBaseCity(String cityName) {
    if (!baseCitiesByName.containsKey(cityName)) {
      throw new IllegalArgumentException("Base city name " + cityName + " is not cached " + baseCitiesByName.keySet());
    }
    City city = new City(baseCitiesByName.get(cityName));
    city.reset();
    return city;
  }

  public static Collection<City> getAllCities() {
    List<City> allCities = new ArrayList<City>(citiesById.size());
    for (City city : citiesById.values()) {
      int cityID = city.getID();
      allCities.add(getCity(cityID));
    }
    Collections.sort(allCities, SORT_CITY_ON_ID);
    return Collections.unmodifiableList(allCities);
  }

  /**
   * @return A Collection of all the city names in this Factory
   */
  public static List<String> getAllCityNames() {
    if (citiesByName == null) {
      return Collections.emptyList();
    } else {
      return new ArrayList<String>(citiesByName.keySet());
    }
  }

  public static City getRandomCity() {
    int rand = (int) (Math.random() * citiesById.size());
    return getCity(rand);
  }

  /**
   * @return A Collection of all the base cities in this Factory sorted on cityID
   */
  public static List<City> getBaseCities() {
    List<City> allBaseCities = new ArrayList<City>(baseCitiesByName.size());
    for (City city : baseCitiesByName.values()) {
      String cityName = city.getName();
      allBaseCities.add(getBaseCity(cityName));
    }
    Collections.sort(allBaseCities, SORT_CITY_ON_ID);
    return Collections.unmodifiableList(allBaseCities);
  }

  public static boolean hasCityForID(int cityID) {
    return citiesById.containsKey(cityID);
  }

  public static boolean hasCityForName(String cityName) {
    return citiesByName.containsKey(cityName);
  }

  public static boolean hasBaseCityForName(String cityName) {
    return baseCitiesByName.containsKey(cityName);
  }

  public static int countCities() {
    return citiesById.size();
  }

  public static int countBaseCities() {
    return baseCitiesByName.size();
  }

  public static void clear() {
    citiesById.clear();
    citiesByName.clear();
    baseCitiesByName.clear();
  }
}
