package com.customwars;
/*
 *DestroyedPipeSeam.java
 *Author: Urusan
 *Contributors:
 *Creation: August 13, 2006, 8:34 AM
 *The DestroyedPipeSeam Plain class is used to create an instance of the Destroyed Pipe Seam Terrain.
 */

public class DestroyedPipeSeam extends Terrain{
    
    //constructor
    public DestroyedPipeSeam(){
        //Statistics
        name = "Plain";
        setMove(new double[] {1,1,1,2,1,-1,-1,1,-1,1});
        basemove = new double[] {1,1,1,2,1,-1,-1,1,-1,1};
        def  = 1;
        index = 19;
    }
}
