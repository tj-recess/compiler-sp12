package edu.ufl.cise.cop5555.sp12;

import edu.ufl.cise.cop5555.sp12.TokenStream;
import edu.ufl.cise.cop5555.sp12.TokenStream.Token;


public class SimpleParser {

	private TokenStream stream;
	private int index = 0;
	private Token token;
	
	public SimpleParser(TokenStream stream)
	{
		this.stream = stream;
		Scanner s = new Scanner(this.stream);
		s.scan();
		consume();
	}
	
	private void consume() 
	{
		token = stream.getToken(index++);
	}

	public SyntaxException parse() {
	try{
		Program();  //method corresponding to start symbol
		return null;
	}   catch (SyntaxException e){return e;}
	}

	private void Program() throws SyntaxException
	{
		match(Kind.PROG);
		match(Kind.IDENTIFIER);
		Block();
		match(Kind.GORP);
		match(Kind.EOF);		
	}

	private void Block() throws SyntaxException
	{
		do
		{
			if(predict(NonTerminal.Declaration))
			{
				Declaration();
			}
			else if(predict(NonTerminal.Command))
			{
				Command();
			}
			else
			{
				break;
			}
			match(Kind.SEMI);
		}while(true);
	}

	private void Command() throws SyntaxException 
	{
		if(predict(NonTerminal.LValue))
		{
			LValue();
			match(Kind.ASSIGN);
			if(predict(NonTerminal.Expression))
			{
				Expression();
			}
			else if(predict(NonTerminal.PairList))
			{
				PairList();
			}
			else
			{
				throw new SyntaxException(token, "unexpected token");
			}
		}
		else if(isKind(Kind.PRINT))
		{
			consume();
			Expression();
		}
		else if(isKind(Kind.PRINTLN))
		{
			consume();
			Expression();
		}
		else if(isKind(Kind.DO))
		{
			consume();
			if(isKind(Kind.LEFT_PAREN))
			{
				consume();
				Expression();
				match(Kind.RIGHT_PAREN);
			}
			else
			{
				LValue();
				match(Kind.COLON);
				match(Kind.LEFT_SQUARE);
				match(Kind.IDENTIFIER);
				match(Kind.COMMA);
				match(Kind.IDENTIFIER);
				match(Kind.RIGHT_SQUARE);
			}
			Block();
			match(Kind.OD);			
		}
		else if(isKind(Kind.IF))
		{
			consume();
			match(Kind.LEFT_PAREN);
			Expression();
			match(Kind.RIGHT_PAREN);
			Block();
			if(isKind(Kind.ELSE))
			{
				consume();
				Block();
			}
			match(Kind.FI);
		}
		//else ε is valid
	}

	private void PairList() throws SyntaxException 
	{
		match(Kind.LEFT_BRACE);
		if(predict(NonTerminal.Pair))
		{
			Pair();
			while(isKind(Kind.COMMA))
			{
				consume();	//consume comma
				Pair();
			}
		}
		match(Kind.RIGHT_BRACE);
	}

	private void Pair() throws SyntaxException 
	{
		match(Kind.LEFT_SQUARE);
		Expression();
		match(Kind.COMMA);
		Expression();
		match(Kind.RIGHT_SQUARE);
	}

	private void Expression() throws SyntaxException 
	{
		Term();
		while(true)
		{
			if(predict(NonTerminal.RelOp))
			{
				RelOp();
			}
			else if(predict(NonTerminal.Term))
			{
				Term();				
			}
			else 
			{
				break;
			}
		}
	}

	private void RelOp() 
	{
		consume();
	}

	private void Term() throws SyntaxException 
	{
		Elem();
		while(true)
		{
			if(predict(NonTerminal.WeakOp))
			{
				WeakOp();
			}
			else if(predict(NonTerminal.Element))
			{
				Elem();
			}
			else 
			{
				break;
			}
		}
	}

	private void WeakOp() 
	{
		consume();
	}

	private void Elem() throws SyntaxException 
	{
		Factor();
		while(true)
		{
			if(predict(NonTerminal.StrongOp))
			{
				StrongOp();
			}
			else if(predict(NonTerminal.Factor))
			{
				Factor();				
			}
			else 
			{
				break;
			}
		}
	}

