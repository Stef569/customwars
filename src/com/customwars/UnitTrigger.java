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

public class UnitTrigger extends Trigger{
    int unitType;
    int UnitHP;
    int UnitFuel;
    int UnitAmmo;
    int x,y;
    int army;
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
     this.x = x;
     this.y = y;
     unitType = u;
     army = a;
     UnitHP = hp;
     UnitFuel = fuel;
     UnitAmmo = ammo;
    }


    
    public void trigger()
    {
        b.placeUnit(b.getMap(),b.getMap().find(new Location(x,y)), unitType, b.getStatArmy(army));
        if(UnitHP != -1)
        b.getMap().find(new Location(x,y)).getUnit().damage(10-UnitHP, false);
        if(UnitFuel != -1)
        {
            b.getMap().find(new Location(x,y)).getUnit().setGas(UnitFuel);
        }
        if(UnitAmmo != -1)
        {
            b.getMap().find(new Location(x,y)).getUnit().setAmmo(UnitAmmo);
        }
    }
}