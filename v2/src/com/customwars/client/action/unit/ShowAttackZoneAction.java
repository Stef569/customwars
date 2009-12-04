package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Show a zone around the unit in which it can attack
 * Showing the attackzone does not select the unit, it only shows a visible zone
 *
 * @author stefan
 */
public class ShowAttackZoneAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private final Unit unit;

  public ShowAttackZoneAction(Unit unit) {
    super("Show Attack zone");
    this.unit = unit;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.mapRenderer = context.getMapRenderer();
  }

  protected void invokeAction() {
    context.clearUndoHistory();

    mapRenderer.removeZones();
    mapRenderer.setAttackZone(unit.getAttackZone());
  }

  public void undo() {
    mapRenderer.removeAttackZone();
  }
}
