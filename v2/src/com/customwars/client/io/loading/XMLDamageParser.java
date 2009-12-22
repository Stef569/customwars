package com.customwars.client.io.loading;

import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.tools.IOUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Load Damage values from an XML file (Idea from MiniWars)
 * The following format is used:
 * <damages>
 * <unit id="LTANK">
 * <enemies>
 * <enemy id="LTANK" basedamage="55"/>
 * ...
 * <enemy id="BSHIP" basedamage="1" altdamage="3"/>
 * </enemies>
 * </unit>
 * </damages>
 */
public class XMLDamageParser implements CWResourceLoader {
  private static final XStream xStream = new XStream(new DomDriver());
  private final InputStream in;

  public XMLDamageParser(InputStream in) {
    this.in = in;
  }

  public void load() throws IOException {
    xStream.alias("damages", List.class);
    xStream.alias("unit", UnitDamage.class);
    xStream.useAttributeFor(UnitDamage.class, "id");
    xStream.alias("enemy", Enemy.class);
    xStream.useAttributeFor(Enemy.class, "id");
    xStream.useAttributeFor(Enemy.class, "basedamage");
    xStream.useAttributeFor(Enemy.class, "altdamage");

    @SuppressWarnings("unchecked")
    List<UnitDamage> damages = (List<UnitDamage>) xStream.fromXML(in);

    try {
      int[][] baseDmgTables = convertToIntegerDamageTable(damages, true);
      UnitFight.setBaseDMG(baseDmgTables);
      int[][] altDmgTables = convertToIntegerDamageTable(damages, false);
      UnitFight.setAltDMG(altDmgTables);
    } finally {
      IOUtil.closeStream(in);
    }
  }

  private static int[][] convertToIntegerDamageTable(List<UnitDamage> damages, boolean baseDMG) {
    int[][] mainDmgTables = new int[50][50];

    for (UnitDamage damage : damages) {
      String unitName = damage.id;
      int attackerID = UnitFactory.getUnit(unitName).getStats().getID();
      int enemies = damage.enemies.size();

      if (enemies > damages.size()) {
        throw new RuntimeException(String.format("There are more enemies(%s) for %s(%s) then units(%s)",
          enemies, unitName, attackerID, damages.size()));
      }

      for (int i = 0; i < damage.enemies.size(); i++) {
        Enemy enemy = damage.enemies.get(i);
        int defenderID = UnitFactory.getUnit(enemy.id).getStats().getID();

        if (baseDMG) {
          mainDmgTables[attackerID][defenderID] = enemy.basedamage;
        } else {
          mainDmgTables[attackerID][defenderID] = enemy.altdamage;
        }
      }
    }
    return mainDmgTables;
  }

  private static class UnitDamage {
    String id;
    List<Enemy> enemies = new ArrayList<Enemy>();
  }

  private static class Enemy {
    String id;
    int basedamage;
    int altdamage;
  }
}
