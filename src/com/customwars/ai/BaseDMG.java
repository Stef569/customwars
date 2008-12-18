package com.customwars.ai;
/*
 *BaseDMG.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 27, 2006, 4:36 PM
 *The BaseDMG class holds a large array of base damage values for use in damage calculation in Unit.
 */

import java.io.*;           //Necessary for file input

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.state.ResourceLoader;
import com.customwars.unit.Unit;

public class BaseDMG {
    public final static int NUM_UNITS = 32;
    //0=Infantry 1=Mech 2=Tank 3=Md Tank 4=Recon 5=Anti-Air 6=Missiles 7=Artillery 8=Rockets 9=APC
    //10=Lander 11=Cruiser 12=Sub 13=B Ship 14=T Copter 15=B Copter 16=Fighter 17=Bomber
    private static int[][] baseDMG = new int[NUM_UNITS][NUM_UNITS];   //Holds the Base Damage for the Primary Weapon
    private static int[][] altDMG = new int [NUM_UNITS][NUM_UNITS];   //Holds the Base Damage for the Secondary Weapon
    private static int[][] baseDMGb = new int[NUM_UNITS][NUM_UNITS];   //Holds the Base Damage for the Primary Weapon in Balance Mode
    private static int[][] altDMGb = new int [NUM_UNITS][NUM_UNITS];   //Holds the Base Damage for the Secondary Weapon in Balance Mode
	final static Logger logger = LoggerFactory.getLogger(BaseDMG.class); 
    
    public static final int BASE_DMG = 0;
    public static final int ALT_DMG = 1;
    public static final int BASE_DMGb = 2;
    public static final int ALT_DMGb = 3;
    
    private static int[][][] allTables = new int[4][NUM_UNITS][NUM_UNITS];
    
    //constructor
    public BaseDMG() 
    {
    	
    }
    
    /** <code><i>SETUP</i></code>
     * <p>
     * Use at the start when getting CW ready. "Restores" the damage tables and assigns the
     * damage tables to each cell in allTables.
     * 
     */
    public static void setup()
    {
    	restoreDamageTables();
    	restoreBalanceDamageTables();
    	
    	allTables[BASE_DMG] = baseDMG;
    	allTables[ALT_DMG] = altDMG;
    	allTables[BASE_DMGb] = baseDMGb;
    	allTables[ALT_DMGb] = altDMGb;
    }
    
