package com.customwars.unit;

/*
 *Unit.java
 *Author: Adam Dziuk
 *Contributors: Urusan, Albert Lai
 *Creation: June 24, 2006, 3:09 PM
 *Unit is an abstract class for units
 */

import java.io.*;
import org.omg.CORBA.BAD_CONTEXT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.BaseDMG;
import com.customwars.Options;
import com.customwars.map.Map;
import com.customwars.map.location.Invention;
import com.customwars.map.location.Locatable;
import com.customwars.map.location.Location;
import com.customwars.map.location.Path;
import com.customwars.map.location.Property;
import com.customwars.ui.Animation;

public abstract class Unit implements Locatable, Serializable{
    //moveType 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
    public final int MOVE_INFANTRY = 0;
    public final int MOVE_MECH = 1;
    public final int MOVE_TREAD = 2;
    public final int MOVE_TIRE = 3;
    public final int MOVE_AIR = 4;
    public final int MOVE_SEA = 5;
    public final int MOVE_TRANSPORT = 6;
    final protected int MOVE_OOZIUM = 7;
    public final int MOVE_PIPE = 8;
    public final int MOVE_HOVER = 9;
	final static Logger logger = LoggerFactory.getLogger(Unit.class); 
	
    protected int hP = 100; //HP (out of 100)
    private int moveType; //move type
    private int ammo;     //current ammunition
    private int maxAmmo;  //maximum ammunition
    protected int unitType; //unit's type, used by BaseDMG
    protected int price;    //price (factors into repair costs)
    private int vision;   //vision range
    private int gas;      //current gas
    private int maxGas;   //maximum gas
    private int dailyGas = 0; //Amount of Gas used daily
    private int move;     //total MP
    protected int minRange; //minimum range (1 for directs)
    private int maxRange; //maximum range (1 for directs)
    protected String name;  //Unit's name
    private Location loc; //Unit's location
    protected Army army;    //Unit's army
    protected Map map;      //The map the unit is on
    public MoveTraverse moveRange;   //Unit's current movement range
    protected boolean active;           //is the unit able to be selected?
    protected double starValue;         //StarValue of the unit.
    private boolean moved = false;    //has the unit moved?
    private boolean changed = false;  //has the unit been changed?
    protected boolean hidden = false;   //is the unit hidden?
    private boolean dived = false;    //is the unit dived/hiding?
    protected boolean detected = false; //has this unit been detected by a spyplane?
    protected Path unitPath;            //the path the unit takes to get from point A to point B
    protected boolean inTransport = false;  //is the unit in a transport?
    private boolean paralyzed = false; //is the unit paralyzed (auto-waits next turn)
    private int COstore[] = new int[10]; // This stores stuff (Sabaki's damage based defense boost, for one) that the
    //COs might alter on a changing Unit to Unit basis.
    private int altCOstore[] = new int[10];
    private float repairMod = 1; //This stores the temporary price modifications (Used for Falcone's SCOP)
    private int[][] enemyCOstore = new int[20][10]; //supports up to 20 armies. woop.
    private int[][] altEnemyCOstore = new int [20][10];
    private boolean noResupply = false; //True means that this unit cannot use Resupply!
    private boolean noRepair = false; //True means this unit cannot use Repair!
    private boolean noResupplied = false; //Can be repaired?
    private boolean noRepaired = false; //Can be resupplied?
    private boolean noCityRepair = false;
    private boolean noCityResupply = false;
    private boolean noFire = false;
    private boolean noCapture = false;
    private boolean noLaunch = false;
    private boolean noDive = false;
    private boolean noRise = false;
    private boolean noHide = false;
    private boolean noAppear = false;
    private boolean noExplode = false;
    private boolean noUnload = false;
    private boolean noJoin = false;
    private boolean noLoad = false;
    private boolean noSpecial1 = false;
    private boolean noSpecial2 = false;
    private boolean noWait = false;
    private float fuelMult =1; //This determines how much fuel this unit burns, in addition to normal fuel.
    protected Unit trapper;
    private boolean perfectMovement = false; //Does this unit have perfect movement?
    private int defensePenalty = 0; //Penalty to defense
    private int attackPenalty = 0; //Penalty to offense
    
    private boolean moving = false;
    private int direction = -1;//whether this unit is being moved or not (and what direction)
    //constructor
    public Unit(){
    }
    
    //constructor
    public Unit(Location l, Army arm, Map m) {
        setLoc(l);
        arm.addUnit(this);
        army = arm;
        map = m;
        m.addUnit(this);
        //trapper = null;
    }
    
