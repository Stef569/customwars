package com.customwars.officer;

import com.customwars.Battle;
import com.customwars.unit.Unit;
/*
 *Sturm.java
 *Author: Kosheh, Adam Dziuk
 *Contributors:
 *Creation:
 *The Sturm class is used to create an instance of the Black Hole CO Sturm (copyright Intelligent Systems).
 */

public class Sturm extends CO{
    
    //constructor
    public Sturm(Battle bat) {
        name = "Sturm";
        setId(23);
        
        String CObiox = "The original commander of the Black Hole army. A mysterious invader from another world. Mastermind of the Cosmo and Macro wars.";             //Holds the condensed CO bio'
        String titlex = "Enigma";
        String hitx = "Plotting invasions"; //Holds the hit
        String missx = "Peace"; //Holds the miss
        String skillStringx = "Invasions";
        String powerStringx = "NO SCOP"; //Holds the Power description
        String superPowerStringx = "Pulls a giant meteor from space, which deals 4 HP of damage to all affected units. Firepower and defense is increased."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        //being dead, Sturm lacks tags
        String[] TagCOsx = {"Lash"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0}; //Number of stars for each special tag.
        int[] TagPercentx = {100}; //Percent for each special tag.

        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Prepare to embrace darkness!",
         "You will tremble before my power!",
         "Fear is all you have left...",
         "You shall not survive!",
         "Burning earth!!!",
         "Such power... I regret crushing it.",};
        
        String[] Victoryx =
        {"Who would have thought you could oppose me?",
         "My name is Sturm. Hear it and tremble.",
         "This is but a taste of my power!"};
        
        String[] Swapx =
        {"You will learn to fear my power!",
         "...."};
        
        String[] defeatx =
        {"Gwaaaaaaaaaahhhh! I underestimated the strength of these worms!",
         "NOOOOOOOOOOOOOOOOOOO!!!"} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Meteor Strike";
        SCOPName = "Meteor Strike";
        COPStars = -1;
        maxStars = 10.0;
        if(bat.getBattleOptions().isBalance())
        {
            COPStars = 5.0;
        }
        else
        {
        perfectMovement = true;
        }
        this.army = army;
        style = BLACK_HOLE;
    }
    
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(SCOP)
                return 110;
            if(COP)
                return 100;
            return 90;
        }
        //DS Stats
        if(SCOP || COP)
            return 130;
        return 120;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(SCOP)
                return 135;
            if(COP)
                return 125;
            return 115;
        }
        //DS Stats
        if(COP || SCOP)
            return 130;
        return 120;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        int s = 4;
        army.getBattle().getMap().doAutoExplosion(army, 2, s, false, army.getSide());
    }
    
    //carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        int s = 8;
        army.getBattle().getMap().doAutoExplosion(army, 2, s, false, army.getSide());
    }
    
    //used to deactivate Andy's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //used to deactivate Andy's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}