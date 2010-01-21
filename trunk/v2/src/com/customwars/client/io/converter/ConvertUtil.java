package com.customwars.client.io.converter;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Direction;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class contains code that is shared between converters
 */
public class ConvertUtil {

  /**
   * Convert the xml '<connect>North, East</connect>' to a List of direction enums [North, East]
   */
  public static List<Direction> readConnectionNode(HierarchicalStreamReader reader) {
    List<Direction> connections = null;

    while (reader.hasMoreChildren()) {
      reader.moveDown();
      final String nodeName = reader.getNodeName();

      if (nodeName.equals("connect")) {
        connections = readDirections(reader.getValue());
        reader.moveUp();
      } else {
        throw new IllegalArgumentException(
          "Could not find 'connect' node. Instead I found " + nodeName + " but that's not what i'm looking for."
        );
      }
    }
    return connections;
  }

  private static List<Direction> readDirections(String directions) {
    List<Direction> connects = new ArrayList<Direction>();
    StringTokenizer tokenizer = new StringTokenizer(directions, ",");
    while (tokenizer.hasMoreTokens()) {
      Direction direction = Direction.getDirection(tokenizer.nextToken());
      connects.add(direction);
    }
    return connects;
  }

  public static void writeField(String fieldName, Object obj, Object val) {
    Field field = Fields.find(Terrain.class, fieldName);
    Fields.write(field, obj, val);
  }
}
