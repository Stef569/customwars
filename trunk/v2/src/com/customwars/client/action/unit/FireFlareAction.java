package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Fires a flare
 * all tiles within flare range, surrounding the center tile are revealed.
 *
 * @author stefan
 */
public class FireFlareAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(FireFlareAction.class);
  private final Location flareCenter;
  private final int flareRange;
  private Map<Tile> map;
  private InGameContext context;

  public FireFlareAction(Location flareCenter) {
    super("Fire flare", false);
    this.flareCenter = flareCenter;
    this.flareRange = App.getInt("plugin.flare_range");
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    this.map = context.getGame().getMap();
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      fireFlare();
    }
  }

  private void fireFlare() {
    logger.debug("Revealing " + flareRange + " tiles around " + flareCenter.getLocationString());

    map.getTile(flareCenter).setFogged(false);
    for (Tile t : map.getSurroundingTiles(flareCenter, 1, flareRange)) {
      t.setFogged(false);
    }
  }
}