    //Fire - the Method used to initiate an attack-counterAttack sequence. returns true if this unit's army is routed
    public boolean fire(Unit u){
        int attack = 0;
        int counter = 0;
        //Are there any conditions that occur before the attack sequence?
        getArmy().getCO().firstStrikeCheck(this, u, true);
        u.getArmy().getCO().firstStrikeCheck(u, this, false);
        
        //first strike
        if(u.getArmy().getCO().hasFirstStrike() && !this.getArmy().getCO().hasFirstStrike() && u.getMaxRange() == 1 && this.getMaxRange()==1){
            boolean isgameover = u.fire(this);
            //if the enemy caused your unit to rout, then you killed yourself
            if(this.isRout()){
                Options.killedSelf = true;
            }
            return isgameover;
        }
        //start normal firing routines
        int t = Math.abs(u.getLocation().getRow() - getLoc().getRow()) + Math.abs(u.getLocation().getCol() - getLoc().getCol());
        //t is distance from the unit.
        boolean destroyed = false;
        if(t >= minRange && t <= getMaxRange()){
            if(u != null){
                if(u.getArmy().getSide() != army.getSide()){
                    
                getArmy().getCO().beforeAttack(this, u, damageCalc(u), true);
                    u.getArmy().getCO().beforeAttack(u, this, damageCalc(u), false);
                    
                    int i = damageCalc(u);
                    int chargeDamage = u.getHP() - i;
                    //charge damage calculation (stops overkill)
                    if(chargeDamage <= 0){
                        chargeDamage = u.getHP();
                    }else{
                        chargeDamage = i;
                    }
                    attack = chargeDamage;
                    destroyed = u.damage(i,true);
                    //charge and salvage
                    if(army.getBattle().getBattleOptions().isBalance())
                    {
                    army.charge(chargeDamage*u.getPrice()/7000/200.0);
                    u.getArmy().charge(chargeDamage*u.getPrice()/7000/100.0);
                    }else{
                    army.charge(chargeDamage*u.getStarValue()/200.0);
                    u.getArmy().charge(chargeDamage*u.getStarValue()/100.0);   
                    }
                    army.addFunds(army.getCO().getEnemySalvage() * chargeDamage * u.getPrice() /1000);
                    u.getArmy().addFunds(u.getArmy().getCO().getFriendlySalvage() * chargeDamage * u.getPrice() /10000);
                
                    if(getAmmo() > 0)
                        if(BaseDMG.findBase(this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
                            setAmmo(getAmmo() - 1);
                    
                    getArmy().getCO().afterAttack(this, u, i, destroyed, true);
                    u.getArmy().getCO().afterAttack(u, this, i, destroyed, false);
                    
                    if(destroyed && u.isRout()){
                        if(u.getArmy().getBattle().removeArmy(u.getArmy(),army,false))return true;
                    }
                    
                    //start counterattack
                    //only if the unit can counter-attack to begin with!
                    if(t == 1 && !destroyed && BaseDMG.find(u, this, army.getBattle().getBattleOptions().isBalance()) > -1) {
                        if(t >= u.getMinRange() && t <= u.getMaxRange()) {
                            //if(this.getHP()-(u.damageCalc(this) * (int)(u.getArmy().getCO().counterAttack/100.0))>0) {
                            getArmy().getCO().beforeCounter(this, u, (int)(u.damageCalc(this) * (u.getArmy().getCO().getCounterAttack()/100.0)), false);
                            u.getArmy().getCO().beforeCounter(u, this, (int)(u.damageCalc(this) * (u.getArmy().getCO().getCounterAttack()/100.0)), true);
                            /*} else {
                                getArmy().getCO().beforeCounter(this, u, this.getHP(), false);
                                u.getArmy().getCO().beforeCounter(u, this, this.getHP(), true);
                            }*/
                            
                            i = (int)(u.damageCalc(this) * (u.getArmy().getCO().getCounterAttack()/100.0)); //Damage by counterattack
                            
                            if(i < 0) {
                                i = 0;
                            }
                            
                            //charge damage calculation (stops overkill)
                            chargeDamage = this.getHP() - i;
                            if(chargeDamage <= 0){
                                chargeDamage = this.getHP();
                            }else{
                                chargeDamage = i;
                                //The unit is destroyed!
                            }
                            counter = chargeDamage;
                            //This is when the enemy unit is counter-attacking against the friendly unit
                            destroyed = this.damage(i, true);
                            //destroyed indicates whether or not the friendly unit is dead
                            
                            //charge and salvage
                            if(army.getBattle().getBattleOptions().isBalance())
                            {
                            army.charge(chargeDamage*this.getPrice()/7000/100.0);
                            u.getArmy().charge(chargeDamage*this.getPrice()/7000/200.0);
                            }else{
                            army.charge(chargeDamage*this.getStarValue()/100.0);
                            u.getArmy().charge(chargeDamage*this.getStarValue()/200.0);   
                            }
                            u.getArmy().addFunds(u.getArmy().getCO().getEnemySalvage() * chargeDamage * this.getPrice() /10000);
                            army.addFunds(army.getCO().getFriendlySalvage() * chargeDamage * this.getPrice() /10000);
                            
                            if(u.getAmmo() > 0)
                                if(BaseDMG.findBase(u.getUType(), this.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
                                    u.setAmmo(u.getAmmo() - 1);
                            
                            //This is the friendly CO's afterCounter call
                            getArmy().getCO().afterCounter(this, u, i, destroyed, false);
                            
                            //This is the enemy CO's afterCounter call
                            u.getArmy().getCO().afterCounter(u, this, i, destroyed, true);
                            
                            if(destroyed && this.isRout()){
                                Options.killedSelf = true;
                                if(army.getBattle().removeArmy(this.getArmy(),null,false))return true;
                            }
                        }
                    }
                    getArmy().getCO().afterAttackAction(this, u, true);
                    u.getArmy().getCO().afterAttackAction(u, this, false);
                }
                /*BattleAnimation battle = new BattleAnimation(this,u,counter, attack);
                battle.setup();
                battle.start();*/
            }
        }
        return false;
    }
    
    //fire, used against inventions
    public void fire(Invention inv){
        boolean destroyed = false;
        if(inv != null){
            //if(inv.getArmy().getSide() != army.getSide()){
            int i = damageCalc(inv);
            inv.damage(i);
            if(getAmmo() > 0)
                if(inv.findBase(this.getUType()) != -1)
                    setAmmo(getAmmo() - 1);
            //}
        }
    }
    
    //Checks if this unit's army has been routed
    public boolean isRout(){
        if(army.getUnits()==null)return true;
        return false;
    }
    
    //Determines is a given location is in the unit's immediate firing range
    public boolean checkFireRange(Location l){
        int t = Math.abs(l.getRow() - getLoc().getRow()) + Math.abs(l.getCol() - getLoc().getCol());
        if(t >= minRange && t <= getMaxRange())return true;
        return false;
    }
    
    //Determines is a given location is adjacent to the unit
    public boolean checkAdjacent(Location l){
        int t = Math.abs(l.getRow() - getLoc().getRow()) + Math.abs(l.getCol() - getLoc().getCol());
        if(t == 1)return true;
        return false;
    }
    
    //Determines is a given location is in the unit's firing range
    public boolean checkDisplayFireRange(Location l){
        int t = Math.abs(l.getRow() - getLoc().getRow()) + Math.abs(l.getCol() - getLoc().getCol());
        //Indirects
        if(minRange > 1){
            if(t >= minRange && t <= getMaxRange())return true;
            return false;
        }else if(getMaxRange() == 0){
            return false;
        }
        //Directs
        if(moveRange == null)calcMoveTraverse();
        if(moveRange.checkMove(l))return true;
        int x = l.getCol();
        int y = l.getRow();
        Location north = new Location(x,y-1);
        Location south = new Location(x,y+1);
        Location east = new Location(x+1,y);
        Location west = new Location(x-1,y);
        if(map.onMap(north) && (moveRange.checkMove(north)||getLoc().equals(north)))return true;
        if(map.onMap(south) && (moveRange.checkMove(south)||getLoc().equals(south)))return true;
        if(map.onMap(east) && (moveRange.checkMove(east)||getLoc().equals(east)))return true;
        if(map.onMap(west) && (moveRange.checkMove(west)||getLoc().equals(west)))return true;
        return false;
    }
    
    //Deals damage to the HP of the Unit, deals with unit destruction, returns true if unit destroyed
    public boolean damage(int dmg, boolean destroy){
        if(dmg < 0) return false;
        hP -= dmg;
        if (hP <= 0){
            hP = 1;
            
            if(destroy){
                if(map.find(this).getTerrain() instanceof Property){
                    ((Property) map.find(this).getTerrain()).endCapture();
                }
                if(this instanceof Transport){
                    Transport trans = (Transport) this;
                    for(int i = trans.getUnitsCarried();i>0;i--){
                        army.removeUnit(trans.getUnit(i));
                    }
                }
                
                Animation tempA = new Animation();
                tempA.setExplosion(this,0);
                tempA.setup(false, false);
                tempA.start();
                
                map.remove(this);
                army.removeUnit(this);
                return true;
            }
        }
        return false;
    }
    
    //Removes a unit from the game
    public void eliminateUnit(){
        if(map.find(this).getTerrain() instanceof Property){
            ((Property) map.find(this).getTerrain()).endCapture();
        }
        if(this instanceof Transport){
            Transport trans = (Transport) this;
            for(int i = trans.getUnitsCarried();i>0;i--){
                army.removeUnit(trans.getUnit(i));
            }
        }
        
        /*Explosion temp = new Explosion(this,0);
        temp.setup();
        temp.start();*/
        
        map.remove(this);
        army.removeUnit(this);
    }
    
//Calculates the damage caused by an attack
    //Calculates the damage caused by an attack
    private int damageCalc(Unit u) {
        int tagStars = this.getArmy().getCO().getTagAffinityStars(this.getArmy().getAltCO());
        int tagStarsPercent = 5;
        if(army.getBattle().getBattleOptions().isBalance())tagStarsPercent = 0;
        int posLuck = 0;
        if(this.getArmy().getCO().getPosLuck() + tagStarsPercent*tagStars > 0) {
            posLuck = army.getBattle().getRNG().nextInt((this.getArmy().getCO().getPosLuck()*getDisplayHP()/10) + tagStarsPercent*tagStars);
        }
        //NEW CODE
        //Adjusts the unit's positive luck damage inbetween
        //minPosLuck and maxPosLuck.
        //Moved it out, so COs with no inherent luck can feel the love!
        if(army.getCO().getMinPosLuck() != -1 && posLuck < army.getCO().getMinPosLuck()) {
            posLuck = army.getCO().getMinPosLuck();
        } else if(army.getCO().getMaxPosLuck() != -1 && posLuck > army.getCO().getMaxPosLuck()) {
            posLuck = army.getCO().getMaxPosLuck();
        }
        
        int negLuck, tdef;
        if(u.getMoveType() != MOVE_AIR)
            tdef = map.find(u).getTerrain().getDef();
        else
            tdef = 0;
        tdef = (int)(tdef * u.getArmy().getCO().getTerrainDefenseMultiplier());
        tdef -= this.getArmy().getCO().getEnemyTerrainPenalty();
        if(tdef < 0)tdef = 0;
        
        negLuck = 0;
        if((this.getArmy().getCO().getNegLuck() > 0)) {
            negLuck = army.getBattle().getRNG().nextInt(this.getArmy().getCO().getNegLuck()*getDisplayHP()/10);
        } else
            negLuck = 0;
        
        //NEW CODE
        //Adjusts the unit's negative luck damage inbetween
        //minNegLuck and maxNegLuck.
        if(army.getCO().getMinNegLuck() != -1 && negLuck < army.getCO().getMinNegLuck()) {
            negLuck = army.getCO().getMinNegLuck();
        } else if(army.getCO().getMaxNegLuck() != -1 && negLuck > army.getCO().getMaxNegLuck()) {
            negLuck = army.getCO().getMaxNegLuck();
        }
        
        int baseDamage = BaseDMG.find(getAmmo(), this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance());
        if(u instanceof Submarine && u.isDived())baseDamage = ((Submarine)u).getDivedDamage(this.getUType());
        else if(u instanceof Stealth && u.isDived())baseDamage = ((Stealth)u).getHiddenDamage(this.getUType());
        
        //main damage formula
        int dmgResult =(int) Math.floor((this.getDisplayHP()/10.0 * ((baseDamage * (this.getArmy().getCO().getAtk(this,u)+ this.getArmy().getComTowers()*10 + this.getArmy().getAtkPercent()-getAttackPenalty())/100.0) + posLuck - negLuck) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.getDefensePenalty()))/100.0)));
        //return (int) Math.floor((this.getDisplayHP()/10.0 * ((baseDamage * (this.getArmy().getCO().getAtk(this,u)+ this.getArmy().getComTowers()*10 + this.getArmy().getAtkPercent()-attackPenalty)/100.0) + posLuck - negLuck) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.defensePenalty))/100.0)));
        int bonus = this.getArmy().getCO().getBonusDamage() - this.getArmy().getCO().getDamagePenalty();
        
        if(dmgResult + bonus >= 0)
            return dmgResult+bonus;
        return 0;
    }

//Calculates the likely damage caused by an attack, used for display
    public int displayDamageCalc(Unit u){
        int tdef;
        if(u.getMoveType() != MOVE_AIR)
            tdef = map.find(u).getTerrain().getDef();
        else
            tdef = 0;
        
        tdef = (int)(tdef * u.getArmy().getCO().getTerrainDefenseMultiplier());
        tdef -= this.getArmy().getCO().getEnemyTerrainPenalty();
        if(tdef < 0)tdef = 0;
        
        int baseDamage = BaseDMG.find(getAmmo(), this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance());
        if(u instanceof Submarine && u.isDived())baseDamage = ((Submarine)u).getDivedDamage(this.getUType());
        else if(u instanceof Stealth && u.isDived())baseDamage = ((Stealth)u).getHiddenDamage(this.getUType());
        
        int dmgResult = (int) Math.floor((this.getDisplayHP()/10.0 * (baseDamage * ((this.getArmy().getCO().getAtk(this,u)+(this.getArmy().getComTowers()*10)+this.getArmy().getAtkPercent()- getAttackPenalty())/100.0 )) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.getDefensePenalty()))/100.0)));
        int bonus = this.getArmy().getCO().getBonusDamage() - this.getArmy().getCO().getDamagePenalty();
        
        //main damage formula
        if(dmgResult >= 0) {
            if(dmgResult + bonus >=0)
                return dmgResult + bonus;
            else
                return 0;
        } else
            return dmgResult;
    }
    
