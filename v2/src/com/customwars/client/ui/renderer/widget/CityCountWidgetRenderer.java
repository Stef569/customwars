package com.customwars.client.ui.renderer.widget;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.WidgetRenderer;
import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.thingle.internal.slick.SlickGraphics;
import org.newdawn.slick.thingle.spi.ThingleGraphics;

import java.awt.Dimension;

/**
 * Render the amount of cities that a map contains horizontally
 */
public class CityCountWidgetRenderer implements WidgetRenderer {
  private static final java.awt.Color NEUTRAL_COLOR = App.getColor("plugin.neutral_color");
  private static final Color BACKGROUND_COLOR = new Color(7, 66, 97);
  private static final int HORIZONTAL_MARGIN = 10;
  private static final int BACKGROUND_MARGIN = 8;
  private final ResourceManager resources;

  private Map<Tile> map;
  private int tileSize;
  private int[] mapCitiesCount;  // index=base city ID, value=city count in the map

  private final Font numbers;
  private Dimension preferredSize;

  public CityCountWidgetRenderer(ResourceManager resources) {
    this.resources = resources;
    numbers = resources.getFont("numbers");
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
    this.tileSize = map.getTileSize();
    calcCityCount();
    calcPreferredSize();
  }

  private void calcCityCount() {
    mapCitiesCount = new int[CityFactory.countBaseCities()];
    for (City baseCity : CityFactory.getBaseCities()) {
      int cityCount = map.getCityCount(baseCity.getName());
      mapCitiesCount[baseCity.getID()] = cityCount;
    }
  }

  private void calcPreferredSize() {
    int citiesCount = mapCitiesCount.length;
    int preferredWidth = (citiesCount * tileSize + citiesCount * HORIZONTAL_MARGIN) - HORIZONTAL_MARGIN;
    int preferredHeight = resources.getSingleCityImageHeight(NEUTRAL_COLOR);
    preferredSize = new Dimension(preferredWidth, preferredHeight);
  }

  public void paint(ThingleGraphics tg, Widget widget, Rectangle bounds) {
    Graphics g = ((SlickGraphics) tg).getGraphics();
    // Note that the thingle graphics start the 0,0 point in the center
    g.translate(-preferredSize.width / 2, 0);
    renderBackGround(g);
    g.setColor(Color.white);
    renderContent(g);
    g.translate(preferredSize.width / 2, 0);
  }

  private void renderBackGround(Graphics g) {
    g.setColor(BACKGROUND_COLOR);
    int x = -BACKGROUND_MARGIN;
    int y = preferredSize.height - tileSize - BACKGROUND_MARGIN;
    int width = preferredSize.width + BACKGROUND_MARGIN * 2;
    int height = tileSize + BACKGROUND_MARGIN * 2;
    g.fillRoundRect(x, y, width, height, 20);
  }

  private void renderContent(Graphics g) {
    int x = 0;
    for (City city : CityFactory.getBaseCities()) {
      int cityCount = mapCitiesCount[city.getID()];
      paintCityCount(city, cityCount, x, g);
      x += tileSize + HORIZONTAL_MARGIN;
    }
  }

  private void paintCityCount(City city, int cityCount, int x, Graphics g) {
    Image cityImage = resources.getCityImage(city, 0, NEUTRAL_COLOR);
    g.drawImage(cityImage, x, 0);

    if (cityCount > 0) {
      int cityCountX = cityImage.getWidth() - numbers.getWidth(cityCount + "");
      int cityCountY = cityImage.getHeight() - numbers.getLineHeight();
      numbers.drawString(x + cityCountX, cityCountY, cityCount + "");
    }
  }

  public int getPreferredWidth() {
    return preferredSize.width;
  }

  public int getPreferredHeight() {
    return preferredSize.height;
  }
}