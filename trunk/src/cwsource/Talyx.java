package cwsource;
/*
 *Talyx.java
 *Author:  Albert Lai
 *Contributors: ChessRules
 *Creation: Juggerv2
 *You Jugger you, always making CCOs that get in!
 *Daaaaaaaamn youuuuuuuuuuuuuuu!
 */
import java.util.ArrayList;

public class Talyx extends CO {
    //constructor
    ArrayList<Property> prop;
    boolean sustain;
    public Talyx() {
        name = "Talyx";
        id = 37;
        
        String CObiox = "An aspiring commander who led a great revolution in Black Hole. The creator of the Parallel Galaxy army.";             //Holds the condensed CO bio'
        String titlex = "Down To Earth";
        String hitx = "Geology"; //Holds the hit
        String missx = "The sky"; //Holds the miss
        String skillStringx = "Units with anti-aerial capabilities have superior firepower.";
        String powerStringx = "Enemy units nearby allied units suffer one HP of damage."; //Holds the Power description
        String superPowerStringx = "Turns terrain effects into firepower bonuses. However, terrain no longer offers any defensive cover."; //Holds the Super description
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
        
        String[] TagCOsx = {"Alexis","Julia","Xavier","Mina","Epoch","Eagle"}; //Names of COs with special tags
        String[] TagNamesx = {"Earthen Fusion","Dystopia","Unseen Truth","Dual Strike","Dual Strike","Dual Strike"}; //Names of special tags
        int[] TagStarsx = {2,1,1,0,0,0}; //Tag stars
        int[] TagPercentx = {120,110,110,105,105,90}; //Tag percents
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"You put up a good fight, but that won't cut it.",
         "You ready to rumble? I mean that literally, you know.", //That "I mean that literally" is a bit blunt
         "I applaude your skills, but I must win nonetheless.",
         "Prepare for the true wrath of nature!",
         "Remember, you brought this upon yourself!",
         "You messed with me. That was your final mistake.",};
        
        String[] Victoryx =
        {"Now you know that the forces of nature are unstoppable.",
         "Well, you look pretty shaken up. My work's done.",
         "Why, is this a victory I've unearthed?",};
        //No, it's actually sedimentary shale, with a vein of quartz, you nimrod >_>
        
        String[] Swapx =
        {"Why must I waste my time with this?",
         "Sigh... If I really must."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Tremor";
        SCOPName = "Magnitude 9";
        COPStars = 3;
        maxStars = 7;
        this.army = army;
        style = PARALLEL_GALAXY;
        prop = new ArrayList<Property>();
    }
    
    public int getAtk(Unit attacker, Unit defender) {
        if(defender != null){
            if(attacker.getUnitType() == 5 || attacker.getUnitType() == 6 ||attacker.getUnitType() == 11 || attacker.getUnitType() == 16 || attacker.getUnitType() == 20 || attacker.getUnitType() == 22 || attacker.getUnitType() == 23 || attacker.getUnitType() == 28  ) {
                if(COP || SCOP)
                    return 120;
                else
                    return 110;
            } else
                if(COP||SCOP)
                    return 110;
                else
                    return 100;
        }
        if(COP || SCOP)return 110;
        return 100;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP||SCOP)return 110;
        return 100;
    }
    public void enemyDayStart(boolean main) {
        double[] temp = (new Wood()).move;
        if(sustain) {
            Location storage;
            
            for(int x = 0; x< army.getBattle().getMap().getMaxCol(); x++) {
                for(int y = 0; y < army.getBattle().getMap().getMaxRow(); y++) {
                    if(army.getBattle().getMap().hasProperty(x,y))
                        prop.add((Property)(army.getBattle().getMap().find(new Location(x,y)).getTerrain()));
                }
            }
            if(prop != null) {
                for(int x = 0; x< army.getBattle().getMap().getMaxCol(); x++) {
                    for(int y = 0; y < army.getBattle().getMap().getMaxRow(); y++) {
                        for(int i = 0; i< prop.size(); i++) {
                            int t =  Math.abs(x - prop.get(i).getTile().getLocation().getCol()) + Math.abs(y - prop.get(i).getTile().getLocation().getRow());
                            if(t<3) {
                                for(int s = 0; s<10; s++) {
                                    if(army.getBattle().getMap().find(new Location(x,y)).getTerrain().moveCost(s) != -1) {
                                        double q = 3-(army.getBattle().getMap().find(new Location(x,y)).getTerrain().baseMoveCost(s));
                                        army.getBattle().getMap().find(new Location(x,y)).getTerrain().addCost(s,q);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void enemyDayEnd(boolean main ){
        int[] temp;
        if(sustain) {
            Location storage;
            if(prop != null) {
                for(int x = 0; x< army.getBattle().getMap().getMaxCol(); x++) {
                    for(int y = 0; y < army.getBattle().getMap().getMaxRow(); y++) {
                        for(int i = 0; i< prop.size(); i++) {
                            int t =  Math.abs(x - prop.get(i).getTile().getLocation().getCol()) + Math.abs(y - prop.get(i).getTile().getLocation().getRow());
                            if(t<3) {
                                for(int s = 0; s<10; s++) {
                                    if(army.getBattle().getMap().find(new Location(x,y)).getTerrain().moveCost(s) != -1) {
                                        double q = (army.getBattle().getMap().find(new Location(x,y)).getTerrain().baseMoveCost(s))-3;
                                        army.getBattle().getMap().find(new Location(x,y)).getTerrain().addCost(s, q);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void dayStart(boolean main ){
        if(sustain) {
            sustain = false;
            prop.clear();
        }
    }
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        sustain = true;
        
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        Unit[] enemyu;
        Army[] armies = army.getBattle().getArmies();
        Location storage;
        int damaged = 0;
        int t;
        // i = armies
        // s = enemy unit
        // v = allied unit
        for(int i = 0; i < armies.length; i++) { //Goes through the armies
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null) { //If hostile
                enemyu = armies[i].getUnits(); //gets the units of army being targetted
                for(int s = 0; s < enemyu.length; s++) { //Cycles through enemy units
                    damaged = 0;
                    for(int v = 0; v < u.length; v++) { //Cycles through own units
                        if(u[v].moveType > 1) {
                            storage = enemyu[s].getLocation();
                            t = Math.abs(u[v].getLocation().getRow() - storage.getRow()) + Math.abs(u[v].getLocation().getCol() - storage.getCol());
                            switch(t) {
                                case 1:
                                    specialDamage(enemyu[s],3);
                                    break;
                                case 2:
                                    specialDamage(enemyu[s],2);
                                    break;
                                case 3:
                                    specialDamage(enemyu[s],1);
                                    break;
                            }
                        }
                    }
                }//This ends the unit for loop
            }
        }
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        enemyTerrainPenalty = 0;
    }
    public void specialDamage(Unit u, int times) {
        for(int i = 0; u.enemyCOstore[statIndex]<5 && i<times; i++) {
            u.damage(10, false);
            u.enemyCOstore[statIndex]++;
        }
    }
}
