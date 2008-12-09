package com.customwars;
/*
 *Plain.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 24, 2006, 11:06 PM
 *The Plain class is used to create an instance of the Plain Terrain.
 */

public class Plain extends Terrain{
    
    //constructor
    public Plain(){
        //Statistics
        name = "Plain";
        move = new double[] {1,1,1,2,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,2,1,-1,-1,1,-1,1};
        def  = 1;
        index = 0;
    }
}