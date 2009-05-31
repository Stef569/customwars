package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow to select a unit to attack (Attack mode)
 *
 * @author stefan
 */
public class StartAttackAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private Game game;
  private Unit unit;
  private Location to;

  public StartAttackAction(Unit unit, Location to) {
    super("Start Attack mode");
    this.unit = unit;
    this.to = to;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
    this.mapRenderer = context.getMapRenderer();
  }

  protected void invokeAction() {
    List<Unit> enemiesInRange = game.getMap().getEnemiesInRangeOf(unit, to);
    List<Location> enemyLocationsInRange = getEnemyLocations(enemiesInRange);

    mapRenderer.removeMoveZone();
    mapRenderer.activateCursor("ATTACK");
    mapRenderer.startCursorTraversal(enemyLocationsInRange);
    mapRenderer.setAttackZone(enemyLocationsInRange);
    context.setInputMode(InGameContext.INPUT_MODE.UNIT_ATTACK);
  }

  private List<Location> getEnemyLocations(List<Unit> enemies) {
    List<Location> enemyLocations = new ArrayList<Location>();
    for (Unit enemy : enemies) {
      enemyLocations.add(enemy.getLocation());
    }
    return enemyLocations;
  }

  public void undo() {
    mapRenderer.removeAttackZone();
    mapRenderer.stopCursorTraversal();

    mapRenderer.activateCursor("SELECT");
  }
}
