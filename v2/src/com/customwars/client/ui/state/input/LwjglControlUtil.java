package com.customwars.client.ui.state.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;

/**
 * Transform Lwjgl controls to text
 * control names are always returned in lower case
 */
public final class LwjglControlUtil {
  private static final String MOUSE_BTN_PREFIX = "BUTTON";

  private LwjglControlUtil() {
  }

  /**
   * Retrieve a Control for a control name ie
   * "F4" will return a KeyControl object that represents the key F4 in Lwjgl
   */
  public static Control getControlByName(String controlName) {
    Control control;

    if (controlName.startsWith(MOUSE_BTN_PREFIX)) {
      control = getMouseControlByName(controlName);
    } else {
      control = getKeyControlByName(controlName);
    }
    return control;
  }

  private static Control getKeyControlByName(String controlName) {
    int key = Keyboard.getKeyIndex(controlName);

    if (key == Keyboard.KEY_NONE) {
      throw new IllegalArgumentException("No Key control for " + controlName);
    }

    return new KeyControl(key);
  }

  private static Control getMouseControlByName(String controlName) {
    int mouseBtn = Mouse.getButtonIndex(controlName);
    if (mouseBtn == -1) {
      throw new IllegalArgumentException("No Mouse control for " + controlName);
    }
    return new MouseButtonControl(mouseBtn);
  }

  public static String getControlAsText(Object control) {
    String controlName;

    if (control instanceof KeyControl) {
      KeyControl keyControl = (KeyControl) control;
      controlName = Keyboard.getKeyName(keyControl.hashCode());
    } else if (control instanceof MouseButtonControl) {
      MouseButtonControl mouseControl = (MouseButtonControl) control;
      controlName = Mouse.getButtonName(mouseControl.hashCode());
    } else {
      throw new IllegalArgumentException("Control " + control + " is not supported");
    }
    return controlName.trim().toLowerCase();
  }

  public static String getControlAsHumanReadableText(Object control) {
    if (control instanceof KeyControl) {
      KeyControl keyControl = (KeyControl) control;
      return Input.getKeyName(keyControl.hashCode());
    } else if (control instanceof MouseButtonControl) {
      MouseButtonControl mouseButtonControl = (MouseButtonControl) control;
      return getMouseButtonAsHumanReadableText(mouseButtonControl);
    } else {
      return "Unknown control";
    }
  }

  private static String getMouseButtonAsHumanReadableText(MouseButtonControl mouseButtonControl) {
    int mouseBtnHash = mouseButtonControl.hashCode();

    if (mouseBtnHash == Input.MOUSE_LEFT_BUTTON) {
      return "Left mouse button";
    } else if (mouseBtnHash == Input.MOUSE_MIDDLE_BUTTON) {
      return "Middle mouse button";
    } else if (mouseBtnHash == Input.MOUSE_RIGHT_BUTTON) {
      return "Right mouse button";
    } else {
      return "Unknown mouse button";
    }
  }
}
