package com.customwars.client.tools;

import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Utilities for thingle gui components
 */
public class ThingleUtil {

  public static void fillCboWithNumbers(Page page, String cboWidgetName, int start, int end, int increment) {
    Widget cboWidget = page.getWidget(cboWidgetName);

    for (int i = start; i < end; i += increment) {
      Widget choice = page.createWidget("choice");
      choice.setText(i + "");
      cboWidget.add(choice);
    }
  }
}
