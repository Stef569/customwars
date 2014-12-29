package com.customwars.client.ui;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.NetworkException;
import com.customwars.client.script.BeanShell;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.hud.ModelEventScreen;
import com.customwars.client.ui.thingle.DialogButtons;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import com.customwars.client.ui.thingle.SimpleThingleDialog;
import com.customwars.client.ui.thingle.ThingleDialog;
import com.customwars.client.ui.thingle.ThingleFileChooser;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.thingle.util.FileChooserListener;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

/**
 * Application wide gui's Contains a console and a game event viewer window.
 * It handles the rendering of all the dialogs in the game.
 */
public class GUI {
  private static final Logger logger = Logger.getLogger(GUI.class);
  private static JFrame eventFrame, consoleFrame;
  private static ModelEventScreen modelEventScreen;
  private static GUIContext guiContext;
  private static Camera2D camera;
  private static boolean inited;
  private static ThingleDialog currentDialog;

  public static void init(GUIContext guiContext) {
    GUI.guiContext = guiContext;

    if (!inited) {
      consoleFrame = BeanShell.get().getConsole();
      initEventScreen();
      inited = true;
    }
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

  /**
   * Show a dialog with one 'OK' button.
   */
  public synchronized static void showdialog(String msg, String title) {
    DialogListener nullListener = new DialogListener() {
      public void buttonClicked(DialogResult button) {
      }
    };
    SimpleThingleDialog simpleDialog = new SimpleThingleDialog(guiContext, msg, title, DialogButtons.OK_BUTTON, nullListener);
    showDialog(simpleDialog);
  }

  /**
   * Show a dialog with 'Yes' 'No' buttons.
   */
  public synchronized static void showConfirmationDialog(String msg, String title, DialogListener listener) {
    SimpleThingleDialog simpleDialog = new SimpleThingleDialog(guiContext, msg, title, DialogButtons.YES_NO_BUTTONS, listener);
    showDialog(simpleDialog);
  }

  public static void renderDialog() {
    if (currentDialog != null) {
      if (currentDialog.isVisible()) {
        guiContext.getInput().resetInputTransform();
        currentDialog.render();
      } else {
        logger.info("Removing Dialog " + currentDialog.getTitle());
        currentDialog = null;
      }
    }
  }

  public static boolean isRenderingDialog() {
    return currentDialog != null && currentDialog.isVisible();
  }

  public static void browseForFile(String title, FileFilter filter, String approveButtonText, File initialDir, FileChooserListener listener) {
    ThingleFileChooser fileChooser = ThingleFileChooser.createOpenDialog(title, filter, approveButtonText, initialDir, listener);
    showDialog(fileChooser);
  }

  public static void browseForFile(String title, FileFilter filter, String approveButtonText, FileChooserListener listener) {
    ThingleFileChooser fileChooser = ThingleFileChooser.createOpenDialog(title, filter, approveButtonText, listener);
    showDialog(fileChooser);
  }

  public static void showSaveFileDialog(String title, FileFilter filter, String approveButtonText, FileChooserListener listener) {
    ThingleFileChooser fileChooser = ThingleFileChooser.createSaveDialog(title, filter, approveButtonText, listener);
    showDialog(fileChooser);
  }

  public static void showDialog(ThingleDialog dialog) {
    logger.info("Show Dialog " + dialog.getTitle());
    dialog.show();
    currentDialog = dialog;
  }

  /**
   * Show a dialog with the text There was a problem sending the message to the server, resend?
   * and 2 buttons: 'Yes' 'No'
   */
  public static void askToResend(NetworkException ex, DialogListener listener) {
    showConfirmationDialog(
      App.translate("gui_err_networkIO_msg") + " " + ex.getMessage(),
      App.translate("gui_err_networkIO_title"),
      listener
    );
  }

  public static int getScreenWidth() {
    return guiContext.getWidth();
  }

  public static int getScreenHeight() {
    return guiContext.getHeight();
  }

  public static Point worldToScreenCoordinate(int x, int y) {
    return new Point(x - camera.getX(), y - camera.getY());
  }
}
