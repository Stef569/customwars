package cwsource;
/*
 *WeaponDmgTable.java
 *Author: Ting Chow
 *Contributors: -
 *Creation: 2/9/2008
 *  The WeaponDmgTable class holds a large array of base stat values for use in 
 *  dealing damage to units.
 */

public class WeaponDmgTable extends StatTable
{   
    //constructor
    public WeaponDmgTable() 
    {
    	super(WeaponType.MAX_WEAPON_TYPES, UnitType.MAX_UNIT_TYPES);
    	setup();
    }
    
    /** <code><i>SETUP</i></code>
     * <p>
     * Use at the start when getting CW ready. "Restores" the damage tables and assigns the
     * damage tables to each cell in allTables.
     * 
     */
    public void setup()
    {
    	restoreStatTable();
    }
    
    /** <code<i>RESTOREDAMAGETABLES</i></code>
     * <p>
     * Internally resets the damage table for normal mode.
     * 
     */
    public void restoreStatTable()
    {
    	//Setup primary weapon base damage    	
		//                                                  0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                                                INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
		statTable[WeaponType.MECH_BZKA]      = new int[]{   -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   15,    5,   55,   -1,   -1,   -1,   -1,   65,   65,   55,   -1,   -1,   -1,   30};
		
		statTable[WeaponType.TANK_CAN]       = new int[]{   -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   10,    5,    1,    1,   -1,   -1,   -1,   -1,   15,   10,   55,   10,    1,   -1,   -1,   65,   65,   55,   -1,   -1,    5,   20};
		
		statTable[WeaponType.MDTANK_CAN]     = new int[]{   -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   95,   95,   85,   -1,   -1,   25,   30};
		
		statTable[WeaponType.ANTIAIR_VLC]    = new int[]{  105,  105,   25,   10,   60,   45,   55,   50,   55,   50,   -1,   -1,   -1,   -1,  105,  105,   65,   75,    5,    1,   25,   -1,   -1,   75,  120,   55,   55,   25,  115,   45,   -1,   30};
		
		statTable[WeaponType.MISSLE_MSL]     = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  115,  115,  100,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,  115,   65,   -1,   -1};
		
		statTable[WeaponType.ARTILLERY_CAN]  = new int[]{   90,   85,   70,   45,   80,   75,   80,   75,   80,   70,   55,   50,   60,   40,   -1,   -1,   -1,   -1,   40,   20,   70,   55,   45,   -1,   -1,   80,   80,   70,   -1,   -1,   50,    5};
		
		statTable[WeaponType.ROCKET_RKT]     = new int[]{   95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   60,   60,   85,   55,   -1,   -1,   -1,   -1,   50,   35,   80,   60,   60,   -1,   -1,   85,   85,   80,   -1,   -1,   60,   15};
		
