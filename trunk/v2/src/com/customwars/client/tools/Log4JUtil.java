package com.customwars.client.tools;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Enumeration;

/**
 * log4J utilities
 */
public class Log4JUtil {
  private Log4JUtil() {
  }

  /**
   * Returns true if it appears that log4j have been previously configured. This code
   * checks to see if there are any appenders defined for log4j which is the
   * definitive way to tell if log4j is already initialized
   */
  public static boolean isLog4JConfigured() {
    Enumeration appenders = Logger.getRoot().getAllAppenders();
    if (appenders.hasMoreElements()) {
      return true;
    } else {
      Enumeration loggers = LogManager.getCurrentLoggers();
      while (loggers.hasMoreElements()) {
        Logger c = (Logger) loggers.nextElement();
        if (c.getAllAppenders().hasMoreElements())
          return true;
      }
    }
    return false;
  }
}
