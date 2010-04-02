package com.customwars.client.ui;

import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Insets;

/**
 * A single Menu item containing: The unit image, the unit name and the unit price.
 * Each of the above is rendered in a box, each box is the dimension of the Image/text + the margins
 * The menu item height is set to the highest inner box height.
 *
 * @author stefan
 */
public class BuyUnitMenuItem extends MenuItem {
  private static final int FONT_HORIZONTAL_MARGIN = 5;
  private static final int ICON_LEFT_MARGIN = 2, ICON_RIGHT_MARGIN = 1;

  private Box imgBox = new ImageBox();
  private Box unitNameTextBox = new TextBox("", container.getDefaultFont());
  private Box unitPriceTextBox = new TextBox("", container.getDefaultFont());

  public BuyUnitMenuItem(Image icon, String unitName, String price, Font font, GUIContext container) {
    super(container);
    init(icon, unitName, price, font);
  }

  protected void init(Image icon, String unitName, String price, Font font) {
    setIcon(icon);
    setUnitNameText(unitName, font);
    setUnitPriceText(price, font);
    super.layout();
  }

  public void setIcon(Image icon) {
    if (icon != null) {
      Insets insets = new Insets(0, ICON_LEFT_MARGIN, 0, ICON_RIGHT_MARGIN);
      imgBox = new ImageBox(icon, insets);
      super.addBox(imgBox);
    }
  }

  public void setUnitNameText(String text, Font font) {
    if (text != null) {
      Insets insets = new Insets(0, FONT_HORIZONTAL_MARGIN, 0, FONT_HORIZONTAL_MARGIN);
      unitNameTextBox = new TextBox(text, font, insets);
      unitNameTextBox.setAlignment(Box.ALIGNMENT.LEFT);
      super.addBox(unitNameTextBox);
    }
  }

  public void setUnitPriceText(String text, Font font) {
    if (text != null) {
      Insets insets = new Insets(0, FONT_HORIZONTAL_MARGIN, 0, FONT_HORIZONTAL_MARGIN);
      unitPriceTextBox = new TextBox(text, font, insets);
      super.addBox(unitPriceTextBox);
    }
  }

  /**
   * Excess horizontal space goes
   * to the unit name if there is any text
   * to the image if there is no text
   */
  @Override
  protected void initHorizontalBoxSizes() {
    if (unitNameTextBox.getWidth() > 0) {
      unitNameTextBox.setWidth(getWidth() - imgBox.getWidth() - unitPriceTextBox.getWidth());
    } else {
      imgBox.setWidth(getWidth());
    }
  }
}