package com.customwars;


public class Shoal extends Terrain{
   
    //constructor
    public Shoal(){
        //Statistics
        name = "Shoal";
        move = new double[] {1,1,1,1,1,-1,1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,1,1,-1,1};
        def  = 0;
        index = 8;
    }
}