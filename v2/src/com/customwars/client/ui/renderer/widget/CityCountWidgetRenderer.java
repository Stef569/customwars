package com.customwars.client.ui.renderer.widget;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.slick.ImageStripFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.WidgetRenderer;
import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.thingle.internal.slick.SlickGraphics;
import org.newdawn.slick.thingle.spi.ThingleGraphics;

import java.awt.Color;

public class CityCountWidgetRenderer implements WidgetRenderer {
  private static final int HORIZONTAL_MARGIN = 10;
  private static final int BACKGROUND_MARGIN = 10;
  private static final org.newdawn.slick.Color BACKGROUND_COLOR = new org.newdawn.slick.Color(7, 66, 97);

  private Map<Tile> map;
  private int[] mapCitiesCount;

  private boolean preDeployed;
  private final ResourceManager resources;
  private final Color neutralColor;
  private final Font numbers;
  private int preferredWidth;

  public CityCountWidgetRenderer(ResourceManager resources) {
    this.resources = resources;
    neutralColor = App.getColor("plugin.neutral_color");
    ImageStrip numberStrip = resources.getSlickImgStrip("numbers");
    numbers = new ImageStripFont(numberStrip, '1');
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
    preDeployed = false;
    mapCitiesCount = new int[5];
    calcMapProperties();
    preferredWidth = (mapCitiesCount.length * map.getTileSize() + mapCitiesCount.length * HORIZONTAL_MARGIN) - HORIZONTAL_MARGIN;
  }

  private void calcMapProperties() {
    for (Tile t : map.getAllTiles()) {
      City city = map.getCityOn(t);

      if (city != null) {
        if (city.getName().equalsIgnoreCase("city")) {
          mapCitiesCount[0]++;
        } else if (city.getName().equalsIgnoreCase("factory")) {
          mapCitiesCount[1]++;
        } else if (city.getName().equalsIgnoreCase("airport")) {
          mapCitiesCount[2]++;
        } else if (city.getName().equalsIgnoreCase("port")) {
          mapCitiesCount[3]++;
        } else if (city.getName().equalsIgnoreCase("HQ")) {
          mapCitiesCount[4]++;
        } else if (city.getName().equalsIgnoreCase("missle_silo")) {
          mapCitiesCount[5]++;
        }

        if (map.getUnitOn(t) != null) {
          preDeployed = true;
        }
      }
    }
  }

  public void paint(ThingleGraphics tg, Widget widget, Rectangle bounds) {
    Graphics g = ((SlickGraphics) tg).getGraphics();
    g.translate(-getPreferredWidth() / 2, 20);
    renderBackGround(g);
    g.translate(0, -20);
    g.setColor(org.newdawn.slick.Color.white);
    renderContent(g);
    g.translate(getPreferredWidth() / 2, 0);
  }

  private void renderBackGround(Graphics g) {
    g.setColor(BACKGROUND_COLOR);
    int x = -BACKGROUND_MARGIN;
    int y = -BACKGROUND_MARGIN;
    int width = getPreferredWidth() + BACKGROUND_MARGIN * 2;
    int height = getPreferredHeight() + BACKGROUND_MARGIN * 2;
    g.fillRoundRect(x, y, width, height, 20);
  }

  private void renderContent(Graphics g) {
    int x = 0;
    for (int cityID = 0; cityID < mapCitiesCount.length; cityID++) {
      paintCityCount(cityID, mapCitiesCount[cityID], x, g);
      x += map.getTileSize() + HORIZONTAL_MARGIN;
    }
  }

  private void paintCityCount(int cityID, int cityCount, int x, Graphics g) {
    Image cityImage = resources.getCityAnim(cityID, neutralColor).getImage(0);

    g.drawImage(cityImage, x, 0);

    if (cityCount > 0) {
      int cityCountX = cityImage.getWidth() - numbers.getWidth(cityCount + "");
      int cityCountY = cityImage.getHeight() - numbers.getLineHeight();
      numbers.drawString(x + cityCountX, cityCountY, cityCount + "");
    }
  }

  public int getPreferredWidth() {
    return preferredWidth;
  }

  public int getPreferredHeight() {
    return 40;
  }
}
