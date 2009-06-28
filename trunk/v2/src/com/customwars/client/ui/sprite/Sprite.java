package com.customwars.client.ui.sprite;

import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A Sprite represents an animation of Images, located at x,y
 * When Animation is null or contains no images
 * A yellow circle is rendered and an error message logged once
 *
 * @author Stefan
 */
public class Sprite {
  private static final Logger logger = Logger.getLogger(Sprite.class);
  private static final int ONE_SECOND = 1000;
  private boolean illegalRenderingLogged;
  private boolean visible = true, updateAnim = true;
  PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  // Position (pixels)
  int locX;
  int locY;

  // Velocity (pixels per second)
  float dx;
  float dy;

  Animation anim;

  /**
   * Create a Sprite with no animation set
   * on position x,y
   * Animation should be set manually, before update or render is invoked.
   */
  public Sprite(int x, int y) {
    locX = x;
    locY = y;
  }

  /**
   * Sprite with Animation on location 0,0
   *
   * @param anim The animation to render
   */
  public Sprite(Animation anim) {
    this(0, 0);
    this.anim = anim;
  }

  /**
   * Sprite with single Image on location x,y
   * We just create an animation with 1 image
   */
  public Sprite(Image img, int x, int y) {
    this(x, y);
    if (img != null) {
      anim = new Animation(false);
      anim.addFrame(img, 1);
    }
  }

  /**
   * Updates this Sprite's Animation and its position based
   * on the velocity.
   *
   * @param elapsedTime the time that this frame took
   */
  public void update(long elapsedTime) {
    locX += dx * elapsedTime / ONE_SECOND;
    locY += dy * elapsedTime / ONE_SECOND;
    if (anim != null && updateAnim) anim.update(elapsedTime);
  }

  /**
   * Render the current Frame Image
   * if there is no animation set then a yellow circle is rendered
   * and an err message is logged once.
   *
   * @param g slick graphics to render the image on
   */
  public void render(Graphics g) {
    if (canRenderAnim(g) && visible) {
      anim.getCurrentFrame().draw(locX, locY);
    }
  }

  boolean canRenderAnim(Graphics g) {
    if (anim == null) {
      renderYellowOval(g);
      logIllegalRendering("tried to paint a Null Animation, indicated on the screen by a yellow filled circle.");
      return false;
    }

    if (anim.getFrameCount() > 0) {
      Image img = anim.getCurrentFrame();
      if (img == null) {
        renderYellowOval(g);
        logIllegalRendering("Empty Image paint attempt");
        return false;
      }
    } else {
      renderYellowOval(g);
      logIllegalRendering("anim contains no images");
      return false;
    }
    return true;
  }

  private void renderYellowOval(Graphics g) {
    Color originalColor = g.getColor();
    g.setColor(Color.yellow);
    g.fillOval(locX, locY, 20, 20);
    g.setColor(originalColor);
  }

  // Log Illegal rendering once
  private void logIllegalRendering(String errMessage) {
    if (illegalRenderingLogged) return;

    logger.warn(errMessage);
    illegalRenderingLogged = true;
  }

  //----------------------------------------------------------------------------
  // EVENTS
  // ---------------------------------------------------------------------------
  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  //----------------------------------------------------------------------------
  // SETTERS
  // ---------------------------------------------------------------------------
  public void setAnim(Animation anim) {
    Animation oldVal = this.anim;
    this.anim = anim;
    changeSupport.firePropertyChange("anim", oldVal, anim);
  }

  public void setLocation(int x, int y) {
    locX = x;
    locY = y;
  }

  public void setX(int locX) {
    this.locX = locX;
  }

  public void setY(int locY) {
    this.locY = locY;
  }

  public void setDx(float dx) {
    this.dx = dx;
  }

  public void setDy(float dy) {
    this.dy = dy;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setUpdateAnim(boolean updateAnim) {
    this.updateAnim = updateAnim;
  }

  //----------------------------------------------------------------------------
  // GETTERS
  // ---------------------------------------------------------------------------
  public int getX() {
    return locX;
  }

  public int getY() {
    return locY;
  }

  public Point getPixelLocation() {
    return new Point(locX, locY);
  }

  public int getHeight() {
    return anim.getCurrentFrame().getHeight();
  }

  public int getWidth() {
    return anim.getCurrentFrame().getWidth();
  }

  public Animation getAnim() {
    return anim;
  }

  // returns true if x and y is within the Sprite
  public boolean hasBeenClickedOn(int x, int y) {
    float xLimit = locX + anim.getCurrentFrame().getWidth();
    float yLimit = locY + anim.getCurrentFrame().getHeight();
    return ((x >= locX) && (x <= xLimit)) && ((y >= locY) && (y <= yLimit));
  }

  public boolean isRenderingAnim(Animation animation) {
    return anim == animation;
  }

  public boolean isRenderingSameAnim(Sprite otherSprite) {
    return otherSprite.isRenderingAnim(anim);
  }

  public boolean isVisible() {
    return visible;
  }
}