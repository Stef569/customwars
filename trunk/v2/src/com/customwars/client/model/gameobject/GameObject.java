package com.customwars.client.model.gameobject;

import com.customwars.client.model.CWPropertyChangeSupport;
import com.customwars.client.model.Observable;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * A Game object has a current state and supports sending events and keeping a list of event listeners.
 * Objects that don't have a state but do support events should not extend this class
 * but instead should implement the Observable Interface.
 */
public class GameObject implements Observable, Serializable {
  protected CWPropertyChangeSupport changeSupport = new CWPropertyChangeSupport(this);
  private static final long serialVersionUID = 1L;

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

  /**
   * Create a copy of this game object. Listeners are NOT copied.
   *
   * @param otherGameObject the game object to copy
   */
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

  /**
   * @see CWPropertyChangeSupport#enableEvents()
   */
  public void enableEvents() {
    changeSupport.enableEvents();
  }

  /**
   * @see CWPropertyChangeSupport#disableEvents()
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
