package com.customwars.client.ui.layout;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.awt.Insets;

public class AnimationBox extends Box {
  private Animation animation;
  private Insets insets;

  public AnimationBox() {
  }

  public AnimationBox(Animation animation, Insets insets) {
    this.animation = animation;
    this.insets = insets;
    width = getAnimBoxWith();
    height = getAnimBoxHeight();
  }

  protected void init() {
    center.x = center(width, getAnimWidth());
    center.y = center(height, getAnimHeight());
  }

  public void renderImpl(Graphics g) {
    if (animation != null)
      g.drawAnimation(animation, x + center.x, y + center.y);
  }

  private int getAnimBoxWith() {
    return insets.left + getAnimWidth() + insets.right;
  }

  public int getAnimBoxHeight() {
    return insets.top + getAnimHeight() + insets.bottom;
  }

  private int getAnimHeight() {
    if (animation != null) {
      return animation.getHeight();
    } else {
      return 0;
    }
  }

  private int getAnimWidth() {
    if (animation != null) {
      return animation.getWidth();
    } else {
      return 0;
    }
  }

  public void setAnim(Animation animation) {
    this.animation = animation;
    init();
  }
}
