package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

public class ProduceUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(ProduceUnitAction.class);
  private InGameContext context;
  private final Unit producer;
  private final Unit unitToBuild;
  private ControllerManager controllerManager;

  public ProduceUnitAction(Unit producer, Unit unitToBuild) {
    super("produce unit", false);
    this.producer = producer;
    this.unitToBuild = unitToBuild;
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    this.controllerManager = context.getControllerManager();
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      produce();
    }
  }

  private void produce() {
    logger.debug(producer.getName() + " producing a " + unitToBuild.getName());
    producer.getOwner().addUnit(unitToBuild);
    producer.add(unitToBuild);

    if (producer.getOwner().isAi()) {
      controllerManager.addAIUnitController(unitToBuild);
    } else {
      controllerManager.addHumanUnitController(unitToBuild);
    }
  }
}
