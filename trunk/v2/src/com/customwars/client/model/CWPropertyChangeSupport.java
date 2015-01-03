package com.customwars.client.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

/**
 * This subclass of {@link PropertyChangeSupport} is almost
 * identical in functionality. The only difference is that
 * the sending of events can be enabled/disabled.
 */
public class CWPropertyChangeSupport extends PropertyChangeSupport {
  private static final long serialVersionUID = 1L;
  private boolean sendEvents;

  public CWPropertyChangeSupport(Object sourceBean) {
    super(sourceBean);
    this.sendEvents = true;
  }

  /**
   * Enable or disable the event notifications to listeners.
   */
  public void setEnableEvents(boolean enable) {
    this.sendEvents = enable;
  }

  /**
   * Enable events to be send to the listeners.
   */
  public void enableEvents() {
    this.sendEvents = true;
  }

  /**
   * Disable the sending of events to the listeners..
   */
  public void disableEvents() {
    this.sendEvents = false;
  }

  @Override
  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if (sendEvents) super.firePropertyChange(propertyName, oldValue, newValue);
  }

  @Override
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    if (sendEvents) super.firePropertyChange(propertyName, oldValue, newValue);
  }

  @Override
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    if (sendEvents) super.firePropertyChange(propertyName, oldValue, newValue);
  }

  @Override
  public void firePropertyChange(PropertyChangeEvent evt) {
    if (sendEvents) super.firePropertyChange(evt);
  }

  @Override
  public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
    if (sendEvents) super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }

  @Override
  public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
    if (sendEvents) super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }

  @Override
  public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
    if (sendEvents) super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }
}
