package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

import java.util.HashMap;

/**
 * Manages every Controller in the game
 *
 * @author stefan
 */
public class ControllerManager {
  private HashMap<Unit, UnitController> unitControllers;
  private HashMap<City, CityController> cityControllers;
  private Game game;
  private ActionManager actionManager;
  private MoveTraverse moveTraverse;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private HUD hud;

  private ControllerManager() {
    this.unitControllers = new HashMap<Unit, UnitController>();
    this.cityControllers = new HashMap<City, CityController>();
  }

  public ControllerManager(Game game, ActionManager actionManager, MoveTraverse moveTraverse, InGameSession inGameSession, MapRenderer mapRenderer, HUD hud) {
    this();
    this.game = game;
    this.actionManager = actionManager;
    this.moveTraverse = moveTraverse;
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
    this.hud = hud;
  }

  public void initCityControllers() {
    cityControllers.clear();
    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral()) {
        if (!player.isAi()) {
          for (City city : player.getAllCities()) {
            CityController cityController = new HumanCityController(game, city, actionManager, inGameSession, mapRenderer, hud);
            cityControllers.put(city, cityController);
          }
        }
      }
    }
  }

  public void initUnitControllers() {
    unitControllers.clear();
    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral())
        if (!player.isAi()) {
          for (Unit unit : player.getArmy()) {
            UnitController unitController = new HumanUnitController(game, unit, actionManager, moveTraverse, inGameSession, mapRenderer);
            unitControllers.put(unit, unitController);
          }
        }
    }
  }

  public void handleUnitAPress(Unit unit) {
    UnitController unitController = unitControllers.get(unit);
    if (unitController instanceof HumanUnitController) {
      HumanUnitController humanUnitController = (HumanUnitController) unitController;
      humanUnitController.handleAPress();
    }
  }

  public void handleUnitBPress(Unit unit) {
    UnitController unitController = unitControllers.get(unit);
    if (unitController instanceof HumanUnitController) {
      HumanUnitController humanUnitController = (HumanUnitController) unitController;
      humanUnitController.handleBPress();
    }
  }

  public void handleCityAPress(City city) {
    CityController cityController = cityControllers.get(city);
    if (cityController instanceof HumanCityController) {
      HumanCityController humanCityController = (HumanCityController) cityController;
      humanCityController.handleAPress();
    }
  }

  public void addHumanUnitController(Unit unit) {
    UnitController unitController = new HumanUnitController(game, unit, actionManager, moveTraverse, inGameSession, mapRenderer);
    unitControllers.put(unit, unitController);
  }

  public void addHumanCityController(City city) {
    HumanCityController unitController = new HumanCityController(game, city, actionManager, inGameSession, mapRenderer, hud);
    cityControllers.put(city, unitController);
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
}