//Calculates the likely damage caused by an attack, used for inventions
    public int damageCalc(Invention i){
        //AW1
        //return (int) ((((hP)/100.0)) * (((BaseDMG.find(ammo, this.getUType(), u.getUType()) * (this.getArmy().getCO().getAtk(this,u)/100.0))) * ((200.0 - u.getArmy().getCO().getDef(this,u))/100.0)) /((100.0 + d*(u.getHP()/10.0))/100.0));
        //AW2+
        return (int) Math.floor((this.getDisplayHP()/10.0 * (i.find(getAmmo(), this.getUType()) * ((this.getArmy().getCO().getInventionAtk(this,null)+(this.getArmy().getComTowers()*10)+this.getArmy().getAtkPercent() - getAttackPenalty())/100.0))));
    }
    
//heals a unit (i is the amount healed out of 100)
    public void heal(int i){
        hP+=i;
        if(hP  > 100)
            hP = 100;
    }
    
//heals a unit to an increment of 10
    public void evenHeal(int i){
        hP+=i;
        hP-=hP%10;
        if(hP  > 100)
            hP = 100;
    }
    
//resupplies the unit
    public void resupply(){
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
    }
    
    //moves the unit to the given Location, checks if the move is valid, returns ambush status
    //now returns the actual ambusher if one is found    
    public Unit move(Location endLoc) 
    {
        Location originalLocation = getLoc();
    	trapper = null;
        //boolean ambush = false;
        int i = unitPath.getLength();
        
        //ensures that a proper moveTraverse has been generated...in the final version, this will happen when the player selects the unit to move
        if(moveRange == null) 
        {
            logger.info("Cannot move yet, move range unchecked");
            return trapper;
        }
        
        //check and execute the move
        if(moveRange.checkMove(endLoc)){
            if(!map.find(endLoc).hasUnit() || map.find(endLoc).getUnit().getArmy().getSide() != getArmy().getSide()){
                //end capture if needed
                if(map.find(this).getTerrain() instanceof Property){
                    ((Property) map.find(this).getTerrain()).endCapture();
                }
                
                //check the path for ambushes
                int[] dirs = unitPath.getItems();
                int x = getLoc().getCol();
                int y = getLoc().getRow();
                for(int j=0; j < dirs.length; j++){
                    //apply direction
                    if(dirs[j]==0)y--;
                    if(dirs[j]==1)x++;
                    if(dirs[j]==2)y++;
                    if(dirs[j]==3)x--;
                    
                    //check for ambush
                    if(map.find(new Location(x,y)).hasUnit() && map.find(new Location(x,y)).getUnit().getArmy().getSide() != getArmy().getSide()) {
                        //record trapper
                        trapper = map.find(new Location(x,y)).getUnit();
                        
                        //undo last move
                        if(dirs[j]==0)y++;
                        if(dirs[j]==1)x--;
                        if(dirs[j]==2)y--;
                        if(dirs[j]==3)x++;
                        endLoc = new Location(x,y);
                        //ambush = true;
                        //calculate new path
                        for(int k=0; k < i-j; k++){
                            unitPath.deleteLast();
                        }
                        i = unitPath.getLength();
                    }
                    //FoW Updating
                    if((this.unitType == UnitID.INFANTRY || this.unitType == UnitID.MECHINF) && this.getArmy().getBattle().getMap().find(this).getTerrain().getName().equals("Mountain"))
                        army.getBattle().clearFog(this.getVision()+2,x,y);
                    else if(unitType != UnitID.SPYPLANE)
                        army.getBattle().clearFog(this.getVision(),x,y);
                    else
                        army.getBattle().clearPiercingFog(getVision(),x,y);
                    
                    //updates the position of units loaded within this

                }

                //animation
                if(!unitPath.isEmpty()) {
                    int[] movement = unitPath.getItems();
                    int curx = getLoc().getCol()*16;
                    int cury = getLoc().getRow()*16;
                    Animation[] animoots = new Animation[movement.length];
                    if(this.getMType() < 2) {
                        for(int s = 0; s < movement.length; s++) {
                            switch(movement[s]) {
                                case 0: //north
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getNorthImage(this),1,0,0,0,UnitGraphics.findYPosition(this),16,UnitGraphics.findYPosition(this)+16,curx,cury,curx,cury-16,100,100,3,0,0);
                                    cury-=16;
                                    break;
                                case 1: //east
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getEastImage(this),1,0,0,0,UnitGraphics.findYPosition(this),16,UnitGraphics.findYPosition(this)+16,curx,cury,curx+16,cury,100,100,3,0,0);
                                    curx+=16;
                                    break;
                                case 2: //south
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getSouthImage(this),1,0,0,0,UnitGraphics.findYPosition(this),16,UnitGraphics.findYPosition(this)+16,curx,cury,curx,cury+16,100,100,3,0,0);
                                    cury+=16;
                                    break;
                                case 3: //west
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getWestImage(this),1,0,0,0,UnitGraphics.findYPosition(this),16,UnitGraphics.findYPosition(this)+16,curx,cury,curx-16,cury,100,100,3,0,0);
                                    curx-=16;
                                    break;
                            }
                        }
                    } else {
                        for(int s = 0; s < movement.length; s++) {
                            switch(movement[s]) {
                                case 0: //north
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getNorthImage(this),1,0,0,0,UnitGraphics.findYPosition(this)/16*24,24,UnitGraphics.findYPosition(this)/16*24+24,curx-4,cury,curx-4,cury-16,100,100,3,0,0);
                                    cury-=16;
                                    break;
                                case 1: //east
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getEastImage(this),1,0,0,0,UnitGraphics.findYPosition(this)/16*24,24,UnitGraphics.findYPosition(this)/16*24+24,curx-4,cury-3,curx-4+16,cury-3,100,100,3,0,0);
                                    curx+=16;
                                    break;
                                case 2: //south
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getSouthImage(this),1,0,0,0,UnitGraphics.findYPosition(this)/16*24,24,UnitGraphics.findYPosition(this)/16*24+24,curx-4,cury,curx-4,cury+16,100,100,3,0,0);
                                    cury+=16;
                                    break;
                                case 3: //west
                                    animoots[s] = new Animation(army.getBattle(),UnitGraphics.getWestImage(this),1,0,0,0,UnitGraphics.findYPosition(this)/16*24,24,UnitGraphics.findYPosition(this)/16*24+24,curx-4,cury-3,curx-4-16,cury-3,100,100,3,0,0);
                                    curx-=16;
                                    break;
                            }
                        }
                    }
                    //hurray, animoots are now finished adding. now to link up and then run said animoots.
                    for(int s = 0; s<animoots.length; s++) {
                        
                        animoots[s].setupMove(false,this, movement[s]);
                        animoots[s].addLock();
                        if(s+1 != animoots.length)
                            animoots[s].linkTo(animoots[s+1]);
                    }
                    animoots[0].start();
                }
                //This finds the highest fuel multiplier of all COs on the field.
                /*if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())gas -= (int)(i*2*mult);
                else gas -= i;*/
                if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())setGas(getGas() - ((int)(unitPath.getFuelUsage(this)*2*getFuelMult())));
                else setGas(getGas() - ((int)(unitPath.getFuelUsage(this)*getFuelMult())));
                
                map.move(this, endLoc);
                this.setLocation(endLoc);
                
                if(this instanceof Transport) {
                    if(((Transport)this).getSlot1() != null)
                        ((Transport)this).getSlot1().setLoc(loc);
                    if(((Transport)this).getSlot2() != null)
                        ((Transport)this).getSlot2().setLoc(loc);
                }
                //reset moveRange
                //moveRange = null;
                setMoved(true);
            }else{
                logger.info("Cannot move, a friendly unit is already occupying that tile");
            }
        }else{
            logger.info("Invalid Move");
        }
        
        //return ambush;
        return trapper;
    }
    
    public Unit getTrapper()
    {
    	return trapper;
    }
    
