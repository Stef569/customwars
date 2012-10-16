package com.customwars.client.controller;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages every Controller in the game
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

  public void initCityControllers() {
    cityControllers.clear();
    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral()) {
        if (player.isAi()) {
          for (City city : player.getAllCities()) {
            setAICityController(city);
          }
        } else {
          for (City city : player.getAllCities()) {
            setHumanCityController(city);
          }
        }
      }
    }
  }

  public void setHumanCityController(City city) {
    CityController cityController = new HumanCityController(city, inGameContext);
    cityControllers.put(city, cityController);
  }

  public void setAICityController(City city) {

  }

  public void initUnitControllers() {
    unitControllers.clear();
    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral()) {
        if (player.isAi()) {
          for (Unit unit : player.getArmy()) {
            setAIUnitController(unit);
          }
        } else {
          for (Unit unit : player.getArmy()) {
            setHumanUnitController(unit);
          }
        }
      }
    }
  }

  public void setHumanUnitController(Unit unit) {
    UnitController unitController = new HumanUnitController(unit, inGameContext);
    unitControllers.put(unit, unitController);
  }

  public void setAIUnitController(Unit unit) {

  }

  public void handleUnitAPress(Unit unit) {
    HumanUnitController humanUnitController = getHumanUnitController(unit);
    humanUnitController.handleAPress();
  }

  public void handleUnitBPress(Unit unit) {
    HumanUnitController humanUnitController = getHumanUnitController(unit);
    humanUnitController.handleBPress();
  }

  private HumanUnitController getHumanUnitController(Unit unit) {
    UnitController unitController = unitControllers.get(unit);
    if (unitController instanceof HumanUnitController) {
      return (HumanUnitController) unitController;
    } else {
      throw new IllegalArgumentException("No human unit controller found for " + unit);
    }
  }

  public void handleCityAPress(City city) {
    HumanCityController humanCityController = getHumanCityController(city);
    humanCityController.handleAPress();
  }

  private HumanCityController getHumanCityController(City city) {
    CityController cityController = cityControllers.get(city);
    if (cityController instanceof HumanCityController) {
      return (HumanCityController) cityController;
    } else {
      throw new IllegalArgumentException("No human city controller found for " + city);
    }
  }

  public void removeAllHumanController() {
    for (Player player : game.getAllPlayers()) {
      for (Unit unit : player.getArmy()) {
        UnitController unitController = unitControllers.get(unit);
        if (unitController instanceof HumanUnitController) {
          unitControllers.remove(unit);
        }
      }
      for (City city : player.getAllCities()) {
        CityController cityController = cityControllers.get(city);
        if (cityController instanceof HumanCityController) {
          cityControllers.remove(city);
        }
      }
    }
  }

  public void removeUnitController(Unit unit) {
    unitControllers.remove(unit);
  }

  public void removeCityController(City city) {
    cityControllers.remove(city);
  }
}
