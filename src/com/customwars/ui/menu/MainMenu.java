package com.customwars.ui.menu;
/*
 *MainMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 27, 2006, 3:22 PM
 *The Main Menu, used to select what you want to play
 */

import java.awt.*;
import javax.swing.*;
import java.io.*;

import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.ai.BaseDMG;
import com.customwars.ai.Battle;
import com.customwars.ai.BattleOptions;
import com.customwars.ai.Mission;
import com.customwars.ai.Options;
import com.customwars.lobbyclient.*;
import com.customwars.map.Map;
import com.customwars.map.location.Location;
import com.customwars.map.location.Property;
import com.customwars.map.location.TerrType;
import com.customwars.officer.CO;
import com.customwars.officer.COList;
import com.customwars.sfx.SFX;
import com.customwars.state.FileSystemManager;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleScreen;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MapEditor;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.UnitGraphics;


import java.awt.color.*;
//import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.*;
import java.util.Vector;
import java.util.Random;

public class MainMenu extends JComponent{
    private static final String TEMPORARYMAP_MAP_FILENAME = "temporarymap.map";
	private static final String TEMPORARYSAVE_SAVE_FILENAME = "temporarysave.save";
	private static String ABSOLUTE_TEMP_FILENAME = "";
	
	private int cx;             //holds the cursor's x position on the co select screen
    private int cy;             //holds the cursor's y position on the co select screen
    private int item;           //holds the menu's current item (both menus use this)
    private int item2;          //holds the second menu's current item (only used by server info screen)
    private boolean inTitleScreen;      //title mode
    private boolean inOptionsScreen;    //options mode
    private boolean inStartANewGameScreen;    //new/load/network mode
    private boolean inMapSelectScreen;        //map select mode
    private boolean inCOselectScreen;         //CO select mode
    private boolean load;       //save select mode
    private boolean sideSelect; //Side Select mode
    private boolean inBattleOptionsScreen;  //battle options mode
    private boolean keymap;     //key mapping mode
    private boolean snailinfo;  //the snail-mode information screen
    private BufferedImage bimg; //the screen, used for double buffering and scaling
    private int scale;          //what scale multiplier is being used
    private JFrame parentFrame;  //the frame that contains the window
    private Location originalLocation;  //the pre-move position of the unit
    private KeyControl keycontroller;   //the KeyControl, used to remove the component
    private MouseControl mousecontroller;   //the MouseControl, used to remove the component
    private int[] coSelections; //the selected COs
    private int[] sideSelections; //the selected sides
    private boolean[] altSelections; //alt costumes for sides.
    private String filename;            //the map filename
    private int numArmies;              //the number of armies on the map
    private int numCOs;                 //the current number of COs selected
    private int selectedArmy = 0;       //the selected army (0 = OS, 1 = BM, etc.)
    private File mapDir;            //the map directory
    private String[] displayNames;  //the names of the maps to be displayed
    private String[] filenames;     //the filenames of the maps in displayNames
    private String[] authors;       //the author of each map
    private String[] descriptions;  //the description of each map
    private int ptypes[] = {0,0,0,0,0,0};//the number of properties on this map
    private int numMaps;            //the number of maps
    private File selectedMap;       //the selected map
    private int mapPage;        //the page in the map list
    private int NUM_COS = 71;   //the number of selectable COs
    private int cat = 0;        //the map directory category
    private int subcat = 0;     //the map directory subcategory
    private String[] cats;      //the categories
    private Battle preview;     //the minimap preview
    private BattleOptions bopt = new BattleOptions();  //the battle options to start the game with
    private boolean chooseKey = false;      //if true, the user is choosing a key
    private int day = 1;
    private int turn = 1;
    private boolean insertNewCO = false;    //used to select new COs in snail mode
    private String [] usernames = {"Unknown"};     //the usernames of the players in snail mode
    private String [] syslog = {"System Log"};     //system log messages
    private String [] chatlog = {"Chat Log"};     //chat log messages
    int syspos = 0;
    int chatpos = 0;
    int glide = -1; //A simple thing to make stuff prettier. 
    int backGlide = -1;
    private boolean info;
    private int infono;
    private int skip = 0;
    private int skipMax = 0;
    public boolean altcostume;
    public boolean mainaltcostume;
    
	private int TITLE_startPixels_newGameBtn = 160;
	private int TITLE_endPixels_newGameBtnWidth = 332;
	private int TITLE_topPixels_newGameBtnHeight = 87;
	private int TITLE_btmPixels_newGameBtnHeight = 60;
	private int TITLE_startPixels_mapsEditorBtn = 143;
	private int TITLE_endPixels_mapsEditorBtn = 350;
	private int TITLE_btmPixels_mapsEditorBtn = 156;
	private int TITLE_topPixels_mapsEditorBtn = 183;
	private int TITLE_startPixels_optionsButton = 175;
	private int TITLE_btmPixels_optionsBtn = 247;
	private int TITLE_topPixels_optionsBtn = 279;
	private int TITLE_endPixels_optionsBtn = 320;
    
	final static Logger logger = LoggerFactory.getLogger(MainMenu.class); 
    
    private CO[][] armyArray = new CO[8][14];
    
    private int visibility = 0; //0=Full 1=Fog 2=Mist
    
    /** Creates a new instance of BattleScreen */
    public MainMenu(JFrame f){
        //makes the panel opaque, and thus visible
        this.setOpaque(true);

        String fileSysLocation = ResourceLoader.properties.getProperty("saveLocation");
        ABSOLUTE_TEMP_FILENAME = fileSysLocation + TEMPORARYMAP_MAP_FILENAME;
        
        cx = 0;
        cy = 0;
        item = 0;
        item2 = 0;
        logger.info("Started through Main menu");
        
        scale = 1;
        
        inTitleScreen = true;
        inOptionsScreen = false;
        inStartANewGameScreen = false;
        inMapSelectScreen = false;
        inCOselectScreen = false;
        load = false;
        info = false;
        inBattleOptionsScreen = false;
        keymap = false;
        Options.snailGame = false;
        snailinfo = false;
        
        //KeyControl is registered with the parent frame
        keycontroller = new KeyControl();
        f.addKeyListener(keycontroller);
        //MouseControl is registered with the parent frame
        mousecontroller = new MouseControl();
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
        parentFrame = f;
        
        for(int i=0; i<8; i++){
            int pos = 0;
            for(int j=0; j<COList.getListing().length; j++){
                if(COList.getListing()[j].getStyle() == i){
                    armyArray[i][pos] = COList.getListing()[j];
                    pos++;
                }
            }
        }
        
        if(Options.refresh)
        {
            refreshListener refresh = new refreshListener();
            Timer timer = new Timer(10000, refresh);
            timer.start();
        }
    }
    
