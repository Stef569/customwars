package com.customwars.client.tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public final class XStreamUtil {
  private static final String ENCODING = "UTF-8";

  private XStreamUtil() {
  }

  public static Object readObject(XStream xStream, InputStream in) {
    try {
      return xStream.fromXML(in);
    } catch (Exception e) {
      throw new RuntimeException("could not read Object from inputstream", e);
    } finally {
      IOUtil.closeStream(in);
    }
  }

  public static void writeObject(XStream xStream, OutputStream os, Object obj) throws IOException {
    BufferedWriter osw;
    try {
      osw = new BufferedWriter(new OutputStreamWriter(os, ENCODING));
      String data = xStream.toXML(obj);
      data = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n" + data;
      osw.write(data);
    } finally {
      IOUtil.closeStream(os);
    }
  }

  /**
   * Force the usage of the ReflectionConverter for the given class.
   * Xstream can use a different converter like the SerializableConverter when
   * a readObject() method is defined and the Serializable interface is implemented. in the given class.
   */
  public static void useReflectionFor(XStream xStream, final Class aClass) {
    xStream.registerConverter(new ReflectionConverter(xStream.getMapper(), xStream.getReflectionProvider()) {
      public boolean canConvert(Class type) {
        return type.equals(aClass);
      }
    });
  }
}
