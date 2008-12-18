package com.customwars.ui.menu;
/*
 *InGameMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 14, 2006, 1:40 AM
 *An abstract in-game menu. Used to make the battle menu and context menus.
 */

import java.awt.*;
import java.awt.color.*;
import java.awt.GradientPaint;
import java.awt.image.*;

import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleScreen;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.SwitchGradient;

public abstract class InGameMenu {
    protected String[] displayItems;
    protected Image[] icons;
    protected int numItems = 0;
    protected int item;
    protected int mx;
    protected int my;
    protected int width;
    ImageObserver screen;
    
    //constructor
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
    
    //Draws the Menu
    public void drawMenu(Graphics2D g){
        Graphics2D g2 = (Graphics2D)g;
        if(icons == null){
            //Makes the gradient
            GradientPaint gp = new GradientPaint(0,0,Color.RED,0,0,Color.BLUE);
            if(screen instanceof BattleScreen)
            gp = SwitchGradient.getGradient(((BattleScreen)screen).getBattle().getArmy(((BattleScreen)screen).getBattle().getTurn()).getColor());
            else
            gp = SwitchGradient.getGradient(8);
            // Fill with a gradient.
            g2.setPaint(gp);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.fillRoundRect(mx,my,width,numItems*16+2,16,16);
            g.setComposite(AlphaComposite.SrcOver);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            for(int i=0; i<numItems; i++){
                g.drawString(displayItems[i],mx+16,my+(i*16)+16);
            }
            
            g.drawImage(MiscGraphics.getPointer(),mx,my+item*16,screen);
            //g.setColor(Color.red);
            //g.fillRoundRect(mx,my+item*16,16,16,16,16);
        }else{
            g.setColor(Color.gray);
            g.fillRect(mx,my,width,numItems*16+2);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            for(int i=0; i<numItems; i++){
                g.drawImage(icons[i],mx+16,my+(i*16)-(icons[i].getHeight(screen)-16),screen);
                g.drawString(displayItems[i],mx+32,my+(i*16)+16);
            }
            
            g.setColor(Color.red);
            g.fillRect(mx,my+item*16,16,16);
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
    public int getMenuItemAt(int x, int y, int scale){
        if(x/scale > mx && x/scale < mx+width){
            if(y/scale > my && y/scale < my+numItems*16){
                return (y/scale-my)/16;
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
