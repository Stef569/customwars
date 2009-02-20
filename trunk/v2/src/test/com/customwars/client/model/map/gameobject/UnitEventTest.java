package test.com.customwars.client.model.map.gameobject;

import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.testData.HardCodedGame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author stefan
 */
public class UnitEventTest implements PropertyChangeListener {
  private Unit unit;
  private GameObjectState oldState;

  @Before
  public void beforeEachTest() {
    unit = HardCodedGame.getInf();
    unit.addPropertyChangeListener(this);
  }

  @After
  public void afterEachTest() {
    unit.removePropertyChangeListener(this);
    unit = null;
  }

  @Test
  /**
   * When a value changes, an event is sent in this case a unit state change
   * The test passes if the propertyChange method can read the change
   */
  public void testEvents() {
    oldState = unit.getState();
    unit.setState(GameObjectState.DESTROYED);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    if (propertyName.equals("state")) {
      Assert.assertEquals(oldState, evt.getOldValue());
      Assert.assertEquals(GameObjectState.DESTROYED, evt.getNewValue());
    } else {
      throw new AssertionError("Received changed property " + propertyName);
    }
  }
}
