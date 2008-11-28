package cwsource;
/*
 *DestroyedPipeSeam.java
 *Author: Urusan
 *Contributors:
 *Creation: August 13, 2006, 8:34 AM
 *The DestroyedPipeSeam Plain class is used to create an instance of the Destroyed Pipe Seam Terrain.
 */

public class DestroyedWall extends Terrain{
    
    //constructor
    public DestroyedWall(){
        //Statistics
        name = "Destroyed Wall";
        move = new double[] {1,1,1,2,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,2,1,-1,-1,1,-1,1};
        def  = 1;
        index = 22;
    }
}
