package edu.ufl.cise.cop5555.sp12.context;

import org.junit.Test;

import edu.ufl.cise.cop5555.sp12.Parser;
import edu.ufl.cise.cop5555.sp12.TestSimpleParser;
import edu.ufl.cise.cop5555.sp12.TokenStream;
import edu.ufl.cise.cop5555.sp12.ast.AST;

public class ContextCheckVisitorTest
{
    private Parser parser = null;
    private ContextCheckVisitor visitor = new ContextCheckVisitor();
    
    private void initParser(String input)
    {
        TokenStream stream = TestSimpleParser.getInitializedTokenStream(input);
        parser = new Parser(stream);
    }
    
    @Test
    public void testVisitProgram() throws Exception
    {
        String input = "prog Test1 gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testUniqueProgramName() throws Exception
    {
        String input = "prog Test1 int Test1; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitBlock() throws Exception
    {
        String input = "prog Test1 boolean bool; bool = true; if(bool) if(true); ; ; fi; fi; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitDeclaration() throws Exception
    {
        String input = "prog Test1 do (true) int x; string y; boolean z; map[int, map[int, map[int, string]]] m1; od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test(expected=ContextException.class)
    public void testVisitAssignExprCommand() throws Exception
    {
        String input = "prog Test int i; boolean b; i = b; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitAssignExprCommand2() throws Exception
    {
        String input = "prog Test1 int a; a = (2*3) ; boolean b; b = (2 & 2) ; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitAssignPairListCommand() throws Exception
    {
        String input = "prog Test map[int, map[string, boolean]] m;" +
                "map[string, boolean] sbmap;" +
//        		" m = {[3,[\"str\", true]]}; gorp";
                "sbmap = {[\"str\", true]}; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitPrintCommand() throws Exception
    {
        String input = "prog Test1 print (true & false) ; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitPrintCommand2() throws Exception
    {
        String input = "prog Test1 print (2 * \"str\") ; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitPrintlnCommand() throws Exception
    {
        String input = "prog Test1 map[int, int] x; println x; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitDoCommand() throws Exception
    {
        String input = "prog Test1 do (2 + 5 == 7) map [int, int] m; od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitDoCommand2() throws Exception
    {
        String input = "prog Test1 boolean c; string s; do ( ( 1 + s)== 3);od ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitDoCommand3() throws Exception
    {
        String input = "prog Test1 do (true) aa = ab; od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitDoEachCommand() throws Exception
    {
        String input = "prog Test1 map[int, int] x; int x1; int x2; boolean y; map[int, map[boolean, map[string, int]]] m1; do x : [x1 , x2] println x1;  od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test(expected=ContextException.class)
    public void testVisitDoEachCommand2() throws Exception
    {
        String input = "prog Test1 map[int, int] x; int x1; string x2; do x : [x1 , x2] println x1;  od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test//(expected=ContextException.class)
    public void testVisitDoEachCommand3() throws Exception
    {
        String input = "prog Test1 map[int, int] x1; map[int, int] x2; int c1; int c2; int c4;" +
        		" do x1 : [c1 , c2] do x2 : [c1, c4] boolean znunu; boolean asfa; znunu = asfa; od; println c4;  od; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitIfElseCommand() throws Exception
    {
        String input = "prog Test1 boolean x; boolean y; if ( x | y ) a = true; else y=s; fi;  gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test
    public void testVisitIfElseCommand2() throws Exception
    {
        String input = "prog Test1 boolean x; boolean y; string a; if ( x | y ) boolean a;" +
        		"a = true; else boolean s; y=s; fi;  gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitIfElseCommand3() throws Exception
    {
        String input = "prog Test1 boolean x; boolean y; string a; if ( x | y )" +
                "a = true; else boolean s; y=s; fi;  gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitIfCommand() throws Exception
    {
        String input = "prog Test int ab; int x; if(x == 10) boolean ab; ab = true ; ; ; fi; ab = 10; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitIfCommand2() throws Exception
    {
        String input = "prog Test int ab; int x; if(x == 10) boolean ab; ab = true ; ; ; fi; ab = 10;" +
                "string ab; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test(expected=ContextException.class)
    public void testVisitSimpleLValue() throws Exception
    {
        String input = "prog Test int ab; int x; if(x == 10) boolean ab; string s; ab = true ; ; ; fi; " +
        		"ab = 10; s = \"xyz\"; string ab; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitExprLValue() throws Exception
    {
        String input = "prog Test map[int, map[int, string]] m; map[int, string] valType;" +
        		"int keyType; m[keyType] = valType; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test(expected=ContextException.class)
    public void testVisitPair() throws Exception
    {
        String input = "prog Test1  map[int, boolean] m; m = {[2,3], [2, false]} ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitPair2() throws Exception
    {
        String input = "prog Test1  map[int, boolean] m; m = {[2, false]} ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test(expected=ContextException.class)
    public void testVisitPairList() throws Exception
    {
        String input = "prog Test1  map[int,boolean] m; m = {[2,3]} ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitPairList2() throws Exception
    {
        String input = "prog Test1 map[int,boolean] m; m = {[2,3],[\"str\", true]} ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitIntegerLiteralExpression() throws Exception
    {
        String input = "prog Test1 int i; i = 2/3*4/5+6-7 ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitBooleanLiteralExpression() throws Exception
    {
        String input = "prog Test1 boolean b; b = true|false & (false|true) & false & true ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitStringLiteralExpression() throws Exception
    {
        String input = "prog Test1 string s; s = \"waste\" + \"of\" + \"time\"; ;gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitUnaryOpExpression() throws Exception
    {
        String input = "prog Test1 int i; i = -1 + -2; if(i == -3) print true; fi; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }

    @Test
    public void testVisitUnaryOpExpression2() throws Exception
    {
        String input = "prog Test1 boolean i; boolean j; i = !j; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test
    public void testVisitBinaryOpExpression() throws Exception
    {
        String input = "prog Test1 string s; int i; i = -3; s = \"abc\" + i; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test
    public void testVisitBinaryOpExpression2() throws Exception
    {
        String input = "prog Test1 string s; int i; boolean b; i = -3; s = \"abc\" + b; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test(expected=ContextException.class)
    public void testVisitBinaryOpExpression3() throws Exception
    {
        String input = "prog Test1 string s; int i; map[int, string] m; i = -3; s = \"abc\" + m; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
    
    @Test
    public void testManyExpressions() throws Exception
    {
        String input = "prog Test map [int , map[string, boolean]] m ;  map[string, boolean] m1; string a; boolean b; m = {[2 , m1]} ; gorp";
        this.initParser(input);
        AST ast = parser.parse();
        ast.visit(visitor, null);
    }
}
