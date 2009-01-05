package com.customwars.officer;
/*
 *Alexis.java
 *Author: Albert Lai
 *Creation: Juggerv2
 *Fun fact! Juggerv1 was untimely killed in an explosion that involved six ducks, an umbrella, and three dozen Black Bombs!
 */

import javax.swing.JOptionPane;

import com.customwars.map.location.Location;
import com.customwars.map.location.Property;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Alexis extends CO{

    Location storage;
    int t = 0;
    boolean counterAttacking = false;
    int store;
//constructor
    public Alexis() {
        name = "Alexis";
        setId(34); //Testing purposes

        String CObiox = "Talyx's crystal-obsessed older sister. Blindly follows orders to please her brother.";             //Holds the condensed CO bio'
        String titlex = "Crystal Heart";
        String hitx = "Crystals"; //Holds the hit
        String missx = "Experiments"; //Holds the miss
        String skillStringx = "Units adjacent to an allied property restore one HP of health at the beginning of each turn.";
        String superPowerStringx = 
                "Units nearby an allied property receive " +
                "firepower bonuses. Enemies nearby their " +
                "own properties suffer one HP of damage. " +
                "                                        " +
                "Alexis is also able to use Crystal Brace" +
                "                                        " +
                "Units nearby an allied property receive " +
                "offensive and defensive bonuses, and    " +
                "restore three HP of health. Affected    " +
                "units have stronger counterattacks."; //Holds the Power description
        String powerStringx = "Units nearby an allied property receive offensive and defensive bonuses, and restore three HP of health. Affected units have stronger counterattacks."; //Holds the Super description
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
        
        String[] TagCOsx = {"Talyx","Ozzy","Mina","Lash"}; //Names of COs with special tags
        String[] TagNamesx = {"Earthen Fusion","Whole Lotta Love","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,110,105,90}; //Percent for each special tag.
       
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
       
       
        String[] COPowerx =
        {"You'll regret angering me!",
        "It's time you learned the real power of the crystals!",
        "You were probably enjoying this battle, until now!",
        "Witness the power the Black Crystal gave to me!",
        "I hope you're ready for what's coming next!",
        "See, now my army really shines, like diamonds!" ,};
       
        String[] Victoryx =
        {"I'm unbreakable, just like a diamond!",
        "Haha, you were shattered!",
        "I'll leave you to clean this up, 'kay?" };
       
        String[] Swapx =
        {"I'll take it for now. You go have a rest.",
        "Guess it's my time to shine!" };
       
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
       
        COPName = "Crystal Brace"; //This is the one that heals/ increases defense
        SCOPName = "Crystal Edge"; //This is the one that increases offense
        COPStars = -1;
        maxStars = 5.0;
        this.army = army;
        style = PARALLEL_GALAXY;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)
        {
        Property[] prop = army.getProperties();
            for(int i = 0; i < prop.length; i++)
            {
                    storage = prop[i].getTile().getLocation();
                    t = 0;
                    t = Math.abs(attacker.getLocation().getRow() - storage.getRow()) + Math.abs(attacker.getLocation().getCol() - storage.getCol());
                    if(t<4) //If the city is within 3 spaces
                        store = 150;
                    else
                        store = 110;
            }//This ends the city for loop
            return store;
        }
        if(COP)
            if(counterAttacking) //Counterattack boost for certain units.
                return 150;
            else return 110;
        else
            return 100;     
    }
   
    public void setChange(Unit u){};
   
    public void unChange(Unit u){};
   
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender)
    {
        Property[] prop = army.getProperties();
        store = 0;
        if(COP)
        {
            for(int i = 0; i < prop.length; i++)
            {
                    storage = prop[i].getTile().getLocation();
                    t = 0;
                    t = Math.abs(defender.getLocation().getRow() - storage.getRow()) + Math.abs(defender.getLocation().getCol() - storage.getCol());
                    if(t<4) //If the city is within 3 spaces
                        store = 150;
                    else
                        store = 110;
            }//This ends the city for loop
            return store;
        }
        else if (SCOP)
            return 110;
        else
            return 100;
    }
   
    public void dayStart(boolean main)
    {
        if(main)
        {
        Property[] prop = army.getProperties();
        if(prop != null)
            {
            Unit[] u = army.getUnits();
            if(u!=null)
                for(int i = 0; i < u.length; i++)
                {
                    if(u[i].getClass() != null)
                    {
                        for(int itwo = 0; itwo<prop.length; itwo++)
                        {
                            if(u[i].getCOstore()[0] == 0) //Has the unit been healed?
                            {
                            storage = prop[itwo].getTile().getLocation();
                                if(u[i].checkAdjacent(storage))
                                {
                                    if(!u[i].isInTransport())
                                    {
                                        u[i].heal(10);
                                        u[i].getCOstore()[0] = 1; //If it has been healed, pre
                                    }//Checking if unit is in transport
                                }//Checking adjacent cities
                            }//Checking for healing
                        }//Cycling through cities
                    } //Are there more units?
                    else
                        break;
                }//This ends the unit for loop
            }
        }
    }
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Property[] prop = army.getProperties();
        Unit[] u = army.getUnits();
        t = 0;
        if(u!=null)
        for(int i = 0; i < u.length; i++) //Cleans out COstore[0]
            u[i].getCOstore()[0] = 0;
       
       //Healing
        for(int itwo = 0; itwo < u.length; itwo++)
            {
                if(u[itwo].getClass() != null)
                {
                    for(int i = 0; i < prop.length; i++)
                    {
                        if(u[itwo].getCOstore()[0] == 0)
                            {
                            storage = prop[i].getTile().getLocation();
                            t = Math.abs(u[itwo].getLocation().getRow() - storage.getRow()) + Math.abs(u[itwo].getLocation().getCol() - storage.getCol());
                                if(t<4) //If the city is within 3 spaces
                                {
                                    if(!u[itwo].isInTransport())
                                    {
                                        u[itwo].heal(30);
                                        u[itwo].getCOstore()[0] = t;
                                    }//Is the unit within a transport?
                                }//Is it within 3 spaces?
                            }//Is the unit healed?
                        }//cycling through properties
                    }//Are there more units?
               }//Cycling through units
    }
   
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        //choose between powers, period.
        
            String pow = JOptionPane.showInputDialog("Choose which SCOP to use:\n1 = Crystal Brace\n2 = Crystal Edge\nNote: Invalid input assumed to be Crystal Edge");
            if(pow != null && pow.length()>0){
                if(pow.charAt(0) == '1'){
                    COPower();
                    return;
                }
            }
        
        
        //activate SCOP
        SCOP = true;
        Property[] prop = army.getProperties();
        Unit[] u = army.getUnits();
        t = 0;
        //This does the damage
        Army[] armies = army.getBattle().getArmies();
        boolean damaged = false;
        //Bascially, this section code was turned inside out, so the ithrees and itwos are going to be a headache
        //<_<
        for(int ithree = 0; ithree < armies.length; ithree++)
        { //Goes through the armies
            if(armies[ithree].getSide() != army.getSide() && armies[ithree].getUnits() != null)
            { //If hostile
                u = armies[ithree].getUnits(); //gets the units of army being targetted
                prop = armies[ithree].getProperties(); //gets propertise of army being targetted
               
                        for(int ifive = 0; ifive < u.length; ifive++)
                        { //Cycles through units
                            for(int i = 0; i < prop.length; i++)
                            { //Cycles through cities
                                storage = prop[i].getTile().getLocation();
                                t = Math.abs(u[ifive].getLocation().getRow() - storage.getRow()) + Math.abs(u[ifive].getLocation().getCol() - storage.getCol());
                                    if(t<4)
                                    {
                                        u[ifive].damage(10,false);
                                        break; //I hope this breaks the current city for loop
                                    }
                            }
                        }//This ends the unit for loop
                     //This ends the if statement checking cities
            }
        }
    }
   
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
   
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
   
    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean attack)
    {
        Property[] prop = army.getProperties();
        if(!attack) //defending
        {
            if(COP) //If Edge is active
                for(int i = 0; i < prop.length; i++)
                {
                    storage = prop[i].getTile().getLocation();
                    t = 0;
                    t = Math.abs(owned.getLocation().getRow() - storage.getRow()) + Math.abs(owned.getLocation().getCol() - storage.getCol());
                    if(t<4) //If the city is within 3 spaces
                        counterAttacking = true;
                    else
                        counterAttacking = false;
                }//This ends the city for loop
        }
    }
   
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
        //Just to be sure, always turn this off.
                counterAttacking = false;
    }
}
