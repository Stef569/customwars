package com.customwars.ui.menus;

import com.customwars.ai.BaseDMG;
import com.customwars.ai.BattleOptions;
import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.UnitGraphics;
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
public class BattleOptionsMenu extends Menu implements State {
  private static final int NUM_MENU_ITEMS = 16;
  private static final int ROW_HEIGHT = 20;
  private static final int TILE_SIZE = 16;
  private static final int HORIZONTAL_PAGE_LEFT_OFFSET = 200;
  private static final int NUM_ITEMS_ON_1_PAGE = 13;

  private BattleOptions battleOptions = new BattleOptions();  //the battle options to start the game with

  private JFrame frame;
  private StateManager stateManager;
  private MenuSession menuSession;
  private KeyControl keyControl = new KeyControl();
  private MouseControl mouseControl = new MouseControl();

  private int unitBanPosX, unitBanPosY;
  private int visibility;

  public BattleOptionsMenu(JFrame frame, StateManager stateManager, MenuSession menuSession) {
    super(NUM_MENU_ITEMS);
    this.menuSession = menuSession;
    super.setHorizontalPageLeftOffset(HORIZONTAL_PAGE_LEFT_OFFSET);
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

  public void paint(Graphics2D g) {
    g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
    int currentMenuItem = getCurrentMenuItem();
    g.setColor(Color.black);
    g.setFont(MainMenuGraphics.getH1Font());

    paintLines(g, currentMenuItem);
  }

  private void paintLines(Graphics2D g, int currentMenuItem) {
    super.resetLines();
    drawConfigLine("Visibility", getVisibility(), currentMenuItem == 0, g);
    drawConfigLine("Weather", getWeather(), currentMenuItem == 1, g);
    drawConfigLine("Funds", battleOptions.getFundsLevel() + "", currentMenuItem == 2, g);
    drawConfigLine("Start Funds", battleOptions.getStartFunds() + "", currentMenuItem == 3, g);
    drawConfigLine("Turn Limit", battleOptions.getTurnLimit() > 0 ? battleOptions.getTurnLimit() + "" : "Off", currentMenuItem == 4, g);
    drawConfigLine("Capture Limit", battleOptions.getCapLimit() > 0 ? battleOptions.getCapLimit() + "" : "Off", currentMenuItem == 5, g);
    drawConfigLine("CO Powers", battleOptions.isCOP() ? "On" + "" : "Off", currentMenuItem == 6, g);
    drawConfigLine("Balance Mode", battleOptions.isBalance() ? "On" + "" : "Off", currentMenuItem == 7, g);
    drawConfigLine("Record Replay", battleOptions.isRecording() ? "On" : "Off", currentMenuItem == 8, g);
    paintUnitBans(g);
    skipLines(2);

    drawConfigLine("Snow Chance", battleOptions.getSnowChance() + "%", currentMenuItem == 10, g);
    drawConfigLine("Rain Chance", battleOptions.getRainChance() + "%", currentMenuItem == 11, g);
    drawConfigLine("Sandstorm Chance", battleOptions.getSandChance() + "%", currentMenuItem == 12, g);

    super.startNewPage();
    drawConfigLine("Min Weather Duration", battleOptions.getMinWTime() + " Days", currentMenuItem == 13, g);
    drawConfigLine("Max Weather Duration", battleOptions.getMaxWTime() + " Days", currentMenuItem == 14, g);
    drawConfigLine("Weather Start", "Day: " + battleOptions.getMinWDay(), currentMenuItem == 15, g);
  }

  private void paintUnitBans(Graphics2D g) {
    int currentTopOffset = getTopOffset(g);

    Image isheet = UnitGraphics.getUnitImage(0, 0);
    Image usheet = UnitGraphics.getUnitImage(2, 0);
    for (int i = 0; i < 2; i++) {
      g.drawImage(isheet, 10 + i * 16, currentTopOffset - 16, 26 + i * 16, currentTopOffset, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, frame);
      if (battleOptions.isUnitBanned(i)) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.fillRect(10 + i * 16, currentTopOffset - 16, 16, 16);
        g.setComposite(AlphaComposite.SrcOver);
      }
    }
    for (int i = 2; i < BaseDMG.NUM_UNITS; i++) {
      if (i < BaseDMG.NUM_UNITS / 2) {
        g.drawImage(usheet, 10 + i * 16, currentTopOffset - 16, 26 + i * 16, currentTopOffset, 0, UnitGraphics.findYPosition(i, 0), 16, UnitGraphics.findYPosition(i, 0) + 16, frame);
        if (battleOptions.isUnitBanned(i)) {
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
          g.fillRect(10 + i * 16, currentTopOffset - 16, 16, 16);
          g.setComposite(AlphaComposite.SrcOver);
        }
      } else {
        g.drawImage(usheet, 10 + (i - BaseDMG.NUM_UNITS / 2) * 16, currentTopOffset + 4, 26 + (i - BaseDMG.NUM_UNITS / 2) * 16, currentTopOffset + 20, 0, UnitGraphics.findYPosition(i, 0), 16,
                UnitGraphics.findYPosition(i, 0) + 16, frame);
        if (battleOptions.isUnitBanned(i)) {
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
          g.fillRect(10 + (i - BaseDMG.NUM_UNITS / 2) * 16, currentTopOffset + 4, 16, 16);
          g.setComposite(AlphaComposite.SrcOver);
        }
      }
    }

    if (getCurrentMenuItem() == 9) {
      g.setColor(Color.red);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
      g.fillRect(10 + (unitBanPosX % (BaseDMG.NUM_UNITS / 2)) * 16, currentTopOffset - 16 + 20 * unitBanPosY, 16, 16);
      g.setComposite(AlphaComposite.SrcOver);
      g.setColor(Color.black);
    }
  }

