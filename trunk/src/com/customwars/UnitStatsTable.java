package com.customwars;
/*
 *UnitStatTable.java
 *Author: Ting Chow
 *Contributors: -
 *Creation: 2/7/2008
 *  The UnitStatTable class holds a large array of base stat values for use in 
 *  creating bland units.
 */

public class UnitStatsTable extends StatTable
{
    //Refer to UnitID.java for each unit's ID
    //Refer to StatID.java for each stat's ID
    // - The stats stored in this table are used when a unit is first deployed
	// - A unit using these stats is treated as if it were bland; further unit
	//   modifications should be found as part of the CO.
	//

	//Because I don't like breaking the nice formatting I have in place already,
	//I'm going to use a bunch of short names for some of the constants.
	protected static int cI = UnitType.INF_C;
	protected static int cV = UnitType.VEH_C;
	protected static int cC = UnitType.CPT_C;
	protected static int cJ = UnitType.JET_C;
	protected static int cS = UnitType.SHP_C;
	protected static int cB = UnitType.SUB_C;
	protected static int cO = UnitType.OOZ_C;
	protected static int cH = UnitType.HOV_C;
	
	protected static int mF = MoveType.FOOT;
	protected static int mM = MoveType.MECH;
	protected static int mT = MoveType.TREAD;
	protected static int mR = MoveType.TIRES;
	protected static int mA = MoveType.AIR;
	protected static int mS = MoveType.SEA;
	protected static int mN = MoveType.TRANS;
	protected static int mO = MoveType.OOZIUM;
	protected static int mP = MoveType.PIPE;
	protected static int mH = MoveType.HOVER;

	//protected static int[][] statTable = new int[UnitType.MAX_UNIT_TYPES][UnitStats.MAX_UNIT_STATS];   //Holds the Base Stats
    
    //constructor
    public UnitStatsTable() 
    {
    	super(UnitType.MAX_UNIT_TYPES, UnitStats.MAX_UNIT_STATS);
    	setup();
    }
    
    /** <code><i>SETUP</i></code>
     * <p>
     * Use at the start when getting CW ready. "Restores" the stat table.
     * 
     */
    public void setup()
    {
    	restoreStatTable();
    }
    
