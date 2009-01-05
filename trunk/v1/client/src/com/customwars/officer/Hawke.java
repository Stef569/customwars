package com.customwars.officer;

import com.customwars.unit.Army;
import com.customwars.unit.Unit;

/*
 *Hawke.java
 *Author: Adam Dziuk and Kosheh
 *Contributors:
 *Creation:
 *The Hawke class is used to create an instance of the Black Hole CO Hawke (copyright Intelligent Systems).
 */

public class Hawke extends CO{
    
    //constructor
    public Hawke() {
        name = "Hawke";
        setId(22);
        
        String CObiox = "A commander of the Black Hole army who will stop at nothing to achieve his goals.";             //Holds the condensed CO bio'
        String titlex = "Predator";
        String hitx = "Black Coffee"; //Holds the hit
        String missx = "Incompetence"; //Holds the miss
        String skillStringx = "All units possess superior firepower. However, his CO power gauge is longer than it should be.";
        String powerStringx = "All enemy units suffer one HP of damage. In addition, all allied units recover one HP."; //Holds the Power description
        String superPowerStringx = "All enemy units suffer two HP of damage. In addition, all allied units recover two HP."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Hawke has very strong attack for all" +
                        "units, but his powers take longer to" +
                        "charge. Both powers focus on dealing" +
                        "mass damage to enemy troops while   " +
                        "simultaneously repairing his own.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Andy","Eagle","Drake","Jess","Lash","Kindle"}; //Names of COs with special tags
        String[] TagNamesx = {"Shaky Alliance","Dual Strike","Dual Strike","Dual Strike","Rebel Yell","Dual strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0,1,0}; //Number of stars for each special tag.
        int[] TagPercentx = {105,70,90,90,110,80}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        
        String[] COPowerx =
        {"This is the end.",
         "Farewell.",
         "I give you credit for pushing me this far.",
         "You have inspired me to take action.",
         "To be defeated is to lose everything. Are you prepared for that outcome?" ,
         "You leave me no choice."};
        
        String[] Victoryx =
        {"Will you sacrifice all? Then perhaps you can win.",
         "Sad...you were no match for me.",
         "There is no chance.  My victory was assured."};
        
        String[] Swapx =
        {"Playtime is over.",
         "Your defeat is inevitable."};
        
        String[] defeatx =
        {"That's it then. We shall withdraw.",
         "... This is one situation I never expected myself to be in."} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Black Wave";
        SCOPName = "Black Storm";
        COPStars = 5.0;
        maxStars = 9.0;
        this.army = army;
        style = BLACK_HOLE;
        
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 120;
        return 110;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null)
                if(!u[i].isInTransport())u[i].heal(10);
            else
                break;
        }
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport())u[s].damage(10, false);
                    } else
                        return;
                }
            }
        }
    }
    
    //carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null)
                if(!u[i].isInTransport())u[i].heal(20);
            else
                break;
        }
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport())u[s].damage(20, false);
                    } else
                        return;
                }
            }
        }
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
