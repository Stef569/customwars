package com.customwars.ui;
/*
 *MiscGraphics.java
 *Author: Urusan
 *Contributors:
 *Creation: July 12, 2006, 12:26 PM
 *Holds graphics information for many minor things
 */

import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

import com.customwars.Options;
import com.customwars.officer.COList;
import com.customwars.officer.COPAnimFilter;
import com.customwars.state.ResourceLoader;

public class MiscGraphics {
    private static Image attackTile;    //the image used for attack range
    private static Image moveTile;      //the image used for move range
    private static Image hpDisplay[] = new Image[9]; //holds the 9 hp display images
    
    //CO Bars
    private static Image coBars[] = new Image[10]; //holds the different colored CO bars
    private static Image coBarsReverse[] = new Image[10]; //holds the different colored reverseCO bars
    private static Image altCOBars[] = new Image[10]; //holds the different colored alt CO bars
    private static Image altCOBarsReverse[] = new Image[10]; //holds the different colored alt reverseCO bars
    private static Image gold;          //the G.
    private static Image questionMark;  //question mark for FOW
    private static Image moneyDisplay[] = new Image[10]; //holds the 10 money display digits
    private static Image smallStars[] = new Image[7];   //The little stars for CO Powers
    private static Image bigStars[] = new Image[7];   //The big stars for Super CO Powers
    private static Image coSheets[] = new Image[COList.getListing().length];    //The CO spritesheets
	//private static Image altFaceCOs[] = new Image[36];    //The altrenate CO face images
    private static Image power;                         //The CO Power text
    private static Image superPower;                    //The Super CO Power text
    private static Image tagBreak;                      //The Tag Break text
    private static Image siloCursor;                      //The Silo Cursor
    private static Image hiddenHP;                      //question mark for hidden hp
    private static Image captureIcon;                   //the capture icon
    private static Image loadIcon;                      //the load icon
    private static Image diveIcon;                      //the dive icon
    private static Image lowAmmoIcon;                      //the low ammo icon
    private static Image lowFuelIcon;                      //the low fuel icon
    private static Image minimap;                      //the minimap sheet
    private static Image cursor[] = new Image[14];      //The Cursors
    private static Image rain[] = new Image[2];         //the rain weather images
    private static Image snow[] = new Image[3];         //the snow weather images
    private static Image sand[] = new Image[3];         //the sand weather images
    private static Image detect;                        //the detection image
    private static Image dayStartScreens[] = new Image[10]; //the day start screen images
    //victory screen images
    private static Image sky;                           //the sky background
    private static Image win;                           //the win bar
    private static Image lose;                          //the lose bar
    private static Image days;                          //the days bar
    //endgame statistics images
    private static Image statsBox;                      //the box that the statistics go in
    //CO bio images
    private static Image skill;
    private static Image COP;
    private static Image SCOP;
    private static Image hit;
    private static Image miss;
    private static Image arrow[] = new Image[10];       //the path arrow 0=h 1=v 2=ne 3=se 4=sw 5=nw 6-9=nsew arrows
	private static Image battleBackground;
	//Explosions
    private static Image airExplode;
    private static Image groundExplode;
    private static Image seaExplode;
    private static int explodeDelay;
    private static Image siloExplode;
    private static int siloDelay;
    //Power stuff
    private static Image superPowerIcon;
    private static Image ANpowerBack;
    private static Image DApowerBack;
    private static Image dialogueBox;
    //Missile Launch
    private static Image missileup;
    
    //sup i added stuff to make filters work - kosheh
    private static Image[] ANback = new Image[10];
    private static Image[] DAback = new Image[10];
    private static Image[] speech = new Image[10];
    private static Image[] SCOPicon = new Image[10];
    private static Image[] smallfont = new Image[26];
    private static Image[] capitalfont = new Image[26];
    private static BufferedImage test;
	private static Image aimCursor;
	private static Image smallHeart;
        private static Image Pointer;
        private static Image intelBackground;
    //constructor
    public MiscGraphics() {
    }
    
