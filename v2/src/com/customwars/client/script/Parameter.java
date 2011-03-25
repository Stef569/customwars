package com.customwars.client.script;

/**
 * A single parameter with a name and a value of type T.
 */
public class Parameter<T> {
  private final String name;
  private final T value;

  public Parameter(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    return name + "->" + value;
  }
}