//moves the unit to the given Location, no checks (used by unload)
    public void forceMove(Location endLoc){
        //map.move(this, endLoc);
        this.setLocation(endLoc);
        map.addUnit(this);
        //reset moveRange
        //moveRange = null;
        setMoved(true);
    }
    
//undos an already completed move
    public void undoMove(Location endLoc, int originalcp){
        int i = unitPath.getLength();
        
        map.move(this, endLoc);
        this.setLocation(endLoc);
        if(!army.getBattle().isFog()){
            /*if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())gas += i*2;
            else gas += i;*/
            if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())setGas(getGas() + ((int)(unitPath.getFuelUsage(this)*2*getFuelMult())));
            else setGas(getGas() + ((int)(unitPath.getFuelUsage(this)*getFuelMult())));
        }
        //reset moveRange
        moveRange = null;
        setMoved(false);
        
        //return old cp to the property
        if(map.find(this).getTerrain() instanceof Property){
            ((Property) map.find(this).getTerrain()).setCapturePoints(originalcp);
        }
    }
    
    //checks if the stealth unit is hidden
    public void setIfHidden(){
        int x = getLoc().getCol();
        int y = getLoc().getRow();
        if(army.getBattle().getFog(x,y)){
            //if in fog, always hidden
            hidden = true;
        }else{
            //otherwise, check for stealths and subs
            if(!isDived()){
                hidden = false;
                return;
            }
            int turn = army.getBattle().getTurn();
            //properties can always see stealth units
            if(map.find(new Location(x,y)).getTerrain() instanceof Property){
                Property prop = (Property)map.find(new Location(x,y)).getTerrain();
                if(prop.getOwner()!=null){
                    if(prop.getOwner().getSide()==army.getBattle().getArmy(turn).getSide()){
                        hidden = false;
                        return;
                    }
                }
            }
            //check for adjacent units
            Location north = new Location(x,y-1);
            Location south = new Location(x,y+1);
            Location east = new Location(x+1,y);
            Location west = new Location(x-1,y);
            if(map.onMap(north) && map.find(north).getUnit() != null && map.find(north).getUnit().getArmy().getSide() == army.getBattle().getArmy(turn).getSide()){
                hidden = false;
                return;
            }
            if(map.onMap(south) && map.find(south).getUnit() != null && map.find(south).getUnit().getArmy().getSide() == army.getBattle().getArmy(turn).getSide()){
                hidden = false;
                return;
            }
            if(map.onMap(east) && map.find(east).getUnit() != null && map.find(east).getUnit().getArmy().getSide() == army.getBattle().getArmy(turn).getSide()){
                hidden = false;
                return;
            }
            if(map.onMap(west) && map.find(west).getUnit() != null && map.find(west).getUnit().getArmy().getSide() == army.getBattle().getArmy(turn).getSide()){
                hidden = false;
                return;
            }
            hidden = true;
        }
    }
    
    //checks if the unit reveals a stealth unit and unstealths them
    public void checkForStealth(Location l){
        if(l == null)l = getLoc();
        int turn = army.getBattle().getTurn();
        int x = l.getCol();
        int y = l.getRow();
        Location north = new Location(x,y-1);
        Location south = new Location(x,y+1);
        Location east = new Location(x+1,y);
        Location west = new Location(x-1,y);
        if(map.onMap(north) && map.find(north).getUnit() != null)map.find(north).getUnit().setIfHidden();
        if(map.onMap(south) && map.find(south).getUnit() != null)map.find(south).getUnit().setIfHidden();
        if(map.onMap(east) && map.find(east).getUnit() != null)map.find(east).getUnit().setIfHidden();
        if(map.onMap(west) && map.find(west).getUnit() != null)map.find(west).getUnit().setIfHidden();
    }
    
