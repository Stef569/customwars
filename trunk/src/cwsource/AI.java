package cwsource;
/*
 *AI.java
 *Author:Adam Dziuk
 *Contributors:
 *Creation: March 17, 2007,11:57 AM
 *The AI interface, made to allow multiple types of AI as well as standardize AI development.
 */

import java.io.*;
import java.util.*;

public abstract class AI extends Thread implements Serializable{
   
    //this is a fucking comment.
    //BITCH
    public void run(){turn();}
    public abstract Army getArmy();
    
    public abstract void turn();
    
    public void moveAndFire(Unit mine, Unit foe, int moveToCol, int moveToRow, BattleScreen bs){
        mine.calcMoveTraverse();
        int ux = mine.getArmy().getBattle().getMap().find(mine).getLocation().getCol();
        int uy = mine.getArmy().getBattle().getMap().find(mine).getLocation().getRow();
        int xc = foe.getLocation().getCol();
        int yc = foe.getLocation().getRow();
        Path p = new Path(ux,uy);
       // System.out.println("recalcing path");
        p.reCalculatePath(moveToCol,moveToRow,mine);
       // System.out.println("done recalcing path");
       CWEvent n = new Action(1,ux,uy,p,xc,yc,0,0);
       bs.executeNextAction(n);
            try{
            Thread.yield();
        } catch (Exception e){
            System.out.println("ERROR IN MOVE AND FIRE " + e);
        }
       
    }
    
    public void wait(Unit mine, BattleScreen bs){
        mine.calcMoveTraverse();
        System.out.println("Waiting unit: " + mine);
         int ux = mine.getArmy().getBattle().getMap().find(mine).getLocation().getCol();
        int uy = mine.getArmy().getBattle().getMap().find(mine).getLocation().getRow();
        Path p = new Path(ux,uy);
      //  p.reCalculatePath(ux,uy,mine);
        CWEvent n = new Action(0,ux,uy,p,0,0,0,0);
        bs.executeNextAction(n);
             try{
            Thread.yield();
        } catch (Exception e){
            System.out.println("ERROR IN WAIT " + e);
        }
    }
    
    public void move(Unit mine, int colx, int rowy, BattleScreen bs){
        //mine.calcMoveTraverse();
          System.out.println("Moving unit: " + mine);
         int ux = mine.getArmy().getBattle().getMap().find(mine).getLocation().getCol();
        int uy = mine.getArmy().getBattle().getMap().find(mine).getLocation().getRow();
        if(mine.getArmy().getBattle().getMap().find(mine.getLocation()).getUnit() != mine)
            System.out.println("ERROR! LOCATIONS DO NOT MATCH");
        Path p = new Path(ux,uy);
        p.reCalculatePath(colx,rowy,mine);
        CWEvent n = new Action(0,ux,uy,p,0,0,0,0);
        bs.executeNextAction(n);
        if(mine.getArmy().getBattle().getMap().find(mine.getLocation()).getUnit() != mine)
            System.out.println("ERROR! LOCATIONS DO NOT MATCH");
        try{
            Thread.yield();
        } catch (Exception e){
            System.out.println("ERROR IN MOVE " + e);
        }
    }
    
    public void standAndFire(Unit mine, Unit foe, BattleScreen bs){
        mine.calcMoveTraverse();
//        foe.calcMoveTraverse();
        int ux = mine.getArmy().getBattle().getMap().find(mine).getLocation().getCol();
        int uy = mine.getArmy().getBattle().getMap().find(mine).getLocation().getRow();
        int xc = foe.getLocation().getCol();
        int yc = foe.getLocation().getRow();
        Path p = new Path(ux,uy);
    //    p.reCalculatePath(ux,uy,mine);
       CWEvent n = new Action(1,ux,uy,p,xc,yc,0,0);
       System.out.println(n);
       
       try{
       bs.executeNextAction(n);
       }catch(Exception e){
           System.out.println(e);
           e.printStackTrace();
       }
            try{
            Thread.yield();
        } catch (Exception e){
            System.out.println("ERROR IN STAND AND FIRE" + e);
        }
    }
    
    public void capture(Unit mine, Property p, BattleScreen bs){
        System.out.println("Capturing Unit: " + mine);
        mine.calcMoveTraverse();
         int ux = mine.getArmy().getBattle().getMap().find(mine).getLocation().getCol();
        int uy = mine.getArmy().getBattle().getMap().find(mine).getLocation().getRow();
        int px = p.getTile().getLocation().getCol();
        int py = p.getTile().getLocation().getRow();
        Path path = new Path(p.getTile().getLocation().getCol(),p.getTile().getLocation().getRow());
      //  p.reCalculatePath(ux,uy,mine);
        CWEvent n = new Action(2,ux,uy,path,px,py,0,0);
        bs.executeNextAction(n);
             try{
            Thread.yield();
        } catch (Exception e){
            System.out.println("ERROR IN CAPTURE" + e);
        }
    }
    
    public Property[] PropertiesInRange(Unit u){
        u.calcMoveTraverse();
        MoveTraverse m = u.getMoveRange();
        Property p;
        int maxCol = u.getArmy().getBattle().getMap().getMaxCol();
        int maxRow = u.getArmy().getBattle().getMap().getMaxRow();
      //  System.out.println("mc" + maxCol);
      //  System.out.println("mr" + maxRow);
        LinkedList<Property> ll = new LinkedList<Property>();
        for(int x = 0; x < maxCol; x++)
            for(int y = 0; y < maxRow; y++){
            //System.out.println(m);
            if(m.checkMove(x,y) && u.getArmy().getBattle().getMap().hasProperty(x,y)){
                p = (Property) u.getArmy().getBattle().getMap().find(new Location(x,y)).getTerrain();
                ll.add(p);
                System.out.println("adding to propsinrange " + p);
            }
            }
      Property[] kosh = new Property[ll.size()];
      
          for(int i = 0; i < ll.size(); i++)
              kosh[i] = ll.removeFirst();
      
     return kosh;
    }
    
