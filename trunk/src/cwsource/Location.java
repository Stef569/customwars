package cwsource;
/*
 *Location.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: June 24, 2006, 3:14 PM
 *A location on a 2D grid. Originally from an AP Case Study.
 */

import java.io.*;

public class Location implements Comparable, Serializable{
    
    private int row;    //the location's row
    private int col;    //the location's column
    
    //constructor
    public Location(int colx, int rowx) {
        row = rowx;
        col = colx;
    }
    
    //compares this location to another object.
    //returns -100 if the object is not a location, 0 if equal,
    //o.row-row if different rows, o.col-col if only the columns are different
    public int compareTo(Object o){
        if(o.getClass() != this.getClass())
            return -100;
        Location t = (Location)o;
        if (this.equals(t))
            return 0;
        else if(row != t.getRow()){
            return (t.getRow() - row);
        } else
            return (t.getCol() - col);
    }
    
    //returns true if o is a location with the same row and col, otherwise returns false
    public boolean equals(Object o){
        if(o.getClass() != this.getClass())
            return false;
        Location t = (Location) o;
        if(row == t.getRow() && col == t.getCol())
            return true;
        else
            return false;
    }
    
    //returns row
    public int getRow(){
        return row;
    }
    
    //returns col
    public int getCol(){
        return col;
    }
    
    //converts the location to a string
    public String toString(){
        return ("LOCATION: " + col + " , " + row);
    }
    
    /**
     * This returns a number equal to the direction the piece is in relative to this location. Only works for tiles that only change in one axis. 0 = north, 1 = west, 2 = south, 3 = east
     * @param compare the result to orient against
     */
    public int orient(Location l)
    {
    	//Expect this to be upgraded later
        int dir = 0;
        //same location? -1!
        if(row == l.getRow() && col == l.getCol())
            return -1;
        
        if(row == l.getRow())
        {
            if(col > l.getCol()) //If this is below, otherwise, above
                return 1;
            else
                return 3;
        }
        else if (col == l.getCol())
        {
            if(row > l.getRow()) //If this is below, otherwise, above
                return 0;
            else
                return 2;
        }
        return -1;
    }
}
