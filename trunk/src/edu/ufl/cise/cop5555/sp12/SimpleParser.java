package edu.ufl.cise.cop5555.sp12;

import java.util.LinkedList;
import java.util.List;

import edu.ufl.cise.cop5555.sp12.TokenStream;
import edu.ufl.cise.cop5555.sp12.TokenStream.Token;
import edu.ufl.cise.cop5555.sp12.ast.*;

public class SimpleParser
{

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

    private Token consume()
    {
        Token returnToken = this.token;
        token = stream.getToken(index++);
        return returnToken;
    }

    public AST parse() throws SyntaxException
    {
        return Program(); // method corresponding to start symbol
    }

    private Program Program() throws SyntaxException
    {
        match(Kind.PROG);
        Token aToken = match(Kind.IDENTIFIER);
        Block aBlock = Block();
        match(Kind.GORP);
        match(Kind.EOF);
        return new Program(aToken, aBlock);
    }

    private Block Block() throws SyntaxException
    {
        List<DecOrCommand> decOrCmd = new LinkedList<DecOrCommand>();
        do
        {
            if (predict(NonTerminal.Declaration))
            {
                decOrCmd.add(Declaration());
            }
            else if (predict(NonTerminal.Command))
            {
                DecOrCommand aDecOrCmd = Command();
                if (aDecOrCmd != null)
                {
                    decOrCmd.add(aDecOrCmd);
                }
            }
            else
            {
                break;
            }
            match(Kind.SEMI);
        } while (true);
        return new Block(decOrCmd);
    }

    private DecOrCommand Command() throws SyntaxException
    {
        Command cmd = null;
        if (predict(NonTerminal.LValue))
        {
            LValue anLValue = LValue();
            match(Kind.ASSIGN);
            if (predict(NonTerminal.Expression))
            {
                Expression anExpr = Expression();
                cmd = new AssignExprCommand(anLValue, anExpr);
            }
            else if (predict(NonTerminal.PairList))
            {
                PairList aPairList = PairList();
                cmd = new AssignPairListCommand(anLValue, aPairList);
            }
            else
            {
                throw new SyntaxException(token, "unexpected token");
            }
        }
        else if (isKind(Kind.PRINT))
        {
            consume();
            Expression expr = Expression();
            cmd = new PrintCommand(expr);
        }
        else if (isKind(Kind.PRINTLN))
        {
            consume();
            Expression expr = Expression();
            cmd = new PrintlnCommand(expr);
        }
        else if (isKind(Kind.DO))
        {

            consume();
            if (isKind(Kind.LEFT_PAREN))
            {
                consume();
                Expression anExpr = Expression();
                match(Kind.RIGHT_PAREN);
                Block aBlock = Block();
                match(Kind.OD);
                cmd = new DoCommand(anExpr, aBlock);
            }
            else
            {
                LValue anLValue = LValue();
                match(Kind.COLON);
                match(Kind.LEFT_SQUARE);
                Token key = match(Kind.IDENTIFIER);
                match(Kind.COMMA);
                Token value = match(Kind.IDENTIFIER);
                match(Kind.RIGHT_SQUARE);
                Block aBlock = Block();
                match(Kind.OD);
                cmd = new DoEachCommand(anLValue, key, value, aBlock);
            }
        }
        else if (isKind(Kind.IF))
        {
            consume();
            match(Kind.LEFT_PAREN);
            Expression anExpr = Expression();
            match(Kind.RIGHT_PAREN);
            Block ifBlock = Block();
            Block elseBlock = null;
            if (isKind(Kind.ELSE))
            {
                consume();
                elseBlock = Block();
            }
            match(Kind.FI);
            if (elseBlock == null)
            {
                cmd = new IfCommand(anExpr, ifBlock);
            }
            else
            {
                cmd = new IfElseCommand(anExpr, ifBlock, elseBlock);
            }
        }
        // else ε is valid
        return cmd;
    }

