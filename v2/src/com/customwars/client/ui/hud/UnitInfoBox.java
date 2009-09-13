package com.customwars.client.ui.hud;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.slick.BasicComponent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;
import tools.NumberUtil;

public class UnitInfoBox extends BasicComponent {
  private static final Color backgroundColor = new Color(0, 0, 0, 0.40f);
  private static final Color textColor = Color.white;
  private static final int INFO_BOXES_LEFT_MARGIN = 3;
  private static final int UNIT_NAME_LEFT_MARGIN = 3;

  private Unit unit;
  private ResourceManager resources;
  private ImageBox unitBox;
  private Row suppliesRow, ammoRow, hpRow;

  public UnitInfoBox(GUIContext container) {
    super(container);
    unitBox = new ImageBox();
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    ImageStrip unitDecorations = resources.getSlickImgStrip("unitDecoration");
    Image ammoImage = unitDecorations.getSubImage(3);
    Image suppliesImage = unitDecorations.getSubImage(4);
    Font defaultFont = container.getDefaultFont();

    unitBox.setWidth(getWidth());
    suppliesRow = new Row(new ImageBox(suppliesImage), new TextBox("", defaultFont));
    suppliesRow.setHorizontalSpacing(5);

    ammoRow = new Row(new ImageBox(ammoImage), new TextBox("", defaultFont));
    ammoRow.setHorizontalSpacing(5);

    hpRow = new Row(new TextBox("hp:", defaultFont), new TextBox("", defaultFont));
    hpRow.setHorizontalSpacing(5);
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (unit != null && !unit.isDestroyed()) {
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
    unitBox.setImage(getEastFacingUnitImg(unit));
    suppliesRow.setText(unit.getSupplies() + "");
    hpRow.setText(unit.getInternalHp() + "");

    Weapon weapon = unit.getAvailableWeapon();
    if (weapon != null) {
      ammoRow.setText(weapon.getAmmo() + "");
    } else {
      ammoRow.setText("0");
    }
  }

  private Image getEastFacingUnitImg(Unit unit) {
    java.awt.Color playerColor = unit.getOwner().getColor();
    return resources.getUnitSpriteSheet(playerColor).getSubImage(5, unit.getID());
  }

  private void locateBoxes(int x, int y) {
    int height = 0;
    unitBox.setLocation(x, y);
    height += unitBox.getHeight();
    ammoRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
    height += ammoRow.getHeight();
    suppliesRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
    height += suppliesRow.getHeight();
    hpRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
  }

  private void renderBoxes(Graphics g) {
    unitBox.render(g);
    renderUnitName(g);
    ammoRow.render(g);
    suppliesRow.render(g);
    hpRow.render(g);
  }

  private void renderUnitName(Graphics g) {
    g.setColor(textColor);
    g.drawString(unit.getName(), getX() + UNIT_NAME_LEFT_MARGIN, getY());
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  /**
   * A Row contains 2 boxes
   * a generic box and a text box with horizontalSpacing between them.
   * The row always has the height of the tallest box
   */
  private class Row extends Box {
    private Box imageBox;
    private TextBox textBox;
    private int horizontalSpacing;

    private Row(Box imageBox, TextBox textBox) {
      this.imageBox = imageBox;
      this.textBox = textBox;
    }

    @Override
    protected void init() {
      int height = NumberUtil.findHighest(imageBox.getHeight(), textBox.getHeight());
      setHeight(height);
    }

    @Override
    public void renderImpl(Graphics g) {
      imageBox.render(g);
      textBox.render(g);
    }

    @Override
    public void setLocation(int x, int y) {
      super.setLocation(x, y);
      imageBox.setLocation(x, y);
      textBox.setLocation(x + imageBox.getWidth() + horizontalSpacing, y);
    }

    @Override
    public void setHeight(int height) {
      super.setHeight(height);
      imageBox.setHeight(height);
      textBox.setHeight(height);
    }

    public void setText(String text) {
      textBox.setText(text);
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
      this.horizontalSpacing = horizontalSpacing;
    }
  }
}