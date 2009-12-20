package com.customwars.client.ui.layout;

import com.customwars.client.ui.Component;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Lay out Components
 * from left-right, right-left, top-bottom, bottom-top
 */
public class Layout {

  public static void locateLeftToRight(Component[] components, int leftMargin, int topMargin) {
    locateLeftToRight(Arrays.asList(components), leftMargin, topMargin);
  }

  public static void locateLeftToRight(List<Component> components, int leftMargin, int topMargin) {
    Point startPoint = new Point(leftMargin, topMargin);
    int currentXPos = 0;

    for (int i = 0; i < components.size(); i++) {
      Component comp = components.get(i);

      if (i == 0) {
        currentXPos = startPoint.x;
      } else {
        Component nextComp = components.get(i - 1);
        currentXPos = nextComp.getMaxX();
      }

      comp.setLocation(currentXPos, startPoint.y);
    }
  }

  public static void locateRightToLeft(Component[] components, int leftMargin, int topMargin) {
    locateRightToLeft(Arrays.asList(components), leftMargin, topMargin);
  }

  public static void locateRightToLeft(List<Component> components, int leftMargin, int topMargin) {
    Point startPoint = new Point(leftMargin, topMargin);
    int currentXPos = startPoint.x;

    for (int i = 0; i < components.size(); i++) {
      Component comp = components.get(i);
      Component nextComp;

      if (i == 0) {
        currentXPos = startPoint.x - comp.getWidth();
      } else {
        nextComp = components.get(i);
        currentXPos -= nextComp.getWidth();
      }

      comp.setLocation(currentXPos, startPoint.y);
    }
  }

  public static void locateBottomToTop(Component[] components, int leftMargin, int topMargin) {
    locateBottomToTop(Arrays.asList(components), leftMargin, topMargin);
  }

  public static void locateBottomToTop(Collection<Component> components, int leftMargin, int topMargin) {
    Point startPoint = new Point(leftMargin, topMargin);
    int currentYPos = startPoint.y;

    for (Component comp : components) {
      currentYPos -= comp.getHeight();
      comp.setLocation(startPoint.x, currentYPos);
    }
  }

  public static void locateTopToBottom(Component[] components, int leftMargin, int topMargin) {
    locateTopToBottom(Arrays.asList(components), leftMargin, topMargin);
  }

  public static void locateTopToBottom(Collection<Component> components, int leftMargin, int topMargin) {
    Point startPoint = new Point(leftMargin, topMargin);
    int currentYPos = startPoint.y;

    for (Component comp : components) {
      comp.setLocation(startPoint.x, currentYPos);
      currentYPos += comp.getHeight();
    }
  }
}
