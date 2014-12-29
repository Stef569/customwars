package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.model.drop.DropLocation;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

/**
 * Drop the unit within the transport to the drop location
 */
public class DropAction extends MoveAnimatedAction {
  private static final Logger logger = Logger.getLogger(DropAction.class);
  private final Unit transport;
  private final DropLocation dropLocation;
  private MessageSender messageSender;

  public DropAction(Unit transport, DropLocation dropLocation) {
    super(dropLocation.getUnit(), transport.getLocation(), dropLocation.getLocation());
    this.transport = transport;
    this.dropLocation = dropLocation;
  }

  protected void init(InGameContext inGameContext) {
    if (inGameContext.isTrapped()) {
      setActionCompleted(true);
      return;
    }

    messageSender = inGameContext.getObj(MessageSender.class);
    logger.debug(String.format("Dropping %s to %s from transport %s(%s)",
      unit.getStats().getName(), to.getLocationString(), transport.getStats().getName(), transport.getLocationString()));

    // MoveTraverse doesn't allow to move outside a transport location.
    // Put the unit on the transport location in the map.
    transport.remove(unit);
    transport.getLocation().add(unit);
    super.init(inGameContext);
  }

  void pathMoveComplete() {
    super.pathMoveComplete();

    Location transportLocation = transport.getLocation();
    // Duplicate sprites are removed, so add the transport back when the unit moved away.
    transportLocation.add(transport);

    // If the unit couldn't move away from the transport location
    // because the dropLocation was not clear, add the unit to the transport again
    if (transportLocation.contains(unit)) {
      transportLocation.remove(unit);
      transport.add(unit);
    } else {
      SFX.playSound("unload");
    }

    assert transportLocation.getLocatableCount() == 1 : "Only the transport is on the transportLocation";
    if (App.isMultiplayer()) sendDrop();
  }

  private void sendDrop() {
    try {
      messageSender.drop(transport, unit, to);
    } catch (NetworkException ex) {
      logger.warn("Could not send drop unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendDrop();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder()
      .add(transport.indexOf(dropLocation.getUnit()))
      .add(dropLocation.getLocation()).build();
  }
}
