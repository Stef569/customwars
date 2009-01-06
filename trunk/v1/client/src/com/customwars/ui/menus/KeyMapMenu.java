package com.customwars.ui.menus;

import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
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
 * Maps a cw key to a user chosen key
 *
 * @author stefan
 * @since 2.0
 */
public class KeyMapMenu extends Menu implements State {
  private static final int NUM_MENU_ITEMS_ON_1_PAGE = 13;
  private static final int ROW_HEIGHT = 20;

  private static final int UP = 0;
  private static final int DOWN = 1;
  private static final int LEFT = 2;
  private static final int RIGHT = 3;
  private static final int SELECT = 4;
  private static final int CANCEL = 5;
  private static final int PGUP = 6;
  private static final int PGDN = 7;
  private static final int ALT_LEFT = 8;
  private static final int ALT_RIGHT = 9;
  private static final int MENU = 10;
  private static final int MINI_MAP = 11;
  private static final int CONST_MODE = 12;
  private static final int DEL_UNIT = 13;
  private static final int TERRAIN_MENU = 14;
  private static final int SIDE_MENU = 15;
  private static final int UNIT_MENU = 16;
  private static final int NEXT_UNIT = 17;
  private static final int NUM_MENU_ITEMS = 18;

  private JFrame frame;
  private StateManager stateManager;
  private KeyControl keyControl = new KeyControl();
  private MouseControl mouseControl = new MouseControl();

  private boolean acceptingNewKey;
  private int newKeyCode;

  protected KeyMapMenu(JFrame frame, StateManager stateManager) {
    super(NUM_MENU_ITEMS);
    this.frame = frame;
    this.stateManager = stateManager;
  }

  public void init() {
    frame.addKeyListener(keyControl);
    frame.addMouseListener(mouseControl);
  }

  public void stop() {
    acceptingNewKey = false;
    frame.removeKeyListener(keyControl);
    frame.removeMouseListener(mouseControl);
  }

  // PAINT
  public void paint(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
    g.setColor(MainMenuGraphics.getH1Color());
    g.setFont(MainMenuGraphics.getH1Font());

    paintLines(getCurrentMenuItem(), g);
    paintUsage(g);

    if (acceptingNewKey) {
      drawLine("press new Key...", g);
    }
  }

  private void paintLines(int currentMenuItem, Graphics g) {
    super.resetLines();
    drawOptionLine("Up", KeyEvent.getKeyText(Options.up), currentMenuItem == 0, g);
    drawOptionLine("Down", KeyEvent.getKeyText(Options.down), currentMenuItem == 1, g);
    drawOptionLine("Left", KeyEvent.getKeyText(Options.left), currentMenuItem == 2, g);
    drawOptionLine("Right", KeyEvent.getKeyText(Options.right), currentMenuItem == 3, g);
    drawOptionLine("Select Button", KeyEvent.getKeyText(Options.akey), currentMenuItem == 4, g);
    drawOptionLine("Cancel Button", KeyEvent.getKeyText(Options.bkey), currentMenuItem == 5, g);
    drawOptionLine("Page Up", KeyEvent.getKeyText(Options.pgup), currentMenuItem == 6, g);
    drawOptionLine("Page Down", KeyEvent.getKeyText(Options.pgdn), currentMenuItem == 7, g);
    drawOptionLine("<", KeyEvent.getKeyText(Options.altleft), currentMenuItem == 8, g);
    drawOptionLine(">", KeyEvent.getKeyText(Options.altright), currentMenuItem == 9, g);
    drawOptionLine("Menu", KeyEvent.getKeyText(Options.menu), currentMenuItem == 10, g);
    drawOptionLine("Minimap", KeyEvent.getKeyText(Options.minimap), currentMenuItem == 11, g);
    drawOptionLine("Constant Mode", KeyEvent.getKeyText(Options.constmode), currentMenuItem == 12, g);

    super.startNewPage();
    drawOptionLine("Delete Unit", KeyEvent.getKeyText(Options.delete), currentMenuItem == 13, g);
    drawOptionLine("Terrain Menu", KeyEvent.getKeyText(Options.tkey), currentMenuItem == 14, g);
    drawOptionLine("Side Menu", KeyEvent.getKeyText(Options.skey), currentMenuItem == 15, g);
    drawOptionLine("Unit Menu", KeyEvent.getKeyText(Options.ukey), currentMenuItem == 16, g);
    drawOptionLine("Next Unit", KeyEvent.getKeyText(Options.nextunit), currentMenuItem == 17, g);
  }

