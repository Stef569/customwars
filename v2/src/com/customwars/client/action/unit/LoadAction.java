package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Load the unit in the transport
 * and remove the unit sprite
 *
 * @author stefan
 */
public class LoadAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private Unit unit;
  private Unit transport;

  public LoadAction(Unit unit, Unit transport) {
    super("Load", false);
    this.unit = unit;
    this.transport = transport;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.mapRenderer = context.getMapRenderer();
  }

  protected void invokeAction() {
    if (context.isTrapped()) return;

    unit.getLocation().remove(unit);
    transport.add(unit);
    mapRenderer.removeUnit(unit);
  }
}