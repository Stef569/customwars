package com.customwars.client.ui.state;

import org.newdawn.slick.Input;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.InputProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the input Commands used in cw(Select, Menu up,...)
 * Storing multiple controls into 1 Command object allows
 * to remap keys that activate a command
 *
 * @author stefan
 */
public class CWInput extends InputProvider {
  public static final Command select = new BasicCommand("Select");
  public static final Command cancel = new BasicCommand("Cancel");
  public static final Command exit = new BasicCommand("Exit");
  public static final Command down = new BasicCommand("Down");
  public static final Command up = new BasicCommand("Up");
  public static final Command left = new BasicCommand("Left");
  public static final Command right = new BasicCommand("Right");
  public static final Command toggleMusic = new BasicCommand("Toggle_Music");
  public static final Command zoomIn = new BasicCommand("Zoom_In");
  public static final Command zoomOut = new BasicCommand("Zoom_Out");
  public static final Command fillMap = new BasicCommand("Fill_Map");
  public static final Command nextPage = new BasicCommand("Next_Page");
  public static final Command prevPage = new BasicCommand("Prev_Page");
  public static final Command recolor = new BasicCommand("Recolor");
  public static final Command delete = new BasicCommand("Delete");
  public static final Command toggleFPS = new BasicCommand("Toggle_FPS");

  private static final int KEY_REPEAT_DELAY = 250;
  private Input input;
  private static List<Command> commands = buildCommandList();

  private static List<Command> buildCommandList() {
    List<Command> commands = new ArrayList<Command>();
    Field[] fields = CWInput.class.getDeclaredFields();
    for (Field field : fields) {
      if (isCommandField(field)) {
        try {
          Command command = (Command) field.get(null);
          commands.add(command);
        }
        catch (IllegalAccessException ex) {
          throw new AssertionError(ex.getMessage() + " Should not occur because the field is public and static");
        }
      }
    }
    return commands;
  }

  private static boolean isCommandField(Field field) {
    return Modifier.isPublic(field.getModifiers())
            && Modifier.isStatic(field.getModifiers())
            && Modifier.isFinal(field.getModifiers())
            && Command.class == field.getType();
  }

  /**
   * Create a new input proider which will provide abstract input descriptions
   * based on the input from the supplied context.
   *
   * @param input The input from which this provider will receive events
   */
  public CWInput(Input input) {
    super(input);
    this.input = input;
    input.enableKeyRepeat(0, KEY_REPEAT_DELAY);
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

  public boolean isNextPage(Command command) {
    return nextPage.equals(command);
  }

  public boolean isRecolor(Command command) {
    return recolor.equals(command);
  }

  public boolean isDelete(Command command) {
    return delete.equals(command);
  }

  public boolean isToggleFPS(Command command) {
    return toggleFPS.equals(command);
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
