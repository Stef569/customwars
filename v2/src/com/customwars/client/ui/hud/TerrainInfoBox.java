package com.customwars.client.ui.hud;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.SpriteManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;
import tools.NumberUtil;

public class TerrainInfoBox extends BasicComponent {
  private static final Color backgroundColor = new Color(0, 0, 0, 0.40f);
  private static final Color textColor = Color.white;
  private static final int INFO_BOXES_LEFT_MARGIN = 3;
  private static final int TERRAIN_NAME_LEFT_MARGIN = 6;

  private ImageStrip terrainImgs;
  private SpriteManager spriteManager;

  private ImageBox terrainBox;
  private Row defenseRow, captureRow;
  private Tile tile;

  public TerrainInfoBox(GUIContext container, SpriteManager spriteManager) {
    super(container);
    this.spriteManager = spriteManager;
  }

  public void loadResources(ResourceManager resources) {
    ImageStrip cityDecorations = resources.getSlickImgStrip("cityDecorations");
    Image captureImg = cityDecorations.getSubImage(0);
    Image defenseImg = cityDecorations.getSubImage(1);

    terrainImgs = resources.getSlickImgStrip("terrains");
    terrainBox = new ImageBox();
    terrainBox.setWidth(getWidth());

    Font defaultFont = container.getDefaultFont();
    defenseRow = new Row(new ImageBox(defenseImg), new TextBox("", defaultFont));
    defenseRow.setHorizontalSpacing(5);
    captureRow = new Row(new ImageBox(captureImg), new TextBox("", defaultFont));
    captureRow.setHorizontalSpacing(5);
  }

  public void renderimpl(GUIContext container, Graphics g) {
    if (tile != null) {
      Color origColor = g.getColor();
      int x = getX();
      int y = getY();

      renderBackground(g, x, y);
      initBoxes(tile.getTerrain());
      locateBoxes(x, y);
      renderBoxes(g);
      g.setColor(origColor);
    }
  }

  private void renderBackground(Graphics g, int x, int y) {
    g.setColor(backgroundColor);
    g.fillRect(x, y, getWidth(), getHeight());
  }

  private void initBoxes(Terrain terrain) {
    terrainBox.setImage(getTerrainImg(terrain));
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

  private void locateBoxes(int x, int y) {
    terrainBox.setLocation(x, y);
    defenseRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + terrainBox.getHeight());
    captureRow.setLocation(x + INFO_BOXES_LEFT_MARGIN, y + terrainBox.getHeight() + defenseRow.getHeight());
  }

  private void renderBoxes(Graphics g) {
    terrainBox.render(g);
    renderTerrainName(g);
    captureRow.render(g);
    defenseRow.render(g);
  }

  private void renderTerrainName(Graphics g) {
    g.setColor(textColor);
    Terrain terrain = tile.getTerrain();
    g.drawString(App.translate(terrain.getName().toLowerCase()), getX() + TERRAIN_NAME_LEFT_MARGIN, getY());
  }

  public void setTile(Tile tile) {
    this.tile = tile;
  }

  /**
   * A Row contains 2 boxes
   * an Image box and a text box with horizontalSpacing between them.
   * The row always has the  height of the tallest box
   */
  private class Row extends Box {
    private ImageBox imageBox;
    private TextBox textBox;
    private int horizontalSpacing;

    private Row(ImageBox imageBox, TextBox textBox) {
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
