package com.customwars.client.ui;

import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Insets;

/**
 * A single Menu item containing an icon and text
 * Each of the above is optional and rendered centered in a box, each box is the dimension of the Image/text + the margins
 * The menu item height is set to the highest inner box height.
 *
 * @author stefan
 */
public class StandardMenuItem extends MenuItem {
  private static final int ICON_LEFT_MARGIN = 2, ICON_RIGHT_MARGIN = 1;
  private static final int FONT_HORIZONTAL_MARGIN = 15;

  private Box imgBox = new ImageBox();
  private Box textBox = new TextBox("", container.getDefaultFont());

  public StandardMenuItem(GUIContext container) {
    this(null, null, null, container);
  }

  public StandardMenuItem(String txt, Font font, GUIContext container) {
    this(null, txt, font, container);
  }

  public StandardMenuItem(String txt, GUIContext container) {
    this(null, txt, container.getDefaultFont(), container);
  }

  public StandardMenuItem(Image icon, GUIContext container) {
    this(icon, null, null, container);
  }

  public StandardMenuItem(Image icon, String txt, GUIContext container) {
    this(icon, txt, container.getDefaultFont(), container);
  }

  public StandardMenuItem(Image icon, String txt, Font font, GUIContext container) {
    super(container);
    init(icon, txt, font);
  }

  protected void init(Image icon, String txt, Font font) {
    setIcon(icon);
    setText(txt, font);
    super.layout();
  }

  public void setIcon(Image icon) {
    if (icon != null) {
      Insets insets = new Insets(0, ICON_LEFT_MARGIN, 0, ICON_RIGHT_MARGIN);
      imgBox = new ImageBox(icon, insets);
      super.addBox(imgBox);
    }
  }

  public void setText(String text, Font font) {
    if (text != null) {
      Insets insets = new Insets(0, FONT_HORIZONTAL_MARGIN, 0, FONT_HORIZONTAL_MARGIN);
      textBox = new TextBox(text, font, insets);
      super.addBox(textBox);
    }
  }

  /**
   * Excess horizontal space goes
   * to the text if there is any text
   * to the image if there is no text
   */
  @Override
  protected void initHorizontalBoxSizes() {
    if (textBox.getWidth() > 0) {
      textBox.setWidth(getWidth() - imgBox.getWidth());
    } else {
      imgBox.setWidth(getWidth());
    }
  }
}