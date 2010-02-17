package com.customwars.client.action.city;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LaunchRocketAction extends DirectAction {
  private MapRenderer mapRenderer;
  private final City rocketSilo;
  private final Unit rocketLauncher;
  private final Location rocketDestination;
  private InGameContext context;
  private Map<Tile> map;

  public LaunchRocketAction(City rocketSilo, Unit rocketLauncher, Location rocketDestination) {
    super("Launch Rocket", false);
    this.rocketSilo = rocketSilo;
    this.rocketLauncher = rocketLauncher;
    this.rocketDestination = rocketDestination;
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    mapRenderer = context.getMapRenderer();
    map = context.getGame().getMap();
  }

  @Override
  public void invokeAction() {
    if (context.isTrapped()) return;

    rocketSilo.launchRocket(rocketLauncher);
    int effectRange = mapRenderer.getCursorEffectRange();
    Collection<Location> explosionArea = getExplosionArea(effectRange);
    mapRenderer.setExplosionArea(explosionArea);

    inflictDamage(explosionArea);
  }

  private void inflictDamage(Collection<Location> explosionArea) {
    for (Location location : explosionArea) {
      Locatable locatable = location.getLastLocatable();

      if (locatable instanceof Unit) {
        Unit unit = (Unit) locatable;
        int siloRocketDamage = App.getInt("plugin.silo_rocket_damage");
        unit.addHp(-siloRocketDamage);
      }
    }
  }

  private Collection<Location> getExplosionArea(int effectRange) {
    List<Location> explosionArea = new ArrayList<Location>();
    explosionArea.add(rocketDestination);

    explosionArea.add(rocketDestination);
    for (Location tile : map.getSurroundingTiles(rocketDestination, 1, effectRange)) {
      explosionArea.add(tile);
    }
    return explosionArea;
  }
}
