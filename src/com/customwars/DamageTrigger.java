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

import com.customwars.map.location.Location;

public class DamageTrigger extends Trigger{
    private int x;
	private int y;
    private int damage;
    private boolean destroy;
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
        this.setX(x);
        this.setY(y);
        this.setDamage(damage);
        this.setDestroy(destroy);
    }
    
    
    
    public void trigger() {
        if(getB().getMap().find(new Location(getX(),getY())).hasUnit()) {
           getB().getMap().find(new Location(getX(),getY())).getUnit().damage(getDamage(), isDestroy()); 
        }
    }



	public void setDamage(int damage) {
		this.damage = damage;
	}



	public int getDamage() {
		return damage;
	}



	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}



	public boolean isDestroy() {
		return destroy;
	}



	public void setY(int y) {
		this.y = y;
	}



	public int getY() {
		return y;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getX() {
		return x;
	}
}