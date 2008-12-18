package com.customwars.officer;
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

import com.customwars.Invention;
import com.customwars.Location;
import com.customwars.Music;
import com.customwars.Options;
import com.customwars.Property;
import com.customwars.Tile;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

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
    
    private String[] COPower;            //Quotes that the CO uses when activating their CO Power
    protected String[] Victory;           //Quotes that the CO uses when victorious
    private String[] Swap;                //Quotes that the CO uses when Swapping in
    private String[] TagCOs;              //Names of COs with special tags
    private String[] TagNames;            //Names of the corresponding Tags
    private int[]    TagStars;            //Number of stars for each special tag.
    private int[]    TagPercent;          //Percent for each special tag.
    private int id;
    protected String COPName;
    protected String SCOPName;
    private int positiveLuck = 10;
    private int negativeLuck = 0;
    private int minPosLuck = -1;
    private int maxPosLuck = -1;
    private int minNegLuck = -1;
    private int maxNegLuck = -1; 
    private boolean hiddenHP = false;   //Are the units HP hidden?
    private double stars = 0.0;         //The CO's current star meter level
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
    private int counterAttack = 100;    //the CO's boost to Counter Attacks'
    protected int captureMultiplier = 100;     //the CO's boost to capturing
    private int friendlySalvage = 0;            //What percent of the destroyed allied units is salvaged for funds.
    private int enemySalvage = 0;          // What percentt of the destroyed enemy units is salvaged for funds.
    private double terrainDefenseMultiplier = 1; //the terrain defense star multiplier for this COs units
    private boolean cleanStore = true ; //cleans COstore everyday - COstore is used for conditional boosts and so on. (ie: if I was built last turn, COStore = 1; if COStore = 1, reactivate this unit
    private boolean cleanEnemyStoreBegin = true; //cleans enemyCOstore at the beginning of every day - used to store persistent enemy problems.
    private boolean cleanEnemyStoreEnd = false; //cleans enemyCOstore at the end of every day - used to store persistent enemy problems.
    private int statIndex; // MUST be used if enemyCOStore is used. Set this to army.getID() in the constructor.
    private int enemyTerrainPenalty = 0;  //the enemy loses this many terrain defense stars
    protected boolean firstStrike = false;  //when true, the CO always attacks first
    protected boolean perfectMovement = false;  //when true, all passable terrain costs 1 mp
    protected boolean snowImmunity = false;     //is this CO immune to snow?
    protected boolean rainImmunity = false;     //is this CO immune to rain (the vision -1 part)?
    protected boolean sandImmunity = false;     //is this CO immune to sandstorms?
    protected boolean piercingVision = false;   //is piercing vision on? (able to see into woods and reefs in FoW)
    private double fundingMultiplier = 100;      //This is the percent change to day to day funding this CO has (ie: 90 = 90% income, 110 = 110% income
    protected double fundingBase = 100;            //This is 'base' funding, so you can return an enemy to their normal funding.
    protected boolean alwaysDelete = false;     //can this CO always delete their units?
    protected boolean selecting = false;        //Turn selecting on to cause BattleScreen to start applying selectAction to the A key presses until selecting is turned off.    protected boolean altCostume = false;       //Is the CO wearing his or her alt costume?
    protected boolean powerDisabled = false;
    protected boolean superDisabled = false;
    private boolean altCostume = false;
    private boolean COPoff = false;            //used to determine if the CO can use the COP
    private boolean SCOPoff = false;           //used to determine if the CO can use the SCOP
    private boolean hiddenPower = false;       //used to determine if the CO's D2D information is hidden or not
    private boolean hiddenUnitInfo = false;    //used to determine if the CO's unit's information can be checked
    private boolean hiddenUnitType = false;    //used to determine if the CO's unit's graphic is displayed correctly
    private boolean hiddenGold = false;
    private boolean hiddenIntel = false;
    private boolean disruptFireDisplay = false;//used to determine if the CO's firing range can be discerned
    protected int[] unitCostMultiplier = {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100};
    public abstract int getAtk(Unit attacker, Unit defender);
    public String special1; //used to determine of the CO has a special action. woo~
    public String special2; //used to name the CO's special action
    private int bonusDamage = 0; //bonus, non-firepower effected damage Shown on the damage display.
    private int damagePenalty = 0; //penalty to firepower - shown on damage display
    private int incomePenalty = 0; //funds subtracted from income per turn.
    private boolean seeFullHP = false; //Allows the CO to see full HP reading
    private boolean hideAllHP = false; //Hides allied HP!
    private boolean mayhem = false; //If this is on, setting this army's units to "active" alows mind control
    
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
        if(getStars() >= COPStars){
            //Random r = new Random();
            logger.info(name + ": " + getCOPower()[army.getBattle().getRNG().nextInt(6)]);
            logger.info(COPName + "!");
            COPower();
            setStars(getStars() - COPStars);
            powerUses++;
            if(powerUses < 10)
                chargeMult = 5.0/(++denom);
            else
                chargeMult = 5.0/10.0;
            double temp = getStars();
            setStars(0);
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
        return getPositiveLuck();
    }
    
    public int getNegLuck(){
        return getNegativeLuck();
    }
    
    //MODIFIED STUFF
    public boolean canCOP() {
        if(getStars() >= COPStars && !isCOPoff())
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
        if(getStars() == maxStars && !isSCOPoff())
            return true;
        return false;
    }
    
    //activates the CO's Super CO Power, deals with the stars
    public void activateSCOP(){
        if(getStars() == maxStars){
            //Random r = new Random();
            logger.info(name + ": " + getCOPower()[army.getBattle().getRNG().nextInt(6)]);
            logger.info(SCOPName + "!");
            superCOPower();
            setStars(0);
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
            setStars(getStars() + (d * chargeMult));
            if(getStars() < 0)
                setStars(0);
            if(getStars() > maxStars)
                setStars(maxStars);
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
        for(int i = 0; i < getTagCOs().length; i++){
            if(getTagCOs()[i].equals(tagCO.getName())){
                return getTagStars()[i];
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
        setFundingMultiplier(change); //changes the funding multiplier
    }
    
    public void restoreFunding() {
        setFundingMultiplier(fundingBase);
    }
    
    public double getFunding() {
        return getFundingMultiplier();
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
    
    public String getCOPName() {
        return COPName;
    }

    public String getSCOPName() {
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

	public void setBonusDamage(int bonusDamage) {
		this.bonusDamage = bonusDamage;
	}

	public int getBonusDamage() {
		return bonusDamage;
	}

	public void setPositiveLuck(int positiveLuck) {
		this.positiveLuck = positiveLuck;
	}

	public int getPositiveLuck() {
		return positiveLuck;
	}

	public void setNegativeLuck(int negativeLuck) {
		this.negativeLuck = negativeLuck;
	}

	public int getNegativeLuck() {
		return negativeLuck;
	}

	public void setSCOPoff(boolean sCOPoff) {
		SCOPoff = sCOPoff;
	}

	public boolean isSCOPoff() {
		return SCOPoff;
	}

	public void setCOPoff(boolean cOPoff) {
		COPoff = cOPoff;
	}

	public boolean isCOPoff() {
		return COPoff;
	}

	public void setDamagePenalty(int damagePenalty) {
		this.damagePenalty = damagePenalty;
	}

	public int getDamagePenalty() {
		return damagePenalty;
	}

	public void setEnemySalvage(int enemySalvage) {
		this.enemySalvage = enemySalvage;
	}

	public int getEnemySalvage() {
		return enemySalvage;
	}

	public void setFriendlySalvage(int friendlySalvage) {
		this.friendlySalvage = friendlySalvage;
	}

	public int getFriendlySalvage() {
		return friendlySalvage;
	}

	public void setEnemyTerrainPenalty(int enemyTerrainPenalty) {
		this.enemyTerrainPenalty = enemyTerrainPenalty;
	}

	public void setTerrainDefenseMultiplier(double terrainDefenseMultiplier) {
		this.terrainDefenseMultiplier = terrainDefenseMultiplier;
	}

	public void setFundingMultiplier(double fundingMultiplier) {
		this.fundingMultiplier = fundingMultiplier;
	}

	public double getFundingMultiplier() {
		return fundingMultiplier;
	}

	public void setIncomePenalty(int incomePenalty) {
		this.incomePenalty = incomePenalty;
	}

	public int getIncomePenalty() {
		return incomePenalty;
	}

	public void setMinNegLuck(int minNegLuck) {
		this.minNegLuck = minNegLuck;
	}

	public int getMinNegLuck() {
		return minNegLuck;
	}

	public void setStars(double stars) {
		this.stars = stars;
	}

	public void setStatIndex(int statIndex) {
		this.statIndex = statIndex;
	}

	public int getStatIndex() {
		return statIndex;
	}

	public void setHiddenUnitType(boolean hiddenUnitType) {
		this.hiddenUnitType = hiddenUnitType;
	}

	public boolean isHiddenUnitType() {
		return hiddenUnitType;
	}

	public void setCounterAttack(int counterAttack) {
		this.counterAttack = counterAttack;
	}

	public int getCounterAttack() {
		return counterAttack;
	}

	public void setMaxNegLuck(int maxNegLuck) {
		this.maxNegLuck = maxNegLuck;
	}

	public int getMaxNegLuck() {
		return maxNegLuck;
	}

	public void setMinPosLuck(int minPosLuck) {
		this.minPosLuck = minPosLuck;
	}

	public int getMinPosLuck() {
		return minPosLuck;
	}

	public void setMaxPosLuck(int maxPosLuck) {
		this.maxPosLuck = maxPosLuck;
	}

	public int getMaxPosLuck() {
		return maxPosLuck;
	}

	public void setSwap(String[] swap) {
		Swap = swap;
	}

	public String[] getSwap() {
		return Swap;
	}

	public void setTagCOs(String[] tagCOs) {
		TagCOs = tagCOs;
	}

	public String[] getTagCOs() {
		return TagCOs;
	}

	public void setTagNames(String[] tagNames) {
		TagNames = tagNames;
	}

	public String[] getTagNames() {
		return TagNames;
	}

	public void setTagPercent(int[] tagPercent) {
		TagPercent = tagPercent;
	}

	public int[] getTagPercent() {
		return TagPercent;
	}

	public void setAltCostume(boolean altCostume) {
		this.altCostume = altCostume;
	}

	public boolean isAltCostume() {
		return altCostume;
	}

	public void setCleanEnemyStoreBegin(boolean cleanEnemyStoreBegin) {
		this.cleanEnemyStoreBegin = cleanEnemyStoreBegin;
	}

	public boolean isCleanEnemyStoreBegin() {
		return cleanEnemyStoreBegin;
	}

	public void setCleanStore(boolean cleanStore) {
		this.cleanStore = cleanStore;
	}

	public boolean isCleanStore() {
		return cleanStore;
	}

	public void setCleanEnemyStoreEnd(boolean cleanEnemyStoreEnd) {
		this.cleanEnemyStoreEnd = cleanEnemyStoreEnd;
	}

	public boolean isCleanEnemyStoreEnd() {
		return cleanEnemyStoreEnd;
	}

	public void setCOPower(String[] cOPower) {
		COPower = cOPower;
	}

	public String[] getCOPower() {
		return COPower;
	}

	public void setHideAllHP(boolean hideAllHP) {
		this.hideAllHP = hideAllHP;
	}

	public boolean isHideAllHP() {
		return hideAllHP;
	}

	public void setDisruptFireDisplay(boolean disruptFireDisplay) {
		this.disruptFireDisplay = disruptFireDisplay;
	}

	public boolean isDisruptFireDisplay() {
		return disruptFireDisplay;
	}

	public void setHiddenIntel(boolean hiddenIntel) {
		this.hiddenIntel = hiddenIntel;
	}

	public boolean isHiddenIntel() {
		return hiddenIntel;
	}

	public void setHiddenGold(boolean hiddenGold) {
		this.hiddenGold = hiddenGold;
	}

	public boolean isHiddenGold() {
		return hiddenGold;
	}

	public void setHiddenPower(boolean hiddenPower) {
		this.hiddenPower = hiddenPower;
	}

	public boolean isHiddenPower() {
		return hiddenPower;
	}

	public void setTagStars(int[] tagStars) {
		TagStars = tagStars;
	}

	public int[] getTagStars() {
		return TagStars;
	}

	public void setMayhem(boolean mayhem) {
		this.mayhem = mayhem;
	}

	public boolean isMayhem() {
		return mayhem;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setHiddenHP(boolean hiddenHP) {
		this.hiddenHP = hiddenHP;
	}

	public boolean isHiddenHP() {
		return hiddenHP;
	}

	public void setHiddenUnitInfo(boolean hiddenUnitInfo) {
		this.hiddenUnitInfo = hiddenUnitInfo;
	}

	public boolean isHiddenUnitInfo() {
		return hiddenUnitInfo;
	}

	public void setSeeFullHP(boolean seeFullHP) {
		this.seeFullHP = seeFullHP;
	}

	public boolean isSeeFullHP() {
		return seeFullHP;
	}
}
