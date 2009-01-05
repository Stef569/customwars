package com.customwars.map.location;
/*
 *Sea.java
 *Author: Killian Hanlon
 *Contributors:
 *Creation: 17/7/06
 *The Sea class is used to create an instance of the Sea Terrain.
 */

public class Sea extends Terrain{
   
    //constructor
    public Sea(){
        //Statistics
        name = "Sea";
        setMove(new double[] {-1,-1,-1,-1,1,1,1,-1,-1,1});
        basemove = new double[] {-1,-1,-1,-1,1,1,1,-1,-1,1};
        def  = 0;
        index = 6;
    }
}