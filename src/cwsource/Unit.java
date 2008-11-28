package cwsource;
/*
 *Unit.java
 *Author: Adam Dziuk
 *Contributors: Urusan, Albert Lai
 *Creation: June 24, 2006, 3:09 PM
 *Unit is an abstract class for units
 */

import java.io.*;
import org.omg.CORBA.BAD_CONTEXT;

public abstract class Unit implements Locatable, Serializable{
    //moveType 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
    final protected int MOVE_INFANTRY = 0;
    final protected int MOVE_MECH = 1;
    final protected int MOVE_TREAD = 2;
    final protected int MOVE_TIRE = 3;
    final protected int MOVE_AIR = 4;
    final protected int MOVE_SEA = 5;
    final protected int MOVE_TRANSPORT = 6;
    final protected int MOVE_OOZIUM = 7;
    final protected int MOVE_PIPE = 8;
    final protected int MOVE_HOVER = 9;
    
    protected int hP = 100; //HP (out of 100)
    protected int moveType; //move type
    protected int ammo;     //current ammunition
    protected int maxAmmo;  //maximum ammunition
    protected int unitType; //unit's type, used by BaseDMG
    protected int price;    //price (factors into repair costs)
    protected int vision;   //vision range
    protected int gas;      //current gas
    protected int maxGas;   //maximum gas
    protected int dailyGas = 0; //Amount of Gas used daily
    protected int move;     //total MP
    protected int minRange; //minimum range (1 for directs)
    protected int maxRange; //maximum range (1 for directs)
    protected String name;  //Unit's name
    protected Location loc; //Unit's location
    protected Army army;    //Unit's army
    protected Map map;      //The map the unit is on
    protected MoveTraverse moveRange;   //Unit's current movement range
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
    //COs might alter on a changing Unit to Unit basis.
    protected int altCOstore[] = new int[10];
    protected float repairMod = 1; //This stores the temporary price modifications (Used for Falcone's SCOP)
    protected int[][] enemyCOstore = new int[20][10]; //supports up to 20 armies. woop.
    protected int[][] altEnemyCOstore = new int [20][10];
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
    protected float fuelMult =1; //This determines how much fuel this unit burns, in addition to normal fuel.
    protected Unit trapper;
    boolean perfectMovement = false; //Does this unit have perfect movement?
    int defensePenalty = 0; //Penalty to defense
    int attackPenalty = 0; //Penalty to offense
    
    boolean moving = false;
    int direction = -1;//whether this unit is being moved or not (and what direction)
    //constructor
    public Unit(){
    }
    
