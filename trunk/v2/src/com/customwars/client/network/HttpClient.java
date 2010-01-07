package com.customwars.client.network;

import com.customwars.client.tools.IOUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around the URLConnection class
 * Allows to POST and GET information from a server using the HTTP Protocol
 */
public class HttpClient {
  private static final Logger logger = Logger.getLogger(HttpClient.class);
  private static final String[] NO_PARAMETERS = new String[]{};
  private final URLConnection connection;
  private final char delimiter;

  /**
   * Create a new HTTP Client using '\n' as the delimiter to split messages
   *
   * @param serverURL The URL to the server
   */
  public HttpClient(String serverURL) throws IOException {
    this(serverURL, '\n');
  }

  /**
   * Create a new HTTP Client using the given delimiter to split messages
   *
   * @param serverURL The URL to the server
   */
  public HttpClient(String serverURL, char delimiter) throws IOException {
    this.delimiter = delimiter;
    this.connection = createConnection(serverURL);
  }

  private URLConnection createConnection(String serverURL) throws IOException {
    URL serverUrl = new URL(serverURL);
    URLConnection con = serverUrl.openConnection();
    con.setAllowUserInteraction(false);
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setUseCaches(false);
    return con;
  }

  /**
   * Send a command without parameters
   *
   * @param command command to send to the server
   */
  public void send(String command) throws IOException {
    send(command, NO_PARAMETERS);
  }

  /**
   * Send a message to the server, to separate the parameter a delimiter is used.
   * A message with parameters has following format:
   * command\na\nb\nc
   * A message with only a command has following format:
   * command
   *
   * @param command    command to send to the server
   * @param parameters An array of parameters like "a","b","c"
   */
  public void send(String command, String... parameters) throws IOException {
    String postData = createPostData(command, parameters);
    PrintStream out = new PrintStream(connection.getOutputStream());
    out.print(postData);
    logSendData(postData);
    out.close();
  }

  private String createPostData(String command, String[] parameters) {
    StringBuilder postData = new StringBuilder(command);
    for (String parameter : parameters) {
      postData.append(delimiter);
      postData.append(parameter);
    }
    return postData.toString();
  }

  /**
   * Read the replies in respond to a message. The first reply is at replies[0] the second at replies[1] ...
   * The server should send replies using the "\n" delimiter
   *
   * @return an array that contains all the replies
   */
  public String[] readReplies() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String[] reply = readString(in);
    in.close();
    logReply(reply);
    return reply;
  }

  private String[] readString(BufferedReader reader) throws IOException {
    List<String> content = new ArrayList<String>();
    String s;
    while ((s = reader.readLine()) != null) {
      content.add(s);
    }
    return content.toArray(new String[content.size()]);
  }

  /**
   * Upload the file to the server prepended by the command
   */
  public void upload(String command, File file) throws IOException {
    // Can't use send(command) here as it should be 1 continuous message
    OutputStream out = connection.getOutputStream();
    PrintStream printStream = new PrintStream(out);
    printStream.print(command);
    printStream.print(delimiter);
    logSendData(command + delimiter);
    IOUtil.copy(new FileInputStream(file), out);
    printStream.close();
  }

  /**
   * Download the reply from the command to the file
   */
  public void download(String command, File file) throws IOException {
    send(command);
    OutputStream fileOutputStream = new FileOutputStream(file);
    IOUtil.copy(connection.getInputStream(), fileOutputStream);
  }

  private void logSendData(String postData) {
    String logMsg = postData.replaceAll(delimiter + "", "-");
    logger.debug(connection.getURL() + " -> " + logMsg);
  }

  private void logReply(String[] reply) {
    logger.debug(connection.getURL() + " <- " + Arrays.toString(reply));
  }
}
