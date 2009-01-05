package com.customwars.map.location;
/*
 *Pipe.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The Pipe class is used to create an instance of the Pipe Terrain.
 */

public class SeaPipe extends Terrain
{   
    //constructor
    public SeaPipe()
    {
        //Statistics
        name = "Sea Pipe";
        setMove(new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1});
        basemove = new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1};;
        def  = 0;
        index = TerrType.SEA_PIPE;
    }
}