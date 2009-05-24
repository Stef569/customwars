package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

public class ConstructCityAction extends DirectAction {
  private Unit unit;
  private Tile tile;
  private City city;
  private Player cityOwner;
  private ControllerManager controllerManager;

  public ConstructCityAction(Unit unit, City city, Tile tile, Player cityOwner) {
    super("Construct City", false);
    this.unit = unit;
    this.tile = tile;
    this.city = city;
    this.cityOwner = cityOwner;
  }

  @Override
  protected void init(InGameContext context) {
    controllerManager = context.getControllerManager();
  }

  @Override
  protected void invokeAction() {
    unit.construct(city);

    if (unit.isConstructionComplete()) {
      unit.stopConstructing();
      addCityToTile();
    }
  }

  private void addCityToTile() {
    city.setOwner(cityOwner);
    city.setLocation(tile);
    tile.setTerrain(city);
    cityOwner.addCity(city);

    if (cityOwner.isAi()) {
      controllerManager.addAICityController(city);
    } else {
      controllerManager.addHumanCityController(city);
    }
  }
}