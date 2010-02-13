package slick;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.ui.slick.ImageRotator;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * Test the image rotator by showing all the unit decorations
 * Pressing a num key[0-9] will hide that frame from the animation
 */
public class ImageRotatorTest extends BasicGame {
  private ImageRotator imgRotator;

  public ImageRotatorTest() {
    super("Test the image rotator");
  }

  @Override
  public void init(GameContainer container) throws SlickException {
    ImageStrip imgStrip = new ImageStrip("testData/unitDecorations.png", 8, 8);
    imgRotator = new ImageRotator(imgStrip.toArray(), 1000);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    imgRotator.update(delta);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawString("Press [0-9] to toggle a frame in the animation\n[a] toggles all", 5, 5);
    imgRotator.draw(150, 150);
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_0) {
      imgRotator.setShowFrame(0, !imgRotator.isFrameVisible(0));
    }
    if (key == Input.KEY_1) {
      imgRotator.setShowFrame(1, !imgRotator.isFrameVisible(1));
    }
    if (key == Input.KEY_2) {
      imgRotator.setShowFrame(2, !imgRotator.isFrameVisible(2));
    }
    if (key == Input.KEY_3) {
      imgRotator.setShowFrame(3, !imgRotator.isFrameVisible(3));
    }
    if (key == Input.KEY_4) {
      imgRotator.setShowFrame(4, !imgRotator.isFrameVisible(4));
    }
    if (key == Input.KEY_5) {
      imgRotator.setShowFrame(5, !imgRotator.isFrameVisible(5));
    }
    if (key == Input.KEY_6) {
      imgRotator.setShowFrame(6, !imgRotator.isFrameVisible(6));
    }
    if (key == Input.KEY_7) {
      imgRotator.setShowFrame(7, !imgRotator.isFrameVisible(7));
    }
    if (key == Input.KEY_8) {
      imgRotator.setShowFrame(8, !imgRotator.isFrameVisible(8));
    }
    if (key == Input.KEY_9) {
      imgRotator.setShowFrame(9, !imgRotator.isFrameVisible(9));
    }
    if (key == Input.KEY_A) {
      if (imgRotator.isFrameVisible(0)) {
        imgRotator.hideAllFrames();
      } else {
        imgRotator.showAllFrames();
      }
    }
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new ImageRotatorTest());
    appGameContainer.setDisplayMode(600, 400, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
  }
}
