package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.COSheet;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Color;

/**
 * Render The money the player can spend, CO and the power gauge
 * The rendering can be performed from left to right or from right to left.
 */
public class COPowerGaugeRenderer implements Renderable {
  private static final int CO_BAR_MIDDLE_SPACING = 5;
  private static final int CO_BAR_CO_HEAD_MARGIN = 4;
  private static final int CO_BAR_TOP_MARGIN = 2;
  private final Game game;
  private final int guiWidth;
  private ResourceManager resources;
  private Font numberFont;
  private Image lightBarImg, darkBarImg;
  private boolean leftToRight = true;

  public COPowerGaugeRenderer(Game game, int guiWidth) {
    this.game = game;
    this.guiWidth = guiWidth;
    this.leftToRight = false;
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    this.numberFont = resources.getFont("numbers");
    this.lightBarImg = resources.getSlickImg("light_bar");
    this.darkBarImg = resources.getSlickImg("dark_bar");
  }

  @Override
  public void render(Graphics g) {
    Player activePlayer = game.getActivePlayer();
    CO co = activePlayer.getCO();
    Color color = activePlayer.getColor();
    int budget = activePlayer.getBudget();

    if (leftToRight) {
      renderLeftToRight(g, co, color, budget + "");
    } else {
      renderRightToLeft(g, co, color, budget + "");
    }
  }

  private void renderLeftToRight(Graphics g, CO co, Color color, String budget) {
    Image coBar = resources.getLeftCOBar(color);
    COSheet coSheet = resources.getCOSheet(co);
    Image coHead = coSheet.getLeftHead(3);

    g.drawImage(coHead, 0, 0);
    g.drawImage(coBar, 0, coHead.getHeight());
    numberFont.drawString(0, coHead.getHeight(), budget);

    renderBars(g, co, coHead.getWidth() + CO_BAR_CO_HEAD_MARGIN, CO_BAR_TOP_MARGIN);
  }

  private void renderRightToLeft(Graphics g, CO co, Color color, String budget) {
    Image coBar = resources.getRightCOBar(color);
    COSheet coSheet = resources.getCOSheet(co);
    Image coHead = coSheet.getRightHead(3);

    g.drawImage(coHead, guiWidth - coHead.getWidth(), 0);
    g.drawImage(coBar, guiWidth - coBar.getWidth(), coHead.getHeight());
    numberFont.drawString(guiWidth - numberFont.getWidth(budget), coHead.getHeight(), budget);

    int barsWidth = getTotalBarsWidth(co);
    int rightOffset = guiWidth - coHead.getWidth() - barsWidth - CO_BAR_CO_HEAD_MARGIN;
    renderBars(g, co, rightOffset, CO_BAR_TOP_MARGIN);
  }

  private int getTotalBarsWidth(CO co) {
    return lightBarImg.getWidth() * co.getMaxBars() + CO_BAR_MIDDLE_SPACING;
  }

  private void renderBars(Graphics g, CO co, int x, int y) {
    int barWidth = lightBarImg.getWidth();

    for (int barIndex = 0; barIndex < co.getMaxBars(); barIndex++) {
      int barX = barIndex * barWidth;

      // Split into 2 groups
      if (barIndex >= co.getMaxBars() / 2) {
        barX += CO_BAR_MIDDLE_SPACING;
      }

      if (barIndex < co.getBars()) {
        g.drawImage(lightBarImg, x + barX, y);
      } else {
        g.drawImage(darkBarImg, x + barX, y);
      }
    }
  }

  public void setRenderLeftToRight(boolean leftToRight) {
    this.leftToRight = leftToRight;
  }
}
