package com.customwars;
/*
 *MapEditor.java
 *Author: Urusan
 *Contributors:
 *Creation: July 19, 2006, 2:38 PM
 *Used to make .map files for use with CW
 */
import java.awt.*;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Random;

//public class MapEditor extends JComponent implements ComponentListener
public class MapEditor extends CWScreen
{
	private int item;           //holds the menu's current item (both menus use this)
    private boolean menu;       //is the main menu in use?
    private boolean tmenu;      //is the terrain menu in use?
    private boolean umenu;      //is the unit menu in use?
    private boolean smenu;      //is the side menu in use?
    private boolean terrain;    //is the editor in terrain mode?
    private boolean unit;       //is the editor in unit mode?
    private boolean constantMode;   //is "constant mode" on?
    private boolean minimap;    //is the minimap on?
    private boolean showCursor;
	final static Logger logger = LoggerFactory.getLogger(MapEditor.class); 
    //private boolean fatMenu;
    private InGameMenu currentMenu = null; //contains the active menu (null if no menus are active)

    private Army selectedArmy;  //the selected army
    private Terrain selectedTerrain;    //the kind of terrain to be used by the editor
    private int unitType;       //The current unit type
    private String mapFilename; //The filename of the map
    private MapKeyControl keycontroller;   //the MapKeyControl, used to remove the component
    private MapMouseControl mousecontroller;   //the MouseControl, used to remove the component
    private int noScroll = 0;                  //don't scroll for this many more mouse commands
    
    /** Creates a new instance of BattleScreen */
    public MapEditor(Battle b, JFrame f)
    {
    	super(b, f);
    	
        //makes the panel opaque, and thus visible
        this.setOpaque(true);
        
        selectedArmy = null;
        selectedTerrain = new Plain();

        item = 0;
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        mapFilename = "temp.map";
        
        showCursor = true;
        //fatMenu = false;

        //KeyControl is registered with the parent frame
        keycontroller = new MapKeyControl(this);
        f.addKeyListener(keycontroller);
        //MouseControl is registered with the parent frame
        mousecontroller = new MapMouseControl();
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
    }
    
    //Draws the screen
    public void drawScreen(Graphics2D g)
    {
        //draws an animated gif in the background
        //this triggers repaint automatically
        //using repaint normally ruins animations
        g.drawImage(MiscGraphics.getMoveTile(),0,0,this);
        
        //draw a black background (if the map is smaller than the screen)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,480,320);
        
        drawMap(g);
        if(showCursor)drawCursor(g);
        
        if(/*!fatMenu &&*/ (menu||tmenu||umenu||smenu))currentMenu.drawMenu(g);
        
        if(minimap)drawMiniMap(g, 0, 0);
        
        //if(fatMenu)drawFatMenu(g);
        
        //causes problems with animated gifs
        //this.repaint();
    }

    /*
    //Draws the map and the units
    public void drawFatMenu(Graphics2D g)
    {
    	g.setColor(Color.black);
    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        g.fillRoundRect(7, 7, this.getWidth()-14, this.getHeight()-14, 16, 16);
        
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
        
        if(smenu)
        {
	        //draw side squares
	        
	        //Neutral
	        g.setColor(Color.gray);
	
	        //Orange Star
	        g.setColor(Color.red);
	
	        //Blue Moon
	        g.setColor(Color.blue);
	
	        //Green Earth
	        g.setColor(Color.green);
	
	        //Yellow Comet
	        g.setColor(Color.yellow);
	
	        //Black Hole
	        g.setColor(Color.black);
        }
        else if(tmenu)
        {
        	int maxRowSize = 16;
        	int bx = 10;
        	int by = 45;
        	int x = 0;
        	int y = 0;

    		Image temp;
        	//int terrID = 0;

        	for(int terrID = 0; terrID < TerrType.MAX_TERRAIN_TYPES; terrID++)
        	{
        		int spriteX1 = TerrType.getSearchIndex(b.getTerrain(terrID, selectedArmy, null));
        		int spriteX2 = spriteX1 + 16;
        		
        		temp = TerrType.getCorrectSheet(b.getTerrain(terrID, selectedArmy, null));
        		
        		g.drawImage(temp, bx + x*16-sx, by + y*16-sy-16, bx + (x+1)*16-sx, by + y*16-sy+16, spriteX1, 0, spriteX2, 32, this);
        		x++;
        		bx+=9;
        		
        		if(x > maxRowSize)
        		{
        			x = 0;
        			bx = 10;
        			y++;
        			by += 4;
        		}
        	}
        }
        else if(umenu)
        {
        	
        }
        
    	drawFMStrings(g);
        
        //sets... text back to normal?
    	g.setColor(Color.white);
    }

	private void drawFMStrings(Graphics2D g) 
	{
		String title = "";
		String left = "";
		String right = "";
		
		int rightx = 0;
		int leftx = 0;
		
		if(tmenu)
		{
			title = "Terrain Menu";
			left = "Side Menu";
			right = "Unit Menu";
			
			leftx = 23;
			rightx = this.getWidth() - 110;
		}
		else if(smenu)
		{
			title = "Side Menu";
			left = "Unit Menu";
			right = "Terrain Menu";
			
			leftx = 23;
			rightx = this.getWidth() - 130;
		}
		else if(umenu)
		{
			title = "Unit Menu";
			left = "Terrain Menu";
			right = "Side Menu";
			
			leftx = 23;
			rightx = this.getWidth() - 110;
		}
		
		//draw title string
		g.setColor(Color.white);
		g.setFont(new Font("SansSerif", Font.BOLD, 16));
		g.drawString(title, (this.getWidth()/2) - 50, 30);
		
		g.drawImage(MiscGraphics.getBigStar(5), 12, this.getHeight()-29, this);
		
		g.drawImage(MiscGraphics.getBigStar(5), this.getWidth() - 23, this.getHeight()-29, this);
		
		//draw left string
		g.setColor(Color.white);
		g.setFont(new Font("SansSerif", Font.BOLD, 16));
		g.drawString(left, leftx, this.getHeight()-19);
		
		//draw right string
		g.setColor(Color.white);
		g.setFont(new Font("SansSerif", Font.BOLD, 16));
		g.drawString(right, rightx, this.getHeight()-19);
	}
     */

