package com.customwars.client.ui.sprite;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CitySprite extends TileSprite implements PropertyChangeListener {
  private final City city;
  private Animation animActive;
  private Animation animInActive;
  private Animation animFogged;
  private boolean renderFogged, renderActive = true;
  private final Color fogFilter = Color.lightGray;

  public CitySprite(TileMap<Tile> map, City city) {
    super(city.getLocation(), map);
    this.city = city;
    Tile cityLocation = (Tile) city.getLocation();
    this.renderFogged = cityLocation.isFogged();
    city.addPropertyChangeListener(this);
  }

  public void setAnimActive(Animation animActive) {
    this.animActive = animActive;
  }

  public void setAnimInActive(Animation animInActive) {
    this.animInActive = animInActive;
  }

  public void setAnimFogged(Animation animFogged) {
    this.animFogged = animFogged;
  }

  public void setLocation(Location newLocation) {
    Tile oldTile = (Tile) getLocation();
    super.setLocation(newLocation);
    Tile newTile = (Tile) newLocation;
    if (oldTile != null) oldTile.removePropertyChangeListener(this);
    newTile.addPropertyChangeListener(this);
  }

  public void updateAnim() {
    if (renderFogged) {
      super.enableFilter(fogFilter);
      setAnim(animFogged);
    } else if (renderActive) {
      super.disableFilter();
      setAnim(animActive);
    } else {
      super.disableFilter();
      setAnim(animInActive);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() == getLocation()) {
      if (propertyName.equals("fog")) {
        renderFogged = (Boolean) evt.getNewValue();
        updateAnim();
      }
    }

    if (evt.getSource() == city) {
      if (propertyName.equals("location")) {
        Tile newLocation = (Tile) evt.getNewValue();
        if (newLocation != null) {
          super.setLocation(newLocation);
        }
      } else if (propertyName.equals("launched")) {
        renderActive = false;
        updateAnim();
      }
    }
  }
}
