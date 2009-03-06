package com.customwars.client.model.gameobject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The base class for each game object
 * Game objects are the subject in the observer pattern, they fire
 * PropertyChangeEvents when a bound field changes.
 * When the field value did not change no event is fired.
 *
 * @author stefan
 */
public class GameObject {
  private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
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
    firePropertyChange("state", oldVal, state);
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

  public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void firePropertyChange(PropertyChangeEvent evt) {
    changeSupport.firePropertyChange(evt);
  }

  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }
}
