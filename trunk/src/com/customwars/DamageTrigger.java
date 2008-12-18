/*
 * UnitTrigger.java
 *
 * Created on October 2, 2007, 4:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.customwars;
import java.io.*;

public class DamageTrigger extends Trigger{
    int x,y;
    int damage;
    boolean destroy;
    /**
     * This sets up the Trigger type and 'primes' it.
     * <p>
     * @param x the x position on the map to be aimed at
     * @param y the y position on the map to be aimed at
     * @param damage the damage to be dealt.
     * @param destroy whether the damage kills or not.
     */
    public DamageTrigger(Battle b, int day, int turn, int x, int y, int damage, boolean destroy){
        super(b, day, turn, 1);
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.destroy = destroy;
    }
    
    
    
    public void trigger() {
        if(getB().getMap().find(new Location(x,y)).hasUnit()) {
           getB().getMap().find(new Location(x,y)).getUnit().damage(damage, destroy); 
        }
    }
}