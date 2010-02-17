package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.model.drop.DropLocation;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Drop the unit within the transport to the drop location
 *
 * @author stefan
 */
public class DropAction extends MoveAnimatedAction {
  private static final Logger logger = Logger.getLogger(DropAction.class);
  private final Unit transport;

  public DropAction(Unit transport, DropLocation dropLocation) {
    super(dropLocation.getUnit(), transport.getLocation(), dropLocation.getLocation());
    this.transport = transport;
  }

  protected void init(InGameContext context) {
    if (context.isTrapped()) {
      setActionCompleted(true);
      return;
    }

    logger.debug(String.format("Dropping %s to %s from transport %s",
      unit.getStats().getName(), to.getLocationString(), transport.getStats().getName()));

    // MoveTraverse doesn't allow to move outside a transport location.
    // Put the unit on the transport location in the map.
    transport.remove(unit);
    transport.getLocation().add(unit);
    super.init(context);
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
  }
}
