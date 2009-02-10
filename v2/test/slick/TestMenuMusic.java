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
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 * when the menu is shown a sound is played
 */
public class TestMenuMusic extends CWState {
  private Music backgroundMusic;
  private Sound menuTickSound;
  private Image image;
  private CWInput input;
  private TestMenu menu;

  public TestMenuMusic(CWInput cwInput) {
    this.input = cwInput;
  }

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("res/sound/menutick.wav");
    backgroundMusic = new Music("res/sound/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);
    backgroundMusic.loop();

    image = new Image("res/image/cliff.gif");
    buildTestMenu();
  }

  private void buildTestMenu() {
    menu = new TestMenu(3, input);
    menu.optionName(0, "Attack!");
    menu.optionName(1, "Select");
    menu.optionName(2, "ok this is a bit longer");
    menu.changeLocation(150, 150);
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("Now in Test Menu and Music state", 80, 80);

    menu.render(gameContainer, stateBasedGame, g);
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
    menu.update(gameContainer, stateBasedGame, delta);
  }

  public void controlPressed(Command command) {
    menu.controlPressed(command);
  }

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_S) {
      backgroundMusic.stop();
    }
  }

  public void mousePressed(int button, int x, int y) {
    if (button == Input.MOUSE_RIGHT_BUTTON)
      menu.changeLocation(x, y);
  }

  public int getID() {
    return 0;
  }
}