//calculates a new MoveTraverse for this unit
    public void calcMoveTraverse(){
        moveRange = new MoveTraverse(this);
    }
    
//returns the unit's moveTraverse
    public MoveTraverse getMoveRange(){
        return this.moveRange;
    }
    
//returns the unit's location
    public Location getLocation(){
        return getLoc();
    }
    
//sets the unit's location, used by move() and forceMove()
    public void setLocation(Location l){
        setLoc(l);
    }
    
//returns HP
    public int getHP(){
        return hP;
    }
    
//returns minimum range
    public int getMinRange(){
        return minRange;
    }
    
//returns maximum range
    public int getMaxRange(){
        if(army.getBattle().getWeather()==3 && !army.getCO().isSandImmune()){
            if(maxRange > 2)return maxRange-1;
        }
        return maxRange;
    }
    
//returns display HP
    public int getDisplayHP(){
        return (hP+9)/10;
    }
    
//returns movement type
    public int getMType(){
        return getMoveType();
    }
    
//returns remaining ammunition
    public int getAmmo(){
        return ammo;
    }
    
//returns the unit's type
    public int getUType(){
        return unitType;
    }
    
//returns the unit's price
    public int getPrice(){
        return price;
    }
    
//returns the vision range
    public int getVision(){
        if(army.getBattle().getWeather()==1 && !army.getCO().isRainImmune()){
            if(vision > 1)return vision-1;
        }
        return vision;
    }
    
