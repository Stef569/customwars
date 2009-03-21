package com.customwars.client.controller;

import com.customwars.client.action.CWAction;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.DirectAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.model.game.Player;
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
      CWAction addUnit = new AddUnitToTileAction(unit, selected, inGameContext);
      showCityPopupMenu.addAction(addUnit, unit.getID() + " " + unit.getName() + " " + unit.getPrice());
    }
    return showCityPopupMenu;
  }

  private class AddUnitToTileAction extends DirectAction {
    Unit unit;                            // Unit to be put on tile
    private Tile tile;                    // Tile to add unit to
    private InGameContext inGameContext;

    public AddUnitToTileAction(Unit unit, Tile tile, InGameContext inGameContext) {
      super("Add unit to tile", false);
      this.unit = unit;
      this.tile = tile;
      this.inGameContext = inGameContext;
    }

    protected void init(InGameContext context) {
    }

    protected void invokeAction() {
      Player cityOwner = city.getOwner();

      if (city.canBuild(unit) && cityOwner.canPurchase(unit)) {
        cityOwner.addUnit(unit);
        cityOwner.addToBudget(-unit.getPrice());
        tile.add(unit);
        inGameContext.addHumanUnitController(unit);
        game.getMap().buildMovementZone(unit);
        game.getMap().buildAttackZone(unit);
        game.setActiveUnit(unit);

        inGameContext.doAction(new WaitAction(unit));
        inGameContext.doAction(new ClearInGameStateAction());
      }
    }
  }
}
