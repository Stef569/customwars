package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Render events to the screen ie
 * unit is supplied, unit is trapped, etc.
 *
 * @author stefan
 */
public class ModelEventsRenderer implements PropertyChangeListener {
  private Game game;
  private Map<Tile> map;
  private MoveTraverse moveTraverse;
  private Image trappedImg, suppliedImg;
  private boolean renderTrapped, renderSupply;
  private int time, TRAPPED_DELAY = 800, SUPPLY_DELAY = 800;
  private int moveTime, moveX, MOVING_DELAY = 50;
  private List<Location> renderLocations;

  public ModelEventsRenderer() {
    renderLocations = new LinkedList<Location>();
  }

  public void loadResources(ResourceManager resources) {
    trappedImg = resources.getSlickImg("TRAPPED");
    suppliedImg = resources.getSlickImg("SUPPLIED");
  }

  public void setGame(Game game) {
    if (this.game != null) {
      removeModelEventListeners(this.game);
    }
    this.game = game;
    this.map = game.getMap();
    addModelEventListeners(game);
  }

  private void addModelEventListeners(Game game) {
    game.addPropertyChangeListener(this);
    Map<Tile> map = game.getMap();

    for (Tile t : map.getAllTiles()) {
      Unit unit = map.getUnitOn(t);
      if (unit != null) unit.addPropertyChangeListener(this);

      City city = map.getCityOn(t);
      if (city != null) city.addPropertyChangeListener(this);
    }
  }

  public void removeModelEventListeners(Game game) {
    game.removePropertyChangeListener(this);
    Map<Tile> map = game.getMap();

    for (Tile t : map.getAllTiles()) {
      Unit unit = map.getUnitOn(t);
      if (unit != null) unit.removePropertyChangeListener(this);

      City city = map.getCityOn(t);
      if (city != null) city.removePropertyChangeListener(this);
    }
  }

  public void update(int elapsedTime) {
    time += elapsedTime;

    if (moveTraverse != null && moveTraverse.foundTrapper()) {
      trapperFound();
    }

    if (renderTrapped) {
      updateTrapped();
    }


    if (renderSupply) {
      updateSupply();
    }

    if (renderSupply || renderTrapped) {
      moveTime += elapsedTime;
      if (moveTime >= MOVING_DELAY) {
        moveX--;
        moveTime = 0;
      }
    }
  }

  private void updateTrapped() {
    if (time >= TRAPPED_DELAY) {
      renderTrapped = false;
      renderLocations.remove(0);
      time = 0;
      moveX = 0;
    }
  }

  private void updateSupply() {
    if (time >= SUPPLY_DELAY) {
      if (!renderLocations.isEmpty()) {
        renderLocations.remove(0);
      }

      if (renderLocations.isEmpty()) {
        renderSupply = false;
      }
      time = 0;
      moveX = 0;
    }
  }

  public void render(int x, int y, Graphics g) {
    if (renderLocations.isEmpty()) return;

    int tileSize = map.getTileSize();
    Location location = renderLocations.get(0);
    int moveOffset = 50;
    int px = (location.getCol() * tileSize) + moveOffset;
    int py = (location.getRow() * tileSize);

    if (renderTrapped) {
      g.drawImage(trappedImg, (x + px) + moveX, y + py);
    } else if (renderSupply) {
      g.drawImage(suppliedImg, (x + px) + moveX, y + py);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() instanceof Unit) {
      Unit unit = (Unit) evt.getSource();
      if (propertyName.equals("supplies") || propertyName.equals("hp")) {
        if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
          Location suppliedLocation = unit.getLocation();
          if (!renderLocations.contains(suppliedLocation)) {
            renderSupply = true;
            renderLocations.add(suppliedLocation);
            time = 0;
            moveTime = 0;
          }
        }
      }
    }
  }

  public void trapperFound() {
    Location moverLocation = moveTraverse.getTrapper().getLocation();
    Location trappedLocation = map.getAdjacent(moverLocation, moveTraverse.getTrappedDirection());
    renderLocations.add(trappedLocation);
    renderTrapped = true;
    moveTraverse.reset();
    time = 0;
    moveTime = 0;
  }

  public void setMoveTraverse(MoveTraverse moveTraverse) {
    this.moveTraverse = moveTraverse;
  }
}
