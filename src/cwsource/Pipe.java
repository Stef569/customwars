package cwsource;
/*
 *Pipe.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 17/7/06
 *The Pipe class is used to create an instance of the Pipe Terrain.
 */

public class Pipe extends Terrain{
   
    //constructor
    public Pipe(){
        //Statistics
        name = "Pipe";
        move = new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1};
        basemove = new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1};;
        def  = 0;
        index = 15;
    }
}