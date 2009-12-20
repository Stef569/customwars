package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Row;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.sprite.SpriteManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class VerticalTerrainInfoPanel extends Box implements InfoPanel {
  private static final int LEFT_MARGIN = 3;
  private static final int TERRAIN_NAME_LEFT_MARGIN = 6;

  private ImageStrip terrainImgs;
  private final SpriteManager spriteManager;

  private ImageBox terrainBox;
  private Row defenseRow, captureRow;
  private Tile tile;

  public VerticalTerrainInfoPanel(SpriteManager spriteManager) {
    this.spriteManager = spriteManager;
  }

  public void loadResources(ResourceManager resources) {
    ImageStrip cityDecorations = resources.getSlickImgStrip("cityDecorations");
    Image captureImg = cityDecorations.getSubImage(0);
    Image defenseImg = cityDecorations.getSubImage(1);

    terrainImgs = resources.getSlickImgStrip("terrains");
    terrainBox = new ImageBox();

    Font defaultFont = resources.getFont("in_game");
    defenseRow = new Row(new ImageBox(defenseImg), new TextBox("", defaultFont));
    defenseRow.setHorizontalSpacing(5);
    captureRow = new Row(new ImageBox(captureImg), new TextBox("", defaultFont));
    captureRow.setHorizontalSpacing(5);
  }

  public void renderImpl(Graphics g) {
    if (tile != null) {
      Color origColor = g.getColor();
      initBoxes(tile.getTerrain());
      locateBoxes();
      renderBoxes(g);
      g.setColor(origColor);
    }
  }

  private void initBoxes(Terrain terrain) {
    terrainBox.setImage(getTerrainImg(terrain));
    terrainBox.setWidth(getWidth());

    defenseRow.setText(terrain.getDefenseBonus() + "");

    if (terrain instanceof City) {
      City city = (City) terrain;
      captureRow.setVisible(true);
      captureRow.setText(city.getCapCountPercentage() + "%");
    } else {
      captureRow.setVisible(false);
    }
  }

  private Image getTerrainImg(Terrain terrain) {
    if (terrain instanceof City) {
      City city = (City) terrain;
      return spriteManager.getCitySprite(city).getAnim().getImage(0);
    } else {
      return terrainImgs.getSubImage(terrain.getID());
    }
  }

  private void locateBoxes() {
    int x = getX();
    int y = getY();
    terrainBox.setLocation(x, y);
    defenseRow.setLocation(x + LEFT_MARGIN, y + terrainBox.getHeight());
    captureRow.setLocation(x + LEFT_MARGIN, y + terrainBox.getHeight() + defenseRow.getHeight());
  }

  private void renderBoxes(Graphics g) {
    terrainBox.render(g);
    renderTerrainName(g);
    captureRow.render(g);
    defenseRow.render(g);
  }

  private void renderTerrainName(Graphics g) {
    g.setColor(Color.white);
    Terrain terrain = tile.getTerrain();
    g.drawString(App.translate(terrain.getName()), getX() + TERRAIN_NAME_LEFT_MARGIN, getY());
  }

  public void setTile(Tile tile) {
    this.tile = tile;
  }
}
