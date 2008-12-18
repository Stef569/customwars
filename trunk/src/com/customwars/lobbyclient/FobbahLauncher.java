package com.customwars.lobbyclient;
/*
 *FobbahLauncher.java
 *Author: Fobbah
 *Creation: July 11, 2006, 10:54 AM
 *Enables interface between fobbah's game browser and CW
 *CONTROLS:
 *w-up
 *a-left
 *s-down
 *d-right
 *enter-action key (similar to the A button in AW)
 *m-opens the menu
 */

//Temporary, used for engine testing and error checking
import java.io.*;
import javax.swing.*;

import com.customwars.ai.BaseDMG;
import com.customwars.ai.Mission;
import com.customwars.ai.Options;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.menu.MainMenu;
import com.customwars.unit.UnitGraphics;


public class FobbahLauncher 
{
    static JFrame frame;
    static MainMenu mm;
    static boolean launched = false;
    
    public static void setLaunched()
    {
    	launched = true;
    }
    public static void init(JFrame framein, MainMenu mmin)
    {
    	frame = framein;
    	mm = mmin;
    	setLaunched();
    }
    public static void main(String[] args) 
    {
    	/*
        //Create window
        frame = new JFrame("Custom Wars");
        frame.setSize(480,320);
        frame.setVisible(true);
        frame.setIconImage(new ImageIcon("images/misc/icon.gif").getImage());
        Mission.mainFrame = frame;
        
        //Initializes the static classes
        Options.InitializeOptions();
        BaseDMG.loadBaseDamage();
        UnitGraphics.loadImages(frame);
        TerrainGraphics.loadImages();
        MiscGraphics.loadImages(frame);
        MainMenuGraphics.loadImages(frame);
        //JoinGame("FobbahTestGame", "", "Fobbah2", "test123", 2);
        //LoginGame("FobbahTestGame", "Fobbah2", "test123");
        //CreateGame();
         */
    	ResourceLoader.init();	
    	launch();
    }
    public static void launch() 
    {
    	if(!launched)
    	{
        //Create window
        frame = new JFrame("Custom Wars");
        frame.setSize(480,320);
        frame.setVisible(true);
        frame.setIconImage(new ImageIcon(ResourceLoader.properties.getProperty("imagesLocation") + "/misc/icon.gif").getImage());
        Mission.mainFrame = frame;
        
        //Initializes the static classes
        Options.InitializeOptions();
        BaseDMG.loadBaseDamage();
        UnitGraphics.loadImages(frame);
        TerrainGraphics.loadImages(frame);
        MiscGraphics.loadImages(frame);
        MainMenuGraphics.loadImages(frame);
    	mm = new MainMenu(frame);
    	mm.setNewLoad();
    	frame.getContentPane().add(mm);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.validate();
    	frame.pack();
        //JoinGame("FobbahTestGame", "", "Fobbah2", "test123", 2);
        //LoginGame("FobbahTestGame", "Fobbah2", "test123");
        //CreateGame();
    	}

    	frame.setVisible(true);
    }
    public static void CreateGame()
    {
    	mm.LaunchCreateServerGame();
    }
    public static void CreateGame(String username, String password, String gamename, String gamepass)
    {
    	mm.LaunchCreateServerGame(username, password, gamename, gamepass);
    }
    
    public static void LoginGame(String gamename, String username, String password)
    {
    	mm.LaunchLoginGame(gamename, username, password);
    }
    public static void JoinGame(String gamename, String masterpass, String username, String userpass, int slotnumber)
    {
    	mm.LaunchJoinGame(gamename, masterpass, username, userpass, slotnumber);
    	
    }

}