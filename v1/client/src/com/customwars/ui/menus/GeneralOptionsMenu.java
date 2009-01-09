package com.customwars.ui.menus;

import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.officer.COList;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.state.State;
import com.customwars.ui.state.StateManager;

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
public class GeneralOptionsMenu extends Menu implements State {
  private static final int NUM_MENU_ITEMS_ON_1_PAGE = 14;
  private static final int ROW_HEIGHT = 20;

  private static final int MUSIC = 0;
  private static final int RANDOM_NUMBER = 1;
  private static final int BALANCED_MODE = 2;
  private static final int SET_IP = 3;
  private static final int AUTO_SAVE = 4;
  private static final int RECORD_REPLAY = 5;
  private static final int CURSOR = 6;
  private static final int REMAP_KEYS = 7;
  private static final int SNAIL_MODE = 8;
  private static final int DEFAULT_BANS = 9;
  private static final int MAIN_SCREEN_CO = 10;
  private static final int SOUND_EFFECTS = 11;
  private static final int BATTLE_BACKGROUND_IMAGE = 12;
  private static final int DEFAULT_USERNAME_PASSWORD = 13;
  private static final int TERRAIN_TILESET = 14;
  private static final int URBAN_TILESET = 15;
  private static final int HQ_TILESET = 16;
  private static final int DEFAULT_LOGIN = 17;
  private static final int AUTO_REFRESH = 18;
  private static final int NUM_MENU_ITEMS = 19;

  private JFrame frame;
  private StateManager stateManager;
  private KeyControl keyControl = new KeyControl();
  private MouseControl mouseControl = new MouseControl();

  public GeneralOptionsMenu(JFrame frame, StateManager stateManager) {
    super(NUM_MENU_ITEMS);
    this.frame = frame;
    this.stateManager = stateManager;
  }

  public void init() {
    frame.addKeyListener(keyControl);
    frame.addMouseListener(mouseControl);
  }

  public void stop() {
    frame.removeKeyListener(keyControl);
    frame.removeMouseListener(mouseControl);
  }

  // PAINT
  public void paint(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
    g.setColor(Color.black);
    g.setFont(MainMenuGraphics.getH1Font());

    paintLines(getCurrentMenuItem(), g);
  }

  private void paintLines(int currentMenuItem, Graphics2D g) {
    super.resetLines();
    drawConfigLine("Music", Options.isMusicOn() ? "On" : "Off", currentMenuItem == 0, g);
    drawConfigLine("Random Numbers", "", currentMenuItem == 1, g);
    drawConfigLine("Balance Mode", Options.isBalance() ? "On" : "Off", currentMenuItem == 2, g);
    drawConfigLine("Set IP", Options.getDisplayIP(), currentMenuItem == 3, g);
    drawConfigLine("Autosave", Options.isAutosaveOn() ? "On" : "Off", currentMenuItem == 4, g);
    drawConfigLine("Record Replay", Options.isRecording() ? "On" : "Off", currentMenuItem == 5, g);
    drawConfigLine("Cursor", "", currentMenuItem == 6, g);
    g.drawImage(MiscGraphics.getCursor(), 70, 120, frame);
    drawConfigLine("Remap Keys", "", currentMenuItem == 7, g);
    drawConfigLine("Snail Mode Server", Options.getServerName(), currentMenuItem == 8, g);
    drawConfigLine("Default Bans", getDefaultBans(), currentMenuItem == 9, g);
    drawConfigLine("Main Screen CO", COList.getListing()[Options.getMainCOID()].getName(), currentMenuItem == 10, g);
    drawConfigLine("Sound Effects", SFX.getMute() ? "On" : "Off", currentMenuItem == 11, g);
    drawConfigLine("Battle Background Image", Options.battleBackground ? "On" : "Off", currentMenuItem == 12, g);
    drawConfigLine("Default Username/Password", Options.getDefaultUsername() + " / " + Options.getDefaultPassword(), currentMenuItem == 13, g);

    super.startNewPage();
    drawConfigLine("Terrain Tileset", getSelectedTerrainSet(), currentMenuItem == 14, g);
    drawConfigLine("Urban Tileset", getSelectedUrbanTerrainSet(), currentMenuItem == 15, g);
    drawConfigLine("HQ Tileset", getSelectedHqTerrainSet(), currentMenuItem == 16, g);
    drawConfigLine("Use Default Login Info", Options.isDefaultLoginOn() ? "On" : "Off", currentMenuItem == 17, g);
    drawConfigLine("AutoRefresh", Options.getRefresh() ? "On" : "Off", currentMenuItem == 18, g);
  }

