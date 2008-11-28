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
                //System.out.println(displayItems[i]);

                if(((i>=9 && i<=14)|| i == 17 | i == 16)) 
                {
                    if(i == 16)
                    {
                        g.drawImage(TerrainGraphics.getUrbanSpriteSheet(0), mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, 0, 0, 16, 32, screen);
                    } 
                    else if(i == 17) 
                    {
                        g.drawImage(TerrainGraphics.getUrbanSpriteSheet(side), mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, 8*16, 0, 9*16, 32, screen);
                    } 
                    else if(i == 9)
                    {
                        g.drawImage(TerrainGraphics.getHQSpriteSheet(side), mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (side-1)*16, 0, (side)*16, 32, screen);
                    } 
                    else
                    {
                        g.drawImage(TerrainGraphics.getUrbanSpriteSheet(side), mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (i-7)*16, 0, (i-6)*16, 32, screen);
                    }
                } 
                else 
                {
                    Image temp = TerrainGraphics.getTerrainSpriteSheet();
                    
                    if(i<4) 
                    { 	//Plain, Wood, Mountain
                    	g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (i)*16, 0, (i+1)*16, 32, screen);
                    }
                    else if(i == 4)
                    { 	//Bridge
                    	g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (14)*16, 0, (15)*16, 32, screen);
                    }
                    else if(i == 20)
                    {
                    	//Put suspension code here
                    	g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (16)*16, 0, (17)*16, 32, screen);
                    }
                    else if(i == 5)
                    { 	//River
                    	g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (18)*16, 0, (19)*16, 32, screen);
                    }
                    else if (i == 8)
                    { 	//Shoal
                    	g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (33)*16, 0, (34)*16, 32, screen);
                    }
                    else if (i == 6)
                    { 	//Sea
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (72)*16, 0, (73)*16, 32, screen);
                    }
                    else if (i == 7)
                    { 	//Reef
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (71)*16, 0, (72)*16, 32, screen);
                    }
                    else if (i == 15)
                    { 	//Pipe
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (103)*16, 0, (104)*16, 32, screen);
                    }
                    else if (i == 18)
                    { 	//PipeSeam
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (118)*16, 0, (119)*16, 32, screen);
                    }
                    else if (i == 19)
                    { 	//Destroyed Seam
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (120)*16, 0, (121)*16, 32, screen);
                    }
                    // ****NEW STUFF****
                    else if (i == TerrType.WALL)
                    { 	//Wall
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (122)*16, 0, (123)*16, 32, screen);
                    }
                    else if (i == TerrType.DEST_WALL)
                    { 	//Destroyed Wall
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (124)*16, 0, (125)*16, 32, screen);
                    }
                    // ****NEW STUFF****
                    else if (i == TerrType.SEA_PIPE)
                    { 	//Pipe
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (126)*16, 0, (127)*16, 32, screen);
                    }
                    else if (i == TerrType.SP_SEAM)
                    { 	//PipeSeam
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (141)*16, 0, (142)*16, 32, screen);
                    }
                    else if (i == TerrType.DEST_SPS)
                    { 	//Destroyed Seam
                        g.drawImage(temp, mx+16, my+((i-currentPosition)*16)-16, mx+32, my+((i-currentPosition)*16)+16, (143)*16, 0, (144)*16, 32, screen);
                    }
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
