package com.customwars.client.io.loading;

import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.io.img.awt.ImgFilter;
import org.newdawn.slick.loading.DeferredResource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tools.IOUtil;
import tools.Xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Handles xml for 1 ImgFilter class
 * Only loading is supported
 * ImgFilters are added to the AwtImageLib
 *
 * @author stefan
 */
public class ImgFilterLoader implements DeferredResource {
  public static final String ROOT_ELEMENT_TAGNAME = "ColorFilters";
  private static final String XML_EL_COLOR_FILTER = "ColorFilter";
  private static final String XML_EL_NAME = "Name";
  private static final String XML_EL_BASE_COLOR = "BaseColor";
  private static final String XML_EL_ORIGINAL_COLORS = "OriginalColors";
  private static final String XML_EL_IGNORED_COLORS = "IgnoredColors";
  private static final String XML_EL_REPLACEMENT_COLOR = "ReplacementColor";
  private static final String XML_EL_REPLACEMENT_COLORS = "ReplacementColors";
  private String colorXmlPath;

  /**
   * @param colorXmlPath full path to the color xml file
   */
  public ImgFilterLoader(String colorXmlPath) {
    this.colorXmlPath = colorXmlPath;
  }

  public void load() throws IOException {
    InputStream colorStream = null;
    try {
      colorStream = IOUtil.getResource(colorXmlPath);
      NodeList colorNodes = Xml.parseXmlStreamToNodeList(colorStream, XML_EL_COLOR_FILTER);
      for (int i = 0; i < colorNodes.getLength(); i++) {
        Element colorElement = Xml.nodeToElement(colorNodes.item(i));
        fromXml(colorElement);
      }
    } finally {
      IOUtil.closeStream(colorStream);
    }
  }

  public void fromXml(Element element) {
    String imgFilterName = Xml.getTextValue(element, XML_EL_NAME);
    Color baseColor = Xml.getColorValue(element, XML_EL_BASE_COLOR);
    List<Color> originalColors = Xml.getColorListFromHex(element, XML_EL_ORIGINAL_COLORS);
    List<Color> ignoredColors = Xml.getColorListFromHex(element, XML_EL_IGNORED_COLORS);

    ImgFilter filter = new ImgFilter(baseColor);
    filter.addIgnoredPixels(ignoredColors.toArray(new Color[]{}));
    filter.addKnownColors(originalColors.toArray(new Color[]{}));

    XPath xPath = XPathFactory.newInstance().newXPath();
    NodeList replaceColorNodes;
    try {
      String expr = "/" + ROOT_ELEMENT_TAGNAME + "/" + XML_EL_COLOR_FILTER + "[Name='" + imgFilterName + "']/" + XML_EL_REPLACEMENT_COLOR;
      replaceColorNodes = (NodeList) xPath.evaluate(expr, element, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }

    for (int i = 0; i < replaceColorNodes.getLength(); i++) {
      Element el = Xml.nodeToElement(replaceColorNodes.item(i));
      addReplacementColor(el, filter);
    }
    AwtImageLib.addImgFilter(imgFilterName, filter);
  }

  private void addReplacementColor(Element element, ImgFilter filter) {
    Color replaceColorName = Xml.getColorValue(element, XML_EL_NAME);
    List<Color> repLaceColors = Xml.getColorListFromHex(element, XML_EL_REPLACEMENT_COLORS);
    filter.addReplacementColors(replaceColorName, repLaceColors);
  }

  public String getDescription() {
    return colorXmlPath;
  }
}
