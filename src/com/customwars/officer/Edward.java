package com.customwars.officer;
/*
 *Edward.java
 *Author: Albert Lai
 *Contributors:
 *Creation: December 11, 2006
 *Edward, perhaps a short CO?
 */

import java.util.Random;

import com.customwars.map.location.Property;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Edward extends CO{
   
    //constructor
    public Edward() {
        name = "Edward";
        setId(40);
       
        String CObiox = "A smuggler and mercenary from Green Earth, but was later forced to flee to Amber Corona.";             //Holds the condensed CO bio'
        String titlex = "It Fell Off a Truck";
        String hitx = "Business"; //Holds the hit
        String missx = "Green Earth"; //Holds the miss
        String skillStringx = "No special skills. Equally proficient with all types of units.";
        String powerStringx = "Can immediately issue orders to units that deployed this turn. Deployment prices drop."; //Holds the Power description
        String superPowerStringx = "Steals half of opponents' income. Enemy units on their own properties suffer one HP of damage. Deployment prices drop."; //Holds the Super description
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
        
        String[] COPowerx =
        {"Your defeat shall be delivered soon." ,
        "Pay up!" ,
        "Special delivery!" ,
        "Be patient. This will be over soon." ,
        "Show me the money!" ,
        "I love quick cash!" ,
        };
       
        String[] Victoryx =
        {"Here's the proof of purchase for your beating. Don't lose it!" ,
        "Supply and Demand dictated your defeat. I both supplied and demanded it!" ,
        "Would you like to order something? Today's thrashing was free!" };
        //These quotes are bad. Like...Hachi on a bad day.
       
        String[] Swapx =
        {"Time to cash in!" ,
        "All ready to go." };
        //"We require additional gold!"
       
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
       
        //No special tags
        String[] TagCOsx = {"Colin", "Yukio", "Varlot", "Eagle", "Hachi"}; //Names of COs with special tags
        String[] TagNamesx = {"Business School", "Black Merchants", "Shadowy Monopoly", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110, 100, 85, 80}; //Percent for each special tag.
       
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
       
        COPName = "Smuggled Goods";
        SCOPName = "Diverted Funds";
        COPStars = 4.0;
        maxStars = 6.0;
        this.army = army;
        style = AMBER_CORONA;
    }
   
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP||SCOP)return 110;
        return 100;
       
    }
   
    public void setChange(Unit u){
    }
   
    public void unChange(Unit u){
    }
   
   
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
   
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        int storeamount = 0; //used to store money gained from enemies
        Property[] prop;     
        

        Army[] armies = army.getBattle().getArmies(); //Get all armies
        for(int i = 0; i < armies.length; i++)
        {  //Going through armies
            prop = armies[i].getProperties();
            if(armies[i].getSide() != army.getSide())
            {
                armies[i].getCO().setFunding(50);
                for(int s = 0; s < prop.length ; s++)
                {
                    //Then, add money to Edward:
                    army.addFunds(army.getBattle().getBattleOptions().getFundsLevel()/4); 
                }
            }
        }
    }
   
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        costMultiplier -= 20;
        SCOP = true;

                Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++)
        {
            if(u[i].getClass() != null){
                if(u[i].getCOstore()[0] == 1)
                {
                    u[i].setActive(true);
                    if (army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Base"))
                        ((Property)(army.getBattle().getMap().find(u[i]).getTerrain())).setCreateLand(false);
                    if (army.getBattle().getMap().find(u[i]).getTerrain().getName().equals("Pipestation"))
                        ((Property)(army.getBattle().getMap().find(u[i]).getTerrain())).setCreatePipe(false);
                       
                }
            }
            else
                return;
        }
        

        
        //lookit me, I'm stealiing ur funds
       
    }
   
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Army[] armies = army.getBattle().getArmies(); //Get all armies
        //Property[] prop;
        for(int i = 0; i < armies.length; i++){  //Going throuhg armies
            if(armies[i].getSide() != army.getSide())
            { //If an opponent... then do the following
                armies[i].getCO().restoreFunding();
                if(armies[i].getAltCO()!=null)armies[i].getAltCO().restoreFunding(); //In case they switch COs
                //restores funding to the base amount
            }
        }
       
    }
   
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        costMultiplier += 20;
        SCOP = false;
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main)
    {
        if (index==15)
        {
            u.getCOstore()[0] = 1;
            if(SCOP)
            {
                u.getCOstore()[0] = 1;
                u.setActive(true);
            }
        }
    }
} 