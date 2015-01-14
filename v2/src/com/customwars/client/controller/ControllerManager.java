package com.customwars.client.controller;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages every Controller in the game.
 * A controller is an object that handles input of gameObjects.
 * They are not defined within the game objects to allow custom behaviour.
 * For now only HumanControllers are used.
 *
 * When a controller could not be found for a unit/city an IllegalArgumentException is thrown
 */
public class ControllerManager {
  private Map<Unit, UnitController> unitControllers;
  private Map<City, CityController> cityControllers;
  private InGameContext inGameContext;
  private Game game;

  private ControllerManager() {
    this.unitControllers = new HashMap<Unit, UnitController>();
    this.cityControllers = new HashMap<City, CityController>();
  }

  public ControllerManager(InGameContext inGameContext) {
    this();
    this.inGameContext = inGameContext;
    this.game = inGameContext.getObj(Game.class);
  }

  /**
   * Create a Default Human Controller for each Unit and City in the game
   */
  public void createControllers() {
    cityControllers.clear();
    unitControllers.clear();

    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral() && !player.isAi()) {
        for (City city : player.getAllCities()) {
          setHumanCityController(city);
        }
        for (Unit unit : player.getArmy()) {
          setHumanUnitController(unit);
        }
      }
    }
  }

  public void setHumanCityController(City city) {
    CityController cityController = new HumanCityController(city, inGameContext);
    cityControllers.put(city, cityController);
  }

  public void setHumanUnitController(Unit unit) {
    UnitController unitController = new HumanUnitController(unit, inGameContext);
    unitControllers.put(unit, unitController);
  }

  public void handleUnitAPress(Unit unit) {
    if (unitControllers.containsKey(unit)) {
      HumanUnitController humanUnitController = getHumanUnitController(unit);
      humanUnitController.handleAPress();
    }
  }

  public void handleUnitBPress(Unit unit) {
    if (unitControllers.containsKey(unit)) {
      HumanUnitController humanUnitController = getHumanUnitController(unit);
      humanUnitController.handleBPress();
    }
  }

  private HumanUnitController getHumanUnitController(Unit unit) {
    if (unitControllers.containsKey(unit)) {
      UnitController unitController = unitControllers.get(unit);
      if (unitController instanceof HumanUnitController) {
        return (HumanUnitController) unitController;
      } else {
        throw new IllegalArgumentException("No human unit controller found for " + unit);
      }
    } else {
      throw new IllegalArgumentException("No human unit controller found for " + unit);
    }
  }

  public void handleCityAPress(City city) {
    if (cityControllers.containsKey(city)) {
      HumanCityController humanCityController = getHumanCityController(city);
      humanCityController.handleAPress();
    }
  }

  private HumanCityController getHumanCityController(City city) {
    if (cityControllers.containsKey(city)) {
      CityController cityController = cityControllers.get(city);
      if (cityController instanceof HumanCityController) {
        return (HumanCityController) cityController;
      } else {
        throw new IllegalArgumentException("No human city controller found for " + city);
      }
    } else {
      throw new IllegalArgumentException("No human city controller found for " + city);
    }
  }

  public void removeUnitController(Unit unit) {
    unitControllers.remove(unit);
  }

  public void removeCityController(City city) {
    cityControllers.remove(city);
  }
}
