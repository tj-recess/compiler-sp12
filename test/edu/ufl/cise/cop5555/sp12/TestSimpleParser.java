package edu.ufl.cise.cop5555.sp12;

import org.junit.Test;

public class TestSimpleParser {
   
	private TokenStream getInitializedTokenStream(String input) {
		TokenStream stream = new TokenStream(input);
		Scanner s = new Scanner(stream);
		s.scan();
		return stream;
	}
	
	
	@Test
	public void testEmptyProg() throws SyntaxException
	{
		String input = "prog Test1 gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
	}
	
	@Test
	public void testIntDec() throws SyntaxException
	{
		String input = "prog Test1 int x; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
	}
	
	@Test
	public void testBooleanDec() throws SyntaxException
	{
		String input = "prog Test1 boolean x; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
	}
	
	@Test
	public void testMapDec() throws SyntaxException
	{
		String input = "prog Test1 map[int,string] y; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
	}
	
	@Test
	public void testMapDec2() throws SyntaxException
	{
		String input = "prog Test1 map[int,map[string,boolean]] m; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
	}
	
	@Test (expected = SyntaxException.class)
	public void xtestMapDec2() throws SyntaxException{
		String input = "prog Test1 map[int,map[string,boolean]] ; gorp";  //semi is the error, should be ident
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
//		assertNotNull(result);
//		assertEquals(SEMI, result.t.kind);
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
	public void Test3() throws SyntaxException
	{
		String input = "prog Test ; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		parser.parse();
		
		
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
	public void xtestMapDec28() throws SyntaxException
	{
        String input = "prog main int x;boolean y;map[string,string] mapHash;x=12;x=12*3;y={};y={[x+y,10/12]};y[10]=20; print hello; println xyz*3;do(x>y) string new;new=\"Hello World\"; od;  do x:[y,z] y=y+z*20; od; if(g>s) mapHash[x]=y; fi; if(g<=s) mapHash[y]=x; else mapHash[x]=y; fi; x=x & !y; x=x-y; x=(x-y*20)/20; gorp";  //This should pass without any errors
        TokenStream stream = getInitializedTokenStream(input);
        SimpleParser parser = new SimpleParser(stream);
        parser.parse();
        
    
        }
}
