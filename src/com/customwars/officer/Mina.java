package com.customwars.officer;

/*
 *Hawke.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: legoman727
 *The Hawke class is used to create an instance of the Parrallell Galaxy CO Mina.
 */

import java.util.Vector;

import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Mina extends CO{
    
    //constructor
    public Mina() {
        name = "Mina";
        setId(29);
        
        String CObiox = "A young enigmatic girl who serves Parallel Galaxy. Deceptively dangerous.";             //Holds the condensed CO bio'
        String titlex = "Pale Moon";
        String hitx = "Victory"; //Holds the hit
        String missx = "Uncertainty"; //Holds the miss
        String skillStringx = "Mina's unstable emotional state results in no strengths or weaknesses for all types of units.";
        String powerStringx = "Unit's defenses may unexpectedly rise."; //Holds the Power description
        String superPowerStringx = "Most expensive enemy units suffer five HP of damage."; //Holds the Super description
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
        String[] TagCOsx = {"Dreadnaught", "Artemis", "Graves", "Julia", "Sophie", "Zeke", "Noah"}; //Names of COs with special tags
        String[] TagNamesx = {"Requiem of Darkness ","Kinship/'s Bond","Desecrate","Dual Strike","Dual Strike","Dual strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,110,105,105,90,85,85}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        
        String[] COPowerx =
        {"Enemy is in range. Activating counter-measures...",
         "Please stop fighting! I don't want to do this!",
         "Stop! ... ... Ignore that outburst, activate the weapons.",
         "The target will be eliminated shortly.",
         "Your defeat is now certain.",
         "I'm sorry..."};
        
        String[] Victoryx =
        {"No... What... ... ... The battle is over.",
         "What have I done?",
         "The enemy has been routed. It is done." };
        
        String[] Swapx =
        {"Very well.",
         "I will take over, now." };
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        COPName = "Potent of Misfortune";
        SCOPName = "Dark Lightning";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = PARALLEL_GALAXY;
        
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide()){
                armies[i].getCO().setNegativeLuck(armies[i].getCO().getNegativeLuck() + 50);
            }
        }
    }
    
    
    
    //carries out Mina's Super CO Power, called by CO.activateSCOP()
    //Gets list of all enemy units, sorts by current price, damages top 20% for 50 damage
    //Rounds up on the amount of units
    //All Enemy Units
    public void superCOPower(){
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Vector v = new Vector();
        Unit[] temp;
        int allUnits;
        for(int t = 0; t < armies.length; t++){
            if(armies[t] != null && armies[t].getSide() != army.getSide()){
                
                temp = armies[t].getUnits();
                if(temp != null)
                    for(int x = 0; x < temp.length; x++)
                        v.add(temp[x]);
            }}
        int i = v.size();
        Unit[] all = new Unit[i];
        for(int p = 0; p < v.size(); p++)
            all[p] =(Unit) v.get(p);
        
        sort(all);
        
        for(int t = all.length-1; t > Math.floor(((all.length -1)/5.0) * 4.0); t--){
            all[t].damage(50, false);
        }
        
    }
    
        
    private static int newGap(int gap) {
        gap=gap*10/13;
        if(gap==9||gap==10)
            gap=11;
        if(gap<1)
            return 1;
        return gap;
    }
    
    private static void sort(Unit a[]) {
        int gap=a.length;
        boolean swapped;
        do {
            swapped=false;
            gap=newGap(gap);
            for(int i=0;i<a.length-gap;i++) {
                if(a[i].getValue()>a[i+gap].getValue()) {
                    swapped=true;
                    Unit temp=a[i];
                    a[i]=a[i+gap];
                    a[i+gap]=temp;
                }
            }
        } while(gap>1||swapped);
    }
    
    
    //used to deactivate Andy's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide()){
                armies[i].getCO().setNegativeLuck(armies[i].getCO().getNegativeLuck() - 50);
            }
        }
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //used to deactivate Andy's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;

    }
}

