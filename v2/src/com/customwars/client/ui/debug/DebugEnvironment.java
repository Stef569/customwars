package com.customwars.client.ui.debug;

import bsh.Interpreter;
import bsh.util.JConsole;
import com.customwars.client.model.game.Game;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Adds additional tools for programmers
 * Events from the model, live objects
 * The JFrames are docked to the right.
 *
 * @author stefan
 */
public class DebugEnvironment {
  private final Dimension screenSize;
  private static final int dockWidth = 400;
  private static final int modelEventsFrameHeight = 300;
  private static final int beanshellFrameHeight = 400;

  private final JFrame eventFrame = new JFrame("Model Events");
  private final JFrame scriptFrame = new JFrame("Beanshell");

  private Interpreter bsh;

  public DebugEnvironment(JConsole console, Interpreter bsh, Game game) {
    this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.bsh = bsh;
    initModelEventsGui(game);
    initBeanshellGui(console);
  }

  private void initModelEventsGui(Game game) {
    final ModelDebugEventScreen modelDebugEventScreen = new ModelDebugEventScreen(eventFrame);
    modelDebugEventScreen.addModelEventListeners(game);

    eventFrame.add(modelDebugEventScreen.getGui());
  }

  private void initBeanshellGui(JConsole console) {
    scriptFrame.add(console);
    scriptFrame.setBounds((int) screenSize.getWidth() - dockWidth, modelEventsFrameHeight, dockWidth, beanshellFrameHeight);
    scriptFrame.setVisible(true);
    new Thread(bsh).start();
  }

  public void show() {
    eventFrame.setBounds((int) screenSize.getWidth() - dockWidth, 0, dockWidth, modelEventsFrameHeight);
    eventFrame.setVisible(true);
  }
}
