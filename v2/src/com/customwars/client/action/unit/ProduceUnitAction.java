package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

public class ProduceUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(ProduceUnitAction.class);
  private InGameContext inGameContext;
  private final Unit producer;
  private final Unit unitToBuild;
  private GameController gameController;
  private MessageSender messageSender;

  public ProduceUnitAction(Unit producer, Unit unitToBuild) {
    super("produce unit", false);
    this.producer = producer;
    this.unitToBuild = unitToBuild;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.gameController = inGameContext.getObj(GameController.class);
    this.messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      produce();
    }
  }

  private void produce() {
    logger.debug(producer.getStats().getName() + " producing a " + unitToBuild.getStats().getName());
    gameController.buildUnit(unitToBuild, producer, producer.getOwner());
    if (App.isMultiplayer()) sendProduce();
  }

  private void sendProduce() {
    try {
      messageSender.buildUnit(unitToBuild, producer, producer.getOwner());
    } catch (NetworkException ex) {
      logger.warn("Could not send produce unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendProduce();
      }
    }
  }
}