	public void drawUnitOnMap(Graphics2D g, int x, int y) 
	{
		//if a unit is there, draw it
		if(visibleAtXY(x, y))
		{
			Unit thisUnit = map.find(new Location(x,y)).getUnit();

			CWArtist.drawUnitAtXY(g, this, thisUnit.getUType(), thisUnit.getArmy().getColor(), x*16-sx, y*16-sy);
		}
	}
    
    //Draws the cursor
    //Also draws out a faded image of the current selection
    public void drawCursor(Graphics2D g)
    {
    	if(unit && selectedArmy != null)
    	{
            drawCursorUnit(g);
    	}
    	else if(terrain)
    	{
    		drawCursorTerrain(g);
    	}
    	
        //g.setColor(Color.red);
        //g.drawRect(cx*16-sx,cy*16-sy,16,16);
        g.drawImage(MiscGraphics.getCursor(), cursorXpos*16-sx-7, cursorYpos*16-sy-7,this);
        
        if(constantMode)
        {
        	g.drawImage(MiscGraphics.getBigStar(5), cursorXpos*16-sx-7, cursorYpos*16-sy-7, this);
        	g.drawImage(MiscGraphics.getBigStar(5), cursorXpos*16-sx-7, cursorYpos*16-sy+11, this);
        	g.drawImage(MiscGraphics.getBigStar(5), cursorXpos*16-sx+12, cursorYpos*16-sy-7, this);
        }
    }

	private void drawCursorUnit(Graphics2D g)
	{
    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.drawImage(UnitGraphics.getUnitImage(unitType, selectedArmy.getID()),cursorXpos*16-sx,cursorYpos*16-sy,cursorXpos*16-sx+16,cursorYpos*16-sy+16,0,UnitGraphics.findYPosition(unitType, selectedArmy.getID()),16,UnitGraphics.findYPosition(unitType, selectedArmy.getID())+16,this);
        g.setComposite(AlphaComposite.SrcOver);
	}
	
	private void drawCursorTerrain(Graphics2D g)
	{
    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
    	
		Image temp;
		
        Terrain ter = selectedTerrain;
        
        int spriteX1 = TerrType.getXIndex(ter);
        int spriteX2 = spriteX1 + 16;
        
        if(ter instanceof Property) 
		{
		    if(((Property)ter).owner == null)
		        temp = TerrainGraphics.getColoredSheet(0);
		    else
		        temp = TerrainGraphics.getColoredSheet(((Property)ter).owner.getColor()+1);
		} 
		else
		{
		    temp = TerrainGraphics.getSpriteSheet();
		}
		
		g.drawImage(temp, cursorXpos*16-sx, cursorYpos*16-sy-16, (cursorXpos+1)*16-sx, cursorYpos*16-sy+16, spriteX1, 0, spriteX2, 32, this);
		
        g.setComposite(AlphaComposite.SrcOver);
	}
    
    //Fills the map with the selected terrain
    public void fillMap(){
        if(selectedTerrain.getIndex()!=8 && selectedTerrain.getIndex()!=9 && selectedTerrain.getIndex()!=18){
            Unit temp;
            for(int i=0; i < map.getMaxCol(); i++){
                for(int j=0; j < map.getMaxRow(); j++){
                    map.find(new Location(i,j)).setTerrain(selectedTerrain);
                    selectTerrain(selectedTerrain.getIndex());
                    temp = map.find(new Location(i,j)).getUnit();
                    if(temp != null){
                        map.remove(temp);
                        temp.getArmy().removeUnit(temp);
                    }
                }
            }
            map.initStyle();
        }
    }
    
    public void randomMap(){
        //set constants
        int PRI = 0;
        
        //clear map
        selectTerrain(PRI);
        fillMap();
        
        Random r = new Random();
        
        
        for(int i=0; i < 1000; i++){
            int num = r.nextInt(100);
            if(num < 30)num = 0;
            else if(num < 60)num = 1;
            else if(num < 70)num = 2;
            else if(num < 90)num = 6;
            else num = 10;
            
            int lim = 100;
            if(num==3 || num==10){
                //lim = r.nextInt(2)+1;
                lim = 1;
            }else if(num==0){
                lim = r.nextInt(40)+1;
            }else if(num==1){
                //lim = r.nextInt(3)+1;
                lim = 1;
            }else if(num==2){
                //lim = r.nextInt(3)+1;
                lim = 1;
            }else{
                lim = r.nextInt(70)+1;
            }
            
            if(num==6)createSeededArea(num, lim, true);
            else createSeededArea(num, lim, true);
        }
        
        //remember to apply the correct visual style
        map.initStyle();
    }
    
