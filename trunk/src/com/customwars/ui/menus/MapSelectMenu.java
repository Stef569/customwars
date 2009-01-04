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
import com.customwars.ui.menu.MenuSession;
import com.customwars.util.GuiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains a list of mapItems
 * Each group of NUM_VISIBLE_ITEMS is one mapPage.
 * When
 * the Mouse down button is pressed or
 * we are at the last item in the list and the down arrow is pressed
 * move 1 mapPage down.
 *
 * @author stefan
 * @since 1.0
 */
public class MapSelectMenu extends Menu {
    private static final Logger logger = LoggerFactory.getLogger(MapSelectMenu.class);
    private static final int NUM_VISIBLE_ITEMS = 12;

    private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    private static final Color BOX_BACKGROUND = new Color(7, 66, 97);

    private List<Map> maps = new MapLoader().loadAllValidMaps();
    private List<Map> filteredMaps; // Contains a sub list of all the maps, can be filtered by the user
    private Map currentSelectedMap; // Contains the current selected map, null when no map is available
    private int mapPage;
    private int selectedMapDir;
    private File[] mapDirs;
    private int[] propertyTypesOnSelectedMap = new int[]{0, 0, 0, 0, 0, 0};

    private PlayerSelection playerSelection = new PlayerSelection();
    private JFrame frame;
    private MenuSession menuSession;

    // todo Why are File names needed in Battle: it will load additional information from the file
    // Battle won't work w/o them.
    private String[] allMapFilenames = new MapLoader().getFileNames();

    public MapSelectMenu(JFrame frame, MenuSession menuSession) {
        super(NUM_VISIBLE_ITEMS);
        this.frame = frame;
        frame.addKeyListener(new KeyControl());
        this.menuSession = menuSession;
        this.filteredMaps = new ArrayList<Map>();

        loadMapDisplayNames();
        loadCategories();
    }

    /**
     * Loads the display names, filter on subCat, aka the playerCount
     */
    private void loadMapDisplayNames() {
        if (maps == null) logger.warn("No maps loaded");
        filteredMaps.clear();

        for (Map map : maps) {
            if (selectedMapDir == 0 || selectedMapDir == map.getPlayerCount() - 1) {
                filteredMaps.add(map);
            }
        }
        loadMiniMapPreview();
    }

    private void loadCategories() {
        List<File> categories = FileSystemManager.getMapCatagories();
        mapDirs = categories.toArray(new File[0]);
    }

