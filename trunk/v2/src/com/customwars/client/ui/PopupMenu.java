package com.customwars.client.ui;

import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.slick.MouseOverArea;
import com.customwars.client.ui.state.CWInput;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
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
 * Menu Options can either be text or an image but not both
 *
 * @author JSR
 */
public class PopupMenu extends BasicComponent implements ComponentListener {
  private static final float HOVER_TRANSPARANCY = 0.6f;
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
  private String popUpName;
  private Input input;
  private boolean visible;

  public PopupMenu(GUIContext guiContext, Font font, String name) {
    super(guiContext);
    this.input = guiContext.getInput();
    this.font = font != null ? font : getDefaultFont();
    this.popUpName = name;
    this.visible = false;
    spacingY = 20;
    spacingX = 10;
    baseColor = Color.white;
    selectColor = Color.black;
    txtOptions = new ArrayList<String>();
    imgOptions = new ArrayList<Image>();
    mouseOverAreas = new ArrayList<MouseOverArea>();
  }

  public PopupMenu(GUIContext guiContext, String popUpName) {
    this(guiContext, null, popUpName);
  }

  public PopupMenu(GUIContext guiContext) {
    this(guiContext, "");
  }

  public void addOption(String popUpName) {
    addMenuOption(popUpName, null);
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
      throw new IllegalArgumentException("null popUpName or img not allowed");
    }
  }

  public void moveUp() {
    setOption(curoptn - 1);
  }

  public void moveDown() {
    setOption(curoptn + 1);
  }

  /**
   * Init mouse over areas once after all options are set
   * this is because we need to know the widest option in the menu before we define the moa bounds
   */
  public void init() {
    initMouseOverAreas();
    height = txtOptions.size() * spacingY;
    selectOption(0);
  }

  private void initMouseOverAreas() {
    for (int i = 0; i < txtOptions.size(); i++) {
      createMouseOverArea(i);
      locateMouseOverArea(i);
    }
  }

  private void createMouseOverArea(int i) {
    int optionHeight = getHeight(txtOptions.get(i), imgOptions.get(i));
    MouseOverArea moa = new MouseOverArea(container, null, 0, 0, spacingX + width, optionHeight, this);
    moa.setNormalColor(new Color(1, 1, 1, 0.0f));
    moa.setMouseOverColor(new Color(1, 1, 1, HOVER_TRANSPARANCY));
    mouseOverAreas.add(moa);
  }

  private void locateMouseOverArea(int i) {
    if (mouseOverAreas != null) {
      MouseOverArea moa = mouseOverAreas.get(i);
      moa.setLocation(spacingX + x, y + (i * spacingY));
    }
  }

  public void render(GUIContext container, Graphics g) {
    if (visible) {
      renderCursor(g);
      renderMouseOverAreas(g);

      // Update current option when hovering over with the mouse
      MouseOverArea selectedMoa = getSelectedMouseOverArea();
      setOption(mouseOverAreas.indexOf(selectedMoa));

      renderText(g);
      renderImg(g);
      resetToBaseColor(g);
    }
  }

  private void renderCursor(Graphics g) {
    if (cursor != null) {
      g.drawImage(cursor, x, (y + (curoptn * spacingY)));
    }
  }

  private void renderMouseOverAreas(Graphics g) {
    for (MouseOverArea moa : mouseOverAreas) {
      moa.render(container, g);

      // moa.render can fire events!
      // These events can modify the moa areas
      // Resulting in a ConcurrentMofication Exception
      // those events should set visible to false
      // moa.render sets a rectangle to graphics
      // resetting the color, removes that rectangle
      if (!isVisible()) {
        resetToBaseColor(g);
        return;
      }
    }
  }

  private void renderText(Graphics g) {
    for (int i = 0; i < txtOptions.size(); i++) {
      if (txtOptions.get(i) != null) {
        int locX = spacingX + x;
        int locY = y + (i * spacingY);
        setCurrentColor(g, i);
        g.drawString(txtOptions.get(i), locX, locY);
        g.setColor(baseColor);
      }
    }
  }

  private void renderImg(Graphics g) {
    for (int i = 0; i < imgOptions.size(); i++) {
      if (imgOptions.get(i) != null) {
        int locX = spacingX + x;
        int locY = y + (i * spacingY);
        setCurrentColor(g, i);
        g.drawImage(imgOptions.get(i), locX, locY);
        g.setColor(baseColor);
      }
    }
  }

  private void resetToBaseColor(Graphics g) {
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
   * New location of this popup
   * This method will relocate all mouseover areas
   */
  public void setLocation(int x, int y) {
    super.setLocation(x, y);
    if (txtOptions != null) {
      for (int i = 0; i < txtOptions.size(); i++) {
        locateMouseOverArea(i);
      }
    }
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
    if (option >= 0 && option < txtOptions.size()) {
      curoptn = option;
      playMenuTick();
      selectOption(curoptn);
    }
  }

  public void setName(String popUpName) {
    this.popUpName = popUpName;
  }

  private void playMenuTick() {
    if (optionChangeSound != null)
      optionChangeSound.play();
  }

  private void selectOption(int i) {
    if (i >= 0 && i < mouseOverAreas.size()) {
      for (MouseOverArea moa : mouseOverAreas) {
        if (mouseOverAreas.get(i) == moa) {
          moa.setSelected(true);
        } else {
          moa.setSelected(false);
        }
      }
    }
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return visible;
  }

  public int getCurrentOption() {
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
    } else if (cwInput.isSelectPressed(command)) {
      selectOption(curoptn);
      MouseOverArea moa = getSelectedMouseOverArea();
      if (moa != null)
        componentActivated(moa);
    }
    consumeEvent();
  }

  /**
   * This method is invoked when a click has been made within a mouse over Area.
   *
   * @param source the mouse over area clicked on
   */
  public void componentActivated(AbstractComponent source) {
    MouseOverArea clickedMoa = getClickedMouseOverArea(source);
    setOption(mouseOverAreas.indexOf(clickedMoa));
    clickedMoa.setFocus(false);
    notifyListeners();
  }

  private MouseOverArea getClickedMouseOverArea(AbstractComponent source) {
    for (MouseOverArea moa : mouseOverAreas) {
      if (source == moa) {
        return moa;
      }
    }
    throw new IllegalStateException("Don't know about moa " + source);
  }

  private MouseOverArea getSelectedMouseOverArea() {
    for (MouseOverArea moa : mouseOverAreas) {
      if (moa.isSelected()) {
        return moa;
      }
    }
    return null;
  }

  public void clear() {
    removeAllListener();
    mouseOverAreas.clear();
    txtOptions.clear();
    imgOptions.clear();
  }

  private void removeAllListener() {
    for (MouseOverArea moa : mouseOverAreas) {
      input.removeListener(moa);
    }
    this.listeners.clear();
  }
}
