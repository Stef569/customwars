package com.customwars.client.ui.state;

import org.newdawn.slick.Input;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.InputProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Contains all the input Commands used in cw(Select, Menu up,...)
 * Storing multiple controls into 1 Command object allows
 * to remap keys that activate a command
 *
 * @author stefan
 */
public class CWInput extends InputProvider {
  private Input input;
  public static Command select = new BasicCommand("Select");
  public static Command cancel = new BasicCommand("Cancel");
  public static Command exit = new BasicCommand("Exit");
  public static Command down = new BasicCommand("Down");
  public static Command up = new BasicCommand("Up");
  public static Command left = new BasicCommand("Left");
  public static Command right = new BasicCommand("Right");
  public static Command toggleMusic = new BasicCommand("Toggle_Music");
  public static Command zoomIn = new BasicCommand("Zoom_In");
  public static Command zoomOut = new BasicCommand("Zoom_Out");
  public static Command fillMap = new BasicCommand("Fill_Map");
  private List<Command> commands;

  /**
   * Create a new input proider which will provide abstract input descriptions
   * based on the input from the supplied context.
   *
   * @param input The input from which this provider will receive events
   */
  public CWInput(Input input) {
    super(input);
    this.input = input;
    commands = Arrays.asList(
            select, cancel, exit,
            down, up, left, right,
            toggleMusic,
            zoomIn, zoomOut, fillMap);
  }

  @Override
  public void bindCommand(Control control, Command command) {
    if (!isControlAlreadyUsed(control))
      super.bindCommand(control, command);
  }

  public boolean isControlAlreadyUsed(Control control) {
    List commands = getUniqueCommands();
    for (Object obj : commands) {
      Command c = (Command) obj;
      if (getControlsFor(c).contains(control))
        return true;
    }
    return false;
  }

  public boolean isSelect(Command command) {
    return select.equals(command);
  }

  public boolean isCancel(Command command) {
    return cancel.equals(command);
  }

  public boolean isExit(Command command) {
    return exit.equals(command);
  }

  public boolean isUp(Command command) {
    return up.equals(command);
  }

  public boolean isDown(Command command) {
    return down.equals(command);
  }

  public boolean isLeft(Command command) {
    return left.equals(command);
  }

  public boolean isRight(Command command) {
    return right.equals(command);
  }

  public boolean isToggleMusic(Command command) {
    return toggleMusic.equals(command);
  }

  public boolean isZoomIn(Command command) {
    return zoomIn.equals(command);
  }

  public boolean isZoomOut(Command command) {
    return zoomOut.equals(command);
  }

  public boolean isFillMap(Command command) {
    return fillMap.equals(command);
  }

  public Command getCommandByName(String commandName) {
    for (Command c : commands) {
      BasicCommand command = (BasicCommand) c;
      if (command.getName().equalsIgnoreCase(commandName))
        return command;
    }
    throw new IllegalArgumentException("No command found for " + commandName + " " + commands);
  }

  public int getMouseX() {
    return input.getMouseX();
  }

  public int getMouseY() {
    return input.getMouseY();
  }
}
