package com.customwars.ui;
/*
 *MainMenuGraphics.java
 *Author: Urusan
 *Contributors:
 *Creation: August 7, 2006, 6:01 AM
 *Holds graphics information for many minor things
 */

import java.awt.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

import com.customwars.officer.COSelectFilter;
import com.customwars.state.ResourceLoader;

public class MainMenuGraphics {
    public static final int MAPNAME_BG_X = 4;
	public static final int MAPNAME_BG_Y = 40;
	public static final int MAPSELECT_UPARROW_X = 84;
	public static final int MAPSELECT_UPARROW_Y = 30;
	public static final int MAPSELECT_DOWNARROW_X = 84;
	public static final int MAPSELECT_DOWNARROW_Y = 312;
	public static final int MAPSELECT_CATEGORY_X = 4;
	public static final int MAPSELECT_CATEGORY_Y = 24;
	private static Image background;        //the background image
    private static Image newGame;           //the new game icon
    private static Image newGameSelected;   //the new game icon when selected
    private static Image maps;           //the design maps icon
    private static Image mapsSelected;   //the design maps icon when selected
    private static Image options;           //the options icon
    private static Image optionsSelected;   //the options icon when selected
    private static Image coLayout;           //the CO select layout
    private static Image mapLayout;   //the map select layout
    private static Image pnum;          //the player #'s
    private static Image[] playerNumber = new Image[8];  //the colored player #'s
    private static Image noCO;          //The NO CO graphic
    private static Image nowDrawing;    //The "now drawing" graphic
    private static Image titleBackground;//The title background
    private static Image logo;          //The CW Logo
    private static Image pgup;          //Page Up
    private static Image pgdn;          //Page Down
    private static Image tags[] = new Image[8]; //Army tags
    private static Image cobanner;      //the banner at the top of the CO screen
    private static Image coslot;        //the frame that goes around a CO
    private static Image coslots[] = new Image[8];      //recolored slots
    private static Image coLayouts[] = new Image[8];    //recolored layouts
    private static Image conametag;     //the co name tags
    private static Image hqbg;          //the HQ backgrounds
     private static Image baseOne[] = new Image[8]; //Intel background 1
    private static Image baseTwo[] = new Image[8]; //Intel background 2
    private static Image baseone;
    private static Image basetwo;
    private static Image emblem;  //the army emblem
    private static Image tagstar;
    private static Image mainmenuCO;
    //private static Image copyright;     //The copyright notice
	public static final int MAP_CATEGORY_ALL_X = 210;
	public static final int MAP_CATEGORY_Y = 30;
	public static final int MAP_CATEGORY_2_X = 250;
	public static final int MAP_CATEGORY_3_X = 270;
	public static final int MAP_CATEGORY_5_X = 310;
	public static final int MAP_CATEGORY_4_X = 290;
	public static final int MAP_CATEGORY_6_X = 330;
	public static final int MAP_CATEGORY_7_X = 350;
	public static final int MAP_CATEGORY_8_X = 370;
	public static final int MAP_CATEGORY_9_X = 390;
	public static final int MAP_CATEGORY_10_X = 410;
    
    
    
    
    //constructor
    public MainMenuGraphics() {
    }
    
    //loads the images before use
    public static void loadImages(Component screen){
    	String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    	
    	
        background = new ImageIcon(imagesLocation + "/misc/mainmenu/background.png").getImage();
        newGame = new ImageIcon(imagesLocation + "/misc/mainmenu/newgame.gif").getImage();
        newGameSelected = new ImageIcon(imagesLocation + "/misc/mainmenu/newgame2.gif").getImage();
        maps = new ImageIcon(imagesLocation + "/misc/mainmenu/maps.gif").getImage();
        mapsSelected = new ImageIcon(imagesLocation + "/misc/mainmenu/maps2.gif").getImage();
        options = new ImageIcon(imagesLocation + "/misc/mainmenu/options.gif").getImage();
        optionsSelected = new ImageIcon(imagesLocation + "/misc/mainmenu/options2.gif").getImage();
        coLayout = new ImageIcon(imagesLocation + "/misc/mainmenu/co_layout.gif").getImage();
        mapLayout = new ImageIcon(imagesLocation + "/misc/mainmenu/map_layout.gif").getImage();
        noCO = new ImageIcon(imagesLocation + "/misc/mainmenu/noco.gif").getImage();
        nowDrawing = new ImageIcon(imagesLocation + "/misc/mainmenu/nowprinting.gif").getImage();
        titleBackground = new ImageIcon(imagesLocation + "/misc/mainmenu/titlebg.png").getImage();
        logo = new ImageIcon(imagesLocation + "/misc/mainmenu/cwlogo.gif").getImage();
        pgup = new ImageIcon(imagesLocation + "/misc/mainmenu/pgup.gif").getImage();
        pgdn = new ImageIcon(imagesLocation + "/misc/mainmenu/pgdn.gif").getImage();
        cobanner = new ImageIcon(imagesLocation + "/misc/mainmenu/cobanner.gif").getImage();
        coslot = new ImageIcon(imagesLocation + "/misc/mainmenu/slot.gif").getImage();
        pnum = new ImageIcon(imagesLocation + "/misc/mainmenu/numbers.gif").getImage();
        conametag = new ImageIcon(imagesLocation + "/misc/mainmenu/conametag.gif").getImage();
        hqbg = new ImageIcon(imagesLocation + "/misc/mainmenu/hqbg.gif").getImage();
        baseone = new ImageIcon(imagesLocation + "/misc/mainmenu/base1.gif").getImage();
        basetwo = new ImageIcon(imagesLocation + "/misc/mainmenu/base2.gif").getImage();
        emblem = new ImageIcon(imagesLocation + "/misc/mainmenu/logos.gif").getImage();
        tagstar = new ImageIcon(imagesLocation + "/misc/mainmenu/tagstar.gif").getImage();
        
        for(int i = 0; i < 8; i++){
            tags[i] = new ImageIcon(imagesLocation + "/misc/mainmenu/tab"+(i+1)+".gif").getImage();
            COSelectFilter colorfilter = new COSelectFilter(i);
            coLayouts[i] = screen.createImage(new FilteredImageSource(coLayout.getSource(),colorfilter));
            coslots[i] = screen.createImage(new FilteredImageSource(coslot.getSource(),colorfilter));
            playerNumber[i] = screen.createImage(new FilteredImageSource(pnum.getSource(),colorfilter));
            baseOne[i] = screen.createImage(new FilteredImageSource(baseone.getSource(),colorfilter));
            baseTwo[i] = screen.createImage(new FilteredImageSource(basetwo.getSource(),colorfilter));
        
        }
    }
    
