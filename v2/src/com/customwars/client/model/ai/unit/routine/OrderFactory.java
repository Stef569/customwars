package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.unit.UnitOrder;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.path.PathFinder;

import java.util.List;

/**
 * Converts a RoutineResult to a UnitOrder
 * A unit order contains the order 'Move, Capture, Attack'
 * The location of the unit
 * The move destination
 * The target contains for example:
 * The unit target for a direct
 * The rocket destination
 * etc
 */
public class OrderFactory {
  private Map map;
  private PathFinder pathFinder;

  public OrderFactory(Map map) {
    this.map = map;
    this.pathFinder = new PathFinder(map);
  }

  public UnitOrder createOrder(RoutineResult result) {
    switch (result.order) {
      case MOVE:
        Location destination = findDestination(result);
        if (destination != null) {
          return new UnitOrder(result.order, result.unitLocation, destination, result.target);
        }
        break;
      case ATTACK_UNIT:
      case ATTACK_CITY:
        return createAttackOrder(result);
      default:
        // Move over the AI path
        return new UnitOrder(result.order, result.unitLocation, result.moveDestination, result.target);
    }

    return null;
  }

  private UnitOrder createAttackOrder(RoutineResult result) {
    return new UnitOrder(result.order, result.unitLocation, result.moveDestination, result.target);
  }

  private Location findDestination(RoutineResult result) {
    return findDestination(result.unitLocation, result.moveDestination);
  }

  private Location findDestination(Location unitLocation, Location destination) {
    Unit unit = map.getUnitOn(unitLocation);

    if (destination.equals(unit.getLocation())) {
      return destination;
    } else {
      int destinationMoveCost = unit.getMoveStrategy().getMoveCost(destination);

      // The destination might be way off, build the best path to the destination
      List<Location> path = pathFinder.getShortestPath(unit, destination);

      if (path.isEmpty()) {
        return null;
      } else {
        // The destination within the move zone of the unit
        return path.get(path.size() - 1);
      }
    }
  }
}
