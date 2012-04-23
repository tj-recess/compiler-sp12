package edu.ufl.cise.cop5555.sp12.codegen;


import static java.lang.System.err;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.ufl.cise.cop5555.sp12.Parser;
import edu.ufl.cise.cop5555.sp12.Scanner;
import edu.ufl.cise.cop5555.sp12.SyntaxException;
import edu.ufl.cise.cop5555.sp12.TokenStream;
import edu.ufl.cise.cop5555.sp12.ast.AST;
import edu.ufl.cise.cop5555.sp12.ast.Program;
import edu.ufl.cise.cop5555.sp12.context.ContextCheckVisitor;
import edu.ufl.cise.cop5555.sp12.context.ContextException;


public class CodeGenVisitorTest
{

    // overrides (protected) defineClass method to make it public
    static class COP5555ClassLoader extends ClassLoader {
        @SuppressWarnings("rawtypes")
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    static boolean WRITE_TO_FILE = true; // write generated classfile?
    static boolean EXECUTE = true; // dynamically execute generated classfile?

    @SuppressWarnings("rawtypes")
    public String main(String input) throws IOException 
    {
        String output = "";
        TokenStream stream = new TokenStream(input);
        Scanner scanner = new Scanner(stream);
        scanner.scan();
        Parser parser = new Parser(stream);
        AST tree = null;
        String progName = null;
        byte[] bytes = null;
        boolean codeGenSuccess = false;
        try {
            tree = parser.parse();
            ContextCheckVisitor tcv = new ContextCheckVisitor();
            tree.visit(tcv, null);
            progName = ((Program) tree).ident.getText();
            CodeGenVisitor gen = new CodeGenVisitor();
            bytes = (byte[]) tree.visit(gen, progName);
            codeGenSuccess = true;
        } catch (SyntaxException e) {
            err.println(e.t.getLineNumber() + ": Syntax error: "  + e.getMessage());
        } catch (ContextException e) {
            err.println("Type error: " + e.getMessage());
        } catch (Exception e) {
            err.println("Error " + e.getMessage());
            e.printStackTrace();
        }
        if (codeGenSuccess) {
            if (WRITE_TO_FILE) {  //  write classfile
                FileOutputStream f;
                System.out.println("writing class " + progName + ".class");
                f = new FileOutputStream(progName + ".class");
                f.write(bytes);
                f.close();
            }
            if (EXECUTE) {  //dynamically execute generated code
                COP5555ClassLoader cl = new COP5555ClassLoader();
                Class c = cl.defineClass(progName, bytes);
                try {
                    //get Method object for main method in generated code
                    @SuppressWarnings("unchecked")
                    Method mainMethod = c.getMethod("main", String[].class);
                    //set up command line arguments for generated class
                    // arg 0 was the source file, the rest should be passed
                    // to generated code
//                  int numArgs = args.length - 1; 
//                  String[] params = new String[numArgs];
//                      for (int j = 0; j != numArgs; j++) {
//                          params[j] = args[j + 1];
//                      }
//                  for (String p : params) System.out.println(p);
//                  Class[] paramTypes = mainMethod.getParameterTypes();
//                  for (Class p : paramTypes) System.out.println(p.getName());
                    Object[] objectParams = new Object[1];
//                  objectParams[0] = params;
                    output = (String) mainMethod.invoke(null, objectParams);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } // else (!codeGenSuccess) terminate
        return output;
    }
    
    @Test
    public void testMap3() throws IOException
    {
        String input = "prog TestMap3 " +
                        "int k; " +
                        "map[boolean, int] v; " +
                        "map[int, map[boolean, int]] m; " +
                        "map [boolean, int] m1; " +
                        "m1 = {[true,43],[false,95]}; " +
                        "print m;" +
                        "gorp";
        
//        String output =
            main(input);
//        Assert.assertEquals("{}", output);
    }
    
    //expected = "{100={false=95, true=43}}";
    @Test
    public void testMap4() throws IOException
    {
        String input = "prog TestMap4 " +
        "int k; " +
        "map[boolean, int] v; " +
        "map[int, map[boolean, int]] m; " +
        "map [boolean, int] m1; " +
        "m1 = {[true,43],[false,95]}; " +
        " m[100] = m1;" +
        "print m;" +
        "gorp";
        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103}{0=100, 2=102, 3=103}";
    @Test
    public void testMap2() throws IOException
    {
        String input = "prog TestMap2 " +
        "int k; " +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,103]}; " +
        "print m0; "+
        "print m1; " +
        "gorp";

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103}{0=100, 2=102, 3=103}";
    @Test
    public void testMapPlus() throws IOException
    {
        String input ="prog TestMapPlus " +
        "int k; " +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,103]}; " +
        "m0 = m0 + m1; " +
        "print m0; "+
        "print m1; " +
        "gorp";

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=104}{0=100, 2=102, 3=104}";
    @Test
    public void testMapPlus2() throws IOException
    {
        String input ="prog TestMapPlus2 " +
        "int k; " +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,104]}; " +
        "m0 = m0 + m1; " +
        "print m0; "+
        "print m1; " +
        "gorp";

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103, 5=100, 6=102, 7=104}{5=100, 6=102, 7=104}";
    @Test
    public void testMapPlus3() throws IOException
    {
        String input ="prog TestMapPlus3 " +
        "int k; " +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[5,100],[6,102], [7,104]}; " +
        "m0 = m0 + m1; " +
        "print m0; "+
        "print m1; " +
        "gorp";

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103}{0=100, 2=102, 3=104}false";
    @Test
    public void testMapEq() throws IOException
    {
        String input ="prog TestMapEq " +
        "int k; " +
        "boolean b;" +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,104]}; " +
        "b = m0 == m1; " +
        "print m0; "+
        "print m1; " +
        "print b; " +
        "gorp";

        main(input);
    }
    
    //expected =         "{0=100, 2=102, 3=103}{0=100, 2=102, 3=103}true";
    @Test
    public void testMapEq1() throws IOException
    {
        String input = "prog TestMapEq1 " +
        "int k; " +
        "boolean b;" +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,103]}; " +
        "b = m0 == m1; " +
        "print m0; "+
        "print m1; " +
        "print b; " +
        "gorp";

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103}{0=100, 2=102, 3=104}true";
    @Test
    public void testMapNeq() throws IOException
    {
        String input =  "prog TestMapNeq " +
            "int k; " +
            "boolean b;" +
            "map[int,int] m0; "+
            "map[int,int] m1; " +
            "m0 = {[0,100],[2,102], [3,103]}; " +
            "m1 = {[0,100],[2,102], [3,104]}; " +
            "b = m0 != m1; " +
            "print m0; "+
            "print m1; " +
            "print b; " +
            "gorp";            

        main(input);
    }
    
    //expected = "{0=100, 2=102, 3=103}{0=100, 2=102, 3=103}false";
    @Test
    public void testMapNeq1() throws IOException
    {
        String input =  "prog TestMapNeq1 " +
        "int k; " +
        "boolean b;" +
        "map[int,int] m0; "+
        "map[int,int] m1; " +
        "m0 = {[0,100],[2,102], [3,103]}; " +
        "m1 = {[0,100],[2,102], [3,103]}; " +
        "b = m0 != m1; " +
        "print m0; "+
        "print m1; " +
        "print b; " +
        "gorp";

        main(input);
    }

}
