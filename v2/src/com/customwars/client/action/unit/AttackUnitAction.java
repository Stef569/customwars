package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
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

/**
 * The unit attacks another Unit
 */
public class AttackUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(AttackUnitAction.class);
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;
  private final Unit attacker, defender;

  /**
   * @param attacker The unit that is attacking
   * @param defender The Unit that is under attack
   */
  public AttackUnitAction(Unit attacker, Unit defender) {
    super("Attack", false);
    this.attacker = attacker;
    this.defender = defender;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      attackUnit();
    }
  }

  public void attackUnit() {
    // Send before the fight. One of the units may die...
    if (App.isMultiplayer()) sendAttackUnit();

    gameController.attack(attacker, defender);

    if (attacker.isDestroyed() || defender.isDestroyed()) {
      SFX.playSound("explode");
    }
  }



  private void sendAttackUnit() {
    try {
      messageSender.attack(attacker, defender);
    } catch (NetworkException ex) {
      logger.warn("Could not send attack unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendAttackUnit();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(defender.getLocation()).build();
  }
}
