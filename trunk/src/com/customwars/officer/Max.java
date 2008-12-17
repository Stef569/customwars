package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*
 *Max.java
 *Author: Adam Dziuk, Kosheh
 *Contributors:
 *Creation:
 *The Max class is used to create an instance of the Orange Star CO Max (copyright Intelligent Systems).
 */

public class Max extends CO{
   
    //constructor
    public Max() {
        name = "Max";
        id = 1;

        String CObiox = "A brave and loyal friend, not to mention a strong fighter, Max hates any kind of treachery, preferring a good, old-fashioned brawl. ";             //Holds the condensed CO bio'
        String titlex = "Loyal Combatant";
        String hitx = "Weight Training"; //Holds the hit
        String missx = "Studying"; //Holds the miss
                            //"                                             " sizing markers
        String skillStringx = "Non-infantry direct-combat units are tops.   " +
                              "Indirect-combat troops are reduced in range. ";
        String powerStringx = "Firepower of all non-infantry direct-combat units rises. "; //Holds the Power description
        String superPowerStringx = "Firepower of all non-infantry direct-combat units rises greatly."; //Holds the Super description
        String intelx = "Max is particularly powerful with  " +
                        "direct units. However, all indirect" +
                        "units suffer from reduced range.";//Holds CO intel on CO select menu
                       
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        
        String[] TagCOsx = {"Andy", "Grit", "Nell", "Sami", "Von Bolt"};              //Names of COs with special tags
        String[] TagNamesx  = {"Power Wrench", "Big Country", "Dual Strike", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {1 ,2, 0, 0 ,0};           //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 105, 90};       //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
       
        String[] COPowerx =
        {"Roll, tanks, roll!",
        "Now you're gonna get hurt!",
        "Hey!  Give up while you still can!",
        "Wanna test might?  I won't lose!",
        "That's enough!  Get outta the road!",
        "Alright, the gloves are comin' off."};
       
        String[] Victoryx =
        {"That was a piece of cake!",
         "Ha! It'll take more than that to beat me!",
         "I'm on a roll!"};
        
        
        String[] Swapx =
        {"I'll crush you like a paper cup!",
        "Bring it on!"} ;


        String[] defeatx =
        {"Ouch... I let my guard down.",
        "Oh, man! Not good! What are we supposed to do now!?"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
       
        COPName = "Max Force";
        SCOPName = "Max Blast";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    } 
 //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if(attacker.getMinRange() == 1 && SCOP && attacker.getUType() > 1)
                return 200;
            if(attacker.getMinRange() == 1 && COP && attacker.getUType() > 1)
                return 140;
            if(attacker.getMinRange() == 1 && attacker.getUType() > 1)
                return 120;
            if(SCOP || COP)
                return 110;
            return 100;
        }
        //DS Stats
        if(attacker.getMinRange() == 1 && SCOP && attacker.getUType() > 1)
            return 190;
        if(attacker.getMinRange() == 1 && COP && attacker.getUType() > 1)
            return 160;
        if(attacker.getMinRange() == 1 && attacker.getUType() > 1)
            return 120;
        if(SCOP || COP)
            return 110;
        return 100;
            
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //changes unit for this CO
    public void setChange(Unit u){
        if(u.getMinRange() > 1)
            u.setMaxRange(u.getMaxRange() - 1);
    }
    
    //unchanges unit
    public void unChange(Unit u){
        if(u.getMinRange() > 1)
            u.setMaxRange(u.getMaxRange() + 1);
    }
    
    //carries out Max's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        if(army.getBattle().getBattleOptions().isBalance()== true)
        {
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if(u[i].getMinRange() == 1 && u[i].getUType() > 1) {
                        u[i]
								.setMove(u[i].getMove() + 1);
                        u[i].setChanged(true);
                    }
                } else
                    return;
            }
        }
    }
    
    //carries out Max's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        }    
    
    //used to deactivate Max's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        if(army.getBattle().getBattleOptions().isBalance()== true){
            Unit[] u = army.getUnits();
            if(u != null)
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null && u[i].isChanged())
                {
                    u[i].setMove(u[i].getMove() - 1);
                }
            }
        }
    }
    
    //used to deactivate Max's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        }  
}
   