    //loads the images before use
    public static void loadImages(Component screen){
    	String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    	
    	
        attackTile = new ImageIcon(imagesLocation + "/misc/attacktile.gif").getImage();
        moveTile = new ImageIcon(imagesLocation + "/misc/movetile.gif").getImage();
        siloCursor = new ImageIcon(imagesLocation + "/misc/silocursor.gif").getImage();
        hiddenHP = new ImageIcon(imagesLocation + "/misc/hidden.gif").getImage();
        captureIcon = new ImageIcon(imagesLocation + "/misc/capture.gif").getImage();
        loadIcon = new ImageIcon(imagesLocation + "/misc/load.gif").getImage();
        diveIcon = new ImageIcon(imagesLocation + "/misc/dive.gif").getImage();
        lowAmmoIcon = new ImageIcon(imagesLocation + "/misc/lowammo.gif").getImage();
        lowFuelIcon = new ImageIcon(imagesLocation + "/misc/lowfuel.gif").getImage();
        minimap = new ImageIcon(imagesLocation + "/misc/minimap.gif").getImage();
        detect = new ImageIcon(imagesLocation + "/misc/detect.gif").getImage();
        battleBackground = new ImageIcon(imagesLocation + "/misc/battlebg.png").getImage();
        
        for(int i = 0; i < 10; i++){
            dayStartScreens[i] = new ImageIcon(imagesLocation + "/misc/dayprog"+ i + ".gif").getImage();
        }
        
        for(int i = 0; i < 14; i++){
            cursor[i] = new ImageIcon(imagesLocation + "/misc/cursors/cursor" + i + ".gif").getImage();
        }
        
        for(int i = 1; i < 10; i++){
            hpDisplay[i-1] = new ImageIcon("images/misc/"+ i + ".gif").getImage();
        }
        
        for(int i = 0; i < 10; i++){
            coBars[i] = new ImageIcon(imagesLocation + "/misc/cobar"+ i + ".gif").getImage();
            coBarsReverse[i] = new ImageIcon(imagesLocation + "/misc/cobar"+ i + "r.gif").getImage();
            altCOBars[i] = new ImageIcon(imagesLocation + "/misc/tagcobar"+ i + ".gif").getImage();
            altCOBarsReverse[i] = new ImageIcon(imagesLocation + "/misc/tagcobar"+ i + "r.gif").getImage();
        }
        
        for(int i = 0; i < 10; i++){
            moneyDisplay[i] = new ImageIcon(imagesLocation + "/misc/money/"+ i + ".gif").getImage();
        }
        gold = new ImageIcon(imagesLocation + "/misc/money/gold.gif").getImage();
        questionMark = new ImageIcon(imagesLocation + "/misc/money/question_mark.gif").getImage();
        
        for(int i = 0; i < 7; i++){
            smallStars[i] = new ImageIcon(imagesLocation + "/misc/stars/small"+ i + ".gif").getImage();
            bigStars[i] = new ImageIcon(imagesLocation + "/misc/stars/large"+ i + ".gif").getImage();
        }
        power = new ImageIcon(imagesLocation + "/misc/stars/cop.gif").getImage();
        superPower = new ImageIcon(imagesLocation + "/misc/stars/scop.gif").getImage();
        tagBreak = new ImageIcon(imagesLocation + "/misc/stars/tag_break.gif").getImage();
        
        for(int i = 0; i < COList.getListing().length; i++){
            coSheets[i] = new ImageIcon(imagesLocation + "/misc/sheets/"+COList.getLowerCaseName(i)+".gif").getImage();
        }
        
        for(int i = 1; i < 4; i++){
            if(i!=3)rain[i-1] = new ImageIcon(imagesLocation + "/misc/weather/rain"+ i + ".gif").getImage();
            snow[i-1] = new ImageIcon(imagesLocation + "/misc/weather/snow"+ i + ".gif").getImage();
            sand[i-1] = new ImageIcon(imagesLocation + "/misc/weather/sandstorm"+ i + ".gif").getImage();
        }
        
        sky = new ImageIcon(imagesLocation + "/misc/victory/sky.gif").getImage();
        win = new ImageIcon(imagesLocation + "/misc/victory/win.gif").getImage();
        lose = new ImageIcon(imagesLocation + "/misc/victory/lose.gif").getImage();
        days = new ImageIcon(imagesLocation + "/misc/victory/days.gif").getImage();
        statsBox = new ImageIcon(imagesLocation + "/misc/stats/form.gif").getImage();
        
        skill = new ImageIcon(imagesLocation + "/misc/skill.gif").getImage();
        COP = new ImageIcon(imagesLocation + "/misc/power.gif").getImage();
        SCOP = new ImageIcon(imagesLocation + "/misc/super.gif").getImage();
        hit = new ImageIcon(imagesLocation + "/misc/hit.gif").getImage();
        miss = new ImageIcon(imagesLocation + "/misc/miss.gif").getImage();

        arrow[0] = new ImageIcon(imagesLocation + "/misc/arrow/h.gif").getImage();
        arrow[1] = new ImageIcon(imagesLocation + "/misc/arrow/v.gif").getImage();
        arrow[2] = new ImageIcon(imagesLocation + "/misc/arrow/ne.gif").getImage();
        arrow[3] = new ImageIcon(imagesLocation + "/misc/arrow/se.gif").getImage();
        arrow[4] = new ImageIcon(imagesLocation + "/misc/arrow/sw.gif").getImage();
        arrow[5] = new ImageIcon(imagesLocation + "/misc/arrow/nw.gif").getImage();
        arrow[6] = new ImageIcon(imagesLocation + "/misc/arrow/n.gif").getImage();
        arrow[7] = new ImageIcon(imagesLocation + "/misc/arrow/e.gif").getImage();
        arrow[8] = new ImageIcon(imagesLocation + "/misc/arrow/s.gif").getImage();
        arrow[9] = new ImageIcon(imagesLocation + "/misc/arrow/w.gif").getImage();

        airExplode = new ImageIcon(imagesLocation + "/misc/air.gif").getImage();
        groundExplode = new ImageIcon(imagesLocation + "/misc/land.gif").getImage();
        seaExplode = new ImageIcon(imagesLocation + "/misc/water.gif").getImage();
        explodeDelay = 22; //Takes 48 frames, or .9 seconds.
        siloExplode = new ImageIcon(imagesLocation + "/misc/siloexplosion.gif").getImage();
        siloDelay = 55; //Takes 1200ms, but a 100ms delay is accounted for
        
        missileup = new ImageIcon(imagesLocation + "/misc/missileup.gif").getImage();
        
        Pointer = new ImageIcon(imagesLocation + "/misc/point.gif").getImage();
        //This should be alphabatized eventually.
        
	//I changed the backgrounds to match the armies; so if possible (AND TO ADD EXTRA WORK)
	//try to get it so the game can differentiate between AN and DA COs.  Sorry I couldn't 
	//do it, I'm in a rush ;_; - kosheh
        ANpowerBack = new ImageIcon(imagesLocation + "/misc/power/ANback.gif").getImage();
	DApowerBack = new ImageIcon(imagesLocation + "/misc/power/DAback.gif").getImage();
        superPowerIcon = new ImageIcon(imagesLocation + "/misc/power/superpower.gif").getImage(); 
        dialogueBox = new ImageIcon(imagesLocation + "/misc/power/diagbox.gif").getImage();
	//THE HEART OF THE COLOR CODING~!! - kosheh
        for(int i=0; i<10; i++){
            COPAnimFilter colorfilter = new COPAnimFilter(i);
            ANback[i] = screen.createImage(new FilteredImageSource(ANpowerBack.getSource(),colorfilter));
            DAback[i] = screen.createImage(new FilteredImageSource(DApowerBack.getSource(),colorfilter));
            SCOPicon[i] = screen.createImage(new FilteredImageSource(superPowerIcon.getSource(),colorfilter));
            speech[i] = screen.createImage(new FilteredImageSource(dialogueBox.getSource(),colorfilter));
	}

        Character c = new Character(' ');
        for(int i = 0; i<26; i++)
        {
            smallfont[i] = new ImageIcon(imagesLocation + "/misc/power/font/" + c.forDigit(i+10,36)+"smal1.gif").getImage();
            capitalfont[i] = new ImageIcon(imagesLocation + "/misc/power/font/" + c.forDigit(i+10,36)+"1.gif").getImage();
        }
        
        aimCursor = new ImageIcon(imagesLocation + "/misc/cursors/aimcursor0.gif").getImage();
        smallHeart = new ImageIcon(imagesLocation + "/misc/smallheart.gif").getImage();
        intelBackground = new ImageIcon(imagesLocation + "/misc/intelbg.gif").getImage();
    }
    
