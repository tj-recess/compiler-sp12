package edu.ufl.cise.cop5555.sp12.context;

import static org.junit.Assert.fail;

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

    @Test
    public void testVisitSimpleType()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitCompoundType()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitAssignExprCommand()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitAssignPairListCommand()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitPrintCommand()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitPrintlnCommand()
    {
        fail("Not yet implemented");
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

    @Test
    public void testVisitSimpleLValue()
    {
        fail("Not yet implemented");
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

    @Test
    public void testVisitPair()
    {
        fail("Not yet implemented");
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
    public void testVisitLValueExpression()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitIntegerLiteralExpression()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitBooleanLiteralExpression()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitStringLiteralExpression()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitUnaryOpExpression()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitBinaryOpExpression()
    {
        fail("Not yet implemented");
    }

}
