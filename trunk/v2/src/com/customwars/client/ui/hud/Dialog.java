package com.customwars.client.ui.hud;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a Swing dialog containing rows of <tt>label: textfield</tt> Components
 * <p/>
 * Usage:
 * Dialog dialog = new Dialog("My dialog");
 * dialog.addTextField("Name");
 * dialog.addTextField("Age");
 * dialog.addSelectBox("City", "London, New York");
 * int res = dialog.show();
 * <p/>
 * // User hit OK
 * if (res == JOptionPane.OK_OPTION) {
 * System.out.println("OK_OPTION");
 * System.out.println(dialog.getFieldValue("name"));
 * }
 */
public class Dialog {
  private final String dialogTitle;
  private final int optionType;
  private final int messageType;
  private final List<JComponent> components;
  private final Map<String, JComponent> fieldValues;

  public Dialog(String dialogTitle) {
    this(dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
  }

  public Dialog(String dialogTitle, int optionType, int messageType) {
    this.dialogTitle = dialogTitle;
    this.optionType = optionType;
    this.messageType = messageType;
    this.components = new ArrayList<JComponent>();
    this.fieldValues = new HashMap<String, JComponent>();
  }

  /**
   * @see #addTextField(String, String)
   */
  public void addTextField(String labelText) {
    addTextField(labelText, "");
  }

  /**
   * Add a label and textfield row to this dialog.
   * The order in wich rows are added matters. When adding 2 rows the first is displayed before the latter.
   *
   * @param labelText     The label to show on the left of the textfield, also used to retrieve the entered textfield value
   * @param textFieldText the default text of the textbox
   */
  public void addTextField(String labelText, String textFieldText) {
    checkIfLabelAlreadyTaken(labelText);
    JLabel label = new JLabel(labelText);
    JTextField textField = new JTextField(textFieldText);
    label.setLabelFor(textField);

    components.add(label);
    components.add(textField);
    fieldValues.put(labelText.toLowerCase(), textField);
  }


  /**
   * Add a label and selectbox row to this dialog.
   * The order in wich rows are added matters. When adding 2 rows the first is displayed before the latter.
   *
   * @param labelText The label to show on the left of the selectbox, also used to retrieve the chosen selectbox entry
   * @param values    the entries of the selectbox
   */
  public void addSelectBox(String labelText, String... values) {
    addSelectBox(labelText, Arrays.asList(values));
  }

  public void addSelectBox(String labelText, Collection<String> values) {
    checkIfLabelAlreadyTaken(labelText);
    JLabel label = new JLabel(labelText);
    JComboBox cboField = new JComboBox(values.toArray());
    label.setLabelFor(cboField);

    components.add(label);
    components.add(cboField);
    fieldValues.put(labelText.toLowerCase(), cboField);
  }

  private void checkIfLabelAlreadyTaken(String lblText) {
    for (String lbl : fieldValues.keySet()) {
      if (lbl.equalsIgnoreCase(lblText)) {
        throw new IllegalArgumentException(lblText + " is already used by " + lbl);
      }
    }
  }

  public int show() {
    return JOptionPane.showConfirmDialog(null, components.toArray(), dialogTitle,
      optionType, messageType);
  }

  /**
   * Retrieves the value from the field
   * where the label of the field equals the given lbl
   * Labels are case insensitive:
   * 'Name' and 'name' will both return the same field value
   */
  public String getFieldValue(String lbl) {
    JComponent component = fieldValues.get(lbl.toLowerCase());

    if (component instanceof JTextField) {
      JTextField field = (JTextField) component;
      return field.getText();
    } else if (component instanceof JComboBox) {
      JComboBox cbo = (JComboBox) component;
      return (String) cbo.getSelectedItem();
    }

    throw new IllegalArgumentException("No field value for " + lbl + " " + fieldValues);
  }
}
