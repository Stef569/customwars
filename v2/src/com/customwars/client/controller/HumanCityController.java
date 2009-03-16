package com.customwars.client.controller;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Allows a human to control a city
 * by using a gui
 *
 * @author stefan
 */
public class HumanCityController extends CityController {
  private ShowPopupMenu showCityPopupMenu;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private CWAction waitAction;
  private CWAction clearInGameState;

  public HumanCityController(Game game, City city, ActionManager actionManager, InGameSession inGameSession, MapRenderer mapRenderer, HUD hud) {
    super(city, game, actionManager);
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
    this.showCityPopupMenu = new ShowPopupMenu("Buy unit menu", hud, inGameSession, mapRenderer);
    this.clearInGameState = actionManager.getAction("CLEAR_INGAME_STATE");
    this.waitAction = actionManager.getAction("UNIT_WAIT");
  }

  public void handleAPress() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();

    if (inGameSession.isDefaultMode() && canShowMenu()) {
      buildMenu(selected);

      // ShowPopUpMenu displays the popup on the 2nd tile
      inGameSession.setClick(2, selected);
      inGameSession.doAction(showCityPopupMenu);
    }
  }

  private boolean canShowMenu() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();
    return !selected.isFogged() && city.getLocation() == selected && city.canBuild();
  }

  private void buildMenu(Tile selected) {
    showCityPopupMenu.clear();

    for (Unit unit : UnitFactory.getAllUnits()) {
      AbstractCWAction addUnit = new AddUnitToTileAction(unit, selected);
      showCityPopupMenu.addAction(addUnit, unit.getID() + " " + unit.getName() + " " + unit.getPrice());
    }
  }

  private class AddUnitToTileAction extends AbstractCWAction {
    Unit unit;
    private Tile t;

    public AddUnitToTileAction(Unit unit, Tile t) {
      super("Add unit to tile", false);
      this.unit = unit;
      this.t = t;
    }

    protected void doActionImpl() {
      Player cityOwner = city.getOwner();

      if (city.canBuild(unit) && cityOwner.canPurchase(unit)) {
        cityOwner.addUnit(unit);
        cityOwner.addToBudget(-unit.getPrice());
        t.add(unit);
        inGameSession.addHumanUnitController(unit);
        game.getMap().buildMovementZone(unit);
        game.getMap().buildAttackZone(unit);
        game.setActiveUnit(unit);

        waitAction.doAction();
        waitAction.setActionCompleted(false);

        clearInGameState.doAction();
        clearInGameState.setActionCompleted(false);
      }
    }
  }
}
