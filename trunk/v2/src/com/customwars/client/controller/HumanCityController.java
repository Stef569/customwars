package com.customwars.client.controller;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.Image;

import java.awt.Color;

/**
 * Allows a human to control a city
 * by using a gui
 *
 * @author stefan
 */
public class HumanCityController extends CityController {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private ResourceManager resources;

  public HumanCityController(City city, InGameContext inGameContext) {
    super(city, inGameContext.getGame());
    this.context = inGameContext;
    this.mapRenderer = inGameContext.getMapRenderer();
    this.resources = context.getResourceManager();

  }

  public void handleAPress() {
    Tile selected = mapRenderer.getCursorLocation();

    if (context.isDefaultMode() && canShowMenu()) {
      CWAction showMenu = buildMenu(selected);
      context.doAction(showMenu);
    }
  }

  private boolean canShowMenu() {
    Tile selected = mapRenderer.getCursorLocation();
    return !selected.isFogged() && city.getLocation() == selected && city.canBuild();
  }

  /**
   * Get Unit copies, display units that this city can build
   * Units that cannot be bought show a darker unit image and have a null action when clicked on.
   */
  private ShowPopupMenu buildMenu(Tile selected) {
    ShowPopupMenu showCityPopupMenu = new ShowPopupMenu("Buy unit menu", selected);

    for (Unit unit : UnitFactory.getAllUnits()) {
      if (city.canBuild(unit)) {
        boolean canAffordUnit = city.getOwner().isWithinBudget(unit.getPrice());
        MenuItem menuItem = buildMenuItem(unit, canAffordUnit);

        if (canAffordUnit) {
          unit.setOwner(city.getOwner());
          CWAction action = ActionFactory.buildAddUnitToTileAction(unit, selected, false);
          showCityPopupMenu.addAction(action, menuItem);
        } else {
          showCityPopupMenu.addAction(null, menuItem);
        }
      }
    }
    return showCityPopupMenu;
  }

  private MenuItem buildMenuItem(Unit unit, boolean active) {
    String unitInfo = unit.getName() + " " + unit.getPrice();
    Color cityColor = city.getOwner().getColor();
    Image unitImage = getUnitImg(unit, cityColor, active);
    return new MenuItem(unitImage, unitInfo, context.getContainer());
  }

  private Image getUnitImg(Unit unit, Color color, boolean active) {
    Image unitImg;

    if (active) {
      unitImg = resources.getUnitImg(unit, color, Direction.EAST);
    } else {
      unitImg = resources.getShadedUnitImg(unit, color, Direction.EAST);
    }
    return unitImg;
  }
}

