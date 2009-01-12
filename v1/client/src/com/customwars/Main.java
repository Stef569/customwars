package com.customwars;
/*
 *Main.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 11, 2006, 10:54 AM
 *Initializes the game and creates a BattleScreen
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
import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.lobbyclient.FobbahLauncher;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleGraphics;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.UnitGraphics;
import com.customwars.ui.menu.MainMenu;

import java.util.zip.CRC32;
public class Main {
    
    public static void main(String[] args) {
    	ResourceLoader.init();
    	
        //Create 
        JFrame frame = new JFrame("Custom Wars");
        frame.setSize(480,320);
        frame.setVisible(true);
        frame.setIconImage(new ImageIcon(ResourceLoader.properties.getProperty("imagesLocation") + "/misc/icon.gif").getImage());
        GameSession.mainFrame = frame;
        
        //Initializes the static classes
        Options.InitializeOptions();
        BaseDMG.restoreDamageTables();
        BaseDMG.restoreBalanceDamageTables();
        UnitGraphics.loadImages(frame);
        TerrainGraphics.loadImages(frame);
        MiscGraphics.loadImages(frame);
        MainMenuGraphics.loadImages(frame);
        BattleGraphics.loadImages(frame);
        
        //Opens the Main Menu
        MainMenu mm = new MainMenu(frame);
        frame.getContentPane().add(mm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.validate();
        frame.pack();
        FobbahLauncher.setLaunched();
    }
}
