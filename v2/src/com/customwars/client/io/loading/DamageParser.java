package com.customwars.client.io.loading;

import tools.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Parse a file filled with ints separated by whitespace
 * Lines starting with COMMENT_PREFIX are ignored
 *
 * @author stefan
 */
public class DamageParser {
  private final static String COMMENT_PREFIX = "//";

  public int[][] read(InputStream in) throws IOException {
    List<List<Integer>> damageTable = new ArrayList<List<Integer>>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line;

    try {
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0)
          continue;
        if (line.startsWith(COMMENT_PREFIX))
          continue;

        List<Integer> intRow = readIntRow(line);
        damageTable.add(intRow);
      }
    } finally {
      IOUtil.closeStream(in);
    }
    return copyTable(damageTable);
  }

  private List<Integer> readIntRow(String line) {
    List<Integer> intRow = new ArrayList<Integer>();
    Scanner scanner = new Scanner(line);

    while (scanner.hasNext()) {
      String number = scanner.next();
      intRow.add(Integer.parseInt(number));
    }
    return intRow;
  }

  private int[][] copyTable(List<List<Integer>> table) {
    final int[][] result = new int[table.size()][];
    for (int row = 0; row < table.size(); row++) {
      result[row] = copyRow(table.get(row).toArray(new Integer[table.size()]));
    }
    return result;
  }

  private int[] copyRow(final Integer[] array) {
    final int[] result = new int[array.length];
    for (int col = 0; col < array.length; col++) {
      result[col] = array[col];
    }
    return result;
  }
}
