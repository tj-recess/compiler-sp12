package edu.ufl.cise.cop5555.sp12.context;

import edu.ufl.cise.cop5555.sp12.Kind;
import edu.ufl.cise.cop5555.sp12.TokenStream.Token;
import edu.ufl.cise.cop5555.sp12.ast.AST;
import edu.ufl.cise.cop5555.sp12.ast.ASTVisitor;
import edu.ufl.cise.cop5555.sp12.ast.AssignExprCommand;
import edu.ufl.cise.cop5555.sp12.ast.AssignPairListCommand;
import edu.ufl.cise.cop5555.sp12.ast.BinaryOpExpression;
import edu.ufl.cise.cop5555.sp12.ast.Block;
import edu.ufl.cise.cop5555.sp12.ast.BooleanLiteralExpression;
import edu.ufl.cise.cop5555.sp12.ast.CompoundType;
import edu.ufl.cise.cop5555.sp12.ast.DecOrCommand;
import edu.ufl.cise.cop5555.sp12.ast.Declaration;
import edu.ufl.cise.cop5555.sp12.ast.DoCommand;
import edu.ufl.cise.cop5555.sp12.ast.DoEachCommand;
import edu.ufl.cise.cop5555.sp12.ast.ExprLValue;
import edu.ufl.cise.cop5555.sp12.ast.IfCommand;
import edu.ufl.cise.cop5555.sp12.ast.IfElseCommand;
import edu.ufl.cise.cop5555.sp12.ast.IntegerLiteralExpression;
import edu.ufl.cise.cop5555.sp12.ast.LValueExpression;
import edu.ufl.cise.cop5555.sp12.ast.Pair;
import edu.ufl.cise.cop5555.sp12.ast.PairList;
import edu.ufl.cise.cop5555.sp12.ast.PrintCommand;
import edu.ufl.cise.cop5555.sp12.ast.PrintlnCommand;
import edu.ufl.cise.cop5555.sp12.ast.Program;
import edu.ufl.cise.cop5555.sp12.ast.SimpleLValue;
import edu.ufl.cise.cop5555.sp12.ast.SimpleType;
import edu.ufl.cise.cop5555.sp12.ast.StringLiteralExpression;
import edu.ufl.cise.cop5555.sp12.ast.Type;
import edu.ufl.cise.cop5555.sp12.ast.UnaryOpExpression;

