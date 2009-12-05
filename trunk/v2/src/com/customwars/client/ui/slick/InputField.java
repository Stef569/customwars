package com.customwars.client.ui.slick;

import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Input;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.TextField;

import java.util.List;

/**
 * The InputField class displays the keys mapped to a particular command and allows the user to
 * change the mapped keys. The user selects an InputField by clicking on it, then can press any key or todo (Including the mouse wheel)
 * to change the mapped value.
 */
public class InputField extends TextField {
  private static final int CLEAR_FIELD = Input.KEY_BACK;
  private CWInput cwInput;
  private Command command;
  private int bindingLimit;

  public InputField(GUIContext container, int x, int y, int width, int height, Command command, CWInput cwInput) {
    this(container, x, y, width, height, null, command, cwInput);
  }

  public InputField(GUIContext container, int x, int y, int width, int height, ComponentListener listener, Command command, CWInput cwInput) {
    super(container, container.getDefaultFont(), x, y, width, height, listener);
    this.command = command;
    this.cwInput = cwInput;
    this.bindingLimit = 250;
  }

  /**
   * Set the display text to the names of the controls mapped to command
   * as a ',' separated list
   */
  public void initDisplayText() {
    String controlsAsText = getControlsAsText();
    setText(controlsAsText);
  }

  private String getControlsAsText() {
    StringBuilder controlsBuilder = new StringBuilder(bindingLimit * 2);
    List<String> controlsAsText = cwInput.getControlsAsText(command);

    for (int i = 0; i < controlsAsText.size() && controlsAsText.size() < bindingLimit; i++) {
      if (i < bindingLimit) {
        controlsBuilder.append(controlsAsText.get(i));
        controlsBuilder.append(", ");
      } else {
        controlsAsText.remove(i);
      }
    }

    // Remove ', '
    return StringUtil.removeCharsFromEnd(controlsBuilder.toString(), 2);
  }

  @Override
  public void keyPressed(int key, char c) {
    if (!hasFocus()) return;

    if (key == CLEAR_FIELD && !cwInput.getControlsFor(command).isEmpty()) {
      for (Object control : cwInput.getControlsFor(command)) {
        cwInput.unbindCommand((Control) control);
      }
    } else {
      cwInput.bindCommand(new KeyControl(key), command);
    }
    initDisplayText();
  }

  public void setBindingLimit(int bindingLimit) {
    this.bindingLimit = bindingLimit;
  }
}
