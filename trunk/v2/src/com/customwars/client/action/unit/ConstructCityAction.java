package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.MapUtil;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

public class ConstructCityAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(ConstructCityAction.class);
  private Unit unit;
  private Map<Tile> map;
  private Tile tile;
  private City city;
  private Player cityOwner;
  private InGameContext context;
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
    this.context = context;
    controllerManager = context.getControllerManager();
    map = context.getGame().getMap();
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      constructCity();
    }
  }

  private void constructCity() {
    unit.construct(city);
    logConstructingProgress();

    if (unit.isConstructionComplete()) {
      unit.stopConstructing();
      addCityToTile();
    }
  }

  private void logConstructingProgress() {
    if (unit.isConstructionComplete()) {
      logger.debug(
        String.format("%s constructed a %s",
          unit.getStats().getName(), city.getName())
      );
    } else {
      logger.debug(
        String.format("%s is constructing a %s constructed:%s%%",
          unit.getStats().getName(), city.getName(), city.getCapCountPercentage())
      );
    }
  }

  private void addCityToTile() {
    MapUtil.addCityToMap(map, tile, city, cityOwner);

    if (cityOwner.isAi()) {
      controllerManager.addAICityController(city);
    } else {
      controllerManager.addHumanCityController(city);
    }
  }
}