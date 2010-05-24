package com.customwars.client.ui;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.NetworkException;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.hud.ModelEventScreen;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.GUIContext;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

/**
 * Application wide gui's Contains a console and a game event viewer window.
 * Live objects can be added to the console
 */
public class GUI {
  public static int YES_OPTION = 0;
  public static int NO_OPTION = 1;

  private static final Logger logger = Logger.getLogger(GUI.class);
  private static Interpreter bsh;
  private static JFrame eventFrame, consoleFrame;
  private static ModelEventScreen modelEventScreen;
  private static GUIContext guiContext;
  private static Camera2D camera;
  private static boolean inited;
  private static ThingleDialog thingleDialog;

  public static void init(GUIContext guiContext) {
    GUI.guiContext = guiContext;

    if (!inited) {
      initConsole();
      initEventScreen();
      inited = true;
    }
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

  public static void setCamera(Camera2D camera) {
    GUI.camera = camera;
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
   * Can the given point fit to the screen, the 'screen' can be either the guiContext or the camera
   * Check if the rectangle can fit to the guiContext this is useful if the camera is smaller then the screen
   * If that is the case the rectangle still fits even outside of the camera bounds.
   */
  public static boolean canFitToScreen(int x, int y) {
    return canFitToScreen(x, y, 0, 0);
  }

  /**
   * Can the given rectangle fit to the screen, the 'screen' can be either the guiContext or the camera
   * Check if the rectangle can fit to the guiContext this is useful if the camera is smaller then the screen
   * If that is the case the rectangle still fits even outside of the camera bounds.
   */
  public static boolean canFitToScreen(int x, int y, int width, int height) {
    int maxX = x + width;
    int maxY = y + height;
    boolean canFitToGuiContext = x >= 0 && maxX <= guiContext.getWidth() && y >= 0 && maxY <= guiContext.getHeight();
    return canFitToGuiContext || camera.canFitWithin(x, y, width, height);
  }

  /**
   * Get the left top point of the inner component
   * so that the component is centered within the container
   */
  public static Point getCenteredRenderPoint(Dimension innerComponentSize, Dimension containerSize) {
    return getCenteredRenderPoint(innerComponentSize.width, innerComponentSize.height, containerSize.width, containerSize.height);
  }

  /**
   * Get the left top point of the inner component
   * so that the component is centered within the container
   */
  public static Point getCenteredRenderPoint(Dimension innerComponentSize, GUIContext guiContext) {
    return getCenteredRenderPoint(innerComponentSize.width, innerComponentSize.height, guiContext.getWidth(), guiContext.getHeight());
  }

  /**
   * Get the left top point of the inner component
   * so that the component is centered within the container
   */
  public static Point getCenteredRenderPoint(int innerComponentWidth, int innerComponentHeight, GUIContext guiContext) {
    return getCenteredRenderPoint(innerComponentWidth, innerComponentHeight, guiContext.getWidth(), guiContext.getHeight());
  }

  /**
   * Get the left top point of the inner component
   * so that the component is centered within the container
   */
  public static Point getCenteredRenderPoint(int innerComponentWidth, int innerComponentHeight, int containerWidth, int containerHeight) {
    Point leftTop = new Point();
    int centerX = (containerWidth / 2) - (innerComponentWidth / 2);
    int centerY = (containerHeight / 2) - (innerComponentHeight / 2);

    // Don't allow the center to go off the screen
    if (centerX < 0) centerX = 0;
    if (centerY < 0) centerY = 0;
    leftTop.setLocation(centerX, centerY);
    return leftTop;
  }

  public static void showExceptionDialog(String title, Throwable e) {
    showExceptionDialog("", e, title);
  }

  public static void showExceptionDialog(String errMsg, Throwable e, String title) {
    String msg = StringUtil.hasContent(errMsg) ? String.format(errMsg + "\n%s", e.getMessage()) : e.getMessage();
    showErrDialog(msg, title);
  }

  public static void showErrDialog(String errMsg, String title) {
    showdialog(errMsg, title);
  }

  public synchronized static void showdialog(String msg, String title) {
    // Gracefully hide a previous shown dialog.
    if (thingleDialog != null) {
      thingleDialog.hide();
    }

    thingleDialog = new ThingleDialog(guiContext, msg, title);
    thingleDialog.show();
  }

  public static int showConfirmationDialog(String msg, String title) {
    return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
  }

  public static void renderDialog() {
    if (thingleDialog != null) {
      if (thingleDialog.isVisible()) {
        thingleDialog.render();
      } else {
        thingleDialog = null;
      }
    }
  }

  public static boolean isRenderingDialog() {
    return thingleDialog != null && thingleDialog.isVisible();
  }

  public static File browseForFile(String title, String approveButtonText) {
    return browseForFile(title, null, approveButtonText);
  }

  public static File browseForFile(String title, FileFilter filter, String approveButtonText) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle(title);
    fileChooser.setApproveButtonText(approveButtonText);

    if (filter != null) {
      fileChooser.setFileFilter(filter);
    }

    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    } else {
      return null;
    }
  }

  public static int askToResend(NetworkException ex) {
    return showConfirmationDialog(
      App.translate("gui_err_networkIO_msg") + " " + ex.getMessage(), App.translate("gui_err_networkIO_title")
    );
  }
}
