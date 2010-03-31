package com.customwars.client.network;

import java.util.Arrays;

/**
 * Thrown when a network message could not be send
 */
public class NetworkException extends Exception {
  private static final String[] NO_REPLY = new String[]{};
  private String reply = "";
  private String message = "";

  public NetworkException(Throwable ex) {
    super(ex);
    this.message = ex.toString();
  }

  public NetworkException(String message) {
    this(message, NO_REPLY);
  }

  public NetworkException(String message, String[] reply) {
    super(message + " reply: " + Arrays.toString(reply));
    this.message = message;
    this.reply = Arrays.toString(reply);
  }

  public String getReply() {
    return reply;
  }

  public String getMessage() {
    return message;
  }
}
