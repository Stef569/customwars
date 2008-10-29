package cwsource;
//Name: Bloody Venus
//Motto: The City Awakens from a Dream...

import java.io.*;
import java.util.*;

public class StandardAI extends AI{
    
    Army army;
    Location aHQ;
    Location eHQ;
    Unit[] units;
    CO currCO;
    CO altCO;
    
    
    public StandardAI(Army a){
        army = a;
    }
    
    
    public void turn(){
        System.out.println("CURRENT THREAD COUNT====== : " + Thread.activeCount());
        BattleScreen bs = Mission.getBattleScreen();
        System.out.println("Generic AI running");
        System.out.println(Thread.currentThread());
        Thread.yield();
        
        //initiates
        units = army.getUnits();
        currCO = army.getCO();
        altCO = army.getAltCO();
        
        try{
            
            //Thread.sleep(5000);
            
            //CO Power checks and usage.
            //System.out.println("Charging MA LAZER");
            //army.charge(500.0);
            
            if (army.isTag())
                ;
            else if(currCO.stars == currCO.maxStars && (altCO != null && altCO.stars == altCO.maxStars)) {
                bs.executeNextAction(new CWEvent(4,0,0));
                System.out.println("AI - TAG");
            } else if(currCO.stars == currCO.maxStars && (altCO != null && altCO.stars > altCO.maxStars/2))
                ;
            else if(currCO.stars == currCO.maxStars) {
                bs.executeNextAction(new CWEvent(3,0,0));
                System.out.println("AI - SCOP");
            } else if(currCO.stars > currCO.COPStars+1)
                ;
            else if(currCO.stars > currCO.COPStars) {
                bs.executeNextAction(new CWEvent(2,0,0));
                System.out.println("AI - COP");
            }
            
            //Unit AI here.
            int con = 1;
            if(units != null)
                for(Unit u: units){
                     System.out.println("Total Number of Units: " + units.length);
                     System.out.println("Unit AI moving: " + con);
                    unitAI(u, bs);
                    con++;
                }
                //Property AI here.
                buildAI(bs);
                
                //Turn ending code.
                //While others may change. These should remain the same.
                
                if(army.isTag()){
                    bs.executeNextAction(new CWEvent(5,0,0));
                    turn();
                    return;
                }
                
                bs.executeNextAction(new CWEvent(6,0,0));
                bs.setDayStart(true);
                System.out.println("EndingTurn");
                bs = null;
                System.out.println("CURRENT THREAD COUNT====== : " + Thread.activeCount());
                return;
        }catch(Exception e){
            System.out.println("ERROR! ~ Alpha1A2: Bloody Venus");
            System.out.println(e);
            e.printStackTrace();
            army.getBattle().endTurn();
        }
    }
    
    public Army getArmy(){
        return army;
    }
    
