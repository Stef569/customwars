package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

public class ProduceUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(ProduceUnitAction.class);
  private InGameContext inGameContext;
  private final Unit producer;
  private final String unitToProduce;
  private GameController gameController;
  private MessageSender messageSender;

  public ProduceUnitAction(Unit producer, String unitToProduce) {
    super("produce unit", false);
    this.producer = producer;
    this.unitToProduce = unitToProduce;
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
    gameController.produceUnit(producer, unitToProduce);
    if (App.isMultiplayer()) sendProduce();
  }

  private void sendProduce() {
    try {
      messageSender.produceUnit(producer, unitToProduce, producer.getOwner());
    } catch (NetworkException ex) {
      logger.warn("Could not send produce unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendProduce();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder()
      .add(producer.getLocation())
      .add(unitToProduce).build();
  }
}
