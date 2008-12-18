package com.customwars;

import java.awt.*;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.customwars.unit.Unit;
import com.customwars.unit.UnitGraphics;

public abstract class CWScreen extends JComponent implements ComponentListener
{
	protected int DEF_TILEW = 30;   //BattleScreen width was 16 tiles
    protected int DEF_TILEH = 20;   //BattleScreen height was 12 tiles
    protected int MAX_TILEW = DEF_TILEW;   //Screen width in tiles
    protected int MAX_TILEH = DEF_TILEH;   //Screen height in tiles
    protected static final int MAX_ARMIES = 10;   //The max allowed armies is 10
    
	protected Battle b;
	protected Map map;
	protected Unit selected;      //holds the currently selected unit
	protected int cursorXpos;             //holds the cursor's x position on the map (in map tiles)
	protected int cursorYpos;             //holds the cursor's y position on the map (in map tiles)
	protected int sx;             //holds the battle screen's x position over the map (in pixels)
	protected int sy;             //holds the battle screen's y position over the map (in pixels)

	//protected BSKeyControl keycontroller;   //the KeyControl, used to remove the component
	//protected BSMouseControl mousecontroller;//the MouseControl, used to remove the component
	
	protected BufferedImage bimg; //the screen, used for double buffering and scaling
	protected int scale;          //what scale multiplier is being used
	protected JFrame parentFrame;  //the frame that contains the window
	
	protected int terrBox_x;
	protected int terrBox_y;
    
	protected int unitBox_x;
	protected int unitBox_y;
    
	protected int trnsBox_x;
	protected int trnsBox_y;
	
