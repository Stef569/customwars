package com.customwars.client.io.loading;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.tools.IOUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * This class can
 * read/write a saved game and a replay game
 * A stream written by this class starts with a header
 */
public class BinaryCW2GameParser {
  private static final String CW2_SAVED_GAME_HEADER = "CW2.saved.game";
  private static final String CW2_REPLAY_HEADER = "CW2.replay";

  public void writeGame(Game game, OutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeUTF(CW2_SAVED_GAME_HEADER);
    ObjectOutputStream objOut = null;
    try {
      objOut = new ObjectOutputStream(out);
      objOut.writeObject(game);
    } finally {
      IOUtil.closeStream(objOut);
    }
  }

  public Game readGame(InputStream in) throws IOException {
    DataInputStream dataIn = new DataInputStream(in);
    validateHeader(CW2_SAVED_GAME_HEADER, dataIn, "saved game stream");

    ObjectInputStream objIn = new ObjectInputStream(in);
    try {
      return (Game) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeStream(objIn);
    }
  }

  public void writeReplay(GameReplay gameReplay, OutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeUTF(CW2_REPLAY_HEADER);
    ObjectOutputStream objOut = null;
    try {
      objOut = new ObjectOutputStream(out);
      objOut.writeObject(gameReplay);
    } finally {
      IOUtil.closeStream(objOut);
    }
  }

  public GameReplay readReplay(InputStream in) throws IOException {
    DataInputStream dataIn = new DataInputStream(in);
    validateHeader(CW2_REPLAY_HEADER, dataIn, "replay stream");

    ObjectInputStream objIn = new ObjectInputStream(in);
    try {
      return (GameReplay) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeStream(objIn);
    }
  }

  private void validateHeader(String expectedHeader, DataInputStream in, String streamName) throws IOException {
    String gameHeaderStart = in.readUTF();

    if (!gameHeaderStart.equals(expectedHeader)) {
      throw new IOException("This stream does not appear to be a " + streamName +
        " found " + gameHeaderStart + " expected " + expectedHeader);
    }
  }
}
