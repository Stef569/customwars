package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */

public class Blandie extends CO{
    
    //constructor
    public Blandie() {
        name = "Blandie";
        id = 34;

        String CObiox = "This CO joined the military and rose    " +
                        "through the ranks. But not quickly      " +
                        "enough, and is now a sub-commander.";             
        //This is seperated into blocks 40 characters long! 
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Just Wanna Be Average";
        String hitx = "Promotion"; //Holds the hit
        String missx = "Potato peeling"; //Holds the miss
        String skillStringx = "Due to lack of experience, this commander is equally proficient with all units.";
        String powerStringx = "Firepower and defense rises slightly."; //Holds the Power description
        String superPowerStringx = "Firepower and defense rises slightly. Movement is also increased by one for all units."; //Holds the Super description
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] COPowerx =
        {"Move out!",
         "Attack!",
         "Forward march!",
         "Onward to victory!",
         "Never surrender!",
         "Push forward!"};
        
        String[] Victoryx =
        {"Mission complete.",
         "Another day, another battle won.",
         "Maybe I'll be up for promotion soon..."};
        
        String[] Swapx =
        {"I won't let you down!",
         "I am assuming command"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Nell"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0}; //Number of stars for each special tag.
        int[] TagPercentx = {100}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Rally!";
        SCOPName = "Moment of Glory";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)return 120;
        if(COP)return 110;
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
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        //+1 move
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                    u[i].setMove(u[i].getMove() + 1);
                    u[i].setChanged(true);
            }
            else
                return;
        }
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        //-1 move
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].isChanged()){
                    u[i].setMove(u[i].getMove() - 1);
                    u[i].setChanged(false);
                }
            }
            else
                return;
        }
    }
}
