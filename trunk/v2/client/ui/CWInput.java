package client.ui;

import org.newdawn.slick.Input;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;

/**
 * Contains all the input Commands used in cw
 * Storing the mapping of keys into 1 object allows
 * to remap keys that active a command or to load different inputs.
 *
 * @author stefan
 */
public class CWInput {
  private InputProvider inputProvider;
  /// Game commands
  private Command select = new BasicCommand("Select");
  private Command cancel = new BasicCommand("Cancel");
  private Command exit = new BasicCommand("Exit");

  // Menu commands
  private Command nextMenuItem = new BasicCommand("Next MenuItem");
  private Command previousMenuItem = new BasicCommand("Previous MenuItem");

  public CWInput(Input input) {
    inputProvider = new InputProvider(input);
    initDefaults();
  }

  private void initDefaults() {
    initGameCommands();
    initMenuCommands();
    inputProvider.bindCommand(new KeyControl(Input.KEY_ESCAPE), exit);
  }

  private void initGameCommands() {
    inputProvider.bindCommand(new KeyControl(Input.KEY_A), select);
    inputProvider.bindCommand(new MouseButtonControl(Input.MOUSE_LEFT_BUTTON), select);
    inputProvider.bindCommand(new KeyControl(Input.KEY_B), cancel);
    inputProvider.bindCommand(new MouseButtonControl(Input.MOUSE_RIGHT_BUTTON), cancel);
  }

  private void initMenuCommands() {
    inputProvider.bindCommand(new KeyControl(Input.KEY_UP), previousMenuItem);
    inputProvider.bindCommand(new KeyControl(Input.KEY_DOWN), nextMenuItem);
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

  public void addCommandListener(InputProviderListener listener) {
    inputProvider.addListener(listener);
  }

  public void removeCommandListener(InputProviderListener listener) {
    inputProvider.removeListener(listener);
  }
}
