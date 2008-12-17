package com.customwars.officer;

import com.customwars.Battle;
import com.customwars.CO;
import com.customwars.Unit;
/*
 *Javier.java
 *Author: Raymond Wynn
 *Contributors;
 *Creation:
 *The Javier class is used to create an instance of the Green Earth CO Javier (copyright Intelligent Systems).
 */

public class Javier extends CO{
    
    private int comTowerBoost = 1;  //Javier's ComTower multiplier
    
    //Constructor
    public Javier(Battle bat) {
        name = "Javier";
        id = 14;
        
        String CObiox = "A Green Earth commander who values chivalry and honor above all else. Often orders his units to charge.";             //Holds the condensed CO bio'
        String titlex = "Quixote Incarnate";
        String hitx = "Honor"; //Holds the hit
        String missx = "Retreating"; //Holds the miss
        String skillStringx = "Units possess superior defenses vs. indirect attacks.";
        String powerStringx = "Improves defense vs. indirect attacks and firepower slightly rises."; //Holds the Power description
        String superPowerStringx = "Units have even greater defenses vs. indirect attacks and firepower increases."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Javier has bolstered defense against" +
                        "indirect fire. His powers boost this" +
                        "defense further, while also granting" +
                        "a universal offensive boost.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Jess","Kanbei","Grimm","Sensei","Sasha","Von Bolt"};              //Names of COs with special tags
        String[] TagNamesx = {"Green Flash", "Code of Honor", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {1 ,1, 0, 0 ,0 ,0};           //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 105, 105, 90};       //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Away put your weapons! You shall not be needing them where you are going!",
         "Charge into the toothy maw! For now is the time for a glorious hindspanking!",
         "Mighty spire of communication, imbue my blade with stabby might!",
         "Your maidenfolk shall soon howl great lamentations!",
         "This day shall be the greatest of days, unless tomorrow is even greater!",
         "Foe, you are mad if you seek to pierce my steely hide!"};
        
        String[] Victoryx =
        {"No sharpness can penetrate my steely hide!",
         "Consider your hindquarters righteously spanked!",
         "You still live, for Javier is masterful but merciful!"};
        
        
        String[] Swapx =
        {"Allow me to slap the foe with extreme prejudice!",
         "My armor has the thickness of an iron dragon clad in a steel girdle!"};
        
        String[] defeatx =
        {"Well done! Your reputation is well deserved.",
         "I am honored to have served with the noblest knights history has ever seen."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        if (bat.getBattleOptions().isBalance()== true){
            COPName = "Iron Shield";
            SCOPName = "Royal Guard";
        }
        else
            COPName = "Tower Shield";
            SCOPName = "Tower of Power";
            
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = GREEN_EARTH;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP)
                return 140;
            if (COP)
                return 130;
            else
                return 100;
        }
        //DS Stats
        if(COP||SCOP)
            return 110+getArmy().getComTowers()*(comTowerBoost-1)*10;
        return 100+getArmy().getComTowers()*(comTowerBoost-1)*10;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if(attacker.getMinRange()>1){
                if (SCOP)
                    return 190;
                if (COP)
                    return 150;
                else
                    return 120;
            }
            if (COP||SCOP)
                return 110;
            else
                return 100;
        }
        //DS Stats
        if(attacker.getMinRange()>1){
            if(COP)
                return 150+getArmy().getComTowers()*comTowerBoost*10;
            if(SCOP)
                return 190+getArmy().getComTowers()*comTowerBoost*10;
            return 120+getArmy().getComTowers()*comTowerBoost*10;
        }
        if(COP || SCOP)
            return 110+getArmy().getComTowers()*comTowerBoost*10;
        return 100+getArmy().getComTowers()*comTowerBoost*10;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        if(army.getBattle().getBattleOptions().isBalance()== false)
        comTowerBoost = 2;
        else;
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if(army.getBattle().getBattleOptions().isBalance()== false)
        comTowerBoost = 3;
        else;
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        comTowerBoost = 1;
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        comTowerBoost = 1;
    }
}