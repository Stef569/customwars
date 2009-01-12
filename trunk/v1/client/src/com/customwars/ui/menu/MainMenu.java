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
import com.customwars.state.NetworkingManager;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.*;
import com.customwars.util.GuiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MainMenu extends JComponent {
	private static final String	TEMPORARYMAP_MAP_FILENAME		= "temporarymap.map";
	private static final String	TEMPORARYSAVE_SAVE_FILENAME	= "temporarysave.save";

	private static String				ABSOLUTE_TEMP_FILENAME			= "";
	private final static Logger	logger											= LoggerFactory.getLogger(MainMenu.class);

	private static final int		MAP_EDITOR									= 1;
	private static final int		OPTION_MENU									= 2;

	// GUI
	private static final int		NUM_VISIBLE_ROWS						= 12;
	private static final Font		DEFAULT_FONT								= new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	private BufferedImage				bufferedImg;
	private int									scaleMuliplier;
	private JFrame							parentJFrame;
	private KeyControl					keycontroller;
	private MouseControl				mousecontroller;
	private String[]						mapCategories;
	private Battle							miniMapBattlePreview;
	private MapLoader						mapLoader										= new MapLoader();
	private List<Map>						maps												= mapLoader.loadAllValidMaps();
	private List<Map>						filteredMaps;
	private String[]						allMapFilenames							= mapLoader.getFileNames();

	private CO[][]							armyArray										= new CO[8][14];
	public MenuSession					sess												= new MenuSession();
	public NetworkingManager		networkingManager;
	private static final int		MAX_USERNAME_LENGTH					= 12;

  public MainMenu(JFrame parentJFrame) {
		// makes the panel opaque, and thus visible
		this.setOpaque(true);
		this.parentJFrame = parentJFrame;

		String fileSysLocation = ResourceLoader.properties.getProperty("saveLocation");
		ABSOLUTE_TEMP_FILENAME = fileSysLocation + TEMPORARYMAP_MAP_FILENAME;

		sess.setCurrentCursorXposition(0);
		sess.setCurrentCursorYposition(0);
		sess.setCurrentlyHighlightedItem(0);
		sess.setCurrentlyHighlightedItem2(0);
		logger.info("Started through Main menu");

		scaleMuliplier = 1;

		sess.setIsTitleScreen(true);
		sess.setIsOptionsScreen(false);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setIsMapSelectScreen(false);
		sess.setIsCOselectScreen(false);
		sess.setIsInfoScreen(false);
		sess.setIsBattleOptionsScreen(false);
		sess.setIsKeymappingScreen(false);
		Options.snailGame = false;
		sess.setIsSnailInfoScreen(false);

		keycontroller = new KeyControl();
		mousecontroller = new MouseControl();
		parentJFrame.addKeyListener(keycontroller);
		parentJFrame.addMouseListener(mousecontroller);
		parentJFrame.addMouseMotionListener(mousecontroller);

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
		networkingManager = new NetworkingManager();
	}

	// called in response to this.repaint();
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = createGraphics2D(getSize().width, getSize().height);
		g2.scale(scaleMuliplier, scaleMuliplier);
		drawScreen(g2);
		g2.dispose();
		g.drawImage(bufferedImg, 0, 0, this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(480 * scaleMuliplier, 320 * scaleMuliplier);
	}

	// makes a Graphics2D object of the given size
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
		if (sess.isTitleScreen()) drawTitleScreen(graphics2D, sess.getCurrentlyHighlightedItem());
		if (sess.isMapSelectScreen()) drawMapSelectScreen(graphics2D);
		if (sess.isCOselectScreen()) drawCOSelectScreen(graphics2D);
		if (sess.isOptionsScreen()) drawOptionsScreen(graphics2D);
		if (sess.isChooseNewGameTypeScreen()) drawNewLoadScreen(graphics2D);
		if (sess.isSideSelect()) drawSideSelectScreen(graphics2D);
		if (sess.isBattleOptionsScreen()) drawBattleOptionsScreen(graphics2D);
		if (sess.isKeyMappingScreen()) drawKeymapScreen(graphics2D);
		if (sess.isSnailInfoScreen()) drawServerInfoScreen(graphics2D);

		// causes problems with animated gifs
		this.repaint();
	}

	public void drawBackground(Graphics2D g) {
		g.drawImage(MainMenuGraphics.getBackground(), 0, 0, this);
	}

	public void drawTitleScreen(Graphics2D graphics2D, int highlightedItem) {
		graphics2D.drawImage(MainMenuGraphics.getTitleBackground(), 0, 0, this);

		switch (highlightedItem) {
			case 0:
				graphics2D.drawImage(MainMenuGraphics.getNewGame(true), 0, 0, this);
				graphics2D.drawImage(MainMenuGraphics.getMaps(false), 0, 0, this);
				graphics2D.drawImage(MainMenuGraphics.getOptions(false), 0, 0, this);
				break;

			case 1:
				graphics2D.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, this);
				graphics2D.drawImage(MainMenuGraphics.getMaps(true), 0, 0, this);
				graphics2D.drawImage(MainMenuGraphics.getOptions(false), 0, 0, this);
				break;

			case 2:
				graphics2D.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, this);
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

		graphic2D.drawString(mapCategories[sess.getCurrentlySelectedMapCategory()], MainMenuGraphics.MAPSELECT_CATEGORY_X, MainMenuGraphics.MAPSELECT_CATEGORY_Y);

		for (int item = 0; item < NUM_VISIBLE_ROWS; item++) {
			if (isMapVisible(item)) {
				String fullMapName = getMap(item).getName();
				String fixedMapName = GuiUtil.fitLine(fullMapName, 148, graphic2D);
				graphic2D.drawString(fixedMapName, 10, 68 + item * 21);
			}
		}

		graphic2D.setColor(Color.red);
		graphic2D.drawRect(10, 50 + sess.getCurrentlyHighlightedItem() * 21, 148, 19);

		if (filteredMaps.size() != 0) {
			graphic2D.setColor(Color.black);
			graphic2D.drawString(getMap(sess.getCurrentlyHighlightedItem()).getName(), 180, 60);
			graphic2D.setFont(MainMenuGraphics.getH1Font());
			graphic2D.drawString("Mapmaker: " + getMap(sess.getCurrentlyHighlightedItem()).getName(), 180, 245);
			graphic2D.setFont(DEFAULT_FONT);
			graphic2D.drawString(getMap(sess.getCurrentlyHighlightedItem()).getDescription(), 180, 265);
		}

		graphic2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		graphic2D.setColor(new Color(7, 66, 97));
		graphic2D.fillRoundRect(180, 275, 280, 40, 20, 20);
		graphic2D.setColor(Color.WHITE);
		graphic2D.setFont(MainMenuGraphics.getH1Font());
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 205 + 16, 284, 221 + 16, 316, 0, TerrType.getYIndex(TerrType.CITY), 16, TerrType.getYIndex(TerrType.CITY) + 32,
				eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[0], 205, 300);
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 247 + 16, 284, 263 + 16, 316, 0, TerrType.getYIndex(TerrType.BASE), 16, TerrType.getYIndex(TerrType.BASE) + 32,
				eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[1], 247, 300);
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 289 + 16, 284, 305 + 16, 316, 0, TerrType.getYIndex(TerrType.PORT), 16, TerrType.getYIndex(TerrType.PORT) + 32,
				eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[2], 289, 300);
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 331 + 16, 284, 347 + 16, 316, 0, TerrType.getYIndex(TerrType.AIRPORT), 16, TerrType.getYIndex(TerrType.AIRPORT) + 32,
				eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[3], 331, 300);
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 373 + 16, 284, 389 + 16, 316, 0, TerrType.getYIndex(TerrType.COM_TOWER), 16,
				TerrType.getYIndex(TerrType.COM_TOWER) + 32, eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[4], 373, 300);
		graphic2D.drawImage(TerrainGraphics.getColoredSheet(0), 415 + 16, 284, 431 + 16, 316, 0, TerrType.getYIndex(TerrType.PIPE_STATION), 16, TerrType
				.getYIndex(TerrType.PIPE_STATION) + 32, eventListener);
		graphic2D.drawString("" + sess.getPropertyTypesOnSelectedMap()[5], 415, 300);

		graphic2D.setColor(new Color(7, 66, 97));
		graphic2D.fillRoundRect(180, 5, 280, 40, 20, 20);
		graphic2D.setColor(Color.white);
		graphic2D.setFont(MainMenuGraphics.getH1Font());

		switch (sess.getCurrentlySelectedSubCategory()) {
			case 0:
				MainMenuGraphics.drawCategories_allSelected(graphic2D);
				break;
			case 1:
				MainMenuGraphics.drawCategories_2playerSelected(graphic2D);
				break;
			case 2:
				MainMenuGraphics.drawCategories_3playerSelected(graphic2D);
				break;
			case 3:
				MainMenuGraphics.drawCategories_4playerSelected(graphic2D);
				break;
			case 4:
				MainMenuGraphics.drawCategories_5playerSelected(graphic2D);
				break;
			case 5:
				MainMenuGraphics.drawCategories_6playersSelected(graphic2D);
				break;
			case 6:
				MainMenuGraphics.drawCategories_7playerSelected(graphic2D);
				break;
			case 7:
				MainMenuGraphics.drawCategories_8PlayerSelected(graphic2D);
				break;
			case 8:
				MainMenuGraphics.drawCategories_9playerSelected(graphic2D);
				break;
			case 9:
				MainMenuGraphics.drawCategories_10playerSelected(graphic2D);
				break;
		}

		drawMiniMap(graphic2D, 180, 65);
	}

	public void drawMiniMap(Graphics2D g, int x, int y) {
		if (filteredMaps.size() != 0) {
			Image minimap = MiscGraphics.getMinimap();
			Map map = miniMapBattlePreview.getMap();

			for (int i = 0; i < map.getMaxCol(); i++) {
				for (int j = 0; j < map.getMaxRow(); j++) {
					// draw terrain
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

					// draw units
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
		sess.setBackGlide(sess.getBackGlide() + 1);
		if (sess.getBackGlide() > 640 * 2) {
			sess.setBackGlide(0);
		}
		g.drawImage(MiscGraphics.getIntelBackground(), 0, -sess.getBackGlide() / 2, this);
		g.drawImage(MiscGraphics.getIntelBackground(), 0, 640 - sess.getBackGlide() / 2, this);

		int offset = 0;
		if (sess.isAltcostume()) offset = 225;
		int offset2 = 0;
		if (sess.isMainaltcostume()) offset2 = 225;

		// Layout
		g.drawImage(MainMenuGraphics.getCOLayout(sess.getSelectedArmyAllegiance()), 0, 52, this);
		g.drawImage(MainMenuGraphics.getCOBanner(), 0, 1, this);
		for (int i = 0; i < 8; i++) {
			if (i == sess.getSelectedArmyAllegiance())
				g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, 0, this);
			else
				g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, -12, this);
		}
		g.drawImage(MainMenuGraphics.getHQBG(), 2, 61, 2 + 156, 61 + 279, 244 * sess.getSelectedArmyAllegiance(), 0, 244 * sess.getSelectedArmyAllegiance() + 244, 279, this);

		// Draw CO framework
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 3; i++) {
				g.drawImage(MainMenuGraphics.getCOSlot(sess.getSelectedArmyAllegiance()), 2 + i * 52, 61 + j * 52, this);
			}
		}
		g.drawImage(MainMenuGraphics.getNoCO(), 2, 61, this);

		// Draw COs
		for (int i = 1; i < 15; i++) {
			CO current = armyArray[sess.getSelectedArmyAllegiance()][i - 1];
			if (current != null) {

				g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 2 + i % 3 * 52, 61 + i / 3 * 52, 2 + i % 3 * 52 + 48, 61 + i / 3 * 52 + 48, offset, 350, 48 + offset, 398,
						this);
			} else {
				break;
			}
		}

		// Draw Cursor
		if (sess.getNumCOs() % 2 == 0)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
		g.drawRect(2 + sess.getCurrentCursorXposition() * 52, 61 + sess.getCurrentCursorYposition() * 52, 48, 48);

		// Draw first CO if selecting second CO
		if (sess.getNumCOs() % 2 == 1) {
			g.drawImage(MiscGraphics.getCOSheet(sess.getCoSelections()[sess.getNumCOs() - 1]), 166, 210, 166 + 32, 210 + 12, 144 + offset2, 350, 144 + offset2 + 32, 350 + 12, this);
			g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, (sess.getCoSelections()[sess.getNumCOs() - 1]) * 15, 50,
					(sess.getCoSelections()[sess.getNumCOs() - 1]) * 15 + 15, this);
		}

		// Draw current CO Info
		CO current = null;

		if (sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1 > -1)
			current = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];

		if (current != null) {
			sess.setGlide(sess.getGlide() + 1);
			g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 339 + (int) (100 * Math.pow(0.89, sess.getGlide())), 44, 339 + 225 + (int) (100 * Math.pow(0.89, sess
					.getGlide())), 44 + 350, offset, 0, offset + 225, 350, this);
			g.drawImage(MainMenuGraphics.getCOName(), 170, 70, 170 + 50, 70 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
			if (sess.getNumCOs() % 2 == 1) {
				g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 166, 226, 166 + 32, 226 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, this);
				g.drawImage(MainMenuGraphics.getCOName(), 199, 226, 199 + 50, 226 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
			} else {
				g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 166, 210, 166 + 32, 210 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, this);
				g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, current.getId() * 15, 50, current.getId() * 15 + 15, this);
			}
		}

		if (sess.getNumCOs() / 2 < 9)
			g.drawImage(MainMenuGraphics.getPlayerNumber(sess.getSelectedArmyAllegiance()), 293, 195, 293 + 15, 195 + 10, sess.getNumCOs() / 2 * 15, 0, sess.getNumCOs() / 2 * 15 + 15,
					10, this);
		else
			g.drawImage(MainMenuGraphics.getPlayerNumber(sess.getSelectedArmyAllegiance()), 293, 195, 293 + 30, 195 + 10, sess.getNumCOs() / 2 * 15, 0, sess.getNumCOs() / 2 * 15 + 30,
					10, this);

		g.setColor(Color.black);
		g.setFont(DEFAULT_FONT);

		if (current != null) {
			int k;

			for (k = 0; k < ((COList.getListing()[COList.getIndex(current)].getIntel().length() / 36) + 1); k++) {// As
				// long
				// as
				// k
				// is
				// shorter
				// than
				// the length, in characters,
				// of the bio divided by 40,
				// incremented by one.
				if (COList.getListing()[COList.getIndex(current)].getIntel().length() - (k + 1) * 36 >= 0) { // Is
					// there
					// more
					// than
					// 40
					// characters
					// left
					// in
					// the bio?
					// Does this intrude upon the 'sacred space' that is the "Side: Main"
					// info?"
					// Draw the substring - 40 characters from the last area.
					g.drawString(COList.getListing()[COList.getIndex(current)].getIntel().substring(k * 36, (k + 1) * 36), 170, 98 + k * 15);

				} else // If there is less than 40 characters left...
				{
					// Avoiding info space.
					// Drawing the rest of the substring.
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
		if (sess.getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
		g.drawString("Music", 10, 20);
		g.setColor(Color.black);
		if (Options.isMusicOn())
			g.drawString("On", 80, 20);
		else
			g.drawString("Off", 80, 20);

		if (sess.getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
		g.drawString("Random Numbers", 10, 40);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
		g.drawString("Balance Mode", 10, 60);
		g.setColor(Color.black);
		if (Options.isBalance())
			g.drawString("On", 120, 60);
		else
			g.drawString("Off", 120, 60);

		if (sess.getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
		g.drawString("Set IP", 10, 80);
		g.setColor(Color.black);
		g.drawString(Options.getDisplayIP(), 60, 80);

		if (sess.getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
		g.drawString("Autosave", 10, 100);
		g.setColor(Color.black);
		if (Options.isAutosaveOn())
			g.drawString("On", 120, 100);
		else
			g.drawString("Off", 120, 100);

		if (sess.getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
		g.drawString("Record Replay", 10, 120);
		g.setColor(Color.black);
		if (Options.isRecording())
			g.drawString("On", 130, 120);
		else
			g.drawString("Off", 130, 120);

		if (sess.getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
		g.drawString("Cursor", 10, 140);
		g.setColor(Color.black);
		g.drawImage(MiscGraphics.getCursor(), 70, 120, this);

		if (sess.getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
		g.drawString("Remap Keys", 10, 160);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
		String bbi = "On";
		if (!Options.battleBackground) bbi = "Off";
		g.drawString("Battle Background Image " + bbi, 10, 180);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 9) g.setColor(Color.red);
		g.drawString("Snail Mode Server: " + Options.getServerName(), 10, 200);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
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
		if (sess.getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
		g.drawString("Main Screen CO: " + COList.getListing()[Options.getMainCOID()].getName(), 10, 240);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
		g.drawString("Sound Effects: ", 10, 260);
		if (!Options.isSFXOn())
			g.drawString("Off", 130, 260);
		else
			g.drawString("On", 130, 260);
		g.setColor(Color.black);
		// Shows current terrain tileset
		if (sess.getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
		g.drawString("Terrain Tileset: ", 10, 280);
		if (Options.getSelectedTerrain() == 0)
			g.drawString("CW", 220, 280);
		else if (Options.getSelectedTerrain() == 1)
			g.drawString("AWDS", 220, 280);
		else if (Options.getSelectedTerrain() == 2) g.drawString(Options.getCustomTerrainString(), 220, 280);
		g.setColor(Color.black);

		// Shows current Urban tileset
		if (sess.getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
		g.drawString("Urban Tileset: ", 10, 300);
		if (Options.getSelectedUrban() == 0)
			g.drawString("CW", 220, 300);
		else if (Options.getSelectedUrban() == 1)
			g.drawString("AWDS", 220, 300);
		else if (Options.getSelectedUrban() == 2) g.drawString(Options.getCustomUrbanString(), 220, 300);
		g.setColor(Color.black);

		// Shows current HQ tileset
		// Shows current Urban tileset
		if (sess.getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
		g.drawString("HQ Tileset: ", 10, 320);
		if (Options.getSelectedHQ() == 0)
			g.drawString("CW", 220, 320);
		else if (Options.getSelectedHQ() == 1)
			g.drawString("AWDS", 220, 320);
		else if (Options.getSelectedHQ() == 2) g.drawString(Options.getCustomHQString(), 220, 320);
		g.setColor(Color.black);
		if (sess.getCurrentlyHighlightedItem() == 16) g.setColor(Color.red);
		g.drawString("Use Default Login Info: ", 220, 20);
		if (Options.isDefaultLoginOn())
			g.drawString("On", 400, 20);
		else
			g.drawString("Off", 400, 20);
		g.setColor(Color.black);
		if (sess.getCurrentlyHighlightedItem() == 17) g.setColor(Color.red);
		g.drawString("Default Username/Password:", 220, 40);
		g.drawString(Options.getDefaultUsername() + " / " + Options.getDefaultPassword(), 220, 60);
		g.setColor(Color.black);
		if (sess.getCurrentlyHighlightedItem() == 18) g.setColor(Color.red);
		g.drawString("AutoRefresh:", 220, 80);
		if (Options.getRefresh())
			g.drawString("On", 350, 80);
		else
			g.drawString("Off", 350, 80);
		g.setColor(Color.black);

	}

	public void drawBattleOptionsScreen(Graphics2D g) {
		int textCol = 20;

		// Visibility
		g.setColor(Color.black);
		g.setFont(MainMenuGraphics.getH1Font());
		if (sess.getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
		g.drawString("Visibility", 10, textCol);
		g.setColor(Color.black);

		if (sess.getVisibility() == 0)
			g.drawString("Full", 120, textCol);
		else if (sess.getVisibility() == 1)
			g.drawString("Fog of War", 120, textCol);
		else
			g.drawString("Mist of War", 120, textCol);

		textCol += 20;

		// Weather
		if (sess.getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
		g.drawString("Weather", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().getWeatherType() == 0)
			g.drawString("Clear", 120, textCol);
		else if (sess.getBopt().getWeatherType() == 1)
			g.drawString("Rain", 120, textCol);
		else if (sess.getBopt().getWeatherType() == 2)
			g.drawString("Snow", 120, textCol);
		else if (sess.getBopt().getWeatherType() == 3)
			g.drawString("Sandstorm", 120, textCol);
		else if (sess.getBopt().getWeatherType() == 4) g.drawString("Random", 120, textCol);

		textCol += 20;

		// Funds
		if (sess.getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
		g.drawString("Funds", 10, textCol);
		g.setColor(Color.black);
		g.drawString(sess.getBopt().getFundsLevel() + "", 120, textCol);

		textCol += 20;

		// Starting Funds
		if (sess.getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
		g.drawString("Start Funds", 10, textCol);
		g.setColor(Color.black);
		g.drawString(sess.getBopt().getStartFunds() + "", 120, textCol);

		textCol += 20;

		// Turn Limit
		if (sess.getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
		g.drawString("Turn Limit", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().getTurnLimit() > 0)
			g.drawString(sess.getBopt().getTurnLimit() + "", 120, textCol);
		else
			g.drawString("Off", 120, textCol);

		textCol += 20;

		// Cap Limit
		if (sess.getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
		g.drawString("Capture Limit", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().getCapLimit() > 0)
			g.drawString(sess.getBopt().getCapLimit() + "", 120, textCol);
		else
			g.drawString("Off", 120, textCol);

		textCol += 20;

		// CO Powers
		if (sess.getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
		g.drawString("CO Powers", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().isCOP())
			g.drawString("On", 120, textCol);
		else
			g.drawString("Off", 120, textCol);

		textCol += 20;

		// Balance Mode
		if (sess.getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
		g.drawString("Balance Mode", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().isBalance())
			g.drawString("On", 120, textCol);
		else
			g.drawString("Off", 120, textCol);

		textCol += 20;

		// Record Replay?
		if (sess.getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
		g.drawString("Record Replay", 10, textCol);
		g.setColor(Color.black);
		if (sess.getBopt().isRecording())
			g.drawString("On", 130, textCol);
		else
			g.drawString("Off", 130, textCol);

		textCol += 20;

		// Unit Bans
		g.setColor(Color.black);
		Image isheet = UnitGraphics.getUnitImage(0, 0);
		Image usheet = UnitGraphics.getUnitImage(2, 0);
		for (int i = 0; i < 2; i++) {
			g.drawImage(isheet, 10 + i * 16, textCol - 16, 26 + i * 16, textCol, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, this);
			if (sess.getBopt().isUnitBanned(i)) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				g.fillRect(10 + i * 16, textCol - 16, 16, 16);
				g.setComposite(AlphaComposite.SrcOver);
			}
		}
		for (int i = 2; i < BaseDMG.NUM_UNITS; i++) {
			if (i < BaseDMG.NUM_UNITS / 2) {
				g.drawImage(usheet, 10 + i * 16, textCol - 16, 26 + i * 16, textCol, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, this);
				if (sess.getBopt().isUnitBanned(i)) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
					g.fillRect(10 + i * 16, textCol - 16, 16, 16);
					g.setComposite(AlphaComposite.SrcOver);
				}
			} else {
				g.drawImage(usheet, 10 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 4, 26 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 20, 0, UnitGraphics.findYPosition(i, 0), 16,
						UnitGraphics.findYPosition(i, 0) + 16, this);
				if (sess.getBopt().isUnitBanned(i)) {
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
					g.fillRect(10 + (i - BaseDMG.NUM_UNITS / 2) * 16, textCol + 4, 16, 16);
					g.setComposite(AlphaComposite.SrcOver);
				}
			}
		}
		if (sess.getCurrentlyHighlightedItem() == 9) {
			g.setColor(Color.red);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g.fillRect(10 + (sess.getCurrentCursorXposition() % (BaseDMG.NUM_UNITS / 2)) * 16, textCol - 16 + 20 * sess.getCurrentCursorYposition(), 16, 16);
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(Color.black);
		}
		textCol += 40;

		if (sess.getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
		g.drawString("Snow Chance", 10, textCol);
		g.setColor(Color.black);
		g.drawString(String.valueOf(sess.getBopt().getSnowChance()) + "%", 165, textCol);
		textCol += 20;

		if (sess.getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
		g.drawString("Rain Chance", 10, textCol);
		g.setColor(Color.black);
		g.drawString(String.valueOf(sess.getBopt().getRainChance()) + "%", 165, textCol);
		textCol += 20;

		if (sess.getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
		g.drawString("Sandstorm Chance", 10, textCol);
		g.setColor(Color.black);
		g.drawString(String.valueOf(sess.getBopt().getSandChance()) + "%", 165, textCol);
		textCol -= 40;

		if (sess.getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
		g.drawString("Min Weather Duration", 210, textCol);
		g.setColor(Color.black);
		g.drawString(String.valueOf(sess.getBopt().getMinWTime()) + " Days", 380, textCol);
		textCol += 20;

		if (sess.getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
		g.drawString("Max Weather Duration", 210, textCol);
		g.setColor(Color.black);
		g.drawString(String.valueOf(sess.getBopt().getMaxWTime()) + " Days", 380, textCol);
		textCol += 20;

		if (sess.getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
		g.drawString("Weather Start", 210, textCol);
		g.setColor(Color.black);
		g.drawString("Day: " + String.valueOf(sess.getBopt().getMinWDay()), 380, textCol);
		textCol += 20;
	}

	public void drawKeymapScreen(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(new Font("SansSerif", Font.BOLD, 12));

		if (sess.getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);

		g.drawString("Up-" + KeyEvent.getKeyText(Options.up), 10, 14);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
		g.drawString("Down-" + KeyEvent.getKeyText(Options.down), 10, 28);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
		g.drawString("Left-" + KeyEvent.getKeyText(Options.left), 10, 42);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
		g.drawString("Right-" + KeyEvent.getKeyText(Options.right), 10, 56);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
		g.drawString("A Button-" + KeyEvent.getKeyText(Options.akey), 10, 70);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
		g.drawString("B Button-" + KeyEvent.getKeyText(Options.bkey), 10, 84);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
		g.drawString("Page Up-" + KeyEvent.getKeyText(Options.pgup), 10, 98);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
		g.drawString("Page Down-" + KeyEvent.getKeyText(Options.pgdn), 10, 112);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 8) g.setColor(Color.red);
		g.drawString("<-" + KeyEvent.getKeyText(Options.altleft), 10, 126);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 9) g.setColor(Color.red);
		g.drawString(">-" + KeyEvent.getKeyText(Options.altright), 10, 140);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 10) g.setColor(Color.red);
		g.drawString("Menu-" + KeyEvent.getKeyText(Options.menu), 10, 154);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 11) g.setColor(Color.red);
		g.drawString("Minimap-" + KeyEvent.getKeyText(Options.minimap), 10, 168);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 12) g.setColor(Color.red);
		g.drawString("Constant Mode-" + KeyEvent.getKeyText(Options.constmode), 10, 182);
		g.setColor(Color.black);

		// SECOND ROW

		if (sess.getCurrentlyHighlightedItem() == 13) g.setColor(Color.red);
		g.drawString("Delete Unit-" + KeyEvent.getKeyText(Options.delete), 130, 14);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 14) g.setColor(Color.red);
		g.drawString("Terrain Menu-" + KeyEvent.getKeyText(Options.tkey), 130, 28);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 15) g.setColor(Color.red);
		g.drawString("Side Menu-" + KeyEvent.getKeyText(Options.skey), 130, 42);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 16) g.setColor(Color.red);
		g.drawString("Unit Menu-" + KeyEvent.getKeyText(Options.ukey), 130, 56);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 17) g.setColor(Color.red);
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
		if (sess.getCurrentlyHighlightedItem() == 0) g.setColor(Color.red);
		g.drawString("New", 15, 30);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 1) g.setColor(Color.red);
		g.drawString("Load", 15, 54);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 2) g.setColor(Color.red);
		g.drawString("Network Game", 15, 78);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 3) g.setColor(Color.red);
		g.drawString("Load Replay", 15, 102);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 4) g.setColor(Color.red);
		g.drawString("Create New Server Game", 15, 126);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 5) g.setColor(Color.red);
		g.drawString("Join Server Game", 15, 150);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 6) g.setColor(Color.red);
		g.drawString("Login to Server Game", 15, 174);
		g.setColor(Color.black);

		if (sess.getCurrentlyHighlightedItem() == 7) g.setColor(Color.red);
		g.drawString("Open Online Lobby", 15, 198);
		g.setColor(Color.black);
		// Draw CO at the main menu
		// Draw COs
		sess.setGlide(sess.getGlide() + 1);
		g.drawImage(MainMenuGraphics.getMainMenuCO(Options.getMainCOID()), 329 + (int) (100 * Math.pow(.95, sess.getGlide())), -5, 329 + 225 + (int) (100 * Math.pow(.95, sess
				.getGlide())), -5 + 350, offset, 0, offset + 225, 350, this);

		// draw description box
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g.setColor(new Color(7, 66, 97));
		g.fillRoundRect(180, 255, 280, 60, 20, 20);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 11));

		if (sess.getCurrentlyHighlightedItem() == 0) {
			g.drawString("Start a new game. This mode is primarily for", 190, 275);
			g.drawString("playing against a friend on the same computer.", 190, 290);
		} else if (sess.getCurrentlyHighlightedItem() == 1) {
			g.drawString("Continue where you started off from your", 190, 275);
			g.drawString("previous game.", 190, 290);
		} else if (sess.getCurrentlyHighlightedItem() == 2) {
			g.drawString("Connect via a friend's IP and enjoy an online ", 190, 275);
			g.drawString("hotseat game with him or her! Hamachi is ", 190, 290);
			g.drawString("suggested for the best connectivity results.", 190, 304);
		} else if (sess.getCurrentlyHighlightedItem() == 3) {
			g.drawString("Already finished a game and feel like reliving ", 190, 275);
			g.drawString("those moments of honour? ", 190, 290);
			g.drawString("Load the replay here!", 190, 304);
		} else if (sess.getCurrentlyHighlightedItem() == 4) {
			g.drawString("Start a new game on the CW server! If you ", 190, 275);
			g.drawString("don't have a friend to battle, you should make", 190, 290);
			g.drawString("an open game so anyone can join and play!", 190, 304);
		} else if (sess.getCurrentlyHighlightedItem() == 5) {
			g.drawString("Join a game on the CW server that's open!", 190, 275);
			g.drawString("All you need is the game name, a handle and", 190, 290);
			g.drawString("a password! Then you're all ready to play!", 190, 304);
		} else if (sess.getCurrentlyHighlightedItem() == 6) {
			g.drawString("Login to one of your current games!", 190, 275);
			g.drawString("Let's hope you're winning, at least.", 190, 290);
			g.drawString("Otherwise, what's the point to logging in?", 190, 304);
		}

	}

	public void drawSideSelectScreen(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(MainMenuGraphics.getH1Font());

		for (int i = 0; i < sess.getNumOfArmiesOnMap(); i++) {
			if (sess.getCurrentlyHighlightedItem() == i) g.setColor(Color.red);
			g.drawString("Army " + (i + 1), 10, 20 + 20 * i);
			g.drawString("Side " + sess.getSideSelections()[i], 70, 20 + 20 * i);
			g.setColor(Color.black);
		}
	}

	public void drawServerInfoScreen(Graphics2D g) {
		g.setFont(MainMenuGraphics.getH1Font());
		// chat screen
		g.setColor(Color.black);
		g.fillRect(0, 0, 480, 100);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(460, 0, 20, 100);
		g.setColor(Color.WHITE);
		g.fillRect(460, 0, 20, 20);
		g.fillRect(460, 80, 20, 20);
		g.setColor(Color.gray);
		if (sess.getCurrentlyHighlightedItem2() == 0) g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 100, 160, 20);
		g.setColor(Color.white);
		g.drawString("Syslog", 0, 120);
		g.setColor(Color.gray);
		if (sess.getCurrentlyHighlightedItem2() == 1) g.setColor(Color.DARK_GRAY);
		g.fillRect(160, 100, 160, 20);
		g.setColor(Color.white);
		g.drawString("Chat", 160, 120);
		g.setColor(Color.gray);
		g.fillRect(320, 100, 160, 20);
		g.setColor(Color.white);
		g.drawString("Send", 320, 120);
		// chat messages
		g.setColor(Color.white);
		for (int i = 0; i < 5; i++) {
			if (sess.getCurrentlyHighlightedItem2() == 0) {
				if (i + sess.getSyspos() < sess.getSyslog().length && i + sess.getSyspos() >= 0) g.drawString(sess.getSyslog()[i + sess.getSyspos()], 0, 18 + i * 20);
			} else {
				if (i + sess.getChatpos() < sess.getChatlog().length && i + sess.getChatpos() >= 0) g.drawString(sess.getChatlog()[i + sess.getChatpos()], 0, 18 + i * 20);
			}
		}

		// information
		g.setColor(Color.BLACK);
		g.drawString("Game Name: " + Options.gamename, 0, 140);
		g.drawString("Login Name: " + Options.username, 0, 160);
		g.drawString("Current day/turn: " + sess.getDay() + "/" + sess.getTurn(), 0, 180);
		for (int i = 0; i < sess.getUsernames().length; i++) {
			g.drawString(sess.getUsernames()[i], (i < 5) ? 0 : 120, 200 + (i % 5) * 20);
		}

		// actions
		g.setColor(Color.gray);
		if (sess.getCurrentlyHighlightedItem() == 0) g.setColor(Color.black);
		g.fillRect(240, 280, 240, 20);
		g.setColor(Color.white);
		g.drawString("Refresh", 340, 300);
		g.setColor(Color.gray);
		if (sess.getCurrentlyHighlightedItem() == 1) g.setColor(Color.black);
		g.fillRect(240, 300, 240, 20);
		g.setColor(Color.white);
		g.drawString("Play", 350, 320);
	}

	public void removeFromFrame() {
		parentJFrame.getContentPane().remove(this);
		parentJFrame.removeKeyListener(keycontroller);
		parentJFrame.removeMouseListener(mousecontroller);
		parentJFrame.removeMouseMotionListener(mousecontroller);
	}

	public void loadMiniMapPreview() {
		sess.getPropertyTypesOnSelectedMap()[0] = 0;
		sess.getPropertyTypesOnSelectedMap()[1] = 0;
		sess.getPropertyTypesOnSelectedMap()[2] = 0;
		sess.getPropertyTypesOnSelectedMap()[3] = 0;
		sess.getPropertyTypesOnSelectedMap()[4] = 0;
		sess.getPropertyTypesOnSelectedMap()[5] = 0;
		if (filteredMaps.size() != 0) {
			sess.setFilename(getFileName(sess.getCurrentlyHighlightedItem()));
			miniMapBattlePreview = new Battle(sess.getFilename());

			Map m = miniMapBattlePreview.getMap();
			for (int yy = 0; yy < m.getMaxRow(); yy++) {
				for (int xx = 0; xx < m.getMaxCol(); xx++) {
					int number = m.find(new Location(xx, yy)).getTerrain().getIndex();
					if (number > 9) {
						if (number == 10)
							sess.getPropertyTypesOnSelectedMap()[0]++;
						else if (number == 11)
							sess.getPropertyTypesOnSelectedMap()[1]++;
						else if (number == 13)
							sess.getPropertyTypesOnSelectedMap()[2]++;
						else if (number == 12)
							sess.getPropertyTypesOnSelectedMap()[3]++;
						else if (number == 14)
							sess.getPropertyTypesOnSelectedMap()[4]++;
						else if (number == 17) sess.getPropertyTypesOnSelectedMap()[5]++;
					}
				}
			}
		}
	}

	// loads the display names, filter on subCat, aka the playerCount
	private void loadMapDisplayNames() {
		if (maps == null) logger.warn("No maps loaded");
		filteredMaps.clear();

		for (Map map : maps) {
			if (sess.getCurrentlySelectedSubCategory() == 0 || sess.getCurrentlySelectedSubCategory() == map.getPlayerCount() - 1) {
				filteredMaps.add(map);
			}
		}
		loadMiniMapPreview();
	}

	public void pressedA() {
		if (sess.isTitleScreen()) {
			titleScreenActions();
		} else if (sess.isInfoScreen()) {
			sess.setIsInfoScreen(false);
		} else if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
			coSelectScreenActions();
		} else if (sess.isOptionsScreen()) {
			optionsScreenActions();
		} else if (sess.isChooseNewGameTypeScreen()) {
			parseSelectedNewGameTypeInput();
		} else if (sess.isSideSelect()) {
			sess.setSideSelect(false);
			sess.setIsBattleOptionsScreen(true);
			sess.setCurrentlyHighlightedItem(0);
		} else if (sess.isBattleOptionsScreen()) {
			startBattle();
		} else if (sess.isMapSelectScreen()) {
			mapSelectScreenActions();
		} else if (sess.isKeyMappingScreen()) {
			sess.setChooseKey(true);
		} else if (sess.isSnailInfoScreen()) {
			networkInfoScreenActions();
		}
	}

	private void startBattle() {
		if (sess.getCurrentlyHighlightedItem() == 9) {
			if (sess.getBopt().isUnitBanned(sess.getCurrentCursorXposition())) {
				sess.getBopt().setUnitBanned(false, sess.getCurrentCursorXposition());
			} else {
				sess.getBopt().setUnitBanned(true, sess.getCurrentCursorXposition());
			}
			return;
		}
		// Creates new instances of critical objects for the battle
		Battle b = new Battle(sess.getFilename(), sess.getCoSelections(), sess.getSideSelections(), sess.getAltSelections(), sess.getBopt());

		// Initialize a swing frame and put a BattleScreen inside
		parentJFrame.setSize(400, 400);
		removeFromFrame();
		BattleScreen bs = new BattleScreen(b, parentJFrame);
		parentJFrame.getContentPane().add(bs);
		parentJFrame.validate();
		parentJFrame.pack();

		// Start the mission
		GameSession.startMission(b, bs);
		// save the initial state for the replay if applicable
		if (sess.getBopt().isRecording()) GameSession.saveInitialState();
	}

	private void networkInfoScreenActions() {
		if (sess.getCurrentlyHighlightedItem() == 0) {
			// refresh
			logger.info("Refreshing");
			refreshInfo();
		} else if (sess.getCurrentlyHighlightedItem() == 1) {
			// play game
			logger.info("Play Game");
			// refresh first
			refreshInfo();

			String reply = null;
			String command = "canplay";
			String extra = Options.gamename + "\n" + Options.username + "\n" + Options.password;

			try {
				reply = networkingManager.sendCommandToMain(command, extra);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				reply = null;
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				reply = null;
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}

			logger.info(reply);
			if (reply.equals("permission granted")) {
				if (sess.getDay() == 1) {
					logger.info("Starting Game");
					// load map from server

					boolean fileGotten = false;

					try {
						fileGotten = networkingManager.getFile("dmap.pl", Options.gamename, TEMPORARYMAP_MAP_FILENAME);
					} catch (MalformedURLException e1) {
						logger.error("Bad URL " + Options.getServerName());
						JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
					} catch (IOException e2) {
						logger.error("Connection problem, unable to get file from server");
						JOptionPane.showMessageDialog(this, "Connection problem, unable to get file from server");
					}

					sess.setFilename(TEMPORARYMAP_MAP_FILENAME);

					// goto co select
					sess.setIsMapSelectScreen(false);
					sess.setIsCOselectScreen(true);
					sess.setIsSnailInfoScreen(false);

					// find number of armies, and thus COs
					try {
						DataInputStream read = new DataInputStream(new FileInputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" + sess.getFilename()));
						int maptype = read.readInt();
						if (maptype == -1) {
							read.readByte(); // skip name
							read.readByte(); // skip author
							read.readByte(); // skip description
							read.readInt();
							read.readInt();
							sess.setNumOfArmiesOnMap(read.readByte());
						} else {
							read.readInt();
							sess.setNumOfArmiesOnMap(read.readInt());
						}
					} catch (IOException exc) {
						logger.error("Couldn't read MAP file [" + sess.getFilename() + "]", exc);
					}
					sess.setNumCOs(0);
					sess.setCoSelections(new int[sess.getNumOfArmiesOnMap() * 2]);
					sess.setAltSelections(new boolean[sess.getNumOfArmiesOnMap() * 2]);
					if (sess.getTurn() != 1) {
						sess.setInsertNewCO(true);
					}
					return;
				}

				// load mission

				boolean fileGotten = false;
				try {
					fileGotten = networkingManager.getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
				} catch (MalformedURLException e1) {
					logger.error("Bad URL " + Options.getServerName());
					JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
				} catch (IOException e2) {
					logger.error("Connection problem, unable to get file from server");
					JOptionPane.showMessageDialog(this, "Connection problem, unable to get file from server");
				}

				String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
				// load mission
				Battle b = new Battle(new Map(30, 20));
				// Initialize a swing frame and put a BattleScreen inside
				parentJFrame.setSize(400, 400);
				removeFromFrame();
				BattleScreen bs = new BattleScreen(b, parentJFrame);
				parentJFrame.getContentPane().add(bs);
				parentJFrame.validate();
				parentJFrame.pack();

				// Start the mission
				GameSession.startMission(null, bs);
				GameSession.loadMission(loadFilename);
			}
		}
	}

	private void mapSelectScreenActions() {
		if (filteredMaps.size() != 0) {
			// New Game
			sess.setIsMapSelectScreen(false);
			sess.setIsCOselectScreen(true);
			sess.setFilename(getFileName(sess.getCurrentlyHighlightedItem()));
			String mapName = getMap(sess.getCurrentlyHighlightedItem()).getName();
			this.sess.setNumOfArmiesOnMap(getMap(sess.getCurrentlyHighlightedItem()).getPlayerCount());

			// New Snail Mode Game
			if (Options.snailGame) {
				sess.setIsCOselectScreen(false);
				sess.setIsSnailInfoScreen(true);
				sess.setCurrentlyHighlightedItem(0);
				sess.setCurrentlyHighlightedItem2(0);

				String comment = JOptionPane.showInputDialog("Type in a comment for your game");

				// get army that the player wants to join
				int joinnum = -1;
				while (joinnum < 1 || joinnum > sess.getNumOfArmiesOnMap()) {
					String t = JOptionPane.showInputDialog("What side do you want to join? Pick from 1-" + sess.getNumOfArmiesOnMap());
					if (t == null) {
						Options.snailGame = false;
						sess.setIsMapSelectScreen(false);
						sess.setIsTitleScreen(true);
						return;
					}
					joinnum = Integer.parseInt(t);
				}

				// register new game
				String command = "newgame";
				String extra = Options.gamename + "\n" + Options.masterpass + "\n" + sess.getNumOfArmiesOnMap() + "\n" + Options.version + "\n" + comment + "\n" + mapName + "\n"
						+ Options.username;
				String reply = null;

				try {
					reply = networkingManager.sendCommandToMain(command, extra);
				} catch (MalformedURLException e1) {
					logger.info("Bad URL " + Options.getServerName());
					JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
				} catch (IOException e2) {
					logger.error("Connection Problem during command " + command + " with information:\n" + extra);
					JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
				}

				while (!reply.equals("game created")) {
					logger.info(reply);
					if (reply.equals("no")) {
						logger.info("Game name taken");
						JOptionPane.showMessageDialog(this, "Game name taken");
						Options.gamename = JOptionPane.showInputDialog("Type in a new name for your game");
						if (Options.gamename == null) {
							return;
						}

						command = "newgame";
						extra = Options.gamename + "\n" + Options.masterpass + "\n" + sess.getNumOfArmiesOnMap() + "\n" + Options.version + "\n" + comment + "\n" + mapName + "\n"
								+ Options.username;
						try {
							reply = networkingManager.sendCommandToMain(command, extra);
						} catch (MalformedURLException e1) {
							logger.info("Bad URL " + Options.getServerName());
							JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
						} catch (IOException e2) {
							logger.error("Connection Problem during command " + command + " with information:\n" + extra);
							JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
						}

					} else {
						Options.snailGame = false;
						sess.setIsMapSelectScreen(false);
						sess.setIsTitleScreen(true);
						return;
					}
				}

				// upload map
				String script = "umap.pl";
				extra = Options.gamename;
				String file = sess.getFilename();

				try {
					String temp = networkingManager.sendFile(script, extra, file);
					logger.info(temp);
				} catch (MalformedURLException e1) {
					logger.info("Bad URL " + Options.getServerName());
					JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
				} catch (IOException e2) {
					logger.error("Connection Problem during command " + command + " with information:\n" + extra);
					JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
				}

				// Join Game
				extra = Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + joinnum + "\n" + Options.version;
				command = "join";

				try {
					reply = networkingManager.sendCommandToMain(command, extra);
				} catch (MalformedURLException e1) {
					logger.info("Bad URL " + Options.getServerName());
					JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
				} catch (IOException e2) {
					logger.error("Connection Problem during command " + command + " with information:\n" + extra);
					JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
				}

				logger.info(reply);
				refreshInfo();
				return;
			}

			sess.setNumCOs(0);
			sess.setCoSelections(new int[sess.getNumOfArmiesOnMap() * 2]);
			sess.setAltSelections(new boolean[sess.getNumOfArmiesOnMap() * 2]);
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

		switch (sess.getCurrentlyHighlightedItem()) {
			case CO_SELECT:
				startCOSelect = true;
				break;
			case LOAD_GAME:
				loadGame();
				break;
			case START_NETWORK_GAME:
				startCOSelect = true;
				Options.startNetwork();
				sess.setCurrentlyHighlightedItem(CO_SELECT);
				break;
			case LOAD_REPLAY:
				loadReplay();
				break;
			case CREATE_SERVER_GAME:
				createServerGame();
				startCOSelect = true;
				sess.setCurrentlyHighlightedItem(CO_SELECT);
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
			// New Game
			sess.setIsChooseNewGameTypeScreen(false);
			sess.setIsMapSelectScreen(true);
			sess.setCurrentlyHighlightedItem(CO_SELECT);
			sess.setMapPage(CO_SELECT);

			// load categories
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

			sess.setCurrentlySelectedMapCategory(CO_SELECT);
			sess.setCurrentlySelectedSubCategory(CO_SELECT);
			loadMapDisplayNames();
			sess.setMapPage(CO_SELECT);
		}
	}

	private void joinIRClobby() {
		parentJFrame.setVisible(false);
		FobbahLauncher.init(parentJFrame, this);
	}

	private void loginToServerGame() {
		int CO_SELECT = 0;
		int LOAD_GAME = 1;
		boolean cannotConnect = false;

		logger.info("Log in to Server Game");

		// try to connect to the server first to see that the user's URL is correct

		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// connect to the game
		Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
		if (Options.gamename == null) return;

		// Get user's name and password
		if (Options.isDefaultLoginOn()) {
			Options.username = Options.getDefaultUsername();
			Options.password = Options.getDefaultPassword();

			if (Options.username == null || Options.username.length() < LOAD_GAME || Options.username.length() > MAX_USERNAME_LENGTH) return;
		} else {
			Options.username = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)", JOptionPane.PLAIN_MESSAGE);
			if (Options.username == null) return;
			Options.password = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password?", JOptionPane.PLAIN_MESSAGE);
			if (Options.password == null) return;
		}

		// try to connect
		String command = "validup";
		String extra = Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + Options.version;
		String reply = null;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		logger.info(reply);
		if (!reply.equals("login successful")) {
			if (reply.equals("version mismatch"))
				JOptionPane.showMessageDialog(this, "Version Mismatch");
			else
				JOptionPane.showMessageDialog(this, "Problem logging in, either the username/password is incorrect or the game has ended");
			return;
		}

		// go to information screen
		Options.snailGame = true;
		sess.setIsSnailInfoScreen(true);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setCurrentlyHighlightedItem(CO_SELECT);
		sess.setCurrentlyHighlightedItem2(CO_SELECT);

		refreshInfo();
	}

	private void joinServerGame() {
		logger.info("Join Server Game");
		boolean cannotConnect = false;

		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// connect to the game
		Options.gamename = JOptionPane.showInputDialog(null, "Name of game:", "Join Game: Name", JOptionPane.PLAIN_MESSAGE);
		if (Options.gamename == null) return;

		// check the master password and get number of players and available slots
		Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
		if (Options.masterpass == null) return;

		// Get user's name, password, and slot
		if (Options.isDefaultLoginOn()) {
			Options.username = Options.getDefaultUsername();
			Options.password = Options.getDefaultPassword();

			if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH) return;
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
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setIsSnailInfoScreen(true);
		refreshInfo();
		if (!sess.isSnailInfoScreen()) {
			JOptionPane.showMessageDialog(this, "The game " + Options.gamename + " has ended");
			return;
		}
		String slot = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
		if (slot == null) {
			sess.setIsTitleScreen(true);
			sess.setIsSnailInfoScreen(false);
			return;
		}

		// Join
		String command = "join";
		String extra = Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version;
		String reply = null;
		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		while (!reply.equals("join successful")) {
			logger.info(reply);
			if (reply.equals("no")) {
				logger.info("Game does not exist");
				Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
				if (Options.gamename == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
				Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
				if (Options.masterpass == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("wrong password")) {
				logger.info("Incorrect Password");
				Options.gamename = JOptionPane.showInputDialog(null, "Name of game:", "Join Game: Name", JOptionPane.PLAIN_MESSAGE);
				if (Options.gamename == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
				Options.masterpass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);
				if (Options.masterpass == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("out of range")) {
				logger.info("Army choice out of range or invalid");
				slot = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
				if (slot == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("slot taken")) {
				logger.info("Army choice already taken");
				slot = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
				if (slot == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else {
				logger.info("Other problem");
				JOptionPane.showMessageDialog(this, "Version Mismatch");
				Options.snailGame = false;
				sess.setIsTitleScreen(true);
				sess.setIsSnailInfoScreen(false);
				return;
			}
			refreshInfo();
			try {
				reply = networkingManager.sendCommandToMain(command, extra);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}
		}

		// go to information screen
		int CO_SELECT = 0;
		Options.snailGame = true;
		sess.setIsSnailInfoScreen(true);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setCurrentlyHighlightedItem(CO_SELECT);
		sess.setCurrentlyHighlightedItem2(CO_SELECT);

		refreshInfo();
	}

	private void createServerGame() {
		logger.info("Create Server Game");
		boolean cannotConnect = false;
		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// find an unused name
		Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name", JOptionPane.PLAIN_MESSAGE);

		if (Options.gamename == null) {
			return;
		}

		String command = "qname";
		String extra = Options.gamename;
		String reply = null;
		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		while (!reply.equals("yes")) {
			logger.info(reply);
			if (reply.equals("no")) {
				logger.info("Game name already taken");
				JOptionPane.showMessageDialog(this, "Game name already taken");
			}
			Options.gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);
			if (Options.gamename == null) {
				return;
			}
			extra = Options.gamename;

			try {
				reply = networkingManager.sendCommandToMain(command, extra);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}

		}

		// set the master password and join
		Options.masterpass = JOptionPane.showInputDialog(null, "Master Password for your game:", "Network Game: Master Pass?", JOptionPane.PLAIN_MESSAGE);
		if (Options.masterpass == null) return;
		if (Options.isDefaultLoginOn()) {
			Options.username = Options.getDefaultUsername();
			Options.password = Options.getDefaultPassword();

			if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH) return;
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

		// start game
		logger.info("starting game");
		Options.snailGame = true;
	}

	private void loadReplay() {
		String tempSaveLocation = ResourceLoader.properties.getProperty("tempSaveLocation");
		String loadFilename = tempSaveLocation + "/temporarysave.save";

		// prompt for replay name
		logger.info("REPLAY MODE");
		// Load Replay
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
				// Initialize a swing frame and put a BattleScreen inside
				parentJFrame.setSize(400, 400);
				removeFromFrame();
				BattleScreen bs = new BattleScreen(b, parentJFrame);
				parentJFrame.getContentPane().add(bs);
				parentJFrame.validate();
				parentJFrame.pack();

				// Start the mission
				GameSession.startMission(null, bs);
				GameSession.loadReplay(loadFilename);
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
				// Initialize a swing frame and put a BattleScreen inside
				parentJFrame.setSize(400, 400);
				removeFromFrame();
				BattleScreen bs = new BattleScreen(b, parentJFrame);
				parentJFrame.getContentPane().add(bs);
				parentJFrame.validate();
				parentJFrame.pack();

				// Start the mission
				GameSession.startMission(null, bs);
				GameSession.loadMission(loadFilename);
			}
		}
	}

	private void coSelectScreenActions() {
		boolean nosecco = false;
		CO temp = null;
		if (sess.getCurrentCursorXposition() == 0 && sess.getCurrentCursorYposition() == 0 && sess.getNumCOs() % 2 == 1) {
			sess.getCoSelections()[sess.getNumCOs()] = -1;
			nosecco = true;
		} else {
			if (sess.getCurrentCursorXposition() == 0 && sess.getCurrentCursorYposition() == 0) return;
			temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
		}

		if (nosecco || temp != null && !(sess.getNumCOs() % 2 == 1 && COList.getIndex(temp) == sess.getCoSelections()[sess.getNumCOs() - 1])) {
			if (!nosecco) {
				sess.getCoSelections()[sess.getNumCOs()] = COList.getIndex(temp);
			}

			sess.getAltSelections()[sess.getNumCOs()] = sess.isAltcostume();
			sess.setMainaltcostume(sess.isAltcostume());
			sess.setAltcostume(false);
			sess.setNumCOs(sess.getNumCOs() + 1);
			sess.setCurrentCursorXposition(0);
			sess.setCurrentCursorYposition(0);

			if (Options.snailGame && sess.getNumCOs() == 2) {
				logger.info("Stop for snail game");
				if (sess.isInsertNewCO()) {
					// load mission
					boolean fileGotten = false;
					String script = "dsave.pl";
					String extra = Options.gamename;

					try {
						fileGotten = networkingManager.getFile(script, extra, TEMPORARYSAVE_SAVE_FILENAME);
					} catch (MalformedURLException e1) {
						logger.info("Bad URL " + Options.getServerName());
						JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
					} catch (IOException e2) {
						logger.error("Connection Problem during command " + script + " with information:\n" + extra);
						JOptionPane.showMessageDialog(this, "Connection Problem during command " + script + " with the following information:\n" + extra);
					}

					String loadFilename = TEMPORARYSAVE_SAVE_FILENAME;
					// load mission
					Battle b = new Battle(new Map(30, 20));
					// Initialize a swing frame and put a BattleScreen inside
					parentJFrame.setSize(400, 400);
					removeFromFrame();
					BattleScreen bs = new BattleScreen(b, parentJFrame);
					parentJFrame.getContentPane().add(bs);
					parentJFrame.validate();
					parentJFrame.pack();

					// Start the mission
					GameSession.startMission(null, bs);
					GameSession.loadMission(loadFilename);
					bs.getBattle().getArmy(sess.getTurn() - 1).setCO(bs.getBattle().getCO(sess.getCoSelections()[0]));
					bs.getBattle().getArmy(sess.getTurn() - 1).setAltCO(bs.getBattle().getCO(sess.getCoSelections()[1]));
				}
				// fill rest with single andys
				for (int i = 2; i < sess.getCoSelections().length; i++) {
					if (i % 2 == 0)
						sess.getCoSelections()[i] = 1;
					else
						sess.getCoSelections()[i] = 0;
				}
				for (int coSelection : sess.getCoSelections())
					logger.info("" + coSelection);
				logger.info("Number of COs: " + sess.getNumCOs());
				if (sess.getNumOfArmiesOnMap() > 2) {
					sess.setIsCOselectScreen(false);
					sess.setSideSelect(true);
					sess.setCurrentlyHighlightedItem(0);
					sess.setSideSelections(new int[sess.getNumOfArmiesOnMap()]);
					for (int i = 0; i < sess.getNumOfArmiesOnMap(); i++)
						sess.getSideSelections()[i] = i;
				} else {
					// no alliances allowed for 2 players
					sess.setSideSelections(new int[] { 0, 1 });
					sess.setIsCOselectScreen(false);
					sess.setIsBattleOptionsScreen(true);
					sess.setCurrentlyHighlightedItem(0);
				}
			}

			if (sess.getNumCOs() == sess.getNumOfArmiesOnMap() * 2) {
				logger.info("Total No of competing COs=[" + sess.getNumCOs() + "]  Armies=[" + sess.getNumOfArmiesOnMap() + "]");

				if (sess.getNumCOs() > 4) {
					sess.setIsCOselectScreen(false);
					sess.setSideSelect(true);
					sess.setCurrentlyHighlightedItem(0);
					sess.setSideSelections(new int[sess.getNumCOs() / 2]);
					for (int i = 0; i < sess.getNumCOs() / 2; i++)
						sess.getSideSelections()[i] = i;
				} else {
					// no alliances allowed for 2 players
					sess.setSideSelections(new int[] { 0, 1 });

					sess.setIsCOselectScreen(false);
					sess.setIsBattleOptionsScreen(true);
					sess.setCurrentlyHighlightedItem(0);
				}
			}
		}
	}

	private void optionsScreenActions() {
		if (sess.getCurrentlyHighlightedItem() == 0) {
			// Music On/Off
			if (Options.isMusicOn())
				Options.turnMusicOff();
			else
				Options.turnMusicOn();
		} else if (sess.getCurrentlyHighlightedItem() == 1) {
		} else if (sess.getCurrentlyHighlightedItem() == 2) {
			// Balance Mode On/Off
			if (Options.isBalance()) {
				Options.turnBalanceModeOff();
			} else {
				Options.turnBalanceModeOn();
			}
			sess.getBopt().setBalance(Options.isBalance());
		} else if (sess.getCurrentlyHighlightedItem() == 3) {
			// Change the IP address
			Options.setIP();
		} else if (sess.getCurrentlyHighlightedItem() == 4) {
			if (Options.isAutosaveOn())
				Options.setAutosave(false);

			else
				Options.setAutosave(true);
		} else if (sess.getCurrentlyHighlightedItem() == 5) {
			if (Options.isRecording())
				Options.setRecord(false);
			else
				Options.setRecord(true);
		} else if (sess.getCurrentlyHighlightedItem() == 7) {
			// remap keys
			sess.setIsOptionsScreen(false);
			sess.setIsKeymappingScreen(true);
			sess.setCurrentlyHighlightedItem(0);
		} else if (sess.getCurrentlyHighlightedItem() == 8) {
			Options.toggleBattleBackground();
		} else if (sess.getCurrentlyHighlightedItem() == 9) {
			// Change the IP address
			Options.setServer();
		} else if (sess.getCurrentlyHighlightedItem() == 10) {
			Options.incrementDefaultBans();
			sess.setBopt(new BattleOptions());
		} else if (sess.getCurrentlyHighlightedItem() == 13 && Options.getSelectedTerrain() == 2) {
			Options.setCustomTerrain();
		} else if (sess.getCurrentlyHighlightedItem() == 14 && Options.getSelectedUrban() == 2) {
			Options.setCustomUrban();
		} else if (sess.getCurrentlyHighlightedItem() == 15 && Options.getSelectedHQ() == 2) {
			Options.setCustomHQ();
		} else if (sess.getCurrentlyHighlightedItem() == 16) {
			Options.toggleDefaultLogin();
		} else if (sess.getCurrentlyHighlightedItem() == 17) {
			Options.setDefaultLogin();
		} else if (sess.getCurrentlyHighlightedItem() == 18) {
			Options.toggleRefresh();
		}
	}

	private void titleScreenActions() {
		if (sess.getCurrentlyHighlightedItem() == 0) {
			sess.setIsTitleScreen(false);
			sess.setIsChooseNewGameTypeScreen(true);
		} else if (sess.getCurrentlyHighlightedItem() == MAP_EDITOR) {
			startMapEditor();
		} else if (sess.getCurrentlyHighlightedItem() == OPTION_MENU) {
			sess.setIsTitleScreen(false);
			sess.setIsOptionsScreen(true);
			sess.setCurrentlyHighlightedItem(0);
		}
	}

	private void startMapEditor() {
		final int MAP_EDITOR_STARTUP_COLS = 30;
		final int MAP_EDITOR_STARTUP_ROWS = 20;
		logger.info("Map Editor");
		Map m = new Map(MAP_EDITOR_STARTUP_COLS, MAP_EDITOR_STARTUP_ROWS);
		Battle bat = new Battle(m);

		parentJFrame.setSize(400, 400);
		MapEditor me = new MapEditor(bat, parentJFrame);
		removeFromFrame();
		parentJFrame.getContentPane().add(me);
		parentJFrame.validate();
		parentJFrame.pack();
	}

	// try to connect to the server to see that the user's URL is correct

	public void pressedB() {
		if (sess.isInfoScreen()) {
			sess.setIsInfoScreen(false);
		} else if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
			if (sess.getNumCOs() == 0) {
				sess.setIsMapSelectScreen(true);
				sess.setIsCOselectScreen(false);
				sess.setCurrentlyHighlightedItem(0);
				sess.setSelectedArmyAllegiance(0);
				if (Options.isNetworkGame()) Options.stopNetwork();
				if (Options.snailGame) {
					sess.setIsMapSelectScreen(false);
					sess.setIsTitleScreen(true);
					Options.snailGame = false;
				}
			} else {
				sess.getCoSelections()[sess.getNumCOs()] = -1;
				sess.setNumCOs(sess.getNumCOs() - 1);
				sess.setCurrentCursorXposition(0);
				sess.setCurrentCursorYposition(0);
			}
		} else if (sess.isOptionsScreen()) {
			sess.setIsTitleScreen(true);
			sess.setIsOptionsScreen(false);
			sess.setCurrentlyHighlightedItem(0);
			if (Options.isNetworkGame()) Options.stopNetwork();
		} else if (sess.isChooseNewGameTypeScreen()) {
			sess.setIsTitleScreen(true);
			sess.setIsChooseNewGameTypeScreen(false);
			sess.setCurrentlyHighlightedItem(0);
			if (Options.isNetworkGame()) Options.stopNetwork();
		} else if (sess.isSideSelect()) {
			sess.setIsCOselectScreen(true);
			sess.setNumCOs(sess.getNumCOs() - 1);
			sess.setSideSelect(false);
			Options.snailGame = false; // not always needed, but doesn't hurt
			sess.setCurrentlyHighlightedItem(0);
			if (Options.isNetworkGame()) Options.stopNetwork();
		} else if (sess.isBattleOptionsScreen()) {
			if (sess.getNumCOs() > 4)
				sess.setSideSelect(true);
			else {
				sess.setNumCOs(sess.getNumCOs() - 1);
				sess.setIsCOselectScreen(true);
			}
			sess.setIsBattleOptionsScreen(false);
			Options.snailGame = false; // not always needed, but doesn't hurt
			sess.setCurrentlyHighlightedItem(0);
			sess.setCurrentCursorXposition(0);
			sess.setCurrentCursorYposition(0);
			if (Options.isNetworkGame()) Options.stopNetwork();
		} else if (sess.isMapSelectScreen()) {
			sess.setIsChooseNewGameTypeScreen(true);
			sess.setIsMapSelectScreen(false);
			Options.snailGame = false; // not always needed, but doesn't hurt
			sess.setCurrentlyHighlightedItem(0);
			if (Options.isNetworkGame()) Options.stopNetwork();
		} else if (sess.isKeyMappingScreen()) {
			sess.setIsKeymappingScreen(false);
			sess.setIsOptionsScreen(true);
			sess.setCurrentlyHighlightedItem(0);
		} else if (sess.isSnailInfoScreen()) {
			Options.snailGame = false;
			sess.setIsSnailInfoScreen(false);
			sess.setIsChooseNewGameTypeScreen(true);
			sess.setCurrentlyHighlightedItem(0);
		}
	}

	public void pressedPGDN() {
		if (sess.isMapSelectScreen()) {
            int nextPage = this.sess.getMapPage() + 1;
            sess.setMapPage(nextPage);
            sess.setCurrentlyHighlightedItem(0);

			loadMiniMapPreview();
		} else if (sess.isSnailInfoScreen()) {
			if (sess.getCurrentlyHighlightedItem2() == 0) {
				sess.setSyspos(sess.getSyspos() + 1);
				if (sess.getSyspos() > sess.getSyslog().length - 5) sess.setSyspos(sess.getSyslog().length - 5);
				if (sess.getSyspos() < 0) sess.setSyspos(0);
			} else if (sess.getCurrentlyHighlightedItem2() == 1) {
				sess.setChatpos(sess.getChatpos() + 1);
				if (sess.getChatpos() > sess.getChatlog().length - 5) sess.setChatpos(sess.getChatlog().length - 5);
				if (sess.getChatpos() < 0) sess.setChatpos(0);
			}
		}
	}

	public void pressedPGUP() {
		if (sess.isMapSelectScreen()) {
			sess.setMapPage(sess.getMapPage() - 1);
			if (sess.getMapPage() < 0) {
				sess.setMapPage(sess.getMapPage() + 1);
			} else
				sess.setCurrentlyHighlightedItem(0);

			loadMiniMapPreview();
		} else if (sess.isSnailInfoScreen()) {
			if (sess.getCurrentlyHighlightedItem2() == 0) {
				sess.setSyspos(sess.getSyspos() - 1);
				if (sess.getSyspos() < 0) sess.setSyspos(0);
			} else if (sess.getCurrentlyHighlightedItem2() == 1) {
				sess.setChatpos(sess.getChatpos() - 1);
				if (sess.getChatpos() < 0) sess.setChatpos(0);
			}
		}
	}

	public void processRightKeyBattleOptions() {
		if (sess.getCurrentlyHighlightedItem() == 0) {
			sess.setVisibility(sess.getVisibility() + 1);
			if (sess.getVisibility() > 2) sess.setVisibility(0);

			if (sess.getVisibility() == 0) {
				sess.getBopt().setFog(false);
				sess.getBopt().setMist(false);
			} else if (sess.getVisibility() == 1) {
				sess.getBopt().setFog(true);
				sess.getBopt().setMist(false);
			} else {
				sess.getBopt().setMist(true);
				sess.getBopt().setFog(false);
			}
		} else if (sess.getCurrentlyHighlightedItem() == 1) {
			int wtemp = sess.getBopt().getWeatherType();
			wtemp++;
			if (wtemp > 4) wtemp = 0;
			sess.getBopt().setWeatherType(wtemp);
		} else if (sess.getCurrentlyHighlightedItem() == 2) {
			int ftemp = sess.getBopt().getFundsLevel();
			ftemp += 500;
			if (ftemp > 10000) ftemp = 10000;
			sess.getBopt().setFundsLevel(ftemp);
		} else if (sess.getCurrentlyHighlightedItem() == 3) {
			int stemp = sess.getBopt().getStartFunds();
			stemp += 500;
			if (stemp > 30000) {
				stemp = 30000;
			}
			sess.getBopt().setStartFunds(stemp);
		} else if (sess.getCurrentlyHighlightedItem() == 4) {
			int temp = sess.getBopt().getTurnLimit();
			temp++;
			sess.getBopt().setTurnLimit(temp);
		} else if (sess.getCurrentlyHighlightedItem() == 5) {
			int temp = sess.getBopt().getCapLimit();
			temp++;
			sess.getBopt().setCapLimit(temp);
		} else if (sess.getCurrentlyHighlightedItem() == 6) {
			if (sess.getBopt().isCOP())
				sess.getBopt().setCOP(false);
			else
				sess.getBopt().setCOP(true);
		} else if (sess.getCurrentlyHighlightedItem() == 7) {
			if (sess.getBopt().isBalance())
				sess.getBopt().setBalance(false);
			else
				sess.getBopt().setBalance(true);
		} else if (sess.getCurrentlyHighlightedItem() == 8) {
			if (sess.getBopt().isRecording())
				sess.getBopt().setReplay(false);
			else
				sess.getBopt().setReplay(true);
		} else if (sess.getCurrentlyHighlightedItem() == 9) {
			sess.setCurrentCursorXposition(sess.getCurrentCursorXposition() + 1);
			if (sess.getCurrentCursorXposition() >= BaseDMG.NUM_UNITS / 2) sess.setCurrentCursorYposition(1);
			if (sess.getCurrentCursorXposition() >= BaseDMG.NUM_UNITS) {
				sess.setCurrentCursorXposition(0);
				sess.setCurrentCursorYposition(0);
			}
		} else if (sess.getCurrentlyHighlightedItem() == 10) {
			if (sess.getBopt().getSnowChance() < 100) sess.getBopt().setSnowChance(sess.getBopt().getSnowChance() + 1);
		} else if (sess.getCurrentlyHighlightedItem() == 11) {
			if (sess.getBopt().getRainChance() < 100) sess.getBopt().setRainChance(sess.getBopt().getRainChance() + 1);
		} else if (sess.getCurrentlyHighlightedItem() == 12) {
			if (sess.getBopt().getSandChance() < 100) sess.getBopt().setSandChance(sess.getBopt().getSandChance() + 1);
		} else if (sess.getCurrentlyHighlightedItem() == 13) {
			sess.getBopt().setMinWTime(sess.getBopt().getMinWTime() + 1);
		} else if (sess.getCurrentlyHighlightedItem() == 14) {
			sess.getBopt().setMaxWTime(sess.getBopt().getMaxWTime() + 1);
		} else if (sess.getCurrentlyHighlightedItem() == 15) {
			sess.getBopt().setMinWDay(sess.getBopt().getMinWDay() + 1);
		}
	}

	public void returnToServerInfo() {
		sess.setIsSnailInfoScreen(true);
		sess.setIsTitleScreen(false);
		Options.snailGame = true;
		refreshInfo();
	}

	public void refreshInfo() {
		String reply = null;
		String command = "getturn";
		String extra = Options.gamename;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		logger.info(reply);

		if (reply.equals("no")) {
			sess.setIsSnailInfoScreen(false);
			sess.setIsChooseNewGameTypeScreen(true);
			Options.snailGame = false;
			sess.setCurrentlyHighlightedItem(0);
			return;
		}

		String[] nums = reply.split("\n");
		sess.setDay(Integer.parseInt(nums[0]));
		sess.setTurn(Integer.parseInt(nums[1]));
		int numplay = Integer.parseInt(nums[2]);
		sess.setUsernames(new String[numplay]);
		System.arraycopy(nums, 3, sess.getUsernames(), 0, numplay);

		command = "getsys";
		extra = Options.gamename;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		sess.setSyslog(reply.split("\n"));
		sess.setSyspos(sess.getSyslog().length - 5);
		if (sess.getSyspos() < 0) sess.setSyspos(0);

		command = "getchat";
		extra = Options.gamename;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		sess.setChatlog(reply.split("\n"));
		sess.setChatpos(sess.getChatlog().length - 5);
		if (sess.getChatpos() < 0) sess.setChatpos(0);
	}

	// This class deals with keypresses
	class KeyControl implements KeyListener {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			int keypress = e.getKeyCode();
			// deal with key remapping
			if (sess.isChooseKey()) {
				switch (sess.getCurrentlyHighlightedItem()) {
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
				sess.setChooseKey(false);
				Options.saveOptions();
			} else if (keypress == Options.up) {
				if (sess.isTitleScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					SFX.playClip(soundLocation + "/menutick.wav");
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(2);
				} else if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					SFX.playClip(soundLocation + "/menutick.wav");
					sess.setCurrentCursorYposition(sess.getCurrentCursorYposition() - 1);
					if (sess.getCurrentCursorYposition() < 0) sess.setCurrentCursorYposition(0);
					if (sess.getCurrentCursorXposition() != 0 || sess.getCurrentCursorYposition() != 0) {
						CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
						if (temp != null) sess.setInfono(COList.getIndex(temp));
					}
					sess.setSkip(0);
					sess.setGlide(0);
				} else if (sess.isInfoScreen()) {
					sess.setSkip(sess.getSkip() - 1);
					if (sess.getSkip() < 0) sess.setSkip(0);
				} else if (sess.isOptionsScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(18);
				} else if (sess.isBattleOptionsScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(15);
				} else if (sess.isChooseNewGameTypeScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(7);
				} else if (sess.isSideSelect()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(sess.getNumOfArmiesOnMap() - 1);
				} else if (sess.isMapSelectScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					SFX.playClip(soundLocation + "/menutick.wav");
					if (sess.getCurrentlyHighlightedItem() < 0) {
						sess.setCurrentlyHighlightedItem(11);
						sess.setMapPage(sess.getMapPage() - 1);
						if (sess.getMapPage() < 0) {
							sess.setMapPage(0);
							sess.setCurrentlyHighlightedItem(0);
						}
					}
					loadMiniMapPreview();
				} else if (sess.isKeyMappingScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(17);
				} else if (sess.isSnailInfoScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					if (sess.getCurrentlyHighlightedItem() < 0) sess.setCurrentlyHighlightedItem(1);
				}
			} else if (keypress == Options.down) {
				if (sess.isTitleScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					SFX.playClip(soundLocation + "/menutick.wav");
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 2) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					SFX.playClip(soundLocation = "/menutick.wav");
					sess.setCurrentCursorYposition(sess.getCurrentCursorYposition() + 1);
					if (sess.getCurrentCursorYposition() > 4) sess.setCurrentCursorYposition(4);
					CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
					if (temp != null) sess.setInfono(COList.getIndex(temp));
					sess.setSkip(0);
					sess.setGlide(0);
				} else if (sess.isInfoScreen()) {
					sess.setSkip(sess.getSkip() + 1);
					if (sess.getSkip() > sess.getSkipMax()) sess.setSkip(sess.getSkipMax());
				} else if (sess.isOptionsScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 18) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isBattleOptionsScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 15) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isChooseNewGameTypeScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 7) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isSideSelect()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > sess.getNumOfArmiesOnMap() - 1) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isMapSelectScreen()) {
					String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					SFX.playClip(soundLocation + "/menutick.wav");

					if (isMapVisible(sess.getCurrentlyHighlightedItem())) {
						sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() - 1);
					}

					if (sess.getCurrentlyHighlightedItem() > 11) {
						sess.setCurrentlyHighlightedItem(0);
						if (isOverLastPage(sess.getMapPage() + 1)) {
							sess.setMapPage(sess.getMapPage() - 1);
							SFX.playClip(soundLocation + "/menutick.wav");
							sess.setCurrentlyHighlightedItem(11);
						}
					}
					loadMiniMapPreview();
				} else if (sess.isKeyMappingScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 17) sess.setCurrentlyHighlightedItem(0);
				} else if (sess.isSnailInfoScreen()) {
					sess.setCurrentlyHighlightedItem(sess.getCurrentlyHighlightedItem() + 1);
					if (sess.getCurrentlyHighlightedItem() > 1) sess.setCurrentlyHighlightedItem(0);
				}
			} else if (keypress == Options.altright || (keypress == Options.right && e.isControlDown())) {
				String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(sess.getCurrentlySelectedSubCategory() + 1);
					SFX.playClip(soundLocation + "/minimap.wav");
					if (sess.getCurrentlySelectedSubCategory() > 9) sess.setCurrentlySelectedSubCategory(0);

					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				} else if (sess.isCOselectScreen()) {
					SFX.playClip(soundLocation + "/minimap.wav");
					sess.setSelectedArmyAllegiance(sess.getSelectedArmyAllegiance() + 1);
					if (sess.getSelectedArmyAllegiance() > 7) sess.setSelectedArmyAllegiance(7);
					if (sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1 > 0) {
						CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
						if (temp != null) sess.setInfono(COList.getIndex(temp));
					}
					sess.setSkip(0);
					sess.setGlide(0);
				}
			} else if (keypress == Options.altleft || (keypress == Options.left && e.isControlDown())) {
				String soundLocation = ResourceLoader.properties.getProperty("soundLocation");

				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(sess.getCurrentlySelectedSubCategory() - 1);
					SFX.playClip(soundLocation + "/minimap.wav");
					if (sess.getCurrentlySelectedSubCategory() < 0) sess.setCurrentlySelectedSubCategory(9);

					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				} else if (sess.isCOselectScreen()) {
					SFX.playClip(soundLocation + "/minimap.wav");
					sess.setSelectedArmyAllegiance(sess.getSelectedArmyAllegiance() - 1);
					if (sess.getSelectedArmyAllegiance() < 0) sess.setSelectedArmyAllegiance(0);
					if (sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1 > 0) {
						CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
						if (temp != null) sess.setInfono(COList.getIndex(temp));
					}
					sess.setSkip(0);
					sess.setGlide(0);
				}
			} else if (keypress == Options.left) {
				String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
				if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
					SFX.playClip(soundLocation + "/menutick.wav");
					sess.setCurrentCursorXposition(sess.getCurrentCursorXposition() - 1);
					if (sess.getCurrentCursorXposition() < 0) {
						sess.setCurrentCursorXposition(0);
						if (sess.getCurrentCursorYposition() != 0) {
							sess.setCurrentCursorXposition(2);
							sess.setCurrentCursorYposition(sess.getCurrentCursorYposition() - 1);
						}
					}
					if (sess.getCurrentCursorXposition() != 0 || sess.getCurrentCursorYposition() != 0) {
						CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
						if (temp != null) sess.setInfono(COList.getIndex(temp));
					}
					sess.setSkip(0);
					sess.setGlide(0);
				} else if (sess.isSideSelect()) {
					if (sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] == 0)
						sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] = sess.getNumOfArmiesOnMap() - 1;
					else
						sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] -= 1;
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 6) {
					Options.decrementCursor();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 11) {
					Options.decrementCO();
					sess.setGlide(0);
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 12) {
					Options.toggleSFX();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 13) {
					Options.decrementTerrain();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 14) {
					Options.decrementUrban();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 15) {
					Options.decrementHQ();
				} else if (sess.isMapSelectScreen()) {

					sess.setCurrentlySelectedMapCategory(sess.getCurrentlySelectedMapCategory() - 1);
					if (sess.getCurrentlySelectedMapCategory() < 0) sess.setCurrentlySelectedMapCategory(mapCategories.length - 1);
					sess.setCurrentlySelectedSubCategory(0);

					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				} else if (sess.isBattleOptionsScreen()) {
					if (sess.getCurrentlyHighlightedItem() == 0) {
						sess.setVisibility(sess.getVisibility() - 1);
						if (sess.getVisibility() < 0) sess.setVisibility(2);

						if (sess.getVisibility() == 0) {
							sess.getBopt().setFog(false);
							sess.getBopt().setMist(false);
						} else if (sess.getVisibility() == 1) {
							sess.getBopt().setFog(true);
							sess.getBopt().setMist(false);
						} else {
							sess.getBopt().setMist(true);
							sess.getBopt().setFog(false);
						}
					} else if (sess.getCurrentlyHighlightedItem() == 1) {
						int wtemp = sess.getBopt().getWeatherType();
						wtemp--;
						if (wtemp < 0) wtemp = 4;
						sess.getBopt().setWeatherType(wtemp);
					} else if (sess.getCurrentlyHighlightedItem() == 2) {
						int ftemp = sess.getBopt().getFundsLevel();
						ftemp -= 500;
						if (ftemp <= 0) ftemp = 500;
						sess.getBopt().setFundsLevel(ftemp);
					} else if (sess.getCurrentlyHighlightedItem() == 3) {
						int stemp = sess.getBopt().getStartFunds();
						stemp -= 500;
						if (stemp <= 0) stemp = 0;
						sess.getBopt().setStartFunds(stemp);
					} else if (sess.getCurrentlyHighlightedItem() == 4) {
						int temp = sess.getBopt().getTurnLimit();
						temp--;
						if (temp < 0) temp = 0;
						sess.getBopt().setTurnLimit(temp);
					} else if (sess.getCurrentlyHighlightedItem() == 5) {
						int temp = sess.getBopt().getCapLimit();
						temp--;
						if (temp < 0) temp = 0;
						sess.getBopt().setCapLimit(temp);
					} else if (sess.getCurrentlyHighlightedItem() == 6) {
						if (sess.getBopt().isCOP())
							sess.getBopt().setCOP(false);
						else
							sess.getBopt().setCOP(true);
					} else if (sess.getCurrentlyHighlightedItem() == 7) {
						if (sess.getBopt().isBalance())
							sess.getBopt().setBalance(false);
						else
							sess.getBopt().setBalance(true);
					} else if (sess.getCurrentlyHighlightedItem() == 8) {
						if (sess.getBopt().isRecording())
							sess.getBopt().setReplay(false);
						else
							sess.getBopt().setReplay(true);
					} else if (sess.getCurrentlyHighlightedItem() == 9) {
						sess.setCurrentCursorXposition(sess.getCurrentCursorXposition() - 1);
						if (sess.getCurrentCursorXposition() < BaseDMG.NUM_UNITS / 2) sess.setCurrentCursorYposition(0);
						if (sess.getCurrentCursorXposition() < 0) {
							sess.setCurrentCursorXposition(BaseDMG.NUM_UNITS - 1);
							sess.setCurrentCursorYposition(1);
						}
					} else if (sess.getCurrentlyHighlightedItem() == 10) {
						if (sess.getBopt().getSnowChance() > 0) sess.getBopt().setSnowChance(sess.getBopt().getSnowChance() - 1);
					} else if (sess.getCurrentlyHighlightedItem() == 11) {
						if (sess.getBopt().getRainChance() > 0) sess.getBopt().setRainChance(sess.getBopt().getRainChance() - 1);
					} else if (sess.getCurrentlyHighlightedItem() == 12) {
						if (sess.getBopt().getSandChance() > 0) sess.getBopt().setSandChance(sess.getBopt().getSandChance() - 1);
					} else if (sess.getCurrentlyHighlightedItem() == 13) {
						if (sess.getBopt().getMinWTime() > 0) sess.getBopt().setMinWTime(sess.getBopt().getMinWTime() - 1);
					} else if (sess.getCurrentlyHighlightedItem() == 14) {
						if (sess.getBopt().getMaxWTime() > 0) sess.getBopt().setMaxWTime(sess.getBopt().getMaxWTime() - 1);
					} else if (sess.getCurrentlyHighlightedItem() == 15) {
						if (sess.getBopt().getMinWDay() > 0) sess.getBopt().setMinWDay(sess.getBopt().getMinWDay() - 1);
					}
				} else if (sess.isSnailInfoScreen()) {
					sess.setCurrentlyHighlightedItem2(sess.getCurrentlyHighlightedItem2() - 1);
					if (sess.getCurrentlyHighlightedItem2() < 0) sess.setCurrentlyHighlightedItem2(1);
				}
			} else if (keypress == Options.right) {
				String soundLocation = ResourceLoader.properties.getProperty("soundLocation");

				if (sess.isCOselectScreen() && !sess.isInfoScreen()) {
					SFX.playClip(soundLocation + "/menutick.wav");
					sess.setCurrentCursorXposition(sess.getCurrentCursorXposition() + 1);
					if (sess.getCurrentCursorXposition() > 2) {
						sess.setCurrentCursorXposition(2);
						if (sess.getCurrentCursorYposition() != 4) {
							sess.setCurrentCursorXposition(0);
							sess.setCurrentCursorYposition(sess.getCurrentCursorYposition() + 1);
						}
					}
					CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
					if (temp != null) sess.setInfono(COList.getIndex(temp));
					sess.setSkip(0);
					sess.setGlide(0);
				} else if (sess.isSideSelect()) {
					if (sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] == sess.getNumOfArmiesOnMap() - 1)
						sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] = 0;
					else
						sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] += 1;
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 6) {
					Options.incrementCursor();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 11) {
					Options.incrementCO();
					sess.setGlide(0);
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 12) {
					Options.toggleSFX();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 13) {
					Options.incrementTerrain();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 14) {
					Options.incrementUrban();
				} else if (sess.isOptionsScreen() && sess.getCurrentlyHighlightedItem() == 15) {
					Options.incrementHQ();
				}
				if (sess.isMapSelectScreen()) {

					sess.setCurrentlySelectedMapCategory(sess.getCurrentlySelectedMapCategory() + 1);
					if (sess.getCurrentlySelectedMapCategory() > mapCategories.length - 1) sess.setCurrentlySelectedMapCategory(0);
					sess.setCurrentlySelectedSubCategory(0);

					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);
					// load maps in new directory
					loadMapDisplayNames();
				} else if (sess.isBattleOptionsScreen()) {
					processRightKeyBattleOptions();
				} else if (sess.isSnailInfoScreen()) {
					sess.setCurrentlyHighlightedItem2(sess.getCurrentlyHighlightedItem2() + 1);
					if (sess.getCurrentlyHighlightedItem2() > 1) sess.setCurrentlyHighlightedItem2(0);
				}
			} else if (keypress != Options.pgdn) {
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
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(0);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_2) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(1);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_3) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(2);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_4) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(3);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_5) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(4);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_6) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(5);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_7) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(6);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_8) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(7);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_9) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(8);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == KeyEvent.VK_0) {
				if (sess.isMapSelectScreen()) {
					sess.setCurrentlySelectedSubCategory(9);
					sess.setCurrentlyHighlightedItem(0);
					sess.setMapPage(0);

					// load maps in new directory
					loadMapDisplayNames();
				}
			} else if (keypress == Options.constmode) {
				logger.info("Alternating Costumes");
				sess.setAltcostume(!sess.isAltcostume());
			} else if (keypress == Options.nextunit) {
				if (sess.isCOselectScreen()) {
					if (sess.isInfoScreen()) {
						sess.setIsInfoScreen(false);
					} else {
						if (sess.getCurrentCursorXposition() == 0 && sess.getCurrentCursorYposition() == 0) {
							Random r = new Random();
							int sel = r.nextInt(COList.getListing().length);
							CO sc = COList.getListing()[sel];
							logger.info("Selecting " + sc.getName());
							sess.setSelectedArmyAllegiance(sc.getStyle());
							for (int i = 0; armyArray[sess.getSelectedArmyAllegiance()][i] != null; i++) {
								if (armyArray[sess.getSelectedArmyAllegiance()][i] == sc) {
									if (i < 2) {
										sess.setCurrentCursorXposition(i + 1);
										sess.setCurrentCursorYposition(0);
									} else {
										sess.setCurrentCursorXposition((i - 2) % 3);
										sess.setCurrentCursorYposition((i - 2) / 3 + 1);
									}
								}
							}
							pressedA();
						} else {
							sess.setIsInfoScreen(true);
						}
					}
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	class MouseControl implements MouseInputListener {
		private static final int	SPLASH_OPTIONS_HEIGHT_TOP				= 279;
		private static final int	SPLASH_OPTIONS_HEIGHT_BOTTOM			= 247;
		private static final int	SPLASH_OPTIONS_WIDTH_END					= 320;
		private static final int	SPLASH_OPTIONS_WIDTH_START				= 175;
		private static final int	SPLASH_MAP_DESIGN_HEIGHT_TOP			= 183;
		private static final int	SPLASH_MAP_DESIGN_HEIGHT_BOTTOM	= 156;
		private static final int	SPLASH_MAP_DESIGN_WIDTH_END			= 350;
		private static final int	SPLASH_MAP_DESIGN_WIDTH_START		= 143;
		private static final int	SPLASH_NEW_GAME_HEIGHT_TOP				= 87;
		private static final int	SPLASH_NEW_GAME_HEIGHT_BOTTOM		= 60;
		private static final int	SPLASH_NEW_GAME_WIDTH_START			= 160;
		private static final int	SPLASH_NEW_GAME_WIDTH_END				= 332;

		public void mouseClicked(MouseEvent e) {
			int cursorXpos = e.getX() - parentJFrame.getInsets().left;
			int cursorYpos = e.getY() - parentJFrame.getInsets().top;

			if (e.getButton() == MouseEvent.BUTTON1) {
				// first mouse button
				if (sess.isTitleScreen()) {
					boolean newGame = cursorXpos > SPLASH_NEW_GAME_WIDTH_START && cursorXpos < SPLASH_NEW_GAME_WIDTH_END && cursorYpos > SPLASH_NEW_GAME_HEIGHT_BOTTOM && cursorYpos < SPLASH_NEW_GAME_HEIGHT_TOP;
					boolean designMaps = cursorXpos > SPLASH_MAP_DESIGN_WIDTH_START && cursorXpos < SPLASH_MAP_DESIGN_WIDTH_END && cursorYpos > SPLASH_MAP_DESIGN_HEIGHT_BOTTOM && cursorYpos < SPLASH_MAP_DESIGN_HEIGHT_TOP;
					boolean optionsScreen = cursorXpos > SPLASH_OPTIONS_WIDTH_START && cursorXpos < SPLASH_OPTIONS_WIDTH_END && cursorYpos > SPLASH_OPTIONS_HEIGHT_BOTTOM && cursorYpos < SPLASH_OPTIONS_HEIGHT_TOP;

					if (newGame) {
						logger.info("Moving into the New Game Menu");
						sess.setCurrentlyHighlightedItem(0);
						sess.setIsTitleScreen(false);
						sess.setIsChooseNewGameTypeScreen(true);
					} else {
						if (designMaps) {
							sess.setCurrentlyHighlightedItem(1);
							logger.info("Moving into the Design Maps Area");
							startMapEditor();
						} else {
							if (optionsScreen) {
								logger.info("Moving into the Options Menu");
								sess.setIsTitleScreen(false);
								sess.setIsOptionsScreen(true);
								sess.setCurrentlyHighlightedItem(0);
							}
						}
					}

				} else {
					if (sess.isChooseNewGameTypeScreen()) {

						int NEW_GAME_WIDTH_START = 11;
						int NEW_GAME_WIDTH_END = 87;
						int NEW_GAME_HEIGHT_TOP = 8;
						int NEW_GAME_HEIGHT_BOTTOM = 28;
						
						int LOAD_GAME_WIDTH_START = 11;
						int LOAD_GAME_WIDTH_END= 87;
						int LOAD_GAME_HEIGHT_TOP = 29;
						int LOAD_GAME_HEIGHT_BOTTOM = 56;
						
						int NETWORK_GAME_WIDTH_START = 11;
						int NETWORK_GAME_WIDTH_END = 200;
						int NETWORK_GAME_HEIGHT_BOTTOM = 76;
						int NETWORK_GAME_HEIGHT_TOP = 57;

						int LOAD_REPLAY_WIDTH_START = 11;
						int LOAD_REPLAY_WIDTH_END = 200;
						int LOAD_REPLAY_HEIGHT_BOTTOM = 106;
						int LOAD_REPLAY_HEIGHT_TOP = 76;
						
						int NEW_SERVER_GAME_WIDTH_START = 11;
						int NEW_SERVER_GAME_WIDTH_END = 334;
						int NEW_SERVER_GAME_HEIGHT_BOTTOM = 132;
						int NEW_SERVER_GAME_HEIGHT_TOP = 107;

						int JOIN_SERVER_GAME_WIDTH_START = 11;
						int JOIN_SERVER_GAME_WIDTH_END = 334;
						int JOIN_SERVER_GAME_HEIGHT_BOTTOM = 156;
						int JOIN_SERVER_GAME_HEIGHT_TOP = 133;
						
						int LOGIN_SERVER_GAME_WIDTH_START = 11;
						int LOGIN_SERVER_GAME_WIDTH_END = 334;
						int LOGIN_SERVER_GAME_HEIGHT_BOTTOM = 182;
						int LOGIN_SERVER_GAME_HEIGHT_TOP = 157;

						int JOIN_LOBBY_WIDTH_START = 11;
						int JOIN_LOBBY_WIDTH_END = 334;
						int JOIN_LOBBY_HEIGHT_BOTTOM = 200;
						int JOIN_LOBBY_HEIGHT_TOP = 183;
						
						boolean newGameClicked = cursorXpos > NEW_GAME_WIDTH_START && cursorXpos < NEW_GAME_WIDTH_END && cursorYpos < NEW_GAME_HEIGHT_BOTTOM && cursorYpos > NEW_GAME_HEIGHT_TOP;
						boolean loadGameClicked =   cursorXpos > LOAD_GAME_WIDTH_START && cursorXpos < LOAD_GAME_WIDTH_END && cursorYpos < LOAD_GAME_HEIGHT_BOTTOM && cursorYpos > LOAD_GAME_HEIGHT_TOP;
						boolean networkGameClicked =    cursorXpos > NETWORK_GAME_WIDTH_START && cursorXpos < NETWORK_GAME_WIDTH_END && cursorYpos < NETWORK_GAME_HEIGHT_BOTTOM && cursorYpos > NETWORK_GAME_HEIGHT_TOP;
						boolean loadReplayClicked = cursorXpos > LOAD_REPLAY_WIDTH_START && cursorXpos < LOAD_REPLAY_WIDTH_END && cursorYpos < LOAD_REPLAY_HEIGHT_BOTTOM && cursorYpos > LOAD_REPLAY_HEIGHT_TOP;
						boolean newServerGameClicked = cursorXpos > NEW_SERVER_GAME_WIDTH_START && cursorXpos < NEW_SERVER_GAME_WIDTH_END && cursorYpos < NEW_SERVER_GAME_HEIGHT_BOTTOM && cursorYpos > NEW_SERVER_GAME_HEIGHT_TOP;
						boolean joinServerGameClicked = cursorXpos > JOIN_SERVER_GAME_WIDTH_START && cursorXpos < JOIN_SERVER_GAME_WIDTH_END && cursorYpos < JOIN_SERVER_GAME_HEIGHT_BOTTOM && cursorYpos > JOIN_SERVER_GAME_HEIGHT_TOP;
						boolean loginToServerGameClicked = cursorXpos > LOGIN_SERVER_GAME_WIDTH_START && cursorXpos < LOGIN_SERVER_GAME_WIDTH_END && cursorYpos < LOGIN_SERVER_GAME_HEIGHT_BOTTOM && cursorYpos > LOGIN_SERVER_GAME_HEIGHT_TOP;
						boolean joinLobbyClicked = cursorXpos > JOIN_LOBBY_WIDTH_START && cursorXpos < JOIN_LOBBY_WIDTH_END && cursorYpos < JOIN_LOBBY_HEIGHT_BOTTOM && cursorYpos > JOIN_LOBBY_HEIGHT_TOP;
						
						if(newGameClicked){
							logger.debug("New Game Clicked");
							sess.setCurrentlyHighlightedItem(0);
						}
						
						if(loadGameClicked){
							logger.debug("Load Game Clicked");
							sess.setCurrentlyHighlightedItem(1);
						}
						
						if(networkGameClicked){
							logger.debug("Network Game Clicked");
							sess.setCurrentlyHighlightedItem(2);
						}
						
						if(loadReplayClicked){
							logger.debug("Load Replay Clicked");
							sess.setCurrentlyHighlightedItem(3);
						}
						
						if(newServerGameClicked){
							logger.debug("New Server Game Clicked");
							sess.setCurrentlyHighlightedItem(4);
						}
						
						if(joinServerGameClicked){
							logger.debug("Join Server Game Clicked");
							sess.setCurrentlyHighlightedItem(5);
						}
						
						if(loginToServerGameClicked){
							logger.debug("Login to Server Game Clicked");
							sess.setCurrentlyHighlightedItem(6);
						}
						
						if(joinLobbyClicked){
							logger.debug("Join Game Lobby Clicked");
							sess.setCurrentlyHighlightedItem(7);
						}
						
						parseSelectedNewGameTypeInput();
						
						
					} else if (sess.isOptionsScreen()) {
						if (cursorXpos < 220) {
							int i = cursorYpos / 20;
							if ((i < 6 || i > 6) && i < SPLASH_NEW_GAME_WIDTH_START) {
								sess.setCurrentlyHighlightedItem(i);
								pressedA();
							} else if (i == 6) {
								sess.setCurrentlyHighlightedItem(i);
								Options.incrementCursor();
							}
						}
					} else if (sess.isMapSelectScreen()) {
						if (cursorYpos < 30) {
							if (cursorXpos < 180) {
								// change category
								sess.setCurrentlySelectedMapCategory(sess.getCurrentlySelectedMapCategory() + 1);
								if (sess.getCurrentlySelectedMapCategory() > mapCategories.length - 1) sess.setCurrentlySelectedMapCategory(0);
								sess.setCurrentlySelectedSubCategory(0);

								sess.setCurrentlyHighlightedItem(0);
								sess.setMapPage(0);

								// load maps in new directory
								String mapsLocation = ResourceLoader.properties.getProperty("mapsLocation");
								loadMapDisplayNames();
							}
						}
						if (cursorYpos < 40 && cursorXpos > 180) {
							// change subcategory
							if (cursorXpos < 240)
								sess.setCurrentlySelectedSubCategory(0);
							else if (cursorXpos < 260)
								sess.setCurrentlySelectedSubCategory(1);
							else if (cursorXpos < 280)
								sess.setCurrentlySelectedSubCategory(2);
							else if (cursorXpos < 300)
								sess.setCurrentlySelectedSubCategory(3);
							else if (cursorXpos < 320)
								sess.setCurrentlySelectedSubCategory(4);
							else if (cursorXpos < 340)
								sess.setCurrentlySelectedSubCategory(5);
							else if (cursorXpos < 360)
								sess.setCurrentlySelectedSubCategory(6);
							else if (cursorXpos < 380)
								sess.setCurrentlySelectedSubCategory(7);
							else if (cursorXpos < 400)
								sess.setCurrentlySelectedSubCategory(8);
							else if (cursorXpos < 480) sess.setCurrentlySelectedSubCategory(9);

							sess.setCurrentlyHighlightedItem(0);
							sess.setMapPage(0);

							// load maps in new directory
							loadMapDisplayNames();
						} else if (cursorYpos > 30 && cursorYpos < 38) {
							if (cursorXpos > 84 && cursorXpos < 98) pressedPGUP();
						} else if (cursorYpos > 50 && cursorYpos < 302) {
							if (cursorXpos < 160) {
								int i = (cursorYpos - 50) / 21;
								if (i < NUM_VISIBLE_ROWS && isMapVisible(i)) {
									sess.setCurrentlyHighlightedItem(i);
									pressedA();
								}
							}
						} else if (cursorYpos > 312 && cursorYpos < 320) {
							if (cursorXpos > 84 && cursorXpos < 98) pressedPGDN();
						}
					} else if (sess.isCOselectScreen()) {
						if (cursorYpos > 61 && cursorYpos < 321 && cursorXpos > 2 && cursorXpos < 158) {
							sess.setCurrentCursorXposition((cursorXpos - 2) / 52);
							sess.setCurrentCursorYposition((cursorYpos - 61) / 52);
							pressedA();
						} else if (cursorXpos >= 3 && cursorXpos <= 155 && cursorYpos <= 53) {
							sess.setSelectedArmyAllegiance((cursorXpos - 3) / 19);
							if (sess.getCurrentCursorXposition() != 0 || sess.getCurrentCursorYposition() != 0) {
								CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];
								if (temp != null) sess.setInfono(COList.getIndex(temp));
							}
						}
					} else if (sess.isSideSelect()) {
						if (cursorXpos < 130) {
							if (cursorYpos / 20 < sess.getNumOfArmiesOnMap()) {
								sess.setCurrentlyHighlightedItem(cursorYpos / 20);
								if (sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] == sess.getNumOfArmiesOnMap() - 1)
									sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] = 0;
								else
									sess.getSideSelections()[sess.getCurrentlyHighlightedItem()] += 1;
							}
						} else {
							pressedA();
						}
					} else if (sess.isBattleOptionsScreen()) {
						if (cursorXpos > 10 && cursorXpos < 10 + BaseDMG.NUM_UNITS / 2 * 16 && cursorYpos > 184 && cursorYpos < 220) {
							sess.setCurrentCursorYposition((cursorYpos - 184) / 20);
							sess.setCurrentCursorXposition((cursorXpos - 10) / 16 + sess.getCurrentCursorYposition() * BaseDMG.NUM_UNITS / 2);
							sess.setCurrentlyHighlightedItem(9);
							pressedA();
						} else if (cursorXpos < 210) {
							if (cursorYpos / 20 < 9) {
								sess.setCurrentlyHighlightedItem(cursorYpos / 20);
								processRightKeyBattleOptions();
							}
						} else {
							pressedA();
						}
					} else if (sess.isSnailInfoScreen()) {
						if (cursorXpos > 240 && cursorXpos < 480 && cursorYpos > 280 && cursorYpos < 300) {
							sess.setCurrentlyHighlightedItem(0);
							pressedA();
						} else if (cursorXpos > 240 && cursorXpos < 480 && cursorYpos > 300 && cursorYpos < 320) {
							sess.setCurrentlyHighlightedItem(1);
							pressedA();
						} else if (cursorXpos > 0 && cursorXpos < 160 && cursorYpos > 100 && cursorYpos < 120) {
							sess.setCurrentlyHighlightedItem2(0);
						} else if (cursorXpos > 160 && cursorXpos < 320 && cursorYpos > 100 && cursorYpos < 120) {
							sess.setCurrentlyHighlightedItem2(1);
						} else if (cursorXpos > 320 && cursorXpos < 480 && cursorYpos > 100 && cursorYpos < 120) {
							// send chat message
							String message = JOptionPane.showInputDialog("Type in your chat message");
							if (message == null) {
								return;
							}

							String command = "sendchat";
							String extra = Options.gamename + "\n" + Options.username + "\n" + message;
							String reply = null;

							try {
								reply = networkingManager.sendCommandToMain(command, extra);
							} catch (MalformedURLException e1) {
								logger.info("Bad URL " + Options.getServerName());
							} catch (IOException e2) {
								logger.error("Connection Problem during command " + command + " with information:\n" + extra);
							}

							logger.info(reply);
							refreshInfo();
						} else if (cursorXpos > 460 && cursorXpos < 480 && cursorYpos > 0 && cursorYpos < 20) {
							pressedPGUP();
						} else if (cursorXpos > 460 && cursorXpos < 480 && cursorYpos > 80 && cursorYpos < 100) {
							pressedPGDN();
						}
					}
				}
			} else {
				// any other button
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
			int x = e.getX() - parentJFrame.getInsets().left;
			int y = e.getY() - parentJFrame.getInsets().top;
			if (sess.isMapSelectScreen()) {
				if (y > 50 && y < 302 && x < 160) {
					int i = (y - 50) / 21;
					if (i < NUM_VISIBLE_ROWS && isMapVisible(i)) {
						if (i != sess.getCurrentlyHighlightedItem()) {
							sess.setCurrentlyHighlightedItem(i);
							loadMiniMapPreview();
						}
					}
				}
			} else if (sess.isCOselectScreen()) {
				if (y > 61 && y < 321 && x > 2 && x < 158) {
					sess.setCurrentCursorXposition((x - 2) / 52);
					sess.setCurrentCursorYposition((y - 61) / 52);
					if (sess.getCurrentCursorXposition() != 0 || sess.getCurrentCursorYposition() != 0) {
						CO temp = armyArray[sess.getSelectedArmyAllegiance()][sess.getCurrentCursorXposition() + sess.getCurrentCursorYposition() * 3 - 1];

						if (temp != null && sess.getInfono() != COList.getIndex(temp)) sess.setGlide(0);
						if (temp != null) sess.setInfono(COList.getIndex(temp));
					}
				}
			}
		}
	}

	// ///////YES, This is a hack :).
	public void LaunchCreateServerGame() {
		logger.info("Create Server Game");
		boolean cannotConnect = false;
		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// find an unused name
		Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
		if (Options.gamename == null) {
			return;
		}

		String command = "qname";
		String extra = Options.gamename;
		String reply = null;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		while (!reply.equals("yes")) {
			logger.info(reply);
			if (reply.equals("no")) {
				logger.info("Game name already taken");
				JOptionPane.showMessageDialog(this, "Game name already taken");
			}
			Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
			if (Options.gamename == null) {
				return;
			}

			try {
				reply = networkingManager.sendCommandToMain("qname", Options.gamename);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}
		}

		// set the master password and join
		Options.masterpass = JOptionPane.showInputDialog("Type in a master password for your game");
		if (Options.masterpass == null) return;
		if (Options.isDefaultLoginOn()) {
			Options.username = Options.getDefaultUsername();
			Options.password = Options.getDefaultPassword();

			if (Options.username == null || Options.username.length() < 1 || Options.username.length() > MAX_USERNAME_LENGTH) return;
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

		// start game
		logger.info("starting game");
		Options.snailGame = true;
		sess.setCurrentlyHighlightedItem(0);

		// New Game
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setIsMapSelectScreen(true);
		sess.setCurrentlyHighlightedItem(0);
		sess.setMapPage(0);

		// load categories
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

		sess.setCurrentlySelectedMapCategory(0);
		sess.setCurrentlySelectedSubCategory(0);
		loadMapDisplayNames();
		sess.setMapPage(0);
	}

	public void LaunchCreateServerGame(String username, String password, String gamename, String gamepass) {
		logger.info("Create Server Game");
		boolean cannotConnect = false;
		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// find an unused name

		// Options.gamename =
		// JOptionPane.showInputDialog("Type in a name for your game");
		Options.gamename = gamename;
		if (Options.gamename == null) {
			return;
		}

		String reply = null;
		String command = "qname";
		String extra = Options.gamename;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		while (!reply.equals("yes")) {
			logger.info(reply);
			if (reply.equals("no")) {
				logger.info("Game name already taken");
				JOptionPane.showMessageDialog(this, "Game name already taken");
			}
			Options.gamename = JOptionPane.showInputDialog("Type in a name for your game");
			if (Options.gamename == null) {
				return;
			}

			try {
				reply = networkingManager.sendCommandToMain("qname", Options.gamename);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}

		}

		// set the master password and join
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
		// Options.password =
		// JOptionPane.showInputDialog("Type in your password for this game");
		Options.password = password;
		if (Options.password == null) return;

		// start game
		logger.info("starting game");
		Options.snailGame = true;
		sess.setCurrentlyHighlightedItem(0);

		// New Game
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setIsMapSelectScreen(true);
		sess.setCurrentlyHighlightedItem(0);
		sess.setMapPage(0);

		// load categories
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

		sess.setCurrentlySelectedMapCategory(0);
		sess.setCurrentlySelectedSubCategory(0);
		loadMapDisplayNames();
		sess.setMapPage(0);
	}

	public void LaunchLoginGame(String gamename, String username, String password) {
		logger.info("Log in to Server Game");
		boolean cannotConnect = false;

		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// connect to the game
		if (gamename == null) return;
		Options.gamename = gamename;

		// Get user's name and password
		if (username == null) return;
		if (password == null) return;
		Options.username = username;
		Options.password = password;

		// try to connect
		String reply = null;
		String command = "validup";
		String extra = Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + Options.version;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		logger.info(reply);
		if (!reply.equals("login successful")) {
			if (reply.equals("version mismatch"))
				JOptionPane.showMessageDialog(this, "Version Mismatch");
			else
				JOptionPane.showMessageDialog(this, "Problem logging in, either the username/password is incorrect or the game has ended");
			return;
		}

		// go to information screen
		Options.snailGame = true;
		sess.setIsSnailInfoScreen(true);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setCurrentlyHighlightedItem(0);
		sess.setCurrentlyHighlightedItem2(0);

		refreshInfo();
	}

	public void LaunchJoinGame(String gamename, String masterpassword, String username, String password, int slotnumber) {
		logger.info("Join Server Game");
		boolean cannotConnect = false;

		// try to connect to the server first to see that the user's URL is correct
		try {
			cannotConnect = !networkingManager.tryToConnect();
		} catch (MalformedURLException e1) {
			logger.error("Bad URL");
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());

		} catch (IOException e2) {
			logger.error("Unable to connect to the server at " + Options.getServerName());
			JOptionPane.showMessageDialog(this, "Unable to connect to the server at " + Options.getServerName());
		}

		if (cannotConnect) {
			return;
		}

		// connect to the game
		if (gamename == null) return;
		Options.gamename = gamename;

		// check the master password and get number of players and available slots
		if (masterpassword == null) return;
		Options.masterpass = masterpassword;

		// Get user's name, password, and slot
		if (username == null) return;
		Options.username = username;

		if (password == null) return;
		Options.password = password;

		String slot = Integer.toString(slotnumber);
		// Join

		String reply = null;
		String command = "join";
		String extra = Options.gamename + "\n" + Options.masterpass + "\n" + Options.username + "\n" + Options.password + "\n" + slot + "\n" + Options.version;

		try {
			reply = networkingManager.sendCommandToMain(command, extra);
		} catch (MalformedURLException e1) {
			logger.info("Bad URL " + Options.getServerName());
			reply = null;
			JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
		} catch (IOException e2) {
			logger.error("Connection Problem during command " + command + " with information:\n" + extra);
			reply = null;
			JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
		}

		while (!reply.equals("join successful")) {
			logger.info(reply);
			if (reply.equals("no")) {
				logger.info("Game does not exist");
				Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
				if (Options.gamename == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
				Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
				if (Options.masterpass == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("wrong password")) {
				logger.info("Incorrect Password");
				Options.gamename = JOptionPane.showInputDialog("Type in the name of the game you want to join");
				if (Options.gamename == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
				Options.masterpass = JOptionPane.showInputDialog("Type in the master password of the game");
				if (Options.masterpass == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("out of range")) {
				logger.info("Army choice out of range or invalid");
				slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
				if (slot == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else if (reply.equals("slot taken")) {
				logger.info("Army choice already taken");
				slot = JOptionPane.showInputDialog("Type in the number of the army you will command");
				if (slot == null) {
					sess.setIsTitleScreen(true);
					sess.setIsSnailInfoScreen(false);
					return;
				}
			} else {
				logger.info("Other problem");
				JOptionPane.showMessageDialog(this, "Version Mismatch");
				Options.snailGame = false;
				sess.setIsTitleScreen(true);
				sess.setIsSnailInfoScreen(false);
				return;
			}
			refreshInfo();

			try {
				reply = networkingManager.sendCommandToMain(command, extra);
			} catch (MalformedURLException e1) {
				logger.info("Bad URL " + Options.getServerName());
				reply = null;
				JOptionPane.showMessageDialog(this, "Bad URL: " + Options.getServerName());
			} catch (IOException e2) {
				logger.error("Connection Problem during command " + command + " with information:\n" + extra);
				reply = null;
				JOptionPane.showMessageDialog(this, "Connection Problem during command " + command + " with the following information:\n" + extra);
			}

		}

		// go to information screen
		Options.snailGame = true;
		sess.setIsSnailInfoScreen(true);
		sess.setIsChooseNewGameTypeScreen(false);
		sess.setCurrentlyHighlightedItem(0);
		sess.setCurrentlyHighlightedItem2(0);

		refreshInfo();
	}

	private class refreshListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			refreshInfo();
		}
	}

	private Map getMap(int item) {
		return filteredMaps.get(sess.getMapPage() * NUM_VISIBLE_ROWS + item);
	}

	private boolean isMapVisible(int item) {
		return sess.getMapPage() * NUM_VISIBLE_ROWS + item < filteredMaps.size();
	}

	private String getFileName(int item) {
		return allMapFilenames[sess.getMapPage() * NUM_VISIBLE_ROWS + item];
	}

	private boolean isOverLastPage(int item) {
		return item > filteredMaps.size() / NUM_VISIBLE_ROWS || (item == filteredMaps.size() / NUM_VISIBLE_ROWS && filteredMaps.size() % NUM_VISIBLE_ROWS == 0);
	}

	public void fobbahInit() {
		sess.setIsTitleScreen(false);
		sess.setIsChooseNewGameTypeScreen(true);
	}

}