    public Property[] EnemyPropertiesInRange(Unit u){
        Property[] inRange = PropertiesInRange(u);
        System.out.println("inrangelength enemy list" + inRange.length);
        if(inRange == null)
            return null;
         LinkedList<Property> ll = new LinkedList<Property>();
         for(Property p: inRange){
             System.out.println(p);
             if(p != null && p.isCapturable() && p.getOwner() != null && p.getOwner().getSide() != u.getArmy().getSide())
                 ll.add(p);
         }
         Property[] uru = new Property[ll.size()];
      for(Property pp:ll){
          for(int i = 0; i < ll.size(); i++)
              uru[i] = pp;
      }
     return uru;
    }
    
    public Property[] NeutralPropertiesInRange(Unit u){
          Property[] inRange = PropertiesInRange(u);
          System.out.println("length of arrayage: " + inRange.length);
          for(Property omg: inRange)
              System.out.println(omg);
         LinkedList<Property> ll = new LinkedList<Property>();
         for(Property p: inRange){
             if(p != null && p.isCapturable() && p.getOwner() == null){
                 System.out.println("adding" + p);
                 ll.add(p);
             } else
                 System.out.println("not adding" + p);
         }
         Property[] vimes = new Property[ll.size()];
      for(Property pp:ll){
          for(int i = 0; i < ll.size(); i++)
              vimes[i] = pp;
      }
     return vimes;
    }
    
    public Property[] AlliedPropertiesInRange(Unit u){
          Property[] inRange = PropertiesInRange(u);
         LinkedList<Property> ll = new LinkedList<Property>();
         for(Property p: inRange){
             if(p.isCapturable() && p.getOwner().getSide() == u.getArmy().getSide())
                 ll.add(p);
         }
         Property[] mg = new Property[ll.size()];
      for(Property pp:ll){
          for(int i = 0; i < ll.size(); i++)
              mg[i] = pp;
      }
     return mg;
    }
    
    public Unit[] enemyUnitsInRange(Unit u){
        MoveTraverse m = u.getMoveRange();
        int maxCol = u.getArmy().getBattle().getMap().getMaxCol();
        int maxRow = u.getArmy().getBattle().getMap().getMaxRow();
 //       System.out.println("mc" + maxCol);
    //    System.out.println("mr" + maxRow);
        boolean[][] range = new boolean[maxCol][maxRow];
        for(int x = 0; x < maxCol; x++)
            for(int y = 0; y < maxRow; y++){
            //System.out.println("x: " + x + " y: " + y);
            range[x][y] = u.checkDisplayFireRange(new Location(x,y));
            }
      Unit[] us = u.getArmy().getBattle().getMap().getUnits(range);
      if(us == null)
          return null;
        LinkedList<Unit> ll = new LinkedList<Unit>();
       for(Unit ul: us)
            if(ul.getArmy().getSide() != getArmy().getSide())
                ll.add(ul);
       Tile[] ts = getArmy().getBattle().getMap().getNeighbors(u.getLocation().getCol(),u.getLocation().getRow());
       for(Tile t: ts){
           if(t.hasUnit()){
               if(t.getUnit().getArmy().getSide() != u.getArmy().getSide())
                   if(ll.indexOf(t.getUnit()) == -1)
                       ll.add(t.getUnit());
           }
       }
       Unit[] tagg = new Unit[ll.size()];
       for(Unit deyn : tagg)
       System.out.println("targetable Unit: " + deyn);
        for(int i = 0; i < ll.size(); i++)
            tagg[i] =(Unit) ll.get(i);
        return tagg;
        //return null;
    }
    
    public Tile[] emptyNeighbors(int c, int r){
        Tile[] t = getArmy().getBattle().getMap().getNeighbors(c,r);
        LinkedList<Tile> ll = new LinkedList<Tile>();
        for(Tile tt : t)
            if(tt.getUnit() == null)
                ll.add(tt);
         Tile[] tagg = new Tile[ll.size()];
        System.out.println(tagg.length);
        for(int i = 0; i < ll.size(); i++)
            tagg[i] =(Tile) ll.get(i);
        if(tagg.length > 0)
            return tagg;
        else
            return null;
    }
    
    public Tile[] emptyTNeighbors(int c, int r, Unit u){
        u.calcMoveTraverse();
        boolean[][] legal = u.getMoveRange().getMoves();        
        Tile[] t = getArmy().getBattle().getMap().getNeighbors(c,r);
        LinkedList<Tile> ll = new LinkedList<Tile>();
        for(Tile tt : t)
            if(tt.getUnit() == null)
                if(legal[tt.getLocation().getCol()][tt.getLocation().getRow()])
                ll.add(tt);
         Tile[] tagg = new Tile[ll.size()];
        System.out.println(tagg.length);
        for(int i = 0; i < ll.size(); i++)
            tagg[i] =(Tile) ll.get(i);
        if(tagg.length > 0)
            return tagg;
        else
            return null;
    }
    
    public int unitCount(int unitType){
     Unit[] units = getArmy().getUnits();
        int count = 0;
        if(units == null)
            return -1;
        for(Unit u: units)
            if(u.getUnitType() == unitType)
                count++;
        return count;
    }
    
}
