package com.customwars.map.location;


public class Shoal extends Terrain{
   
    //constructor
    public Shoal(){
        //Statistics
        name = "Shoal";
        setMove(new double[] {1,1,1,1,1,-1,1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,-1,1,1,-1,1};
        def  = 0;
        index = 8;
    }
}