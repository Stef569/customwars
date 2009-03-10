package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameSession;

/**
 * The active unit joins the unit @ the 2nd selected tile.
 *
 * @author stefan
 */
public class JoinAction extends AbstractCWAction {
  private Game game;
  private InGameSession inGameSession;

  public JoinAction(Game game, InGameSession inGameSession) {
    super("Join", false);
    this.game = game;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    Map<Tile> map = game.getMap();
    Unit activeUnit = game.getActiveUnit();
    Unit target = (Unit) inGameSession.getClick(2).getLocatable(0);

    //Add Money if joining cause the target to go over 10HP
    int tempHp = activeUnit.getHp() + target.getHp() - target.getMaxHp();

    if (tempHp > 0)
      game.getActivePlayer().addToBudget((tempHp * activeUnit.getPrice()) / activeUnit.getMaxHp());

    // add HP, supplies, ammo to target
    target.addHp(activeUnit.getHp());
    target.addSupplies(activeUnit.getSupplies());
    target.addAmmo(activeUnit.getAvailableWeapon().getAmmo());

    // todo make the dived state of the unit being moved onto the same as the one moving
    //    target.setDived(activeUnit.isDived());

    // Remove Unit
    map.getTile(activeUnit.getLocation()).remove(activeUnit);
    activeUnit.getOwner().removeUnit(activeUnit);
    game.setActiveUnit(target);
  }
}
