package cwsource;
/*
 *Walter.java
 *Author: -
 *Contributors: -
 *Creation: -
 *The Walter class is used to create an instance of the JC CO Walter
 */

public class Walter extends CO {
    private int enemyDmg = 0;
    private int day;
    //constructor
    public Walter() {
        name = "Walter";
        id = 64;
        
        String CObiox = "Commander of the Capitol Guard of Jade Cosmos. He excels in survival and sheer tenacity.";             //Holds the condensed CO bio'
        String titlex = "The Long Standing Guardian";
        String hitx = "Tongue Twisters"; //Holds the hit
        String missx = "Colloquialisms"; //Holds the miss
        String skillStringx = "Using Jade Cosmos' intelligence division, Walter can view units' HP more accurately than other COs.";
        String powerStringx = "Enemies take additional damage when they attack."; //Holds the Power description
        String superPowerStringx = "Instantly depletes all enemy ammunition. Enemies cannot view any unit's HP and suffers minor damage."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Walter's sort of precise. He can see" +
                "the exact HP of his units, and his  " +
                "power allows him to reflect damage. " +
                "His super drains ammo and hides all " +
                "HP readouts." +
                "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Amy","Olaf","Carrie","Koshi"}; //Names of COs with special tags
        String[] TagNamesx = {"Lord and Lady","Hoarfrost","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,105}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Greater opponents have burnt out against me." ,
         "For the Cosmos' sake, I will endure you!" ,
         "I am the most stalwart foe that you will ever face!" ,
         "Morale is the key to victory, but you seem rather....ill equipped." ,
         "A battle does not determine who is right. It determines who is left. Me, of course.",
         "The spirit of Royal Cosmos will carry us for eternity!" };
        
        String[] Victoryx =
        {"Another battle has been won. Royal Cosmos is safe for now." ,
         "You have been outwitted, outmatched and outmaneuvered." ,
         "Hah, yet again I have stood the test of time!"};
        
        String[] Swapx =
        {"It's time to wear them down." ,
         "...Just a little longer."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Memento";
        SCOPName = "Magnum Opus";
        COPStars = 3.0;
        maxStars = 6.0;
        
        //see exact HP
        seeFullHP = true;
        
        this.army = army;
        style = JADE_COSMOS;
        day = -1;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        int atk = 100;
        
        if(SCOP || COP) {
            atk += 10;
        }
        
        return atk;
    }
    
    public void setChange(Unit u) {
        
    }
    
    public void unChange(Unit u) {
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender) {
        int def = 100;
        
        if(SCOP || COP) {
            def += 10;
        }
        
        return def;
    }
    
//carries out Walter's CO Power, called by CO.activateCOP()
    public void COPower() {
        enemyDmg = 0;
        COP = true;
    }
    
//carries out Walter's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        hideAllHP = true;
        day = 0;
        Army[] allArmies = army.getBattle().getArmies();
        
        if(allArmies != null) {
            for(int i = 0; i < allArmies.length; i++) {
                Army pickArmy = allArmies[i];
                
                if(pickArmy != null && pickArmy.getSide() != army.getSide()) {
                    Unit[] pickUnits = pickArmy.getUnits();
                    
                    if(pickUnits != null) {
                        for(int j = 0; j < pickUnits.length; j++) {
                            Unit targUnit = pickUnits[j];
                            
                            if(targUnit != null) {
                                if(targUnit.maxAmmo > 0) {
                                    targUnit.ammo = 0;
                                    
                                    int leftoverHP = targUnit.getHP() % 10;
                                    
                                    targUnit.damage(leftoverHP, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void dayStart(boolean main) {
        if(day != -1) {
            day++;
            if(day > 1) {
                hideAllHP = false;
                day = -1;        }
        }
    }
//used to deactivate Walter's CO Power the next day
    public void deactivateCOP() {
        COP = false;
    }
    
//used to deactivate Walter's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
    }
    
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) 
    {
        if(COP && !attack) 
{
            if(damage > 0 && damage < owned.getHP()) {
                enemyDmg = damage;
            } else if(damage > owned.getHP()){
                enemyDmg = owned.getHP();
            }else{
                enemyDmg = 0;
            }
        }
    }
    
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) 
    {
        if(COP && enemyDmg > 0)
        {
            int returnDmg = (int)(enemyDmg * 0.4);
            
            enemy.damage(returnDmg, false);
            
            enemyDmg = 0;
        }
    }
}
