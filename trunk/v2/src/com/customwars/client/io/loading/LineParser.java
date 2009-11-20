package com.customwars.client.io.loading;

import com.customwars.client.tools.IOUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read lines from a Stream
 * Empty lines and lines starting with COMMENT_PREFIX are skipped
 * Each time a line is read invoke the parseLine(line) method
 */
public abstract class LineParser implements CWResourceLoader {
  private final static String COMMENT_PREFIX = "//";
  private final InputStream stream;

  public LineParser(InputStream stream) {
    this.stream = stream;
  }

  public void load() throws IOException {
    for (String line : IOUtil.getLinesFromFile(stream)) {
      if (!line.startsWith(COMMENT_PREFIX)) {
        parseLine(line);
      }
    }
  }

  public abstract void parseLine(String line);
}
