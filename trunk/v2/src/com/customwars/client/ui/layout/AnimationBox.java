package com.customwars.client.ui.layout;

import com.customwars.client.ui.GUI;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.awt.Insets;
import java.awt.Point;

/**
 * A Box around an Animation, The animation is centered within the box.
 * The box can be larger then the animation by providing insets
 */
public class AnimationBox extends Box {
  private Animation animation;
  private Insets insets;

  /**
   * Create an empty animation box
   */
  public AnimationBox() {
    this(null, new Insets(0, 0, 0, 0));
  }

  public AnimationBox(Animation animation, Insets insets) {
    this.insets = insets;
    setAnim(animation);
  }

  /**
   * Change the animation in this box
   * the width and height of the box are adjusted to the new animation size
   * the center is reset
   */
  public void setAnim(Animation animation) {
    this.animation = animation;
    setWidth(getBoxWidth());
    setHeight(getBoxHeight());
    init();
  }

  @Override
  public void init() {
    Point center = GUI.getCenteredRenderPoint(getAnimWidth(), getAnimHeight(), getWidth(), getHeight());
    setCenter(center.x, center.y);
  }

  public void renderImpl(Graphics g) {
    if (animation != null) {
      renderAnim(g);
    }
  }

  private void renderAnim(Graphics g) {
    int y = getY() + getCenterY();
    switch (getAlignement()) {
      case LEFT:
        g.drawAnimation(animation, getX(), y);
        break;
      case CENTER:
        g.drawAnimation(animation, getX() + getCenterX(), y);
        break;
      case RIGHT:
        g.drawAnimation(animation, getBoxWidth() - getAnimWidth(), y);
        break;
    }
  }

  private int getBoxWidth() {
    return insets.left + getAnimWidth() + insets.right;
  }

  private int getBoxHeight() {
    return insets.top + getAnimHeight() + insets.bottom;
  }

  private int getAnimHeight() {
    return animation != null ? animation.getHeight() : 0;
  }

  private int getAnimWidth() {
    return animation != null ? animation.getWidth() : 0;
  }
}
