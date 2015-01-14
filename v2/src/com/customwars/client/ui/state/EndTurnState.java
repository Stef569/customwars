package com.customwars.client.ui.state;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.GUI;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;

/**
 * Show information about the next turn.
 * End the current turn when any key or mouse button has been pressed
 */
public class EndTurnState extends CWState {
  private Game game;
  private Player nextPlayer;
  private int nextDay;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    game = stateSession.game;
    nextPlayer = game.getNextActivePlayer();
    nextDay = game.getDay() + 1;
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      Image coBackgroundImg = resources.getEndTurnImg(nextPlayer.getCO());
      Point center = GUI.getCenteredRenderPoint(coBackgroundImg.getWidth(), coBackgroundImg.getHeight(), container);
      g.drawImage(coBackgroundImg, center.x, center.y);

      int maxTextWidth = g.getFont().getWidth("Day 99");
      int nextDayTextX = center.x + coBackgroundImg.getWidth() - maxTextWidth - 10;
      int nextDayTextY = center.y + 10;
      g.drawString("Day " + nextDay, nextDayTextX, nextDayTextY);
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
    changeToState("IN_GAME");
    game.endTurn();
  }

  public int getID() {
    return 15;
  }
}