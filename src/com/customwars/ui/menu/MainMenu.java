package com.customwars.ui.menu;
/*
 *MainMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 27, 2006, 3:22 PM
 *The Main Menu, used to select what you want to play
 */

import com.customwars.ai.*;
import com.customwars.loader.MapLoader;
import com.customwars.lobbyclient.FobbahLauncher;
import com.customwars.map.Map;
import com.customwars.map.location.Location;
import com.customwars.map.location.Property;
import com.customwars.map.location.TerrType;
import com.customwars.officer.CO;
import com.customwars.officer.COList;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.*;
import com.customwars.util.GuiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

public class MainMenu extends JComponent {
  private static final String TEMPORARYMAP_MAP_FILENAME = "temporarymap.map";
  private static final String TEMPORARYSAVE_SAVE_FILENAME = "temporarysave.save";

  private static String ABSOLUTE_TEMP_FILENAME = "";
  private final static Logger logger = LoggerFactory.getLogger(MainMenu.class);

  // Modes
  private boolean isTitleScreen;
  private boolean isOptionsScreen;
  private boolean isChooseNewGameTypeScreen;
  private boolean isMapSelectScreen;
  private boolean isCOselectScreen;
  private boolean isSideSelect;
  private boolean isBattleOptionsScreen;
  private boolean isKeyMappingScreen;
  private boolean isSnailInfoScreen;

  private static final int MAP_EDITOR = 1;
  private static final int OPTION_MENU = 2;

  // GUI
  private static final int NUM_VISIBLE_ROWS = 12;
  private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
  public boolean altcostume;
  public boolean mainaltcostume;
  private int cx;             //holds the cursor's x position on the co select screen
  private int cy;             //holds the cursor's y position on the co select screen
  private int currentlyHighlightedItem;           //holds the menu's current item (both menus use this)
  private int item2;          //holds the second menu's current item (only used by server info screen)
  private BufferedImage bufferedImg;           //the screen, used for double buffering and scaling
  private int scale;                    //what scale multiplier is being used
  private JFrame parentFrame;           //the frame that contains the window
  private KeyControl keycontroller;     //the KeyControl, used to remove the component
  private MouseControl mousecontroller; //the MouseControl, used to remove the component
  private int[] coSelections;       //the selected COs
  private int[] sideSelections;     //the selected sides
  private boolean[] altSelections;  //alt costumes for sides.
  private String filename;          //the map filename
  private int numArmies;            //the number of armies on the map
  private int numCOs;               //the current number of COs selected
  private int selectedArmy = 0;     //the selected army (0 = OS, 1 = BM, etc.)
  private int ptypes[] = {0, 0, 0, 0, 0, 0};  //the number of properties on this map
  private int mapPage;        //the page in the map list
  private int currentMapCategory = 0;        //the map directory category
  private int currentlySelectedSubCategory = 0;     //the map directory subcategory
  private String[] mapCategories;      //the categories
  private Battle preview;     //the minimap preview
  private boolean chooseKey = false;      //if true, the user is choosing a key
  private boolean insertNewCO = false;    //used to select new COs in snail mode
  private String[] usernames = {"Unknown"};     //the usernames of the players in snail mode
  private int glide = -1; //A simple thing to make stuff prettier.
  private int infono;
  private int skip = 0;
  private int skipMax = 0;
  private boolean isInfoScreen;
  private int backGlide = -1;

  // MODEL
  private BattleOptions bopt = new BattleOptions();  //the battle options to start the game with
  private int day = 1;
  private int turn = 1;

  private MapLoader mapLoader = new MapLoader();
  private List<Map> maps = mapLoader.loadAllValidMaps();
  private List<Map> filteredMaps;                           //the maps to be displayed
  private String[] filenames= mapLoader.getFileNames();     //the filenames of all the maps

  private CO[][] armyArray = new CO[8][14];
  private int visibility = 0; //0=Full 1=Fog 2=Mist

  private String[] syslog = {"System Log"};    //system log messages
  private String[] chatlog = {"Chat Log"};     //chat log messages
  private int syspos = 0;
  private int chatpos = 0;

  // NETWORK
  private static final int MAX_USERNAME_LENGTH = 12;

  /**
   * Creates a new instance of BattleScreen
   */
  public MainMenu(JFrame f) {
    //makes the panel opaque, and thus visible
    this.setOpaque(true);

    String fileSysLocation = ResourceLoader.properties.getProperty("saveLocation");
    ABSOLUTE_TEMP_FILENAME = fileSysLocation + TEMPORARYMAP_MAP_FILENAME;

    cx = 0;
    cy = 0;
    setCurrentlyHighlightedItem(0);
    item2 = 0;
    logger.info("Started through Main menu");

    scale = 1;

    setIsTitleScreen(true);
    setIsOptionsScreen(false);
    setIsNewload(false);
    setIsMapSelectScreen(false);
    setIsCOselectScreen(false);
    setIsInfoScreen(false);
    setIsBattleOptionsScreen(false);
    setIsKeymappingScreen(false);
    Options.snailGame = false;
    setIsSnailInfoScreen(false);

    keycontroller = new KeyControl();
    f.addKeyListener(keycontroller);
    mousecontroller = new MouseControl();
    f.addMouseListener(mousecontroller);
    f.addMouseMotionListener(mousecontroller);
    parentFrame = f;

    for (int i = 0; i < 8; i++) {
      int pos = 0;
      for (int j = 0; j < COList.getListing().length; j++) {
        if (COList.getListing()[j].getStyle() == i) {
          armyArray[i][pos] = COList.getListing()[j];
          pos++;
        }
      }
    }

    if (Options.refresh) {
      refreshListener refresh = new refreshListener();
      Timer timer = new Timer(10000, refresh);
      timer.start();
    }

    filteredMaps = new ArrayList<Map>();
  }

  //called in response to this.repaint();
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = createGraphics2D(getSize().width, getSize().height);
    g2.scale(scale, scale);

