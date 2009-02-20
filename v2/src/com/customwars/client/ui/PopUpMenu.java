package com.customwars.client.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a menu on the screen, at location locX, locY
 * Menu txtOptions can either be text or an image but not both
 *
 * @author JSR
 */

public class PopUpMenu {

  private List<String> txtOptions;
  private List<Image> imgOptions;
  private Color baseColor;
  private Color selectColor;
  private int locX;
  private int locY;
  private int mouseX;
  private int mouseY;
  private int spacingY;
  private int spacingX;
  private int curoptn;
  private Sound menuSound;
  private Image cursor;

  public PopUpMenu() {
    mouseX = -1;
    mouseY = -1;
    spacingY = 20;
    spacingX = 10;
    baseColor = Color.white;
    selectColor = Color.black;
    txtOptions = new ArrayList<String>();
    imgOptions = new ArrayList<Image>();
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
  }

  public void moveUp() {
    setOption(curoptn--);
  }

  public void moveDown() {
    setOption(curoptn++);
  }
  
  public int getOption(){
      return curoptn;
  }

  private void setOption(int option) {
    if (option >= 0 && option < txtOptions.size() && option != curoptn) {
      curoptn = option;
      playMenuTick();
    }
  }

  public void render(Graphics g) {
    handleMouse(g.getFont());

    if (cursor != null) {
      g.drawImage(cursor, locX, (locY + (curoptn * spacingY)));
    }

    for (int i = 0; i < txtOptions.size(); i++) {
      int x = spacingX + locX;
      int y = locY + (i * spacingY);

      if (imgOptions.get(i) != null) {
        g.drawImage(imgOptions.get(i), x, y);
      } else {
        setCurrentColor(g, i);
        g.drawString(txtOptions.get(i), x, y);
        g.setColor(baseColor);
      }
    }
  }

  public void handleMouse(Font font) {
    for (int i = 0; i < txtOptions.size(); i++) {
      int picSpaceY;
      int picSpaceX;

      if (imgOptions.get(i) != null) {
        picSpaceX = imgOptions.get(i).getWidth();
        picSpaceY = imgOptions.get(i).getHeight();
      } else {
        picSpaceX = font.getWidth(txtOptions.get(i));
        picSpaceY = font.getHeight(txtOptions.get(i));
      }

      if (mouseX >= locX && mouseX <= spacingX + locX + picSpaceX) {
        if (mouseY >= locY + (i * spacingY) && mouseY <= locY + (i * spacingY) + picSpaceY) {
          if (curoptn != i) {
            setOption(i);
            mouseX = -1;
            mouseY = -1;
          }
        }
      }
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
    if (cwInput.isNextMenuItemPressed(command)) {
      moveDown();
    } else if (cwInput.isPreviousMenuItemPressed(command)) {
      moveUp();
    }
  }

  public void mouseMoved(int newx, int newy) {
    mouseX = newx;
    mouseY = newy;
  }

  private void playMenuTick() {
    if (menuSound != null)
      menuSound.play();
  }
}
