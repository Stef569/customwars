package com.customwars;
/*
 *Wood.java
 *Author: Urusan
 *Contributors:
 *Creation: July 2, 2006, 6:30 AM
 *The Wood class is used to create an instance of the Wood Terrain.
 */

public class Wood extends Terrain{
    
    //constructor
    public Wood(){
        //Statistics
        name = "Wood";
        setMove(new double[] {1,1,2,3,1,-1,-1,1,-1,4});
        basemove = new double[] {1,1,2,3,1,-1,-1,1,-1,4};
        def  = 2;
        index = 1;
    }
}
