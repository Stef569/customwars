package slick;

import com.customwars.client.ui.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 * when the menu is shown a sound is played
 */
public class TestMenuMusic extends BasicGameState implements InputProviderListener {
  private Music backgroundMusic;
  private Sound menuTickSound;
  private Image image;
  private Image cursor;
  private int option;
  private CWInput input;

  public TestMenuMusic(CWInput cwInput) {
    this.input = cwInput;
    cwInput.addListener(this);
  }

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("res/sound/menutick.wav");
    backgroundMusic = new Music("res/sound/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);
    backgroundMusic.loop();

    image = new Image("res/image/cliff.gif");
    cursor = new Image("res/image/white.png");
    option = 0;
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("Now in Test Menu and Music state", 80, 80);
    g.drawString("Option 1", 100, 100);
    g.drawString("Option 2", 100, 120);
    g.drawString("Option 3", 100, 140);

    //One line... all action?
    g.drawImage(cursor, 80, (100 + (option * 20)));
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
  }

  public void controlPressed(Command command) {
    if (input.isSelectPressed(command)) {
      menuTickSound.play();
    } else if (input.isNextMenuItemPressed(command) && option < 2) {
      option++;
    } else if (input.isPreviousMenuItemPressed(command) && option > 0) {
      option--;
    }
  }

  public void controlReleased(Command command) {
  }

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_S) {
      backgroundMusic.stop();
    }
  }

  public int getID() {
    return 0;
  }
}
