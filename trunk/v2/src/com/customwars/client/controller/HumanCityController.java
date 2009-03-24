package com.customwars.client.controller;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Allows a human to control a city
 * by using a gui
 *
 * @author stefan
 */
public class HumanCityController extends CityController {
  private InGameContext inGameContext;
  private MapRenderer mapRenderer;

  public HumanCityController(City city, InGameContext inGameContext) {
    super(city, inGameContext.getGame());
    this.inGameContext = inGameContext;
    this.mapRenderer = inGameContext.getMapRenderer();
  }

  public void handleAPress() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();

    if (inGameContext.isDefaultMode() && canShowMenu()) {
      CWAction showMenu = buildMenu(selected);
      inGameContext.doAction(showMenu);
    }
  }

  private boolean canShowMenu() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();
    return !selected.isFogged() && city.getLocation() == selected && city.canBuild();
  }

  private ShowPopupMenu buildMenu(Tile selected) {
    ShowPopupMenu showCityPopupMenu = new ShowPopupMenu("Buy unit menu", selected);

    for (Unit unit : UnitFactory.getAllUnits()) {
      if (city.canBuild(unit) && city.getOwner().isWithinBudget(unit.getPrice())) {
        unit.setOwner(city.getOwner());
        CWAction addUnitToTileAction = ActionFactory.buildAddUnitToTileAction(unit, selected, false, false);
        showCityPopupMenu.addAction(addUnitToTileAction, unit.getID() + " " + unit.getName() + " " + unit.getPrice());
      }
    }
    return showCityPopupMenu;
  }
}
