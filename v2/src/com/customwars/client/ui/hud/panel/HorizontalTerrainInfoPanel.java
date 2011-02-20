package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Component;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Layout;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.sprite.SpriteManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Dimension;

public class HorizontalTerrainInfoPanel extends HorizontalInfoPanel {
  private ResourceManager resources;
  private ImageStrip statusImageStrip;

  private final Component[] starBoxes = new Box[4];
  private ImageBox captureImgBox;
  private TextBox textBox;    // Displays the capture points or the hp of a city
  private String terrainName;
  private Terrain terrain;
  private final SpriteManager spriteManager;

  public HorizontalTerrainInfoPanel(GUIContext container, SpriteManager spriteManager) {
    this.spriteManager = spriteManager;
  }

  @Override
  public void loadResources(ResourceManager resources) {
    super.loadResources(resources);
    this.resources = resources;
    this.statusImageStrip = resources.getSlickImgStrip("panel_info");
    createBoxes();
    locateBoxes();
  }

  private void createBoxes() {
    setStarBoxes(starBoxes.length - 1);
    captureImgBox = new ImageBox(statusImageStrip.getSubImage(2));
    textBox = new TextBox("00", font);
  }

  private void setStarBoxes(int activeStarsCount) {
    for (int i = 0; i < starBoxes.length; i++) {
      Image starImg = i < activeStarsCount ? statusImageStrip.getSubImage(4) : statusImageStrip.getSubImage(5);
      starBoxes[i] = new ImageBox(starImg);
    }
  }

  private void locateBoxes() {
    Layout.locateLeftToRight(starBoxes, 0, 0);
    Component lastStar = starBoxes[starBoxes.length - 1];
    captureImgBox.setLocation(lastStar.getMaxX(), 0);
    textBox.setLocation(captureImgBox.getMaxX(), 0);
  }

  @Override
  protected void renderName(Graphics g) {
    font.drawString(0, 0, terrainName);
  }

  @Override
  protected void renderInfo(Graphics g) {
    for (Component box : starBoxes) {
      box.render(g);
    }
    captureImgBox.render(g);
    textBox.render(g);
  }

  @Override
  public void setTile(Tile tile) {
    setTerrain(tile.getTerrain());
  }

  public void setTerrain(Terrain terrain) {
    this.terrain = terrain;
    this.terrainName = App.translate(terrain.getName());
    initValues();
    super.initSize();
  }

  private void initValues() {
    setFrontImage(getImg(terrain));
    setStarBoxes(terrain.getDefenseBonus());
    locateBoxes();

    if (terrain instanceof City) {
      City city = (City) terrain;
      textBox.setVisible(true);

      Tile t = (Tile) city.getLocation();
      if (t.isFogged()) {
        textBox.setText(" ");
      } else {
        if (city.canBeDestroyed()) {
          textBox.setText(city.getHp() + "");
        } else {
          captureImgBox.setVisible(true);
          textBox.setText(city.getRemainingCapturePoints() + "");
        }
      }
    } else {
      captureImgBox.setVisible(false);
      textBox.setVisible(false);
    }
  }

  private Image getImg(Terrain terrain) {
    if (terrain instanceof City) {
      City city = (City) terrain;
      return spriteManager.getCitySprite(city).getAnim().getImage(0);
    } else {
      return resources.getSlickImgStrip("terrains").getSubImage(terrain.getID());
    }
  }

  @Override
  protected Dimension getNameSize() {
    int width = font.getWidth(terrainName);
    int height = font.getHeight("P");
    return new Dimension(width + 5, height + 5);
  }

  @Override
  protected Dimension getInfoSize() {
    int width = textBox.getMaxX();
    int height = textBox.getMaxY();
    return new Dimension(width, height);
  }
}