	protected CWScreen(Battle b, JFrame f)
	{
        //makes the panel opaque, and thus visible
        this.setOpaque(true);
        
        this.b = b;
        this.map = b.getMap();
        selected = null;
        cursorXpos = 0;
        cursorYpos = 0;
        sx = 0;
        sy = 0;
        
        //center small maps
        if(map.getMaxCol() < 30)
        {
            sx = -((30 - map.getMaxCol())/2)*16;
        }
        
        if(map.getMaxRow() < 20)
        {
            sy = -((20 - map.getMaxRow())/2)*16;
        }

        scale = 1;
        
        /*
        //KeyControl is registered with the parent frame
        keycontroller = kc;
        f.addKeyListener(keycontroller);
        
        //MouseControl is registered with the parent frame
        mousecontroller = mc;
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
        */
        
        parentFrame = f;
        this.addComponentListener(this);
	}

    
    //called in response to this.repaint();
    public void paintComponent(Graphics g) 
    {
        //clears the background
        super.paintComponent(g);
        
        //converts to Graphics2D
        Dimension d = getSize();
        Graphics2D g2 = createGraphics2D(d.width, d.height);
        g2.scale(scale,scale);
        
        drawScreen(g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }
   
    //makes a Graphics2D object of the given size
    public final Graphics2D createGraphics2D(int w, int h) 
    {
        Graphics2D g2 = null;
        
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) 
        {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    } 
    
    //Draws the map and the units
    public  void drawMap(Graphics2D g)
    {
        for(int x = sx / 16; x < map.getMaxCol(); x++)
        {
            for(int y = sy / 16; y < map.getMaxRow(); y++)
            {
                drawTerrainOnMap(g, x, y);
                drawUnitOnMap(g, x, y);
            }
        }
    }

	public final void drawTerrainOnMap(Graphics2D g, int x, int y) 
	{
		Tile currTile = map.find(new Location(x,y));
		CWArtist.drawTerrainAtXY(g, this, currTile, x*16-sx, y*16-sy);
	}

	public void drawUnitOnMap(Graphics2D g, int x, int y) 
	{
		//if a unit is there, draw it
		if(visibleAtXY(x, y))
		{
			Unit thisUnit = map.find(new Location(x,y)).getUnit();

			//Spacing issues I can't bother to fix at the moment. <_<
		    if(thisUnit == selected && selected.getDirection() != -1 && selected.getMType() >1)
		    {
		        switch(selected.getDirection()) 
		        {
		            case 0: //north
		                
		                g.drawImage(UnitGraphics.getNorthImage(thisUnit),x*16-sx-4,y*16-sy-6, x*16-sx+16-4+8,y*16-sy+16+8-6,  0,UnitGraphics.findYPosition(thisUnit)/16*24,24,UnitGraphics.findYPosition(thisUnit)/16*24+24,this);
		                
		                break;
		            case 1: //east
		                g.drawImage(UnitGraphics.getEastImage(thisUnit),x*16-sx-4,y*16-sy-2,x*16-sx+16+8-4,y*16-sy+16-2+8,0,UnitGraphics.findYPosition(thisUnit)/16*24,24,UnitGraphics.findYPosition(thisUnit)/16*24+24,this);
		                
		                break;
		            case 2: //south
		                g.drawImage(UnitGraphics.getSouthImage(thisUnit),x*16-sx-4, y*16-sy-6,  x*16-sx+16+8-4,  y*16-sy+16+8-6,  0,UnitGraphics.findYPosition(thisUnit)/16*24,24,UnitGraphics.findYPosition(thisUnit)/16*24+24,this);
		                
		                break;
		            case 3: //west
		                g.drawImage(UnitGraphics.getWestImage(thisUnit),x*16-sx-4,y*16-sy-2,x*16-sx+16+8-4,y*16-sy+16-2+8,0,UnitGraphics.findYPosition(thisUnit)/16*24,24,UnitGraphics.findYPosition(thisUnit)/16*24+24,this);
		                
		                break;
		        }
		    }
		    else 
		    {
		    	CWArtist.drawUnitAtXY(g, this, thisUnit.getUType(), thisUnit.getArmy().getColor(), x*16-sx, y*16-sy);
		    }
		    
		    CWArtist.drawUnitStatus(g, this, thisUnit, x*16-sx,y*16-sy);
		}
	}
    
    public final void drawInfoBox(Graphics2D g) 
    {
        Tile currTile = map.find(new Location(cursorXpos,cursorYpos));
        
        drawTerrainInfoBox(g, currTile);
        drawUnitInfoBox(g, currTile);
    }

	public final void drawUnitInfoBox(Graphics2D g, Tile currTile) 
	{
		//Unit box
        //The following visibility evaluation method be working
        if(visibleAtXY(cursorXpos, cursorYpos))
        {
            CWArtist.drawUnitInfo(g, this, currTile, unitBox_x, unitBox_y);
            CWArtist.drawTransInfoBox(g, this, currTile.getUnit(), trnsBox_x, trnsBox_y);
        }
	}

	public final void drawTerrainInfoBox(Graphics2D g, Tile currTile) 
	{
        //Terrain box
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g.setColor(new Color(5,46,68));
        g.fillRect(terrBox_x, terrBox_y, 32, 56);
        g.setComposite(AlphaComposite.SrcOver);
        
        // *******Draw Terrain Tile Here!*******
        //g.drawImage(temp, terrBox_x + 8, terrBox_y - 6, terrBox_x + 8 + 16, terrBox_y + 24, spriteX1, spriteY1, spriteX2, spriteY2, this);
        CWArtist.drawTerrainAtXY(g, this, currTile, terrBox_x + 8, terrBox_y + 12);
        
        CWArtist.drawTerrainInfo(g, this, currTile, terrBox_x, terrBox_y);
	}
    
    //tells the GUI what size the window is
    public Dimension getPreferredSize()
    {
        return new Dimension(16*MAX_TILEW*scale,16*MAX_TILEH*scale);
    }
    
	public final void setBattle(Battle b)
	{
		this.b = b;
	}
	
	public final Battle getBattle()
	{
		return b;
	}
	
	public final void setMap(Map m)
	{
		this.map = m;
	}
	
	public final Map getMap()
	{
		return map;
	}
	
	public final Location getCursorLoc()
	{
		return new Location(cursorXpos, cursorYpos);
	}
	
	public final Unit getSelectedUnit()
	{
		return selected;
	}
	
	public final boolean visibleAtXY(int x, int y)
    {                   	  
    	//If there is no unit at the given location, then nothing should be displayed
    	if(map != null && map.onMap(x, y) && map.find(new Location(x,y)).getUnit() != null)
    	    	{
    		Unit thisUnit = map.find(new Location(x,y)).getUnit();
    		
    		//Do not display the unit if it is moving
    		if(!thisUnit.isMoving() )
    		{
	    		//If the current analyzed unit is allied to the current player, it should be
	    		//visible no matter what the conditions are like
	    		if(thisUnit.getArmy().getSide() == b.getArmy(b.getTurn()).getSide())
	    		{
	    			return true;
	    		}
	    		//Otherwise, more conditions need to be checked
	    		else
	    		{
	    			//Check FoW based conditions
    				//Check if the current unit is 'hidden'
    				//If it is not hidden, it should be displayed
	    			if(b.isFog() && !thisUnit.isHidden())
	    			{
	    					return true;
	    			}
	    			//Check for MoW based conditions
    				//Check if the current unit is 'dived' or if the unit has been 
	    			//detected. If it is not dived, or it has been detected, it should 
	    			//be displayed
	    			else if(b.isMist() && (!thisUnit.isDived() || thisUnit.isDetected()))
	    			{
	    					return true;
	    			}
	    			//Check for clear conditions
    				//Check if the current unit is 'hidden'
    				//If it is not hidden, it should be displayed
	    			else if(!thisUnit.isHidden())
	    			{
	    				return true;
	    			}
	    		}
    		}
    	}
    	
    	return false;
    }

    /** <code>SETINFOBOXXYS</code> <p>
     * Sets the (x, y) coordinates of the info boxes, depending on where the cursor is
     * currently on the screen. If the cursor is half-way or more toward the right side
     * of the screen, the info boxes will be set to display on the left side. Otherwise,
     * the info boxes will be set to display on the right side.<p>
     * Currently this method is being called in four circumstances: <br>
     * (a) When the <code>BattleScreen</code> object is first initialized <br>
     * (b) When the left key is pressed <br>
     * (c) When the right key is pressed <br>
     * (d) When the mouse is moved <br>
     */
	public final void setInfoBoxXYs() 
	{
		//So if the cursor's x coordinate is far enough to the right,
		//print the boxes to the left side.
		if(cursorXpos > (MAX_TILEW/2)+(sx/16))
		{
			//Define the top left-most corner of the terrain box
			terrBox_x = 0;
			terrBox_y = 16*MAX_TILEH-56;
			
			//Define the top left-most corner of the unit box
			unitBox_x = 32;
			unitBox_y = 16*MAX_TILEH-56;
			
			//Define the top left-most corner of the trans box
			trnsBox_x = 64;
			trnsBox_y = 16*MAX_TILEH-56;
		}
		//Otherwise print the boxes to the right side.
		else
		{
    		//Define the top left-most corner of the terrain box
    		terrBox_x = 16*MAX_TILEW-32;
    		terrBox_y = 16*MAX_TILEH-56;
    		
    		//Define the top left-most corner of the unit box
    		unitBox_x = 16*MAX_TILEW-64;
    		unitBox_y = 16*MAX_TILEH-56;
    		
    		//Define the top left-most corner of the trans box
    		trnsBox_x = 16*MAX_TILEW-96;
    		trnsBox_y = 16*MAX_TILEH-56;
		}
	}
    
    public final void drawMiniMap(Graphics2D g, int x, int y){
        Image minimap = MiscGraphics.getMinimap();
        Map map = this.map;
        for(int i=0; i < map.getMaxCol(); i++){
            for(int j=0; j < map.getMaxRow();j++){
                //draw terrain
                int terraintype = map.find(new Location(i,j)).getTerrain().getIndex();
                if(terraintype < 9){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0+(terraintype*4),0,4+(terraintype*4),4,this);
                }else if(terraintype == 9){
                    int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getOwner().getColor();
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,36+(armycolor*4),0,40+(armycolor*4),4,this);
                }else if(terraintype < 15 || terraintype == 17){
                    int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getColor();
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,76+(armycolor*4),0,80+(armycolor*4),4,this);
                }else if(terraintype == 15 || terraintype == TerrType.SEA_PIPE){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,120,0,124,4,this);
                }else if(terraintype == 16){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,128,0,132,4,this);
                }else if(terraintype == 18 || terraintype == TerrType.SP_SEAM){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,124,0,128,4,this);
                }else if(terraintype == 19 || terraintype == TerrType.DEST_SPS){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0,0,4,4,this);
                }
                
                
                //draw units
                if(map.find(new Location(i,j)).hasUnit()){
                    int armycolor = map.find(new Location(i,j)).getUnit().getArmy().getColor();
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,132+(armycolor*4),0,136+(armycolor*4),4,this);
                }
            }
        }
    }

    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e){}
    public void componentResized(ComponentEvent e) // Called whenever the container frame is resized
    {
        Dimension PanelSize = getSize();
        //Takes the window size, divides by tile size, rounds up
        MAX_TILEW = (int)(Math.ceil(PanelSize.getWidth() /(16 * scale)));
        MAX_TILEH = (int)(Math.ceil(PanelSize.getHeight() / (16 * scale)));
        if(MAX_TILEW > map.getMaxCol()) //Frame cant be wider than map
        {
            MAX_TILEW = map.getMaxCol();
        }
        if(MAX_TILEH > map.getMaxRow()) //Frame cant be shorter than map
        {
            MAX_TILEH = map.getMaxRow();
        }
        if(MAX_TILEW < 30) //Maintain minimum width
        {
            MAX_TILEW = 30;
        }
        if(MAX_TILEH < 20) //Maintain minimum height
        {
            MAX_TILEH = 20;
        }
        //Rounds up the screen size itself to whole tiles
        int sizex = MAX_TILEW * (16 * scale);
        int sizey = MAX_TILEH * (16 * scale);
        if(sx/16+MAX_TILEW > map.getMaxCol() || sy/16+MAX_TILEH > map.getMaxRow()){
            sx = 0;
            sy = 0;
            cursorXpos = 0;
            cursorYpos = 0;
            //center small maps
            if(map.getMaxCol() < 30){
                sx = -((30 - map.getMaxCol())/2)*16;
            }
            if(map.getMaxRow() < 20){
                sy = -((20 - map.getMaxRow())/2)*16;
            }
        }
        setPreferredSize(new Dimension(sizex, sizey));
        parentFrame.pack();
    }

    public abstract void drawScreen(Graphics2D g);
    public abstract void drawCursor(Graphics2D g);   
}
