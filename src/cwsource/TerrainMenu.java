package cwsource;
/*
 *TerrainMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 20, 2006, 9:39 AM
 *A menu in the map editor. Used to select Terrain
 */

import java.awt.*;
import java.awt.image.*;

public class TerrainMenu extends ScrollMenu{
    int side;
    public TerrainMenu(int side, ImageObserver screen)
    {
        //super((480-96)/2,(320-80)/2,96,screen,8);
        super((480-96)/2,0,96,screen,20);
        this.side = side;
        
        String[] s = {"Plain","Wood","Mountain","Road","Bridge","River","Sea","Reef","Shoal","HQ","City","Base","Airport","Port","ComTower","Pipeline","Silo","Pipestation","Pipe Seam","Destroyed Pipe Seam","Suspension Bridge",
        		      "Wall", "Destroyed Wall", "Sea Pipe", "Sea Pipe Seam", "Destroyed Sea Pipe Seam"};
        
        Image[] icons = new Image[26];
        //Image[] icons = new Image[s.length];
        
        for(int i=0; i < 26; i++)
        //for(int i=0; i < s.length; i++)
        {
            icons[i] = TerrainGraphics.getTerrainImage(i,side);
        }
        super.loadStrings(s,icons);
    }
    
    public void drawMenu(Graphics2D g){
        if(icons == null)
        {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f)); 
	        g.setColor(Color.gray); 
	        g.fillRoundRect(mx,my,width,maxItems*16+2,16,16); 
	        g.setComposite(AlphaComposite.SrcOver);
	            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            
            for(int i=currentPosition; i<maxItems+currentPosition; i++)
            {
                g.drawString(displayItems[i],mx+16,my+((i-currentPosition)*16)+16);
            }
            
            g.drawImage(MiscGraphics.getPointer(),mx,my+item*16,screen); 
        }
        else
        {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f)); 
	        g.setColor(Color.gray); 
	        g.fillRoundRect(mx,my,width,maxItems*16+2,16,16); 
	        g.setComposite(AlphaComposite.SrcOver);
	            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD,16));
            
            for(int i = currentPosition; i < maxItems + currentPosition; i++)
            {
                if((i>=9 && i<=14)|| i == 17) 
                {
                    //use the colored sprite sheet for properties
                    Image temp = TerrainGraphics.getColoredSheet(side);
                    g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, 0, TerrType.getYIndex(i), 16, TerrType.getYIndex(i) + 32, screen);
                    
                } 
                else 
                {
                    // the uncolored sprite sheet will work
                    Image temp = TerrainGraphics.getSpriteSheet();
                    g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, 0, TerrType.getYIndex(i), 16, TerrType.getYIndex(i) + 32, screen);
                }
                    
                g.drawString(displayItems[i],mx+32,my+((i-currentPosition)*16)+16);
            }
            
            g.drawImage(MiscGraphics.getPointer(),mx,my+item*16,screen); 
        }
    }
    
    public int doMenuItem(){
        return item;
    }
}
