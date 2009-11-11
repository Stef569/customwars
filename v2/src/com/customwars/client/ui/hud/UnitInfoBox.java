package com.customwars.client.ui.hud;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.NumberUtil;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.slick.BasicComponent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

public class UnitInfoBox extends BasicComponent {
  private static final Color backgroundColor = new Color(0, 0, 0, 0.40f);
  private static final Color textColor = Color.white;
  private static final int INFO_BOXES_LEFT_MARGIN = 3;
  private static final int UNIT_NAME_LEFT_MARGIN = 3;
  private static final int VERTICAL_SPACING = 3;

  private Unit unit;
  private String unitName;
  private ResourceManager resources;
  private ImageBox unitImgBox;
  private Row suppliesRow, ammoRow, hpRow;
  private Font font;

  public UnitInfoBox(GUIContext container) {
    super(container);
    unitImgBox = new ImageBox();
  }

  @Override
  public void loadResources(ResourceManager resources) {
    font = container.getDefaultFont();
    this.resources = resources;
    ImageStrip unitDecorations = resources.getSlickImgStrip("unitDecoration");
    Image ammoImage = unitDecorations.getSubImage(3);
    Image suppliesImage = unitDecorations.getSubImage(4);

    unitImgBox.setWidth(getWidth());
    suppliesRow = new Row(new ImageBox(suppliesImage), new TextBox("", font));
    suppliesRow.setHorizontalSpacing(VERTICAL_SPACING);

    ammoRow = new Row(new ImageBox(ammoImage), new TextBox("", font));
    ammoRow.setHorizontalSpacing(VERTICAL_SPACING);

    hpRow = new Row(new TextBox("hp:", font), new TextBox("", font));
    hpRow.setHorizontalSpacing(VERTICAL_SPACING);
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
    Image unitImg = resources.getUnitImg(unit, Direction.EAST);
    unitImgBox.setImage(unitImg);
    suppliesRow.setText(unit.getSupplies() + "");
    hpRow.setText(unit.getInternalHp() + "");

    Weapon weapon = unit.getAvailableWeapon();
    if (weapon != null) {
      ammoRow.setText(weapon.getAmmo() + "");
    } else {
      ammoRow.setText("0");
    }
  }

  private void locateBoxes(int x, int y) {
    int unitNameHeight = font.getHeight(unitName);
    int height = 0;

    unitImgBox.setLocation(x, y + unitNameHeight);
    height += unitImgBox.getHeight() + unitNameHeight;
    ammoRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
    height += ammoRow.getHeight();
    suppliesRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
    height += suppliesRow.getHeight();
    hpRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + height);
  }

  private void renderBoxes(Graphics g) {
    unitImgBox.render(g);
    renderUnitName(g);
    ammoRow.render(g);
    suppliesRow.render(g);
    hpRow.render(g);
  }

  private void renderUnitName(Graphics g) {
    g.setColor(textColor);
    g.drawString(unitName, getX() + UNIT_NAME_LEFT_MARGIN, getY());
  }

  public void setUnit(Unit unit) {
    this.unit = unit;

    if (unit != null) {
      this.unitName = unit.getName();
    } else {
      this.unitName = null;
    }
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
