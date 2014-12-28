package slick;

import com.customwars.client.model.game.Game;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.thingle.MenuListener;
import com.customwars.client.ui.thingle.ThingleMenu;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Crecen
 */
public class TestMenu extends CWState {
  private int windowHeight;
  private ThingleMenu mainMenu;

  public void init(final GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    windowHeight = container.getHeight();
    Font font = resources.getFont("menu");
    
    mainMenu = new ThingleMenu("testData/", "testMenu.xml", new MenuListener() {
      public void selected(int selectedIndex) {
        switch (selectedIndex) {
          case 0:
            changeToState("terrainmenu");
            break;
          case 1:
            changeToState("keymenu");
            break;
          case 2:
            loadHardCodedMap();
            changeToState("IN_GAME");
            break;
          case 3:
            changeToState("MAP_EDITOR");
            break;
          case 4:
            container.exit();
            break;
        }
      }
    });
    mainMenu.setFont(font);
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    mainMenu.enable();
  }

  public void render(GameContainer gameContainer, Graphics g) throws SlickException {
    g.setColor(Color.white);
    mainMenu.render();

    g.setColor(Color.lightGray);
    switch (mainMenu.getSelectedIndex()) {
      case 0:
        g.drawString("Scroll a mini-map and see the terrain up close", 210, windowHeight - 40);
        break;
      case 1:
        g.drawString("The way buttons will be set on the keyboard.", 210, windowHeight - 40);
        break;
      case 2:
        g.drawString("Play a test game between the main coders", 210, windowHeight - 40);
        break;
      case 3:
        g.drawString("Create a map", 210, windowHeight - 40);
        break;
      case 4:
        g.drawString("Quit the game", 210, windowHeight - 40);
        break;
    }
  }

  public void update(GameContainer gameContainer, int elapsedTime) throws SlickException {
  }

  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    mainMenu.disable();
  }

  private void loadHardCodedMap() {
    Game game = HardCodedGame.getGame();
    stateSession.game = game;
    stateSession.map = game.getMap();
  }

  public int getID() {
    return 100;
  }
}
