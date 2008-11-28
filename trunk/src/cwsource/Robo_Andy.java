package cwsource;
/*
 *Robo_Andy.java
 *Author: -
 *Contributors: -
 *Creation: -
 *The Robo_Andy class is used to create an instance of the CO Robo_Andy
 */

public class Robo_Andy extends CO {
    //constructor
    public Robo_Andy() {
        name = "Robo-Andy";
        id = 60;
        
        String CObiox = "After the (moderate) success of the Robo-Andys, Lash was asked by the military to create artificial intelligence able to direct troops. However, being given orders by a computer freaked out the soldiers of OS somewhat, so she fixed the problem by putting it in the original Robo-Andy.";             //Holds the condensed CO bio'
        String titlex = "Not Andy";
        String hitx = "Puppet Shows"; //Holds the hit
        String missx = "Housework"; //Holds the miss
        String skillStringx = "Robo-Andy is Lash's newest advancement in technology. He incorporates the newest battle AI and robotics developed by Lash herself. However, his processors are outdated and tend to overheat often. His superior AI allows for efficient command of troops, but behaves erratically when he begins to heat up.";
        String powerStringx = "Robo-Andy releases the excess heat from his activities. This benefits his army as well; units are powered up and restore 2 HP."; //Holds the Power description
        String superPowerStringx = "WARNING! WARNING! Robo-Andy is overheating! His emergency venting module activates. He receives a bit of damage, but quickly shrugs it off with his autorepair system. His units heal 1 HP after any attack or counter-attack, and deal 2 HP damage to the offender if they are destroyed."; //Holds the Super description
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
                     //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        String[] TagCOsx = {"Lash","Andy"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,110}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"DADADA! ANNOYING PESTS DESERVE TO BE SWATTED!",
         "ERROR DETECTED. PREPARE TO BE ELIMINATED!",
         "YOU'RE A RUDE ONE! EAT HEAVY METAL!",
         "KYAAA! SUCH A JERK! TIME TO DIE!",
         "WARNING! WARNING! EXECUTING EMERGENCY VENTING MODULE.",
         "YOU'RE SO DISAPPOINTING. WITNESS MY POWER!"};
        
        String[] Victoryx =
        {"USELESS CREATURE!",
         "THANK YOU FOR USING ROBO-ANDY, INFERIOR BEING!",
         "FOR MORE EXCITING PERFORMANCES PLEASE UPGRADE TO THE LATEST VERSION."};
        
        String[] Swapx =
        {"HOW MAY I SERVE YOU, INFERIOR BEING?",
         "ALT+TAB! ALT+TAB! ALT+TAB!"};
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Cooldown";
        SCOPName = "Critical Mass";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        int atk = 100;
        
        //D2D firepower boost not available during SCOP; only default firepower boost
        if((stars < COPStars) && !SCOP) {
            atk += 10;
        } else if(stars >= 2.0) {
            atk -= 10;
        }
        
        if(COP || SCOP) {
            return atk += 10;
        }
        
        return atk;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender) {
        int def = 100;
        
        //D2D defense boost not available during SCOP; only default defense boost
        /*if((stars < COPStars) && !SCOP) {
            def += 10;
        }*/
        
        if(COP || SCOP) {
            def += 10;
        }
        
        return def;
    }
    
    //carries out Robo-Andy's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                u[i].heal(20);
            } else {
                return;
            }
        }
    }
    
    //carries out Robo-Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
    }
    
    //used to deactivate Robo-Andy's CO Power the next day
    public void deactivateCOP() {
        COP = false;
    }
    
    //Robo-Andy can't use his COP if he's charged 3 or more stars
    public void dayStart(boolean main) {
        if(stars > 3.0) {
            COPoff = true;
        } else {
            COPoff = false;
        }
    }
    
    //Robo-Andy can't use his COP if he's charged 3 or more stars
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(stars > 3.0) {
            COPoff = true;
        }
    }
    
    //Robo-Andy's units heal after combat during the SCOP
    //This includes any attack or counter-attack
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        if(SCOP) {
            owned.heal(10);
        }
    }
    
    //Robo-Andy's units explode and do 2 damage when destroyed during the SCOP
    //This is after the enemy's counter-attack (when the friendly unit is suicided)
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(SCOP && destroy) {
            enemy.damage(10, false);
        }
    }
    
    //Robo-Andy's units explode and do 2 damage when destroyed during the SCOP
    //This is after the enemy's attack (when the friendly unit is annihilated)
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(SCOP && destroy) {
            enemy.damage(10, false);
        }
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //used to deactivate Robo-Andy's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
    }
}