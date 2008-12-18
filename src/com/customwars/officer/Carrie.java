package com.customwars.officer;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */
import java.util.Vector;

import com.customwars.Tile;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;
public class Carrie extends CO{
    int units; //stores enemy units
    int selected;
    //constructor
    public Carrie() {
        name = "Carrie";
        setId(49);
        
        String CObiox =
                "A stubborn young woman from Orange Star." +
                "Carries a hot temper. ";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Saboteur";
        String hitx = "Commando Crisis"; //Holds the hit
        String missx = "Bullies"; //Holds the miss
        String skillStringx =
                "Units receive a firepower boost when    " +
                "attacking an enemy of the same class";
        String powerStringx =
                "Engine performance is put into high     " +
                "gear, allowing her most expensive units " +
                "to receive orders twice. "; //Holds the Power description
        String superPowerStringx =
                "Selected half of opponent's units become" +
                "paralyzed. "; //Holds the Super description
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
        {"Their weakpoint is exposed, hit it for massive damage!",
         "This part is connected to the firing mechanism, so when it breaks...",
         "Mechanical failure in 3, 2, 1...",
         "Hey! They broke my gamegizmo! Oh, I'm mad now!",
         "They never bother to check their parts. Big mistake!",
         "This is nothing compared to level 36!" };
        
        String[] Victoryx =
        {"And now, back to Commando Crisis!",
         "I win, you lose, and your parts suck.",
         "Looks like I threw a monkey wrench into your plans!" };
        
        String[] Swapx =
        {"Time to get to work..",
         "But I was just about to beat the boss!"};
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Koshi", "Jared", "Amy", "Nell"}; //Names of COs with special tags
        String[] TagNamesx = {"Otaku Attack", "Securty Hole", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tag
        int[] TagStarsx = {2,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105, 105, 90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Reboot";
        SCOPName = "Critical Malfunction";
        COPStars = 4.0;
        maxStars = 7.0;
        this.army = army;
        style = JADE_COSMOS;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        int atk = 100;
        //woo, increased attack under COP/SCOP
        if(COP||SCOP) atk += 10;
        if(defender != null){
            //If the attacker and defender are both infantry units
            if((attacker.getMType()==attacker.MOVE_INFANTRY || attacker.getMType()==attacker.MOVE_MECH)&&(defender.getMType()==defender.MOVE_INFANTRY || defender.getMType()==defender.MOVE_MECH))
                atk+=10;
            //If both attacker and defender are "tanks" ie tanks and Battlecrafts
            if((attacker.getUnitType() == 2 || attacker.getUnitType() == 3 || attacker.getUnitType() == 18 || attacker.getUnitType() == 19 || attacker.getUnitType() == 25) && (defender.getUnitType() == 2 || defender.getUnitType() == 3 || defender.getUnitType() == 18 || defender.getUnitType() == 19 || defender.getUnitType() == 25))
                atk+=10;
            //If both attacker and defender are "indirect", excepting B-ship
            if((attacker.getUnitType() == 7 || attacker.getUnitType() == 26 || attacker.getUnitType() == 8 || attacker.getUnitType() == 6) && (defender.getUnitType() == 7 || defender.getUnitType() == 26 || defender.getUnitType() == 8 || defender.getUnitType() == 6))
                atk+=10;
            //If both attacker and defender are "vehicles"
            if((attacker.getUnitType() == 4 || attacker.getUnitType() == 5 || attacker.getUnitType() == 9) && (defender.getUnitType() == 4 || defender.getUnitType() == 5 || defender.getUnitType() == 9))
                atk+=10;
            //If both attacker and defender are copters, or zepplins
            if((attacker.getUnitType() == 14 || attacker.getUnitType() == 15 || attacker.getUnitType() == 28) && (defender.getUnitType() == 14 || defender.getUnitType() == 15 || defender.getUnitType() == 28))
                atk+=10;
            //If both attacker and defender are Stealths, bombers, fighters, or black bombs
            if((attacker.getUnitType() == 17 || attacker.getUnitType() == 16 || attacker.getUnitType() == 23|| attacker.getUnitType() == 24) && (defender.getUnitType() == 17 || defender.getUnitType() == 16 || defender.getUnitType() == 23|| defender.getUnitType() == 24))
                atk+=10;
            //If both attacker and defender are 'ships'
            if((attacker.getUnitType() == 13 || attacker.getUnitType() == 11 || attacker.getUnitType() == 22|| attacker.getUnitType() == 30) && (defender.getUnitType() == 13 || defender.getUnitType() == 11 || defender.getUnitType() == 22|| defender.getUnitType() == 30))
                atk+=10;
            //If both attacker and defender are "vehicles"
            if((attacker.getUnitType() == 12 || attacker.getUnitType() == 21 || attacker.getUnitType() == 10) && (defender.getUnitType() == 12 || defender.getUnitType() == 21 || defender.getUnitType() == 10))
                atk+=10;
        }
        return atk;
        
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
    
    //carries out Carrie's CO Power
    //Gets list of all allied units, sorts by current price, refreshes
    //Rounds up on the amount of units
    public void COPower(){
        COP = true;
        Unit[] all = army.getUnits();
        sort(all);
        for(int t = all.length-1; t > Math.floor(((all.length -1)/5.0) * 4.0); t--){
            all[t].setActive(true);
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
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        selecting = true;
        units = 0;
        selected = 0;
        Army[] armies = army.getBattle().getArmies();
        for(int i = 0; i<armies.length;i++) {
            if(armies[i].getSide() != army.getSide())
                units+= armies[i].getNumberOfUnits();
        }
        units/=2; //Units divided by two - amount of units Carrie can select.
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    
    public void selectAction(Tile t) {
        selected++;
        t.getUnit().setParalyzed(true);
        if(selected >=units )
            selecting = false;
    }
    public boolean validSelection(Tile t) {
        if(t.hasUnit()) {
            if(t.getUnit().getArmy().getSide() != army.getSide()) {
                return true;
            }
        }
        return false;
    }
    public void invalidSelection() //IF they hit the wrong button
    {
        //Nothing happens if they select the wrong tile
    }
    //Hitting B exits automatically.
}
