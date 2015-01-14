package com.customwars.client.ui.state;

import com.customwars.client.App;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Give credit to those that have contributed to Custom wars
 */
public class CreditsState extends CWState {
  private static final int TYPE_DELAY = 100;
  private static final int PAUSE_DELAY = 2000;
  private String ASCII_FACE =
      ",  ---  " +
      ", ----- " +
      ",-------" +
      ", ^   ^ " +
      ",   *   " +
      ", \\___/ ";

  private int time = TYPE_DELAY;
  private int pauseTime = PAUSE_DELAY;

  private int renderRow = 0;
  private int renderCol = 0;
  private int scrollingY;

  private Font font;
  private int lineHeight;
  private int maxRowsOnScreen;
  private String[] lines;
  private boolean finished;

  @Override
  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    String header = App.get("game.name") + " CREDITS";
    String credits = header + "," + App.get("game.credits") + ASCII_FACE;
    lines = credits.split(",");
    font = resources.getFont("credits");
    lineHeight = font.getLineHeight();
    maxRowsOnScreen = gameContainer.getHeight() / lineHeight;
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (finished) {
      pauseTime -= delta;
    } else {
      time -= delta;
    }

    // Restart after the pause
    if (pauseTime < 0) {
      restart();
      pauseTime = 0;
    }

    if (time <= 0) {
      updateLine();
    }
  }

  private void updateLine() {
    time = TYPE_DELAY;

    // If we are moving down to the next line
    if (renderCol > lines[renderRow].length() - 1) {
      // We've rendered all characters
      if (renderRow >= lines.length - 1) {
        pauseTime = PAUSE_DELAY;
        finished = true;
      } else {
        moveToNextLine();
      }
    } else {
      // Move to next character
      renderCol++;
    }
  }

  private void moveToNextLine() {
    renderRow++;
    renderCol = 0;

    // When the row is near the bottom edge, Scroll up
    if (renderRow > maxRowsOnScreen - 2) {
      scrollingY += lineHeight;
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    int y = 0;
    g.setColor(Color.white);

    // Only render the rows we have typed out so far (renderRow = current row)
    for (int i = 0; i < renderRow + 1; i++) {
      String line = lines[i];
      // Render whole line if it's a previous one, otherwise render the col
      int len = i < renderRow ? line.length() : renderCol;
      String text = line.substring(0, len);
      if (text.length() != 0) {
        int width = font.getWidth(text);
        int centerX = container.getWidth() / 2;
        int textX = centerX - width / 2;
        font.drawString(textX, y - scrollingY, text);
      }

      y += lineHeight;
    }
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_ESCAPE) {
      super.changeToPreviousState();
    } else if (key == Input.KEY_SPACE || key == Input.KEY_ENTER) {
      showAll();
    }
  }

  public void showAll() {
    if (lines.length == 0) {
      renderRow = renderCol = 0;
    } else {
      renderRow = maxRowsOnScreen-2;
      renderCol = 0;
      scrollingY = 0;
    }
  }

  public void restart() {
    renderCol = 0;
    renderRow = 0;
    scrollingY = 0;
    time = TYPE_DELAY;
    finished = false;
  }

  @Override
  public int getID() {
    return 2;
  }
}