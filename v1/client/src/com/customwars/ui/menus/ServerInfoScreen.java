package com.customwars.ui.menus;

import com.customwars.ai.Battle;
import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.loader.MapFormatException;
import com.customwars.loader.MapLoader;
import com.customwars.map.Map;
import com.customwars.sfx.SFX;
import com.customwars.state.NetworkException;
import com.customwars.state.NetworkSession;
import com.customwars.state.NetworkingManager;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleScreen;
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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class ServerInfoScreen extends Menu implements State {
  private static final Logger logger = Logger.getLogger(ServerInfoScreen.class);
  private static final String TEMPORARYMAP_MAP_FILENAME = "temporarymap.map";
  private static final String TEMPORARYSAVE_SAVE_FILENAME = "temporarysave.save";

  private StateManager stateManager;
  private NetworkingManager networkingManager;
  private NetworkSession networkSession;
  private MenuSession menuSession;
  private JFrame frame;

  private KeyControl keyControl = new KeyControl();
  private MouseControl mouseControl = new MouseControl();

  private static final int SHOW_SYSLOG = 0;
  private static final int SHOW_CHAT = 1;
  private static final int REFRESH = 2;
  private static final int PLAY = 3;
  private static final int SEND_CHAT_MSG = 4;
  private static final int NUM_MENU_ITEMS = 5;

  private static final int BTN_WIDTH = 161, BTN_HEIGHT = 20;

  protected ServerInfoScreen(NetworkSession networkSession, JFrame frame, MenuSession menuSession, StateManager stateManager) {
    super(NUM_MENU_ITEMS);
    this.stateManager = stateManager;
    this.menuSession = menuSession;
    this.frame = frame;
    this.networkSession = networkSession;
    this.networkingManager = new NetworkingManager();
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
    int currentMenuItem = getCurrentMenuItem();
    paintChatScreen(g);

    g.setFont(MainMenuGraphics.getH1Font());
    paintButtons(g, currentMenuItem);
    paintChatMsgs(g, currentMenuItem);
    paintInfo(g);
  }

  private void paintChatScreen(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, 480, 100);
    g.setColor(Color.DARK_GRAY);
    g.fillRect(460, 0, 20, 100);
    g.setColor(Color.WHITE);
    g.fillRect(460, 0, 20, 20);
    g.fillRect(460, 80, 20, 20);
    g.setColor(Color.gray);
  }

  private void paintButtons(Graphics g, int currentMenuItem) {
    drawBtn("Syslog", 50, 120, currentMenuItem == SHOW_SYSLOG, g);
    drawBtn("Chat", 218, 120, currentMenuItem == SHOW_CHAT, g);
    drawBtn("Send", 377, 120, currentMenuItem == SEND_CHAT_MSG, g);

    drawBtn("Refresh", 370, 290, currentMenuItem == REFRESH, g);
    drawBtn("Play", 385, 315, currentMenuItem == PLAY, g);
  }

  private void drawBtn(String txt, int textX, int textY, boolean highLighted, Graphics g) {
    int UNDER_BASELINE_PX = 5;

    if (highLighted) {
      g.setColor(Color.black);
    } else {
      g.setColor(Color.GRAY);
    }

    int txtLength = GuiUtil.getStringWidth(txt, g);
    int leftTopX = textX - BTN_WIDTH / 2 + txtLength / 2;
    int leftTopY =  textY - BTN_HEIGHT;

    // Fill from top left to baseLine
    g.fillRect(leftTopX, leftTopY, BTN_WIDTH, BTN_HEIGHT);

    // Fill from baseLine to UNDER_BASELINE_PX
    g.fillRect(leftTopX, textY , BTN_WIDTH, UNDER_BASELINE_PX);

    // Paint white text on top
    g.setColor(Color.WHITE);
    g.drawString(txt, textX, textY);
  }

  private void paintChatMsgs(Graphics g, int currentMenuItem) {
    int sysPos = networkSession.getSysPos();
    int chatPos = networkSession.getChatPos();
    String[] sysLog = networkSession.getSysLog();
    String[] chatLog = networkSession.getChatLog();

    g.setColor(Color.white);
    for (int i = 0; i < 5; i++) {
      if (currentMenuItem == 0) {
        if (i + sysPos < sysLog.length && i + networkSession.getSysPos() >= 0)
          g.drawString(sysLog[i + sysPos], 0, 18 + i * 20);
      } else {
        if (i + chatPos < chatLog.length && i + chatPos >= 0) g.drawString(chatLog[i + chatPos], 0, 18 + i * 20);
      }
    }
  }

  private void paintInfo(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawString("Game Name: " + networkSession.getGameName(), 0, 140);
    g.drawString("Login Name: " + networkSession.getUserName(), 0, 160);
    g.drawString("Current day/turn: " + networkSession.getDay() + "/" + networkSession.getTurn(), 0, 180);
    for (int i = 0; i < networkSession.getUserNames().length; i++) {
      g.drawString(networkSession.getUserNames()[i], (i < 5) ? 0 : 120, 200 + (i % 5) * 20);
    }
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

      final boolean SEND_CHAT_MSG_CLICK = x > 320 && x < 480 && y > 100 && y < 120;
      final boolean SHOW_SYSLOG_CLICK = x > 0 && x < 160 && y > 100 && y < 120;
      final boolean SHOW_CHAT_CLICK = x > 160 && x < 320 && y > 100 && y < 120;
      final boolean REFRESH_CLICK = x > 240 && x < 480 && y > 280 && y < 300;
      final boolean PLAY_CLICKED = x > 240 && x < 480 && y > 300 && y < 320;
      final boolean SCROLL_UP = x > 460 && x < 480 && y > 0 && y < 20;
      final boolean SCROLL_DOWN = x > 460 && x < 480 && y > 80 && y < 100;

      if (SwingUtilities.isLeftMouseButton(e)) {
        if (SHOW_SYSLOG_CLICK) {
          setCurrentMenuItem(SHOW_SYSLOG);
        } else if (SHOW_CHAT_CLICK) {
          setCurrentMenuItem(SHOW_CHAT);
        } else if (SEND_CHAT_MSG_CLICK) {
          setCurrentMenuItem(SHOW_CHAT);
          String message = JOptionPane.showInputDialog("Type in your chat message");
          if (message == null) return;
          String msg = networkSession.getGameName() + "\n" + networkSession.getUserName() + "\n" + message;
          sendChatMsg(msg);
        } else if (REFRESH_CLICK) {
          setCurrentMenuItem(REFRESH);
          pressCurrentItem();
        } else if (PLAY_CLICKED) {
          setCurrentMenuItem(PLAY);
          pressCurrentItem();
        } else if (SCROLL_UP) {
          //pressedPGUP();
        } else if (SCROLL_DOWN) {
          //pressedPGDN();
        }
      } else if (SwingUtilities.isRightMouseButton(e)) {
        stateManager.changeToState("NEW_GAME");
      }
      frame.repaint(0);
    }
  }

  private void pressCurrentItem() {
    int currentMenuItem = getCurrentMenuItem();

    switch (currentMenuItem) {
      case REFRESH:
      case SHOW_SYSLOG:
        logger.info("Refreshing");
        try {
          networkSession.refreshInfo(networkSession.getGameName());
        } catch (NetworkException e) {
          logger.fatal(e);
        }
        break;
      case SHOW_CHAT:
      case SEND_CHAT_MSG:
        String message = JOptionPane.showInputDialog("Type in your chat message");
        sendChatMsg(message);
        break;
      case PLAY:
        try {
          play();
        } catch (IOException e) {
          logger.fatal(e);
        } catch (MapFormatException e) {
          logger.fatal(e);
        }
        break;
    }
  }

  private boolean sendChatMsg(String msg) {
    if (msg == null) return false;

    String command = "sendchat";
    String extra = Options.gamename + "\n" + Options.username + "\n" + msg;
    String reply = "";

    try {
      reply = networkingManager.sendCommandToMain(command, extra);
    } catch (MalformedURLException e1) {
      logger.info("Bad URL " + Options.getServerName());
      return false;
    } catch (IOException e2) {
      logger.error("Connection Problem during command " + command + " with information:\n" + extra);
      return false;
    }

    return !reply.equals("no");
  }

  private void play() throws IOException, MapFormatException {
    String msg = Options.gamename + "\n" + Options.username + "\n" + Options.password;
    String reply = networkingManager.sendCommandToMain("canplay", msg);

    if (reply.equals("permission granted")) {
      if (networkSession.getDay() == 1) {
        // Start a new Game and change to CO_SELECT
        startNewGame();
      } else {
        // Load the last saved Battle from server
        // Continue the Battle
        continueBattle();
      }
    }
  }

  // load map from server as TEMPORARYMAP_MAP_FILENAME
  // load and store map into menuSession, skipping the Map Selection Menu
  // CO_SELECT can use the map to calc the amount of co's needed etc. by using the
  // map.getPlayerCount() method
  // todo map should contain all information, still menuSession needs a fileName?
  private void startNewGame() throws MapFormatException, IOException {
    logger.info("Starting New Game");
    downloadMap();
    Map map = new MapLoader().loadMap(new File(TEMPORARYMAP_MAP_FILENAME));
    menuSession.setMap(map);
    menuSession.setMapFileName(TEMPORARYMAP_MAP_FILENAME);
    stateManager.changeToState("CO_SELECT");
  }

  // load mission, and continue the battle
  private void continueBattle() {
    logger.info("Continue Game");
    downloadMap();
    Battle b = new Battle(new Map(30, 20));
    BattleScreen bs = new BattleScreen(b, frame);

    // Initialize a swing frame and put a BattleScreen inside
    frame.setSize(400, 400);
    // Testing only, stop this state and add BattleScreen, should be another state IN_GAME.
    stop();
    frame.getContentPane().removeAll();
    frame.getContentPane().add(bs);
    frame.validate();
    frame.pack();

    // Start the mission
    GameSession.startMission(null, bs);
    GameSession.loadMission(TEMPORARYSAVE_SAVE_FILENAME);
  }

  private boolean downloadMap() {
    try {
      return networkingManager.getFile("dsave.pl", Options.gamename, TEMPORARYSAVE_SAVE_FILENAME);
    } catch (MalformedURLException e1) {
      logger.error("Bad URL " + Options.getServerName());
      JOptionPane.showMessageDialog(frame, "Bad URL: " + Options.getServerName());
    } catch (IOException e2) {
      logger.error("Connection problem, unable to get file from server");
      JOptionPane.showMessageDialog(frame, "Connection problem, unable to get file from server");
    }
    return false;
  }

  public static void main(String[] args) {
    ResourceLoader.init();
    Options.setServerName("http://localhost/customwars/cw/cw1/");
    String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
    SFX.setSoundLocation(SOUND_LOCATION);
    JFrame frame = new JFrame();
    GameSession.mainFrame = frame;
    MiscGraphics.loadImages(frame);
    MainMenuGraphics.loadImages(frame);
    // Act as if the user fills in the details
    NetworkSession networkSession = new NetworkSession();
    networkSession.setGameName("anything unique will do");
    networkSession.setUserName("unique!");

    final State keyMapMenu = new ServerInfoScreen(networkSession, frame, new MenuSession(), new StateManager(frame));
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
