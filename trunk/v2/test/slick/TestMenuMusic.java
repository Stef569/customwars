package test.slick;

import client.ui.CWInput;
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
  private CWInput cwInput;
  private TestMenu testmenu;

  public TestMenuMusic(CWInput cwInput) {
    this.cwInput = cwInput;
    cwInput.addCommandListener(this);
    testmenu = new TestMenu(3);
    testmenu.setLocation(80,100);
  }

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("v2/res/sound/menutick.wav");
    backgroundMusic = new Music("v2/res/sound/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);
    backgroundMusic.loop();

    image = new Image("v2/res/image/cliff.gif");
    cursor = new Image("v2/res/image/white.png");
    testmenu.setOptionName("Option 1");
    testmenu.setOptionName("Option 2");
    testmenu.setOptionName("Option 3");
    testmenu.setImage(cursor);
    testmenu.setSound(menuTickSound);
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("Now in Test Menu and Music state", 80, 80);
    testmenu.render(g);
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
  }

  public void controlPressed(Command command) {
    if (cwInput.isSelectPressed(command)) {
      menuTickSound.play();
    }
    testmenu.controlPressed(command, cwInput);
  }

  public void controlReleased(Command command) {
  }

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_S) {
      backgroundMusic.stop();
    }
  }
  
  public void mouseMoved(int oldx, int oldy, int newx, int newy){
    testmenu.mouseMoved(newx, newy);
  }

  public int getID() {
    return 0;
  }
}
