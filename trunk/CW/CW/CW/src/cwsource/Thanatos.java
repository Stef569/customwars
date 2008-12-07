package cwsource;
/*
 *Thanatos.java
 *Author: -
 *Contributors: -
 *Creation:
 *The Thanatos class is used to create an instance of the PG CO Thanatos.
 */

import java.util.ArrayList;

public class Thanatos extends CO {
    private ArrayList<Unit> currentScouts;
    private boolean suddenStrike;
    
    //constructor
    public Thanatos() {
        name = "Thanatos";
        id = 53;
        
        String CObiox = "The cold and unforgiving commander of Parallel Galaxy intelligence. Does not tolerate failure within his ranks. ";             //Holds the condensed CO bio'
        String titlex = "Reconnaissance";
        String hitx = "Obedience"; //Holds the hit
        String missx = "Incompetence"; //Holds the miss
        String skillStringx = "As vision rises, so does defense. However, defense from terrain is halved.";
        String powerStringx = "If the enemy cannot attack units that aren't in vision range at the beginning of their turn"; //Holds the Power description
        String superPowerStringx = "All units take damage depending on the amount of vision they hav."; //Holds the Super description
        String intelx = "";
                intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] COPowerx =
        {"You are operating blind. Prepare for annihilation.",
         "Your weakness is laid bare before the world.",
         "The futility of your operation is astounding.",
         "I shall soon turn your defeat from a probability to a fact.",
         "Intelligence is always the deciding factor of a battle.",
         "Your lack of foresight has doomed you."};
        
        String[] Victoryx =
        {"I have discovered the truth. Your skills are negligible.",
         "Your cause was hopeless from the first day. Only Parallel Galaxy will remain.",
         "Your intelligence was lacking, in more ways than one."};
        
        String[] Swapx =
        {"My data collection is complete. I'll take over now.",
         "I have mapped their every weakness."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        String[] TagCOsx = {"Graves", "Talyx", "Julia", "Cassidy", "Sonja", "Ain"}; //Names of COs with special tags
        String[] TagNamesx = {"Memento Mori", "Mindquake", "Thought Police", "Decisive Ambush", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1, 1, 1, 1, 0, 0}; //Number of stars for each special tag.
        int[] TagPercentx = {105, 105, 105, 105, 90, 90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Lightning Scythe";
        SCOPName = "Counterintelligence";
        COPStars = 4.0;
        maxStars = 8.0;
        this.army = army;
        style = PARALLEL_GALAXY;
        
        currentScouts = new ArrayList<Unit>();
        suddenStrike = false;
        
        terrainDefenseMultiplier = 0.5;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit owned, Unit enemy) {
        int atk = 100;
        
        if(COP || SCOP) {
            atk += 10;
        }
        
        if(COP) {
            if(owned.vision <= 2) {
                atk += 20;
            } else if((owned.vision >= 3) && (owned.vision <= 4)) {
                atk += 30;
            } else if((owned.vision >= 5)) {
                atk += 40;
            }
        }
        
        return atk;
    }
    
    public void setChange(Unit u) {
        
    }
    
    public void unChange(Unit u) {
        
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit enemy, Unit owned) {
        int def = 100;
        
        if(SCOP) {
            def += 10;
        }
        if(COP){
            def += 20;
        }
        
        if((owned.vision >= 3) && (owned.vision <= 4)) {
            def += 20;
        } else if((owned.vision >= 5)) {
            def += 30;
        }
        
        if(suddenStrike && !isACurrentScout(enemy)) {
            def += 200;
        }
        
        return def;
    }
    
//carries out Olaf's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        suddenStrike = true;
        Army[] armies = army.getBattle().getArmies();
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide() && armies[a].getUnits() != null) {
                Unit[] u = armies[a].getUnits();
                for(int n = 0; n < u.length; n++) {
                    if(u[n].name.equals("MDTank") || u[n].name.equals("Neotank")|| u[n].name.equals("Megatank")|| u[n].maxRange>1) {
                        u[n].vision++;
                        u[n].enemyCOstore[statIndex][0] = 1;
                    }
                }
            }
        }
    }
    
    //carries out Olaf's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        
        //mass damage
        Army[] armies = army.getBattle().getArmies();
        
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide() && armies[a].getUnits() != null) {
                Unit[] u = armies[a].getUnits();
                
                for(int n = 0; n < u.length; n++) {
                    
                    if(u[n].getClass() != null) {
                        if(!u[n].isInTransport()) {
                            u[n].damage(u[n].vision, false);
                            if(u[n].name.equals("MDTank") || u[n].name.equals("Neotank")|| u[n].name.equals("Megatank")|| u[n].maxRange>1) {
                                u[n].damage(10, false);
                                u[n].vision++;
                                u[n].enemyCOstore[statIndex][0] = 1;
                            }
                        }
                    } else {
                        //return;
                        //Why is there a return here?
                        //rofl I dunno
                    }
                }
            }
        }
    }
    