    public void createSeededArea(int SEC, int LIM, boolean cont){
        //seed ocean
        Random r = new Random();
        int numfill = 0;
        int r1 = r.nextInt(map.getMaxCol());
        int r2 = r.nextInt(map.getMaxRow());
        selectTerrain(SEC);
        map.find(new Location(r1,r2)).setTerrain(selectedTerrain);
        selectTerrain(SEC);
        numfill++;
        
        //fill out ocean
        boolean grid[][] = new boolean[map.getMaxCol()][map.getMaxRow()];
        for(int i=0; i<map.getMaxCol(); i++){
            for(int j=0; j<map.getMaxRow(); j++){
                if(map.find(new Location(i,j)).getTerrain().getIndex()==SEC){
                    grid[i][j]=true;
                }
            }
        }
        Tile node = map.find(new Location(r1,r2));
        int x = r1;
        int y = r2;
        while(node != null){
            if(map.onMap(x+1,y)){
                if(r.nextInt(2)==1){
                    map.find(new Location(x+1,y)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(map.onMap(x-1,y)){
                if(r.nextInt(2)==1){
                    map.find(new Location(x-1,y)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(map.onMap(x,y+1)){
                if(r.nextInt(2)==1){
                    map.find(new Location(x,y+1)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(map.onMap(x,y-1)){
                if(r.nextInt(2)==1){
                    map.find(new Location(x,y-1)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            grid[x][y] = true;
            if(numfill > LIM)break;
            
            boolean found = false;
            for(int i=0; i < map.getMaxCol(); i++){
                for(int j=0; j < map.getMaxRow(); j++){
                    if(!grid[i][j]){
                        if(map.find(new Location(i,j)).getTerrain().getIndex()==SEC){
                            node = map.find(new Location(i,j));
                            x = i;
                            y = j;
                            found = true;
                            break;
                        }
                    }
                }
                if(found)break;
            }
            
            if(!found)node = null;
        }
        
        //remove totally surrounded single tiles
        if(cont){
            for(int i=0; i < map.getMaxCol(); i++){
                for(int j=0; j < map.getMaxRow(); j++){
                    if(map.find(new Location(i,j)).getTerrain().getIndex()!=SEC){
                        if(!map.onMap(i+1,j) || map.find(new Location(i+1,j)).getTerrain().getIndex() == SEC){
                            if(!map.onMap(i-1,j) || map.find(new Location(i-1,j)).getTerrain().getIndex() == SEC){
                                if(!map.onMap(i,j+1) || map.find(new Location(i,j+1)).getTerrain().getIndex() == SEC){
                                    if(!map.onMap(i,j-1) || map.find(new Location(i,j-1)).getTerrain().getIndex() == SEC){
                                        map.find(new Location(i,j)).setTerrain(selectedTerrain);
                                        selectTerrain(SEC);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void mirrorHorizontal(){
        //create double width map
        int w = map.getMaxCol()*2;
        int h = map.getMaxRow();
        int oldw = map.getMaxCol();
        int oldh = map.getMaxRow();
        
        Map temp = new Map(w,h);
        temp.copyMap(map);
        
        map = temp;
        b = new Battle(map);
        
        //finalize copy
        selectedArmy = null;
        selected = null;
        cursorXpos = 0;
        cursorYpos = 0;
        sx = 0;
        sy = 0;
        //center small maps
        if(map.getMaxCol() < 30){
            sx = -((30 - map.getMaxCol())/2)*16;
        }
        if(map.getMaxRow() < 20){
            sy = -((20 - map.getMaxRow())/2)*16;
        }
        item = 0;
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        
        //mirror
        for(int j = 0; j < oldh; j++){
            for(int i = 0; i < oldw; i++){
                selectTerrain(map.find(new Location(i,j)).getTerrain().getIndex());
                map.find(new Location(w-i-1,j)).setTerrain(selectedTerrain);
            }
        }
        
        selectedTerrain = new Plain();
        map.initStyle();
    }
    
    public void mirrorVertical(){
        //create double height map
        int w = map.getMaxCol();
        int h = map.getMaxRow()*2;
        int oldw = map.getMaxCol();
        int oldh = map.getMaxRow();
        
        Map temp = new Map(w,h);
        temp.copyMap(map);
        
        map = temp;
        b = new Battle(map);
        
        //finalize copy
        selectedArmy = null;
        selected = null;
        cursorXpos = 0;
        cursorYpos = 0;
        sx = 0;
        sy = 0;
        //center small maps
        if(map.getMaxCol() < 30){
            sx = -((30 - map.getMaxCol())/2)*16;
        }
        if(map.getMaxRow() < 20){
            sy = -((20 - map.getMaxRow())/2)*16;
        }
        item = 0;
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        
        //mirror
        //mirror
        for(int j = 0; j < oldh; j++){
            for(int i = 0; i < oldw; i++){
                selectTerrain(map.find(new Location(i,j)).getTerrain().getIndex());
                map.find(new Location(i,h-j-1)).setTerrain(selectedTerrain);
            }
        }
        
        selectedTerrain = new Plain();
        map.initStyle();
    }
    
    //removes the component from the frame
    public void removeFromFrame(){
        parentFrame.getContentPane().remove(this);
        parentFrame.removeKeyListener(keycontroller);
        parentFrame.removeMouseListener(mousecontroller);
        parentFrame.removeMouseMotionListener(mousecontroller);
    }
    
    private void endBattle(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
    }
    
    //helper routine: places a terrain when enter is hit (or in constant mode)
    private void placeTile(){
        //erase any previous HQs of the same type
        if(selectedTerrain.getIndex() == 9){
            for(int i=0; i<map.getMaxCol(); i++){
                for(int j=0; j<map.getMaxRow(); j++){
                    if(map.find(new Location(i,j)).getTerrain().getIndex()==9){
                        if(((Property)map.find(new Location(i,j)).getTerrain()).getColor() == ((Property)selectedTerrain).getColor()){
                            map.find(new Location(i,j)).setTerrain(new Plain());
                        }
                    }
                }
            }
        }
        map.find(new Location(cursorXpos,cursorYpos)).setTerrain(selectedTerrain);
        selectTerrain(selectedTerrain.getIndex());
        map.initStyle();
    }
    
    //Select a Terrain
    private void selectTerrain(int type){
        switch(type){
            case 0:
                selectedTerrain = new Plain();
                break;
            case 1:
                selectedTerrain = new Wood();
                break;
            case 2:
                selectedTerrain = new Mountain();
                break;
            case 3:
                selectedTerrain = new Road();
                break;
            case 4:
                selectedTerrain = new Bridge();
                break;
            case 5:
                selectedTerrain = new River();
                break;
            case 6:
                selectedTerrain = new Sea();
                break;
            case 7:
                selectedTerrain = new Reef();
                break;
            case 8:
                selectedTerrain = new Shoal();
                break;
            case 9:
                if(selectedArmy != null)
                    selectedTerrain = new HQ(selectedArmy);
                break;
            case 10:
                if(selectedArmy == null)
                    selectedTerrain = new City();
                else
                    selectedTerrain = new City(selectedArmy);
                break;
            case 11:
                if(selectedArmy == null)
                    selectedTerrain = new Base();
                else
                    selectedTerrain = new Base(selectedArmy);
                break;
            case 12:
                if(selectedArmy == null)
                    selectedTerrain = new Airport();
                else
                    selectedTerrain = new Airport(selectedArmy);
                break;
            case 13:
                if(selectedArmy == null)
                    selectedTerrain = new Port();
                else
                    selectedTerrain = new Port(selectedArmy);
                break;
            case 14:
                if(selectedArmy == null)
                    selectedTerrain = new ComTower();
                else
                    selectedTerrain = new ComTower(selectedArmy);
                break;
            case 15:
                selectedTerrain = new Pipe();
                break;
            case 16:
                selectedTerrain = new Silo();
                break;
            case 17:
                if(selectedArmy == null)
                    selectedTerrain = new Pipestation();
                else
                    selectedTerrain = new Pipestation(selectedArmy);
                break;
            case 18:
                selectedTerrain = new PipeSeam();
                break;
            case 19:
                selectedTerrain = new DestroyedPipeSeam();
                break;
            case 20:
                selectedTerrain = new SuspensionBridge();
                break;
            case TerrType.WALL:
                selectedTerrain = new Wall();
                break;
            case TerrType.DEST_WALL:
                selectedTerrain = new DestroyedWall();
                break;
            case TerrType.SEA_PIPE:
                selectedTerrain = new SeaPipe();
                break;
            case TerrType.SP_SEAM:
                selectedTerrain = new SeaPipeSeam();
                break;
            case TerrType.DEST_SPS:
                selectedTerrain = new DestroyedSeaPipeSeam();
                break;
        }
    }
    
    //Places a new unit
    private void placeUnit(Map m, Tile t, int type){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        Army a = selectedArmy;
        
        if(a != null){
            switch(type){
                case 0:
                    new Infantry(x,y,a,m);
                    break;
                case 1:
                    new Mech(x,y,a,m);
                    break;
                case 2:
                    new Tank(x,y,a,m);
                    break;
                case 3:
                    new MDTank(x,y,a,m);
                    break;
                case 4:
                    new Recon(x,y,a,m);
                    break;
                case 5:
                    new AntiAir(x,y,a,m);
                    break;
                case 6:
                    new Missiles(x,y,a,m);
                    break;
                case 7:
                    new Artillery(x,y,a,m);
                    break;
                case 8:
                    new Rockets(x,y,a,m);
                    break;
                case 9:
                    new APC(x,y,a,m);
                    break;
                case 10:
                    new Lander(x,y,a,m);
                    break;
                case 11:
                    new Cruiser(x,y,a,m);
                    break;
                case 12:
                    new Submarine(x,y,a,m);
                    break;
                case 13:
                    new Battleship(x,y,a,m);
                    break;
                case 14:
                    new TCopter(x,y,a,m);
                    break;
                case 15:
                    new BCopter(x,y,a,m);
                    break;
                case 16:
                    new Fighter(x,y,a,m);
                    break;
                case 17:
                    new Bomber(x,y,a,m);
                    break;
                case 18:
                    new Neotank(x,y,a,m);
                    break;
                case 19:
                    new MegaTank(x,y,a,m);
                    break;
                case 20:
                    new Piperunner(x,y,a,m);
                    break;
                case 21:
                    new BlackBoat(x,y,a,m);
                    break;
                case 22:
                    new Carrier(x,y,a,m);
                    break;
                case 23:
                    new Stealth(x,y,a,m);
                    break;
                case 24:
                    new BlackBomb(x,y,a,m);
                    break;
                case 25:
                    new Battlecraft(x,y,a,m);
                    break;
                case 26:
                    new Artillerycraft(x,y,a,m);
                    break;
                case 27:
                    new Shuttlerunner(x,y,a,m);
                    break;
                case 28:
                    new Zeppelin(x,y,a,m);
                    break;
                case 29:
                    new Spyplane(x,y,a,m);
                    break;
                case 30:
                    new Destroyer(x,y,a,m);
                    break;
                case 31:
                    new Oozium(x,y,a,m);
                    break;
            }
        }
    }
    
    //writes new .map files
    public void saveMap(){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
        
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
        
        //find the armies on the map
        for(int i=0; i<map.getMaxCol(); i++){
            for(int j=0; j<map.getMaxRow(); j++){
                //check for owned properties
                if(map.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)map.find(new Location(i,j)).getTerrain()).getColor();
                    if(map.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
                
                //check for units
                if(map.find(new Location(i,j)).getUnit()!=null){
                    int color = map.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
        
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
        
        for(int i = 0; i < colors.length; i++)colors[i]--;
        
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
            
            //HEADER
            //version number
            write.writeInt(-1);
            //name
            write.writeBytes(map.getMapName());
            write.writeByte(0);
            //author
            write.writeBytes(map.getMapAuthor());
            write.writeByte(0);
            //description
            write.writeBytes(map.getMapDescription());
            write.writeByte(0);
            //Width
            write.writeInt(map.getMaxCol());
            //Height
            write.writeInt(map.getMaxRow());
            //Number of Armies
            write.writeByte(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeByte(colors[i]);
            
            //TERRAIN
            for(int i=0; i<map.getMaxCol(); i++){
                for(int j=0; j<map.getMaxRow(); j++){
                    ter = map.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeByte(ter.getIndex());
                    
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeByte(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeByte(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeByte(-1);
                    }
                }
            }
            
            //UNITS
            for(int i=0; i<map.getMaxCol(); i++){
                for(int j=0; j<map.getMaxRow(); j++){
                    uni = map.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeByte(uni.getUnitType());
                        //side
                        write.writeByte(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
            
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeByte(-10);
            for(int i = 0; i<map.getTriggers().size(); i++){
                write.writeInt(map.getTriggers().get(i).type);
                write.writeInt(map.getTriggers().get(i).day);
                write.writeInt(map.getTriggers().get(i).turn);
                switch(map.getTriggers().get(i).type)
                {
                    case 0:
                        //unit trigger
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).unitType);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).x);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).y);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).army);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).UnitHP);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).UnitFuel);
                        write.writeInt(((UnitTrigger)map.getTriggers().get(i)).UnitAmmo);
                        break;
                    case 1:
                        //damage trigger
                        write.writeInt(((DamageTrigger)map.getTriggers().get(i)).x);
                        write.writeInt(((DamageTrigger)map.getTriggers().get(i)).y);
                        write.writeInt(((DamageTrigger)map.getTriggers().get(i)).damage);
                        write.writeBoolean(((DamageTrigger)map.getTriggers().get(i)).destroy);
                }
            }
            //Signals end of Command List
            write.writeByte(-11);
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    //writes new .map files
    public static void saveMap(Map m, String mapFilename){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
        
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
        
        //find the armies on the map
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                //check for owned properties
                if(m.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)m.find(new Location(i,j)).getTerrain()).getColor();
                    if(m.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
                
                //check for units
                if(m.find(new Location(i,j)).getUnit()!=null){
                    int color = m.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
        
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
        
        for(int i = 0; i < colors.length; i++)colors[i]--;
        
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
            
            //HEADER
            //version number
            write.writeInt(-1);
            //name
            write.writeBytes(m.getMapName());
            write.writeByte(0);
            //author
            write.writeBytes(m.getMapAuthor());
            write.writeByte(0);
            //description
            write.writeBytes(m.getMapDescription());
            write.writeByte(0);
            //Width
            write.writeInt(m.getMaxCol());
            //Height
            write.writeInt(m.getMaxRow());
            //Number of Armies
            write.writeByte(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeByte(colors[i]);
            
            //TERRAIN
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    ter = m.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeByte(ter.getIndex());
                    
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeByte(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeByte(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeByte(-1);
                    }
                }
            }
            
            //UNITS
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    uni = m.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeByte(uni.getUnitType());
                        //side
                        write.writeByte(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
            
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeByte(-10);
            /*for(int i = 0; i<m.getTriggers().size(); i++){
                write.writeInt(m.getTriggers().get(i).type);
                write.writeInt(m.getTriggers().get(i).day);
                write.writeInt(m.getTriggers().get(i).turn);
                switch(m.getTriggers().get(i).type)
                {
                    case 0:
                        //unit trigger
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).unitType);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).army);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitHP);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitFuel);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitAmmo);
                        break;
                    case 1:
                        //damage trigger
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).damage);
                        write.writeBoolean(((DamageTrigger)m.getTriggers().get(i)).destroy);
                }
            }
            //Signals end of Command List
            write.writeByte(-11);*/
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    //writes old .map files
    /*public void saveMap(){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
     
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
     
        //find the armies on the map
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                //check for owned properties
                if(m.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)m.find(new Location(i,j)).getTerrain()).getColor();
                    if(m.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
     
                //check for units
                if(m.find(new Location(i,j)).getUnit()!=null){
                    int color = m.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
     
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
     
        for(int i = 0; i < colors.length; i++)colors[i]--;
     
        //DEBUG
        /*

        for(int i = 0; i < colors.length; i++)System.out.print(colors[i]);
     
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
     
            //HEADER
            //Width
            write.writeInt(m.getMaxCol());
            //Height
            write.writeInt(m.getMaxRow());
            //Number of Armies
            write.writeInt(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeInt(colors[i]);
     
            //TERRAIN
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    ter = m.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeInt(ter.getIndex());
     
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeInt(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeInt(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeInt(-1);
                    }
                }
            }
     
            //UNITS
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    uni = m.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeInt(uni.getUnitType());
                        //side
                        write.writeInt(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
     
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeInt(-10);
        }catch(IOException e){
            System.err.println(e);
        }
    }*/
    
    public void pressedA()
    {
        if(menu)
        {
            menuActions();
        }
        else if(tmenu)
        {
            terrainMenuActions();
        }
        else if(smenu)
        {
            armyMenuActions();
        }
        else if(umenu)
        {
            unitMenuActions();
        }
        else if(terrain)
        {
            placeTile();
        }
        else if(unit)
        {
            placeUnit(map,map.find(new Location(cursorXpos,cursorYpos)),unitType);
        }
    }

	private void unitMenuActions() 
	{
		umenu=false;
		unitType = currentMenu.doMenuItem();
		currentMenu = null;
		terrain = false;
		unit = true;
	}

	private void armyMenuActions() {
		smenu=false;
		int menuselection = currentMenu.doMenuItem();
		if(menuselection != 0)
		    selectedArmy = b.getArmy(menuselection-1);
		else
		    selectedArmy = null;
		selectTerrain(selectedTerrain.getIndex());
		currentMenu = null;
	}

	private void terrainMenuActions() {
		tmenu=false;
		int menuselection = currentMenu.doMenuItem();
		selectTerrain(menuselection);
		currentMenu = null;
		terrain = true;
		unit = false;
	}

	private void menuActions() {
		menu=false;
		int menuselection = currentMenu.doMenuItem();
		if(menuselection == 1){
		    if(scale < 4)
		        scale++;
		    else
		        scale = 1;
		    parentFrame.pack();
		}else if(menuselection == 2){
		    fillMap();
		}else if(menuselection == 3){
		    JFileChooser fc = new JFileChooser();
		    fc.setDialogTitle("Save Map");
		    if(new File("./maps/User Maps/").exists() && new File("./maps/User Maps/").isDirectory()){
		        fc.setCurrentDirectory(new File("./maps/User Maps/"));
		    }else{
		        fc.setCurrentDirectory(new File("./maps/"));
		    }
		    fc.setApproveButtonText("Save");
		    /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "CW and AWD Maps", "map", "awd");
		    FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
		            "AWD Maps", "awd");
		    FileNameExtensionFilter filter3 = new FileNameExtensionFilter(
		            "CW Maps", "map");
		    fc.setFileFilter(filter);
		    fc.setFileFilter(filter2);
		    fc.setFileFilter(filter3);*/
		    int returnVal = fc.showOpenDialog(this);
		    if(returnVal != 1){
		        String x = fc.getSelectedFile().getPath();
		        if(x.length()<5 || !x.substring(x.length()-4).equals(".map"))x += ".map";
		        mapFilename = x;
		        logger.info("mapfilename =" +mapFilename );
		        saveMap();
		    }
		}else if(menuselection == 4){
		    //load a map
		    JFileChooser fc = new JFileChooser();
		    fc.setDialogTitle("Load Map");
		    if(new File("./maps/User Maps/").exists() && new File("./maps/User Maps/").isDirectory()){
		        fc.setCurrentDirectory(new File("./maps/User Maps/"));
		    }else{
		        fc.setCurrentDirectory(new File("./maps/"));
		    }
		    fc.setApproveButtonText("Load");
		    /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "CW and AWD Maps", "map", "awd");
		    FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
		            "AWD Maps", "awd");
		    FileNameExtensionFilter filter3 = new FileNameExtensionFilter(
		            "CW Maps", "map");
		    fc.setFileFilter(filter3);
		    fc.setFileFilter(filter2);
		    fc.setFileFilter(filter);*/
		    int returnVal = fc.showOpenDialog(this);
		    
		    if(returnVal != 1){
		        String x = fc.getSelectedFile().getPath();
		        if(mapFilename.length()<5 || (!mapFilename.substring(mapFilename.length()-4).equals(".map") && !mapFilename.substring(mapFilename.length()-4).equals(".awd")))mapFilename += ".map";
		        logger.info(x);
		        File saveFile = new File(x);
		        if(saveFile.exists()){
		            mapFilename = x;
		            
		            b = new Battle(mapFilename);
		            map = b.getMap();
		            
		            selectedArmy = null;
		            selectedTerrain = new Plain();
		            selected = null;
		            cursorXpos = 0;
		            cursorYpos = 0;
		            sx = 0;
		            sy = 0;
		            //center small maps
		            if(map.getMaxCol() < 30){
		                sx = -((30 - map.getMaxCol())/2)*16;
		            }
		            if(map.getMaxRow() < 20){
		                sy = -((20 - map.getMaxRow())/2)*16;
		            }
		            item = 0;
		            unitType = 0;
		            menu = false;
		            tmenu = false;
		            umenu = false;
		            smenu = false;
		            terrain = true;
		            unit = false;
		            constantMode = false;
		            minimap = false;
		        }
		    }
		}else if(menuselection == 5){
		    int w = getPositiveInteger("Input the new map's width",1);
		    int h = getPositiveInteger("Input the new map's height",1);
		    
		    map = new Map(w,h);
		    b = new Battle(map);
		    
		    selectedArmy = null;
		    selectedTerrain = new Plain();
		    selected = null;
		    cursorXpos = 0;
		    cursorYpos = 0;
		    sx = 0;
		    sy = 0;
//center small maps
		    if(map.getMaxCol() < 30){
		        sx = -((30 - map.getMaxCol())/2)*16;
		    }
		    if(map.getMaxRow() < 20){
		        sy = -((20 - map.getMaxRow())/2)*16;
		    }
		    item = 0;
		    unitType = 0;
		    menu = false;
		    tmenu = false;
		    umenu = false;
		    smenu = false;
		    terrain = true;
		    unit = false;
		    constantMode = false;
		    minimap = false;
		    //mapFilename = "temp.map";
		}else if(menuselection == 6){
		    MAX_TILEW = getPositiveInteger("Enter a width in tiles for the screen",16);
		    MAX_TILEH = getPositiveInteger("Enter a height in tiles for the screen",12);
		    parentFrame.pack();
		}else if(menuselection == 7){
		    int w = getPositiveInteger("Input the new map's width",1);
		    int h = getPositiveInteger("Input the new map's height",1);
		    
		    Map temp = new Map(w,h);
		    temp.copyMap(map);
		    
		    map = temp;
		    b = new Battle(map);
		    
		    selectedArmy = null;
		    selectedTerrain = new Plain();
		    selected = null;
		    cursorXpos = 0;
		    cursorYpos = 0;
		    sx = 0;
		    sy = 0;
		    //center small maps
		    if(map.getMaxCol() < 30){
		        sx = -((30 - map.getMaxCol())/2)*16;
		    }
		    if(map.getMaxRow() < 20){
		        sy = -((20 - map.getMaxRow())/2)*16;
		    }
		    item = 0;
		    unitType = 0;
		    menu = false;
		    tmenu = false;
		    umenu = false;
		    smenu = false;
		    terrain = true;
		    unit = false;
		    constantMode = false;
		    minimap = false;
		    //mapFilename = "temp.map";
		}else if(menuselection == 8){
		    endBattle();
		}else if(menuselection == 9){
		    map.setMapName(JOptionPane.showInputDialog("Enter a name for the map",map.getMapName()));
		    map.setMapAuthor(JOptionPane.showInputDialog("Enter the author's name",map.getMapAuthor()));
		    map.setMapDescription(JOptionPane.showInputDialog("Enter a description for this map",map.getMapDescription()));
		}else if(menuselection == 10){
		    randomMap();
		}else if(menuselection == 11){
		    String sel = JOptionPane.showInputDialog("Horizontal Mirror or Vertical Mirror?\nType h or v.");
		    if(sel != null){
		        if(sel.equals("h")){
		            mirrorHorizontal();
		        }else if(sel.equals("v")){
		            mirrorVertical();
		        }
		    }
		}
		
		currentMenu = null;
	}
    
    public void pressedB(){
        if(menu || tmenu || umenu || smenu){
            menu = false;
            tmenu = false;
            umenu = false;
            smenu = false;
            currentMenu = null;
        }else{
            if(map.find(new Location(cursorXpos,cursorYpos)).getTerrain() instanceof Property){
                if(((Property)map.find(new Location(cursorXpos,cursorYpos)).getTerrain()).getOwner()!=null)
                    selectedArmy = b.getArmy(((Property)map.find(new Location(cursorXpos,cursorYpos)).getTerrain()).getOwner().getID());
                else
                    selectedArmy = null;
            }
            selectTerrain(map.find(new Location(cursorXpos,cursorYpos)).getTerrain().getIndex());
            terrain = true;
            unit = false;
        }
    }
    
    public int getPositiveInteger(String message, int lowerLimit){
        int num = 0;
        boolean valid = false;
        while(!valid){
            String x = JOptionPane.showInputDialog(message);
            try{
                num = Integer.parseInt(x);
                if(num >= lowerLimit)valid = true;
                else{
                    valid = false;
                    continue;
                }
            }catch(NumberFormatException exc){
                valid = false;
                continue;
            }
        }
        return num;
    }
    
    //This class deals with keypresses
    class MapKeyControl implements KeyListener{
        MapEditor parentScreen;
        
        public MapKeyControl(MapEditor m){
            super();
            parentScreen=m;
        }
        
        public void keyTyped(KeyEvent e) {}
        
        public void keyPressed(KeyEvent e)
        {
            int keypress = e.getKeyCode();
            
            //if(!fatMenu)
            {
	            if(keypress == Options.up)
	            {
	                keyUpActions();
	            }
	            else if(keypress == Options.down)
	            {
	                keyDownActions();
	            }
	            else if(keypress == Options.left)
	            {
	                keyLeftActions();
	            }
	            else if(keypress == Options.right)
	            {
	                keyRightActions();
	            }
	            else if(keypress == Options.akey)
	            {
	                pressedA();
	            }
	            else if(keypress == Options.constmode)
	            {
	                toggleConstantMode();
	            }
	            else if(keypress == Options.delete)
	            {
	                deleteActions();
	            }
	            else if(keypress == Options.tkey)
	            {
	                invokeTerrainMenu();
	            }
	            else if(keypress == Options.skey)
	            {
	                invokeSideMenu();
	            }
	            else if(keypress == Options.ukey)
	            {
	                invokeUnitMenu();
	            }
	            else if(keypress == Options.bkey)
	            {
	                pressedB();
	            }
	            else if(keypress == Options.menu)
	            {
	                if(selected == null){
	                    if(!tmenu && !umenu && !smenu){
	                        if(!menu){
	                            menu=true;
	                            currentMenu = new MapMenu(parentScreen);
	                        }else menu=false;
	                    }
	                }
	            }
	            else if(keypress == Options.minimap)
	            {
	                if(!minimap){
	                    minimap = true;
	                }else minimap=false;
	            }
	            /*
	            else if(keypress == Options.fat_editor_menu &&
	            		!tmenu &&
	            		!smenu &&
	            		!umenu)
	            {
            		fatMenu = true;
            		showCursor = false;
            		
            		if(!tmenu && !smenu && !umenu)
            			tmenu = true;
	            }
	            */
            }
            /*
            else
            {
            	if(keypress == Options.fat_editor_menu)
            	{
            		fatMenu = false;
            		showCursor = true;
            		
			        smenu=false;
			    	tmenu=false;
			    	umenu=false;
            	}
	            else if(keypress == Options.tkey)
	            {
				    if(!tmenu)
				    {
				        tmenu=true;
				        smenu=false;
				        umenu=false;
				    }
	            }
	            else if(keypress == Options.skey)
	            {
    			    if(!smenu)
    			    {
    			        smenu=true;
    			    	tmenu=false;
    			    	umenu=false;
    			    }
	            }
	            else if(keypress == Options.ukey)
	            {
    			    if(!umenu)
    			    {
    			        umenu=true;
    			        smenu=false;
    			        tmenu=false;
    			    }
	            }
            }
            */
        }

		private void invokeUnitMenu() 
		{
			if(!menu && !smenu && !tmenu)
			{
			    if(!umenu){
			        umenu=true;
			        if(selectedArmy!=null)
			            currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
			        else
			            currentMenu = new UnitMenu(0,parentScreen);
			    }else umenu=false;
			}
		}

		private void invokeTerrainMenu() 
		{
			if(!menu && !umenu && !smenu)
			{
			    if(!tmenu)
			    {
			        tmenu=true;
			        if(selectedArmy!=null)
			            currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
			        else
			            currentMenu = new TerrainMenu(0,parentScreen);
			    }else tmenu=false;
			}
		}

		private void invokeSideMenu() 
		{
			if(!menu && !umenu && !tmenu){
			    if(!smenu){
			        smenu=true;
			        currentMenu = new SideMenu(parentScreen);
			    }else smenu=false;
			}
		}

		private void deleteActions() 
		{
			Unit temp = map.find(new Location(cursorXpos,cursorYpos)).getUnit();
			if(temp != null){
			    map.remove(temp);
			    temp.getArmy().removeUnit(temp);
			}
		}

		private void toggleConstantMode() {
			if(constantMode)
			{
				constantMode = false;
				logger.info("Constant Mode Off");
			}
			else
			{
				constantMode = true;
				logger.info("Constant Mode On");
			}
		}

		private void keyRightActions() {
			setInfoBoxXYs();
			if(menu){
			    
			}else if(tmenu){
			    tmenu = false;
			    currentMenu = null;
			    umenu = true;
			    if(selectedArmy!=null)
			        currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
			    else
			        currentMenu = new UnitMenu(0,parentScreen);
			}else if(umenu){
			    umenu = false;
			    currentMenu = null;
			    smenu = true;
			    currentMenu = new SideMenu(parentScreen);
			}else if(smenu){
			    smenu = false;
			    currentMenu = null;
			    tmenu = true;
			    if(selectedArmy!=null)
			        currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
			    else
			        currentMenu = new TerrainMenu(0,parentScreen);
			}else if(map.onMap(cursorXpos+1,cursorYpos)){
			    cursorXpos++;
			    if(cursorXpos >= sx/16+MAX_TILEW-2 && cursorXpos < map.getMaxCol()-2)sx += 16;
			}
			if(constantMode == true){
			    constantModeAction();
			}
		}

		private void keyLeftActions() {
			setInfoBoxXYs();
			if(menu)
			{
			    
			}else if(tmenu)
			{
			    tmenu = false;
			    currentMenu = null;
			    smenu = true;
			    currentMenu = new SideMenu(parentScreen);
			}
			else if(umenu)
			{
			    umenu = false;
			    currentMenu = null;
			    tmenu = true;
			    if(selectedArmy!=null)
			        currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
			    else
			        currentMenu = new TerrainMenu(0,parentScreen);
			}else if(smenu){
			    smenu = false;
			    currentMenu = null;
			    umenu = true;
			    if(selectedArmy!=null)
			        currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
			    else
			        currentMenu = new UnitMenu(0,parentScreen);
			}else if(map.onMap(cursorXpos-1,cursorYpos)){
			    cursorXpos--;
			    if(cursorXpos < sx/16+2 && sx > 0)sx -= 16;
			}
			if(constantMode == true){
			    constantModeAction();
			}
		}

		private void keyDownActions() {
			setInfoBoxXYs();
			if(menu || tmenu || umenu || smenu){
			    currentMenu.goDown();
			}else if(map.onMap(cursorXpos,cursorYpos+1)){
			    cursorYpos++;
			    if(cursorYpos >= sy/16+MAX_TILEH-2 && cursorYpos < map.getMaxRow()-2)sy += 16;
			}
			if(constantMode == true){
			    constantModeAction();
			}
		}

		private void keyUpActions() {
			setInfoBoxXYs();
			if(menu || tmenu || umenu || smenu){
			    currentMenu.goUp();
			}else if(map.onMap(cursorXpos,cursorYpos-1)){
			    cursorYpos--;
			    if(cursorYpos < sy/16+2 && sy > 0)sy -= 16;
			}
			if(constantMode == true)
			{
			    constantModeAction();
			}
		}

		private void constantModeAction() 
		{
			if(unit && selectedArmy != null)
			{
                placeUnit(map,map.find(new Location(cursorXpos,cursorYpos)),unitType);
			}
			if(terrain)
			{
			    placeTile();
			}
		}
        
        public void keyReleased(KeyEvent e) {}
    }
    
    class MapMouseControl implements MouseInputListener{
        public void mouseClicked(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            logger.info(x + "," + y + ":" + e.getButton());
            
            if(e.getButton() == MouseEvent.BUTTON1)
            {
                if(menu || smenu){
                    int mitem = currentMenu.getMenuItemAt(x,y,scale);
                    if(mitem != -1){
                        currentMenu.setMenuItem(mitem);
                        pressedA();
                    }
                }else if(tmenu || umenu){
                    int mitem = currentMenu.getMenuItemAt(x,y,scale);
                    if(mitem != -1){
                        System.out.print(mitem);
                        if(mitem == -2)currentMenu.goUp();
                        else if(mitem == -3)currentMenu.goDown();
                        else{
                            currentMenu.setMenuItem(mitem);
                            pressedA();
                        }
                    }
                }
            }
            else
            {
                //any other button
                if(!menu && !umenu && !tmenu && !smenu){
                    if(x < 32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }else if(x > MAX_TILEW*16*scale-32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx += 16;
                            if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }else if(y > MAX_TILEH*16*scale-32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy += 16;
                            if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    
                    cursorXpos = sx/16 + x/(16*scale);
                    if(cursorXpos < 0)cursorXpos=0;
                    else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
                    cursorYpos = sy/16 + y/(16*scale);
                    if(cursorYpos < 0)cursorYpos=0;
                    else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
                    pressedB();
                }
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        
        public void mouseExited(MouseEvent e) {}
        
        public void mousePressed(MouseEvent e) {
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            logger.info(x + "," + y + ":" + e.getButton());
            
            if(e.getButton() == MouseEvent.BUTTON1){
                if(!menu && !umenu && !tmenu && !smenu){
                    if(x < 32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }else if(x > MAX_TILEW*16*scale-32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx += 16;
                            if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }else if(y > MAX_TILEH*16*scale-32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy += 16;
                            if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    
                    cursorXpos = sx/16 + x/(16*scale);
                    if(cursorXpos < 0)cursorXpos=0;
                    else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
                    cursorYpos = sy/16 + y/(16*scale);
                    if(cursorYpos < 0)cursorYpos=0;
                    else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
                    pressedA();
                }
            }
        }
        public void mouseReleased(MouseEvent e) {}
        
        public void mouseDragged(MouseEvent e) {
            if(!menu && !umenu && !tmenu && !smenu){
                int x = e.getX() - parentFrame.getInsets().left;
                int y = e.getY() - parentFrame.getInsets().top;
                
                if(noScroll == 0){
                    if(x < 32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }else if(x > MAX_TILEW*16*scale-32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx += 16;
                            if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }else if(y > MAX_TILEH*16*scale-32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy += 16;
                            if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    noScroll = 3;
                }else noScroll--;
                
                cursorXpos = sx/16 + x/(16*scale);
                if(cursorXpos < 0)cursorXpos=0;
                else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
                cursorYpos = sy/16 + y/(16*scale);
                if(cursorYpos < 0)cursorYpos=0;
                else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
                pressedA();
            }
        }
        
        public void mouseMoved(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            
            setInfoBoxXYs();
            
            if(!menu && !umenu && !tmenu && !smenu){
                if(noScroll == 0){
                    if(x < 32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }else if(x > MAX_TILEW*16*scale-32*scale){
                        if(map.getMaxCol() > DEF_TILEW){
                            sx += 16;
                            if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }else if(y > MAX_TILEH*16*scale-32*scale){
                        if(map.getMaxRow() > DEF_TILEH){
                            sy += 16;
                            if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    noScroll = 3;
                }else noScroll--;
                
                cursorXpos = sx/16 + x/(16*scale);
                if(cursorXpos < 0)cursorXpos=0;
                else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
                cursorYpos = sy/16 + y/(16*scale);
                if(cursorYpos < 0)cursorYpos=0;
                else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
            }
        }
    }
}