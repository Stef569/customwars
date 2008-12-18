package com.customwars.officer;
/*
 *Melanthe
 *Author: Albert Lai
 *Creator: Deoxy Knight
 *One of the few COs that Deoxy approves of. Wonder why? :3
 * "You reap what you sow!"
 * "Melanthe doesn't sow!"
 * "Popsicle stand!"
 */

import java.util.Random;

import com.customwars.map.location.Wood;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Melanthe extends CO{
    boolean sustainCOP = false;
    boolean sustainSCOP = false;
    //constructor
    public Melanthe() {
        name = "Melanthe";
        setId(44);
        
        String CObiox = "A genius scientist that harbors a great hatred for humankind. Believes plants to be superior life forms.";             //Holds the condensed CO bio'
        String titlex = "Pod Person";
        String hitx = "Botany"; //Holds the hit
        String missx = "Bovines"; //Holds the miss
        String skillStringx = "Effects of natural terrain are increased by one star. However, repairs on properties are halved.";
        String powerStringx = "Units on natural terrain restore three HP of health. Effects of natural terrain are increased by two stars."; //Holds the Power description
        String superPowerStringx = "Enemies on natural terrain suffer two HP of damage. In addition, allied units on natural terrain restore one HP of health. Movement costs increase for enemies."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Melanthe benefits more from natural " +
                        "terrain defense but has inferior    " +
                        "repairing skills.  Her powers focus " +
                        "on using the environment to boost   " +
                        "her own units while her super also  " +
                        "cripples the movement of enemies.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] COPowerx =
        {"Biological warfare is overrated; botanical warfare is supreme.",
         "The power of nature is on my side!" ,
         "'Blades of grass' has a new meaning now, doesn’t it?" ,
         "Don't breathe in... heh heh heh." ,
         "Ground up the enemy into fertilizer!",
         "Don't have allergies? You will now."
        };
        
        String[] Victoryx =
        {"The seeds of success have been sown.",
         "The enemy was a lawn, and I its mower.",
         "Now that... that was fun."
        };
        
        
        String[] Swapx =
        {"It’s about time.",
         "I can do this myself!"
        };
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Lash", "Falcone", "Jugger", "Von Bolt", "Kindle", "Koal"}; //Names of COs with special tags
        String[] TagNamesx = {"Earthly Delight", "Omnivore", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,2,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,120,80,80,80,80}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Synthetic Seedlings";
        SCOPName = "Agent Dreadroot";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLACK_HOLE;
        repairHp = 1;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP||COP) return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        setTerrainDefenseMultiplier(1);
        if(army.getBattle().getMap().find(defender).getTerrain().getName().equals("Wood") || army.getBattle().getMap().find(defender).getTerrain().getName().equals("Plain") || army.getBattle().getMap().find(defender).getTerrain().getName().equals("Mountain")||army.getBattle().getMap().find(defender).getTerrain().getName().equals("Reef"))
            setTerrainDefenseMultiplier(((army.getBattle().getMap().find(defender).getTerrain().getDef() + 1.0)/army.getBattle().getMap().find(defender).getTerrain().getDef()));
        //Increases terrain stars by one on Plains, Reefs, Woods, and Mountains.
        if(COP)
            if(army.getBattle().getMap().find(defender).getTerrain().getName().equals("Wood") || army.getBattle().getMap().find(defender).getTerrain().getName().equals("Plain") || army.getBattle().getMap().find(defender).getTerrain().getName().equals("Mountain")||army.getBattle().getMap().find(defender).getTerrain().getName().equals("Reef"))                setTerrainDefenseMultiplier(((army.getBattle().getMap().find(defender).getTerrain().getDef() + 2.0)/army.getBattle().getMap().find(defender).getTerrain().getDef()));
        //Increases terrain stars by two under effects of COP
        if(SCOP || COP)return 110;
        return 100;
    }
    
    /*
    public void enemyDayStart(boolean main) 
    {
        double[] temp = (new Wood()).move;
        if(sustainSCOP) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //move[] 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1 && temp[s] != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, temp[s]);
                    }
                    
                }
            }
        }
    }
    
    public void enemyDayEnd(boolean main ){
        double[] temp = (new Wood()).move;
        if(sustainSCOP) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //Used to be just restore cost
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1 && temp[s] != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, -temp[s]);
                    }
                }
            }
        }
    }
    */
    
    public void dayStart(boolean main ){
        if(sustainSCOP) {
            sustainSCOP = false;
            /*    for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++)
                {
                    for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++)
                    {
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().restoreCost();;
                    }
                }*/
        }
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
            Unit[] u = army.getUnits();
            {
                for(int i = 0; i < u.length; i++){
                    if(u[i].getClass() != null) {
                        if(army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Wood") || army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Plain") || army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Mountain")||army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Reef")) {
                            if(!u[i].isInTransport())
                                u[i].heal(30);
                        }
                    }else{
                        return;
                    }
                }
            }
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        sustainSCOP = true;
        
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Wood") || army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Plain") || army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Mountain")||army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Reef"))
                    u[i].heal(10);
            }else{
                return;
            }
        }
        
        Army[] armies = army.getBattle().getArmies(); //Get all armies
        for(int i = 0; i < armies.length; i++)
        {
        	//For all enemy armies
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null)
            {
                double[] temp = (new Wood()).getMove();
                
                for(int q = 0; q < temp.length; q++)
                {
                	if(temp[q] <= 0)
                		temp[q] = 0;
                }
                
                armies[i].addTerrCosts_global(temp);
            	
                u = armies[i].getUnits();
                
                for(int s = 0; s < u.length; s++)
                {
                    if(u[s].getClass() != null)
                    {
                        if(army.getBattle().getMap().find(u[s]).getTerrain().getName().equals("Wood") || army.getBattle().getMap().find(u[s]).getTerrain().getName().equals("Plain") || army.getBattle().getMap().find(u[s]).getTerrain().getName().equals("Mountain")||army.getBattle().getMap().find(u[s]).getTerrain().getName().equals("Reef"))
                        {
                            if(u[s].getArmy().getSide()!=army.getSide())
                            {
                                if(!u[s].isInTransport()) 
                                {
                                	u[s].damage(10, false);
                                }
                            }
                        }
                    } 
                    else
                    {
                        return;
                    }
                }
            }
        }
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP()
    {
        SCOP = false;
        
        double[] temp = (new Wood()).getMove();
        
        for(int i = 0; i < temp.length; i++)
        {
        	if(temp[i] <= 0)
        		temp[i] = 0;
        	else
        		temp[i] *= -1;
        }

        Army[] armies = army.getBattle().getArmies(); //Get all armies
        for(int i = 0; i < armies.length; i++)
        {
        	//For all enemy armies
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null)
            {
                armies[i].addTerrCosts_global(temp);
            }
        }
    }
}