public class ContextCheckVisitor implements ASTVisitor
{
    private Token programName = null;
    private SymbolTable symbolTable = new SymbolTable();
    private static final Type stringType = new SimpleType(Kind.STRING);
    private static final Type booleanType = new SimpleType(Kind.BOOLEAN);
    private static final Type intType = new SimpleType(Kind.INT);

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception
    {
        this.programName = program.ident;
        program.block.visit(this, arg);
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception
    {
        this.symbolTable.enterScope();
        
        //visit all decOrCommand
        for (DecOrCommand decOrCommand : block.decOrCommands)
        {
            decOrCommand.visit(this, arg);
        }
        
        this.symbolTable.exitScope();
        return null;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg)
            throws Exception
    {
        check(!declaration.ident.equals(this.programName), declaration, "Identifier name is same as program name");
        this.symbolTable.insert(declaration.ident.getText(), declaration);
        return null;
    }

    @Override
    public Object visitSimpleType(SimpleType simpleType, Object arg)
            throws Exception
    {
        check(simpleType.type.equals(Kind.INT) || simpleType.type.equals(Kind.BOOLEAN)
                || simpleType.type.equals(Kind.STRING),
                simpleType, "Simple Type is something other than Int, Boolean or String");
        return null;
    }

    @Override
    public Object visitCompoundType(CompoundType compoundType, Object arg)
            throws Exception
    {
        compoundType.keyType.visit(this, arg);
        compoundType.valType.visit(this, arg);
        return compoundType;
    }

    @Override
    public Object visitAssignExprCommand(AssignExprCommand assignExprCommand,
            Object arg) throws Exception
    {
        Type lhsType = (Type) assignExprCommand.lValue.visit(this, arg);
        Type rhsType = (Type) assignExprCommand.expression.visit(this, arg);
        check(lhsType.equals(rhsType), assignExprCommand, "Incompatible types in assignment");
        return null;
    }

    /**
     * <pre>
     * AssignPairListCommand ::= LValue PairList
     * Condition
     * let LValue.type = (keyType,valType) and
     * PairList.type = (pairKeyType,valKeyType) in
     * keyType == pairKeyType &&
     * valType == pairValType
     * Example:
     * if m = {[a,b]}, then w must have
     * m.keyType == type of a &&
     * m.valType == type of b
     *</pre>
     */
    @Override
    public Object visitAssignPairListCommand(
            AssignPairListCommand assignPairListCommand, Object arg)
            throws Exception
    {
        Type lvalueType = (Type)assignPairListCommand.lValue.visit(this, arg);
        Type pairListType = (Type) assignPairListCommand.pairList.visit(this, arg);
        
        check(lvalueType instanceof CompoundType, assignPairListCommand, 
                "assignPairListCommand.lvalue is not of CompoundType");
        check(pairListType instanceof CompoundType, assignPairListCommand, 
                "assignPairListCommand.lvalue is not of CompoundType");
        CompoundType lvalueCType = (CompoundType) lvalueType;
        CompoundType pairListCType = (CompoundType) pairListType;
        
        check(lvalueCType.keyType.equals(pairListCType.keyType), assignPairListCommand, 
                "lvalue.keyType != pairList.keyType");
        check(lvalueCType.valType.equals(pairListCType.valType), assignPairListCommand, 
        "lvalue.valType != pairList.valType");        
        
        return null;
    }

    @Override
    public Object visitPrintCommand(PrintCommand printCommand, Object arg)
            throws Exception
    {
        return null;
    }

    @Override
    public Object visitPrintlnCommand(PrintlnCommand printlnCommand, Object arg)
            throws Exception
    {
        return null;
    }

    /**
     * <pre>
     * DoCommand ::= Expression Block
     * Condition: Expression.type == boolean
     * </pre>
     */
    @Override
    public Object visitDoCommand(DoCommand doCommand, Object arg)
            throws Exception
    {
        Type expressionType = (Type) doCommand.expression.visit(this, arg);
        check(expressionType instanceof SimpleType &&  ((SimpleType)expressionType).type.equals(Kind.BOOLEAN),
                doCommand, "Expression in doCommand is not of boolean type");
        return null;
    }

    /**
     * <pre>
     * DoEachCommand ::= LValue IDENTIFIER0 IDENTIFIER1 Block
     * Condition:
     * let LValue.type = (keyType,valType)
     * in
     * Identifier0t.type == keyType &&
     * Identifier1.type == valType
     * 
     * Example:
     * do x:[a,b] ....od
     * x must be defined with a map type, say (t1,t2)
     * Then a must be defined and have type t1, and b must be
     * defined with type t2.
     * </pre>
     */
    @Override
    public Object visitDoEachCommand(DoEachCommand doEachCommand, Object arg)
            throws Exception
    {
        Type lvalueType = (Type)doEachCommand.lValue.visit(this, arg);
        check(lvalueType instanceof CompoundType, doEachCommand, "LvalueType != CompoundType");
        CompoundType lvalueCType = (CompoundType)lvalueType;
        check(lvalueCType.keyType.type.equals(doEachCommand.key.kind), doEachCommand, " Identifier0t.type != keyType");
        check(lvalueCType.valType.equals(doEachCommand.val.kind), doEachCommand, " Identifier0t.type != keyType");
        //TODO : this is wrong/incomplete.
        return null;
    }

    @Override
    public Object visitIfCommand(IfCommand ifCommand, Object arg)
            throws Exception
    {
        Type expressionType = (Type)ifCommand.expression.visit(this, arg);
        check(expressionType.equals(booleanType), ifCommand, "expression is not of boolean type");
        return null;
    }

    @Override
    public Object visitIfElseCommand(IfElseCommand ifElseCommand, Object arg)
            throws Exception
    {
        Type expressionType = (Type)ifElseCommand.expression.visit(this, arg);
        check(expressionType.equals(booleanType), ifElseCommand, "expression is not of boolean type");
        return null;
    }

    /**
     * Condition: IDENTIFIER ∈ symbol table and defined in
     * current scope.
     * SimpleLValue.type ≔ IDENTIFIER.type where the
     * type of the IDENTIFIER is obtained from the symbol table
     */
    @Override
    public Object visitSimpleLValue(SimpleLValue simpleLValue, Object arg)
            throws Exception
    {
        Declaration dec = this.symbolTable.lookup(simpleLValue.identifier.getStringVal());
        check(dec != null && dec.type.equals(new SimpleType(simpleLValue.identifier.kind)),
                simpleLValue, "Either Identifier's declaration is not present in symbol table OR type of simpleLValue " +
                		"is not same as type of declaration");
        return dec.type;
    }

    /**
     * <p>Condition:
     * IDENTIFIER ∈ symbol table and defined in current scope.<br>
     * Condition:
     * let IDENTIFIER.type = (keyType,valType) in keyType == Expression.type<br>
     * ExprLValue.type ≔ valType</p>
     * <p>Example:<br>
     * m[e]<br>
     * m is declared to be map of type (keyType, valType) <br>
     * e must have the same type as keyType.<br>
     * The type of m[e] is valType</p>
     */
    @Override
    public Object visitExprLValue(ExprLValue exprLValue, Object arg)
            throws Exception
    {
        Declaration dec = this.symbolTable.lookup(exprLValue.identifier.getStringVal());
        check(dec.type instanceof CompoundType, dec, "Declaration is not of compound type");
        CompoundType cType = (CompoundType) dec.type;   //dec.type is guaranteed to be of compound type
        Type expressionType = (Type)exprLValue.expression.visit(this, arg);
        check(cType.keyType.equals(expressionType), exprLValue.expression, 
                "keyType in Identifier's declaration is not same as exprLValue.expression's type");
        Type exprLvalType = (Type) exprLValue.visit(this, arg);
        check(cType.valType.equals(exprLvalType), exprLValue, 
                "valType in Identifier's declaration is not same as exprLValue's type");
        return cType.valType;
    }

    @Override
    public Object visitPair(Pair pair, Object arg) throws Exception
    {
        return null;
    }

    @Override
    public Object visitPairList(PairList pairList, Object arg) throws Exception
    {
        Pair lastMatched = null;
        for(Pair aPair : pairList.pairs)
        {
            if(lastMatched != null)
            {
                Type lastMatchedExpr0Type = (Type) lastMatched.expression0.visit(this, arg);
                Type lastMatchedExpr1Type = (Type) lastMatched.expression1.visit(this, arg);
                Type aPairExpr0Type = (Type) aPair.expression0.visit(this, arg);
                Type aPairExpr1Type = (Type) aPair.expression1.visit(this, arg);
                check(lastMatchedExpr0Type.equals(aPairExpr0Type) && lastMatchedExpr1Type.equals(aPairExpr1Type)
                        , pairList, "All pairs in pairlist are not of same type");
            }
            lastMatched = aPair;
        }
        return null;
    }

    @Override
    public Object visitLValueExpression(LValueExpression lValueExpression, Object arg) throws Exception
    {
        return lValueExpression.lValue.visit(this, arg);
    }

    @Override
    public Object visitIntegerLiteralExpression(
            IntegerLiteralExpression integerLiteralExpression, Object arg)
            throws Exception
    {
        check(integerLiteralExpression.integerLiteral.kind.equals(Kind.INT), integerLiteralExpression,
        "Type of integerLiteral is not equal to Int");
        return new SimpleType(Kind.INT);
    }

    @Override
    public Object visitBooleanLiteralExpression(
            BooleanLiteralExpression booleanLiteralExpression, Object arg)
            throws Exception
    {
        check(booleanLiteralExpression.booleanLiteral.kind.equals(Kind.BOOLEAN), booleanLiteralExpression,
        "Type of booleanLiteral is not equal to Boolean");
        return new SimpleType(Kind.BOOLEAN);
    }

    @Override
    public Object visitStringLiteralExpression(
            StringLiteralExpression stringLiteralExpression, Object arg)
            throws Exception
    {
        check(stringLiteralExpression.stringLiteral.kind.equals(Kind.STRING), stringLiteralExpression,
                "Type of stringLiteral is not equal to String");
        return new SimpleType(Kind.STRING);
    }

    /**
     * <pre>
     * Condition: op == - or op == ! &&
     * if op = - then Expression.type = int &&
     * if op = ! then Expression.type = boolean
     * </pre>
     */
    @Override
    public Object visitUnaryOpExpression(UnaryOpExpression unaryOpExpression,
            Object arg) throws Exception
    {
        Type unaryOpType = (Type)unaryOpExpression.expression.visit(this, arg);
        check(unaryOpType instanceof SimpleType, unaryOpExpression.expression, "unaryOpType is not a simple type");
        SimpleType s = (SimpleType)unaryOpType;
        check((s.type.equals(Kind.INT) && unaryOpExpression.op.equals(Kind.MINUS))
                || (s.type.equals(Kind.BOOLEAN) && unaryOpExpression.op.equals(Kind.BOOLEAN)),
                unaryOpExpression, "operator is not compatible with type (- with Int or ! with boolean");
        return unaryOpType;
    }

    /**
     * <pre>
     * Rules for BinaryOpExpressions
1. <Expression0>.type == <Expression1>.type unless the op is a + and one of them
is a string and the other an int or boolean.
2. + can be applied to all types except boolean. The type is the type of the result. If
one of the arguments is a string, then the result is a string.
3. ==, !=, >, <, ≤, ≥ apply to any type and the result is boolean
4. * and - can be applied to integers and maps. The result is the same as the
argument type
5. / can be applied to integers, the result is the same as the argument type.
6. & and | can be applied to boolean types, the result is a boolean.
     */
    @Override
    public Object visitBinaryOpExpression(
            BinaryOpExpression binaryOpExpression, Object arg) throws Exception
    {
        Type expr0Type = (Type)binaryOpExpression.expression0.visit(this, arg);
        Type expr1Type = (Type)binaryOpExpression.expression1.visit(this, arg);
        
        Type resultType = null;
        boolean bothTypesAreSame = expr0Type.equals(expr1Type);
        boolean bothTypesAreBoolean = expr0Type.equals(booleanType) && expr1Type.equals(booleanType);
        boolean oneStringAndOtherIntOrBoolean = 
                    (expr0Type.equals(stringType) && (expr1Type.equals(intType) || expr1Type.equals(booleanType)))
                || (expr1Type.equals(stringType) && (expr0Type.equals(intType) || expr0Type.equals(booleanType)));
        
        if(binaryOpExpression.op.equals(Kind.PLUS))
        {
            if(bothTypesAreSame && !bothTypesAreBoolean)
            {
                resultType = expr0Type;
            }
            else if(oneStringAndOtherIntOrBoolean)
            {
                resultType = stringType;
            }
            else
            {
                //throw exception
                check(false, binaryOpExpression, "Operator is '+' AND\n" + 
                    "Either both types are not same or both are boolean\n" +
                    "OR\n" +
                    "!(One type is String and other type is Int or Boolean)");
            }
        }
        else
        {
            check(bothTypesAreSame, binaryOpExpression, "Operator is not '+' AND both types are not same");
            Kind op = binaryOpExpression.op;
            if(op.equals(Kind.EQUALS) || op.equals(Kind.NOT_EQUALS) || op.equals(Kind.LESS_THAN)
                    || op.equals(Kind.GREATER_THAN) || op.equals(Kind.AT_LEAST) || op.equals(Kind.AT_MOST))
            {
                resultType = booleanType;
            }
            else if(op.equals(Kind.TIMES) || op.equals(Kind.MINUS))
            {
                check(expr0Type.equals(intType) || expr0Type instanceof CompoundType, 
                        binaryOpExpression, "Operator is '*' or '-' and expression type is" +
                        		"different than map or int type");
                resultType = expr0Type;
            }
            else if(op.equals(Kind.DIVIDE))
            {
                check(expr0Type.equals(intType), 
                        binaryOpExpression, "Operator is '/' and expression type is not Int");
                resultType = expr0Type;
            }
            else if(op.equals(Kind.AND) || op.equals(Kind.OR))
            {
                check(expr0Type.equals(booleanType), binaryOpExpression, "Operator is & or | AND " +
                		"expression type is not boolean");
                resultType = expr0Type;
            }
            else
            {
                check(false, binaryOpExpression, "Something went terribly wrong - programming logic error");
            }
        }
        return resultType;
    }

    private void check(boolean equals, AST node, String message) throws ContextException
    {
        if(!equals)
        {
            throw new ContextException(node, message);
        }
    }

}
