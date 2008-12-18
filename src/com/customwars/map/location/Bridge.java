package com.customwars.map.location;


public class Bridge extends Terrain{
   
    //constructor
    public Bridge(){
        //Statistics
        name = "Bridge";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 0;
        index = 4;
    }
}