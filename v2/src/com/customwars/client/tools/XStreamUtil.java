package com.customwars.client.tools;

import com.thoughtworks.xstream.XStream;

import java.io.BufferedWriter;
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

  public static void writeObject(XStream xStream, OutputStream os, Object obj) {
    BufferedWriter osw;
    try {
      osw = new BufferedWriter(new OutputStreamWriter(os, ENCODING));
      String data = xStream.toXML(obj);
      data = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>\n" + data;
      osw.write(data);
    } catch (Exception e) {
      throw new RuntimeException("Could not write object to stream.", e);
    } finally {
      IOUtil.closeStream(os);
    }
  }
}
