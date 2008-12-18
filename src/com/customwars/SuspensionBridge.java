package com.customwars;
/*
 *Road.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The Road class is used to create an instance of the Road Terrain.
 */

public class SuspensionBridge extends Terrain{
   
    //constructor
    public SuspensionBridge(){
        //Statistics
        name = "Bridge";
        setMove(new double[] {1,1,1,1,1,1,1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,1,1,1,-1,1};
        def  = 0;
        index = 20;
    }
}