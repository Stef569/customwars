package com.customwars.client.ui.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

/**
 * A mouse selected area that can be used for menus or buttons
 *
 * @author kevin
 */
public class MouseOverArea extends BasicComponent {
  /**
   * The default state
   */
  private static final int NORMAL = 1;

  /**
   * The mouse down state
   */
  private static final int MOUSE_DOWN = 2;

  /**
   * The mouse selected state
   */
  private static final int MOUSE_OVER = 3;

  /**
   * The normalImage being displayed in normal state
   */
  private Image normalImage;

  /**
   * The normalImage being displayed in mouseOver state
   */
  private Image mouseOverImage;

  /**
   * The normalImage being displayed in mouseDown state
   */
  private Image mouseDownImage;

  /**
   * The colour used in normal state
   */
  private Color normalColor = new Color(1, 1, 1, 0.0f);

  /**
   * The colour used in mouseOver state
   */
  private Color mouseOverColor = Color.white;

  /**
   * The colour used in mouseDown state
   */
  private Color mouseDownColor = Color.white;

  /**
   * The sound for mouse selected
   */
  private Sound mouseOverSound;

  /**
   * The sound for mouse down
   */
  private Sound mouseDownSound;

  /**
   * The shape defining the area
   */
  private Rectangle area;

  /**
   * The current normalImage being displayed
   */
  private Image currentImage;

  /**
   * The current color being used
   */
  private Color currentColor;

  /**
   * True if the mouse is selected the area
   */
  private boolean selected;

  /**
   * True if the mouse button is pressed
   */
  private boolean mouseDown;

  /**
   * The state of the area
   */
  private int state = NORMAL;

  /**
   * True if the mouse has been up since last press
   */
  private boolean mouseUp;

  public MouseOverArea(GUIContext container) {
    this(container, null, 0, 0, 0, 0);
  }

  public MouseOverArea(GUIContext container, Image img) {
    this(container, img, 0, 0);
  }

  public MouseOverArea(GUIContext container, Image img, ComponentListener listener) {
    this(container, img, 0, 0);
    addListener(listener);
    consumeEvent();
  }

  /**
   * Create a new mouse selected area
   *
   * @param container The container displaying the mouse selected area
   * @param image     The normalImage to display
   * @param x         The position of the area
   * @param y         the position of the area
   * @param listener  A listener to add to the area
   */
  public MouseOverArea(GUIContext container, Image image, int x, int y, ComponentListener listener) {
    this(container, image, x, y, image.getWidth(), image.getHeight());
    addListener(listener);
    consumeEvent();
  }

  /**
   * Create a new mouse selected area
   *
   * @param container The container displaying the mouse selected area
   * @param image     The normalImage to display
   * @param x         The position of the area
   * @param y         the position of the area
   */
  public MouseOverArea(GUIContext container, Image image, int x, int y) {
    this(container, image, x, y, image.getWidth(), image.getHeight());
  }

  /**
   * Create a new mouse selected area
   *
   * @param container The container displaying the mouse selected area
   * @param image     The normalImage to display
   * @param x         The position of the area
   * @param y         the position of the area
   * @param width     The width of the area
   * @param height    The height of the area
   * @param listener  A listener to add to the area
   */
  public MouseOverArea(GUIContext container, Image image, int x, int y,
                       int width, int height, ComponentListener listener) {
    this(container, image, x, y, width, height);
    addListener(listener);
    consumeEvent();
  }

  /**
   * Create a new mouse selected area
   *
   * @param container The container displaying the mouse selected area
   * @param image     The normalImage to display
   * @param x         The position of the area
   * @param y         the position of the area
   * @param width     The width of the area
   * @param height    The height of the area
   */
  public MouseOverArea(GUIContext container, Image image, int x, int y,
                       int width, int height) {
    this(container, image, new Rectangle(x, y, width, height));
  }

  /**
   * Create a new mouse selected area
   *
   * @param container The container displaying the mouse selected area
   * @param image     The normalImage to display
   * @param shape     The shape defining the area of the mouse sensitive zone
   */
  public MouseOverArea(GUIContext container, Image image, Rectangle shape) {
    super(container);

    area = shape;
    normalImage = image;
    currentImage = image;
    mouseOverImage = image;
    mouseDownImage = image;

    currentColor = normalColor;

    state = NORMAL;
    Input input = container.getInput();
    selected = area.contains(input.getMouseX(), input.getMouseY());
    mouseDown = input.isMouseButtonDown(0);
    updateImage();
  }

  /**
   * Moves the component.
   *
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation(int x, int y) {
    if (area != null) {
      area.setX(x);
      area.setY(y);
    }
  }

  public void setArea(float x, float y, float width, float height) {
    area = new Rectangle(x, y, width, height);
  }

  /**
   * Returns the position in the X coordinate
   *
   * @return x
   */
  public int getX() {
    return (int) area.getX();
  }

