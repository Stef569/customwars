package com.customwars.client.ui.thingle;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.spi.ThingleException;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays a Thingle dialog containing rows of <tt>label: textfield</tt> Widgets
 * <p/>
 * Usage:
 * final ThingleInputDialog dialog = new ThingleInputDialog("New Person", new InputDialogListener() {
 * public void buttonClicked(ThingleInputDialog dialog, DialogResult button) {
 * if (button == DialogResult.OK) {
 * System.out.println("OK_OPTION");
 * System.out.println(dialog.getFieldValue("name"));
 * System.out.println(dialog.getFieldValue("age"));
 * }
 * }
 * });
 * dialog.addTextField("Name", "Me");
 * dialog.addTextField("Age", "25");
 * dialog.show();
 * }
 */
public class ThingleInputDialog implements ThingleDialog {
  private final String title;                             // The title of the dialog
  private final Page page;                                // The Thingle page to render the dialog
  private final Map<String, Widget> fieldValues;          // The textfield widgets by label name
  private boolean visible;                                // True if the dialog is visible
  private Widget root;                                    // The dialog Widget containing all other widgets
  private InputDialogListener listener;                   // The listener to be notified of dialog events

  public ThingleInputDialog(String title, InputDialogListener listener) {
    this.title = title;
    this.listener = listener;
    page = new Page();
    fieldValues = new HashMap<String, Widget>();
    initThingle();
  }

  private void initThingle() {
    Theme theme = new ThinglePageLoader(App.get("gui.path")).loadTheme("greySkin.properties");
    page.setDrawDesktop(false);
    page.setTheme(theme);
    buildLayout();
  }

  public void buildLayout() {
    try {
      String xml =
        "<dialog name='input_dialog' columns='1' modal='true'>" +
          "<panel name='container' columns='1' top='10' left='10' bottom='10' right='10' gap='8'/>" +
          "<panel name='buttons' halign='right' top='10' left='10' bottom='10' right='10' gap='20'>" +
          "<button text='ok' action='ok() '/>" +
          "<button text='Cancel'  action='cancel()'/>" +
          "</panel>" +
          "</dialog>";
      root = page.parse(xml, this);
      page.add(root);

      root.setText(title);
    } catch (ThingleException ex) {
      throw new RuntimeException("Check the xml it's invalid", ex);
    }
  }

  /**
   * @see #addTextField(String, String)
   */
  public void addTextField(String labelText) {
    addTextField(labelText, "");
  }

  /**
   * Add a label and textfield row to this dialog.
   * The order in which rows are added matters. When adding 2 rows the first is displayed before the latter.
   *
   * @param labelText     The label to show on the left of the textfield, also used to retrieve the entered textfield value
   * @param textFieldText the default text of the textbox
   */
  public void addTextField(String labelText, String textFieldText) {
    checkIfLabelAlreadyTaken(labelText);
    Widget label = page.createWidget("label");
    label.setText(labelText);
    Widget textField = page.createWidget("textfield");
    textField.setText(textFieldText);
    textField.setInteger("columns", 26);

    Widget container = page.getWidget("container");
    container.add(label);
    container.add(textField);

    fieldValues.put(labelText.toLowerCase(), textField);
  }

  private void checkIfLabelAlreadyTaken(String lblText) {
    for (String lbl : fieldValues.keySet()) {
      if (lbl.equalsIgnoreCase(lblText)) {
        throw new IllegalArgumentException(lblText + " is already used by " + lbl);
      }
    }
  }

  /**
   * Retrieves the value from the field
   * where the label of the field equals the given lbl
   * Labels are case insensitive:
   * 'Name' and 'name' will both return the same field value
   */
  public String getFieldValue(String lbl) {
    if (fieldValues.containsKey(lbl)) {
      Widget widget = fieldValues.get(lbl.toLowerCase());
      return widget.getText();
    } else {
      throw new IllegalArgumentException("No field for label" + lbl);
    }
  }

  public void ok() {
    listener.buttonClicked(this, DialogResult.OK);
    hide();
  }

  public void cancel() {
    listener.buttonClicked(this, DialogResult.CANCEL);
    hide();
  }

  public void hide() {
    page.remove(root);
    page.disable();
    visible = false;
  }

  public void show() {
    page.layout();
    page.enable();
    visible = true;
  }


  public void render() {
    if (visible) {
      page.render();
    }
  }

  public String getTitle() {
    return title;
  }

  public boolean isVisible() {
    return visible;
  }
}
