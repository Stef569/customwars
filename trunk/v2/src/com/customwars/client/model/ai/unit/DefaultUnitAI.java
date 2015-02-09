package com.customwars.client.model.ai.unit;

import com.customwars.client.controller.AIUnitController;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.CWGameController;
import com.customwars.client.model.GameController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * The Unit AI asks for advice from the advisors and creates a list of unit orders.
 * Always pass a copy of the game to this class as it will make changes to the game.
 */
public class DefaultUnitAI implements UnitAI {
  private final ControllerManager controllerManager;
  private final Game gameCopy;

  /**
   * Create a new Default Unit AI, this AI will create orders for each unit.
   *
   * @param gameCopy          a Copy of the game
   * @param controllerManager The controller manager, controls each unit and city in the game.
   */
  public DefaultUnitAI(Game gameCopy, ControllerManager controllerManager) {
    this.gameCopy = gameCopy;
    this.controllerManager = controllerManager;
  }

  /**
   * Allows each Unit in the game to think about the best 'action' to perform.
   *
   * @return A list of Unit orders
   */
  public List<UnitOrder> findBestUnitOrders() {
    GameController gameController = new CWGameController(gameCopy, controllerManager);
    Map map = gameCopy.getMap();

    // AI Orders may change the players army and other units
    // Make a copy of the unit list to prevent a ConcurrentModification exceptions
    List<Unit> aiUnits = new ArrayList<Unit>();
    for (Unit unit : gameCopy.getActivePlayer().getArmy()) {
      if (!unit.isInTransport() && unit.isActive()) {
        aiUnits.add(unit);
      }
    }

    List<UnitOrder> orders = findBestUnitOrders(gameController, map, aiUnits);
    return orders;
  }

  @Override
  public boolean isGameOver() {
    return gameCopy.isGameOver();
  }

  private List<UnitOrder> findBestUnitOrders(GameController gameController, Map map, List<Unit> aiUnits) {
    List<UnitOrder> orders = new ArrayList<UnitOrder>();

    for (Unit unit : aiUnits) {
      // We use a copy of the units, if a unit it destroyed it will still be in this list
      if (unit.getOwner() != null && unit.getLocation() != null && !unit.isDestroyed()) {
        AIUnitController controller = new AIUnitController(map, unit, gameController);

        // Select the unit
        gameCopy.setActiveUnit(unit);
        map.buildMovementZone(unit);
        map.buildAttackZone(unit);

        // Set initial presets, actions for specific units that have the highest priority
        PresetSituations presets = new PresetSituations(gameCopy);
        presets.think();

        // Ask for advice
        UnitAdvisor unitAdvisor = new DefaultUnitAdvisor(gameCopy, unit, presets);
        UnitOrder order = unitAdvisor.createBestOrder();

        if (order != null) {
          // Execute the order in the AI world
          executeOrderInAIWorld(map, unit, controller, order);

          // Store the order to be executed in the game GUI
          orders.add(order);
        }
      }
    }

    return orders;
  }

  private void executeOrderInAIWorld(Map map, Unit unit, AIUnitController controller, UnitOrder order) {
    Location moveDestination = order.moveDestination;
    City cityOnDestination = map.getCityOn(moveDestination);
    Unit unitOnMoveDestination = map.getUnitOn(moveDestination);
    Unit unitOnTarget = map.getUnitOn(order.target);
    City cityOnTarget = map.getCityOn(order.target);

    switch (order.order) {
      case DO_NOTHING:
      case WAIT:
        break;
      case CAPTURE:
        controller.captureCity(cityOnDestination);
        break;
      case ATTACK_UNIT:
        if (unit.isDirect()) {
          if (!order.moveDestination.equals(unit.getLocation())) {
            controller.moveTo(order.moveDestination);
          }
        }

        controller.attackEnemyDirect(unitOnTarget);
        break;
      case ATTACK_CITY:
        controller.attackCity(unit, cityOnTarget);
        break;
      case SUPPLY:
        break;
      case READY_FOR_TRANSPORT:
        break;
      case JOIN:
        controller.join(unitOnMoveDestination);
        break;
      case HIDE:
        break;
      case SUBMERGE:
        controller.dive();
      case SURFACE:
        controller.surface();
      case UNLOAD:
        break;
      case BUILD_TEMP_BASE:
        break;
      case FIRE_SILO_ROCKET:
        controller.fireSiloRocket(cityOnDestination, order.moveDestination, order.target);
        break;
      case LOAD_CO:
        controller.loadCo(cityOnDestination);
        break;
      case HEAL:
      case MOVE:
        controller.moveTo(order.moveDestination);
        break;
      case DO_CO_POWER:
        controller.coPower();
        break;
    }
  }
}