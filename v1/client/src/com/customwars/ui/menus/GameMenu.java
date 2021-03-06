package com.customwars.ui.menus;

import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.sfx.SFX;
import com.customwars.state.NetworkException;
import com.customwars.state.ResourceLoader;
import com.customwars.state.NetworkSession;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.state.State;
import com.customwars.ui.state.StateManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The GameMenu handles SinglePlayer and Network games
 *
 * @author stefan
 * @since 2.0
 */
public class GameMenu extends Menu implements State {
    private static final Logger logger = Logger.getLogger(GameMenu.class);
    private static final Font MENU_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static final Font DESCRIPTION_FONT = new Font("Arial", Font.BOLD, 11);

    private static final Color DESCRIPTION_BOX_BACKGROUND = new Color(7, 66, 97);
    private static final Color DESCRIPTION_COLOR = Color.WHITE;

    private static final int START_SINGLEPLAYER_GAME = 0;
    private static final int LOAD_GAME = 1;
    private static final int START_NETWORK_GAME = 2;
    private static final int LOAD_REPLAY = 3;
    private static final int CREATE_SERVER_GAME = 4;
    private static final int JOIN_SERVER_GAME = 5;
    private static final int LOGIN_TO_SERVER_GAME = 6;
    private static final int JOIN_IRC_LOBBY = 7;
    private static final int NUM_MENU_ITEMS = 8;

    private JFrame frame;
    private StateManager stateManager;
    private KeyControl keyControl = new KeyControl();
    private MouseControl mouseControl = new MouseControl();

    private int coGlide = -1;  // Used to glide the co in
    private NetworkSession networkSession;

    public GameMenu(JFrame frame, StateManager stateManager) {
        super(NUM_MENU_ITEMS);
        this.frame = frame;
        this.stateManager = stateManager;
        this.networkSession = new NetworkSession();
    }

    public void init() {
        frame.addKeyListener(keyControl);
        frame.addMouseListener(mouseControl);
    }

    public void stop() {
        coGlide = -1;
        frame.removeKeyListener(keyControl);
        frame.removeMouseListener(mouseControl);
    }

    // PAINT
    public void paint(Graphics2D g) {
        g.drawImage(MainMenuGraphics.getBackground(), 0, 0, frame);
        int currentMenuItem = getCurrentMenuItem();
        paintMenu(g, currentMenuItem);
        paintGlidingCo(g);
        paintDescriptionBox(g);
        paintDescription(g, currentMenuItem);
    }

    private void paintMenu(Graphics2D g, int currentMenuItem) {
        g.setFont(MENU_FONT);

        setHighlightColor(currentMenuItem == START_SINGLEPLAYER_GAME, g);
        g.drawString("New", 15, 30);
        setHighlightColor(currentMenuItem == LOAD_GAME, g);
        g.drawString("Load", 15, 54);
        setHighlightColor(currentMenuItem == START_NETWORK_GAME, g);
        g.drawString("Network Game", 15, 78);
        setHighlightColor(currentMenuItem == LOAD_REPLAY, g);
        g.drawString("Load Replay", 15, 102);
        setHighlightColor(currentMenuItem == CREATE_SERVER_GAME, g);
        g.drawString("Create New Server Game", 15, 126);
        setHighlightColor(currentMenuItem == JOIN_SERVER_GAME, g);
        g.drawString("Join Server Game", 15, 150);
        setHighlightColor(currentMenuItem == LOGIN_TO_SERVER_GAME, g);
        g.drawString("Login to Server Game", 15, 174);
        setHighlightColor(currentMenuItem == JOIN_IRC_LOBBY, g);
        g.drawString("Open Online Lobby", 15, 198);

        g.setColor(Color.black);
    }

    private void paintGlidingCo(Graphics2D g) {
        final int dx = 329, dy = 350, dx2 = 255, dy2 = -5;
        g.drawImage(MainMenuGraphics.getMainMenuCO(Options.getMainCOID()),
                calcGlide(++coGlide) + dx,
                dy2, calcGlide(coGlide) + dx + dx2,
                dy2 + dy, 0, 0,
                dx2, dy, frame);
    }

    private int calcGlide(int glide) {
        return (int) (100 * Math.pow(.95, glide));
    }

    private void paintDescriptionBox(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(DESCRIPTION_BOX_BACKGROUND);
        g.fillRoundRect(180, 255, 280, 60, 20, 20);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(Color.BLACK);
    }

