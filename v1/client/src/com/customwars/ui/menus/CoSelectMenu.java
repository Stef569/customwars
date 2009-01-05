package com.customwars.ui.menus;

import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.loader.MapLoader;
import com.customwars.map.Map;
import com.customwars.officer.CO;
import com.customwars.officer.COList;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.state.State;
import com.customwars.ui.state.StateManager;
import com.customwars.util.GuiUtil;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author stefan
 * @since 2.0
 */
public class CoSelectMenu implements State {
  private static final Logger logger = Logger.getLogger(CoSelectMenu.class);
  private static final int NUM_MENU_ITEMS = 15;
  private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

  private int selectedArmy;
  private boolean altcostume;
  private boolean mainaltcostume;

  private int numCOs, numPlayers;
  private CO[][] armyArray = new CO[8][14];
  private CO selectedCO;
  private int[] coSelections;
  private boolean[] altSelections;
  private int infono;
  private int cursorX, cursorY;   // Square Position(0..NUM_MENU_ITEMS)

  private JFrame frame;
  private StateManager stateManager;
  private MenuSession menuSession;
  private MouseControl mouseControl = new MouseControl();
  private KeyControl keyControl = new KeyControl();
  private int coGlide = -1;  // Used to glide the co in

  public CoSelectMenu(JFrame frame, StateManager stateManager, MenuSession menuSession) {
    this.frame = frame;
    this.stateManager = stateManager;
    this.menuSession = menuSession;
    fillArmyArray();
  }

  private void fillArmyArray() {
    for (int i = 0; i < 8; i++) {
      for (int j = 0, pos = 0; j < COList.getListing().length; j++) {
        if (COList.getListing()[j].getStyle() == i) {
          armyArray[i][pos] = COList.getListing()[j];
          pos++;
        }
      }
    }
  }

  public void init() {
    Map map = menuSession.getMap();
    if (map != null) {
      numPlayers = map.getPlayerCount();
      coSelections = new int[numPlayers * 2];
      altSelections = new boolean[numPlayers * 2];
    } else {
      throw new RuntimeException("No maps set in menuSession");
    }
    frame.addKeyListener(keyControl);
    frame.addMouseListener(mouseControl);
    frame.addMouseMotionListener(mouseControl);
  }

  public void stop() {
    frame.removeKeyListener(keyControl);
    frame.removeMouseListener(mouseControl);
    frame.removeMouseMotionListener(mouseControl);
  }

  public void paint(Graphics2D g) {
    paintGlidingCo(g);
    if (coGlide++ > 640 * 2) {
      coGlide = 0;
    }

    g.drawImage(MiscGraphics.getIntelBackground(), 0, -coGlide / 2, frame);
    g.drawImage(MiscGraphics.getIntelBackground(), 0, 640 - coGlide / 2, frame);

    int offset = 0;
    if (altcostume) offset = 225;

    paintLayout(g);
    paintCoFrameWork(g);
    paintCOs(g, offset);
    paintCursor(g);

    // Draw first CO if selecting second CO
    if (numCOs % 2 == 1) {
      g.drawImage(MiscGraphics.getCOSheet(coSelections[numCOs - 1]), 166, 210, 166 + 32, 210 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, frame);
      g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, (coSelections[numCOs - 1]) * 15, 50, (coSelections[numCOs - 1]) * 15 + 15, frame);
    }

    paintCurrentCoInfo(g, offset);
    paintPlayerNumber(g);

    g.setColor(Color.black);
    g.setFont(DEFAULT_FONT);

    int CO_INFO_BOX_WIDTH = 170, CO_INFO_BOX_TOP_OFFSET = 98;

