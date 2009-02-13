package com.customwars.client.ui;

import org.newdawn.slick.Input;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;

/**
 * Contains all the input Commands used in cw(Select, Menu up,...)
 * Storing multiple controls into 1 Command object allows
 * to remap keys that active a command and to load input from a file.
 *
 * @author stefan
 */
public class CWInput extends InputProvider {
  /// Game commands
  private Command select = new BasicCommand("Select");
  private Command cancel = new BasicCommand("Cancel");
  private Command exit = new BasicCommand("Exit");

  // Menu commands
  private Command nextMenuItem = new BasicCommand("Next MenuItem");
  private Command previousMenuItem = new BasicCommand("Previous MenuItem");

  /**
   * Create a new input proider which will provide abstract input descriptions
   * based on the input from the supplied context.
   *
   * @param input The input from which this provider will receive events
   */
  public CWInput(Input input) {
    super(input);
    initDefaults();
  }

  private void initDefaults() {
    initGameCommands();
    initMenuCommands();
    bindCommand(new KeyControl(Input.KEY_ESCAPE), exit);
  }

  private void initGameCommands() {
    bindCommand(new KeyControl(Input.KEY_A), select);
    bindCommand(new MouseButtonControl(Input.MOUSE_LEFT_BUTTON), select);
    bindCommand(new KeyControl(Input.KEY_B), cancel);
    bindCommand(new MouseButtonControl(Input.MOUSE_RIGHT_BUTTON), cancel);
  }

  private void initMenuCommands() {
    bindCommand(new KeyControl(Input.KEY_UP), previousMenuItem);
    bindCommand(new KeyControl(Input.KEY_DOWN), nextMenuItem);
  }

  public boolean isSelectPressed(Command command) {
    return select.equals(command);
  }

  public boolean isCancelPressed(Command command) {
    return cancel.equals(command);
  }

  public boolean isExitPressed(Command command) {
    return exit.equals(command);
  }

  public boolean isNextMenuItemPressed(Command command) {
    return nextMenuItem.equals(command);
  }

  public boolean isPreviousMenuItemPressed(Command command) {
    return previousMenuItem.equals(command);
  }
}
