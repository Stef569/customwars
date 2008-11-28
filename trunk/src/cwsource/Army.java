package cwsource;
/*
 *Army.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: July 1, 2006, 1:27 PM
 *The Army class holds an army, including its unit list and strategic intel
 */

//import java.util.Random;
import java.io.*;
import java.awt.Color;

public class Army implements Serializable{
    //private final int unitLimit = 50;           //The Unit Limit
    private UnitList units;                     //The Unit List
    private PropertyList properties;            //The Property List
    private int id;                             //The Army's ID number, it's position in the army list (-1 = neutral)
    private CO co;                              //The army's CO
    private Battle battle;                      //The Battle the Army is in
    private CO altCO;                           //The army's alternate CO
    private int side;                           //The army's side (0=A 1=B 2=C 3=D etc.)
    private int color;                          //the army's color (0=red, 1=blue, etc.)
    private int funds;                          //The money at this army's disposal
    private int atkPercent = 0;                 //The Attack Percent for every attack.
    //private int exLuck;                       //Extra Luck from various sources
    private int tag = 0;                        //Is this Army TagBreaking? 0 = off. 1 = current CO. 2 = next CO.
    private boolean canTagSwap;                 //In a TagBreak, has the army swapped yet?
    private int numComTowers;                   //The number of Com Towers owned by the army
    //private Color dayStartColor[] = new Color[2];//The army's day start color

    public AI ai = null;                        //The army's AI
    
    private int[][][] terrCost;       //The player's terrain costs increases on a map.
                                      //Yes it is a three dimensonal array.
    		 						  //Yes it will be a pain in the butt but probably not
                                      //too consuming, as long as the maps are not too
                                      //large or there aren't too many players.
    
    //constructor
    public Army(int id, CO co, CO altCO, int side, int color, Battle b){
        units = new UnitList();
        properties = new PropertyList();
        this.id = id;
        this.co = co;
        this.co.statIndex = id;
        this.altCO = altCO;
        this.co.setArmy(this);
        if(altCO != null){this.altCO.setArmy(this); this.altCO.statIndex = id;}
        this.side = side;
        this.color = color;
        funds = 0;
        this.battle = b;
        //int i = isSpecTag();
        //if(i > -1 && !Options.balanceMode)
        //    exLuck = co.TagStars[i]*5;
        numComTowers = 0;
        
        //NEW CODE, TERRAIN COST INCREASES ON A MAP
        //No need to loop and initialize; all cells are initialized to 0 anyway.
        
        //initializeTerrCost(b);
        
        //set day start colors
        /*
        if(color==0){
            //OS
            dayStartColor[0] = new Color(200, 0, 0);
            dayStartColor[1] = new Color(255, 0, 0);
        }else if(color==1){
            //BM
            dayStartColor[0] = new Color(0, 0, 200);
            dayStartColor[1] = new Color(0, 0, 255);
        }else if(color==2){
            //GE
            dayStartColor[0] = new Color(0, 200, 0);
            dayStartColor[1] = new Color(0, 255, 0);
        }else if(color==3){
            //YC
            dayStartColor[0] = new Color(200, 200, 0);
            dayStartColor[1] = new Color(255, 255, 0);
        }else if(color==4){
            //BH
            dayStartColor[0] = new Color(100, 100, 100);
            dayStartColor[1] = new Color(150, 150, 150);
        }else if(color==5){
            //RC
            dayStartColor[0] = new Color(0, 100, 0);
            dayStartColor[1] = new Color(0, 200, 0);
        }else if(color==6){
            //AC
            dayStartColor[0] = new Color(150, 150, 0);
            dayStartColor[1] = new Color(200, 200, 0);
        }else if(color==7){
            //PG
            dayStartColor[0] = new Color(0, 0, 0);
            dayStartColor[1] = new Color(150, 150, 150);
        }else if(color==8){
            //CS
            dayStartColor[0] = new Color(150, 100, 0);
            dayStartColor[1] = new Color(250, 200, 0);
        }else if(color==9){
            //CD
            dayStartColor[0] = new Color(100, 100, 200);
            dayStartColor[1] = new Color(100, 100, 255);
        }else{
            //defaults
            dayStartColor[0] = new Color(0, 0, 0);
            dayStartColor[1] = new Color(255, 255, 255);
        }
         */
    }

	public void initializeTerrCost(Battle b) 
	{
		if(b != null)
        {
        	if(b.getMap() != null)
        	{
        		terrCost = new int[b.getMap().getMaxCol()][b.getMap().getMaxRow()][MoveID.MAX_MOVE_TYPES];
        	}
        	else
        	{
        		System.out.println("Map is null!");
        	}
        }
    	else
    	{
    		System.out.println("Battle is null!");
    	}
	}
    
    //adds a unit to this Army
    public void addUnit(Unit u){
        units.insertLast(u);
    }
    
    //removes a unit from this Army
    public void removeUnit(Unit u){
        units.deleteItem(u);
    }
    
    //adds a property to this Army
    public void addProperty(Property p){
        properties.insertLast(p);
        co.propChange(p);
        if(p instanceof ComTower)numComTowers++;
    }
    
