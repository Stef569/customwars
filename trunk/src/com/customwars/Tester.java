package com.customwars;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */

import java.util.Random;

import com.customwars.officer.CO;
import com.customwars.unit.Unit;

public class Tester extends CO{
    
    //constructor
    public Tester() {
        name = "Blandie";
        setId(34);
        
        String[] COPowerx =
        {"Move Out!",
         "Attack!",
         "Forward March!",
         "Onward To Victory!",
         "Never Surrender!",
         "Push Forward!"};
        
        String[] Victoryx =
        {"Mission Complete.",
         "Another day, another battle won.",
         "Victory is sweet."};
        
        String[] Swapx =
        {"I won't let you down",
         "I am assuming command"};
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Nell"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0}; //Number of stars for each special tag.
        int[] TagPercentx = {100}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
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
