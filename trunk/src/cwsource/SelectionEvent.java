package cwsource;
/*FCUS
 *BuildEvent.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:46 AM
 *Contains information about unit construction
 */

import java.io.*;

public class SelectionEvent extends CWEvent implements Serializable{
    private int x;
    private int y;
    
    //constructor
    public SelectionEvent(int xc, int yc, int d, int tr){
        super(9,d,tr);
        x = xc;
        y = yc;
    }
    
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public String toString(){
        return "Type: " + type + " Day: " + day + " Turn: " + turn + " X: " + x + " Y: " + y;
    }
}
