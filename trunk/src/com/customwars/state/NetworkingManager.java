package com.customwars.state;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.ai.Options;

public class NetworkingManager {

	private final static Logger	logger	= LoggerFactory.getLogger(NetworkingManager.class);

	public boolean tryToConnect() throws MalformedURLException, IOException {
		String command = "test";
		String reply = "";

		URL url = new URL(Options.getServerName() + "main.pl");
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-type", "text/plain");
		con.setRequestProperty("Content-length", command.length() + "");
		PrintStream out = new PrintStream(con.getOutputStream());
		out.print(command);
		out.flush();
		out.close();
		InputStream inputStream = con.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader in = new BufferedReader(inputStreamReader);
		String s;
		while ((s = in.readLine()) != null) {
			reply += s;
		}
		in.close();

		logger.info("Trying to connect to=[" + Options.getServerName() + "main.pl" + "] result=[" + reply + "]");
		if (!reply.equals("success")) {
			logger.info("Could not connect to=["  + Options.getServerName() + "main.pl" + "]");
			return false;
		}

		return true;
	}

	// try to connect to the server to see that the user's URL is correct
	public String sendCommandToMain(String command, String extra) throws MalformedURLException, IOException {
		String reply = "";

		URL url = new URL(Options.getServerName() + "main.pl");
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-type", "text/plain");
		if (extra.equals("")) {
			con.setRequestProperty("Content-length", command.length() + "");
		} else {
			con.setRequestProperty("Content-length", (command.length() + 1 + extra.length()) + "");
		}
		PrintStream out = new PrintStream(con.getOutputStream());
		out.print(command);
		if (!extra.equals("")) {
			out.print("\n");
			out.print(extra);
		}
		out.flush();
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String s = in.readLine();
		if (s != null) {
			reply += s;
			while ((s = in.readLine()) != null) {
				reply += "\n";
				reply += s;
			}
		}
		in.close();

		return reply;
	}

	public String sendFile(String script, String input, String file) throws MalformedURLException, IOException {
		String reply = "";

		URL url = new URL(Options.getServerName() + script);
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-type", "text/plain");
		byte buffer[] = new byte[1];
		logger.info("opening file");
		File source = new File(file);
		con.setRequestProperty("Content-length", (input.length() + 1 + source.length()) + "");

		PrintStream out1 = new PrintStream(con.getOutputStream());
		out1.print(input);
		out1.print("\n");
		FileInputStream src = new FileInputStream(file);
		logger.debug(">[" + Options.getServerName() + script +  "] Sending file [" + src + "]");
		OutputStream out = con.getOutputStream();
		while (true) {
			int count = src.read(buffer);
			if (count == -1) break;
			out.write(buffer);
		}
		out.flush();
		out.close();
		out1.flush();
		out1.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String s = in.readLine();
		if (s != null) {
			reply += s;
			while ((s = in.readLine()) != null) {
				reply += "\n";
				reply += s;
			}
		}
		in.close();

		return reply;
	}

	public boolean getFile(String script, String input, String file) throws MalformedURLException, IOException {
		URL url = new URL(Options.getServerName() + script);
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-type", "text/plain");
		logger.info("opening file");
		File source = new File(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
		logger.debug("Getting file [" + source + "]");
		logger.debug("< [" + Options.getServerName() + script +  "] Getting file [" + source + "]");
		con.setRequestProperty("Content-length", input.length() + "");
		PrintStream out = new PrintStream(con.getOutputStream());
		out.print(input);
		out.flush();
		out.close();

		// recieve reply
		byte buffer[] = new byte[1];
		FileOutputStream output = new FileOutputStream(ResourceLoader.properties.getProperty("saveLocation") + "/" + file);
		logger.debug("Getting reply [" + ResourceLoader.properties.getProperty("saveLocation") + "/" + file + "]");
		InputStream in = con.getInputStream();
		while (true) {
			int count = in.read(buffer);
			if (count == -1) break;
			output.write(buffer);
		}
		in.close();
		output.close();

		return true;
	}

}
