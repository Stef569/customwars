package com.customwars.client.ui.state;

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
  private Command select = new BasicCommand("Select");
  private Command cancel = new BasicCommand("Cancel");
  private Command exit = new BasicCommand("Exit");
  private Command down = new BasicCommand("Up");
  private Command up = new BasicCommand("Down");
  private Command left = new BasicCommand("Left");
  private Command right = new BasicCommand("Right");
  private Command muteMusic = new BasicCommand("Mute Sound");

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
    initMoveCommands();
  }

  private void initGameCommands() {
    bindCommand(new KeyControl(Input.KEY_A), select);
    bindCommand(new MouseButtonControl(Input.MOUSE_LEFT_BUTTON), select);
    bindCommand(new KeyControl(Input.KEY_B), cancel);
    bindCommand(new MouseButtonControl(Input.MOUSE_RIGHT_BUTTON), cancel);
    bindCommand(new KeyControl(Input.KEY_ESCAPE), exit);
    bindCommand(new KeyControl(Input.KEY_S), muteMusic);
  }

  private void initMoveCommands() {
    bindCommand(new KeyControl(Input.KEY_UP), up);
    bindCommand(new KeyControl(Input.KEY_DOWN), down);
    bindCommand(new KeyControl(Input.KEY_LEFT), left);
    bindCommand(new KeyControl(Input.KEY_RIGHT), right);
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

  public boolean isUpPressed(Command command) {
    return up.equals(command);
  }

  public boolean isDownPressed(Command command) {
    return down.equals(command);
  }

  public boolean isLeftPressed(Command command) {
    return left.equals(command);
  }

  public boolean isRightPressed(Command command) {
    return right.equals(command);
  }

  public boolean isMusicMuted(Command command) {
    return muteMusic.equals(command);
  }
}
