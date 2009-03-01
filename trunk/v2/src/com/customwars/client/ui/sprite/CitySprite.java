package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CitySprite extends TileSprite implements PropertyChangeListener {
  private City city;
  private Animation animActive;
  private Animation animFogged;
  private boolean renderFogged;
  private ImageStrip decorations;

  public CitySprite(Location tile, TileMap map, City city, ImageStrip decorations) {
    super(tile, map);
    this.city = city;
    this.decorations = decorations;
    city.addPropertyChangeListener(this);
  }

  public void setAnimActive(Animation animActive) {
    this.animActive = animActive;
  }

  public void setAnimFogged(Animation animFogged) {
    this.animFogged = animFogged;
  }

  public void setRenderFoggedImg(boolean renderFogged) {
    this.renderFogged = renderFogged;
    updateAnim();
  }

  public void setLocation(Location newLocation) {
    Tile oldTile = (Tile) getLocation();
    super.setLocation(newLocation);
    Tile newTile = (Tile) newLocation;
    if (oldTile != null) oldTile.removePropertyChangeListener(this);
    newTile.addPropertyChangeListener(this);
  }

  public void render(int x, int y, Graphics g) {
    super.render(x, y, g);
  }

  public void updateAnim() {
    setAnim(renderFogged ? animFogged : animActive);
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
      if (propertyName.equalsIgnoreCase("location")) {
        Tile newLocation = (Tile) evt.getNewValue();
        if (newLocation != null) {
          super.setLocation(newLocation);
        }
      }
    }
  }
}
