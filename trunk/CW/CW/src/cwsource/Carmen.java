package cwsource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 *Carmen.java
 *Author: -
 *Contributors:
 *Creation:
 *The Carmen class is used to create an instance of the Amber Corona CO Carmen.
 */

public class Carmen extends CO {
    private boolean itsWarpinTime;
    private double currStarCharge;
    private double altStarCharge;
	final static Logger logger = LoggerFactory.getLogger(Carmen.class); 
    //constructor
    public Carmen() {
        name = "Carmen";
        id = 48;
        
        String CObiox = "A CO at home in the cities.";             //Holds the condensed CO bio'
        String titlex = "Haute Stuff";
        String hitx = "Geography"; //Holds the hit
        String missx = "Red Hands"; //Holds the miss
        String skillStringx = "Attacks against units on cities deal more damage.";
        String powerStringx = "Attacks against units on cities deal significantly more damage. The enemy cannot charge this turn."; //Holds the Power description
        String superPowerStringx = "Select one city. That city is yours. Attacks against units on cities deal significantly more damage, and movement through cities drop to 0"; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "A urban warrior that is able to     " +
                        "quicky seize contested properites.  " +
                        "Carmen's units specialize against   " +
                        "units that are on properties." +
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
        
        String[] TagCOsx = {"Varlot", "Eric", "Edward", "Kanbei", "Sonja"}; //Names of COs with special tags
        String[] TagNamesx = {"Percievable Opaque", "Dual Strike", "Dual Strike", "Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,105,105,85,85}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Where in the world is your ability to command?",
         "You’re all so dreadfully plebeian...",
         "Grace is all one needs.",
         "You thought you were safe hiding there, didn’t you?",
         "Your stupidity is only outmatched by your ability to fail.",
         "Think out your plan like a woman of action. Then act out your plan like a woman of thought."};
        
        String[] Victoryx =
        {"The worst part about being really good at something is that someone's always coming along trying to prove that they're better at what you do.",
         "See you next crime!",
         "Competition's fine, but I'd rather someone showed me something they were good at." };
        
        String[] Swapx =
        {"I'll just quietly slip in...",
         "Surely this battle is not worthy of my attention?"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Metropolition";
        SCOPName = "Callous Conquer";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = AMBER_CORONA;
        
        itsWarpinTime = false;
        
        //Used to remember the enemy's old star charge
        currStarCharge = 0.0;
        altStarCharge = 0.0;
    }
    
    public int getAtk(Unit attacker, Unit defender) {
        if(defender != null){
            if(army.getBattle().getMap().find(defender).getTerrain().isUrban()) {
                if(COP) {
                    return 130;
                } else if(SCOP) {
                    return 180;
                }
                
                return 130;
            } else {
                if(COP) {
                    return 110;
                } else if(SCOP) {
                    return 120;
                }
                
                return 100;
            }
        } else {
            if(COP || SCOP)
                return 110;
            return 100;
        }
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender) {
        if(COP || SCOP) {
            return 110;
        }
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Carmen's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        itsWarpinTime = true;
        setupWarpCities();
    }
    
    //carries out Carmen's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        selecting = true;
        

    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP() {
        COP = false;
        itsWarpinTime = false;
        undoWarpCities();
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        selecting = false;
        

    }
    
    public boolean validSelection(Tile t) {
        //City is '10' in MASTER INDEX LIST
        
        if(t.getTerrain().getIndex() == 10) {
            Property p = (Property)army.getBattle().getMap().find(t.getLocation()).getTerrain();
            
            if((p.getOwner() == null) || (p.getOwner().getSide() != army.getSide())) {
                return true;
            }
        }
        
        return false;
    }
    
    public void selectAction(Tile t) {
        if(selecting) {
            Property p = (Property)army.getBattle().getMap().find(t.getLocation()).getTerrain();
            
            p.setOwner(army);
            
            selecting = false;
        }
    }
    
    public void cancelSelection() //If they press B
    {
        selecting = false;
    }
    
    public void invalidSelection() //IF they hit the wrong button
    {
        //DO NOTHING D:
    }
    
    public void setupWarpCities() {
        for(int col = 0; col < army.getBattle().getMap().getMaxCol(); col++) {
            for(int row = 0; row < army.getBattle().getMap().getMaxRow(); row++) {
                if(army.getBattle().getMap().find(new Location(col,row)).getTerrain().isUrban()) {
                    army.getBattle().getMap().find(new Location(col,row)).getTerrain().newMoveSet(new double[] {0.00,0.00,0.00,0.00,0.00,-1,-1,0.00,-1,0.00});
                    logger.info("warpCity" +army.getBattle().getMap().find(new Location(col,row)).getTerrain().getBaseMove());
                }
            }
        }
    }
    
    public void undoWarpCities() {
        for(int col = 0; col < army.getBattle().getMap().getMaxCol(); col++) {
            for(int row = 0; row < army.getBattle().getMap().getMaxRow(); row++) {
                if(army.getBattle().getMap().find(new Location(col,row)).getTerrain().isUrban()) {
                    army.getBattle().getMap().find(new Location(col,row)).getTerrain().restoreCost();
                }
            }
        }
    }
    
    public void enemyDayStart(boolean main) {
        if(itsWarpinTime) {
            //It's Warpin' Time, but not for the enemy!
            
            undoWarpCities();
            
            itsWarpinTime = false;
        }
    }
    
    //Remember the enemy's charge star count before all damage is dealt
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        if(COP && enemy != null) {
            currStarCharge = enemy.getArmy().getCO().getStars();
            if(enemy.getArmy().getAltCO()!= null)
                altStarCharge = enemy.getArmy().getAltCO().getStars();
        }
    }
    
    //Reset the enemy's charge star count after all damage is dealt
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        if(COP && enemy != null) {
            enemy.getArmy().getCO().stars = currStarCharge;
            if(enemy.getArmy().getAltCO()!= null)
                enemy.getArmy().getAltCO().stars = altStarCharge;
            
            currStarCharge = 0.0;
            altStarCharge = 0.0;
        }
    }
}