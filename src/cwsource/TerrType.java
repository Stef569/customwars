package cwsource;

import java.awt.Image;

public final class TerrType 
{
	//MAX TERRAIN COUNT
	public static final int MAX_TERRAIN_TYPES = 26;
	
	public static final int NOT_A_TERRAIN = -1;
	
	//BASIC TERRAIN
	public static final int PLAINS = 0;
	public static final int WOOD = 1;
	public static final int MOUNTAIN = 2;
	public static final int ROAD = 3;
	public static final int BRIDGE = 4;
	public static final int RIVER = 5;
	public static final int SEA = 6;
	public static final int REEF = 7;
	public static final int SHOAL = 8;

	//PROPERTIES
	public static final int HQ = 9;
	public static final int CITY = 10;
	public static final int BASE = 11;
	public static final int AIRPORT = 12;
	public static final int PORT = 13;
	public static final int COM_TOWER = 14;
	public static final int PIPE_STATION = 17;
	
	//EVERYTHING ELSE
	public static final int PIPE = 15;
	public static final int SILO = 16;
	public static final int PIPE_SEAM = 18;
	public static final int DEST_SEAM = 19;	
	public static final int SUS_BRIDGE = 20;
	public static final int WALL = 21;
	public static final int DEST_WALL = 22;
	
	public static final int SEA_PIPE = 23;
	public static final int SP_SEAM = 24;
	public static final int DEST_SPS = 25;	
	
