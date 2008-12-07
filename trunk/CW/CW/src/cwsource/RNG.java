package cwsource;
/*
 *RNG.java
 *Author: Adam Dziuk 
 *Contributors:
 *The RNG class is used to create an RNG, for the purpose of seeding the RNG.
 */

import java.util.Random;
import java.io.*;

public class RNG implements Serializable{
    
    private Random r;
    //private int seed;
    
    /** Creates a new instance of RNG */
    public RNG() {
        r = new Random();
        //seed = r.nextInt();
        //r = new Random(seed);
        
        //System.out.println("SEED = "+seed);
    }
    
    /*public RNG(int i) {
        r = new Random(i);
        //seed = i;
    }*/
    
    public int nextInt(int i){
        return r.nextInt(i);
    }
    
    /*public static int getSeed(){
        return seed;
    }*/
}
