package com.customwars.client.action;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Tile;

/**
 * Capture a City
 *
 * @author stefan
 */
public class CaptureAction extends CWAction {
  private InGameSession session;
  private Game game;

  protected CaptureAction(Game game, InGameSession session) {
    super("Capture", false);
    this.game = game;
    this.session = session;
  }

  void doActionImpl() {
    if (session.isTrapped()) return;

    Tile clicked = session.getClick(2);
    Unit activeUnit = game.getActiveUnit();
    capture((City) clicked.getTerrain(), activeUnit);
  }

  private void capture(City city, Unit unit) {
    if (city.canBeCapturedBy(unit)) {
      unit.setUnitState(UnitState.CAPTURING);
      city.capture(unit);

      if (city.isCapturedBy(unit)) {
        unit.setUnitState(UnitState.IDLE);
      }
    }
  }

  void undoAction() {
  }
}
