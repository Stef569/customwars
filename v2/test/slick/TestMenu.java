package slick;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.ui.MenuItem;
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

import java.util.Arrays;
import java.util.List;

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
    Image whiteSquare = new Image("testData/white.png");
    ImageStrip cursor = new ImageStrip("testData/point.png", 15, 11);

    Image mapOption = new Image("testData/map.png");
    Image keyInputOption = new Image("testData/KeyInput.png");
    Image gameOption = new Image("testData/game.png");
    Image endTurn = new Image("testData/EndTurn.png");

    testmenu = new PopupMenu(container);
    List<MenuItem> items = Arrays.asList(
            new MenuItem(mapOption, "Option 1: Test Map Terrain", container),
            new MenuItem(keyInputOption, container),
            new MenuItem(gameOption, "", container),
            new MenuItem("Map Editor", container)
    );

    for (MenuItem item : items) {
      testmenu.addItem(item);
    }

    testmenu.setLocation(250, 150);
    testmenu.setMenuTickSound(new Sound("testData/menutick.wav"));
    testmenu.addListener(this);
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
    switch (testmenu.getCurrentItem()) {
      case 0:
        g.drawString("Scroll a mini-map and see the terrain up close", 210, 440);
        break;
      case 1:
        g.drawString("The way buttons will be set on the keyboard.", 210, 440);
        break;
      case 2:
        g.drawString("Play a test game between the main coders", 210, 440);
        break;
      case 3:
        g.drawString("Create a map", 210, 440);
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
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeGameState("terrainmenu");
        break;
      case 1:
        changeGameState("keymenu");
        break;
      case 2:
        changeGameState("IN_GAME");
        break;
      case 3:
        changeGameState("MAP_EDITOR");
        break;
    }
  }
}