    public static Font getH1Font(){
		return new Font("SansSerif", Font.BOLD, 16);
    } 
    
    
    //returns the background
    public static Image getBackground(){
        return background;
    }
    
    //returns the CO Layout
    public static Image getCOLayout(int i){
        return coLayouts[i];
    }
    
    //returns the Map Layout
    public static Image getMapBG(){
        return mapLayout;
    }
    
    //returns the new game icon
    public static Image getNewGame(boolean isSelected){
        if(isSelected)return newGameSelected;
        return newGame;
    }
    
    //returns the design maps icon
    public static Image getMaps(boolean isSelected){
        if(isSelected)return mapsSelected;
        return maps;
    }
    
    //returns the options icon
    public static Image getOptions(boolean isSelected){
        if(isSelected)return optionsSelected;
        return options;
    }
    
    //returns the No CO graphic
    public static Image getNoCO(){
        return noCO;
    }
    
    public static Image getPlayerNumber(int number){
        return playerNumber[number];
    }
    
    //returns the Now Drawing graphic
    public static Image getNowDrawing(){
        return nowDrawing;
    }
    
    public static Image getTitleBackground(){
        return titleBackground;
    }
    
    public static Image getLogo(){
        return logo;
    }
    
    public static Image getMapSelectUpArrow(){
        return pgup;
    }
    
    public static Image getMapSelectDownArrow(){
        return pgdn;
    }
    
    public static Image getArmyTag(int i){
        return tags[i];
    }
    
    public static Image getCOBanner(){
        return cobanner;
    }
    
    public static Image getCOSlot(int i){
        return coslots[i];
    }
    
    public static Image getMainMenuCO(int i){
        return MiscGraphics.getCOSheet(i);
    }
    
    public static Image getCOName(){
        return conametag;
    }
    
    public static Image getHQBG(){
        return hqbg;
    }
    
    public static Image getBaseOne(int i){
        return baseOne[i];
    }

    public static Image getBaseTwo(int i){
        return baseTwo[i];
    }
    
    public static Image getEmblem(){
        return emblem;
    }
   
    public static Image getTagStar(){
        return tagstar;
    }

	public static Color getH1Color() {
		return Color.black;
	}
	
	public static void drawCategories_allSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.red);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_2playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_3playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_4playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_5playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_6playersSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_7playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_8PlayerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_9playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
	}

	public static void drawCategories_10playerSelected(Graphics2D graphic2D) {
		graphic2D.setColor(Color.white);
		graphic2D.drawString("ALL", MainMenuGraphics.MAP_CATEGORY_ALL_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("2", MainMenuGraphics.MAP_CATEGORY_2_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("3", MainMenuGraphics.MAP_CATEGORY_3_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("4", MainMenuGraphics.MAP_CATEGORY_4_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("5", MainMenuGraphics.MAP_CATEGORY_5_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("6", MainMenuGraphics.MAP_CATEGORY_6_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("7", MainMenuGraphics.MAP_CATEGORY_7_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("8", MainMenuGraphics.MAP_CATEGORY_8_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.drawString("9", MainMenuGraphics.MAP_CATEGORY_9_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.red);
		graphic2D.drawString("10", MainMenuGraphics.MAP_CATEGORY_10_X, MainMenuGraphics.MAP_CATEGORY_Y);
		graphic2D.setColor(Color.white);
	}
    
}