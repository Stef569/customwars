package com.customwars.officer;

import com.customwars.unit.Army;
import com.customwars.unit.Unit;

/*
 *Drake.java
 *Author:
 *Contributors: Kosheh
 *Creation:
 *The Drake class is used to create an instance of the Green Earth CO Drake (copyright Intelligent Systems).
 */

public class Drake extends CO{
    
    //constructor
    public Drake() {
        name = "Drake";
        setId(12);
        
        String CObiox = "A bighearted former pirate who hates fighting. Also a great surfer.";             //Holds the condensed CO bio'
        String titlex = "Nautical Nightmare";
        String hitx = "The sea"; //Holds the hit
        String missx = "High places"; //Holds the miss
        String skillStringx = "Naval units have superior firepower, but air units have reduced defenses.";
        String powerStringx = "Causes a tidal wave that does one HP of damage to all enemy units."; //Holds the Power description
        String superPowerStringx = "Causes a giant tidal wave that does two HP of damage to all enemy units."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Drake dominates the sea with his    " +
                "powerful ships, although his air    " +
                "force is comparatively weak.  Powers" +
                "invoke mass damage among enemy units" +
                "and cut their supplies.  The super  " +
                "leaves a storm in its wake.";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] COPowerx =
        {"Panic is for landlubbers!",
         "Prepare to be washed away!",
         "Time to show you how we do things at sea!",
         "Wave good-bye to your troops!",
         "Can't you just feel the riptide washing you out to open sea?",
         "Drake is taking the helm!",};
        
        String[] Victoryx =
        {"I'm just glad I survived that...",
         "That was some rough sailing!",
         "Blow me down... we finally won."};
        
        String[] Swapx =
        {"Slow down, matey. What's the rush?",
         "I've got to toughen up by the next turn!"};
        
        String[] defeatx =
        {"A job well done! You've beaten me.",
         "Curses! I've been defeated. Time to hoist sail and flee!"} ;
        
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        String[] TagCOsx = {"Eagle","Jess","Hawke","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Stormwatch","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,105,90,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Tsunami";
        SCOPName = "Typhoon";
        COPStars = 4.0;
        maxStars = 7.0;
        this.army = army;
        style = GREEN_EARTH;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(attacker.getMoveType() == attacker.MOVE_SEA){
            if(army.getBattle().getBattleOptions().isBalance()){
                if(COP || SCOP) return 120;
                return 110;
            }else{
                if(COP || SCOP) return 130;
                return 120;
            }
        }else if(attacker.getMoveType() == attacker.MOVE_AIR){
            if(COP || SCOP) return 100;
            return 90;
        }else{
            if(COP || SCOP) return 110;
            return 100;
        }
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(army.getBattle().getBattleOptions().isBalance() && attacker.getMoveType() == attacker.MOVE_SEA){
            if(SCOP || COP)
                return 120;
            return 110;
        }
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Olaf's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        //army.getBattle().startWeather(1,1);
        
        //mass damage
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport()){
                            u[s].damage(10, false);
                            
                            u[s].setGas(u[s].getGas()/2);
                            if(!army.getBattle().getBattleOptions().isBalance())u[s].setAmmo(u[s].getAmmo()/2);
                        }
                    } else
                        return;
                }
            }
        }
    }
    
//carries out Olaf's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        //if(!army.getBattle().getBattleOptions().isBalance())army.getBattle().startWeather(1,1);
        
        
        //mass damage
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport()){
                            u[s].damage(20, false);
                            u[s].setGas(u[s].getGas()/2);
                            if(!army.getBattle().getBattleOptions().isBalance()){
                                u[s].setAmmo(u[s].getAmmo()/2);
                                army.getBattle().startWeather(1,1);
                            }
                        }
                    } else
                        return;
                }
            }
        }
    }
    public void dayEnd(boolean main){
        if(army.getBattle().getBattleOptions().isBalance())
            if(SCOP)
                army.getBattle().startWeather(1,1);
    }
//used to deactivate Olaf's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        
    }
    
//used to deactivate Olaf's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        //if(army.getBattle().getBattleOptions().isBalance())army.getBattle().startWeather(1,1);
    }
}