		statTable[WeaponType.CRUISER_ASM]    = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,   25,   90,    5,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,    5,   -1,   -1,   65,   65,   -1,   -1,   -1,   25,   -1};
		
		statTable[WeaponType.SUBMARINE_TRP]  = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   25,   55,   55,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   -1};
		
		statTable[WeaponType.BATTLESHIP_CAN] = new int[]{   95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   95,   95,   95,   50,   -1,   -1,   -1,   -1,   50,   25,   80,   95,   60,   -1,   -1,   95,   95,   80,   -1,   -1,   95,   20};
		
		statTable[WeaponType.BCOPTER_MSL]    = new int[]{   -1,   -1,   55,   25,   55,   25,   65,   65,   65,   60,   25,   25,   25,   25,   95,   65,   -1,   -1,   20,   10,   55,   25,   25,   -1,   -1,   65,   65,   55,   -1,   -1,   55,   25};
		
		statTable[WeaponType.FIGHTER_MSL]    = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  100,  100,   55,  100,   -1,   -1,   -1,   -1,   -1,   85,  120,   -1,   -1,   -1,  120,   65,   -1,   -1};
		
		statTable[WeaponType.BOMBER_BMB]     = new int[]{  110,  110,  105,   95,  105,   95,  105,  105,  105,  105,   95,   50,   95,   75,   -1,   -1,   -1,   -1,   90,   35,  105,   95,   75,   -1,   -1,  105,  105,  105,   -1,   -1,   85,   35};
		
		statTable[WeaponType.NEOTANK_CAN]    = new int[]{   -1,   -1,  105,   75,  125,  115,  125,  115,  125,  125,   40,   30,   15,   15,   -1,   -1,   -1,   -1,   55,   40,  105,   40,   15,   -1,   -1,  125,  125,  105,   -1,   -1,   30,   35};
		
		statTable[WeaponType.MEGATANK_CAN]   = new int[]{   -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   95,   95,   85,   -1,   -1,   25,   30};
		
		statTable[WeaponType.PIPERUNNER_CAN] = new int[]{   95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   60,   60,   85,   55,  105,  105,   65,   75,   50,   25,   80,   60,   60,   75,  120,   85,   85,   80,  105,   45,   60,   15};
		
		statTable[WeaponType.CARRIER_MSL]    = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  115,  115,  100,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,  115,   65,   -1,   -1};

		statTable[WeaponType.STEALTH_OMNI]   = new int[]{   90,   90,   75,   70,   85,   50,   85,   75,   85,   85,   65,   35,   55,   45,   95,   85,   45,   70,   60,   15,   80,   65,   45,   55,  120,   85,   85,   80,   85,   45,   65,   30};
		
		statTable[WeaponType.BCRAFT_CAN]     = new int[]{   -1,   -1,   95,   75,   95,   95,   80,   80,   80,   95,   50,   35,   35,   15,   -1,   -1,   -1,   -1,   55,   30,   70,   50,   15,   -1,   -1,   95,   95,   95,   -1,   -1,   35,   30};
		
		statTable[WeaponType.ACRAFT_CAN]     = new int[]{   95,   90,   75,   50,   85,   80,   85,   80,   85,   75,   55,   50,   50,   40,   -1,   -1,   -1,   -1,   45,   20,   75,   55,   45,   -1,   -1,   85,   85,   75,   -1,   -1,   50,   10};
		
		statTable[WeaponType.ZEPPELIN_CAN]   = new int[]{   50,   45,   40,   25,   45,   40,   45,   40,   45,   40,   30,   30,   45,   25,   50,   50,   35,   40,   20,   10,   40,   30,   30,   45,  100,   45,   45,   40,   55,   22,   30,    5};
		
		statTable[WeaponType.DESTROYER_CAN]  = new int[]{  -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   55,   90,   25,   25,   -1,   -1,   -1,   -1,   45,   25,  100,   55,   25,   -1,   -1,   95,   95,  105,   -1,   -1,   55,   35};
    	

    	//Setup secondary weapon base damage    	
		//                                                  0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                                                INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
    	statTable[WeaponType.INF_GUN]        = new int[]{   55,   45,    5,    1,   12,    5,   26,   15,   25,   14,   -1,   -1,   -1,   -1,   30,    7,   -1,   -1,    1,    1,    5,   -1,   -1,   -1,   -1,   25,   25,   -1,   -1,   -1,   -1,   20};
    	
		statTable[WeaponType.MECH_GUN]       = new int[]{   65,   55,    6,    1,   18,    6,   35,   32,   35,   20,   -1,   -1,   -1,   -1,   35,    9,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.TANK_GUN]       = new int[]{   75,   70,    6,    1,   40,    6,   30,   45,   55,   45,   -1,   -1,   -1,   -1,   40,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.MDTANK_GUN]     = new int[]{  105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,   45,   45,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.RECON_GUN]      = new int[]{   70,   65,    6,    1,   35,    4,   28,   45,   55,   45,   -1,   -1,   -1,   -1,   35,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.CRUISER_AAG]    = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  105,  105,   85,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		
		statTable[WeaponType.BCOPTER_GUN]    = new int[]{   75,   75,    6,    1,   30,    6,   35,   25,   35,   25,   -1,   -1,   -1,   -1,   95,   65,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.NEOTANK_GUN]    = new int[]{  125,  115,   10,    1,   65,   17,   55,   65,   75,   65,   -1,   -1,   -1,   -1,   55,   22,   -1,   -1,    1,    1,   10,   -1,   -1,   -1,   -1,   65,   65,   -1,   -1,   -1,   -1,   20};
		
		statTable[WeaponType.MEGATANK_GUN]   = new int[]{  135,  125,   10,    1,   65,   17,   55,   65,   75,   65,   -1,   -1,   -1,   -1,   55,   22,   -1,   -1,    1,    1,   10,   -1,   -1,   -1,   -1,   75,   75,   -1,   -1,   -1,   -1,   30};
		
		statTable[WeaponType.BCRAFT_GUN]     = new int[]{   85,   75,    6,    1,   35,    5,   25,   35,   45,   35,   -1,   -1,   -1,   -1,   35,    8,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};

		statTable[WeaponType.DESTROYER_GUN]  = new int[]{  105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,   45,   45,   -1,   -1,   -1,   -1,   20};
    }
}