	public static int getSearchIndex(Tile currTile, Battle b)
	{
		if(currTile != null && b != null)
		{
	        Terrain ter = currTile.getTerrain();
	        
			if(ter instanceof Property) 
		    {
		        if(b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow()) && ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ))
		        {
		        	//spriteX1 = (ter.getIndex()-7)*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return (ter.getIndex()-7)*16;
		        } 
		        else if(ter instanceof Pipestation) 
		        {
		        	//spriteX1 = 8*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return 8*16;
		        } 
		        else if(ter instanceof HQ)
		        {
		        	//spriteX1 = (((Property)ter).owner.getCO().getStyle())*16;
		        	//spriteX2 = (((Property)ter).owner.getCO().getStyle()+1)*16;
		        	return (((Property)ter).owner.getCO().getStyle())*16;
		        } 
		        else if (ter instanceof Silo)
		        {
		        	//Launched Silo
		            if(((Silo)ter).launched)
		            {
		            	//spriteX1 = 1*16;
		            	//spriteX2 = 2*16;
		            	return 16;
		            }
		            //Loaded Silo
		            else
		            {	
		            	//spriteX1 = 0*16;
		            	//spriteX2 = 1*16;
		            	return 0;
		            }
		        }
		        else
		        {
		        	//spriteX1 = (ter.getIndex()-7)*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return (ter.getIndex()-7)*16;
		        }
		    } 
		    else 
		    {
		        if(ter.index<3) 
		        { 
		        	//Plain, Wood, Mountain
		        	//spriteX1 = (ter.getIndex())*16;
		        	//spriteX2 = (ter.getIndex()+1)*16;
		        	return (ter.getIndex())*16;
		        } 
		        else if(ter.index == 3)
		        { 
		        	//Road
		        	//spriteX1 = (ter.getStyle()+3)*16;
		        	//spriteX2 = (ter.getStyle()+4)*16;
		        	return (ter.getStyle()+3)*16;
		        } 
		        else if(ter.index == 4)
		        { 
		        	//Bridge
		        	//spriteX1 = (ter.getStyle()+14)*16;
		        	//spriteX2 = (ter.getStyle()+15)*16;
		        	return (ter.getStyle()+14)*16;
		        }
		        else if(ter.index == 20)
		        {
		            //Put suspension code here
		        	//spriteX1 = (ter.getStyle()+16)*16;
		        	//spriteX2 = (ter.getStyle()+17)*16;
		        	return (ter.getStyle()+16)*16;
		        }
		        else if(ter.index == 5)
		        { 
		        	//River
		        	//spriteX1 = (ter.getStyle()+18)*16;
		        	//spriteX2 = (ter.getStyle()+19)*16;
		        	return (ter.getStyle()+18)*16;
		        } 
		        else if (ter.index == 8)
		        { 
		        	//Shoal
		        	//spriteX1 = (ter.getStyle()+33)*16;
		        	//spriteX2 = (ter.getStyle()+34)*16;
		        	return (ter.getStyle()+33)*16;
		        } 
		        else if (ter.index == 6)
		        { 
		        	//Sea
		        	//spriteX1 = (ter.getStyle()+72)*16;
		        	//spriteX2 = (ter.getStyle()+73)*16;
		        	return (ter.getStyle()+72)*16;
		        } 
		        else if (ter.index == 7)
		        { 
		        	//Reef
		        	//spriteX1 = 71*16;
		        	//spriteX2 = 72*16;
		        	return 71*16;
		        } 
		        else if (ter.index == 15)
		        { 
		        	//Pipe
		        	//spriteX1 = (ter.getStyle()+103)*16;
		        	//spriteX2 = (ter.getStyle()+104)*16;
		        	return (ter.getStyle()+103)*16;
		        } 
		        else if (ter.index == 18)
		        { 
		        	//PipeSeam
		        	//spriteX1 = (ter.getStyle()+118)*16;
		        	//spriteX2 = (ter.getStyle()+119)*16;
		        	return (ter.getStyle()+118)*16;
		        } 
		        else if (ter.index == 19)
		        { 
		        	//Destroyed Seam
		        	//spriteX1 = (ter.getStyle()+120)*16;
		        	//spriteX2 = (ter.getStyle()+121)*16;
		        	return (ter.getStyle()+120)*16;
		        } 
		        // ****NEW STUFF****
		        else if (ter.index == TerrType.WALL)
		        { 
		        	//Wall
		        	//spriteX1 = (ter.getStyle()+122)*16;
		        	//spriteX2 = (ter.getStyle()+123)*16;
		        	return (ter.getStyle()+122)*16;
		        } 
		        else if (ter.index == TerrType.DEST_WALL)
		        { 
		        	//Destroyed Wall
		        	//spriteX1 = (ter.getStyle()+137)*16;
		        	//spriteX2 = (ter.getStyle()+138)*16;
		        	return (ter.getStyle()+137)*16;
		        }
		        // ****NEW STUFF****
		        else if (ter.index == TerrType.SEA_PIPE)
		        { 
		        	//Sea Pipe
		        	//spriteX1 = (ter.getStyle()+139)*16;
		        	//spriteX2 = (ter.getStyle()+140)*16;
		        	return (ter.getStyle()+139)*16;
		        } 
		        else if (ter.index == TerrType.SP_SEAM)
		        { 
		        	//Sea Pipe Seam
		        	//spriteX1 = (ter.getStyle()+154)*16;
		        	//spriteX2 = (ter.getStyle()+155)*16;
		        	return (ter.getStyle()+154)*16;
		        } 
		        else if (ter.index == TerrType.DEST_SPS)
		        { 
		        	//Destroyed Sea Pipe Seam
		        	//spriteX1 = (ter.getStyle()+156)*16;
		        	//spriteX2 = (ter.getStyle()+157)*16;
		        	return (ter.getStyle()+156)*16;
		        }
	    	}
	    }
		
		return NOT_A_TERRAIN;
	}
	
	public static int getSearchIndex(Terrain ter)
	{
		if(ter != null)
		{
			if(ter instanceof Property) 
		    {
		        if(ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ))
		        {
		        	//spriteX1 = (ter.getIndex()-7)*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return (ter.getIndex()-7)*16;
		        } 
		        else if(ter instanceof Pipestation) 
		        {
		        	//spriteX1 = 8*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return 8*16;
		        } 
		        else if(ter instanceof HQ)
		        {
		        	//spriteX1 = (((Property)ter).owner.getCO().getStyle())*16;
		        	//spriteX2 = (((Property)ter).owner.getCO().getStyle()+1)*16;
		        	return (((Property)ter).owner.getCO().getStyle())*16;
		        } 
		        else if (ter instanceof Silo)
		        {
		        	//Launched Silo
		            if(((Silo)ter).launched)
		            {
		            	//spriteX1 = 1*16;
		            	//spriteX2 = 2*16;
		            	return 16;
		            }
		            //Loaded Silo
		            else
		            {	
		            	//spriteX1 = 0*16;
		            	//spriteX2 = 1*16;
		            	return 0;
		            }
		        }
		        else
		        {
		        	//spriteX1 = (ter.getIndex()-7)*16;
		        	//spriteX2 = (ter.getIndex()-6)*16;
		        	return (ter.getIndex()-7)*16;
		        }
		    } 
		    else 
		    {
		        if(ter.index<3) 
		        { 
		        	//Plain, Wood, Mountain
		        	//spriteX1 = (ter.getIndex())*16;
		        	//spriteX2 = (ter.getIndex()+1)*16;
		        	return (ter.getIndex())*16;
		        } 
		        else if(ter.index == 3)
		        { 
		        	//Road
		        	//spriteX1 = (ter.getStyle()+3)*16;
		        	//spriteX2 = (ter.getStyle()+4)*16;
		        	return (ter.getStyle()+3)*16;
		        } 
		        else if(ter.index == 4)
		        { 
		        	//Bridge
		        	//spriteX1 = (ter.getStyle()+14)*16;
		        	//spriteX2 = (ter.getStyle()+15)*16;
		        	return (ter.getStyle()+14)*16;
		        }
		        else if(ter.index == 20)
		        {
		            //Put suspension code here
		        	//spriteX1 = (ter.getStyle()+16)*16;
		        	//spriteX2 = (ter.getStyle()+17)*16;
		        	return (ter.getStyle()+16)*16;
		        }
		        else if(ter.index == 5)
		        { 
		        	//River
		        	//spriteX1 = (ter.getStyle()+18)*16;
		        	//spriteX2 = (ter.getStyle()+19)*16;
		        	return (ter.getStyle()+18)*16;
		        } 
		        else if (ter.index == 8)
		        { 
		        	//Shoal
		        	//spriteX1 = (ter.getStyle()+33)*16;
		        	//spriteX2 = (ter.getStyle()+34)*16;
		        	return (ter.getStyle()+33)*16;
		        } 
		        else if (ter.index == 6)
		        { 
		        	//Sea
		        	//spriteX1 = (ter.getStyle()+72)*16;
		        	//spriteX2 = (ter.getStyle()+73)*16;
		        	return (ter.getStyle()+72)*16;
		        } 
		        else if (ter.index == 7)
		        { 
		        	//Reef
		        	//spriteX1 = 71*16;
		        	//spriteX2 = 72*16;
		        	return 71*16;
		        } 
		        else if (ter.index == 15)
		        { 
		        	//Pipe
		        	//spriteX1 = (ter.getStyle()+103)*16;
		        	//spriteX2 = (ter.getStyle()+104)*16;
		        	return (ter.getStyle()+103)*16;
		        } 
		        else if (ter.index == 18)
		        { 
		        	//PipeSeam
		        	//spriteX1 = (ter.getStyle()+118)*16;
		        	//spriteX2 = (ter.getStyle()+119)*16;
		        	return (ter.getStyle()+118)*16;
		        } 
		        else if (ter.index == 19)
		        { 
		        	//Destroyed Seam
		        	//spriteX1 = (ter.getStyle()+120)*16;
		        	//spriteX2 = (ter.getStyle()+121)*16;
		        	return (ter.getStyle()+120)*16;
		        } 
		        // ****NEW STUFF****
		        else if (ter.index == TerrType.WALL)
		        { 
		        	//Wall
		        	//spriteX1 = (ter.getStyle()+122)*16;
		        	//spriteX2 = (ter.getStyle()+123)*16;
		        	return (ter.getStyle()+122)*16;
		        } 
		        else if (ter.index == TerrType.DEST_WALL)
		        { 
		        	//Destroyed Wall
		        	//spriteX1 = (ter.getStyle()+137)*16;
		        	//spriteX2 = (ter.getStyle()+138)*16;
		        	return (ter.getStyle()+137)*16;
		        }
		        // ****NEW STUFF****
		        else if (ter.index == TerrType.SEA_PIPE)
		        { 
		        	//Sea Pipe
		        	//spriteX1 = (ter.getStyle()+139)*16;
		        	//spriteX2 = (ter.getStyle()+140)*16;
		        	return (ter.getStyle()+139)*16;
		        } 
		        else if (ter.index == TerrType.SP_SEAM)
		        { 
		        	//Sea Pipe Seam
		        	//spriteX1 = (ter.getStyle()+154)*16;
		        	//spriteX2 = (ter.getStyle()+155)*16;
		        	return (ter.getStyle()+154)*16;
		        } 
		        else if (ter.index == TerrType.DEST_SPS)
		        { 
		        	//Destroyed Sea Pipe Seam
		        	//spriteX1 = (ter.getStyle()+156)*16;
		        	//spriteX2 = (ter.getStyle()+157)*16;
		        	return (ter.getStyle()+156)*16;
		        }
	    	}
	    }
		
		return NOT_A_TERRAIN;
	}
	
	public static Image getCorrectSheet(Terrain ter)
	{
		if(ter instanceof Property) 
	    {
			if(ter instanceof HQ)
		        return TerrainGraphics.getHQSpriteSheet(((Property)ter).owner.getColor()+1);
			else if(((Property)ter).owner == null)
	            return TerrainGraphics.getUrbanSpriteSheet(0);
	        else
	            return TerrainGraphics.getUrbanSpriteSheet(((Property)ter).owner.getColor()+1);
	    } 
	    else 
	    {
	        return TerrainGraphics.getTerrainSpriteSheet();
	    }
	}
}