    public static Image getSmallHeart()
    {
    	return smallHeart;
    }
    
    public static Image getAimCursor()
    {
    	return aimCursor;
    }
    
    public static void setAimCursor(String aimImg)
    {
        aimCursor = new ImageIcon(aimImg).getImage();
    }
    
    public static void restoreAimCursor()
    {        
    	String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
        aimCursor = new ImageIcon(imagesLocation + "/misc/cursors/aimcursor0.gif").getImage();
    }
    
    public static Image getLandExplode() {
        return groundExplode;
    }
    public static Image getSeaExplode() {
        return seaExplode;
    }
    public static Image getAirExplode() {
        return airExplode;
    }
    public static int getExplodeDelay(){
        return explodeDelay;
    }
    public static Image getSiloExplode(){
        return siloExplode;
    }
    public static int getSiloDelay(){
        return siloDelay;
    }
    public static Image getMissileUp(){
        return missileup;
    }
    public static Image getPointer(){
        return Pointer;
    }
    //AN Background (the lines, of SuperPowre)
    public static Image getPowerBackground(int style){
        return ANback[style];
    }
    //DA Background (the EVIL lines, of SuperPowre)
    public static Image getEvilPowerBackground(int style){
        return DAback[style];
    }
    //This is the "SUPER POWER" that appears
    public static Image getSuperPowerIcon(int style){
        return SCOPicon[style];
    }
    //This is the dialogue box
    public static Image getDialogueBox(int style){
        return speech[style];
    }
    public static Image getPowerFONT(char c){
        Character tempc = new Character(c);
        if(tempc.isUpperCase(c))
            return capitalfont[tempc.digit(c,36)-10];
        if (tempc.isLowerCase(c))
            return smallfont[tempc.digit(c,36)-10];
        return capitalfont[25]; //If the character doensn't exist, return Z
    }
    //returns the attack tile
    public static Image getAttackTile(){
        return attackTile;
    }
    
