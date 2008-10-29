package cwsource;
/*
This 'broken' version is cool because it has code for real '+4 max HP'
 */

import java.util.Random;

public class Napoleon extends CO{
    boolean prevent; //Damage prevention
    //constructor
    public Napoleon() {
        name = "Napoleon";
        id = 54;
        
        String CObiox = "The current commander of the Black Hole army. Believes Black Hole's ingenuity gives it a right to control the world.";             //Holds the condensed CO bio'
        String titlex = "Eagle Scout";
        String hitx = "Sudoku"; //Holds the hit
        String missx = "Horror Movies"; //Holds the miss
        String skillStringx = "Unit's defensive capabilities are unaffected by loss of HP";
        String powerStringx = "Ground units restore 1.5 HP of health for each star inherent to their terrain. Air and naval units restore 3 HP of health."; //Holds the Power description
        String superPowerStringx = "Units fight as though they were four HP stronger and inflict extra damage relative to thier terrain cover."; //Holds the Super description
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
        {"Me and what army, you ask?  I have many more soldiers than you think.",
         "Size does not matter in a battle of wills.",
         "I can take any attack.  You would be wrong to test that statement.",
         "You can give up now and avoid further bloodshed.  Either way, though, my troops simply won't die.",
         "My casualties are not dead, but simply waiting to fight again.",
         "I need not worry about attrition.  On the other hand, you do.",};
        //Jezum crow, all these quotes are "I can X, although Y"
        /*lol, I would suggest
         *"Me and what army? Look around you!"
         *eh, okay
         *"My army can take any attack!"
         *"Try all you want, my troops won't die!"
         *uh, what? "lol, they're just sleeping."
         *"A war of attrition? Laughable."
         */
        
        String[] Victoryx =
        {"The survival of the Black Hole army is assured.",
         "My casualties are recovering.  Yours are not so lucky.",
         "Why were you so confident?  Success is earned, not granted."};
        //Eh, "Success is earned, not granted" is fine.
        
        String[] Swapx =
        {"Our losses are unacceptable.  I will fix that.",
         "Stand aside; I will deal with them.",};
        //Our losses are unacceptable!
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Flak", "Adder", "Lash", "Graves", "Kindle"}; //Names of COs with special tags
        String[] TagNamesx = {"Brain and Brawn", "Snakes in the Grass", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,105,80}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Survival Tactics";
        SCOPName = "Last Stand"; //moar liek, first stand
        COPStars = 3.0;
        maxStars = 8.0;
        this.army = army;
        style = BLACK_HOLE;
        
        prevent = false;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110 + (int)(attacker.COstore[0]*1.1);
        return 100;
        
    }
    
    public void setChange(Unit u){
        if(SCOP)
            u.COstore[0] = 40;
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Terrain def normally
        int defense = 0;
        if(prevent)
            return 200;
        int SCOPdefense = 0;
        if(defender.moveType != defender.MOVE_AIR) {
            defense = (10-defender.getDisplayHP())*army.getBattle().getMap().find(defender).getTerrain().getDef();
            //Terrain defense as if the unit had 14 units instead of just 10.
            SCOPdefense= defense + 4*army.getBattle().getMap().find(defender).getTerrain().getDef();
        }
        if (SCOP)
            return 110 + SCOPdefense;
        //eh, check this.
        if (COP)
            return 110 + defense;
        else
            return 100 + defense;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                if(u[i].moveType == u[i].MOVE_SEA || u[i].moveType == u[i].MOVE_TRANSPORT) { //if sea or transport sea unit:
                    if(!u[i].isInTransport())
                        u[i].heal(30);
                } else if(u[i].moveType == u[i].MOVE_AIR) { //if air unit
                    if(!u[i].isInTransport())
                        u[i].heal(30);
                } else { //Anywhar, if land or otherwise unit: (ie, lol pipe units)
                    if(!u[i].isInTransport()) {
                        u[i].heal(15 * u[i].getMap().find(u[i]).getTerrain().getDef());
                    }
                }
            } else {
                return;
            }
        }
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        Unit[] u = army.getUnits();
        int difference;
        
        for(int i = 0; i<u.length; i++) {
            u[i].COstore[0] = 40;
            checkHealth(u[i]);
        }
        //This block heals a unit if it has less than full life.
    }
    private void checkHealth(Unit u) {
        
        int difference = 100 - u.getHP(); //Amount of missing HP the unit has
        if(difference <= u.COstore[0]) { //If the missing HP is less than the 4 'reserve' HP
            u.heal(difference);
            u.COstore[0] -= difference;
        }
        if(difference > u.COstore[0]) {
            u.heal(u.COstore[0]);
            u.COstore[0] = 0;
        }
    }
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    
    
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        if(BaseDMG.findBase(owned.getUType(), enemy.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
        { //If this unit can fire with primary weapon.
        if(attack && owned.ammo>0 && (owned.getMType() != owned.MOVE_AIR))
        { //If this unit is not an air unit, and has primary weapon ammo left
            enemy.damage(5 * owned.getMap().find(owned).getTerrain().getDef(), false);
        }
        
        }
        if(damage>owned.getHP() && !attack) //If Napoleon is blocking and will take lethal damage
        {
            if(damage<owned.getHP()+owned.COstore[0]) { // If the damage is less than the
                owned.damage(damage-owned.getHP(), false);
                owned.charge(damage-owned.getHP(), enemy);
                owned.salvage(damage-owned.getHP(), enemy);
                prevent = true;
            }
        }
        
    }
    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(BaseDMG.findBase(owned.getUType(), enemy.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
        { //If this unit can fire with primary weapon.
        if(attack && owned.ammo>0 && (owned.getMType() != owned.MOVE_AIR))
        { //If this unit is not an air unit, and has primary weapon ammo left
            enemy.damage(5 * owned.getMap().find(owned).getTerrain().getDef(), false);
        }
        }
        if(damage>owned.getHP() && !attack) //If Napoleon is blocking and will take lethal damage
        {
            if(damage<owned.getHP()+owned.COstore[0]) { // If the damage is less than the
                owned.damage(damage-owned.getHP(), false);
                owned.charge(damage-owned.getHP(), enemy);
                owned.salvage(damage-owned.getHP(), enemy);
                prevent = true;
            }
        }
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        prevent = false;
        checkHealth(owned);
    }
    
}
