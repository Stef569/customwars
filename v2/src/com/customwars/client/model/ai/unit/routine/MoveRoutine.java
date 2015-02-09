package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.path.PathFinder;
import org.apache.log4j.Logger;

import java.util.EnumSet;

/**
 * Attempts to find the best destination location for this unit.
 * Units that can capture move to free cities.
 * All land units move towards the enemy hq/factories
 * Air units move towards enemy units
 * Naval units move towards enemy naval units, battleship and carrier in a supporting role.
 */
public class MoveRoutine implements AIRoutine {
  private static final Logger logger = Logger.getLogger(MoveRoutine.class);
  private final Game game;
  private final Map map;
  private final Unit unit;
  private final PathFinder pathFinder;

  public MoveRoutine(Game game, Unit unit) {
    this.unit = unit;
    this.game = game;
    this.map = game.getMap();
    this.pathFinder = new PathFinder(map);
  }

  public RoutineResult think() {
    if (unit.isLand()) {
      Location bestMoveDestination = findBestMoveDestinationForLandUnits();
      if (bestMoveDestination != null) {
        RoutineResult result = new RoutineResult(Fuz.UNIT_ORDER.MOVE, unit.getLocation(), bestMoveDestination);
        return result;
      }
    } else if (unit.isAir()) {
      Location bestMoveDestination = findBestMoveDestinationForAirUnits();
      if (bestMoveDestination != null) {
        RoutineResult result = new RoutineResult(Fuz.UNIT_ORDER.MOVE, unit.getLocation(), bestMoveDestination);
        return result;
      }
    } else if (unit.isNaval()) {
      Location bestMoveDestination = findBestMoveDestinationForNavalUnits();
      if (bestMoveDestination != null) {
        RoutineResult result = new RoutineResult(Fuz.UNIT_ORDER.MOVE, unit.getLocation(), bestMoveDestination);
        return result;
      }
    }

    return null;
  }

  private Location findBestMoveDestinationForLandUnits() {
    Location cityLocationInMoveZone = findFreeEnemyCityLocationWithinRange(unit, unit.getStats().getMovement());
    Location freeCityLocation = findFreeEnemyCityLocationWithinRange(unit, 10);
    Location hqLocation = findClosestCity(game.getActivePlayer(), "hq");
    Location factoryLocation = findClosestCity(game.getActivePlayer(), "factory");

    if (unit.getStats().canCapture()) {
      if (cityLocationInMoveZone != null) {
        logger.debug(unit.getName() + " wants to move to city in movezone @ " + cityLocationInMoveZone.getLocationString());
        return cityLocationInMoveZone;
      } else if (freeCityLocation != null) {
        logger.debug(unit.getName() + " wants to move to nearby city @ " + freeCityLocation.getLocationString());
        return freeCityLocation;
      } else {
        Location freeCityLocationFarAway = findFreeEnemyCityLocationWithinRange(unit, 100);

        if (freeCityLocationFarAway != null) {
          logger.debug(unit.getName() + " wants to move to city far far away @ " + freeCityLocationFarAway.getLocationString());
          return freeCityLocationFarAway;
        }
      }
    } else if (hqLocation != null) {
      if (!map.hasUnitOn(hqLocation)) {
        logger.debug(unit.getName() + " wants to move to HQ @ " + hqLocation.getLocationString());
        return hqLocation;
      }
    } else if (factoryLocation != null) {
      if (!map.hasUnitOn(factoryLocation)) {
        logger.debug(unit.getName() + " wants to move to Factory @ " + factoryLocation.getLocationString());
        return factoryLocation;
      }
    }

    // We could not find any destination for this unit.
    return null;
  }

  private Location findFreeEnemyCityLocationWithinRange(Unit unit, int range) {
    for (Tile t : game.getMap().getSurroundingTiles(unit.getLocation(), 1, range)) {
      City city = map.getCityOn(t);

      if (city != null) {
        boolean free = !map.hasUnitOn(t);
        boolean allied = city.isAlliedWith(unit.getOwner());
        boolean destructible = city.canGainExperienceFromDestroying();
        boolean canMoveTo = true;

        if (range == unit.getStats().getMovement()) {
          canMoveTo = pathFinder.canMoveTo(unit, city.getLocation());
        }

        if (free && !allied && !destructible && canMoveTo) {
          return t;
        }
      }
    }

    return null;
  }

  private Location findClosestCity(Player aiPlayer, String cityName) {
    Location cityLocation = null;
    int distance = 0;

    for (Player otherPlayer : game.getActivePlayers()) {
      if (!otherPlayer.isAlliedWith(aiPlayer)) {
        City aiCity = aiPlayer.findCity(cityName);
        City otherCity = otherPlayer.findCity(cityName);

        if (aiCity != null && otherCity != null) {
          int cityDistance = TileMap.getDistanceBetween(aiCity.getLocation(), otherCity.getLocation());

          if (cityDistance > distance) {
            distance = cityDistance;
            cityLocation = otherCity.getLocation();
          }
        }
      }
    }

    Location freeLocationNearCity = null;
    if (map.hasUnitOn(cityLocation)) {
      // Find a free location NEAR the city
      for (Location location : map.getSurroundingTiles(cityLocation, 1, 3)) {
        if (!map.hasUnitOn(location)) {
          freeLocationNearCity = location;
        }
      }
      return freeLocationNearCity;
    } else {
      return cityLocation;
    }
  }

  private Location findBestMoveDestinationForAirUnits() {
    // Move towards enemy units
    for (Tile t : map.getAllTiles()) {
      if (map.hasUnitOn(t)) {
        Unit otherUnit = map.getUnitOn(t);
        boolean allied = otherUnit.isAlliedWith(unit.getOwner());

        if (!allied) {
          Location freeSpot = findFreeSpotNearUnit(otherUnit);

          if (freeSpot != null) {
            return freeSpot;
          }
        }
      }
    }

    return null;
  }

  private Location findBestMoveDestinationForNavalUnits() {
    // Battleship and carrier
    if (unit.isBallistic() || unit.getStats().canLaunchUnit()) {
      // Stay close to friendly naval units
      for (Tile t : map.getAllTiles()) {
        if (map.canTraverseTile(unit, t) && map.hasUnitOn(t)) {
          Unit otherUnit = map.getUnitOn(t);
          boolean allied = otherUnit.isAlliedWith(unit.getOwner());

          if (allied && otherUnit.isNaval()) {
            Location freeSpot = findFreeSpotNearUnit(otherUnit);

            if (freeSpot != null) {
              return freeSpot;
            }
          }
        }
      }

      return null;
    } else {
      // Move towards enemy naval units
      for (Tile t : map.getAllTiles()) {
        if (map.canTraverseTile(unit, t) && map.hasUnitOn(t)) {
          Unit otherUnit = map.getUnitOn(t);
          boolean allied = otherUnit.isAlliedWith(unit.getOwner());

          if (!allied && otherUnit.isNaval()) {
            Location freeSpot = findFreeSpotNearUnit(otherUnit);

            if (freeSpot != null) {
              return freeSpot;
            }
          }
        }
      }

      return null;
    }
  }

  private Location findFreeSpotNearUnit(Unit otherUnit) {
    for (Tile t : map.getSurroundingTiles(otherUnit.getLocation(), 1, 3)) {
      if (map.canTraverseTile(unit, t) && !map.hasUnitOn(t)) {
        return t;
      }
    }

    return null;
  }

  @Override
  public EnumSet<Fuz.UNIT_ORDER> getSupportedOrders() {
    return EnumSet.of(Fuz.UNIT_ORDER.MOVE);
  }
}
