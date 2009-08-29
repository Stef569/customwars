package com.customwars.client;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.hud.ModelEventScreen;
import org.apache.log4j.Logger;

import javax.swing.JFrame;

/**
 * Application wide gui's Contains a console and a game event viewer window.
 * Live objects can be added to the console
 */
public class AppGUI {
  private static final Logger logger = Logger.getLogger(AppGUI.class);
  private static Interpreter bsh;
  private static JFrame eventFrame, consoleFrame;
  private static ModelEventScreen modelEventScreen;

  public static void init() {
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
    if (consoleFrame.isVisible()) {
      hideConsoleFrame();
    } else {
      showConsoleFrame();
    }
  }

  public static void showConsoleFrame() {
    consoleFrame.setVisible(true);
  }

  public static void hideConsoleFrame() {
    consoleFrame.setVisible(false);
  }

  public static void toggleEventFrame() {
    if (eventFrame.isVisible()) {
      hideEventFrame();
    } else {
      showEventFrame();
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
  public static void addConsoleScriptObj(String objScriptName, Object obj) {
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
  public static void removeConsoleScriptObj(String objScriptName) {
    try {
      bsh.unset(objScriptName);
    } catch (EvalError ex) {
      logger.warn("Could not remove object " + objScriptName);
    }
  }

  /**
   * @return a list of all references to live objects
   */
  public static String[] getAllObjConsoleReferences() {
    return bsh.getNameSpace().getAllNames();
  }
}
