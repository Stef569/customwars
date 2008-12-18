package com.customwars.ai;
/*
 *BattleOptions.java
 *Author: Urusan
 *Contributors:
 *Creation: September 15, 2006, 11:25 PM
 *Battle-specific options, such as Fog of War and Income per Property
 */

import java.io.*;

public class BattleOptions implements Serializable{
    private boolean fog;            //is Fog of War on?
    private boolean mist;           //is Mist of War on?
    
    private int weather;            //what type of weather? 0=Clear 1=Rain 2=Snow 3=Sandstorm 4=Random
    private int funds;              //funds per property
    private int turnLimit;          //what is the turn limit? 0 for infinite
    private int captureLimit;       //how many captured properties to win? 0 for infinite
    private boolean coPowers;       //are CO Powers on?
    private boolean balance;        //is balance mode on?
    private boolean recordReplay;   //should a replay be recorded?
    private int startFunds;         //starting funds for each player
    private boolean unitBans[] = new boolean[BaseDMG.NUM_UNITS];   //the list of unit bans true = banned
    private boolean aiControl[] = new boolean[10]; //which armies are AI controlled?
    private int snowchance;         //Chance of snow
    private int rainchance;         //Chance of Rain
    private int sandchance;         //Chance of sandstorm
    private int minweatherday;             //Earliest day weather can occur
    private int minweathertime;             //min days a random weather will go for
    private int maxweathertime;             //max days a random weather will go for
        
    //constructor
    public BattleOptions(){
        fog = false;
        mist = false;        
        weather = 0;        
        funds = 1000;
        turnLimit = 0;
        captureLimit = 0;
        coPowers = true;
        balance = Options.isBalance();
        recordReplay = Options.isRecording();
        snowchance = 8; //Chance of Weather per day. (All armies turns = day) 1 = 1% Range is 0 to 100%
        rainchance = 8;
        sandchance = 8;
        minweatherday = 1; //Minimum is Day 1. Weather won't occur on days before this.
        minweathertime = 1;
        maxweathertime = 1; //Must be greater than or equal to minweathertime 
        if(Options.defaultBans==5){
            for(int i = 0; i < BaseDMG.NUM_UNITS; i++){
                unitBans[i] = true;
            }
        }else{
            for(int i = 0; i < BaseDMG.NUM_UNITS; i++){
                unitBans[i] = false;
            }
            if(Options.defaultBans==0){
                //CW Bans
                unitBans[31] = true;    //Oozium banned by default
            }else if(Options.defaultBans==1){
                //AWDS Bans
                for(int i = 25; i < BaseDMG.NUM_UNITS; i++){
                    unitBans[i] = true;
                }
            }else if(Options.defaultBans==2){
                //AW2 Bans
                for(int i = 19; i < BaseDMG.NUM_UNITS; i++){
                    unitBans[i] = true;
                }
            }else if(Options.defaultBans==3){
                //AW1 Bans
                for(int i = 18; i < BaseDMG.NUM_UNITS; i++){
                    unitBans[i] = true;
                }
            }
        }
    }
    
    //Is Fog of War on?
    public boolean isFog()
    {
        return fog;    	
    }
    
    //set fog of war
    public void setFog(boolean fow)
    {
    	fog = fow;
    }
    
    //Is Mist of War on?
    public boolean isMist()
    {
    	return mist;
    }
    
    //set mist of war
    public void setMist(boolean mow)
    {
    	mist = mow;
    }
    
    //What are the weather settings?
    public int getWeatherType(){
        return weather;
    }
    
    //set the weather settings
    public void setWeatherType(int w){
        weather = w;
    }
    
    //What is the funds multiplier?
    public int getFundsLevel(){
        return funds;
    }
    
    //set the funds multiplier
    public void setFundsLevel(int f){
        if(f >= 0)funds = f;
    }
    
    //What is the turn limit?
    public int getTurnLimit(){
        return turnLimit;
    }
    
    //set the turn limit
    public void setTurnLimit(int t){
        turnLimit = t;
    }
    
    //What is the cap limit?
    public int getCapLimit(){
        return captureLimit;
    }
    
    //set the cap limit
    public void setCapLimit(int c){
        captureLimit = c;
    }
    
    //Are CO Powers on?
    public boolean isCOP(){
        return coPowers;
    }
    
    //set CO Powers
    public void setCOP(boolean cop){
        coPowers = cop;
    }
    
    //Is Balance Mode on?
    public boolean isBalance(){
        return balance;
    }
    
    //set balance
    public void setBalance(boolean bal){
        balance = bal;
    }
    
    //Is replay recording on?
    public boolean isRecording(){
        return recordReplay;
    }
    
    //set recording
    public void setReplay(boolean rep){
        recordReplay = rep;
    }
    
    public int getStartFunds() {
        return startFunds;
    }
    
    //set the starting funds
    public void setStartFunds(int s) {
        if(s >= 0) {
            startFunds = s;
        }
    }
    
    public boolean isUnitBanned(int i){
        return unitBans[i];
    }
    
    public void setUnitBanned(boolean newset, int unit){
        unitBans[unit] = newset;
    }
    
    public void setAIControl(boolean b, int army){
        aiControl[army] = b;
    }
    
    public boolean isAIControl(int army){
        return aiControl[army];
        
    }
    public int getSnowChance(){
        return snowchance;
    }
   
    public int getRainChance(){
        return rainchance;
    }
   
    public int getSandChance(){
        return sandchance;
    }

    public int getMinWDay(){
        return minweatherday;
    }
    public int getMinWTime(){
        return minweathertime;
    }
   
    public int getMaxWTime(){
        return maxweathertime;
    } 
    public void setSnowChance(int value){
        snowchance = value;
    }
   
    public void setRainChance(int value){
        rainchance= value;
    }
   
    public void setSandChance(int value){
         sandchance= value;
    }

    public void setMinWDay(int value){
         minweatherday= value;
    }
    public void setMinWTime(int value){
         minweathertime= value;
    }
   
    public void setMaxWTime(int value){
         maxweathertime= value;
    } 
}
