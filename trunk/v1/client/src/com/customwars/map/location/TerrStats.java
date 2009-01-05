package com.customwars.map.location;

public final class TerrStats 
{
	public static final int MAX_TERRAIN_STATS = 4; 
	
	public static final int TERR_TYPE = 0;
	public static final int HP = 1; //Only used for certain terrain types...
	public static final int STYLE = 2;
	public static final int DEF_STARS = 3;
	
	public static final int MAX_PROPERTY_STATS = 8;
	
	public static final int CAPT_LIMIT = 5; //0 = cannot be captured
	public static final int VISION = 6; //-1 = can't see!, 0 = see itself, etc...
	public static final int FUNDS = 7;
}
