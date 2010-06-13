package com.customwars.client.tools;

import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.spi.ThingleColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilities for thingle gui components
 */
public final class ThingleUtil {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private ThingleUtil() {
  }

  /**
   * Select the child of a combobox with the given text. The cboChildName is entered in the cbo.
   * <p/>
   * If the cboChildName is a choice in the cbo then the selected index is updated
   * to the index of the cboChildName.
   * <p/>
   * If the cboChildName is not a choice in the cbo then the
   * selected index is put to -1(custom value)
   *
   * @param cbo          The combobox where we want to select a child from
   * @param cboChildName The name of the child that we want to select in the cbo
   */
  public static void selectChild(Widget cbo, String cboChildName) {
    int selected = -1;
    for (int childIndex = 0; childIndex < cbo.getChildrenCount(); childIndex++) {
      Widget child = cbo.getChild(childIndex);
      if (child.getText().equals(cboChildName)) {
        selected = childIndex;
      }
    }

    selectChild(cbo, selected);
  }

  /**
   * Select the child of a combobox with the given color property.
   * <p/>
   * If the color is a choice in the cbo then the selected index is updated to the index of the cbo child.
   * <p/>
   * If the color is not a choice in the cbo then the selected index is put to -1(custom value)
   *
   * @param cbo      The combobox where we want to select a child from
   * @param property The name of the color property of a child that we want to select in the cbo (background, foreground)
   * @param color    The color of a child in the cbo widget
   */
  public static void selectChild(Widget cbo, String property, Color color) {
    int selected = -1;

    for (int childIndex = 0; childIndex < cbo.getChildrenCount(); childIndex++) {
      Widget child = cbo.getChild(childIndex);
      ThingleColor childColor = child.getColor(property);

      if (isEqualColor(color, childColor)) {
        selected = childIndex;
        break;
      }
    }

    selectChild(cbo, selected);

    if (selected != -1) {
      cbo.setColor(property, cbo.getChild(selected).getColor(property));
    }
  }

  private static boolean isEqualColor(Color color, ThingleColor childColor) {
    return childColor.getBlue() == color.getBlue() &&
      childColor.getGreen() == color.getGreen() &&
      childColor.getRed() == color.getRed();
  }

  public static void selectChild(Widget cbo, int index) {
    if (index != -1) {
      cbo.setText(cbo.getChild(index).getText());
    }
    cbo.setInteger("selected", index);
  }

  public static void fillCboWithNumbers(Page page, String cboWidgetName, int start, int end, int increment) {
    Widget cboWidget = page.getWidget(cboWidgetName);
    fillCboWithNumbers(page, cboWidget, start, end, increment);
  }

  public static void fillCboWithNumbers(Page page, Widget cboWidget, int start, int end, int increment) {
    Collection<String> numbers = new ArrayList<String>();
    for (int i = start; i < end; i += increment) {
      numbers.add(i + "");
    }
    fillCbo(page, cboWidget, numbers);
  }

  public static void fillCbo(Page page, Widget cboWidget, Iterable<String> data) {
    for (String text : data) {
      Widget choice = page.createWidget("choice");
      choice.setText(text);
      cboWidget.add(choice);
    }
  }

  /**
   * Fill a list widget with string values
   * If scrollToEnd scroll to the end of the list
   */
  public static void fillList(Page page, Widget listWidget, boolean scrollToEnd, String... values) {
    listWidget.removeChildren();
    for (String line : values) {
      Widget listItem = page.createWidget("item");
      listItem.setText(line);
      listWidget.add(listItem);
    }

    if (scrollToEnd) {
      page.layout();
      listWidget.setScroll(0, 100);
    }
  }

  /**
   * Create a choice widget with text, add it to the listWidget
   * and return the create choice widget.
   */
  public static Widget addToList(Page page, Widget listWidget, String text) {
    Widget choice = page.createWidget("choice");
    choice.setText(text);
    listWidget.add(choice);
    return choice;
  }
}
