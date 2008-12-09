package com.customwars.lobbyclient;
import java.util.LinkedList;

public class Game 
{
	public String gamename = "";
	public int numplayers;
	public int maxplayers;
	public int turnnumber;
	public int maxturns;
	public String mapname = "";
	public String version = "";
	public LinkedList playerlist = new LinkedList();
	public String lastaction = "";
	public String comment = "";
	
	public void printgame()
	{
		System.out.println("Game Name: " + gamename);
		System.out.println("Players: " + numplayers + "/" + maxplayers);
		System.out.println("Turn: " + turnnumber + "/" + maxturns);
		System.out.println("Mapname: " + mapname);
		System.out.println("Version: " + version);
		System.out.print("Players: ");
		for(int i = 0; i < playerlist.size(); i++)
		{
			System.out.print("" + playerlist.get(i) + " ");
		}
		System.out.println();
		System.out.println("Last Action: " + lastaction);
		System.out.println("Comment: " + comment);
	}
}
