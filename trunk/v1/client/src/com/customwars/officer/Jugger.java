package com.customwars.officer;

import com.customwars.ai.Battle;
import com.customwars.unit.Unit;

/*
 *Jugger.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *The Jugger class is used to create an instance of the Black Hole CO Jugger (copyright Intelligent
 
Systems).
 */

public class Jugger extends CO{
    
//constructor
    public Jugger(Battle bat) {
        name = "Jugger";
        setId(24);
        
        String CObiox = "A robot-like commander with the Black Hole army. No one knows his true identity!";             //Holds the condensed CO bio'
        String titlex = "I, Robot";
        String hitx = "Energy"; //Holds the hit
        String missx = "Static electricity"; //Holds the miss
        String skillStringx = "Units may suddenly deal more damage than expected, but units' firepower is inherently low.";
        String powerStringx = "There is a chance he might get a stronger blow, but base firepower is slightly reduced."; //Holds the Power description
        String superPowerStringx = "There is a chance he might get a devestating blow, but firepower is reduced."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Jugger's firepower is naturally low." +
                        "However, he randomly strikes for    " +
                        "additional damage. Powers decrease  " +
                        "firepower and increase luck.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Koal","Kindle"}; //Names of COs with special tags
        String[] TagNamesx = {"Power Surge","Fireworks"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Enemy: Prepare for mega hurtz.",
         "Memory: upgraded. Shell: shined. Ready to uh...roll.",
         "Enemy system purge initiated...",
         "Blue screen of death!",
         "Crushware loaded...",
         "Approaching system meltdown.",};
        
        String[] Victoryx =
        {"Victory; downloading party hat.",
         "Victory dance initiated.",
         "Jugger; superior. Enemy; lame."};
        
        String[] Swapx =
        {"All systems, go.",
        "Jugger rebooted. Ready to smash."};
        
        String[] defeatx =
        {"Critical Error: Does not compute.",
         "Victory impossible! Units overwhelmed. Jugger must... Control-Alt-Delete."} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Overclock";
        SCOPName = "System Crash";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLACK_HOLE;
        
        if (bat.getBattleOptions().isBalance() == true){
        setPositiveLuck(30);
        }
        else
        {
        setPositiveLuck(30);
        setNegativeLuck(15);
        }

    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)
            return 75;
        if(COP)
            return 80;
        return 85;
    }
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
//carries out Jugger's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
        setPositiveLuck(65);
        }
        //DS Stats
        setPositiveLuck(55);
        setNegativeLuck(25);
    }
    
//carries out Jugger's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
        setPositiveLuck(110);
        }
        //DS Stats
        setPositiveLuck(95);
        setNegativeLuck(45);
        
    }
    
//used to deactivate Jugger's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        setPositiveLuck(30);
        setNegativeLuck(15);
    }
    
//used to deactivate Jugger's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        setPositiveLuck(30);
        setNegativeLuck(15);
    }
}