    //called in response to this.repaint();
    public void paintComponent(Graphics g) {
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
    
    //tells the GUI what size the window is
    public Dimension getPreferredSize(){
        //return new Dimension(256*scale,192*scale);
        return new Dimension(480*scale,320*scale);
    }
    
    //makes a Graphics2D object of the given size
    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
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
    
    //Draws the screen
    public void drawScreen(Graphics2D g){
        //draws an animated gif in the background
        //this triggers repaint automatically
        //using repaint normally ruins animations
        //g.drawImage(MiscGraphics.getMoveTile(),0,0,this);
        
        //draw a white background (if the map is smaller than the screen)
        //g.setColor(Color.white);
        //g.fillRect(0,0,256,192);
        
        drawBackground(g);
        if(inTitleScreen)drawTitleScreen(g);
        if(inMapSelectScreen)drawMapSelectScreen(g);
        
        if(inCOselectScreen)drawCOSelectScreen(g);
        if(info && inCOselectScreen)drawInfoScreen(g);
        if(inOptionsScreen)drawOptionsScreen(g);
        if(inStartANewGameScreen)drawNewLoadScreen(g);
        if(sideSelect)drawSideSelectScreen(g);
        if(inBattleOptionsScreen)drawBattleOptionsScreen(g);
        if(keymap)drawKeymapScreen(g);
        if(snailinfo)drawServerInfoScreen(g);
        
        //causes problems with animated gifs
        this.repaint();
    }
    
    //draws the background
    public void drawBackground(Graphics2D g){
        g.drawImage(MainMenuGraphics.getBackground(),0,0,this);
    }
    
    //Draws the title screen
    public void drawTitleScreen(Graphics2D g){
        //Draw the title
        //g.setColor(Color.black);
        //g.setFont(new Font("Serif", Font.BOLD, 40));
        //g.drawString("Custom Wars",10,50);
        
        //draw title background
        g.drawImage(MainMenuGraphics.getTitleBackground(),0,0,this);
        //g.drawImage(MainMenuGraphics.getLogo(),0,0,this);
        
        //draw the three menu choices
        if(item == 0)g.drawImage(MainMenuGraphics.getNewGame(true),0,0,this);
        else g.drawImage(MainMenuGraphics.getNewGame(false),0,0,this);
        if(item == 1)g.drawImage(MainMenuGraphics.getMaps(true),0,0,this);
        else g.drawImage(MainMenuGraphics.getMaps(false),0,0,this);
        if(item == 2)g.drawImage(MainMenuGraphics.getOptions(true),0,0,this);
        else g.drawImage(MainMenuGraphics.getOptions(false),0,0,this);
        
        //write the copyright notice
        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Advance Wars is � Nintendo/Intelligent Systems",100,310);
    }
    
    public void drawInfoScreen(Graphics2D g) {
        
        String[] bio;
        int i, store, adjust;
        int starStore = 0;
        g.setColor(Color.white);
        //(256*scale,192*scale)
        g.drawImage(MainMenuGraphics.getBackground(),0,0, this);
/*
        g.drawImage(MainMenuGraphics.getBaseOne(selectedArmy),0,0,this);
        g.drawImage(MainMenuGraphics.getHQBG(),233,35,233+244,35+279,244*selectedArmy,0,244*selectedArmy+244,279,this);
        g.drawImage(MainMenuGraphics.getEmblem(),250,20,250+36,20+36,36*selectedArmy,0,36*selectedArmy+36,36,this);
*/
        //Draws the COs
        String sidestring, costring;
        store = 0;
        adjust = 0;
        
        //Draw the main CO
        if(!altcostume)
            g.drawImage(MiscGraphics.getCOSheet(infono),300,40,300 + 225, 40 + 350, 0, 0, 225, 350, this);
        else
            g.drawImage(MiscGraphics.getCOSheet(infono),300,40,300 + 225, 40 + 350, 225, 0, 450, 350, this);
        //Draw the 'basic information'
        g.setColor(Color.black);
        /*g.setFont(new Font("Impact", Font.BOLD, 38));
        g.drawString(COList.getListing()[infono].getName(), 15,53);
        g.setColor(Color.gray);*/
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString("CO: " + COList.getListing()[infono].getName(), 10,20);
        g.drawString(COList.getListing()[infono].getTitle(), (COList.getListing()[infono].getName().length() + 9)*6 + 10,20);
        //CO Bio
        //This is a 'word wrap' thing, used multiple times. Listen up, I'm only going to document this once. >_>
        for(i = 0; i<((COList.getListing()[infono].getBio().length()/40)+1); i++) {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
            if(COList.getListing()[infono].getBio().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left in the bio?
                //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                if((40 + i*15- skip*96*scale) + adjust <25 && 40 + i*15- skip*96*scale+ adjust >0)
                    adjust += 25; //If so, move this, and everything after this, 25 pixels down.
                //Draw the substring - 40 characters from the last area.
                g.drawString(COList.getListing()[infono].getBio().substring(i*40, (i+1)*40), 10, 40 + i*15- skip*96*scale + adjust);
                
            } else //If there is less than 40 characters left...
            {
                if((40 + i*15- skip*96*scale + adjust)<25 && 40 + i*15- skip*96*scale + adjust>0)
                    adjust += 25;
                //Avoiding info space.
                //Drawing the rest of the substring.
                g.drawString(COList.getListing()[infono].getBio().substring(i*40), 10,40 + i*15- skip*96*scale);
            }
            if(i+1 ==((COList.getListing()[infono].getBio().length()/40)+1))
                store = i; //Store is used to get placement.
        }
        store+=2;
        //This draws the Hit and Miss
        if(((store+1)*15- skip*96*scale+ adjust)<25 && ((store+1)*15- skip*96*scale+ adjust)>0)
            adjust += 25;
        g.drawImage(MiscGraphics.getHitIcon(),10 ,(store+1)*15- skip*96*scale + adjust,this);
        g.drawString(COList.getListing()[infono].getHit(), 45,40 + (store-1)*15- skip*96*scale + adjust);
        store++;
        if(((store+1)*15- skip*96*scale+ adjust)<25 && ((store+1)*15- skip*96*scale+ adjust)>0)
            adjust += 25;
        g.drawImage(MiscGraphics.getMissIcon(),10 ,(store+1)*15- skip*96*scale + adjust,this);
        g.drawString(COList.getListing()[infono].getMiss(), 45,40 + (store-1)*15- skip*96*scale + adjust);
        store++;
        //This draws the power bar
        if((22 + (store)*15- skip*96*scale + adjust)<25 && (22 + (store)*15- skip*96*scale + adjust)>0)
            adjust += 25;
        //If the CO has a COP
        if(COList.getListing()[infono].getCOPStars() != -1)
            for(i = 0; i<COList.getListing()[infono].getCOPStars()+1; i++) {
            g.drawImage(MiscGraphics.getSmallStar(5),10+i*6, 22 + (store)*15- skip*96*scale + adjust, this);
            starStore = i;
            }
        //Aaand...the SCOP
        for(i = 0; i<(COList.getListing()[infono].getMaxStars()-COList.getListing()[infono].getCOPStars()); i++) {
            g.drawImage(MiscGraphics.getBigStar(5),10+i*8 + starStore*6, 20 + (store)*15- skip*96*scale + adjust, this);
        }
        
        store++;
        //This draws the Day to Day.
        if(((store+1)*15- skip*96*scale+ adjust)<25 && ((store+1)*15- skip*96*scale+ adjust)>0)
            adjust += 25;
        g.drawImage(MiscGraphics.getSkillIcon(),10 ,(store+1)*15- skip*96*scale + adjust,this);
        
        //Word wrap for the skill
        for(i = 0; i<((COList.getListing()[infono].getD2D().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
        {
            if(COList.getListing()[infono].getD2D().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                    adjust += 25;
                
                g.drawString(COList.getListing()[infono].getD2D().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*96*scale + adjust);
            } else {
                if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                    adjust += 25;
                g.drawString(COList.getListing()[infono].getD2D().substring(i*40), 10,40 + i*15 + store*15- skip*96*scale + adjust);
            }
            if(i+1 ==((COList.getListing()[infono].getD2D().length()/40)+1))
                store +=i;
        }
        store += 2;
        //This draws the Power
        if(COList.getListing()[infono].getCOPStars() != -1) {
            if(((store+1) * 15- skip*96*scale+ adjust)<25 && ((store+1) * 15- skip*96*scale+ adjust)>0)
                adjust += 25;
            
            g.drawImage(MiscGraphics.getPowerIcon(), 10, (store+1) * 15- skip*96*scale + adjust, this);
            g.drawString(COList.getListing()[infono].getCOPName(), 26, (store+2) * 15 - 2- skip*96*scale + adjust);
            
            for(i = 0; i<((COList.getListing()[infono].getCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(COList.getListing()[infono].getCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                        adjust += 25;
                    g.drawString(COList.getListing()[infono].getCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*96*scale + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                        adjust += 25;
                    
                    g.drawString(COList.getListing()[infono].getCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*96*scale + adjust);
                }
                if(i+1 == ((COList.getListing()[infono].getCOPString().length()/40)+1))
                    store +=i;
            }
            
            store += 2;
        }
        //This draws the SCOP
        if(((store+1) * 15- skip*96*scale+ adjust)<25 && ((store+1) * 15- skip*96*scale+ adjust)>0)
            adjust += 25;
        g.drawImage(MiscGraphics.getSuperIcon(), 10, (store+1) * 15- skip*96*scale + adjust, this);
        g.drawString(COList.getListing()[infono].getSCOPName(), 26, (store+2) * 15 - 2- skip*96*scale + adjust);
        for(i = 0; i<((COList.getListing()[infono].getSCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
        {
            if(COList.getListing()[infono].getSCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                    adjust += 25;
                g.drawString(COList.getListing()[infono].getSCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*96*scale + adjust);
            } else {
                if((40 + i*15 + store*15- skip*96*scale+ adjust)<25 && (40 + i*15 + store*15- skip*96*scale+ adjust)>0)
                    adjust += 25;
                g.drawString(COList.getListing()[infono].getSCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*96*scale + adjust);
            }
            if(i+1 == ((COList.getListing()[infono].getSCOPString().length()/40)+1))
                store +=i;
        }
        //Draws tags
        store += 2;
        for(i = 0; i< COList.getListing()[infono].getTagStars().length; i++) {
            if(COList.getListing()[infono].getTagStars()[i] > 0) {
                if(i*15 + (store+2) * 15 - 2- skip*96*scale+ adjust<25 && i*15 + (store+2) * 15 - 2- skip*96*scale+ adjust>0)
                    adjust += 25;
                g.drawString(COList.getListing()[infono].getTagCOs()[i], 10, i*15 + (store+2) * 15 - 2- skip*96*scale + adjust);
                for(int t =0; t<COList.getListing()[infono].getTagStars()[i]; t++) {
                    g.drawImage(MiscGraphics.getBigStar(5), COList.getListing()[infono].getTagCOs()[i].length()*5 + 15 + t*8,(store+2) * 15 - 2- skip*96*scale + adjust+ i*15 -10, this);
                }
                if(i+1 == COList.getListing()[infono].getTagStars().length)
                    store +=i;
            }
        }
        
        skipMax = ((store * 15)/(96*scale));
        
    }
    public void drawMapSelectScreen(Graphics2D g){
        
        g.drawImage(MainMenuGraphics.getMapLayout(),4,40,this);
        g.drawImage(MainMenuGraphics.getPageUp(),84,30,this);
        g.drawImage(MainMenuGraphics.getPageDown(),84,312,this);
        
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        g.drawString(cats[cat],4,20);
        //+": "+subcats[subcat]
        
        for(int i = 0; i < 12; i++)
            if(mapPage*12 + i < numMaps)
                g.drawString(displayNames[mapPage*12 + i],10,68+i*21);
        
        g.setColor(Color.red);
        g.drawRect(10,50+item*21,148,19);
        
        if(numMaps!= 0){
            g.setColor(Color.black);
            g.drawString(displayNames[mapPage*12 + item],180,60);
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.drawString("Mapmaker: "+authors[mapPage*12 + item],180,245);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(descriptions[mapPage*12 + item],180,265);
        }
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g.setColor(new Color(7,66,97));
        g.fillRoundRect(180,275,280,40,20,20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawImage(TerrainGraphics.getColoredSheet(0),205+16,284,221+16,316,0, TerrType.getYIndex(TerrType.CITY), 16, TerrType.getYIndex(TerrType.CITY) + 32, this);
        g.drawString(""+ptypes[0],205,300);
        g.drawImage(TerrainGraphics.getColoredSheet(0),247+16,284,263+16,316,0, TerrType.getYIndex(TerrType.BASE), 16, TerrType.getYIndex(TerrType.BASE) + 32, this);
        g.drawString(""+ptypes[1],247,300);
        g.drawImage(TerrainGraphics.getColoredSheet(0),289+16,284,305+16,316,0, TerrType.getYIndex(TerrType.PORT), 16, TerrType.getYIndex(TerrType.PORT) + 32, this);
        g.drawString(""+ptypes[2],289,300);
        g.drawImage(TerrainGraphics.getColoredSheet(0),331+16,284,347+16,316,0, TerrType.getYIndex(TerrType.AIRPORT), 16, TerrType.getYIndex(TerrType.AIRPORT) + 32, this);
        g.drawString(""+ptypes[3],331,300);
        g.drawImage(TerrainGraphics.getColoredSheet(0),373+16,284,389+16,316,0, TerrType.getYIndex(TerrType.COM_TOWER), 16, TerrType.getYIndex(TerrType.COM_TOWER) + 32, this);
        g.drawString(""+ptypes[4],373,300);
        g.drawImage(TerrainGraphics.getColoredSheet(0),415+16,284,431+16,316,0, TerrType.getYIndex(TerrType.PIPE_STATION), 16, TerrType.getYIndex(TerrType.PIPE_STATION) + 32, this);
        g.drawString(""+ptypes[5],415,300);
        
        g.setColor(new Color(7,66,97));
        g.fillRoundRect(180,5,280,40,20,20);
        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        if(subcat == 0)g.setColor(Color.red);
        g.drawString("ALL",210,30);
        g.setColor(Color.white);
        if(subcat == 1)g.setColor(Color.red);
        g.drawString("2",250,30);
        g.setColor(Color.white);
        if(subcat == 2)g.setColor(Color.red);
        g.drawString("3",270,30);
        g.setColor(Color.white);
        if(subcat == 3)g.setColor(Color.red);
        g.drawString("4",290,30);
        g.setColor(Color.white);
        if(subcat == 4)g.setColor(Color.red);
        g.drawString("5",310,30);
        g.setColor(Color.white);
        if(subcat == 5)g.setColor(Color.red);
        g.drawString("6",330,30);
        g.setColor(Color.white);
        if(subcat == 6)g.setColor(Color.red);
        g.drawString("7",350,30);
        g.setColor(Color.white);
        if(subcat == 7)g.setColor(Color.red);
        g.drawString("8",370,30);
        g.setColor(Color.white);
        if(subcat == 8)g.setColor(Color.red);
        g.drawString("9",390,30);
        g.setColor(Color.white);
        if(subcat == 9)g.setColor(Color.red);
        g.drawString("10",410,30);
        
        drawMiniMap(g,180,65);
    }
    
    public void drawMiniMap(Graphics2D g, int x, int y)
    {
        if(numMaps != 0)
        {
            Image minimap = MiscGraphics.getMinimap();
            
            Map map = preview.getMap();
            
            for(int i=0; i < map.getMaxCol(); i++)
            {
                for(int j=0; j < map.getMaxRow();j++)
                {
                    //draw terrain
                    int terraintype = map.find(new Location(i,j)).getTerrain().getIndex();
                    if(terraintype < 9)
                    {
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0+(terraintype*4),0,4+(terraintype*4),4,this);
                    }
                    else if(terraintype == 9)
                    {
                        int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getOwner().getColor();
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,36+(armycolor*4),0,40+(armycolor*4),4,this);
                    }
                    else if(terraintype < 15 || terraintype == 17)
                    {
                        int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getColor();
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,76+(armycolor*4),0,80+(armycolor*4),4,this);
                    }
                    else if(terraintype == 15)
                    {
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,120,0,124,4,this);
                    }
                    else if(terraintype == 16)
                    {
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,128,0,132,4,this);
                    }
                    else if(terraintype == 18)
                    {
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,124,0,128,4,this);
                    }
                    else if(terraintype == 19)
                    {
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0,0,4,4,this);
                    }
                    
                    
                    //draw units
                    if(map.find(new Location(i,j)).hasUnit()){
                        int armycolor = map.find(new Location(i,j)).getUnit().getArmy().getColor();
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,132+(armycolor*4),0,136+(armycolor*4),4,this);
                    }
                }
            }
        }else{
            g.drawImage(MainMenuGraphics.getNowDrawing(),x,y,this);
        }
    }
    
    public void drawCOSelectScreen(Graphics2D g)
    {
        backGlide++;
        if(backGlide > 640*2)
        {
            backGlide = 0;
        }
        g.drawImage(MiscGraphics.getIntelBackground(), 0, -backGlide/2, this);
        g.drawImage(MiscGraphics.getIntelBackground(), 0, 640-backGlide/2, this);
        
        int offset = 0;
        if(altcostume)offset = 225;
        int offset2 = 0;
        if(mainaltcostume)offset2 = 225;
        
        //Layout
        g.drawImage(MainMenuGraphics.getCOLayout(selectedArmy),0,52,this);
        g.drawImage(MainMenuGraphics.getCOBanner(),0,1,this);
        for(int i=0; i < 8; i++){
            if(i==selectedArmy)g.drawImage(MainMenuGraphics.getArmyTag(i),3+i*19,0,this);
            else g.drawImage(MainMenuGraphics.getArmyTag(i),3+i*19,-12,this);
        }
        g.drawImage(MainMenuGraphics.getHQBG(),2,61,2+156,61+279,244*selectedArmy,0,244*selectedArmy+244,279,this);
        
        //Draw CO framework
        for(int j = 0; j < 5; j++){
            for(int i = 0; i < 3; i++){
                g.drawImage(MainMenuGraphics.getCOSlot(selectedArmy),2+i*52,61+j*52,this);
            }
        }
        g.drawImage(MainMenuGraphics.getNoCO(),2,61,this);
        
        //Draw COs
        for(int i=1; i < 15; i++){
            CO current = armyArray[selectedArmy][i-1];
            if(current != null){
                
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)),2+i%3*52,61+i/3*52,2+i%3*52+48,61+i/3*52+48,0+offset,350,48+offset,398,this);
            }else{
                break;
            }
        }
        
        //Draw Cursor
        if(numCOs % 2 == 0)g.setColor(Color.RED);
        else g.setColor(Color.BLUE);
        g.drawRect(2+cx*52,61+cy*52,48,48);
        
        //Draw first CO if selecting second CO
        if(numCOs%2==1)
        {
            //g.drawImage(MiscGraphics.getCOSheet(coSelections[numCOs-1]-1),166,210,166+32,210+12,144+offset2,350,144+offset2+32,350+12,this);
            //g.drawImage(MainMenuGraphics.getCOName(),199,210,199+50,210+15,0,(coSelections[numCOs-1]-1)*15,50,(coSelections[numCOs-1]-1)*15+15,this);
        	g.drawImage(MiscGraphics.getCOSheet(coSelections[numCOs-1]),166,210,166+32,210+12,144+offset2,350,144+offset2+32,350+12,this);
            g.drawImage(MainMenuGraphics.getCOName(),199,210,199+50,210+15,0,(coSelections[numCOs-1])*15,50,(coSelections[numCOs-1])*15+15,this);
        }
        
        //Draw current CO Info
        CO current = null;
        
        if(cx+cy*3-1>-1)current = armyArray[selectedArmy][cx+cy*3-1];
        
        if(current != null)
        {
            glide++;
            g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)),339+(int)(100*Math.pow(0.89,glide)),44,339+225+(int)(100*Math.pow(0.89,glide)),44+350,offset,0,offset+225,350,this);
            g.drawImage(MainMenuGraphics.getCOName(),170,70,170+50,70+15,0,current.getId()*15,50,current.getId()*15+15,this);
            /*g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(current.getBio(),170,100);*/
            if(numCOs%2==1){
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)),166,226,166+32,226+12,144+offset,350,144+offset+32,350+12,this);
                g.drawImage(MainMenuGraphics.getCOName(),199,226,199+50,226+15,0,current.getId()*15,50,current.getId()*15+15,this);
            }else{
                g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)),166,210,166+32,210+12,144+offset,350,144+offset+32,350+12,this);
                g.drawImage(MainMenuGraphics.getCOName(),199,210,199+50,210+15,0,current.getId()*15,50,current.getId()*15+15,this);
            }
        }
        
        if(numCOs/2<9)
            g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy),293,195,293+15,195+10,numCOs/2*15,0,numCOs/2*15+15,10,this);
        else
            g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy),293,195,293+30,195+10,numCOs/2*15,0,numCOs/2*15+30,10,this);
        
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        
        if(current != null)
        {
	        int store = 0;
	        int k;
	        
	        for(k = 0; k < ((COList.getListing()[COList.getIndex(current)].getIntel().length()/36)+1); k++) 
	        {//As long as k is shorter than the length, in characters, of the bio divided by 40, incremented by one.
	            if(COList.getListing()[COList.getIndex(current)].getIntel().length() - (k+1)*36 >= 0) { //Is there more than 40 characters left in the bio?
	                //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
	                //Draw the substring - 40 characters from the last area.
	                g.drawString(COList.getListing()[COList.getIndex(current)].getIntel().substring(k*36, (k+1)*36), 170, 98 + k*15);
	                
	            } else //If there is less than 40 characters left...
	            {
	                //Avoiding info space.
	                //Drawing the rest of the substring.
	                g.drawString(COList.getListing()[COList.getIndex(current)].getIntel().substring(k*36), 170, 98 + k*15);
	            }
	        }
	        
	        g.setColor(Color.black);
	        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
	        g.drawString(COList.getListing()[COList.getIndex(current)].getTitle(),220,80);
        
        }
    }
    
    public void drawOptionsScreen(Graphics2D g){
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        if(item == 0)g.setColor(Color.red);
        g.drawString("Music",10,20);
        g.setColor(Color.black);
        if(Options.isMusicOn())
            g.drawString("On",80,20);
        else
            g.drawString("Off",80,20);
        
        if(item == 1)g.setColor(Color.red);
        g.drawString("Random Numbers",10,40);
        g.setColor(Color.black);
        
        if(item == 2)g.setColor(Color.red);
        g.drawString("Balance Mode",10,60);
        g.setColor(Color.black);
        if(Options.isBalance())
            g.drawString("On",120,60);
        else
            g.drawString("Off",120,60);
        
        if(item == 3)g.setColor(Color.red);
        g.drawString("Set IP",10,80);
        g.setColor(Color.black);
        g.drawString(Options.getDisplayIP(),60,80);
        
        if(item == 4)g.setColor(Color.red);
        g.drawString("Autosave",10,100);
        g.setColor(Color.black);
        if(Options.isAutosaveOn())
            g.drawString("On",120,100);
        else
            g.drawString("Off",120,100);
        
        if(item == 5)g.setColor(Color.red);
        g.drawString("Record Replay",10,120);
        g.setColor(Color.black);
        if(Options.isRecording())
            g.drawString("On",130,120);
        else
            g.drawString("Off",130,120);
        
        if(item == 6)g.setColor(Color.red);
        g.drawString("Cursor",10,140);
        g.setColor(Color.black);
        g.drawImage(MiscGraphics.getCursor(),70,120,this);
        
        if(item == 7)g.setColor(Color.red);
        g.drawString("Remap Keys",10,160);
        g.setColor(Color.black);
        
        if(item == 8)g.setColor(Color.red);
        String bbi = "On";
        if(!Options.battleBackground)bbi = "Off";
        g.drawString("Battle Background Image "+bbi,10,180);
        g.setColor(Color.black);
        
        if(item == 9)g.setColor(Color.red);
        g.drawString("Snail Mode Server: " + Options.getServerName(),10,200);
        g.setColor(Color.black);
        
        if(item == 10)g.setColor(Color.red);
        String bans = "";
        if(Options.getDefaultBans()==0){
            bans = "CW";
        }else if(Options.getDefaultBans()==1){
            bans = "AWDS";
        }else if(Options.getDefaultBans()==2){
            bans = "AW2";
        }else if(Options.getDefaultBans()==3){
            bans = "AW1";
        }else if(Options.getDefaultBans()==4){
            bans = "No Bans";
        }else if(Options.getDefaultBans()==5){
            bans = "All Bans";
        }
        g.drawString("Default Bans: " + bans,10,220);
        g.setColor(Color.black);
        if(item == 11)g.setColor(Color.red);
        g.drawString("Main Screen CO: " + COList.getListing()[Options.getMainCOID()].getName(),10,240);
        g.setColor(Color.black);
        
        if(item == 12)g.setColor(Color.red);
        g.drawString("Sound Effects: ",10,260);
        if(SFX.getMute())
            g.drawString("Off",130,260);
        else
            g.drawString("On",130,260);
        g.setColor(Color.black);
        //Shows current terrain tileset
        if(item == 13)g.setColor(Color.red);
        g.drawString("Terrain Tileset: ",10,280);
        if(Options.getSelectedTerrain() == 0)
            g.drawString("CW", 220, 280);
        else if(Options.getSelectedTerrain() == 1)
            g.drawString("AWDS", 220, 280);
        else if (Options.getSelectedTerrain() == 2)
            g.drawString(Options.getCustomTerrainString(), 220,280);
        g.setColor(Color.black);
        
        //Shows current Urban tileset
        if(item == 14)g.setColor(Color.red);
        g.drawString("Urban Tileset: ",10,300);
        if(Options.getSelectedUrban() == 0)
            g.drawString("CW", 220, 300);
        else if(Options.getSelectedUrban() == 1)
            g.drawString("AWDS", 220, 300);
        else if (Options.getSelectedUrban() == 2)
            g.drawString(Options.getCustomUrbanString(), 220,300);
        g.setColor(Color.black);
        
        //Shows current HQ tileset
        //Shows current Urban tileset
        if(item == 15)g.setColor(Color.red);
        g.drawString("HQ Tileset: ",10,320);
        if(Options.getSelectedHQ() == 0)
            g.drawString("CW", 220, 320);
        else if(Options.getSelectedHQ() == 1)
            g.drawString("AWDS", 220, 320);
        else if (Options.getSelectedHQ() == 2)
            g.drawString(Options.getCustomHQString(), 220,320);
        g.setColor(Color.black);
        if(item == 16)g.setColor(Color.red);        
        g.drawString("Use Default Login Info: ", 220, 20);
        if(Options.isDefaultLoginOn())
            g.drawString("On",400,20);
        else
            g.drawString("Off",400,20);
        g.setColor(Color.black);
        if(item == 17)g.setColor(Color.red);
        g.drawString("Default Username/Password:",220,40);
        g.drawString(Options.getDefaultUsername() + " / " + Options.getDefaultPassword(),220,60);
        g.setColor(Color.black);
        if(item == 18)g.setColor(Color.red);
        g.drawString("AutoRefresh:",220,80);
        if(Options.getRefresh())
            g.drawString("On",350,80);
        else
            g.drawString("Off",350,80);
            g.setColor(Color.black);
        
    }
    
    public void drawBattleOptionsScreen(Graphics2D g){
        int textCol = 20;
        
        /*
        //Fog of War
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        if(item == 0)g.setColor(Color.red);
        g.drawString("Fog Of War",10,textCol);
        g.setColor(Color.black);
        if(bopt.isFog())
            g.drawString("On",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        
        //Mist of War
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        if(item == 1)g.setColor(Color.red);
        g.drawString("Mist Of War",10,textCol);
        g.setColor(Color.black);
        if(bopt.isMist())
            g.drawString("On",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        */
        
        //Visibility
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        if(item == 0)g.setColor(Color.red);
        g.drawString("Visibility",10,textCol);
        g.setColor(Color.black);
        
        if(visibility == 0)
        	g.drawString("Full",120,textCol);
        else if(visibility == 1)
        	g.drawString("Fog of War",120,textCol);
        else
        	g.drawString("Mist of War",120,textCol);
        
        textCol += 20;
        
        //Weather
        if(item == 1)g.setColor(Color.red);
        g.drawString("Weather",10,textCol);
        g.setColor(Color.black);
        if(bopt.getWeatherType()==0)
            g.drawString("Clear",120,textCol);
        else if(bopt.getWeatherType()==1)
            g.drawString("Rain",120,textCol);
        else if(bopt.getWeatherType()==2)
            g.drawString("Snow",120,textCol);
        else if(bopt.getWeatherType()==3)
            g.drawString("Sandstorm",120,textCol);
        else if(bopt.getWeatherType()==4)
            g.drawString("Random",120,textCol);
        
        textCol += 20;
        
        //Funds
        if(item == 2)g.setColor(Color.red);
        g.drawString("Funds",10,textCol);
        g.setColor(Color.black);
        g.drawString(bopt.getFundsLevel()+"",120,textCol);
        
        textCol += 20;
        
        //Starting Funds
        if(item == 3)g.setColor(Color.red);
        g.drawString("Start Funds",10,textCol);
        g.setColor(Color.black);
        g.drawString(bopt.getStartFunds()+"",120,textCol);
        
        textCol += 20;
        
        //Turn Limit
        if(item == 4)g.setColor(Color.red);
        g.drawString("Turn Limit",10,textCol);
        g.setColor(Color.black);
        if(bopt.getTurnLimit()>0)
            g.drawString(bopt.getTurnLimit()+"",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        
        //Cap Limit
        if(item == 5)g.setColor(Color.red);
        g.drawString("Capture Limit",10,textCol);
        g.setColor(Color.black);
        if(bopt.getCapLimit()>0)
            g.drawString(bopt.getCapLimit()+"",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        
        //CO Powers
        if(item == 6)g.setColor(Color.red);
        g.drawString("CO Powers",10,textCol);
        g.setColor(Color.black);
        if(bopt.isCOP())
            g.drawString("On",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        
        //Balance Mode
        if(item == 7)g.setColor(Color.red);
        g.drawString("Balance Mode",10,textCol);
        g.setColor(Color.black);
        if(bopt.isBalance())
            g.drawString("On",120,textCol);
        else
            g.drawString("Off",120,textCol);
        
        textCol += 20;
        
        //Record Replay?
        if(item == 8)g.setColor(Color.red);
        g.drawString("Record Replay",10,textCol);
        g.setColor(Color.black);
        if(bopt.isRecording())
            g.drawString("On",130,textCol);
        else
            g.drawString("Off",130,textCol);
        
        textCol += 20;
        
        //Unit Bans
        g.setColor(Color.black);
        Image isheet = UnitGraphics.getUnitImage(0,0);
        Image usheet = UnitGraphics.getUnitImage(2,0);
        for(int i=0; i < 2; i++){
            g.drawImage(isheet,10+i*16,textCol-16,26+i*16,textCol,0,UnitGraphics.findYPosition(i,0),16,UnitGraphics.findYPosition(i,0)+16,this);
            if(bopt.isUnitBanned(i)){
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g.fillRect(10+i*16,textCol-16,16,16);
                g.setComposite(AlphaComposite.SrcOver);
            }
        }
        for(int i=2; i < BaseDMG.NUM_UNITS; i++){
            if(i < BaseDMG.NUM_UNITS/2){
                g.drawImage(usheet,10+i*16,textCol-16,26+i*16,textCol,0,UnitGraphics.findYPosition(i,0),16,UnitGraphics.findYPosition(i,0)+16,this);
                if(bopt.isUnitBanned(i)){
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g.fillRect(10+i*16,textCol-16,16,16);
                    g.setComposite(AlphaComposite.SrcOver);
                }
            } else{
                g.drawImage(usheet,10+(i-BaseDMG.NUM_UNITS/2)*16,textCol+4,26+(i-BaseDMG.NUM_UNITS/2)*16,textCol+20,0,UnitGraphics.findYPosition(i,0),16,UnitGraphics.findYPosition(i,0)+16,this);
                if(bopt.isUnitBanned(i)){
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g.fillRect(10+(i-BaseDMG.NUM_UNITS/2)*16,textCol+4,16,16);
                    g.setComposite(AlphaComposite.SrcOver);
                }
            }
        }
        if(item == 9){
            g.setColor(Color.red);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.fillRect(10+(cx%(BaseDMG.NUM_UNITS/2))*16,textCol-16+20*cy,16,16);
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(Color.black);
        }
        textCol += 40;
        
        if(item == 10)g.setColor(Color.red);
        g.drawString("Snow Chance",10,textCol);
        g.setColor(Color.black);
        g.drawString(String.valueOf(bopt.getSnowChance()) + "%",165,textCol);
        textCol += 20;
        
        if(item == 11)g.setColor(Color.red);
        g.drawString("Rain Chance",10,textCol);
        g.setColor(Color.black);
        g.drawString(String.valueOf(bopt.getRainChance()) + "%",165,textCol);
        textCol += 20;
        
        if(item == 12)g.setColor(Color.red);
        g.drawString("Sandstorm Chance",10,textCol);
        g.setColor(Color.black);
        g.drawString(String.valueOf(bopt.getSandChance()) + "%",165,textCol);
        textCol -=40;
        
        if(item == 13)g.setColor(Color.red);
        g.drawString("Min Weather Duration",210,textCol);
        g.setColor(Color.black);
        g.drawString(String.valueOf(bopt.getMinWTime()) + " Days",380,textCol);
        textCol += 20;
        
        if(item == 14)g.setColor(Color.red);
        g.drawString("Max Weather Duration",210,textCol);
        g.setColor(Color.black);
        g.drawString(String.valueOf(bopt.getMaxWTime())+ " Days",380,textCol);
        textCol += 20;
        
        if(item == 15)g.setColor(Color.red);
        g.drawString("Weather Start",210,textCol);
        g.setColor(Color.black);
        g.drawString("Day: " + String.valueOf(bopt.getMinWDay()),380,textCol);
        textCol += 20;
        

    }
    
    public void drawKeymapScreen(Graphics2D g){
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        if(item == 0)g.setColor(Color.red);
        
        g.drawString("Up-" + KeyEvent.getKeyText(Options.up),10,14);
        g.setColor(Color.black);
        
        if(item == 1)g.setColor(Color.red);
        g.drawString("Down-" + KeyEvent.getKeyText(Options.down),10,28);
        g.setColor(Color.black);
        
        if(item == 2)g.setColor(Color.red);
        g.drawString("Left-" + KeyEvent.getKeyText(Options.left),10,42);
        g.setColor(Color.black);
        
        if(item == 3)g.setColor(Color.red);
        g.drawString("Right-" + KeyEvent.getKeyText(Options.right),10,56);
        g.setColor(Color.black);
        
        if(item == 4)g.setColor(Color.red);
        g.drawString("A Button-" + KeyEvent.getKeyText(Options.akey),10,70);
        g.setColor(Color.black);
        
        if(item == 5)g.setColor(Color.red);
        g.drawString("B Button-" + KeyEvent.getKeyText(Options.bkey),10,84);
        g.setColor(Color.black);
        
        if(item == 6)g.setColor(Color.red);
        g.drawString("Page Up-" + KeyEvent.getKeyText(Options.pgup),10,98);
        g.setColor(Color.black);
        
        if(item == 7)g.setColor(Color.red);
        g.drawString("Page Down-" + KeyEvent.getKeyText(Options.pgdn),10,112);
        g.setColor(Color.black);
        
        if(item == 8)g.setColor(Color.red);
        g.drawString("<-" + KeyEvent.getKeyText(Options.altleft),10,126);
        g.setColor(Color.black);
        
        if(item == 9)g.setColor(Color.red);
        g.drawString(">-" + KeyEvent.getKeyText(Options.altright),10,140);
        g.setColor(Color.black);
        
        if(item == 10)g.setColor(Color.red);
        g.drawString("Menu-" + KeyEvent.getKeyText(Options.menu),10,154);
        g.setColor(Color.black);
        
        if(item == 11)g.setColor(Color.red);
        g.drawString("Minimap-" + KeyEvent.getKeyText(Options.minimap),10,168);
        g.setColor(Color.black);
        
        if(item == 12)g.setColor(Color.red);
        g.drawString("Constant Mode-" + KeyEvent.getKeyText(Options.constmode),10,182);
        g.setColor(Color.black);
        
        //SECOND ROW
        
        if(item == 13)g.setColor(Color.red);
        g.drawString("Delete Unit-" + KeyEvent.getKeyText(Options.delete),130,14);
        g.setColor(Color.black);
        
        if(item == 14)g.setColor(Color.red);
        g.drawString("Terrain Menu-" + KeyEvent.getKeyText(Options.tkey),130,28);
        g.setColor(Color.black);
        
        if(item == 15)g.setColor(Color.red);
        g.drawString("Side Menu-" + KeyEvent.getKeyText(Options.skey),130,42);
        g.setColor(Color.black);
        
        if(item == 16)g.setColor(Color.red);
        g.drawString("Unit Menu-" + KeyEvent.getKeyText(Options.ukey),130,56);
        g.setColor(Color.black);
        
        if(item == 17)g.setColor(Color.red);
        g.drawString("Next Unit-" + KeyEvent.getKeyText(Options.nextunit),130,70);
        g.setColor(Color.black);
        
        g.drawString("Usage: No mouse",130,140);
        g.drawString("1. Select the key",130,152);
        g.drawString("2. Press the A button",130,164);
        g.drawString("3. Press the new key",130,176);
    }
    
public void drawNewLoadScreen(Graphics2D g){
        int offset = 0;
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        if(item == 0)g.setColor(Color.red);
        g.drawString("New",15,30);
        g.setColor(Color.black);
             
        if(item == 1)g.setColor(Color.red);
        g.drawString("Load",15,54);
        g.setColor(Color.black);
       
        if(item == 2)g.setColor(Color.red);
        g.drawString("Network Game",15,78);
        g.setColor(Color.black);
       
        if(item == 3)g.setColor(Color.red);
        g.drawString("Load Replay",15,102);
        g.setColor(Color.black);
       
        if(item == 4)g.setColor(Color.red);
        g.drawString("Create New Server Game",15,126);
        g.setColor(Color.black);
       
        if(item == 5)g.setColor(Color.red);
        g.drawString("Join Server Game",15,150);
        g.setColor(Color.black);
       
        if(item == 6)g.setColor(Color.red);
        g.drawString("Login to Server Game",15,174);
        g.setColor(Color.black);
        
        if(item == 7)g.setColor(Color.red);
        g.drawString("Open Online Lobby",15,198);
        g.setColor(Color.black);
         //Draw CO at the main menu
        //Draw COs
            glide++;
            g.drawImage(MainMenuGraphics.getMainMenuCO(Options.getMainCOID()),329+(int)(100*Math.pow(.95,glide)),-5,329+225+(int)(100*Math.pow(.95,glide)),-5+350,offset,0,offset+225,350,this);
           
        //draw description box
           g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
           g.setColor(new Color(7,66,97));
        g.fillRoundRect(180,255,280,60,20,20);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));           
       
        if (item==0){
        g.drawString("Start a new game. This mode is primarily for",190,275);
        g.drawString("playing against a friend on the same computer.",190,290);
        }else if (item==1){
        g.drawString("Continue where you started off from your",190,275);
        g.drawString("previous game.",190,290);
        }else if (item==2){
        g.drawString("Connect via a friend's IP and enjoy an online ",190,275);
        g.drawString("hotseat game with him or her! Hamachi is ",190,290);
        g.drawString("suggested for the best connectivity results.",190,304);
        }else if (item==3){
        g.drawString("Already finished a game and feel like reliving ",190,275);
        g.drawString("those moments of honour? ",190,290);
        g.drawString("Load the replay here!",190,304);
        }else if (item==4){
        g.drawString("Start a new game on the CW server! If you ",190,275);
        g.drawString("don't have a friend to battle, you should make",190,290);
        g.drawString("an open game so anyone can join and play!",190,304);
        }else if (item==5){
        g.drawString("Join a game on the CW server that's open!",190,275);
        g.drawString("All you need is the game name, a handle and",190,290);
        g.drawString("a password! Then you're all ready to play!",190,304);
        }else if (item==6){
        g.drawString("Login to one of your current games!",190,275);
        g.drawString("Let's hope you're winning, at least.",190,290);
        g.drawString("Otherwise, what's the point to logging in?",190,304);
        }
       
    } 
    
    public void drawSideSelectScreen(Graphics2D g){
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        for(int i = 0; i < numArmies; i++){
            if(item == i)g.setColor(Color.red);
            g.drawString("Army " + (i+1),10,20+20*i);
            g.drawString("Side " + sideSelections[i],70,20+20*i);
            g.setColor(Color.black);
        }
    }
    
    public void drawServerInfoScreen(Graphics2D g){
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        //chat screen
        g.setColor(Color.black);
        g.fillRect(0,0,480,100);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(460,0,20,100);
        g.setColor(Color.WHITE);
        g.fillRect(460,0,20,20);
        g.fillRect(460,80,20,20);
        g.setColor(Color.gray);
        if(item2 == 0)g.setColor(Color.DARK_GRAY);
        g.fillRect(0,100,160,20);
        g.setColor(Color.white);
        g.drawString("Syslog",0,120);
        g.setColor(Color.gray);
        if(item2 == 1)g.setColor(Color.DARK_GRAY);
        g.fillRect(160,100,160,20);
        g.setColor(Color.white);
        g.drawString("Chat",160,120);
        g.setColor(Color.gray);
        g.fillRect(320,100,160,20);
        g.setColor(Color.white);
        g.drawString("Send",320,120);
        //chat messages
        g.setColor(Color.white);
        for(int i=0; i < 5; i++){
            if(item2 == 0){
                if(i+syspos < syslog.length && i+syspos >= 0)g.drawString(syslog[i+syspos],0,18+i*20);
            } else{
                if(i+chatpos < chatlog.length && i+chatpos >= 0)g.drawString(chatlog[i+chatpos],0,18+i*20);
            }
        }
        
        //information
        g.setColor(Color.BLACK);
        g.drawString("Game Name: " + Options.gamename,0,140);
        g.drawString("Login Name: " + Options.username,0,160);
        //g.drawString("Password: " + Options.password,10,80);
        g.drawString("Current day/turn: " + day + "/" + turn,0,180);
        for(int i =0; i < usernames.length; i++){
            g.drawString(usernames[i],(i<5)?0:120,200+(i%5)*20);
        }
        
        //actions
        g.setColor(Color.gray);
        if(item == 0)g.setColor(Color.black);
        g.fillRect(240,280,240,20);
        g.setColor(Color.white);
        g.drawString("Refresh",340,300);
        g.setColor(Color.gray);
        if(item == 1)g.setColor(Color.black);
        g.fillRect(240,300,240,20);
        g.setColor(Color.white);
        g.drawString("Play",350,320);
    }
    
    //remove the component from the frame
    public void removeFromFrame(){
        parentFrame.getContentPane().remove(this);
        parentFrame.removeKeyListener(keycontroller);
        parentFrame.removeMouseListener(mousecontroller);
        parentFrame.removeMouseMotionListener(mousecontroller);
    }
    
    //loads a new minimap preview
    public void loadPreview(){
        ptypes[0] = 0;
        ptypes[1] = 0;
        ptypes[2] = 0;
        ptypes[3] = 0;
        ptypes[4] = 0;
        ptypes[5] = 0;
        if(numMaps != 0){
            filename = filenames[mapPage*12 + item];
            preview = new Battle(filename);
            
            Map m = preview.getMap();
            for(int yy = 0; yy < m.getMaxRow(); yy++){
                for(int xx = 0; xx < m.getMaxCol(); xx++){
                    int number = m.find(new Location(xx,yy)).getTerrain().getIndex();
                    if(number > 9){
                        if(number == 10)ptypes[0]++;
                        else if(number == 11)ptypes[1]++;
                        else if(number == 13)ptypes[2]++;
                        else if(number == 12)ptypes[3]++;
                        else if(number == 14)ptypes[4]++;
                        else if(number == 17)ptypes[5]++;
                    }
                }
            }
        }
    }
    
    //loads the display names
    private void loadMapDisplayNames(){
    	File[] mapFiles= new File[0];
        mapFiles = (File[])FileSystemManager.getAllAvailableMaps().toArray(mapFiles);
        displayNames = new String[mapFiles.length];
        filenames = new String[mapFiles.length];
        //numPlayers = new int[mapFiles.length];
        authors = new String[mapFiles.length];
        descriptions = new String[mapFiles.length];
        numMaps = 0;
        
        for(int i = 0; i < mapFiles.length; i++){
            displayNames[numMaps] = mapFiles[i].getPath();
            filenames[numMaps] = displayNames[numMaps];
            
            if(displayNames[numMaps].substring(displayNames[numMaps].length()-4,displayNames[numMaps].length()).equals(".map")){
                String name = "";
                String author = "";
                String desc = "";
                int num = 0;
                //check name
                try{
                    //open file
                    DataInputStream read = new DataInputStream(new FileInputStream(displayNames[numMaps]));
                    int version = read.readInt();
                    if(version == -1){
                        char tempbyte;
                        //read name
                        while(true){
                            tempbyte = (char)read.readByte();
                            if(tempbyte==0)break;
                            name += tempbyte;
                        }
                        //read author
                        while(true){
                            tempbyte = (char)read.readByte();
                            if(tempbyte==0)break;
                            author += tempbyte;
                        }
                        //read description
                        while(true){
                            tempbyte = (char)read.readByte();
                            if(tempbyte==0)break;
                            desc += tempbyte;
                        }
                        read.readInt();
                        read.readInt();
                        num = (int)read.readByte();
                    }else{
                        read.readInt();
                        num = read.readInt();
                    }
                }catch(IOException e){
                	logger.error("Couldn't read MAP file [ " + filename+ "]", e);
                    logger.error(filenames[numMaps]);
                    logger.error("error:", e);
                    continue;
                }
                
                if(subcat == 0 || subcat == num - 1){
                    if(name.equals("")){
                        //Clean up name
                        int lastslash = displayNames[numMaps].indexOf('\\');
                        if(lastslash==-1){
                            lastslash = displayNames[numMaps].indexOf('/');
                            while(displayNames[numMaps].indexOf('/',lastslash+1)!=-1){
                                lastslash = displayNames[numMaps].indexOf('/',lastslash+1);
                            }
                        }else{
                            while(displayNames[numMaps].indexOf('\\',lastslash+1)!=-1){
                                lastslash = displayNames[numMaps].indexOf('\\',lastslash+1);
                            }
                        }
                        displayNames[numMaps] = displayNames[numMaps].substring(lastslash+1,displayNames[numMaps].length()-4);
                    }else{
                        displayNames[numMaps] = name;
                    }
                    
                    if(author.equals("")){
                        authors[numMaps] = "Unknown";
                    }else{
                        authors[numMaps] = author;
                    }
                    
                    if(desc.equals("")){
                        descriptions[numMaps] = "No information is available on this map";
                    }else{
                        descriptions[numMaps] = desc;
                    }
                    //numPlayers[numMaps] = num;
                    numMaps++;
                }
            }
        }
        loadPreview();
    }
    
    public void pressedA()
    {
        if(inTitleScreen)
        {
            titleScreenActions();
        }
        else if(info)
        {
            info = false;
        }
        else if(inCOselectScreen && !info)
        {
            coSelectScreenActions();
        }
        else if(inOptionsScreen)
        {
            optionsScreenActions();
        }
        else if(inStartANewGameScreen)
        {
            newLoadOrNetworkBranch();
        }
        else if(sideSelect)
        {
            sideSelect = false;
            inBattleOptionsScreen = true;
            item = 0;
        }
        else if(inBattleOptionsScreen)
        {
            startBattle();
        }
        else if(inMapSelectScreen)
        {
            mapSelectScreenActions();
        }
        else if(keymap)
        {
            chooseKey = true;
        }
        else if(snailinfo)
        {
            networkInfoScreenActions();
        }
    }

	private void startBattle() {
		if(item == 9)
		{
		    if(bopt.isUnitBanned(cx))
		    {
		        bopt.setUnitBanned(false,cx);
		    }
		    else
		    {
		        bopt.setUnitBanned(true,cx);
		    }
		    return;
		}
		//Creates new instances of critical objects for the battle
		Battle b = new Battle(filename,coSelections,sideSelections,altSelections,bopt);
		
		//Initialize a swing frame and put a BattleScreen inside
		parentFrame.setSize(400,400);
		removeFromFrame();
		BattleScreen bs = new BattleScreen(b,parentFrame);
		parentFrame.getContentPane().add(bs);
		parentFrame.validate();
		parentFrame.pack();
		
		//Start the mission
		Mission.startMission(b,bs);
		//save the initial state for the replay if applicable
		if(bopt.isRecording())Mission.saveInitialState();
	}

	private void networkInfoScreenActions() {
		if(item == 0){
		    //refresh
		    logger.info("Refreshing");
		    refreshInfo();
		}else if(item == 1){
		    //play game
		    logger.info("Play Game");
		    //refresh first
		    refreshInfo();
		    
		    String reply = sendCommandToMain("canplay",Options.gamename+"\n"+Options.username+"\n"+Options.password);
		    logger.info(reply);
		    if(reply.equals("permission granted")){
		        if(day == 1){
		            logger.info("Starting Game");
		            //load map from server
		            getFile("dmap.pl", Options.gamename, TEMPORARYMAP_MAP_FILENAME);
		            filename = TEMPORARYMAP_MAP_FILENAME;
		            
		            //goto co select
		            inMapSelectScreen = false;
		            inCOselectScreen = true;
		            snailinfo = false;
		            
		            //find number of armies, and thus COs
		            try{
		                DataInputStream read = new DataInputStream(new FileInputStream( ResourceLoader.properties.getProperty("saveLocation") + "/" + filename));
		                int maptype = read.readInt();
		                if(maptype == -1){
		                    while(read.readByte()!=0); //skip name
		                    while(read.readByte()!=0); //skip author
		                    while(read.readByte()!=0); //skip description
		                    read.readInt();
		                    read.readInt();
		                    numArmies = read.readByte();
		                }else{
		                    read.readInt();
		                    numArmies = read.readInt();
		                }
		            }catch(IOException exc){
		            	logger.error("Couldn't read MAP file [" + filename+"]", exc);
		                System.exit(1);
		            }
		            numCOs = 0;
		            coSelections = new int[numArmies*2];
		            altSelections = new boolean[numArmies*2];
		            if(turn!=1){
		                insertNewCO = true;
		            }
		            return;
		        }
		        
		        //load mission
		        getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
		        String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
		        //load mission
		        Battle b = new Battle(new Map(30,20));
		        //Initialize a swing frame and put a BattleScreen inside
		        parentFrame.setSize(400,400);
		        removeFromFrame();
		        BattleScreen bs = new BattleScreen(b,parentFrame);
		        parentFrame.getContentPane().add(bs);
		        parentFrame.validate();
		        parentFrame.pack();
		        
		        //Start the mission
		        Mission.startMission(null,bs);
		        Mission.loadMission(loadFilename);
		    }
		}
	}

	private void mapSelectScreenActions() {
		if(numMaps != 0){
		    //New Game
		    inMapSelectScreen = false;
		    inCOselectScreen = true;
		    
		    //File[] tempFile = mapDir.listFiles();
		    filename = filenames[mapPage*12 + item];
		    String mapName = displayNames[mapPage*12 + item];
		    
		    //find number of armies, and thus COs
		    try{
		        DataInputStream read = new DataInputStream(new FileInputStream(filename));
		        int maptype = read.readInt();
		        if(maptype == -1){
		            while(read.readByte()!=0); //skip name
		            while(read.readByte()!=0); //skip author
		            while(read.readByte()!=0); //skip description
		            read.readInt();
		            read.readInt();
		            numArmies = read.readByte();
		        }else{
		            read.readInt();
		            numArmies = read.readInt();
		        }
		    }catch(IOException exc){
		    	logger.error("Could not read Map file [" + filename+ "]", exc);
		        System.exit(1);
		    }
		    
		    //New Snail Mode Game
		    if(Options.snailGame){
		        inCOselectScreen = false;
		        snailinfo = true;
		        item = 0;
		        item2 = 0;
		        
		        String comment = JOptionPane.showInputDialog("Type in a comment for your game");
		        
		        //get army that the player wants to join
		        int joinnum = -1;
		        while(joinnum < 1 || joinnum > numArmies){
		            String t = JOptionPane.showInputDialog("What side do you want to join? Pick from 1-" + numArmies);
		            if (t == null){
		                Options.snailGame = false;
		                inMapSelectScreen = false;
		                inTitleScreen = true;
		                return;
		            }
		            joinnum = Integer.parseInt(t);
		        }
		        
		        //register new game
		        String reply = sendCommandToMain("newgame",Options.gamename+"\n"+Options.masterpass+"\n"+numArmies+"\n"+Options.version+"\n"+comment+"\n"+mapName+"\n"+Options.username);
		        while(!reply.equals("game created")){
		            logger.info(reply);
		            if(reply.equals("no")){
		                logger.info("Game name taken");
		                JOptionPane.showMessageDialog(this,"Game name taken");
		                Options.gamename = JOptionPane.showInputDialog("Type in a new name for your game");
		                if(Options.gamename == null)return;
		                reply = sendCommandToMain("newgame",Options.gamename+"\n"+Options.masterpass+"\n"+numArmies+"\n"+Options.version+"\n"+comment+"\n"+mapName+"\n"+Options.username);
		            }else{
		                Options.snailGame = false;
		                inMapSelectScreen = false;
		                inTitleScreen = true;
		                return;
		            }
		        }
		        
		        //upload map
		        String temp = sendFile("umap.pl", Options.gamename, filename);
		        logger.info(temp);
		        //TODO: keep retrying if failed
		        
		        //Join Game
		        reply = sendCommandToMain("join",Options.gamename+"\n"+Options.masterpass+"\n"+Options.username+"\n"+Options.password+"\n"+joinnum+"\n"+Options.version);
		        logger.info(reply);
		        //TODO: keep retrying if failed OR merge with game creation in this case
		        
		        //Goto info screen
		        refreshInfo();
		        return;
		    }
		    
		    numCOs = 0;
		    coSelections = new int[numArmies*2];
		    altSelections = new boolean[numArmies*2];
		}
	}

	private void newLoadOrNetworkBranch() {
		boolean startCOSelect = false;
        String tempSaveLocation = ResourceLoader.properties.getProperty("tempSaveLocation");	
		
		String loadFilename =  tempSaveLocation + "/temporarysave.save";
		
		if(item == 0)
		{
		    startCOSelect = true;
		}
		else if(item==1)
		{
		    JFileChooser fc = new JFileChooser();
		    fc.setDialogTitle("Load Game");
		    fc.setCurrentDirectory(new File("./"));
		    fc.setApproveButtonText("Load");
		    //FileNameExtensionFilter filter = new FileNameExtensionFilter(
		    //        "CW Save Files", "save");
		    //fc.setFileFilter(filter);
		    int returnVal = fc.showOpenDialog(this);
		    
		    if(returnVal != 1){
		    	loadFilename = fc.getSelectedFile().getPath();
		        File saveFile = new File(loadFilename);
		        if(saveFile.exists()){
		            Battle b = new Battle(new Map(30,20));
		            //Initialize a swing frame and put a BattleScreen inside
		            parentFrame.setSize(400,400);
		            removeFromFrame();
		            BattleScreen bs = new BattleScreen(b,parentFrame);
		            parentFrame.getContentPane().add(bs);
		            parentFrame.validate();
		            parentFrame.pack();
		            
		            //Start the mission
		            Mission.startMission(null,bs);
		            Mission.loadMission(loadFilename);
		        }
		    }
		}
		else if(item==2)
		{
		    //Network Game
		    startCOSelect = true;
		    Options.startNetwork();
		    item = 0;
		}
		else if(item==3)
		{
		    //prompt for replay name
		    logger.info("REPLAY MODE");
		    //Load Replay
		    JFileChooser fc = new JFileChooser();
		    fc.setDialogTitle("Load Replay");
		    fc.setCurrentDirectory(new File("./"));
		    fc.setApproveButtonText("Load");
		    //FileNameExtensionFilter filter = new FileNameExtensionFilter(
		    //        "CW Replay Files", "replay");
		    //fc.setFileFilter(filter);
		    int returnVal = fc.showOpenDialog(this);
		    
		    if(returnVal != 1){
		        loadFilename = fc.getSelectedFile().getPath();
		        
		        File saveFile = new File(loadFilename);
		        if(saveFile.exists()){
		            Battle b = new Battle(new Map(30,20));
		            //Initialize a swing frame and put a BattleScreen inside
		            parentFrame.setSize(400,400);
		            removeFromFrame();
		            BattleScreen bs = new BattleScreen(b,parentFrame);
		            parentFrame.getContentPane().add(bs);
		            parentFrame.validate();
		            parentFrame.pack();
		            
		            //Start the mission
		            Mission.startMission(null,bs);
		            Mission.loadReplay(loadFilename);
		        }
		    }
		}else if(item==4){
		    logger.info("Create Server Game");
		    //try to connect to the server first to see that the user's URL is correct
		    if(!tryToConnect())return;
		    
		    //find an unused name
		    Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
		    if(Options.gamename == null)return;
		    String reply = sendCommandToMain("qname",Options.gamename);
		    while(!reply.equals("yes")){
		        logger.info(reply);
		        if(reply.equals("no")){
		            logger.info("Game name already taken");
		            JOptionPane.showMessageDialog(this,"Game name already taken");
		        }
		        Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
		        if(Options.gamename == null)return;
		        reply = sendCommandToMain("qname",Options.gamename);
		    }
		    
		    //set the master password and join
		    Options.masterpass = JOptionPane.showInputDialog("Type in a master password for your game");
		    if(Options.masterpass == null)return;
		    if(Options.isDefaultLoginOn()){
		    	Options.username = Options.getDefaultUsername();
		    	Options.password = Options.getDefaultPassword();
		    	
		    	if(Options.username == null || Options.username.length()<1 || Options.username.length()>12)
		    		return;
		    }else{
		    	while(true){
		    		Options.username = JOptionPane.showInputDialog("Type in your username for this game (12 characters max)");
		    		if(Options.username == null)return;
		    		if(Options.username.length()<1)continue;
		    		if(Options.username.length()>12)continue;
		    		break;
		    	}
		        Options.password = JOptionPane.showInputDialog("Type in your password for this game");
		        if(Options.password == null)return;
		    }
		    
		    //start game
		    logger.info("starting game");
		    Options.snailGame = true;
		    startCOSelect = true;
		    item = 0;
		    
		}else if(item==5){
		    logger.info("Join Server Game");
		    
		    //try to connect to the server first to see that the user's URL is correct
		    if(!tryToConnect())return;
		    
		    //connect to the game
		    Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
		    if(Options.gamename == null)return;
		    
		    //check the master password and get number of players and available slots
		    Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
		    if(Options.masterpass == null)return;
		    
		    //Get user's name, password, and slot
		    if(Options.isDefaultLoginOn()){
		    	Options.username = Options.getDefaultUsername();
		    	Options.password = Options.getDefaultPassword();
		    	
		    	if(Options.username == null || Options.username.length()<1 || Options.username.length()>12)
		    		return;
		    }else{
		    	while(true){
		    		Options.username = JOptionPane.showInputDialog("Type in your username for this game (12 characters max)");
		    		if(Options.username == null)return;
		    		if(Options.username.length()<1)continue;
		    		if(Options.username.length()>12)continue;
		    		break;
		    	}
		    	Options.password = JOptionPane.showInputDialog("Type in your password for this game");
		    	if(Options.password == null)return;
		    }
		    inStartANewGameScreen = false;
		    snailinfo = true;
		    refreshInfo();
		    if(!snailinfo){
		        JOptionPane.showMessageDialog(this,"The game "+Options.gamename+" has ended");
		        return;
		    }
		    String slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
		    if(slot == null){
		        inTitleScreen = true;
		        snailinfo = false;
		        return;
		    }
		    
		    //Join
		    String reply = sendCommandToMain("join",Options.gamename+"\n"+Options.masterpass+"\n"+Options.username+"\n"+Options.password+"\n"+slot+"\n"+Options.version);
		    while(!reply.equals("join successful")){
		        logger.info(reply);
		        if(reply.equals("no")){
		            logger.info("Game does not exist");
		            Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
		            if(Options.gamename == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		            Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
		            if(Options.masterpass == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		        }else if(reply.equals("wrong password")){
		            logger.info("Incorrect Password");
		            Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
		            if(Options.gamename == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		            Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
		            if(Options.masterpass == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		        }else if(reply.equals("out of range")){
		            logger.info("Army choice out of range or invalid");
		            slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
		            if(slot == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		        }else if(reply.equals("slot taken")){
		            logger.info("Army choice already taken");
		            slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
		            if(slot == null){
		                inTitleScreen = true;
		                snailinfo = false;
		                return;
		            }
		        }else{
		            logger.info("Other problem");
		            JOptionPane.showMessageDialog(this,"Version Mismatch");
		            Options.snailGame = false;
		            snailinfo = false;
		            inTitleScreen = true;
		            return;
		        }
		        refreshInfo();
		        reply = sendCommandToMain("join",Options.gamename+"\n"+Options.masterpass+"\n"+Options.username+"\n"+Options.password+"\n"+slot+"\n"+Options.version);
		    }
		    
		    //go to information screen
		    Options.snailGame = true;
		    snailinfo = true;
		    inStartANewGameScreen = false;
		    item = 0;
		    item2 = 0;
		    
		    refreshInfo();
		    return;
		    
		}else if(item==6){
		    logger.info("Log in to Server Game");
		    
		    //try to connect to the server first to see that the user's URL is correct
		    if(!tryToConnect())return;
		    
		    //connect to the game
		    Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to login to");
		    if(Options.gamename == null)return;
		    
		    //Get user's name and password
		    if(Options.isDefaultLoginOn()){
		    	Options.username = Options.getDefaultUsername();
		    	Options.password = Options.getDefaultPassword();
		    	
		    	if(Options.username == null || Options.username.length()<1 || Options.username.length()>12)
		    		return;
		    }else{
		    	Options.username = JOptionPane.showInputDialog("Type in your username for this game");
		    	if(Options.username == null)return;
		    	Options.password = JOptionPane.showInputDialog("Type in your password for this game");
		    	if(Options.password == null)return;
		    }
		    
		    //try to connect
		    String reply = sendCommandToMain("validup",Options.gamename+"\n"+Options.username+"\n"+Options.password+"\n"+Options.version);
		    logger.info(reply);
		    if(!reply.equals("login successful")){
		        if(reply.equals("version mismatch"))JOptionPane.showMessageDialog(this,"Version Mismatch");
		        else JOptionPane.showMessageDialog(this,"Problem logging in, either the username/password is incorrect or the game has ended");
		        return;
		    }
		    
		    //go to information screen
		    Options.snailGame = true;
		    snailinfo = true;
		    inStartANewGameScreen = false;
		    item = 0;
		    item2 = 0;
		    
		    refreshInfo();
		    return;
		}
		else if(item == 7)
		{
			parentFrame.setVisible(false);
			FobbahLauncher.init(parentFrame, this);
			BigFrame frame = new BigFrame();
		}
		
		if(startCOSelect){
		    //New Game
		    inStartANewGameScreen = false;
		    inMapSelectScreen = true;
		    item = 0;
		    mapPage = 0;
		    
		    //load categories
	        String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
	        mapsLocation = mapsLocation + "/";
	        
	        FilenameFilter filter = new FilenameFilter() {
	        	public boolean accept(File dir, String name) {
	        		return !name.startsWith(".");
	        	}
	        };
	        
	        File[] dirs = new File(mapsLocation).listFiles(filter);
		    
		    
		    Vector<String> v = new Vector();
		    int numcats = 0;
		    for(int i = 0; i < dirs.length; i++){
		        if(dirs[i].isDirectory()){
		            v.add(dirs[i].getName());
		            numcats++;
		        }
		    }
		    if(numcats == 0){
		        logger.info("NO MAP DIRECTORIES! QUITTING!");
		        System.exit(1);
		    }
		    cats = new String[numcats];
		    for(int i = 0; i < numcats; i++){
		        cats[i] = v.get(i);
		    }
		    
		    cat = 0;
		    subcat = 0;
		    
		    mapDir = new File(mapsLocation + "/" + cats[cat]);
		    loadMapDisplayNames();
		    mapPage = 0;
		}
	}

	private void coSelectScreenActions() 
	{
		boolean nosecco = false;
		CO temp = null;
		if(cx==0 && cy==0 && numCOs % 2 == 1)
		{
		    coSelections[numCOs] = -1;
		    nosecco = true;
		}
		else
		{
		    if(cx==0 && cy==0)return;
		    temp = armyArray[selectedArmy][cx+cy*3-1];
		}
		
		if(nosecco || temp != null && !(numCOs % 2 == 1 && COList.getIndex(temp) == coSelections[numCOs-1]))
		{
		    if(!nosecco)
		    {
		    	//coSelections[numCOs] = COList.getIndex(temp);
		    	coSelections[numCOs] = COList.getIndex(temp);
		    }
		    
		    altSelections[numCOs] = altcostume;
		    mainaltcostume = altcostume;
		    altcostume = false;
		    numCOs++;
		    cx = 0;
		    cy = 0;
		    
		    if(Options.snailGame && numCOs == 2){
		        logger.info("Stop for snail game");
		        if(insertNewCO){
		            //load mission
		            getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
		            String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
		            //load mission
		            Battle b = new Battle(new Map(30,20));
		            //Initialize a swing frame and put a BattleScreen inside
		            parentFrame.setSize(400,400);
		            removeFromFrame();
		            BattleScreen bs = new BattleScreen(b,parentFrame);
		            parentFrame.getContentPane().add(bs);
		            parentFrame.validate();
		            parentFrame.pack();
		            
		            //Start the mission
		            Mission.startMission(null,bs);
		            Mission.loadMission(loadFilename);
		            bs.getBattle().getArmy(turn-1).setCO(bs.getBattle().getCO(coSelections[0]));
		            bs.getBattle().getArmy(turn-1).setAltCO(bs.getBattle().getCO(coSelections[1]));
		        }
		        //fill rest with single andys
		        for(int i = 2; i < coSelections.length; i++){
		            if(i%2==0)coSelections[i]=1;
		            else coSelections[i]=0;
		        }
		        for(int i = 0; i < coSelections.length; i++)logger.info(""+coSelections[i]);
		        logger.info("Number of COs: "+numCOs);
		        if(numArmies > 2){
		            inCOselectScreen = false;
		            sideSelect = true;
		            item = 0;
		            sideSelections = new int[numArmies];
		            for(int i=0; i<numArmies; i++)sideSelections[i] = i;
		        }else{
		            //no alliances allowed for 2 players
		            sideSelections = new int[] {0,1};
		            inCOselectScreen = false;
		            inBattleOptionsScreen = true;
		            item = 0;
		        }
		    }
		    
		    if(numCOs == numArmies*2){
		        //coSelections[numCOs] = cx+cy*4;
		        //numCOs++;
			    logger.info("Total No of competing COs=["+numCOs+"]  Armies=["+numArmies+"]");
		        
		        //int[] sideSelect = {0,0};
		        if(numCOs > 4){
		            inCOselectScreen = false;
		            sideSelect = true;
		            item = 0;
		            sideSelections = new int[numCOs/2];
		            for(int i=0; i<numCOs/2; i++)sideSelections[i] = i;
		        }else{
		            //no alliances allowed for 2 players
		            sideSelections = new int[] {0,1};
		            
		            inCOselectScreen = false;
		            inBattleOptionsScreen = true;
		            item = 0;
		        }
		    }
		}
	}

	private void optionsScreenActions() {
		if(item == 0)
		{
		    //Music On/Off
		    if(Options.isMusicOn())Options.turnMusicOff();
		    else Options.turnMusicOn();
		}
		else if(item==1)
		{
		    //RNG
		                /*int l = 0;
		                boolean valid = false;
		                while(!valid){
		                    String x = JOptionPane.showInputDialog("input a positive int for the seeding of the RNG, 0 or lower for random");
		                    if(x.length() > 0){
		                        try{
		                            l = Integer.parseInt(x);
		                            valid = true;
		                        }catch(NumberFormatException exc){
		                            valid = false;
		                        }
		                    }else{
		                        valid = true;
		                    }
		                }
		                Options.setRNG(l);*/
		}
		else if(item==2)
		{
		    //Balance Mode On/Off
		    if(Options.isBalance())
		    {
		        Options.turnBalanceModeOff();
		    }
		    else
		    {
		        Options.turnBalanceModeOn();
		    }
		    bopt.setBalance(Options.isBalance());
		}
		else if(item==3)
		{
		    //Change the IP address
		    Options.setIP();
		}
		else if(item==4)
		{
		    if(Options.isAutosaveOn())
		    	Options.setAutosave(false);
		    
		    else
		    	Options.setAutosave(true);
		}
		else if(item==5)
		{
		    if(Options.isRecording())Options.setRecord(false);
		    else Options.setRecord(true);
		}
		else if(item==7)
		{
		    //remap keys
		    inOptionsScreen = false;
		    keymap = true;
		    item = 0;
		}
		else if(item==8)
		{
		    Options.toggleBattleBackground();
		}
		else if(item==9)
		{
		    //Change the IP address
		    Options.setServer();
		}
		else if(item==10)
		{
		    Options.incrementDefaultBans();
		    bopt = new BattleOptions();
		}
		else if(item == 13 && Options.getSelectedTerrain() == 2)
		{
		    Options.setCustomTerrain();
		}
		else if(item == 14 && Options.getSelectedUrban() == 2)
		{
		    Options.setCustomUrban();
		}
		else if(item == 15 && Options.getSelectedHQ() == 2)
		{
		    Options.setCustomHQ();
		}
		else if(item==16)
		{
			Options.toggleDefaultLogin();
		}
		else if(item==17)
		{
		    Options.setDefaultLogin();
		}
                else if(item==18)
                {
                    Options.toggleRefresh();
                }
	}

	private void titleScreenActions() {
		if(item==0)
		{
		    inTitleScreen = false;
		    inStartANewGameScreen = true;
		}
		else if(item==1)
		{
		    //Start the map editor
		    logger.info("Map Editor");
		    Map m = new Map(30,20);
		    //Map m = new Map(16,12);
		    Battle bat = new Battle(m);
		    
		    parentFrame.setSize(400,400);
		    MapEditor me = new MapEditor(bat,parentFrame);
		    removeFromFrame();
		    parentFrame.getContentPane().add(me);
		    parentFrame.validate();
		    parentFrame.pack();
		}
		else if(item==2)
		{
		    //Goto the option menu
		    inTitleScreen = false;
		    inOptionsScreen = true;
		    item = 0;
		}
	}
    
    //try to connect to the server to see that the user's URL is correct
    public boolean tryToConnect(){
        String command = "test";
        String reply = "";
        try{
            URL url = new URL(Options.getServerName() + "main.pl");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-type", "text/plain");
            con.setRequestProperty("Content-length", command.length()+"");
            PrintStream out = new PrintStream(con.getOutputStream());
            out.print(command);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s;
            while ((s = in.readLine()) != null) {
                reply += s;
            }
            in.close();
        }catch(MalformedURLException e1){
            logger.error("Bad URL");
            JOptionPane.showMessageDialog(this,"Bad URL: "+Options.getServerName());
            return false;
        }catch(IOException e2){
            logger.error("Unable to connect to the server at "+Options.getServerName());
            JOptionPane.showMessageDialog(this,"Unable to connect to the server at "+Options.getServerName());
            return false;
        }
        logger.info(reply);
        if(!reply.equals("success")){
        	logger.info("Could not connect to server");
            return false;
        }
        
        return true;
    }
    
    //try to connect to the server to see that the user's URL is correct
    public String sendCommandToMain(String command, String extra){
        String reply = "";
        try{
            URL url = new URL(Options.getServerName() + "main.pl");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-type", "text/plain");
            if(extra.equals("")){
                con.setRequestProperty("Content-length", command.length()+"");
            }else{
                con.setRequestProperty("Content-length", (command.length()+1+extra.length())+"");
            }
            PrintStream out = new PrintStream(con.getOutputStream());
            out.print(command);
            if(!extra.equals("")){
                out.print("\n");
                out.print(extra);
            }
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s = in.readLine();
            if(s != null){
                reply += s;
                while ((s = in.readLine()) != null) {
                    reply += "\n";
                    reply += s;
                }
            }
            in.close();
        }catch(MalformedURLException e1){
            logger.info("Bad URL "+Options.getServerName());
            JOptionPane.showMessageDialog(this,"Bad URL: "+Options.getServerName());
            return null;
        }catch(IOException e2){
            logger.error("Connection Problem during command "+command+" with information:\n"+extra);
            JOptionPane.showMessageDialog(this,"Connection Problem during command "+command+" with the following information:\n"+extra);
            return null;
        }
        
        return reply;
    }
    
    public String sendFile(String script, String input, String file){
        String reply = "";
        try{
            URL url = new URL(Options.getServerName() + script);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-type", "text/plain");
            byte buffer[] = new byte[1];
            logger.info("opening file");
            File source = new File(file);
            con.setRequestProperty("Content-length", (input.length()+1+source.length())+"");
            
            PrintStream out1 = new PrintStream(con.getOutputStream());
            out1.print(input);
            out1.print("\n");
            FileInputStream src = new FileInputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
            logger.debug("Sending file [" + src+"]");
            OutputStream out = con.getOutputStream();
            while(true){
                int count = src.read(buffer);
                if(count == -1)break;
                out.write(buffer);
            }
            out.flush();
            out.close();
            out1.flush();
            out1.close();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s = in.readLine();
            if(s != null){
                reply += s;
                while ((s = in.readLine()) != null) {
                    reply += "\n";
                    reply += s;
                }
            }
            in.close();
        }catch(MalformedURLException e1){
            logger.error("Bad URL "+Options.getServerName());
            JOptionPane.showMessageDialog(this,"Bad URL: "+Options.getServerName());
            return null;
        }catch(IOException e2){
            logger.error("Connection problem, unable to send file");
            JOptionPane.showMessageDialog(this,"Connection problem, unable to send file");
            return null;
        }
        
        return reply;
    }
    
    public boolean getFile(String script, String input, String file){
        try{
            URL url = new URL(Options.getServerName() + script);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-type", "text/plain");
            logger.info("opening file");
            File source = new File(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
            logger.debug("Getting file [" + source+"]");
            con.setRequestProperty("Content-length", input.length()+"");
            PrintStream out = new PrintStream(con.getOutputStream());
            out.print(input);
            out.flush();
            out.close();
            
            //recieve reply
            byte buffer[] = new byte[1];
            FileOutputStream output = new FileOutputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" +file);
            logger.debug("Getting reply [" + ResourceLoader.properties.getProperty("saveLocation") + "/" +file +"]");
            InputStream in = con.getInputStream();
            while(true){
                int count = in.read(buffer);
                if(count == -1)break;
                output.write(buffer);
            }
            in.close();
            output.close();
        }catch(MalformedURLException e1){
            logger.error("Bad URL "+Options.getServerName());
            JOptionPane.showMessageDialog(this,"Bad URL: "+Options.getServerName());
            return false;
        }catch(IOException e2){
            logger.error("Connection problem, unable to get file from server");
            JOptionPane.showMessageDialog(this,"Connection problem, unable to get file from server");
            return false;
        }
        
        return true;
    }
    
    public void pressedB(){
        if(info){
            info = false;
        }else if(inCOselectScreen&&!info){
            if(numCOs == 0){
                //title = true;
                inMapSelectScreen = true;
                inCOselectScreen = false;
                item = 0;
                selectedArmy = 0;
                if(Options.isNetworkGame())Options.stopNetwork();
                if(Options.snailGame){
                    inMapSelectScreen = false;
                    inTitleScreen = true;
                    Options.snailGame = false;
                }
            }else{
                coSelections[numCOs] = -1;
                numCOs--;
                cx = 0;
                cy = 0;
            }
        }else if(inOptionsScreen){
            inTitleScreen = true;
            inOptionsScreen = false;
            item = 0;
            if(Options.isNetworkGame())Options.stopNetwork();
        }else if(inStartANewGameScreen){
            inTitleScreen = true;
            inStartANewGameScreen = false;
            item = 0;
            if(Options.isNetworkGame())Options.stopNetwork();
        }else if(sideSelect){
            //title = true;
            inCOselectScreen = true;
            numCOs--;
            sideSelect = false;
            Options.snailGame = false; //not always needed, but doesn't hurt
            item = 0;
            if(Options.isNetworkGame())Options.stopNetwork();
        }else if(inBattleOptionsScreen){
            //title = true;
            if(numCOs > 4)sideSelect = true;
            else{
                numCOs--;
                inCOselectScreen = true;
            }
            inBattleOptionsScreen = false;
            Options.snailGame = false; //not always needed, but doesn't hurt
            item = 0;
            cx = 0;
            cy = 0;
            if(Options.isNetworkGame())Options.stopNetwork();
        }else if(inMapSelectScreen){
            inStartANewGameScreen = true;
            inMapSelectScreen = false;
            Options.snailGame = false; //not always needed, but doesn't hurt
            item = 0;
            if(Options.isNetworkGame())Options.stopNetwork();
        }else if(keymap){
            keymap=false;
            inOptionsScreen = true;
            item = 0;
        }else if(snailinfo){
            Options.snailGame = false;
            snailinfo = false;
            inStartANewGameScreen = true;
            item = 0;
        }
    }
    
    public void pressedPGDN(){
        if(inMapSelectScreen){
            mapPage++;
            if(mapPage>numMaps/12 || (mapPage==numMaps/12 && numMaps%12==0)){
                mapPage--;
            }else
                item=0;
            
            loadPreview();
        }else if(snailinfo){
            if(item2 == 0){
                syspos++;
                if(syspos > syslog.length-5)syspos = syslog.length-5;
                if(syspos < 0) syspos = 0;
            }else if(item2 == 1){
                chatpos++;
                if(chatpos > chatlog.length-5)chatpos = chatlog.length-5;
                if(chatpos < 0) chatpos = 0;
            }
        }
    }
    
    public void pressedPGUP(){
        if(inMapSelectScreen){
            mapPage--;
            if(mapPage<0){
                mapPage++;
            }else
                item=0;
            
            loadPreview();
        }else if(snailinfo){
            if(item2 == 0){
                syspos--;
                if(syspos < 0)syspos = 0;
            }else if(item2 == 1){
                chatpos--;
                if(chatpos < 0)chatpos = 0;
            }
        }
    }
    
    public void processRightKeyBattleOptions()
    {
        if(item == 0)
        {
        	visibility++;
        	if(visibility > 2)visibility = 0;
        	
        	if(visibility == 0)
        	{
        		bopt.setFog(false);
        		bopt.setMist(false);
        	}
        	else if(visibility == 1)
        	{
        		bopt.setFog(true);
        		bopt.setMist(false);
        	}
        	else
        	{
        		bopt.setMist(true);
        		bopt.setFog(false);
        	}
        }
        else if(item==1){
            int wtemp = bopt.getWeatherType();
            wtemp++;
            if(wtemp > 4)wtemp = 0;
            bopt.setWeatherType(wtemp);
        }else if(item==2){
            int ftemp = bopt.getFundsLevel();
            ftemp += 500;
            if(ftemp > 10000)ftemp = 10000;
            bopt.setFundsLevel(ftemp);
        }else if(item==3){
            int stemp = bopt.getStartFunds();
            stemp += 500;
            if(stemp > 30000) {
                stemp = 30000;
            }
            bopt.setStartFunds(stemp);
        }else if(item==4){
            int temp = bopt.getTurnLimit();
            temp++;
            bopt.setTurnLimit(temp);
        }else if(item==5){
            int temp = bopt.getCapLimit();
            temp++;
            bopt.setCapLimit(temp);
        }else if(item==6){
            if(bopt.isCOP())bopt.setCOP(false);
            else bopt.setCOP(true);
        }else if(item==7){
            if(bopt.isBalance())bopt.setBalance(false);
            else bopt.setBalance(true);
        }else if(item==8){
            if(bopt.isRecording())bopt.setReplay(false);
            else bopt.setReplay(true);
        }else if(item==9){
            cx++;
            if(cx >= BaseDMG.NUM_UNITS/2)cy=1;
            if(cx >= BaseDMG.NUM_UNITS){
                cx=0;
                cy=0;
            }
        }else if(item==10){
            if(bopt.getSnowChance()<100)
            bopt.setSnowChance(bopt.getSnowChance()+1);
        }else if(item==11){
            if(bopt.getRainChance()<100)
            bopt.setRainChance(bopt.getRainChance()+1);
        }else if(item==12){
            if(bopt.getSandChance()<100)
            bopt.setSandChance(bopt.getSandChance()+1);
        }else if(item==13){
            bopt.setMinWTime(bopt.getMinWTime()+1);
        }else if(item==14){
            bopt.setMaxWTime(bopt.getMaxWTime()+1);
        }else if(item==15){
            bopt.setMinWDay(bopt.getMinWDay()+1);
        }
    }
    
    public void returnToServerInfo(){
        snailinfo = true;
        inTitleScreen = false;
        Options.snailGame = true;
        refreshInfo();
    }
    
    public void refreshInfo(){
        String reply = sendCommandToMain("getturn",Options.gamename);
        logger.info(reply);
        if(reply.equals("no")){
            snailinfo = false;
            inStartANewGameScreen = true;
            Options.snailGame = false;
            item = 0;
            return;
        }
        String[] nums = reply.split("\n");
        day = Integer.parseInt(nums[0]);
        turn = Integer.parseInt(nums[1]);
        int numplay = Integer.parseInt(nums[2]);
        usernames = new String[numplay];
        for(int i = 0; i < numplay; i++)usernames[i] = nums[3+i];
        
        reply = sendCommandToMain("getsys",Options.gamename);
        syslog = reply.split("\n");
        syspos = syslog.length-5;
        if(syspos < 0)syspos = 0;
        
        reply = sendCommandToMain("getchat",Options.gamename);
        chatlog = reply.split("\n");
        chatpos = chatlog.length-5;
        if(chatpos < 0)chatpos = 0;
    }
    
    //This class deals with keypresses
    class KeyControl implements KeyListener{
        public void keyTyped(KeyEvent e) {}
        
        public void keyPressed(KeyEvent e) {
            int keypress = e.getKeyCode();
            //deal with key remapping
            if(chooseKey){
                switch(item){
                    case 0:
                        Options.up = keypress;
                        break;
                    case 1:
                        Options.down = keypress;
                        break;
                    case 2:
                        Options.left = keypress;
                        break;
                    case 3:
                        Options.right = keypress;
                        break;
                    case 4:
                        Options.akey = keypress;
                        break;
                    case 5:
                        Options.bkey = keypress;
                        break;
                    case 6:
                        Options.pgup = keypress;
                        break;
                    case 7:
                        Options.pgdn = keypress;
                        break;
                    case 8:
                        Options.altleft = keypress;
                        break;
                    case 9:
                        Options.altright = keypress;
                        break;
                    case 10:
                        Options.menu = keypress;
                        break;
                    case 11:
                        Options.minimap = keypress;
                        break;
                    case 12:
                        Options.constmode = keypress;
                        break;
                    case 13:
                        Options.delete = keypress;
                        break;
                    case 14:
                        Options.tkey = keypress;
                        break;
                    case 15:
                        Options.skey = keypress;
                        break;
                    case 16:
                        Options.ukey = keypress;
                        break;
                    case 17:
                        Options.nextunit = keypress;
                        break;
                }
                logger.debug("Key Pressed: " + keypress);
                chooseKey = false;
                Options.saveOptions();
            }else if(keypress == Options.up){
                if(inTitleScreen){
                    String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    SFX.playClip(soundLocation +"/menutick.wav");
                    item--;
                    if(item<0)item=2;
                }else if(inCOselectScreen && !info){
                	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    SFX.playClip(soundLocation + "/menutick.wav");
                    cy--;
                    if(cy<0)cy=0;
                    if(cx != 0 || cy != 0){
                        CO temp = armyArray[selectedArmy][cx+cy*3-1];
                        if(temp != null)infono = COList.getIndex(temp);
                    }
                    skip = 0;
                    glide = 0;
                }else if(info){
                    skip--;
                    if(skip<0)
                        skip = 0;
                }else if(inOptionsScreen){
                    item--;
                    if(item<0)item=18;
                }else if(inBattleOptionsScreen){
                    item--;
                    if(item<0)item=15;
                }else if(inStartANewGameScreen){
                    item--;
                    if(item<0)item=7;
                }else if(sideSelect){
                    item--;
                    if(item<0)item=numArmies-1;
                }else if(inMapSelectScreen){
                	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    item--;
                    SFX.playClip(soundLocation + "/menutick.wav");
                    if(item<0){
                        item=11;
                        mapPage--;
                        if(mapPage<0){
                            mapPage=0;
                            item=0;
                        }
                    }
                    loadPreview();
                }else if(keymap){
                    item--;
                    if(item<0)item=17;
                }else if(snailinfo){
                    item--;
                    if(item<0)item=1;
                }
            }else if(keypress == Options.down){
                if(inTitleScreen){
                	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    SFX.playClip(soundLocation + "/menutick.wav");
                    item++;
                    if(item>2)item=0;
                }else if(inCOselectScreen&&!info){
                	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    SFX.playClip(soundLocation = "/menutick.wav");
                    cy++;
                    if(cy>4)cy=4;
                    CO temp = armyArray[selectedArmy][cx+cy*3-1];
                    if(temp != null)infono = COList.getIndex(temp);
                    skip = 0;
                    glide = 0;
                }else if(info){
                    skip ++;
                    if(skip>skipMax)
                        skip = skipMax;
                }else if(inOptionsScreen){
                    item++;
                    if(item>18)item=0;
                }else if(inBattleOptionsScreen){
                    item++;
                    if(item>15)item=0;
                }else if(inStartANewGameScreen){
                    item++;
                    if(item>7)item=0;
                }else if(sideSelect){
                    item++;
                    if(item>numArmies-1)item=0;
                }else if(inMapSelectScreen){
                	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                    item++;
                    SFX.playClip(soundLocation + "/menutick.wav");
                    
                    if(mapPage*12+item >= numMaps){
                        item--;
                    }
                    
                    if(item>11){
                        item=0;
                        mapPage++;
                        if(mapPage>numMaps/12 || (mapPage==numMaps/12 && numMaps%12 == 0)){
                            mapPage--;
                            SFX.playClip(soundLocation + "/menutick.wav");
                            item=11;
                        }
                    }
                    loadPreview();
                }else if(keymap){
                    item++;
                    if(item>17)item=0;
                }else if(snailinfo){
                    item++;
                    if(item>1)item=0;
                }
            }else if(keypress == Options.altright || (keypress == Options.right && e.isControlDown())){
            	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                if(inMapSelectScreen){
                    subcat++;
                    SFX.playClip(soundLocation + "/minimap.wav");
                    if(subcat > 9)subcat = 0;
                    
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }else if(inCOselectScreen) {
                    SFX.playClip(soundLocation + "/minimap.wav");
                    selectedArmy++;
                    if(selectedArmy > 7)selectedArmy = 7;
                    if(cx+cy*3-1 > 0) {
                        CO temp = armyArray[selectedArmy][cx+cy*3-1];
                        if(temp != null)infono = COList.getIndex(temp);
                    }
                    skip = 0;
                    glide = 0;
                }
            }else if(keypress == Options.altleft || (keypress == Options.left && e.isControlDown())){
            	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                
            	if(inMapSelectScreen){
                    subcat--;
                    SFX.playClip(soundLocation + "/minimap.wav");
                    if(subcat < 0)subcat = 9;
                    
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }else if(inCOselectScreen){
                    SFX.playClip(soundLocation + "/minimap.wav");
                    selectedArmy--;
                    if(selectedArmy < 0)selectedArmy = 0;
                    if(cx+cy*3-1 > 0) {
                        CO temp = armyArray[selectedArmy][cx+cy*3-1];
                        if(temp != null)infono = COList.getIndex(temp);
                    }
                    skip = 0;
                    glide = 0;
                }
            }else if(keypress == Options.left){
            	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                if(inCOselectScreen && !info){
                    SFX.playClip(soundLocation + "/menutick.wav");
                    cx--;
                    if(cx<0){
                        cx=0;
                        if(cy!=0){
                            cx=2;
                            cy--;
                        }
                    }
                    if(cx != 0 || cy != 0){
                        CO temp = armyArray[selectedArmy][cx+cy*3-1];
                        if(temp != null)infono = COList.getIndex(temp);
                    }
                    skip = 0;
                    glide = 0;
                }else if(sideSelect){
                    if(sideSelections[item] == 0)sideSelections[item] = numArmies-1;
                    else sideSelections[item]-=1;
                }else if(inOptionsScreen && item == 6){
                    Options.decrementCursor();
                }else if (inOptionsScreen && item == 11){
                    Options.decrementCO();
                    glide = 0;
                }else if (inOptionsScreen && item == 12){
                    SFX.toggleMute();
                }else if(inOptionsScreen && item == 13){
                    Options.decrementTerrain();
                }else if (inOptionsScreen && item == 14){
                    Options.decrementUrban();
                }else if (inOptionsScreen && item == 15){
                    Options.decrementHQ();
                }else if(inMapSelectScreen){
                
                    cat--;
                    if(cat < 0)cat = cats.length-1;
                    subcat = 0;
                    
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
                    
                    mapDir = new File(mapsLocation + "/" + cats[cat]);
                    loadMapDisplayNames();
                }
                else if(inBattleOptionsScreen)
                {
                    if(item == 0)
                    {
                    	visibility--;
                    	if(visibility < 0)visibility = 2;
                    	
                    	if(visibility == 0)
                    	{
                    		bopt.setFog(false);
                    		bopt.setMist(false);
                    	}
                    	else if(visibility == 1)
                    	{
                    		bopt.setFog(true);
                    		bopt.setMist(false);
                    	}
                    	else
                    	{
                    		bopt.setMist(true);
                    		bopt.setFog(false);
                    	}
                    }
                    else if(item==1)
                    {
                        int wtemp = bopt.getWeatherType();
                        wtemp--;
                        if(wtemp < 0)wtemp = 4;
                        bopt.setWeatherType(wtemp);
                    }else if(item==2){
                        int ftemp = bopt.getFundsLevel();
                        ftemp -= 500;
                        if(ftemp <= 0)ftemp = 500;
                        bopt.setFundsLevel(ftemp);
                    }else if(item==3){
                        int stemp = bopt.getStartFunds();
                        stemp -= 500;
                        if(stemp <= 0)stemp = 0;
                        bopt.setStartFunds(stemp);
                    }else if(item==4){
                        int temp = bopt.getTurnLimit();
                        temp--;
                        if(temp < 0)temp = 0;
                        bopt.setTurnLimit(temp);
                    }else if(item==5){
                        int temp = bopt.getCapLimit();
                        temp--;
                        if(temp < 0)temp = 0;
                        bopt.setCapLimit(temp);
                    }else if(item==6){
                        if(bopt.isCOP())bopt.setCOP(false);
                        else bopt.setCOP(true);
                    }else if(item==7){
                        if(bopt.isBalance())bopt.setBalance(false);
                        else bopt.setBalance(true);
                    }else if(item==8){
                        if(bopt.isRecording())bopt.setReplay(false);
                        else bopt.setReplay(true);
                    }else if(item==9){
                        cx--;
                        if(cx < BaseDMG.NUM_UNITS/2)cy=0;
                        if(cx < 0){
                            cx=BaseDMG.NUM_UNITS-1;
                            cy=1;
                        }
                    }else if(item==10){
                        if(bopt.getSnowChance()>0)
                        bopt.setSnowChance(bopt.getSnowChance()-1);
                    }else if(item==11){
                        if(bopt.getRainChance()>0)
                        bopt.setRainChance(bopt.getRainChance()-1);
                    }else if(item==12){
                        if(bopt.getSandChance()>0)
                        bopt.setSandChance(bopt.getSandChance()-1);
                    }else if(item==13){
                        if(bopt.getMinWTime() > 0)
                        bopt.setMinWTime(bopt.getMinWTime()-1);
                    }else if(item==14){
                        if(bopt.getMaxWTime() > 0)
                        bopt.setMaxWTime(bopt.getMaxWTime()-1);
                    }else if(item==15){
                        if(bopt.getMinWDay()>0)
                        bopt.setMinWDay(bopt.getMinWDay()-1);
                    }
                }else if(snailinfo){
                    item2--;
                    if(item2<0)item2=1;
                }
            }else if(keypress == Options.right){
                String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
            	
                if(inCOselectScreen && !info){
                    SFX.playClip(soundLocation + "/menutick.wav");
                    cx++;
                    if(cx>2){
                        cx=2;
                        if(cy!=4){
                            cx=0;
                            cy++;
                        }
                    }
                    CO temp = armyArray[selectedArmy][cx+cy*3-1];
                    if(temp != null)infono = COList.getIndex(temp);
                    skip = 0;
                    glide = 0;
                }else if(sideSelect){
                    if(sideSelections[item] == numArmies-1)sideSelections[item] = 0;
                    else sideSelections[item]+=1;
                }else if(inOptionsScreen && item == 6){
                    Options.incrementCursor();
                }else if (inOptionsScreen && item == 11){
                    Options.incrementCO();
                    glide = 0;
                }else if (inOptionsScreen && item == 12){
                    SFX.toggleMute();
                }else if(inOptionsScreen && item == 13){
                    Options.incrementTerrain();
                }else if (inOptionsScreen && item == 14){
                    Options.incrementUrban();
                }else if (inOptionsScreen && item == 15){
                    Options.incrementHQ();
                }if(inMapSelectScreen){
                
                    cat++;
                    if(cat > cats.length-1)cat = 0;
                    subcat = 0;
                    
                    item=0;
                    mapPage=0;
                    String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
                    
                    //load maps in new directory
                    mapDir = new File(mapsLocation + "/" + cats[cat]);
                    loadMapDisplayNames();
                    
                }else if(inBattleOptionsScreen){
                    processRightKeyBattleOptions();
                }else if(snailinfo){
                    item2++;
                    if(item2>1)item2=0;
                }
            }else if(keypress == Options.pgdn){
                pressedPGDN();
            }else if(keypress == Options.pgup){
                pressedPGUP();
            }else if(keypress == Options.akey){
                pressedA();
                String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                SFX.playClip(soundLocation + "/ok.wav");
            }else if(keypress == Options.bkey){
                pressedB();
                String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
                SFX.playClip(soundLocation + "/cancel.wav");
            }else if(keypress == KeyEvent.VK_1){
                if(inMapSelectScreen){
                    subcat = 0;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_2){
                if(inMapSelectScreen){
                    subcat = 1;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_3){
                if(inMapSelectScreen){
                    subcat = 2;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_4){
                if(inMapSelectScreen){
                    subcat = 3;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_5){
                if(inMapSelectScreen){
                    subcat = 4;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_6){
                if(inMapSelectScreen){
                    subcat = 5;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_7){
                if(inMapSelectScreen){
                    subcat = 6;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_8){
                if(inMapSelectScreen){
                    subcat = 7;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_9){
                if(inMapSelectScreen){
                    subcat = 8;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == KeyEvent.VK_0){
                if(inMapSelectScreen){
                    subcat = 9;
                    item=0;
                    mapPage=0;
                    
                    //load maps in new directory
                    loadMapDisplayNames();
                }
            }else if(keypress == Options.constmode){
                logger.info("Alternating Costumes");
                if(altcostume)
                    altcostume = false;
                else
                    altcostume = true;
            }else if(keypress == Options.nextunit){
                if(inCOselectScreen){
                    if(info){
                        info = false;
                    }else{
                        if(cx == 0 && cy==0){
                            Random r = new Random();
                            int sel = r.nextInt(COList.getListing().length);
                            CO sc = COList.getListing()[sel];
                            logger.info("Selecting "+sc.getName());
                            selectedArmy = sc.getStyle();
                            for(int i=0;armyArray[selectedArmy][i]!=null;i++){
                                if(armyArray[selectedArmy][i]==sc){
                                    if(i<2){
                                        cx = i+1;
                                        cy = 0;
                                    }else{
                                        cx = (i-2)%3;
                                        cy = (i-2)/3+1;
                                    }
                                }
                            }
                            pressedA();
                        }else{
                            info = true;
                        }
                    }
                }
            }
        }
        
        public void keyReleased(KeyEvent e) {}
    }
    
    class MouseControl implements MouseInputListener{
        public void mouseClicked(MouseEvent e){
            int clickedXCoOrds = e.getX() - parentFrame.getInsets().left;
            int clickedYCoOrds = e.getY() - parentFrame.getInsets().top;
            
            uiMenuLoop: if(e.getButton() == e.BUTTON1){
                //first mouse button


            	if(inTitleScreen){

					boolean startNewGameButtonClicked = clickedXCoOrds > TITLE_startPixels_newGameBtn && clickedXCoOrds < TITLE_endPixels_newGameBtnWidth && clickedYCoOrds > TITLE_btmPixels_newGameBtnHeight && clickedYCoOrds < TITLE_topPixels_newGameBtnHeight;
					boolean mapEditorButtonClicked = clickedXCoOrds > TITLE_startPixels_mapsEditorBtn && clickedXCoOrds < TITLE_endPixels_mapsEditorBtn && clickedYCoOrds > TITLE_btmPixels_mapsEditorBtn && clickedYCoOrds < TITLE_topPixels_mapsEditorBtn;
					boolean optionsButtonClicked = clickedXCoOrds > TITLE_startPixels_optionsButton && clickedXCoOrds < TITLE_endPixels_optionsBtn && clickedYCoOrds > TITLE_btmPixels_optionsBtn && clickedYCoOrds < TITLE_topPixels_optionsBtn;

					if(startNewGameButtonClicked){
                        item = 0;
                        logger.info("Moving into the New Game Menu");
                        pressedA();
                        break uiMenuLoop;
                        
                    } 
					
					if(mapEditorButtonClicked){
						item = 1;
						logger.info("Moving into the Design Maps Area");
						pressedA();
						break uiMenuLoop;
					}

					
					if(optionsButtonClicked){
						item = 2;
						logger.info("Moving into the Options Menu");
						pressedA();
						break uiMenuLoop;
					}
					
                }
            	
            	if(inStartANewGameScreen){
                    if(clickedXCoOrds < 130){
                        int i = clickedYCoOrds/30;
                        if(i < 8){
                            item = i;
                            logger.info("Moving into Load replay from Menu");
                            pressedA();
                            break uiMenuLoop;
                        }
                    }
                
                    
                    
                    
	                if(inOptionsScreen){
	                    if(clickedXCoOrds < 220){
	                        int i = clickedYCoOrds/20;
	                        if((i < 6 || i >6) && i < 11){
	                            item = i;
	                            pressedA();
	                            break uiMenuLoop;
	                        }else if(i == 6){
	                            item = i;
	                            Options.incrementCursor();
	                        }
	                    }
	                }
                
                
                    }else if(inMapSelectScreen){
	                    if(clickedYCoOrds < 30){
	                        if(clickedXCoOrds < 180){
	                            //change category
	                            cat++;
	                            if(cat > cats.length-1)cat = 0;
	                            subcat = 0;
	                            
	                            item=0;
	                            mapPage=0;
	                            
	                            //load maps in new directory
	                            String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
	                            
	                            mapDir = new File(mapsLocation + "/" + cats[cat]);
	                            loadMapDisplayNames();
	                        }
	                    }
	                    if(clickedYCoOrds < 40 && clickedXCoOrds > 180){
	                        //change subcategory
	                        if(clickedXCoOrds < 240){
	                        	subcat = 0;
	                        }else if(clickedXCoOrds < 260){
	                        	subcat = 1;
	                        }
	                        else if(clickedXCoOrds < 280){
	                        	subcat = 2;
	                        }
	                        else if(clickedXCoOrds < 300){
	                        	subcat = 3;
	                        }
	                        else if(clickedXCoOrds < TITLE_endPixels_optionsBtn){
	                        	subcat = 4;
	                        }
	                        else if(clickedXCoOrds < 340){
	                        	subcat = 5;
	                        }
	                        else if(clickedXCoOrds < 360){
	                        	subcat = 6;
	                        }
	                        else if(clickedXCoOrds < 380){
	                        	subcat = 7;
	                        }
	                        else if(clickedXCoOrds < 400){
	                        	subcat = 8;
	                        }
	                        else if(clickedXCoOrds < 480){
	                        	subcat = 9;
	                        }
	                        
	                        item=0;
	                        mapPage=0;
	                        
	                        //load maps in new directory
	                        loadMapDisplayNames();
	                    }else if(clickedYCoOrds > 30 && clickedYCoOrds < 38){
	                        if(clickedXCoOrds > 84 && clickedXCoOrds < 98){
	                        	pressedPGUP();
	                        }
	                    }else if(clickedYCoOrds > 50 && clickedYCoOrds < 302){
	                        if(clickedXCoOrds < 160){
	                            int i = (clickedYCoOrds-50)/21;
	                            if(i < 12 && mapPage*12+i < numMaps){
	                                item = i;
	                                pressedA();
	                                break uiMenuLoop;                                
	                            }
	                        }
	                    }else if(clickedYCoOrds > 312 && clickedYCoOrds < TITLE_endPixels_optionsBtn){
	                        if(clickedXCoOrds > 84 && clickedXCoOrds < 98){
	                        	pressedPGDN();
	                        }
	                    }
	                }

            	if(inCOselectScreen){
                    if(clickedYCoOrds > 61 && clickedYCoOrds < 321 && clickedXCoOrds > 2 && clickedXCoOrds < 158){
                        cx = (clickedXCoOrds-2)/52;
                        cy = (clickedYCoOrds-61)/52;
                        pressedA();
                    }else if(clickedXCoOrds >= 3 && clickedXCoOrds <= 155 && clickedYCoOrds <= 53){
                        selectedArmy = (clickedXCoOrds - 3)/19;
                        if(cx != 0 || cy != 0){
                            CO temp = armyArray[selectedArmy][cx+cy*3-1];
                            if(temp != null)infono = COList.getIndex(temp);;
                        }
                    }
            	}else if(sideSelect){
                    if(clickedXCoOrds < 130){
                        if(clickedYCoOrds/20 < numArmies){
                            item = clickedYCoOrds/20;
                            if(sideSelections[item] == numArmies-1)sideSelections[item] = 0;
                            else sideSelections[item]+=1;
                        }
                    }else{
                        pressedA();
                    }
                
                
                }else if(inBattleOptionsScreen){
                    if(clickedXCoOrds > 10 && clickedXCoOrds < 10+BaseDMG.NUM_UNITS/2*16 && clickedYCoOrds > 184 && clickedYCoOrds < 220){
                        cy = (clickedYCoOrds-184)/20;
                        cx = (clickedXCoOrds-10)/16+cy*BaseDMG.NUM_UNITS/2;
                        item = 9;
                        pressedA();
                    }else if(clickedXCoOrds < 210){
                        if(clickedYCoOrds/20 < 9){
                            item = clickedYCoOrds/20;
                            processRightKeyBattleOptions();
                        }
                    }else{
                        pressedA();
                    }
                }else if(snailinfo){
                    if(clickedXCoOrds > 240 && clickedXCoOrds < 480 && clickedYCoOrds > 280 && clickedYCoOrds < 300){
                        item = 0;
                        pressedA();
                    }else if(clickedXCoOrds > 240 && clickedXCoOrds < 480 && clickedYCoOrds > 300 && clickedYCoOrds < TITLE_endPixels_optionsBtn){
                        item = 1;
                        pressedA();
                    }else if(clickedXCoOrds > 0 && clickedXCoOrds < 160 && clickedYCoOrds > 100 && clickedYCoOrds < 120){
                        item2 = 0;
                    }else if(clickedXCoOrds > 160 && clickedXCoOrds < TITLE_endPixels_optionsBtn && clickedYCoOrds > 100 && clickedYCoOrds < 120){
                        item2 = 1;
                    }else if(clickedXCoOrds > TITLE_endPixels_optionsBtn && clickedXCoOrds < 480 && clickedYCoOrds > 100 && clickedYCoOrds < 120){
                        //send chat message
                        String message = JOptionPane.showInputDialog("Type in your chat message");
                        if(message == null)return;
                        String reply = sendCommandToMain("sendchat",Options.gamename+"\n"+Options.username+"\n"+message);
                        logger.info(reply);
                        refreshInfo();
                    }else if(clickedXCoOrds >460  && clickedXCoOrds <480  && clickedYCoOrds > 0 && clickedYCoOrds < 20){
                        pressedPGUP();
                    }else if(clickedXCoOrds > 460 && clickedXCoOrds < 480 && clickedYCoOrds > 80 && clickedYCoOrds < 100){
                        pressedPGDN();
                    }
                }
            }else{
                //any other button
                pressedB();
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}
        
        public void mouseMoved(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            if(inMapSelectScreen){
                if(y > 50 && y < 302 && x < 160){
                    int i = (y-50)/21;
                    if(i < 12 && mapPage*12+i < numMaps){
                        if(i != item){
                            item = i;
                            loadPreview();
                        }
                    }
                }
            }else if(inCOselectScreen){
                if(y > 61 && y < 321 && x > 2 && x < 158){
                    cx = (x-2)/52;
                    cy = (y-61)/52;
                    if(cx != 0 || cy != 0){
                        CO temp = armyArray[selectedArmy][cx+cy*3-1];
                        
                        if(temp != null && infono!= COList.getIndex(temp)) glide = 0;
                        if(temp != null)infono = COList.getIndex(temp);
                    }
                }
            }
        }
    }
    /////////YES, This is a hack :).
    public void LaunchCreateServerGame()
    {

    	boolean startCOSelect = false;
        logger.info("Create Server Game");
        //try to connect to the server first to see that the user's URL is correct
        if(!tryToConnect())return;
        
        //find an unused name
        Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
        if(Options.gamename == null)return;
        String reply = sendCommandToMain("qname",Options.gamename);
        while(!reply.equals("yes")){
            logger.info(reply);
            if(reply.equals("no")){
                logger.info("Game name already taken");
                JOptionPane.showMessageDialog(this,"Game name already taken");
            }
            Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
            if(Options.gamename == null)return;
            reply = sendCommandToMain("qname",Options.gamename);
        }
        
        //set the master password and join
        Options.masterpass = JOptionPane.showInputDialog("Type in a master password for your game");
        if(Options.masterpass == null)return;
        if(Options.isDefaultLoginOn()){
        	Options.username = Options.getDefaultUsername();
        	Options.password = Options.getDefaultPassword();
        	
        	if(Options.username == null || Options.username.length()<1 || Options.username.length()>12)
        		return;
        }else{
        	while(true){
        		Options.username = JOptionPane.showInputDialog("Type in your username for this game (12 characters max)");
        		if(Options.username == null)return;
        		if(Options.username.length()<1)continue;
        		if(Options.username.length()>12)continue;
        		break;
        	}
        	Options.password = JOptionPane.showInputDialog("Type in your password for this game");
        	if(Options.password == null)return;
        }
        
        //start game
        logger.info("starting game");
        Options.snailGame = true;
        startCOSelect = true;
        item = 0;
        
        if(startCOSelect){
            //New Game
            inStartANewGameScreen = false;
            inMapSelectScreen = true;
            item = 0;
            mapPage = 0;
            
            //load categories
            String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
            File[] dirs = new File(mapsLocation + "/").listFiles();
            Vector<String> v = new Vector();
            int numcats = 0;
            for(int i = 0; i < dirs.length; i++){
                if(dirs[i].isDirectory()){
                    v.add(dirs[i].getName());
                    numcats++;
                }
            }
            if(numcats == 0){
                logger.info("NO MAP DIRECTORIES! QUITTING!");
                System.exit(1);
            }
            cats = new String[numcats];
            for(int i = 0; i < numcats; i++){
                cats[i] = v.get(i);
            }
            
            cat = 0;
            subcat = 0;
            mapDir = new File(mapsLocation + "/" + cats[cat]);
            loadMapDisplayNames();
            mapPage = 0;
        }
        
    
        
        
    }
    public void LaunchCreateServerGame(String username, String password, String gamename, String gamepass)
    {

    	boolean startCOSelect = false;
        logger.info("Create Server Game");
        //try to connect to the server first to see that the user's URL is correct
        if(!tryToConnect())return;
        
        //find an unused name
        
        //Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
        Options.gamename = gamename;
        if(Options.gamename == null)return;
        String reply = sendCommandToMain("qname",Options.gamename);
        while(!reply.equals("yes")){
            logger.info(reply);
            if(reply.equals("no")){
                logger.info("Game name already taken");
                JOptionPane.showMessageDialog(this,"Game name already taken");
            }
            Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
            if(Options.gamename == null)return;
            reply = sendCommandToMain("qname",Options.gamename);
        }
        
        //set the master password and join
        Options.masterpass = gamepass;
        //Options.masterpass = JOptionPane.showInputDialog("Type in a master password for your game");
        if(Options.masterpass == null)return;
        Options.username = username;
        while(true){
            
            if(Options.username == null)
            {
            	Options.username = JOptionPane.showInputDialog("Type in your username for this game (12 characters max)");
            }
            if(Options.username.length()<1)continue;
            if(Options.username.length()>12)continue;
            
            break;
        }
        //Options.password = JOptionPane.showInputDialog("Type in your password for this game");
        Options.password = password;
        if(Options.password == null)return;
        
        //start game
        logger.info("starting game");
        Options.snailGame = true;
        startCOSelect = true;
        item = 0;
        
        if(startCOSelect){
            //New Game
            inStartANewGameScreen = false;
            inMapSelectScreen = true;
            item = 0;
            mapPage = 0;
            
            //load categories
            File[] dirs = new File("maps/").listFiles();
            Vector<String> v = new Vector();
            int numcats = 0;
            for(int i = 0; i < dirs.length; i++){
                if(dirs[i].isDirectory()){
                    v.add(dirs[i].getName());
                    numcats++;
                }
            }
            if(numcats == 0){
                logger.info("NO MAP DIRECTORIES! QUITTING!");
                System.exit(1);
            }
            cats = new String[numcats];
            for(int i = 0; i < numcats; i++){
                cats[i] = v.get(i);
            }
            
            cat = 0;
            subcat = 0;
            String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
            mapDir = new File(mapsLocation + "/" + cats[cat]);
            loadMapDisplayNames();
            mapPage = 0;
        }
        
    
        
        
    }
    public void LaunchLoginGame(String gamename, String username, String password)
    {
        logger.info("Log in to Server Game");
        
        //try to connect to the server first to see that the user's URL is correct
        if(!tryToConnect())return;
        
        //connect to the game
        if(gamename == null)return;
        Options.gamename = gamename;
        
        
        //Get user's name and password
        if(username == null)return;
        if(password == null)return;
        Options.username = username;
        Options.password = password;
        
        //try to connect
        String reply = sendCommandToMain("validup",Options.gamename+"\n"+Options.username+"\n"+Options.password+"\n"+Options.version);
        logger.info(reply);
        if(!reply.equals("login successful")){
            if(reply.equals("version mismatch"))JOptionPane.showMessageDialog(this,"Version Mismatch");
            else JOptionPane.showMessageDialog(this,"Problem logging in, either the username/password is incorrect or the game has ended");
            return;
        }
        
        //go to information screen
        Options.snailGame = true;
        snailinfo = true;
        inStartANewGameScreen = false;
        item = 0;
        item2 = 0;
        
        refreshInfo();
        return;
    }
    
    public void LaunchJoinGame(String gamename, String masterpassword, String username, String password, int slotnumber)
    {
        logger.info("Join Server Game");
        
        //try to connect to the server first to see that the user's URL is correct
        if(!tryToConnect())return;
        
        //connect to the game
        if(gamename == null)return;
        Options.gamename = gamename;
        
        //check the master password and get number of players and available slots
        if(masterpassword == null)return;
        Options.masterpass = masterpassword;

        //Get user's name, password, and slot
        if(username == null)return;
        Options.username = username;
        
        if(password == null)return;
        Options.password = password;

        String slot = Integer.toString(slotnumber);
        //Join
        String reply = sendCommandToMain("join",Options.gamename+"\n"+Options.masterpass+"\n"+Options.username+"\n"+Options.password+"\n"+slot+"\n"+Options.version);
        while(!reply.equals("join successful")){
            logger.info(reply);
            if(reply.equals("no")){
                logger.info("Game does not exist");
                Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
                if(Options.gamename == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
                Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
                if(Options.masterpass == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
            }else if(reply.equals("wrong password")){
                logger.info("Incorrect Password");
                Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
                if(Options.gamename == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
                Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
                if(Options.masterpass == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
            }else if(reply.equals("out of range")){
                logger.info("Army choice out of range or invalid");
                slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
                if(slot == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
            }else if(reply.equals("slot taken")){
                logger.info("Army choice already taken");
                slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
                if(slot == null){
                    inTitleScreen = true;
                    snailinfo = false;
                    return;
                }
            }else{
                logger.info("Other problem");
                JOptionPane.showMessageDialog(this,"Version Mismatch");
                Options.snailGame = false;
                snailinfo = false;
                inTitleScreen = true;
                return;
            }
            refreshInfo();
            reply = sendCommandToMain("join",Options.gamename+"\n"+Options.masterpass+"\n"+Options.username+"\n"+Options.password+"\n"+slot+"\n"+Options.version);
        }
        
        //go to information screen
        Options.snailGame = true;
        snailinfo = true;
        inStartANewGameScreen = false;
        item = 0;
        item2 = 0;
        
        refreshInfo();
        return;
        
    
    }
    public void setNewLoad()
    {
    	inTitleScreen = false;
    	inStartANewGameScreen = true;
    	this.repaint();
    }
    
    private class refreshListener implements ActionListener{
        public void actionPerformed(ActionEvent evt){
            refreshInfo();
        }
    }
}

