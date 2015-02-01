package com.customwars.client.model.ai.unit;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Create orders from CWActions
 */
public class UnitAiActions {
  private final List<UnitOrder> orders;
  private final Game game;
  private final Map map;

  public UnitAiActions(List<UnitOrder> orders, Game game) {
    this.orders = orders;
    this.game = game;
    this.map = game.getMap();
  }

  public List<CWAction> createActions() {
    List<CWAction> actions = new ArrayList<CWAction>();

    for (UnitOrder order : orders) {
      CWAction action = createAction(order);
      if (action != null) {
        actions.add(action);
      }
    }

    return actions;
  }

  private CWAction createAction(UnitOrder order) {
    Unit unit = map.getUnitOn(order.unitLocation);

    if (unit == null) {
      throw new IllegalStateException("An action cannot be created, there is no unit on the unit location");
    }

    Location moveDestination = order.moveDestination;
    City cityOnMoveDestination = map.getCityOn(moveDestination);
    Unit unitOnMoveDestination = map.getUnitOn(moveDestination);
    Unit unitOnTarget = map.getUnitOn(order.target);
    City cityOnTarget = map.getCityOn(order.target);
    Location destination = order.moveDestination;

    if (unit.isDestroyed()) {
      return null;
    }

    game.setActiveUnit(unit);
    map.buildMovementZone(unit);
    map.buildAttackZone(unit);

    switch (order.order) {
      case DO_NOTHING:
      case WAIT:
      case CAPTURE:
        return ActionFactory.buildCaptureAction(unit, cityOnMoveDestination);
      case ATTACK_UNIT:
        return ActionFactory.buildUnitVsUnitAttackAction(unit, unitOnTarget, moveDestination);
      case ATTACK_CITY:
        return ActionFactory.buildUnitVsCityAttackAction(unit, cityOnTarget, moveDestination);
      case SUPPLY:
        break;
      case READY_FOR_TRANSPORT:
        break;
      case JOIN:
        return ActionFactory.buildJoinAction(unit, unitOnMoveDestination);
      case HIDE:
        break;
      case SUBMERGE:
        break;
      case DIVE:
        return ActionFactory.buildDiveAction(unit, destination);
      case SURFACE:
        return ActionFactory.buildSurfaceAction(unit, destination);
      case UNLOAD:
        break;
      case BUILD_TEMP_BASE:
        break;
      case FIRE_SILO_ROCKET:
        return ActionFactory.buildLaunchRocketAction(unit, cityOnMoveDestination, order.target);
      case LOAD_CO:
        return ActionFactory.buildLoadCOAction(unit, order.moveDestination);
      case HEAL:
      case MOVE:
        return ActionFactory.buildMoveAction(unit, destination);
      case DO_CO_POWER:
        break;
    }

    return null;
  }
}