//returns the unit's MP
    public int getMove(){
        return move;
    }
    
    public int getUnitType(){
        return unitType;
    }
    
//returns the remaining gas
    public int getGas(){
        return gas;
    }
    
//returns the unit's Army
    public Army getArmy(){
        return army;
    }
    
//returns the unit's Map
    public Map getMap(){
        return map;
    }
    
    public void setActive(boolean a){
        active = a;
        if(a == false){
            setMoved(false);
        }
        if(a == true && isParalyzed() == true){
            active = false;
            setMoved(true);
            setParalyzed(false);
        }
        
    }
    
    //daily gas usage, returns true if the unit is destroyed and it causes the army to rout
    public boolean dailyUse(){
        boolean destroyed = false;
        if(getDailyGas() > 0){
            if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune()) setGas(getGas() - (getDailyGas()*2));
            else setGas(getGas() - getDailyGas());
        }
        if(getGas() <= 0 && (getMoveType() == MOVE_AIR || getMoveType() == MOVE_SEA || getMoveType() == MOVE_TRANSPORT))
            destroyed = this.damage(1000 , true);
        if(destroyed && isRout())
            return true;
        return false;
    }
    
    public boolean getMoved(){
        return isMoved();
    }
    
    public boolean isDived(){
        return dived;
    }
    
    public boolean isHidden(){
        return hidden;
    }
    
    public void setHidden(boolean isHidden){
        hidden = isHidden;
    }
    
    public boolean isDetected(){
        return detected;
    }
    
    public void setDetected(boolean det){
        detected = det;
    }
    
    public boolean isActive(){
        return active;
    }
    
    public boolean isLowOnAmmo(){
        if(getAmmo() <= getMaxAmmo()/2 && getMaxAmmo() > 0)return true;
        return false;
    }
    
    public boolean isLowOnFuel(){
        if((double)getGas()/getMaxGas() < .5)return true;
        return false;
    }
    
    public int getValue(){
        return (int) (price * (hP / 100.0));
    }
    
