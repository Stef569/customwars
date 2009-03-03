package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.testdata.TestData;
import org.junit.Test;

/**
 * @author stefan
 */
public class PlayerTest {

  /**
   * A player with only an ID and a HQ
   */
  @Test
  public void createMapPlayer() {
    Player player = new Player(0, CityFactory.getCity(TestData.HQ));
  }
}
