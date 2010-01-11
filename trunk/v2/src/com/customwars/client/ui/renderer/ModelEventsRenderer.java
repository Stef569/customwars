package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
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
public class ModelEventsRenderer implements PropertyChangeListener, Renderable {
  private static final int TRAPPED_DELAY = 350, SUPPLY_DELAY = 350, MOVING_DELAY = 30;

  // Model
  private Game game;
  private Map<Tile> map;
  private MoveTraverse moveTraverse;
  private final List<Location> renderLocations;

  // GUI
  private final Camera2D camera;
  private Image trappedImg, suppliedImg;
  private boolean renderTrapped, renderSupply;
  private int time, moveTime, moveX;

  public ModelEventsRenderer(MoveTraverse moveTraverse, Game game, Camera2D camera) {
    this.camera = camera;
    renderLocations = new LinkedList<Location>();
    setMoveTraverse(moveTraverse);
    setGame(game);
  }

  private void setMoveTraverse(MoveTraverse moveTraverse) {
    this.moveTraverse = moveTraverse;
    moveTraverse.addPropertyChangeListener(this);
  }

  private void setGame(Game game) {
    if (this.game != null) {
      removeModelEventListeners(this.game);
    }
    this.game = game;
    this.map = game.getMap();
    addModelEventListeners(game);
  }

  private void addModelEventListeners(Game game) {
    game.addPropertyChangeListener(this);
    game.addPropertyChangeListenerToEachPlayer(this);
    game.getMap().addListenerToAllTilesUnitsAndCities(this);
  }

  public void removeAllListeners() {
    moveTraverse.removePropertyChangeListener(this);
    removeModelEventListeners(game);
  }

  private void removeModelEventListeners(Game game) {
    game.removePropertyChangeListener(this);
    game.getMap().removeListenerFromAllTilesUnitsAndCities(this);
  }

  public void loadResources(ResourceManager resources) {
    trappedImg = resources.getSlickImg("TRAPPED");
    suppliedImg = resources.getSlickImg("SUPPLIED");
  }

  public void update(int elapsedTime) {
    time += elapsedTime;

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
      } else {
        renderSupply = false;
      }
      time = 0;
      moveX = 0;
    }
  }

  public void render(Graphics g) {
    if (renderLocations.isEmpty()) return;

    int tileSize = map.getTileSize();
    Location location = renderLocations.get(0);
    int px = (location.getCol() * tileSize) + tileSize;
    int py = (location.getRow() * tileSize);

    if (renderTrapped) {
      g.drawImage(trappedImg, (px) + moveX, py);
    } else if (renderSupply) {
      g.drawImage(suppliedImg, (px) + moveX, py);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() instanceof Unit) {
      Unit unit = (Unit) evt.getSource();
      if (propertyName.equals("supplies")) {
        if ((Integer) evt.getNewValue() > (Integer) evt.getOldValue()) {
          unitSuppliesChange(unit);
        }
      } else if (propertyName.equals("state")) {
        unitStateChanged(evt);
      }
    } else if (evt.getSource() instanceof MoveTraverse) {
      if (propertyName.equals("trapped")) {
        if ((Boolean) evt.getNewValue()) {
          trapperFound();
        }
      }
    } else if (evt.getSource() instanceof Player) {
      if (propertyName.equals("unit")) {
        unitInPlayerChange(evt);
      }
    }
  }

  private void unitSuppliesChange(Unit unit) {
    Location suppliedLocation = unit.getLocation();
    if (!renderLocations.contains(suppliedLocation)) {
      renderSupply = true;
      renderLocations.add(suppliedLocation);
      time = 0;
      moveTime = 0;
    }
  }

  private void unitStateChanged(PropertyChangeEvent evt) {
    if (evt.getNewValue().equals(GameObjectState.DESTROYED)) {
      camera.shake();
    }
  }

  private void trapperFound() {
    renderLocations.add(moveTraverse.getTrapperLocation());
    renderTrapped = true;
    time = 0;
    moveTime = 0;
  }

  private void unitInPlayerChange(PropertyChangeEvent evt) {
    if (evt.getNewValue() != null) {
      // If a new unit is added to a player in the game make sure to add a listener
      Unit unit = (Unit) evt.getNewValue();
      unit.addPropertyChangeListener(this);
    }
  }
}