    private void paintDescription(Graphics2D g, int currentMenuItem) {
        g.setFont(DESCRIPTION_FONT);
        g.setColor(DESCRIPTION_COLOR);

        switch (currentMenuItem) {
            case START_SINGLEPLAYER_GAME:
                g.drawString("Start a new game. This mode is primarily for", 190, 275);
                g.drawString("playing against a friend on the same computer.", 190, 290);
                break;
            case LOAD_GAME:
                g.drawString("Continue where you started off from your", 190, 275);
                g.drawString("previous game.", 190, 290);
                break;
            case START_NETWORK_GAME:
                g.drawString("Connect via a friend's IP and enjoy an online ", 190, 275);
                g.drawString("hotseat game with him or her! Hamachi is ", 190, 290);
                g.drawString("suggested for the best connectivity results.", 190, 304);
                break;
            case LOAD_REPLAY:
                g.drawString("Already finished a game and feel like reliving ", 190, 275);
                g.drawString("those moments of honour? ", 190, 290);
                g.drawString("Load the replay here!", 190, 304);
                break;
            case CREATE_SERVER_GAME:
                g.drawString("Start a new game on the CW server! If you ", 190, 275);
                g.drawString("don't have a friend to battle, you should make", 190, 290);
                g.drawString("an open game so anyone can join and play!", 190, 304);
                break;
            case JOIN_SERVER_GAME:
                g.drawString("Join a game on the CW server that's open!", 190, 275);
                g.drawString("All you need is the game name, a handle and", 190, 290);
                g.drawString("a password! Then you're all ready to play!", 190, 304);
                break;
            case LOGIN_TO_SERVER_GAME:
                g.drawString("Login to one of your current games!", 190, 275);
                g.drawString("Let's hope you're winning, at least.", 190, 290);
                g.drawString("Otherwise, what's the point to logging in?", 190, 304);
                break;
            case JOIN_IRC_LOBBY:
                break;
            default:
                throw new AssertionError("Could not paint current menu item: " + currentMenuItem);
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
        private static final int NEW_GAME_WIDTH_START = 11;
        private static final int NEW_GAME_WIDTH_END = 87;
        private static final int NEW_GAME_HEIGHT_TOP = 8;
        private static final int NEW_GAME_HEIGHT_BOTTOM = 28;

        private static final int LOAD_GAME_WIDTH_START = 11;
        private static final int LOAD_GAME_WIDTH_END = 87;
        private static final int LOAD_GAME_HEIGHT_TOP = 29;
        private static final int LOAD_GAME_HEIGHT_BOTTOM = 56;

        private static final int NETWORK_GAME_WIDTH_START = 11;
        private static final int NETWORK_GAME_WIDTH_END = 200;
        private static final int NETWORK_GAME_HEIGHT_BOTTOM = 76;
        private static final int NETWORK_GAME_HEIGHT_TOP = 57;

        private static final int LOAD_REPLAY_WIDTH_START = 11;
        private static final int LOAD_REPLAY_WIDTH_END = 200;
        private static final int LOAD_REPLAY_HEIGHT_BOTTOM = 106;
        private static final int LOAD_REPLAY_HEIGHT_TOP = 76;

        private static final int NEW_SERVER_GAME_WIDTH_START = 11;
        private static final int NEW_SERVER_GAME_WIDTH_END = 334;
        private static final int NEW_SERVER_GAME_HEIGHT_BOTTOM = 132;
        private static final int NEW_SERVER_GAME_HEIGHT_TOP = 107;

        private static final int JOIN_SERVER_GAME_WIDTH_START = 11;
        private static final int JOIN_SERVER_GAME_WIDTH_END = 334;
        private static final int JOIN_SERVER_GAME_HEIGHT_BOTTOM = 156;
        private static final int JOIN_SERVER_GAME_HEIGHT_TOP = 133;

        private static final int LOGIN_SERVER_GAME_WIDTH_START = 11;
        private static final int LOGIN_SERVER_GAME_WIDTH_END = 334;
        private static final int LOGIN_SERVER_GAME_HEIGHT_BOTTOM = 182;
        private static final int LOGIN_SERVER_GAME_HEIGHT_TOP = 157;

        private static final int JOIN_LOBBY_WIDTH_START = 11;
        private static final int JOIN_LOBBY_WIDTH_END = 334;
        private static final int JOIN_LOBBY_HEIGHT_BOTTOM = 200;
        private static final int JOIN_LOBBY_HEIGHT_TOP = 183;

        public void mouseClicked(MouseEvent e) {
            int x = e.getX() - frame.getInsets().left;
            int y = e.getY() - frame.getInsets().top;

            if (SwingUtilities.isLeftMouseButton(e)) {
                boolean newGameClicked = x > NEW_GAME_WIDTH_START && x < NEW_GAME_WIDTH_END && y < NEW_GAME_HEIGHT_BOTTOM && y > NEW_GAME_HEIGHT_TOP;
                boolean loadGameClicked = x > LOAD_GAME_WIDTH_START && x < LOAD_GAME_WIDTH_END && y < LOAD_GAME_HEIGHT_BOTTOM && y > LOAD_GAME_HEIGHT_TOP;
                boolean networkGameClicked = x > NETWORK_GAME_WIDTH_START && x < NETWORK_GAME_WIDTH_END && y < NETWORK_GAME_HEIGHT_BOTTOM && y > NETWORK_GAME_HEIGHT_TOP;
                boolean loadReplayClicked = x > LOAD_REPLAY_WIDTH_START && x < LOAD_REPLAY_WIDTH_END && y < LOAD_REPLAY_HEIGHT_BOTTOM && y > LOAD_REPLAY_HEIGHT_TOP;
                boolean newServerGameClicked = x > NEW_SERVER_GAME_WIDTH_START && x < NEW_SERVER_GAME_WIDTH_END && y < NEW_SERVER_GAME_HEIGHT_BOTTOM && y > NEW_SERVER_GAME_HEIGHT_TOP;
                boolean joinServerGameClicked = x > JOIN_SERVER_GAME_WIDTH_START && x < JOIN_SERVER_GAME_WIDTH_END && y < JOIN_SERVER_GAME_HEIGHT_BOTTOM && y > JOIN_SERVER_GAME_HEIGHT_TOP;
                boolean loginToServerGameClicked = x > LOGIN_SERVER_GAME_WIDTH_START && x < LOGIN_SERVER_GAME_WIDTH_END && y < LOGIN_SERVER_GAME_HEIGHT_BOTTOM && y > LOGIN_SERVER_GAME_HEIGHT_TOP;
                boolean joinLobbyClicked = x > JOIN_LOBBY_WIDTH_START && x < JOIN_LOBBY_WIDTH_END && y < JOIN_LOBBY_HEIGHT_BOTTOM && y > JOIN_LOBBY_HEIGHT_TOP;

                if (newGameClicked) {
                    setCurrentMenuItem(START_SINGLEPLAYER_GAME);
                }

                if (loadGameClicked) {
                    setCurrentMenuItem(LOAD_GAME);
                }

                if (networkGameClicked) {
                    setCurrentMenuItem(START_NETWORK_GAME);
                }

                if (loadReplayClicked) {
                    setCurrentMenuItem(LOAD_REPLAY);
                }

                if (newServerGameClicked) {
                    setCurrentMenuItem(CREATE_SERVER_GAME);
                }

                if (joinServerGameClicked) {
                    setCurrentMenuItem(JOIN_SERVER_GAME);
                }

                if (loginToServerGameClicked) {
                    setCurrentMenuItem(LOGIN_TO_SERVER_GAME);
                }

                if (joinLobbyClicked) {
                    setCurrentMenuItem(JOIN_IRC_LOBBY);
                }

                if (newGameClicked || loadGameClicked || networkGameClicked || loadReplayClicked ||
                        newServerGameClicked || joinServerGameClicked || loginToServerGameClicked || joinLobbyClicked) {
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
            case START_SINGLEPLAYER_GAME:
                stateManager.changeToState("START_SINGLEPLAYER_GAME");
                break;
            case LOAD_GAME:
                stateManager.changeToState("LOAD_GAME");
                break;

            case START_NETWORK_GAME:
                Options.startNetwork();
                break;

            case LOAD_REPLAY:
                stateManager.changeToState("LOAD_REPLAY");
                break;

            case CREATE_SERVER_GAME:
                try {
                    createServerGame();
                } catch (NetworkException e) {
                    logger.fatal("Could not start server game", e);
                    JOptionPane.showMessageDialog(frame, e);
                    break;
                }
                logger.debug("starting snail game");
                stateManager.changeToState("SNAIL_GAME");
                break;

            case JOIN_SERVER_GAME:
                try {
                    joinServerGame();
                } catch (NetworkException e) {
                    logger.fatal("Could not join network game", e);
                    JOptionPane.showMessageDialog(frame, e);
                    break;
                }

                String slot = JOptionPane.showInputDialog(frame, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
                if (slot == null) {
                    stateManager.changeToState("NEW_GAME");
                    break;
                }
                stateManager.changeToState("SNAIL_GAME");
                break;

            case LOGIN_TO_SERVER_GAME:
                try {
                    loginToServer();
                } catch (NetworkException e) {
                    logger.fatal("Could not start network game", e);
                    JOptionPane.showMessageDialog(frame, e);
                    break;
                }
                stateManager.changeToState("SNAIL_GAME");
                break;

            case JOIN_IRC_LOBBY:
                stateManager.changeToState("JOIN_IRC_LOBBY");
                break;

            default:
                throw new AssertionError("Could not handle current menu item: " + currentMenuItem);
        }
    }

    private void loginToServer() throws NetworkException {
        String gameName, userName, userPass;

        gameName = JOptionPane.showInputDialog(frame, "Type in a name for your game:", "Network Game: Name?", JOptionPane.PLAIN_MESSAGE);

        if (Options.isDefaultLoginOn()) {
            userName = Options.getDefaultUsername();
            userPass = Options.getDefaultPassword();

        } else {
            userName = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)", JOptionPane.PLAIN_MESSAGE);
            userPass = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password?", JOptionPane.PLAIN_MESSAGE);
        }

        networkSession.loginToServerGame(gameName, userName, userPass);
    }

    private void joinServerGame() throws NetworkException {
        String gameName, masterPass, userName, userPass, slot;
        gameName = JOptionPane.showInputDialog(null, "Name of game:", "Join Game: Name", JOptionPane.PLAIN_MESSAGE);
        masterPass = JOptionPane.showInputDialog(null, "Enter Password for game:", "Join Game: Master Pass", JOptionPane.PLAIN_MESSAGE);

        if (Options.isDefaultLoginOn()) {
            userName = Options.getDefaultUsername();
            userPass = Options.getDefaultPassword();
        } else {
            userName = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)", JOptionPane.PLAIN_MESSAGE);
            userPass = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password", JOptionPane.PLAIN_MESSAGE);
        }

        slot = JOptionPane.showInputDialog(null, "Type in the number of the army you will command:", "Network Game: Army No.?", JOptionPane.PLAIN_MESSAGE);
        int slotNum = Integer.parseInt(slot);
        networkSession.joinServerGame(gameName, masterPass, userName, userPass, slotNum);
    }

    private void createServerGame() throws NetworkException {
        String gamename = JOptionPane.showInputDialog(null, "Type in a name for your game:", "Network Game: Name", JOptionPane.PLAIN_MESSAGE);
        String masterpass = JOptionPane.showInputDialog(null, "Master Password for your game:", "Network Game: Master Pass?", JOptionPane.PLAIN_MESSAGE);

        String userName, userPassword;

        // set the master password and join
        if (Options.isDefaultLoginOn()) {
            userName = Options.getDefaultUsername();
            userPassword = Options.getDefaultPassword();
        } else {
            userName = JOptionPane.showInputDialog(null, "Username for your game:", "Network Game: User(12char)?", JOptionPane.PLAIN_MESSAGE);
            userPassword = JOptionPane.showInputDialog(null, "Password for your game:", "Network Game: Password?", JOptionPane.PLAIN_MESSAGE);
        }
        networkSession.createServerGame(gamename, masterpass, userName, userPassword);
    }

    public static void main(String[] args) {
        ResourceLoader.init();
        String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
        SFX.setSoundLocation(SOUND_LOCATION);
        JFrame frame = new JFrame();
        GameSession.mainFrame = frame;
        MainMenuGraphics.loadImages(frame);
        MiscGraphics.loadImages(frame);
        final GameMenu gameMenu = new GameMenu(frame, new StateManager(frame));
        gameMenu.init();
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                gameMenu.paint((Graphics2D) g);
            }
        };
        frame.add(panel);
        frame.setSize(485, 350);
        frame.setVisible(true);
    }
}
