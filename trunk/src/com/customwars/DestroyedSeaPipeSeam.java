package com.customwars;
/*
 *DestroyedPipeSeam.java
 *Author: Urusan
 *Contributors:
 *Creation: August 13, 2006, 8:34 AM
 *The DestroyedPipeSeam Plain class is used to create an instance of the Destroyed Pipe Seam Terrain.
 */

public class DestroyedSeaPipeSeam extends Terrain
{    
    //constructor
    public DestroyedSeaPipeSeam()
    {
        //Statistics
        name = "Sea";
        setMove(new double[] {-1,-1,-1,-1,1,1,1,-1,-1,1});
        basemove = new double[] {-1,-1,-1,-1,1,1,1,-1,-1,1};
        def  = 0;
        index = TerrType.DEST_SPS;
    }
}
