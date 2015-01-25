package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.GameInformation;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if the unit can capture a city.
 * When that city is a silo, fire a rocket instead.
 * Cities are rated ie HQ has a higher rating then a silo.
 */
public class CaptureRoutine implements AIRoutine {
  private final Game game;
  private final Map map;
  private final Unit unit;
  private final GameInformation gameInfo;

  public CaptureRoutine(Game game, Unit unit) {
    this.unit = unit;
    this.game = game;
    this.map = game.getMap();
    gameInfo = new GameInformation(game);
  }

  public RoutineResult think() {
    // Return empty routine result if we cannot capture
    if (!unit.getStats().canCapture()) return null;

    gameInfo.generate();
    City city = map.getCityOn(unit.getLocation());

    boolean isCapturingCity = city != null && city.isBeingCapturedBy(unit);

    // Continue capturing when possible!
    if (isCapturingCity) {
      return createResult(unit, city);
    }

    List<City> citiesInRange = gameInfo.getCitiesInRangeOf(unit);
    RoutineResult result = captureCity(citiesInRange);
    return result;
  }

  /**
   * Find the best unit to capture a city in range.
   */
  private RoutineResult captureCity(List<City> citiesInRange) {
    int cityInRangeCount = citiesInRange.size();

    if (cityInRangeCount > 0) {
      City bestCity = findBestCity(citiesInRange);
      List<Unit> unitsInRangeOfCity = gameInfo.getUnitsInRangeOf(bestCity);
      List<Unit> activeUnitsInRangeOfCity = getActiveUnits(unitsInRangeOfCity);
      int unitsInRangeOfCityCount = activeUnitsInRangeOfCity.size();

      if (bestCity != null) {
        if (unitsInRangeOfCityCount == 1) {
          return createResult(unit, bestCity);
        } else if (unitsInRangeOfCityCount > 1 && activeUnitsInRangeOfCity.contains(unit)) {
          return createResult(unit, bestCity);
        } else if (cityInRangeCount == 1) {
          return createResult(unit, bestCity);
        } else {
          return searchForAnyUnit(citiesInRange);
        }
      }
    }

    return null;
  }

  private City findBestCity(List<City> citiesInRange) {
    int bestCityIndex = findBestCityIndex(citiesInRange);

    if (bestCityIndex == -1) {
      return null;
    } else {
      return citiesInRange.get(bestCityIndex);
    }
  }

  private int findBestCityIndex(List<City> citiesInRange) {
    int[] cityRating = rateCities(citiesInRange);
    return findBestCityIndex(cityRating);
  }

  private int[] rateCities(List<City> citiesInRange) {
    int[] cityRating = new int[citiesInRange.size()];

    for (int i = 0; i < citiesInRange.size(); i++) {
      City city = citiesInRange.get(i);
      boolean enemyCity = !city.isAlliedWith(unit.getOwner());
      boolean emptyTile = !map.hasUnitOn(city.getLocation());
      emptyTile = emptyTile || map.getUnitOn(city.getLocation()) == unit;

      // When low on hp it might be a better idea
      // to heal or join...
      boolean lowHp = unit.getHpPercentage() < 45;

      if (enemyCity && emptyTile) {
        if (city.isHQ() && !lowHp) {
          cityRating[i] = 10;
        } else if (city.canBuild()) {
          cityRating[i] = 8;
        } else if (city.canLaunchRocket()) {
          cityRating[i] = 4;
        } else {
          if (!lowHp) {
            cityRating[i] = 6;
          }
        }
      }
    }

    return cityRating;
  }

  /**
   * This will find the best city index in reach of the unit.
   *
   * @param cityRating The rating of each city, NOTE THIS MIGHT HAVE 0 FOR EACH CITY IF NO CITY IS AVAILABLE
   * @return the best city index or -1 if no city is available
   */
  private int findBestCityIndex(int[] cityRating) {
    int bestCityIndex = -1;
    int highestRating = 0;

    for (int i = 0; i < cityRating.length; i++) {
      if (cityRating[i] > highestRating) {
        highestRating = cityRating[i];
        bestCityIndex = i;
      }
    }

    return bestCityIndex;
  }

  private RoutineResult searchForAnyUnit(List<City> citiesInRange) {
    for (City city : citiesInRange) {
      List<Unit> unitsInRangeOfCity = gameInfo.getUnitsInRangeOf(city);
      List<Unit> activeUnitsInRangeOfCity = getActiveUnits(unitsInRangeOfCity);

      if (activeUnitsInRangeOfCity.size() > 1) {
        boolean enemyCity = !city.isAlliedWith(unit.getOwner());
        boolean emptyTile = !map.hasUnitOn(city.getLocation());

        if (enemyCity && emptyTile) {
          Unit firstUnit = activeUnitsInRangeOfCity.get(0);
          return createResult(firstUnit, city);
        }
      }
    }

    return null;
  }

  private List<Unit> getActiveUnits(List<Unit> units) {
    List<Unit> activeUnits = new ArrayList<Unit>();

    for (Unit unit : units) {
      if (unit.isActive()) {
        activeUnits.add(unit);
      }
    }
    return activeUnits;
  }

  private RoutineResult createResult(Unit unit, City city) {
    if (city.canLaunchRocket()) {
      return new RoutineResult(Fuz.UNIT_ORDER.FIRE_SILO_ROCKET, unit.getLocation(), city.getLocation(), findGoodRocketLocation());
    } else {
      return new RoutineResult(Fuz.UNIT_ORDER.CAPTURE, unit.getLocation(), city.getLocation());
    }
  }

  /**
   * The location in the map that does some damage to unit(s) of the enemy
   */
  private Location findGoodRocketLocation() {
    return findEnemyBlob();
  }

  /**
   * Attempts to find enemy units grouped together
   */
  private Location findEnemyBlob() {
    int enemyUnitsCount = 0;
    int maxUnits = 0;
    Location bestLocation = null;

    // Look around each enemy unit
    // Count the other enemy units around it
    // Max units = Max damage
    // Don't shoot on your own units!
    for (Player player : game.getActivePlayers()) {
      if (player != game.getActivePlayer()) {
        for (Unit unit : player.getArmy()) {
          if (!unit.isInTransport()) {
            for (Tile t : map.getSurroundingTiles(unit.getLocation(), 1, 3)) {
              if (!t.isFogged()) {
                if (map.hasUnitOn(t)) {
                  Unit otherUnit = map.getUnitOn(t);

                  if (!otherUnit.isAlliedWith(game.getActivePlayer())) {
                    enemyUnitsCount++;
                  }
                }
              }
            }

            if (enemyUnitsCount > maxUnits) {
              maxUnits = enemyUnitsCount;
              bestLocation = unit.getLocation();
            }
          }
        }
      }
    }

    if (bestLocation == null) {
      return getEnemyHQLocation();
    } else {
      return bestLocation;
    }
  }

  private Location getEnemyHQLocation() {
    Player firstEnemyPlayer;
    int i = 0;
    do {
      firstEnemyPlayer = game.getPlayerByID(i++);
    } while (firstEnemyPlayer.isAlliedWith(game.getActivePlayer()));

    return firstEnemyPlayer.getHq().getLocation();
  }
}
