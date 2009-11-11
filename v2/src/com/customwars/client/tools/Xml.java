package com.customwars.client.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Util functions to handle a Xml dom tree
 *
 * @author Stefan
 */
public final class Xml {
  public static final int FIRST_N0DE = 0;
  private static final String DEFAULT_STRING_REPLACE_VAL = "";
  private static final Number DEFAULT_NUMBER_REPLACE_VAL = 0;
  private static final String DEFAULT_TOKEN = ",";

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private Xml() {
  }

  // ---------------------------------------------------------------------------
  // Retrieve data from a xml dom tree element
  // if null or not found then replace with:
  // DEFAULT_STRING_REPLACE_VAL for a String
  // DEFAULT_NUMBER_REPLACE_VAL for a number
  // false for a boolean
  // ---------------------------------------------------------------------------
  /**
   * Takes an xml element and the tag name, look for the tag and get
   * the text content
   * i.e for <employee><name>John</name></employee> xml snippet if
   * the Element points to employee and tagName is name this function will return John
   */
  public static String getTextValue(Element element, String tagName) {
    String textVal = "";
    NodeList nodeList = element.getElementsByTagName(tagName);
    if (nodeList != null && nodeList.getLength() > 0) {
      Element el = (Element) nodeList.item(FIRST_N0DE);
      if (el != null && el.getFirstChild() != null) {
        textVal = el.getFirstChild().getNodeValue();
      } else {
        textVal = DEFAULT_STRING_REPLACE_VAL;
      }
    } else {
      textVal = DEFAULT_STRING_REPLACE_VAL;
    }
    return textVal;
  }

  /**
   * Calls getTextValue and returns an int value
   */
  public static Integer getIntValue(Element element, String tagName) {
    String textVal;
    Integer intVal;
    try {
      textVal = getTextValue(element, tagName);
      intVal = Integer.parseInt(textVal);
    } catch (NumberFormatException ex) {
      intVal = (Integer) DEFAULT_NUMBER_REPLACE_VAL;
    }
    return intVal;
  }

  public static Byte getByteValue(Element element, String tagName) {
    String textVal;
    Byte byteVal;
    try {
      textVal = getTextValue(element, tagName);
      byteVal = Byte.parseByte(textVal);
    } catch (NumberFormatException ex) {
      byteVal = (Byte) DEFAULT_NUMBER_REPLACE_VAL;
    }
    return byteVal;
  }

  /**
   * Turns the String true into boolean True. ignoring case
   * Anything else then "True" results in false!
   */
  public static Boolean getBoolValue(Element element, String tagName) {
    String textVal;
    Boolean bool;
    textVal = getTextValue(element, tagName);
    bool = Boolean.parseBoolean(textVal);
    return bool;
  }

  /**
   * Tokenise the string found in the element, split on ,
   * add the results to the list and return it as strings.
   */
  public static List<String> getListValues(Element element, String tagName) {
    List<String> list = new LinkedList<String>();
    String text = getTextValue(element, tagName);
    StringTokenizer tokenizer = new StringTokenizer(text, DEFAULT_TOKEN);
    while (tokenizer.hasMoreTokens()) {
      list.add(tokenizer.nextToken());
    }
    return list;
  }

  /**
   * Tokenise the string found in the element, split on ,
   * add the results to the list and return it as Integers.
   */
  public static List<Integer> getIntValues(Element element, String tagName, int radius) {
    List<Integer> intList = new ArrayList<Integer>();
    List<String> stringList = getListValues(element, tagName);
    for (String s : stringList) {
      intList.add(Integer.parseInt(s.trim(), radius));
    }
    return intList;
  }

  /**
   * Get A Color from an element by reading it's content
   * pass the text on to the ColorUtil to transform it to a Color
   * Input can be hex or a defined colorName
   *
   * @see ColorUtil
   */
  public static Color getColorValue(Element element, String tagName) {
    String textVal = getTextValue(element, tagName);
    return ColorUtil.getColorFromText(textVal);
  }

  public static List<Color> getColorListFromText(Element element, String tagName) {
    List<Color> colorList = new ArrayList<Color>();
    List<String> colorNameList = getListValues(element, tagName);
    for (String textVal : colorNameList) {
      colorList.add(ColorUtil.toColor(textVal));
    }
    return colorList;
  }

  public static List<Color> getColorListFromHex(Element element, String tagName) {
    List<Color> colorList = new ArrayList<Color>();
    List<Integer> intList = getIntValues(element, tagName, 16);
    for (Integer intVal : intList) {
      colorList.add(new Color(intVal));
    }
    return colorList;
  }

  // ---------------------------------------------------------------------------
  // Retrieve data from a xml dom tree element
  // if null or not found then replace with:
  // the replaceWith parameter
  // ---------------------------------------------------------------------------
  /**
   * Takes an xml element and the tag name, look for the tag and get
   * the text content
   * i.e for <employee><name>John</name></employee> xml snippet if
   * the Element points to employee and tagName is name this function will return John
   * else if it is not found then replace it with: replaceWith
   */
  public static String getTextValue(Element element, String tagName, String replaceWith) {
    String textVal = "";
    NodeList nodeList = element.getElementsByTagName(tagName);
    if (nodeList != null && nodeList.getLength() > 0) {
      Element el = (Element) nodeList.item(0);
      if (el != null && el.getFirstChild() != null) {
        textVal = el.getFirstChild().getNodeValue();
      } else {
        textVal = replaceWith;
      }
    } else {
      textVal = replaceWith;
    }
    return textVal;
  }

