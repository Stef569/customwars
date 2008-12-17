package com.customwars.officer;

import com.customwars.Battle;
import com.customwars.CO;
import com.customwars.Property;
import com.customwars.Unit;
 /*
  *Colin.java
  *Author: Adam Dziuk
  *Contributors: Kosheh
  *Creation:
  *The Colin class is used to create an instance of the Blue Moon CO Colin (copyright Intelligent Systems).
  */

public class Colin extends CO{
    
    int temp; //used to store damage for Power of Money
    
    //constructor
    public Colin(Battle bat) {
        name = "Colin";
        id = 9;
        
        String CObiox = "Blue Moon's rich boy CO and Sasha's little brother. A gifted CO with a sharp, if insecure, mind.";             //Holds the condensed CO bio'
        String titlex = "Richie-Rich";
        String hitx = "Olaf and Grit"; //Holds the hit
        String missx = "Black Hole"; //Holds the miss
        String skillStringx = "The heir to a vast fortune who can purchase units at bargain-basement prices. Troops' low firepower stems from his lack of confidence.";
        String powerStringx = "Increases current funds by 20 percent."; //Holds the Power description
        String superPowerStringx = "Uses wealth to increase the strength of units. The more funds available, the more firepower his units receive."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Colin lacks the hard hitting power  " +
                "of most COs, but he gets his units  " +
                "at a reduced cost.  His power boosts" +
                "his current funds while his super   " +
                "takes his funds and converts them to" +
                "offensive power.";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Sasha","Olaf","Grit","Von Bolt","Lash"}; //Names of COs with special tags
        String[] TagNamesx = {"Trust Fund","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,105,105,90,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Let me show you the power of money!",
         "This is not a drill... I won't give up so easily!",
         "People of Blue Moon!  I need your help!",
         "I'll give it everything I've got!",
         "You're not getting away with this!",
         "I'll show you what I can do!"};
        
        String[] Victoryx =
        {"I w-won! Whew!",
         "I'll win if I try my best!",
         "Whew... I won! I really won!"};
        
        String[] Swapx =
        {"Let me do the buying!",
         "I'll give it my all!"};
        
        String[] defeatx =
        {"If only Commander Olaf were here...",
         "Wait till I tell my sis! Then you'll be sorry!"} ;
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        if (bat.getBattleOptions().isBalance()== true)
            costMultiplier = 90;
        else
            costMultiplier = 80;
        
        COPName = "Gold Rush";
        SCOPName = "Power of Money";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = BLUE_MOON;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)
            return temp;
        if(COP)
            return 100;
        return 90;
        
    }
    
    public void setChange(Unit u){
        u.setPrice(costMultiplier*u.getPrice()/100);
    }
    
    public void unChange(Unit u){
        u.setPrice(100*u.getPrice()/costMultiplier);
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Kanbei's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        if(army.getBattle().getBattleOptions().isBalance()) {
            Property[] prop = army.getProperties();
            for(int s = 0; s < prop.length ; s++) {
                //Then, add 40% of funds to Colin
                army.addFunds(2*army.getBattle().getBattleOptions().getFundsLevel()/5);
            }
        }else{
            int o =army.getFunds();
            o /= 2;
            army.addFunds(o);
        }
    }
    
//carries out Kanbei's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if (army.getBattle().getBattleOptions().isBalance()== true){
            temp = army.getFunds()/400;
            temp += 100;} else {
            temp = army.getFunds()/300;
            temp += 100;}
    }
    
//used to deactivate Kanbei's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Kanbei's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}