    /** <code<i>RESTOREDAMAGETABLES</i></code>
     * <p>
     * Internally resets the damage table for normal mode.
     * 
     */
    public static void restoreDamageTables()
    {
    	//Setup primary weapon base damage    	
		//                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
		baseDMG[0] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[1] = new int[]{   -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   15,    5,   55,   -1,   -1,   -1,   -1,   65,   65,   55,   -1,   -1,   -1,   30};
		baseDMG[2] = new int[]{   -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   10,    5,    1,    1,   -1,   -1,   -1,   -1,   15,   10,   55,   10,    1,   -1,   -1,   65,   65,   55,   -1,   -1,    5,   20};
		baseDMG[3] = new int[]{   -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   95,   95,   85,   -1,   -1,   25,   30};
		baseDMG[4] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[5] = new int[]{  105,  105,   25,   10,   60,   45,   55,   50,   55,   50,   -1,   -1,   -1,   -1,  105,  105,   65,   75,    5,    1,   25,   -1,   -1,   75,  120,   55,   55,   25,  115,   45,   -1,   30};
		baseDMG[6] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  115,  115,  100,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,  115,   65,   -1,   -1};
		baseDMG[7] = new int[]{   90,   85,   70,   45,   80,   75,   80,   75,   80,   70,   55,   50,   60,   40,   -1,   -1,   -1,   -1,   40,   20,   70,   55,   45,   -1,   -1,   80,   80,   70,   -1,   -1,   50,    5};
		baseDMG[8] = new int[]{   95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   60,   60,   85,   55,   -1,   -1,   -1,   -1,   50,   35,   80,   60,   60,   -1,   -1,   85,   85,   80,   -1,   -1,   60,   15};
		baseDMG[9] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[10] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[11] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,   25,   90,    5,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,    5,   -1,   -1,   65,   65,   -1,   -1,   -1,   25,   -1};
		baseDMG[12] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   25,   55,   55,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   -1};
		baseDMG[13] = new int[]{  95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   95,   95,   95,   50,   -1,   -1,   -1,   -1,   50,   25,   80,   95,   60,   -1,   -1,   95,   95,   80,   -1,   -1,   95,   20};
		baseDMG[14] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[15] = new int[]{  -1,   -1,   55,   25,   55,   25,   65,   65,   65,   60,   25,   25,   25,   25,   95,   65,   -1,   -1,   20,   10,   55,   25,   25,   -1,   -1,   65,   65,   55,   -1,   -1,   55,   25};
		baseDMG[16] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  100,  100,   55,  100,   -1,   -1,   -1,   -1,   -1,   85,  120,   -1,   -1,   -1,  120,   65,   -1,   -1};
		baseDMG[17] = new int[]{ 110,  110,  105,   95,  105,   95,  105,  105,  105,  105,   95,   50,   95,   75,   -1,   -1,   -1,   -1,   90,   35,  105,   95,   75,   -1,   -1,  105,  105,  105,   -1,   -1,   85,   35};
		baseDMG[18] = new int[]{  -1,   -1,  105,   75,  125,  115,  125,  115,  125,  125,   40,   30,   15,   15,   -1,   -1,   -1,   -1,   55,   40,  105,   40,   15,   -1,   -1,  125,  125,  105,   -1,   -1,   30,   35};
		baseDMG[19] = new int[]{  -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   95,   95,   85,   -1,   -1,   25,   30};
		baseDMG[20] = new int[]{  95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   60,   60,   85,   55,  105,  105,   65,   75,   50,   25,   80,   60,   60,   75,  120,   85,   85,   80,  105,   45,   60,   15};
		baseDMG[21] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[22] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  115,  115,  100,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,  115,   65,   -1,   -1};
		baseDMG[23] = new int[]{  90,   90,   75,   70,   85,   50,   85,   75,   85,   85,   65,   35,   55,   45,   95,   85,   45,   70,   60,   15,   80,   65,   45,   55,  120,   85,   85,   80,   85,   45,   65,   30};
		baseDMG[24] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[25] = new int[]{  -1,   -1,   95,   75,   95,   95,   80,   80,   80,   95,   50,   35,   35,   15,   -1,   -1,   -1,   -1,   55,   30,   70,   50,   15,   -1,   -1,   95,   95,   95,   -1,   -1,   35,   30};
		baseDMG[26] = new int[]{  95,   90,   75,   50,   85,   80,   85,   80,   85,   75,   55,   50,   50,   40,   -1,   -1,   -1,   -1,   45,   20,   75,   55,   45,   -1,   -1,   85,   85,   75,   -1,   -1,   50,   10};
		baseDMG[27] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[28] = new int[]{  50,   45,   40,   25,   45,   40,   45,   40,   45,   40,   30,   30,   45,   25,   50,   50,   35,   40,   20,   10,   40,   30,   30,   45,  100,   45,   45,   40,   55,   22,   30,    5};
		baseDMG[29] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMG[30] = new int[]{  -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   55,   90,   25,   25,   -1,   -1,   -1,   -1,   45,   25,  100,   55,   25,   -1,   -1,   95,   95,  105,   -1,   -1,   55,   35};
		baseDMG[31] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
    	

    	//Setup secondary weapon base damage    	
		//                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
		altDMG[0] = new int[]{    55,   45,    5,    1,   12,    5,   26,   15,   25,   14,   -1,   -1,   -1,   -1,   30,    7,   -1,   -1,    1,    1,    5,   -1,   -1,   -1,   -1,   25,   25,   -1,   -1,   -1,   -1,   20};
		altDMG[1] = new int[]{    65,   55,    6,    1,   18,    6,   35,   32,   35,   20,   -1,   -1,   -1,   -1,   35,    9,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		altDMG[2] = new int[]{    75,   70,    6,    1,   40,    6,   30,   45,   55,   45,   -1,   -1,   -1,   -1,   40,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		altDMG[3] = new int[]{   105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,   45,   45,   -1,   -1,   -1,   -1,   20};
		altDMG[4] = new int[]{    70,   65,    6,    1,   35,    4,   28,   45,   55,   45,   -1,   -1,   -1,   -1,   35,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		altDMG[5] = new int[]{    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[6] = new int[]{    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[7] = new int[]{    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[8] = new int[]{    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[9] = new int[]{    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[10] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[11] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  105,  105,   85,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[12] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[13] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[14] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[15] = new int[]{   75,   75,    6,    1,   30,    6,   35,   25,   35,   25,   -1,   -1,   -1,   -1,   95,   65,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		altDMG[16] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[17] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[18] = new int[]{  125,  115,   10,    1,   65,   17,   55,   65,   75,   65,   -1,   -1,   -1,   -1,   55,   22,   -1,   -1,    1,    1,   10,   -1,   -1,   -1,   -1,   65,   65,   -1,   -1,   -1,   -1,   20};
		altDMG[19] = new int[]{  135,  125,   10,    1,   65,   17,   55,   65,   75,   65,   -1,   -1,   -1,   -1,   55,   22,   -1,   -1,    1,    1,   10,   -1,   -1,   -1,   -1,   75,   75,   -1,   -1,   -1,   -1,   30};
		altDMG[20] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[21] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[22] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[23] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[24] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[25] = new int[]{   85,   75,    6,    1,   35,    5,   25,   35,   45,   35,   -1,   -1,   -1,   -1,   35,    8,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,   35,   35,   -1,   -1,   -1,   -1,   20};
		altDMG[26] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[27] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[28] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[29] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMG[30] = new int[]{  105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,   45,   45,   -1,   -1,   -1,   -1,   20};
		altDMG[31] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
    }
    
    /** <code><i>RESTOREBALANCEDAMAGETABLES</i></code>
     * <p>
     * Internally resets the damage table for balance mode.
     *  
     */
    public static void restoreBalanceDamageTables()
    {
    	//Setup primary weapon base damage (for balance mode)
		//                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
		baseDMGb[0] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[1] = new int[]{  -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   15,    5,   55,   -1,   -1,   -1,   -1,   45,   85,   55,   -1,   -1,   -1,   30};
		baseDMGb[2] = new int[]{  -1,   -1,   55,   15,   85,   65,   85,   70,   85,   75,   10,    5,    1,    1,   -1,   -1,   -1,   -1,   15,   10,   55,   10,    1,   -1,   -1,   45,   85,   55,   -1,   -1,    5,   20};
		baseDMGb[3] = new int[]{  -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   85,  105,   85,   -1,   -1,   25,   30};
		baseDMGb[4] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[5] = new int[]{ 105,  105,   25,   10,   60,   45,   55,   50,   55,   50,   -1,   -1,   -1,   -1,  105,  105,   65,   75,    5,    1,   25,   -1,   -1,   75,  120,   25,   55,   25,  115,   45,   -1,   30};
		baseDMGb[6] = new int[]{  25,   20,   20,   15,   20,   20,   20,   20,   20,   20,   15,   15,   20,   10,  115,  115,  100,  100,   10,    5,   20,   15,   15,  100,  120,   20,   20,   20,  115,   65,   15,    3};
		baseDMGb[7] = new int[]{  90,   85,   70,   45,   80,   75,   80,   75,   80,   70,   55,   35,   60,   30,   -1,   -1,   -1,   -1,   40,   20,   70,   55,   45,   -1,   -1,   70,   80,   70,   -1,   -1,   50,    5};
		baseDMGb[8] = new int[]{  95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   45,   45,   85,   45,   -1,   -1,   -1,   -1,   50,   35,   80,   60,   60,   -1,   -1,   80,   85,   80,   -1,   -1,   60,   15};
		baseDMGb[9] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[10] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[11] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,   25,   90,    5,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   25,    5,   -1,   -1,   85,   85,   -1,   -1,   -1,   25,   -1};
		baseDMGb[12] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   55,   25,   55,   65,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  105,   75,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   95,   -1};
		baseDMGb[13] = new int[]{ 95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   95,   85,   95,   50,   -1,   -1,   -1,   -1,   50,   25,   80,  105,   60,   -1,   -1,   95,   95,   80,   -1,   -1,   95,   20};
		baseDMGb[14] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[15] = new int[]{ -1,   -1,   55,   25,   55,   25,   65,   65,   65,   60,   25,   25,   25,   25,   95,   65,   -1,   -1,   20,   10,   55,   25,   25,   -1,   -1,   65,   65,   55,   -1,   -1,   55,   25};
		baseDMGb[16] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  100,  100,   55,  100,   -1,   -1,   -1,   -1,   -1,   85,  120,   -1,   -1,   -1,  120,   65,   -1,   -1};
		baseDMGb[17] = new int[]{140,  125,  120,  110,  125,  110,  125,  125,  125,  125,  105,   65,  105,   75,   -1,   -1,   -1,   -1,  105,   45,  125,  105,   75,   -1,   -1,  125,  125,  125,   -1,   -1,  105,   35};
		baseDMGb[18] = new int[]{ -1,   -1,  105,   75,  125,  115,  125,  115,  125,  125,   40,   30,   15,   15,   -1,   -1,   -1,   -1,   55,   40,  105,   40,   15,   -1,   -1,  105,  125,  105,   -1,   -1,   30,   35};
		baseDMGb[19] = new int[]{ -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   35,   30,   10,   10,   -1,   -1,   -1,   -1,   45,   25,   85,   35,   10,   -1,   -1,   85,  105,   85,   -1,   -1,   25,   30};
		baseDMGb[20] = new int[]{ 95,   90,   80,   55,   90,   85,   90,   80,   85,   80,   60,   60,   85,   55,  105,  105,   65,   75,   50,   25,   80,   60,   60,   75,  120,   80,   85,   80,  105,   45,   60,   15};
		baseDMGb[21] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
              //baseDMGb[22] = new int[]{ 25,   20,   20,   15,   20,   20,   20,   20,   20,   20,   10,   15,   20,   10,  115,  115,  100,  100,   10,    5,   20,   15,   15,  100,  120,   20,   20,   20,  115,   65,   15,    3};
		baseDMGb[22] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  115,  115,  100,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,  115,   65,   -1,   -1};
                baseDMGb[23] = new int[]{ 90,   90,   75,   70,   85,   50,   85,   75,   85,   85,   65,   35,   55,   45,   95,   85,   45,   70,   60,   15,   80,   65,   45,   55,  120,   85,   85,   80,   85,   45,   65,   30};
		baseDMGb[24] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[25] = new int[]{ -1,   -1,   85,   35,   80,   75,   85,   80,   80,   85,   40,   35,   35,   15,   -1,   -1,   -1,   -1,   25,   20,   65,   50,   15,   -1,   -1,   55,   95,   65,   -1,   -1,   35,   30};
		baseDMGb[26] = new int[]{ 95,   90,   75,   50,   85,   80,   85,   80,   85,   75,   40,   40,   40,   25,   -1,   -1,   -1,   -1,   45,   20,   75,   50,   35,   -1,   -1,   85,   85,   75,   -1,   -1,   40,   10};
		baseDMGb[27] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[28] = new int[]{ 50,   45,   35,   25,   40,   35,   40,   35,   40,   35,   25,   25,   40,   20,   45,   45,   30,   35,   15,   10,   35,   25,   25,   40,  100,   35,   40,   35,   55,   18,   25,    5};
		baseDMGb[29] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		baseDMGb[30] = new int[]{ -1,   -1,   85,   55,  105,  105,  105,  105,  105,  105,   45,   90,   25,   25,   -1,   -1,   -1,   -1,   45,   25,  100,   55,   25,   -1,   -1,  115,  115,  105,   -1,   -1,   55,   35};
		baseDMGb[31] = new int[]{ -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};


    	//Setup secondary weapon base damage (for balance mode)
		//                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
		//                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
		altDMGb[0] = new int[]{   55,   45,    5,    1,   12,    5,   26,   15,   25,   14,   -1,   -1,   -1,   -1,   30,    7,   -1,   -1,    1,    1,    5,   -1,   -1,   -1,   -1,    5,   25,   -1,   -1,   -1,   -1,   20};
		altDMGb[1] = new int[]{   65,   55,    6,    1,   18,    6,   35,   32,   35,   20,   -1,   -1,   -1,   -1,   35,    9,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,    6,   35,   -1,   -1,   -1,   -1,   20};
		altDMGb[2] = new int[]{   75,   70,    6,    1,   40,    6,   30,   45,   55,   45,   -1,   -1,   -1,   -1,   40,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,    6,   35,   -1,   -1,   -1,   -1,   20};
		altDMGb[3] = new int[]{  105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,    8,   45,   -1,   -1,   -1,   -1,   20};
		altDMGb[4] = new int[]{   70,   65,    6,    1,   35,    4,   28,   45,   55,   45,   -1,   -1,   -1,   -1,   35,   10,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,    6,   35,   -1,   -1,   -1,   -1,   20};
		altDMGb[5] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[6] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[7] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[8] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[9] = new int[]{   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[10] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[11] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  105,  105,   85,  100,   -1,   -1,   -1,   -1,   -1,  100,  120,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[12] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[13] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[14] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[15] = new int[]{  75,   75,    6,    1,   30,    6,   35,   25,   35,   25,   -1,   -1,   -1,   -1,   95,   65,   -1,   -1,    1,    1,    6,   -1,   -1,   -1,   -1,    6,   35,   -1,   -1,   -1,   -1,   20};
		altDMGb[16] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[17] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[18] = new int[]{ 125,  115,   10,    1,   65,   17,   55,   65,   75,   65,   -1,   -1,   -1,   -1,   55,   22,   -1,   -1,    1,    1,   10,   -1,   -1,   -1,   -1,   10,   65,   -1,   -1,   -1,   -1,   20};
		altDMGb[19] = new int[]{ 105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,    8,   45,   -1,   -1,   -1,   -1,   20};
		altDMGb[20] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[21] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[22] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[23] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[24] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[25] = new int[]{  45,   35,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,    6,   45,   -1,   -1,   -1,   -1,   20};
		altDMGb[26] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[27] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[28] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[29] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
		altDMGb[30] = new int[]{ 105,   95,    8,    1,   45,    7,   35,   45,   55,   45,   -1,   -1,   -1,   -1,   45,   12,   -1,   -1,    1,    1,    8,   -1,   -1,   -1,   -1,   45,   45,   -1,   -1,   -1,   -1,   20};
		altDMGb[31] = new int[]{  -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1};
    }
    
    /** <code><i>SETDAMAGE_ATKRANDTARG</i></code>
     * <p>
     * Sets the damage dealt by units with ID = <code>attackerID</code> against targets with
     * ID = <code>targetID</code>. The <code>dmgTable</code> variable determines which table
     * will be adjusted with this change. Remember that if the damage tables are changed, their
     * original values need to be restored later on!
     * @param attackerID
     * <p><t>Used as the row variable for assigning <code>damage</code> to a damage table. 
     * Should also be the unitID of the affected attacking unit.
     * @param targetID
     * <p><t>Used as the column variable for assigning <code>damage</code> to a damage table. 
     * Should also be the unitID of the affected target unit.
     * @param damage
     * <p><t>The damage that the attacking unit and it's target should deal to each other.
     * @param dmgTable
     * <p><t>The damage table which will be using this change. Determined by table ID.
     */
    public static void setDamage_atkrANDtarg(int attackerID, int targetID, int damage, int dmgTable)
    {
    	if(dmgTable >= BASE_DMG && dmgTable <= ALT_DMGb)
    	{
    		allTables[dmgTable][attackerID][targetID] = damage;
    	}
    }
    
    /** <code><i>SETDAMAGE_ATKRARRAY</i></code>
     * <p>
     * Sets the damage values for an attacking unit.
     * 
     * @param attackerID
     * @param damage
     * @param dmgTable
     */
    public static void setDamage_atkrArray(int attackerID, int[] damage, int dmgTable)
    {
    	if(dmgTable >= BASE_DMG && dmgTable <= ALT_DMGb)
    	{
    		for(int targetID = 0; targetID < NUM_UNITS; targetID++)
    		{
    			allTables[dmgTable][attackerID][targetID] = damage[targetID];
    		}
    	}
    }
    
    /**<code><i>SETDAMAGE_TARGARRAY</i></code>
     * <p>
     * Sets the damage values for a target unit.
     * 
     * @param targetID
     * @param damage
     * @param dmgTable
     */
    public static void setDamage_targArray(int targetID, int[] damage, int dmgTable)
    {
    	if(dmgTable >= BASE_DMG && dmgTable <= ALT_DMGb)
    	{
    		for(int attackerID = 0; attackerID < NUM_UNITS; attackerID++)
    		{
    			allTables[dmgTable][attackerID][targetID] = damage[attackerID];
    		}
    	}
    }
    
    
    //reads the table from BaseDMG.txt and AltDMG.txt in the program's working directory
    public static void loadBaseDamage(){
        try{
            String baseDMGLocation = ResourceLoader.properties.getProperty("BaseDMGLocation");
            String altDMGLocation = ResourceLoader.properties.getProperty("AltDMGLocation");
            
			BufferedReader reader = new BufferedReader(new FileReader(baseDMGLocation));
            int r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)baseDMG[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(altDMGLocation));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)altDMG[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(baseDMGLocation));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)baseDMGb[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(altDMGLocation));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)altDMGb[r][c] = nums[c];
                    r++;
                }
            }
        }catch(Exception e){
        	logger.error("error:", e);
        }
    }
    
    
    //reads the table from alternate text files in the program's working directory
    public static void loadAlternateTables(String baseNormal, String altNormal, String baseBal, String altBal)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(baseNormal));
            int r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)baseDMG[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(altNormal));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)altDMG[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(baseBal));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)baseDMGb[r][c] = nums[c];
                    r++;
                }
            }
            
            reader = new BufferedReader(new FileReader(altBal));
            r = 0;
            
            //read file
            while(true){
                //read input
                String input = reader.readLine();
                if(input == null)break;
                
                //get ints
                int[] nums = extractInts(input);
                
                //insert ints if applicable
                if(nums[0]!=-10){
                    for(int c=0; c < NUM_UNITS; c++)altDMGb[r][c] = nums[c];
                    r++;
                }
            }
        }catch(Exception e){
            logger.error("error=", e);
        }
    }
    
    //reads the table from BaseDMG.txt and AltDMG.txt in the program's working directory
