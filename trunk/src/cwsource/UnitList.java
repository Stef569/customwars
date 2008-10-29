package cwsource;
/*
 *UnitList.java
 *Author: Urusan
 *Contributors:
 *Creation: July 14, 2006, 4:24 AM
 *A Unit List. A linked list for holding an Army's Units
 */

import java.io.*;

class UnitLink implements Serializable{
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
}

public class UnitList implements Serializable{
    
    public UnitLink first; //pointer to the first link
    public UnitLink last;  //pointer to the last link
    
    //constructor
    public UnitList(){
        first = null;
        last = null;
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return first==null;
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(Unit o){
        UnitLink newLink = new UnitLink(o);
        int originalSize = getSize();
        
        if(isEmpty())
            last = newLink;
        newLink.next = first;
        first = newLink;
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            UnitLink current = first;
            while(current.next!=null){
                current = current.next;
            }
            current.next = newLink;
        }
    }
    
    //insert the an object at the end of the list
    public void insertLast(Unit o){
        UnitLink newLink = new UnitLink(o);
        int originalSize = getSize();
        if(isEmpty()){
            first = newLink;
        }else{
            last.next = newLink;
        }
        last = newLink;
        
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            UnitLink current = first;
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
    public boolean deleteItem(Unit o){
        if(isEmpty())return false;
        UnitLink current = first;
        UnitLink previous = null;
        
        while(current!=null){
            if(current.data == o){
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
    
    //deletes the first equivalent object from the list
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
    }
    
    //gets the size of the list
    public int getSize(){
        UnitLink current = first;
        int i = 0;
        
        while(current!=null){
            i++;
            current = current.next;
        }
        return i;
    }
    
    //returns an array with all the objects in the list
    public Unit[] getItems(){
        if(isEmpty())return null;
        
        Unit[] o = new Unit[getSize()];
        UnitLink current = first;
        int i = 0;
        
        while(current!=null){
            o[i]=current.data;
            i++;
            current = current.next;
        }
        return o;
    }
    
    //converts the list to a string for debugging
    public String toString(){
        UnitLink current = first;
        String s = "";
        while(current!=null){
            s += current;
            s += " ";
            current = current.next;
        }
        
        return s;
    }
    
}