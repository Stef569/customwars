package com.customwars;
/*
  * Edge.java
  *
  * Created on November 16, 2006, 8:42 PM
  *
  * To change this template, choose Tools | Template Manager
  * Edge's abilities ignore day to day price reductions for both he and the enemy(s)
  * His SCOP, however, factors in his 80% reduction in price only, and not that of mines he may control. (via a variable instead of Cost Multiplier)
  */

public class Edge extends CO{
    
//Constructor
    public Edge() {
        name = "Edge";
        id = 33;
        
        String CObiox = "Loves to live and fight on the edge. He prefers risks and gambles over safety.";             //Holds the condensed CO bio'
        String titlex = "Dared to Fly...And Fell";
        String hitx = "Disadvantages"; //Holds the hit
        String missx = "Cheaters"; //Holds the miss
        String skillStringx = "Edge likes to live on the edge. Units are stronger when engaging stronger units, but firepower is reduced when engaging a weaker unit.";
        String powerStringx = "Units receive a large firepower boost when engaging a stronger unit. "; //Holds the Power description
        String superPowerStringx = "Deployment costs drop and units get first strike when engaging a stronger unit."; //Holds the Super description
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
        
        
//dis guy haz a lot of tags, lol
        String[] TagCOsx = {"Sasha","Grimm","Jared","Colin"}; //Names of COs with special tags
        String[] TagNamesx = {"Confident Abilities","Daredevil Business ","Forward Drive","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,2,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,105,110,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"I feel a bit dirty for doing this...",
         "I gave you a chance, but you didn't want it.",
         "You gotta live life on the edge!",
         "You were never too good.",
         "You're going to be feeling edgy about this...",
         "C'mon, give the little guys a chance!",
         "What's a little risk taking going to hurt?",  };
        
        String[] Victoryx =
        {"Heh, good game.",
         "Power isn't everything y'know...",
         "Big risk, big reward!" };
        
        
        String[] Swapx =
        {"This game is gonna become a whole lot riskier!",
         "This better not be too easy" };
        //"Ready or not, here I come!" };
        //He doesn't have swap codes, so I made some up.
        //Whoops, overlooked that. >_>
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Eccentricity";
        
        SCOPName = "Tempestous Technique";
        COPStars = 3.0;
        maxStars = 7.0;
        
        this.army = army;
        style = BLUE_MOON;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        if(defender != null){
            if ((attacker.moveType > 1)&&(defender.moveType > 1 )) //If both attacker and defender are not infantry movement
            {
                if (SCOP) {
                    //What's that big line of code do?
                    // The first part tells you the price, disregarding 'small HP' (smaller than 10 on a scale 1-100).
                    // Then the cost multiplier - basically the discount / increased expense - is nullified
                    //getValue also returns the current value of the unit, damage included. I'm not sure Banjo intended that, but it fits the theme.
                    //By the way, there is no costMultiplier nullification because price is constantly recalculated - see the Kanbei code!
                    if(((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier)*4.0/5) > ((defender.price*100.0/defender.getArmy().getCO().costMultiplier)))
                        return 90;
                    else if(((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier)*4.0/5) < ((defender.price*100.0/defender.getArmy().getCO().costMultiplier)))
                        return 130;
                    else
                        return 100;
                } else if (COP) {
                    if((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier) > ((defender.price*100/defender.getArmy().getCO().costMultiplier)))
                        return 90;
                    else if((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier) < ((defender.price*100/defender.getArmy().getCO().costMultiplier)))
                        return 160;
                    else
                        return 100;
                } else {
                    if((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier) > ((defender.price*100.0/defender.getArmy().getCO().costMultiplier)))
                        return 90;
                    else if((attacker.price*100.0/attacker.getArmy().getCO().costMultiplier) < ((defender.price*100.0/defender.getArmy().getCO().costMultiplier)))
                        return 120;
                    else
                        return 100;
                }
            } else
                return 100;
        }
        if(COP||SCOP)return 110;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP) {
            return 110;
        }
        if (COP) {
            if((defender.price*100.0/defender.getArmy().getCO().costMultiplier) < (attacker.price*100.0/attacker.getArmy().getCO().costMultiplier))
                return 130; //If the defending unit is cheaper than the attacking unit's unaltered price
            else
                return 110;
        } else //Return day to day defense
        {
            return 100;
        }
    }
    
    public void setChange(Unit u){
        if(SCOP)
            u.price = costMultiplier*u.price/100;
    }
    
    public void unChange(Unit u){
        if(SCOP)
            u.price = 100*u.price/costMultiplier;
    }
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        costMultiplier = 80;
        
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null)
                u[i].price = costMultiplier*u[i].price/100;
            //        u.price = costMultiplier*u.price/100;
            else
                break;
        }
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
//        u.price = costMultiplier*u.price/100;
        Unit[] u2 = army.getUnits();
        //This resets Edge's cost back to the normal one
        for(int i = 0; i < u2.length; i++){
            if(u2[i].getClass() != null)
                u2[i].price = 100*u2[i].price/costMultiplier;
            else
                break;
        }
        costMultiplier = 100;
    }
    
    public void firstStrikeCheck(Unit owned, Unit enemy, boolean attack)
    {
        if (!attack) //if Edge is defending
        {
            if ((enemy.moveType > 1)&&(owned.moveType > 1 )) {//If the units aren't infantry o_o
                if(SCOP) //if the SCOP is active
                    if(((owned.price*100.0/owned.getArmy().getCO().costMultiplier)*4/5) < ((enemy.price*100.0/enemy.getArmy().getCO().costMultiplier))) {
                    firstStrike = true;}
                if(COP) //if the SCOP is active
                    if(((owned.price*100.0/owned.getArmy().getCO().costMultiplier)) < ((enemy.price*100.0/enemy.getArmy().getCO().costMultiplier))) {
                    counterAttack = 130;}
                //Edge counterattacks first if his units are cheaper than those of his opponenet
                //Currently, beforeAttack is positioned before counterattacks are computed.
            }
        }
        firstStrike = false;  
    }

    //This turns off firstStrike after the unit is done attacking.
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        if(SCOP) //if the SCOP is active
        {firstStrike = false;}//Feh, we dun need check. }
        
        if(COP) //if the SCOP is active
        {counterAttack = 100;}
    }
}