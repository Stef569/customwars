package test.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.Input;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 * when the menu is shown a sound is played
 * on mouse click another sound is played
 */
public class TestMenuMusic extends BasicGameState {
  private Sound menuTick;
  private Image image;
  private Image cursor;
  private int option;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    menuTick = new Sound("v2/res/sound/menutick.wav");
    image = new Image("v2/res/image/cliff.gif");
    cursor = new Image("v2/res/image/white.png");
    option = 0;
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("Now in Test Menu and Music state", 80, 80);
    g.drawString("Option 1", 100, 100);
    g.drawString("Option 2", 100, 120);
    g.drawString("Option 3", 100, 140);
    
    //One line... all action?
    g.drawImage(cursor, 80, (100+(option*20)));
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
  }

  public void keyPressed(int key, char c) {
    if(key == Input.KEY_A) {
      menuTick.play();
    }
    
    if(key == Input.KEY_UP && option > 0){
        option--;
    }
    
    if(key == Input.KEY_DOWN && option < 2){
        option++;
    }
  }
}
