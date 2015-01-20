package com.customwars.client.model.ai.fuzzy;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.UnitFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information from the game used by the build AI.
 * Fields are public, collections are private use getXX
 */
public class BuildAIInformation {
  public Fuz.MAP_SIZE mapSize;
  public Fuz.MAP_TYPE mapType;
  public Fuz.BATTLE_CONDITION battleConditions;
  public int visibleLandTiles;
  public Fuz.GAME_PROGRESS gameProgress;
  public City[] factories;

  protected Map<City, Map<City, Fuz.DISTANCE>> cityDistances;
  protected Map<Player, Map<String, Integer>> unitsCountByPlayer;
  protected Map<Player, Map<String, Integer>> cityCountByPlayer;
  protected List<Fuz.CONSTRUCTION_POSSIBILITY> constructionPossibilities;
  protected List<String> mostExpensiveUnits;

  /**
   * The enemies sorted by distance, 0 = very close, 1 = close, ...
   */
  private List<Enemy> highThreatsInMap;
  public Fuz.PLAYER_FINANCE finance;

  public BuildAIInformation() {
    unitsCountByPlayer = new HashMap<Player, Map<String, Integer>>(UnitFactory.countUnits());
    cityCountByPlayer = new HashMap<Player, Map<String, Integer>>(CityFactory.countBaseCities());
    constructionPossibilities = new ArrayList<Fuz.CONSTRUCTION_POSSIBILITY>(3);
    cityDistances = new HashMap<City, Map<City, Fuz.DISTANCE>>();
    highThreatsInMap = new ArrayList<Enemy>();
    mostExpensiveUnits = new ArrayList<String>();
  }

  public void setUnitCount(Player player, String unitName, int unitCount) {
    if (!unitsCountByPlayer.containsKey(player)) {
      unitsCountByPlayer.put(player, new HashMap<String, Integer>());
    }

    Map<String, Integer> unitsCount = unitsCountByPlayer.get(player);
    unitsCount.put(unitName, unitCount);
  }

  public void setCityCount(Player player, String cityName, int cityCount) {
    if (!cityCountByPlayer.containsKey(player)) {
      cityCountByPlayer.put(player, new HashMap<String, Integer>());
    }

    Map<String, Integer> citiesCount = cityCountByPlayer.get(player);
    citiesCount.put(cityName, cityCount);
  }

  public void addConstructionPossibility(Fuz.CONSTRUCTION_POSSIBILITY possibility) {
    constructionPossibilities.add(possibility);
  }

  public void addCityDistance(City factory, City city, Fuz.DISTANCE distance) {
    if (!cityDistances.containsKey(factory)) {
      cityDistances.put(factory, new HashMap<City, Fuz.DISTANCE>());
    }

    cityDistances.get(factory).put(city, distance);
  }

  public void setMostExpensiveUnits(List<String> mostExpensiveUnits) {
    this.mostExpensiveUnits = mostExpensiveUnits;
  }

  public void setHighThreatsInMap(List<Enemy> highThreatsInMap) {
    this.highThreatsInMap = highThreatsInMap;
  }


  public int getVisibleTilesPercentage() {
    return visibleLandTiles;
  }

  public int getUnitCount(Player player, String unitName) {
    Map<String, Integer> unitCountByUnitName = unitsCountByPlayer.get(player);

    if (unitCountByUnitName.containsKey(unitName)) {
      return unitCountByUnitName.get(unitName);
    } else {
      return 0;
    }
  }

  public int getCityCount(Player player, String cityName) {
    if (!cityCountByPlayer.containsKey(player)) {
      throw new IllegalArgumentException("Player not found + " + player.getName());
    } else if (!cityCountByPlayer.get(player).containsKey(cityName.toUpperCase())) {
      return 0;
    }

    return cityCountByPlayer.get(player).get(cityName.toUpperCase());
  }

  public boolean canConstruct(Fuz.CONSTRUCTION_POSSIBILITY possibility) {
    return constructionPossibilities.contains(possibility);
  }

  /**
   * Finds the distance to a city near the given city.
   * Cities don't move so the distances are cached.
   *
   * @param city The city to find the closest city for
   * @return The distance to any city
   */
  public Fuz.DISTANCE getDistanceToNearestCity(City city) {
    Map<City, Fuz.DISTANCE> distances = cityDistances.get(city);

    if (distances != null) {
      List<Map.Entry<City, Fuz.DISTANCE>> list = new ArrayList<Map.Entry<City, Fuz.DISTANCE>>(distances.entrySet());

      Collections.sort(list, new Comparator<Map.Entry<City, Fuz.DISTANCE>>() {
        @Override
        public int compare(Map.Entry<City, Fuz.DISTANCE> o1, Map.Entry<City, Fuz.DISTANCE> o2) {
          return o1.getValue().compareTo(o2.getValue());
        }
      });
      return list.get(0).getValue();
    }

    return null;
  }

  /**
   * Retrieve the distance in tiles between 2 cities.
   * Note the first city parameter is always 1 of the factories of the active player.
   *
   * @param city      One of the factories of the active player
   * @param otherCity The city to find the distance to
   * @return The fuzzy distance
   */
  public Fuz.DISTANCE getDistanceBetween(City city, City otherCity) {
    return cityDistances.get(city).get(otherCity);
  }

  public List<String> getMostExpensiveUnits() {
    return mostExpensiveUnits;
  }

  public List<Enemy> getHighThreatsInMap() {
    return highThreatsInMap;
  }
}
