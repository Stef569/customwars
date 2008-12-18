package com.customwars;

import com.customwars.unit.MoveType;
/*
 *TerrStatsTable.java
 *Author: Ting Chow
 *Contributors: -
 *Creation: 2/7/2008
 *  The TerrStatsTable class holds a large array of base stat values for use in 
 *  creating bland terrain.
 */


public class TerrStatsTable extends StatTable
{
    //Refer to MoveType.java for each movement type's ID
    //Refer to TerrainType.java for each terrain type's ID
    //protected static int[][] statTable = new int[TerrType.MAX_TERRAIN_TYPES][TerrStats.MAX_PROPERTY_STATS];   //Holds the Base Stats
    
    //constructor
    public TerrStatsTable() 
    {
    	super(TerrType.MAX_TERRAIN_TYPES, MoveType.MAX_MOVE_TYPES);
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
    	// Will there be destructible terrain!?
		//                                                0     1     2     3     4     5     6
		//                                             TYPE    HP STYLE DSTAR CAPTL VISIO FUNDS
		statTable[TerrType.PLAINS]     	= new int[]{      0,   -1,    0,    1,    0,   -1,    0};
		statTable[TerrType.WOOD]      	= new int[]{	  1,   -1,    0,    2,    0,   -1,    0};
		statTable[TerrType.MOUNTAIN]  	= new int[]{	  2,   -1,    0,    4,    0,   -1,    0};
		statTable[TerrType.ROAD]  	  	= new int[]{	  3,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.BRIDGE]    	= new int[]{	  4,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.RIVER]     	= new int[]{	  5,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.SEA]       	= new int[]{	  6,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.REEF]      	= new int[]{	  7,   -1,    0,    1,    0,   -1,    0};
		statTable[TerrType.SHOAL]     	= new int[]{	  8,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.HQ]     	  	= new int[]{	  9,   -1,    0,    4,   20,    0, 1000};
		statTable[TerrType.CITY]      	= new int[]{	 10,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.BASE]      	= new int[]{	 11,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.AIRPORT]   	= new int[]{	 12,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.PORT]   	  	= new int[]{	 13,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.COM_TOWER]  	= new int[]{	 14,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.PIPE]  	  	= new int[]{	 15,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.SILO]  	    = new int[]{	 16,   -1,    0,    3,    0,   -1,    0};
		statTable[TerrType.PIPE_STATION] = new int[]{	 17,   -1,    0,    3,   20,    0, 1000};
		statTable[TerrType.PIPE_SEAM] 	= new int[]{	 18,   99,    0,    0,    0,   -1,    0};
		statTable[TerrType.DEST_SEAM] 	= new int[]{     19,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.SUS_BRIDGE]  = new int[]{	 20,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.WALL]  		= new int[]{	 21,   99,    0,    0,    0,   -1,    0};
		statTable[TerrType.DEST_WALL]  	= new int[]{	 22,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.SEA_PIPE] 	= new int[]{	 23,   -1,    0,    0,    0,   -1,    0};
		statTable[TerrType.SP_SEAM] 	= new int[]{     24,   99,    0,    0,    0,   -1,    0};
		statTable[TerrType.DEST_SPS]    = new int[]{	 25,   -1,    0,    0,    0,   -1,    0};
    }
}