package com.customwars.officer;
/*
 *Mary.java
 *Author: -
 *Contributors:
 *Creation: -
 *Mary
 */

import java.util.Random;
import java.util.ArrayList;

import com.customwars.Army;
import com.customwars.CO;
import com.customwars.Mission;
import com.customwars.Property;
import com.customwars.Tile;
import com.customwars.Unit;

public class Mary extends CO {
    private ArrayList<Tile> bonusTiles;
    private ArrayList<Unit> afflictedUnits;
    
    //constructor
    public Mary() {
        name = "Mary";
        id = 52;
        
        String CObiox = "A ruthless commander recruited into the Amber Corona Mercenaries by Eric. ";             //Holds the condensed CO bio'
        String titlex = "Bloody Mary";
        String hitx = "Cocktails"; //Holds the hit
        String missx = "Excuses"; //Holds the miss
        String skillStringx = "Units attacked by Mary cannot be repaired. Whenever Mary destroys an enemy unit on a property, she gains a capture bonus for that property.";
        String powerStringx = "Deployment and repairs from properties are disabled."; //Holds the Power description
        String superPowerStringx = "Terrain stars now reduces the foe's defense and Mary's capture bonus is greatly increased. All units gain substantial firepower."; //Holds the Super description
        
        String intelx = "Units attacked by Mary can't be     " +
                        "repaired. Mary's units also capture " +
                        "more effectively on cities where she" +
                        "has destroyed a unit the day before." +
                        "Mary's power disables deployment,   " +
                        "while her super reverses terrain."; //el on CO select menu, 6 lines max
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        String[] COPowerx =
        {"Keep marching guys, the victory is close!",
         "No pain! NO GLORY!",
         "A drop of blood is a drop of effort...you must be proud of your troops.",
         "People call me Bloody Mary....and no, you can't drink me.",
         "We will fight until your last soldier is dead!",
         "I am a soldier, I fight where I am told, and I win where I fight."
        };
        
        String[] Victoryx =
        {"Heh, looks like all my effort paid off in the end",
         "Lets go, people. Lets have a drink to celebrate this victory.",
         "All right troops, now you can rest, your job here is done."};
        
        String[] Swapx =
        {"*sigh* I have no regrets. Good job defeating me",
         "Oh well...back to intensive training, boys. And try not to call for mommy this time."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Koal", "Eric", "Yukio", "Sabaki", "Ozzy", "Carmen", "Edward", "Nell", "Andy"}; //Names of COs with special tags
        String[] TagNamesx = {"Path of Blood", "Hands of the Underworld", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2, 1, 0, 0, 0, 0, 0, 0, 0, 0}; //Number of stars for each special tag.
        int[] TagPercentx = {115, 110, 105, 105, 105, 105, 105, 105, 90, 80}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Highway of Pain";
        SCOPName = "Bloody Mary";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = AMBER_CORONA;
        
        bonusTiles = new ArrayList<Tile>();
        afflictedUnits = new ArrayList<Unit>();
        cleanEnemyStoreBegin = false;
        cleanEnemyStoreEnd = true;
        
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit friend, Unit enemy) {
        int atk = 100;
        
        if(COP) {
            atk += 20;
        } else if(SCOP) {
            atk += 30;
        }
        
        //System.out.println("CALC'd ATK = " + atk);
        
        return atk;
    }
    
    public void setChange(Unit u) {}
    
    public void unChange(Unit u) {}
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit enemy, Unit friend) {
        int def = 100;
        return def;
    }
    
    //carries out Mary's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        
        Army[] armies = army.getBattle().getArmies();
        
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide()) {
                //Disable all enemy properties! Charge!
                Property[] props = armies[a].getProperties();
                
                for(int p = 0; p < props.length; p++) {
                    disableProperty(props[p]);
                }
            }
        }
    }
    
    //carries out Mary's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i<armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                u = armies[i].getUnits();
                for(int s = 0; s<u.length; s++) {
                    if(u[s].getMoveType() != u[s].MOVE_AIR) {
                        u[s].setDefensePenalty(u[s]
								.getDefensePenalty()
								+ (army.getBattle().getMap().find(u[s]).getTerrain().getDef() * u[s].getDisplayHP()* 2));
                        u[s].getEnemyCOstore()[statIndex][0] = army.getBattle().getMap().find(u[s]).getTerrain().getDef() * u[s].getDisplayHP()* 2;
                        if(army.getBattle().getMap().find(u[s]).getTerrain().isUrban() && ((Property)army.getBattle().getMap().find(u[s]).getTerrain()).getCp() != ((Property)army.getBattle().getMap().find(u[s]).getTerrain()).getTotalcp()) {
                            u[s].setDefensePenalty(u[s]
									.getDefensePenalty()
									+ (army.getBattle().getMap().find(u[s]).getTerrain().getDef() * u[s].getDisplayHP()* 1));
                            u[s].getEnemyCOstore()[statIndex][0] += army.getBattle().getMap().find(u[s]).getTerrain().getDef() * u[s].getDisplayHP()* 1;
                        }
                    }
                }
            }
        }
    }
    
