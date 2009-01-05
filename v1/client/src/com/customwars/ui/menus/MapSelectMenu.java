package com.customwars.ui.menus;

import com.customwars.ai.Battle;
import com.customwars.ai.GameSession;
import com.customwars.loader.MapLoader;
import com.customwars.map.Map;
import com.customwars.map.location.Location;
import com.customwars.map.location.Property;
import com.customwars.map.location.TerrType;
import com.customwars.sfx.SFX;
import com.customwars.state.FileSystemManager;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.state.StateManager;
import com.customwars.ui.state.State;
import com.customwars.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains a list of mapItems
 * Each group of NUM_VISIBLE_ITEMS mapItems is one page.
 * move 1 page down when the last item in the list is selected and the down arrow is pressed
 * <p/>
 * Note
 * The word Visible is used to validate against the current amount of maps shown in the menu(1 page)
 * The word vaidate is used to validate against all maps(not only 1 page)
 * The word item is one of the visible items from 0 to NUM_VISIBLE_ITEMS-1
 * <p/>
 * When we want to know if a visible item is out of bounds for all map items
 * then we need to multiply the menu item with the page offset
 * page * NUM_VISIBLE_ITEMS + item or in a method int getMapItem(int item)
 *
 * @author stefan
 * @since 2.0
 */
public class MapSelectMenu extends Menu implements State {
  private static final int NUM_VISIBLE_ITEMS = 12;
  private static final int MENU_ITEM_LEFT_OFFSET = 10;
  private static final int MENU_ITEM_TOP_OFFSET = 68;
  private static final int MENU_ITEM_SPACING = 21;

  private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
  private static final Color BOX_BACKGROUND = new Color(7, 66, 97);

  private List<Map> maps;         // All the available maps
  private List<Map> filteredMaps; // Contains a sub list of all the maps, can be filtered by the user
  private Map miniMap;            // Contains the current selected miniMap, null when no minimap is available
  private int[] propertyTypesOnSelectedMap = new int[]{0, 0, 0, 0, 0, 0};

  private int page;
  private int selectedMapDir;
  private File[] mapDirs;

  private JFrame frame;
  private StateManager stateManager;
  private MenuSession menuSession;
  private MouseControl mouseControl = new MouseControl();
  private KeyControl keyControl = new KeyControl();
  private PlayerSelection playerSelection;

  // todo Why are File names needed in Battle: it will load additional information from the file?
  // Battle won't work w/o them.
  private String[] allMapFilenames = new MapLoader().getFileNames();


  public MapSelectMenu(JFrame frame, StateManager stateManager, MenuSession menuSession, List<Map> maps) {
    super(NUM_VISIBLE_ITEMS);
    this.menuSession = menuSession;
    if (maps == null)
      throw new IllegalArgumentException("No maps loaded");

    this.frame = frame;
    this.stateManager = stateManager;
    this.maps = maps;
    this.playerSelection = new PlayerSelection(frame);
    this.filteredMaps = new ArrayList<Map>();

    filterMapsOnPlayerCount();
    loadDirs();
  }

  public void init() {
    frame.addKeyListener(keyControl);
    frame.addMouseListener(mouseControl);
    frame.addMouseMotionListener(mouseControl);
    playerSelection.initInput();
  }

  public void stop() {
    frame.removeKeyListener(keyControl);
    frame.removeMouseListener(mouseControl);
    frame.removeMouseMotionListener(mouseControl);
    playerSelection.clear();
  }

  private void filterMapsOnPlayerCount() {
    filteredMaps.clear();
    int playerFilter = playerSelection.getCurrentMenuItem();

    for (Map map : maps) {
      if (playerFilter == PlayerSelection.ALL || playerFilter == map.getPlayerCount() - 1) {
        filteredMaps.add(map);
      }
    }

    // After we filtered select the first item on the first page and load the minimap
    setCurrentMenuItem(0);
    page = 0;
    loadMiniMapPreview();
  }

  private void loadDirs() {
    List<File> categories = FileSystemManager.getMapCatagories();
    mapDirs = categories.toArray(new File[0]);
  }

  private void loadMiniMapPreview() {
    Arrays.fill(propertyTypesOnSelectedMap, 0);

    if (!filteredMaps.isEmpty()) {
      String fileName = getFileName(getCurrentMenuItem());
      Battle miniMapBattlePreview = new Battle(fileName);
      miniMap = miniMapBattlePreview.getMap();
      countProperties(miniMap);
    } else {
      miniMap = null;
    }
  }