  private String getVisibility() {
    if (battleOptions.isMist())
      return "Mist of War";
    else if (battleOptions.isFog())
      return "Fog of War";
    else
      return "Full";
  }

  private String getWeather() {
    if (battleOptions.getWeatherType() == 0)
      return "Clear";
    else if (battleOptions.getWeatherType() == 1)
      return "Rain";
    else if (battleOptions.getWeatherType() == 2)
      return "Snow";
    else if (battleOptions.getWeatherType() == 3)
      return "Sandstorm";
    else if (battleOptions.getWeatherType() == 4)
      return "Random";
    else
      return "Undefined";
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
      } else if (keypress == Options.left) {
        leftKeypressActions();
      } else if (keypress == Options.right) {
        rightKeypressActions();
      }
      frame.repaint(0);
    }
  }

  private void leftKeypressActions() {
    switch (getCurrentMenuItem()) {
      case 2:
        int funds = withinBounds(battleOptions.getFundsLevel() - 500, 10000);
        battleOptions.setFundsLevel(funds);
        break;
      case 3:
        int startFunds = withinBounds(battleOptions.getStartFunds() - 500, 30000);
        battleOptions.setStartFunds(startFunds);
        break;
      case 4:
        battleOptions.setTurnLimit(battleOptions.getTurnLimit() - 1);
        break;
      case 5:
        battleOptions.setCapLimit(battleOptions.getCapLimit() - 1);
        break;
      case 9:
        unitBanPosX--;
        validateUnitBanPositon();
        break;
      case 10:
        int snowChangePerc = withinBounds(battleOptions.getSnowChance() - 1, 100);
        battleOptions.setSnowChance(snowChangePerc);
        break;
      case 11:
        int rainChangePerc = withinBounds(battleOptions.getRainChance() - 1, 100);
        battleOptions.setRainChance(rainChangePerc);
        break;
      case 12:
        int sandChangePerc = withinBounds(battleOptions.getSandChance() - 1, 100);
        battleOptions.setSandChance(sandChangePerc);
        break;
      case 13:
        battleOptions.setMinWTime(battleOptions.getMinWTime() - 1);
        break;
      case 14:
        battleOptions.setMaxWTime(battleOptions.getMaxWTime() - 1);
        break;
      case 15:
        int minWeatherDays = withinBounds(battleOptions.getMinWDay() - 1, 100);
        battleOptions.setMinWDay(minWeatherDays);
        break;
    }
  }

  private void rightKeypressActions() {
    switch (getCurrentMenuItem()) {
      case 2:
        int funds = withinBounds(battleOptions.getFundsLevel() + 500, 10000);
        battleOptions.setFundsLevel(funds);
        break;
      case 3:
        int startFunds = withinBounds(battleOptions.getStartFunds() + 500, 30000);
        battleOptions.setStartFunds(startFunds);
        break;
      case 4:
        battleOptions.setTurnLimit(battleOptions.getTurnLimit() + 1);
        break;
      case 5:
        battleOptions.setCapLimit(battleOptions.getCapLimit() + 1);
        break;
      case 9:
        unitBanPosX++;
        validateUnitBanPositon();
        break;
      case 10:
        int snowChangePerc = withinBounds(battleOptions.getSnowChance() + 1, 100);
        battleOptions.setSnowChance(snowChangePerc);
        break;
      case 11:
        int rainChangePerc = withinBounds(battleOptions.getRainChance() + 1, 100);
        battleOptions.setRainChance(rainChangePerc);
        break;
      case 12:
        int sandChangePerc = withinBounds(battleOptions.getSandChance() + 1, 100);
        battleOptions.setSandChance(sandChangePerc);
        break;
      case 13:
        battleOptions.setMinWTime(battleOptions.getMinWTime() + 1);
        break;
      case 14:
        battleOptions.setMaxWTime(battleOptions.getMaxWTime() + 1);
        break;
      case 15:
        int minWeatherDays = withinBounds(battleOptions.getMinWDay() + 1, 100);
        battleOptions.setMinWDay(minWeatherDays);
        break;

    }
  }

  private class MouseControl extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      int x = e.getX() - frame.getInsets().left;
      int y = e.getY() - frame.getInsets().top;
      final boolean ON_LEFT_PAGE_CLICK = x < 210;
      final boolean ON_RIGHT_PAGE_CLICK = !ON_LEFT_PAGE_CLICK && y < 80;
      final boolean ON_UNIT_BANS_CLICK = x > 10 && x < 10 + BaseDMG.NUM_UNITS / 2 * TILE_SIZE && y > 184 && y < 220;

      if (SwingUtilities.isLeftMouseButton(e)) {
        int row = y / ROW_HEIGHT;
        if (ON_LEFT_PAGE_CLICK) {
          if (row < 9) {
            setMenuItemAndPress(row);

            // 10 is the menu item line that contains the images, so go back 1 menuItem
            // for the menuItems that come after the image row.
          } else if (row > 10) {
            setMenuItemAndPress(row - 1);
          }

          if (ON_UNIT_BANS_CLICK) {
            unitBanPosY = (y - 184) / ROW_HEIGHT;
            unitBanPosX = (x - 10) / TILE_SIZE + unitBanPosY * BaseDMG.NUM_UNITS / 2;
            validateUnitBanPositon();
            setMenuItemAndPress(9);
          }
        } else if (ON_RIGHT_PAGE_CLICK) {
          setMenuItemAndPress(NUM_ITEMS_ON_1_PAGE + row);
        } else {
          menuSession.setBattleOptions(battleOptions);
          stateManager.changeToState("IN_GAME");
        }
        frame.repaint(0);
      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("MAIN_MENU");
      }
    }
  }

  private void setMenuItemAndPress(int i) {
    setCurrentMenuItem(i);
    pressCurrentItem();
  }

  private void validateUnitBanPositon() {
    // Lower bounds
    if (unitBanPosX < 0) {
      unitBanPosX = 0;
      //  handle going to next/Previous line
      int lastXPos = BaseDMG.NUM_UNITS / 2 - 1;
      if (unitBanPosY == 1) {
        unitBanPosX = lastXPos;
        unitBanPosY--;
      } else if (unitBanPosY == 0) {
        unitBanPosX = lastXPos;
        unitBanPosY++;
      }
    }

    // Upper bounds
    if (unitBanPosX >= BaseDMG.NUM_UNITS / 2) {
      unitBanPosY = 1;
    }

    if (unitBanPosX >= BaseDMG.NUM_UNITS) {
      this.unitBanPosX = 0;
      this.unitBanPosY = 0;
    }
  }

  private void pressCurrentItem() {
    switch (getCurrentMenuItem()) {
      case 0:
        visibility++;
        setBattleOptionsVisibility();
        break;
      case 1:
        int weather = withinBounds(battleOptions.getWeatherType() + 1, 4);
        battleOptions.setWeatherType(weather);
        break;
      case 2:
        int funds = withinBounds(battleOptions.getFundsLevel() + 500, 10000);
        battleOptions.setFundsLevel(funds);
        break;
      case 3:
        int startFunds = withinBounds(battleOptions.getStartFunds() + 500, 30000);
        battleOptions.setStartFunds(startFunds);
        break;
      case 4:
        battleOptions.setTurnLimit(battleOptions.getTurnLimit() + 1);
        break;
      case 5:
        battleOptions.setCapLimit(battleOptions.getCapLimit() + 1);
        break;
      case 6:
        battleOptions.setCOP(!battleOptions.isCOP());
        break;
      case 7:
        battleOptions.setBalance(!battleOptions.isBalance());
        break;
      case 8:
        battleOptions.setReplay(!battleOptions.isRecording());
        break;
      case 9:
        toggleBanUnit(unitBanPosX, unitBanPosY);
        break;
      case 10:
        if (battleOptions.getSnowChance() < 100) battleOptions.setSnowChance(battleOptions.getSnowChance() + 1);
        break;
      case 11:
        if (battleOptions.getRainChance() < 100) battleOptions.setRainChance(battleOptions.getRainChance() + 1);
        break;
      case 12:
        if (battleOptions.getSandChance() < 100) battleOptions.setSandChance(battleOptions.getSandChance() + 1);
        break;
      case 13:
        battleOptions.setMinWTime(battleOptions.getMinWTime() + 1);
        break;
      case 14:
        battleOptions.setMaxWTime(battleOptions.getMaxWTime() + 1);
        break;
      case 15:
        battleOptions.setMinWDay(battleOptions.getMinWDay() + 1);
        break;
    }
  }

  private void toggleBanUnit(int x, int y) {
    if (battleOptions.isUnitBanned(x)) {
      battleOptions.setUnitBanned(false, x);
    } else {
      battleOptions.setUnitBanned(true, x);
    }
  }

  /**
   * @param item the new item to validate against the menu item bounds
   * @param max  exclusive max bound
   *             (item=2 max=2) will return 2,
   *             (item=3 max=2) will return 0
   * @return max if the item was <0
   *         0 if the item was > max
   *         item if the item was within bounds
   */
  @Override
  int withinBounds(int item, int max) {
    if (item < 0) {
      return max;
    } else if (item > max) {
      return 0;
    } else {
      return item;
    }
  }

  private void setBattleOptionsVisibility() {
    if (visibility > 2) visibility = 0;

    if (visibility == 0) {
      battleOptions.setFog(false);
      battleOptions.setMist(false);
    } else if (visibility == 1) {
      battleOptions.setFog(true);
      battleOptions.setMist(false);
    } else {
      battleOptions.setMist(true);
      battleOptions.setFog(false);
    }
  }

  public static void main(String[] args) {
    ResourceLoader.init();

    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);
    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;
    UnitGraphics.loadImages(frame);
    MainMenuGraphics.loadImages(frame);
    final BattleOptionsMenu battleOptionsMenu = new BattleOptionsMenu(frame, new StateManager(frame), new MenuSession());
    battleOptionsMenu.init();
    JPanel panel = new JPanel() {
      protected void paintComponent(Graphics g) {
        battleOptionsMenu.paint((Graphics2D) g
        );
      }
    };

    frame.add(panel);
    frame.setSize(485, 350);
    frame.setVisible(true);
  }
}
