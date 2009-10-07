package com.customwars.client.ui.state;

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
  public static final Command select = new BasicCommand("select");
  public static final Command cancel = new BasicCommand("cancel");
  public static final Command exit = new BasicCommand("exit");
  public static final Command down = new BasicCommand("down");
  public static final Command up = new BasicCommand("up");
  public static final Command left = new BasicCommand("left");
  public static final Command right = new BasicCommand("right");
  public static final Command toggleMusic = new BasicCommand("toggle_music");
  public static final Command zoomIn = new BasicCommand("zoom_in");
  public static final Command zoomOut = new BasicCommand("zoom_out");
  public static final Command fillMap = new BasicCommand("fill_map");
  public static final Command nextPage = new BasicCommand("next_page");
  public static final Command prevPage = new BasicCommand("prev_page");
  public static final Command recolor = new BasicCommand("recolor");
  public static final Command delete = new BasicCommand("delete");
  public static final Command toggleFPS = new BasicCommand("toggle_fps");
  public static final Command endTurn = new BasicCommand("end_turn");
  public static final Command toggleConsole = new BasicCommand("toggle_console");
  public static final Command toggleEventViewer = new BasicCommand("toggle_eventviewer");
  public static final Command save = new BasicCommand("save");
  public static final Command open = new BasicCommand("open");

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

  public boolean isEndTurn(Command command) {
    return endTurn.equals(command);
  }

  public boolean isToggleConsole(Command command) {
    return toggleConsole.equals(command);
  }

  public boolean isToggleEventViewer(Command command) {
    return toggleEventViewer.equals(command);
  }

  public boolean isSave(Command command) {
    return save.equals(command);
  }

  public boolean isOpen(Command command) {
    return open.equals(command);
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
    for (Command command : CWInput.commands) {
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

    for (Control controlObj : controls) {
      Control control = controlObj;
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
