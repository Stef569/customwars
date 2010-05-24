package com.customwars.client.ui;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * The thingle dialog is similar to a Swing dialog. But differs at following points:
 * It is integrated in the game container and cannot move outside of it.
 * The look & feel can be changed by changing colors in a config file and it looks more game like.
 * The game container does not loose focus.
 * <p/>
 * todo block until user performed an action?
 */
public class ThingleDialog {
  private final Page page;
  private final GUIContext guiContext;
  private boolean visible;

  public ThingleDialog(GUIContext guiContext, String msg, String title) {
    this.guiContext = guiContext;
    Theme theme = new ThinglePageLoader(App.get("gui.path")).loadTheme("greySkin.properties");
    page = new Page();
    page.setDrawDesktop(false);
    page.setTheme(theme);

    try {
      String msgXML = "<textarea name='msg' wrap='true' columns='40' rows='2' border='false' editable='false'/>";
      String btnXML = "<button name='okBtn' text=' " + App.translate("ok") + " ' action='dialogOkPressed'/>";

      page.add(page.parse(
        "<dialog name='dialog' modal='true' columns='1'>" +
          "<panel columns='1'>" + msgXML + "</panel>" +
          "<panel halign='center' top='5' bottom='5'>" + btnXML + "</panel>" +
          "</dialog>", this)
      );

      Widget dialog = page.getWidget("dialog");
      dialog.setText(title);
      dialog.getChild("msg").setText(msg);
      dialog.getChild("okBtn").focus();
    } catch (ThingleException ex) {
      throw new RuntimeException("Check the xml it's invalid", ex);
    }
  }

  public void show() {
    page.layout();
    page.enable();
    visible = true;
  }

  public void hide() {
    page.disable();
    visible = false;
  }

  public void render() {
    if (visible) {
      guiContext.getInput().resetInputTransform();
      page.render();
    }
  }

  public void dialogOkPressed() {
    // Hide dialog when the ok button is pressed
    hide();
  }

  public boolean isVisible() {
    return visible;
  }
}
