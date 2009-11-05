package com.customwars.client.ui.hud;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.slick.BasicComponent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

public class TransportInfoBox extends BasicComponent {
  private static final Color backgroundColor = new Color(0, 0, 0, 0.40f);
  private static final Color textColor = Color.white;
  private Unit unit;
  private ImageBox unitInTransportBox1, unitInTransportBox2;
  private ResourceManager resources;

  public TransportInfoBox(GUIContext container) {
    super(container);
    unitInTransportBox1 = new ImageBox();
    unitInTransportBox2 = new ImageBox();
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (unit != null && !unit.isDestroyed() && unit.getLocatableCount() > 0) {
      Color origColor = g.getColor();
      int x = getX();
      int y = getY();

      renderBackground(g, x, y);
      initBoxes();
      locateBoxes(x, y);
      renderBoxes(g);
      g.setColor(origColor);
    }
  }

  private void renderBackground(Graphics g, int x, int y) {
    g.setColor(backgroundColor);
    g.fillRect(x, y, getWidth(), getHeight());
  }

  private void initBoxes() {
    if (unit.getLocatableCount() == 1) {
      prepareComponentFor1Unit();
    } else if (unit.getLocatableCount() == 2) {
      prepareComponentFor2Units();
    }
  }

  private void prepareComponentFor1Unit() {
    Unit unitInTransport1 = (Unit) unit.getLocatable(0);
    setUnitImageInBox(unitInTransportBox1, unitInTransport1);
    unitInTransportBox2.setImage(null);
  }

  private void prepareComponentFor2Units() {
    Unit unitInTransport1 = (Unit) unit.getLocatable(0);
    setUnitImageInBox(unitInTransportBox1, unitInTransport1);

    Unit unitInTransport2 = (Unit) unit.getLocatable(1);
    setUnitImageInBox(unitInTransportBox2, unitInTransport2);
  }

  private void setUnitImageInBox(ImageBox imageBox, Unit unit) {
    Image unitImg = getEastFacingUnitImg(unit);
    imageBox.setImage(unitImg);
  }

  private Image getEastFacingUnitImg(Unit unit) {
    java.awt.Color playerColor = unit.getOwner().getColor();
    return resources.getUnitImg(unit, playerColor, Direction.EAST);
  }

  private void locateBoxes(int x, int y) {
    int height = 0;
    unitInTransportBox1.setLocation(x, y);
    height += unitInTransportBox1.getHeight();
    unitInTransportBox2.setLocation(x, y + height);
  }

  private void renderBoxes(Graphics g) {
    unitInTransportBox1.render(g);
    unitInTransportBox2.render(g);
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }
}