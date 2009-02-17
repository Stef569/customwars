package com.customwars.client.ui;

import com.customwars.client.ui.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.MouseOverArea;

/**
 * Shows a menu on the screen, at location locx, locy
 *
 * @author JSR
 */

public class PopUpMenu {

  private String[] options;
  private Image[] optImg;
  private Color baseColor;
  private Color selectColor;
  private int locx;
  private int locy;
  private int mousex;
  private int mousey;
  private int spacingy;
  private Image cursor;
  private int curoptn;
  private Sound menuSound;
  private MouseOverArea moa;

  //For those junk menu functions
  private int spacing;
  private int picSpaceX;
  private int picSpaceY;

  PopUpMenu(int noOfOptions) {
    locx = 0;
    locy = 0;
    mousex = -1;
    mousey = -1;
    curoptn = 0;
    spacing = 0;
    picSpaceX = 0;
    picSpaceY = 0;
    spacingy = 20;
    cursor = null;
    menuSound = null;
    setSize(noOfOptions);
    baseColor = new Color(Color.white);
    selectColor = new Color(Color.black);
  }

  public void setSize(int noOfOptions) {
    String[] temp = new String[0];
    Image[] tempImg = new Image[0];
    if (options != null)
      temp = options;
    if (optImg != null)
      tempImg = optImg;
    options = new String[noOfOptions];
    optImg = new Image[noOfOptions];
    for (int i = 0; i < options.length; i++) {
      options[i] = "";
      optImg[i] = null;
      if (i < temp.length)
        options[i] = temp[i];
      if (i < tempImg.length)
        optImg[i] = tempImg[i];
    }
  }

  public void setSpacing(int number) {
    spacingy = number;
  }

  public void setLocation(int x, int y) {
    locx = x;
    locy = y;
  }

  public void setCursorImage(Image theImage) {
    cursor = theImage;
  }

  public void setSound(Sound theSound) {
    if (theSound != null)
      menuSound = theSound;
  }

  public void setColor(Color base, Color selected) {
    baseColor = base;
    selectColor = selected;
  }

  public void setOptionImage(Image image) {
    for (int i = 0; i < options.length; i++) {
      if (image != null) {
        if (optImg[i] == null) {
          optImg[i] = image;
          return;
        }
      }
    }
  }

  public void setOptionImage(int index, Image image) {
    if (index >= 0 && index < optImg.length) {
      if (image != null)
        optImg[index] = image;
    }
  }

  public void setOptionName(String name) {
    for (int i = 0; i < options.length; i++) {
      if (name != null) {
        if (options[i] == null || options[i].matches("")) {
          options[i] = name;
          return;
        }
      }
    }
  }

  public void setOptionName(int index, String name) {
    if (index >= 0 && index < options.length) {
      if (name != null)
        options[index] = name;
    }
  }

  public void moveUp() {
    if (curoptn > 0)
      curoptn--;
  }

  public void moveDown() {
    if (curoptn + 1 < options.length)
      curoptn++;
  }

  public void render(Graphics g) {
    if (cursor != null)
      spacing = cursor.getWidth() + spacingy;

    //Deals with the mouse...
    for (int i = 0; i < options.length; i++) {

      //Saves space, time, and money
      if (optImg[i] != null) {
        picSpaceX = optImg[i].getWidth();
        picSpaceY = optImg[i].getHeight();
      } else {
        picSpaceX = g.getFont().getWidth(options[i]);
        picSpaceY = g.getFont().getHeight(options[i]);
      }

      if (mousex >= locx && mousex <= spacing + locx + picSpaceX) {
        if (mousey >= locy + (i * spacingy) && mousey <=
                locy + (i * spacingy) + picSpaceY) {
          if (curoptn != i) {
            curoptn = i;
            mousex = -1;
            mousey = -1;
            playSound();
          }
        }
      }
    }

    for (int i = 0; i < options.length; i++) {
      if (curoptn == i) {
        g.setColor(selectColor);
        if (optImg[i] != null)
          g.drawImage(optImg[i], spacing + locx,
                  locy + (i * spacingy), selectColor);
      } else {
        g.setColor(baseColor);
        if (optImg[i] != null)
          g.drawImage(optImg[i], spacing + locx,
                  locy + (i * spacingy), baseColor);
      }

      if (optImg[i] == null)
        g.drawString(options[i], spacing + locx, locy + (i * spacingy));
    }

    if (cursor != null)
      g.drawImage(cursor, locx, (locy + (curoptn * spacingy)));

    g.setColor(baseColor);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isNextMenuItemPressed(command)) {
      moveDown();
      playSound();
    } else if (cwInput.isPreviousMenuItemPressed(command)) {
      moveUp();
      playSound();
    }
  }

  public void mouseMoved(int newx, int newy) {
    mousex = newx;
    mousey = newy;
  }

  private void playSound() {
    if (menuSound != null)
      menuSound.play();
  }
}
