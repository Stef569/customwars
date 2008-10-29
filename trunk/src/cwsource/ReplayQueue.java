package cwsource;
/*
 *ReplayQueue.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:53 AM
 *A queue containing CWEvents for replays
 */

import java.io.*;

class EventLink implements Serializable{
    public CWEvent data;     //Holds the link's data
    public EventLink next;   //Holds the next link
    
    //constructor
    public EventLink(CWEvent u){
        data = u;
    }
    
    //returns the Link's data
    public CWEvent getData(){
        return data;
    }
    
    //converts the data to a string for debugging
    public String toString(){
        return data.toString();
    }
}

public class ReplayQueue implements Serializable{
    
    public EventLink first; //pointer to the first link
    public EventLink last;  //pointer to the last link
    
    //constructor
    public ReplayQueue(){
        first = null;
        last = null;
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return first==null;
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(CWEvent o){
        EventLink newLink = new EventLink(o);
        int originalSize = getSize();
        
        if(isEmpty())
            last = newLink;
        newLink.next = first;
        first = newLink;
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            EventLink current = first;
            while(current.next!=null){
                current = current.next;
            }
            current.next = newLink;
        }
    }
    
    //insert the an object at the end of the list
    public void insertLast(CWEvent o){
        EventLink newLink = new EventLink(o);
        int originalSize = getSize();
        if(isEmpty()){
            first = newLink;
        }else{
            last.next = newLink;
        }
        last = newLink;
        
        //if the list is broken for some reason, repair it
        if(originalSize == getSize()){
            EventLink current = first;
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
    public boolean deleteItem(CWEvent o){
        if(isEmpty())return false;
        EventLink current = first;
        EventLink previous = null;
        
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
    public boolean deleteEqualItem(CWEvent o){
        if(isEmpty())return false;
        EventLink current = first;
        EventLink previous = null;
        
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
        EventLink current = first;
        int i = 0;
        
        while(current!=null){
            i++;
            current = current.next;
        }
        return i;
    }
    
    //returns an array with all the objects in the list
    public CWEvent[] getItems(){
        if(isEmpty())return null;
        
        CWEvent[] o = new CWEvent[getSize()];
        EventLink current = first;
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
        EventLink current = first;
        String s = "";
        while(current!=null){
            s += current;
            s += "\n";
            current = current.next;
        }
        
        return s;
    }
    
    //push onto queue
    public void push(CWEvent o){
        insertLast(o);
    }
    
    //pop the next CWEvent off the queue and return it
    public CWEvent pop(){
        if(first!=null){
            CWEvent temp = first.getData();
            deleteFirst();
            return temp;
        }
        return null;
    }
    
    public void clear(){
        first = null;
        last = null;
    }
}
