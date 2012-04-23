package edu.ufl.cise.cop5555.sp12.codegen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Demo
{
//    static int x;
//    static int y;
//    static int z;
//    static String s;
//    static String t;
    
    static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    static HashMap<Integer, Integer> anotherMap = new HashMap<Integer, Integer>();
    static HashMap<Integer, HashMap<Integer, Boolean>> mapInAMap = new HashMap<Integer, HashMap<Integer,Boolean>>();
    
    public static void main(String[] args)
    {
//        z = 3;
//        
//        z = x + y;
//        t = s + z; 
//        t = s + t;
//        
//        s = "str";
        
        map.put(1, 2);
        
        System.out.println(map.get(1));
        
        map.clear();
        map.putAll(anotherMap);
        
//        Set<Entry<Integer, Integer>> set = map.entrySet();
//        Iterator<Entry<Integer, Integer>> it = set.iterator();
//        
//        while(it.hasNext())
//        {
//            Entry<Integer, Integer> anEntry = it.next();
//            Integer key = anEntry.getKey();
//            System.out.println(key);
//            
//        }
        
        Set<Entry<Integer, HashMap<Integer, Boolean>>> set = mapInAMap.entrySet();
        Iterator<Entry<Integer, HashMap<Integer, Boolean>>> it = set.iterator();
        
        do
        {
            Entry<Integer, HashMap<Integer, Boolean>> anEntry = it.next();
            Integer key = anEntry.getKey();
            System.out.println(key);
            HashMap<Integer, Boolean> value = anEntry.getValue();
            for(Entry<Integer, Boolean> e : value.entrySet())
            {
                System.out.println(e.getKey());
                System.out.println(e.getValue());
            }
            
        }while(it.hasNext());
                
//        if(t.startsWith(s));
    }
}