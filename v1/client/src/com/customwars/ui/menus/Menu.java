package com.customwars.ui.menus;

import com.customwars.sfx.SFX;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.util.GuiUtil;

import java.awt.*;

/**
 * A Menu where you can change the selected item by invoking either:
 * menuMoveUp, menuMoveDown or setMenuPosition
 * each function cannot move out of bounds instead it will:
 * set current menu item to max when attempting to go lower the 0 and
 * set current menu item to 0 when trying to go over numMenuItems
 * <p/>
 * <p/>A menuTick sound is played when the position is changed
 * withinBounds(int item, int max) can be used by the subclass for bound checks.
 * <p/>
 * Menu also offers a way to layout painting of text by placing text into 1 line.
 * By using the Default values LEFT_OFFSET, TOP_OFFSET and HORIZONTAL_PAGE_LEFT_OFFSET
 * Each time drawLine is called <tt>line<tt> is incremented
 *
 * @author stefan
 * @since 2.0
 */
public abstract class Menu {
    private static final Color HIGHLIGHT_COLOR = Color.RED;
    private int LEFT_OFFSET = 10, TOP_OFFSET = 20, HORIZONTAL_PAGE_LEFT_OFFSET = 235;
    private int numMenuItems;
    private int currentMenuItem;
    private int line;
    private String CONFIG_LINE_DELIMITER = ": ";
    private int multiHorizontalPageLeftOffset;

    protected Menu(int numMenuItems) {
        this.numMenuItems = numMenuItems;
    }

    void menuMoveUp() {
        setCurrentMenuItem(--currentMenuItem);
    }

    void menuMoveDown() {
        setCurrentMenuItem(++currentMenuItem);
    }

    void setCurrentMenuItem(int item) {
        currentMenuItem = withinBounds(item, numMenuItems - 1);
        playMenuTick();
    }

    /**
     * @param item the new item to validate against the menu item bounds
     * @param max  exclusive max bound
     *             (item=2 max=2) will return 2,
     *             (item=3 max=2) will return 0
     * @return max if the item was <0
     *         0 if the item was > max
     *         item if the item was within bounds
     */
    int withinBounds(int item, int max) {
        if (item < 0) {
            return max;
        } else if (item > max) {
            return 0;
        } else {
            return item;
        }
    }

    int getCurrentMenuItem() {
        return currentMenuItem;
    }

    int getTopOffset(Graphics g) {
      int fontHeight = g.getFontMetrics().getHeight();
      return multiHorizontalPageLeftOffset + TOP_OFFSET + line * fontHeight;
    }

    boolean isFirstItemSelected() {
        return currentMenuItem == 0;
    }

    boolean isLastItemSelected() {
        return currentMenuItem == numMenuItems - 1;
    }

    void playMenuTick() {
        SFX.playSound("menutick.wav");
    }

    // PAINT
    /**
     * Paints multiple strings on 1 line
     * delimiter is used between each string
     */
    public void drawMultiTextOnLine(Graphics g, String delimiter, String... strings) {
        int leftOffset = 0;
        for (String txt : strings) {
            txt += delimiter;
            leftOffset += GuiUtil.getStringWidth(txt, g);
            drawOnCurrentLine(txt, LEFT_OFFSET + leftOffset, g);
        }
    }

    /**
     * Paints a config txt and next to it the value on 1 line.
     * config is painted in HIGHLIGHT_COLOR if highLight is true
     * configVal is painted in defaultColor
     * <p/>
     * Example:
     * drawOptionLine("Music Muted",Options.isMusicMuted ? "Yes","No", currentMenuItem=4, g)
     * Output:
     * Music Muted: Yes
     */
    public void drawConfigLine(String config, String configVal, boolean highLight, Graphics g) {
        Color defaultColor = MainMenuGraphics.getH1Color();
        drawConfigLine(config, configVal, highLight ? HIGHLIGHT_COLOR : defaultColor, defaultColor, g);
    }

    /**
     * Paints a config and next to it the value on 1 line.
     * config is painted in highLightColor and configVal is painted in defaultColor
     * <p/>
     * Example:
     * drawOptionLine("Music Muted",Options.isMusicMuted ? "Yes","No", currentMenuItem=4, g)
     * Output:
     * Music Muted: Yes
     *
     * @param highLightColor Color used to paint the config string.
     * @param defaultColor   Color used to paint the configVal string.
     */
    public void drawConfigLine(String config, String configVal, Color highLightColor, Color defaultColor, Graphics g) {
        config += CONFIG_LINE_DELIMITER;
        g.setColor(highLightColor);
        drawOnCurrentLine(config, multiHorizontalPageLeftOffset, g);

        g.setColor(defaultColor);
        if (configVal != null) {
            int configWidth = GuiUtil.getStringWidth(config, g);
            drawOnCurrentLine(configVal, multiHorizontalPageLeftOffset + configWidth, g);
        }
        line++;
    }

    void setHighlightColor(boolean highLight, Graphics g) {
        if (highLight) {
            g.setColor(HIGHLIGHT_COLOR);
        } else {
            g.setColor(MainMenuGraphics.getH1Color());
        }
    }

    private void drawOnCurrentLine(String lineTxt, int leftOffset, Graphics g) {
        int fontHeight = g.getFontMetrics().getHeight();
        g.drawString(lineTxt, LEFT_OFFSET + leftOffset, TOP_OFFSET + line * fontHeight);
    }

    /**
     * Draws a single line
     * The line is updated on each invocation.
     * This method works with all fonts.
     */
    public void drawLine(String lineTxt, Graphics g) {
        int fontHeight = g.getFontMetrics().getHeight();
        g.drawString(lineTxt, LEFT_OFFSET + multiHorizontalPageLeftOffset, TOP_OFFSET + line++ * fontHeight);
    }

    // SETTERS
    public void setLeftOffset(int leftOffset) {
        this.LEFT_OFFSET = leftOffset;
    }

    public void setTopOffset(int topOffset) {
        this.TOP_OFFSET = topOffset;
    }

    public void setHorizontalPageLeftOffset(int horizontalPageLeftOffset) {
        this.HORIZONTAL_PAGE_LEFT_OFFSET = horizontalPageLeftOffset;
    }

    public void setOptionLineDelimiter(String optionLineDelimiter) {
        this.CONFIG_LINE_DELIMITER = optionLineDelimiter;
    }

    // ACTIONS
    public void resetLines() {
        line = 0;
        multiHorizontalPageLeftOffset = 0;
    }

    public void startNewPage() {
        line = 0;
        multiHorizontalPageLeftOffset += HORIZONTAL_PAGE_LEFT_OFFSET;
    }

    public void skipLines(int lines) {
        line += lines;
    }
}