    //stop animation for the attack tile
    public static void endAttackTile(){
        attackTile.flush();
    }
    
    //returns the move tile
    public static Image getMoveTile(){
        return moveTile;
    }
    
    //stop animation for the move tile
    public static void endMoveTile(){
        moveTile.flush();
    }
    
    //returns a hp display image
    public static Image getHpDisplay(int num){
        return hpDisplay[num-1];
    }
    
    //returns a money display digit based on a character
    public static Image getMoneyDigit(char num){
        int i=0;
        switch(num){
            case '0':
                i=0;
                break;
            case '1':
                i=1;
                break;
            case '2':
                i=2;
                break;
            case '3':
                i=3;
                break;
            case '4':
                i=4;
                break;
            case '5':
                i=5;
                break;
            case '6':
                i=6;
                break;
            case '7':
                i=7;
                break;
            case '8':
                i=8;
                break;
            case '9':
                i=9;
                break;
        }
        return moneyDisplay[i];
    }
    
    //returns a CO Bar
    public static Image getCOBar(int color){
        return coBars[color];
    }
    
    //returns a reverse CO Bar
    public static Image getReverseCOBar(int color){
        return coBarsReverse[color];
    }
    
    //returns an alt CO Bar
    public static Image getAltCOBar(int color){
        return altCOBars[color];
    }
    
    //returns a and alt reverse CO Bar
    public static Image getAltReverseCOBar(int color){
        return altCOBarsReverse[color];
    }
    
