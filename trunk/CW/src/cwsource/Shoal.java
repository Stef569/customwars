package cwsource;
/*
 *Shoal.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Shoal class is used to create an instance of the Shoal Terrain.
 */

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