package com.customwars;
/*
 *MoveCostTable.java
 *Author: Ting Chow
 *Contributors: -
 *Creation: 2/7/2008
 *  The MoveCostTable class holds a large array of base stat values for use in 
 *  determining move costs.
 */

public class MoveCostTable extends StatTable
{
    //Refer to MoveType.java for each movement type's ID
    //Refer to TerrainType.java for each terrain type's ID
    //protected static int[][] statTable = new int[TerrType.MAX_TERRAIN_TYPES][MoveType.MAX_MOVE_TYPES];   //Holds the Base Stats
    
    //constructor
    public MoveCostTable() 
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
		//                                                0     1     2     3     4     5     6     7     8     9
		//                                             FOOT  MECH TREAD TIRES   AIR   SEA TRANS  OOZE  PIPE  HOVR
		statTable[TerrType.PLAINS]     	 = new int[]{     1,    1,    1,    2,    1,   -1,   -1,    1,   -1,    1};
		statTable[TerrType.WOOD]      	 = new int[]{	  1,	1,	  2,	3,	  1,   -1,   -1,    1,   -1,    4};
		statTable[TerrType.MOUNTAIN]  	 = new int[]{	  2,	1,   -1,   -1,	  1,   -1,   -1,	1,	 -1,   -1};
		statTable[TerrType.ROAD]  	  	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.BRIDGE]    	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.RIVER]     	 = new int[]{	  2,	1,	 -1,   -1,	  1,   -1,	 -1,	1,   -1,	1};
		statTable[TerrType.SEA]       	 = new int[]{	 -1,   -1,   -1,   -1,    1,    1,    1,   -1,   -1,    1};
		statTable[TerrType.REEF]      	 = new int[]{	 -1,   -1,	 -1,   -1,	  1,	2,	  2,   -1,	 -1,	1};
		statTable[TerrType.SHOAL]     	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	  1,	1,   -1,	1};
		statTable[TerrType.HQ]     	  	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.CITY]      	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.BASE]      	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	  1,	1};
		statTable[TerrType.AIRPORT]   	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.PORT]   	  	 = new int[]{	  1,	1,	  1,	1,	  1,    1,	  1,	1,	 -1,	1};
		statTable[TerrType.COM_TOWER]  	 = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.PIPE]  	  	 = new int[]{	 -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,	  1,   -1};
		statTable[TerrType.SILO]  	     = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.PIPE_STATION] = new int[]{	  1,	1,	  1,	1,	  1,   -1,	 -1,	1,	 -1,	1};
		statTable[TerrType.PIPE_SEAM] 	 = new int[]{	 -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,	  1,   -1};
		statTable[TerrType.DEST_SEAM] 	 = new int[]{     1,    1,    1,    2,    1,   -1,   -1,    1,   -1,    1};
		statTable[TerrType.SUS_BRIDGE]   = new int[]{	  1,	1,	  1,	1,	  1,    1,	  1,	1,	 -1,	1};
		statTable[TerrType.WALL]  		 = new int[]{	 -1,   -1,	 -1,   -1,	  1,   -1,	 -1,   -1,	 -1,   -1};
		statTable[TerrType.DEST_WALL] 	 = new int[]{     1,    1,    1,    2,    1,   -1,   -1,    1,   -1,    1};
		
		statTable[TerrType.SEA_PIPE] 	 = new int[]{	 -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,	  1,   -1};
		statTable[TerrType.SP_SEAM] 	 = new int[]{	 -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,	  1,   -1};
		statTable[TerrType.DEST_SPS]     = new int[]{	 -1,   -1,   -1,   -1,    1,    1,    1,   -1,   -1,    1};
    }
}