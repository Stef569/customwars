package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.BuyUnitMenuItem;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

/**
 * Allows a human to control a city
 * by using a gui
 */
public class HumanCityController extends CityController {
  private static final org.newdawn.slick.Color DARKER_TEXT_COLOR = new org.newdawn.slick.Color(255, 255, 255, 100);
  private final InGameContext inGameContext;
  private final MapRenderer mapRenderer;
  private final ResourceManager resources;
  private final GUIContext guiContext;

  public HumanCityController(City city, InGameContext inGameContext) {
    super(city, inGameContext.getObj(Game.class));
    this.inGameContext = inGameContext;
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
    this.resources = inGameContext.getObj(ResourceManager.class);
    this.guiContext = inGameContext.getObj(GUIContext.class);
  }

  public void handleAPress() {
    Tile selected = mapRenderer.getCursorLocation();

    if (inGameContext.isDefaultMode() && canShowMenu()) {
      new ClearInGameStateAction().invoke(inGameContext);
      PopupMenu popupMenu = buildMenu(selected);
      inGameContext.doAction(new ShowPopupMenuAction(popupMenu));
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
  private PopupMenu buildMenu(Tile selected) {
    PopupMenu popupMenu = new PopupMenu(guiContext, "Buy city menu");

    for (Unit unit : UnitFactory.getAllUnits()) {
      if (city.canBuild(unit)) {
        int unitPrice = unit.getPrice();
        boolean canAffordUnit = city.getOwner().isWithinBudget(unitPrice);
        CWAction addUnitToTileAction;

        if (canAffordUnit) {
          addUnitToTileAction = ActionFactory.buildAddUnitToTileAction(unit, city.getOwner(), selected);
        } else {
          addUnitToTileAction = null;
        }

        MenuItem menuItem = buildMenuItem(unit, addUnitToTileAction);
        popupMenu.addItem(menuItem);
      }
    }
    return popupMenu;
  }

  private MenuItem buildMenuItem(Unit unit, CWAction action) {
    String unitName = App.translate(unit.getStats().getName());
    String unitPrice = "$" + unit.getPrice();
    Color cityColor = city.getOwner().getColor();
    Image unitImage = getUnitImg(unit, cityColor, action != null);
    MenuItem item = buildMenuItem(action, unitImage, unitName, unitPrice);

    if (action == null) {
      item.setTextColor(DARKER_TEXT_COLOR);
    }
    return item;
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

  /**
   * Build a menu item backed by a CWAction
   *
   * @param action The action to perform when clicked on the menu item
   */
  public MenuItem buildMenuItem(final CWAction action, Image img, String unitName, String unitPrice) {
    MenuItem menuItem = new BuyUnitMenuItem(img, unitName, unitPrice, guiContext.getDefaultFont(), guiContext);
    menuItem.addListener(new ComponentListener() {
      public void componentActivated(AbstractComponent source) {
        inGameContext.doAction(action);
      }
    });
    return menuItem;
  }
}

