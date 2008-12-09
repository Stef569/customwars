package com.customwars;
/*
 *Road.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The Road class is used to create an instance of the Road Terrain.
 */

public class Road extends Terrain{
   
    //constructor
    public Road(){
        //Statistics
        name = "Road";
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 0;
        index = 3;
    }
}