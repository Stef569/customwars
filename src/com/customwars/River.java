package com.customwars;
/*
 *River.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The River class is used to create an instance of the River Terrain.
 */

public class River extends Terrain{
   
    //constructor
    public River(){
        //Statistics
        name = "River";
        setMove(new double[] {2,1,-1,-1,1,-1,-1,1,-1,1});
        basemove = new double[] {2,1,-1,-1,1,-1,-1,1,-1,1};
        def  = 0;
        index = 5;
    }
}