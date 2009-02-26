package test.slick;

import com.customwars.client.ui.PopUpMenu;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateLogic;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 * when the mouse hovers over the menu items a sound is played
 */
public class TestMenuMusic extends CWState implements ComponentListener {
  private Music backgroundMusic;
  private Sound menuTickSound;
  private Image image;
  private PopUpMenu testmenu;

  public TestMenuMusic(CWInput cwInput, StateLogic stateLogic) {
    super(cwInput, stateLogic);
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("res/sound/menutick.wav");
    backgroundMusic = new Music("res/sound/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);
    backgroundMusic.loop();

    image = new Image("res/image/cliff.gif");
    Image cursor = new Image("res/image/white.png");

    testmenu = new PopUpMenu(container);
    testmenu.setLocation(80, 100);
    testmenu.addOption("Option 1: Test Map Terrain");
    testmenu.addOption("Option 2: Key Input Change (Under Construction)");
    testmenu.addOption("Option 3: Spritesheets");
    testmenu.setCursorImage(cursor);
    testmenu.setOptionChangeSound(menuTickSound);
    testmenu.addListener(this);
    //testmenu.setVerticalMargin(50);   // Uncomment to show 50px vertical margin
    //testmenu.addOptionImage(image);   // Uncomment to show 4th menu option as image
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("ENTER: TO GO BACK TO MENU", 400, 80);
    g.drawString("Now in Test Menu and Music state", 80, 80);
    testmenu.render(gameContainer, g);
    switch (testmenu.getOption()) {
      case 0:
        g.drawString("Scroll a mini-map and see the terrain up close. \n" +
                "Use [1] and [2] to switch firing cursor.", 80, 200);
        break;
      case 1:
        g.drawString("The way buttons will be set on the keyboard. \n" +
                "Still under construction :(...", 80, 200);
        break;
      case 2:
        g.drawString("Loads up the spritesheets used for the game. \n" +
                "Use your mouse scroll wheel to swap colors.", 80, 200);
        break;
    }
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int elapsedTime) throws SlickException {
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isSelectPressed(command)) {
      menuTickSound.play();
    }

    if (cwInput.isToggleMusicPressed(command)) {
      if (backgroundMusic.playing()) {
        backgroundMusic.pause();
      } else {
        backgroundMusic.resume();
      }
    }
    testmenu.controlPressed(command, cwInput);
  }

  public int getID() {
    return 0;
  }

  public void componentActivated(AbstractComponent source) {
    PopUpMenu popUpMenu = (PopUpMenu) source;
    switch (popUpMenu.getOption()) {
      case 0:
        changeGameState("terrainmenu");
        break;
      case 1:
        changeGameState("keymenu");
        break;
      case 2:
        changeGameState("spritemenu");
        break;
    }
  }
}
