package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * The unit attacks a City
 */
public class AttackCityAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(AttackCityAction.class);
  private final Unit attacker;
  private final City city;
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;

  /**
   * @param attacker The unit that is attacking
   * @param city     The city that is under attack
   */
  public AttackCityAction(Unit attacker, City city) {
    super("Attack City", false);
    this.attacker = attacker;
    this.city = city;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      attackCity();
    }
  }

  public void attackCity() {
    gameController.attack(attacker, city);

    if (city.isDestroyed()) {
      SFX.playSound("explode");
    }

    if (App.isMultiplayer()) sendAttackCity();
  }

  private void sendAttackCity() {
    try {
      messageSender.attack(attacker, city);
    } catch (NetworkException ex) {
      logger.warn("Could not send attack city", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendAttackCity();
      }
    }
  }
}