	private void StrongOp() 
	{
		consume();
	}

	private void Factor() throws SyntaxException 
	{
		if(predict(NonTerminal.LValue))
		{
			LValue();
		}
		else if(isKind(Kind.INTEGER_LITERAL) || isKind(Kind.BOOLEAN_LITERAL) || isKind(Kind.STRING_LITERAL))
		{
			consume();
		}
		else if(isKind(Kind.LEFT_PAREN))
		{
			consume();
			Expression();
			match(Kind.RIGHT_PAREN);
		}
		else if(isKind(Kind.NOT))
		{
			consume();
			Factor();
		}
		else if(isKind(Kind.MINUS))
		{
			consume();
			Factor();
		}
		else
		{
			throw new SyntaxException(token, "unexpected token");
		}
	}

	private void LValue() throws SyntaxException 
	{
		match(Kind.IDENTIFIER);
		if(isKind(Kind.LEFT_SQUARE))
		{
			consume();
			Expression();
			match(Kind.RIGHT_SQUARE);
		}
	}

	private void Declaration() throws SyntaxException 
	{
		//control here means predict(Declaration) already returned true
		//i.e. type must have been matched already
		Type();
		match(Kind.IDENTIFIER);
	}

	private void Type() throws SyntaxException 
	{
		if(predict(NonTerminal.SimpleType))
		{
			SimpleType();
		}
		else if(predict(NonTerminal.CompoundType))
		{
			CompoundType();			
		}
	}

	private void CompoundType() throws SyntaxException 
	{
		consume();
		match(Kind.LEFT_SQUARE);
		SimpleType();
		match(Kind.COMMA);
		Type();
		match(Kind.RIGHT_SQUARE);
	}

	private void SimpleType() throws SyntaxException 
	{
		consume();
	}

	private boolean isKind(Kind kind) 
	{
		return kind == token.kind;
	}
	
	private void match(Kind kind) throws SyntaxException 
	{
		if (isKind(kind)) 
		{
			consume();
		}
		else
		{
			throw new SyntaxException(this.token, "expected " + kind);
		}
	}
	
	private boolean predict(NonTerminal x)
	{
		switch(x)
		{
		case Block:
			return predict(NonTerminal.Declaration) || predict(NonTerminal.Command);
		case Declaration:
			return predict(NonTerminal.Type);
		case Type:
			return predict(NonTerminal.SimpleType) || predict(NonTerminal.CompoundType);
		case SimpleType:
			return isKind(Kind.INT) || isKind(Kind.BOOLEAN) || isKind(Kind.STRING);
		case CompoundType:
			return isKind(Kind.MAP);
		case Command:
			return predict(NonTerminal.LValue) || isKind(Kind.PRINT) || isKind(Kind.PRINTLN) || isKind(Kind.DO)
			|| isKind(Kind.IF) || predict(NonTerminal.Epsilon);
		case Epsilon:
			return isKind(Kind.SEMI);
		case LValue:
			return isKind(Kind.IDENTIFIER);
		case Expression:
			return predict(NonTerminal.Term);
		case Term:
			return predict(NonTerminal.Element);
		case Element:
			return predict(NonTerminal.Factor);
		case Factor:
			return isKind(Kind.IDENTIFIER) || isKind(Kind.INTEGER_LITERAL) || isKind(Kind.BOOLEAN_LITERAL)
			|| isKind(Kind.STRING_LITERAL) || isKind(Kind.LEFT_PAREN) || isKind(Kind.NOT) || isKind(Kind.MINUS);
		case Pair:
			return isKind(Kind.LEFT_SQUARE);
		case PairList:
			return isKind(Kind.LEFT_BRACE);
		case RelOp:
			return isKind(Kind.OR) || isKind(Kind.AND) || isKind(Kind.EQUALS) || isKind(Kind.NOT_EQUALS) || isKind(Kind.LESS_THAN)
			|| isKind(Kind.GREATER_THAN) || isKind(Kind.AT_MOST) || isKind(Kind.AT_LEAST);
		case WeakOp:
			return isKind(Kind.PLUS) || isKind(Kind.MINUS);
		case StrongOp:
			return isKind(Kind.TIMES) || isKind(Kind.DIVIDE);
		}
		return false;
	}
}
	