//used to deactivate Mary's CO Power the next day
    public void deactivateCOP() {
        COP = false;
    }
    
//used to deactivate Mary's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i<armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                u = armies[i].getUnits();
                for(int s = 0; s<u.length; s++) {
                    u[s].setDefensePenalty(u[s]
							.getDefensePenalty()
							- u[s].getEnemyCOstore()[statIndex][0]);
                }
            }
        }
    }
    
    public void dayStart(boolean main) {
        //To make sure that bonuses stay on a tile FEREVAAAAAHHHH
        //until it is captured of course lol
        
        Army[] armies = army.getBattle().getArmies();
        
        //Enable all enemy properties!
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide()) {
                Property[] props = armies[a].getProperties();
                
                for(int p = 0; p < props.length; p++) {
                    enableProperty(props[p]);
                }
            }
        }
        
        //Repair and resupply conflicts start over
        for(int u = 0; u < afflictedUnits.size(); u++) {
            System.out.println("Restored");
            afflictedUnits.get(u).setNoCityRepair(false);
            afflictedUnits.get(u).setNoRepaired(false);
        }
        
        afflictedUnits = new ArrayList<Unit>();
    }
    
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        if(attack) {
            afflictUnit(enemy);
        }
    }
    
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        //When a unit is destroyed by Mary, her units gain a capture bonus on that tile.
        //FEREVAAAAAAAAAAAAAAAAAAAAHHH
        //Or until her first capture on that property
        if(destroy) {
            Tile t = enemy.getMap().find(enemy);
            
            if(t.getTerrain().isUrban()) {
                Property p = (Property)t.getTerrain();
                
                if(p.isCapturable()) {
                    bonusTiles.add(t);
                }
            }
        }
        
        //This is to reset the terrain penalty modification done in beforeAttack()
        if(COP || SCOP) {
            setEnemyTerrainPenalty(0);
        }
    }
    
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(destroy) {
            Tile t = enemy.getMap().find(enemy);
            
            if(t.getTerrain().isUrban()) {
                System.out.println("Added!");
                bonusTiles.add(t);
            }
        }
    }
    
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        //Capture is 2
        if(main && index == 2) {
            if(isBonusTile(army.getBattle().getMap().find(u))) {
                Property P = (Property)army.getBattle().getMap().find(u).getTerrain();
                
                if(P.getOwner() == u.getArmy()) {
                    //System.out.println("Bonus Tile removed!");
                    
                    bonusTiles.remove(army.getBattle().getMap().find(u));
                } else if(SCOP) {
                    if(P.getCapturePoints() > 15) {
                        P.setCapturePoints(P.getCapturePoints() - 15);
                    } else {
                        //System.out.println("Bonus Tile removed!");
                        
                        //boolean gameEnd = P.setOwner(u.getArmy());
                        
                        if(P.setOwner(u.getArmy())) {
                            Mission.getBattleScreen().endBattle();
                        }
                        
                        //System.out.println(P.getMaxCapturePoints());
                        
                        //bonusTiles.remove(army.getBattle().getMap().find(u));
                    }
                    
                    bonusTiles.remove(army.getBattle().getMap().find(u));
                } else {
                    if(P.getCapturePoints() > 5) {
                        P.setCapturePoints(P.getCapturePoints() - 5);
                    } else {
                        //System.out.println("Bonus Tile removed!");
                        
                        //boolean gameEnd = P.setOwner(u.getArmy());
                        
                        if(P.setOwner(u.getArmy())) {
                            Mission.getBattleScreen().endBattle();
                        }
                        
                        //bonusTiles.remove(army.getBattle().getMap().find(u));
                    }
                    
                    bonusTiles.remove(army.getBattle().getMap().find(u));
                }
            }
        }
    }
    
    public boolean isBonusTile(Tile t) {
        for(int i = 0; i < bonusTiles.size(); i++) {
            Tile curr = bonusTiles.get(i);
            
            if(curr.getLocation().equals(t.getLocation())) {
                //System.out.println("Bonus Tile detected!");
                
                return true;
            }
        }
        
        return false;
    }
    
    public void disableProperty(Property p) {
        p.setCreateAir(false);
        p.setCreateLand(false);
        p.setCreateSea(false);
        
        p.setRepairAir(false);
        p.setRepairLand(false);
        p.setRepairSea(false);
    }
    
    public void enableProperty(Property p) {
        if(p.getName().equals("City") || p.getName().equals("HQ")) {
            p.setRepairLand(true);
        } else if(p.getName().equals("Base")) {
            p.setCreateLand(true);
            p.setRepairLand(true);
        } else if(p.getName().equals("Airport")) {
            p.setCreateAir(true);
            p.setRepairAir(true);
        } else if(p.getName().equals("Port")) {
            p.setCreateSea(true);
            p.setRepairSea(true);
        } else if(p.getName().equals("Pipestation")) {
            p.setCreatePipe(true);
            p.setRepairPipe(true);
        }
    }
    
    public void afflictUnit(Unit enemy) {
        afflictedUnits.add(enemy);
        enemy.setNoCityRepair(true);
        enemy.setNoRepaired(true);
    }
}