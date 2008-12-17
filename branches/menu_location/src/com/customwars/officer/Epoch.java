package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*
 *Epoch.java
 *Author:Adam Dziuk
 *Contributors:Justin Gregory, All the little people.
 *Creation: Xenesis
 *The Epoch class is used to create an instance of the PG CO Epoch (Made by Xenesis)
 */

public class Epoch extends CO{
    
//constructor
    public Epoch() {
        name = "Epoch";
        id = 30;
        
        String CObiox = "The first prototype unit of Jugger. Crude in design and technology, the power systems of Epoch are extremely prone to short circuiting, resulting in bursts of energy and instability.";             //Holds the condensed CO bio'
        String titlex = "Whars Beta";
        String hitx = "Tests"; //Holds the hit
        String missx = "Upgrades"; //Holds the miss
        String skillStringx = "Epoch is blessed with simple programming so it has only basic command abilities. Powers charge at an unmatched speed.";
        String powerStringx = "Firepower is increased and unit HP is hidden from opponents."; //Holds the Power description
        String superPowerStringx = "Direct combat units gain one movement and indirect combat units recieve one extra range. Hides HP from opponents. Firepower is increased."; //Holds the Super description
                     //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Jugger","Koal","Lash","Andy","Flak"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike","Dual Strike","Dual Strike","Dual Strike","High Voltage"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,0,0,0,1}; //Number of stars for each special tag.
        int[] TagPercentx = {70,90,105,105,110}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Error: Overcharged. initiate saftey mode.",
         "Power surge detected.",
         "Overload! Release Energy!",
         "System Over. Overload equals very yes.",
         "Blackout!",
         "Register your firmware for continued use!",};
        
        String[] Victoryx =
        {"Epoch Succesful. Upgrade irrelevant",
         "Test Completed. Anaylsing battle log...",
         "Victory recorded in database and filed under category 'insulting'" };
        
        String[] Swapx =
        {"Compiling database for a shocking performance",
         "Tactics upgraded, only time remains."};
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Short Circuit";
        SCOPName = "Shocker";
        COPStars = 1.0;
        maxStars = 5.0;
        setPositiveLuck(10);
        setNegativeLuck(0);
        this.army = army;
        style = PARALLEL_GALAXY;
    }
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP)
            return 120;
        if(SCOP)
            return 130;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP)
            return 120;
        if(SCOP)
            return 130;
        return 100;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        hiddenHP = true;
    }
    
    //carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        hiddenHP = true;
        Unit[] u = army.getUnits();
        if(u != null)
            for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].getMinRange() > 1 && u[i].getUType() > 1){
                    u[i]
							.setMaxRange(u[i].getMaxRange() + 1);
                    u[i].setChanged(true);
                }else{
                    u[i].setMove(u[i].getMove() + 1);
                    u[i].setChanged(true);
                }
            } else
                return;
            }
    }
    
    
    //used to deactivate Andy's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        hiddenHP = false;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //used to deactivate Andy's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        hiddenHP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                if(u[i].getMinRange() > 1 && u[i].getUType() > 1)
                    u[i]
							.setMaxRange(u[i].getMaxRange() - 1);
                else
                    u[i].setMove(u[i].getMove() - 1);
                u[i].setChanged(false);
            } else
                    return;
            
        }
    }
}