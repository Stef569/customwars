package com.customwars.client.ui.state;

import com.customwars.client.model.Statistics;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.layout.TextBox;
import org.apache.log4j.Logger;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.Collection;

public class GameOverState extends CWState {
  private static final Logger logger = Logger.getLogger(GameOverState.class);
  private final Collection<TextBox> table = new ArrayList<TextBox>();

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    Statistics stats = stateSession.stats;
    Font defaultFont = container.getDefaultFont();

    // todo create a Table gui object where rows and columns can be added, instead of this mess.
    // todo create column for each player, create row for each statistic
    table.clear();
    int row = 0;
    for (Player p : stateSession.game.getAllPlayers()) {
      Statistics.PlayerStatistics playerStats = stats.getPlayerStats(p);

      TextBox header = new TextBox("Player " + p.getName(), defaultFont);
      TextBox unitsCreated = new TextBox("Units created: " + playerStats.unitsCreated, defaultFont);
      TextBox unitsKilled = new TextBox("Units killed: " + playerStats.unitsKilled, defaultFont);
      TextBox unitsLost = new TextBox("Units lost: " + playerStats.unitsLost, defaultFont);
      TextBox citiesCapped = new TextBox("cities capped: " + playerStats.citiesCaptured, defaultFont);

      header.setLocation(50 + row * 150, 80);
      table.add(header);
      unitsCreated.setLocation(50 + row * 150, 90);
      table.add(unitsCreated);
      unitsKilled.setLocation(50 + row * 150, 100);
      table.add(unitsKilled);
      unitsLost.setLocation(50 + row * 150, 110);
      table.add(unitsLost);
      citiesCapped.setLocation(50 + row * 150, 120);
      table.add(citiesCapped);
      row++;
    }

    logger.info("Game Over");
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawString("GAME OVER, Statistics: ", 100, 40);

    for (TextBox txtBox : table) {
      txtBox.render(g);
    }
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void keyPressed(int key, char c) {
    continueToMainMenu();
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    continueToMainMenu();
  }

  private void continueToMainMenu() {
    stateSession.clear();
    stateChanger.resumeRecordingStateHistory();
    changeToState("MAIN_MENU");
  }

  public int getID() {
    return 16;
  }
}