//used to deactivate Olaf's CO Power the next day
    public void deactivateCOP() {
        COP = false;
        Army[] armies = army.getBattle().getArmies();
        
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide() && armies[a].getUnits() != null) {
                Unit[] u = armies[a].getUnits();
                for(int n = 0; n < u.length; n++) {
                    if(u[n].enemyCOstore[statIndex][0] == 1)
                        u[n].vision--;
                }
            }
        }
        
    }
    
//used to deactivate Olaf's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        
        Army[] armies = army.getBattle().getArmies();
        for(int a = 0; a < armies.length; a++) {
            if(armies[a].getSide() != army.getSide() && armies[a].getUnits() != null) {
                Unit[] u = armies[a].getUnits();
                
                for(int n = 0; n < u.length; n++) {
                    if(u[n].enemyCOstore[statIndex][0] == 1)
                        u[n].vision--;
                }
            }
        }
    }
    
    public void dayStart(boolean main) {
        //At the start of Thanatos' next day, the sudden strike effect is disabled
        if(suddenStrike) {
            suddenStrike = false;
            currentScouts = new ArrayList<Unit>();
            logger.info("Sudden Strike disabled!");
        }
    }
    
    public void enemyDayStart(boolean main) {
        //Each enemy gets a different set of currentScouts
        currentScouts = new ArrayList<Unit>();
        
        //It is assumed that the sudden strike effect only takes place while Thanatos is up front
        //If Thanatos is not the main CO, the sudden strike effect dissipates
        if(main) {
            if(suddenStrike) {
                calcCurrentScouts();
            }
        } else {
            suddenStrike = false;
            logger.info("Sudden Strike disabled!");
        }
    }
    
    public boolean isACurrentScout(Unit enemy) {
        for(int s = 0; s < currentScouts.size(); s++) {
            Unit temp = currentScouts.get(s);
            
            if(temp.getLocation().equals(enemy.getLocation()) && temp.getArmy().getSide() == enemy.getArmy().getSide()) {
                return true;
            }
        }
        
        return false;
    }
    
    public void calcCurrentScouts() {
        Army[] players = army.getBattle().getArmies();
        
        //For each enemy player, check for each unit if it can see any of Thanatos' units
        //If the unit is capable of seeing something, add it to currentScouts
        //Otherwise, don't add the unit to currentScouts
        for(int p = 0; p < players.length; p++) {
            //Checks only occur on players that are enemies
            if(players[p].getSide() != army.getSide()) {
                Unit[] enemyUnits = players[p].getUnits();
                Unit[] myUnits = army.getUnits();
                
                //Make sure both of the Unit arrays are not null; if either are null, then there's no point in progressing any further!
                if(enemyUnits == null || myUnits == null) {
                    return;
                }
                
                //This check is performed for each unit under the player's control
                for(int e = 0; e < enemyUnits.length; e++) {
                    //Whenever one of Thanatos' units is sighted, the enemy unit is added
                    //No need to check for all of Thanatos' units, so break when one is found
                    for(int m = 0; m < myUnits.length; m++) {
                        int eCol = enemyUnits[e].getLocation().getCol();
                        int eRow = enemyUnits[e].getLocation().getRow();
                        
                        int mCol = myUnits[m].getLocation().getCol();
                        int mRow = myUnits[m].getLocation().getRow();
                        
                        int rCol = eCol - mCol;
                        int rRow = eRow - mRow;
                        
                        if(rCol < 0) {
                            rCol *= -1;
                        }
                        
                        if(rRow < 0) {
                            rRow *= -1;
                        }
                        
                        if(rCol + rRow <= enemyUnits[e].vision) {
                            logger.info("Thanatos' unit is sighted at (" + mCol + ", " + mRow + ")");
                            logger.info("Sighting unit is = " + enemyUnits[e].name + " at (" + eCol + ", " + eRow + ")");
                            currentScouts.add(enemyUnits[e]);
                            break;
                        }
                    }
                }
            }
        }
    }
}