/*    public static void loadBalanceBaseDamage(){
        try{
 
        }catch(Exception e){

        }
    }*/
    
    //finds the appropriate value on the table, used by Unit.DamageCalc()
    public static int find(int ammo, int r, int c, boolean balance){
        if(balance){
            if(ammo > 0)
                if(baseDMGb[r][c]!=-1)
                    return baseDMGb[r][c];
            return altDMGb[r][c];
        }else{
            if(ammo > 0)
                if(baseDMG[r][c]!=-1)
                    return baseDMG[r][c];
            return altDMG[r][c];
        }
    }
    
    public static int find(Unit u, Unit u2, boolean balance){
        int ammo = u.getAmmo();
        if(balance){
            if(ammo > 0)
                if(baseDMGb[u.getUType()][u2.getUType()]!=-1)
                    return baseDMGb[u.getUType()][u2.getUType()];
            return altDMGb[u.getUType()][u2.getUType()];
        }else{
            if(ammo > 0)
                if(baseDMG[u.getUType()][u2.getUType()]!=-1)
                    return baseDMG[u.getUType()][u2.getUType()];
            return altDMG[u.getUType()][u2.getUType()];
        }
    }
    
    //finds the base damage, regardless of ammunition
    public static int findBase(int r, int c, boolean balance){
        if(balance)return baseDMGb[r][c];
        return baseDMG[r][c];
    }
    
    public static int findBase(Unit u, Unit u2, boolean balance){
        if(u != null && u2 != null){
            if(balance)return baseDMGb[u.getUType()][u2.getUType()];
            return baseDMG[u.getUType()][u2.getUType()];
        }
        return -3;
    }
    
    //finds the secondary base damage, regardless of ammunition
    public static int findAlt(int r, int c, boolean balance){
        if(balance)return altDMGb[r][c];
        return altDMG[r][c];
    }
    
    //utility routine, takes a string and extracts all of the valid integers from it
    private static int[] extractInts(String input){
        int[] output = new int[NUM_UNITS];    //holds the ints
        int j = 0;                      //holds the index of the current number
        boolean foundNum = false;       //has an integer been found yet?
        int foundAt = 0;                //where was the number first found?
        
        //used to mark if no ints found
        output[0] = -10;
        
        for(int i=0; i < input.length();i++){
            char c = input.charAt(i);
            if(c >= '0' && c <='9'){
                if(!foundNum){
                    foundNum = true;
                    foundAt = i;
                }
            }else if(c == '-' && !foundNum){
                foundNum = true;
                foundAt = i;
            }else if(foundNum){
                String sub = input.substring(foundAt,i--);
                output[j] = Integer.parseInt(sub);
                j++;
                
                foundNum = false;
            }
        }
        
        if(foundNum){
            String sub = input.substring(foundAt,input.length());
            output[j] = Integer.parseInt(sub);
            j++;
            
            foundNum = false;
        }
        
        return output;
    }
}