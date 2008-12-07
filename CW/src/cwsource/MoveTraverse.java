package cwsource;
/*
 *MoveTraverse.java
 *Author: Urusan
 *Contributors:
 *Creation: June 30, 2006
 *The MoveTraverse class checks the movement range of a unit in AW, and creates an array that stores the possible moves. This array is used to check and display movement.
 */

import java.io.*;

class MoveTraverse implements Serializable{
    private boolean moveArray[][];  //holds the valid moves
    private int mpLeftArray[][];   //holds the move costs
    
    //constructor
    public MoveTraverse(Unit u){
        //set to proper size
        moveArray = new boolean[u.getMap().getMaxCol()][u.getMap().getMaxRow()];
        mpLeftArray = new int[u.getMap().getMaxCol()][u.getMap().getMaxRow()];
        
        //set moveCost array to -1
        for(int i=0; i<mpLeftArray.length; i++){
            for(int j=0; j<mpLeftArray[0].length; j++){
                mpLeftArray[i][j] = -1;
            }
        }
        
        //start the paths
        u.startPath();
        
        if(u.name.equals("Oozium")){
            int x = u.getLocation().getCol();
            int y = u.getLocation().getRow();
            Map map = u.getMap();
            
            if(x != u.getMap().getMaxCol()-1){
                double moveCost = map.find(new Location(x+1,y)).getTerrain().moveCost(u.getMType());
                if(moveCost!=-1)moveArray[x+1][y] = true;
            }
            if(x != 0){
                double moveCost = map.find(new Location(x-1,y)).getTerrain().moveCost(u.getMType());
                if(moveCost!=-1)moveArray[x-1][y] = true;
            }
            if(y != u.getMap().getMaxRow()-1){
                double moveCost = map.find(new Location(x,y+1)).getTerrain().moveCost(u.getMType());
                if(moveCost!=-1)moveArray[x][y+1] = true;
            }
            if(y != 0){
                double moveCost = map.find(new Location(x,y-1)).getTerrain().moveCost(u.getMType());
                if(moveCost!=-1)moveArray[x][y-1] = true;
            }
        }else{
            //get unit info
            int y = u.getLocation().getRow();
            int x = u.getLocation().getCol();
            int mp = u.getMove();
            int gas = u.getGas();
            int tind = u.getMap().find(new Location(x,y)).getTerrain().getIndex();
            
            //d = direction, 0=north, 1=east, 2=south, 3=west, this sends probes in all 4 directions
            initMoveTraverse(x,y+1,0,mp,false,false,u.getMap(),u,gas,tind);
            initMoveTraverse(x+1,y,1,mp,false,false,u.getMap(),u,gas,tind);
            initMoveTraverse(x,y-1,2,mp,false,false,u.getMap(),u,gas,tind);
            initMoveTraverse(x-1,y,3,mp,false,false,u.getMap(),u,gas,tind);
        }
        //DEBUG
        //System.out.println(this);
    }
    
