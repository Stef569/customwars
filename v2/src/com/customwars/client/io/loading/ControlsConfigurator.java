package com.customwars.client.io.loading;

import com.customwars.client.ui.state.input.CWInput;
import com.customwars.client.ui.state.input.LwjglControlUtil;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import tools.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Read input binds from a Properties object
 * only keys that start with INPUT_PREFIX are used, all the other key-value pairs are ignored.
 * The key is the command and the value are the control(s):
 * move_up=UP
 * select=A,Button0
 *
 * Some command examples are:
 * move_up, select, undo
 *
 * Controls:
 * Some key button examples are:
 * UP, CTRL, A, F4, NUMPAD6
 *
 * Some mouse button examples are:
 * Button0(Left mouse button)
 * Button1(Right mouse button)
 *
 * 1 command can have multiple ',' separate controls
 * Command objects should be defined in CWInput
 *
 * @author stefan
 */
public class ControlsConfigurator {
  private CWInput inputProvider;

  public ControlsConfigurator(CWInput inputProvider) {
    this.inputProvider = inputProvider;
  }

  /**
   * Read every key that starts with INPUT_PREFIX
   * Parse the key into a command and the value into controls
   * bind each control -> command to the InputProvider
   *
   * @param properties contains the user input bindings
   *                   key(Command) -> value(controls) ie:
   *                   user.input.select -> a,b,c
   */
  public void configure(Properties properties) {
    for (String key : properties.stringPropertyNames()) {
      String controls = properties.getProperty(key);

      if (key.startsWith(CWInput.INPUT_PREFIX)) {
        String commandName = key.substring(CWInput.INPUT_PREFIX.length() + 1);  //Skip the prefix and the dot
        parseInput(commandName.trim().toLowerCase(), controls.trim().toUpperCase());
      }
    }
  }

  private void parseInput(String commandName, String controls) {
    Command command = inputProvider.getCommandByName(commandName);
    String[] controlsArray = controls.split(",");
    List<Control> controlList = parseControls(controlsArray);
    bindControls(command, controlList);
  }

  private List<Control> parseControls(String[] controls) {
    List<Control> controlList = new ArrayList<Control>();

    for (String controlName : controls) {
      Control control = LwjglControlUtil.getControlByName(controlName);

      if (controlList.contains(control)) {
        throw new IllegalArgumentException("Duplicate control " + controlName + " found");
      } else {
        controlList.add(control);
      }
    }
    return controlList;
  }

  private void bindControls(Command command, List<Control> controls) {
    for (Control control : controls) {
      checkControlAlreadyUsed(control, command);
      inputProvider.bindCommand(control, command);
    }
  }

  /**
   * Check if the control is already used by another command
   *
   * @param control control to be checked for duplicity
   * @param command the name of the command that the given control is going to binded to(for error message only)
   */
  private void checkControlAlreadyUsed(Control control, Command command) {
    boolean alreadyUsed = inputProvider.isControlAlreadyUsed(control);
    String controlText = LwjglControlUtil.getControlAsHumanReadableText(control);
    BasicCommand duplicateCommand = (BasicCommand) inputProvider.getCommandForControl(control);
    String duplicateCommandText = duplicateCommand == null ? "" : duplicateCommand.getName();
    String errMsg = String.format("control %s for %s is already used by %s",
      controlText, command, duplicateCommandText);

    Args.validate(alreadyUsed, errMsg);
  }
}
