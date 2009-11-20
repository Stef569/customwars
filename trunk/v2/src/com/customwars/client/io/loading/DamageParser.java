package com.customwars.client.io.loading;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Parse a file filled with ints separated by a single whitespace
 *
 * @author stefan
 */
public class DamageParser extends LineParser {
  private final List<List<Integer>> damageTable;

  public DamageParser(InputStream stream) {
    super(stream);
    this.damageTable = new ArrayList<List<Integer>>();
  }

  public void parseLine(String line) {
    List<Integer> intRow = readIntRow(line);
    damageTable.add(intRow);
  }

  private List<Integer> readIntRow(String line) {
    List<Integer> intRow = new ArrayList<Integer>();
    Scanner scanner = new Scanner(line);

    while (scanner.hasNext()) {
      intRow.add(scanner.nextInt());
    }
    return intRow;
  }

  public int[][] getDmgTable() {
    return convertToPrimitiveArray(damageTable);
  }

  private int[][] convertToPrimitiveArray(List<List<Integer>> table) {
    final int[][] result = new int[table.size()][];

    for (int row = 0; row < table.size(); row++) {
      List<Integer> list = table.get(row);
      Integer[] arrRow = list.toArray(new Integer[table.size()]);
      result[row] = convertToPrimitiveArray(arrRow);
    }
    return result;
  }

  private int[] convertToPrimitiveArray(final Integer[] array) {
    final int[] result = new int[array.length];
    for (int col = 0; col < array.length; col++) {
      result[col] = array[col];
    }
    return result;
  }
}
