package slick;

import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.Layout;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.awt.Insets;

public class BoxLayoutTest extends BasicGame {
  private Box gameImgBox, txtBox;
  private TextBox txtBox2, txtBox3;
  private boolean topToBottom;
  private boolean leftToRight;
  private boolean bottomToTop;
  private boolean rightToLeft;
  private boolean equalBoxWidths;
  private Box[] boxes;

  public BoxLayoutTest() {
    super("Box Layout Tests");
  }

  @Override
  public void init(GameContainer gameContainer) throws SlickException {
    txtBox = new TextBox("I'll get you!", gameContainer.getDefaultFont());
    txtBox.setLocation(250, 35);
    txtBox.setBorderColor(Color.gray);

    gameImgBox = new ImageBox(new Image("testData/game.png"), new Insets(15, 5, 15, 5));
    gameImgBox.setLocation(50, 50);
    gameImgBox.setBorderColor(Color.gray);

    ImageBox endTurnImgBox = new ImageBox(new Image("testData/EndTurn.png"), new Insets(0, 0, 0, 0));
    endTurnImgBox.setLocation(110, 200);
    endTurnImgBox.setBorderColor(Color.green);

    txtBox2 = new TextBox("who me?", gameContainer.getDefaultFont(), new Insets(20, 10, 20, 10));
    txtBox2.setLocation(100, 240);
    txtBox2.setBorderColor(Color.blue);

    txtBox3 = new TextBox("", gameContainer.getDefaultFont());
    txtBox3.setLocation(10, 290);
    txtBox3.setBorderColor(Color.blue);
    txtBox3.setText("this text is set after creating the textbox hoozah");

    boxes = new Box[]{gameImgBox, txtBox, txtBox2, txtBox3};
  }

  @Override
  public void update(GameContainer gameContainer, int i) throws SlickException {
    if (topToBottom) {
      Layout.locateTopToBottom(boxes, 10, 50);
      topToBottom = false;
    } else if (leftToRight) {
      Layout.locateLeftToRight(boxes, 10, 50);
      leftToRight = false;
    } else if (bottomToTop) {
      Layout.locateBottomToTop(boxes, 10, 200);
      bottomToTop = false;
    } else if (rightToLeft) {
      Layout.locateRightToLeft(boxes, gameContainer.getWidth(), 50);
      rightToLeft = false;
    }

    if (equalBoxWidths) {
      int widest = getWidestBox(boxes);
      for (Box box : boxes) {
        box.setWidth(widest);
      }
      equalBoxWidths = false;
    }
  }

  public int getWidestBox(Box[] boxes) {
    int widest = 0;
    for (Box box : boxes) {
      if (box.getWidth() > widest) {
        widest = box.getWidth();
      }
    }
    return widest;
  }

  public void render(GameContainer gameContainer, Graphics g) throws SlickException {
    g.drawString("Press L(Left),R(Right),T(Top) or B(Bottom) to layout the boxes", 10, 10);
    g.drawString("Z to equal the widths", 10, 28);
    gameImgBox.render(g);
    txtBox.render(g);
    txtBox2.render(g);
    txtBox3.render(g);
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_T) {
      topToBottom = true;
      leftToRight = false;
      bottomToTop = false;
      rightToLeft = false;
    } else if (key == Input.KEY_L) {
      leftToRight = true;
      topToBottom = false;
      bottomToTop = false;
      rightToLeft = false;
    } else if (key == Input.KEY_B) {
      bottomToTop = true;
      leftToRight = false;
      topToBottom = false;
      rightToLeft = false;
    } else if (key == Input.KEY_R) {
      rightToLeft = true;
      bottomToTop = false;
      leftToRight = false;
      topToBottom = false;
    } else if (key == Input.KEY_Z) {
      equalBoxWidths = true;
    }
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new BoxLayoutTest());
    appGameContainer.setDisplayMode(600, 400, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.setShowFPS(false);
    appGameContainer.start();
  }

}
