package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;


/*
 *Nell.java
 *Author: Kosheh, Paul Whan, Adam Dziuk
 *Contributors:
 *Creation:
 *The Nell class is used to create an instance of the Orange Star CO Nell (copyright Intelligent Systems).
 */

public class Nell extends CO{
    
//constructor
    public Nell() {
        name = "Nell";
        id = 3;

        String CObiox = "Rachel's older sister and supreme commander of the Orange Star army, Nell is an able commanding officer with a superb sense of fashion.";             //Holds the condensed CO bio'
        String titlex = "Lady Luck";
        String hitx = "Willful students"; //Holds the hit
        String missx = "Downtime"; //Holds the miss
        String skillStringx = "Sometimes strikes with slightly more force than expected. She's the first to tell you she was born lucky.";
        String powerStringx = "Has a chance to strike with more damage than expected. Lucky!"; //Holds the Power description
        String superPowerStringx = "Improves her chance to strike with massive damage. Very lucky!"; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Nells units are lucky, which means  " +
                        "they may randomly deal more damage  " +
                        "than expected.  Her powers increase " +
                        "the luck range higher beyond the day" +
                        "to day, she may become incredibly   " +
                        "lucky!";//Holds CO intel on CO select menu
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Rachel","Andy","Max","Sami","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Windfall","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,105,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Luck IS a skill!",
         "Hmm... Time to get serious!",
         "I'm just getting started!",
         "Don't hate me just because I'm lucky!",
         "Everything will work out!",
         "I'm feelin' lucky!"};
        
        String[] Victoryx =
        {"Did I go too far?",
         "Lady luck was with me!",
         "...And that's how it's done."};
        
        String[] Swapx =
        {"I hope I get lucky...",
         "Let's get down to business!"};
        
        String[] defeatx =
        {"Seems like I just wasn't lucky enough...",
         "Congratulations! You've beaten me!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Lucky Star";
        SCOPName = "Lady Luck";
        COPStars = 3.0;
        maxStars = 6.0;
        setPositiveLuck(15);
        setNegativeLuck(0);
        this.army = army;
        style = ORANGE_STAR;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP||SCOP)
            return 110;
        return 100;
    }
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
//carries out Nell's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
                //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            setPositiveLuck(40);
        }
        //DS Stats
        else
            setPositiveLuck(60);
    }
    
//carries out Nell's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if(army.getBattle().getBattleOptions().isBalance()== true){
            setPositiveLuck(80);
        }
        //DS Stats
        else
        setPositiveLuck(100);
        
    }
    
//used to deactivate Nell's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        setPositiveLuck(15);
    }
    
//used to deactivate Nell's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        setPositiveLuck(15);
    }
}