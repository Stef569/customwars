package cwsource;
/*
 *TerrStatsTable.java
 *Author: Ting Chow
 *Contributors: -
 *Creation: 2/7/2008
 *  The TerrStatsTable class holds a large array of base stat values for use in 
 *  creating bland terrain.
 */


public class WeaponStatsTable extends StatTable
{
    //Refer to MoveType.java for each movement type's ID
    //Refer to TerrainType.java for each terrain type's ID
    //protected static int[][] statTable = new int[TerrType.MAX_TERRAIN_TYPES][TerrStats.MAX_PROPERTY_STATS];   //Holds the Base Stats
    
    //constructor
    public WeaponStatsTable() 
    {
    	super(WeaponType.MAX_WEAPON_TYPES, WeaponStats.MAX_WEAPON_STATS);
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
		//                                                     0     1     2     3     4
		//                                               WEAP_ID WTYPE  MINR  MAXR  AMMO
		statTable[WeaponType.INF_GUN]        = new int[]{     0,    0,    1,    1,    0};
		statTable[WeaponType.MECH_BZKA]      = new int[]{	  1,	0,	  1,	1,	  3};
		statTable[WeaponType.MECH_GUN]       = new int[]{	  2,	0,	  1,	1,	  0};
		statTable[WeaponType.TANK_CAN]       = new int[]{	  3,	0,	  1,	1,	  9};
		statTable[WeaponType.TANK_GUN]       = new int[]{	  4,	0,	  1,	1,	  0};
		statTable[WeaponType.MDTANK_CAN]     = new int[]{	  5,	0,	  1,	1,	  8};
		statTable[WeaponType.MDTANK_GUN]     = new int[]{	  6,	0,	  1,	1,	  0};
		statTable[WeaponType.RECON_GUN]      = new int[]{	  7,	0,	  1,	1,	  0};
		statTable[WeaponType.ANTIAIR_VLC]    = new int[]{	  8,	0,	  1,	1,	  9};
		statTable[WeaponType.MISSLE_MSL]     = new int[]{	  9,	0,	  3,	5,	  6};
		statTable[WeaponType.ARTILLERY_CAN]  = new int[]{	 10,	1,	  2,	3,	  9};
		statTable[WeaponType.ROCKET_RKT]     = new int[]{	 11,	1,	  3,	5,	  6};
		statTable[WeaponType.CRUISER_ASM]    = new int[]{	 12,	0,	  1,	1,	  9};
		statTable[WeaponType.CRUISER_AAG]    = new int[]{	 13,	0,	  1,	1,	  0};
		statTable[WeaponType.SUBMARINE_TRP]  = new int[]{	 14,	0,	  1,	1,	  6};
		statTable[WeaponType.BATTLESHIP_CAN] = new int[]{	 15,	1,	  2,	6,	  9};
		statTable[WeaponType.BCOPTER_MSL]  	 = new int[]{	 16,    0,    1,    1,    6};
		statTable[WeaponType.BCOPTER_GUN]  	 = new int[]{	 17,	0,	  1,	1,	  0};
		statTable[WeaponType.FIGHTER_MSL]    = new int[]{	 18,	0,	  1,	1,	  9};
		statTable[WeaponType.BOMBER_BMB] 	 = new int[]{	 19,    0,    1,    1,    9};
		statTable[WeaponType.NEOTANK_CAN] 	 = new int[]{    20,    0,    1,    1,    9};
		statTable[WeaponType.NEOTANK_GUN] 	 = new int[]{    21,    0,    1,    1,    0};
		statTable[WeaponType.MEGATANK_CAN]   = new int[]{	 22,	0,	  1,	1,	  3};
		statTable[WeaponType.MEGATANK_GUN]   = new int[]{	 23,    0,	  1,    1,	  0};
		statTable[WeaponType.PIPERUNNER_CAN] = new int[]{	 24,    1,    2,    6,    9};
		statTable[WeaponType.CARRIER_MSL]    = new int[]{	 25,    1,    3,    8,    9};
		statTable[WeaponType.STEALTH_OMNI]   = new int[]{	 26,    0,    1,    1,    6};
		statTable[WeaponType.BCRAFT_CAN]     = new int[]{	 27,    0,    1,    1,    9};
		statTable[WeaponType.BCRAFT_GUN]     = new int[]{	 28,    0,    1,    1,    0};
		statTable[WeaponType.ACRAFT_CAN]     = new int[]{	 29,    1,    3,    4,    6};
		statTable[WeaponType.ZEPPELIN_CAN]   = new int[]{	 30,    1,    2,    4,    9};
		statTable[WeaponType.DESTROYER_CAN]  = new int[]{	 31,    0,    1,    1,    9};
		statTable[WeaponType.DESTROYER_GUN]  = new int[]{	 32,    0,    1,    1,    6};
    }
}