package com.customwars.client.ui.hud.panel;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Component;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Layout;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.List;

public class HorizontalTransportInfoPanel extends Box implements InfoPanel {
  private static final Color backgroundColor = new Color(0, 0, 0, 0.40f);
  private final List<Component> transportBoxes = new ArrayList<Component>();
  private ResourceManager resources;
  private Unit unit;

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void renderImpl(Graphics g) {
    Color origColor = g.getColor();
    g.translate(getX(), getY());
    renderBackground(g);
    renderBoxes(g);
    g.translate(-getX(), -getY());
    g.setColor(origColor);
  }

  private void renderBackground(Graphics g) {
    g.setColor(backgroundColor);
    g.fillRect(0, 0, getWidth(), getHeight());
  }

  private void renderBoxes(Graphics g) {
    for (Component transportBox : transportBoxes) {
      transportBox.render(g);
    }
  }

  public void setTile(Tile tile) {
    if (tile.getLocatableCount() > 0) {
      setUnit((Unit) tile.getLastLocatable());
    }
  }

  public void setUnit(Unit unit) {
    if (unit != null && !unit.isDestroyed() && unit.getLocatableCount() > 0) {
      this.unit = unit;
      initBoxes();
    } else {
      this.unit = null;
      transportBoxes.clear();
    }
  }

  private void initBoxes() {
    transportBoxes.clear();
    for (int i = 0; i < unit.getLocatableCount(); i++) {
      Unit unitInTransport = (Unit) unit.getLocatable(i);
      Image unitImg = getEastFacingUnitImg(unitInTransport);
      transportBoxes.add(new ImageBox(unitImg));
    }
    Layout.locateLeftToRight(transportBoxes, 0, 0);
  }

  private Image getEastFacingUnitImg(Unit unit) {
    java.awt.Color playerColor = unit.getOwner().getColor();
    return resources.getUnitImg(unit, playerColor, Direction.EAST);
  }
}