 /*   //returns a CO's CO Bar Portrait
    public static Image getCOBarPortrait(int id){
        return coBarCOs[id];
    }
    
    //returns a CO's CO Bar Portrait
    public static Image getReverseCOBarPortrait(int id){
        return coBarCOsReverse[id];
    } */
    
    //returns a CO Face
   /* public static Image getCOFace(int id){
        return faceCOs[id];
    }*/
    
    //returns the G.
    public static Image getGold(){
        return gold;
    }
    
    //returns a small star (0= empty to 5=full and 6=active)
    public static Image getSmallStar(int type){
        return smallStars[type];
    }
    
    //returns a big star (0= empty to 5=full and 6=active)
    public static Image getBigStar(int type){
        return bigStars[type];
    }
    
    //returns the question mark for FOW
    public static Image getQuestionMark(){
        return questionMark;
    }
    
    //returns the CO Power Text
    public static Image getPower(){
        return power;
    }
    
    //returns the Super CO Power text
    public static Image getSuperPower(){
        return superPower;
    }
    
    //returns the Tag Break Text
    public static Image getTagBreak(){
        return tagBreak;
    }
    
    //returns a CO's CO full body sprite
  /*  public static Image getCOFullBody(int id){
        return fullBodyCOs[id];
    }*/
    
    //returns a CO's Sprite Sheet
    public static Image getCOSheet(int id){
        return coSheets[id];
    }
    
    //returns a CO's CO altrenate full body sprite
   /*public static Image getCOFullBodyAlt(int id){
       return altFullBodyCOs[id];
    }*/
    
    //returns the Silo Cursor
    public static Image getSiloCursor(){
        return siloCursor;
    }
    
    //returns the question mark for hidden hp
    public static Image getHiddenHP(){
        return hiddenHP;
    }
    
    //returns the capture icon
    public static Image getCaptureIcon(){
        return captureIcon;
    }
    
    //returns the load icon
    public static Image getLoadIcon(){
        return loadIcon;
    }
    
    //returns the dive icon
    public static Image getDiveIcon(){
        return diveIcon;
    }
    
    //returns the low ammo icon
    public static Image getLowAmmoIcon(){
        return lowAmmoIcon;
    }
    
    //returns the low fuel icon
    public static Image getLowFuelIcon(){
        return lowFuelIcon;
    }
    
    //returns the cursor
    public static Image getCursor(){
        return cursor[Options.getCursorIndex()];
    }
    
    //returns the minimap sheet
    public static Image getMinimap(){
        return minimap;
    }
    
    //returns a rain tile
    public static Image getRain(int id){
        return rain[id];
    }
    
    //returns a snow tile
    public static Image getSnow(int id){
        return snow[id];
    }
    
    //returns a sandstorm tile
    public static Image getSand(int id){
        return sand[id];
    }
    
    //returns the detect icon
    public static Image getDetectIcon(){
        return detect;
    }
    
    //returns a given army's day start background
    public static Image getDayStart(int id){
        return dayStartScreens[id];
    }
    
    //returns the victory screen sky
    public static Image getSky(){
        return sky;
    }
    
    //returns the victory screen win bar
    public static Image getWin(){
        return win;
    }
    
    //returns the victory screen lose bar
    public static Image getLose(){
        return lose;
    }
    
    //returns the victory screen days bar
    public static Image getDays(){
        return days;
    }
    
    //returns the endgame statistics box
    public static Image getStatsBox(){
        return statsBox;
    }
    //returns the skill icon
    public static Image getSkillIcon(){
        return skill;
    }
    //returns the power icon
    public static Image getPowerIcon(){
        return COP;
    }
    //returns the SCOP icon
    public static Image getSuperIcon(){
        return SCOP;
    }
    public static Image getHitIcon(){
        return hit;
    }
    public static Image getMissIcon(){
        return miss;
    }
    
    public static Image getArrow(int index){
        return arrow[index];
    }
    
    public static Image getBattleBackground(){
        return battleBackground;
    }
    public static Image getIntelBackground(){
        return intelBackground;
    }
}