    drawScreen(g2);
    g2.dispose();
    g.drawImage(bufferedImg, 0, 0, this);
  }

  public Dimension getPreferredSize() {
    return new Dimension(480 * scale, 320 * scale);
  }

  //makes a Graphics2D object of the given size
  public Graphics2D createGraphics2D(int width, int height) {
    Graphics2D graphics2D;
    boolean imgNotInitialized = bufferedImg == null || bufferedImg.getWidth() != width || bufferedImg.getHeight() != height;
	
    if (imgNotInitialized) {
      bufferedImg = (BufferedImage) createImage(width, height);
    }
    
    graphics2D = bufferedImg.createGraphics();
    graphics2D.setBackground(getBackground());
    graphics2D.clearRect(0, 0, width, height);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    return graphics2D;
  }

  public void drawScreen(Graphics2D graphics2D) {
    drawBackground(graphics2D);
    if (isTitleScreen()) drawTitleScreen(graphics2D, getCurrentlyHighlightedItem());
    if (isMapSelectScreen()) drawMapSelectScreen(graphics2D);
    if (isCOselectScreen()) drawCOSelectScreen(graphics2D);
    if (isOptionsScreen()) drawOptionsScreen(graphics2D);
    if (isChooseNewGameTypeScreen()) drawNewLoadScreen(graphics2D);
    if (isSideSelect()) drawSideSelectScreen(graphics2D);
    if (isBattleOptionsScreen()) drawBattleOptionsScreen(graphics2D);
    if (isKeyMappingScreen()) drawKeymapScreen(graphics2D);
    if (isSnailInfoScreen()) drawServerInfoScreen(graphics2D);

    //causes problems with animated gifs
    this.repaint();
  }

  public void drawBackground(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getBackground(), 0, 0, this);
  }

  public void drawTitleScreen(Graphics2D graphics2D, int highlightedItem) {
    graphics2D.drawImage(MainMenuGraphics.getTitleBackground(), 0, 0, this);

	switch(highlightedItem){
	  case 0:	graphics2D.drawImage(MainMenuGraphics.getNewGame(true), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getMaps(false), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getOptions(false), 0, 0, this);
		    	break;
		    		
	  case 1:	graphics2D.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getMaps(true), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getOptions(false), 0, 0, this);
	    		break;
	    			
	  case 2:	graphics2D.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getMaps(false), 0, 0, this);
	    		graphics2D.drawImage(MainMenuGraphics.getOptions(true), 0, 0, this);
	    		break;
	 }
	    
    graphics2D.setColor(Color.white);
	graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 14));
	final String COPYRIGHT = "\u00a9";
	graphics2D.drawString("Advance Wars is " + COPYRIGHT + " Nintendo/Intelligent Systems", 100, 310);
  }

  public void drawMapSelectScreen(Graphics2D graphic2D) {
    MainMenu eventListener = this;
    
	graphic2D.drawImage(MainMenuGraphics.getMapBG(), MainMenuGraphics.MAPNAME_BG_X, MainMenuGraphics.MAPNAME_BG_Y, eventListener);
    graphic2D.drawImage(MainMenuGraphics.getMapSelectUpArrow(), MainMenuGraphics.MAPSELECT_UPARROW_X, MainMenuGraphics.MAPSELECT_UPARROW_Y, eventListener);
    graphic2D.drawImage(MainMenuGraphics.getMapSelectDownArrow(), MainMenuGraphics.MAPSELECT_DOWNARROW_X, MainMenuGraphics.MAPSELECT_DOWNARROW_Y, eventListener);

    graphic2D.setColor(MainMenuGraphics.getH1Color());
    graphic2D.setFont(MainMenuGraphics.getH1Font());

    graphic2D.drawString(mapCategories[getCurrentMapCategory()], MainMenuGraphics.MAPSELECT_CATEGORY_X, MainMenuGraphics.MAPSELECT_CATEGORY_Y);

    for (int item = 0; item < NUM_VISIBLE_ROWS; item++){
      if (isMapVisible(item)) {
        String fullMapName = getMap(item).getName();
        String fixedMapName = GuiUtil.fitLine(fullMapName,148,graphic2D);
        graphic2D.drawString(fixedMapName, 10, 68 + item * 21);       
      }
    }
    
    graphic2D.setColor(Color.red);
    graphic2D.drawRect(10, 50 + eventListener.getCurrentlyHighlightedItem() * 21, 148, 19);

    if (filteredMaps.size() != 0) {
      graphic2D.setColor(Color.black);
      graphic2D.drawString(getMap(eventListener.getCurrentlyHighlightedItem()).getName(), 180, 60);
      graphic2D.setFont(MainMenuGraphics.getH1Font());
      graphic2D.drawString("Mapmaker: " + getMap(eventListener.getCurrentlyHighlightedItem()).getName(), 180, 245);
      graphic2D.setFont(DEFAULT_FONT);
      graphic2D.drawString(getMap(eventListener.getCurrentlyHighlightedItem()).getDescription(), 180, 265);
    }

    graphic2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
    graphic2D.setColor(new Color(7, 66, 97));
    graphic2D.fillRoundRect(180, 275, 280, 40, 20, 20);
    graphic2D.setColor(Color.WHITE);
    graphic2D.setFont(MainMenuGraphics.getH1Font());
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 205 + 16, 284, 221 + 16, 316, 0, TerrType.getYIndex(TerrType.CITY), 16, TerrType.getYIndex(TerrType.CITY) + 32, eventListener);
    graphic2D.drawString("" + ptypes[0], 205, 300);
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 247 + 16, 284, 263 + 16, 316, 0, TerrType.getYIndex(TerrType.BASE), 16, TerrType.getYIndex(TerrType.BASE) + 32, eventListener);
    graphic2D.drawString("" + ptypes[1], 247, 300);
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 289 + 16, 284, 305 + 16, 316, 0, TerrType.getYIndex(TerrType.PORT), 16, TerrType.getYIndex(TerrType.PORT) + 32, eventListener);
    graphic2D.drawString("" + ptypes[2], 289, 300);
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 331 + 16, 284, 347 + 16, 316, 0, TerrType.getYIndex(TerrType.AIRPORT), 16, TerrType.getYIndex(TerrType.AIRPORT) + 32, eventListener);
    graphic2D.drawString("" + ptypes[3], 331, 300);
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 373 + 16, 284, 389 + 16, 316, 0, TerrType.getYIndex(TerrType.COM_TOWER), 16, TerrType.getYIndex(TerrType.COM_TOWER) + 32, eventListener);
    graphic2D.drawString("" + ptypes[4], 373, 300);
    graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 415 + 16, 284, 431 + 16, 316, 0, TerrType.getYIndex(TerrType.PIPE_STATION), 16, TerrType.getYIndex(TerrType.PIPE_STATION) + 32, eventListener);
    graphic2D.drawString("" + ptypes[5], 415, 300);

    graphic2D.setColor(new Color(7, 66, 97));
    graphic2D.fillRoundRect(180, 5, 280, 40, 20, 20);
    graphic2D.setColor(Color.white);
    graphic2D.setFont(MainMenuGraphics.getH1Font());

	switch(getCurrentlySelectedSubCategory()){
	    case 0:	MainMenuGraphics.drawCategories_allSelected(graphic2D);
	    		break;
	    case 1:	MainMenuGraphics.drawCategories_2playerSelected(graphic2D);
	    		break;
	    case 2:	MainMenuGraphics.drawCategories_3playerSelected(graphic2D);
    			break;
	    case 3:	MainMenuGraphics.drawCategories_4playerSelected(graphic2D);
    			break;
	    case 4:	MainMenuGraphics.drawCategories_5playerSelected(graphic2D);
    			break;
	    case 5:	MainMenuGraphics.drawCategories_6playersSelected(graphic2D);
    			break;
	    case 6:	MainMenuGraphics.drawCategories_7playerSelected(graphic2D);
    			break;
	    case 7:	MainMenuGraphics.drawCategories_8PlayerSelected(graphic2D);
    			break;
	    case 8:	MainMenuGraphics.drawCategories_9playerSelected(graphic2D);
    			break;
	    case 9:	MainMenuGraphics.drawCategories_10playerSelected(graphic2D);
    			break;
    }
    
    drawMiniMap(graphic2D, 180, 65);
  }

  public void drawMiniMap(Graphics2D g, int x, int y) {
    if (filteredMaps.size() != 0) {
      Image minimap = MiscGraphics.getMinimap();
      Map map = preview.getMap();

      for (int i = 0; i < map.getMaxCol(); i++) {
        for (int j = 0; j < map.getMaxRow(); j++) {
          //draw terrain
          int terraintype = map.find(new Location(i, j)).getTerrain().getIndex();
          if (terraintype < 9) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, (terraintype * 4), 0, 4 + (terraintype * 4), 4, this);
          } else if (terraintype == 9) {
            int armycolor = ((Property) map.find(new Location(i, j)).getTerrain()).getOwner().getColor();
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 36 + (armycolor * 4), 0, 40 + (armycolor * 4), 4, this);
          } else if (terraintype < 15 || terraintype == 17) {
            int armycolor = ((Property) map.find(new Location(i, j)).getTerrain()).getColor();
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 76 + (armycolor * 4), 0, 80 + (armycolor * 4), 4, this);
          } else if (terraintype == 15) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 120, 0, 124, 4, this);
          } else if (terraintype == 16) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 128, 0, 132, 4, this);
          } else if (terraintype == 18) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 124, 0, 128, 4, this);
          } else if (terraintype == 19) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 0, 0, 4, 4, this);
          }

          //draw units
          if (map.find(new Location(i, j)).hasUnit()) {
            int armycolor = map.find(new Location(i, j)).getUnit().getArmy().getColor();
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 132 + (armycolor * 4), 0, 136 + (armycolor * 4), 4, this);
          }
        }
      }
    } else {
      g.drawImage(MainMenuGraphics.getNowDrawing(), x, y, this);
    }
  }

  public void drawCOSelectScreen(Graphics2D g) {
    backGlide++;
    if (backGlide > 640 * 2) {
      backGlide = 0;
    }
    g.drawImage(MiscGraphics.getIntelBackground(), 0, -backGlide / 2, this);
    g.drawImage(MiscGraphics.getIntelBackground(), 0, 640 - backGlide / 2, this);

    int offset = 0;
    if (altcostume) offset = 225;
    int offset2 = 0;
    if (mainaltcostume) offset2 = 225;

    //Layout
    g.drawImage(MainMenuGraphics.getCOLayout(selectedArmy), 0, 52, this);
    g.drawImage(MainMenuGraphics.getCOBanner(), 0, 1, this);
    for (int i = 0; i < 8; i++) {
      if (i == selectedArmy) g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, 0, this);
      else g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, -12, this);
    }
    g.drawImage(MainMenuGraphics.getHQBG(), 2, 61, 2 + 156, 61 + 279, 244 * selectedArmy, 0, 244 * selectedArmy + 244, 279, this);

    //Draw CO framework
    for (int j = 0; j < 5; j++) {
      for (int i = 0; i < 3; i++) {
        g.drawImage(MainMenuGraphics.getCOSlot(selectedArmy), 2 + i * 52, 61 + j * 52, this);
      }
    }
    g.drawImage(MainMenuGraphics.getNoCO(), 2, 61, this);

    //Draw COs
    for (int i = 1; i < 15; i++) {
      CO current = armyArray[selectedArmy][i - 1];
      if (current != null) {

        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 2 + i % 3 * 52, 61 + i / 3 * 52, 2 + i % 3 * 52 + 48, 61 + i / 3 * 52 + 48, offset, 350, 48 + offset, 398, this);
      } else {
        break;
      }
    }

    //Draw Cursor
    if (numCOs % 2 == 0) g.setColor(Color.RED);
    else g.setColor(Color.BLUE);
    g.drawRect(2 + cx * 52, 61 + cy * 52, 48, 48);

    //Draw first CO if selecting second CO
    if (numCOs % 2 == 1) {
      g.drawImage(MiscGraphics.getCOSheet(coSelections[numCOs - 1]), 166, 210, 166 + 32, 210 + 12, 144 + offset2, 350, 144 + offset2 + 32, 350 + 12, this);
      g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, (coSelections[numCOs - 1]) * 15, 50, (coSelections[numCOs - 1]) * 15 + 15, this);
    }

    //Draw current CO Info
    CO current = null;

    if (cx + cy * 3 - 1 > -1) current = armyArray[selectedArmy][cx + cy * 3 - 1];

    if (current != null) {
      glide++;
      g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 339 + (int) (100 * Math.pow(0.89, glide)), 44, 339 + 225 + (int) (100 * Math.pow(0.89, glide)), 44 + 350, offset, 0, offset + 225, 350, this);
      g.drawImage(MainMenuGraphics.getCOName(), 170, 70, 170 + 50, 70 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
      if (numCOs % 2 == 1) {
        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 166, 226, 166 + 32, 226 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, this);
        g.drawImage(MainMenuGraphics.getCOName(), 199, 226, 199 + 50, 226 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
      } else {
        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 166, 210, 166 + 32, 210 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, this);
        g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
      }
    }

    if (numCOs / 2 < 9)
      g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy), 293, 195, 293 + 15, 195 + 10, numCOs / 2 * 15, 0, numCOs / 2 * 15 + 15, 10, this);
    else
      g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy), 293, 195, 293 + 30, 195 + 10, numCOs / 2 * 15, 0, numCOs / 2 * 15 + 30, 10, this);

    g.setColor(Color.black);
    g.setFont(DEFAULT_FONT);

    if (current != null) {
      int k;

      for (k = 0; k < ((COList.getListing()[COList.getIndex(current)].getIntel().length() / 36) + 1); k++)
      {//As long as k is shorter than the length, in characters, of the bio divided by 40, incremented by one.
        if (COList.getListing()[COList.getIndex(current)].getIntel().length() - (k + 1) * 36 >= 0) { //Is there more than 40 characters left in the bio?
          //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
          //Draw the substring - 40 characters from the last area.
          g.drawString(COList.getListing()[COList.getIndex(current)].getIntel().substring(k * 36, (k + 1) * 36), 170, 98 + k * 15);

        } else //If there is less than 40 characters left...
        {
          //Avoiding info space.
          //Drawing the rest of the substring.
          g.drawString(COList.getListing()[COList.getIndex(current)].getIntel().substring(k * 36), 170, 98 + k * 15);
        }
      }

      g.setColor(Color.black);
      g.setFont(new Font("SansSerif", Font.PLAIN, 10));
      g.drawString(COList.getListing()[COList.getIndex(current)].getTitle(), 220, 80);

    }
  }

  public void drawOptionsScreen(Graphics2D g) {
    g.setColor(Color.black);
    g.setFont(MainMenuGraphics.getH1Font());
    if (getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
    g.drawString("Music", 10, 20);
    g.setColor(Color.black);
    if (Options.isMusicOn())
      g.drawString("On", 80, 20);
    else
      g.drawString("Off", 80, 20);

    if (getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
    g.drawString("Random Numbers", 10, 40);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
    g.drawString("Balance Mode", 10, 60);
    g.setColor(Color.black);
    if (Options.isBalance())
      g.drawString("On", 120, 60);
    else
      g.drawString("Off", 120, 60);

    if (getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
    g.drawString("Set IP", 10, 80);
    g.setColor(Color.black);
    g.drawString(Options.getDisplayIP(), 60, 80);

    if (getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
    g.drawString("Autosave", 10, 100);
    g.setColor(Color.black);
    if (Options.isAutosaveOn())
      g.drawString("On", 120, 100);
    else
      g.drawString("Off", 120, 100);

    if (getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
    g.drawString("Record Replay", 10, 120);
    g.setColor(Color.black);
    if (Options.isRecording())
      g.drawString("On", 130, 120);
    else
      g.drawString("Off", 130, 120);

    if (getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
    g.drawString("Cursor", 10, 140);
    g.setColor(Color.black);
    g.drawImage(MiscGraphics.getCursor(), 70, 120, this);

    if (getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
    g.drawString("Remap Keys", 10, 160);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
    String bbi = "On";
    if (!Options.battleBackground) bbi = "Off";
    g.drawString("Battle Background Image " + bbi, 10, 180);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 9) g.setColor(Color.red);
    g.drawString("Snail Mode Server: " + Options.getServerName(), 10, 200);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
    String bans = "";
    if (Options.getDefaultBans() == 0) {
      bans = "CW";
    } else if (Options.getDefaultBans() == 1) {
      bans = "AWDS";
    } else if (Options.getDefaultBans() == 2) {
      bans = "AW2";
    } else if (Options.getDefaultBans() == 3) {
      bans = "AW1";
    } else if (Options.getDefaultBans() == 4) {
      bans = "No Bans";
    } else if (Options.getDefaultBans() == 5) {
      bans = "All Bans";
    }
    g.drawString("Default Bans: " + bans, 10, 220);
    g.setColor(Color.black);
    if (getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
    g.drawString("Main Screen CO: " + COList.getListing()[Options.getMainCOID()].getName(), 10, 240);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
    g.drawString("Sound Effects: ", 10, 260);
    if (SFX.getMute())
      g.drawString("Off", 130, 260);
    else
      g.drawString("On", 130, 260);
    g.setColor(Color.black);
    //Shows current terrain tileset
    if (getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
    g.drawString("Terrain Tileset: ", 10, 280);
    if (Options.getSelectedTerrain() == 0)
      g.drawString("CW", 220, 280);
    else if (Options.getSelectedTerrain() == 1)
      g.drawString("AWDS", 220, 280);
    else if (Options.getSelectedTerrain() == 2)
      g.drawString(Options.getCustomTerrainString(), 220, 280);
    g.setColor(Color.black);

    //Shows current Urban tileset
    if (getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
    g.drawString("Urban Tileset: ", 10, 300);
    if (Options.getSelectedUrban() == 0)
      g.drawString("CW", 220, 300);
    else if (Options.getSelectedUrban() == 1)
      g.drawString("AWDS", 220, 300);
    else if (Options.getSelectedUrban() == 2)
      g.drawString(Options.getCustomUrbanString(), 220, 300);
    g.setColor(Color.black);

    //Shows current HQ tileset
    //Shows current Urban tileset
    if (getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
    g.drawString("HQ Tileset: ", 10, 320);
    if (Options.getSelectedHQ() == 0)
      g.drawString("CW", 220, 320);
    else if (Options.getSelectedHQ() == 1)
      g.drawString("AWDS", 220, 320);
    else if (Options.getSelectedHQ() == 2)
      g.drawString(Options.getCustomHQString(), 220, 320);
    g.setColor(Color.black);
    if (getCurrentlyHighlightedItem() == 16) g.setColor(Color.red);
    g.drawString("Use Default Login Info: ", 220, 20);
    if (Options.isDefaultLoginOn())
      g.drawString("On", 400, 20);
    else
      g.drawString("Off", 400, 20);
    g.setColor(Color.black);
    if (getCurrentlyHighlightedItem() == 17) g.setColor(Color.red);
    g.drawString("Default Username/Password:", 220, 40);
    g.drawString(Options.getDefaultUsername() + " / " + Options.getDefaultPassword(), 220, 60);
    g.setColor(Color.black);
    if (getCurrentlyHighlightedItem() == 18) g.setColor(Color.red);
    g.drawString("AutoRefresh:", 220, 80);
    if (Options.getRefresh())
      g.drawString("On", 350, 80);
    else
      g.drawString("Off", 350, 80);
    g.setColor(Color.black);

  }

  public void drawBattleOptionsScreen(Graphics2D g) {
    int textCol = 20;

    //Visibility
    g.setColor(Color.black);
    g.setFont(MainMenuGraphics.getH1Font());
    if (getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
    g.drawString("Visibility", 10, textCol);
    g.setColor(Color.black);

    if (visibility == 0)
      g.drawString("Full", 120, textCol);
    else if (visibility == 1)
      g.drawString("Fog of War", 120, textCol);
    else
      g.drawString("Mist of War", 120, textCol);

    textCol += 20;

    //Weather
    if (getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
    g.drawString("Weather", 10, textCol);
    g.setColor(Color.black);
    if (bopt.getWeatherType() == 0)
      g.drawString("Clear", 120, textCol);
    else if (bopt.getWeatherType() == 1)
      g.drawString("Rain", 120, textCol);
    else if (bopt.getWeatherType() == 2)
      g.drawString("Snow", 120, textCol);
    else if (bopt.getWeatherType() == 3)
      g.drawString("Sandstorm", 120, textCol);
    else if (bopt.getWeatherType() == 4)
      g.drawString("Random", 120, textCol);

    textCol += 20;

    //Funds
    if (getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
    g.drawString("Funds", 10, textCol);
    g.setColor(Color.black);
    g.drawString(bopt.getFundsLevel() + "", 120, textCol);

    textCol += 20;

    //Starting Funds
    if (getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
    g.drawString("Start Funds", 10, textCol);
    g.setColor(Color.black);
    g.drawString(bopt.getStartFunds() + "", 120, textCol);

    textCol += 20;

    //Turn Limit
    if (getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
    g.drawString("Turn Limit", 10, textCol);
    g.setColor(Color.black);
    if (bopt.getTurnLimit() > 0)
      g.drawString(bopt.getTurnLimit() + "", 120, textCol);
    else
      g.drawString("Off", 120, textCol);

    textCol += 20;

    //Cap Limit
    if (getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
    g.drawString("Capture Limit", 10, textCol);
    g.setColor(Color.black);
    if (bopt.getCapLimit() > 0)
      g.drawString(bopt.getCapLimit() + "", 120, textCol);
    else
      g.drawString("Off", 120, textCol);

    textCol += 20;

    //CO Powers
    if (getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
    g.drawString("CO Powers", 10, textCol);
    g.setColor(Color.black);
    if (bopt.isCOP())
      g.drawString("On", 120, textCol);
    else
      g.drawString("Off", 120, textCol);

    textCol += 20;

    //Balance Mode
    if (getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
    g.drawString("Balance Mode", 10, textCol);
    g.setColor(Color.black);
    if (bopt.isBalance())
      g.drawString("On", 120, textCol);
    else
      g.drawString("Off", 120, textCol);

    textCol += 20;

    //Record Replay?
    if (getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
    g.drawString("Record Replay", 10, textCol);
    g.setColor(Color.black);
    if (bopt.isRecording())
      g.drawString("On", 130, textCol);
    else
      g.drawString("Off", 130, textCol);

    textCol += 20;

    //Unit Bans
    g.setColor(Color.black);
    Image isheet = UnitGraphics.getUnitImage(0, 0);
    Image usheet = UnitGraphics.getUnitImage(2, 0);
    for (int i = 0; i < 2; i++) {
      g.drawImage(isheet, 10 + i * 16, textCol - 16, 26 + i * 16, textCol, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, this);
      if (bopt.isUnitBanned(i)) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.fillRect(10 + i * 16, textCol - 16, 16, 16);
        g.setComposite(AlphaComposite.SrcOver);
      }
    }
    for (int i = 2; i < BaseDMG.NUM_UNITS; i++) {
      if (i < BaseDMG.NUM_UNITS / 2) {
        g.drawImage(usheet, 10 + i * 16, textCol - 16, 26 + i * 16, textCol, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, this);
        if (bopt.isUnitBanned(i)) {
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
          g.fillRect(10 + i * 16, textCol - 16, 16, 16);
          g.setComposite(AlphaComposite.SrcOver);
        }
      } else {
        g.drawImage(usheet, 10 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 4, 26 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 20, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, this);
        if (bopt.isUnitBanned(i)) {
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
          g.fillRect(10 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 4, 16, 16);
          g.setComposite(AlphaComposite.SrcOver);
        }
      }
    }
    if (getCurrentlyHighlightedItem() == 9) {
      g.setColor(Color.red);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
      g.fillRect(10 + (cx % (BaseDMG.NUM_UNITS / 2)) * 16, textCol - 16 + 20 * cy, 16, 16);
      g.setComposite(AlphaComposite.SrcOver);
      g.setColor(Color.black);
    }
    textCol += 40;

    if (getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
    g.drawString("Snow Chance", 10, textCol);
    g.setColor(Color.black);
    g.drawString(String.valueOf(bopt.getSnowChance()) + "%", 165, textCol);
    textCol += 20;

    if (getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
    g.drawString("Rain Chance", 10, textCol);
    g.setColor(Color.black);
    g.drawString(String.valueOf(bopt.getRainChance()) + "%", 165, textCol);
    textCol += 20;

    if (getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
    g.drawString("Sandstorm Chance", 10, textCol);
    g.setColor(Color.black);
    g.drawString(String.valueOf(bopt.getSandChance()) + "%", 165, textCol);
    textCol -= 40;

    if (getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
    g.drawString("Min Weather Duration", 210, textCol);
    g.setColor(Color.black);
    g.drawString(String.valueOf(bopt.getMinWTime()) + " Days", 380, textCol);
    textCol += 20;

    if (getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
    g.drawString("Max Weather Duration", 210, textCol);
    g.setColor(Color.black);
    g.drawString(String.valueOf(bopt.getMaxWTime()) + " Days", 380, textCol);
    textCol += 20;

    if (getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
    g.drawString("Weather Start", 210, textCol);
    g.setColor(Color.black);
    g.drawString("Day: " + String.valueOf(bopt.getMinWDay()), 380, textCol);
    textCol += 20;
  }

  public void drawKeymapScreen(Graphics2D g) {
    g.setColor(Color.black);
    g.setFont(new Font("SansSerif", Font.BOLD, 12));

    if (getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);

    g.drawString("Up-" + KeyEvent.getKeyText(Options.up), 10, 14);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
    g.drawString("Down-" + KeyEvent.getKeyText(Options.down), 10, 28);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
    g.drawString("Left-" + KeyEvent.getKeyText(Options.left), 10, 42);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
    g.drawString("Right-" + KeyEvent.getKeyText(Options.right), 10, 56);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
    g.drawString("A Button-" + KeyEvent.getKeyText(Options.akey), 10, 70);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
    g.drawString("B Button-" + KeyEvent.getKeyText(Options.bkey), 10, 84);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
    g.drawString("Page Up-" + KeyEvent.getKeyText(Options.pgup), 10, 98);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
    g.drawString("Page Down-" + KeyEvent.getKeyText(Options.pgdn), 10, 112);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
    g.drawString("<-" + KeyEvent.getKeyText(Options.altleft), 10, 126);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 9) g.setColor(Color.red);
    g.drawString(">-" + KeyEvent.getKeyText(Options.altright), 10, 140);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
    g.drawString("Menu-" + KeyEvent.getKeyText(Options.menu), 10, 154);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
    g.drawString("Minimap-" + KeyEvent.getKeyText(Options.minimap), 10, 168);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
    g.drawString("Constant Mode-" + KeyEvent.getKeyText(Options.constmode), 10, 182);
    g.setColor(Color.black);

    //SECOND ROW

    if (getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
    g.drawString("Delete Unit-" + KeyEvent.getKeyText(Options.delete), 130, 14);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
    g.drawString("Terrain Menu-" + KeyEvent.getKeyText(Options.tkey), 130, 28);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
    g.drawString("Side Menu-" + KeyEvent.getKeyText(Options.skey), 130, 42);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 16) g.setColor(Color.red);
    g.drawString("Unit Menu-" + KeyEvent.getKeyText(Options.ukey), 130, 56);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 17) g.setColor(Color.red);
    g.drawString("Next Unit-" + KeyEvent.getKeyText(Options.nextunit), 130, 70);
    g.setColor(Color.black);

    g.drawString("Usage: No mouse", 130, 140);
    g.drawString("1. Select the key", 130, 152);
    g.drawString("2. Press the A button", 130, 164);
    g.drawString("3. Press the new key", 130, 176);
  }

  public void drawNewLoadScreen(Graphics2D g) {
    int offset = 0;
    g.setColor(Color.black);
    g.setFont(new Font("SansSerif", Font.BOLD, 24));
    if (getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
    g.drawString("New", 15, 30);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
    g.drawString("Load", 15, 54);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
    g.drawString("Network Game", 15, 78);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
    g.drawString("Load Replay", 15, 102);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
    g.drawString("Create New Server Game", 15, 126);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
    g.drawString("Join Server Game", 15, 150);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
    g.drawString("Login to Server Game", 15, 174);
    g.setColor(Color.black);

    if (getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
    g.drawString("Open Online Lobby", 15, 198);
    g.setColor(Color.black);
    //Draw CO at the main menu
    //Draw COs
    glide++;
    g.drawImage(MainMenuGraphics.getMainMenuCO(Options.getMainCOID()), 329 + (int) (100 * Math.pow(.95, glide)), -5, 329 + 225 + (int) (100 * Math.pow(.95, glide)), -5 + 350, offset, 0, offset + 225, 350, this);

    //draw description box
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
    g.setColor(new Color(7, 66, 97));
    g.fillRoundRect(180, 255, 280, 60, 20, 20);
    g.setComposite(AlphaComposite.SrcOver);
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 11));

    if (getCurrentlyHighlightedItem() == 0) {
      g.drawString("Start a new game. This mode is primarily for", 190, 275);
      g.drawString("playing against a friend on the same computer.", 190, 290);
    } else if (getCurrentlyHighlightedItem() == 1) {
      g.drawString("Continue where you started off from your", 190, 275);
      g.drawString("previous game.", 190, 290);
    } else if (getCurrentlyHighlightedItem() == 2) {
      g.drawString("Connect via a friend's IP and enjoy an online ", 190, 275);
      g.drawString("hotseat game with him or her! Hamachi is ", 190, 290);
      g.drawString("suggested for the best connectivity results.", 190, 304);
    } else if (getCurrentlyHighlightedItem() == 3) {
      g.drawString("Already finished a game and feel like reliving ", 190, 275);
      g.drawString("those moments of honour? ", 190, 290);
      g.drawString("Load the replay here!", 190, 304);
    } else if (getCurrentlyHighlightedItem() == 4) {
      g.drawString("Start a new game on the CW server! If you ", 190, 275);
      g.drawString("don't have a friend to battle, you should make", 190, 290);
      g.drawString("an open game so anyone can join and play!", 190, 304);
    } else if (getCurrentlyHighlightedItem() == 5) {
      g.drawString("Join a game on the CW server that's open!", 190, 275);
      g.drawString("All you need is the game name, a handle and", 190, 290);
      g.drawString("a password! Then you're all ready to play!", 190, 304);
    } else if (getCurrentlyHighlightedItem() == 6) {
      g.drawString("Login to one of your current games!", 190, 275);
      g.drawString("Let's hope you're winning, at least.", 190, 290);
      g.drawString("Otherwise, what's the point to logging in?", 190, 304);
    }

  }

  public void drawSideSelectScreen(Graphics2D g) {
    g.setColor(Color.black);
    g.setFont(MainMenuGraphics.getH1Font());

    for (int i = 0; i < numArmies; i++) {
      if (getCurrentlyHighlightedItem() == i) g.setColor(Color.red);
      g.drawString("Army " + (i + 1), 10, 20 + 20 * i);
      g.drawString("Side " + sideSelections[i], 70, 20 + 20 * i);
      g.setColor(Color.black);
    }
  }

  public void drawServerInfoScreen(Graphics2D g) {
    g.setFont(MainMenuGraphics.getH1Font());
    //chat screen
    g.setColor(Color.black);
    g.fillRect(0, 0, 480, 100);
    g.setColor(Color.DARK_GRAY);
    g.fillRect(460, 0, 20, 100);
    g.setColor(Color.WHITE);
    g.fillRect(460, 0, 20, 20);
    g.fillRect(460, 80, 20, 20);
    g.setColor(Color.gray);
    if (item2 == 0) g.setColor(Color.DARK_GRAY);
    g.fillRect(0, 100, 160, 20);
    g.setColor(Color.white);
    g.drawString("Syslog", 0, 120);
    g.setColor(Color.gray);
    if (item2 == 1) g.setColor(Color.DARK_GRAY);
    g.fillRect(160, 100, 160, 20);
    g.setColor(Color.white);
    g.drawString("Chat", 160, 120);
    g.setColor(Color.gray);
    g.fillRect(320, 100, 160, 20);
    g.setColor(Color.white);
    g.drawString("Send", 320, 120);
    //chat messages
    g.setColor(Color.white);
    for (int i = 0; i < 5; i++) {
      if (item2 == 0) {
        if (i + syspos < syslog.length && i + syspos >= 0) g.drawString(syslog[i + syspos], 0, 18 + i * 20);
      } else {
        if (i + chatpos < chatlog.length && i + chatpos >= 0) g.drawString(chatlog[i + chatpos], 0, 18 + i * 20);
      }
    }

    //information
    g.setColor(Color.BLACK);
    g.drawString("Game Name: " + Options.gamename, 0, 140);
    g.drawString("Login Name: " + Options.username, 0, 160);
    g.drawString("Current day/turn: " + day + "/" + turn, 0, 180);
    for (int i = 0; i < usernames.length; i++) {
      g.drawString(usernames[i], (i < 5) ? 0 : 120, 200 + (i % 5) * 20);
    }

    //actions
    g.setColor(Color.gray);
    if (getCurrentlyHighlightedItem() == 0) g.setColor(Color.black);
    g.fillRect(240, 280, 240, 20);
    g.setColor(Color.white);
    g.drawString("Refresh", 340, 300);
    g.setColor(Color.gray);
    if (getCurrentlyHighlightedItem() == 1) g.setColor(Color.black);
    g.fillRect(240, 300, 240, 20);
    g.setColor(Color.white);
    g.drawString("Play", 350, 320);
  }

  public void removeFromFrame() {
    parentFrame.getContentPane().remove(this);
    parentFrame.removeKeyListener(keycontroller);
    parentFrame.removeMouseListener(mousecontroller);
    parentFrame.removeMouseMotionListener(mousecontroller);
  }

  public void loadMiniMapPreview() {
    ptypes[0] = 0;
    ptypes[1] = 0;
    ptypes[2] = 0;
    ptypes[3] = 0;
    ptypes[4] = 0;
    ptypes[5] = 0;
    if (filteredMaps.size() != 0) {
      filename = getFileName(getCurrentlyHighlightedItem());
      preview = new Battle(filename);

      Map m = preview.getMap();
      for (int yy = 0; yy < m.getMaxRow(); yy++) {
        for (int xx = 0; xx < m.getMaxCol(); xx++) {
          int number = m.find(new Location(xx, yy)).getTerrain().getIndex();
          if (number > 9) {
            if (number == 10) ptypes[0]++;
            else if (number == 11) ptypes[1]++;
            else if (number == 13) ptypes[2]++;
            else if (number == 12) ptypes[3]++;
            else if (number == 14) ptypes[4]++;
            else if (number == 17) ptypes[5]++;
          }
        }
      }
    }
  }

  //loads the display names, filter on subCat, aka the playerCount
  private void loadMapDisplayNames() {
    if (maps == null) logger.warn("No maps loaded");
    filteredMaps.clear();

    for (Map map : maps) {
      if (getCurrentlySelectedSubCategory() == 0 || getCurrentlySelectedSubCategory() == map.getPlayerCount() - 1) {
        filteredMaps.add(map);
      }
    }
    loadMiniMapPreview();
  }

  public void pressedA() {
    if (isTitleScreen()) {
      titleScreenActions();
    } else if (isInfoScreen()) {
      isInfoScreen = false;
    } else if (isCOselectScreen() && !isInfoScreen()) {
      coSelectScreenActions();
    } else if (isOptionsScreen()) {
      optionsScreenActions();
    } else if (isChooseNewGameTypeScreen()) {
      parseSelectedNewGameTypeInput();
    } else if (isSideSelect()) {
      setSideSelect(false);
      setIsBattleOptionsScreen(true);
      setCurrentlyHighlightedItem(0);
    } else if (isBattleOptionsScreen()) {
      startBattle();
    } else if (isMapSelectScreen()) {
      mapSelectScreenActions();
    } else if (isKeyMappingScreen()) {
      chooseKey = true;
    } else if (isSnailInfoScreen()) {
      networkInfoScreenActions();
    }
  }

  private void startBattle() {
    if (getCurrentlyHighlightedItem() == 9) {
      if (bopt.isUnitBanned(cx)) {
        bopt.setUnitBanned(false, cx);
      } else {
        bopt.setUnitBanned(true, cx);
      }
      return;
    }
    //Creates new instances of critical objects for the battle
    Battle b = new Battle(filename, coSelections, sideSelections, altSelections, bopt);

    //Initialize a swing frame and put a BattleScreen inside
    parentFrame.setSize(400, 400);
    removeFromFrame();
    BattleScreen bs = new BattleScreen(b, parentFrame);
    parentFrame.getContentPane().add(bs);
    parentFrame.validate();
    parentFrame.pack();

    //Start the mission
    Mission.startMission(b, bs);
    //save the initial state for the replay if applicable
    if (bopt.isRecording()) Mission.saveInitialState();
  }

  private void networkInfoScreenActions() {
    if (getCurrentlyHighlightedItem() == 0) {
      //refresh
      logger.info("Refreshing");
      refreshInfo();
    } else if (getCurrentlyHighlightedItem() == 1) {
      //play game
      logger.info("Play Game");
      //refresh first
      refreshInfo();

      String reply = sendCommandToMain("canplay", Options.gamename + "\n" + Options.username + "\n" + Options.password);
      logger.info(reply);
      if (reply.equals("permission granted")) {
        if (day == 1) {
          logger.info("Starting Game");
          //load map from server
          getFile("dmap.pl", Options.gamename, TEMPORARYMAP_MAP_FILENAME);
          filename = TEMPORARYMAP_MAP_FILENAME;

          //goto co select
          setIsMapSelectScreen(false);
          setIsCOselectScreen(true);
          setIsSnailInfoScreen(false);

          //find number of armies, and thus COs
          try {
            DataInputStream read = new DataInputStream(new FileInputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" + filename));
            int maptype = read.readInt();
            if (maptype == -1) {
              read.readByte(); //skip name
              read.readByte(); //skip author
              read.readByte(); //skip description
              read.readInt();
              read.readInt();
              numArmies = read.readByte();
            } else {
              read.readInt();
              numArmies = read.readInt();
            }
          } catch (IOException exc) {
            logger.error("Couldn't read MAP file [" + filename + "]", exc);
          }
          numCOs = 0;
          coSelections = new int[numArmies * 2];
          altSelections = new boolean[numArmies * 2];
          if (turn != 1) {
            insertNewCO = true;
          }
          return;
        }

        //load mission
        getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
        String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
        //load mission
        Battle b = new Battle(new Map(30, 20));
        //Initialize a swing frame and put a BattleScreen inside
        parentFrame.setSize(400, 400);
        removeFromFrame();
        BattleScreen bs = new BattleScreen(b, parentFrame);
        parentFrame.getContentPane().add(bs);
        parentFrame.validate();
        parentFrame.pack();

        //Start the mission
        Mission.startMission(null, bs);
        Mission.loadMission(loadFilename);
      }
    }
  }

  private void mapSelectScreenActions() {
    if (filteredMaps.size() != 0) {
      //New Game
    	setIsMapSelectScreen(false);
    	setIsCOselectScreen(true);
      filename = getFileName(getCurrentlyHighlightedItem());
      String mapName = getMap(getCurrentlyHighlightedItem()).getName() ;
      this.numArmies = getMap(getCurrentlyHighlightedItem()).getPlayerCount();

      //New Snail Mode Game
      if (Options.snailGame) {
    	setIsCOselectScreen(false);
    	setIsSnailInfoScreen(true);
        setCurrentlyHighlightedItem(0);
        item2 = 0;

        String comment = JOptionPane.showInputDialog("Type in a comment for your game");

        //get army that the player wants to join
        int joinnum = -1;
        while (joinnum < 1 || joinnum > numArmies) {
          String t = JOptionPane.showInputDialog("What side do you want to join? Pick from 1-" + numArmies);
          if (t == null) {
            Options.snailGame = false;
            setIsMapSelectScreen(false);
            setIsTitleScreen(true);
            return;
          }
          joinnum = Integer.parseInt(t);
        }

        //register new game
        String reply = sendCommandToMain("newgame", Options.gamename + "\n" + Options.masterpass + "\n" + numArmies + "\n" + Options.version + "\n" + comment + "\n" + mapName + "\n" + Options.username);
        while (!reply.equals("game created")) {
          logger.info(reply);
          if (reply.equals("no")) {
            logger.info("Game name taken");
            JOptionPane.showMessageDialog(this, "Game name taken");
            Options.gamename = JOptionPane.showInputDialog("Type in a new name for your game");
            if (Options.gamename == null) return;
            reply = sendCommandToMain("newgame", Options.gamename + "\n" + Options.masterpass + "\n" + numArmies + "\n" + Options.version + "\n" + comment + "\n" + mapName + "\n" + Options.username);
          } else {
            Options.snailGame = false;
            setIsMapSelectScreen(false);
            setIsTitleScreen(true);
            return;
          }
        }

        //upload map
        String temp = sendFile("umap.pl", Options.gamename, filename);
        logger.info(temp);
        //TODO: keep retrying if failed

        //Join Game
        reply = sendCommandToMain("join", Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + joinnum + "\n" + Options.version);
        logger.info(reply);
        //TODO: keep retrying if failed OR merge with game creation in this case

        //Goto info screen
        refreshInfo();
        return;
      }

      numCOs = 0;
      coSelections = new int[numArmies * 2];
      altSelections = new boolean[numArmies * 2];
    }
  }

  private void parseSelectedNewGameTypeInput() {
    boolean startCOSelect = false;

    final int CO_SELECT = 0;
    final int LOAD_GAME = 1;
    final int START_NETWORK_GAME = 2;
    final int LOAD_REPLAY = 3;
    final int CREATE_SERVER_GAME = 4;
    final int JOIN_SERVER_GAME = 5;
    final int LOGIN_TO_SERVER_GAME = 6;
    final int JOIN_IRC_LOBBY = 7;
    
    switch(getCurrentlyHighlightedItem()){
	    case CO_SELECT:
	    			startCOSelect = true;
	    			break;
	    case LOAD_GAME:
	    			loadGame();
	    			break;
	    case START_NETWORK_GAME:
			  		startCOSelect = true;
			  		Options.startNetwork();
			  		setCurrentlyHighlightedItem(CO_SELECT);
	    			break;
	    case LOAD_REPLAY:
	    			loadReplay();
	    			break;
	    case CREATE_SERVER_GAME:
					createServerGame();
					startCOSelect = true;
					setCurrentlyHighlightedItem(CO_SELECT);
	    			break;
	    case JOIN_SERVER_GAME:
			  		joinServerGame();
	    			break;
	    case LOGIN_TO_SERVER_GAME:
	    			loginToServerGame();
	    			break;
	    case JOIN_IRC_LOBBY:
	    			joinIRClobby();
			    	break;
    }
    
    

    if (startCOSelect) {
      //New Game
    	setIsNewload(false);
    	setIsMapSelectScreen(true);
      setCurrentlyHighlightedItem(CO_SELECT);
      mapPage = CO_SELECT;

      //load categories
      String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
      mapsLocation = mapsLocation + "/";

      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return !name.startsWith(".");
        }
      };

      File[] dirs = new File(mapsLocation).listFiles(filter);


      Vector<String> v = new Vector<String>();
      int numcats = CO_SELECT;
      for (File dir : dirs) {
        if (dir.isDirectory()) {
          v.add(dir.getName());
          numcats++;
        }
      }
      if (numcats == CO_SELECT) {
        logger.info("NO MAP DIRECTORIES! QUITTING!");
      }
      mapCategories = new String[numcats];
      for (int i = CO_SELECT; i < numcats; i++) {
        mapCategories[i] = v.get(i);
      }

      setCurrentlySelectedMapCategory(CO_SELECT);
      setCurrentlySelectedSubCategory(CO_SELECT);
      loadMapDisplayNames();
      mapPage = CO_SELECT;
    }
  }

private void joinIRClobby() {
	parentFrame.setVisible(false);
	  FobbahLauncher.init(parentFrame, this);
}

private void loginToServerGame() {
	int CO_SELECT = 0;
	int LOAD_GAME = 1;
	
	logger.info("Log in to Server Game");

	  //try to connect to the server first to see that the user's URL is correct
	  if (!tryToConnect()) return;

	  //connect to the game
	  Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
	  if (Options.gamename == null) return;

	  //Get user's name and password
	  if (Options.isDefaultLoginOn()) {
	    Options.username = Options.getDefaultUsername();
	    Options.password = Options.getDefaultPassword();

	    if (Options.username == null || Options.username.length() < LOAD_GAME || Options.username.length() > MAX_USERNAME_LENGTH)
	      return;
	  } else {
		Options.username = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)", JOptionPane.PLAIN_MESSAGE);
	    if (Options.username == null) return;
	    Options.password = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password?", JOptionPane.PLAIN_MESSAGE);
	    if (Options.password == null) return;
	  }

	  //try to connect
	  String reply = sendCommandToMain("validup", Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + Options.version);
	  logger.info(reply);
	  if (!reply.equals("login successful")) {
	    if (reply.equals("version mismatch")) JOptionPane.showMessageDialog(this, "Version Mismatch");
	    else
	      JOptionPane.showMessageDialog(this, "Problem logging in, either the username/password is incorrect or the game has ended");
	    return;
	  }

	  //go to information screen
	  Options.snailGame = true;
	  setIsSnailInfoScreen(true);
	  setIsNewload(false);
	  setCurrentlyHighlightedItem(CO_SELECT);
	  item2 = CO_SELECT;

	  refreshInfo();
}

private void joinServerGame() {
	logger.info("Join Server Game");

	  //try to connect to the server first to see that the user's URL is correct
	  if (!tryToConnect()) return;

	  //connect to the game
	  Options.gamename = JOptionPane.showInputDialog(null, "Name of game:", "Join Game: Name", JOptionPane.PLAIN_MESSAGE);
	  if (Options.gamename == null) return;

	  //check the master password and get number of players and available slots
	  Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
	  if (Options.masterpass == null) return;

	  //Get user's name, password, and slot
	  if (Options.isDefaultLoginOn()) {
	    Options.username = Options.getDefaultUsername();
	    Options.password = Options.getDefaultPassword();

	    if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH)
	      return;
	  } else {
	    while (true) {
	    	Options.username = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)", JOptionPane.PLAIN_MESSAGE);
	      if (Options.username == null) return;
	      if (Options.username.length() < 1) continue;
	      if (Options.username.length() > MAX_USERNAME_LENGTH) continue;
	      break;
	    }
	    Options.password = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password", JOptionPane.PLAIN_MESSAGE);
	    if (Options.password == null) return;
	  }
	  setIsNewload(false);
	  isChooseNewGameTypeScreen = false;
	  setIsSnailInfoScreen(true);
	  refreshInfo();
	  if (!isSnailInfoScreen()) {
	    JOptionPane.showMessageDialog(this, "The game " + Options.gamename + " has ended");
	    return;
	  }
	  String slot  = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
	  if (slot == null) {
		  setIsTitleScreen(true);
		  setIsSnailInfoScreen(false);
		  return;
	  }

	  //Join
	  String reply = sendCommandToMain("join", Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version);
	  while (!reply.equals("join successful")) {
	    logger.info(reply);
	    if (reply.equals("no")) {
	      logger.info("Game does not exist");
	      Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
	      if (Options.gamename == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	      Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
	      if (Options.masterpass == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	    } else if (reply.equals("wrong password")) {
	      logger.info("Incorrect Password");
	      Options.gamename = JOptionPane.showInputDialog(null, "Name of game:", "Join Game: Name", JOptionPane.PLAIN_MESSAGE);
	      if (Options.gamename == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	      Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
	      if (Options.masterpass == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	    } else if (reply.equals("out of range")) {
	      logger.info("Army choice out of range or invalid");
	      slot  = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
	      if (slot == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	    } else if (reply.equals("slot taken")) {
	      logger.info("Army choice already taken");
	      slot  = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
	      if (slot == null) {
	    	  setIsTitleScreen(true);
	    	  setIsSnailInfoScreen(false);
	        return;
	      }
	    } else {
	      logger.info("Other problem");
	      JOptionPane.showMessageDialog(this, "Version Mismatch");
	      Options.snailGame = false;
	      setIsTitleScreen(true);
	      setIsSnailInfoScreen(false);
	      return;
	    }
	    refreshInfo();
	    reply = sendCommandToMain("join", Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version);
	  }

	  //go to information screen
	  int CO_SELECT = 0;
	  Options.snailGame = true;
	  setIsSnailInfoScreen(true);
	  setIsNewload(false);
	  setCurrentlyHighlightedItem(CO_SELECT);
	  item2 = CO_SELECT;

	  refreshInfo();
}

private void createServerGame() {
		logger.info("Create Server Game");
	  //try to connect to the server first to see that the user's URL is correct
	  if (!tryToConnect()) {
		  return;
	  }

	   //find an unused name
	  Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name", JOptionPane.PLAIN_MESSAGE);

	  if (Options.gamename == null) {
		  return;
	  }
	  
	  String reply = sendCommandToMain("qname", Options.gamename);
	  while (!reply.equals("yes")) {
	    logger.info(reply);
	    if (reply.equals("no")) {
	      logger.info("Game name already taken");
	      JOptionPane.showMessageDialog(this, "Game name already taken");
	    }
	    Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
	    if (Options.gamename == null) return;
	    reply = sendCommandToMain("qname", Options.gamename);
	  }

	  //set the master password and join
	  Options.masterpass = JOptionPane.showInputDialog(null, "Master Password for your game:", "Network Game: Master Pass?", JOptionPane.PLAIN_MESSAGE);
	  if (Options.masterpass == null) return;
	  if (Options.isDefaultLoginOn()) {
	    Options.username = Options.getDefaultUsername();
	    Options.password = Options.getDefaultPassword();

	    if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH)
	      return;
	  } else {
	    while (true) {
	    	Options.username = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)?", JOptionPane.PLAIN_MESSAGE);
	      if (Options.username == null) return;
	      if (Options.username.length() < 1) continue;
	      if (Options.username.length() > MAX_USERNAME_LENGTH) continue;
	      break;
	    }
	    Options.password = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password?", JOptionPane.PLAIN_MESSAGE);
	    if (Options.password == null) return;
	  }

	  //start game
	  logger.info("starting game");
	  Options.snailGame = true;
}

private void loadReplay() {
    String tempSaveLocation = ResourceLoader.properties.getProperty("tempSaveLocation");
    String loadFilename = tempSaveLocation + "/temporarysave.save";
    
	//prompt for replay name
	  logger.info("REPLAY MODE");
	  //Load Replay
	  JFileChooser fc = new JFileChooser();
	  fc.setDialogTitle("Load Replay");
	  fc.setCurrentDirectory(new File("./"));
	  fc.setApproveButtonText("Load");
	  int returnVal = fc.showOpenDialog(this);

	  if (returnVal != 1) {
	    loadFilename = fc.getSelectedFile().getPath();

	    File saveFile = new File(loadFilename);
	    if (saveFile.exists()) {
	      Battle b = new Battle(new Map(30, 20));
	      //Initialize a swing frame and put a BattleScreen inside
	      parentFrame.setSize(400, 400);
	      removeFromFrame();
	      BattleScreen bs = new BattleScreen(b, parentFrame);
	      parentFrame.getContentPane().add(bs);
	      parentFrame.validate();
	      parentFrame.pack();

	      //Start the mission
	      Mission.startMission(null, bs);
	      Mission.loadReplay(loadFilename);
	    }
	  }
}

private void loadGame() {
    String tempSaveLocation = ResourceLoader.properties.getProperty("tempSaveLocation");
    String loadFilename = tempSaveLocation + "/temporarysave.save";
    
	JFileChooser fc = new JFileChooser();
	  fc.setDialogTitle("Load Game");
	  fc.setCurrentDirectory(new File("./"));
	  fc.setApproveButtonText("Load");
	  int returnVal = fc.showOpenDialog(this);

	  if (returnVal != 1) {
	    loadFilename = fc.getSelectedFile().getPath();
	    File saveFile = new File(loadFilename);
	    if (saveFile.exists()) {
	      Battle b = new Battle(new Map(30, 20));
	      //Initialize a swing frame and put a BattleScreen inside
	      parentFrame.setSize(400, 400);
	      removeFromFrame();
	      BattleScreen bs = new BattleScreen(b, parentFrame);
	      parentFrame.getContentPane().add(bs);
	      parentFrame.validate();
	      parentFrame.pack();

	      //Start the mission
	      Mission.startMission(null, bs);
	      Mission.loadMission(loadFilename);
	    }
	  }
}

  private void coSelectScreenActions() {
    boolean nosecco = false;
    CO temp = null;
    if (cx == 0 && cy == 0 && numCOs % 2 == 1) {
      coSelections[numCOs] = -1;
      nosecco = true;
    } else {
      if (cx == 0 && cy == 0) return;
      temp = armyArray[selectedArmy][cx + cy * 3 - 1];
    }

    if (nosecco || temp != null && !(numCOs % 2 == 1 && COList.getIndex(temp) == coSelections[numCOs - 1])) {
      if (!nosecco) {
        coSelections[numCOs] = COList.getIndex(temp);
      }

      altSelections[numCOs] = altcostume;
      mainaltcostume = altcostume;
      altcostume = false;
      numCOs++;
      cx = 0;
      cy = 0;

      if (Options.snailGame && numCOs == 2) {
        logger.info("Stop for snail game");
        if (insertNewCO) {
          //load mission
          getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
          String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
          //load mission
          Battle b = new Battle(new Map(30, 20));
          //Initialize a swing frame and put a BattleScreen inside
          parentFrame.setSize(400, 400);
          removeFromFrame();
          BattleScreen bs = new BattleScreen(b, parentFrame);
          parentFrame.getContentPane().add(bs);
          parentFrame.validate();
          parentFrame.pack();

          //Start the mission
          Mission.startMission(null, bs);
          Mission.loadMission(loadFilename);
          bs.getBattle().getArmy(turn - 1).setCO(bs.getBattle().getCO(coSelections[0]));
          bs.getBattle().getArmy(turn - 1).setAltCO(bs.getBattle().getCO(coSelections[1]));
        }
        //fill rest with single andys
        for (int i = 2; i < coSelections.length; i++) {
          if (i % 2 == 0) coSelections[i] = 1;
          else coSelections[i] = 0;
        }
        for (int coSelection : coSelections) logger.info("" + coSelection);
        logger.info("Number of COs: " + numCOs);
        if (numArmies > 2) {
        	setIsCOselectScreen(false);
        	setSideSelect(true);
          setCurrentlyHighlightedItem(0);
          sideSelections = new int[numArmies];
          for (int i = 0; i < numArmies; i++) sideSelections[i] = i;
        } else {
          //no alliances allowed for 2 players
          sideSelections = new int[]{0, 1};
          setIsCOselectScreen(false);
          setIsBattleOptionsScreen(true);
          setCurrentlyHighlightedItem(0);
        }
      }

      if (numCOs == numArmies * 2) {
        logger.info("Total No of competing COs=[" + numCOs + "]  Armies=[" + numArmies + "]");

        if (numCOs > 4) {
        	setIsCOselectScreen(false);
          setSideSelect(true);
          setCurrentlyHighlightedItem(0);
          sideSelections = new int[numCOs / 2];
          for (int i = 0; i < numCOs / 2; i++) sideSelections[i] = i;
        } else {
          //no alliances allowed for 2 players
          sideSelections = new int[]{0, 1};

          setIsCOselectScreen(false);
          setIsBattleOptionsScreen(true);
          setCurrentlyHighlightedItem(0);
        }
      }
    }
  }

  private void optionsScreenActions() {
    if (getCurrentlyHighlightedItem() == 0) {
      //Music On/Off
      if (Options.isMusicOn()) Options.turnMusicOff();
      else Options.turnMusicOn();
    } else if (getCurrentlyHighlightedItem() == 1) {
    } else if (getCurrentlyHighlightedItem() == 2) {
      //Balance Mode On/Off
      if (Options.isBalance()) {
        Options.turnBalanceModeOff();
      } else {
        Options.turnBalanceModeOn();
      }
      bopt.setBalance(Options.isBalance());
    } else if (getCurrentlyHighlightedItem() == 3) {
      //Change the IP address
      Options.setIP();
    } else if (getCurrentlyHighlightedItem() == 4) {
      if (Options.isAutosaveOn())
        Options.setAutosave(false);

      else
        Options.setAutosave(true);
    } else if (getCurrentlyHighlightedItem() == 5) {
      if (Options.isRecording()) Options.setRecord(false);
      else Options.setRecord(true);
    } else if (getCurrentlyHighlightedItem() == 7) {
      //remap keys
      setIsOptionsScreen(false);
      setIsKeymappingScreen(true);
      setCurrentlyHighlightedItem(0);
    } else if (getCurrentlyHighlightedItem() == 8) {
      Options.toggleBattleBackground();
    } else if (getCurrentlyHighlightedItem() == 9) {
      //Change the IP address
      Options.setServer();
    } else if (getCurrentlyHighlightedItem() == 10) {
      Options.incrementDefaultBans();
      bopt = new BattleOptions();
    } else if (getCurrentlyHighlightedItem() == 13 && Options.getSelectedTerrain() == 2) {
      Options.setCustomTerrain();
    } else if (getCurrentlyHighlightedItem() == 14 && Options.getSelectedUrban() == 2) {
      Options.setCustomUrban();
    } else if (getCurrentlyHighlightedItem() == 15 && Options.getSelectedHQ() == 2) {
      Options.setCustomHQ();
    } else if (getCurrentlyHighlightedItem() == 16) {
      Options.toggleDefaultLogin();
    } else if (getCurrentlyHighlightedItem() == 17) {
      Options.setDefaultLogin();
    } else if (getCurrentlyHighlightedItem() == 18) {
      Options.toggleRefresh();
    }
  }

  private void titleScreenActions() {
    if (getCurrentlyHighlightedItem() == 0) {
      setIsTitleScreen(false);
      setIsNewload(true);
    } else if (getCurrentlyHighlightedItem() == MAP_EDITOR) {
      startMapEditor();
    } else if (getCurrentlyHighlightedItem() == OPTION_MENU) {
    	setIsTitleScreen(false);      
    	setIsOptionsScreen(true);
    	setCurrentlyHighlightedItem(0);
    }
  }

  private void startMapEditor() {
    final int MAP_EDITOR_STARTUP_COLS = 30;
    final int MAP_EDITOR_STARTUP_ROWS= 20;
    logger.info("Map Editor");
    Map m = new Map(MAP_EDITOR_STARTUP_COLS, MAP_EDITOR_STARTUP_ROWS);
    Battle bat = new Battle(m);

    parentFrame.setSize(400, 400);
    MapEditor me = new MapEditor(bat, parentFrame);
    removeFromFrame();
    parentFrame.getContentPane().add(me);
    parentFrame.validate();
    parentFrame.pack();
  }

  //try to connect to the server to see that the user's URL is correct
  public boolean tryToConnect() {
    String command = "test";
    String reply = "";
    try {
      URL url = new URL(Options.getServerName() + "main.pl");
      URLConnection con = url.openConnection();
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-type", "text/plain");
      con.setRequestProperty("Content-length", command.length() + "");
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
    } catch (MalformedURLException e1) {
      logger.error("Bad URL");
      JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
      return false;
    } catch (IOException e2) {
      logger.error("Unable to connect to the server at " + Options.getServerName());
      JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
      return false;
    }
    logger.info(reply);
    if (!reply.equals("success")) {
      logger.info("Could not connect to server");
      return false;
    }

    return true;
  }

  //try to connect to the server to see that the user's URL is correct
  public String sendCommandToMain(String command, String extra) {
    String reply = "";
    try {
      URL url = new URL(Options.getServerName() + "main.pl");
      URLConnection con = url.openConnection();
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-type", "text/plain");
      if (extra.equals("")) {
        con.setRequestProperty("Content-length", command.length() + "");
      } else {
        con.setRequestProperty("Content-length", (command.length() + 1 + extra.length()) + "");
      }
      PrintStream out = new PrintStream(con.getOutputStream());
      out.print(command);
      if (!extra.equals("")) {
        out.print("\n");
        out.print(extra);
      }
      out.flush();
      out.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String s = in.readLine();
      if (s != null) {
        reply += s;
        while ((s = in.readLine()) != null) {
          reply += "\n";
          reply += s;
        }
      }
      in.close();
    } catch (MalformedURLException e1) {
      logger.info("Bad URL " + Options.getServerName());
      JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
      return null;
    } catch (IOException e2) {
      logger.error("Connection Problem during command " + command + " with information:\n" + extra);
      JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
      return null;
    }

    return reply;
  }

  public String sendFile(String script, String input, String file) {
    String reply = "";
    try {
      URL url = new URL(Options.getServerName() + script);
      URLConnection con = url.openConnection();
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-type", "text/plain");
      byte buffer[] = new byte[1];
      logger.info("opening file");
      File source = new File(file);
      con.setRequestProperty("Content-length", (input.length() + 1 + source.length()) + "");

      PrintStream out1 = new PrintStream(con.getOutputStream());
      out1.print(input);
      out1.print("\n");
      FileInputStream src = new FileInputStream(file);
      logger.debug("Sending file [" + src + "]");
      OutputStream out = con.getOutputStream();
      while (true) {
        int count = src.read(buffer);
        if (count == -1) break;
        out.write(buffer);
      }
      out.flush();
      out.close();
      out1.flush();
      out1.close();

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String s = in.readLine();
      if (s != null) {
        reply += s;
        while ((s = in.readLine()) != null) {
          reply += "\n";
          reply += s;
        }
      }
      in.close();
    } catch (MalformedURLException e1) {
      logger.error("Bad URL " + Options.getServerName());
      JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
      return null;
    } catch (IOException e2) {
      logger.error("Connection problem, unable to send file");
      JOptionPane.showMessageDialog(this, "Connection problem, unable to send file");
      return null;
    }

    return reply;
  }

  public boolean getFile(String script, String input, String file) {
    try {
      URL url = new URL(Options.getServerName() + script);
      URLConnection con = url.openConnection();
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-type", "text/plain");
      logger.info("opening file");
      File source = new File(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
      logger.debug("Getting file [" + source + "]");
      con.setRequestProperty("Content-length", input.length() + "");
      PrintStream out = new PrintStream(con.getOutputStream());
      out.print(input);
      out.flush();
      out.close();

      //recieve reply
      byte buffer[] = new byte[1];
      FileOutputStream output = new FileOutputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
      logger.debug("Getting reply [" + ResourceLoader.properties.getProperty("saveLocation") + "/" + file + "]");
      InputStream in = con.getInputStream();
      while (true) {
        int count = in.read(buffer);
        if (count == -1) break;
        output.write(buffer);
      }
      in.close();
      output.close();
    } catch (MalformedURLException e1) {
      logger.error("Bad URL " + Options.getServerName());
      JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
      return false;
    } catch (IOException e2) {
      logger.error("Connection problem, unable to get file from server");
      JOptionPane.showMessageDialog(this, "Connection problem, unable to get file from server");
      return false;
    }

    return true;
  }

  public void pressedB() {
    if (isInfoScreen()) {
    	setIsInfoScreen(false);
    } else if (isCOselectScreen() && !isInfoScreen()) {
      if (numCOs == 0) {
    	setIsMapSelectScreen(true);
    	setIsCOselectScreen(false);
        setCurrentlyHighlightedItem(0);
        selectedArmy = 0;
        if (Options.isNetworkGame()) Options.stopNetwork();
        if (Options.snailGame) {
        	setIsMapSelectScreen(false);
        	setIsTitleScreen(true);
          Options.snailGame = false;
        }
      } else {
        coSelections[numCOs] = -1;
        numCOs--;
        cx = 0;
        cy = 0;
      }
    } else if (isOptionsScreen()) {
    	setIsTitleScreen(true);
    	setIsOptionsScreen(false);
      setCurrentlyHighlightedItem(0);
      if (Options.isNetworkGame()) Options.stopNetwork();
    } else if (isChooseNewGameTypeScreen()) {
    	setIsTitleScreen(true);
    	setIsNewload(false);
      setCurrentlyHighlightedItem(0);
      if (Options.isNetworkGame()) Options.stopNetwork();
    } else if (isSideSelect()) {
    	setIsCOselectScreen(true);
      numCOs--;
      setSideSelect(false);
      Options.snailGame = false; //not always needed, but doesn't hurt
      setCurrentlyHighlightedItem(0);
      if (Options.isNetworkGame()) Options.stopNetwork();
    } else if (isBattleOptionsScreen()) {
      if (numCOs > 4) setSideSelect(true);
      else {
        numCOs--;
        setIsCOselectScreen(true);
      }
      setIsBattleOptionsScreen(false);
      Options.snailGame = false; //not always needed, but doesn't hurt
      setCurrentlyHighlightedItem(0);
      cx = 0;
      cy = 0;
      if (Options.isNetworkGame()) Options.stopNetwork();
    } else if (isMapSelectScreen()) {
    	setIsNewload(true);
    	setIsMapSelectScreen(false);
      Options.snailGame = false; //not always needed, but doesn't hurt
      setCurrentlyHighlightedItem(0);
      if (Options.isNetworkGame()) Options.stopNetwork();
    } else if (isKeyMappingScreen()) {
    	setIsKeymappingScreen(false);
    	setIsOptionsScreen(true);
      setCurrentlyHighlightedItem(0);
    } else if (isSnailInfoScreen()) {
      Options.snailGame = false;
      setIsSnailInfoScreen(false);
      setIsNewload(true);
      setCurrentlyHighlightedItem(0);
    }
  }

  public void pressedPGDN() {
    if (isMapSelectScreen()) {
      if (isOverLastPage(++mapPage)) {
        mapPage--;
      } else
        setCurrentlyHighlightedItem(0);

      loadMiniMapPreview();
    } else if (isSnailInfoScreen()) {
      if (item2 == 0) {
        syspos++;
        if (syspos > syslog.length - 5) syspos = syslog.length - 5;
        if (syspos < 0) syspos = 0;
      } else if (item2 == 1) {
        chatpos++;
        if (chatpos > chatlog.length - 5) chatpos = chatlog.length - 5;
        if (chatpos < 0) chatpos = 0;
      }
    }
  }

  public void pressedPGUP() {
    if (isMapSelectScreen()) {
      mapPage--;
      if (mapPage < 0) {
        mapPage++;
      } else
        setCurrentlyHighlightedItem(0);

      loadMiniMapPreview();
    } else if (isSnailInfoScreen()) {
      if (item2 == 0) {
        syspos--;
        if (syspos < 0) syspos = 0;
      } else if (item2 == 1) {
        chatpos--;
        if (chatpos < 0) chatpos = 0;
      }
    }
  }

  public void processRightKeyBattleOptions() {
    if (getCurrentlyHighlightedItem() == 0) {
      visibility++;
      if (visibility > 2) visibility = 0;

      if (visibility == 0) {
        bopt.setFog(false);
        bopt.setMist(false);
      } else if (visibility == 1) {
        bopt.setFog(true);
        bopt.setMist(false);
      } else {
        bopt.setMist(true);
        bopt.setFog(false);
      }
    } else if (getCurrentlyHighlightedItem() == 1) {
      int wtemp = bopt.getWeatherType();
      wtemp++;
      if (wtemp > 4) wtemp = 0;
      bopt.setWeatherType(wtemp);
    } else if (getCurrentlyHighlightedItem() == 2) {
      int ftemp = bopt.getFundsLevel();
      ftemp += 500;
      if (ftemp > 10000) ftemp = 10000;
      bopt.setFundsLevel(ftemp);
    } else if (getCurrentlyHighlightedItem() == 3) {
      int stemp = bopt.getStartFunds();
      stemp += 500;
      if (stemp > 30000) {
        stemp = 30000;
      }
      bopt.setStartFunds(stemp);
    } else if (getCurrentlyHighlightedItem() == 4) {
      int temp = bopt.getTurnLimit();
      temp++;
      bopt.setTurnLimit(temp);
    } else if (getCurrentlyHighlightedItem() == 5) {
      int temp = bopt.getCapLimit();
      temp++;
      bopt.setCapLimit(temp);
    } else if (getCurrentlyHighlightedItem() == 6) {
      if (bopt.isCOP()) bopt.setCOP(false);
      else bopt.setCOP(true);
    } else if (getCurrentlyHighlightedItem() == 7) {
      if (bopt.isBalance()) bopt.setBalance(false);
      else bopt.setBalance(true);
    } else if (getCurrentlyHighlightedItem() == 8) {
      if (bopt.isRecording()) bopt.setReplay(false);
      else bopt.setReplay(true);
    } else if (getCurrentlyHighlightedItem() == 9) {
      cx++;
      if (cx >= BaseDMG.NUM_UNITS / 2) cy = 1;
      if (cx >= BaseDMG.NUM_UNITS) {
        cx = 0;
        cy = 0;
      }
    } else if (getCurrentlyHighlightedItem() == 10) {
      if (bopt.getSnowChance() < 100)
        bopt.setSnowChance(bopt.getSnowChance() + 1);
    } else if (getCurrentlyHighlightedItem() == 11) {
      if (bopt.getRainChance() < 100)
        bopt.setRainChance(bopt.getRainChance() + 1);
    } else if (getCurrentlyHighlightedItem() == 12) {
      if (bopt.getSandChance() < 100)
        bopt.setSandChance(bopt.getSandChance() + 1);
    } else if (getCurrentlyHighlightedItem() == 13) {
      bopt.setMinWTime(bopt.getMinWTime() + 1);
    } else if (getCurrentlyHighlightedItem() == 14) {
      bopt.setMaxWTime(bopt.getMaxWTime() + 1);
    } else if (getCurrentlyHighlightedItem() == 15) {
      bopt.setMinWDay(bopt.getMinWDay() + 1);
    }
  }

  public void returnToServerInfo() {
	setIsSnailInfoScreen(true);
	setIsTitleScreen(false);
    Options.snailGame = true;
    refreshInfo();
  }

  public void refreshInfo() {
    String reply = sendCommandToMain("getturn", Options.gamename);
    logger.info(reply);
    
    if (reply.equals("no")) {
      setIsSnailInfoScreen(false);
      setIsNewload(true);
      Options.snailGame = false;
      setCurrentlyHighlightedItem(0);
      return;
    }
    
    String[] nums = reply.split("\n");
    day = Integer.parseInt(nums[0]);
    turn = Integer.parseInt(nums[1]);
    int numplay = Integer.parseInt(nums[2]);
    usernames = new String[numplay];
    System.arraycopy(nums, 3, usernames, 0, numplay);

    reply = sendCommandToMain("getsys", Options.gamename);
    syslog = reply.split("\n");
    syspos = syslog.length - 5;
    if (syspos < 0) syspos = 0;

    reply = sendCommandToMain("getchat", Options.gamename);
    chatlog = reply.split("\n");
    chatpos = chatlog.length - 5;
    if (chatpos < 0) chatpos = 0;
  }

  //This class deals with keypresses
  class KeyControl implements KeyListener {
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
      int keypress = e.getKeyCode();
      //deal with key remapping
      if (chooseKey) {
        switch (getCurrentlyHighlightedItem()) {
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
      } else if (keypress == Options.up) {
        if (isTitleScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          SFX.playClip(soundLocation + "/menutick.wav");
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(2);
        } else if (isCOselectScreen() && !isInfoScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          SFX.playClip(soundLocation + "/menutick.wav");
          cy--;
          if (cy < 0) cy = 0;
          if (cx != 0 || cy != 0) {
            CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
            if (temp != null) infono = COList.getIndex(temp);
          }
          skip = 0;
          glide = 0;
        } else if (isInfoScreen()) {
          skip--;
          if (skip < 0)
            skip = 0;
        } else if (isOptionsScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(18);
        } else if (isBattleOptionsScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(15);
        } else if (isChooseNewGameTypeScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(7);
        } else if (isSideSelect()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(numArmies - 1);
        } else if (isMapSelectScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          SFX.playClip(soundLocation + "/menutick.wav");
          if (getCurrentlyHighlightedItem() < 0) {
            setCurrentlyHighlightedItem(11);
            mapPage--;
            if (mapPage < 0) {
              mapPage = 0;
              setCurrentlyHighlightedItem(0);
            }
          }
          loadMiniMapPreview();
        } else if (isKeyMappingScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(17);
        } else if (isSnailInfoScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          if (getCurrentlyHighlightedItem() < 0) setCurrentlyHighlightedItem(1);
        }
      } else if (keypress == Options.down) {
        if (isTitleScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          SFX.playClip(soundLocation + "/menutick.wav");
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 2) setCurrentlyHighlightedItem(0);
        } else if (isCOselectScreen() && !isInfoScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          SFX.playClip(soundLocation = "/menutick.wav");
          cy++;
          if (cy > 4) cy = 4;
          CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
          if (temp != null) infono = COList.getIndex(temp);
          skip = 0;
          glide = 0;
        } else if (isInfoScreen()) {
          skip++;
          if (skip > skipMax)
            skip = skipMax;
        } else if (isOptionsScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 18) setCurrentlyHighlightedItem(0);
        } else if (isBattleOptionsScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 15) setCurrentlyHighlightedItem(0);
        } else if (isChooseNewGameTypeScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 7) setCurrentlyHighlightedItem(0);
        } else if (isSideSelect()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > numArmies - 1) setCurrentlyHighlightedItem(0);
        } else if (isMapSelectScreen()) {
          String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          SFX.playClip(soundLocation + "/menutick.wav");

          if (isMapVisible(getCurrentlyHighlightedItem())) {
            setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() - 1);
          }

          if (getCurrentlyHighlightedItem() > 11) {
            setCurrentlyHighlightedItem(0);
            if (isOverLastPage(++mapPage)) {
              mapPage--;
              SFX.playClip(soundLocation + "/menutick.wav");
              setCurrentlyHighlightedItem(11);
            }
          }
          loadMiniMapPreview();
        } else if (isKeyMappingScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 17) setCurrentlyHighlightedItem(0);
        } else if (isSnailInfoScreen()) {
          setCurrentlyHighlightedItem(getCurrentlyHighlightedItem() + 1);
          if (getCurrentlyHighlightedItem() > 1) setCurrentlyHighlightedItem(0);
        }
      } else if (keypress == Options.altright || (keypress == Options.right && e.isControlDown())) {
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(getCurrentlySelectedSubCategory() + 1);
          SFX.playClip(soundLocation + "/minimap.wav");
          if (getCurrentlySelectedSubCategory() > 9) setCurrentlySelectedSubCategory(0);

          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        } else if (isCOselectScreen()) {
          SFX.playClip(soundLocation + "/minimap.wav");
          selectedArmy++;
          if (selectedArmy > 7) selectedArmy = 7;
          if (cx + cy * 3 - 1 > 0) {
            CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
            if (temp != null) infono = COList.getIndex(temp);
          }
          skip = 0;
          glide = 0;
        }
      } else if (keypress == Options.altleft || (keypress == Options.left && e.isControlDown())) {
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");

        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(getCurrentlySelectedSubCategory() - 1);
          SFX.playClip(soundLocation + "/minimap.wav");
          if (getCurrentlySelectedSubCategory() < 0) setCurrentlySelectedSubCategory(9);

          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        } else if (isCOselectScreen()) {
          SFX.playClip(soundLocation + "/minimap.wav");
          selectedArmy--;
          if (selectedArmy < 0) selectedArmy = 0;
          if (cx + cy * 3 - 1 > 0) {
            CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
            if (temp != null) infono = COList.getIndex(temp);
          }
          skip = 0;
          glide = 0;
        }
      } else if (keypress == Options.left) {
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        if (isCOselectScreen() && !isInfoScreen()) {
          SFX.playClip(soundLocation + "/menutick.wav");
          cx--;
          if (cx < 0) {
            cx = 0;
            if (cy != 0) {
              cx = 2;
              cy--;
            }
          }
          if (cx != 0 || cy != 0) {
            CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
            if (temp != null) infono = COList.getIndex(temp);
          }
          skip = 0;
          glide = 0;
        } else if (isSideSelect()) {
          if (sideSelections[getCurrentlyHighlightedItem()] == 0) sideSelections[getCurrentlyHighlightedItem()] = numArmies - 1;
          else sideSelections[getCurrentlyHighlightedItem()] -= 1;
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 6) {
          Options.decrementCursor();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 11) {
          Options.decrementCO();
          glide = 0;
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 12) {
          SFX.toggleMute();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 13) {
          Options.decrementTerrain();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 14) {
          Options.decrementUrban();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 15) {
          Options.decrementHQ();
        } else if (isMapSelectScreen()) {

          setCurrentlySelectedMapCategory(getCurrentMapCategory() - 1);
          if (getCurrentMapCategory() < 0) setCurrentlySelectedMapCategory(mapCategories.length - 1);
          setCurrentlySelectedSubCategory(0);

          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        } else if (isBattleOptionsScreen()) {
          if (getCurrentlyHighlightedItem() == 0) {
            visibility--;
            if (visibility < 0) visibility = 2;

            if (visibility == 0) {
              bopt.setFog(false);
              bopt.setMist(false);
            } else if (visibility == 1) {
              bopt.setFog(true);
              bopt.setMist(false);
            } else {
              bopt.setMist(true);
              bopt.setFog(false);
            }
          } else if (getCurrentlyHighlightedItem() == 1) {
            int wtemp = bopt.getWeatherType();
            wtemp--;
            if (wtemp < 0) wtemp = 4;
            bopt.setWeatherType(wtemp);
          } else if (getCurrentlyHighlightedItem() == 2) {
            int ftemp = bopt.getFundsLevel();
            ftemp -= 500;
            if (ftemp <= 0) ftemp = 500;
            bopt.setFundsLevel(ftemp);
          } else if (getCurrentlyHighlightedItem() == 3) {
            int stemp = bopt.getStartFunds();
            stemp -= 500;
            if (stemp <= 0) stemp = 0;
            bopt.setStartFunds(stemp);
          } else if (getCurrentlyHighlightedItem() == 4) {
            int temp = bopt.getTurnLimit();
            temp--;
            if (temp < 0) temp = 0;
            bopt.setTurnLimit(temp);
          } else if (getCurrentlyHighlightedItem() == 5) {
            int temp = bopt.getCapLimit();
            temp--;
            if (temp < 0) temp = 0;
            bopt.setCapLimit(temp);
          } else if (getCurrentlyHighlightedItem() == 6) {
            if (bopt.isCOP()) bopt.setCOP(false);
            else bopt.setCOP(true);
          } else if (getCurrentlyHighlightedItem() == 7) {
            if (bopt.isBalance()) bopt.setBalance(false);
            else bopt.setBalance(true);
          } else if (getCurrentlyHighlightedItem() == 8) {
            if (bopt.isRecording()) bopt.setReplay(false);
            else bopt.setReplay(true);
          } else if (getCurrentlyHighlightedItem() == 9) {
            cx--;
            if (cx < BaseDMG.NUM_UNITS / 2) cy = 0;
            if (cx < 0) {
              cx = BaseDMG.NUM_UNITS - 1;
              cy = 1;
            }
          } else if (getCurrentlyHighlightedItem() == 10) {
            if (bopt.getSnowChance() > 0)
              bopt.setSnowChance(bopt.getSnowChance() - 1);
          } else if (getCurrentlyHighlightedItem() == 11) {
            if (bopt.getRainChance() > 0)
              bopt.setRainChance(bopt.getRainChance() - 1);
          } else if (getCurrentlyHighlightedItem() == 12) {
            if (bopt.getSandChance() > 0)
              bopt.setSandChance(bopt.getSandChance() - 1);
          } else if (getCurrentlyHighlightedItem() == 13) {
            if (bopt.getMinWTime() > 0)
              bopt.setMinWTime(bopt.getMinWTime() - 1);
          } else if (getCurrentlyHighlightedItem() == 14) {
            if (bopt.getMaxWTime() > 0)
              bopt.setMaxWTime(bopt.getMaxWTime() - 1);
          } else if (getCurrentlyHighlightedItem() == 15) {
            if (bopt.getMinWDay() > 0)
              bopt.setMinWDay(bopt.getMinWDay() - 1);
          }
        } else if (isSnailInfoScreen()) {
          item2--;
          if (item2 < 0) item2 = 1;
        }
      } else if (keypress == Options.right) {
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");

        if (isCOselectScreen() && !isInfoScreen()) {
          SFX.playClip(soundLocation + "/menutick.wav");
          cx++;
          if (cx > 2) {
            cx = 2;
            if (cy != 4) {
              cx = 0;
              cy++;
            }
          }
          CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
          if (temp != null) infono = COList.getIndex(temp);
          skip = 0;
          glide = 0;
        } else if (isSideSelect()) {
          if (sideSelections[getCurrentlyHighlightedItem()] == numArmies - 1) sideSelections[getCurrentlyHighlightedItem()] = 0;
          else sideSelections[getCurrentlyHighlightedItem()] += 1;
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 6) {
          Options.incrementCursor();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 11) {
          Options.incrementCO();
          glide = 0;
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 12) {
          SFX.toggleMute();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 13) {
          Options.incrementTerrain();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 14) {
          Options.incrementUrban();
        } else if (isOptionsScreen() && getCurrentlyHighlightedItem() == 15) {
          Options.incrementHQ();
        }
        if (isMapSelectScreen()) {

          setCurrentlySelectedMapCategory(getCurrentMapCategory() + 1);
          if (getCurrentMapCategory() > mapCategories.length - 1) setCurrentlySelectedMapCategory(0);
          setCurrentlySelectedSubCategory(0);

          setCurrentlyHighlightedItem(0);
          mapPage = 0;
          //load maps in new directory
          loadMapDisplayNames();
        } else if (isBattleOptionsScreen()) {
          processRightKeyBattleOptions();
        } else if (isSnailInfoScreen()) {
          item2++;
          if (item2 > 1) item2 = 0;
        }
      } else if (keypress == Options.pgdn) {
        pressedPGDN();
      } else if (keypress == Options.pgup) {
        pressedPGUP();
      } else if (keypress == Options.akey) {
        pressedA();
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        SFX.playClip(soundLocation + "/ok.wav");
      } else if (keypress == Options.bkey) {
        pressedB();
        String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        SFX.playClip(soundLocation + "/cancel.wav");
      } else if (keypress == KeyEvent.VK_1) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(0);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_2) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(1);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_3) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(2);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_4) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(3);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_5) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(4);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_6) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(5);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_7) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(6);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_8) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(7);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_9) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(8);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == KeyEvent.VK_0) {
        if (isMapSelectScreen()) {
          setCurrentlySelectedSubCategory(9);
          setCurrentlyHighlightedItem(0);
          mapPage = 0;

          //load maps in new directory
          loadMapDisplayNames();
        }
      } else if (keypress == Options.constmode) {
        logger.info("Alternating Costumes");
        altcostume = !altcostume;
      } else if (keypress == Options.nextunit) {
        if (isCOselectScreen()) {
          if (isInfoScreen()) {
        	 setIsInfoScreen(false);
          } else {
            if (cx == 0 && cy == 0) {
              Random r = new Random();
              int sel = r.nextInt(COList.getListing().length);
              CO sc = COList.getListing()[sel];
              logger.info("Selecting " + sc.getName());
              selectedArmy = sc.getStyle();
              for (int i = 0; armyArray[selectedArmy][i] != null; i++) {
                if (armyArray[selectedArmy][i] == sc) {
                  if (i < 2) {
                    cx = i + 1;
                    cy = 0;
                  } else {
                    cx = (i - 2) % 3;
                    cy = (i - 2) / 3 + 1;
                  }
                }
              }
              pressedA();
            } else {
            	setIsInfoScreen(true);
            }
          }
        }
      }
    }

    public void keyReleased(KeyEvent e) {
    }
  }

  class MouseControl implements MouseInputListener {
    private static final int OPTIONS_HEIGHT_TOP = 279;
	private static final int OPTIONS_HEIGHT_BOTTOM = 247;
	private static final int OPTIONS_WIDTH_END = 320;
	private static final int OPTIONS_WIDTH_START = 175;
	private static final int MAP_DESIGN_HEIGHT_TOP = 183;
	private static final int MAP_DESIGN_HEIGHT_BOTTOM = 156;
	private static final int MAP_DESIGN_WIDTH_END = 350;
	private static final int MAP_DESIGN_WIDTH_START = 143;
	private static final int NEW_GAME_HEIGHT_TOP = 87;
	private static final int NEW_GAME_HEIGHT_BOTTOM = 60;
	private static final int NEW_GAME_WIDTH_START = 160;
	private static final int NEW_GAME_WIDTH_END = 332;

	public void mouseClicked(MouseEvent e) {
      int x = e.getX() - parentFrame.getInsets().left;
      int y = e.getY() - parentFrame.getInsets().top;


      if (e.getButton() == MouseEvent.BUTTON1) {
        //first mouse button
        if (isTitleScreen()) {
        	boolean newGame = x > NEW_GAME_WIDTH_START && x < NEW_GAME_WIDTH_END && y > NEW_GAME_HEIGHT_BOTTOM && y < NEW_GAME_HEIGHT_TOP;
        	boolean designMaps = x > MAP_DESIGN_WIDTH_START && x < MAP_DESIGN_WIDTH_END && y > MAP_DESIGN_HEIGHT_BOTTOM && y < MAP_DESIGN_HEIGHT_TOP;
        	boolean optionsScreen = x > OPTIONS_WIDTH_START && x < OPTIONS_WIDTH_END && y > OPTIONS_HEIGHT_BOTTOM && y < OPTIONS_HEIGHT_TOP;

        	if (newGame) {
        		logger.info("Moving into the New Game Menu");
	            setCurrentlyHighlightedItem(0);
	            setIsTitleScreen(false);
	            setIsNewload(true);
	          } else {
				if (designMaps) {
				    setCurrentlyHighlightedItem(1);
				    logger.info("Moving into the Design Maps Area");
				    startMapEditor();
				  } else {
					if (optionsScreen) {
					    logger.info("Moving into the Options Menu");
					    setIsTitleScreen(false);
					    setIsOptionsScreen(true);
					    setCurrentlyHighlightedItem(0);
					  }
				}
			}
        	
        } else if (isChooseNewGameTypeScreen()) {
          if (x < 130) {
            int i = y / 30;
            if (i < 8) {
              setCurrentlyHighlightedItem(i);
              pressedA();
            }
          }
        } else if (isOptionsScreen()) {
          if (x < 220) {
            int i = y / 20;
            if ((i < 6 || i > 6) && i < 11) {
              setCurrentlyHighlightedItem(i);
              pressedA();
            } else if (i == 6) {
              setCurrentlyHighlightedItem(i);
              Options.incrementCursor();
            }
          }
        } else if (isMapSelectScreen()) {
          if (y < 30) {
            if (x < 180) {
              //change category
              setCurrentlySelectedMapCategory(getCurrentMapCategory() + 1);
              if (getCurrentMapCategory() > mapCategories.length - 1) setCurrentlySelectedMapCategory(0);
              setCurrentlySelectedSubCategory(0);

              setCurrentlyHighlightedItem(0);
              mapPage = 0;

              //load maps in new directory
              String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
              loadMapDisplayNames();
            }
          }
          if (y < 40 && x > 180) {
            //change subcategory
            if (x < 240) setCurrentlySelectedSubCategory(0);
            else if (x < 260) setCurrentlySelectedSubCategory(1);
            else if (x < 280) setCurrentlySelectedSubCategory(2);
            else if (x < 300) setCurrentlySelectedSubCategory(3);
            else if (x < 320) setCurrentlySelectedSubCategory(4);
            else if (x < 340) setCurrentlySelectedSubCategory(5);
            else if (x < 360) setCurrentlySelectedSubCategory(6);
            else if (x < 380) setCurrentlySelectedSubCategory(7);
            else if (x < 400) setCurrentlySelectedSubCategory(8);
            else if (x < 480) setCurrentlySelectedSubCategory(9);

            setCurrentlyHighlightedItem(0);
            mapPage = 0;

            //load maps in new directory
            loadMapDisplayNames();
          } else if (y > 30 && y < 38) {
            if (x > 84 && x < 98)
              pressedPGUP();
          } else if (y > 50 && y < 302) {
            if (x < 160) {
              int i = (y - 50) / 21;
              if (i < NUM_VISIBLE_ROWS && isMapVisible(i)) {
                setCurrentlyHighlightedItem(i);
                pressedA();
              }
            }
          } else if (y > 312 && y < 320) {
            if (x > 84 && x < 98)
              pressedPGDN();
          }
        } else if (isCOselectScreen()) {
          if (y > 61 && y < 321 && x > 2 && x < 158) {
            cx = (x - 2) / 52;
            cy = (y - 61) / 52;
            pressedA();
          } else if (x >= 3 && x <= 155 && y <= 53) {
            selectedArmy = (x - 3) / 19;
            if (cx != 0 || cy != 0) {
              CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];
              if (temp != null) infono = COList.getIndex(temp);
            }
          }
        } else if (isSideSelect()) {
          if (x < 130) {
            if (y / 20 < numArmies) {
              setCurrentlyHighlightedItem(y / 20);
              if (sideSelections[getCurrentlyHighlightedItem()] == numArmies - 1) sideSelections[getCurrentlyHighlightedItem()] = 0;
              else sideSelections[getCurrentlyHighlightedItem()] += 1;
            }
          } else {
            pressedA();
          }
        } else if (isBattleOptionsScreen()) {
          if (x > 10 && x < 10 + BaseDMG.NUM_UNITS / 2 * 16 && y > 184 && y < 220) {
            cy = (y - 184) / 20;
            cx = (x - 10) / 16 + cy * BaseDMG.NUM_UNITS / 2;
            setCurrentlyHighlightedItem(9);
            pressedA();
          } else if (x < 210) {
            if (y / 20 < 9) {
              setCurrentlyHighlightedItem(y / 20);
              processRightKeyBattleOptions();
            }
          } else {
            pressedA();
          }
        } else if (isSnailInfoScreen()) {
          if (x > 240 && x < 480 && y > 280 && y < 300) {
            setCurrentlyHighlightedItem(0);
            pressedA();
          } else if (x > 240 && x < 480 && y > 300 && y < 320) {
            setCurrentlyHighlightedItem(1);
            pressedA();
          } else if (x > 0 && x < 160 && y > 100 && y < 120) {
            item2 = 0;
          } else if (x > 160 && x < 320 && y > 100 && y < 120) {
            item2 = 1;
          } else if (x > 320 && x < 480 && y > 100 && y < 120) {
            //send chat message
            String message = JOptionPane.showInputDialog("Type in your chat message");
            if (message == null) return;
            String reply = sendCommandToMain("sendchat", Options.gamename + "\n" + Options.username + "\n" + message);
            logger.info(reply);
            refreshInfo();
          } else if (x > 460 && x < 480 && y > 0 && y < 20) {
            pressedPGUP();
          } else if (x > 460 && x < 480 && y > 80 && y < 100) {
            pressedPGDN();
          }
        }
      } else {
        //any other button
        pressedB();
      }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
      int x = e.getX() - parentFrame.getInsets().left;
      int y = e.getY() - parentFrame.getInsets().top;
      if (isMapSelectScreen()) {
        if (y > 50 && y < 302 && x < 160) {
          int i = (y - 50) / 21;
          if (i < NUM_VISIBLE_ROWS && isMapVisible(i)) {
            if (i != getCurrentlyHighlightedItem()) {
              setCurrentlyHighlightedItem(i);
              loadMiniMapPreview();
            }
          }
        }
      } else if (isCOselectScreen()) {
        if (y > 61 && y < 321 && x > 2 && x < 158) {
          cx = (x - 2) / 52;
          cy = (y - 61) / 52;
          if (cx != 0 || cy != 0) {
            CO temp = armyArray[selectedArmy][cx + cy * 3 - 1];

            if (temp != null && infono != COList.getIndex(temp)) glide = 0;
            if (temp != null) infono = COList.getIndex(temp);
          }
        }
      }
    }
  }

  /////////YES, This is a hack :).
  public void LaunchCreateServerGame() {
    logger.info("Create Server Game");
    //try to connect to the server first to see that the user's URL is correct
    if (!tryToConnect()) return;

    //find an unused name
    Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
    if (Options.gamename == null) return;
    String reply = sendCommandToMain("qname", Options.gamename);
    while (!reply.equals("yes")) {
      logger.info(reply);
      if (reply.equals("no")) {
        logger.info("Game name already taken");
        JOptionPane.showMessageDialog(this, "Game name already taken");
      }
      Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
      if (Options.gamename == null) return;
      reply = sendCommandToMain("qname", Options.gamename);
    }

    //set the master password and join
    Options.masterpass = JOptionPane.showInputDialog("Type in a master password for your game");
    if (Options.masterpass == null) return;
    if (Options.isDefaultLoginOn()) {
      Options.username = Options.getDefaultUsername();
      Options.password = Options.getDefaultPassword();

      if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH)
        return;
    } else {
      while (true) {
        Options.username = JOptionPane.showInputDialog("Type in your username for this game (" + MAX_USERNAME_LENGTH + " characters max)");
        if (Options.username == null) return;
        if (Options.username.length() < 1) continue;
        if (Options.username.length() > MAX_USERNAME_LENGTH) continue;
        break;
      }
      Options.password = JOptionPane.showInputDialog("Type in your password for this game");
      if (Options.password == null) return;
    }

    //start game
    logger.info("starting game");
    Options.snailGame = true;
    setCurrentlyHighlightedItem(0);

      //New Game
      setIsNewload(false);
      setIsMapSelectScreen(true);
      setCurrentlyHighlightedItem(0);
      mapPage = 0;

      //load categories
      String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
      File[] dirs = new File(mapsLocation + "/").listFiles();
      Vector<String> v = new Vector<String>();
      int numcats = 0;
      for (File dir : dirs) {
        if (dir.isDirectory()) {
          v.add(dir.getName());
          numcats++;
        }
      }
      if (numcats == 0) {
        logger.info("NO MAP DIRECTORIES! QUITTING!");
      }
      mapCategories = new String[numcats];
      for (int i = 0; i < numcats; i++) {
        mapCategories[i] = v.get(i);
      }

      setCurrentlySelectedMapCategory(0);
      setCurrentlySelectedSubCategory(0);
      loadMapDisplayNames();
      mapPage = 0;
  }

  public void LaunchCreateServerGame(String username, String password, String gamename, String gamepass) {
    logger.info("Create Server Game");
    //try to connect to the server first to see that the user's URL is correct
    if (!tryToConnect()) return;

    //find an unused name

    //Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
    Options.gamename = gamename;
    if (Options.gamename == null) return;
    String reply = sendCommandToMain("qname", Options.gamename);
    while (!reply.equals("yes")) {
      logger.info(reply);
      if (reply.equals("no")) {
        logger.info("Game name already taken");
        JOptionPane.showMessageDialog(this, "Game name already taken");
      }
      Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
      if (Options.gamename == null) return;
      reply = sendCommandToMain("qname", Options.gamename);
    }

    //set the master password and join
    Options.masterpass = gamepass;
    if (Options.masterpass == null) return;
    Options.username = username;
    while (true) {

      if (Options.username == null) {
        Options.username = JOptionPane.showInputDialog("Type in your username for this game (" + MAX_USERNAME_LENGTH + " characters max)");
      }
      if (Options.username.length() < 1) continue;
      if (Options.username.length() > MAX_USERNAME_LENGTH) continue;

      break;
    }
    //Options.password = JOptionPane.showInputDialog("Type in your password for this game");
    Options.password = password;
    if (Options.password == null) return;

    //start game
    logger.info("starting game");
    Options.snailGame = true;
    setCurrentlyHighlightedItem(0);

    //New Game
    setIsNewload(false);
    setIsMapSelectScreen(true);
    setCurrentlyHighlightedItem(0);
    mapPage = 0;

    //load categories
    File[] dirs = new File("maps/").listFiles();
    Vector<String> v = new Vector<String>();
    int numcats = 0;
    for (File dir : dirs) {
      if (dir.isDirectory()) {
        v.add(dir.getName());
        numcats++;
      }
    }
    if (numcats == 0) {
      logger.info("NO MAP DIRECTORIES! QUITTING!");
    }
    mapCategories = new String[numcats];
    for (int i = 0; i < numcats; i++) {
      mapCategories[i] = v.get(i);
    }

    setCurrentlySelectedMapCategory(0);
    setCurrentlySelectedSubCategory(0);
    loadMapDisplayNames();
    mapPage = 0;
  }

  public void LaunchLoginGame(String gamename, String username, String password) {
    logger.info("Log in to Server Game");

    //try to connect to the server first to see that the user's URL is correct
    if (!tryToConnect()) return;

    //connect to the game
    if (gamename == null) return;
    Options.gamename = gamename;

    //Get user's name and password
    if (username == null) return;
    if (password == null) return;
    Options.username = username;
    Options.password = password;

    //try to connect
    String reply = sendCommandToMain("validup", Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + Options.version);
    logger.info(reply);
    if (!reply.equals("login successful")) {
      if (reply.equals("version mismatch")) JOptionPane.showMessageDialog(this, "Version Mismatch");
      else
        JOptionPane.showMessageDialog(this, "Problem logging in, either the username/password is incorrect or the game has ended");
      return;
    }

    //go to information screen
    Options.snailGame = true;
    setIsSnailInfoScreen(true);
    setIsNewload(false);
    setCurrentlyHighlightedItem(0);
    item2 = 0;

    refreshInfo();
  }

  public void LaunchJoinGame(String gamename, String masterpassword, String username, String password, int slotnumber) {
    logger.info("Join Server Game");

    //try to connect to the server first to see that the user's URL is correct
    if (!tryToConnect()) return;

    //connect to the game
    if (gamename == null) return;
    Options.gamename = gamename;

    //check the master password and get number of players and available slots
    if (masterpassword == null) return;
    Options.masterpass = masterpassword;

    //Get user's name, password, and slot
    if (username == null) return;
    Options.username = username;

    if (password == null) return;
    Options.password = password;

    String slot = Integer.toString(slotnumber);
    //Join
    String reply = sendCommandToMain("join", Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version);
    while (!reply.equals("join successful")) {
      logger.info(reply);
      if (reply.equals("no")) {
        logger.info("Game does not exist");
        Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
        if (Options.gamename == null) {
          setIsTitleScreen(true);
          setIsSnailInfoScreen(false);
          return;
        }
        Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
        if (Options.masterpass == null) {
        	setIsTitleScreen(true);
        	setIsSnailInfoScreen(false);
          return;
        }
      } else if (reply.equals("wrong password")) {
        logger.info("Incorrect Password");
        Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
        if (Options.gamename == null) {
        	setIsTitleScreen(true);
        	setIsSnailInfoScreen(false);
          return;
        }
        Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
        if (Options.masterpass == null) {
        	setIsTitleScreen(true);
        	setIsSnailInfoScreen(false);
          return;
        }
      } else if (reply.equals("out of range")) {
        logger.info("Army choice out of range or invalid");
        slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
        if (slot == null) {
        	setIsTitleScreen(true);
        	setIsSnailInfoScreen(false);
          return;
        }
      } else if (reply.equals("slot taken")) {
        logger.info("Army choice already taken");
        slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
        if (slot == null) {
        	setIsTitleScreen(true);
        	setIsSnailInfoScreen(false);
          return;
        }
      } else {
        logger.info("Other problem");
        JOptionPane.showMessageDialog(this, "Version Mismatch");
        Options.snailGame = false;
        setIsTitleScreen(true);
        setIsSnailInfoScreen(false);
        return;
      }
      refreshInfo();
      reply = sendCommandToMain("join", Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version);
    }

    //go to information screen
    Options.snailGame = true;
    setIsSnailInfoScreen(true);
    setIsNewload(false);
    setCurrentlyHighlightedItem(0);
    item2 = 0;

    refreshInfo();
  }

  public void setNewLoad() {
	setIsTitleScreen(false);
	setIsNewload(true);
    this.repaint();
  }
  
  private class refreshListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      refreshInfo();
    }
  }

  private Map getMap(int item) {
    return filteredMaps.get(mapPage * NUM_VISIBLE_ROWS + item);
  }

  private boolean isMapVisible(int item) {
    return mapPage * NUM_VISIBLE_ROWS + item < filteredMaps.size();
  }

  private String getFileName(int item) {
    return filenames[mapPage * NUM_VISIBLE_ROWS + item];
  }

  private boolean isOverLastPage(int mapPage) {
    return mapPage > filteredMaps.size() / NUM_VISIBLE_ROWS ||
            (mapPage == filteredMaps.size() / NUM_VISIBLE_ROWS && filteredMaps.size() % NUM_VISIBLE_ROWS == 0);
  }

  public boolean isOptionsScreen() {
	return isOptionsScreen;
  }

  public void setIsOptionsScreen(boolean options) {
	  this.isOptionsScreen = options;
  }

  public boolean isChooseNewGameTypeScreen() {
	  return isChooseNewGameTypeScreen;
  }

  public void setIsNewload(boolean isNewGameTypeScreen) {
	this.isChooseNewGameTypeScreen = isNewGameTypeScreen;
  }

  public boolean isMapSelectScreen() {
	return isMapSelectScreen;
  }

  public void setIsMapSelectScreen(boolean mapSelect) {
	this.isMapSelectScreen = mapSelect;
  }

  public boolean isCOselectScreen() {
	return isCOselectScreen;
  }

  public void setIsCOselectScreen(boolean coSelect) {
	isCOselectScreen = coSelect;
  }

  public boolean isBattleOptionsScreen() {
	return isBattleOptionsScreen;
  }

  public void setIsBattleOptionsScreen(boolean battleOptions) {
	this.isBattleOptionsScreen = battleOptions;
  }

  public boolean isKeyMappingScreen() {
	return isKeyMappingScreen;
  }

  public void setIsKeymappingScreen(boolean keymap) {
	this.isKeyMappingScreen = keymap;
  }

  public boolean isSnailInfoScreen() {
	return isSnailInfoScreen;
  }

  public void setIsSnailInfoScreen(boolean snailinfo) {
	this.isSnailInfoScreen = snailinfo;
  }

  public boolean isInfoScreen() {
	  return isInfoScreen;
  }

  public void setIsInfoScreen(boolean info) {
	this.isInfoScreen = info;
  }

  public boolean isTitleScreen() {
	  return isTitleScreen;
  }

  public void setIsTitleScreen(boolean title) {
	  this.isTitleScreen = title;
  }
  
  public boolean isSideSelect() {
	return isSideSelect;
  }

  public void setSideSelect(boolean isSideSelect) {
	  this.isSideSelect = isSideSelect;
  }

private void setCurrentlySelectedSubCategory(int currentlySelectedSubCategory) {
	this.currentlySelectedSubCategory = currentlySelectedSubCategory;
}

private int getCurrentlySelectedSubCategory() {
	return currentlySelectedSubCategory;
}

private void setCurrentlySelectedMapCategory(int currentMapCategory) {
	this.currentMapCategory = currentMapCategory;
}

private int getCurrentMapCategory() {
	return currentMapCategory;
}

private void setCurrentlyHighlightedItem(int currentlyHighlightedItem) {
	this.currentlyHighlightedItem = currentlyHighlightedItem;
}

private int getCurrentlyHighlightedItem() {
	return currentlyHighlightedItem;
}  
}