//returns the unit's name
    public String getName(){
        return name;
    }
    
//adds fuel, used in joining
    public void addGas(int amount){
        setGas(getGas() + amount);
        if(getGas() > getMaxGas())setGas(getMaxGas());
    }
    
//adds ammo, used in joining
    public void addAmmo(int amount){
        setAmmo(getAmmo() + amount);
        if(getAmmo() > getMaxAmmo())setAmmo(getMaxAmmo());
    }
    
    //moves the unit in a given direction
    public void goDirection(int direction){
        if(unitPath.isEmpty()){
            unitPath.insertLast(direction);
        }else{
            int lastDir = unitPath.getLast();
            if((lastDir == 0 && direction == 2) || (lastDir == 2 && direction == 0) || (lastDir == 1 && direction == 3) || (lastDir == 3 && direction == 1))
                unitPath.deleteLast();
            else unitPath.insertLast(direction);
        }
    }
    
    public void startPath(){
        unitPath = new Path(getLoc().getCol(),getLoc().getRow());
    }
    
    public Path getPath(){
        return unitPath;
    }
    
    //replaces the current path, used in replays
    public void setPath(Path p){
        unitPath = p;
    }
    
    //get the Star Value of a unit
    public double getStarValue(){
        return starValue;
    }
    
    //is the unit in a transport?
    public boolean isInTransport(){
        return inTransport;
    }
    
    //set if a unit is in a transport
    public void setInTransport(boolean intrans){
        inTransport = intrans;
    }
    public void setOutTransport(){
        inTransport = false;
    }
