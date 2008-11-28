package cwsource;
 /*
  *Artemis.java
  *Author: -
  *Contributors: -
  *Creation:
  *The Artemis class is used to create an instance of the OS CO Artemis.
  */

import java.util.ArrayList;

public class Artemis extends CO {
    private ArrayList<Property> blindProps;
    
    //constructor
    public Artemis() {
        name = "Artemis";
        id = 57;
        
        String CObiox = "Orange Star's hotheaded advisor turned CO. Weakness for cute girls.";             //Holds the condensed CO bio'
        String titlex = "Rain Man";
        String hitx = "Cute Girls, Rainy days."; //Holds the hit
        String missx = "Tripping over his own feet."; //Holds the miss
        String skillStringx = "Artemis' troops receive an attack boost during rain. They get no vision penalty in rain.";
        String powerStringx = "Causes rain to fall. Enemies take damage if they move. The more they move, the more damage is dealt."; //Holds the Power description
        String superPowerStringx = "Causes rain to fall. Enemies lose an additional one vision and cannot see their properties being captured. Firepower is increased."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "" +
                "" +
                "" +
                "" +
                "" +
                "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] COPowerx =
        {"I hope you packed your swimsuit!",
         "Sorry to rain on your parade, but you're gonna get washed away!",
         "The forecast calls for a downpour of rain, with a chance of pain...",
         "Look! A distraction! No? Well, how about a CO Power?",
         "Look out, you're about to get wet! ...Very, very, wet.",
         "Neo to A5. This is too easy!"};
        
        String[] Victoryx =
        {"*Yawn* Is it over already? Time for a well-deserved break.",
         "Rain, rain... Come around whenever you want.",
         "Another day, another battle... When will it end?"};
        
        String[] Swapx =
        {"Let's see.. Ah! Time to test out this strategy!",
         "Zzzz... Huh? My turn already? Better get started!"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        String[] TagCOsx = {"Rachel","Mina","Nana","Nell","Olaf","Ember","Dreadnaught"}; //Names of COs with special tags
        String[] TagNamesx = {"Ray of Hope","Kinship's Bond","Fire and Water","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,2,2,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,115,120,105,90,80,80}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Flash Flood";
        SCOPName = "Torrential Rain";
        COPStars = 4.0;
        maxStars = 5.0;
        this.army = army;
        style = ORANGE_STAR;
        
        rainImmunity = true;
        
        blindProps = new ArrayList<Property>();
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit friend, Unit enemy) {
        int atk = 100;
        
        //rain
        if(army.getBattle().getWeather() == 1) {
            if(SCOP) {
                atk += 25;
            } else {
                atk += 10;
            }
        }
        
        //otherwise
        if(SCOP)
            atk+=10;
        if(COP)
            atk+=15;
        
        return atk;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit enemy, Unit friend) {
        int def = 100;
        
        if(SCOP || COP) {
            def += 10;
        }
        
        return def;
    }
    
    //carries out Artemis' CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        army.getBattle().startWeather(1, 1);
        army.getBattle().calculateFoW();
    }
    
    //carries out Artemis' Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        army.getBattle().startWeather(1, 1);
        
        //mass blind
        Army[] players = army.getBattle().getArmies();
        Unit[] units = null;
        Property[] props = null;
        
        for(int i = 0; i < players.length; i++) {
            if(players[i].getSide() != army.getSide()) {
                //Now to blind enemy units
                units = players[i].getUnits();
                
                if(units != null) {
                    for(int s = 0; s < units.length; s++) {
                        if(units[s].getClass() != null) {
                            units[s].vision--;
                            units[s].enemyCOstore[statIndex][0] = 5;
                        } else {
                            //return;
                        }
                    }
                }
                
                //Now to blind enemy properties!
                props = players[i].getProperties();
                
                if(props != null) {
                    for(int j = 0; j < props.length; j++) {
                        if(props[j].getClass() != null) {
                            props[j].setVisionRange(-1);
                            blindProps.add(props[j]);
                        } else {
                            //return;
                        }
                    }
                }
            }
        }
    }
    
