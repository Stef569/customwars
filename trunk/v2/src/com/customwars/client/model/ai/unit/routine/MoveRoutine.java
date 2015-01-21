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

/**
 * Attempts to find the best destination location for this unit
 */
public class MoveRoutine implements AIRoutine {
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
    Location cityLocation = findFreeEnemyCityLocation(unit);

    if (cityLocation != null) {
      RoutineResult result = new RoutineResult(Fuz.UNIT_ORDER.MOVE, unit.getLocation(), cityLocation);
      result.debug = "city";
      return result;
    } else {
      Location hqLocation = findClosestEnemyHQ(game.getActivePlayer());

      if (hqLocation != null) {
        if (!map.hasUnitOn(hqLocation)) {
          RoutineResult result = new RoutineResult(Fuz.UNIT_ORDER.MOVE, unit.getLocation(), hqLocation);
          result.debug = "hq";
          return result;
        }
      }
    }

    return null;
  }

  private Location findFreeEnemyCityLocation(Unit unit) {
    for (Tile t : game.getMap().getSurroundingTiles(unit.getLocation(), 1, unit.getStats().getMovement())) {
      City city = map.getCityOn(t);

      if (city != null) {
        boolean free = !map.hasUnitOn(t);
        boolean allied = city.isAlliedWith(unit.getOwner());
        boolean destructible = city.canGainExperienceFromDestroying();
        boolean canMoveTo = pathFinder.canMoveTo(unit, city.getLocation());

        if (free && !allied && !destructible && canMoveTo) {
          return t;
        }
      }
    }

    return null;
  }

  private Location findClosestEnemyHQ(Player aiPlayer) {
    Location hqLocation = null;
    int distance = 0;

    for (Player otherPlayer : game.getActivePlayers()) {
      if (!otherPlayer.isAlliedWith(aiPlayer)) {
        City aiHQ = aiPlayer.getHq();
        City otherHQ = otherPlayer.getHq();

        if (aiHQ != null && otherHQ != null) {
          int hqDistance = TileMap.getDistanceBetween(aiHQ.getLocation(), otherHQ.getLocation());

          if (hqDistance > distance) {
            distance = hqDistance;
            hqLocation = otherPlayer.getHq().getLocation();
          }
        }
      }
    }

    Location freeLocationNearHQ = hqLocation;

    // Find a free location NEAR the HQ
    if (map.hasUnitOn(hqLocation)) {
      for (Location l : map.getSurroundingTiles(hqLocation, 1, 3)) {
        if (!map.hasUnitOn(l)) {
          freeLocationNearHQ = l;
        }
      }
    }

    return freeLocationNearHQ;
  }
}
