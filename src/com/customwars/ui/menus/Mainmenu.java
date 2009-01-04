package com.customwars.ui.menus;

import com.customwars.ai.GameSession;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.menu.MenuSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The MainMenu is shown at the start of the game
 *
 * @author stefan
 * @since 2.0
 */
public class Mainmenu extends Menu {
    private static final Logger logger = LoggerFactory.getLogger(Mainmenu.class);
    private static final String COPYRIGHT_SYMBOL = "\u00a9";
    private static final String COPYRING = "Advance Wars is " + COPYRIGHT_SYMBOL + " Nintendo/Intelligent Systems";

    private static final Font COPYRIGHT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Color COPYRIGHT_COLOR = Color.WHITE;

    private static final int NEW_GAME = 0;
    private static final int MAP_EDITOR = 1;
    private static final int OPTIONS = 2;
    private static final int NUM_MENU_ITEMS = 3;

    private JFrame frame;
    private MenuSession menuSession;

    public Mainmenu(JFrame frame, MenuSession menuSession) {
        super(NUM_MENU_ITEMS);
        this.frame = frame;
        this.menuSession = menuSession;
        frame.addKeyListener(new KeyControl());
        frame.addMouseListener(new MouseControl());
        logger.info("Started through Main menu");
    }

    protected void paintMenu(Graphics2D g) {
        g.drawImage(MainMenuGraphics.getTitleBackground(), 0, 0, frame);
        paintMenu(g, getCurrentMenuItem());
    }

    public void paintMenu(Graphics2D g, int currentMenuItem) {
        switch (currentMenuItem) {
            case NEW_GAME:
                g.drawImage(MainMenuGraphics.getNewGame(true), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getMaps(false), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getOptions(false), 0, 0, frame);
                break;

            case MAP_EDITOR:
                g.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getMaps(true), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getOptions(false), 0, 0, frame);
                break;

            case OPTIONS:
                g.drawImage(MainMenuGraphics.getNewGame(false), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getMaps(false), 0, 0, frame);
                g.drawImage(MainMenuGraphics.getOptions(true), 0, 0, frame);
                break;
            default:
                throw new AssertionError("Could not paint current menu item: " + currentMenuItem);
        }

        g.setFont(COPYRIGHT_FONT);
        g.setColor(COPYRIGHT_COLOR);
        g.drawString(COPYRING, 100, 310);
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
            }
            frame.repaint(0);
        }
    }

    private class MouseControl extends MouseAdapter {
        private static final int OPTIONS_HEIGHT_TOP = 279;
        private static final int OPTIONS_HEIGHT_BOTTOM = 247;
        private static final int OPTIONS_WIDTH_END = 320;
        private static final int OPTIONS_WIDTH_START = 175;
        private static final int MAP_DESIGN_HEIGHT_TOP = 183;
        private static final int MAP_DESIGN_HEIGHT_BOTTOM = 156;
        private static final int MAP_DESIGN_WIDTH_END = 350;
        private static final int MAP_DESIGN_WIDTH_START = 143;
        private static final int NEW_GAME_HEIGHT_TOP = 87;
        private static final int NEW_GAME_HEIGHT_BOTTOM = 60;
        private static final int NEW_GAME_WIDTH_START = 160;
        private static final int NEW_GAME_WIDTH_END = 332;

        public void mouseClicked(MouseEvent e) {
            int x = e.getX() - frame.getInsets().left;
            int y = e.getY() - frame.getInsets().top;

            if (SwingUtilities.isLeftMouseButton(e)) {
                boolean newGame = x > NEW_GAME_WIDTH_START && x < NEW_GAME_WIDTH_END && y > NEW_GAME_HEIGHT_BOTTOM && y < NEW_GAME_HEIGHT_TOP;
                boolean designMaps = x > MAP_DESIGN_WIDTH_START && x < MAP_DESIGN_WIDTH_END && y > MAP_DESIGN_HEIGHT_BOTTOM && y < MAP_DESIGN_HEIGHT_TOP;
                boolean optionsScreen = x > OPTIONS_WIDTH_START && x < OPTIONS_WIDTH_END && y > OPTIONS_HEIGHT_BOTTOM && y < OPTIONS_HEIGHT_TOP;

                if (newGame) {
                    setCurrentMenuItem(NEW_GAME);
                } else if (designMaps) {
                    setCurrentMenuItem(MAP_EDITOR);
                } else if (optionsScreen) {
                    setCurrentMenuItem(OPTIONS);
                }

                // If the Mouse click was in range, perform the action clicked on
                if (newGame || designMaps || optionsScreen) {
                    pressCurrentItem();
                    frame.repaint(0);
                }
            }           
        }
    }

    private void pressCurrentItem() {
        int currentMenuItem = getCurrentMenuItem();

        switch (currentMenuItem) {
            case NEW_GAME:
                logger.info("Moving into the New Game Menu");
                menuSession.changeToState("NEW_GAME");
                break;
            case MAP_EDITOR:
                logger.info("Moving into the Design Maps Area");
                menuSession.changeToState("MAP_EDITOR");
                break;
            case OPTIONS:
                logger.info("Moving into the Options Menu");
                menuSession.changeToState("OPTIONS");
                break;
            default:
                throw new AssertionError("Could not handle current menu item: " + currentMenuItem);
        }
    }

    public static void main(String[] args) {
        ResourceLoader.init();
        String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
        SFX.setSoundLocation(SOUND_LOCATION);
        JFrame frame = new JFrame();
        GameSession.mainFrame = frame;
        MainMenuGraphics.loadImages(frame);
        final Mainmenu mainMenu = new Mainmenu(frame, new MenuSession());
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