    //used to deactivate Artemis' CO Power the next day
    public void deactivateCOP() {
        COP = false;
        
    }
    
    //used to deactivate Artemis' Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        Army[] players = army.getBattle().getArmies();
        Unit[] units = null;
        
        //Unblind enemy units
        for(int i = 0; i < players.length; i++) {
            if(players[i].getSide() != army.getSide()) {
                units = players[i].getUnits();
                
                if(units != null) {
                    for(int s = 0; s < units.length; s++) {
                        if(units[s].getClass() != null) {
                            //Check if Artemis is the 'main' CO
                            if(army.getCO() == this) {
                                if(units[s].enemyCOstore[statIndex][0] == 5) {
                                    units[s].vision++;
                                    units[s].enemyCOstore[statIndex][0] = 0;
                                }
                            } else {
                                if(units[s].altEnemyCOstore[statIndex][0] == 5) {
                                    units[s].vision++;
                                    units[s].altEnemyCOstore[statIndex][0] = 0;
                                }
                            }
                        } else {
                            //return;
                        }
                    }
                }
            }
        }
        
        for(int q = 0; q < blindProps.size(); q++) {
            blindProps.get(q).restoreVisionRange();
        }
        blindProps = new ArrayList<Property>();
    }
    
    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main) {
        //The flash flood effect only applies to enemy units, and only during the COP
        //Actually, I guess the action index doesn't matter, just as long as the unit expends movement
        //I'll just not do it again if the index counts for firing, since beforeAttack already takes care of that
        //Uh... and I can't make it occur if the unit is being deployed, or the moment the unit is being repaired,
        //and I'll assume that the effect doesn't apply to currently loaded units either...
        if(COP) {
            // 1 = attack
            // 15 = build new unit
            // 17 = ambush during unloading
            // 21 = city repair
            if(index != 1 && index != 15 && index != 17 && index != 21) {
                int mpLeft = u.getMoveRange().checkMPLeft(u.getLocation().getCol(), u.getLocation().getRow());
                
                if(mpLeft != -1) {
                    int mpUsed = u.getMove() - mpLeft;
                    int dmg = (int)(3.5 * mpUsed);
                    
                    u.damage(dmg, false);
                }
            }
        }
        if(SCOP) {
            //Blind enemy units which are newly created
            if(index == 15) {
                u.vision--;
                u.enemyCOstore[statIndex][0] = 5;
            }
            //Blind enemy properties which are newly captured
            else if(index == 2) {
                Property tempP = (Property)army.getBattle().getMap().find(u).getTerrain();
                
                //Check if recently captured!
                if(tempP.getOwner().getID() == u.getArmy().getID()) {
                    tempP.setVisionRange(-1);
                    blindProps.add(tempP);
                }
            }
        }
    }
    public void enemyDayStart(boolean main) {
        if(main && COP) {
            Army[] players = army.getBattle().getArmies();
            Unit[] units = null;
            
            for(int i = 0; i < players.length; i++) {
                if(players[i].getSide() != army.getSide()) {
                    //Now to blind enemy units
                    units = players[i].getUnits();
                    
                    if(units != null) {
                        for(int s = 0; s < units.length; s++) {
                            if(units[s].getClass() != null) {
                                units[s].calcMoveTraverse();
                            } else {
                                //return;
                            }
                        }
                    }
                }
            }
        }
    }
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        //If flash flood is on, then the enemy unit is supposed to be hurting itself when it moves.
        //This effect deals the damage before the attacks occur, so the enemy will be in for a surprise!
        //Also the damage only occurs for enemeies, so only when Artemis is not attacking.
        if(COP && !attack) {
            int mpLeft = enemy.getMoveRange().checkMPLeft(enemy.getLocation().getCol(), enemy.getLocation().getRow());
            
            if(mpLeft != -1) {
                int mpUsed = enemy.getMove() - mpLeft;
                int dmg = (int)(3.5 * mpUsed);
                
                enemy.damage(dmg, false);
            }
        }
    }
}