    private PairList PairList() throws SyntaxException
    {
        List<Pair> pairList = new LinkedList<Pair>();
        match(Kind.LEFT_BRACE);
        if (predict(NonTerminal.Pair))
        {
            pairList.add(Pair());
            while (isKind(Kind.COMMA))
            {
                consume(); // consume comma
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
        Expression expr0 = null;
        Expression expr1 = null;
        Kind operatorKind = null;

        if(predict(NonTerminal.Term))
        {
            expr0 = Term();
        }
        while(predict(NonTerminal.RelOp))
        {
            Token aToken = RelOp();
            operatorKind = aToken.kind;
            expr1 = Term();
            expr0 = new BinaryOpExpression(expr0, operatorKind, expr1);
        }
        return expr0;
    }

    private Token RelOp()
    {
        return consume();
    }

    private Expression Term() throws SyntaxException
    {
        Expression expr0 = null;
        Kind opKind = null;
        Expression expr1 = null;
        if (predict(NonTerminal.Element))
        {
            expr0 = Elem();
        }
        while (predict(NonTerminal.WeakOp))
        {
            Token aToken = WeakOp();
            opKind = aToken.kind;
            expr1 = Elem();
            expr0 = new BinaryOpExpression(expr0, opKind, expr1);
        }
        return expr0;
    }

    private Token WeakOp()
    {
        return consume();
    }

    private Expression Elem() throws SyntaxException
    {
        Expression expr0 = null;
        Kind opKind = null;
        Expression expr1 = null;
        if(predict(NonTerminal.Factor))
        {
            expr0 = Factor();
        }
        while(predict(NonTerminal.StrongOp))
        {
            Token aToken = StrongOp();
            opKind = aToken.kind;
            expr1 = Factor();
            expr0 = new BinaryOpExpression(expr0, opKind, expr1);
        }
        return expr0;
    }

    private Token StrongOp()
    {
        return consume();
    }

    private Expression Factor() throws SyntaxException
    {
        if (predict(NonTerminal.LValue))
        {
            LValue anLValue = LValue();
            return new LValueExpression(anLValue);
        }
        else if (isKind(Kind.INTEGER_LITERAL))
        {
            return new IntegerLiteralExpression(consume());
        }
        else if (isKind(Kind.BOOLEAN_LITERAL))
        {
            return new BooleanLiteralExpression(consume());
        }
        else if (isKind(Kind.STRING_LITERAL))
        {
            return new StringLiteralExpression(consume());
        }
        else if (isKind(Kind.LEFT_PAREN))
        {
            consume();
            Expression anExpr = Expression();
            match(Kind.RIGHT_PAREN);
            return anExpr;
        }
        else if (isKind(Kind.NOT))
        {
            consume();
            Expression anExpr = Factor();
            return new UnaryOpExpression(Kind.NOT, anExpr);
        }
        else if (isKind(Kind.MINUS))
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
        Token aToken = match(Kind.IDENTIFIER);
        if (isKind(Kind.LEFT_SQUARE))
        {
            aToken = consume();
            Expression anExpr = Expression();
            match(Kind.RIGHT_SQUARE);
            return new ExprLValue(aToken, anExpr);
        }
        return new SimpleLValue(aToken);
    }

    private DecOrCommand Declaration() throws SyntaxException
    {
        // control here means predict(Declaration) already returned true
        // i.e. type must have been matched already
        Type aType = Type();
        Token aToken = match(Kind.IDENTIFIER);
        return new Declaration(aType, aToken);
    }

    private Type Type() throws SyntaxException
    {
        Type aType = null;
        if (predict(NonTerminal.SimpleType))
        {
            aType = SimpleType();
        }
        else if (predict(NonTerminal.CompoundType))
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
        Token aToken = consume();
        return new SimpleType(aToken.kind);
    }

    private boolean isKind(Kind kind)
    {
        return kind == token.kind;
    }

    private Token match(Kind kind) throws SyntaxException
    {
        if (isKind(kind))
        {
            return consume();
        }
        else
        {
            throw new SyntaxException(this.token, "expected " + kind);
        }
    }

    private boolean predict(NonTerminal x)
    {
        switch (x)
        {
        case Block:
            return predict(NonTerminal.Declaration)
                    || predict(NonTerminal.Command);
        case Declaration:
            return predict(NonTerminal.Type);
        case Type:
            return predict(NonTerminal.SimpleType)
                    || predict(NonTerminal.CompoundType);
        case SimpleType:
            return isKind(Kind.INT) || isKind(Kind.BOOLEAN)
                    || isKind(Kind.STRING);
        case CompoundType:
            return isKind(Kind.MAP);
        case Command:
            return predict(NonTerminal.LValue) || isKind(Kind.PRINT)
                    || isKind(Kind.PRINTLN) || isKind(Kind.DO)
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
            return isKind(Kind.IDENTIFIER) || isKind(Kind.INTEGER_LITERAL)
                    || isKind(Kind.BOOLEAN_LITERAL)
                    || isKind(Kind.STRING_LITERAL) || isKind(Kind.LEFT_PAREN)
                    || isKind(Kind.NOT) || isKind(Kind.MINUS);
        case Pair:
            return isKind(Kind.LEFT_SQUARE);
        case PairList:
            return isKind(Kind.LEFT_BRACE);
        case RelOp:
            return isKind(Kind.OR) || isKind(Kind.AND) || isKind(Kind.EQUALS)
                    || isKind(Kind.NOT_EQUALS) || isKind(Kind.LESS_THAN)
                    || isKind(Kind.GREATER_THAN) || isKind(Kind.AT_MOST)
                    || isKind(Kind.AT_LEAST);
        case WeakOp:
            return isKind(Kind.PLUS) || isKind(Kind.MINUS);
        case StrongOp:
            return isKind(Kind.TIMES) || isKind(Kind.DIVIDE);
        }
        return false;
    }
}
