package com.customwars.client.ui.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class GameOverState extends CWState {

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawString("GAME OVER ", 100, 150);
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public int getID() {
    return 10;
  }
}
