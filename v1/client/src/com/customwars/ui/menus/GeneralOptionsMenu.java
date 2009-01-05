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
import com.customwars.util.GuiUtil;

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
  private static final Color HIGHLIGHT_COLOR = Color.RED;
  private static final int NUM_MENU_ITEMS = 19;
  private JFrame frame;
  private StateManager stateManager;

  private KeyControl keyControl = new KeyControl();
  private MouseControl mouseControl = new MouseControl();

  // Layout
  private int LEFT_OFFSET = 10, TOP_OFFSET = 20;
  private int line;
  private int fontHeight;
  private int rightPageLeftOffset;

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
    this.fontHeight = g.getFontMetrics(MainMenuGraphics.getH1Font()).getHeight();
    paintLines(getCurrentMenuItem(), g);
  }

  private void paintLines(int currentMenuItem, Graphics2D g) {
    line = 0;
    rightPageLeftOffset = 0;
    drawOptionLine("Music", Options.isMusicOn() ? "On" : "Off", currentMenuItem == 0, g);
    drawOptionLine("Random Numbers", "", currentMenuItem == 1, g);
    drawOptionLine("Balance Mode", Options.isBalance() ? "On" : "Off", currentMenuItem == 2, g);
    drawOptionLine("Set IP", Options.getDisplayIP(), currentMenuItem == 3, g);
    drawOptionLine("Autosave", Options.isAutosaveOn() ? "On" : "Off", currentMenuItem == 4, g);
    drawOptionLine("Record Replay", Options.isRecording() ? "On" : "Off", currentMenuItem == 5, g);
    drawOptionLine("Cursor", "", currentMenuItem == 6, g);
    g.drawImage(MiscGraphics.getCursor(), 70, 120, frame);
    drawOptionLine("Remap Keys", "", currentMenuItem == 7, g);
    drawOptionLine("Snail Mode Server", Options.getServerName(), currentMenuItem == 8, g);
    drawOptionLine("Default Bans", getDefaultBans(), currentMenuItem == 9, g);
    drawOptionLine("Main Screen CO", COList.getListing()[Options.getMainCOID()].getName(), currentMenuItem == 10, g);
    drawOptionLine("Sound Effects", SFX.getMute() ? "On" : "Off", currentMenuItem == 11, g);
    drawOptionLine("Battle Background Image", Options.battleBackground ? "On" : "Off", currentMenuItem == 12, g);
    drawOptionLine("Default Username/Password", Options.getDefaultUsername() + " / " + Options.getDefaultPassword(), currentMenuItem == 13, g);

    line = 0;
    rightPageLeftOffset = 235;
    drawOptionLine("Terrain Tileset", getSelectedTerrainSet(), currentMenuItem == 14, g);
    drawOptionLine("Urban Tileset", getSelectedUrbanTerrainSet(), currentMenuItem == 15, g);
    drawOptionLine("HQ Tileset", getSelectedHqTerrainSet(), currentMenuItem == 16, g);
    drawOptionLine("Use Default Login Info", Options.isDefaultLoginOn() ? "On" : "Off", currentMenuItem == 17, g);
    drawOptionLine("AutoRefresh", Options.getRefresh() ? "On" : "Off", currentMenuItem == 18, g);
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

  private void drawOptionLine(String option, String val, boolean highLight, Graphics g) {
    option += ": ";
    setColor(highLight, g);
    g.drawString(option, LEFT_OFFSET + rightPageLeftOffset, TOP_OFFSET + line * fontHeight);
    int optionWidth = GuiUtil.getStringWidth(option, g);

    g.setColor(Color.BLACK);
    if (val != null) {
      g.drawString(val, LEFT_OFFSET + rightPageLeftOffset + optionWidth, TOP_OFFSET + line * fontHeight);
    }
    line++;
  }

  private void setColor(boolean highLight, Graphics g) {
    if (highLight) {
      g.setColor(HIGHLIGHT_COLOR);
    } else {
      g.setColor(Color.BLACK);
    }
  }

  // INPUT
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
      }
      frame.repaint(0);
    }
  }

  private class MouseControl extends MouseAdapter {
    private static final int SPLASH_NEW_GAME_WIDTH_START = 160;

    public void mouseClicked(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;

      if (SwingUtilities.isLeftMouseButton(e)) {
        if (x < 220) {
          int i = y / 20;
          if (i < SPLASH_NEW_GAME_WIDTH_START) {
            setCurrentMenuItem(i);
          }

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
      case 6:
        Options.incrementCursor();
      case 10:
        Options.decrementCO();
      case 11:
        SFX.toggleMute();
      case 14:
        Options.decrementTerrain();
      case 15:
        Options.decrementUrban();
      case 16:
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
