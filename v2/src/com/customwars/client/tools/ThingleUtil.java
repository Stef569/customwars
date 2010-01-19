package com.customwars.client.tools;

import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

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
   *
   * If the cboChildName is a choice in the cbo then the selected index is updated
   * to the index of the cboChildName.
   *
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

    cbo.setText(cboChildName);
    cbo.setInteger("selected", selected);
  }

  public static void fillCboWithNumbers(Page page, String cboWidgetName, int start, int end, int increment) {
    Widget cboWidget = page.getWidget(cboWidgetName);

    for (int i = start; i < end; i += increment) {
      Widget choice = page.createWidget("choice");
      choice.setText(i + "");
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
