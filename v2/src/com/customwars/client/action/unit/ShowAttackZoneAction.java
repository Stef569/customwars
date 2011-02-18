package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Show a zone around the unit in which it can attack
 * Showing the attackzone does not select the unit, it only shows a visible zone
 *
 * @author stefan
 */
public class ShowAttackZoneAction extends DirectAction {
  private InGameContext inGameContext;
  private MapRenderer mapRenderer;
  private final Unit unit;
  private Map map;

  public ShowAttackZoneAction(Unit unit) {
    super("Show Attack zone");
    this.unit = unit;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.map = inGameContext.getObj(Game.class).getMap();
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
  }

  protected void invokeAction() {
    inGameContext.clearUndoHistory();

    mapRenderer.removeZones();
    map.buildMovementZone(unit);
    map.buildAttackZone(unit);
    mapRenderer.setAttackZone(unit.getAttackZone());
  }

  public void undo() {
    mapRenderer.removeAttackZone();
  }
}
