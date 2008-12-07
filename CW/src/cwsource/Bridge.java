package cwsource;
/*
 *Bridge.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The Bridge class is used to create an instance of the Bridge Terrain.
 */

public class Bridge extends Terrain{
   
    //constructor
    public Bridge(){
        //Statistics
        name = "Bridge";
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 0;
        index = 4;
    }
}