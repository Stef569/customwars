package com.customwars.client.model.gameobject;

import com.customwars.client.model.Observable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameObject implements Observable {
  protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  /**
   * The different states a gameObject can be in, at all times a gameObject is in one of these states
   */
  private GameObjectState state;

  public GameObject(GameObjectState state) {
    this.state = state;
  }

  public GameObject() {
    this(GameObjectState.IDLE);
  }

  public GameObject(GameObject otherGameObject) {
    this.state = otherGameObject.state;
  }

  public void setState(GameObjectState state) {
    GameObjectState oldVal = this.state;
    this.state = state;
    changeSupport.firePropertyChange("state", oldVal, state);
  }

  public boolean isIdle() {
    return state == GameObjectState.IDLE;
  }

  public boolean isActive() {
    return state == GameObjectState.ACTIVE;
  }

  public boolean isDestroyed() {
    return state == GameObjectState.DESTROYED;
  }

  public GameObjectState getState() {
    return state;
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(PropertyChangeEvent evt) {
    changeSupport.firePropertyChange(evt);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }
}
