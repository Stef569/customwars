package com.customwars.client.io.loading;

import com.customwars.client.ui.state.CWInput;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;
import tools.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Read User preferences:
 * Input: keys, mouse scroll+buttons
 *
 * @author stefan
 */
public class UserConfigParser {
  public static final String INPUT_PREFIX = "user.input";
  private static final String MOUSE_BTN_PREFIX = "BUTTON";
  private CWInput inputProvider;

  public UserConfigParser(CWInput inputProvider) {
    this.inputProvider = inputProvider;
  }

  /**
   * Read every key that starts with INPUT_PREFIX
   * Parse the key into a command and controls, add each
   * command/controls pair to the inputProvider
   *
   * @param properties containing the user Input configuration ie:
   * user.input.select -> a,b,c
   */
  public void readInputConfig(Properties properties) {
    for (Object o : properties.stringPropertyNames()) {
      String key = (String) o;
      String val = properties.getProperty(key);
      if (key.startsWith(INPUT_PREFIX)) {
        key = key.substring(INPUT_PREFIX.length() + 1);  //Skip the prefix and the dot
        parseInput(key, val);
      }
    }
  }

  private void parseInput(String commandName, String controls) {
    Command command = inputProvider.getCommandByName(commandName);
    List<Control> controlList = getControls(controls);

    for (Control control : controlList) {
      inputProvider.bindCommand(control, command);
    }
  }

  private List<Control> getControls(String controls) {
    List<Control> controlList = new ArrayList<Control>();

    for (StringTokenizer tok = new StringTokenizer(controls, ","); tok.hasMoreTokens();) {
      String controlName = tok.nextToken().toUpperCase();
      Control control = getControlByName(controlName);
      Args.checkForNull(control, "control is null");

      if (!controlList.contains(control))
        controlList.add(control);
    }
    return controlList;
  }

  private Control getControlByName(String controlName) {
    Control control;
    if (controlName.startsWith(MOUSE_BTN_PREFIX)) {
      control = getMouseControlByName(controlName);
    } else {
      control = getKeyControlByName(controlName);
    }
    return control;
  }

  private Control getKeyControlByName(String controlName) {
    int key = Keyboard.getKeyIndex(controlName);
    if (key == Keyboard.KEY_NONE) {
      throw new IllegalArgumentException("No Key control for " + controlName);
    }

    return new KeyControl(key);
  }

  private Control getMouseControlByName(String controlName) {
    int mouseBtn = Mouse.getButtonIndex(controlName);
    if (mouseBtn == -1) {
      throw new IllegalArgumentException("No Mouse control for " + controlName);
    }
    return new MouseButtonControl(mouseBtn);
  }

  /**
   * Writes the current command bindings to a new Properties file
   */
  public Properties writeInputConfig() {
    Properties properties = new Properties();
    for (Object obj : inputProvider.getUniqueCommands()) {
      BasicCommand command = (BasicCommand) obj;
      List controls = inputProvider.getControlsFor(command);
      writeLine(command, controls, properties);
    }
    return properties;
  }

  private void writeLine(BasicCommand command, List controls, Properties properties) {
    String commandName = command.getName();
    String controlList = "";

    for (Object control : controls) {
      String controlName;
      if (control instanceof KeyControl) {
        controlName = getKeyString((KeyControl) control);
      } else if (control instanceof MouseButtonControl) {
        controlName = getMouseString((MouseButtonControl) control);
      } else {
        throw new IllegalArgumentException("Control " + control + " is not supported");
      }
      controlList += controlName + ",";
    }

    // Strip trailing ,
    if (controlList.length() > 0) {
      controlList = controlList.substring(0, controlList.length() - 1);
    }
    properties.setProperty(INPUT_PREFIX + "." + commandName, controlList);
  }

  private String getKeyString(KeyControl keyControl) {
    String keyName = Keyboard.getKeyName(keyControl.hashCode());
    if (keyName == null) keyName = "";
    return keyName;
  }

  private String getMouseString(MouseButtonControl mouseControl) {
    String mouseBtn = Mouse.getButtonName(mouseControl.hashCode());
    if (mouseBtn == null) mouseBtn = "";
    return mouseBtn;
  }
}
