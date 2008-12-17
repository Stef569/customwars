package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;

/*
 *Flakb.java
 *Author: Adam Dziuk, Kosheh
 *Contributors: 
 *Creation:
 *The Flak class is used to create an instance of the Black Hole CO Flak (copyright Intelligent Systems) with balanced stats (By Paul Whan).
 */

public class Flakb extends CO{
   
    //constructor
    public Flakb() {
        name = "Flak";
        id = 19;
        
        String[] TagCOsx = {"Adder","Lash"}; //Names of COs with special tags
        String[] TagNamesx = {"Totally Flaked","Bruise Cruise"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {105,110}; //Percent for each special tag. 
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        String[] COPowerx =
        {"You can't hide from me! I'll hunt you down like a dog!",
        "Brute force!",
        "Grr! Now you've really made me angry!",
        "Nnnggrr! Braaaawrr! Smaarrrrgh!",
        "You're gonna get pummeled!",
        "I'll hit you 'til you lose your lunch!",};
       
        String[] Victoryx =
        {"I've still got what it takes!",
         "Graaa! Blaarrgggg! Smaaarrgggghhh!!",
         "Enough pussyfooting around!"};
        
        String[] Swapx =
        {"I'll crush you all!",
        "Let me at 'em!"}  ;
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
       
        COPName = "Brute Force";
        SCOPName = "Barbaric Blow";
        COPStars = 3.0;
        maxStars = 6.0;
        setPositiveLuck(25);
        setNegativeLuck(10);
        this.army = army;
        style = BLACK_HOLE;
    } 
    
        //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP||SCOP)
            return 110;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
        public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
         setPositiveLuck(50);
        setNegativeLuck(20);
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
         setPositiveLuck(90);
        setNegativeLuck(40);

    }    
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
           COP = false;
           setPositiveLuck(25);
        setNegativeLuck(10);
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        setPositiveLuck(25);
        setNegativeLuck(10);
    }
}