    if (selectedCO != null) {
      String fullTxt = COList.getListing()[COList.getIndex(selectedCO)].getIntel();
      String[] multiLineTxt = GuiUtil.convertToMultiLineArray(fullTxt, CO_INFO_BOX_WIDTH, g);

      for (int lineNumber = 0; lineNumber < multiLineTxt.length - 1; lineNumber++) {
        g.drawString(multiLineTxt[lineNumber], CO_INFO_BOX_WIDTH, CO_INFO_BOX_TOP_OFFSET + lineNumber * 15);
      }

      g.setColor(Color.black);
      g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
      g.drawString(COList.getListing()[COList.getIndex(selectedCO)].getTitle(), 220, 80);
    }
  }

  private void paintGlidingCo(Graphics2D g) {
    final int dx = 329, dy = 350, dx2 = 255, dy2 = -5;
    coGlide++;
    g.drawImage(MainMenuGraphics.getMainMenuCO(Options.getMainCOID()),
            calcGlide(coGlide) + dx,
            dy2, calcGlide(coGlide) + dx + dx2,
            dy2 + dy, 0, 0,
            dx2, dy, frame);
  }

  private int calcGlide(int glide) {
    return (int) (100 * Math.pow(.95, glide));
  }

  private void paintLayout(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getCOLayout(selectedArmy), 0, 52, frame);
    g.drawImage(MainMenuGraphics.getCOBanner(), 0, 1, frame);
    for (int i = 0; i < 8; i++) {
      if (i == selectedArmy)
        g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, 0, frame);
      else
        g.drawImage(MainMenuGraphics.getArmyTag(i), 3 + i * 19, -12, frame);
    }
    g.drawImage(MainMenuGraphics.getHQBG(), 2, 61, 2 + 156, 61 + 279, 244 * selectedArmy, 0, 244 * selectedArmy + 244, 279, frame);
  }

  private void paintCoFrameWork(Graphics2D g) {
    for (int j = 0; j < 5; j++) {
      for (int i = 0; i < 3; i++) {
        g.drawImage(MainMenuGraphics.getCOSlot(selectedArmy), 2 + i * 52, 61 + j * 52, frame);
      }
    }
    g.drawImage(MainMenuGraphics.getNoCO(), 2, 61, frame);
  }

  private void paintCOs(Graphics2D g, int offset) {
    for (int i = 1; i < NUM_MENU_ITEMS; i++) {
      CO current = armyArray[selectedArmy][i - 1];
      if (current != null) {
        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(current)), 2 + i % 3 * 52, 61 + i / 3 * 52, 2 + i % 3 * 52 + 48, 61 + i / 3 * 52 + 48, offset, 350, 48 + offset, 398, frame);
      }
    }
  }

  private void paintCursor(Graphics2D g) {
    if (numCOs % 2 == 0)
      g.setColor(Color.RED);
    else
      g.setColor(Color.BLUE);
    g.drawRect(2 + cursorX * 52, 61 + cursorY * 52, 48, 48);
  }

  private void paintCurrentCoInfo(Graphics2D g, int offset) {
    if (selectedCO != null) {
      coGlide++;
      g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(selectedCO)), 339 + (int) (100 * Math.pow(0.89, coGlide)), 44, 339 + 225 + (int) (100 * Math.pow(0.89, coGlide)), 44 + 350, offset, 0, offset + 225, 350, frame);
      g.drawImage(MainMenuGraphics.getCOName(), 170, 70, 170 + 50, 70 + 15, 0, selectedCO.getId() * 15, 50, selectedCO.getId() * 15 + 15, frame);
      if (numCOs % 2 == 1) {
        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(selectedCO)), 166, 226, 166 + 32, 226 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, frame);
        g.drawImage(MainMenuGraphics.getCOName(), 199, 226, 199 + 50, 226 + 15, 0, selectedCO.getId() * 15, 50, selectedCO.getId() * 15 + 15, frame);
      } else {
        g.drawImage(MiscGraphics.getCOSheet(COList.getIndex(selectedCO)), 166, 210, 166 + 32, 210 + 12, 144 + offset, 350, 144 + offset + 32, 350 + 12, frame);
        g.drawImage(MainMenuGraphics.getCOName(), 199, 210, 199 + 50, 210 + 15, 0, selectedCO.getId() * 15, 50, selectedCO.getId() * 15 + 15, frame);
      }
    }
  }

  private void paintPlayerNumber(Graphics2D g) {
    if (numCOs / 2 < 9)
      g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy), 293, 195, 293 + 15, 195 + 10, numCOs / 2 * 15, 0, numCOs / 2 * 15 + 15, 10, frame);
    else
      g.drawImage(MainMenuGraphics.getPlayerNumber(selectedArmy), 293, 195, 293 + 30, 195 + 10, numCOs / 2 * 15, 0, numCOs / 2 * 15 + 30, 10, frame);
  }

  // INPUT
  private class KeyControl extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          //menuMoveUp();
          break;
        case KeyEvent.VK_DOWN:
          //menuMoveDown();
          break;
        case KeyEvent.VK_A:
          pressCurrentItem();
          break;
      }
      frame.repaint(0);
    }
  }

  private class MouseControl extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;

      if (SwingUtilities.isLeftMouseButton(e)) {
        boolean withinSquares = y > 61 && y < 321 && x > 2 && x < 158;
        boolean withinCoColor = x >= 3 && x <= 155 && y <= 53;

        // Find the co on x,y
        if (withinSquares) {
          pressCurrentItem();
        } else if (withinCoColor) {
          selectedArmy = (x - 3) / 19;
          if (cursorX != 0 || cursorY != 0) {
            CO temp = armyArray[selectedArmy][cursorX + cursorY * 3 - 1];
            if (temp != null) infono = COList.getIndex(temp);
          }
        }
        frame.repaint(0);
      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("START_SINGLEPLAYER_GAME");
      }
    }

    public void mouseMoved(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;
      boolean withinSquares = y > 61 && y < 321 && x > 2 && x < 158;

      if (withinSquares) {
        cursorX = (x - 2) / 52;
        cursorY = (y - 61) / 52;
        if (cursorX != 0 || cursorY != 0) {
          CO temp = armyArray[selectedArmy][cursorX + cursorY * 3 - 1];

          if (temp != null) {
            infono = COList.getIndex(temp);
            selectedCO = temp;
          }
        }
        frame.repaint(0);
      }
    }
  }

  private void pressCurrentItem() {
    boolean nosecco = false;
    CO temp = null;
    if (cursorX == 0 && cursorY == 0 && numCOs % 2 == 1) {
      coSelections[numCOs] = -1;
      nosecco = true;
    } else {
      if (cursorX == 0 && cursorY == 0) return;
      temp = armyArray[selectedArmy][cursorX + cursorY * 3 - 1];
    }

    if (nosecco || temp != null && !(numCOs % 2 == 1 && COList.getIndex(temp) == coSelections[numCOs - 1])) {
      if (!nosecco) {
        coSelections[numCOs] = COList.getIndex(temp);
      }

      altSelections[numCOs] = altcostume;
      mainaltcostume = altcostume;
      altcostume = false;
      numCOs++;
      cursorX = 0;
      cursorY = 0;

      if (numCOs == numPlayers * 2) {
        logger.info("Total No of competing COs=[" + numCOs + "]  Players=[" + numPlayers + "]");

        int[] sideSelect = {0, 0};
        if (numCOs > 4) {
          sideSelect = new int[numCOs / 2];
          for (int i = 0; i < numCOs / 2; i++) sideSelect[i] = i;
        } else {
          //no alliances allowed for 2 players
          sideSelect = new int[]{0, 1};
        }
      }
    }
  }

  public static void main(String[] args) {
    ResourceLoader.init();
    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);
    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;
    MainMenuGraphics.loadImages(frame);
    MiscGraphics.loadImages(frame);
    MenuSession menuSession = new MenuSession();

    // User selected the first map...
    Map map = new MapLoader().loadAllValidMaps().get(0);
    menuSession.setMap(map);

    final CoSelectMenu coSelect = new CoSelectMenu(frame, new StateManager(frame), menuSession);
    coSelect.init();
    JPanel panel = new JPanel() {
      protected void paintComponent(Graphics g) {
        coSelect.paint((Graphics2D) g);
      }
    };
    frame.add(panel);
    frame.setSize(480, 320);
    frame.setVisible(true);
  }
}
