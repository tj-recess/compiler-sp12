package edu.ufl.cise.cop5555.sp12.codegen;


public class Demo
{
    static int x;
    static int y;
    static int z;
    static String s;
    static String t;
    public static void main(String[] args)
    {
        z = x + y;
        t = s + z; 
        t = s + t;
        
        if(t.startsWith(s));
    }
}