package com.customwars.client.ui.state;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Show information about the next turn
 * end the current turn when the delay has passed
 */
public class EndTurnState extends CWState {
  private Game game;
  private Player nextPlayer;
  private int nextDay;
  private boolean endTurnPressed;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    game = stateSession.game;
    nextPlayer = game.getNextActivePlayer(game.getActivePlayer());
    nextDay = game.getDay() + 1;
    endTurnPressed = false;
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      g.drawString("Day " + nextDay + " " + nextPlayer.getName() + " Make your moves", 150, 150);
    }
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void keyPressed(int key, char c) {
    endTurn();
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    endTurn();
  }

  private void endTurn() {
    if (!endTurnPressed) {
      endTurnPressed = true;
      changeToState("IN_GAME");
      game.endTurn();
    }
  }

  public int getID() {
    return 15;
  }
}