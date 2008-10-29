package cwsource;
/*
 *Path.java
 *Author: Urusan
 *Contributors:
 *Creation: August 16, 2006, 8:50 AM
 *Holds path data. Used to correctly calculate fuel usage and check for ambushes in FoW
 */

import java.io.*;

class PathLink implements Serializable{
    public byte direction;   //Holds the link's direction 0=north 1=east 2=south 3=west
    public PathLink next;   //Holds the next link
    
    //constructor
    public PathLink(int u){
        direction = (byte)u;
    }
    
    //returns the Link's data
    public int getDirection(){
        return direction;
    }
    
    //converts the data to a string for debugging
    public String toString(){
        return "" + direction;
    }
}

public class Path implements Serializable{
    
    public PathLink first;  //pointer to the first link
    public PathLink last;   //pointer to the last link
    private int sx;         //the starting x position
    private int sy;         //the starting y position
    
    //constructor
    public Path(int x, int y){
        first = null;
        last = null;
        sx = x;
        sy = y;
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return first==null;
    }
    
    //Reset the path
    public void resetPath(){
        first=null;
        last=null;
    }
    
    //recalculate the path to lead to the following coordinates
    public void reCalculatePath(int nx, int ny, Unit u){
        //is it an Oozium
        if(u.getName().equals("Oozium")){
            resetPath();
            int x=u.getLocation().getCol();
            int y=u.getLocation().getRow();
            if(ny == y-1)insertFirst(0);
            if(nx == x+1)insertFirst(1);
            if(ny == y+1)insertFirst(2);
            if(nx == x-1)insertFirst(3);
            return;
        }
        
        //Normal Units
        boolean done = false;   //flags when done
        //reset the path
        resetPath();
        //get unit info
        int y = u.getLocation().getRow();
        int x = u.getLocation().getCol();
        int mp = u.getMove();
        int gas = u.getGas();
        int tind = u.getMap().find(new Location(x,y)).getTerrain().getIndex();
        //d = direction, 0=north, 1=east, 2=south, 3=west, this sends probes in all 4 directions
        done = pathCalculate(x,y-1,0,mp,false,false,u.getMap(),u,gas,nx,ny,tind);
        if(!done)done = pathCalculate(x+1,y,1,mp,false,false,u.getMap(),u,gas,nx,ny,tind);
        if(!done)done = pathCalculate(x,y+1,2,mp,false,false,u.getMap(),u,gas,nx,ny,tind);
        if(!done)done = pathCalculate(x-1,y,3,mp,false,false,u.getMap(),u,gas,nx,ny,tind);
    }
    