  private String getSelectedHqTerrainSet() {
    if (Options.getSelectedHQ() == 0)
      return "CW";
    else if (Options.getSelectedHQ() == 1)
      return "AWDS";
    else if (Options.getSelectedHQ() == 2)
      return Options.getCustomHQString();
    else
      return "";
  }

  private String getDefaultBans() {
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
    return bans;
  }

  private String getSelectedTerrainSet() {
    if (Options.getSelectedTerrain() == 0)
      return "CW";
    else if (Options.getSelectedTerrain() == 1)
      return "AWDS";
    else if (Options.getSelectedTerrain() == 2)
      return Options.getCustomTerrainString();
    else {
      return "";
    }
  }

  private String getSelectedUrbanTerrainSet() {
    if (Options.getSelectedUrban() == 0)
      return "CW";
    else if (Options.getSelectedUrban() == 1)
      return "AWDS";
    else if (Options.getSelectedUrban() == 2)
      return Options.getCustomUrbanString();
    else
      return "";
  }

  // INPUT
  private class KeyControl extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      int keypress = e.getKeyCode();
      if (keypress == Options.up) {
        menuMoveUp();
      } else if (keypress == Options.down) {
        menuMoveDown();
      } else if (keypress == Options.akey) {
        pressCurrentItem();
      }
      frame.repaint(0);
    }
  }

  private class MouseControl extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;
      boolean LEFT_COLUMN_CLICKED = x < 220;
      boolean RIGHT_COLUMN_CLICKED = x > 245 && x < 460;

      if (SwingUtilities.isLeftMouseButton(e)) {
        int rowClickedOn = y / ROW_HEIGHT;
        if (LEFT_COLUMN_CLICKED) {
          setCurrentMenuItem(rowClickedOn);
          pressCurrentItem();
          frame.repaint(0);
        } else if (RIGHT_COLUMN_CLICKED) {
          setCurrentMenuItem(NUM_MENU_ITEMS_ON_1_PAGE + rowClickedOn);
          pressCurrentItem();
          frame.repaint(0);
        }
      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("MAIN_MENU");
      }
    }
  }

  private void pressCurrentItem() {
    int currentMenuItem = getCurrentMenuItem();
    switch (currentMenuItem) {
      case CURSOR:
        Options.decrementCursor();
      case MAIN_SCREEN_CO:
        Options.decrementCO();
      case MUSIC:
        SFX.toggleMute();
      case TERRAIN_TILESET:
        Options.decrementTerrain();
      case URBAN_TILESET:
        Options.decrementUrban();
      case HQ_TILESET:
        Options.decrementHQ();
    }
  }

  public static void main(String[] args) {
    ResourceLoader.init();
    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);
    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;
    MiscGraphics.loadImages(frame);
    MainMenuGraphics.loadImages(frame);
    final GeneralOptionsMenu generalOptionsMenu = new GeneralOptionsMenu(frame, new StateManager(frame));
    generalOptionsMenu.init();
    JPanel panel = new JPanel() {
      protected void paintComponent(Graphics g) {
        generalOptionsMenu.paint((Graphics2D) g);
      }
    };
    frame.add(panel);
    frame.setSize(485, 350);
    frame.setVisible(true);
  }
}