    //constructor
    public Unit(Location l, Army arm, Map m) {
        loc = l;
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
        int t = Math.abs(u.getLocation().getRow() - loc.getRow()) + Math.abs(u.getLocation().getCol() - loc.getCol());
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
                    army.addFunds(army.getCO().enemySalvage * chargeDamage * u.getPrice() /1000);
                    u.getArmy().addFunds(u.getArmy().getCO().friendlySalvage * chargeDamage * u.getPrice() /10000);
                
                    if(ammo > 0)
                        if(BaseDMG.findBase(this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
                            ammo--;
                    
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
                            getArmy().getCO().beforeCounter(this, u, (int)(u.damageCalc(this) * (u.getArmy().getCO().counterAttack/100.0)), false);
                            u.getArmy().getCO().beforeCounter(u, this, (int)(u.damageCalc(this) * (u.getArmy().getCO().counterAttack/100.0)), true);
                            /*} else {
                                getArmy().getCO().beforeCounter(this, u, this.getHP(), false);
                                u.getArmy().getCO().beforeCounter(u, this, this.getHP(), true);
                            }*/
                            
                            i = (int)(u.damageCalc(this) * (u.getArmy().getCO().counterAttack/100.0)); //Damage by counterattack
                            
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
                            u.getArmy().addFunds(u.getArmy().getCO().enemySalvage * chargeDamage * this.getPrice() /10000);
                            army.addFunds(army.getCO().friendlySalvage * chargeDamage * this.getPrice() /10000);
                            
                            if(u.ammo > 0)
                                if(BaseDMG.findBase(u.getUType(), this.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
                                    u.ammo--;
                            
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
            if(ammo > 0)
                if(inv.findBase(this.getUType()) != -1)
                    ammo--;
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
        int t = Math.abs(l.getRow() - loc.getRow()) + Math.abs(l.getCol() - loc.getCol());
        if(t >= minRange && t <= getMaxRange())return true;
        return false;
    }
    
    //Determines is a given location is adjacent to the unit
    public boolean checkAdjacent(Location l){
        int t = Math.abs(l.getRow() - loc.getRow()) + Math.abs(l.getCol() - loc.getCol());
        if(t == 1)return true;
        return false;
    }
    
    //Determines is a given location is in the unit's firing range
    public boolean checkDisplayFireRange(Location l){
        int t = Math.abs(l.getRow() - loc.getRow()) + Math.abs(l.getCol() - loc.getCol());
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
        if(map.onMap(north) && (moveRange.checkMove(north)||loc.equals(north)))return true;
        if(map.onMap(south) && (moveRange.checkMove(south)||loc.equals(south)))return true;
        if(map.onMap(east) && (moveRange.checkMove(east)||loc.equals(east)))return true;
        if(map.onMap(west) && (moveRange.checkMove(west)||loc.equals(west)))return true;
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
        if(army.getCO().minPosLuck != -1 && posLuck < army.getCO().minPosLuck) {
            posLuck = army.getCO().minPosLuck;
        } else if(army.getCO().maxPosLuck != -1 && posLuck > army.getCO().maxPosLuck) {
            posLuck = army.getCO().maxPosLuck;
        }
        
        int negLuck, tdef;
        if(u.moveType != MOVE_AIR)
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
        if(army.getCO().minNegLuck != -1 && negLuck < army.getCO().minNegLuck) {
            negLuck = army.getCO().minNegLuck;
        } else if(army.getCO().maxNegLuck != -1 && negLuck > army.getCO().maxNegLuck) {
            negLuck = army.getCO().maxNegLuck;
        }
        
        int baseDamage = BaseDMG.find(ammo, this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance());
        if(u instanceof Submarine && u.dived)baseDamage = ((Submarine)u).getDivedDamage(this.getUType());
        else if(u instanceof Stealth && u.dived)baseDamage = ((Stealth)u).getHiddenDamage(this.getUType());
        
        //main damage formula
        int dmgResult =(int) Math.floor((this.getDisplayHP()/10.0 * ((baseDamage * (this.getArmy().getCO().getAtk(this,u)+ this.getArmy().getComTowers()*10 + this.getArmy().getAtkPercent()-attackPenalty)/100.0) + posLuck - negLuck) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.defensePenalty))/100.0)));
        //return (int) Math.floor((this.getDisplayHP()/10.0 * ((baseDamage * (this.getArmy().getCO().getAtk(this,u)+ this.getArmy().getComTowers()*10 + this.getArmy().getAtkPercent()-attackPenalty)/100.0) + posLuck - negLuck) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.defensePenalty))/100.0)));
        int bonus = this.getArmy().getCO().bonusDamage - this.getArmy().getCO().damagePenalty;
        
        if(dmgResult + bonus >= 0)
            return dmgResult+bonus;
        return 0;
    }

//Calculates the likely damage caused by an attack, used for display
    public int displayDamageCalc(Unit u){
        int tdef;
        if(u.moveType != MOVE_AIR)
            tdef = map.find(u).getTerrain().getDef();
        else
            tdef = 0;
        
        tdef = (int)(tdef * u.getArmy().getCO().getTerrainDefenseMultiplier());
        tdef -= this.getArmy().getCO().getEnemyTerrainPenalty();
        if(tdef < 0)tdef = 0;
        
        int baseDamage = BaseDMG.find(ammo, this.getUType(), u.getUType(),army.getBattle().getBattleOptions().isBalance());
        if(u instanceof Submarine && u.dived)baseDamage = ((Submarine)u).getDivedDamage(this.getUType());
        else if(u instanceof Stealth && u.dived)baseDamage = ((Stealth)u).getHiddenDamage(this.getUType());
        
        int dmgResult = (int) Math.floor((this.getDisplayHP()/10.0 * (baseDamage * ((this.getArmy().getCO().getAtk(this,u)+(this.getArmy().getComTowers()*10)+this.getArmy().getAtkPercent()- attackPenalty)/100.0 )) * ((200.0-(u.getArmy().getCO().getDef(this,u)+(tdef*u.getDisplayHP()) - u.defensePenalty))/100.0)));
        int bonus = this.getArmy().getCO().bonusDamage - this.getArmy().getCO().damagePenalty;
        
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
        return (int) Math.floor((this.getDisplayHP()/10.0 * (i.find(ammo, this.getUType()) * ((this.getArmy().getCO().getInventionAtk(this,null)+(this.getArmy().getComTowers()*10)+this.getArmy().getAtkPercent() - attackPenalty)/100.0))));
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
        gas = maxGas;
        ammo = maxAmmo;
    }
    
    //moves the unit to the given Location, checks if the move is valid, returns ambush status
    //now returns the actual ambusher if one is found    
    public Unit move(Location endLoc) 
    {
        Location originalLocation = loc;
    	trapper = null;
        //boolean ambush = false;
        int i = unitPath.getLength();
        
        //ensures that a proper moveTraverse has been generated...in the final version, this will happen when the player selects the unit to move
        if(moveRange == null) 
        {
            System.out.println("Cannot move yet, move range unchecked");
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
                int x = loc.getCol();
                int y = loc.getRow();
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
                        army.getBattle().clearFog(this.vision+2,x,y);
                    else if(unitType != UnitID.SPYPLANE)
                        army.getBattle().clearFog(this.vision,x,y);
                    else
                        army.getBattle().clearPiercingFog(vision,x,y);
                    
                    //updates the position of units loaded within this

                }

                //animation
                if(!unitPath.isEmpty()) {
                    int[] movement = unitPath.getItems();
                    int curx = loc.getCol()*16;
                    int cury = loc.getRow()*16;
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
                if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())gas -= (int)(unitPath.getFuelUsage(this)*2*fuelMult);
                else gas -= (int)(unitPath.getFuelUsage(this)*fuelMult);
                
                map.move(this, endLoc);
                this.setLocation(endLoc);
                
                if(this instanceof Transport) {
                    if(((Transport)this).slot1 != null)
                        ((Transport)this).slot1.loc = loc;
                    if(((Transport)this).slot2 != null)
                        ((Transport)this).slot2.loc = loc;
                }
                //reset moveRange
                //moveRange = null;
                moved = true;
            }else{
                System.out.println("Cannot move, a friendly unit is already occupying that tile");
            }
        }else{
            System.out.println("Invalid Move");
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
        moved = true;
    }
    
//undos an already completed move
    public void undoMove(Location endLoc, int originalcp){
        int i = unitPath.getLength();
        
        map.move(this, endLoc);
        this.setLocation(endLoc);
        if(!army.getBattle().isFog()){
            /*if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())gas += i*2;
            else gas += i;*/
            if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune())gas += (int)(unitPath.getFuelUsage(this)*2*fuelMult);
            else gas += (int)(unitPath.getFuelUsage(this)*fuelMult);
        }
        //reset moveRange
        moveRange = null;
        moved = false;
        