//Returns a string with the Unit's important information
    public String toString(){
        return (name + ": HP: " + hP + " Active: " + active + " LOCATION:" + getLoc());
    }
    //This function charges the owner of this unit as if it dealt damage to army a.
    public void charge(int damage, Unit u) {
        army.charge(damage*starValue/100.0);
        u.getArmy().charge(damage*starValue/200.0);
    }
    public void salvage(int damage, Unit u) {
        army.addFunds(army.getCO().getEnemySalvage() * damage * u.getPrice() /10000);
        u.getArmy().addFunds(u.getArmy().getCO().getFriendlySalvage() * damage * u.getPrice() /10000);
    }
    
    public String writeToFile(){
        /*
         *
    protected int move;     //total MP
    protected int minRange; //minimum range (1 for directs)
    protected int maxRange; //maximum range (1 for directs)
    protected boolean active;           //is the unit able to be selected?
    protected double starValue;         //StarValue of the unit.
    protected boolean moved = false;    //has the unit moved?
    protected boolean changed = false;  //has the unit been changed?
    protected boolean hidden = false;   //is the unit hidden?
    protected boolean dived = false;    //is the unit dived/hiding?
    protected boolean detected = false; //has this unit been detected by a spyplane?
    protected Path unitPath;            //the path the unit takes to get from point A to point B
    protected boolean inTransport = false;  //is the unit in a transport?
    protected boolean paralyzed = false; //is the unit paralyzed (auto-waits next turn)
    protected int COstore[] = new int[10]; // This stores stuff (Sabaki's damage based defense boost, for one) that the
    protected int altCOstore[] = new int[10];
    protected float repairMod = 1; //This stores the temporary price modifications (Used for Falcone's SCOP).
    protected int[] enemyCOstore = new int[20]; //supports up to 20 armies. woop.
    protected int[] altEnemyCOstore = new int [20];
    boolean noResupply = false; //True means that this unit cannot use Resupply!
    boolean noRepair = false; //True means this unit cannot use Repair!
    boolean noResupplied = false; //Can be repaired?
    boolean noRepaired = false; //Can be resupplied?
    boolean noCityRepair = false;
    boolean noCityResupply = false;
    boolean noFire = false;
    boolean noCapture = false;
    boolean noLaunch = false;
    boolean noDive = false;
    boolean noRise = false;
    boolean noHide = false;
    boolean noAppear = false;
    boolean noExplode = false;
    boolean noUnload = false;
    boolean noJoin = false;
    boolean noLoad = false;
    boolean noSpecial1 = false;
    boolean noSpecial2 = false;
    boolean noWait = false;
    private Unit trapper;
    boolean perfectMovement = false; //Does this unit have perfect movement?
    int defensePenalty = 0; //Penalty to defense
    int attackPenalty = 0; //Penalty to offense*/
        String output = "<unit> \"" + name + "\"\n";                            //<unit> "name"
        output += "id : " + unitType + " </unit>\n";                            //id: # </unit>
        output += "<army = " + army.getID() + " >\n";                           //<army = # >
        //Map is assumed, moveTransverse ignored
        output += "<location = " + getLoc().getCol() + "," + getLoc().getRow() + " >\n";  //<location = #,# >
        output += "<hP = " + hP + " >\n";                                       //<hP = # >
        output += "<MoveType = " + getMoveType() + " >\n";                           //<MoveType = # >
        output += "<ammo = " + getAmmo() + " of " + getMaxAmmo() + " >\n";                //<ammo = # of # >
        output += "<gas = " + getGas() + " of " + getMaxGas() + " >\n";                   //<gas = # of # >
        output += "Daily = " + getDailyGas() + "\n" + "Fuel Multiplier = "           //Daily = #
                + getFuelMult() + " </gas>\n";                                       //Fuel Multiplier = # </gas>
        output += "<price = " + price + " x " + getRepairMod() + " >\n";             //<price = # x # >
        output += "<vision = " + getVision() + " >\n";                               //<vision = # >
        return output;
    }

	public void setMoveType(int moveType) {
		this.moveType = moveType;
	}

	public int getMoveType() {
		return moveType;
	}

	public void setVision(int vision) {
		this.vision = vision;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setEnemyCOstore(int[][] enemyCOstore) {
		this.enemyCOstore = enemyCOstore;
	}

	public int[][] getEnemyCOstore() {
		return enemyCOstore;
	}

	public void setAltEnemyCOstore(int[][] altEnemyCOstore) {
		this.altEnemyCOstore = altEnemyCOstore;
	}

	public int[][] getAltEnemyCOstore() {
		return altEnemyCOstore;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setCOstore(int cOstore[]) {
		COstore = cOstore;
	}

	public int[] getCOstore() {
		return COstore;
	}

	public void setRepairMod(float repairMod) {
		this.repairMod = repairMod;
	}

	public float getRepairMod() {
		return repairMod;
	}

	public void setDefensePenalty(int defensePenalty) {
		this.defensePenalty = defensePenalty;
	}

	public int getDefensePenalty() {
		return defensePenalty;
	}

	public void setNoRepair(boolean noRepair) {
		this.noRepair = noRepair;
	}

	public boolean isNoRepair() {
		return noRepair;
	}

	public void setNoResupply(boolean noResupply) {
		this.noResupply = noResupply;
	}

	public boolean isNoResupply() {
		return noResupply;
	}

	public void setNoCityRepair(boolean noCityRepair) {
		this.noCityRepair = noCityRepair;
	}

	public boolean isNoCityRepair() {
		return noCityRepair;
	}

	public void setNoCityResupply(boolean noCityResupply) {
		this.noCityResupply = noCityResupply;
	}

	public boolean isNoCityResupply() {
		return noCityResupply;
	}

	public void setNoResupplied(boolean noResupplied) {
		this.noResupplied = noResupplied;
	}

	public boolean isNoResupplied() {
		return noResupplied;
	}

	public void setNoUnload(boolean noUnload) {
		this.noUnload = noUnload;
	}

	public boolean isNoUnload() {
		return noUnload;
	}

	public void setNoJoin(boolean noJoin) {
		this.noJoin = noJoin;
	}

	public boolean isNoJoin() {
		return noJoin;
	}

	public void setNoLoad(boolean noLoad) {
		this.noLoad = noLoad;
	}

	public boolean isNoLoad() {
		return noLoad;
	}

	public void setNoExplode(boolean noExplode) {
		this.noExplode = noExplode;
	}

	public boolean isNoExplode() {
		return noExplode;
	}

	public void setNoWait(boolean noWait) {
		this.noWait = noWait;
	}

	public boolean isNoWait() {
		return noWait;
	}

	public void setGas(int gas) {
		this.gas = gas;
	}

	public void setAltCOstore(int altCOstore[]) {
		this.altCOstore = altCOstore;
	}

	public int[] getAltCOstore() {
		return altCOstore;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	public void setNoRepaired(boolean noRepaired) {
		this.noRepaired = noRepaired;
	}

	public boolean isNoRepaired() {
		return noRepaired;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public void setNoCapture(boolean noCapture) {
		this.noCapture = noCapture;
	}

	public boolean isNoCapture() {
		return noCapture;
	}

	public void setAttackPenalty(int attackPenalty) {
		this.attackPenalty = attackPenalty;
	}

	public int getAttackPenalty() {
		return attackPenalty;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Location getLoc() {
		return loc;
	}

	public void setDailyGas(int dailyGas) {
		this.dailyGas = dailyGas;
	}

	public int getDailyGas() {
		return dailyGas;
	}

	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

	public int getMaxAmmo() {
		return maxAmmo;
	}

	public void setMaxGas(int maxGas) {
		this.maxGas = maxGas;
	}

	public int getMaxGas() {
		return maxGas;
	}

	public void setNoFire(boolean noFire) {
		this.noFire = noFire;
	}

	public boolean isNoFire() {
		return noFire;
	}

	public void setParalyzed(boolean paralyzed) {
		this.paralyzed = paralyzed;
	}

	public boolean isParalyzed() {
		return paralyzed;
	}

	public void setPerfectMovement(boolean perfectMovement) {
		this.perfectMovement = perfectMovement;
	}

	public boolean isPerfectMovement() {
		return perfectMovement;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setDived(boolean dived) {
		this.dived = dived;
	}

	public void setFuelMult(float fuelMult) {
		this.fuelMult = fuelMult;
	}

	public float getFuelMult() {
		return fuelMult;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setNoAppear(boolean noAppear) {
		this.noAppear = noAppear;
	}

	public boolean isNoAppear() {
		return noAppear;
	}

	public void setNoHide(boolean noHide) {
		this.noHide = noHide;
	}

	public boolean isNoHide() {
		return noHide;
	}

	public void setNoRise(boolean noRise) {
		this.noRise = noRise;
	}

	public boolean isNoRise() {
		return noRise;
	}

	public void setNoDive(boolean noDive) {
		this.noDive = noDive;
	}

	public boolean isNoDive() {
		return noDive;
	}

	public void setNoSpecial1(boolean noSpecial1) {
		this.noSpecial1 = noSpecial1;
	}

	public boolean isNoSpecial1() {
		return noSpecial1;
	}

	public void setNoSpecial2(boolean noSpecial2) {
		this.noSpecial2 = noSpecial2;
	}

	public boolean isNoSpecial2() {
		return noSpecial2;
	}

	public void setNoLaunch(boolean noLaunch) {
		this.noLaunch = noLaunch;
	}

	public boolean isNoLaunch() {
		return noLaunch;
	}
}