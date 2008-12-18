package com.customwars.ui;
/*
 *BattleScreen.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 11, 2006, 7:57 AM
 *The Battle Screen, the central graphical component of CW during a battle
 */

import java.awt.*;
import javax.swing.*;

import java.io.*;
import java.util.ArrayList;

import java.awt.event.*;
import javax.swing.event.MouseInputListener;
//import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.Battle;
import com.customwars.BuildEvent;
import com.customwars.CWEvent;
import com.customwars.DeleteEvent;
import com.customwars.Mission;
import com.customwars.Music;
import com.customwars.Options;
import com.customwars.SFX;
import com.customwars.SelectionEvent;
import com.customwars.map.Map;
import com.customwars.map.Tile;
import com.customwars.map.location.Invention;
import com.customwars.map.location.Location;
import com.customwars.map.location.Property;
import com.customwars.map.location.Silo;
import com.customwars.map.location.TerrType;
import com.customwars.officer.CO;
import com.customwars.officer.COList;
import com.customwars.officer.Fighter;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.menu.BattleMenu;
import com.customwars.ui.menu.BuildMenu;
import com.customwars.ui.menu.ContextMenu;
import com.customwars.ui.menu.InGameMenu;
import com.customwars.ui.menu.MENU_SEL;
import com.customwars.ui.menu.MainMenu;
import com.customwars.ui.menu.OptionsMenu;
import com.customwars.unit.APC;
import com.customwars.unit.Action;
import com.customwars.unit.AntiAir;
import com.customwars.unit.Army;
import com.customwars.unit.Artillery;
import com.customwars.unit.Artillerycraft;
import com.customwars.unit.BCopter;
import com.customwars.unit.Battlecraft;
import com.customwars.unit.Battleship;
import com.customwars.unit.BlackBoat;
import com.customwars.unit.BlackBomb;
import com.customwars.unit.Bomber;
import com.customwars.unit.Carrier;
import com.customwars.unit.Cruiser;
import com.customwars.unit.Destroyer;
import com.customwars.unit.Infantry;
import com.customwars.unit.Lander;
import com.customwars.unit.MDTank;
import com.customwars.unit.Mech;
import com.customwars.unit.MegaTank;
import com.customwars.unit.Missiles;
import com.customwars.unit.MoveTraverse;
import com.customwars.unit.Neotank;
import com.customwars.unit.Oozium;
import com.customwars.unit.Piperunner;
import com.customwars.unit.Recon;
import com.customwars.unit.Rockets;
import com.customwars.unit.Shuttlerunner;
import com.customwars.unit.Spyplane;
import com.customwars.unit.Stealth;
import com.customwars.unit.Submarine;
import com.customwars.unit.TCopter;
import com.customwars.unit.Tank;
import com.customwars.unit.Transport;
import com.customwars.unit.UNIT_COMMANDS;
import com.customwars.unit.Unit;
import com.customwars.unit.UnitGraphics;
import com.customwars.unit.Zeppelin;

//public class BattleScreen extends JComponent implements ComponentListener
public class BattleScreen extends CWScreen 
{
	private static final String SAVE_FILE_EXT = ".save";
	private static final String REPLAY_FILE_EXT = ".replay";
	private static final String TEMPORARYSAVE_SAVE_FILENAME = "temporarysave.save";
	private int item;           //holds the menu's current item (both menus use this)
    private boolean menu;       //is the main menu in use?
    private boolean cmenu;      //is the context menu in use?
    private boolean move;       //is the game in move mode?
    private boolean fire;       //is the game in fire mode?
    private boolean bmenu;      //is the build menu in use?
    private boolean carbmenu;   //is the carrier build menu in use?
    private boolean silo;       //is a silo in use?
    private boolean special1;
    private boolean special2;
    private boolean repair;     //is a black boat in repair mode?
    private boolean fireRange;  //is the firing range being checked?
    private boolean minimap;    //is the minimap on?
    private boolean daystart;   //is the day start screen up?
    private boolean denyContinue;//is the player allowed to bypass the day start screen?
    private boolean victory;    //is the victory screen on?
    private boolean endStats;   //is the Ending Statistics screen on?
    private boolean intel;      //is the Intel screen on?
    private boolean replay;     //is replay mode on?
    private boolean delete;
    private int statArmyIndex = 0;//which army should the stats be shown for?
    private int statType = 0;   //which kind of statistics should we display? (0=general 1=units built 2=units lost)
    //private Battle b;           //holds the battle (Army List and DTD Mechanics)
    private InGameMenu currentMenu = null; //contains the active menu (null if no menus are active)
    //private BufferedImage bimg; //the screen, used for double buffering and scaling
    //private int scale;          //what scale multiplier is being used
    //private JFrame parentFrame;  //the frame that contains the window
    private Location originalLocation;  //the pre-move position of the unit
    private int originalcp;             //the pre-move capture points
    
    //When is this used?
    //private Location currTile;       //the new location of the unit (used by join and load)
    private Tile moveToTile;
    private Tile targTile;
	final static Logger logger = LoggerFactory.getLogger(BattleScreen.class); 
    
    private BSKeyControl keycontroller;   //the KeyControl, used to remove the component
    private BSMouseControl mousecontroller;//the MouseControl, used to remove the component
    private Unit targetUnit;          //used in join and load
    private int unload = 0;             //used for unloading 0=not unloading, 1=unloading slot1, 2=unloading slot2
    private int takeoff = 0;            //See above
    private Carrier tempCarrier;
    private Unit launching;
    private boolean beingLaunched = false;
    private boolean backAllowed = true; //is going back allowed?
    private boolean outOfMoveRange = false; //is the cursor out of the move range?
    private int unitCyclePosition = 0;  //the position in the unit cycle (see Key N)
    private int noScroll = 0;                  //don't scroll for this many more mouse commands
    //used to determine if CO info screen is on
    private boolean info, alt;
    private int infono;
    private int skip = 0; //This is used to skip lines of text.
    private int skipMax = 0;
    
    private float a;
    private boolean dialogue;
    
    private ArrayList<Location> targetTilesWithinRange;
    private int currContextTarg;
    
    int cx_p = 0;
    int cy_p = 0;
    //private boolean hideAllHP = false;
    
    /** Creates a new instance of BattleScreen */
    public BattleScreen(Battle b, JFrame f)
    {
    	super(b, f);

    	item = 0;
        menu = false;
        cmenu = false;
        move = false;
        fire = false;
        silo = false;
        repair = false;
        fireRange = false;
        minimap = false;
        victory = false;
        endStats = false;
        replay = false;
        info = false;
        infono = 0;
        special1 = false;
        special2 = false;
        delete = false;
        scale = 1;
        
        targetTilesWithinRange = new ArrayList<Location>();
        currContextTarg = 0;
        
        int numArmies = b.getArmies().length;
        for(int i=0; i<numArmies; i++){
            b.cursorLocation[i] = new Location(0,0);
            Property props[] = b.getArmy(i).getProperties();
            if(props!=null){
                for(int k=0; k < props.length; k++){
                    if(props[k].getIndex()==9){
                        b.cursorLocation[i] = props[k].getTile().getLocation();
                    }
                }
            }
        }
        
        moveCursorTo(b.cursorLocation[0]);
        
        //KeyControl is registered with the parent frame
        keycontroller = new BSKeyControl();
        f.addKeyListener(keycontroller);
        mousecontroller = new BSMouseControl();
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
        
        //turn on turn start display for first turn
        daystart = true;
        denyContinue = false;
        
        //logistics?
        moveToTile = null;
        targTile = null;

        //Initializes the (x,y) coordinates of the info boxes
        setInfoBoxXYs();
    }
    
