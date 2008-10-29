package cwsource;
/*
 *ERIC.java
 *Author: Albert Lai
 *Contributors:
 *Creation:
 *I HATE CHOKES MUCHLY
 *TO CRUSH THEM IS MY INTENT
 *YAA-AAA-AAA-AAA-AAGH!!
 */

import java.util.Random;

public class Eric extends CO{
    
//constructor
    public Eric() {
        name = "Eric";
        id = 42; //placeholder
        
        String CObiox = "Yukio's enforcer. Strong and loves to muscle people into doing what he wants. Very determined and will run over anything and anyone that stands in his way.";             //Holds the condensed CO bio'
        String titlex = "All Out of Bubble Gum";
        String hitx = "Muscles"; //Holds the hit
        String missx = "Pansies"; //Holds the miss
        String skillStringx = "All units receive an attack boost while within the range of an enemy indirect unit.";
        String powerStringx = "Launches a missile to a point of Yukio's choosing."; //Holds the Power description
        String superPowerStringx = "Strength increases when engaging enemies in groups. The more units near the target, the more damage he deals."; //Holds the Super description
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
        
        String[] TagCOsx = {"Yukio", "Max", "Flak", "Grit", "Adder", "Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Criminal Intent","Old Rivals","Steriods","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,90,90,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        
        String[] COPowerx =
        {"Your frontlines are flatlined!",
        "Hide all you want, I'll still hit you.",
        "It's time to knock a few heads together.",
        "You won't like me when you're angry!",
        "I'm on a rampage!",
        "And you spend so long to set that up..."  };
        
        String[] Victoryx =
        {"Sometimes I even amaze myself!",
        "Consider yourself busted!",
        "The bigger they are, the harder they fall!" };
        
        
        String[] Swapx =
        {"You guys are pushovers!",
        "You think you can block me?" };
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Bombardment";
        SCOPName = "Strategic Strike";
        COPStars = 3.0; //3/7
        maxStars = 7.0;
        this.army = army;
        style = AMBER_CORONA;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(defender == null){
            if(SCOP || COP)return 110;
            return 100;
        }
        Unit[] u = defender.getArmy().getUnits();
        int store = 0;
        int store2 = 0;
        int t = 0;
        
        for(int i = 0; i < u.length; i++)
        {
                t = Math.abs(attacker.getLocation().getRow() - u[i].getLocation().getRow()) + Math.abs(attacker.getLocation().getCol() - u[i].getLocation().getCol());
                if((u[i].getMinRange() != 1) && t<=u[i].getMaxRange() && t >= u[i].getMinRange())
                    store ++;
                    //If the unit is ranged, and has this unit in its target zone
                //Might as well check for SCOP stuff now too.
                t = Math.abs(defender.getLocation().getRow() - u[i].getLocation().getRow()) + Math.abs(defender.getLocation().getCol() - u[i].getLocation().getCol());
                if(t==1)
                    store2++;
        }
        if(SCOP)
            return 110 + 10*store + store2*30;
        if(COP)
            return 110 + 10*store;
        else 
            return 100 + 10 * store;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        Unit[] u = defender.getArmy().getUnits();
        int store= 0;
        int t = 0;
        
        for(int i = 0; i < u.length; i++)
        {
                //Might as well check for SCOP stuff now too.
                t = Math.abs(attacker.getLocation().getRow() - u[i].getLocation().getRow()) + Math.abs(attacker.getLocation().getCol() - u[i].getLocation().getCol());
                if(t==1)
                    store++;
        }
        if(SCOP)
            return 110 + store*20;
        if(COP)
            return 110;
        else
            return 100;
    }
    
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        selecting = true;
        //fire ze missiles!
    }
    
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void selectAction(Tile t)
    {
        army.getBattle().getMap().doExplosion(2,3,t.getLocation().getCol(),t.getLocation().getRow(),false);
        selecting = false;
    }
    public boolean validSelection(Tile t)
    {
        if(army.getBattle().getMap().onMap(t.getLocation()))
            return true;
        return false;
    }
    public void invalidSelection() //IF they hit the wrong area, try again!
    {
        selecting = true;
    }
    public void cancelSelection() //If they press B
    {
        //YOU WASTED THE COP
        selecting = false;
    }
}
