package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Layout;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Graphics;

import java.awt.Dimension;

public class HorizontalUnitInfoPanel extends HorizontalInfoPanel {
  private ResourceManager resources;
  private Unit unit;
  private String unitName;
  private ImageStrip statusImageStrip;

  private ImageBox hpImgBox;
  private TextBox hpTxtBox;
  private ImageBox suppliesImgBox;
  private TextBox suppliesTxtBox;
  private ImageBox ammoImgBox;
  private TextBox ammoTxtBox;

  @Override
  public void loadResources(ResourceManager resources) {
    super.loadResources(resources);
    this.resources = resources;
    this.statusImageStrip = resources.getSlickImgStrip("panel_info");
    createBoxes();
    locateBoxes();
  }

  private void createBoxes() {
    hpImgBox = new ImageBox(statusImageStrip.getSubImage(1));
    hpTxtBox = new TextBox("00", font);
    suppliesImgBox = new ImageBox(statusImageStrip.getSubImage(0));
    suppliesTxtBox = new TextBox("00", font);
    ammoImgBox = new ImageBox(statusImageStrip.getSubImage(3));
    ammoTxtBox = new TextBox("00", font);
  }

  private void locateBoxes() {
    Layout.locateLeftToRight(new Box[]{hpImgBox, hpTxtBox, suppliesImgBox, suppliesTxtBox, ammoImgBox, ammoTxtBox}, 0, 0);
  }

  @Override
  protected void renderName(Graphics g) {
    font.drawString(0, 0, unitName);
  }

  @Override
  protected void renderInfo(Graphics g) {
    hpImgBox.render(g);
    hpTxtBox.render(g);
    suppliesImgBox.render(g);
    suppliesTxtBox.render(g);
    ammoImgBox.render(g);
    ammoTxtBox.render(g);
  }

  @Override
  public void setTile(Tile tile) {
    if (tile.getLocatableCount() > 0) {
      setUnit((Unit) tile.getLastLocatable());
    }
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
    this.unitName = unit != null ? App.translate(unit.getStats().getName()) : "Unnamed";
    initValues();
    super.initSize();
  }

  private void initValues() {
    setFrontImage(resources.getUnitImg(unit, Unit.DEFAULT_ORIENTATION));
    hpTxtBox.setText(unit.getHp() + "");
    suppliesTxtBox.setText(unit.getSupplies() + "");
    ammoTxtBox.setText(getWeaponAmmo() + "");
  }

  private int getWeaponAmmo() {
    int weaponAmmo = 0;
    if (unit.hasSecondaryWeapon()) {
      weaponAmmo = unit.getAvailableWeapon().getAmmo();
    } else if (unit.hasPrimaryWeapon()) {
      weaponAmmo = unit.getPrimaryWeapon().getAmmo();
    }
    return weaponAmmo;
  }

  protected Dimension getNameSize() {
    int width = font.getWidth(unitName);
    int height = font.getHeight("P");
    return new Dimension(width, height);
  }

  protected Dimension getInfoSize() {
    int width = ammoTxtBox.getMaxX();
    int height = ammoTxtBox.getMaxY();
    return new Dimension(width, height);
  }
}
