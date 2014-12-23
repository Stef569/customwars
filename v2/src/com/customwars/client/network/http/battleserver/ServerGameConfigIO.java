package com.customwars.client.network.http.battleserver;

import com.customwars.client.network.ServerGameConfig;
import com.customwars.client.network.ServerPlayer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Writes the game config to a stream
 * Reads the game config from a stream
 * The stream is closed after the method ha been executed
 */
public class ServerGameConfigIO {
  public static ServerGameConfig read(InputStream in) throws IOException {
    BufferedReader reader = null;
    ServerGameConfig config = new ServerGameConfig();

    try {
      reader = new BufferedReader(new InputStreamReader(in));
      int numOfPlayers = Integer.parseInt(reader.readLine());

      for (int i = 0; i < numOfPlayers; i++) {
        int slot = Integer.parseInt(reader.readLine());
        int team = Integer.parseInt(reader.readLine());
        Color color = new Color(Integer.parseInt(reader.readLine()));
        String controller = reader.readLine();
        String coName = reader.readLine();
        ServerPlayer player = new ServerPlayer(slot, coName, color, team, controller);
        config.addPlayer(player);
      }
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

    return config;
  }

  public static void write(ServerGameConfig config, OutputStream out) throws IOException {
    BufferedWriter writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(out));
      writer.write(config.getPlayers().size() + "");
      writer.newLine();

      for (ServerPlayer player : config.getPlayers()) {
        writer.write(player.getSlot() + "");
        writer.newLine();
        writer.write(player.getTeam() + "");
        writer.newLine();
        writer.write(player.getColor().getRGB() + "");
        writer.newLine();
        writer.write(player.getController());
        writer.newLine();
        writer.write(player.getCOName());
        writer.newLine();
      }
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
