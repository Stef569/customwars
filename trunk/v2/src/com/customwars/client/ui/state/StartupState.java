package com.customwars.client.ui.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * State in which all resources are loaded
 *
 * @author stefan
 */
public class StartupState extends CWState {
  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    resources.loadAll();
    changeToState("MAIN_MENU");
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    renderLoadingProgress(g);
  }

  private void renderLoadingProgress(Graphics g) {
    g.drawString("Loading...", 100, 100);
  }

  public int getID() {
    return 0;
  }
}