    //recursive helper for reCalculatePath
    private boolean pathCalculate(int x, int y, int d, int mp, boolean left, boolean right, Map map, Unit u, int gas, int nx, int ny, int ptindex){
        //are the x,y coords on the map?
        if(map.onMap(x,y)){
            double moveCost = map.find(new Location(x,y)).getTerrain().moveCost(u.getMType()); //temp, stores move cost
            
            int ctindex = map.find(new Location(x,y)).getTerrain().getIndex();
            //Hovercraft exception, cannot move straight from sea to land
            if(u.getMType()==u.MOVE_HOVER){
                if(ptindex == 6 || ptindex == 7){
                    //moving from water
                    //only allow movement onto water, shoals, or ports
                    if(!((ctindex >= 5 && ctindex <= 8) || ctindex == 13))return false;
                }else if(ctindex == 6 || ctindex == 7){
                    //moving into water
                    //only allow movement from water, shoals, or ports
                    if(!((ptindex >= 5 && ptindex <= 8) || ptindex == 13))return false;
                }
            }
            
            //if not impassible, subtract move cost from mp
            if(moveCost != -1){
                //deal with perfect movement
                if(u.getArmy().getCO().hasPerfectMovement()|| u.perfectMovement)moveCost = 1;
                mp -= moveCost;
                gas-= moveCost;
            }else{
                //impassible
                return false;
            }
            //not enough mp to move onto this title?
            if(mp < 0)return false;
            //not enough gas to move onto this title?
            if(gas < 0)return false;
            //is an enemy unit in the way? (also checks if it the unit is attempting to move onto itself)
            /*
            if((map.find(new Location(x,y)).getUnit() == null) || 
               ((map.find(new Location(x,y)).getUnit() != u) &&
               (map.find(new Location(x,y)).getUnit().getArmy().getSide() == u.getArmy().getSide())) ||
               map.find(new Location(x,y)).getUnit().isDived() ||
               (map.find(new Location(x,y)).getUnit().isHidden() && 
               !map.find(new Location(x,y)).getUnit().getArmy().getBattle().isMist()))
               */
            // [CHANGE]
            if(pathableAtXY(x, y, map, u))
            {
                //move is valid, is this what we're looking for?
                if(x == nx && y == ny){
                    //check if shortest path
                    if(mp == u.getMoveRange().checkMPLeft(x,y))
                    {
                        insertFirst(d);
                        return true;
                    }
                }
                //if not, keep searching
                if(u.getMoveRange().checkMPLeft(x,y)==-1 || u.getMoveRange().checkMPLeft(x,y)<=mp){
                    //if there is 0 mp left, don't check any further
                    if(mp == 0)return false;
                    //send out probes to the left, right, and straight ahead
                    for(int i=-1;i<2;i++){
                        boolean done = false;
                        //don't go left or right twice in a row, it's worthless (unless you're a hovercraft <_<)
                        if((!((left == true && i == -1)||(right == true && i == 1)))||u.getMType()==u.MOVE_HOVER){
                            //find correct direction and send recursive probes
                            switch((d+i)%4){
                                case -1:
                                case 3:
                                    done = pathCalculate(x-1,y,3,mp,i==-1,i==1,map,u,gas,nx,ny,ctindex);
                                    break;
                                case 0:
                                    done = pathCalculate(x,y-1,0,mp,i==-1,i==1,map,u,gas,nx,ny,ctindex);
                                    break;
                                case 1:
                                    done = pathCalculate(x+1,y,1,mp,i==-1,i==1,map,u,gas,nx,ny,ctindex);
                                    break;
                                case 2:
                                    done = pathCalculate(x,y+1,2,mp,i==-1,i==1,map,u,gas,nx,ny,ctindex);
                                    break;
                            }
                            if(done){
                                insertFirst(d);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    //check a given path to see if it's legal
    public boolean isLegal(Unit u){
        if(u.getName().equals("Oozium"))
            return true;
        int[] pathArray = getItems();   //get the path directions in an array
        //get unit info
        int y = u.getLocation().getRow();
        int x = u.getLocation().getCol();
        int mp = u.getMove();
        int gas = u.getGas();
        Map map = u.getMap();
        int ptindex = u.getMap().find(new Location(x,y)).getTerrain().getIndex();
        //check the path
        for(int i = 0; i < pathArray.length; i++){
            //apply direction
            if(pathArray[i]==0)y--;
            else if(pathArray[i]==1)x++;
            else if(pathArray[i]==2)y++;
            else if(pathArray[i]==3)x--;
            //are the x,y coords on the map?
            if(map.onMap(x,y)){
                double moveCost = map.find(new Location(x,y)).getTerrain().moveCost(u.getMType()); //temp, stores move cost
                int ctindex = map.find(new Location(x,y)).getTerrain().getIndex();
                //Hovercraft exception, cannot move straight from sea to land
                if(u.getMType()==u.MOVE_HOVER){
                    if(ptindex == 6 || ptindex == 7){
                        //moving from water
                        //only allow movement onto water, shoals, or ports
                        if(!((ctindex >= 5 && ctindex <= 8) || ctindex == 13))return false;
                    }else if(ctindex == 6 || ctindex == 7){
                        //moving into water
                        //only allow movement from water, shoals, or ports
                        if(!((ptindex >= 5 && ptindex <= 8) || ptindex == 13))return false;
                    }
                }
                ptindex = ctindex;
                //if not impassible, subtract move cost from mp
                if(moveCost != -1){
                    //deal with perfect movement
                    if(u.getArmy().getCO().hasPerfectMovement()|| u.perfectMovement)moveCost = 1;
                    mp -= moveCost;
                    gas-= moveCost;
                }else{
                    //impassible
                    return false;
                }
                //not enough mp to move onto this title?
                if(mp < 0)return false;
                //not enough gas to move onto this title?
                if(gas < 0)return false;
                //is an enemy unit in the way? (also checks if it the unit is attempting to move onto itself)
                if((map.find(new Location(x,y)).getUnit() == null) || ((map.find(new Location(x,y)).getUnit() != u)&&(map.find(new Location(x,y)).getUnit().getArmy().getSide() == u.getArmy().getSide()))||(map.find(new Location(x,y)).getUnit().isHidden())){
                    //move is valid
                    //nothing here...
                }else{
                    //move not valid
                    return false;
                }
            }
        }
        //no problems, thus the path is fine
        return true;
    }
    
    //check a given path to see what the fuel usage is...assumes a legal path
    public int getFuelUsage(Unit u){
        if(u.getName().equals("Oozium")){
            if(this.isEmpty())return 0;
            return 1;
        }
        int[] pathArray = getItems();   //get the path directions in an array
        //get unit info
        int y = u.getLocation().getRow();
        int x = u.getLocation().getCol();
        Map map = u.getMap();
        int gasUsage = 0;
        
        //follow the path
        for(int i = 0; i < pathArray.length; i++){
            //apply direction
            if(pathArray[i]==0)y--;
            else if(pathArray[i]==1)x++;
            else if(pathArray[i]==2)y++;
            else if(pathArray[i]==3)x--;
            //are the x,y coords on the map?
            if(map.onMap(x,y)){
                double moveCost = map.find(new Location(x,y)).getTerrain().moveCost(u.getMType()); //temp, stores move cost
                //if not impassible, add move cost to moveCost
                if(moveCost != -1){
                    //deal with perfect movement
                    if(u.getArmy().getCO().hasPerfectMovement()|| u.perfectMovement)moveCost = 1;
                    gasUsage += moveCost;
                }else{
                    //impassible
                    //return 1000;
                }
            }
        }
        //no problems, thus the path is fine
        return gasUsage;
    }
    
    //truncate a circular path
    public void truncatePath(int x, int y){
        int cx = sx;
        int cy = sy;
        
        if(isEmpty())return;
        PathLink current = first;
        if(sx == x && sy == y)resetPath();
        
        while(current!=null){
            switch(current.direction){
                case 0:
                    cy--;
                    break;
                case 1:
                    cx++;
                    break;
                case 2:
                    cy++;
                    break;
                case 3:
                    cx--;
                    break;
            }
            
            if(cx == x && cy == y){
                current.next = null;
                last = current;
                return;
            }
            
            current = current.next;
        }
    }
    
    //returns the end coordinates of the path
    public Location findEndCoordinates(){
        int x = sx;
        int y = sy;
        
        PathLink temp = first;
        while(temp!=null){
            switch(temp.getDirection()){
                case 0:
                    y--;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    y++;
                    break;
                case 3:
                    x--;
                    break;
            }
            temp = temp.next;
        }
        
        return new Location(x,y);
    }
    
    //returns the last direction
    public int getLast(){
        return last.getDirection();
    }
    
    //deletes the last direction
    public void deleteLast(){
        deleteItem(last);
    }
    
    //insert the an object at the end of the list
    public void insertLast(int o){
        PathLink newLink = new PathLink(o);
        int originalSize = getLength();
        if(isEmpty()){
            first = newLink;
        }else{
            last.next = newLink;
        }
        last = newLink;
        
        //if the list is broken for some reason, repair it
        if(originalSize == getLength()){
            PathLink current = first;
            while(current.next!=null){
                current = current.next;
            }
            current.next = newLink;
        }
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(int o){
        PathLink newLink = new PathLink(o);
        int originalSize = getLength();
        
        if(isEmpty())
            last = newLink;
        newLink.next = first;
        first = newLink;
        
        //if the list is broken for some reason, repair it
        if(originalSize == getLength()){
            PathLink current = first;
            while(current.next!=null){
                current = current.next;
            }
            current.next = newLink;
        }
    }
    
    //delete the first link, returns false if the item was not deleted
    public boolean deleteFirst(){
        if(isEmpty())return false;
        if(first.next == null)
            last = null;
        first = first.next;
        return true;
    }
    
    //deletes a given object from the list
    public boolean deleteItem(PathLink o){
        if(isEmpty())return false;
        PathLink current = first;
        PathLink previous = null;
        
        while(current!=null){
            if(current == o){
                if(previous == null){
                    first = current.next;
                }else{
                    previous.next = current.next;
                }
                if(current.next == null){
                    last = previous;
                }
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }
    
    //deletes the first equivalent object from the list
    public boolean deleteEqualItem(int o){
        if(isEmpty())return false;
        PathLink current = first;
        PathLink previous = null;
        
        while(current!=null){
            if(current.direction == o){
                if(previous == null){
                    first = current.next;
                }else{
                    previous.next = current.next;
                }
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }
    
    //gets the length of the path
    public int getLength(){
        PathLink current = first;
        int i = 0;
        
        while(current!=null){
            i++;
            current = current.next;
        }
        return i;
    }
    
    //returns an array with all the objects in the list
    public int[] getItems(){
        if(isEmpty())return new int[getLength()];
        
        int[] o = new int[getLength()];
        PathLink current = first;
        int i = 0;
        
        while(current!=null){
            o[i]=current.direction;
            i++;
            current = current.next;
        }
        return o;
    }
    
    //converts the list to a string for debugging
    public String toString(){
        PathLink current = first;
        String s = "";
        while(current!=null){
            s += current;
            s += " ";
            current = current.next;
        }
        
        return s;
    }
    
    //[NEW]
    public boolean pathableAtXY(int x, int y, Map m, Unit movingUnit)
    {                   	  
    	//If there is no unit at the given location, then it is open
    	if(m.find(new Location(x,y)).getUnit() != null)
    	{
    		Unit thisUnit = m.find(new Location(x,y)).getUnit();
    		
    		//If the current analyzed unit is allied to the current player, the square
    		//is open.
    		if(thisUnit.getArmy().getSide() == movingUnit.getArmy().getSide())
    		{
    			return true;
    		}
    		//Otherwise, more conditions need to be checked
    		else
    		{
    			//Check FoW based conditions
				//Check if the current unit is 'hidden'
				//If it is not hidden, the square is open
    			if(movingUnit.getArmy().getBattle().isFog() && !thisUnit.isHidden())
    			{
    				return false;
    			}
    			//Check for MoW based conditions
				//Check if the current unit is 'dived' or if the unit has been 
    			//detected. If it is not dived, or it has been detected, it is
    			//thus occupying the square and it is not open
    			else if(movingUnit.getArmy().getBattle().isMist() && (!thisUnit.isDived() || thisUnit.detected))
    			{
    				return false;
    			}
    			//Check for clear conditions
				//Check if the current unit is 'hidden'
				//If it is not hidden, its square is closed
    			else if(!thisUnit.isHidden())
    			{
    				return false;
    			}	    		
    		}
    	}
    	
    	return true;
    }
}