    //removes a property from this Army
    public void removeProperty(Property p){
        co.propUnChange(p);
        properties.deleteItem(p);
        if(p instanceof ComTower)numComTowers--;
    }
    
    public void changeAllProp(){
        if(!properties.isEmpty()){
        for(int i = 0; i<properties.list.size(); i++)
        {
        co.propChange(properties.list.get(i));
        }
        }
    }
    
    public void unChangeAllProp(){
        if(!properties.isEmpty()){
        for(int i = 0; i<properties.list.size(); i++)
        {
        co.propUnChange(properties.list.get(i));
        }
        }
    }
    
    //mark all the Army's units as either Active (true) or Inactive (false)
    public void setAllActive(boolean b){
        if(!units.isEmpty()){
        for(int i = 0; i<units.list.size(); i++)
        {
                units.list.get(i).setActive(b);
        }
        }
    }
    
    public void unChangeAll(){
        if(!units.isEmpty()){
        for(int i = 0; i<units.list.size(); i++)
        {
        co.unChange(units.list.get(i));
        }
        }
    }
    
    public void setChangeAll(){
        for(int i = 0; i<units.list.size(); i++)
        {
        co.setChange(units.list.get(i));
        }
    }
    
    public Battle getBattle(){
        return battle;
    }
    
    //returns a copy of the unit list
    public Unit[] getUnits(){
        return units.getItems();
    }
    
    //returns a copy of the property list
    public Property[] getProperties(){
        return properties.getItems();
    }
    
    //Can this Army TagBreak?
    public boolean canTag(){
        if(altCO != null && co.canSCOP() && altCO.canSCOP() && !co.SCOP && !co.COP)
            return true;
        return false;
    }
    
    public int getTag(){
        return tag;
    }
    
    public int getAtkPercent(){
        return atkPercent;
    }
    
    public boolean canTagSwap(){
        return canTagSwap;
    }
    
    //returns the number of units
    public int getNumberOfUnits(){
        return units.getSize();
    }
    
    //returns the number of units
    public int getNumberOfProperties(){
        return properties.getSize();
    }
    
    //returns the Army's CO
    public CO getCO(){
        return co;
    }
    
    //returns the Army's ID number
    public int getID(){
        return id;
    }
    
    //sets the Army's ID number
    public void setID(int newid){
        id = newid;
    }
    
    //returns the Army's color
    public int getColor(){
        return color;
    }
    
    //returns the Army's side
    public int getSide(){
        return side;
    }
    
    //returns the Alternate CO
    public CO getAltCO(){
        return altCO;
    }
    
    //returns the Army's funds
    public int getFunds(){
        return funds;
    }
    
    //adds funds
    public void addFunds(int amount){
        funds += amount;
        if(funds < 0)funds = 0;
    }
    
    public void unTag(){
        tag = 0;
        int i = isSpecTag();
        if(i > -1)
            atkPercent = 0;
    }
    
    //swaps COs
    public void swap(){
        //Random r = new Random();
        CO temp = co;
        if(co.isCOP())
            co.deactivateCOP();
        if(co.isSCOP())
            co.deactivateSCOP();
            
        //swaps the unit storages of the COs during a swap
        int[] storage = new int[10];
        Unit[] u = this.getUnits();
        for(int i = 0; i < this.getNumberOfUnits(); i++)
        {
            for(int s = 0; s< u[i].altCOstore.length; s++)
            {
            storage[s] = u[i].altCOstore[s];
            u[i].altCOstore[s] = u[i].COstore[s];
            u[i].COstore[s] = storage[s];
            }
        }
        int[][] storage2;
        //This switches the enemyCOstore of all enemies which uses enemyCOstore.
        Army[] a = battle.getArmies();
        for(int i = 0; i<a.length; i++)
        {
            if(a[i].getSide() != this.getSide())
            {
                u = a[i].getUnits();
                storage2 = new int[a[i].getNumberOfUnits()][10];
                for(int t= 0; t<a[i].getNumberOfUnits(); t++)
                {
                    for(int s = 0; s < 10; s++)
                    {
                        storage2[t] = u[t].altEnemyCOstore[this.co.statIndex];
                        u[t].altEnemyCOstore[this.co.statIndex] = u[t].enemyCOstore[this.co.statIndex];
                        u[t].enemyCOstore[this.co.statIndex] = storage2[t];
                    }
                }
            }
        }
        unChangeAll();
        unChangeAllProp();
        co = altCO;
        altCO = temp;
        setChangeAll();
        changeAllProp();
        System.out.println(co.name + ": " + co.Swap[battle.getRNG().nextInt(2)]);
        //What does the above statement do?
        if(tag > 0)
            canTagSwap = false;
    }
    
    //Tag Breaks!
    public void tagBreak(){
        int i = isSpecTag();
        if(i > -1){
            System.out.println(co.TagNames[i] + "!");
            atkPercent = (co.TagPercent[i] - 100);
            if(battle.getBattleOptions().isBalance()) atkPercent = 0;
        } else
            System.out.println("Dual Strike!");
        tag = 1;
        canTagSwap = true;
        co.activateSCOP();
        
        if(Options.isMusicOn()){
            Music.startPowerMusic(2,co.getStyle());
        }
    }
    
