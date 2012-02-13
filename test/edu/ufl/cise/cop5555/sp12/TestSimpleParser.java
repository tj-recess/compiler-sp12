package edu.ufl.cise.cop5555.sp12;

import static edu.ufl.cise.cop5555.sp12.Kind.*;
import static org.junit.Assert.*;

import org.junit.Test;

import edu.ufl.cise.cop5555.sp12.Scanner;
import edu.ufl.cise.cop5555.sp12.SimpleParser;
import edu.ufl.cise.cop5555.sp12.TokenStream;

public class TestSimpleParser {
   
	private TokenStream getInitializedTokenStream(String input) {
		TokenStream stream = new TokenStream(input);
		Scanner s = new Scanner(stream);
		s.scan();
		return stream;
	}
	
	
	@Test
	public void testEmptyProg(){
		String input = "prog Test1 gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
	}
	
	@Test
	public void testIntDec(){
		String input = "prog Test1 int x; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
	}
	
	@Test
	public void testBooleanDec(){
		String input = "prog Test1 boolean x; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
	}
	
	@Test
	public void testMapDec(){
		String input = "prog Test1 map[int,string] y; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
	}
	
	@Test
	public void testMapDec2(){
		String input = "prog Test1 map[int,map[string,boolean]] m; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
	}
	
	@Test
	public void xtestMapDec2(){
		String input = "prog Test1 map[int,map[string,boolean]] ; gorp";  //semi is the error, should be ident
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNotNull(result);
		assertEquals(SEMI, result.t.kind);
	}
	
	@Test
	public void Test1()
	{
		String input = "prog gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNotNull(result);
		
	}
	
	@Test
	public void Test2()
	{
		String input = "gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNotNull(result);
		
	}
	
	@Test
	public void Test3()
	{
		String input = "prog Test ; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	@Test
	public void Test4()
	{
		String input = "prog Test int a; int b; ; ;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	@Test
	public void Test5()
	{
		String input = "prog Test a[true] = truegorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNotNull(result);
		
	}
	@Test
	public void Test6()
	{
		String input = "";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNotNull(result);
		
	}
	@Test
	public void Test7()
	{
		String input = "prog Test a[true] = true; ;;;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	
	@Test
	public void Test8()
	{
		String input = "prog Test int a; boolean b;string s;map[int,int] m; map[int,map[int , boolean ] ] m; map[int,map[boolean,map[string,string]]] m;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	
	@Test
	public void Test9()
	{
		String input = "prog Test a = b & b -2; print \"abc\"+2;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	@Test
	public void Test10()
	{
		String input = "prog Test if(2* \"abc\") ; fi ;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	
	@Test
	public void Test11()
	{
		String input = "prog Test if(2* \"abc\") ; else ; fi ;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	
	@Test
	public void Test12()
	{
		String input = "prog Test int a; boolean b; string c; do b : [a,b] od; gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);
		
	}
	
	@Test
	public void Test13()
	{
		String input = "prog Test do (2* 5 + a[\"abc\"] == true ) od;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);	
		
	}
	
	@Test
	public void Test14()
	{
		String input = "prog Test a[2/ \"a\"] = {} ;gorp";
		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);	
		
	}
	@Test
	public void Test15()
	{
		String input = "prog Test a[(!2)] = {[2,false] , [2,2] };gorp";

		TokenStream stream = getInitializedTokenStream(input);
		SimpleParser parser = new SimpleParser(stream);
		SyntaxException result = parser.parse();
		assertNull(result);	
		
	}
}