    private void loadMiniMapPreview() {
        Arrays.fill(propertyTypesOnSelectedMap, 0);

        if (filteredMaps.size() != 0) {
            String fileName = getFileName(getCurrentMenuItem());
            Battle miniMapBattlePreview = new Battle(fileName);
            currentSelectedMap = miniMapBattlePreview.getMap();
            countProperties(currentSelectedMap);
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

    // Paint
    protected void paintMenu(Graphics2D g) {
        g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
        int currentMenuItem = getCurrentMenuItem();
        paintMapSelectScreen(g, currentMenuItem);
        paintPropertyBox(g);
        paintPropertyCount(g);
        paintSelectBox(g, currentMenuItem);
        paintMiniMap(g, 180, 65);
        playerSelection.paintMenu(g);
    }

    public void paintMapSelectScreen(Graphics2D g, int currentMenuItem) {
        g.drawImage(MainMenuGraphics.getMapBG(), MainMenuGraphics.MAPNAME_BG_X, MainMenuGraphics.MAPNAME_BG_Y, frame);
        g.drawImage(MainMenuGraphics.getMapSelectUpArrow(), MainMenuGraphics.MAPSELECT_UPARROW_X, MainMenuGraphics.MAPSELECT_UPARROW_Y, frame);
        g.drawImage(MainMenuGraphics.getMapSelectDownArrow(), MainMenuGraphics.MAPSELECT_DOWNARROW_X, MainMenuGraphics.MAPSELECT_DOWNARROW_Y, frame);

        g.setColor(MainMenuGraphics.getH1Color());
        g.setFont(MainMenuGraphics.getH1Font());

        g.drawString(mapDirs[selectedMapDir].getName(), MainMenuGraphics.MAPSELECT_CATEGORY_X, MainMenuGraphics.MAPSELECT_CATEGORY_Y);

        for (int item = 0; item < NUM_VISIBLE_ITEMS; item++) {
            if (isMapItemVisible(item)) {
                String fullMapName = getMap(item).getName();
                String fixedMapName = GuiUtil.fitLine(fullMapName, 148, g);
                g.drawString(fixedMapName, 10, 68 + item * 21);
            }
        }

        if (filteredMaps.size() != 0) {
            g.setColor(Color.black);
            g.drawString(getMap(currentMenuItem).getName(), 180, 60);
            g.setFont(MainMenuGraphics.getH1Font());
            g.drawString("Mapmaker: " + getMap(currentMenuItem).getName(), 180, 245);
            g.setFont(DEFAULT_FONT);
            g.drawString(getMap(currentMenuItem).getDescription(), 180, 265);
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
        g.drawRect(10, 50 + currentMenuItem * 21, 148, 19);
        g.setColor(Color.BLACK);
    }

    public void paintMiniMap(Graphics2D g, int x, int y) {
        if (filteredMaps.size() != 0) {
            Image minimap = MiscGraphics.getMinimap();

            for (int i = 0; i < currentSelectedMap.getMaxCol(); i++) {
                for (int j = 0; j < currentSelectedMap.getMaxRow(); j++) {
                    paintTerrain(g, minimap, i, j, x, y);
                    paintUnits(g, minimap, i, j, x, y);
                }
            }
        } else {
            g.drawImage(MainMenuGraphics.getNowDrawing(), x, y, frame);
        }
    }

    private void paintTerrain(Graphics2D g, Image minimap, int i, int j, int x, int y) {
        int terraintype = currentSelectedMap.find(new Location(i, j)).getTerrain().getIndex();
        if (terraintype < 9) {
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, (terraintype * 4), 0, 4 + (terraintype * 4), 4, frame);
        } else if (terraintype == 9) {
            int armycolor = ((Property) currentSelectedMap.find(new Location(i, j)).getTerrain()).getOwner().getColor();
            g.drawImage(minimap, x + (i * 4), y + (j * 4), x + (i * 4) + 4, y + (j * 4) + 4, 36 + (armycolor * 4), 0, 40 + (armycolor * 4), 4, frame);
        } else if (terraintype < 15 || terraintype == 17) {
            int armycolor = ((Property) currentSelectedMap.find(new Location(i, j)).getTerrain()).getColor();
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
        if (currentSelectedMap.find(col, row).hasUnit()) {
            int armycolor = currentSelectedMap.find(col, row).getUnit().getArmy().getColor();
            g.drawImage(minimap, x + (col * 4), y + (row * 4), x + (col * 4) + 4, y + (row * 4) + 4, 132 + (armycolor * 4), 0, 136 + (armycolor * 4), 4, frame);
        }
    }

    private class PlayerSelection extends Menu {
        private static final int NUM_MENU_ITEMS = 10;

        protected PlayerSelection() {
            super(NUM_MENU_ITEMS);
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
    }

    // INPUT
    private class KeyControl extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int keypress = e.getKeyCode();
            switch (keypress) {
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

    private void pressCurrentItem() {
    }

    // Getters
    private Map getMap(int item) {
        return filteredMaps.get(mapPage * NUM_VISIBLE_ITEMS + item);
    }

    private boolean isMapItemVisible(int item) {
        return mapPage * NUM_VISIBLE_ITEMS + item < filteredMaps.size();
    }

    private String getFileName(int item) {
        return allMapFilenames[mapPage * NUM_VISIBLE_ITEMS + item];
    }

    private boolean isOverLastPage(int item) {
        return item > filteredMaps.size() / NUM_VISIBLE_ITEMS || (item == filteredMaps.size() / NUM_VISIBLE_ITEMS && filteredMaps.size() % NUM_VISIBLE_ITEMS == 0);
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
        final Menu mainMenu = new MapSelectMenu(frame, new MenuSession());
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                mainMenu.paintMenu((Graphics2D) g);
            }
        };
        frame.add(panel);
        frame.setSize(480, 320);
        frame.setVisible(true);
    }
}
