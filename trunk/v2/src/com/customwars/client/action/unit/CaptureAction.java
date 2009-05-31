package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.ui.state.InGameContext;

/**
 * Capture the given City with the given unit
 *
 * @author stefan
 */
public class CaptureAction extends DirectAction {
  private InGameContext context;
  private ControllerManager controllerManager;
  private Unit unit;
  private City city;

  public CaptureAction(Unit unit, City city) {
    super("Capture", false);
    this.unit = unit;
    this.city = city;
  }

  protected void init(InGameContext context) {
    this.context = context;
    controllerManager = context.getControllerManager();
  }

  protected void invokeAction() {
    if (context.isTrapped()) return;
    capture();
  }

  private void capture() {
    unit.setUnitState(UnitState.CAPTURING);
    city.capture(unit);

    if (city.isCapturedBy(unit)) {
      unit.setUnitState(UnitState.IDLE);
      controllerManager.addHumanCityController(city);
      city.resetCapturing();
      SFX.playSound("captured");
    }
  }
}