    //recursive helper for the constructor
    private void initMoveTraverse(int x, int y, int d, int mp, boolean left, boolean right, Map map, Unit u, int gas, int ptindex)
    {
        //are the x,y coords on the map?
        if(map.onMap(x,y))
        {
        	//Changed to also take the player's personal movement costs into account
            //double moveCost = map.find(new Location(x,y)).getTerrain().moveCost(u.getMType()) + u.getArmy().getTerrCost(x, y, u.getMType()); //temp, stores move cost
        	double moveCost = map.find(new Location(x,y)).getTerrain().moveCost(u.getMType()); //temp, stores move cost
            
            //if not impassible, subtract move cost from mp
            if(moveCost != -1)
            {
            	//apply this AFTER the impassable check...
            	moveCost += u.getArmy().getTerrCost(x, y, u.getMType());
            	
                //deal with perfect movement
                if(u.getArmy().getCO().hasPerfectMovement() || u.perfectMovement)moveCost = 1;
                mp -= moveCost;
                gas-= moveCost;
            }
            else
            {
                //impassible
                return;
            }
            
            int ctindex = map.find(new Location(x,y)).getTerrain().getIndex();
            //Hovercraft exception, cannot move straight from sea to land
            if(u.getMType()==u.MOVE_HOVER){
                if(ptindex == 6 || ptindex == 7){
                    //moving from water
                    //only allow movement onto water, shoals, or ports
                    if(!((ctindex >= 5 && ctindex <= 8) || ctindex == 13))return;
                }else if(ctindex == 6 || ctindex == 7){
                    //moving into water
                    //only allow movement from water, shoals, or ports
                    if(!((ptindex >= 5 && ptindex <= 8) || ptindex == 13))return;
                }
            }
            //not enough mp to move onto this title?
            if(mp < 0)return;
            //not enough gas to move onto this title?
            if(gas < 0)return;
            //is an enemy unit in the way? (also checks if it the unit is attempting to move onto itself)
            //A unit in MoW occupies it's square
            //A unit in FoW can block if it is hidden
            /*
            if((map.find(new Location(x,y)).getUnit() == null) || 
               ((map.find(new Location(x,y)).getUnit() != u) && 
               (map.find(new Location(x,y)).getUnit().getArmy().getSide() == u.getArmy().getSide())) ||
               (map.find(new Location(x,y)).getUnit().getArmy().getBattle().isFog() &&
                map.find(new Location(x,y)).getUnit().isHidden()) || 
               (map.find(new Location(x,y)).getUnit().getArmy().getBattle().isMist() &&
            	!map.find(new Location(x,y)).getUnit().isDived()) ||
            	map.find(new Location(x,y)).getUnit().detected)
            */
            // [CHANGE]
            if(pathableAtXY(x, y, map, u))
            {
                //mark move as valid
                moveArray[x][y]=true;
                //stop searching if a more efficient path (with more MP) already exists
                if(mpLeftArray[x][y]==-1 || mpLeftArray[x][y]<mp){
                    //records MP left
                    mpLeftArray[x][y]=mp;
                    
                    //if there is 0 mp left, don't check any further
                    if(mp == 0)return;
                    //send out probes to the left, right, and straight ahead
                    for(int i=-1;i<2;i++){
                        //don't go left or right twice in a row, it's worthless (unless you're a hovercraft <_<)
                        if((!((left == true && i == -1)||(right == true && i == 1)))|| u.getMType()==u.MOVE_HOVER){
                            //find correct direction and send recursive probes
                            switch((d+i)%4){
                                case -1:
                                case 3:
                                    //0=north, 1=east, 2=south, 3=west
                                    initMoveTraverse(x-1,y,3,mp,i==-1,i==1,map,u,gas,ctindex);
                                    break;
                                case 0:
                                    initMoveTraverse(x,y+1,0,mp,i==-1,i==1,map,u,gas,ctindex);
                                    break;
                                case 1:
                                    initMoveTraverse(x+1,y,1,mp,i==-1,i==1,map,u,gas,ctindex);
                                    break;
                                case 2:
                                    initMoveTraverse(x,y-1,2,mp,i==-1,i==1,map,u,gas,ctindex);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    //used to check if a particular move is legal
    public boolean checkMove(int x,int y){
        return moveArray[x][y];
    }
    
    //used to check if a particular move is legal
    public boolean checkMove(Location l){
        return moveArray[l.getCol()][l.getRow()];
    }
    
    //used to check the remaining mp at a location
    public int checkMPLeft(int x,int y){
        return mpLeftArray[x][y];
    }
    
    //converts the array into an easy to a displayable string format, used for debug
    public String toString(){
        String work = "";
        for(int col = 0; col < moveArray.length; col++){
            for(int row = 0; row < moveArray[col].length; row++){
                if(moveArray[col][row]==true){
                    work += "1";
                }else{
                    work += "0";
                }
            }
            work += "\n";
        }
        
        work += "\n";
        
        for(int col = 0; col < mpLeftArray.length; col++){
            for(int row = 0; row < mpLeftArray[col].length; row++){
                if(mpLeftArray[col][row] >= 0)work += " ";
                work += mpLeftArray[col][row];
            }
            work += "\n";
        }
        return work;
    }
    
    public boolean[][] getMoves(){
        return moveArray;
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
				//If it is not hidden, the square is not open.
    			if(movingUnit.getArmy().getBattle().isFog() && !thisUnit.isHidden())
    			{
    				return false;
    			}
    			//Check for MoW based conditions
				//Check if the current unit is 'dived' or if the unit has been 
    			//detected. If it is not dived, or it has been detected, it is
    			//thus occupying the square and it is not open.
    			else if(movingUnit.getArmy().getBattle().isMist() && (!thisUnit.isDived() || thisUnit.detected))
    			{
    				return false;
    			}
    			//Check for clear conditions
				//Check if the current unit is 'hidden'
				//If it is not hidden, the square is not open.
    			else if(!thisUnit.isHidden())
    			{
    				return false;
    			}	    		
    		}
    	}
    	
    	return true;
    }
}