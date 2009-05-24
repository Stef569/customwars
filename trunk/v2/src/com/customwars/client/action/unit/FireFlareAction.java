package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

/**
 * Fires a flare
 * all tiles within flare range, surrounding the center tile are revealed for 1 turn.
 *
 * @author stefan
 */
public class FireFlareAction extends DirectAction {
  private Tile flareCenter;
  private int flareRange;
  private Map<Tile> map;

  public FireFlareAction(Tile flareCenter) {
    super("Fire Flare", false);
    this.flareCenter = flareCenter;
  }

  @Override
  protected void init(InGameContext context) {
    this.map = context.getGame().getMap();
    flareRange = App.getInt("plugin.flare_range");
  }

  @Override
  protected void invokeAction() {
    for (Tile t : map.getSurroundingTiles(flareCenter, 1, flareRange)) {
      t.setFogged(false);
    }
  }
}
