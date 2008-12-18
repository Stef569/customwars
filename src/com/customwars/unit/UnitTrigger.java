/*
 * UnitTrigger.java
 *
 * Created on October 2, 2007, 4:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.customwars.unit;
import java.io.*;

import com.customwars.Battle;
import com.customwars.Trigger;
import com.customwars.map.location.Location;

public class UnitTrigger extends Trigger{
    private int unitType;
    private int UnitHP;
    private int UnitFuel;
    private int UnitAmmo;
    private int x;
	private int y;
    private int army;
    /**
     * This sets up the Trigger type and 'primes' it.
     * <p>
     * @param x the x position on the map to be spawned at
     * @param y the y position on the map to be spawned at
     * @param u the unit type
     * @param a the turn of the starting armies the unit belongs to.
     * @param hp the hp the unit spawns at. Set to -1 to default to full
     * @param fuel the fuel the unit spawns with. Set to -1 to default to full
     * @param ammo the ammo the unit spawns with. Set to -1 to default to full
     */
    public UnitTrigger(Battle b, int day, int turn, int x, int y, int u, int a, int hp, int fuel, int ammo){
        super(b, day, turn, 0);
     this.setX(x);
     this.setY(y);
     setUnitType(u);
     setArmy(a);
     setUnitHP(hp);
     setUnitFuel(fuel);
     setUnitAmmo(ammo);
    }


    
    public void trigger()
    {
        getB().placeUnit(getB().getMap(),getB().getMap().find(new Location(getX(),getY())), getUnitType(), getB().getStatArmy(getArmy()));
        if(getUnitHP() != -1)
        getB().getMap().find(new Location(getX(),getY())).getUnit().damage(10-getUnitHP(), false);
        if(getUnitFuel() != -1)
        {
            getB().getMap().find(new Location(getX(),getY())).getUnit().setGas(getUnitFuel());
        }
        if(getUnitAmmo() != -1)
        {
            getB().getMap().find(new Location(getX(),getY())).getUnit().setAmmo(getUnitAmmo());
        }
    }



	public void setArmy(int army) {
		this.army = army;
	}



	public int getArmy() {
		return army;
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



	public void setUnitType(int unitType) {
		this.unitType = unitType;
	}



	public int getUnitType() {
		return unitType;
	}



	public void setUnitHP(int unitHP) {
		UnitHP = unitHP;
	}



	public int getUnitHP() {
		return UnitHP;
	}



	public void setUnitFuel(int unitFuel) {
		UnitFuel = unitFuel;
	}



	public int getUnitFuel() {
		return UnitFuel;
	}



	public void setUnitAmmo(int unitAmmo) {
		UnitAmmo = unitAmmo;
	}



	public int getUnitAmmo() {
		return UnitAmmo;
	}
}