        //return old cp to the property
        if(map.find(this).getTerrain() instanceof Property){
            ((Property) map.find(this).getTerrain()).setCapturePoints(originalcp);
        }
    }
    
    //checks if the stealth unit is hidden
    public void setIfHidden(){
        int x = loc.getCol();
        int y = loc.getRow();
        if(army.getBattle().getFog(x,y)){
            //if in fog, always hidden
            hidden = true;
        }else{
            //otherwise, check for stealths and subs
            if(!dived){
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
        if(l == null)l = loc;
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
        return moveRange;
    }
    
//returns the unit's location
    public Location getLocation(){
        return loc;
    }
    
//sets the unit's location, used by move() and forceMove()
    protected void setLocation(Location l){
        loc = l;
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
        return moveType;
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
            moved = false;
        }
        if(a == true && paralyzed == true){
            active = false;
            moved = true;
            paralyzed = false;
        }
        
    }
    
    //daily gas usage, returns true if the unit is destroyed and it causes the army to rout
    public boolean dailyUse(){
        boolean destroyed = false;
        if(dailyGas > 0){
            if(army.getBattle().getWeather()==2 && !army.getCO().isSnowImmune()) gas -= dailyGas*2;
            else gas -= dailyGas;
        }
        if(gas <= 0 && (moveType == MOVE_AIR || moveType == MOVE_SEA || moveType == MOVE_TRANSPORT))
            destroyed = this.damage(1000 , true);
        if(destroyed && isRout())
            return true;
        return false;
    }
    
    public boolean getMoved(){
        return moved;
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
        if(ammo <= maxAmmo/2 && maxAmmo > 0)return true;
        return false;
    }
    
    public boolean isLowOnFuel(){
        if((double)gas/maxGas < .5)return true;
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
        gas += amount;
        if(gas > maxGas)gas = maxGas;
    }
    
//adds ammo, used in joining
    public void addAmmo(int amount){
        ammo += amount;
        if(ammo > maxAmmo)ammo = maxAmmo;
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
        unitPath = new Path(loc.getCol(),loc.getRow());
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
        return (name + ": HP: " + hP + " Active: " + active + " LOCATION:" + loc);
    }
    //This function charges the owner of this unit as if it dealt damage to army a.
    public void charge(int damage, Unit u) {
        army.charge(damage*starValue/100.0);
        u.getArmy().charge(damage*starValue/200.0);
    }
    public void salvage(int damage, Unit u) {
        army.addFunds(army.getCO().enemySalvage * damage * u.getPrice() /10000);
        u.getArmy().addFunds(u.getArmy().getCO().friendlySalvage * damage * u.getPrice() /10000);
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
        output += "<location = " + loc.getCol() + "," + loc.getRow() + " >\n";  //<location = #,# >
        output += "<hP = " + hP + " >\n";                                       //<hP = # >
        output += "<MoveType = " + moveType + " >\n";                           //<MoveType = # >
        output += "<ammo = " + ammo + " of " + maxAmmo + " >\n";                //<ammo = # of # >
        output += "<gas = " + gas + " of " + maxGas + " >\n";                   //<gas = # of # >
        output += "Daily = " + dailyGas + "\n" + "Fuel Multiplier = "           //Daily = #
                + fuelMult + " </gas>\n";                                       //Fuel Multiplier = # </gas>
        output += "<price = " + price + " x " + repairMod + " >\n";             //<price = # x # >
        output += "<vision = " + vision + " >\n";                               //<vision = # >
        return output;
    }
}