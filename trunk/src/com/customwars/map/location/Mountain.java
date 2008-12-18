package com.customwars.map.location;
/*
 *Mountain.java
 *Author: Killian Hanlon
 *Contributors:
 *Creation: 17/7/06
 *The Mountain class is used to create an instance of the Mountain Terrain.
 */

public class Mountain extends Terrain{
   
    //constructor
    public Mountain(){
        //Statistics
        name = "Mountain";
        setMove(new double[] {2,1,-1,-1,1,-1,-1,1,-1,-1});
        basemove = new double[] {2,1,-1,-1,1,-1,-1,1,-1,-1};;
        def  = 4;
        index = 2;
    }
}