    /** <code><i>restoreStatTable</i></code>
     * <p>
     * Internally resets the damage table for normal mode.
     * 
     */
    public void restoreStatTable()
    {	
    	//Setup primary base stats
    	//
    	//NOTE: Star charge values are multiplied up by 10, since I'm too lazy to be dealing with doubles
    	//  						   		  	       0     1     2     3     4     5     6     7     8     9    10    11
		//                                         UTYPE    HP  FUEL VISIO  MOVE MTYPE DFUEL  COST  STAR CLASS  FPWR DFNSE
    	statTable[UnitType.INFANTRY]   = new int[]{    0,  100,   99,    2,    3,   mF,    0, 1000,    4,   cI,  100,  100};
    	statTable[UnitType.MECHINF]    = new int[]{    1,  100,   70,    2,    3,   mM,    0, 3000,    4,   cI,  100,  100};
    	statTable[UnitType.TANK]       = new int[]{    2,  100,   70,    3,    6,   mT,    0, 7000,   10,   cV,  100,  100};
    	statTable[UnitType.MDTANK]     = new int[]{    3,  100,   50,    1,    5,   mT,    0,15000,   16,   cV,  100,  100};
    	statTable[UnitType.RECON]      = new int[]{    4,  100,   80,    5,    8,   mR,    0, 4000,   10,   cV,  100,  100};
    	statTable[UnitType.ANTIAIR]    = new int[]{    5,  100,   60,    2,    6,   mT,    0, 8000,   10,   cV,  100,  100};
    	statTable[UnitType.MISSILE]     = new int[]{    6,  100,   50,    5,    4,   mR,    0,12000,   14,   cV,  100,  100};
    	statTable[UnitType.ARTILLERY]  = new int[]{    7,  100,   70,    3,    6,   mT,    0, 6000,   10,   cV,  100,  100};
    	statTable[UnitType.ROCKET]     = new int[]{    8,  100,   50,    2,    5,   mR,    0,14000,   14,   cV,  100,  100};
    	statTable[UnitType.APC]    	   = new int[]{    9,  100,   70,    1,    6,   mT,    0, 5000,    8,   cV,  100,  100};
    	statTable[UnitType.LANDER]     = new int[]{   10,  100,   99,    1,    6,   mN,    1,12000,   10,   cS,  100,  100};
    	statTable[UnitType.CRUISER]    = new int[]{   11,  100,   99,    3,    6,   mS,    1,15000,   16,   cS,  100,  100};
    	statTable[UnitType.SUBMARINE]  = new int[]{   12,  100,   60,    5,    5,   mS,    1,15000,   18,   cB,  100,  100};
    	statTable[UnitType.BATTLESHIP] = new int[]{   13,  100,   50,    1,    5,   mT,    0,16000,   22,   cS,  100,  100};
    	statTable[UnitType.TCOPTER]    = new int[]{   14,  100,   99,    2,    6,   mA,    2, 5000,   10,   cC,  100,  100};
    	statTable[UnitType.BCOPTER]    = new int[]{   15,  100,   99,    3,    6,   mA,    2, 9000,   12,   cC,  100,  100};
    	statTable[UnitType.FIGHTER]    = new int[]{   16,  100,   99,    2,    9,   mA,    5,20000,   18,   cJ,  100,  100};
    	statTable[UnitType.BOMBER]     = new int[]{   17,  100,   99,    2,    7,   mA,    5,20000,   18,   cJ,  100,  100};
    	statTable[UnitType.NEOTANK]    = new int[]{   18,  100,   99,    2,    5,   mT,    0,20000,   18,   cV,  100,  100};
    	statTable[UnitType.MEGATANK]   = new int[]{   19,  100,   50,    1,    4,   mT,    0,22000,   22,   cV,  100,  100};
    	statTable[UnitType.PIPERUNNER] = new int[]{   20,  100,   85,    4,    9,   mP,    0,18000,   20,   cV,  100,  100};
    	statTable[UnitType.BLACKBOAT]  = new int[]{   21,  100,   60,    1,    7,   mN,    1, 7500,   10,   cS,  100,  100};
    	statTable[UnitType.CARRIER]    = new int[]{   22,  100,   99,    4,    5,   mS,    1,30000,   22,   cS,  100,  100};
    	statTable[UnitType.STEALTH]    = new int[]{   23,  100,   60,    4,    6,   mA,    5,24000,   20,   cJ,  100,  100};
    	statTable[UnitType.BLACKBOMB]  = new int[]{   24,  100,   45,    1,    9,   mA,    5,25000,    6,   cJ,  100,  100};
    	statTable[UnitType.BCRAFT]     = new int[]{   25,  100,   70,    3,    5,   mH,    0,10000,   12,   cH,  100,  100};
    	statTable[UnitType.ACRAFT]     = new int[]{   26,  100,   70,    4,    4,   mH,    0,10000,   12,   cH,  100,  100};
    	statTable[UnitType.SRUNNER]    = new int[]{   27,  100,   85,    3,   11,   mP,    0,10000,   10,   cV,  100,  100};
    	statTable[UnitType.ZEPPELIN]   = new int[]{   28,  100,   70,    2,    5,   mA,    2,10000,   12,   cJ,  100,  100};
    	statTable[UnitType.SPYPLANE]   = new int[]{   29,  100,   99,    7,    8,   mA,    5,15000,   12,   cJ,  100,  100};
    	statTable[UnitType.DESTROYER]  = new int[]{   30,  100,   99,    2,    6,   mS,    1,15000,   16,   cS,  100,  100};
    	statTable[UnitType.OOZIUM]     = new int[]{   31,  100,   99,    1,    1,   mO,   -2,10000,   40,   cO,  100,  100};
    	statTable[UnitType.SUB_DIVE]   = new int[]{   32,  100,   60,    5,    5,   mS,    1,20000,   18,   cB,  100,  100};
    	statTable[UnitType.STL_HIDE]   = new int[]{   33,  100,   60,    4,    6,   mA,    5,24000,   20,   cJ,  100,  100};
    }
    
    /*
    /** <code<i>changeStatTableVal</i></code>
     * <p>
     * Modifies the value of one cell (UNIT_ID x STAT_ID) in the base stat table.
     * Boundary checks are made to prevent any tampering with values that don't exist
     * in the base stat table.
     * 
    public void changeStatTableVal(int UNIT_ID, int STAT_ID, int value)
    {
    	if((UNIT_ID >= 0 && UNIT_ID < UnitType.MAX_UNIT_TYPES) &&
    	   (STAT_ID >= 0 && STAT_ID < UnitStats.MAX_UNIT_STATS))
    	{
    		statTable[UNIT_ID][STAT_ID] = value;
    	}
    }
    
    /** <code><i>getUnitStats</i></code>
     * <p>
     * Returns the base stats of a unit with the given unit ID.
     * 
    public int[] getUnitStats(int UNIT_ID)
    {
    	if(UNIT_ID >= 0 && UNIT_ID < UnitType.MAX_UNIT_TYPES)
    	{
	    	return statTable[UNIT_ID];
    	}
    	return null;
    }
    */
}