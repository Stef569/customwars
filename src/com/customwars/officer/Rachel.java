package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*/*
 *Rachel.java
 *Author:
 *Contributors: Kosheh, Kanon the Blue Ferret, Adam Dziuk
 *Creation:
 *The Rachel class is used to create an instance of the Orange Star CO Rachel (copyright Intelligent Systems).
 */

public class Rachel extends CO{
   
    //constructor
    public Rachel() {
        name = "Rachel";
        id = 6;

        String CObiox = "Brings a breath of fresh air to her troops. Strives to follow in the footsteps of her older sister, Nell. Led the Allied Nations during the Omega war.";             //Holds the condensed CO bio'
        String titlex = "Rocket Girl";
        String hitx = "Hard Work"; //Holds the hit
        String missx = "Excuses"; //Holds the miss
        String skillStringx = "Her troops are quite hardworking, and they increase repairs by one on properties.";
        String powerStringx = "Has a chance to strike with more damage than expected. Lucky!"; //Holds the Power description
        String superPowerStringx = "Launches three missiles from Orange Star HQ in Omega Land. "; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Rachel has enhanced repair speeds on" +
                        "her properties and no real weakness." +
                        "She's like her sister and can become" +
                        "very lucky during her power, while  " +
                        "her super fires 3 missiles at the   " +
                        "enemy army.                         ";//Holds CO intel on CO select menu, 6 lines max
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Nell","Jake","Ozzy","Artemis","Sasha","Koal","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Windfall","Orange Crush","Explosion","Ray of Hope","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,2,1,2,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,120,105,115,105,65,90}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;    
       
        String[] COPowerx =
        {"I will protect this land to the end!",
        "This time, I'm serious!",
        "You don't have a chance!",
        "I'm going to send you packing!",
        "Don't pick a fight with me!",
        "Finally, some real competition!"};
       
        String[] Victoryx =
        {"Another one down... Who's next?",
         "I never give up.",
         "Don't take me lightly just because I'm cute!"};
       
        String[] Swapx =
        {"Get on the ready line!",
        "Where are you looking? You're fighting ME!"};
               
        String[] defeatx =
        {"You beat me! Hmmm... Not bad!",
         "You've got to be kidding me!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
       
        COPName = "Lucky Lass";
        SCOPName = "Covering Fire";
        COPStars = 3.0;
        maxStars = 6.0;
        repairHp = 3;
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
        if(army.getBattle().getBattleOptions().isBalance()==true)setPositiveLuck(30);
        else setPositiveLuck(40);
        }

//carries out Nell's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        int s = 3;
        if(army.getBattle().getBattleOptions().isBalance()==true)
          s = 2;
        army.getBattle().getMap().doInfExplosion(army, 2, s, false, army.getSide());
        army.getBattle().getMap().doAutoExplosion(army, 2, s, false, army.getSide());
        army.getBattle().getMap().doHPExplosion(army, 2, s, false, army.getSide());
        }
       
//used to deactivate Nell's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        setPositiveLuck(10);
    }
   
//used to deactivate Nell's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        setPositiveLuck(10);
    }
}