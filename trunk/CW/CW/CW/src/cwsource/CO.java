package cwsource;
/*
 *CO.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: July 1, 2006, 1:27 PM
 *CO is an abstract class for Commanding Officers
 */

//import java.util.Random;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CO implements Serializable{
    //Style Constants
    protected final int ORANGE_STAR = 0;
    protected final int BLUE_MOON = 1;
    protected final int GREEN_EARTH = 2;
    protected final int YELLOW_COMET = 3;
    protected final int BLACK_HOLE = 4;
    protected final int JADE_COSMOS = 5;
    protected final int AMBER_CORONA = 6;
    protected final int PARALLEL_GALAXY = 7;
    
    protected String name;                //Holds the CO's name
    protected String CObio;             //Holds the condensed CO bio'
    protected String title;
    protected String pos; //The CO's army position
    protected String hit; //Holds the hit
    protected String miss; //Holds the miss
    protected String skillString;
    protected String skillStats;
    protected String powerString; //Holds the Power description
    protected String powerStats;
    protected String superPowerString; //Holds the Super description
    protected String superPowerStats;
    protected String intel; //Holds the CO intel shown on the CO select screen
    protected String[] defeat;
	final static Logger logger = LoggerFactory.getLogger(CO.class); 
    
    protected String[] COPower;            //Quotes that the CO uses when activating their CO Power
    protected String[] Victory;           //Quotes that the CO uses when victorious
    protected String[] Swap;                //Quotes that the CO uses when Swapping in
    protected String[] TagCOs;              //Names of COs with special tags
    protected String[] TagNames;            //Names of the corresponding Tags
    protected int[]    TagStars;            //Number of stars for each special tag.
    protected int[]    TagPercent;          //Percent for each special tag.
    protected int id;
    protected String COPName;
    protected String SCOPName;
    protected int positiveLuck = 10;
    protected int negativeLuck = 0;
    protected int minPosLuck = -1;
    protected int maxPosLuck = -1;
    protected int minNegLuck = -1;
    protected int maxNegLuck = -1; 
    protected boolean hiddenHP = false;   //Are the units HP hidden?
    protected double stars = 0.0;         //The CO's current star meter level
    protected double COPStars;            //The number of stars required to use a CO Power
    protected double maxStars;            //The number of stars required to use a Super CO Power, and the maximum
    protected double chargeMult = 1.0;    //The charge multiplier, this starts at 1 and goes down as the CO uses their powers
    protected int denom = 5;              //The charge multiplier's denominator, used to calculate it
    protected int powerUses = 0;          //Number of times the CO has used a power
    protected boolean COP = false;        //is the CO Power active?
    protected boolean SCOP = false;       //is the Super CO Power active?
    protected Army army;                  //A reference to the Army that the CO commands
    protected int style;                  //The visual style of the CO's units (0=Orange Star 1=Blue Moon , etc.)
    protected int repairHp = 2;           //the number of hp repaired by friendly property (generally 2)
    protected int costMultiplier = 100;   //the CO's cost multiplier (usually 100%)
    protected int counterAttack = 100;    //the CO's boost to Counter Attacks'
    protected int captureMultiplier = 100;     //the CO's boost to capturing
    protected int friendlySalvage = 0;            //What percent of the destroyed allied units is salvaged for funds.
    protected int enemySalvage = 0;          // What percentt of the destroyed enemy units is salvaged for funds.
    protected double terrainDefenseMultiplier = 1; //the terrain defense star multiplier for this COs units
    protected boolean cleanStore = true ; //cleans COstore everyday - COstore is used for conditional boosts and so on. (ie: if I was built last turn, COStore = 1; if COStore = 1, reactivate this unit
    protected boolean cleanEnemyStoreBegin = true; //cleans enemyCOstore at the beginning of every day - used to store persistent enemy problems.
    protected boolean cleanEnemyStoreEnd = false; //cleans enemyCOstore at the end of every day - used to store persistent enemy problems.
    protected int statIndex; // MUST be used if enemyCOStore is used. Set this to army.getID() in the constructor.
    protected int enemyTerrainPenalty = 0;  //the enemy loses this many terrain defense stars
    protected boolean firstStrike = false;  //when true, the CO always attacks first
    protected boolean perfectMovement = false;  //when true, all passable terrain costs 1 mp
    protected boolean snowImmunity = false;     //is this CO immune to snow?
    protected boolean rainImmunity = false;     //is this CO immune to rain (the vision -1 part)?
    protected boolean sandImmunity = false;     //is this CO immune to sandstorms?
    protected boolean piercingVision = false;   //is piercing vision on? (able to see into woods and reefs in FoW)
    protected double fundingMultiplier = 100;      //This is the percent change to day to day funding this CO has (ie: 90 = 90% income, 110 = 110% income
    protected double fundingBase = 100;            //This is 'base' funding, so you can return an enemy to their normal funding.
    protected boolean alwaysDelete = false;     //can this CO always delete their units?
    protected boolean selecting = false;        //Turn selecting on to cause BattleScreen to start applying selectAction to the A key presses until selecting is turned off.    protected boolean altCostume = false;       //Is the CO wearing his or her alt costume?
    protected boolean powerDisabled = false;
    protected boolean superDisabled = false;
    protected boolean altCostume = false;
    protected boolean COPoff = false;            //used to determine if the CO can use the COP
    protected boolean SCOPoff = false;           //used to determine if the CO can use the SCOP
    protected boolean hiddenPower = false;       //used to determine if the CO's D2D information is hidden or not
    protected boolean hiddenUnitInfo = false;    //used to determine if the CO's unit's information can be checked
    protected boolean hiddenUnitType = false;    //used to determine if the CO's unit's graphic is displayed correctly
    protected boolean hiddenGold = false;
    protected boolean hiddenIntel = false;
    protected boolean disruptFireDisplay = false;//used to determine if the CO's firing range can be discerned
    protected int[] unitCostMultiplier = {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100};
    public abstract int getAtk(Unit attacker, Unit defender);
    public String special1; //used to determine of the CO has a special action. woo~
    public String special2; //used to name the CO's special action
    int bonusDamage = 0; //bonus, non-firepower effected damage Shown on the damage display.
    int damagePenalty = 0; //penalty to firepower - shown on damage display
    int incomePenalty = 0; //funds subtracted from income per turn.
    protected boolean seeFullHP = false; //Allows the CO to see full HP reading
    protected boolean hideAllHP = false; //Hides allied HP!
    protected boolean mayhem = false; //If this is on, setting this army's units to "active" alows mind control
    
    //used to get the attack bonus for damage calculation against inventions
    public int getInventionAtk(Unit attacker, Invention inv){
        return getAtk(attacker,null);
    }
    
    //used to get the defense bonus for damage calculation
    public abstract int getDef(Unit attacker, Unit defender);
    
    //carries out the CO's CO Power, called by CO.activateCOP()
    public abstract void COPower();
    
    //carries out the CO's Super CO Power, called by CO.activateSCOP()
    public abstract void superCOPower();
    
    //used to deactivate the CO's CO Power the next day
    public abstract void deactivateCOP();
    
    //used to deactivate the CO's Super CO Power the next day
    public abstract void deactivateSCOP();
    
    public abstract void setChange(Unit u);
    
    public abstract void unChange(Unit u);
    
    public void propChange(Property p) { };
    
    public void propUnChange(Property p) { };
    
    //activates the CO's CO Power, deals with the stars
    public void activateCOP(){
        if(stars >= COPStars){
            //Random r = new Random();
            logger.info(name + ": " + COPower[army.getBattle().getRNG().nextInt(6)]);
            logger.info(COPName + "!");
            COPower();
            stars -= COPStars;
            powerUses++;
            if(powerUses < 10)
                chargeMult = 5.0/(++denom);
            else
                chargeMult = 5.0/10.0;
            double temp = stars;
            stars = 0;
            this.charge(temp);
            
            if(Options.isMusicOn()){
                Music.startPowerMusic(0,style);
            }
        }
    }
    
    public void dayStart(boolean main){}
    
    public void dayEnd(boolean main ) {}
    
    public double getStars(){
        return stars;
    }
    
    
    public int getPosLuck(){
        return positiveLuck;
    }
    
    public int getNegLuck(){
        return negativeLuck;
    }
    
    //MODIFIED STUFF
    public boolean canCOP() {
        if(stars >= COPStars && !COPoff)
            return true;
        return false;
    }
    
    public boolean isCOP(){
        return COP;
    }
    
    public boolean isSCOP(){
        return SCOP;
    }
    
    //MODIFIED STUFF
    public boolean canSCOP() {
        if(stars == maxStars && !SCOPoff)
            return true;
        return false;
    }
    
    //activates the CO's Super CO Power, deals with the stars
    public void activateSCOP(){
        if(stars == maxStars){
            //Random r = new Random();
            logger.info(name + ": " + COPower[army.getBattle().getRNG().nextInt(6)]);
            logger.info(SCOPName + "!");
            superCOPower();
            stars = 0;
            powerUses++;
            if(powerUses < 10)
                chargeMult = 5.0/(++denom);
            else
                chargeMult = 5.0/10.0;
            if(Options.isMusicOn()){
                if(army.getTag() == 0){
                    Music.startPowerMusic(1,style);
                }
            }
        }
    }
    
    //used to charge a CO's star meter
    public void charge(double d){
        if(army.getBattle().getBattleOptions().isCOP()){
            stars += (d * chargeMult);
            if(stars < 0)
                stars = 0;
            if(stars > maxStars)
                stars = maxStars;
        }
    }
    
    //returns the CO's name
    public String getName(){
        return name;
    }
    
    //Set's the CO's Army, used in the Army constructor
    public void setArmy(Army a){
        army = a;
    }
    
    //Gets the COs army
    public Army getArmy(){
        return army;
    }
    
    //returns the visual style of the CO's units
    public int getStyle(){
        return style;
    }
    
    public int getMaxStars(){
        return (int)(maxStars+.5);
    }
    
    public int getCOPStars(){
        return (int)(COPStars+.5);
    }
    
    public int getRepairHp(){
        return repairHp;
    }
    
    public int getCostMultiplier(){
        return costMultiplier;
    }
    
    public int getCaptureMultiplier(){
        return captureMultiplier;
    }
    
    public int getEnemyTerrainPenalty(){
        return enemyTerrainPenalty;
    }
    
    public double getTerrainDefenseMultiplier(){
        return terrainDefenseMultiplier;
    }
    
    public boolean hasFirstStrike(){
        return firstStrike;
    }
    
    public boolean hasPerfectMovement(){
        return perfectMovement;
    }
    
    public boolean isSnowImmune(){
        return snowImmunity;
    }
    
    public boolean isRainImmune(){
        return rainImmunity;
    }
    
    public boolean isSandImmune(){
        return sandImmunity;
    }
    
    public boolean isPiercingVision(){
        return piercingVision;
    }
    public int getTagAffinityStars(CO tagCO){
        if(tagCO == null)return 0;
        for(int i = 0; i < TagCOs.length; i++){
            if(TagCOs[i].equals(tagCO.getName())){
                return TagStars[i];
            }
        }
        return 0;
    }
    /*Unit owned is the unit this CO owns. Enemy is the enemy. Damage is damage dealt
     *Attack is the unit attacking
     *(When counterattacking, attack is true for the counterattacking unit)
     *For firstStrikeCheck and afterAttackAction, attack is the unit that initiated the attack.
     */
    
    //First Strike Check is soley for if you have conditional first strikes that are happening.
    public void firstStrikeCheck(Unit owned, Unit enemy, boolean attack){}
    //Before attack occurrs right before damage is calculated. Damage is the damage the attack is going to do, prior modifications.
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack){}
    //AfterAttack is after ammo, salvage, charging, etc. has been done.
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack){}
    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean attack){}
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack){}
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack){}
    /*Performs an ability after each action. index is the action performed. Called after the unit becomes 'waited'
     *0 = wait, 1 = fire, 2 = capture, 3 = APC resupply unit, 4 = join, 5 = load, 6 = unload 1 or 2 units,
     *8 = fire silo, 9 = explode, 10 = repair, 11 = Subdive, 12 = subrise, 13 = stealth hide, 14 = stealth appear
     *15 = build new unit 16 = APC finished resupplying 17 = ambush during unloading 18 = ordinary ambush
     *19 = an Oozium moves somewhere 20 = fire on invention 21 = city repair 22 = Special ability 1 23 = special ability 2
     *NOT SUPPORTED: 7 = unload two units, however, code treats 6 as unload both one and two units.
     *Unit u is the unit that performed the action.
     *Unit repaired is *only* used in actions 3, 6 and 10, APC resupply, unload, repair
     *[URU NOTE: I made the transport "repaired" and the loaded unit u for action 5]
     *Please check before attempting to preform things upon repaired!
     *
     *Side note: afterAction is called twice when unloading two units, four times when supplying four units
     */
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
    }
    
    //If the enemy does something.
    //IMPORTANT: When a TRAP! occurs, this function is called; the Unit u is the trapped unit, and the Unit repaired is the trapper
    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main) {
    }
    
    public void setFunding(float change) {
        fundingMultiplier = change; //changes the funding multiplier
    }
    
    public void restoreFunding() {
        fundingMultiplier = fundingBase;
    }
    
    public double getFunding() {
        return fundingMultiplier;
    }
    
    public boolean canAlwaysDelete(){
        return alwaysDelete;
    }
    //Is the person currently selecting?
    public boolean isSelecting() {
        return selecting;
    }
    
    public void selectAction(Tile t) {
        
    }
    public boolean validSelection(Tile t) {
        return false;
    }
    public void invalidSelection() //IF they hit the wrong button
    {
        selecting = false;
    }
    public void cancelSelection() //If they press B
    {
        selecting = false;
    }
    public void enemyDayStart(boolean main ){}
    
    public void enemyDayEnd(boolean main ) {}
    
    public String getBio(){
        return CObio;
    }
    public String getHit(){
        return hit;
    }
    public String getTitle(){
        return title;
    }
    public String getPos(){
        return pos;
    }
    public String getMiss(){
        return miss;
    }
    public String getD2D(){
        return skillString;
    }
    public String getCOPString(){
        return powerString;
    }
    public String getSCOPString(){
        return superPowerString;
    }
    public String getIntel(){
        return intel;
    }
    
    String getCOPName() {
        return COPName;
    }

    String getSCOPName() {
        return SCOPName;
    }
    
    String getD2DStats() {
        return skillStats;
    }
    
    String getCOPStats() {
        return powerStats;
    }
    
    String getSCOPStats() {
        return superPowerStats;
    }
    
    public int[] getUnitCostMultiplier(){
        return unitCostMultiplier;
    }
    //Call to see if a unit can be the target of the special ability
    public boolean canUseSpecial1(Unit owned){
        return false;
    }
    //Call to see if a unit is in range of a special ability (used for drawing)
    public boolean canTargetSpecial1(Unit owned, Location target) {
        return false;
    }
    public void useSpecial1(Unit owned, Location target){
        logger.info("not overridden!");
    }
    //Call to see if a unit can be the target of the special ability
    public boolean canUseSpecial2(Unit owned){
        return false;
    }
    //Call to see if a unit is in range of a special ability (used for drawing)
    public boolean canTargetSpecial2(Unit owned, Location target) {
        return false;
    }
    public void useSpecial2(Unit owned, Location target){
        
    }
}
