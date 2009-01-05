package com.customwars.ui.menus;

import java.awt.*;

/**
 * A State is an independant Graphical component
 * First it init is invoked
 * then we paint the component when needed
 * when this state is over stop is invoked
 *
 * @author stefan
 * @since 2.0
 */
public interface State {
  void init();

  void paint(Graphics2D g);

  void stop();
}
