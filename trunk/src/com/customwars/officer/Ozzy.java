package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*
 *Ozzy.java
 *Author: Albert Lai
 *Contributors:
 *Creation: November 13, 2006
 *This creates a "Ozzy McOzzenheimer" class, etc.
 *
 */

public class Ozzy extends CO{
    int cityStore;
    int playerStore;   
    boolean store; //used for pseudo counterattack reduction during COP
//Constructor
public Ozzy() {
        name = "Ozzy";
        id = 39; //placeholder
       
        String CObiox = "A psychoanalyist who specializes in defensive tactics when he's not playing his guitar. A habitual womanizer.";             //Holds the condensed CO bio'
        String titlex = "Hard ROCK!";
        String hitx = "Mind Games"; //Holds the hit
        String missx = "Waiting"; //Holds the miss
        String skillStringx = "Ozzy's defensive tactics lowers the amount of damage his units take.";
        String powerStringx = "Units gain a modest defense boost. Enemy counterattacks are less effective."; //Holds the Power description
        String superPowerStringx = "Defense rises dramatically. Counterattack strength is doubled."; //Holds the Super description
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
        //dis guy haz a lot of tags, lol
        String[] TagCOsx = {"Alexis","Kosheh","Rachel","Sophie","Dreadnaught","Flak","Epoch"}; //Names of COs with special tags
        String[] TagNamesx = {"Whole Lotta Love","Ramble On","Eruption","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,2,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,110,90,90,80}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        String[] COPowerx =
        {"IT'S BEEN A LONG TIME SINCE I ROCK 'N ROLLED, MMM-HMM!",
        "So when're you gunna start fighting?",
        "Time to unleash the MAGIC...the SPANISH CASTLE magic...",
        "Jet Black, Johnny B...Killer Queen...Dizzy Lizzy, 'n Major Tom...",
        "Let's crank up the gain a little bit, shall we?",
        "You're still there? Guess I'd better start trying." };
        //I don't get *any* of these quotes.
        // "I... wanna rock and roll all fight!" should be a quote. Then again, maybe not.
       
        String[] Victoryx =
        {"Finally. Can I get back to my guitar now?",
        "So nobody ever told you how it was gunna be, eh?",
        "I could say something cliche here, but I think I've done enough already." };
        //The third quote is a classic cop-out unless every other quote is a cliche. Shame shame >:O
       
        String[] Swapx =
        {"..All in my brain...Lately thin-Huh? Wait, it's my turn?",
        "Gunna getcha...Gunna shootchya...Gunna, gunna...gunna pull the trigger!" };
       
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
       
        COPName = "Hammer On";

        SCOPName = "Grand Finale";
        COPStars = 3.0;
        maxStars = 6.0;
        //We should give someone a .5 star COP, just for kicks.
        this.army = army;
        style = AMBER_CORONA; //Change later
    }
   
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
            if (COP||SCOP)
                return 110;
            else   
                return 100;
}
   
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
            if(SCOP)
            {
                return (130+defender.getCOstore()[0]*40);
            }
            else if (COP)
            {
                if (store)
                    return (152 + defender.getCOstore()[0]*10); //This comes out to 60% reduction, on top of the 120 defense.
                                                        //Trust me >_>
                else
                    return (120+defender.getCOstore()[0]*10); //10 point defense boost if the unit has attacked the turn before
            }
            else
            {
                return (100+defender.getCOstore()[0]*10);
            }
    }
   
    public void setChange(Unit u){};
   
    public void unChange(Unit u){};
   
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower()
    {   
        COP = true;
    }

    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){   
        SCOP = true;
        counterAttack = 200;
    }
   
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP()
    {
        store = false; //just in case
        COP = false;
    }

    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
       
    }
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack)
    {
        if (attack)
        {
            owned.getCOstore()[0] = 1;
            if(COP)
                store = true;

        }
        else
        {
            if(SCOP)
                if(owned.getCOstore()[0] == 1) //If the unit had attacked
                    counterAttack = 200;
        }
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
      if (attack)
        if (COP)
        {
          store = false;
        }
      counterAttack = 100;
    }
}