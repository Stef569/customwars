package com.customwars.client.ui.state;

import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CommandEnum;
import org.newdawn.slick.Input;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;
import tools.StringUtil;

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
  public final String INPUT_PREFIX = "user.input";
  public final Command SELECT = new CWCommand(CommandEnum.SELECT);
  public final Command CANCEL = new CWCommand(CommandEnum.CANCEL);
  public final Command EXIT = new CWCommand(CommandEnum.EXIT);
  public final Command DOWN = new CWCommand(CommandEnum.DOWN);
  public final Command UP = new CWCommand(CommandEnum.UP);
  public final Command LEFT = new CWCommand(CommandEnum.LEFT);
  public final Command RIGHT = new CWCommand(CommandEnum.RIGHT);
  public final Command TOGGLE_MUSIC = new CWCommand(CommandEnum.TOGGLE_MUSIC);
  public final Command ZOOM_IN = new CWCommand(CommandEnum.ZOOM_IN);
  public final Command ZOOM_OUT = new CWCommand(CommandEnum.ZOOM_OUT);
  public final Command FILL_MAP = new CWCommand(CommandEnum.FILL_MAP);
  public final Command NEXT_PAGE = new CWCommand(CommandEnum.NEXT_PAGE);
  public final Command PREV_PAGE = new CWCommand(CommandEnum.PREV_PAGE);
  public final Command RECOLOR = new CWCommand(CommandEnum.RECOLOR);
  public final Command DELETE = new CWCommand(CommandEnum.DELETE);
  public final Command TOGGLE_FPS = new CWCommand(CommandEnum.TOGGLE_FPS);
  public final Command END_TURN = new CWCommand(CommandEnum.END_TURN);
  public final Command TOGGLE_CONSOLE = new CWCommand(CommandEnum.TOGGLE_CONSOLE);
  public final Command TOGGLE_EVENT_VIEWER = new CWCommand(CommandEnum.TOGGLE_EVENTVIEWER);
  public final Command SAVE = new CWCommand(CommandEnum.SAVE);
  public final Command OPEN = new CWCommand(CommandEnum.OPEN);

  private Input input;
  private List<Command> commands = buildCommandList();

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
      Command c = (Command) obj;
      if (getControlsFor(c).contains(control))
        return true;
    }
    return false;
  }

  public void resetInputTransition() {
    input.setOffset(0, 0);
    input.setScale(1, 1);
  }

  public Command getCommandByName(String commandName) {
    for (Command c : commands) {
      BasicCommand command = (BasicCommand) c;
      if (command.getName().equalsIgnoreCase(commandName))
        return command;
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

  @SuppressWarnings("unchecked")
  public String getControlsAsText(Command command) {
    List<Control> controls = getControlsFor(command);
    return getControlsAsText(controls, 20);
  }

  @SuppressWarnings("unchecked")
  public String getControlsAsText(Command command, int maxControls) {
    List<Control> controls = getControlsFor(command);
    return getControlsAsText(controls, maxControls);
  }

  public String getControlsAsText(List<Control> controls, int maxControls) {
    String txt = "";
    int controlCounter = 0;

    for (Control control : controls) {
      txt += convertControlToText(control) + ", ";

      if (++controlCounter >= maxControls) {
        break;
      }
    }

    // remove trailing comma and space
    return StringUtil.removeCharsFromEnd(txt, 2);
  }

  public String convertControlToText(Control control) {
    if (control instanceof KeyControl) {
      KeyControl keyControl = (KeyControl) control;
      return Input.getKeyName(keyControl.hashCode());
    } else if (control instanceof MouseButtonControl) {
      MouseButtonControl mouseButtonControl = (MouseButtonControl) control;
      if (mouseButtonControl.hashCode() == Input.MOUSE_LEFT_BUTTON) {
        return "Left Mouse button";
      } else if (mouseButtonControl.hashCode() == Input.MOUSE_MIDDLE_BUTTON) {
        return "Middle Mouse button";
      } else if (mouseButtonControl.hashCode() == Input.MOUSE_RIGHT_BUTTON) {
        return "Right Mouse button";
      } else {
        return "Unknown mouse button";
      }
    } else {
      return "unknown control";
    }
  }

  public void setActive(boolean active) {
    super.setActive(active);

    // Clear any control pressed records, that ended up in the input queue
    input.clearControlPressedRecord();
    input.clearKeyPressedRecord();
    input.clearMousePressedRecord();
  }
}
