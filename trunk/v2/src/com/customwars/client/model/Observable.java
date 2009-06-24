package com.customwars.client.model;

import java.beans.PropertyChangeListener;

/**
 * Defines Objects that can send PropertyChangeEvents to multiple listeners
 */
public interface Observable {
  void addPropertyChangeListener(PropertyChangeListener listener);

  void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

  void removePropertyChangeListener(PropertyChangeListener listener);

  void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
