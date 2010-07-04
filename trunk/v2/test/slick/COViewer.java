package slick;

import com.customwars.client.ui.COSheet;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * The co viewer tests the co sheet
 */
public class COViewer extends BasicGame {
  private COSheet coSheet;

  public COViewer() {
    super("view co");
  }

  @Override
  public void init(GameContainer container) throws SlickException {
    coSheet = new COSheet("res/testData/adder.gif");
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(coSheet.getLeftBodyImg(), 0, 0);
    g.drawImage(coSheet.getLeftHead(1), 100, 100);
    g.drawImage(coSheet.getLeftTorso(2), 250, 250);
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new COViewer());
    appGameContainer.setDisplayMode(800, 600, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.start();
  }
}
