package com.customwars.client.ui;

import com.customwars.client.ui.state.CWInput;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.util.Log;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows a menu on the screen, at location locX, locY
 * Menu txtOptions can either be text or an image but not both
 *
 * @author JSR
 */

public class PopUpMenu extends AbstractComponent implements ComponentListener {
  private static final int ITEM_HEIGHT = 10;
  private GUIContext container;
  private List<String> txtOptions;
  private List<Image> imgOptions;
  private List<MouseOverArea> mouseOverAreas;
  private Color baseColor;
  private Color selectColor;
  private int locX;
  private int locY;
  private int spacingY;
  private int spacingX;
  private int curoptn;
  private Sound menuSound;
  private Image cursor;
  private Font font;
  private Dimension dimension;
  private boolean initDone;

  public PopUpMenu(GUIContext container, Font font) {
    super(container);
    this.container = container;
    this.font = font == null ? getDefaultFont() : font;
    dimension = new Dimension();
    spacingY = 20;
    spacingX = 10;
    baseColor = Color.white;
    selectColor = Color.black;
    txtOptions = new ArrayList<String>();
    imgOptions = new ArrayList<Image>();
    mouseOverAreas = new ArrayList<MouseOverArea>();
  }

  public PopUpMenu(GUIContext container) {
    this(container, null);
  }

  private Font getDefaultFont() {
    Font defaultFont = null;
    try {
      defaultFont = new AngelCodeFont(
              "org/newdawn/slick/data/default.fnt",
              "org/newdawn/slick/data/default_00.tga");
    } catch (SlickException e) {
      Log.error(e);
    }
    return defaultFont;
  }

  public void setVerticalMargin(int verticalMargin) {
    spacingY = verticalMargin;
  }

  public void setCursorImage(Image theImage) {
    cursor = theImage;
    calcHorizontalMargin();
  }

  private void calcHorizontalMargin() {
    if (cursor != null)
      this.spacingX = cursor.getWidth() + spacingX;
  }

  public void setLocation(int x, int y) {
    locX = x;
    locY = y;
  }

  public int getX() {
    return locX;
  }

  public int getY() {
    return locY;
  }

  public int getWidth() {
    return (int) dimension.getWidth();
  }

  public int getHeight() {
    return (int) dimension.getHeight();
  }

  /**
   * @param menuTickSound The sound to play when the menu changes to another menu item
   *                      null to disable that sound
   */
  public void setSound(Sound menuTickSound) {
    menuSound = menuTickSound;
  }

  public void setColor(Color base, Color selected) {
    baseColor = base;
    selectColor = selected;
  }

  public void addOption(String name) {
    addMenuOption(name, null);
  }

  public void addOptionImage(Image image) {
    addMenuOption(null, image);
  }

  private void addMenuOption(String name, Image img) {
    txtOptions.add(name);
    imgOptions.add(img);

    int width = font.getWidth(name);

    // store total width and total height
    if (width > dimension.getWidth()) {
      dimension.setSize(width, ITEM_HEIGHT + spacingX);
    }
  }

  public void moveUp() {
    setOption(curoptn - 1);
  }

  public void moveDown() {
    setOption(curoptn + 1);
  }

  public int getOption() {
    return curoptn;
  }

  private void setOption(int option) {
    if (option >= 0 && option < txtOptions.size() && option != curoptn) {
      curoptn = option;
      playMenuTick();
    }
  }

  public void render(GUIContext container, Graphics g) throws SlickException {
    if (!initDone) init();

    if (cursor != null) {
      g.drawImage(cursor, locX, (locY + (curoptn * spacingY)));
    }

    for (MouseOverArea moa : mouseOverAreas) {
      moa.render(container, g);
    }

    for (int i = 0; i < txtOptions.size(); i++) {
      int x = spacingX + locX;
      int y = locY + (i * spacingY);
      setCurrentColor(g, i);
      g.drawString(txtOptions.get(i), x, y);
      g.setColor(baseColor);
    }
    g.setColor(baseColor);
  }

  /**
   * Init mouse over areas once
   * this is because we need to know the widest option in the menu before we define the moa bounds
   */
  private void init() {
    for (int i = 0; i < txtOptions.size(); i++) {
      int x = spacingX + locX;
      int y = locY + (i * spacingY);
      MouseOverArea moa = new MouseOverArea(container, null, x, y, (int) dimension.getWidth(), (int) dimension.getHeight(), this);
      moa.setNormalColor(new Color(1, 1, 100, 0.5f));
      moa.setMouseOverColor(new Color(1, 1, 200, 0.8f));
      mouseOverAreas.add(moa);
      initDone = true;
    }
  }

  private void setCurrentColor(Graphics g, int i) {
    if (curoptn == i) {
      g.setColor(selectColor);
    } else {
      g.setColor(baseColor);
    }
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isDownPressed(command)) {
      moveDown();
    } else if (cwInput.isUpPressed(command)) {
      moveUp();
    }
  }

  private void playMenuTick() {
    if (menuSound != null)
      menuSound.play();
  }

  /**
   * This method is invoked when a click has been made within a mouse over Area.
   *
   * @param source the mouse over area clicked on
   */
  public void componentActivated(AbstractComponent source) {
    for (MouseOverArea moa : mouseOverAreas) {
      if (source == moa) {
        setOption(mouseOverAreas.indexOf(moa));
        moa.setFocus(false);
        notifyListeners();
      }
    }
  }
}