  private void countProperties(Map map) {
    for (int row = 0; row < map.getMaxRow(); row++) {
      for (int col = 0; col < map.getMaxCol(); col++) {
        int terrainIndex = map.find(col, row).getTerrain().getIndex();
        if (terrainIndex >= TerrType.CITY) {
          if (terrainIndex == TerrType.CITY)
            propertyTypesOnSelectedMap[0]++;
          else if (terrainIndex == TerrType.BASE)
            propertyTypesOnSelectedMap[1]++;
          else if (terrainIndex == TerrType.PORT)
            propertyTypesOnSelectedMap[2]++;
          else if (terrainIndex == TerrType.AIRPORT)
            propertyTypesOnSelectedMap[3]++;
          else if (terrainIndex == TerrType.COM_TOWER)
            propertyTypesOnSelectedMap[4]++;
          else if (terrainIndex == TerrType.PIPE_STATION)
            propertyTypesOnSelectedMap[5]++;
        }
      }
    }
  }

  // PAINT
  public void paint(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
    int currentMenuItem = getCurrentMenuItem();

    paintBackground(g);
    playerSelection.paintMenu(g);

    if (!filteredMaps.isEmpty()) {
      paintMenu(g);
      paintMap(g, currentMenuItem);
      paintPropertyBox(g);
      paintPropertyCount(g);
      paintSelectBox(g, currentMenuItem);
      paintMiniMap(g, 180, 65);
    }
  }

  private void paintBackground(Graphics g) {
    g.drawImage(MainMenuGraphics.getMapBG(), MainMenuGraphics.MAPNAME_BG_X, MainMenuGraphics.MAPNAME_BG_Y, frame);
    g.drawImage(MainMenuGraphics.getMapSelectUpArrow(), MainMenuGraphics.MAPSELECT_UPARROW_X, MainMenuGraphics.MAPSELECT_UPARROW_Y, frame);
    g.drawImage(MainMenuGraphics.getMapSelectDownArrow(), MainMenuGraphics.MAPSELECT_DOWNARROW_X, MainMenuGraphics.MAPSELECT_DOWNARROW_Y, frame);
    g.setColor(MainMenuGraphics.getH1Color());
    g.setFont(MainMenuGraphics.getH1Font());
    g.drawString(mapDirs[selectedMapDir].getName(), MainMenuGraphics.MAPSELECT_CATEGORY_X, MainMenuGraphics.MAPSELECT_CATEGORY_Y);
  }

  private void paintMenu(Graphics2D g) {
    g.setColor(MainMenuGraphics.getH1Color());
    g.setFont(MainMenuGraphics.getH1Font());
    g.drawString(mapDirs[selectedMapDir].getName(), MainMenuGraphics.MAPSELECT_CATEGORY_X, MainMenuGraphics.MAPSELECT_CATEGORY_Y);

    for (int item = 0; item < NUM_VISIBLE_ITEMS; item++) {
      if (isValidItem(item)) {
        String fullMapName = getMap(item).getName();
        String fixedMapName = GuiUtil.fitLine(fullMapName, 148, g);
        g.drawString(fixedMapName, MENU_ITEM_LEFT_OFFSET, MENU_ITEM_TOP_OFFSET + item * MENU_ITEM_SPACING);
      }
    }
  }

  private void paintMap(Graphics2D g, int currentMenuItem) {
    if (!filteredMaps.isEmpty()) {
      Map currentMap = getMap(currentMenuItem);
      g.setColor(Color.black);
      g.drawString(currentMap.getName(), 180, 60);
      g.setFont(MainMenuGraphics.getH1Font());
      g.drawString("Mapmaker: " + currentMap.getAuthor(), 180, 245);
      g.setFont(DEFAULT_FONT);
      g.drawString(currentMap.getDescription(), 180, 265);
    }

    g.setColor(Color.white);
    g.setFont(MainMenuGraphics.getH1Font());
  }

  private void paintPropertyBox(Graphics2D g) {
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
    g.setColor(BOX_BACKGROUND);
    g.fillRoundRect(180, 275, 280, 40, 20, 20);
  }

