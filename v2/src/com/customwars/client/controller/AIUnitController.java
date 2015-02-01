package com.customwars.client.controller;

import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.path.MoveTraverse;

public class AIUnitController {
  private final GameController gameController;
  private final Unit unit;
  private final Map map;

  public AIUnitController(Map map, Unit unit, GameController gameController) {
    this.map = map;
    this.unit = unit;
    this.gameController = gameController;
  }

  public void moveTo(Location destination) {
    MoveTraverse moveTraverse = new MoveTraverse(map);
    moveTraverse.prepareMove(unit, destination);

    while (!moveTraverse.isPathMoveComplete()) {
      moveTraverse.update();
    }
    gameController.makeUnitWait(unit);
  }

  public void captureCity(City city) {
    gameController.teleport(unit.getLocation(), city.getLocation());
    gameController.capture(unit, city);
    gameController.makeUnitWait(unit);
  }

  public void attackEnemyDirect(Unit enemy) {
    gameController.attack(unit, enemy);
    gameController.makeUnitWait(unit);
  }

  public void attackCity(Unit unit, City city) {
    gameController.attack(unit, city);
    gameController.makeUnitWait(unit);
  }

  public void fireSiloRocket(City city, Location destination, Location rocketDestination) {
    gameController.teleport(unit.getLocation(), destination);
    gameController.launchRocket(unit, city, rocketDestination, 4);
  }

  public void join(Unit target) {
    gameController.join(unit, target);
  }

  public void loadCo(City city) {
    gameController.teleport(unit.getLocation(), city.getLocation());
    gameController.loadCO(unit);
  }

  public void dive() {
    gameController.dive(unit);
  }

  public void surface() {
    gameController.surface(unit);
  }

  public void coPower() {
    gameController.coPower();
  }
}
