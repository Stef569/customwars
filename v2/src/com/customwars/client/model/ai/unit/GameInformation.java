package com.customwars.client.model.ai.unit;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameInformation {
  private Game game;
  private Map map;
  private HashMap<Unit, List<City>> citiesInRangeByUnit;
  private HashMap<City, List<Unit>> unitsInRangeByCity;

  public GameInformation(Game game) {
    this.game = game;
    this.map = game.getMap();
    citiesInRangeByUnit = new HashMap<Unit, List<City>>();
    unitsInRangeByCity = new HashMap<City, List<Unit>>();
  }

  public void generate() {
    buildCitiesInRange();
  }

  private void buildCitiesInRange() {
    for (Unit unit : game.getActivePlayer().getArmy()) {
      List<City> citiesInRange = buildCitiesInRange(unit);
      citiesInRangeByUnit.put(unit, citiesInRange);

      for (City city : citiesInRange) {
        if (!unitsInRangeByCity.containsKey(city)) {
          unitsInRangeByCity.put(city, new ArrayList<Unit>());
        }
        unitsInRangeByCity.get(city).add(unit);
      }
    }
  }

  public List<City> buildCitiesInRange(Unit unit) {
    List<City> citiesInRange = new ArrayList<City>();
    map.buildMovementZone(unit);

    for (Location t : unit.getMoveZone()) {
      if (map.hasCityOn(t)) {
        City city = map.getCityOn(t);
        citiesInRange.add(city);
      }
    }

    return citiesInRange;
  }

  public List<City> getCitiesInRangeOf(Unit unit) {
    return citiesInRangeByUnit.get(unit);
  }

  public List<Unit> getUnitsInRangeOf(City city) {
    if (unitsInRangeByCity.containsKey(city)) {
      return unitsInRangeByCity.get(city);
    } else {
      return Collections.emptyList();
    }
  }

}
