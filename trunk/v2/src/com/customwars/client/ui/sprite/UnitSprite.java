package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A 2D unit
 *
 * @author stefan
 */
public class UnitSprite extends TileSprite implements PropertyChangeListener {
  // Image Position in the decorations ImageStrip
  private static final int CAPTURE = 0;
  private static final int SUBMERGED = 1;
  private static final int LOAD = 2;
  private static final int LOW_AMMO = 3;
  private static final int LOW_SUPPLIES = 4;

  // Image Positions within a tile
  // Unit Decoration = 16px width
  private static final int UNIT_DECOR_LOWER_LEFT_X = 1;
  private static final int UNIT_DECOR_LOWER_LEFT_Y = 24;
  private static final int UNIT_DECOR_LOWER_RIGHT_X = 16;
  private static final int UNIT_DECOR_LOWER_RIGHT_Y = 40;
  private static final int UNIT_DECOR_MIDDLE_RIGHT_X = 14;
  private static final int UNIT_DECOR_MIDDLE_RIGHT_Y = 14;

  private ImageStrip decorations;

  private Animation animLeft;
  private Animation animRight;
  private Animation animUp;
  private Animation animDown;
  private Animation animInActive;
  private Animation animDying;

  private Unit unit;
  private boolean lowHp, lowAmmo, lowSupplies;

  public UnitSprite(Tile tile, TileMap map, Unit unit, ImageStrip decorations) {
    super(tile, map);
    this.unit = unit;
    this.decorations = decorations;
    unit.addPropertyChangeListener(this);

    assert tile == unit.getLocation() : "Unitsprite should have same location as the unit";
  }

  // ----------------------------------------------------------------------------
  // Setters
  // ----------------------------------------------------------------------------
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

  public void setLocation(Location newLocation) {
    super.setLocation(newLocation);
    Tile oldTile = (Tile) getLocation();
    Tile newTile = (Tile) newLocation;
    if (oldTile != null) oldTile.removePropertyChangeListener(this);
    if (newTile != null) newTile.addPropertyChangeListener(this);
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
        setAnim(animDying);
        break;
    }
  }

  public void render(int x, int y, Graphics g) {
    super.render(x, y, g);

    translateOffset(g, false);
    if (lowHp) {
      g.drawString(unit.getHp() + "", x + locX + UNIT_DECOR_LOWER_RIGHT_X, y + locY + UNIT_DECOR_LOWER_RIGHT_Y);
    }

    if (lowAmmo) {
      g.drawImage(decorations.getSubImage(LOW_AMMO), x + locX + UNIT_DECOR_MIDDLE_RIGHT_X, y + locY + UNIT_DECOR_MIDDLE_RIGHT_Y, null);
    }

    if (lowSupplies) {
      g.drawImage(decorations.getSubImage(LOW_SUPPLIES), x + locX + UNIT_DECOR_MIDDLE_RIGHT_X, y + locY + UNIT_DECOR_MIDDLE_RIGHT_Y, null);
    }

    undoTranslateOffset(g);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() == getLocation()) {
      if (propertyName.equals("fog")) {
        setVisible(!(Boolean) evt.getNewValue());
      }
    }

    if (evt.getSource() == unit) {
      if (propertyName.equals("hp")) {
        lowHp = unit.hasLowHp();
      } else if (propertyName.equals("supply")) {
        lowSupplies = unit.hasLowSupplies();
      } else if (propertyName.equals("ammo")) {
        lowAmmo = unit.hasLowAmmo();
      } else if (propertyName.equals("state")) {
        changeState((GameObjectState) evt.getNewValue());
      } else if (propertyName.equals("orientation")) {
        setOrientation((Direction) evt.getNewValue());
      } else if (propertyName.equals("location")) {
        setLocation((Location) evt.getNewValue());
      }
    }
  }

  /**
   * Changes the current sprite animation by updating the sprite to the unit GameObjectState
   * this results in a new animation to be set over the current one.
   */
  public void updateAnim() {
    changeState(unit.getState());
  }
}
