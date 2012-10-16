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

public class VerticalTransportInfoPanel extends Box implements InfoPanel {
  private final List<Component> transportBoxes = new ArrayList<Component>();
  private ResourceManager resources;
  private Unit unit;

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void renderImpl(Graphics g) {
    Color origColor = g.getColor();
    g.translate(getX(), getY());
    renderBoxes(g);
    g.translate(-getX(), -getY());
    g.setColor(origColor);
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
    if (unit != null && !unit.isDestroyed() && unit.hasUnitsInTransport()) {
      this.unit = unit;
      initBoxes();
    } else {
      this.unit = null;
      transportBoxes.clear();
    }
  }

  private void initBoxes() {
    transportBoxes.clear();
    for (int i = 0; i < unit.getUnitsInTransportCount(); i++) {
      Unit unitInTransport = unit.getUnitInTransport(i);
      Image unitImg = getEastFacingUnitImg(unitInTransport);
      transportBoxes.add(i, new ImageBox(unitImg));
    }
    Layout.locateTopToBottom(transportBoxes, 0, 0);
  }

  private Image getEastFacingUnitImg(Unit unit) {
    java.awt.Color playerColor = unit.getOwner().getColor();
    return resources.getUnitImg(unit, playerColor, Direction.EAST);
  }
}
