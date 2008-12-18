package com.customwars;
/*
 *Reef.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Reef class is used to create an instance of the Reef Terrain.
 */

public class Reef extends Terrain{
   
    //constructor
    public Reef(){
        //Statistics
        name = "Reef";
        setMove(new double[] {-1,-1,-1,-1,1,2,2,-1,-1,1});
        basemove = new double[] {-1,-1,-1,-1,1,2,2,-1,-1,1};
        def  = 1;
        index = 7;
    }
} 