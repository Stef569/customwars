package com.customwars.client.action.unit;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.state.InGameContext;

/**
 * Drop next unit from the inGameContext within the transport
 * to the next inGameContext drop location
 *
 * @author stefan
 */
public class DropAction extends MoveAnimatedAction {
  private Unit transporter;

  public DropAction(Unit transporter) {
    super(null, transporter.getLocation(), null);
    this.transporter = transporter;
  }

  protected void init(InGameContext context) {
    if (context.isTrapped()) {
      setActionCompleted(true);
      return;
    }

    to = context.getNextDropLocation();
    unit = context.getNextUnitToBeDropped();

    // MoveTraverse doesn't allow to move outside a transport location.
    // Put the unit on the transport location in the map.
    transporter.remove(unit);
    transporter.getLocation().add(unit);
    super.init(context);
  }

  void pathMoveComplete() {
    super.pathMoveComplete();

    Location transportLocation = transporter.getLocation();
    // Duplicate sprites are removed, so add the transport back when the unit moved away.
    transportLocation.add(transporter);

    // If the unit couldn't move away from the transport location
    // because the dropLocation was not clear, add the unit to the transport again
    if (transportLocation.contains(unit)) {
      transportLocation.remove(unit);
      transporter.add(unit);
    }

    assert transportLocation.getLocatableCount() == 1 : "Only the transport is on the transportLocation";
  }
}
