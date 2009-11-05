package com.customwars.client.io.loading;

import com.customwars.client.ui.state.input.CWInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.LWJGLException;
import org.newdawn.slick.Input;

import java.util.List;
import java.util.Properties;

/**
 * Test reading user properties
 * starting with keys,
 * mouse controls can't be tested since they require a display.
 *
 * @author stefan
 */
public class ControlsConfiguratorTest {
  private ControlsConfigurator controlsConfigurator;
  private CWInput inputProvider;

  @Before
  public void beforeEachTest() throws LWJGLException {
    inputProvider = new CWInput(new Input(0));
    controlsConfigurator = new ControlsConfigurator(inputProvider);
  }

  @Test
  public void testReadingKeyInput() {
    Properties properties = new Properties();
    properties.put(CWInput.INPUT_PREFIX + ".CanCeL", "x,W,a,9,f5");
    properties.put(CWInput.INPUT_PREFIX + ".SELECT", "B,C,D,E,F,ESCAPE");
    properties.put(CWInput.INPUT_PREFIX + ".Toggle_Music", "1");

    controlsConfigurator.configure(properties);
    List commands = inputProvider.getUniqueCommands();
    Assert.assertFalse(commands.contains(null));
    Assert.assertEquals(3, commands.size());
    Assert.assertEquals(5, inputProvider.getControlsFor(CWInput.CANCEL).size());
    Assert.assertEquals(6, inputProvider.getControlsFor(CWInput.SELECT).size());
    Assert.assertEquals(1, inputProvider.getControlsFor(CWInput.TOGGLE_MUSIC).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(CWInput.ZOOM_IN).size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadingDuplicateKeyInput() {
    Properties properties = new Properties();
    // Dilema 2 keys binded to 2 different commands
    // Throws an exception
    properties.put(CWInput.INPUT_PREFIX + ".CANCEL", "A");
    properties.put(CWInput.INPUT_PREFIX + ".SELECT", "A");
    controlsConfigurator.configure(properties);

    Assert.assertEquals(1, inputProvider.getControlsFor(CWInput.CANCEL).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(CWInput.SELECT).size());
  }

  @Test()
  public void testReadingDuplicateCommands() {
    Properties properties = new Properties();
    // 2 commands, the 2nd command overwrites the first command
    // Only B is used as control for CANCEL
    properties.put(CWInput.INPUT_PREFIX + ".CANCEL", "A");
    properties.put(CWInput.INPUT_PREFIX + ".CANCEL", "B");
    controlsConfigurator.configure(properties);
  }
}
