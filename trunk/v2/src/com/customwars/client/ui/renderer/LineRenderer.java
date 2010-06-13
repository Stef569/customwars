package com.customwars.client.ui.renderer;

import com.customwars.client.tools.FontUtil;
import com.customwars.client.tools.NumberUtil;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Render lines of text to the screen.
 * The horizontal overflow allows to cut off lines that are larger then the maximum size.
 * <p/>
 * Basic usage:
 * LineRenderer controlsRenderer = new LineRenderer(font);
 * controlsRenderer.addText("Header");
 * controlsRenderer.addText("line1");
 * controlsRenderer.setLocation(10, 10);
 * controlsRenderer.render(g);
 */
public class LineRenderer implements Renderable {
  /**
   * Controls the horizontal overflow
   */
  public static enum OVERFLOW {
    /**
     * Visible means that everything is rendered even when the text is larger then the maximum size
     */
    VISIBLE,

    /**
     * Hidden means any text that is larger then the maximum size is not shown
     */
    HIDDEN
  }

  private static final Color DEFAULT_TEXT_COLOR = Color.white;
  private static final Color DEFAULT_BACKGROUND_COLOR = new Color(15, 15, 15, 90);
  private static final int INNER_TEXT_MARGIN = 3;
  private final Font font;
  private final int fontHeight;
  private final List<String> textLines;

  private final Point location;
  private Color textColor, backgroundColor;
  private Dimension maxSize;
  private Rectangle rectangle;
  private boolean visible;
  private boolean inited;
  private OVERFLOW overflow;

  public LineRenderer(Font font) {
    this.font = font;
    this.fontHeight = font.getLineHeight();
    this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
    this.textColor = DEFAULT_TEXT_COLOR;
    this.textLines = new ArrayList<String>();
    this.location = new Point(0, 0);
    this.visible = true;
    this.overflow = OVERFLOW.VISIBLE;
  }

  /**
   * Add 1 line of text to be rendered
   * The order in witch text is added is the same order in witch the lines are rendered horizontally.
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
    int[] stringLengths = FontUtil.getStringLenghts(textLines, font);
    int widestLine = NumberUtil.findHighest(stringLengths);
    int totalHeight = textLines.size() * fontHeight;

    if (overflow == OVERFLOW.VISIBLE) {
      this.rectangle = new Rectangle(location.x, location.y, widestLine, totalHeight);
    } else {
      this.rectangle = new Rectangle(location.x, location.y, maxSize.width, maxSize.height);
    }
  }

  private void renderImpl(Graphics g) {
    Color origColor = g.getColor();
    g.translate(location.x, location.y);
    renderBackground(g);
    renderLines(textLines, g);
    g.setColor(origColor);
    g.translate(-location.x, -location.y);
  }

  private void renderBackground(Graphics g) {
    g.setColor(backgroundColor);
    g.fillRect(-INNER_TEXT_MARGIN, -INNER_TEXT_MARGIN, rectangle.getWidth() + INNER_TEXT_MARGIN, rectangle.getHeight() + INNER_TEXT_MARGIN);
  }

  private void renderLines(List<String> lines, Graphics g) {
    g.setColor(textColor);

    if (overflow == OVERFLOW.HIDDEN) {
      g.setClip(rectangle);
    }

    int y = 0;
    for (String text : lines) {
      Font originalFont = g.getFont();
      g.setFont(font);
      g.drawString(text, 0, y);
      y += fontHeight;
      g.setFont(originalFont);
    }

    if (overflow == OVERFLOW.HIDDEN) {
      g.clearClip();
    }
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setOverflow(OVERFLOW overflow) {
    this.overflow = overflow;
    calcComponentSize();
  }

  public void setMaxSize(int maxWidth, int maxHeight) {
    this.maxSize = new Dimension(maxWidth, maxHeight);
    calcComponentSize();
  }

  public void setLocation(int x, int y) {
    location.setLocation(x, y);
  }

  public void clearText() {
    textLines.clear();
  }
}
