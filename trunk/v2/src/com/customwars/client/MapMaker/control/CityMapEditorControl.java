package com.customwars.client.MapMaker.control;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

public class CityMapEditorControl implements MapEditorControl {

  @Override
  public void addToTile(Tile t, int id, Player player) {
    City city = CityFactory.getCity(id);
    city.setOwner(player);
    player.addCity(city);
    city.setLocation(t);
    t.setTerrain(city);
  }

  @Override
  public void removeFromTile(Tile t) {
    t.setTerrain(CityFactory.getCity(0));
  }

  @Override
  public void fillMap(Map<Tile> map, int id) {
    // map can't be filled with cities
  }

  @Override
  public boolean isTypeOf(Class c) {
    return c == City.class;
  }
}
