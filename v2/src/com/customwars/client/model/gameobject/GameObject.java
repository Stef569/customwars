package com.customwars.client.model.gameobject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The base class for each game object
 * Game objects are the subject in the observer pattern, they fire
 * PropertyChangeEvents when a bound field changes.
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

  /**
   * Report an int bound property update to any registered listeners.
   * No event is fired if old and new are equal.
   */
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Report a boolean bound property update to any registered listeners.
   * No event is fired if old and new are equal.
   */
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Report a bound property update to any registered listeners.
   * No event is fired if old and new are equal and non-null.
   */
  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Fire an existing PropertyChangeEvent to any registered listeners.
   * No event is fired if the given event's old and new values are
   * equal and non-null.
   *
   * @param evt The PropertyChangeEvent object.
   */
  public void firePropertyChange(PropertyChangeEvent evt) {
    changeSupport.firePropertyChange(evt);
  }

  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    changeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Adds a PropertyChangeListener to the listener list for a specific
   * property.
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName one of the property names listed above
   * @param listener     the property change listener to be added
   * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
   * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
   */
  public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(listener);
  }

  /**
   * Removes a <code>PropertyChangeListener</code> from the listener
   * list for a specific property. This method should be used to remove
   * <code>PropertyChangeListener</code>s
   * that were registered for a specific bound property.
   * <p>
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName a valid property name
   * @param listener     the PropertyChangeListener to be removed
   * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
   */
  public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }
}
