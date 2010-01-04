package com.customwars.client.io.loading;

import com.customwars.client.model.game.Game;
import com.customwars.client.tools.IOUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * This class can read/write a Game + map from/to a stream
 * A stream written by this class starts with CW2_HEADER_START
 * When a stream is passed for reading that doesn't start with CW2_HEADER_START then
 * IOException is thrown.
 */
public class BinaryCW2GameParser {
  private static final String CW2_HEADER_START = "CW2.game";

  public Game readGame(InputStream in) throws IOException {
    DataInputStream dataIn = new DataInputStream(in);
    validateStream(dataIn);

    ObjectInputStream objIn = new ObjectInputStream(in);
    try {
      return (Game) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeStream(objIn);
    }
  }

  private void validateStream(DataInputStream dataIn) throws IOException {
    String gameHeaderStart = dataIn.readUTF();

    if (!gameHeaderStart.equals(CW2_HEADER_START)) {
      throw new IOException("This stream does not appear to be a game save stream");
    }
  }

  public void writeGame(Game game, OutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeUTF(CW2_HEADER_START);
    ObjectOutputStream objOut = null;
    try {
      objOut = new ObjectOutputStream(out);
      objOut.writeObject(game);
    } finally {
      IOUtil.closeStream(objOut);
    }
  }
}
