package com.customwars.client;

import com.customwars.client.io.loading.BinCW2MapParserTest;
import com.customwars.client.io.loading.ControlsConfiguratorTest;
import com.customwars.client.io.loading.FileSystemManagerTest;
import com.customwars.client.model.game.GameTest;
import com.customwars.client.model.gameobject.CityTest;
import com.customwars.client.model.gameobject.CityXStreamTest;
import com.customwars.client.model.gameobject.TerrainTest;
import com.customwars.client.model.gameobject.TerrainXStreamTest;
import com.customwars.client.model.gameobject.UnitEventTest;
import com.customwars.client.model.gameobject.UnitFightTest;
import com.customwars.client.model.gameobject.UnitTest;
import com.customwars.client.model.gameobject.UnitXStreamTest;
import com.customwars.client.model.map.TileMapTest;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BinCW2MapParserTest.class, FileSystemManagerTest.class, ControlsConfiguratorTest.class,
  GameTest.class, TileMapTest.class,
  UnitTest.class, UnitEventTest.class, UnitFightTest.class, CityTest.class, TerrainTest.class,
  CityXStreamTest.class, TerrainXStreamTest.class, UnitXStreamTest.class
})
public class CwClientTestRunner {
  @BeforeClass
  public static void beforeAllTests() {
    // Ignore any logging messages
    Logger.getRootLogger().addAppender(new NullAppender());
  }
}