  private void paintPropertyCount(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.setFont(MainMenuGraphics.getH1Font());
    g.drawImage(TerrainGraphics.getColoredSheet(0), 205 + 16, 284, 221 + 16, 316, 0, TerrType.getYIndex(TerrType.CITY), 16, TerrType.getYIndex(TerrType.CITY) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[0], 205, 300);
    g.drawImage(TerrainGraphics.getColoredSheet(0), 247 + 16, 284, 263 + 16, 316, 0, TerrType.getYIndex(TerrType.BASE), 16, TerrType.getYIndex(TerrType.BASE) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[1], 247, 300);
    g.drawImage(TerrainGraphics.getColoredSheet(0), 289 + 16, 284, 305 + 16, 316, 0, TerrType.getYIndex(TerrType.PORT), 16, TerrType.getYIndex(TerrType.PORT) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[2], 289, 300);
    g.drawImage(TerrainGraphics.getColoredSheet(0), 331 + 16, 284, 347 + 16, 316, 0, TerrType.getYIndex(TerrType.AIRPORT), 16, TerrType.getYIndex(TerrType.AIRPORT) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[3], 331, 300);
    g.drawImage(TerrainGraphics.getColoredSheet(0), 373 + 16, 284, 389 + 16, 316, 0, TerrType.getYIndex(TerrType.COM_TOWER), 16, TerrType.getYIndex(TerrType.COM_TOWER) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[4], 373, 300);
    g.drawImage(TerrainGraphics.getColoredSheet(0), 415 + 16, 284, 431 + 16, 316, 0, TerrType.getYIndex(TerrType.PIPE_STATION), 16, TerrType.getYIndex(TerrType.PIPE_STATION) + 32, frame);
    g.drawString("" + propertyTypesOnSelectedMap[5], 415, 300);
  }

  private void paintSelectBox(Graphics2D g, int currentMenuItem) {
    g.setColor(Color.RED);
    g.drawRect(MENU_ITEM_LEFT_OFFSET, 50 + currentMenuItem * MENU_ITEM_SPACING, 148, 19);
    g.setColor(Color.BLACK);
  }

  public void paintMiniMap(Graphics2D g, int x, int y) {
    if (!filteredMaps.isEmpty()) {
      Image minimap = MiscGraphics.getMinimap();

      for (int i = 0; i < miniMap.getMaxCol(); i++) {
        for (int j = 0; j < miniMap.getMaxRow(); j++) {
          paintTerrain(g, minimap, i, j, x, y);
          paintUnits(g, minimap, i, j, x, y);
        }
      }
    } else {
      g.drawImage(MainMenuGraphics.getNowDrawing(), x, y, frame);
    }
  }

