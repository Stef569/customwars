package com.customwars.officer;
/*
 *Yukio
 *Author: Albert Lai
 *Contributors:
 *Creation: December 11, 2006
 * Hidden HP (lol, not implemented yet)
 * If yousa don't hand over de battle, youse gunna sleep with da fishes, ya hear?
 */
import java.util.ArrayList;

import com.customwars.Army;
import com.customwars.CO;
import com.customwars.Property;
import com.customwars.Unit;

public class Yukio extends CO{
    boolean sustain = false;
    //constructor
    ArrayList<Property> repairs;
    ArrayList<Integer> lostIncome;
    ArrayList<Integer> count; //Ticks every time Yukio takes a turn. Needs to tick to 2 to remove a property.
    boolean prevent = false;
    boolean swap = false;
    public Yukio() {
        name = "Yukio";
        id = 38;
        
        String CObiox = "A master of the black market who joins Amber Corona for reasons of his own. Has little trust in anyone.";             //Holds the condensed CO bio'
        String titlex = "Godfather";
        String hitx = "Money"; //Holds the hit
        String missx = "Traitors"; //Holds the miss
        String skillStringx = "Enemy repairs are increased by 50%.";
        String powerStringx = "Enemy units are deployed at 8 HP, and they lose their funds at the end of the day."; //Holds the Power description
        String superPowerStringx = "Units built last turn are built at 6HP and paralyzed"; //Holds the Super description
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
        {"I made them an offer they couldn't refuse...",
         "How do you know you don't like bribes if you've never taken one?",
         "Bring up a list of all contacts in the region.",
         "If you can't beat them, buy them.",
         "A man in my position can't afford to be made to look ridiculous.",
         "The richest man is the one with the most powerful friends." };
        
        String[] Victoryx =
        {"The key to a successful leader is influence.",
         "Victory depends on who you know and who you can buy.",
         "If history has taught us anything, it's that you can kill anyone." };
        
        String[] Swapx =
        {"It's nothing personal. Strictly business.",
         "It's time to see what my associates can do." };
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Eric","Edward","Carmen","Sonja","Kanbei", "Sabaki"}; //Names of COs with special tags
        String[] TagNamesx = {"Criminal Intent", "Black Merchants", "Perceivable Opaque", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,110, 90, 90, 105}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Criminal Connections";
        SCOPName = "Underworld Assault";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = AMBER_CORONA;
        
        cleanEnemyStoreBegin = false;
        cleanEnemyStoreEnd = false;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        if(SCOP)return 110;
        if(COP)return 120;
        return 100;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(prevent)
            return 200;
        if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        sustain = true;
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        for(int i = 0; i< armies.length; i++) {
            Unit[] u = armies[i].getUnits();
            if(armies[i].getSide()!=army.getSide()) {
                for(int s = 0; s<u.length; s++) {
                    if(u[s].getEnemyCOstore()[statIndex][0]%10 == 1) {
                        u[s].damage(40, false);
                        u[s].setParalyzed(true);
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
    public void deactivateSCOP() {
        SCOP = false;
    }
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        if(!attack && SCOP) {
            if(enemy.getArmy().getFunds() >= enemy.getPrice() * 0.2 ) {
                enemy.getArmy().removeFunds(((int)(enemy.getPrice() * 0.2)));
            } else
                prevent = true;
        }
    }
    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean attack) {
        if(!attack && SCOP) {
            if(enemy.getArmy().getFunds() >= enemy.getPrice() * 0.2 && damage > 0) {
                enemy.getArmy().removeFunds(((int)(enemy.getPrice() * 0.2)));
                army.addFunds(((int)(enemy.getPrice() * 0.2)));
            } else
                prevent = true;
        }
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        prevent = false;
    }
    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main) {
        
        if(index == 15) {
            if(COP || (sustain && !main))
                u.damage(20,false);
            u.getEnemyCOstore()[statIndex][0] = 11; //Used for SCOP calculation.
            u.setRepairMod((float)(u.getRepairMod() + 0.5));
        }
    }
    public void dayStart(boolean main) { 
        if(sustain) {
            Army[] armies = army.getBattle().getArmies(); //Get all armies
            for(int i = 0; i < armies.length; i++) {
                if(armies[i].getSide() != army.getSide()) {
                    armies[i].removeFunds((armies[i].getFunds())); //Removes the funds.
                }
            }
            sustain = false;
        }
        if(main) {
            Army[] armies = army.getBattle().getArmies();
            for(int i = 0; i< armies.length; i++) {
                Unit[] u = armies[i].getUnits();
                if(armies[i].getSide()!=army.getSide()) {
                    for(int s = 0; s<u.length; s++) {
                        if(u[s].getEnemyCOstore()[statIndex][0]%10 == 1)
                        {
                        u[s].getEnemyCOstore()[statIndex][0]--;
                        }
                    }
                }
            }
        }
        swap = main;
    }
    
    public void dayEnd(boolean main) {

        if(main) {
            Army[] armies = army.getBattle().getArmies();
            for(int i = 0; i< armies.length; i++) {
                Unit[] u = armies[i].getUnits();
                if(armies[i].getSide()!=army.getSide()) {
                    for(int s = 0; s<u.length; s++) {
                        if(u[s].getEnemyCOstore()[statIndex][0] < 10)
                        {
                        u[s].setRepairMod((float) (u[s]
								.getRepairMod() + .5));
                        u[s].getEnemyCOstore()[statIndex][0] +=10;
                        }
                    }
                }
            }
        } else {
            if(swap) {
                Army[] armies = army.getBattle().getArmies();
                for(int i = 0; i< armies.length; i++) {
                    Unit[] u = armies[i].getUnits();
                    if(armies[i].getSide()!=army.getSide()) {
                        for(int s = 0; s<u.length; s++) {
                            if(u[s].getEnemyCOstore()[statIndex][0] >= 10)
                            u[s].setRepairMod((float) (u[s]
									.getRepairMod() - .5));
                            u[s].getEnemyCOstore()[statIndex][0] -=10;
                        }
                    }
                }
                swap = false;
            }
        }
    }
}
