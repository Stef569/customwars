package cwsource;
/*
 *ScrollMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 19, 2006, 4:24 PM
 *An abstract in-game menu that scrolls. Used to make very large battle menus.
 */

import java.awt.*;
import java.awt.image.*;

public abstract class ScrollMenu extends InGameMenu{
    /*protected String[] displayItems;
    private int numItems = 0;
    protected int item;
    private int mx;
    private int my;*/
    protected int maxItems;           //The maximum items allowed on the sceen at one time
    protected int currentPosition;    //The top item in the menu
    
    //constructor
    public ScrollMenu(int x, int y, int width, ImageObserver screen, int maxItems){
        super(x,y,width,screen);
        this.maxItems = maxItems;
        currentPosition = 0;
    }
    
    //YOU MUST CALL THIS IN AN IMPLEMENTING CONSTRUCTOR, loads the strings
    protected void loadStrings(String[] s){
        super.loadStrings(s);
        if(numItems < maxItems)maxItems = numItems;
        icons = null;
    }
    
    //YOU MUST CALL THIS IN AN IMPLEMENTING CONSTRUCTOR, loads the strings (and icons)
    protected void loadStrings(String[] s, Image[] i){
        super.loadStrings(s);
        if(numItems < maxItems)maxItems = numItems;
        icons = i;
    }
    
    //Draws the Menu
    public void drawMenu(Graphics2D g){
        if(icons == null){
            g.setColor(Color.gray);
            g.fillRect(mx,my,width,maxItems*16+2);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            for(int i=currentPosition; i<maxItems+currentPosition; i++){
                g.drawString(displayItems[i],mx+16,my+((i-currentPosition)*16)+16);
            }
            
            g.setColor(Color.red);
            g.fillRect(mx,my+(item-currentPosition)*16,16,16);
        }else{
            g.setColor(Color.gray);
            g.fillRect(mx,my,width,maxItems*16+2);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            for(int i=currentPosition; i<maxItems+currentPosition; i++){
                g.drawImage(icons[i],mx+16,my+((i-currentPosition)*16)-(icons[i].getHeight(screen)-16),screen);
                g.drawString(displayItems[i],mx+32,my+((i-currentPosition)*16)+16);
            }
            
            g.setColor(Color.red);
            g.fillRect(mx,my+(item-currentPosition)*16,16,16);
        }
    }
    
    //call this when the up key is pressed
    public void goUp(){
        super.goUp();
        if(item < currentPosition)currentPosition--;
        if(item == numItems - 1){
            currentPosition = numItems - maxItems;
        }
    }
    
    //call this when the down key is pressed
    public void goDown(){
        super.goDown();
        if(item-currentPosition >= maxItems)currentPosition++;
        if(item == 0){
            currentPosition = 0;
        }
    }
    
    //used by the mouse routines, returns the menu item at that location
    //-1 = not on menu, -2 = scroll up, -3 = scroll down
    public int getMenuItemAt(int x, int y, int scale){
        if(x/scale > mx && x/scale < mx+width){
            if(y/scale > my && y/scale < my+maxItems*16){
                return (y/scale-my)/16+currentPosition;
            }
            if(y/scale < my && y/scale > my-16)return -2;
            if(y/scale > my+maxItems*16-16 && y/scale < my+maxItems*16+16)return -3;
            //if(y > my+numItems*16)return -3;
            return -1;
        }
        return -1;
    }
    
    //call this when an item is selected to do the selected action
    //public abstract int doMenuItem();
}