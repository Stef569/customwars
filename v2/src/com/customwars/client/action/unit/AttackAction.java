package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.UnitFight;
import com.customwars.client.ui.state.InGameSession;

/**
 * Attack a unit on the 3th Tile
 *
 * @author stefan
 */
public class AttackAction extends AbstractCWAction {
  private Game game;
  private InGameSession inGameSession;
  private UnitFight unitFight;

  public AttackAction(Game game, InGameSession inGameSession, UnitFight unitFight) {
    super("Attack", false);
    this.game = game;
    this.inGameSession = inGameSession;
    this.unitFight = unitFight;
  }

  protected void doActionImpl() {
    if (inGameSession.isTrapped()) return;

    Tile selected = inGameSession.getClick(3);
    Unit activeUnit = game.getActiveUnit();
    Unit defender = (Unit) selected.getLastLocatable();
    attackUnit(activeUnit, defender);
  }

  /**
   * @param attacker The unit that is attacking
   * @param defender The Unit that is under attacked
   */
  public void attackUnit(Unit attacker, Unit defender) {
    unitFight.initAttack(attacker, defender);
    attacker.attack(defender, unitFight);
    unitFight.clear();
  }
}