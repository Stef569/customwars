package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

public class TakeOffAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(TakeOffAction.class);
  private final Unit launcher;
  private final Unit unitReadyForTakeOff;

  public TakeOffAction(Unit launcher, Unit unitReadyForTakeOff) {
    super("Take off", true);
    this.launcher = launcher;
    this.unitReadyForTakeOff = unitReadyForTakeOff;
  }

  @Override
  protected void init(InGameContext context) {
  }

  @Override
  protected void invokeAction() {
    logger.debug(unitReadyForTakeOff.getStats().getName() + " taking off from " + launcher);

    launcher.remove(unitReadyForTakeOff);
    launcher.getLocation().add(unitReadyForTakeOff);
  }
}
