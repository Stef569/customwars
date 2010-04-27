package com.customwars.client.model.co;

import org.newdawn.slick.Color;

public enum CoStyle {
  ORANGE_STAR(Color.red),
  BLUE_MOON(Color.blue),
  GREEN_EARTH(new Color(0, 0.6f, 0, 1.0f)),
  YELLOW_COMMET(new Color(0xADAD00)),
  BLACK_HOLE(Color.gray),
  NONE(Color.black);
  private final Color color;

  CoStyle(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