  public static Integer getIntValue(Element element, String tagName, int replaceWith) {
    String textVal;
    int intVal;
    try {
      textVal = getTextValue(element, tagName);
      intVal = Integer.parseInt(textVal);
    } catch (NumberFormatException ex) {
      // Empty Strings means replace with 'replaceWith'
      intVal = replaceWith;
    }
    return intVal;
  }

  public static Byte getByteValue(Element element, String tagName, byte replaceWith) {
    String textVal;
    Byte byteVal;
    try {
      textVal = getTextValue(element, tagName);
      byteVal = Byte.parseByte(textVal);
    } catch (NumberFormatException ex) {
      // Empty Strings means a 'replaceWith' value.
      byteVal = replaceWith;
    }
    return byteVal;
  }

  // ---------------------------------------------------------------------------
  // Util
  // ---------------------------------------------------------------------------
  public static Element nodeToElement(Node node) {
    if (node == null) {
      throw new IllegalArgumentException("Node canot be null");
    }
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      return (Element) node;
    } else {
      throw new IllegalArgumentException("Node is not an element but " + node.getNodeType());
    }
  }

  // ---------------------------------------------------------------------------
  // Create Document
  // ---------------------------------------------------------------------------
  /**
   * Create an empty xml document
   */
  public static Document createXmlDocument() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;

    try {
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      throw new RuntimeException("Could not create docBuilder", ex);
    }

    return docBuilder.newDocument();
  }

  /**
   * Create a parsed xml document
   */
  public static Document createXmlDocument(InputStream stream) throws IOException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;

    try {
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      throw new RuntimeException("Could not create docBuilder", ex);
    }

    try {
      return docBuilder.parse(stream);
    } catch (SAXParseException ex) {
      throw new RuntimeException("XML Parsing error @" + "line: " + ex.getLineNumber(), ex);
    } catch (SAXException ex) {
      throw new RuntimeException("Xml SaxParser Error", ex);
    }
  }

  /**
   * Parse a xml file to a nodeList
   *
   * @param stream         The stream to an xml document
   * @param xmlRootElement The root ellement to retrieve.
   * @return a NodeList of ellements.
   * @throws java.io.IOException when the xml document could not be found/IO Error
   * @throws RuntimeException    When the xml document could not be parsed/ Parserconfig error
   */
  public static NodeList parseXmlStreamToNodeList(InputStream stream, String xmlRootElement) throws IOException {
    Document doc;
    NodeList xmlNodes;

    doc = createXmlDocument(stream);
    xmlNodes = doc.getElementsByTagName(xmlRootElement);
    return xmlNodes;
  }

  // ---------------------------------------------------------------------------
  // Output Document
  // ---------------------------------------------------------------------------
  public static void toFile(String fileName, Document XMLdoc) {
    Transformer transformer;
    try {
      transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(XMLdoc));
      transformer.transform(new DOMSource(XMLdoc), new StreamResult(new File(fileName)));
    }
    catch (TransformerConfigurationException ex) {
      throw new RuntimeException(ex);
    } catch (TransformerException ex) {
      throw new RuntimeException("Could not transform Xml document", ex);
    }
  }

  /**
   * Writes the document to a file
   *
   * @param file the file to store the document to
   * @param doc  the xml source document to transform to the file
   */
  public static void writeXmlFile(Document doc, File file) throws FileNotFoundException {
    try {
      Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      transform(new DOMSource(doc), result);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Transforms the Document doc to a string.
   *
   * @param doc the Document to convert to string
   * @return the Document as a string
   */
  public static String toString(Document doc) {
    StreamResult streamResult = new StreamResult(new StringWriter());
    try {
      transform(new DOMSource(doc), streamResult);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return streamResult.getWriter().toString();
  }

  private static Result transform(Source source, Result result) throws UnsupportedEncodingException {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setAttribute("indent-number", 4);

    try {
      Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(source, result);
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  // ---------------------------------------------------------------------------
  // property XML document helper functions
  // ---------------------------------------------------------------------------
  /**
   * Create an Element and add it to the root element
   * Ignoring null values
   */
  public static void createElement(Document doc, Element root, String tagName, String val) {
    if (tagName.trim().length() == 0) {
      throw new IllegalArgumentException("tagName cannot be empty");
    }

    // Null values are ignored
    if (val == null) {
      return;
    }
    if (val.trim().length() == 0) {
      throw new IllegalArgumentException("val cannot be empty");
    }

    Element el = doc.createElement(tagName);
    el.setTextContent(val);
    root.appendChild(el);
  }

  /**
   * Search for an Element with tagName under element
   */
  public static boolean hasElement(Element element, String tagName) {
    String txtVal = getTextValue(element, tagName);
    return txtVal != null;
  }
}
