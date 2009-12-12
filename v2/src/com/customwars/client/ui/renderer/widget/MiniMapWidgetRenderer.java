package com.customwars.client.ui.renderer.widget;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.MiniMapRenderer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.WidgetRenderer;
import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.thingle.internal.slick.SlickGraphics;
import org.newdawn.slick.thingle.spi.ThingleGraphics;

import java.awt.Point;

/**
 * Render a MiniMap centered within a Thingle Widget
 * The mini map cannot render outside the panel bounds
 */
public class MiniMapWidgetRenderer implements WidgetRenderer {
  private final MiniMapRenderer miniMapRenderer;

  public MiniMapWidgetRenderer(ResourceManager resources) {
    miniMapRenderer = new MiniMapRenderer();
    miniMapRenderer.setTerrainMiniMap(resources.getSlickImgStrip("miniMap"));
  }

  public void setMap(Map<Tile> map) {
    miniMapRenderer.setMap(map);
  }

  public void paint(ThingleGraphics tg, Widget widget, Rectangle panelBounds) {
    Graphics g = ((SlickGraphics) tg).getGraphics();
    int miniMapWidth = getPreferredWidth();
    int miniMapHeight = getPreferredHeight();

    Point center = GUI.getCenteredRenderPoint(miniMapWidth, miniMapHeight, panelBounds.width, panelBounds.height);
    g.translate(center.x, center.y);
    g.setClip(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
    miniMapRenderer.render(g);
    g.clearClip();
    g.translate(-center.x, -center.y);
  }

  public int getPreferredWidth() {
    return miniMapRenderer.getWidth();
  }

  public int getPreferredHeight() {
    return miniMapRenderer.getHeight();
  }
}
