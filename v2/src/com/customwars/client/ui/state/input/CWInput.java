package com.customwars.client.ui.state.input;

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
 * Multiple controls can be mapped to 1 Command object
 *
 * @author stefan
 */
public class CWInput extends InputProvider {
  public static final Command SELECT = new CWCommand(CommandEnum.SELECT);
  public static final Command CANCEL = new CWCommand(CommandEnum.CANCEL);
  public static final Command EXIT = new CWCommand(CommandEnum.EXIT);
  public static final Command DOWN = new CWCommand(CommandEnum.DOWN);
  public static final Command UP = new CWCommand(CommandEnum.UP);
  public static final Command LEFT = new CWCommand(CommandEnum.LEFT);
  public static final Command RIGHT = new CWCommand(CommandEnum.RIGHT);
  public static final Command TOGGLE_MUSIC = new CWCommand(CommandEnum.TOGGLE_MUSIC);
  public static final Command ZOOM_IN = new CWCommand(CommandEnum.ZOOM_IN);
  public static final Command ZOOM_OUT = new CWCommand(CommandEnum.ZOOM_OUT);
  public static final Command FILL_MAP = new CWCommand(CommandEnum.FILL_MAP);
  public static final Command NEXT_PAGE = new CWCommand(CommandEnum.NEXT_PAGE);
  public static final Command PREV_PAGE = new CWCommand(CommandEnum.PREV_PAGE);
  public static final Command RECOLOR = new CWCommand(CommandEnum.RECOLOR);
  public static final Command DELETE = new CWCommand(CommandEnum.DELETE);
  public static final Command TOGGLE_FPS = new CWCommand(CommandEnum.TOGGLE_FPS);
  public static final Command END_TURN = new CWCommand(CommandEnum.END_TURN);
  public static final Command TOGGLE_CONSOLE = new CWCommand(CommandEnum.TOGGLE_CONSOLE);
  public static final Command TOGGLE_EVENT_VIEWER = new CWCommand(CommandEnum.TOGGLE_EVENT_VIEWER);
  public static final Command SAVE = new CWCommand(CommandEnum.SAVE);
  public static final Command OPEN = new CWCommand(CommandEnum.OPEN);
  public static final Command NEW = new CWCommand(CommandEnum.NEW);
  public static final Command UNIT_CYCLE = new CWCommand(CommandEnum.UNIT_CYCLE);

  private final Input input;
  private final List<Command> commands = buildCommandList();

  private List<Command> buildCommandList() {
    List<Command> commands = new ArrayList<Command>();
    Field[] fields = CWInput.class.getDeclaredFields();
    for (Field field : fields) {
      if (isCommandField(field)) {
        try {
          Command command = (Command) field.get(this);
          commands.add(command);
        }
        catch (IllegalAccessException ex) {
          throw new AssertionError(ex.getMessage() + " Should not occur because the field is public");
        }
      }
    }
    return commands;
  }

  private boolean isCommandField(Field field) {
    return Modifier.isPublic(field.getModifiers())
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
    input.enableKeyRepeat();
  }

  @Override
  public void bindCommand(Control control, Command command) {
    if (!isControlAlreadyUsed(control))
      super.bindCommand(control, command);
  }

  public boolean isControlAlreadyUsed(Control control) {
    List commands = getUniqueCommands();
    for (Object obj : commands) {
      Command command = (Command) obj;
      if (getControlsFor(command).contains(control))
        return true;
    }
    return false;
  }

  public void resetInputTransform() {
    input.resetInputTransform();
  }

  public Command getCommandByName(String commandName) {
    for (Command command : commands) {
      BasicCommand basicCommand = (BasicCommand) command;
      if (basicCommand.getName().equalsIgnoreCase(commandName))
        return basicCommand;
    }
    throw new IllegalArgumentException("No command found for " + commandName + " " + commands);
  }

  public Command getCommandForControl(Control control) {
    for (Command command : commands) {
      if (getControlsFor(command).contains(control)) {
        return command;
      }
    }
    return null;
  }

  public int getMouseX() {
    return input.getMouseX();
  }

  public int getMouseY() {
    return input.getMouseY();
  }

  public int getAbsoluteMouseX() {
    return input.getAbsoluteMouseX();
  }

  public int getAbsoluteMouseY() {
    return input.getAbsoluteMouseY();
  }

  @SuppressWarnings("unchecked")
  public List<String> getControlsAsText(Command command) {
    List<Control> controls = getControlsFor(command);
    List<String> controlsAsText = new ArrayList();

    for (Control control : controls) {
      String humanReadableControlName = LwjglControlUtil.getControlAsHumanReadableText(control);
      controlsAsText.add(humanReadableControlName);
    }

    return controlsAsText;
  }

  public void setActive(boolean active) {
    super.setActive(active);

    // Clear any control pressed records, that are still in the input queue
    input.clearControlPressedRecord();
    input.clearKeyPressedRecord();
    input.clearMousePressedRecord();
  }
}