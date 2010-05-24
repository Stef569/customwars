package com.customwars.client.io.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.awt.Color;

/**
 * Convert a hex value<color>FFFFFF</color> to the java.awt.Color object
 * The Alpha value defaults to 255
 */
public class HexColorConverter implements Converter {
  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
    Color color = (Color) o;
    writer.setValue(String.valueOf(color.getRGB()));
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    int rgb = Integer.valueOf(reader.getValue(), 16);
    return new Color(rgb);
  }

  @Override
  public boolean canConvert(Class type) {
    // String comparison is used here because Color.class loads the class which in turns instantiates AWT,
    // which is nasty if you don't want it.
    return type.getName().equals("java.awt.Color");
  }
}
