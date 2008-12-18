package com.customwars.ui.menu;
/*
 *BattleMenu.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 14, 2006, 1:54 AM
 *The main menu in a battle. Used to get intel, set the options, save the game, and end the turn.
 */

//TEMPORARY
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.Battle;
import com.customwars.CWEvent;
import com.customwars.Options;
import com.customwars.SFX;
import com.customwars.officer.COList;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.Animation;
import com.customwars.ui.DialogueBox;
import com.customwars.ui.MiscGraphics;

import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class BattleMenu extends InGameMenu{
    Battle b;   //Holds the battle this menu is used for
	final static Logger logger = LoggerFactory.getLogger(BattleMenu.class); 
    //constructor
    public BattleMenu(Battle battle, boolean COP, boolean SCOP, boolean tag, boolean swap, boolean end, ImageObserver screen){
        //super((256-96)/2,(192-80)/2);
        //super((256-96)/2,(162-96)/2);
        //super((256-96)/2,(162-112)/2);
        super((480-96)/2,(320-128)/2,96,screen);
        
        b = battle;
        
        String[] s = new String[11]; //max in one menu is 10
        s[0] = "CO";
        s[1] = "Intel";
        s[2] = "Options";
        s[3] = "Save";
        int i = 4;
        if(!Options.snailGame){
            s[i] = "Load";
            i++;
        }
        if(Options.isNetworkGame()){
            if(Options.getSend())s[5] = "Send On";
            else s[i] = "Send Off";
            i++;
        }
        if(COP){s[i]="COP";i++;}
        if(SCOP){s[i]="SCOP";i++;}
        if(tag){s[i]="Tag Break";i++;}
        if(swap){s[i]="Swap";i++;}
        if(end){s[i]="End";i++;}
        
        String[] s2 = new String[i];
        for(int j=0;j<i;j++)s2[j]=s[j];
        super.loadStrings(s2);
    }
    
    public int doMenuItem(){
    	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
    	
        SFX.playClip(soundLocation + "/ok.wav");
        switch(item){
            case 0:
            	logger.info("CO Dossier");
                return 12;
            case 1:
            	logger.info("Intel");
                return 1;
            case 2:
            	logger.info("Options");
                return 2;
            case 3:
                return 13;
            default:
                if(displayItems[item].equals("End")){
                	logger.info("End");
                    boolean endGame = b.endTurn();
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(6,b.getDay(),b.getTurn()));
                    if(endGame)return -10;
                    else return 10;
                }else if(displayItems[item].equals("Load")){
                    return 14;
                }else if(displayItems[item].equals("COP")){
                    int[] red = {255};
                    int[] green = {255};
                            int[] blue = {255};
                    int offset = 0;
                    if(b.getArmy(b.getTurn()).getCO().isAltCostume())offset = 225;
                    
                    DialogueBox taunt = new DialogueBox(b,b.getArmy(b.getTurn()).getCO().getCOPower()[b.getRNG().nextInt(6)]);
                  
                    /*Animation boxUp = new RectangleBox(b,4, red, green, blue, 0,480,0,255,480,300,100,100,50,0,0);
                    Animation box = new RectangleBox(b,4, red, green, blue, 0,255,0,255,480,300,100,100,80,0,0);*/
                    Animation COmovein;
                    Animation COfadeout;
                    
                    Animation fadeinbackground = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0, 0,100,25,0,-8);//1 second fade in
                    Animation background = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0, 100,100,100,0,0);//1 second
                    Animation fadeoutbackground = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0,100,0,25,0,0);//.25 second fade out
                    
                    if(!b.getArmy(b.getTurn()).getCO().isAltCostume()) {
                        COmovein = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),2,0,0,0, 0, 225, 350,480,0,320,0,100,100,60,25,-8);//.25 second move in
                        COfadeout = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),2,0,0,0, 0, 225, 350,320,0,-100,0,100,100,30,0,8);//.25 second move out
                    } else {
                        COmovein = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),2,0,0,0,225, 0, 450, 350,0,320,0,100,100,60,25,-8);//.25 second move in
                        COfadeout = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),2,0,0,225, 0, 450, 350,320,0,-100,0,100,100,30,0,8);//.25 second move out
                    }

                    taunt.setup();
                    
                    fadeinbackground.setup(true,false);
                    background.setup(true,false);
                    fadeoutbackground.setup(true,false);
                    
                    fadeinbackground.addLock();
                    background.addLock();
                    fadeoutbackground.addLock();
                    
                    COmovein.setup(true,false);
                    COfadeout.setup(true,false);
                    
                    
                    COmovein.linkTo(COfadeout);
                    
                    
                    background.linkTo(fadeoutbackground);
                    fadeinbackground.linkTo(background);
                    
                    taunt.linkTo(fadeinbackground);
                    taunt.linkTo(COmovein);
                    Character c = new Character(' ');
                    for(int i = 0; i<b.getArmy(b.getTurn()).getCO().getCOPName().length(); i++) {
                        //Each letter lasts for a second.
                        if(!c.isWhitespace(b.getArmy(b.getTurn()).getCO().getCOPName().charAt(i))) {
                            Animation intemp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 0,100,5,50+i*10,0);
                            Animation temp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 100,100,50,0,0);
                            Animation outtemp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 100,0,5,0,0);
                            
                            intemp.setup(true,false);
                            temp.setup(true,false);
                            outtemp.setup(true,false);
                            
                            temp.linkTo(outtemp);
                            intemp.linkTo(temp);
                            
                            taunt.linkTo(intemp);
                        }
                    }

                    taunt.start();
                    
                    logger.info("COP");
                    b.getArmy(b.getTurn()).getCO().activateCOP();
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(2,b.getDay(),b.getTurn()));
                }else if(displayItems[item].equals("SCOP")){
                    DialogueBox taunt = new DialogueBox(b,b.getArmy(b.getTurn()).getCO().getCOPower()[b.getRNG().nextInt(6)]);
                    
                    Animation COmovein;
                    Animation CO;
                    Animation COfadeout;
                    
                    Animation fadeinbackground = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0, 0,100,50,0,-8);//1 second fade in
                    Animation background = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0, 100,100,b.getArmy(b.getTurn()).getCO().getSCOPName().length()*9,0,0);//1 second
                    Animation fadeoutbackground = new Animation(b,MiscGraphics.getPowerBackground(b.getTurn()),3,0,0,0,0,480,320,0,0,0,0,100,0,25,0,0);//.25 second fade out
                    //(1+1+0.25)
                    Animation fallPower = new Animation(b,MiscGraphics.getSuperPowerIcon(b.getTurn()),2,0,0,0,0,480,180,0,-165,0,77, 100,100,30,30,-5);//half-second delay, .5 sec fade in
                    Animation sustainPower = new Animation(b,MiscGraphics.getSuperPowerIcon(b.getTurn()),2,0,0,0,0,480,180,0,77,0,77, 100,100,15,0,0);//0.5 second
                    Animation fadePower = new Animation(b,MiscGraphics.getSuperPowerIcon(b.getTurn()),2,0,0,0,0,480,180,0,77,0,77,100,0,15,0,0);//.25 second fade out
                    
                    if(!b.getArmy(b.getTurn()).getCO().isAltCostume()) {
                        COmovein = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,0, 0, 225, 350,480,0,320,0,100,100,60,75,-8);//.25 second move in
                        CO = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,0, 0, 225, 350,320,0,320,0,100,100,b.getArmy(b.getTurn()).getCO().getSCOPName().length()*5,0,0);//1.5 second stay put
                        COfadeout = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,0, 0, 225, 350,320,0,320,0,100,0,30,0,8);//.25 second move out
                    } else {
                        COmovein = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,0,225, 0, 450, 350,0,320,0,100,100,60,75,-8);//.25 second move in
                        CO = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,0, 225, 0, 450, 350,0,320,0,100,100,b.getArmy(b.getTurn()).getCO().getSCOPName().length()*5,0,0);//1.5 second stay put
                        COfadeout = new Animation(b,MiscGraphics.getCOSheet(COList.getIndex(b.getArmy(b.getTurn()).getCO())),1,0,0,225, 0, 450, 350,320,0,320,0,100,0,30,0,8);//.25 second move out
                    }
                    taunt.setup();
                    fadeinbackground.setup(true,false);
                    background.setup(true,false);
                    fadeoutbackground.setup(true,false);
                    
                    fallPower.setup(true,false);
                    sustainPower.setup(true,false);
                    fadePower.setup(true,false);
                    
                    COmovein.setup(true,false);
                    CO.setup(true,false);
                    COfadeout.setup(true,false);
                    
                    fallPower.addLock();
                    sustainPower.addLock();
                    fadePower.addLock();
                    
                    fadeinbackground.addLock();
                    background.addLock();
                    fadeoutbackground.addLock();
                    
                    CO.linkTo(COfadeout);
                    COmovein.linkTo(CO);
                    
                    sustainPower.linkTo(fadePower);
                    fallPower.linkTo(sustainPower);
                    
                    background.linkTo(fadeoutbackground);
                    fadeinbackground.linkTo(background);
                    
                    Character c = new Character(' ');
                    for(int i = 0; i<b.getArmy(b.getTurn()).getCO().getSCOPName().length(); i++) {
                        //Each letter lasts for a second.
                        if(!c.isWhitespace(b.getArmy(b.getTurn()).getCO().getSCOPName().charAt(i))) {
                            Animation intemp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getSCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 0,100,5,125+i*9,0);
                            Animation temp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getSCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 100,100,50,0,0);
                            Animation outtemp = new Animation(b,MiscGraphics.getPowerFONT(b.getArmy(b.getTurn()).getCO().getSCOPName().charAt(i)),1,0,0,0,0,16,32,30+i*16,160,30+i*16,160, 100,0,5,0,0);
                            
                            intemp.setup(true, false);
                            temp.setup(true, false);
                            outtemp.setup(true, false);
                            
                            
                            temp.linkTo(outtemp);
                            intemp.linkTo(temp);
                            
                            taunt.linkTo(intemp);
                        }
                    }
                    taunt.linkTo(fallPower);
                    taunt.linkTo(fadeinbackground);
                    taunt.linkTo(COmovein);
                    
                    taunt.start();
                    logger.info("SCOP");
                    b.getArmy(b.getTurn()).getCO().activateSCOP();
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(3,b.getDay(),b.getTurn()));
                }else if(displayItems[item].equals("Tag Break")){
                	logger.info("Tag Break");
                    b.getArmy(b.getTurn()).tagBreak();
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(4,b.getDay(),b.getTurn()));
                }else if(displayItems[item].equals("Swap")){
                	logger.info("Swap");
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(5,b.getDay(),b.getTurn()));
                    /*if(b.getArmy(b.getTurn()).canTagSwap()){
                        b.getArmy(b.getTurn()).swap();
                        //refresh army, etc.
                    }else{*/
                        b.getArmy(b.getTurn()).swap();
                        boolean endGame = b.endTurn();
                        if(endGame)return -10;
                        else return 10;
                    //}
                }else if(displayItems[item].equals("Send On") || displayItems[item].equals("Send Off")){
                    Options.toggleSend();
                }
        }
        return 0;
    }
    
    public static BattleMenu generateContext(Battle battle, ImageObserver screen){
        boolean COP = false, SCOP = false, tag = false, swap = false, end = false;
        if(battle.getArmy(battle.getTurn()).getCO().canCOP() && !battle.getArmy(battle.getTurn()).getCO().isCOP() && !battle.getArmy(battle.getTurn()).getCO().isSCOP() && battle.getArmy(battle.getTurn()).getCO().getCOPStars() != -1)
            COP = true;
        if(battle.getArmy(battle.getTurn()).getTag() > 0 ){
            if(battle.getArmy(battle.getTurn()).canTagSwap())
                swap = true;
            else
                end = true;
        }else if(battle.getArmy(battle.getTurn()).canTag()){
            swap = true;
            //COP = true;
            SCOP = true;
            tag = true;
            end = true;
        }else if(battle.getArmy(battle.getTurn()).getCO().canSCOP()){
            if(battle.getArmy(battle.getTurn()).getAltCO()!= null)swap = true;
            //COP = true;
            SCOP = true;
            end = true;
        }else if(battle.getArmy(battle.getTurn()).getCO().canCOP() && !battle.getArmy(battle.getTurn()).getCO().isCOP() &&!battle.getArmy(battle.getTurn()).getCO().isSCOP()){
            if(battle.getArmy(battle.getTurn()).getAltCO()!= null)swap = true;
            //COP = true;
            end = true;
        }else if(battle.getArmy(battle.getTurn()).getAltCO()!= null && !battle.getArmy(battle.getTurn()).getCO().isCOP()&&!battle.getArmy(battle.getTurn()).getCO().isSCOP()){
            swap = true;
            end = true;
        }else{
            end = true;
        }
        
        return new BattleMenu(battle,COP,SCOP,tag,swap,end,screen);
    }
}
