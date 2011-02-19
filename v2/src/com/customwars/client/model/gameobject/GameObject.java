package com.customwars.client.model.gameobject;

import com.customwars.client.model.CWPropertyChangeSupport;
import com.customwars.client.model.Observable;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * A Gameobject has a current state and supports sending events and keeping a list of event listeners.
 * Objects that don't have a state but do support events should not extend this class
 * but instead should implement the Observable Interface.
 */
public class GameObject implements Observable, Serializable {
  protected CWPropertyChangeSupport changeSupport = new CWPropertyChangeSupport(this);

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
    copyListeners(otherGameObject);
  }

  private void copyListeners(GameObject otherGameObject) {
    if (otherGameObject.changeSupport != null) {
      for (PropertyChangeListener listener : otherGameObject.changeSupport.getPropertyChangeListeners()) {
        changeSupport.addPropertyChangeListener(listener);
      }
    }
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

  /**
   * @see com.customwars.client.model.CWPropertyChangeSupport#enableEvents()
   */
  public void enableEvents() {
    changeSupport.enableEvents();
  }

  /**
   * @see com.customwars.client.model.CWPropertyChangeSupport#disableEvents()
   */
  public void disableEvents() {
    changeSupport.disableEvents();
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }
}
