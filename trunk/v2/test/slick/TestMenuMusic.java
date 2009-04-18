package slick;

import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
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
  private PopupMenu testmenu;

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    menuTickSound = new Sound("testData/menutick.wav");
    backgroundMusic = new Music("testData/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);

    image = new Image("testData/cliff.gif");
    Image cursor = new Image("testData/white.png");

    testmenu = new PopupMenu(container);
    testmenu.addItem(new MenuItem("Option 1: Test Map Terrain", container));
    testmenu.addItem(new MenuItem("Option 2: Key Input Change (Under Construction)", container));

    testmenu.setLocation(80, 100);
    testmenu.setCursor(cursor);
    testmenu.setMenuTickSound(menuTickSound);
    testmenu.addListener(this);
    //testmenu.setVerticalSpacing(50);  // Uncomment to show 50px vertical margin
    //testmenu.addOptionImage(image);   // Uncomment to show 4th menu option as image
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    backgroundMusic.loop();
  }

  public void render(GameContainer gameContainer, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.drawString("ENTER: TO GO BACK TO MENU", 400, 80);
    g.drawString("Now in Test Menu and Music state", 80, 80);
    testmenu.render(gameContainer, g);
    switch (testmenu.getCurrentItem()) {
      case 0:
        g.drawString("Scroll a mini-map and see the terrain up close. \n" +
                "Use [1] and [2] to switch firing cursor.", 80, 200);
        break;
      case 1:
        g.drawString("The way buttons will be set on the keyboard. \n" +
                "Still under construction :(...", 80, 200);
        break;
    }
  }

  public void update(GameContainer gameContainer, int elapsedTime) throws SlickException {
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isSelect(command)) {
      menuTickSound.play();
    }

    testmenu.controlPressed(command, cwInput);
  }

  public void keyReleased(int key, char c) {
    if (key == Input.KEY_S) {
      super.toggleMusic(backgroundMusic);
    }
  }

  public void mouseClicked(int button, int x, int y, int clickCount) {
    if (button == 1) {
      testmenu.setLocation(x, y);
    }
  }

  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    backgroundMusic.stop();
  }

  public int getID() {
    return 0;
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeGameState("terrainmenu");
        break;
      case 1:
        changeGameState("keymenu");
        break;
    }
  }
}
