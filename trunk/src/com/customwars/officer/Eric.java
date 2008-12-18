package com.customwars.officer;
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
import java.util.ArrayList;

import com.customwars.map.Tile;
import com.customwars.map.location.Terrain;
import com.customwars.unit.Unit;

public class Eric extends CO{
    ArrayList<Terrain> destroyed;
//constructor
    public Eric() {
        name = "Eric";
        setId(42); //placeholder
        
        String CObiox = "Yukio's enforcer. Strong and loves to muscle people into doing what he wants. Very determined and will run over anything and anyone that stands in his way.";             //Holds the condensed CO bio'
        String titlex = "All Out of Bubble Gum";
        String hitx = "Muscles"; //Holds the hit
        String missx = "Pansies"; //Holds the miss
        String skillStringx = "All units receive an attack boost while within the immediate firing range of an enemy unit.";
        String powerStringx = "Launches a missile to a point of Yukio's choosing."; //Holds the Power description
        String superPowerStringx = "After Eric destroys and enemy, movement over that tile falls to zero."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Eric is a foe of anyone who relies  " +
                        "on stalling the enemy to win. His   " +
                        "units gain power if they are able to" +
                        "be hit by the enemy. His COP fires  " +
                        "a missile, and his SCOP allows units" +
                        "to advance through a front quickly.";//Holds CO intel on CO select menu, 6 lines max
        
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
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        
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
        
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
        
        COPName = "Bombardment";
        SCOPName = "Strategic Strike";
        COPStars = 4.0; //3/7
        maxStars = 7.0;
        this.army = army;
        style = AMBER_CORONA;
        
        destroyed = new ArrayList<Terrain>();
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
        
        for(int i = 0; i < u.length; i++) {
            t = Math.abs(attacker.getLocation().getRow() - u[i].getLocation().getRow()) + Math.abs(attacker.getLocation().getCol() - u[i].getLocation().getCol());
            if((u[i].getMinRange() != 1) && t<=u[i].getMaxRange() && t >= u[i].getMinRange())
                store++;
            //If the unit is ranged, and has this unit in its target zone
            if(u[i].checkFireRange(attacker.getLocation()))
            {
            if((u[i].getMoveType() != u[i].MOVE_INFANTRY && u[i].getMoveType() != u[i].MOVE_MECH) || COP || SCOP)
                store++;
            }
            //If the enemy can hit this unit.
            //Might as well check for SCOP stuff now too.
        }
        
        if(SCOP)
            return 110 + 15*store;
        if(COP)
            return 110 + 5*store;
        else
            return 100 + 5 * store;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        
        if(COP || SCOP)
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
        for(int i = 0; i<destroyed.size(); i++) {
            for(int s = 0; s<10; s++) {
                if(destroyed.get(i).moveCost(s) != -1) {
                    destroyed.get(i).addCost(s, destroyed.get(i).getBaseMove()[s]);}
            }
        }
        destroyed.clear();
    }
    public void selectAction(Tile t) {
        army.getBattle().getMap().doExplosion(2,3,t.getLocation().getCol(),t.getLocation().getRow(),false);
        selecting = false;
    }
    public boolean validSelection(Tile t) {
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
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(attack && destroy && SCOP) {
            destroyed.add(army.getBattle().getMap().find(enemy).getTerrain());
            army.getBattle().getMap().find(enemy).getTerrain().newMoveSet(new double[] {0.00,0.00,0.00,0.00,0.00,-1,-1,0.00,-1,0.00});
        }
    }
}
