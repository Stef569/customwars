package com.customwars.client.ui;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.hud.ModelEventScreen;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.GUIContext;

import javax.swing.JFrame;

/**
 * Application wide gui's Contains a console and a game event viewer window.
 * Live objects can be added to the console
 */
public class GUI {
  private static final Logger logger = Logger.getLogger(GUI.class);
  private static Interpreter bsh;
  private static JFrame eventFrame, consoleFrame;
  private static ModelEventScreen modelEventScreen;
  private static GUIContext guiContext;
  private static Camera2D camera;

  public static void init(GUIContext guiContext, Camera2D camera2D) {
    GUI.guiContext = guiContext;
    GUI.camera = camera2D;
    initConsole();
    initEventScreen();
  }

  private static void initConsole() {
    consoleFrame = new JFrame("Console");
    JConsole console = new JConsole();
    bsh = new Interpreter(console);
    consoleFrame.add(console);
    consoleFrame.setBounds(0, 0, 400, 400);
    new Thread(bsh).start();
  }

  private static void initEventScreen() {
    eventFrame = new JFrame("Model Events");
    modelEventScreen = new ModelEventScreen(eventFrame);
    eventFrame.add(modelEventScreen.getGui());
    eventFrame.setBounds(0, 0, 350, 750);
  }

  public static void toggleConsoleFrame() {
    if (consoleFrame != null) {
      if (consoleFrame.isVisible()) {
        hideConsoleFrame();
      } else {
        showConsoleFrame();
      }
    }
  }

  public static void showConsoleFrame() {
    consoleFrame.setVisible(true);
  }

  public static void hideConsoleFrame() {
    consoleFrame.setVisible(false);
  }

  public static void toggleEventFrame() {
    if (eventFrame != null) {
      if (eventFrame.isVisible()) {
        hideEventFrame();
      } else {
        showEventFrame();
      }
    }
  }

  public static void showEventFrame() {
    eventFrame.setVisible(true);
  }

  public static void hideEventFrame() {
    eventFrame.setVisible(false);
  }

  /**
   * @param game The game to show events from in the model events screen
   */
  public static void setGame(Game game) {
    modelEventScreen.setGame(game);
  }

  /**
   * Add a live object to the console
   *
   * @param objScriptName The name to reference the object
   * @param obj           The object which methods should become accesible from the console
   */
  public static void addLiveObjToConsole(String objScriptName, Object obj) {
    try {
      bsh.set(objScriptName, obj);
    } catch (EvalError ex) {
      logger.warn("Could not add object " + objScriptName);
    }
  }

  /**
   * Remove a live object from the console
   *
   * @param objScriptName The name that an object has been previously referenced to
   */
  public static void removeLiveObjFromConsole(String objScriptName) {
    try {
      bsh.unset(objScriptName);
    } catch (EvalError ex) {
      logger.warn("Could not remove object " + objScriptName);
    }
  }

  /**
   * @return a list of names for each live objects
   */
  public static String[] getAllLiveObjectNames() {
    return bsh.getNameSpace().getVariableNames();
  }

  /**
   * Can the given rectangle fit to the screen, the 'screen' can be either the guiContext or the camera
   * Check if the rectangle can fit to the guiContext this is useful if the camera is smaller then the screen
   * If that is the case the rectangle still fits even outside of the camera bounds.
   */
  public static boolean canFitToScreen(int x, int y, int width, int height) {
    int maxX = x + width;
    int maxY = y + height;
    boolean canFitToGuiContext = x > 0 && maxX < guiContext.getWidth() && y > 0 && maxY < guiContext.getHeight();
    return canFitToGuiContext || camera.canFitWithin(x, y, width, height);
  }
}
