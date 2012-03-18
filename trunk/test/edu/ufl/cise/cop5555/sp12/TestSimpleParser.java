package edu.ufl.cise.cop5555.sp12;

import org.junit.Test;

import edu.ufl.cise.cop5555.sp12.ast.AST;
import edu.ufl.cise.cop5555.sp12.ast.ToStringVisitor;

public class TestSimpleParser
{

    private TokenStream getInitializedTokenStream(String input)
    {
        TokenStream stream = new TokenStream(input);
        Scanner s = new Scanner(stream);
        s.scan();
        return stream;
    }

    @Test
    public void testEmptyProg() throws Exception
    {
        String input = "prog Test1 gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        // parser.parse();
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);

    }

    @Test
    public void testIntDec() throws Exception
    {
        String input = "prog Test1 int x; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        // parser.parse();
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testBooleanDec() throws Exception
    {
        String input = "prog Test1 boolean x; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        // parser.parse();
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testMapDec() throws Exception
    {
        String input = "prog Test1 map[int,string] y; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testMapDec2() throws Exception
    {
        String input = "prog Test1 map[int,map[string,boolean]] m; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test(expected = SyntaxException.class)
    public void xtestMapDec2() throws SyntaxException
    {
        String input = "prog Test1 map[int,map[string,boolean]] ; gorp"; // semi
                                                                         // is
                                                                         // the
                                                                         // error,
                                                                         // should
                                                                         // be
                                                                         // ident
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
        // assertNotNull(result);
        // assertEquals(SEMI, result.t.kind);
    }

    @Test(expected = SyntaxException.class)
    public void Test1() throws SyntaxException
    {
        String input = "prog gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
    }

    @Test(expected = SyntaxException.class)
    public void Test2() throws SyntaxException
    {
        String input = "gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
    }

    @Test
    public void Test3() throws Exception
    {
        String input = "prog Test ; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void Test4() throws SyntaxException
    {
        String input = "prog Test int a; int b; ; ;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test(expected = SyntaxException.class)
    public void Test5() throws SyntaxException
    {
        String input = "prog Test a[true] = truegorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
    }

    @Test(expected = SyntaxException.class)
    public void Test6() throws SyntaxException
    {
        String input = "";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test7() throws SyntaxException
    {
        String input = "prog Test a[true] = true; ;;;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test8() throws SyntaxException
    {
        String input = "prog Test int a; boolean b;string s;map[int,int] m; map[int,map[int , boolean ] ] m; map[int,map[boolean,map[string,string]]] m;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test9() throws SyntaxException
    {
        String input = "prog Test a = b & b -2; print \"abc\"+2;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test10() throws SyntaxException
    {
        String input = "prog Test if(2* \"abc\") ; fi ;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test11() throws SyntaxException
    {
        String input = "prog Test if(2* \"abc\") ; else ; fi ;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test12() throws SyntaxException
    {
        String input = "prog Test int a; boolean b; string c; do b : [a,b] od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test13() throws SyntaxException
    {
        String input = "prog Test do (2* 5 + a[\"abc\"] == true ) od;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test14() throws SyntaxException
    {
        String input = "prog Test a[2/ \"a\"] = {} ;gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test15() throws SyntaxException
    {
        String input = "prog Test a[(!2)] = {[2,false] , [2,2] };gorp";

        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();

    }

    @Test
    public void Test16() throws Exception
    {
        String input = "prog Test1 do println : [x1 , x2] println x1;  od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void xtestMapDec28() throws Exception
    {
        String input = "prog main int x;boolean y;map[string,string] mapHash;x=12;x=12*3;y={};y={[x+y,10/12]};y[10]=20; print hello; println xyz*3;do(x>y) string new;new=\"Hello World\"; od;  do x:[y,z] y=y+z*20; od; if(g>s) mapHash[x]=y; fi; if(g<=s) mapHash[y]=x; else mapHash[x]=y; fi; x=x & !y; x=x-y; x=(x-y*20)/20; gorp"; // This
                                                                                                                                                                                                                                                                                                                                        // should
                                                                                                                                                                                                                                                                                                                                        // pass
                                                                                                                                                                                                                                                                                                                                        // without
                                                                                                                                                                                                                                                                                                                                        // any
                                                                                                                                                                                                                                                                                                                                        // errors
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        // parser.parse();
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);

    }

    @Test
    public void xtestMapDec30()
    {
        String input = "prog y string x; x[x[x(4)]]=-y[4+-y]; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST tree;
        try
        {
            tree = parser.parse();
            ToStringVisitor pv = new ToStringVisitor();
            try
            {
                tree.visit(pv, "");
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String s = pv.getString();
            System.out.println(s);
        } catch (SyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testCommandNestedDo79() throws Exception
    {
        String input = "prog Test1 do x1 : [x1 , x2] do x2 : [x1, c4] znunu = asfa; od; println x;  od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandIf84() throws Exception
    {
        String input = "prog Test1 if (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) ab = true ; ; ; fi; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, "");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void xtestCommandIfElse87() throws Exception
    {
        String input = "prog Test1 if ( x | y ) a = true; else y=s; fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandIf85() throws Exception
    {
        String input = "prog Test1 if (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; ab = sc; ;fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void xTestCommandIfElse87() throws Exception
    {
        String input = "prog Test1 if ( x | y ) a = true; else y=s; fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandIfElse90() throws Exception
    {
        String input = "prog Test1 if ((a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f)) a = true; else y=s; fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandIfElse92() throws Exception
    {
        String input = "prog Test1 if (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; ab = sc;  fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandIfElse93() throws Exception
    {
        String input = "prog Test1 if (a) b=true; if (x==5) at=5; else at = 6; fi; if (x==5) a=w; fi; fi;  gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testFullProgram94() throws Exception
    {
        String input = "prog selectsort\n" +
                        "map[int, int] array;\n" + 
                        "int i; int j;  int sizeofarr;\n" +
                        "i =0; j=0; sizeofarr = 100;\n" +
                        "do (i < sizeofarr)\n" +
                            "do ( j < sizeofarr)\n" +
                                 "if ( array[i] > array[j])\n" +
                                 "int tmp; tmp=0;\n" +
                                      "tmp = array[i];\n" +
                                     "array[i] = array[j]; \n" +
                                     "array[j] = array[i];\n" +
                                 "fi;\n" +
                             "od;\n" +
                        "od;\n" +
                        "gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand40() throws Exception
    {
        String input = "prog Test1 m = a --b; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand41() throws Exception
    {
        String input = "prog Test1 m = a --!b; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand42() throws Exception
    {
        String input = "prog Test1 m = a -(-!b) | !-(b); gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandPrint50() throws Exception
    {
        String input = "prog Test1 print s + 5 + true + \"s\"; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandPrint51() throws Exception
    {
        String input = "prog Test1 println s + 5 + true + \"s\"; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandDo57() throws Exception
    {
        String input = "prog Test1 do (2 + 5) map [int, int] m; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandDo58() throws Exception
    {
        String input = "prog Test1 do (2 + 5) aa = ab; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandDo62() throws Exception
    {
        String input = "prog Test1 do (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) ; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommandDo64() throws Exception
    {
        String input = "prog Test1 do (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandDo65() throws Exception
    {
        String input = "prog Test1 do (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; ab = sc; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandDo66() throws Exception
    {
        String input = "prog Test1 do x : [x1 , x2] ;  od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandDo67() throws Exception
    {
        String input = "prog Test1 int x; boolean y; map[int, map[boolean, map[string, int]]] m1; do x : [x1 , x2] println x1;  od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommandDo68() throws Exception
    {
        String input = "prog Test1 int x; boolean y; map[int, map[boolean, map[string, int]]] m1; do (a + b - c * d / e | \"a\" < true > false >= x <= y - -a != k & f) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; ab = sc; od; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void xtestMapDec6() throws Exception
    {
        String input = "prog Test1 map[map[string,boolean],int] m; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommand2() throws Exception
    {
        String input = "prog Test1 m = x + 4 - 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand3() throws Exception
    {
        String input = "prog Test1 m = 1 + 4 - 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommand4() throws Exception
    {
        String input = "prog Test1 m = 1 / 4 * 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommand5() throws Exception
    {
        String input = "prog Test1 m = true / false * \"strliteral\"; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }

    @Test
    public void testCommand6() throws Exception
    {
        String input = "prog Test1 m = 5 + 3 - true + false - \"strliteral\" | true & false != 5 ; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand20() throws Exception
    {
        String input = "prog Test1 m[5+4] = l; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand21() throws Exception
    {
        String input = "prog Test1 m = 4 < 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand22() throws Exception
    {
        String input = "prog Test1 m[4 +5 * 6 - 7] = 4 < 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand23() throws Exception
    {
        String input = "prog Test1 m[11] = 4 < 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand24() throws Exception
    {
        String input = "prog Test1 m[11 | 1 & 5 < 6 >= 1 != 19] = 4 < 5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand25() throws Exception
    {
        String input = "prog Test1 m[true | false & \"qs\" < m >= q != 19] = b4 < s5; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
    
    @Test
    public void testCommand26() throws Exception
    {
        String input = "prog Test1 m[true | false & \"qs\" < m >= q != 19] = b4[11 <= 2] * s5[a]; gorp";
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        AST ast = parser.parse();
        ToStringVisitor pv = new ToStringVisitor();
        ast.visit(pv, " ");
        String st = pv.getString();
        System.out.println(st);
    }
}
