package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
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
  // Image Position in the decorationImgs List
  private static final int CAPTURE = 0;
  private static final int SUBMERGED = 1;
  private static final int LOAD = 2;
  private static final int LOW_AMMO = 3;
  private static final int LOW_SUPPLIES = 4;

  // Image Positions within a tile
  // Tile = 32
  // Unit Decoration = 16
  private static final int UNIT_DECOR_LOWER_LEFT_X = 1;
  private static final int UNIT_DECOR_LOWER_LEFT_Y = 24;
  private static final int UNIT_DECOR_LOWER_RIGHT_X = 16;
  private static final int UNIT_DECOR_LOWER_RIGHT_Y = 40;
  private static final int UNIT_DECOR_MIDDLE_RIGHT_X = 14;
  private static final int UNIT_DECOR_MIDDLE_RIGHT_Y = 14;

  private ImageStrip decorationImgs;

  private Animation animLeft;
  private Animation animRight;
  private Animation animUp;
  private Animation animDown;

  private Unit unit;
  private boolean lowHp, lowAmmo, lowSupplies;

  public UnitSprite(int x, int y, Location location, TileMap map, Unit unit) {
    super(x, y, location, map);
    this.unit = unit;
  }

  public void render(Graphics g) {
    super.render(g);

    translateOffset(g, false);
    if (lowHp) {
      g.drawString(unit.getHp() + "", locX + UNIT_DECOR_LOWER_RIGHT_X, locY + UNIT_DECOR_LOWER_RIGHT_Y);
    }

    if (lowAmmo) {
      g.drawImage(decorationImgs.getSubImage(LOW_AMMO), locX + UNIT_DECOR_MIDDLE_RIGHT_X, locY + UNIT_DECOR_MIDDLE_RIGHT_Y, null);
    }

    if (lowSupplies) {
      g.drawImage(decorationImgs.getSubImage(LOW_SUPPLIES), locX + UNIT_DECOR_MIDDLE_RIGHT_X, locY + UNIT_DECOR_MIDDLE_RIGHT_Y, null);
    }

    undoTranslateOffset(g);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (propertyName.equals("hp")) {
      lowHp = unit.hasLowHp();
    } else if (propertyName.equals("supply")) {
      lowSupplies = unit.hasLowSupplies();
    } else if (propertyName.equals("ammo")) {
      lowAmmo = unit.hasLowAmmo();
    }
  }
}
