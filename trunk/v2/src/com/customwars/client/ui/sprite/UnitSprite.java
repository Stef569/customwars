package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A 2D unit
 *
 * @author stefan
 */
public class UnitSprite extends TileSprite implements PropertyChangeListener {
  // Image Position in the decorations ImageStrip
  private static final int CAPTURED = 0;
  private static final int SUBMERGED = 1;
  private static final int LOAD = 2;
  private static final int LOW_AMMO = 3;
  private static final int LOW_SUPPLIES = 4;

  private ImageStrip decorations;
  private Font font;

  private Animation animLeft;
  private Animation animRight;
  private Animation animUp;
  private Animation animDown;
  private Animation animInActive;
  private Animation animDying;

  private Unit unit;
  private boolean lowHp, lowAmmo, lowSupplies;
  private boolean remove = false;

  public UnitSprite(Tile tile, TileMap map, Unit unit, ImageStrip decorations) {
    super(tile, map);
    this.unit = unit;
    this.decorations = decorations;
    lowHp = unit.hasLowHp();
    lowAmmo = unit.hasLowAmmo();
    lowSupplies = unit.hasLowSupplies();
    addUnitListeners();
    assert tile == unit.getLocation() : "Unitsprite should have same location as the unit";
  }

  private void addUnitListeners() {
    unit.addPropertyChangeListener(this);
    if (unit.hasPrimaryWeapon()) unit.getPrimaryWeapon().addPropertyChangeListener(this);
  }

  private void setOrientation(Direction dir) {
    switch (dir) {
      case EAST:
        setAnim(animRight);
        break;
      case NORTH:
        setAnim(animUp);
        break;
      case SOUTH:
        setAnim(animDown);
        break;
      case WEST:
        setAnim(animLeft);
        break;
    }
  }

  public void setAnimLeft(Animation animLeft) {
    this.animLeft = animLeft;
  }

  public void setAnimRight(Animation animRight) {
    this.animRight = animRight;
  }

  public void setAnimUp(Animation animUp) {
    this.animUp = animUp;
  }

  public void setAnimDown(Animation animDown) {
    this.animDown = animDown;
  }

  public void setAnimInActive(Animation animInActive) {
    this.animInActive = animInActive;
  }

  public void setAnimDying(Animation animDying) {
    this.animDying = animDying;
  }

  /**
   * Changes the current sprite animation by updating the sprite to the unit GameObjectState
   * this results in a new animation to be set over the current one.
   */
  public void updateAnim() {
    changeState(unit.getState());
  }

  public void setLocation(Location newLocation) {
    Tile oldTile = (Tile) getLocation();
    if (oldTile != null) oldTile.removePropertyChangeListener(this);

    if (newLocation instanceof Tile) {
      super.setLocation(newLocation);
      Tile newTile = (Tile) newLocation;
      newTile.addPropertyChangeListener(this);
    }
  }

  private void changeState(GameObjectState gameObjectState) {
    switch (gameObjectState) {
      case IDLE:
        setAnim(animInActive);
        break;
      case ACTIVE:
        setAnim(animRight);
        break;
      case DESTROYED:
        if (animDying.isStopped()) {
          animDying.restart();
        }
        setAnim(animDying);
        break;
    }
  }

  public void update(long elapsedTime) {
    super.update(elapsedTime);

    if (anim.isStopped() && unit.isDestroyed()) {
      setVisible(false);
      remove = true;
    }
  }

  public void render(int x, int y, Graphics g) {
    super.render(x, y, g);
    if (isVisible() && !unit.isDestroyed()) {
      translate(x, y, g);

      if (lowHp) {
        renderLowerRight(unit.getHp() + "");
      } else if (lowAmmo) {
        renderLowerRight(decorations.getSubImage(LOW_AMMO), g);
      }

      if (lowSupplies) {
        renderLowerLeft(decorations.getSubImage(LOW_SUPPLIES), g);
      }

      renderUnitState(unit.getUnitState(), g);
      undoTranslate(x, y, g);
    }
  }

  private void translate(int x, int y, Graphics g) {
    g.translate(x, y);
    translateOffset(g, false);
  }


  private void renderUnitState(UnitState unitState, Graphics g) {
    if (unit.getLocatableCount() > 0) {
      renderLowerLeft(decorations.getSubImage(LOAD), g);
    }

    switch (unitState) {
      case CAPTURING:
        renderLowerLeft(decorations.getSubImage(CAPTURED), g);
        break;
      case SUBMERGED:
        renderLowerLeft(decorations.getSubImage(SUBMERGED), g);
        break;
    }
  }

  private void undoTranslate(int x, int y, Graphics g) {
    undoTranslateOffset(g);
    g.translate(-x, -y);
  }

  private void renderLowerLeft(String txt) {
    int x = 2;
    int y = getHeight() - font.getLineHeight();
    font.drawString(locX + x, locY + y, txt);
  }

  private void renderLowerLeft(Image img, Graphics g) {
    int x = 2;
    int y = getHeight() - img.getHeight();
    g.drawImage(img, locX + x, locY + y);
  }

  private void renderLowerRight(String txt) {
    int x = getWidth() - font.getWidth(txt);
    int y = getHeight() - font.getLineHeight();
    font.drawString(locX + x, locY + y, txt);
  }

  private void renderLowerRight(Image img, Graphics g) {
    int x = getWidth() - img.getWidth();
    int y = getHeight() - img.getHeight();
    g.drawImage(img, locX + x, locY + y);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() == getLocation()) {
      // stop rendering this sprite when the tile is fogged, unless the unit is dying
      if (propertyName.equals("fog") && !unit.isDestroyed()) {
        setVisible(!(Boolean) evt.getNewValue());
      }
    } else if (evt.getSource() == unit) {
      if (propertyName.equals("hp")) {
        lowHp = unit.hasLowHp();
      } else if (propertyName.equals("supplies")) {
        lowSupplies = unit.hasLowSupplies();
      } else if (propertyName.equals("state")) {
        changeState((GameObjectState) evt.getNewValue());
      } else if (propertyName.equals("orientation")) {
        setOrientation((Direction) evt.getNewValue());
      } else if (propertyName.equals("location")) {
        setLocation((Location) evt.getNewValue());
      }
    } else if (evt.getSource() == unit.getPrimaryWeapon()) {
      if (propertyName.equals("ammo")) {
        lowAmmo = unit.hasLowAmmo();
      }
    }
  }

  public boolean canBeRemoved() {
    return remove;
  }

  public void setFont(Font font) {
    this.font = font;
  }
}
