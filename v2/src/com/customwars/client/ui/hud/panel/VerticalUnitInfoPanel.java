package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Row;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class VerticalUnitInfoPanel extends Box implements InfoPanel {
  private static final int INFO_BOXES_LEFT_MARGIN = 3;
  private static final int UNIT_NAME_LEFT_MARGIN = 3;
  private static final int VERTICAL_SPACING = 3;

  private ResourceManager resources;
  private final ImageBox unitImgBox;
  private Row suppliesRow, ammoRow, hpRow;
  private Font font;
  private Unit unit;
  private String unitName;

  public VerticalUnitInfoPanel() {
    unitImgBox = new ImageBox();
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    font = resources.getFont("in_game");
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
  public void renderImpl(Graphics g) {
    if (unit != null && !unit.isDestroyed()) {
      initBoxes();
      locateBoxes();
      renderBoxes(g);
    }
  }

  private void initBoxes() {
    Image unitImg = resources.getUnitImg(unit, Direction.EAST);
    unitImgBox.setImage(unitImg);
    unitImgBox.setWidth(getWidth());
    suppliesRow.setText(unit.getSupplies() + "");
    hpRow.setText(unit.getInternalHp() + "");
    ammoRow.setText(unit.getAmmo() + "");
  }

  private void locateBoxes() {
    int x = getX();
    int y = getY();
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
    g.drawString(unitName, getX() + UNIT_NAME_LEFT_MARGIN, getY());
  }

  public void setTile(Tile tile) {
    if (tile.getLocatableCount() > 0) {
      setUnit((Unit) tile.getLastLocatable());
    }
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
    this.unitName = unit != null ? App.translate(unit.getStats().getName()) : null;
  }
}
