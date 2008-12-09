package com.customwars;
/*
 *Amy.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: Xaif
 *The Amy class is used to create an instance of the Royal Cosmos CO Amy
 */

public class Amy extends CO{
    boolean counter; //Used to disable counter
    boolean defending;
    boolean sustain = false;
    //constructor
    public Amy() {
        name = "Amy";
        id = 50;
        
        String CObiox =
                "The Commander in Chief of Jade Cosmos. A" +
                "former physicist who was thrust into war" +
                " emerging as a hardened naval commander." +
                "Intelligent and loyal. ";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Hover Wrath";
        String hitx = "Quantum Theory"; //Holds the hit
        String missx = "Hawke"; //Holds the miss
        String skillStringx =
                "Particularly adept with Jade Cosmos'    " +
                "signature units, she can command        " +
                "hovercraft units at optimal level. Reefs" +
                "pose no problem for Amy's units. ";
        String powerStringx =
                "Enemy non-infantry units become      " +
                "unreliable. Hovercraft units are     " +
                "strengthened, and they gain one extra" +
                "movement and extended one range. "; //Holds the Power description
        String superPowerStringx =
                "Enemy non-infantry units are weakened" +
                "and cannot counterattack. Hovercraft " +
                "units receive a firepower boost. "; //Holds the Super description
        String intelx = "Amy's a naval commander who is      " +
                        "particularly adept with hovercrafts." +
                        "Her powers enhance hovercraft units " +
                        "and also hinder enemy machinery.";//Holds CO intel on CO select menu, 6 lines max
        intel = intelx;
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Drake", "Walter", "Jared", "Hawke", "Carrie"};              //Names of COs with special tags
        String[] TagNamesx  = {"Unwavering Debt", "Lord and Lady", "Atomisation", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {2,1,1,0,0};           //Number of stars for each special tag.
        int[] TagPercentx = {115,110,110,70, 105};       //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"EMP? Why, it stands for Electromagnetic Pulse. You'll soon understand.",
         "Quantum… you probably don’t even know what it means.",
         "Do you know how much energy splitting of the atom produces? Let’s see.",
         "Allow me demonstrate how flimsy hovercrafts are not.",
         "For your sake, I hope your machines are magnetically shielded.",
         "Without science we wouldn’t be doing what we are now." };
        
        String[] Victoryx =
        {"That one was for you Lara.",
         "Theoretically speaking, it appears you lost.",
         "For lack of a better word, I feel fully ionised!" };
        
        String[] Swapx =
        {"Ok, I’ll put some theory into practice.",
         "Time to break some bonds."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Fluid Dynamics";
        SCOPName = "Photon Discharge";
        COPStars = 3.0;
        maxStars = 8.0;
        this.army = army;
        style = JADE_COSMOS;
        cleanEnemyStoreBegin = false;
        cleanEnemyStoreEnd = true;
    }
    
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(attacker.moveType == attacker.MOVE_HOVER) {
            if(SCOP || COP)
                return 130;
            return 120;
        }
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(attacker.moveType != attacker.MOVE_INFANTRY && attacker.moveType != attacker.MOVE_MECH && SCOP)
            return 200;
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){
        u.price = (costMultiplier*unitCostMultiplier[u.unitType])*u.price/10000;
    }
    
    public void unChange(Unit u){
        u.price = 10000*u.price/(costMultiplier*unitCostMultiplier[u.unitType]);
    }
    
    public void dayStart(boolean main){
        if(main) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new double[] {-1,-1,-1,-1,1,1,1,-1,-1,1});
                }
            }
        }
        if(sustain)
        {
        Army[] armies = army.getBattle().getArmies();
        for(int i = 0; i<armies.length; i++) {
            if(armies[i].getSide()!=army.getSide()) {
                Unit[] u = armies[i].getUnits();
                for(int s = 0; s<u.length; s++) {
                    if(u[s].moveType != u[s].MOVE_INFANTRY && u[s].moveType != u[s].MOVE_MECH) {
                        u[s].attackPenalty -= u[s].enemyCOstore[statIndex][0];
                    }
                }
            }
        }
        sustain = false;
        }
    }
    public void dayEnd(boolean main){
        if(main&& army.getBattle().getWeather() == 0) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().restoreCost();
                }
            }
        }
    }
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        if(u != null) {
            for(int i = 0; i<u.length; i++) {
                if(u[i].getUType() == 26) {
                    u[i].maxRange++;
                    u[i].changed = true;
                }
                if(u[i].getUType() == 25) {
                    u[i].move++;
                    u[i].changed = true;
                }
            }
        }
        unitCostMultiplier[26] -= 30;
        unitCostMultiplier[25] -= 30;
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        if(u != null) {
            for(int i = 0; i<u.length; i++) {
                if(u[i].getUType() == 26 && u[i].changed) {
                    u[i].maxRange--;
                    u[i].changed = false;
                }
                if(u[i].getUType() == 25 && u[i].changed) {
                    u[i].move--;
                    u[i].changed = false;
                }
            }
        }
        
        unitCostMultiplier[26] += 30;
        unitCostMultiplier[25] += 30;
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        //if Amy is attacked by non-infantry, the damage is nulled.
        if(SCOP && enemy.moveType != enemy.MOVE_INFANTRY && enemy.moveType != enemy.MOVE_MECH)
            counter = true;
        //IF the cop is active, and Amy is not attacking with an infantry unit.
        
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        counter = false;
    }
}



