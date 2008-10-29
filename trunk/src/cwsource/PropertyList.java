package cwsource;
/*
 *PropertyList.java
 *Author: Urusan
 *Contributors:
 *Creation: August 11, 2006, 6:22 AM
 *A Property List. A linked list for holding an Army's Properties
 */

import java.io.*;

class PropertyLink implements Serializable{
    public Property data;       //Holds the link's data
    public PropertyLink next;   //Holds the next link
    
    //constructor
    public PropertyLink(Property u){
        data = u;
    }
    
    //returns the Link's data
    public Property getData(){
        return data;
    }
    
    //converts the data to a string for debugging
    public String toString(){
        return data.toString();
    }
}

public class PropertyList implements Serializable{
    
    public PropertyLink first; //pointer to the first link
    public PropertyLink last;  //pointer to the last link
    
    //constructor
    public PropertyList(){
        first = null;
        last = null;
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return first==null;
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(Property o){
        PropertyLink newLink = new PropertyLink(o);
        int originalSize = getSize();
        
        if(isEmpty())
            last = newLink;
        newLink.next = first;
        first = newLink;
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            PropertyLink current = first;
            while(current.next!=null){
                current = current.next;
            }
            current.next = newLink;
        }
    }
    
    //insert the an object at the end of the list
    public void insertLast(Property o){
        PropertyLink newLink = new PropertyLink(o);
        int originalSize = getSize();
        if(isEmpty()){
            first = newLink;
        }else{
            last.next = newLink;
        }
        last = newLink;
        
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            PropertyLink current = first;
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
    public boolean deleteItem(Property o){
        if(isEmpty())return false;
        PropertyLink current = first;
        PropertyLink previous = null;
        
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
    }
    
    //gets the size of the list
    public int getSize(){
        PropertyLink current = first;
        int i = 0;
        
        while(current!=null){
            i++;
            current = current.next;
        }
        return i;
    }
    
    //returns an array with all the objects in the list
    public Property[] getItems(){
        if(isEmpty())return null;
        
        Property[] o = new Property[getSize()];
        PropertyLink current = first;
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
        PropertyLink current = first;
        String s = "";
        while(current!=null){
            s += current;
            s += " ";
            current = current.next;
        }
        
        return s;
    }
    
}