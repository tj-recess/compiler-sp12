package edu.ufl.cise.cop5555.sp12;

import java.util.LinkedList;
import java.util.List;

import edu.ufl.cise.cop5555.sp12.TokenStream;
import edu.ufl.cise.cop5555.sp12.TokenStream.Token;
import edu.ufl.cise.cop5555.sp12.ast.*;


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

	public AST parse() throws SyntaxException 
	{
		return Program();  //method corresponding to start symbol
	}

	private Program Program() throws SyntaxException
	{
		match(Kind.PROG);
		match(Kind.IDENTIFIER);
		Block aBlock = Block();
		match(Kind.GORP);
		match(Kind.EOF);
		return new Program(this.token, aBlock);
	}

	private Block Block() throws SyntaxException
	{
		List<DecOrCommand> decOrCmd = new LinkedList<DecOrCommand>();
		do
		{
			if(predict(NonTerminal.Declaration))
			{
				decOrCmd.add(Declaration());
			}
			else if(predict(NonTerminal.Command))
			{
				decOrCmd.add(Command());
			}
			else
			{
				break;
			}
			match(Kind.SEMI);
		}while(true);
		return new Block(decOrCmd);
	}

	private DecOrCommand Command() throws SyntaxException 
	{
		Command cmd = null;
		if(predict(NonTerminal.LValue))
		{
			LValue anLValue = LValue();
			match(Kind.ASSIGN);
			if(predict(NonTerminal.Expression))
			{
				Expression anExpr = Expression();
				cmd = new AssignExprCommand(anLValue, anExpr);
			}
			else if(predict(NonTerminal.PairList))
			{
				PairList aPairList = PairList();
				cmd = new AssignPairListCommand(anLValue, aPairList);
			}
			else
			{
				throw new SyntaxException(token, "unexpected token");
			}
		}
		else if(isKind(Kind.PRINT))
		{
			consume();
			Expression expr = Expression();
			cmd = new PrintCommand(expr);
		}
		else if(isKind(Kind.PRINTLN))
		{
			consume();
			Expression expr = Expression();
			cmd = new PrintlnCommand(expr);
		}
		else if(isKind(Kind.DO))
		{
			Expression anExpr = null;
			consume();
			if(isKind(Kind.LEFT_PAREN))
			{
				consume();
				anExpr = Expression();
				match(Kind.RIGHT_PAREN);
			}
			else
			{
				LValue anLValue = LValue();
				match(Kind.COLON);
				match(Kind.LEFT_SQUARE);
				match(Kind.IDENTIFIER);
				match(Kind.COMMA);
				match(Kind.IDENTIFIER);
				match(Kind.RIGHT_SQUARE);
				anExpr = new LValueExpression(anLValue);
			}
			Block aBlock = Block();
			match(Kind.OD);
			cmd = new DoCommand(anExpr, aBlock);
		}
		else if(isKind(Kind.IF))
		{
			consume();
			match(Kind.LEFT_PAREN);
			Expression anExpr = Expression();
			match(Kind.RIGHT_PAREN);
			Block ifBlock = Block();
			Block elseBlock = null;
			if(isKind(Kind.ELSE))
			{
				consume();
				elseBlock = Block();
			}
			match(Kind.FI);
			if(elseBlock == null)
			{
				cmd = new IfCommand(anExpr, ifBlock);
			}
			else
			{
				cmd = new IfElseCommand(anExpr, ifBlock, elseBlock);
			}
		}
		//else ε is valid
		return cmd;
	}

	private PairList PairList() throws SyntaxException 
	{
		List<Pair> pairList = new LinkedList<Pair>();
		match(Kind.LEFT_BRACE);
		if(predict(NonTerminal.Pair))
		{
			pairList.add(Pair());
			while(isKind(Kind.COMMA))
			{
				consume();	//consume comma
				pairList.add(Pair());
			}
		}
		match(Kind.RIGHT_BRACE);
		return new PairList(pairList);
	}

	private Pair Pair() throws SyntaxException 
	{
		match(Kind.LEFT_SQUARE);
		Expression expr0 = Expression();
		match(Kind.COMMA);
		Expression expr1 = Expression();
		match(Kind.RIGHT_SQUARE);
		return new Pair(expr0, expr1);
	}

	private Expression Expression() throws SyntaxException 
	{
		Expression expr0 = Term();
		Expression expr1 = null;
		while(true)
		{
			Kind operatorKind = null;
			if(predict(NonTerminal.RelOp))
			{
				RelOp();
				operatorKind = this.token.kind;
			}
			else if(predict(NonTerminal.Term))
			{
				expr1 = Term();				
			}
			else 
			{
				break;
			}
			expr0 = new BinaryOpExpression(expr0, operatorKind, expr1);
		}
		return expr0;
	}

	private void RelOp() 
	{
		consume();
	}

	private Expression Term() throws SyntaxException 
	{
		Expression expr0 = Elem();
		while(true)
		{
			Kind opKind = null;
			Expression expr1 = null;
			if(predict(NonTerminal.WeakOp))
			{
				WeakOp();
				opKind = this.token.kind;
			}
			else if(predict(NonTerminal.Element))
			{
				expr1 = Elem();
			}
			else 
			{
				break;
			}
			expr0 = new BinaryOpExpression(expr0, opKind, expr1);
		}
		return expr0;
	}

	private void WeakOp() 
	{
		consume();
	}

	private Expression Elem() throws SyntaxException 
	{
		Expression expr0 = Factor();
		while(true)
		{
			Kind opKind = null;
			Expression expr1 = null;
			if(predict(NonTerminal.StrongOp))
			{
				StrongOp();
				opKind = this.token.kind;
			}
			else if(predict(NonTerminal.Factor))
			{
				expr1 = Factor();				
			}
			else 
			{
				break;
			}
			expr0 = new BinaryOpExpression(expr0, opKind, expr1);
		}
		return expr0;
	}

	private void StrongOp() 
	{
		consume();
	}

	private Expression Factor() throws SyntaxException 
	{
		if(predict(NonTerminal.LValue))
		{
			LValue anLValue = LValue();
			return new LValueExpression(anLValue);
		}
		else if(isKind(Kind.INTEGER_LITERAL))
		{
			consume();
			return new IntegerLiteralExpression(this.token);
		}
		else if(isKind(Kind.BOOLEAN_LITERAL))
		{
			consume();
			return new BooleanLiteralExpression(this.token);
		}
		else if(isKind(Kind.STRING_LITERAL))
		{
			consume();
			return new StringLiteralExpression(this.token);
		}
		else if(isKind(Kind.LEFT_PAREN))
		{
			consume();
			Expression anExpr = Expression();
			match(Kind.RIGHT_PAREN);
			return anExpr;
		}
		else if(isKind(Kind.NOT))
		{
			consume();
			Expression anExpr = Factor();
			return new UnaryOpExpression(Kind.NOT, anExpr);
		}
		else if(isKind(Kind.MINUS))
		{
			consume();
			Expression anExpr = Factor();
			return new UnaryOpExpression(Kind.MINUS, anExpr);
		}
		else
		{
			throw new SyntaxException(token, "unexpected token");
		}
	}

	private LValue LValue() throws SyntaxException 
	{
		match(Kind.IDENTIFIER);
		if(isKind(Kind.LEFT_SQUARE))
		{
			consume();
			Expression anExpr = Expression();
			match(Kind.RIGHT_SQUARE);
			return new ExprLValue(this.token, anExpr);
		}
		return new SimpleLValue(this.token);
	}

	private DecOrCommand Declaration() throws SyntaxException 
	{
		//control here means predict(Declaration) already returned true
		//i.e. type must have been matched already
		Type aType = Type();
		match(Kind.IDENTIFIER);
		return new Declaration(aType, this.token);
	}

	private Type Type() throws SyntaxException 
	{
		Type aType = null;
		if(predict(NonTerminal.SimpleType))
		{
			aType = SimpleType();
		}
		else if(predict(NonTerminal.CompoundType))
		{
			aType = CompoundType();
		}
		return aType;
	}

	private Type CompoundType() throws SyntaxException 
	{
		consume();
		match(Kind.LEFT_SQUARE);
		SimpleType keyType = SimpleType();
		match(Kind.COMMA);
		Type valType = Type();
		match(Kind.RIGHT_SQUARE);
		return new CompoundType(keyType, valType);
	}

	private SimpleType SimpleType() throws SyntaxException 
	{
		consume();
		return new SimpleType(this.token.kind);
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
	