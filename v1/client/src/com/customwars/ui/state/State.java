package com.customwars.ui.state;

import java.awt.*;

/**
 * A State is an independant Graphical component
 * First init is invoked
 * then we paint the component when needed
 * when this state is going to be terminated stop is invoked, allowing the state to perform any clean up needed.
 *
 * @author stefan
 * @since 2.0
 */
public interface State {
  void init();

  void paint(Graphics2D g);

  void stop();
}
