package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.tools.NumberUtil;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Dimension;

/**
 * Template for a Horizontal Info panel
 */
public abstract class HorizontalInfoPanel extends Box implements InfoPanel {
  private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.40f);
  private static final Color BORDER_COLOR = Color.black;
  private static final int BACKGROUND_MARGIN = 1;
  private final int tileSize;
  protected ImageBox imgBox;
  protected Font font;
  private int imgTranslationY;
  private boolean renderBorder = false;

  public HorizontalInfoPanel() {
    tileSize = App.getInt("plugin.tilesize");
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.font = resources.getFont("default");
  }

  public void setFrontImage(Image img) {
    this.imgBox = new ImageBox(img);
    imgTranslationY = Math.abs(tileSize - img.getHeight());
  }

  @Override
  public final void renderImpl(Graphics g) {
    renderBackground(g);
    g.translate(getX(), getY() + BACKGROUND_MARGIN);
    renderFrontImg(g);
    g.translate(imgBox.getWidth(), 0);
    renderName(g);
    g.translate(0, (float) getNameSize().getHeight());
    renderInfo(g);
    g.resetTransform();
  }

  private void renderFrontImg(Graphics g) {
    g.translate(0, -imgTranslationY + getHeight() / 2 - tileSize / 2);
    imgBox.render(g);
    g.translate(0, imgTranslationY - getHeight() / 2 + tileSize / 2);
  }

  private void renderBackground(Graphics g) {
    Color origColor = g.getColor();
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(getX(), getY(), getWidth(), getHeight());
    if (renderBorder) {
      g.setColor(BORDER_COLOR);
      g.drawRect(getX(), getY(), getWidth(), getHeight());
    }
    g.setColor(origColor);
  }

  protected abstract void renderName(Graphics g);

  protected abstract void renderInfo(Graphics g);

  public void setRenderBorder(boolean renderBorder) {
    this.renderBorder = renderBorder;
  }

  public void initSize() {
    Dimension nameBoxSize = getNameSize();
    Dimension infoBoxSize = getInfoSize();
    int widestBox = NumberUtil.findHighest(nameBoxSize.width, infoBoxSize.width);
    int tallest = NumberUtil.findHighest(tileSize, getNameSize().height + getInfoSize().height);
    int widest = imgBox.getWidth() + widestBox;

    setWidth(widest + (BACKGROUND_MARGIN * 2) + 2); // +2 width calculation is not 100% correct
    setHeight(tallest + BACKGROUND_MARGIN * 2);
  }

  protected abstract Dimension getNameSize();

  protected abstract Dimension getInfoSize();
}
