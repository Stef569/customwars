package com.customwars.client.ui;

import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.slick.MouseOverArea;
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
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a menu on the screen, at location locX, locY
 * Menu txtOptions can either be text or an image but not both
 *
 * @author JSR
 */

public class PopUpMenu extends BasicComponent implements ComponentListener {
  private static final float HOVER_TRANSPARANCY = 0.6f;
  private GUIContext container;
  private List<String> txtOptions;
  private List<Image> imgOptions;
  private List<MouseOverArea> mouseOverAreas;
  private Color baseColor;
  private Color selectColor;

  private int spacingY;
  private int spacingX;
  private int curoptn;
  private Sound optionChangeSound;
  private Image cursor;
  private Font font;
  private boolean initDone;

  public PopUpMenu(GUIContext container, Font font) {
    super(container);
    this.container = container;
    this.font = font != null ? font : getDefaultFont();
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

  public void addOption(String name) {
    addMenuOption(name, null);
  }

  public void addOptionImage(Image image) {
    addMenuOption(null, image);
  }

  private void addMenuOption(String name, Image img) {
    if (name != null || img != null) {
      txtOptions.add(name);
      imgOptions.add(img);

      int optionWidth = getWidth(name, img);
      if (optionWidth > width) {
        width = optionWidth;
      }
    } else {
      throw new IllegalArgumentException("null name or img not allowed");
    }
  }

  public void moveUp() {
    setOption(curoptn - 1);
  }

  public void moveDown() {
    setOption(curoptn + 1);
  }

  /**
   * Init mouse over areas once
   * this is because we need to know the widest option in the menu before we define the moa bounds
   */
  private void init() {
    int numOptions = txtOptions.size();
    for (int i = 0; i < numOptions; i++) {
      createMouseOverArea(i);
    }
    height = numOptions * spacingY;
    initDone = true;
  }

  private void createMouseOverArea(int i) {
    MouseOverArea moa;
    int locX = spacingX + x;
    int locY = y + (i * spacingY);
    int optionHeight = getHeight(txtOptions.get(i), imgOptions.get(i));

    moa = new MouseOverArea(container, null, locX, locY, width, optionHeight, this);
    moa.setNormalColor(new Color(1, 1, 1, 0.0f));
    moa.setMouseOverColor(new Color(1, 1, 1, HOVER_TRANSPARANCY));
    mouseOverAreas.add(moa);
  }

  public void render(GUIContext container, Graphics g) throws SlickException {
    if (!initDone) init();

    if (cursor != null) {
      g.drawImage(cursor, x, (y + (curoptn * spacingY)));
    }

    for (MouseOverArea moa : mouseOverAreas) {
      if (moa.isSelected()) {
        setOption(mouseOverAreas.indexOf(moa));
      }
      moa.render(container, g);
    }

    // Text on top of the mouse over area
    for (int i = 0; i < txtOptions.size(); i++) {
      if (txtOptions.get(i) != null) {
        int locX = spacingX + x;
        int locY = y + (i * spacingY);
        setCurrentColor(g, i);
        g.drawString(txtOptions.get(i), locX, locY);
        g.setColor(baseColor);
      }
    }
    g.setColor(baseColor);
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

  /**
   * @param optionChangeSound The sound to play when the menu changes to another menu option
   *                          null to disable that sound
   */
  public void setOptionChangeSound(Sound optionChangeSound) {
    this.optionChangeSound = optionChangeSound;
  }

  public void setColor(Color base, Color selected) {
    baseColor = base;
    selectColor = selected;
  }

  private void setCurrentColor(Graphics g, int i) {
    if (curoptn == i) {
      g.setColor(selectColor);
    } else {
      g.setColor(baseColor);
    }
  }

  private void setOption(int option) {
    if (option != curoptn && option >= 0 && option < txtOptions.size()) {
      curoptn = option;
      playMenuTick();
      selectOption(curoptn);
    }
  }

  private void playMenuTick() {
    if (optionChangeSound != null)
      optionChangeSound.play();
  }

  private void selectOption(int i) {
    for (MouseOverArea moa : mouseOverAreas) {
      if (mouseOverAreas.get(i) == moa) {
        moa.setSelected(true);
      } else {
        moa.setSelected(false);
      }
    }
  }


  public int getOption() {
    return curoptn;
  }

  private int getWidth(String option, Image img) {
    if (img != null) {
      return img.getWidth();
    } else {
      return font.getWidth(option);
    }
  }

  private int getHeight(String option, Image img) {
    if (img != null) {
      return img.getHeight();
    } else {
      return spacingY;
    }
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

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isDownPressed(command)) {
      moveDown();
    } else if (cwInput.isUpPressed(command)) {
      moveUp();
    }
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
