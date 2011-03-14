package com.customwars.client.tools;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
  /**
   * This is a static utility class. It cannot be constructed.
   */
  private ListUtil() {
  }

  /**
   * Join the given lists into 1 list.
   */
  public static <T> List<T> join(List<T>... lists) {
    List<T> result = new ArrayList<T>();
    for (List<T> list : lists) {
      result.addAll(list);
    }
    return result;
  }
}