    private void unitAI(Unit u, BattleScreen bs){
        try{
            Thread.sleep(500);
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Running Unit AI: " + u);
        
        //Capture Case Code
        if(u.getUType() < 2){
            Property take = null;
            if(u.getMap().hasProperty(u.getLocation().getCol(),u.getLocation().getRow())){
                Property temp = (Property) u.getMap().find(u).getTerrain();
                if(temp.cp < temp.totalcp){
                    super.capture(u,temp,bs);
                    return;
                }
            }
            Property[] pe = super.EnemyPropertiesInRange(u);
            System.out.println("Enemy Prop List Length for Unit: " + u + " : " + pe.length);
            Property[] pn = super.NeutralPropertiesInRange(u);
            System.out.println("Neutral Prop List Length for Unit: " + u + " : " + pn.length);
            if(pe.length > 0)
                take = PSelect(pe);
            if(pn.length > 0 && take == null)
                take = PSelect(pn);
            if(take != null){
                System.out.println("capturing with unit: " + u + "Capturing: " + take);
                super.capture(u,take,bs);
                return;
            }
        }
        
        //Attack Code
        boolean ranged  = u.getMinRange()>1;
        Unit[] us = super.enemyUnitsInRange(u);
        if(us == null || us.length == 0){
            //No Enemies Code
            u.calcMoveTraverse();
            int min = 1000000;
            Property hq = closestHQ();
            int hqx = hq.getTile().getLocation().getCol();
            int hqy = hq.getTile().getLocation().getRow();
            Tile t = null;
            boolean[][] moves = u.getMoveRange().getMoves();
            for(int col = 0; col < u.getMap().getMaxCol(); col++){
                for(int row = 0; row < u.getMap().getMaxRow(); row++){
                    if(moves[col][row] && army.getBattle().getMap().find(new Location(col,row)).getUnit() == null){
                        int temp = Math.abs(col-hqx) + Math.abs(row-hqy);
                        if(min > temp){
                            min = temp;
                            t = u.getMap().find(new Location(col,row));
                        }
                    }
                }
            }
            if(t == null)
            super.wait(u,bs);
            else
                super.move(u,t.getLocation().getCol(),t.getLocation().getRow(),bs);
            return;
        }
        if(us.length > 0){
            System.out.println("us greater than zero");
            int max = -1, temp;
            Unit ma = null;
            for(Unit ut: us){
                temp = u.getValue()*BaseDMG.find(u,ut,true);
                if(temp > max && (super.emptyTNeighbors(ut.getLocation().getCol(), ut.getLocation().getRow(), u)!= null || Math.abs(ut.getLocation().getRow()-u.getLocation().getRow()) == 1 ||Math.abs(ut.getLocation().getCol()-u.getLocation().getCol()) == 1)){
                    max = temp;
                    ma = ut;
                } else {
                    System.out.println("temp not greater than max");
                }
            }
            
            if(ma == null){
                System.out.println("wait for ma");
                super.wait(u, bs);
                return;
            }
            
            if(!ranged){
                //at this point ma should be the most advantagous unit to fight, well, using the fairly limited criteria
                
                max = -1;
                Tile tt = null;
                Tile[] ts = super.emptyTNeighbors(ma.getLocation().getCol(), ma.getLocation().getRow(), u);
                if(ts != null){
                for(Tile tu: ts){
                    temp = tu.getTerrain().getDef();
                    if(temp > max){
                        max = temp;
                        tt = tu;
                    }
                }
                if(tt == null){
                    System.out.println("wait for tt");
                    super.wait(u, bs);
                    return;
                }
                System.out.println(u);
                System.out.println(ma);
                System.out.println(tt);
                System.out.println(bs);
                super.moveAndFire(u,ma,tt.getLocation().getCol(),tt.getLocation().getRow(), bs);
                return;
            }super.standAndFire(u,ma,bs);
            }
            
            //at this point tt should be the most advantagous tile to fight from, well, using the fairly limited criteria
            
            if(ranged){
                super.standAndFire(u,ma,bs);
                return;
            }
            
            
        }
        
    }
    
    private Property PSelect(Property[] ps){
        LinkedList<Property> hq = new LinkedList<Property>();
        LinkedList<Property> bases = new LinkedList<Property>();
        LinkedList<Property> air = new LinkedList<Property>();
        LinkedList<Property> ports = new LinkedList<Property>();
        LinkedList<Property> towers = new LinkedList<Property>();
        LinkedList<Property> pipes = new LinkedList<Property>();
        LinkedList<Property> cities = new LinkedList<Property>();
        for(Property p: ps){
            if(p.getIndex() == 9  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                hq.add(p);
            if(p.getIndex() == 10  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                cities.add(p);
            if(p.getIndex() == 11  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                bases.add(p);
            if(p.getIndex() == 12  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                air.add(p);
            if(p.getIndex() == 13  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                ports.add(p);
            if(p.getIndex() == 14  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                towers.add(p);
            if(p.getIndex() == 17  && !army.getBattle().getMap().find(p.getTile().getLocation()).hasUnit())
                pipes.add(p);
        }
        if(hq.size() > 0)
            return hq.remove();
        if(bases.size() > 0)
            return bases.remove();
        if(air.size() > 0)
            return air.remove();
        if(ports.size() > 0)
            return ports.remove();
        if(towers.size() > 0)
            return towers.remove();
        if(pipes.size() > 0)
            return pipes.remove();
        if(cities.size() > 0)
            return cities.remove();
        else return null;
    }
    
    private void buildAI(BattleScreen bs){
        System.out.println("ARMY COLOR: " + army.getColor());
        Property[] builds = army.getProperties();
        Property p;
        for(int i = builds.length-1; i > 0; i--){
            p = builds[i];
            if(p.createLand && army.getBattle().getMap().find(p.getTile().getLocation()).getUnit() == null){
                if(army.getFunds() >= 1000 && super.unitCount(0) < 3){
                    CWEvent n = new BuildEvent(0,p.getTile().getLocation().getCol(),p.getTile().getLocation().getRow(),0,0);
                    bs.executeNextAction(n);
                    if(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit() == null)
                    System.out.println("BUILDING ERROR ERROR ERROR =======================================================");
                   // super.wait(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit(),bs);
               } else if(army.getFunds() >= 4000*army.getCO().getCostMultiplier()/100 && super.unitCount(4) == 0){
                    CWEvent n = new BuildEvent(4,p.getTile().getLocation().getCol(),p.getTile().getLocation().getRow(),0,0);
                    bs.executeNextAction(n);
                    if(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit() == null)
                    System.out.println("BUILDING ERROR ERROR ERROR =======================================================");
                  //  super.wait(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit(),bs);
                } else if(army.getFunds() >= 7000*army.getCO().getCostMultiplier()/100){
                    CWEvent n = new BuildEvent(2,p.getTile().getLocation().getCol(),p.getTile().getLocation().getRow(),0,0);
                    bs.executeNextAction(n);
                    if(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit() == null)
                    System.out.println("BUILDING ERROR ERROR ERROR =======================================================");
                   // super.wait(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit(),bs);
                } else if(army.getFunds() >= 1000*army.getCO().getCostMultiplier()/100){
                    CWEvent n = new BuildEvent(0,p.getTile().getLocation().getCol(),p.getTile().getLocation().getRow(),0,0);
                    bs.executeNextAction(n);
                    if(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit() == null)
                    System.out.println("BUILDING ERROR ERROR ERROR =======================================================");
                   // super.wait(army.getBattle().getMap().find(p.getTile().getLocation()).getUnit(),bs);
                }
            }
            
        }
    }
    
    public boolean hasRecon(){
        Unit[] units = army.getUnits();
        boolean hasRecon = false;
        if(units == null)
            return false;
        for(Unit u: units)
            if(u.getUnitType() == 4)
                hasRecon = true;
        return hasRecon;
    }
    
    public Property closestHQ(){
        Property[] allhqs = army.getBattle().getMap().HQs();
        int minlength = 100000;
        Property close = allhqs[0];
        LinkedList<Property> hq = new LinkedList<Property>();
        Property mhq = army.getProperties()[0];
        for(Property p: allhqs){
               if(p.getOwner().getSide() != army.getSide())
                   hq.add(p);
               else if(p.getOwner() == army)
                   mhq = p;
        }
         Property[] fayt = new Property[hq.size()];
        for(int i = 0; i < hq.size(); i++)
            fayt[i] =(Property) hq.get(i);
         
        for(Property p: fayt){
            int temp = Math.abs(mhq.getTile().getLocation().getCol() - p.getTile().getLocation().getCol()) + Math.abs(mhq.getTile().getLocation().getRow() - p.getTile().getLocation().getRow());
            if(minlength > temp){
                minlength = temp;
                close = p;
            }
        }
        return close;
    }
}