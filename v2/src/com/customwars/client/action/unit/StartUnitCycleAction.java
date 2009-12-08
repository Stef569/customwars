package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.MapUtil;
import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StartUnitCycleAction extends DirectAction {
  private InGameContext inGameContext;
  private CursorController cursorControl;
  private Map<Tile> map;

  public StartUnitCycleAction() {
    super("Start unit Cycle");
  }

  protected void init(InGameContext context) {
    this.inGameContext = context;
    this.cursorControl = context.getCursorController();
    this.map = context.getGame().getMap();
  }

  protected void invokeAction() {
    Collection<Locatable> units = convertToLocatables(map.getActiveUnits());
    List<Location> unitLocations = MapUtil.getLocationsFor(units);
    cursorControl.startCursorTraversal(unitLocations);
    inGameContext.setInputMode(InGameContext.INPUT_MODE.UNIT_CYCLE);
    cursorControl.moveCursorToNextLocation();
  }

  private Collection<Locatable> convertToLocatables(Collection<Unit> units) {
    Collection<Locatable> locatables = new ArrayList<Locatable>();
    for (Unit unit : units) {
      locatables.add(unit);
    }
    return locatables;
  }

  @Override
  public void undo() {
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.stopCursorTraversal();
  }
}