  /**
   * Returns the position in the Y coordinate
   *
   * @return y
   */
  public int getY() {
    return (int) area.getY();
  }

  /**
   * Set the normal color used on the image in the default state
   *
   * @param color The color to be used
   */
  public void setNormalColor(Color color) {
    normalColor = color;
  }

  /**
   * Set the color to be used when the mouse is selected the area
   *
   * @param color The color to be used when the mouse is selected the area
   */
  public void setMouseOverColor(Color color) {
    mouseOverColor = color;
  }

  /**
   * Set the color to be used when the mouse is down the area
   *
   * @param color The color to be used when the mouse is down the area
   */
  public void setMouseDownColor(Color color) {
    mouseDownColor = color;
  }

  /**
   * Set the normal image used on the image in the default state
   *
   * @param image The image to be used
   */
  public void setNormalImage(Image image) {
    normalImage = image;
  }

  /**
   * Set the image to be used when the mouse is selected the area
   *
   * @param image The image to be used when the mouse is selected the area
   */
  public void setMouseOverImage(Image image) {
    mouseOverImage = image;
  }

  /**
   * Set the image to be used when the mouse is down the area
   *
   * @param image The image to be used when the mouse is down the area
   */
  public void setMouseDownImage(Image image) {
    mouseDownImage = image;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public void setWidth(int width) {
    area.setWidth(width);
  }

  public void setHeight(int height) {
    area.setHeight(height);
  }

  /**
   * @see org.newdawn.slick.gui.AbstractComponent#render(org.newdawn.slick.gui.GUIContext,
   *      org.newdawn.slick.Graphics)
   */
  public void renderimpl(GUIContext container, Graphics g) {
    if (currentImage != null) {

      int xp = (int) (area.getX() + ((getWidth() - currentImage.getWidth()) / 2));
      int yp = (int) (area.getY() + ((getHeight() - currentImage.getHeight()) / 2));

      currentImage.draw(xp, yp, currentColor);
    } else {
      g.setColor(currentColor);
      g.fill(area);
    }
    updateImage();
  }

  /**
   * Update the current normalImage based on the mouse state
   */
  private void updateImage() {
    if (!selected) {
      currentImage = normalImage;
      currentColor = normalColor;
      state = NORMAL;
      mouseUp = false;
    } else {
      if (mouseDown) {
        if ((state != MOUSE_DOWN) && (mouseUp)) {
          if (mouseDownSound != null) {
            mouseDownSound.play();
          }
          currentImage = mouseDownImage;
          currentColor = mouseDownColor;
          state = MOUSE_DOWN;
          mouseUp = false;
        }
      } else {
        mouseUp = true;
        if (state != MOUSE_OVER) {
          if (mouseOverSound != null) {
            mouseOverSound.play();
          }
          currentImage = mouseOverImage;
          currentColor = mouseOverColor;
          state = MOUSE_OVER;
        }
      }
    }

    mouseDown = false;
    state = NORMAL;
  }

  /**
   * Set the mouse selected sound effect
   *
   * @param sound The mouse selected sound effect
   */
  public void setMouseOverSound(Sound sound) {
    mouseOverSound = sound;
  }

  /**
   * Set the mouse down sound effect
   *
   * @param sound The mouse down sound effect
   */
  public void setMouseDownSound(Sound sound) {
    mouseDownSound = sound;
  }

  /**
   * @see org.newdawn.slick.util.InputAdapter#mouseMoved(int,int,int,int)
   */
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    selected = isWithinArea(newx, newy);
  }

  /**
   * @see org.newdawn.slick.util.InputAdapter#mousePressed(int,int,int)
   */
  public void mousePressed(int button, int mx, int my) {
    selected = isWithinArea(mx, my);
    if (button == 0) {
      mouseDown = true;
    }
  }

  /**
   * @see org.newdawn.slick.util.InputAdapter#mouseReleased(int,int,int)
   */
  public void mouseReleased(int button, int mx, int my) {
    selected = isWithinArea(mx, my);
    if (button == 0) {
      mouseDown = false;
    }
  }

  private boolean isWithinArea(int x, int y) {
    return area.contains(x, y);
  }

  /**
   * @see org.newdawn.slick.gui.AbstractComponent#getHeight()
   */
  public int getHeight() {
    return (int) area.getHeight();
  }

  /**
   * @see org.newdawn.slick.gui.AbstractComponent#getWidth()
   */
  public int getWidth() {
    return (int) area.getWidth();
  }

  public boolean isSelected() {
    return selected;
  }
}
