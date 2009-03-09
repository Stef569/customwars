package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow to select a unit to attack (Attack mode)
 *
 * @author stefan
 */
public class StartAttackAction extends AbstractCWAction {
  private Game game;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;

  public StartAttackAction(Game game, MapRenderer mapRenderer, InGameSession unitSession) {
    super("Start Attack mode");
    this.game = game;
    this.mapRenderer = mapRenderer;
    this.inGameSession = unitSession;
  }

  protected void doActionImpl() {
    Tile selectedTile = inGameSession.getClick(2);
    Unit activeUnit = game.getActiveUnit();
    List<Unit> enemiesInRange = game.getMap().getEnemiesInRangeOf(activeUnit, selectedTile);
    List<Location> enemyLocationsInRange = getEnemyLocations(enemiesInRange);

    mapRenderer.activateCursor("ATTACK");
    mapRenderer.startCursorTraversal(enemyLocationsInRange);
    mapRenderer.removeMoveZone();
    mapRenderer.setAttackZone(enemyLocationsInRange);
    inGameSession.setMode(InGameSession.MODE.UNIT_ATTACK);
  }

  private List<Location> getEnemyLocations(List<Unit> enemies) {
    List<Location> enemyLocations = new ArrayList<Location>();
    for (Unit enemy : enemies) {
      enemyLocations.add(enemy.getLocation());
    }
    return enemyLocations;
  }

  public void undoAction() {
    mapRenderer.removeAttackZone();
    mapRenderer.stopCursorTraversal();

    mapRenderer.activateCursor("SELECT");
    mapRenderer.stopCursorTraversal();
  }
}
