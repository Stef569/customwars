package com.customwars;
/*
 *UnitList.java
 *Author: Urusan
 *Contributors:
 *Creation: July 14, 2006, 4:24 AM
 *A Unit List. A linked list for holding an Army's Units
 */

import java.io.*;
import java.util.LinkedList;
/*class UnitLink implements Serializable{
    public Unit data;       //Holds the link's data
    public UnitLink next;   //Holds the next link
    
    //constructor
    public UnitLink(Unit u){
        data = u;
    }
    
    //returns the Link's data
    public Unit getData(){
        return data;
    }
    
    //converts the data to a string for debugging
    public String toString(){
        return data.toString();
    }
}*/

public class UnitList implements Serializable{
    public LinkedList<Unit> list;
    public Unit first;
    //constructor
    public UnitList(){
        list = new LinkedList<Unit>();
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return list.isEmpty();
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(Unit o){
        list.addFirst(o);
    }
    
    //insert the an object at the end of the list
    public void insertLast(Unit o){
        list.addLast(o);
    }
    
    //delete the first link, returns false if the item was not deleted
    public boolean deleteFirst(){
        
        return list.removeFirst() == null;
    }
    
    //deletes a given object from the list
    public boolean deleteItem(Unit o){
        return list.remove(o);
    }
    
    /*//deletes the first equivalent object from the list
    public boolean deleteEqualItem(Unit o){
        if(isEmpty())return false;
        UnitLink current = first;
        UnitLink previous = null;
        
        while(current!=null){
            if(current.data.equals(o)){
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
    }*/
    
    //gets the size of the list
    public int getSize(){
        return list.size();
    }
    
    //returns an array with all the objects in the list
    public Unit[] getItems(){
        Unit[] u = new Unit[list.size()];
        for(int i = 0; i < list.size(); i++)
            u[i] = (Unit)list.get(i);
        return u;
    }
    
    //converts the list to a string for debugging
    public String toString(){
        String s = "";
        for(int i = 0; i < list.size(); i++)
            s += list.get(i) + " ";
        
        return s;
    }
    
}