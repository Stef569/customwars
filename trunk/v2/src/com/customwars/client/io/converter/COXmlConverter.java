package com.customwars.client.io.converter;

import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.COStyle;
import com.customwars.client.model.co.Power;
import com.customwars.client.tools.UCaseMap;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Convert a CO xml element into a CO object
 * coZone, power and super power are optional.
 * The following xml is an example that loads the CO Brenner:
 * <p/>
 * <co name="brenner" style="TWELFT_BATTALION">
 * <title></title>
 * <hit></hit>
 * <miss></miss>
 * <bio></bio>
 * <skill></skill>
 * <coZone>3</coZone>
 * <power name="Reinforce">
 * <description></description>
 * </power>
 * <intel>
 * <string></string>
 * <string></string>
 * </intel>
 * <quotes>
 * <string></string>
 * <string></string>
 * </quotes>
 * <victory>
 * <string></string>
 * </victory>
 * <defeat>
 * <string></string>
 * </defeat>
 * </co>
 */
public class COXmlConverter implements Converter {
  private final Map<String, COStyle> styles = new UCaseMap<COStyle>();

  public COXmlConverter(Collection<COStyle> stylesCollection) {
    for (COStyle style : stylesCollection) {
      styles.put(style.getName(), style);
    }
  }

  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
    throw new UnsupportedOperationException("Cos only have read from xml support");
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    String coName = reader.getAttribute("name");
    String style = reader.getAttribute("style");
    COStyle CoStyle = styles.get(style);
    reader.moveDown();
    String title = reader.getValue();
    gotoNextNode(reader);
    String hit = reader.getValue();
    gotoNextNode(reader);
    String miss = reader.getValue();
    gotoNextNode(reader);
    String bio = reader.getValue();
    gotoNextNode(reader);
    String skill = reader.getValue();
    gotoNextNode(reader);

    // coZone, power and super power are optional
    int coZone = 0;
    if (reader.getNodeName().equals("coZone")) {
      coZone = Integer.parseInt(reader.getValue());
      gotoNextNode(reader);
    }

    Power power = Power.NONE;
    if (reader.getNodeName().equals("power")) {
      power = readPower(reader);
    }

    Power superPower = Power.NONE;
    if (reader.getNodeName().equals("superpower")) {
      superPower = readPower(reader);
    }

    String intel = reader.getValue();
    gotoNextNode(reader);
    String[] quotes = readStringArray(reader);
    gotoNextNode(reader);
    String[] victory = readStringArray(reader);
    gotoNextNode(reader);
    String[] defeat = readStringArray(reader);
    reader.moveUp();
    return new BasicCO(coName, CoStyle, bio, title, coZone, hit, miss, skill, power, superPower, intel, defeat, victory, quotes);
  }

  private void gotoNextNode(HierarchicalStreamReader reader) {
    reader.moveUp();
    reader.moveDown();
  }

  private Power readPower(HierarchicalStreamReader reader) {
    String powerName = reader.getAttribute("name");
    reader.moveDown();
    String powerDescription = reader.getValue();
    reader.moveUp();
    gotoNextNode(reader);
    return new Power(powerName, powerDescription);
  }

  private static String[] readStringArray(HierarchicalStreamReader reader) {
    List<String> strings = new ArrayList<String>();

    while (reader.hasMoreChildren()) {
      reader.moveDown();
      strings.add(reader.getValue());
      reader.moveUp();
    }
    return strings.toArray(new String[strings.size()]);
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass == BasicCO.class;
  }
}
