package com.customwars.client.ui.renderer;

import com.customwars.client.tools.NumberUtil;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Render lines of text to the screen
 *
 * Basic usage:
 * controlsRenderer = new ControlsRenderer(cwInput, font);
 * controlsRenderer.addText("Header");
 * controlsRenderer.addText("line1");
 * controlsRenderer.setLocation(10, 10);
 * controlsRenderer.render(g);
 */
public class LineRenderer implements Renderable {
  private static final Color TEXT_COLOR = Color.white;
  private static final Color BACKGROUND_COLOR = new Color(15, 15, 15, 90);
  private static final int INNER_TEXT_MARGIN = 3;
  private final Font font;
  private final int fontHeight;
  private final List<String> textLines;

  private final Point location;
  private Dimension size;
  private boolean visible;
  private boolean inited;

  public LineRenderer(Font font) {
    this.font = font;
    this.fontHeight = font.getLineHeight();
    this.textLines = new ArrayList<String>();
    this.location = new Point(0, 0);
    this.visible = true;
  }

  /**
   * Add 1 line of text to be rendered
   * The order in witch text is added is the same order in witch the lines are rendered
   */
  public void addText(String txt) {
    textLines.add(txt);
  }

  public void render(Graphics g) {
    if (!inited) {
      calcComponentSize();
      inited = true;
    }

    if (visible) {
      renderImpl(g);
    }
  }

  private void calcComponentSize() {
    int[] stringLengths = calcStringLenghts();
    int widestControlString = NumberUtil.findHighest(stringLengths);
    int totalHeight = textLines.size() * fontHeight;
    this.size = new Dimension(widestControlString, totalHeight);
  }

  private int[] calcStringLenghts() {
    List<Integer> lenghts = new ArrayList<Integer>();
    for (String control : textLines) {
      int width = font.getWidth(control);
      lenghts.add(width);
    }

    return convertToIntArray(lenghts);
  }

  private static int[] convertToIntArray(List<Integer> ints) {
    int[] primitiveIntArray = new int[ints.size()];
    for (int i = 0; i < ints.size(); i++) {
      primitiveIntArray[i] = ints.get(i);
    }
    return primitiveIntArray;
  }

  private void renderImpl(Graphics g) {
    Color origColor = g.getColor();
    g.translate(location.x, location.y);
    renderBackground(g);
    renderLines(g);
    g.setColor(origColor);
    g.translate(-location.x, -location.y);
  }

  private void renderBackground(Graphics g) {
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(-INNER_TEXT_MARGIN, -INNER_TEXT_MARGIN, size.width + INNER_TEXT_MARGIN, size.height + INNER_TEXT_MARGIN);
  }

  private void renderLines(Graphics g) {
    g.setColor(TEXT_COLOR);

    int y = 0;
    for (String text : textLines) {
      g.drawString(text, 0, y);
      y += fontHeight;
    }
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setLocation(int x, int y) {
    location.setLocation(x, y);
  }
}
