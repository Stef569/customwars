package com.customwars.client.ui.state;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class GameOverState extends CWState {
  private Logger logger = Logger.getLogger(GameOverState.class);

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    logger.info("Game Over");
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
