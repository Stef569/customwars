package com.customwars.client.action.city;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.state.InGameContext;

import java.util.List;

public class LaunchRocketAction extends DirectAction {
  private City city;
  private Unit unit;
  private GameRenderer gameRenderer;

  public LaunchRocketAction(City city, Unit unit) {
    super("Launch Rocket", false);
    this.city = city;
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext context) {
    gameRenderer = context.getGameRenderer();
  }

  @Override
  public void invokeAction() {
    city.launchRocket(unit);
    List<Location> effectRange = gameRenderer.getCursorEffectRange();
    gameRenderer.setExplosionArea(effectRange);

    for (Location location : effectRange) {
      if (location instanceof Tile) {
        Tile t = (Tile) location;
        Locatable locatable = t.getLastLocatable();

        if (locatable instanceof Unit) {
          Unit unit = (Unit) locatable;
          unit.addHp(-App.getInt("plugin.rocketdamage"));
        }
      }
    }
  }
}
