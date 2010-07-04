package com.customwars.client.model.co;

import java.io.Serializable;

public class Power implements Serializable {
  public static final Power NONE = new Power("", "");
  private final String name, description;
  private boolean active;

  public Power(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Power(Power power) {
    this.name = power.name;
    this.description = power.description;
    this.active = power.active;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  protected boolean isActive() {
    return active;
  }

  protected void activate() {
    this.active = true;
  }

  protected void deActivate() {
    this.active = false;
  }
}
