package test.slick;

import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.PopUpMenu;
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
 * when the mouse hovers over the menu items a sound is played
 */
public class TestMenuMusic extends com.customwars.client.ui.CWState {
  private Music backgroundMusic;
  private Sound menuTickSound;
  private Image image;
  private CWInput cwInput;
  private PopUpMenu testmenu;

  public TestMenuMusic(CWInput cwInput) {
    this.cwInput = cwInput;
    testmenu = new PopUpMenu();
    testmenu.setLocation(80, 100);
  }

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("res/sound/menutick.wav");
    backgroundMusic = new Music("res/sound/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);
    backgroundMusic.loop();

    image = new Image("res/image/cliff.gif");
    Image cursor = new Image("res/image/white.png");
    testmenu.addOption("Option 1: Test Map Terrain");
    testmenu.addOption("Option 2: Key Input Change (Under Construction)");
    testmenu.addOption("Option 3: Spritesheets");
    testmenu.setCursorImage(cursor);
    testmenu.setSound(menuTickSound);
    //testmenu.setVerticalMargin(50);   // uncomment to show 50px vertical margin
    //testmenu.addOptionImage(image);   // Uncomment to test 4th menu option as image
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("ENTER: TO GO BACK TO MENU", 400, 80);
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

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_S) {
      backgroundMusic.stop();
    }
  }

  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    testmenu.mouseMoved(newx, newy);
  }
  
  public int setState(){
      return testmenu.getOption()+1;
  }

  public int getID() {
    return 0;
  }
}
