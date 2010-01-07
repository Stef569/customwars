package com.customwars.client.network;

/**
 * A network user
 */
public class User {
  private final String name;
  private final String pass;

  public User(String name, String pass) throws NetworkException {
    this.name = name.replaceAll("\n", " ");
    this.pass = pass.replaceAll("\n", " ");

    if (name.length() >= 12) {
      throw new NetworkException("name cannot exceed 12 characters limit");
    }
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return pass;
  }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", pass='" + pass + '\'' +
      '}';
  }
}
