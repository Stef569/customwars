package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Destroys the selected unit
 */
public class DeleteUnitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(DeleteUnitAction.class);
  private final Location selected;
  private MessageSender messageSender;
  private Map map;

  public DeleteUnitAction(Location selected) {
    super("Delete unit", false);
    this.selected = selected;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    messageSender = inGameContext.getObj(MessageSender.class);
    map = inGameContext.getObj(Game.class).getMap();
  }

  @Override
  protected void invokeAction() {
    if (App.isMultiplayer()) sendDeleteUnit();

    Unit unit = map.getUnitOn(selected);
    unit.destroy(true);
  }

  private void sendDeleteUnit() {
    try {
      messageSender.deleteUnit(selected);
    } catch (NetworkException ex) {
      logger.warn("Could not send delete unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendDeleteUnit();
      }
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(selected).build();
  }
}
