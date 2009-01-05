package com.customwars.ui.menu;

import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleScreen;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.SwitchGradient;
import com.customwars.util.GuiUtil;

import java.awt.*;
import java.awt.image.ImageObserver;

/*
 *InGameMenu.java
 *Author: Urusan
 *Contributors: Stef
 *Creation: July 14, 2006, 1:40 AM
 *An abstract in-game menu. Used to make the battle menu and context menus.
 */
public abstract class InGameMenu {
    private static final int FONT_HEIGHT = 20;
    private static final Font MENU_ITEM_FONT = new Font(Font.SANS_SERIF, Font.BOLD, FONT_HEIGHT);
    private static final int LEFT_OFFSET = 16;

    protected String[] displayItems;
    protected Image[] icons;
    protected int numItems = 0;
    protected int item;
    protected int mx;
    protected int my;
    protected int width;
    ImageObserver screen;

    public InGameMenu(int x, int y, int w, ImageObserver screen){
        mx = x;
        my = y;
        width = w;
        this.screen = screen;
    }

    //YOU MUST CALL THIS IN AN IMPLEMENTING CONSTRUCTOR, loads the strings (and icons)
    protected void loadStrings(String[] s){
        displayItems = s;
        numItems = s.length;
        icons = null;
    }

    //YOU MUST CALL THIS IN AN IMPLEMENTING CONSTRUCTOR, loads the strings (and icons)
    protected void loadStrings(String[] s, Image[] i){
        displayItems = s;
        numItems = s.length;
        icons = i;
    }

    public void drawMenu(Graphics2D g){
      if (icons == null) {
        //Makes the gradient
        GradientPaint gp;
        if (screen instanceof BattleScreen)
          gp = SwitchGradient.getGradient(((BattleScreen) screen).getBattle().getArmy(((BattleScreen) screen).getBattle().getTurn()).getColor());
        else
          gp = SwitchGradient.getGradient(8);
        // Fill with a gradient.
        g.setPaint(gp);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.fillRoundRect(mx, my, width, numItems * FONT_HEIGHT + 2, LEFT_OFFSET, FONT_HEIGHT);
        g.setComposite(AlphaComposite.SrcOver);

        g.setColor(Color.black);
        g.setFont(MENU_ITEM_FONT);
        for (int i = 0; i < numItems; i++) {
          String fullMenuItem = displayItems[i];
          String wrappedMenuItem = GuiUtil.fitLine(fullMenuItem, width - LEFT_OFFSET, g);
          g.drawString(wrappedMenuItem, mx + LEFT_OFFSET, my + (i * FONT_HEIGHT) + FONT_HEIGHT);
        }

        g.drawImage(MiscGraphics.getPointer(), mx, my + item * FONT_HEIGHT, screen);
      } else {
        g.setColor(Color.gray);
        g.fillRect(mx, my, width, numItems * FONT_HEIGHT + 2);
        g.setColor(Color.black);
        g.setFont(MENU_ITEM_FONT);
        for (int i = 0; i < numItems; i++) {
          g.drawImage(icons[i], mx + LEFT_OFFSET, my + (i * FONT_HEIGHT) - (icons[i].getHeight(screen) - FONT_HEIGHT), screen);
          g.drawString(displayItems[i], mx + LEFT_OFFSET*2, my + (i * FONT_HEIGHT) + FONT_HEIGHT);
        }

        g.setColor(Color.red);
        g.fillRect(mx, my + item *FONT_HEIGHT, LEFT_OFFSET, FONT_HEIGHT);
      }
    }

    //call this when the up key is pressed
    public void goUp(){
        SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/menutick.wav");
        if(item > 0)item--;
        else item = numItems-1;
    }

    //call this when the down key is pressed
    public void goDown(){
        SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/menutick.wav");
        if(item < numItems-1)item++;
        else item = 0;
    }

    //used by the mouse routines, returns the menu item at that location
    //-1 = not on menu
    public int getMenuItemAt(int x, int y, int scale) {
      if (x / scale > mx && x / scale < mx + width) {
        if (y / scale > my && y / scale < my + numItems * FONT_HEIGHT) {
          return (y / scale - my) / FONT_HEIGHT;
        }
        return -1;
      }
      return -1;
    }

    public void setMenuItem(int i){
        if(i<numItems)item = i;
    }

    //call this when an item is selected to do the selected action
    public abstract int doMenuItem();
}
