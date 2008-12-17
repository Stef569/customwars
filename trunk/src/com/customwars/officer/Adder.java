package com.customwars.officer;

import com.customwars.Battle;
import com.customwars.CO;
import com.customwars.Unit;
/*
 *Andy.java
 *Author: Adam Dziuk
 *Contributors: Kosheh
 *Creation: July 4, 2006, 10:17 PM
 *The Adder class is used to create an instance of the Black Hole CO Adder (copyright Intelligent Systems).
 */

public class Adder extends CO{
    
//Constructor
public Adder(Battle bat) {
        name = "Adder";
        id = 21;

        String CObiox = "A self-absorbed commander who believes his skills are matchless. Second to Hawke in rank.";             //Holds the condensed CO bio'
        String titlex = "Snake Eyes";
        String hitx = "His own face"; //Holds the hit
        String missx = "Dirty things"; //Holds the miss
        String skillStringx = "Adept at making quick command decisions, his CO power gauge is shorter than it should be.";
        String powerStringx = "Movement range for all units is increased by one space."; //Holds the Power description
        String superPowerStringx = "Movement range for all units is increased by two spaces. Firepower rises."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Adder can get his power gauge filled" +
                        "quicker than other COs.  Both of his" +
                        "powers enhance movement abilities.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Flak","Koal","Lash","Hawke"}; //Names of COs with special tags
        String[] TagNamesx = {"Totally Flaked","Creepy Crawly","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {105,110,105,105}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Heh heh heh... kneel before Adder!",
        "Heh heh heh... it only hurts for a moment.",
        "The look of terror on your face... It's absolutely delicious.",
        "Heh heh heh... I'm going to enjoy breaking you!",
        "Heh heh heh... Can't a guy have a little fun!?",
        "Wriggle, worm...wriggle!"};
        
        String[] Victoryx =
        {"Heh heh heh... What did you expect?",
        "My apologies. Should I have gone easier on you?",
        "Don't forget me now. That would be a shame."};
        
        
        String[] Swapx =
        {"Hee hee hee! This is gonna be good...",
        "My venom courses through your veins."};
        
        String[] defeatx =
        {"Hssss! Today was... unlucky. A bad day. Nothing more.",
         "It's the blasted weather! That was the problem! Hssss!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Sideslip";
        if (bat.getBattleOptions().isBalance()== true)
            SCOPName = "Snakebite";
        else
            SCOPName = "Sidewinder";
        COPStars = 2.0;
        maxStars = 5.0;
        this.army = army;
        style = BLACK_HOLE;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP)
                return 130;
            if (COP)
                return 110;
            else    
                return 100;}
        //DS Mode Stats
        if(COP||SCOP)
            return 110;
        else return 100;}
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Mode Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(COP || SCOP)
                return 110;
            return 100;
        }
        //DS Mode Stats
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
        public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].setMove(u[i].getMove() + 1);
                u[i].setChanged(true);
            }
            else
                return;
        }
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].setMove(u[i].getMove() + 2);
                u[i].setChanged(true);}
            else
                return;
        }
    }    
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
           COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                u[i].setMove(u[i].getMove() - 1);}
            else
                return;
    }
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                u[i].setMove(u[i].getMove() - 2);}
            else
                return;
        }
    }
}