    //Draws the screen
    public void drawScreen(Graphics2D g){
        //draws an animated gif in the background
        //this triggers repaint automatically
        //using repaint normally ruins animations
        g.drawImage(MiscGraphics.getMoveTile(),0,0,this);
        
        //draw a black background (if the map is smaller than the screen)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,480,320);
        if(Options.battleBackground){
            g.drawImage(MiscGraphics.getBattleBackground(),0,0,this);
        }
        
        drawMap(g);
        if(b.isFog() || b.isMist())drawFog(g);
        if(move)
        {
            drawMoveRange(g);
            drawPath(g);
        }
        if(fire)drawFireRange(g);
        if(fireRange)drawDisplayFireRange(g);
        if(unload > 0)drawUnloadRange(g);
        if(takeoff > 0)drawLaunchRange(g);
        if(repair)drawRepairRange(g);
        if(special1)drawSpecial1Range(g);
        if(special2)drawSpecial2Range(g);
        if(!silo)drawCursor(g);
        else drawSiloCursor(g);
        
        drawWeather(g);
        
        drawCOBar(g);
        drawInfoBox(g);
        drawAnimations(g);
        
        if((menu||cmenu||bmenu||carbmenu) && !b.animlock)currentMenu.drawMenu(g);
        
        if(minimap)drawMiniMap(g, 0, 0);
        if(daystart)drawDayStartScreen(g);
        if(intel)drawIntelScreen(g);
        if(info)drawInfoScreen(g); //Info
        
        if(victory)drawVictoryScreen(g);
        if(endStats)drawEndStats(g);
        
        //causes problems with animated gifs
        //this.repaint();
    }
    public void drawMap(Graphics2D g){
        super.drawMap(g);
        if (beingLaunched) { //draw the carrier (which is in temporary storage when a loaded unit is being launched)
        	g.drawImage(UnitGraphics.getUnitImage(tempCarrier),tempCarrier.getLocation().getCol()*16,tempCarrier.getLocation().getRow()*16,tempCarrier.getLocation().getCol()*16+16,tempCarrier.getLocation().getRow()*16+16,0,UnitGraphics.findYPosition(tempCarrier),16,UnitGraphics.findYPosition(tempCarrier)+16,this);        
        }
    }
    public void drawAnimations(Graphics2D g){
        //hey, draw that in reverse order!
        if(!b.diagQueue.isEmpty()) {
            if(!b.diagQueue.get(0).timer.isRunning()) {
                b.diagQueue.get(0).start();
            } else {
                b.diagQueue.get(0).draw(g,this);
            }
        }
        
        for(int i = 0; i<b.getLayerFour().size(); i++) {
            ((Animation)b.getLayerFour().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerThree().size(); i++) {
            ((Animation)b.getLayerThree().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerTwo().size(); i++) {
            ((Animation)b.getLayerTwo().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerOne().size(); i++) {
            ((Animation)b.getLayerOne().get(i)).draw(g,this);
        }
        
        g.setComposite(AlphaComposite.SrcOver);
    }

	public boolean assessHideTheHP() 
	{
		Army[] allArmies = b.getArmies();                    
		if(allArmies != null)
		{
			for(int i = 0; i < allArmies.length; i++)
			{
				Army pickArmy = allArmies[i];
				
				if(pickArmy != null && pickArmy.getSide() != b.getArmy(b.getTurn()).getSide())
				{
					CO currCO = pickArmy.getCO();
					
					if(currCO != null)
					{
						if(currCO.isHideAllHP())
						{
							/*
							hideTheHP = true;
							i = allArmies.length + 1;
							*/
							
							return true;
						}
					}
				}
			}
		}
		//return hideTheHP;
		
		return false;
	}
    
    //draws the cursor
    public void drawCursor(Graphics2D g){
       //update the cursor's graphical location
       cx_p = (cx_p + (cursorXpos*16)) / 2;
       cy_p = (cy_p + (cursorYpos*16)) / 2;
       
        //Check to see if the aiming cursor should be drawn
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) {
           g.drawImage(MiscGraphics.getAimCursor(),cx_p-sx-7,cy_p-sy-7,this);
        } else {
            //Makes sure that the aiming cursor is set to its normal cursor
            MiscGraphics.restoreAimCursor();
            g.drawImage(MiscGraphics.getCursor(),cx_p-sx-7,cy_p-sy-7,this);
        }
        //g.drawImage(MiscGraphics.getCursor(),cx*16-sx-16,cy*16-sy-16,this);
    } 
    
    //draws the cursor
    public void drawSiloCursor(Graphics2D g){
        g.drawImage(MiscGraphics.getSiloCursor(),cursorXpos*16-sx-32,cursorYpos*16-sy-32,this);
    }
    
    public void drawPath(Graphics2D g){
        int[] path = selected.getPath().getItems();
        if(path != null){
            //g.setColor(Color.red);
            int x = selected.getLocation().getCol();
            int y = selected.getLocation().getRow();
            
            if(path.length != 0){
                //start
                //if(path[0]==1 || path[0]==3)g.drawImage(MiscGraphics.getArrow(0),x*16-sx,y*16-sy,this);
                //else g.drawImage(MiscGraphics.getArrow(1),x*16-sx,y*16-sy,this);
                if(path[0] == 0)y--;
                else if(path[0] == 1)x++;
                else if(path[0] == 2)y++;
                else if(path[0] == 3)x--;
                int olddir = path[0];
                
                //middle
                for(int i=1; i<path.length; i++){
                    int index = 0;
                    if(olddir==0){
                        if(path[i]==0)index = 1;
                        if(path[i]==1)index = 3;
                        if(path[i]==3)index = 4;
                    }else if(olddir==1){
                        if(path[i]==0)index = 5;
                        if(path[i]==1)index = 0;
                        if(path[i]==2)index = 4;
                    }else if(olddir==2){
                        if(path[i]==1)index = 2;
                        if(path[i]==2)index = 1;
                        if(path[i]==3)index = 5;
                    }else if(olddir==3){
                        if(path[i]==0)index = 2;
                        if(path[i]==2)index = 3;
                        if(path[i]==3)index = 0;
                    }
                    
                    g.drawImage(MiscGraphics.getArrow(index),x*16-sx,y*16-sy,this);
                    if(path[i] == 0)y--;
                    else if(path[i] == 1)x++;
                    else if(path[i] == 2)y++;
                    else if(path[i] == 3)x--;
                    olddir = path[i];
                }
                
                //end
                if(olddir == 0)g.drawImage(MiscGraphics.getArrow(6),x*16-sx,y*16-sy,this);
                else if(olddir == 1)g.drawImage(MiscGraphics.getArrow(7),x*16-sx,y*16-sy,this);
                else if(olddir == 2)g.drawImage(MiscGraphics.getArrow(8),x*16-sx,y*16-sy,this);
                else if(olddir == 3)g.drawImage(MiscGraphics.getArrow(9),x*16-sx,y*16-sy,this);
                
            }
        }
    }
    
    //draws the CO Bar
    public void drawCOBar(Graphics2D g){
        if(cursorXpos < (MAX_TILEW/2)+(sx/16)){
            g.drawImage(MiscGraphics.getReverseCOBar(b.getArmy(b.getTurn()).getColor()),16*MAX_TILEW-71,0,this);
            if(!b.getArmy(b.getTurn()).getCO().isAltCostume()){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),16*MAX_TILEW-1,0,16*MAX_TILEW-33,12,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),16*MAX_TILEW-1,0,16*MAX_TILEW-33,12,369,350,401,362,this);
            }
            if(b.getArmy(b.getTurn()).getAltCO() != null)drawAltCOBar(g);
            
            g.drawImage(MiscGraphics.getGold(),16*MAX_TILEW-59,9,this);
            String funds = "" + b.getArmy(b.getTurn()).getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),16*MAX_TILEW-5-j*6,9,this);
            }
            
            if(b.getArmy(b.getTurn()).getTag() > 0){
                g.drawImage(MiscGraphics.getTagBreak(),16*MAX_TILEW-73,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isCOP()){
                g.drawImage(MiscGraphics.getPower(),16*MAX_TILEW-73,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isSCOP()){
                g.drawImage(MiscGraphics.getSuperPower(),16*MAX_TILEW-73,0,this);
            }else{
                int maxStars = b.getArmy(b.getTurn()).getCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),16*MAX_TILEW-33-6*i-6,0,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),16*MAX_TILEW-33-COPStars*6-7-7*(i-COPStars),0,this);
                    }
                }
            }
        }else{
            g.drawImage(MiscGraphics.getCOBar(b.getArmy(b.getTurn()).getColor()),0,0,this);
            if(!b.getArmy(b.getTurn()).getCO().isAltCostume()){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),0,0,32,12,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),0,0,32,12,369,350,401,362,this);
            }
            
            if(b.getArmy(b.getTurn()).getAltCO() != null)drawAltCOBar(g);
            
            g.drawImage(MiscGraphics.getGold(),0,9,this);
            String funds = "" + b.getArmy(b.getTurn()).getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),54-j*6,9,this);
            }
            
            if(b.getArmy(b.getTurn()).getTag() > 0){
                g.drawImage(MiscGraphics.getTagBreak(),39,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isSCOP()){
                g.drawImage(MiscGraphics.getSuperPower(),39,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isCOP()){
                g.drawImage(MiscGraphics.getPower(),39,0,this);
            }else{
                int maxStars = b.getArmy(b.getTurn()).getCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),33+6*i,0,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),33+6*i,0,this);
                    }
                }
            }
        }
    }
    
    //draws the Alt CO Bar
    public void drawAltCOBar(Graphics2D g){
        if(cursorXpos < (MAX_TILEW/2)+(sx/16)){
            g.drawImage(MiscGraphics.getAltReverseCOBar(b.getArmy(b.getTurn()).getColor()),16*MAX_TILEW-71,0,this);
            if(!b.getArmy(b.getTurn()).getAltCO().isAltCostume()){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),16*MAX_TILEW-1,21,16*MAX_TILEW-33,33,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),16*MAX_TILEW-1,21,16*MAX_TILEW-33,33,369,350,401,362,this);
            }
            if(b.getArmy(b.getTurn()).getTag() == 0){
                int maxStars = b.getArmy(b.getTurn()).getAltCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getAltCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getAltCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),16*MAX_TILEW-33-6*i-6,21,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),16*MAX_TILEW-33-COPStars*6-7-7*(i-COPStars),21,this);
                    }
                }
            }
        }else{
            g.drawImage(MiscGraphics.getAltCOBar(b.getArmy(b.getTurn()).getColor()),0,0,this);
            if(!b.getArmy(b.getTurn()).getAltCO().isAltCostume()){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),0,21,32,33,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),0,21,32,33,369,350,401,362,this);
            }
            if(b.getArmy(b.getTurn()).getTag() == 0){
                int maxStars = b.getArmy(b.getTurn()).getAltCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getAltCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getAltCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),33+6*i,21,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),33+6*i,21,this);
                    }
                }
            }
        }
    }
    
    //draws the Movement range
    public void drawMoveRange(Graphics2D g)
    {
        if(selected.getMoveRange() == null ) selected.calcMoveTraverse();
        MoveTraverse mt = selected.getMoveRange();
        
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        //g.setColor(Color.blue);
        for(int i=sx/16;i<map.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<map.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //if(mt.checkMove(i,j))g.fillRect(i*16-sx,j*16-sy,16,16);
                if(mt.checkMove(i,j) && !map.find(new Location(i,j)).getTerrain().getName().equals("Wall"))g.drawImage(MiscGraphics.getMoveTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    //Draws the launching range of launched units
    public void drawLaunchRange(Graphics2D g)
    {
        if(launching.getMoveRange() == null ) launching.calcMoveTraverse();
        MoveTraverse mt = launching.getMoveRange();
        
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        //g.setColor(Color.blue);
        for(int i=sx/16;i<map.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<map.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //if(mt.checkMove(i,j))g.fillRect(i*16-sx,j*16-sy,16,16);
                if(mt.checkMove(i,j) && !map.find(new Location(i,j)).getTerrain().getName().equals("Wall"))g.drawImage(MiscGraphics.getMoveTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    //draws the Firing Range
    public void drawFireRange(Graphics2D g){
        //makes the tiles translucent
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        /*
        for(int i=sx/16;i<m.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW) {
                break;
            }
            
            for(int j=sy/16;j<m.getMaxRow();j++) 
            {
                if(j>=sy/16+MAX_TILEH) 
                {
                    break;
                }
                if(i < 0 || j < 0) 
                {
                    continue;
                }
                
                //[CHANGE]
                for(int k = 0; k < contextTargs.size(); k++)
                {
                	if((new Location(i, j).equals(contextTargs.get(k))))
                	{
                        g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                	}
                }
            }
        }
        */
        
        for(int k = 0; k < targetTilesWithinRange.size(); k++)
        {
        	Location currLoc = targetTilesWithinRange.get(k);
        	int i = currLoc.getCol();
        	int j = currLoc.getRow();
        	g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
        }
        
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws Fog of War
    public void drawFog(Graphics2D g){
        //makes the tiles translucent
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        
        //If there is any fog present, use black squares
        //But if there is mist present and no fog, use white squares
        if(b.isFog())
        {
        	g.setColor(Color.black);
        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }
        else if(b.isMist())
        {
        	g.setColor(Color.white);
        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        }
        
        for(int i=sx/16;i<map.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<map.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //draw fog tile
                if(b.getFog(i,j))g.fillRect(i*16-sx,j*16-sy,16,16);
                
                //draw detected units
                if(map.find(new Location(i,j)).hasUnit())
                {
                    Unit tempu = map.find(new Location(i,j)).getUnit();
                    
                    if(tempu.getArmy().getSide() != b.getArmy(b.getTurn()).getSide() && tempu.isHidden() && tempu.isDetected())
                    {
                        //sets alpha back to normal
                        g.setComposite(AlphaComposite.SrcOver);
                        g.drawImage(MiscGraphics.getDetectIcon(),i*16-sx,j*16-sy,this);
                        //makes the tiles translucent
                        if(b.isFog())
                        {
                        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        }
                        else if(b.isMist())
                        {
                        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                        }
                    }
                }
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the weather
    public void drawWeather(Graphics2D g)
    {
        if(b.getWeather()!=0){
            if(b.getWeather()==1){
                //rain
                for(int i=sx/16;i<map.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<map.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getRain(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getRain(1),i*16-sx,j*16-sy,this);
                    }
                }
            }else if(b.getWeather()==2){
                //rain
                for(int i=sx/16;i<map.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<map.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getSnow(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSnow(1),i*16-sx,j*16-sy,this);
                        //g.drawImage(MiscGraphics.getSnow(2),i*16-sx,j*16-sy,this);
                    }
                }
            }else if(b.getWeather()==3){
                //rain
                for(int i=sx/16;i<map.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<map.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getSand(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSand(1),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSand(2),i*16-sx,j*16-sy,this);
                    }
                }
            }
        }
    }
    
    //draws the Repair Range
    public void drawRepairRange(Graphics2D g)
    {
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<map.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            
            for(int j=sy/16;j<map.getMaxRow();j++)
            {
                if(j>=sy/16+MAX_TILEH)break;
                
                if(i < 0 || j < 0)continue;
                
                //int rX = Math.abs(moveToTile.getLocation().getCol() - i);
                //int rY = Math.abs(moveToTile.getLocation().getRow() - j);

                Location origLoc = selected.getLocation();
                selected.setLocation(moveToTile.getLocation());
                if(selected.checkAdjacent(new Location(i,j)))
                //if(rX + rY == 1)
                {
                	g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
                selected.setLocation(origLoc);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    public void drawSpecial1Range(Graphics2D g)
    {
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<map.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            
            for(int j=sy/16;j<map.getMaxRow();j++)
            {
                if(j>=sy/16+MAX_TILEH)break;
                
                if(i < 0 || j < 0)continue;
                //if(selected.checkAdjacent(new Location(i,j)))g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                
                Location origLoc = selected.getLocation();
                selected.setLocation(moveToTile.getLocation());
                if(selected.getArmy().getCO().canTargetSpecial1(selected, new Location(i,j)))
                {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
                selected.setLocation(origLoc);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    public void drawSpecial2Range(Graphics2D g)
    {
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<map.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            
            for(int j=sy/16;j<map.getMaxRow();j++)
            {
                if(j>=sy/16+MAX_TILEH)break;
                
                if(i < 0 || j < 0)continue;

                Location origLoc = selected.getLocation();
                selected.setLocation(moveToTile.getLocation());
                if(selected.getArmy().getCO().canTargetSpecial2(selected, new Location(i,j)))
                {
                	g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
                selected.setLocation(origLoc);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Display Firing Range
    public void drawDisplayFireRange(Graphics2D g){
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<map.getMaxCol();i++) {
            if(i>=sx/16+MAX_TILEW)
                break;
            
            for(int j=sy/16;j<map.getMaxRow();j++) {
                if(j>=sy/16+MAX_TILEH) {
                    break;
                }
                if(i < 0 || j < 0) {
                    continue;
                }
                
                int colOffset = Math.abs(i - selected.getLocation().getCol());
                int rowOffset = Math.abs(j - selected.getLocation().getRow());
                int distance = colOffset + rowOffset;
                
                if(selected.checkDisplayFireRange(new Location(i,j)) && (!selected.getArmy().getCO().isDisruptFireDisplay() || b.getArmy(b.getTurn()).getSide() == selected.getArmy().getSide())) {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                } else if(selected.getArmy().getCO().isDisruptFireDisplay() && (distance <= 1) && b.getArmy(b.getTurn()).getSide() != selected.getArmy().getSide()) {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Unloading range
    public void drawUnloadRange(Graphics2D g)
    {
        Transport trans = (Transport) selected;
        //makes the tiles translucent
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<map.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            
            for(int j=sy/16;j<map.getMaxRow();j++)
            {
                if(j>=sy/16+MAX_TILEH)break;
                
                if(i < 0 || j < 0)continue;
                
                //int rX = Math.abs(moveToTile.getLocation().getCol() - i);
                //int rY = Math.abs(moveToTile.getLocation().getRow() - j);

                Location origLoc = selected.getLocation();
                selected.setLocation(moveToTile.getLocation());
                if(trans.checkUnloadRange(new Location(i,j),unload))
                //if(rX + rY == 1 &&
                //   moveToTile.getTerrain().moveCost(trans.getUnit(unload).getMType()) != -1)
                {
                	g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
                selected.setLocation(origLoc);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Day Start screen
    public void drawDayStartScreen(Graphics2D g)
    {
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        g.drawImage(MiscGraphics.getDayStart(b.getArmy(b.getTurn()).getColor()),0,0,MAX_TILEW*16,MAX_TILEH*16,0,0,480,320,this);
        
        //draw COs
        int coh = (MAX_TILEH*16-350)/2;
        if(coh < 0)coh = 0;
        if(b.getArmy(b.getTurn()).getAltCO()!=null){
            if(!b.getArmy(b.getTurn()).getAltCO().isAltCostume()){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),MAX_TILEW*16*2/3+20,coh,(MAX_TILEW*16*2/3+20)+225,coh+350,0,0,225,350,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getAltCO())),MAX_TILEW*16*2/3+20,coh,(MAX_TILEW*16*2/3+20)+225,coh+350,225,0,450,350,this);
            }       }
        if(!b.getArmy(b.getTurn()).getCO().isAltCostume()){
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),MAX_TILEW*16*2/3,coh,(MAX_TILEW*16*2/3)+225,coh+350,0,0,225,350,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),MAX_TILEW*16*2/3,coh,(MAX_TILEW*16*2/3)+225,coh+350,225,0,450,350,this);
        }
        //draw day text
        g.setColor(Color.black);
        g.setFont(new Font("Impact", Font.BOLD, MAX_TILEH*16/3));
        g.drawString("Day "+b.getDay(),10,MAX_TILEH*16*15/24);
    }
    
    //draws the CO Bar
    public void drawArmyCOBar(Army a, int x, int y, boolean displayMoney, Graphics2D g){
        g.drawImage(MiscGraphics.getCOBar(a.getColor()),x,y,this);
        if(!a.getCO().isAltCostume()){
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(a.getCO())),x,y,x+32,y+12,144,350,176,362,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(a.getCO())),x,y,x+32,y+12,369,350,401,362,this);
        }
        if(a.getAltCO() != null)drawArmyAltCOBar(a,x,y,g);
        
        
        g.drawImage(MiscGraphics.getGold(),x+0,y+9,this);
        
        //New stuff here
        boolean hidePower  = determineIfPowerHidden(a.getCO());
        boolean hideGold = determineIfGoldHidden(a.getCO());
        if(displayMoney && !hideGold) {
            String funds = "" + a.getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),x+54-j*6,y+9,this);
            }
        }else{
            g.drawImage(MiscGraphics.getQuestionMark(),x+54,y+9,this);
        }
        
        if(a.getTag()>0){
            g.drawImage(MiscGraphics.getTagBreak(),x+39,y,this);
        }else if(a.getCO().isSCOP()){
            g.drawImage(MiscGraphics.getSuperPower(),x+39,y,this);
        }else if(a.getCO().isCOP()){
            g.drawImage(MiscGraphics.getPower(),x+39,y,this);
        } else if(hidePower) {
            g.drawImage(MiscGraphics.getQuestionMark(),x+33,y,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+40,y,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+47,y,this);
        } else {
            int maxStars = a.getCO().getMaxStars();
            int COPStars = a.getCO().getCOPStars();
            double currentStars = a.getCO().getStars();
            double totalStars = currentStars;
            int starType = 0;
            for(int i=0;i<maxStars;i++){
                if(currentStars == 0.0){
                    starType = 0;
                }else if(currentStars >= 1.0){
                    if(i<COPStars){
                        if(totalStars >= COPStars)starType = 6;
                        else starType = 5;
                    }else{
                        if(totalStars >= maxStars)starType = 6;
                        else starType = 5;
                    }
                    currentStars-=1.0;
                }else{
                    if(currentStars <= .4)starType=1;
                    else if(currentStars <= .6)starType=2;
                    else if(currentStars <= .8)starType=3;
                    else if(currentStars <= 1.0)starType=4;
                    currentStars = 0.0;
                }
                
                if(i<COPStars){
                    g.drawImage(MiscGraphics.getSmallStar(starType),x+33+6*i,y,this);
                }else{
                    g.drawImage(MiscGraphics.getBigStar(starType),x+33+6*i,y,this);
                }
            }
        }
    }
    
    public boolean determineIfPowerHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.isHiddenPower() && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    public boolean determineIfGoldHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.isHiddenGold() && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    public boolean determineIfIntelHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.isHiddenIntel() && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    
    //draws the Alt CO Bar
    public void drawArmyAltCOBar(Army a, int x, int y, Graphics2D g){
        //if(a.getAltCO() != null){
        g.drawImage(MiscGraphics.getAltCOBar(a.getColor()),x,y,this);
        if(!a.getAltCO().isAltCostume()){
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(a.getAltCO())),x,y+21,x+32,y+33,144,350,176,362,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(a.getAltCO())),x,y+21,x+32,y+33,369,350,401,362,this);
        }
        
        //New stuff here
        boolean hideInfo = determineIfPowerHidden(a.getAltCO());
        
        if(hideInfo) {
            g.drawImage(MiscGraphics.getQuestionMark(),x+33,y+21,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+40,y+21,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+47,y+21,this);
        } else if(a.getTag() == 0) {
            int maxStars = a.getAltCO().getMaxStars();
            int COPStars = a.getAltCO().getCOPStars();
            double currentStars = a.getAltCO().getStars();
            double totalStars = currentStars;
            int starType = 0;
            for(int i=0;i<maxStars;i++){
                if(currentStars == 0.0){
                    starType = 0;
                }else if(currentStars >= 1.0){
                    if(i<COPStars){
                        if(totalStars >= COPStars)starType = 6;
                        else starType = 5;
                    }else{
                        if(totalStars >= maxStars)starType = 6;
                        else starType = 5;
                    }
                    currentStars-=1.0;
                }else{
                    if(currentStars <= .4)starType=1;
                    else if(currentStars <= .6)starType=2;
                    else if(currentStars <= .8)starType=3;
                    else if(currentStars <= 1.0)starType=4;
                    currentStars = 0.0;
                }
                
                if(i<COPStars){
                    g.drawImage(MiscGraphics.getSmallStar(starType),x+33+6*i,y+21,this);
                }else{
                    g.drawImage(MiscGraphics.getBigStar(starType),x+33+6*i,y+21,this);
                }
            }
        }
    }
    //draws the info screen
    public void drawInfoScreen(Graphics2D g) {
        String[] bio;
        int i, store, adjust;
        int starStore = 0;
        g.drawImage(MainMenuGraphics.getBackground(),0,0, this);
        //Draws the COs
        String sidestring, costring;
        //These two are used for adjusting the text so it doesn't interfere with the top info bar
        store = 0;
        adjust = 0;
        int charShift = 0;
        Character c = new Character(' ');
        int s;
        if(!alt) {
            //Draw the main CO
            if(!b.getArmy(infono).getCO().isAltCostume())
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(infono).getCO())),300,40,300 + 225, 40 + 350, 0, 0, 225, 350, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(infono).getCO())),300,40,300 + 225, 40 + 350, 225, 0, 450, 350, this);
            //
            //Draw the 'basic information'
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            g.drawString("Side: " + (infono+1), 8,20);
            g.drawString("Main CO: " + b.getArmy(infono).getCO().getName(), 45,20);
            g.drawString(b.getArmy(infono).getCO().getTitle(), (b.getArmy(infono).getCO().getName().length() + 9)*6 + 45,20);
            
            //CO Bio
            //This is a 'word wrap' thing, used multiple times. Listen up, I'm only going to document this once. >_>
            for(i = 0; i<((b.getArmy(infono).getCO().getBio().length()/40)+1); i++) {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
                if(b.getArmy(infono).getCO().getBio().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left in the bio?
                    //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                    if((40 + i*15- skip*MAX_TILEH*8+ store*15) + adjust <25 && 40 + i*15- skip*MAX_TILEH*8+ + store*15 +adjust >0)
                        adjust += 25; //If so, move this, and everything after this, 25 pixels down.
                    //Draw the substring - 40 characters from the last area.
                    if(c.isWhitespace(b.getArmy(infono).getCO().getBio().charAt((i+1)*40-charShift))) { //If the string ends with a space/ next line starts with one, 'sfine, continue
                        g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {//If not - TIME TO PARSE THIS MOTHER
                        for(s = 1; s<41; s++) {
                            if(c.isWhitespace(b.getArmy(infono).getCO().getBio().charAt((i+1)*40-charShift-s))) {
                                break;
                            }
                        }
                        if(s!=40) {
                            g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift-s), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                            charShift+=s; //Shift the substring over
                        } else {
                            g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift-s), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                            charShift+=s; //Shift the substring over
                            s = 0;
                        }
                    }
                    
                } else //If there is less than 40 characters left...
                {
                    if((40 + i*15- skip*MAX_TILEH*8 + adjust+ store*15)<25 && store*15+ 40 + i*15- skip*MAX_TILEH*8 + adjust>0)
                        adjust += 25;
                    //Avoiding info space.
                    //Drawing the rest of the substring.
                    //First we see if
                    g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getCO().getBio().length()/40)+1))
                    store = i; //Store is used to get placement.
            }
            store += 2;
            
            //This draws the Hit and Miss
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getHitIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getCO().getHit(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getMissIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getCO().getMiss(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            
            //This draws the power bar
            if(((store+1)*15- skip*MAX_TILEH*8 + adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8 + adjust)>0)
                adjust += 25;
            if(b.getArmy(infono).getCO().getCOPStars() != -1)
                for(i = 0; i<b.getArmy(infono).getCO().getCOPStars()+1; i++) {
                g.drawImage(MiscGraphics.getSmallStar(5),10+i*6, (store+1)*15- skip*MAX_TILEH*8 + adjust, this);
                starStore = i;
                }
            //Aaand...the SCOP
            for(i = 0; i<(b.getArmy(infono).getCO().getMaxStars()-b.getArmy(infono).getCO().getCOPStars()); i++) {
                g.drawImage(MiscGraphics.getBigStar(5),10+i*8 + starStore*6, (store+1)*15- skip*MAX_TILEH*8 + adjust, this);
            }
            store++;
            //This draws the Day to Day.
            
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            
            g.drawImage(MiscGraphics.getSkillIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            
            //Word wrap for the skill
            for(i = 0; i<((b.getArmy(infono).getCO().getD2D().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getCO().getD2D().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    
                    g.drawString(b.getArmy(infono).getCO().getD2D().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getD2D().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getCO().getD2D().length()/40)+1))
                    store +=i;
            }
            store += 2;
            //This draws the Power
            if(b.getArmy(infono).getCO().getCOPStars() != -1) {
                if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                    adjust += 25;
                
                g.drawImage(MiscGraphics.getPowerIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
                g.drawString(b.getArmy(infono).getCO().getCOPName(), 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                
                for(i = 0; i<((b.getArmy(infono).getCO().getCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
                {
                    if(b.getArmy(infono).getCO().getCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        g.drawString(b.getArmy(infono).getCO().getCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        
                        g.drawString(b.getArmy(infono).getCO().getCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    }
                    if(i+1 == ((b.getArmy(infono).getCO().getCOPString().length()/40)+1))
                        store +=i;
                }
                
                store += 2;
            }
            //This draws the SCOP
            if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getSuperIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
            g.drawString(b.getArmy(infono).getCO().getSCOPName(), 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
            for(i = 0; i<((b.getArmy(infono).getCO().getSCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getCO().getSCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getSCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getSCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 == ((b.getArmy(infono).getCO().getSCOPString().length()/40)+1))
                    store +=i;
            }
            //Draws tags
            store += 2;
            for(i = 0; i< b.getArmy(infono).getCO().getTagStars().length; i++) {
                if(b.getArmy(infono).getCO().getTagStars()[i] > 0) {
                    if(i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust<25 && i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getTagCOs()[i], 10, i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                    for(int t =0; t<b.getArmy(infono).getCO().getTagStars()[i]; t++) {
                        g.drawImage(MiscGraphics.getBigStar(5), b.getArmy(infono).getCO().getTagCOs()[i].length()*25 + 15 + t*8,(store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust+ i*15 -10, this);
                    }
                    if(i+1 == b.getArmy(infono).getCO().getTagStars().length)
                        store +=i;
                }
            }
            skipMax = ((store * 15)/(MAX_TILEH*8));
        } else {
            //Draw the main CO
            if(!b.getArmy(infono).getAltCO().isAltCostume())
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(infono).getAltCO())),175,40,175 + 225, 40 + 350, 0, 0, 225, 350, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(infono).getAltCO())),175,40,175 + 225, 40 + 350, 225, 0, 450, 350, this);
            //Draw the 'basic information'
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            g.drawString("Side: " + (infono+1), 8,20);
            g.drawString("Main CO: " + b.getArmy(infono).getAltCO().getName(), 45,20);
            g.drawString(b.getArmy(infono).getAltCO().getTitle(), (b.getArmy(infono).getAltCO().getName().length() + 9)*6 + 45,20);
            
            //CO Bio
            //This is a 'word wrap' thing, used multiple times. Listen up, I'm only going to document this once. >_>
            for(i = 0; i<((b.getArmy(infono).getAltCO().getBio().length()/40)+1); i++) {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
                if(b.getArmy(infono).getAltCO().getBio().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left in the bio?
                    //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                    if((40 + i*15- skip*MAX_TILEH*8) + adjust <25 && 40 + i*15- skip*MAX_TILEH*8+ adjust >0)
                        adjust += 25; //If so, move this, and everything after this, 25 pixels down.
                    //Draw the substring - 40 characters from the last area.
                    g.drawString(b.getArmy(infono).getAltCO().getBio().substring(i*40, (i+1)*40), 10, 40 + i*15- skip*MAX_TILEH*8 + adjust);
                    
                } else //If there is less than 40 characters left...
                {
                    if((40 + i*15- skip*MAX_TILEH*8 + adjust)<25 && 40 + i*15- skip*MAX_TILEH*8 + adjust>0)
                        adjust += 25;
                    //Avoiding info space.
                    //Drawing the rest of the substring.
                    g.drawString(b.getArmy(infono).getAltCO().getBio().substring(i*40), 10,40 + i*15- skip*MAX_TILEH*8);
                }
                if(i+1 ==((b.getArmy(infono).getAltCO().getBio().length()/40)+1))
                    store = i; //Store is used to get placement.
            }
            store+=2;
            //This draws the Hit and Miss
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getHitIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getAltCO().getHit(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getMissIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getAltCO().getMiss(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            
            //This draws the power bar
            //If the CO has a COP
            if((22 + (store)*15- skip*96*scale + adjust)<25 && (22 + (store)*15- skip*96*scale + adjust)>0)
                adjust += 25;
            if(b.getArmy(infono).getAltCO().getCOPStars() != -1)
                for(i = 0; i<b.getArmy(infono).getAltCO().getCOPStars()+1; i++) {
                g.drawImage(MiscGraphics.getSmallStar(5),10+i*6, 22 + (store)*15- skip*96*scale + adjust, this);
                starStore = i;
                }
            //Aaand...the SCOP
            for(i = 0; i<(b.getArmy(infono).getAltCO().getMaxStars()-b.getArmy(infono).getAltCO().getCOPStars()); i++) {
                g.drawImage(MiscGraphics.getBigStar(5),10+i*8 + starStore*6, 20 + (store)*15- skip*96*scale + adjust, this);
            }
            
            store++;
            
            //This draws the Day to Day.
            
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            
            g.drawImage(MiscGraphics.getSkillIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            
            //Word wrap for the skill
            for(i = 0; i<((b.getArmy(infono).getAltCO().getD2D().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getAltCO().getD2D().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    
                    g.drawString(b.getArmy(infono).getAltCO().getD2D().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getD2D().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getAltCO().getD2D().length()/40)+1))
                    store +=i;
            }
            store += 2;
            //This draws the Power
            if(b.getArmy(infono).getAltCO().getCOPStars() != -1) {
                if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                    adjust += 25;
                
                g.drawImage(MiscGraphics.getPowerIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
                g.drawString(b.getArmy(infono).getAltCO().getCOPName(), 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                
                for(i = 0; i<((b.getArmy(infono).getAltCO().getCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
                {
                    if(b.getArmy(infono).getAltCO().getCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        g.drawString(b.getArmy(infono).getAltCO().getCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        
                        g.drawString(b.getArmy(infono).getAltCO().getCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    }
                    if(i+1 == ((b.getArmy(infono).getAltCO().getCOPString().length()/40)+1))
                        store +=i;
                }
                
                store += 2;
            }
            //This draws the SCOP
            if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getSuperIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
            g.drawString(b.getArmy(infono).getAltCO().getSCOPName(), 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
            for(i = 0; i<((b.getArmy(infono).getAltCO().getSCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getAltCO().getSCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getSCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getSCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 == ((b.getArmy(infono).getAltCO().getSCOPString().length()/40)+1))
                    store +=i;
            }
            //Draws tags
            store += 2;
            for(i = 0; i< b.getArmy(infono).getAltCO().getTagStars().length; i++) {
                if(b.getArmy(infono).getAltCO().getTagStars()[i] > 0) {
                    if(i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust<25 && i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getTagCOs()[i], 10, i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                    for(int t =0; t<b.getArmy(infono).getAltCO().getTagStars()[i]; t++) {
                        g.drawImage(MiscGraphics.getBigStar(5), b.getArmy(infono).getAltCO().getTagCOs()[i].length()*5 + 15 + t*8,(store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust+ i*15 -10, this);
                    }
                    if(i+1 == b.getArmy(infono).getAltCO().getTagStars().length)
                        store +=i;
                }
            }
            skipMax = ((store * 15)/(MAX_TILEH*8));
        }
    }
    //draws the Intel screen
    public void drawIntelScreen(Graphics2D g){
        boolean displayMoney = true;        //is it ok to display the enemy money and properties?
        boolean displayArmyMoney = true;    //is it ok to display this army's money? (self and allies)
        Army ctarmy = b.getArmy(b.getTurn());
        
        if(b.getWeather()==1 || b.isFog()) {
            displayMoney = false;
        }
        
        //fill in background
        g.drawImage(MainMenuGraphics.getBackground(),0,0,this);
        //g.drawImage(MiscGraphics.getDayStart(b.getArmy(b.getTurn()).getColor()),0,0,MAX_TILEW*16,MAX_TILEH*16,0,0,256,192,this);
        
        //Day and Turn
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Day "+b.getDay(),0,15);
        g.setColor(Color.yellow);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        if(b.getTurn()<5)g.fillRect(0,20+32*b.getTurn(),130,32);
        else g.fillRect(130,20+32*(b.getTurn()-5),130,32);
        g.setComposite(AlphaComposite.SrcOver);
        
        Army[] arms = b.getArmies();
        
        for(int i=0; i < arms.length; i++) {
            if(!displayMoney && arms[i].getSide() != ctarmy.getSide()) {
                displayArmyMoney = false;
            } else {
                displayArmyMoney = true;
            }
            
            //draw units
            if(i < 5) {
                //This line is used for drawing normal units
                g.drawImage(UnitGraphics.getUnitImage(0,arms[i].getColor()),90,20+i*32,90+16,20+i*32+16,0,UnitGraphics.findYPosition(0,arms[i].getCO().getStyle()),16,UnitGraphics.findYPosition(0,arms[i].getCO().getStyle())+16,this);
            }
            
            else g.drawImage(UnitGraphics.getUnitImage(0, arms[i].getColor()),220,20+(i-5)*32,220+16,20+(i-5)*32+16, 0, UnitGraphics.findYPosition(0,arms[i].getCO().getStyle()), 16, UnitGraphics.findYPosition(0,arms[i].getCO().getStyle())+16 ,this);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            //This is new!
            boolean hideInfo = determineIfIntelHidden(arms[i].getCO());
            
            String uCounter = "";
            
            if(hideInfo) {
                uCounter = "x?";
            } else {
                uCounter = "x"+arms[i].getNumberOfUnits();
            }
            
            if(i < 5) {
                g.drawString(uCounter,106,20+16+i*32);
            } else {
                g.drawString(uCounter,236,20+16+(i-5)*32);
            }
            
            //draw properties
            Image temp;
            temp = TerrainGraphics.getColoredSheet(arms[i].getColor()+1);
            if(i<5)g.drawImage(temp,90,20+10+i*32-10,90+16,20+10+i*32+22,0,TerrType.getYIndex(TerrType.CITY),16,TerrType.getYIndex(TerrType.CITY) + 32,this);
            else g.drawImage(temp,220,20+10+(i-5)*32-10,220+16,20+10+(i-5)*32+22,0,TerrType.getYIndex(TerrType.CITY),16,TerrType.getYIndex(TerrType.CITY) + 32,this);
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            //There is new stuff here
            if(displayArmyMoney && !hideInfo) {
                if(i<5) {
                    g.drawString("x"+arms[i].getNumberOfProperties(),106,20+32+i*32);
                } else {
                    g.drawString("x"+arms[i].getNumberOfProperties(),236,20+32+(i-5)*32);
                }
            } else {
                if(i<5) {
                    g.drawString("x?",106,20+32+i*32);
                } else {
                    g.drawString("x?",236,20+32+(i-5)*32);
                }
            }
            
            //draw D2D bars
            if(i < 5)drawArmyCOBar(arms[i],0,20+32*i,displayArmyMoney,g);
            else drawArmyCOBar(arms[i],130,20+32*(i-5),displayArmyMoney,g);
        }
    }
    //draws a dialogue box
    //uh, untested.
    /*public void drawDialogueBox(Graphics2D g, String string){
        g.setColor(Color.WHITE);
        g.fillRect(0,MAX_TILEH*16-35,MAX_TILEW*16, MAX_TILEH*16);
     
        for(int i = 0; i<((string.length()/50)+1); i++)
        {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
            if(string.length() - (i+1)*50 >= 0)
            { //Is there more than 40 characters left in the bio?
                //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                //Draw the substring - 40 characters from the last area.
                g.drawString(string.substring(i*50, (i+1)*50), 10, 50 + i*15*MAX_TILEH*8);
     
            }
            else //If there is less than 40 characters left...
            {
                //Avoiding info space.
                //Drawing the rest of the substring.
                g.drawString(string.substring(i*40), 10,50 + i*15*MAX_TILEH*8);
            }
        }
    }*/
    //draws the Day Start screen
    public void drawVictoryScreen(Graphics2D g){
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        
        //draw the basic format
        g.drawImage(MiscGraphics.getSky(),0,0,this);
        g.drawImage(MiscGraphics.getWin(),0,0,this);
        g.drawImage(MiscGraphics.getLose(),0,0,this);
        g.drawImage(MiscGraphics.getDays(),0,0,this);
        
        //write name and day
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(map.getMapName(),14,100);
        g.drawString(b.getDay()+" Days",109,100);
        
        //draw CO heads
        //draw victorious COs
        int numArmies = b.getNumArmies();
        Army varmies[] = new Army[numArmies];
        for(int i=numArmies-1; i >= 0; i--){
            varmies[i] = b.getArmy(i);
            if(varmies[i].getAltCO()!=null){
                if(!varmies[i].getAltCO().isAltCostume())
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(varmies[i].getAltCO())),24+i*48,0,(24+i*48)+48,48,48,350,96,398,this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(varmies[i].getAltCO())),24+i*48,0,(24+i*48)+48,48,273,350,320,398,this);
            }
            if(!varmies[i].getCO().isAltCostume())
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(varmies[i].getCO())),0+i*48,0,(0+i*48)+48,48,48,350,96,398,this);
            else
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(varmies[i].getCO())),0+i*48,0,(0+i*48)+48,48,273,350,320,398,this);
        }
        //draw defeated COs
        int numStatArmies = b.getNumStatArmies();
        int lpos = numStatArmies-numArmies-1;
        for(int i=numStatArmies-1; i >=0; i--){
            boolean isLoser = true;
            Army statArmy = b.getStatArmy(i);
            //check if a victorious army
            for(int j=0; j < varmies.length; j++){
                if(statArmy == varmies[j])isLoser = false;
            }
            if(isLoser){
                if(statArmy.getAltCO()!=null){
                    if(!statArmy.getAltCO().isAltCostume())
                        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getAltCO())),94+lpos*48,144,(94+lpos*48)+48,192,96,350,143,398,this);
                    else
                        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getAltCO())),94+lpos*48,144,(94+lpos*48)+48,192,321,350,368,398,this);
                }
                if(!statArmy.getCO().isAltCostume())
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getCO())),70+lpos*48,144,(70+lpos*48)+48,192,96,350,143,398,this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getCO())),70+lpos*48,144,(70+lpos*48)+48,192,321,350,368,398,this);
                lpos--;
            }
        }
    }
    
    //draws the Day Start screen
    public void drawEndStats(Graphics2D g){
        Army statArmy = b.getStatArmy(statArmyIndex);
        
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        
        //draw basic format
        g.drawImage(MainMenuGraphics.getBackground(),0,0,this);
        g.drawImage(MiscGraphics.getStatsBox(),0,0,this);
        
        //write name and day
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(map.getMapName(),24,34);
        g.drawString(b.getDay()+" Days",194,34);
        
        //draw CO faces and names
        if(statArmy!=null){
            if(!statArmy.getCO().isAltCostume())
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getCO())),51,41,51 + 32, 41 + 12, 144, 350, 175, 362, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getCO())),51,41,51 + 32, 41 + 12, 369, 350, 400, 362, this);
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(statArmy.getCO().getName(),90,49);
            if(statArmy.getAltCO()!=null){
                if(!statArmy.getAltCO().isAltCostume())
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getAltCO())),148,41,148 + 32, 41 + 12, 144, 350, 175, 362, this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(statArmy.getAltCO())),148,41,148 + 32, 41 + 12, 369, 350, 400, 362, this);
                g.setColor(Color.black);
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.drawString(statArmy.getAltCO().getName(),186,49);
            }
            
            if(statType == 0){
                //draw remaining buildings
                Image temp;
                temp = TerrainGraphics.getTerrainImage(10,statArmy.getColor()+1);
                g.drawImage(temp,34,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(11,statArmy.getColor()+1);
                g.drawImage(temp,89,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(12,statArmy.getColor()+1);
                g.drawImage(temp,144,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(13,statArmy.getColor()+1);
                g.drawImage(temp,62,150-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(14,statArmy.getColor()+1);
                g.drawImage(temp,117,150-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(17,statArmy.getColor()+1);
                g.drawImage(temp,172,150-(temp.getHeight(this)-16),this);
                //calculate buildings
                int proptypes[] = new int[6];
                Property props[] = statArmy.getProperties();
                for(int i = 0; i < props.length; i++){
                    if(props[i].getIndex() == 10)proptypes[0]++;
                    else if(props[i].getIndex() == 11)proptypes[1]++;
                    else if(props[i].getIndex() == 12)proptypes[2]++;
                    else if(props[i].getIndex() == 13)proptypes[3]++;
                    else if(props[i].getIndex() == 14)proptypes[4]++;
                    else if(props[i].getIndex() == 17)proptypes[5]++;
                }
                //draw strings
                g.setColor(Color.black);
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.drawString("x"+proptypes[0],57,134);
                g.drawString("x"+proptypes[1],112,134);
                g.drawString("x"+proptypes[2],167,134);
                g.drawString("x"+proptypes[3],86,165);
                g.drawString("x"+proptypes[4],141,165);
                g.drawString("x"+proptypes[5],196,165);
            }else if(statType == 1){
                g.drawString("Units Built",57,134);
            }else if(statType == 2){
                g.drawString("Units Lost",57,134);
            }
        }
    }
    
    //moves the cursor by a certain amount (untested)
    /*private void moveCursorRelative(int xadj, int yadj){
        if(m.onMap(cx+xadj,cy+yadj)){
            cx += xadj;
            cy += yadj;
     
            //this part needs work
            if(cy < sy/16)sy -= 16;
        }
    }*/
    
    //moves the cursor to a given position
    private void moveCursorTo(Location l){
        int x = l.getCol();
        int y = l.getRow();
        
        if(map.onMap(x,y)){
            cursorXpos = x;
            cursorYpos = y;
            
            if(cursorXpos < sx/16 || cursorXpos >= sx/16+MAX_TILEW || cursorYpos < sy/16 || cursorYpos >= sy/16+MAX_TILEH){
                sx = (cursorXpos - 8) * 16;
                sy = (cursorYpos - 6) * 16;
                if(sx < 0) sx = 0;
                if(sy < 0) sy = 0;
                if(sx+MAX_TILEW*16 > map.getMaxCol()*16) sx = map.getMaxCol()*16-MAX_TILEW*16;
                if(sy+MAX_TILEH*16 > map.getMaxRow()*16) sy = map.getMaxRow()*16-MAX_TILEH*16;
                if(sx < 0) sx = 0;
                if(sy < 0) sy = 0;
            }
            
            //center small maps
            if(map.getMaxCol() < 30){
                sx = -((30 - map.getMaxCol())/2)*16;
            }
            if(map.getMaxRow() < 20){
                sy = -((20 - map.getMaxRow())/2)*16;
            }
        }
    }
    
    //
    public void removeFromFrame(){
        parentFrame.getContentPane().remove(this);
        parentFrame.removeKeyListener(keycontroller);
        parentFrame.removeMouseListener(mousecontroller);
        parentFrame.removeMouseMotionListener(mousecontroller);
    }
    
    public void resetBattle(Battle newBattle){
        b = newBattle;
        map = b.getMap();
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
        menu = false;
        cmenu = false;
        move = false;
        fire = false;
        silo = false;
        repair = false;
        fireRange = false;
        minimap = false;
        victory = false;
        endStats = false;
        replay = false;
        
        //scale = 1;
        
        //for(int i=0; i<10; i++){
        //    b.cursorLocation[i] = new Location(0,0);
        //}
        moveCursorTo(b.cursorLocation[b.getTurn()]);
        
        if(Options.isNetworkGame())daystart = true;
        denyContinue = false;
    }
    
    public void startReplay(){
        replay = true;
    }
    
    public void endBattle(){
        victory = true;
        //end the game asap if a snail game
        if(Options.snailGame){
            b.sendCommandToMain("endgame",Options.gamename+"\n"+Options.username+"\n"+Options.password);
        }
        //save the replay if applicable
        if(b.getBattleOptions().isRecording() && !replay){
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save replay file...");
            fc.setCurrentDirectory(new File("./"));
            fc.setApproveButtonText("Save");
            /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CW Replay Files", "replay");
            fc.setFileFilter(filter);*/
            int returnVal = fc.showOpenDialog(this);
            
            if(returnVal != 1){
                String filename = fc.getSelectedFile().getPath();
                if(filename.length()<8 || !filename.substring(filename.length()-7).equals(REPLAY_FILE_EXT))filename += REPLAY_FILE_EXT;
                Mission.saveReplay(filename);
            }
        }
    }
    
    private void returnToMain(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
        //hopefully, this should make the intro music work right
        if(Options.isMusicOn())Music.startMainMenuMusic();
        Options.snailGame = false;
    }
    
    private void returnToServerScreen(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
        //hopefully, this should make the intro music work right
        if(Options.isMusicOn())Music.startMainMenuMusic();
        mm.returnToServerInfo();
    }
    //builds a unit from a carrier
    private void buildUnit(Map m, Tile t, int type){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        Army a = ((Property)t.getTerrain()).getOwner();
        
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
                BlackBomb newbomb = new BlackBomb(x,y,a,m);
                //Bypasses price modifications
                if(b.getBattleOptions().isBalance())
                    newbomb.setPrice(15000);
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
    
    //Builds a new unit
    private void buildCarrierUnit(Army a, Map m, Tile t, int type){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        
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
                BlackBomb newbomb = new BlackBomb(x,y,a,m);
                //Bypasses price modifications
                if(b.getBattleOptions().isBalance())
                    newbomb.setPrice(15000);
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
    
    public void pressedA()
    {
        //what is selecting Vimes? (--Urusan)
        //It's used for actions that require selection. <_<
    	Tile clickedTile = map.find(new Location(cursorXpos,cursorYpos));
    	
    	
        if(!b.diagQueue.isEmpty())
            b.diagQueue.get(0).pressedA();
        if(b.animlock)
            return;
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) 
        {
        	logger.info("Do Select Action");
        	
            selectAction(new Location(cursorXpos, cursorYpos));
			return;
        }
        
        if(delete)
        {
        	logger.info("Do Delete Command");
        	//If the delete command won't work, don't let anything else work
            if(!deleteCommand(new Location(cursorXpos,cursorYpos)))
            	return;
        }
        if(!daystart && !intel && !victory && !endStats && !replay)
        {
            if(menu)
            {
            	logger.info("Do Menu Action");
            	
                menuActions();
            }
            else if(cmenu)
            {
            	logger.info("Do Context Action");
            	
            	//Don't forget to move the unit first!
                contextMenuActions(selected);
            }
            else if(bmenu)
            {
            	logger.info("Do Deploy Effect");
            	
                deployEffect();
            }
            else if(carbmenu)
            {
                logger.info("Do Carrier Build");
                buildEffect(selected);
            }
            else if(fireRange)
            {
                //Don't do anything
            }
            else if(unload > 0 && 
            		selected instanceof Transport)
            {
            	logger.info("Do Unload Effect");
            	//user.checkUnloadRange(new Location(cx,cy), slot)
            	//Move the unit over and then start firing
    			if(autoMove(selected))	
    				unloadEffect((Transport)selected, unload);
    			
    			
            }
            else if(takeoff > 0 && 
            		selected instanceof Carrier)
            {
                logger.info("Do Launch Effect");
                launchEffect((Carrier)selected,takeoff);
            }
            //else if(move)
            else if(move && 
            		selected.isActive() && 
            		(selected.getArmy().getID() == b.getArmy(b.getTurn()).getID() || b.getArmy(b.getTurn()).getCO().isMayhem()))
            {
            	logger.info("Do Move Command");
                //if(!moveCommand())
                	//return;
            	//move = true; //ASDF < BOOKMARK
                
            	//move command delayed so that the unit's remaining actions will
            	//occur right after the move command
            	//moveCommand(selected)
            	
            	//Move the unit to this tile later
            	moveToTile = clickedTile;
            
                //Really ugly hack...
                if(!map.find(selected).equals(moveToTile))     {
                    selected.setMoved(true); 
                }
            	//Determine correct context menu
            	setContextMenu(selected, moveToTile);
            }
            else if(silo)
            {
            	logger.info("Display Silo Cursor");

    			//Move the unit over and then start firing
    			if(autoMove(selected))	
    				siloEffect(selected);
    			
    			silo = false;
            }
            else if(special1)
            {
            	logger.info("Display Special1");
            	
            	//Move the unit over and then start firing
    			if(autoMove(selected))	
    				special1Effect(selected);
    			
    			special1 = false;
            }
            else if(special2)
            {
            	logger.info("Display Special2");
            	
            	//Move the unit over and then start firing
    			if(autoMove(selected))	
    				special2Effect(selected);
    			
    			special2 = false;
            }  
            //Targeted abilities
            else if(clickedTile.hasUnit())
            {
            	logger.info("Display Unit Targeting Options");
            	
            	Unit target = clickedTile.getUnit();
                
                if(fire) {
                    //Move the unit over and then start firing
                    if(autoMove(selected))
                        fireAtUnitEffect(selected, target);
                    
                    fire = false;
                } else if(repair) {
                    //Move the unit over and then start repairing
        			if(autoMove(selected))	
        				repairEffect(selected, target);
        			
        			repair = false;
                }
                else
                {
                    //if(m.find(new Location(cx,cy)).getUnit().isActive())
                	//if(m.find(new Location(cx,cy)).hasUnit())
                	//[CHANGE]
                	if(visibleAtXY(cursorXpos, cursorYpos))
                    {
                		//Selecting a unit at this position
                		logger.info("Selection of a unit");
                        selectUnitAction();
                    }
                	else
                	{
                        //Same as hitting m
                        getBattleMenu();
                        return;
                    }
                }
            }
            else if(clickedTile.getTerrain() instanceof Invention)
            {
            	logger.info("Display Invention Targeting Options");
            	
                if(fire)
                {
        		    Invention inv = (Invention) clickedTile.getTerrain();

        			//Move the unit over and then start firing
        			autoMove(selected);	
                    fireAtInventionEffect(selected, inv);
                }
                else
                {
                    //Same as hitting m
                    getBattleMenu();
                    return;
                }
            }
            else if(clickedTile.getTerrain() instanceof Property && !fire && !repair)
            {
            	logger.info("Display Build Menu");
            	
                currentMenu = BuildMenu.generateContext((Property)clickedTile.getTerrain(),b.getTurn(),b,this);
                if(currentMenu != null)
                {
                    bmenu = true;
                    String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    SFX.playClip(soundLocation + "/ok.wav");
                }
                else
                {
                    //Same as hitting m
                    getBattleMenu();
                    return;
                }
            }
            else
            {
            	logger.info("Display Battle Menu");
                //Same as hitting m
                getBattleMenu();
                return;
            }
        }
        else if(daystart)
        {
            logger.info("ending day start screen");
            if(!denyContinue)
            {
            	daystart = false;
            }
            /*
           if(b.getArmies()[b.getTurn()].hasAI())
           {
				AI temp = new Alpha1AI(b.getArmies()[b.getTurn()]);
               	b.getArmies()[b.getTurn()].ai = temp;
                b.getArmies()[b.getTurn()].runAI();
                Thread.yield();
                logger.info("post AI");
        	}
        	*/
        }
        else if(victory)
        {
            victory = false;
            endStats = true;
        }
        else if(endStats)
        {
            returnToMain();
        }
        else if(replay)
        {
            executeNextAction();
        }
    }
    
    public void setContextMenu(Unit user, Tile targTile)
    {		    
    	targetUnit = targTile.getUnit();
    
	    if(targetUnit != null &&
	       targetUnit != user)
	    {
	        //JOIN
	        if(targetUnit.getUnitType() == user.getUnitType() && 
	           canJoin(user, targetUnit))
	        {
	        	//If the join command can't work, don't let it work
	            //if(!joinCommand(selected, moveTempUnit))
	            //	return;
	        	//if(joinCommand(user, targetUnit) && currProp != null)
	        	//	currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the join command succeeds
	        	
	        	logger.info("JOIN-based context menu");
	        	
	        	//JOIN based context menu
	        	Location origLoc = user.getLocation();
	        	user.setLocation(moveToTile.getLocation()); 
	        	currentMenu = ContextMenu.generateContext(user,true,false,false,false,this);
	        	cmenu = true;
				user.setLocation(origLoc);
	        	return;
	        }
	        
	        //LOAD
	        else if(targetUnit instanceof Transport && 
	        		canLoad(user, (Transport)targetUnit))
	        {
	        	//If the load command can't work, don't let it work
	            //if(!loadCommand(selected, moveTempUnit))
	            //	return;
	        	//if(loadCommand(user, (Transport)targetUnit) && currProp != null)
	        	//	currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the load command succeeds
	        	
	        	logger.info("LOAD-based context menu");
	        	
	        	//LOAD based context menu
	        	Location origLoc = user.getLocation();
	        	user.setLocation(moveToTile.getLocation());
	        	currentMenu = ContextMenu.generateContext(user, false,true,false,false,this);
	        	cmenu = true;
				user.setLocation(origLoc);
	        	return;
	        }
        	logger.info("What?");
        	
	        //return;
	    }
	    /*
	    else if(targetUnit != null ||
	    		targetUnit != user)
	    {
	    */
        	logger.info("ANYTHING ELSE-based context menu");
        	
	        //safeMoveEffect(user);
        	Location origLoc = user.getLocation();
        	user.setLocation(moveToTile.getLocation());
			currentMenu = ContextMenu.generateContext(user, false,false,false,false, this);
			targetTilesWithinRange = ContextMenu.getContextTargs();
			cmenu = true;
			user.setLocation(origLoc);
			
			return;
			/*
	    }
	    
    	
    		*/
    }

	private void selectUnitAction() 
	{
		//select
		selected = map.find(new Location(cursorXpos,cursorYpos)).getUnit();
		move = true;
		outOfMoveRange = false;
		selected.calcMoveTraverse();
		selected.getPath().resetPath();
		String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
		SFX.playClip(soundLocation + "/check.wav");
	}

	private void fireAtInventionEffect(Unit user, Invention target) 
	{
		if(user.checkFireRange(new Location(cursorXpos,cursorYpos)))
		{
		    //if(inv.find(selected.getAmmo(),selected.getUnitType()) > -1)
		    if(canFireAtInvention(user, target))
		    {
		        //if(selected.getArmy().getSide() != m.find(new Location(cx,cy)).getUnit().getArmy().getSide()){
		        user.fire(target);
		        fire = false;
		        endUnitTurn(20,user,null);
		        //}
		    }
		}
	}

	private boolean canFireAtInvention(Unit user, Invention target) 
	{
		return user.damageCalc(target) > -1;
	}

	private void repairEffect(Unit user, Unit target) 
	{
        if(canUseRepair(user, target))
        {
			int repairCost = (int)(target.getPrice()/10* target.getRepairMod());
        	
        	target.heal(10);
        	b.getArmy(b.getTurn()).removeFunds(repairCost);
            
            if(!target.isNoResupplied())
                target.resupply();
            repair = false;
            endUnitTurn(10,user,target);
        }
	}
	
	public boolean canUseRepair(Unit user, Unit target)
	{		
		if(user != map.find(target).getUnit())
		{
		    if(user.checkAdjacent(new Location(cursorXpos,cursorYpos)))
		    {
		        if(user.getArmy() == target.getArmy())
		        {
		            if(target.getDisplayHP() != 10 && (target.getPrice()/10 * target.getRepairMod()) <= b.getArmy(b.getTurn()).getFunds()&& !target.isNoRepaired())
		            {
		            	return true;
		            }
		        }
		    }
		}
		
		return false;
	}

	private void fireAtUnitEffect(Unit user, Unit target) 
	{
        if(canFireAtUnit(user, target))
        {
            boolean gameEnd = selected.fire(map.find(new Location(cursorXpos,cursorYpos)).getUnit());
            
            fire = false;
            
            endUnitTurn(1,user,null);
            
            if(gameEnd)endBattle();
            
            if(Options.killedSelf)
            {
                Options.killedSelf = false;
                if(Options.snailGame)returnToServerScreen();
            }
        }
	}
	
	public boolean canFireAtUnit(Unit user, Unit target)
	{		
		if(selected != map.find(new Location(cursorXpos,cursorYpos)).getUnit() && user.displayDamageCalc(target) > -1)
		{
		    if(selected.getArmy().getSide() != map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide())
		    {
		        if(selected.checkFireRange(new Location(cursorXpos,cursorYpos)))
		        {
		            //if(!m.find(new Location(cx,cy)).getUnit().isHidden())
		        	if(visibleAtXY(cursorXpos,cursorYpos))
		            {
		            	return true;
		            }
		        }
		    }
		}
		
		return false;
	}

	private void special2Effect(Unit user)
	{
		if(canUseSpecial2(user)) 
		{
		    special2 = false;
		    user.getArmy().getCO().useSpecial2(user, (new Location(cursorXpos,cursorYpos)));
		    
		    endUnitTurn(23,user,null);
		}
	}

	public boolean canUseSpecial2(Unit user) 
	{
		return user.getArmy().getCO().canTargetSpecial2(user, (new Location(cursorXpos,cursorYpos)));
	}

	private void special1Effect(Unit user) 
	{
		if(canUseSpecial1(user)) 
		{
		    special1 = false;
		    user.getArmy().getCO().useSpecial1(user, (new Location(cursorXpos,cursorYpos)));
		    
		    endUnitTurn(22,user,null);
		}
	}

	public boolean canUseSpecial1(Unit user) 
	{
		return user.getArmy().getCO().canTargetSpecial1(user, (new Location(cursorXpos,cursorYpos)));
	}

	private void siloEffect(Unit user) 
	{
		silo = false;
		
		((Silo)map.find(selected.getLocation()).getTerrain()).launch();
		Animation launchup = new Animation(b, MiscGraphics.getMissileUp(), 1, 0, 0, selected.getLocation().getCol()*16, selected.getLocation().getRow()*16, selected.getLocation().getCol()*16, -40, 100, 100, 25, 0, 0);
		Animation launchdown = new Animation(b, MiscGraphics.getMissileUp(), 1, 0, 0, cursorXpos*16, 0, cursorXpos*16, cursorYpos*16, 100, 100, 25, 10 , 0);
		Animation temp = new Animation();
		temp.setSiloExplosion(new Location(cursorXpos,cursorYpos),b,0);
		
		launchup.setup(false,false);
		launchdown.setup(false,false);
		temp.setup(false,true);
		
		launchdown.linkTo(temp);
		launchup.linkTo(launchdown);
		
		launchup.start();
		
		endUnitTurn(8,user,null);
		//LAUNCH
		map.doExplosion(2,3,cursorXpos,cursorYpos, false);
	}

	private void moveCommand(Unit user)
	{
		//if(user.getMoveRange().checkMove(cx,cy) || (m.find(new Location(cx,cy)).hasUnit() && m.find(new Location(cx,cy)).getUnit() == selected))
		if(user.getMoveRange().checkMove(cursorXpos,cursorYpos) || (map.find(new Location(cursorXpos,cursorYpos)).hasUnit() && map.find(new Location(cursorXpos,cursorYpos)).getUnit() == selected))
		{
		    //OOZIUM MOVE OVERIDES EVERYTHING ELSE ^_^
		    //if(selected.getName().equals("Oozium"))
			if(selected instanceof Oozium)
		    {
		        ooziumMoveCommand((Oozium)user);
		    }
			else
			{
		        normalMoveCommand(user);
		    }
		}
                else{
                    if (beingLaunched) { //return to carrier
                        Transport trans;
                        trans = (Transport)tempCarrier;
                        trans.load(selected);
                        
                        map.remove(selected);
                        map.addUnit(tempCarrier);
                        
                        beingLaunched=false;
                        tempCarrier.setLaunched(false);
                        move = false;
                    }
                }
	}
	
	public boolean autoMove(Unit user)
	{
		if(selected instanceof Oozium)
	    {
			ooziumMoveCommand((Oozium)user);
			return false;
	    }
		else
		{
	        return autoMoveCommand(user);
	    }
	}

	private boolean autoMoveCommand(Unit user) 
	{
		Property currProp = null;
		originalLocation = user.getLocation();
		Location nextLoc = moveToTile.getLocation();
	    
    	move = false;
		selected.setMoved(false);
		if(map.find(originalLocation).getTerrain() instanceof Property) 
		{
		    originalcp = ((Property)map.find(originalLocation).getTerrain()).getCapturePoints();
		    currProp = (Property)map.find(originalLocation).getTerrain();
		}
		
        //new code
        if(beingLaunched && ((originalLocation.equals(nextLoc)) || !selected.getMoveRange().checkMove(nextLoc)))
        {
                    logger.info("Cancel the move!");
            //return to carrier
            Transport trans;
            trans = (Transport)tempCarrier;
            trans.load(selected);
            
            map.remove(selected);
            map.addUnit(tempCarrier);
            
            beingLaunched=false;
            tempCarrier.setLaunched(false);
            move = false;
            selected.setMoved(false);
            //selected = null;
        } 
        else if(!map.find(nextLoc).hasUnit() ||
                (map.find(nextLoc).getUnit().isHidden() &&
                (map.find(nextLoc).getUnit().getArmy().getSide() != user.getArmy().getSide()))) 
        {
		    Unit ambusher = user.move(nextLoc);
		    
		    if(ambusher != null)
		    {
		        ambushedMoveEffect(user);
		        return false;
		    } 
		    else 
		    {
                        logger.info("Hurray!");
                        return true;
		        //safeMoveEffect(user);
		    	//move = false;
		    }
		}
		else
		{
		    targetUnit = map.find(nextLoc).getUnit();
		    
		    if(targetUnit != user  && targetUnit.getDisplayHP() != 10)
		    {
		        //JOIN
		        if(targetUnit.getUnitType() == user.getUnitType())
		        {
		        	joinEffect(user, targetUnit);
		        	
		        	if(currProp != null)
		        		currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the join command succeeds		        	
		        }
		        
		    }
		        //LOAD
		        else if(targetUnit instanceof Transport && targetUnit != user)
		        {
		        	loadEffect(user, (Transport)targetUnit);
		        	
		        	if(currProp != null)
		        		currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the load command succeeds
		        }
		    else if (targetUnit != user)
		    {
                        logger.info("no!");
                        move = false;
                        //WHY ARE YOU CRUSHING YOUR OWN UNITS
		        //safeMoveEffect(user);
		    	//move = false;
                        map.move(selected, originalLocation);
                        selected.setLocation( originalLocation);
                        //DIE IN A FIRE
                        return false;
		    }
		}
        return true;
	}

	private void normalMoveCommand(Unit user) 
	{
		Property currProp = null;
		originalLocation = user.getLocation();
		
		if(map.find(originalLocation).getTerrain() instanceof Property) 
		{
		    originalcp = ((Property)map.find(originalLocation).getTerrain()).getCapturePoints();
		    currProp = (Property)map.find(originalLocation).getTerrain();
		}
		
        //new code
        if (beingLaunched && originalLocation.equals(new Location(cursorXpos,cursorYpos))) 
        {
            //return to carrier
            Transport trans;
            trans = (Transport)tempCarrier;
            trans.load(selected);
            
            map.remove(selected);
            map.addUnit(tempCarrier);
            
            beingLaunched=false;
                        tempCarrier.setLaunched(false);
            move = false;
            selected.setMoved(false);
            selected = null;
        } 
        else if(!map.find(new Location(cursorXpos,cursorYpos)).hasUnit() ||
                (map.find(new Location(cursorXpos,cursorYpos)).getUnit().isHidden() &&
                (map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide() != user.getArmy().getSide()))) 
        {
		    Unit ambusher = user.move(new Location(cursorXpos,cursorYpos));
		    
		    if(ambusher != null)
		    {
		        ambushedMoveEffect(user);
		    } 
		    else 
		    {
		        safeMoveEffect(user);
		    }
		}
		else
		{
		    //currTile = new Location(cx,cy);
		    targetUnit = map.find(new Location(cursorXpos,cursorYpos)).getUnit();
		    
		    if(targetUnit != user)
		    {
		        //JOIN
		        if(targetUnit.getUnitType() == user.getUnitType())
		        {
		        	//If the join command can't work, don't let it work
		            //if(!joinCommand(selected, moveTempUnit))
		            //	return;
		        	if(joinCommand(user, targetUnit) && currProp != null)
		        		currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the join command succeeds
		        }
		        
		        //LOAD
		        else if(targetUnit instanceof Transport)
		        {
		        	//If the load command can't work, don't let it work
		            //if(!loadCommand(selected, moveTempUnit))
		            //	return;
		        	if(loadCommand(user, (Transport)targetUnit) && currProp != null)
		        		currProp.setCapturePoints(currProp.getMaxCapturePoints()); //captures are reset if the load command succeeds
		        }
		    }
		    else
		    {
		        safeMoveEffect(user);
		    }
		}
	}

	private void safeMoveEffect(Unit user)
	{
		//Safe move effect, what?
		//Means that this move action will not cause the user unit to become ambushed
		//It also means that the unit will be given a chance to utilize its context
		//menu
		move = false;
                selected.setMoved(false);
		currentMenu = ContextMenu.generateContext(user,false,false,false,false, this);
		//currentMenu = ContextMenu.generateContext(user,user.getLocation(),false,false,false,false, this);
		targetTilesWithinRange = ContextMenu.getContextTargs();
		cmenu = true;
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
		SFX.playClip(soundLocation + "/ok.wav");
	}

	private void ambushedMoveEffect(Unit user) 
	{
		move = false;
		selected.setMoved(false);
		Army[] armies = b.getArmies();
		
		for(int i = 0; i < armies.length; i++) 
		{
		    if(user.getTrapper().getArmy().getSide() != armies[i].getSide()) 
		    {
		        armies[i].getCO().afterEnemyAction(user, 18, null, true);
		        
		        if(armies[i].getAltCO() != null)
		            armies[i].getAltCO().afterEnemyAction(user, 18, null, false);
		    }
		}
		
		endUnitTurn(18,user,user.getTrapper());
		
		if(backAllowed == false)
		{
		    backAllowed = true;
		}
	}

	private void ooziumMoveCommand(Oozium user) 
	{
		if(!map.find(new Location(cursorXpos,cursorYpos)).hasUnit() || map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide() != selected.getArmy().getSide())
		{
		    originalLocation = selected.getLocation();
		    move = false;
                    selected.setMoved(false);
		    //boolean gameEnd = selected.move(new Location(cx,cy));
		    Unit lastUnit = selected.move(new Location(cursorXpos,cursorYpos));
		    endUnitTurn(19,user,lastUnit);
		    if(lastUnit != null)endBattle();
		}
		else if(map.find(new Location(cursorXpos,cursorYpos)).hasUnit() && map.find(new Location(cursorXpos,cursorYpos)).getUnit() == selected)
		{
		    move = false;
                    selected.setMoved(false);
		    endUnitTurn(19,user,null);
		}
	}

	private void deployEffect() 
	{
		int newUnit = currentMenu.doMenuItem();
		
		if(newUnit != -1)
		{
		    bmenu = false;
		    buildUnit(map,map.find(new Location(cursorXpos,cursorYpos)),newUnit);
	        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
		    SFX.playClip(soundLocation + "/ok.wav");
		    //if(newUnit != -1){
		    b.getArmy(b.getTurn()).removeFunds(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getPrice());
		    //}
		    currentMenu = null;
		    Army[] armies = b.getArmies();
		    
		    for(int i = 0; i < armies.length; i++) 
		    {
		        if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide()!= armies[i].getSide()) 
		        {
		            armies[i].getCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
		            
		            if(armies[i].getAltCO() != null)
		                armies[i].getAltCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
		        }
		    }
		    map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
		    
		    if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO() != null)
		        map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
		    
		    b.updateFoW();
		    
		    if(b.getBattleOptions().isRecording())
		    	b.getReplay().push(new BuildEvent(newUnit,cursorXpos,cursorYpos,b.getDay(),b.getTurn()));
		}
	}

	private void unloadEffect(Transport user, int slot) 
	{
		//Transport trans = (Transport) selected;
		
		if(user.checkUnloadRange(new Location(cursorXpos,cursorYpos), slot))
		{
		    if(map.find(new Location(cursorXpos,cursorYpos)).hasUnit())
		    {
		        ambushedUnloadEffect(user);
		    }
		    else
		    {
		        Unit target = user.unload(slot);
		        
		        target.forceMove(new Location(cursorXpos,cursorYpos));
		        target.setActive(false);
		        
		        if(user.getUnitsCarried()>0)
		        {
		            currentMenu = ContextMenu.generateContext(selected,false,false,true,false,this);
		        	//currentMenu = ContextMenu.generateContext(selected,selected.getLocation(),false,false,true,false,this);
		            cmenu = true;
		            backAllowed = false;
		            //Activates once for unloading the first time.
		            
		            user.getArmy().getCO().afterAction(user, 6, target, true);
		            
		            if(user.getArmy().getAltCO() != null)
		            {
		            	user.getArmy().getAltCO().afterAction(user, 6, target, false);
		            }
		            
		            //adds action to the queue
		            int replayAction = 6;
		            
		            if(unload == 2)
		            {
		            	replayAction = 7;
		            }
		            
		            CWEvent tempev= new Action(replayAction,originalLocation.getCol(),originalLocation.getRow(),selected.getPath(),cursorXpos,cursorYpos,b.getDay(),b.getTurn());
		            logger.info("temp env ="+tempev);
		            b.getReplay().push(tempev);
		        }
		        else
		        {
		            endUnitTurn(6,user,target);
		            
		            if(backAllowed == false)
		            {
		            	backAllowed = true;
		            }
		        }		    
                    }
		}else{
                    //This, er, works for the first unload. Second unload, not so much
                    currentMenu = ContextMenu.generateContext(selected,false,false,true,false,this);
                    //currentMenu = ContextMenu.generateContext(selected,selected.getLocation(),false,false,true,false,this);
                    cmenu = true;
                    backAllowed = false;
                }
                    unload = 0;
	}

	private void ambushedUnloadEffect(Unit user) 
	{
		//ambush
		endUnitTurn(17,user,user.getTrapper());
		unload = 0;
		if(backAllowed == false)backAllowed = true;
	}

	private void contextMenuActions(Unit user) 
	{
		cmenu=false;
		move=false;
		int action = currentMenu.doMenuItem();
		logger.info("Selected Action=["+action+"]");
		
		switch(action){
		
			case UNIT_COMMANDS.UNDO:	
										user.setDirection(-1);
										logger.debug("UNIT_COMMAND: Undoing action");
										break;
										
			case UNIT_COMMANDS.FIRE:	
										fire = true;
									    if(targetTilesWithinRange.size() > 0) {
									    	currContextTarg = 0;
									    	cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
									    	cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
									    }
									    logger.debug("UNIT_COMMAND: Firing at X[" + cursorXpos +"] Y[" + cursorYpos +"]");
									    break;
			
			case UNIT_COMMANDS.CAPTURE:	
										if(autoMove(user)){
											captureEffect(user);
										}
										logger.debug("UNIT_COMMAND: Capturing");
										break;
										
			case UNIT_COMMANDS.RESUPPLY:	
										if(autoMove(user)){
											resupplyEffect(user);
										}
										logger.debug("UNIT_COMMAND: Resupplying");
										break;
			
			case UNIT_COMMANDS.JOIN: 	
										autoMove(user);
										logger.debug("UNIT_COMMAND: Joining");
										break;
										
			
			case UNIT_COMMANDS.LOAD: 	
										autoMove(user);
										logger.debug("UNIT_COMMAND: Loading");
										break;
			
			case UNIT_COMMANDS.UNLOAD_1: 
			    						unload = 1;
			    						logger.debug("UNIT_COMMAND: Unloading 1");
			    						break;
			
			case UNIT_COMMANDS.UNLOAD_2:
										unload = 2;
										logger.debug("UNIT_COMMAND: Unloading 2");
										break;
										
			case UNIT_COMMANDS.LAUNCH_SILO:
										silo = true;
										logger.debug("UNIT_COMMAND: Launching Silo");
										break;
			
			case UNIT_COMMANDS.EXPLODE:
										explodeEffect(user);
										logger.debug("UNIT_COMMAND: Exploding");
										break;
										
			case UNIT_COMMANDS.REPAIR:
										repair = true;
										logger.debug("UNIT_COMMAND: Repairing");
										break;
			
			case UNIT_COMMANDS.DIVE: 	
										if(autoMove(user)){
											diveEffect(user);
										}
										logger.debug("UNIT_COMMAND: Diving");
										break;
										
			case UNIT_COMMANDS.RISE:			
										if(autoMove(user)){
											riseEffect(user);
										}
										logger.debug("UNIT_COMMAND: rising");
										break;
										
			case UNIT_COMMANDS.HIDE:	
										if(autoMove(user)){
											hideEffect(user);
										}
										logger.debug("UNIT_COMMAND: Hiding");
										break;
										
			case UNIT_COMMANDS.APPEAR:	
										if(autoMove(user)){
											appearEffect(user); 
										}
										logger.debug("UNIT_COMMAND: Appearing");
										break;
										
			case UNIT_COMMANDS.SPECIAL1:
										special1 = true;
										logger.debug("UNIT_COMMAND: Special 1");
										break;
										
			case UNIT_COMMANDS.SPECIAL2:							
										special2 = true;
										logger.debug("UNIT_COMMAND: Special 2");
										break;
										
			case UNIT_COMMANDS.LAUNCH:	
							            takeoff = 1;
							            launching = ((Transport)selected).getSlot1();
							            launching.calcMoveTraverse();
							            logger.debug("UNIT_COMMAND: Launching");
							            break;
			
			case UNIT_COMMANDS.LAUNCH2:							
										takeoff = 2;
										launching = ((Transport)selected).getSlot2();
										launching.calcMoveTraverse();
										logger.debug("UNIT_COMMAND: Launching 2");
										break;
										
			case UNIT_COMMANDS.BUILD: 
										logger.info("It's building time!");
										currentMenu = BuildMenu.generateCarrierMenu(selected.getArmy().getFunds(),selected.getArmy().getCO(),b,this);
										carbmenu = true;
										cmenu = false;
										logger.debug("UNIT_COMMAND: Building");
										break;
			default:							
										if(autoMove(user)) {	
											waitEffect(user);
										}
										logger.debug("UNIT_COMMAND: Move");
										break;
										
		}
		
		if(!cmenu && !carbmenu){
			currentMenu = null;
		}
			
	}

	private void waitEffect(Unit user) 
	{
            
		endUnitTurn(0,user,null);
		if(backAllowed == false)backAllowed = true;
	}
        private void buildEffect(Unit user){
		int newUnit = currentMenu.doMenuItem();
		
		if(newUnit != -1)
		{
		    
                    Army carrierArmy = selected.getArmy();
                    map.remove(selected);
		    buildCarrierUnit(carrierArmy, map,map.find(new Location(cursorXpos,cursorYpos)),newUnit);
                    Unit nooUnit = map.find(new Location(cursorXpos,cursorYpos)).getUnit();
                    map.remove(nooUnit);
                    nooUnit.setLoc(new Location(cursorXpos,cursorYpos));
                    ((Carrier)selected).load(nooUnit);
        	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
		    SFX.playClip(soundLocation + "/ok.wav");
		    b.getArmy(b.getTurn()).removeFunds(nooUnit.getPrice()*4/5); 
                    // holy christ this is a sucky way to handle funds
                    map.addUnit(selected);
                    carbmenu = false;
		    currentMenu = null;
                    //This handles after action
		    Army[] armies = b.getArmies();
		    for(int i = 0; i < armies.length; i++) 
		    {
		        if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide()!= armies[i].getSide()) 
		        {
		            armies[i].getCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
		            
		            if(armies[i].getAltCO() != null)
		                armies[i].getAltCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
		        }
		    }
		    map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
		    
		    if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO() != null)
		        map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
                    //endUnitTurn(UNIT_COMMANDS.BUILD,selected,nooUnit);
                    selected.setActive(false);
                    selected = null;
		}
        }
        private void launchEffect(Carrier user, int slot)
        {
            user.setLaunched(true);
            tempCarrier = user;
            logger.info("we got this far man");
            map.remove(tempCarrier);
            launching = user.unload(slot);
            selected = launching;
            logger.info(selected.getName());
            
            //temporarily transfer the carrier into storage to make room for the launched unit
            beingLaunched = true;
            //launching.setLocation(trans.getLocation());
            moveToTile = map.find(new Location(cursorXpos, cursorYpos));
            this.setContextMenu(selected,moveToTile);
            /*if(autoMove(selected))
            {
                currentMenu = ContextMenu.generateContext(selected,false,false,false,false,this);
                //cmenu = true;
            } else {
                backAllowed = false;
                endUnitTurn(UNIT_COMMANDS.LAUNCH - 1 + slot, tempCarrier, selected);
            }*/
            
            /*launching.forceMove(user.getLocation());
            outOfMoveRange = false;
            selected.calcMoveTraverse();
            selected.getPath().resetPath();
            selected = launching;*/
            //backAllowed = false;
            /*
            if(user.getUnitsCarried()>0) {
                currentMenu = ContextMenu.generateContext(selected,false,false,false,true,this);
                cmenu = true;
                backAllowed = false;
                //Activates once for unloading the first time.
                
                user.getArmy().getCO().afterAction(user, UNIT_COMMANDS.LAUNCH, target, true);
                
                if(user.getArmy().getAltCO() != null) {
                    user.getArmy().getAltCO().afterAction(user, UNIT_COMMANDS.LAUNCH, target, false);
                }
                
                //adds action to the queue
                int replayAction = UNIT_COMMANDS.LAUNCH;
                
                if(takeoff == 2) {
                    replayAction = UNIT_COMMANDS.LAUNCH2;
                }
                
                CWEvent tempev= new Action(replayAction,originalLocation.getCol(),originalLocation.getRow(),selected.getPath(),cx,cy,b.getDay(),b.getTurn());
                logger.info(tempev);
                b.getReplay().push(tempev);
            } else {
                endUnitTurn(6,user,target);
                
                if(backAllowed == false) {
                    backAllowed = true;
                }
            }
            unload = 0;*/
            
        }
	private void appearEffect(Unit user) 
	{
		((Stealth)user).appear();
		endUnitTurn(14,user,null);
	}

	private void hideEffect(Unit user) 
	{
		((Stealth)user).hide();
		endUnitTurn(13,user,null);
	}

	private void riseEffect(Unit user) 
	{
		((Submarine)user).rise();
		endUnitTurn(12,user,null);
	}

	private void diveEffect(Unit user)
	{
		((Submarine)user).dive();
		endUnitTurn(11,user,null);
	}

	private void explodeEffect(Unit user)
	{
		//String x = JOptionPane.showInputDialog("About to Explode - Are you sure? y/n");
		
		Object[] options = {"GO BOOOOOOOM", "no boom"};
		int choice = JOptionPane.showOptionDialog(this, 
		     "Are you sure you want the Black Bomb to explode?", 
		     "BOOM", 
		     JOptionPane.YES_NO_CANCEL_OPTION, 
		     JOptionPane.INFORMATION_MESSAGE,
		     null,
		     options,
		     options[0]);
		
		//if(x != null && !x.equals("") && (x.charAt(0) == 'y' || x.charAt(0) == 'Y'))
		if(choice == JOptionPane.YES_OPTION)
		{
			if(autoMove(user))
			{
			    if(b.getBattleOptions().isBalance())
			    {
			    	map.doExplosion(2,3,user.getLocation().getCol(),user.getLocation().getRow(), false);
			    }
			    else
			    {
			    	map.doExplosion(3,5,user.getLocation().getCol(),user.getLocation().getRow(), false);
			    }
			    
			    user.eliminateUnit();
			    endUnitTurn(9,user,null);
			}
		}
		else
		{
		    cmenu = true;
		}
	}

	private void loadEffect(Unit user, Transport target) 
	{
		//Transport trans = (Transport) targetUnit;
		target.load(user);
		//Remove Unit being moved from map
		map.remove(user);
                user.setLoc(target.getLoc());
		//end Unit's turn
		endUnitTurn(5,user,(Unit)target);
	}

	private void joinEffect(Unit user, Unit target) 
	{		
		//Add Money if over 10HP
		int temphp = user.getDisplayHP() + target.getDisplayHP() - 10;
		
		if(temphp > 0)
			b.getArmy(b.getTurn()).addFunds((temphp*user.getPrice())/10);
		
		//add HP to unit being moved onto
		target.heal(user.getDisplayHP()*10);
		//add supplies to unit being moved onto
		target.addGas(user.getGas());
		target.addAmmo(user.getAmmo());
		//make the dived state of the unit being moved onto the same as the one moving
		target.setDived(user.isDived());
		//Remove Unit being moved
		map.remove(user);
		user.getArmy().removeUnit(user);
		//end Unit's turn
		user = target;
		endUnitTurn(4,user,target);
		//moveTempUnit.setActive(false);
		user = null;
	}

	private void resupplyEffect(Unit user)
	{
		((APC)user).resupplyAdjacent();
		endUnitTurn(16,user,null);
		if(backAllowed == false)backAllowed = true;
	}

	private void captureEffect(Unit user) 
	{
		boolean endGame = ((Property)map.find(user.getLocation()).getTerrain()).capture(selected);
		endUnitTurn(2,user,null);
		if(endGame)endBattle();
	}

	private void selectAction(Location loc) 
	{
		if(b.getArmy(b.getTurn()).getCO().validSelection(b.getMap().find(loc))) 
		{
		    logger.info("Selection cycle activated!");
		    
		    b.getArmy(b.getTurn()).getCO().selectAction(b.getMap().find(loc));
		    
		    if(b.getBattleOptions().isRecording())
		    {
		    	b.getReplay().push(new SelectionEvent(cursorXpos,cursorYpos,b.getDay(),b.getTurn()));
		    }
		    
		    return;
		} 
		else 
		{
		    b.getArmy(b.getTurn()).getCO().invalidSelection();
		    return;
		}
	}

	private void menuActions() 
	{
		menu=false;
		b.cursorLocation[b.getTurn()] = new Location(cursorXpos,cursorYpos);
		
		int menuselection = currentMenu.doMenuItem();
		currentMenu = null;
		
		if(menuselection == MENU_SEL.INTEL)
		{
		    intel = true;
		}
		else if(menuselection == MENU_SEL.OPTION)
		{
		    menu = true;
		    currentMenu = new OptionsMenu(this);
		}
		else if(menuselection == MENU_SEL.RESIZE_SCREEN)
		{
		    resizeScreen();
		}
		else if(menuselection == 4)
		{
		    if(scale < 4)
		        scale++;
		    else
		        scale = 1;
		    parentFrame.pack();
		}
		else if(menuselection == 5)
		{
		    //endBattle();
		    returnToMain();
		}
		else if(menuselection == MENU_SEL.YIELD)
		{
			yieldCommand();
		}
		else if(menuselection == MENU_SEL.TOGGLE_MUSIC)
		{
		    toggleMusic();
		}
		else if(menuselection == MENU_SEL.DELETE)
		{
		    delete = true;
		}
		else if(menuselection == 9)
		{
		    whatTheHellIsThisItLooksImportant();
		}
		else if(menuselection == MENU_SEL.SAVE_REPLAY)
		{
		    saveReplay();
		}
		else if(menuselection == MENU_SEL.END_TURN)
		{
		    logger.info("Turn Start");
		    daystart = true;
		    if(Options.isNetworkGame() && Options.getSend())denyContinue = true;
		    if(Options.snailGame){
		        returnToServerScreen();
		    }
		}
		else if(menuselection == 12)
		{
		    info = true;
		}
		else if(menuselection == MENU_SEL.SAVE_GAME)
		{
		    saveGame();
		}
		else if(menuselection == MENU_SEL.LOAD_GAME)
		{
		    loadLocalGame();
		}
		else if(menuselection == MENU_SEL.MAYBE_ERROR)
		{
		    endBattle();
		}
		
		moveCursorTo(b.cursorLocation[b.getTurn()]);
	}

	private void whatTheHellIsThisItLooksImportant() 
	{
		if(b.getBattleOptions().isRecording()){
		    if(!Options.isNetworkGame() && !Options.snailGame){
		        logger.info("Replay"+ b.getReplay());
		        JFileChooser fc = new JFileChooser();
		        fc.setDialogTitle("Save replay file...");
		        fc.setCurrentDirectory(new File("./"));
		        fc.setApproveButtonText("Save");
		        /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		                "CW Replay Files", "replay");
		        fc.setFileFilter(filter);*/
		        int returnVal = fc.showOpenDialog(this);
		        
		        if(returnVal != 1)
		        {
		            String filename = fc.getSelectedFile().getPath();
		            
		            if(filename.length()<8 || !filename.substring(filename.length()-7).equals(REPLAY_FILE_EXT))
		            {
		            	filename += REPLAY_FILE_EXT;
		            }
		            
		            Mission.saveReplay(filename);
		        }
		    }
		}
	}

	private void saveGame() {
		if(!Options.snailGame){
		    logger.info("Save");
		    JFileChooser fc = new JFileChooser();
		    fc.setDialogTitle("Save game...");
		    fc.setCurrentDirectory(new File("./"));
		    fc.setApproveButtonText("Save");
		    /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "CW Save Files", "save");
		    fc.setFileFilter(filter);*/
		    int returnVal = fc.showOpenDialog(this);
		    
		    if(returnVal != 1){
		        String x = fc.getSelectedFile().getPath();
		        if(x.length()<6 || !x.substring(x.length()-5).equals(SAVE_FILE_EXT))x += SAVE_FILE_EXT;
		        Mission.saveMission(x);
		    }
		}else{
		    Mission.saveMission(TEMPORARYSAVE_SAVE_FILENAME);
		    b.sendFile("usave.pl", Options.gamename,TEMPORARYSAVE_SAVE_FILENAME);
		}
	}

	private void loadLocalGame() {
		logger.info("Load");
		boolean validSave = false;
		
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Load game...");
		fc.setCurrentDirectory(new File("./"));
		fc.setApproveButtonText("Load");
		/*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "CW Save Files", "save");
		fc.setFileFilter(filter);*/
		int returnVal = fc.showOpenDialog(this);
		
		if(returnVal != 1){
		    String filename = fc.getSelectedFile().getPath();
		    File saveFile = new File(filename);
		    if(saveFile.exists()){
		        validSave = true;
		    }
		    
		    if(validSave)Mission.loadMission(filename);
		}
	}

	private void saveReplay() {
		if(!Options.isNetworkGame() && !Options.snailGame)
		{
		    logger.info("Replay"+b.getReplay());
		}
	}

	private void resizeScreen() {
		MAX_TILEW = getPositiveInteger("Enter a width in tiles for the screen",16);
		MAX_TILEH = getPositiveInteger("Enter a height in tiles for the screen",12);
		parentFrame.pack();
	}

	private void toggleMusic() {
		if(Options.isMusicOn())Options.turnMusicOff();
		else Options.turnMusicOn();
		if(Options.isMusicOn())Music.startMusic(COList.getIndex(b.getArmy(b.getTurn()).getCO()));
	}

	private void yieldCommand() 
	{
		//YIELD
		//String yield = JOptionPane.showInputDialog("About to Yield!! - Are you sure? y/n");
		
		Object[] options = {"Yes I'm boned :(", "RAWR PVP KEEP FIGHTIN"};
		int choice = JOptionPane.showOptionDialog(this, 
		     "Wait! Are you sure you want to yield?", 
		     "Yielding?!", 
		     JOptionPane.YES_NO_CANCEL_OPTION, 
		     JOptionPane.WARNING_MESSAGE,
		     null,
		     options,
		     options[0]);
		
		logger.info("choice = " + choice);

		//if(yield != null && !yield.equals("") && (yield.charAt(0) == 'y' || yield.charAt(0) == 'Y'))
		if(choice == JOptionPane.YES_OPTION)
		{
		    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(7,b.getDay(),b.getTurn()));

		    boolean defeat = b.removeArmy(b.getArmy(b.getTurn()),null,false);

		    if(defeat)
		    {
		        endBattle();
		    }
		    if(!defeat && Options.snailGame)
		    {
		        returnToServerScreen();
		    }
		}
		else
		{
		    //menu = true;
		}
	}

	private boolean deleteCommand(Location loc) 
	{
		if(canDelete(loc))
		{
			Unit dunit = map.find(loc).getUnit();

            logger.info("Delete Unit");
            dunit.eliminateUnit();
            
            if(b.getBattleOptions().isRecording())
            {
            	b.getReplay().push(new DeleteEvent(cursorXpos,cursorYpos,b.getDay(),b.getTurn()));
            }
            
            boolean defeat = false;
            
            if(dunit.isRout())
            {
                defeat = b.removeArmy(dunit.getArmy(),dunit.getArmy(),false);
                
                if(defeat)
                {
                	endBattle();
                }
                if(!defeat && Options.snailGame)
                {
                	returnToServerScreen();
                }
            }
            delete = false;
            
            return true;
		    
		}
		delete = false;
		
		return false;
	}
	
	public boolean canDelete(Location loc)
	{		
		if(map.find(loc).hasUnit())
		{
			Unit dunit = map.find(loc).getUnit();
	    
		    if(dunit.getArmy()==b.getArmy(b.getTurn()))
		    {
		        if(dunit.isActive() || dunit.getArmy().getCO().canAlwaysDelete())
		        	return true;
		    }
		}
		
	    return false;
	}

	private boolean loadCommand(Unit user, Transport target) 
	{
		//Transport trans = (Transport)target;
		if(canLoad(user, target))
		{
		    move = false;
                    selected.setMoved(false);
		    currentMenu = ContextMenu.generateContext(selected,false,true,false,false,this);
		    //currentMenu = ContextMenu.generateContext(selected,selected.getLocation(),false,true,false,false,this);
		    cmenu = true;
		    
		    return true;
		}
		
		return false;
	}

	public boolean canLoad(Unit user, Transport targ) 
	{
		//The load conditions are:
		//
		// (1) The transport needs to be able to carry the unit
		// (2) The transport needs to have enough room to carry the unit
		// (3) The transport needs to have the loading ability be enabled
		//
		
		return targ.canCarry(user.getUnitType()) && targ.roomAvailable() && !targ.isNoLoad();
	}

	private boolean joinCommand(Unit user, Unit target) 
	{
		//boolean success = false;
		
		if(canJoin(user, target))
		{
	        move = false;
                selected.setMoved(false);
	        currentMenu = ContextMenu.generateContext(selected,true,false,false,false,this);
	        //currentMenu = ContextMenu.generateContext(selected,selected.getLocation(),true,false,false,false,this);
	        cmenu = true;
	        
	        return true;
		}
		
		return false;
	}
	
	public boolean canJoin(Unit user, Unit target)
	{
		//The join conditions are:
		//
		// (1) The two units must belong to the same army
		// (2) The target unit must have 10 or less displayed HP
		// (3) Both units can use the join command
		// (4) If the unit-type of the units is Transport, the two unit
		//     must not have any units loaded
		//
		
        if(target != user)
        {
            //JOIN
            if(target.getUnitType() == user.getUnitType())
            {
                if(target.getArmy() == user.getArmy())
                {
                    if(target.getDisplayHP()!=10 && !target.isNoJoin() && !user.isNoJoin())
                    {
                        if(target instanceof Transport)
                        {
                            if(((Transport)target).getUnitsCarried()!=0 || ((Transport)user).getUnitsCarried()!=0)
                            {
                            	return false;
                            }
                        }
                        
                        return true;
                    }
                }
            }
        }

        return false;
	}
    
    public void pressedB(){
        if(!b.diagQueue.isEmpty())
            b.diagQueue.get(0).pressedA();
        if(b.animlock) {
            for(int i = 0; i<b.getLayerOne().size(); i++) {
                b.getLayerOne().get(i).removeTimers();
                //b.getLayerOne().remove(i);
            }
            for(int i = 0; i<b.getLayerTwo().size(); i++) {
                b.getLayerTwo().get(i).removeTimers();
                //b.getLayerTwo().remove(i);
            }
            for(int i = 0; i<b.getLayerThree().size(); i++) {
                b.getLayerThree().get(i).removeTimers();
                //b.getLayerThree().remove(i);
            }
            for(int i = 0; i<b.getLayerFour().size(); i++) {
                b.getLayerFour().get(i).removeTimers();
                //b.getLayerFour().remove(i);
            }
            for(int i = 0; i<b.queue.size(); i++) {
                b.queue.get(i).removeTimers();
                //b.queue.remove(i);
            }
            
            b.animlock = false;
            return;
        }
        
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) {
            b.getArmy(b.getTurn()).getCO().cancelSelection();
        }
        if(delete) {
            delete = false;
        }
        if(!daystart && !intel && !victory && !endStats && !replay && !info)
        {
            if(backAllowed)
            {
                //if a normal menu is active, turn it off without taking any action
                if(menu || bmenu || carbmenu)
                {
                    currentMenu = null;
                    menu = false;
                    bmenu = false;
                    carbmenu = false;
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/cancel.wav");
                }
                else if(fireRange)
                {
                    //Firing Range mode is active, turn it off
                    fireRange = false;
                    //selected = null;
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/cancel.wav");
                }
                else if(cmenu || fire || (unload > 0) || (takeoff > 0) || silo || repair || special1 || special2)
                {
                    //if move already done: reverse the move, end the mode, and deselect
                    //NOTE: SEARCH RETURN TO CARRIER TO LOOK UP HOW TO REFRESH THE UNIT RIGHT
                    //selected.undoMove(originalLocation,originalcp);
                    
                    if (beingLaunched) { //return to carrier
                        Transport trans;
                        trans = (Transport)tempCarrier;
                        trans.load(selected);
                        
                        map.remove(selected);
                        map.addUnit(tempCarrier);
                        
                        beingLaunched=false;
                                    tempCarrier.setLaunched(false);
                    }
                    if(cmenu){
                        cmenu = false;
                        currentMenu = null;
                    }else if(fire){
                        fire = false;
                    }else if(unload > 0){
                        unload = 0;
                    }else if(takeoff > 0){
                        takeoff = 0;
                    }else if(silo){
                        silo = false;
                    }else if(repair){
                        repair = false;
                    }else if (special1){
                        special1 = false;
                    }else if (special2){
                        special2 = false;
                    }
                    //selected = null;
                }
                else if(move)
                {
                    //if in move mode, end it and deselect the unit
                    if (beingLaunched) 
                    { //return to carrier
                        Transport trans;
                        trans = (Transport)tempCarrier;
                        trans.load(selected);
                        
                        map.remove(selected);
                        map.addUnit(tempCarrier);
                        
                        beingLaunched=false;
            tempCarrier.setLaunched(false);
                    }
                    
                    
                    move = false;
                    selected.setMoved(false);
                    selected.setDirection(-1);
                    selected = null;
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/cancel.wav");
                }
                else
                {
                    //Nothing is on, turn on firing range mode if possible
                    if(map.find(new Location(cursorXpos,cursorYpos)).hasUnit())
                    {
                        selected=map.find(new Location(cursorXpos,cursorYpos)).getUnit();
                        
                        // [CHANGE]
                        if(visibleAtXY(cursorXpos, cursorYpos))
                        {
                            selected.calcMoveTraverse();
                            fireRange = true;
                    		String soundLocation = ResourceLoader.properties.getProperty("soundLocation");                            
                            SFX.playClip(soundLocation + "/check.wav");
                        }
                        else
                        {
                            selected = null;
                        }
                    }
                }
            }
        }else if(intel){
            intel = false;
        }else if(info){
            info = false;
        }else if(endStats){
            victory = true;
            endStats = false;
        }else if(replay){
            replay = false;
            b.getReplay().clear();
        }
    }
    
    private void executeNextAction(){
        executeNextAction(b.getNextReplayEvent());
    }
    
    public void executeNextAction(CWEvent event){
        logger.info("Next action event= ["+ event +"]");
        if(event == null){
            //end of replay, resume
            replay = false;
        }else{
            switch(event.getType()){
                case 0:	
                	logger.info("In case 0");
                    //action
                    Action a = (Action)event;
                    selected = map.find(new Location(a.getUnitX(),a.getUnitY())).getUnit();
                    originalLocation = new Location(a.getUnitX(),a.getUnitY());
                    if(a.getID()==4){
                        //join
                        targetUnit = map.find(new Location(a.getX(),a.getY())).getUnit();
                        //Add Money if over 10HP
                        int temphp = selected.getDisplayHP() + targetUnit.getDisplayHP() - 10;
                        if(temphp > 0)b.getArmy(b.getTurn()).addFunds((temphp*selected.getPrice())/10);
                        //add HP to unit being moved onto
                        targetUnit.heal(selected.getDisplayHP()*10);
                        //add supplies to unit being moved onto
                        targetUnit.addGas(selected.getGas());
                        targetUnit.addAmmo(selected.getAmmo());
                        //make the dived state of the unit being moved onto the same as the one moving
                        targetUnit.setDived(selected.isDived());
                        //Remove Unit being moved
                        map.remove(selected);
                        selected.getArmy().removeUnit(selected);
                        //end Unit's turn
                        //NEEDS TESTING
                        selected = targetUnit;
                        endUnitTurn(4,selected,null);
                        //moveTempUnit.setActive(false);
                        selected = null;
                        break;
                    }else if(a.getID()==5){
                    	logger.info("In case 0 else if");
                        //load
                        targetUnit = map.find(new Location(a.getX(),a.getY())).getUnit();
                        loadEffect(selected, (Transport)targetUnit);
                        break;
                    }
                    else
                    {
                        if(selected instanceof Oozium)
                        {
                        	logger.info("In case 0 oozium");
                            //Ooziums act differently
                        	Oozium user = (Oozium)selected;
                        	user.calcMoveTraverse();
                        	user.setPath(a.getPath());
                            if(map.find(new Location(a.getX(),a.getY())).hasUnit() && map.find(new Location(a.getX(),a.getY())).getUnit() == selected)
                            {
                                endUnitTurn(19,user,null);
                            }
                            else
                            {
                                //boolean gameEnd = selected.move(new Location(a.getX(),a.getY()));
                            	Unit lastUnit = user.move(new Location(a.getX(),a.getY()));
                                endUnitTurn(19,user,null);
                                if(lastUnit != null)endBattle();
                            }
                        }else{
                            //move
                        	logger.info("In case 0 else else");
                            selected.calcMoveTraverse();
                            selected.setPath(a.getPath());
                            selected.move(a.getPath().findEndCoordinates());
                            //action
                            switch(a.getID()){
                                case 0:
                                	logger.info("In case 0 again");
                                	//wait
                                    endUnitTurn(0,selected,null);
                                    break;
                                case 1:
                                	logger.info("In case 1");
                                    //fire
                                    if(map.find(new Location(a.getX(),a.getY())).hasUnit()){
                                        //unit
                                        boolean gameEnd = selected.fire(map.find(new Location(a.getX(),a.getY())).getUnit());
                                        fire = false;
                                        endUnitTurn(1,selected,null);
                                        if(gameEnd)endBattle();
                                    }else{
                                        //invention
                                        Invention inv = (Invention) map.find(new Location(a.getX(),a.getY())).getTerrain();
                                        selected.fire(inv);
                                        fire = false;
                                        endUnitTurn(20,selected,null);
                                    }
                                    break;
                                case 2:
                                	logger.info("In case 2");
                                    captureEffect(selected);
                                    break;
                                case 3:
                                	logger.info("In case 3");
                                    //resupply
                                    ((APC)selected).resupplyAdjacent();
                                    endUnitTurn(16,selected,null);
                                    break;
                                case 6:
                                	logger.info("In case 6");
                                    //unload slot 1
                                case 7:
                                	logger.info("In case 7");
                                    //unload slot 2
                                    Transport trans = (Transport) selected;
                                    if(map.find(new Location(a.getX(),a.getY())).hasUnit()){
                                        //ambush
                                        endUnitTurn(17,selected,null);
                                    }else{
                                    	logger.info("In case 7 else");
                                        int tempUnload = 1;
                                        if(a.getID()==7)tempUnload = 2;
                                        Unit unloading = trans.unload(tempUnload);
                                        unloading.forceMove(new Location(a.getX(),a.getY()));
                                        unloading.setActive(false);
                                        if(trans.getUnitsCarried()>0){
                                            //Activates once for unloading the first time.
                                            trans.getArmy().getCO().afterAction(trans, 6, unloading, true);
                                            if(trans.getArmy().getAltCO() != null)
                                                trans.getArmy().getAltCO().afterAction(trans, 6, unloading, false);
                                            //do next action ASAP
                                            Action a2 = (Action) b.getNextReplayEvent();
                                            logger.info("a2 ="+a2);
                                            int nextAction = a2.getID();
                                            if(nextAction==0){
                                                endUnitTurn(0,selected,null);
                                            }else if(nextAction == 6){
                                                if(map.find(new Location(a2.getX(),a2.getY())).hasUnit()){
                                                    //ambush
                                                    endUnitTurn(17,selected,null);
                                                }else{
                                                    unloading = trans.unload(1);
                                                    unloading.forceMove(new Location(a2.getX(),a2.getY()));
                                                    unloading.setActive(false);
                                                }
                                            }else{
                                                endUnitTurn(6,selected,unloading);
                                            }
                                        }
                                    }
                                    break;
                                case 8:
                                	logger.info("In case 8");
                                    //launch
                                    ((Silo)map.find(selected.getLocation()).getTerrain()).launch();
                                    endUnitTurn(8,selected,null);
                                    map.doExplosion(2,3,a.getX(),a.getY(), false);
                                    break;
                                case 9:
                                	logger.info("In case 9");
                                    //explode
                                    if(b.getBattleOptions().isBalance())
                                        map.doExplosion(2,3,selected.getLocation().getCol(),selected.getLocation().getRow(), false);
                                    else
                                        map.doExplosion(3,5,selected.getLocation().getCol(),selected.getLocation().getRow(), false);
                                    selected.eliminateUnit();
                                    endUnitTurn(9,selected,null);
                                    break;
                                case 10:
                                	logger.info("In case 10");
                                    //repair
                                    Unit temp = map.find(new Location(a.getX(),a.getY())).getUnit();
                                    if(temp.getDisplayHP() != 10 && temp.getPrice()/10 <= b.getArmy(b.getTurn()).getFunds()){
                                        temp.heal(10);
                                        b.getArmy(b.getTurn()).removeFunds(temp.getPrice()/10);
                                    }
                                    temp.resupply();
                                    endUnitTurn(10,selected,temp);
                                    break;
                                case 11:
                                	logger.info("In case 11");
                                    diveEffect(selected);
                                    break;
                                case 12:
                                	logger.info("In case 12");
                                    riseEffect(selected);
                                    break;
                                case 13:
                                	logger.info("In case 13");
                                    hideEffect(selected);
                                    break;
                                case 14:
                                	logger.info("In case 14");
                                    appearEffect(selected);
                                    break;
                                case 22:
                                	logger.info("In case 22");
                                    //Special1
                                    selected.getArmy().getCO().useSpecial1(selected, (new Location(cursorXpos,cursorYpos)));
                                    endUnitTurn(22,selected,null);
                                case 23:
                                	logger.info("In case 23");
                                    selected.getArmy().getCO().useSpecial2(selected, (new Location(cursorXpos,cursorYpos)));
                                    endUnitTurn(23,selected,null);
                            }
                        }
                    }
                    moveCursorTo(new Location(a.getX(),a.getY()));
                    break;
                case 1:
                    //build
                    BuildEvent be = (BuildEvent)event;
                    moveCursorTo(new Location(be.getX(),be.getY()));
                    buildUnit(map,map.find(new Location(be.getX(),be.getY())),be.getUnitType());
                    b.getArmy(b.getTurn()).removeFunds(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getPrice());
                    Army[] armies = b.getArmies();
                    for(int i = 0; i < armies.length; i++) {
                        if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getSide()!= armies[i].getSide()) {
                            armies[i].getCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
                            if(armies[i].getAltCO()!= null)
                                armies[i].getAltCO().afterEnemyAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
                        }
                    }
                    map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, true);
                    if(map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO() != null)
                        map.find(new Location(cursorXpos,cursorYpos)).getUnit().getArmy().getAltCO().afterAction(map.find(new Location(cursorXpos,cursorYpos)).getUnit(), 15, null, false);
                    
                    b.updateFoW();
                    break;
                case 2:
                    //COP
                    b.getArmy(b.getTurn()).getCO().activateCOP();
                    break;
                case 3:
                    //SCOP
                    b.getArmy(b.getTurn()).getCO().activateSCOP();
                    break;
                case 4:
                    //Tag
                    b.getArmy(b.getTurn()).tagBreak();
                    break;
                case 5:
                    //Swap
                    if(b.getArmy(b.getTurn()).canTagSwap()){
                        b.getArmy(b.getTurn()).swap();
                        b.getArmy(b.getTurn()).setAllActive(true);
                        b.getArmy(b.getTurn()).getCO().activateSCOP();
                    }else{
                        b.getArmy(b.getTurn()).swap();
                        boolean endGame = b.endTurn();
                        if(endGame)endBattle();
                    }
                    break;
                case 6:
                    //End Turn
                    boolean endGame = b.endTurn();
                    if(endGame)endBattle();
                    break;
                case 7:
                    //Yield
                    boolean defeat = b.removeArmy(b.getArmy(b.getTurn()),null,false);
                    if(defeat){
                        endBattle();
                    }
                    break;
                case 8:
                    //Delete
                    DeleteEvent de = (DeleteEvent) event;
                    Unit dunit = map.find(new Location(de.getX(),de.getY())).getUnit();
                    moveCursorTo(new Location(de.getX(),de.getY()));
                    dunit.eliminateUnit();
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/explode.wav");
                    boolean defeat2 = false;
                    if(dunit.isRout())defeat2 = b.removeArmy(dunit.getArmy(),dunit.getArmy(),false);
                    if(defeat2)endBattle();
                    break;
                case 9:
                    //Selection
                    SelectionEvent se = (SelectionEvent) event;
                    moveCursorTo(new Location(se.getX(),se.getY()));
                    if(b.getArmy(b.getTurn()).getCO().validSelection(b.getMap().find(new Location(se.getX(),se.getY())))) {
                        b.getArmy(b.getTurn()).getCO().selectAction(map.find(new Location(se.getX(),se.getY())));
                    } else
                        b.getArmy(b.getTurn()).getCO().cancelSelection();
                    break;
            }
        }
    }
    
    private void endUnitTurn(int action, Unit user, Unit target)
    {
    	user.setActive(false);
        b.updateFoW();
        user.setIfHidden();
        user.checkForStealth(null);
        user.checkForStealth(originalLocation);
        //Vimes' AfterAction() code (improved by urusan)
        Army[] armies = b.getArmies();
        for(int i = 0; i < armies.length; i++) 
        {
            if(selected.getArmy().getSide()!= armies[i].getSide()) 
            {
                armies[i].getCO().afterEnemyAction(user, action, target, true);
                if(armies[i].getAltCO() != null)
                    armies[i].getAltCO().afterEnemyAction(user, action, target, false);
            }
        }
        user.getArmy().getCO().afterAction(user, action, target, true);
        if(user.getArmy().getAltCO() != null)
        	user.getArmy().getAltCO().afterAction(user, action, target, false);
        //if recording is on, add the action to the replay queue
        if(b.getBattleOptions().isRecording() && !replay){
            int replayAction = -1;
            if(action == 16){
                replayAction = 3;
            }else if(action == 6 || action == 17){
                if(unload == 2)replayAction = 7;
                else replayAction = 6;
            }else if(action == 18){
                replayAction = 0;
            }else if(action == 19){
                replayAction = 0;
            }else if(action == 20){
                replayAction = 1;
            }else{
                replayAction = action;
            }
            if (beingLaunched) {
                map.addUnit(tempCarrier);
                tempCarrier.setActive(false);
                tempCarrier = null;
                beingLaunched=false;
                unload = 0;
            }
            if(replayAction >= 0){
                CWEvent tempev= new Action(replayAction,originalLocation.getCol(),originalLocation.getRow(),selected.getPath(),cursorXpos,cursorYpos,b.getDay(),b.getTurn());
                logger.info("tempev="+tempev);
                b.getReplay().push(tempev);
            }
        }
        
        logger.info("Deselect unit");
        selected = null;
    }
    
    private void getBattleMenu()
    {
        //Same as hitting m
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        SFX.playClip(soundLocation + "/ok.wav");
        
        if(selected == null)
        {
            if(!bmenu && !cmenu && !carbmenu)
            {
                if(!menu)
                {
                    menu=true;
                    currentMenu = BattleMenu.generateContext(b,this);
                }
                else 
                {
                	menu=false;
                }
            }
            else if(bmenu)
            {
            	logger.info("bmenu is active!");
            }
            else if(cmenu)
            {
            	logger.info("cmenu is active!");
            }
            else if(carbmenu)
            {
                logger.info("carbmenu is active!");
            }
        }
        else
        {
        	logger.info("You are still selecting something!");
                logger.info("And that something is:" + selected);
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
	
	public boolean isFiring()
	{
		return fire;
	}
    
    public void setDayStart(boolean b){
        daystart = b;
    }
    
    public int getSX() {
        return sx;
    }
    public int getSY() {
        return sy;
    }
    
    //This class deals with keypresses
    class BSKeyControl implements KeyListener
    {
        BattleScreen parentScreen;
        
        public void setPScreen(BattleScreen b)
        {
        	parentScreen = b;
        }
        
        public void keyTyped(KeyEvent e) {}
        
        public void keyPressed(KeyEvent e) 
        {
            int keypress = e.getKeyCode();
            
            if(!daystart && !intel && !victory && !endStats && !replay)
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
                else if(keypress == Options.bkey){
                    pressedB();
                }
                else if(keypress == Options.menu)
                {
                    menuActions();
                }
                else if(keypress == Options.nextunit)
                {
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/maptick.wav");
                    if(selected == null){
                        if(!bmenu && !cmenu && !menu && !carbmenu){
                            Unit[] unitList = b.getArmy(b.getTurn()).getUnits();
                            if(unitList != null){
                                if(unitCyclePosition>=unitList.length)unitCyclePosition=0;
                                boolean found = false;
                                for(int i=unitCyclePosition; i<unitList.length; i++){
                                    if(unitList[i].isActive() && !unitList[i].isInTransport()){
                                        moveCursorTo(unitList[i].getLocation());
                                        unitCyclePosition=i+1;
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found){
                                    for(int i=0; i<unitList.length; i++){
                                        if(unitList[i].isActive() && !unitList[i].isInTransport()){
                                            moveCursorTo(unitList[i].getLocation());
                                            unitCyclePosition=i+1;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if(keypress == Options.minimap)
                {
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/minimap.wav");
                    if(!minimap){
                        minimap = true;
                    }else minimap=false;
                }
            }
            else if(daystart)
            {
                if(keypress == Options.akey)
                {
                    pressedA();
                }
            }
            else if(intel)
            {
                if(keypress == Options.bkey)
                {
                    pressedB();
                    SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/cancel.wav");
                }
            }
            else if(victory)
            {
                if(keypress == Options.akey){
                    pressedA();
                }
            }
            else if(endStats)
            {
                endStatsActions(keypress);
            }
            else if(replay)
            {
                replayActions(keypress);
            }
        }

		private void replayActions(int keypress) {
			if(keypress == Options.akey){
			    pressedA();
			}else if(keypress == Options.bkey){
			    pressedB();
			}else if(keypress == Options.left){
			    if(map.onMap(cursorXpos-1,cursorYpos)){
			        cursorXpos--;
			        if(cursorXpos < sx/16+2 && sx > 0)sx -= 16;
			    }
			}else if(keypress == Options.right){
			    if(map.onMap(cursorXpos+1,cursorYpos)){
			        cursorXpos++;
			        if(cursorXpos >= sx/16+MAX_TILEW-2 && cursorXpos < map.getMaxCol()-2)sx += 16;
			    }
			}else if(keypress == Options.up){
			    if(map.onMap(cursorXpos,cursorYpos-1)){
			        cursorYpos--;
			        if(cursorYpos < sy/16+2 && sy > 0)sy -= 16;
			    }
			}else if(keypress == Options.down){
			    if(map.onMap(cursorXpos,cursorYpos+1)){
			        cursorYpos++;
			        if(cursorYpos >= sy/16+MAX_TILEH-2 && cursorYpos < map.getMaxRow()-2)sy += 16;
			    }
			}else if(keypress == Options.fogkey){
			    if(b.isFog()){
			        b.setFog(false);
			    }else{
			        b.setFog(true);
			    }
			    b.calculateFoW();
			}else if(keypress == Options.intelkey){
			    intel = true;
			}
		}

		private void endStatsActions(int keypress) {
			if(keypress == Options.akey)
			{
			    pressedA();
			}
			else if(keypress == Options.bkey)
			{
			    pressedB();
			}
			else if(keypress == Options.left)
			{
			    statArmyIndex--;
			    if(statArmyIndex < 0)statArmyIndex = 0;
			}
			else if(keypress == Options.right)
			{
			    statArmyIndex++;
			    if(b.getStatArmy(statArmyIndex) == null)statArmyIndex--;
			}
			else if(keypress == Options.up)
			{
			    statType--;
			    if(statType < 0)statType = 0;
			}
			else if(keypress == Options.down)
			{
			    statType++;
			    if(statType > 2)statType = 2;
			}
		}

		private void menuActions() {
	        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
			SFX.playClip(soundLocation + "/ok.wav");
			if(selected == null){
			    if(!bmenu && !cmenu && !carbmenu){
			        if(!menu){
			            menu=true;
			            //currentMenu = new ContextMenu(true,false,false,true,true,false,false,false);
			            //currentMenu = new ContextMenu(false,false,true,false,false,false,true,true);
			            //currentMenu = ContextMenu.generateContext(selected);
			            currentMenu = BattleMenu.generateContext(b,parentScreen);
			        }else menu=false;
			    }
			}
		}

		private void keyRightActions() {
			//[SET INFO BOXES:RIGHT]
			//move Terrain, Unit and Trans boxes if necessary
			setInfoBoxXYs();
			
			if(info){
			    if(alt) //If we are on an alternate CO, move to the main CO
			    {
			        alt = false;
			        infono++;
			    } else { //If we are on a main CO
			        if(b.getArmy(infono).getAltCO() != null) { //If the alt CO exists
			            alt = true;
			        } else //if there is no alt CO for the previous CO
			            infono++;
			        skip = 0;
			    }
			    if(infono > b.getNumArmies() -1 )
			        infono = 0;
			}else if(menu || cmenu || bmenu || carbmenu){
			    
			}
			else if(map.onMap(cursorXpos+1,cursorYpos))
			{
				//[NEW]
				if(fire)
				{
					currContextTarg++;
			            SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/target.wav");
					
					if(currContextTarg >= targetTilesWithinRange.size())
						currContextTarg = 0;
					
					cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
					cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
				}
				else
				{
			        cursorXpos++;
			        SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/maptick.wav");
			            if(cursorXpos >= sx/16+MAX_TILEW-2 && cursorXpos < map.getMaxCol()-2)sx += 16;
			        if(move){
			            if(selected.getMoveRange().checkMove(cursorXpos,cursorYpos)){
			                if(outOfMoveRange){
			                    selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                    outOfMoveRange = false;
			                }else{
			                    selected.goDirection(1);
			                    selected.getPath().truncatePath(cursorXpos,cursorYpos);
			                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                }
			            }else{
			                if(cursorXpos == selected.getLocation().getCol() && cursorYpos == selected.getLocation().getRow()){
			                    selected.getPath().resetPath();
			                }else{
			                    outOfMoveRange = true;
			                }
			            }
			        }
				}
			}
		}

		private void keyLeftActions() {
			//[SET INFO BOXES:LEFT]
			//move Terrain, Unit and Trans boxes if necessary
			setInfoBoxXYs();
			
			if(info){
			    if(alt) //If we are on an alternate CO, move to the main CO
			        alt = false;
			    else { //If we are on a main CO
			        infono--;
			        if(infono < 0)
			            infono = b.getNumArmies() - 1 ;
			        
			        if(b.getArmy(infono).getAltCO() != null) { //If the alt CO exists
			            alt = true;
			        }
			        skip = 0;
			    }
			    
			}else if(menu || cmenu || bmenu || carbmenu){
			    
			}
			else if(map.onMap(cursorXpos-1,cursorYpos))
			{

				//[NEW]
				if(fire)
				{
					currContextTarg--;
					SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/target.wav");
			            
					if(currContextTarg < 0)
						currContextTarg = targetTilesWithinRange.size() - 1;
					
					cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
					cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
				}
				else
				{
			        cursorXpos--;
			            SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/maptick.wav");
			        if(cursorXpos < sx/16+2 && sx > 0)sx -= 16;
			        if(move){
			            if(selected.getMoveRange().checkMove(cursorXpos,cursorYpos)){
			                if(outOfMoveRange){
			                    selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                    outOfMoveRange = false;
			                }else{
			                    selected.goDirection(3);
			                    selected.getPath().truncatePath(cursorXpos,cursorYpos);
			                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                }
			            }else{
			                if(cursorXpos == selected.getLocation().getCol() && cursorYpos == selected.getLocation().getRow()){
			                    selected.getPath().resetPath();
			                }else{
			                    outOfMoveRange = true;
			                }
			            }
			        }
				}
			}
		}

		private void keyDownActions() {
			if(info)
			{
			    skip ++;
			    if(skip>skipMax)
			        skip = skipMax;
			}
			else if(menu || cmenu || bmenu || carbmenu)
			{
			    currentMenu.goDown();
			}
			else if(map.onMap(cursorXpos,cursorYpos+1))
			{
				//[NEW]
				if(fire)
				{
			        currContextTarg--;
			        SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/target.wav");
					
					if(currContextTarg < 0)
						currContextTarg = targetTilesWithinRange.size() - 1;
					
					cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
					cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
				}
				else
				{
			        cursorYpos++;
			            SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/maptick.wav");
			        if(cursorYpos >= sy/16+MAX_TILEH-2 && cursorYpos < map.getMaxRow()-2)sy += 16;
			        if(move){
			            if(selected.getMoveRange().checkMove(cursorXpos,cursorYpos)){
			                if(outOfMoveRange){
			                    selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                    outOfMoveRange = false;
			                }else{
			                    selected.goDirection(2);
			                    selected.getPath().truncatePath(cursorXpos,cursorYpos);
			                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                }
			            }else{
			                if(cursorXpos == selected.getLocation().getCol() && cursorYpos == selected.getLocation().getRow()){
			                    selected.getPath().resetPath();
			                }else{
			                    outOfMoveRange = true;
			                }
			            }
			        }
				}
			}
		}

		private void keyUpActions() {
			if(info){
			    skip--;
			    if(skip<0)
			        skip = 0;
			}else if(menu || cmenu || bmenu || carbmenu){
			    currentMenu.goUp();
			}
			else if(map.onMap(cursorXpos,cursorYpos-1))
			{
				//[NEW]
				if(fire)
				{
			            currContextTarg++;
			            SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/target.wav");
					
					if(currContextTarg >= targetTilesWithinRange.size())
						currContextTarg = 0;
					
					cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
					cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
				}
				else
				{
			        cursorYpos--;
			        SFX.playClip(ResourceLoader.properties.getProperty("soundLocation") + "/maptick.wav");
			            if(cursorYpos < sy/16+2 && sy > 0)sy -= 16;
			        if(move){
			            if(selected.getMoveRange().checkMove(cursorXpos,cursorYpos)){
			                if(outOfMoveRange){
			                    selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                    outOfMoveRange = false;
			                }else{
			                    selected.goDirection(0);
			                    selected.getPath().truncatePath(cursorXpos,cursorYpos);
			                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
			                }
			            }else{
			                if(cursorXpos == selected.getLocation().getCol() && cursorYpos == selected.getLocation().getRow()){
			                    selected.getPath().resetPath();
			                }else{
			                    outOfMoveRange = true;
			                }
			            }
			        }
				}
			}
		}
        
        public void keyReleased(KeyEvent e) {}
    }
    
    //Used to automatically resize as the window changes
    
    class BSMouseControl implements MouseInputListener{
        public void mouseClicked(MouseEvent e){
            int xCoOrdinates = e.getX() - parentFrame.getInsets().left;
            int yCoOrdinates = e.getY() - parentFrame.getInsets().top;
            logger.info("info =" + xCoOrdinates + "," + yCoOrdinates + ":" + e.getButton());
            
            if(e.getButton() == MouseEvent.BUTTON1){
                if(menu || cmenu){
                    int mitem = currentMenu.getMenuItemAt(xCoOrdinates,yCoOrdinates,scale);
                    if(mitem != -1){
                        currentMenu.setMenuItem(mitem);
                        pressedA();
                    }
                }else if(bmenu || carbmenu){
                    int mitem = currentMenu.getMenuItemAt(xCoOrdinates,yCoOrdinates,scale);
                    if(mitem != -1){
                        logger.info(""+mitem);
                        if(mitem == -2)currentMenu.goUp();
                        else if(mitem == -3)currentMenu.goDown();
                        else{
                            currentMenu.setMenuItem(mitem);
                            pressedA();
                        }
                    }
                }else{
                    if(noScroll == 0){
                        if(xCoOrdinates < 32*scale){
                            if(map.getMaxCol() > DEF_TILEW){
                                sx -= 16;
                                if(sx < 0)sx=0;
                            }
                        }else if(xCoOrdinates > MAX_TILEW*16*scale-32*scale){
                            if(map.getMaxCol() > DEF_TILEW){
                                sx += 16;
                                if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                            }
                        }
                        
                        if(yCoOrdinates < 32*scale){
                            if(map.getMaxRow() > DEF_TILEH){
                                sy -= 16;
                                if(sy < 0)sy=0;
                            }
                        }else if(yCoOrdinates > MAX_TILEH*16*scale-32*scale){
                            if(map.getMaxRow() > DEF_TILEH){
                                sy += 16;
                                if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                            }
                        }
                        noScroll = 3;
                    }else noScroll--;

                    boolean validClick = true;
                    
                    if(fire) 
                    {
                    	validClick = validFiringTargetSelected(xCoOrdinates, yCoOrdinates, targetTilesWithinRange);
                    }
                    
                    if(validClick)
                    {
	                    cursorXpos = sx/16 + xCoOrdinates/(16*scale);
	                    if(cursorXpos < 0)cursorXpos=0;
	                    else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
	                    cursorYpos = sy/16 + yCoOrdinates/(16*scale);
	                    if(cursorYpos < 0)cursorYpos=0;
	                    else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
	                    
	                    if(move)selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
	                    
                    	pressedA();
                    }
                }
            }
            else
            {
                //any other button
                pressedB();
            }
        }

		private boolean validFiringTargetSelected(int xCoOrdinates, int yCoOrdinates, ArrayList<Location> targetTilesWithinRange) {
			boolean validTargetSelected = false;
			
			Location clickedTile = new Location(xCoOrdinates * scale / 16 , yCoOrdinates * scale / 16); 
			
			for(int tileNo = 0; tileNo < targetTilesWithinRange.size() && !validTargetSelected; tileNo++) {
				if(clickedTile.equals(targetTilesWithinRange.get(tileNo))) {
					validTargetSelected = true;
				}
			}

			return validTargetSelected;
		}
        
        public void mouseEntered(MouseEvent e) {}
        
        public void mouseExited(MouseEvent e) {}
        
        public void mousePressed(MouseEvent e) {}
        
        public void mouseReleased(MouseEvent e) {}
        
        public void mouseDragged(MouseEvent e) {}
        
        public void mouseMoved(MouseEvent e)
        {
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            
            setInfoBoxXYs();
            
            if(!menu && !cmenu && !bmenu && !carbmenu)
            {
                if(noScroll == 0)
                {
                    if(x < 32*scale)
                    {
                        if(map.getMaxCol() > DEF_TILEW)
                        {
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }
                    else if(x > MAX_TILEW*16*scale-32*scale)
                    {
                        if(map.getMaxCol() > DEF_TILEW)
                        {
                            sx += 16;
                            if(sx > (map.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale)
                    {
                        if(map.getMaxRow() > DEF_TILEH)
                        {
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }
                    else if(y > MAX_TILEH*16*scale-32*scale)
                    {
                        if(map.getMaxRow() > DEF_TILEH)
                        {
                            sy += 16;
                            if(sy > (map.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    noScroll = 3;
                }
                else noScroll--;
                
                cursorXpos = sx/16 + x/(16*scale);
                if(cursorXpos < 0)cursorXpos=0;
                else if(cursorXpos >= map.getMaxCol())cursorXpos=map.getMaxCol()-1;
                cursorYpos = sy/16 + y/(16*scale);
                if(cursorYpos < 0)cursorYpos=0;
                else if(cursorYpos >= map.getMaxRow())cursorYpos=map.getMaxRow()-1;
                
                if(move)selected.getPath().reCalculatePath(cursorXpos,cursorYpos,selected);
            }

            if(fire)
            {
            	//Check to see if the cursor's new position is over a valid target.
            	boolean onTarg = false;
            	
                for(int k = 0; k < targetTilesWithinRange.size() && !onTarg; k++)
                {
                	if((new Location(cursorXpos, cursorYpos).equals(targetTilesWithinRange.get(k))))
                	{
                		currContextTarg = k;
                		onTarg = true;
                	}
                }
        		
                //Set the cusor's current coordinates to that of the current target.
                //If the mouse does not move over any valid targets, this code should
                //keep locking the coordinates back to that of the target before.
        		cursorXpos = targetTilesWithinRange.get(currContextTarg).getCol();
        		cursorYpos = targetTilesWithinRange.get(currContextTarg).getRow();
            }
        }
    }
    
    /* Dunn't work
    public void addScreenGlide(Location end, int delay, int duration) {
        Timer timer = new Timer(16, null);//30 frames per second
        EndListener endPerformer = new EndListener(end, duration, timer);
        timer.addActionListener(endPerformer); //End performer kills the timer thread after a set amount of time
        timer.setInitialDelay(delay*16+16);
        timer.setRepeats(true);
        timer.start();
    }
     
     
    private class EndListener implements ActionListener{
        int duration;
        int ticker;
        Location loc;
        Timer timer;
        public EndListener(Location end, int duration, Timer t) {
            timer = t;
            ticker = 0;
            this.duration = duration;
            loc = end;
        }
        public void actionPerformed(ActionEvent evt) {
            ticker++;
            if(ticker > duration)
            {
                timer.setRepeats(false);
                logger.info("End");
            }
            else{
            sx+=(loc.getCol() - (sx+MAX_TILEW)/2)/duration; //increments distance equally over the duration
            sy+=(loc.getRow() - (sy+MAX_TILEH)/2)/duration;
            logger.info("Gliding");
            }
        }
    }
     
     */
}