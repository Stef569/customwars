package cwsource;
/*
 *UnitMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 20, 2006, 11:12 AM
 *A menu in the map editor. Used to select Units
 */

import java.awt.*;
import java.awt.image.*;

public class UnitMenu extends ScrollMenu{
    
    private int side = 0;
    public UnitMenu(int sid,ImageObserver screen){
        //super((480-96)/2,(320-80)/2,96,screen,8);
        super((480-96)/2,0,96,screen,20);
        side = sid;
        String[] s = {"Infantry","Mech","Tank","Md Tank","Recon","Anti-Air","Missiles","Artillery","Rockets","APC","Lander","Cruser","Sub","Battleship","T Copter","B Copter","Fighter","Bomber","Neotank","Megatank","Piperunner","Black Boat","Carrier","Stealth","Black Bomb","Bcraft","Acraft","Shuttlerunner","Zeppelin","Spyplane","Destroyer","Oozium"};
        super.loadStrings(s);
    }
    
    public void drawMenu(Graphics2D g){
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f)); 
        g.setColor(Color.gray); 
        g.fillRoundRect(mx,my,width,maxItems*16+2,16,16); 
        g.setComposite(AlphaComposite.SrcOver);
        
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD,16));
        for(int i=currentPosition; i<maxItems+currentPosition; i++){
            int zx = mx+16;
            int zy = my+((i-currentPosition)*16);
            int ypos = UnitGraphics.findYPosition(i,side);
            g.drawImage(UnitGraphics.getUnitImage(i,side),zx,zy,zx+16,zy+16,0,ypos,16,ypos+16,screen);
            g.drawString(displayItems[i],mx+32,my+((i-currentPosition)*16)+16);
        }
        
        g.drawImage(MiscGraphics.getPointer(),mx,my+item*16,screen); 
    }
    
    public int doMenuItem(){
        return item;
    }
}