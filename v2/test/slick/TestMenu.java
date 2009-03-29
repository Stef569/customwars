package slick;

import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.Color;
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
 * @author Crecen
 */
public class TestMenu extends CWState implements ComponentListener {
  private Music backgroundMusic;
  private Image image;
  private PopupMenu testmenu;

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    backgroundMusic = new Music("testData/shortBackground.ogg");
    backgroundMusic.setVolume(0.5F);

    image = new Image("testData/MainMenu.png");
    //Image cursor = new Image("res/image/white.png");
    Image mapOption = new Image("testData/map.png");
    Image gameOption = new Image("testData/game.png");
    Image keyInput = new Image("testData/KeyInput.png");
    Image endTurn = new Image("testData/EndTurn.png");

    testmenu = new PopupMenu(container, "Test Menu");

    //testmenu.addOption("Option 1: Test Map Terrain");
    testmenu.addOptionImage(mapOption);
    //testmenu.addOption("Option 2: Key Input Change (Under Construction)");
    testmenu.addOptionImage(keyInput);
    //testmenu.addOption("Option 3: Game Test");
    testmenu.addOptionImage(gameOption);

    testmenu.init();
    testmenu.setVerticalMargin(50);
    testmenu.setVisible(true);

    testmenu.setLocation(200, 150);
    //testmenu.setCursorImage(cursor);
    testmenu.setOptionChangeSound(new Sound("testData/menutick.wav"));
    testmenu.addListener(this);
    //testmenu.setVerticalMargin(50);   // Uncomment to show 50px vertical margin
    //testmenu.addOptionImage(image);   // Uncomment to show 4th menu option as image
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    backgroundMusic.loop();
  }

  public void render(GameContainer gameContainer, Graphics g) throws SlickException {
    g.drawImage(image, 0, 0);
    g.setColor(Color.white);
    g.drawString("ENTER: TO GO BACK TO MENU", 400, 10);
    testmenu.render(gameContainer, g);

    g.setColor(Color.darkGray);
    switch (testmenu.getCurrentOption()) {
      case 0:
        g.drawString("Scroll a mini-map and see the terrain up close", 210, 440);
        break;
      case 1:
        g.drawString("The way buttons will be set on the keyboard.", 210, 440);
        break;
      case 2:
        g.drawString("Play a test game between the main coders", 210, 440);
        break;
    }
    g.setColor(Color.white);
  }

  public void update(GameContainer gameContainer, int elapsedTime) throws SlickException {
  }

  public void controlPressed(Command command, CWInput cwInput) {
    testmenu.controlPressed(command, cwInput);
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
    switch (popupMenu.getCurrentOption()) {
      case 0:
        changeGameState("terrainmenu");
        break;
      case 1:
        changeGameState("keymenu");
        break;
      case 2:
        changeGameState("IN_GAME");
        break;
    }
  }
}