    public int isSpecTag(){
        if(altCO != null && co.TagCOs != null)
            for(int i = 0; i < co.TagCOs.length; i++)
                if(co.TagCOs[i] == altCO.getName())
                    return i;
        return -1;
    }
    
    //does daily use for all units, returns true if army is routed and it causes victory/defeat
    public boolean allDailyUse(){
        if(!units.isEmpty()){
            for(int i = 0; i<units.list.size(); i++) {
                if(units.list.get(i).dailyUse())
                    if(this.getBattle().removeArmy(this,null,false))
                        return true;
            }
        }
        return false;
    }
    
    public void charge(double d){
        if(co.COP || co.SCOP) return;
        co.charge(d);
        if(altCO != null)altCO.charge(d/4.0);
    }
    
    
    //subtracts funds
    public void removeFunds(int amount){
        funds -= amount;
        if(funds < 0)funds = 0;
    }
    
    //return the number of com towers the army owns
    public int getComTowers(){
        return numComTowers;
    }

    //sets the Army's CO
    public void setCO(CO c){
        co = c;
        co.setArmy(this);
    }
    
    //sets the Army's CO
    public void setAltCO(CO c){
        altCO = c;
        if(altCO != null){
            altCO.setArmy(this);
            altCO.statIndex = id;
        }
    }
    
    public boolean hasAI(){
        if(ai != null)
            return true;
                    return false;
    }
    
    public void runAI(){
        ai.start();
    }    
    
    public boolean isTag()
    {
    	return ((tag == 1) || (tag == 2));
    }
    
    public void incrementTag(){
        tag++;
    }
    /** <code>SETTERRCOSTS_SQUARE</code>
     * <p>
     * Sets the terrain cost increases for a given square for this player.
     * <p>
     * @param costInc
     * @param col
     * @param row
     */
	public void addTerrCosts_square(double[] costInc, int col, int row) 
	{
		if(getBattle().getMap().onMap(col, row))
		{
			for(int uID = 0; uID < terrCost.length && uID < costInc.length; uID++)
			{
				terrCost[col][row][uID] += costInc[uID];
			}
		}
	}
    
    public void addTerrCosts_area(double[] costInc, int c1, int r1, int c2, int r2)
    {
    	for(int col = c1 ; col < getBattle().getMap().getMaxRow() && col < c2; col++)
    	{
        	for(int row = r1 ; row < getBattle().getMap().getMaxCol() && row < r2; row++)
        	{
        		addTerrCosts_square(costInc, col, row);
        	}
    	}
    }
    
    public void addTerrCosts_diamond(double[] costInc, int col, int row, int rad)
    {
    	int offset = 0;
    	
        for(int colOffset = -1 * rad; colOffset <= rad; colOffset++)
        {
            for(int rowOffset = -1 * offset; rowOffset <= offset; rowOffset++)
            {
            	addTerrCosts_square(costInc, col + colOffset, row + rowOffset);
            }
            if(colOffset<0)
            	offset++;
            else
            	offset--;
        }
    }
    
    public void addTerrCosts_global(double[] costInc)
    {
    	addTerrCosts_area(costInc, 0, 0, getBattle().getMap().getMaxCol(), getBattle().getMap().getMaxRow());
    }
    
    public void addTerrCosts_terrain(double[] costInc, int terrID, int... terrIDarray)
    {
    	for(int col = 0; col < getBattle().getMap().getMaxRow(); col++)
    	{
        	for(int row = 0; row < getBattle().getMap().getMaxCol(); row++)
        	{
        		if(getBattle().getMap().onMap(col, row))
        		{
	        		int terrIndx = getBattle().getMap().find(new Location(col, row)).getTerrain().index;
	
	        		if(terrIndx == terrID)
	        		{            		
	            		addTerrCosts_square(costInc, col, row);
	        		}
	        		else if(terrIDarray != null)
	        		{
	        			for(int terrCount = 0; terrCount < terrIDarray.length; terrCount++)
	        			{
	    	        		if(terrIndx == terrIDarray[terrCount])
	    	        		{            		
	    	            		addTerrCosts_square(costInc, col, row);
	    	        		}
	        			}
	        		}
        		}
        	}
    	}
    }
    
    public void resetTerrCosts_global()
    {
    	double[] emptyCost = new double[terrCost.length];
    	addTerrCosts_area(emptyCost, 0, 0, getBattle().getMap().getMaxCol(), getBattle().getMap().getMaxRow());
    }
    
    public double getTerrCost(int col, int row, int mType)
    {
    	if(getBattle().getMap().onMap(col, row) && mType >= 0 && mType < MoveID.MAX_MOVE_TYPES)
    	{
    		return terrCost[col][row][mType];
    	}
    	
    	return 0.0;
    }

    //returns the given day start color
    //public Color getDayStartColor(int index){
    //    return dayStartColor[index];
    //}
}