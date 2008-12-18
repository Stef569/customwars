package com.customwars.map.location;
/*
 *PropertyList.java
 *Author: Urusan
 *Contributors:
 *Creation: August 11, 2006, 6:22 AM
 *A Property List. A linked list for holding an Army's Properties
 */

import java.io.*;
import java.util.LinkedList;


public class PropertyList implements Serializable{
    
 public LinkedList<Property> list;
    
    //constructor
    public PropertyList(){
        list = new LinkedList<Property>();
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return list.isEmpty();
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(Property o){
        list.addFirst(o);
    }
    
    //insert the an object at the end of the list
    public void insertLast(Property o){
        list.addLast(o);
    }
    
    //delete the first link, returns false if the item was not deleted
    public boolean deleteFirst(){
        
        return list.removeFirst() == null;
    }
    
    //deletes a given object from the list
    public boolean deleteItem(Property o){
        return list.remove(o);
    }
    
    /*//deletes the first equivalent object from the list
    public boolean deleteEqualItem(Property o){
        if(isEmpty())return false;
        PropertyLink current = first;
        PropertyLink previous = null;
        
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
    public Property[] getItems(){
        Property[] u = new Property[list.size()];
        for(int i = 0; i < list.size(); i++)
            u[i] = (Property)list.get(i);
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