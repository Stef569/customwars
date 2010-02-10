package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.slick.ImageRotator;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A 2D unit
 *
 * @author stefan
 */
public class UnitSprite extends TileSprite implements PropertyChangeListener {
  private static final int UNIT_STATUS_FRAME_DURATION = 2000;

  // Image Position in the status ImageStrip
  private static final int CAPTURED = 0;
  private static final int SUBMERGED = 1;
  private static final int LOAD = 2;
  private static final int LOW_AMMO = 3;
  private static final int LOW_SUPPLIES = 4;
  private static final int NONE = 5;
  private static final int RANK_I = 6;
  private static final int RANK_II = 7;
  private static final int RANK_V = 8;
  private static final int CO = 9;

  private final ImageRotator statusRotator;
  private Font hpFont;

  private Animation animLeft;
  private Animation animRight;
  private Animation animUp;
  private Animation animDown;
  private Animation animInActive;
  private Animation animDying;

  private final Unit unit;
  private boolean lowHp;
  private boolean remove;

  public UnitSprite(TileMap<Tile> map, Unit unit, ImageStrip statusImgStrip) {
    super(unit.getLocation(), map);
    this.unit = unit;
    statusRotator = new ImageRotator(statusImgStrip.toArray(), UNIT_STATUS_FRAME_DURATION);
    statusRotator.hideAllFrames();
    lowHp = unit.hasLowHp();
    statusRotator.setShowFrame(LOW_AMMO, unit.hasLowAmmo());
    statusRotator.setShowFrame(LOW_SUPPLIES, unit.hasLowSupplies());
    statusRotator.setShowFrame(LOAD, unit.getLocatableCount() > 0);
    experienceChange(unit.getExperience());

    // Hide the unit when it is on a fogged location
    Tile unitLocation = (Tile) unit.getLocation();
    setVisible(!unitLocation.isFogged());
    addUnitListeners();
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

  public void setHpFont(Font hpFont) {
    this.hpFont = hpFont;
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

  @Override
  public void update(long elapsedTime) {
    super.update(elapsedTime);
    statusRotator.update(elapsedTime);

    if (anim.isStopped() && unit.isDestroyed()) {
      setVisible(false);
      remove = true;
    }
  }

  @Override
  public void render(Graphics g) {
    super.render(g);
    if (isVisible() && !unit.isDestroyed()) {
      translateOffset(g, false);
      renderUnitStatus();
      undoTranslateOffset(g);
    }
  }

  private void renderUnitStatus() {
    if (lowHp) {
      renderLowerRight(unit.getHp() + "");
    }

    int yOffset = getHeight() - statusRotator.getHeight();
    statusRotator.draw(getX(), getY() + yOffset);
  }

  private void renderLowerRight(String txt) {
    int xOffset = getWidth() - hpFont.getWidth(txt);
    int yOffset = getHeight() - hpFont.getLineHeight();
    hpFont.drawString(getX() + xOffset, getY() + yOffset, txt);
  }

  public boolean canBeRemoved() {
    return remove;
  }

  public boolean isDying() {
    return unit.isDestroyed();
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
        statusRotator.setShowFrame(LOW_SUPPLIES, unit.hasLowSupplies());
      } else if (propertyName.equals("state")) {
        changeState((GameObjectState) evt.getNewValue());
      } else if (propertyName.equals("orientation")) {
        setOrientation((Direction) evt.getNewValue());
      } else if (propertyName.equals("location")) {
        setLocation((Location) evt.getNewValue());
      } else if (propertyName.equals("transport")) {
        statusRotator.setShowFrame(LOAD, unit.getLocatableCount() > 0);
      } else if (propertyName.equals("unitState")) {
        unitStateChange((UnitState) evt.getNewValue());
      } else if (propertyName.equals("experience")) {
        experienceChange((Integer) evt.getNewValue());
      }
    } else if (evt.getSource() == unit.getPrimaryWeapon()) {
      if (propertyName.equals("ammo")) {
        statusRotator.setShowFrame(LOW_AMMO, unit.hasLowAmmo());
      }
    }
  }

  private void experienceChange(int newExperience) {
    switch (newExperience) {
      case 0:
        break;
      case 1:
        showRank(RANK_I);
        break;
      case 2:
        showRank(RANK_II);
        break;
      case 3:
        showRank(RANK_V);
        break;
    }
  }

  private void showRank(int rank) {
    statusRotator.showFrame(rank);
    hideOtherRanks(rank);
  }

  private void hideOtherRanks(int rank) {
    if (rank != RANK_I) statusRotator.hideFrame(RANK_I);
    if (rank != RANK_II) statusRotator.hideFrame(RANK_II);
    if (rank != RANK_V) statusRotator.hideFrame(RANK_V);
  }

  private void unitStateChange(UnitState newUnitState) {
    switch (newUnitState) {
      case CAPTURING:
        statusRotator.showFrame(CAPTURED);
        break;
      case SUBMERGED:
        statusRotator.showFrame(SUBMERGED);
        break;
      case IDLE:
        statusRotator.hideFrame(CAPTURED);
        statusRotator.hideFrame(SUBMERGED);
        break;
      default:
        throw new AssertionError(newUnitState + " not handled in switch case");
    }
  }
}