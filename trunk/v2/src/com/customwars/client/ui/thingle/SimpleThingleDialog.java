package com.customwars.client.ui.thingle;

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
 */
public class SimpleThingleDialog implements ThingleDialog {
  private final GUIContext guiContext;
  private final String title;                             // The title of the dialog
  private final String message;                           // The instructions to the user in the dialog
  private final Page page;                                // The Thingle page to render the dialog
  private final DialogButtons buttons;                    // The buttons available to this dialog

  private boolean visible;                                // True if the dialog is visible
  private DialogResult result = DialogResult.CANCEL;      // The button that was clicked on
  private DialogListener listener;                        // The listener to be notified of dialog events
  private Widget dialogWidget;                            // The dialog as a thingle widget

  public SimpleThingleDialog(GUIContext guiContext, String msg, String title, DialogButtons buttons, DialogListener listener) {
    this.guiContext = guiContext;
    this.message = msg;
    this.title = title;
    this.buttons = buttons;
    this.listener = listener;
    page = new Page();
    initThingle();
  }

  private void initThingle() {
    Theme theme = new ThinglePageLoader(App.get("gui.path")).loadTheme("greySkin.properties");
    page.setDrawDesktop(false);
    page.setTheme(theme);
  }

  public void show() {
    buildLayout();
    page.layout();
    page.enable();
    visible = true;
  }

  private void buildLayout() {
    try {
      String messageXml = "<textarea name='msg' wrap='true' columns='40' rows='2' border='false' editable='false'/>";
      String buttonXml = buildButtonLayout();

      dialogWidget = page.parse(
              "<dialog name='dialog' modal='true' columns='1'>" +
                      "<panel columns='1'>" + messageXml + "</panel>" +
                      "<panel gap='20' halign='center' top='5' bottom='5'>" + buttonXml + "</panel>" +
                      "</dialog>", this);
      page.add(dialogWidget);

      Widget dialog = page.getWidget("dialog");
      dialog.setText(title);
      dialog.getChild("msg").setText(message);

      putFocusOnDefaultButton(dialog);

    } catch (ThingleException ex) {
      throw new RuntimeException("Check the xml it's invalid", ex);
    }
  }

  private void putFocusOnDefaultButton(Widget dialog) {
    Widget okButton = dialog.getChild("okBtn");
    Widget yesButton = dialog.getChild("yesBtn");

    if (okButton != null) {
      okButton.focus();
    } else if (yesButton != null) {
      yesButton.focus();
    }
  }

  private String buildButtonLayout() {
    StringBuilder html = new StringBuilder();

    switch (buttons) {
      case YES_NO_BUTTONS:
        createButtonXml("yesBtn", "yes", "dialogYesPressed", html);
        createButtonXml("noBtn", "no", "dialogNoPressed", html);
        break;
      case YES_NO_CANCEL_BUTTONS:
        createButtonXml("yesBtn", "yes", "dialogYesPressed", html);
        createButtonXml("noBtn", "no", "dialogNoPressed", html);
        createButtonXml("cancelBtn", "cancel", "dialogCancelPressed", html);
        break;
      case OK_BUTTON:
        createButtonXml("okBtn", "ok", "dialogOkPressed", html);
        break;
      case OK_CANCEL_BUTTON:
        createButtonXml("okBtn", "ok", "dialogOkPressed", html);
        createButtonXml("cancelBtn", "cancel", "dialogCancelPressed", html);
        break;
    }
    return html.toString();
  }

  private void createButtonXml(String name, String text, String action, StringBuilder html) {
    html.append("<button name='");
    html.append(name);
    html.append("' text=' ");
    html.append(App.translate(text));
    html.append(" ' action='");
    html.append(action);
    html.append("'/>");
  }

  public void hide() {
    page.remove(dialogWidget);
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
    result = DialogResult.OK;
    listener.buttonClicked(result);
    hide();
  }

  public void dialogYesPressed() {
    result = DialogResult.YES;
    listener.buttonClicked(result);
    hide();
  }

  public void dialogNoPressed() {
    result = DialogResult.NO;
    listener.buttonClicked(result);
    hide();
  }

  public void dialogCancelPressed() {
    result = DialogResult.CANCEL;
    listener.buttonClicked(result);
    hide();
  }

  public DialogResult getResult() {
    return result;
  }

  public String getTitle() {
     return title;
   }
                      
  public boolean isVisible() {
    return visible;
  }
}
