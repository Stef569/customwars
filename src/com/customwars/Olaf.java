package com.customwars;
/*
 *Olaf.java
 *Author: Kosheh
 *Contributors: Urusan
 *Creation:
 *The Olaf class is used to create an instance of the Blue Moon CO Olaf (copyright Intelligent Systems).
 */

public class Olaf extends CO{
   
    //constructor
    public Olaf() {
        name = "Olaf";
        id = 7;
       
        String CObiox = "He may be a pompous braggart, but his tactical prowess has earned him the respect of his peers and the admiration of his people.";             //Holds the condensed CO bio'
        String titlex = "Old Man Winter";
        String hitx = "Warm boots"; //Holds the hit
        String missx = "Rain clouds"; //Holds the miss
        String skillStringx = "Winter poses no problem for Olaf or his troops. Snow causes his firepower to rise, and his troops can move through it without any fuel penalties.";
        String powerStringx = "Causes snow to fall for two days, causing his firepower to rise."; //Holds the Power description
        String superPowerStringx = "A mighty blizzard causes two HP of damage to all enemy troops. The snow will also cause his firepower to rise for two days."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Being used to the harsh weathers of " +
                        "Blue Moon, Olaf's troops are boosted" +
                        "during snow storms.  His powers both" +
                        "cause snow to fall for 2 days while " +
                        "his super also strikes all enemies  " +
                        "for instant mass damage.            ";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] COPowerx =
        {"Oho ho ho.  Do you think your pitiful troops can stand the cold?",
        "Neither man nor machine can withstand the fury of nature!",
        "You're going to regret challenging me!",
        "Let the winds of war bring snow!",
        "I'll bury you!",
        "Your weapons are powerless before the might of nature!"};
       
        String[] Victoryx =
        {"Olaf's troops know no match!",
         "I won! ...That is...we won!",
         "In the end, Olaf stands victorious!"};
         
        String[] Swapx =
        {"I'll teach you the meaning of power!",
        "Finally, my turn!"};
       
        String[] defeatx =
        {"Unbelievable! We've been forced to withdraw? What's going on?",
         "Next time, I will give them a display of true might! Mark my words!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        String[] TagCOsx = {"Grit","Colin","Lash","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Snow Patrol","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,105,80,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        COPName = "Blizzard";
        SCOPName = "Winter Fury";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLUE_MOON;
        
        snowImmunity = true;
    }

    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //snow
        if(army.getBattle().getWeather()==2){
            if(COP || SCOP) return 130;
            return 120;
        }
        
        //otherwise
        if(COP || SCOP) return 110;
        return 100;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Olaf's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        army.getBattle().startWeather(2,2);
    }
    
//carries out Olaf's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        army.getBattle().startWeather(2,2);
        
        //mass damage
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
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
    
//used to deactivate Olaf's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        
    }
    
//used to deactivate Olaf's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        
    }
}