  private void paintTerrain(Graphics2D g, Image minimap, int i, int j, int x, int y) {
    int terraintype = miniMap.find(new Location(i, j)).getTerrain().getIndex();
    if (terraintype < 9) {
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, (terraintype * 4), 0, 4 + (terraintype * 4), 4, frame);
    } else if (terraintype == 9) {
      int armycolor = ((Property) miniMap.find(new Location(i, j)).getTerrain()).getOwner().getColor();
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 36 + (armycolor * 4), 0, 40 + (armycolor * 4), 4, frame);
    } else if (terraintype < 15 || terraintype == 17) {
      int armycolor = ((Property) miniMap.find(new Location(i, j)).getTerrain()).getColor();
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 76 + (armycolor * 4), 0, 80 + (armycolor * 4), 4, frame);
    } else if (terraintype == 15) {
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 120, 0, 124, 4, frame);
    } else if (terraintype == 16) {
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 128, 0, 132, 4, frame);
    } else if (terraintype == 18) {
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 124, 0, 128, 4, frame);
    } else if (terraintype == 19) {
      g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 0, 0, 4, 4, frame);
    }
  }

  private void paintUnits(Graphics2D g, Image minimap, int col, int row, int x, int y) {
    if (miniMap.find(col, row).hasUnit()) {
      int armycolor = miniMap.find(col, row).getUnit().getArmy().getColor();
      g.drawImage(minimap, x + (col * 4), y + (row * 4), x + (col * 4) + 4, y + (row * 4) + 4, 132 + (armycolor * 4), 0, 136 + (armycolor * 4), 4, frame);
    }
  }

  // INPUT
  private class PlayerSelection extends Menu {
    public static final int ALL = 0;
    private static final int NUM_MENU_ITEMS = 10;

    private JFrame frame;
    private PlayerSelectionMouseControl playerSelectionMouseControl = new PlayerSelectionMouseControl();

    public PlayerSelection(JFrame frame) {
      super(NUM_MENU_ITEMS);
      this.frame = frame;
    }

    public void initInput() {
      frame.addMouseListener(playerSelectionMouseControl);
    }

    public void clear() {
      frame.removeMouseListener(playerSelectionMouseControl);
    }

    void paintMenu(Graphics2D g) {
      paintPlayerSelectBox(g);
      paintPlayerSelections(g, getCurrentMenuItem());
    }

    private void paintPlayerSelectBox(Graphics2D g) {
      g.setColor(BOX_BACKGROUND);
      g.fillRoundRect(180, 5, 280, 40, 20, 20);
    }

    private void paintPlayerSelections(Graphics2D g, int currentMenuItem) {
      switch (currentMenuItem) {
        case 0:
          MainMenuGraphics.drawCategories_allSelected(g);
          break;
        case 1:
          MainMenuGraphics.drawCategories_2playerSelected(g);
          break;
        case 2:
          MainMenuGraphics.drawCategories_3playerSelected(g);
          break;
        case 3:
          MainMenuGraphics.drawCategories_4playerSelected(g);
          break;
        case 4:
          MainMenuGraphics.drawCategories_5playerSelected(g);
          break;
        case 5:
          MainMenuGraphics.drawCategories_6playersSelected(g);
          break;
        case 6:
          MainMenuGraphics.drawCategories_7playerSelected(g);
          break;
        case 7:
          MainMenuGraphics.drawCategories_8PlayerSelected(g);
          break;
        case 8:
          MainMenuGraphics.drawCategories_9playerSelected(g);
          break;
        case 9:
          MainMenuGraphics.drawCategories_10playerSelected(g);
          break;
        default:
          throw new AssertionError("Could not paint current menu item: " + currentMenuItem);
      }
    }

    // INPUT
    private class PlayerSelectionMouseControl extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        int x = e.getX() - frame.getInsets().left;
        int y = e.getY() - frame.getInsets().top;

        if (SwingUtilities.isLeftMouseButton(e)) {
          boolean MAX_PLAYER_COUNT_RANGE_CLICKED = y < 40 && x > 180;
          boolean PLAYER_COUNT_1_CLICKED = x < 240;
          boolean PLAYER_COUNT_2_CLICKED = x < 260;
          boolean PLAYER_COUNT_3_CLICKED = x < 280;
          boolean PLAYER_COUNT_4_CLICKED = x < 300;
          boolean PLAYER_COUNT_5_CLICKED = x < 320;
          boolean PLAYER_COUNT_6_CLICKED = x < 340;
          boolean PLAYER_COUNT_7_CLICKED = x < 360;
          boolean PLAYER_COUNT_8_CLICKED = x < 380;
          boolean PLAYER_COUNT_9_CLICKED = x < 400;
          boolean PLAYER_COUNT_10_CLICKED = x < 480;

          if (MAX_PLAYER_COUNT_RANGE_CLICKED) {
            // change subcategory
            if (PLAYER_COUNT_1_CLICKED)
              setCurrentMenuItem(0);
            else if (PLAYER_COUNT_2_CLICKED)
              setCurrentMenuItem(1);
            else if (PLAYER_COUNT_3_CLICKED)
              setCurrentMenuItem(2);
            else if (PLAYER_COUNT_4_CLICKED)
              setCurrentMenuItem(3);
            else if (PLAYER_COUNT_5_CLICKED)
              setCurrentMenuItem(4);
            else if (PLAYER_COUNT_6_CLICKED)
              setCurrentMenuItem(5);
            else if (PLAYER_COUNT_7_CLICKED)
              setCurrentMenuItem(6);
            else if (PLAYER_COUNT_8_CLICKED)
              setCurrentMenuItem(7);
            else if (PLAYER_COUNT_9_CLICKED)
              setCurrentMenuItem(8);
            else if (PLAYER_COUNT_10_CLICKED)
              setCurrentMenuItem(9);

            filterMapsOnPlayerCount();
          }
          frame.repaint(0);
        }
      }
    }
  }

  private class KeyControl extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          menuMoveUp();
          break;
        case KeyEvent.VK_DOWN:
          menuMoveDown();
          break;
        case KeyEvent.VK_A:
          pressCurrentItem();
          break;
        case KeyEvent.VK_RIGHT:
          selectedMapDir = withinBounds(++selectedMapDir, mapDirs.length - 1);
          break;
        case KeyEvent.VK_LEFT:
          selectedMapDir = withinBounds(--selectedMapDir, mapDirs.length - 1);
          break;
      }
      // todo THIS IS FOR TESTING ONLY!!!!!!!!!!!
      // Repaint after user action, later replaced by 1 sec repaint Timer.
      frame.repaint(0);
    }
  }

  private class MouseControl extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;

      if (SwingUtilities.isLeftMouseButton(e)) {
        boolean CHANGE_DIR_RANGE_CLICKED = y < 30 && x < 180;
        boolean PAGE_UP_CLICKED = y > 30 && y < 38 && x > 84 && x < 98;
        boolean PAGE_DOWN_CLICKED = y > 312 && y < 320 && x > 84 && x < 98;
        boolean MAP_ITEM_CLICK = y > 50 && y < 302 && x < 160;

        if (CHANGE_DIR_RANGE_CLICKED) {
          selectedMapDir++;
          if (selectedMapDir > mapDirs.length - 1) selectedMapDir = 0;

          // load maps in new directory
          filterMapsOnPlayerCount();
        }


        if (PAGE_UP_CLICKED) {
          menuPageUp();
        } else if (MAP_ITEM_CLICK) {
          int clickedItem = (y - 50) / MENU_ITEM_SPACING;
          if (isVisible(clickedItem) && isValidItem(clickedItem)) {
            setCurrentMenuItem(clickedItem);
            pressCurrentItem();
          }
        } else if (PAGE_DOWN_CLICKED) {
          menuPageDown();
        }
      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("NEW_GAME");
      }
      frame.repaint(0);
    }

    public void mouseMoved(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;
      boolean MAP_ITEM_HOVER = y > 50 && y < 302 && x < 160;

      if (MAP_ITEM_HOVER) {
        int i = (y - 50) / MENU_ITEM_SPACING;
        if (isValidItem(i)) {
          if (i != getCurrentMenuItem()) {
            setCurrentMenuItem(i);
            loadMiniMapPreview();
            frame.repaint(0);
          }
        }
      }

    }
  }

  @Override
  void menuMoveUp() {
    if (isFirstItemSelected()) {
      menuPageUp();
    } else {
      super.menuMoveUp();
    }
    loadMiniMapPreview();
  }

  private void menuPageUp() {
    int newMapPage = page - 1;
    if (isValidPage(newMapPage)) {
      page = newMapPage;
      setCurrentMenuItem(NUM_VISIBLE_ITEMS - 1);
    }
  }

  @Override
  void menuMoveDown() {
    if (withinMapItems(getCurrentMenuItem() + 1)) {
      if (isLastItemSelected()) {
        menuPageDown();
      } else {
        super.menuMoveDown();
      }
    }
    loadMiniMapPreview();
  }

  private void menuPageDown() {
    int newMapPage = page + 1;
    if (isValidPage(newMapPage)) {
      page = newMapPage;
      setCurrentMenuItem(0);
    }
  }

  private void pressCurrentItem() {
    if (!filteredMaps.isEmpty()) {
      int currentMenuItem = getCurrentMenuItem();
      Map map = getMap(currentMenuItem);
      String fileName = getFileName(currentMenuItem);
      menuSession.setMap(map);
      menuSession.setMapFileName(fileName);
      stateManager.changeToState("CO_SELECT");
    }
  }

  // GETTERS
  private boolean isValidItem(int item) {
    return getMapItem(item) < filteredMaps.size();
  }

  private boolean isVisible(int item) {
    return item < NUM_VISIBLE_ITEMS;
  }

  /**
   * Checks page for bounds conditions
   */
  private boolean isValidPage(int mapPage) {
    boolean overLastPage = mapPage > filteredMaps.size() / NUM_VISIBLE_ITEMS || (mapPage == filteredMaps.size() / NUM_VISIBLE_ITEMS && filteredMaps.size() % NUM_VISIBLE_ITEMS == 0);
    return mapPage >= 0 && !overLastPage;
  }

  private boolean withinMapItems(int item) {
    return isWithinBounds(getMapItem(item), filteredMaps.size() - 1);
  }

  private boolean isWithinBounds(int item, int max) {
    return item == withinBounds(item, max);
  }

  /**
   * When going off bounds, stay on the bound value.
   */
  @Override
  int withinBounds(int item, int max) {
    if (item < 0) {
      return 0;
    } else if (item > max) {
      return max;
    } else {
      return item;
    }
  }

  private int getMapItem(int item) {
    return page * NUM_VISIBLE_ITEMS + item;
  }

  private Map getMap(int item) {
    return filteredMaps.get(getMapItem(item));
  }

  private String getFileName(int item) {
    return allMapFilenames[getMapItem(item)];
  }

  public static void main(String[] args) {
    ResourceLoader.init();
    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);

    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;

    MainMenuGraphics.loadImages(frame);
    TerrainGraphics.loadImages(frame);
    MiscGraphics.loadImages(frame);

    final MapSelectMenu mapSelectMenu = new MapSelectMenu(frame, new StateManager(frame), new MenuSession(), new MapLoader().loadAllValidMaps());
    mapSelectMenu.init();
    JPanel panel = new JPanel() {
      protected void paintComponent(Graphics g) {
        mapSelectMenu.paint((Graphics2D) g);
      }
    };
    frame.add(panel);
    frame.setSize(480, 320);
    frame.setVisible(true);
  }
}