  private void paintUsage(Graphics g) {
    skipLines(4);
    drawLine("Usage:", g);
    drawLine("1. Select the key", g);
    drawLine("2. Press the select key", g);
    drawLine("3. Press the new key", g);
  }

  // INPUT
  private class KeyControl extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      if (acceptingNewKey) {
        newKeyCode = e.getKeyCode();
        pressCurrentItem();
        frame.repaint(0);
        return;
      }
      int keyCode = e.getKeyCode();
      if (keyCode == Options.up) {
        menuMoveUp();
      } else if (keyCode == Options.down) {
        menuMoveDown();
      } else if (keyCode == Options.akey) {
        acceptingNewKey = true;
      } else if (keyCode == Options.bkey) {
        acceptingNewKey = false;
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
        } else if (RIGHT_COLUMN_CLICKED) {
          setCurrentMenuItem(NUM_MENU_ITEMS_ON_1_PAGE + rowClickedOn);
        }

        if (LEFT_COLUMN_CLICKED || RIGHT_COLUMN_CLICKED) {
          acceptingNewKey = !acceptingNewKey;
          frame.repaint(0);
        }

      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("OPTIONS");
      }
    }
  }

  /**
   * Invoked when acceptingNewKey = true
   */
  private void pressCurrentItem() {
    switch (getCurrentMenuItem()) {
      case UP:
        Options.up = newKeyCode;
        break;
      case DOWN:
        Options.down = newKeyCode;
        break;
      case LEFT:
        Options.left = newKeyCode;
        break;
      case RIGHT:
        Options.right = newKeyCode;
        break;
      case SELECT:
        Options.akey = newKeyCode;
        break;
      case CANCEL:
        Options.bkey = newKeyCode;
        break;
      case PGDN:
        Options.pgdn = newKeyCode;
        break;
      case PGUP:
        Options.pgup = newKeyCode;
        break;
      case ALT_LEFT:
        Options.altleft = newKeyCode;
        break;
      case ALT_RIGHT:
        Options.altright = newKeyCode;
        break;
      case MENU:
        Options.menu = newKeyCode;
        break;
      case MINI_MAP:
        Options.minimap = newKeyCode;
        break;
      case CONST_MODE:
        Options.constmode = newKeyCode;
        break;
      case DEL_UNIT:
        Options.delete = newKeyCode;
        break;
      case TERRAIN_MENU:
        Options.tkey = newKeyCode;
        break;
      case SIDE_MENU:
        Options.skey = newKeyCode;
        break;
      case UNIT_MENU:
        Options.ukey = newKeyCode;
        break;
      case NEXT_UNIT:
        Options.nextunit = newKeyCode;
        break;
    }
    acceptingNewKey = false;
  }

  public static void main(String[] args) {
    ResourceLoader.init();
    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);
    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;
    MiscGraphics.loadImages(frame);
    MainMenuGraphics.loadImages(frame);
    final State keyMapMenu = new KeyMapMenu(frame, new StateManager(frame));
    keyMapMenu.init();
    JPanel panel = new JPanel() {
      protected void paintComponent(Graphics g) {
        keyMapMenu.paint((Graphics2D) g);
      }
    };
    frame.add(panel);
    frame.setSize(485, 350);
    frame.setVisible(true);
  }
}
