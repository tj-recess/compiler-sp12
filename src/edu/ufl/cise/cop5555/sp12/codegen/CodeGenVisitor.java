package edu.ufl.cise.cop5555.sp12.codegen;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import edu.ufl.cise.cop5555.sp12.Kind;
import edu.ufl.cise.cop5555.sp12.TokenStream.Token;
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

public class CodeGenVisitor implements ASTVisitor, Opcodes {
	
	String className;
	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	FieldVisitor fv;
	MethodVisitor mv;
	AnnotationVisitor av0;
//	HashMap<String, String> variableTypeMap = new HashMap<String, String>();
	
	private String getTypeString(Type type)
	{
	    if(type instanceof SimpleType)
	    {
	        switch(((SimpleType)type).type)
	        {
	        case INT:
	            return "I";
	        case BOOLEAN:
	            return "Z";
	        case STRING:
	            return "Ljava/lang/String;";
	        }
	    }
	    return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		className = program.ident.getText();
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		//set label on first instruction of main method
		Label lstart = new Label();
		mv.visitLabel(lstart);
		//visit block to generate code and field declarations
		program.block.visit(this,null);
		//add return instruction
		mv.visitInsn(RETURN);
		Label lend= new Label();
		mv.visitLabel(lend);
		//visit local variable--the only one in our project is the String[] argument to the main method
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, lstart, lend, 0);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
		cw.visitEnd();
		//convert class file to byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//visit children
		for (DecOrCommand cd : block.decOrCommands) {
			cd.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
	    String type = (String)declaration.type.visit(this, null);
	    fv = cw.visitField(ACC_STATIC, declaration.ident.getText(), type, null, null);
        fv.visitEnd();
//        this.variableTypeMap.put(declaration.ident.getText(), type);
		return null;
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
	    return getTypeString(simpleType);
	}

	@Override
	public Object visitCompoundType(CompoundType compoundType, Object arg)
			throws Exception {
		// TODO P6
        throw new UnsupportedOperationException();
	}

	@Override
	public Object visitAssignExprCommand(AssignExprCommand assignExprCommand,
			Object arg) throws Exception {
	    assignExprCommand.expression.visit(this, arg);
	    Token token = (Token) assignExprCommand.lValue.visit(this, arg);
//	    mv.visitFieldInsn(PUTSTATIC, className, token.getText(), variableTypeMap.get(token.getText()));
	    mv.visitFieldInsn(PUTSTATIC, className, token.getText(), 
	            getTypeString(assignExprCommand.expression.expressionType));
		return null;
	}

	@Override
	public Object visitAssignPairListCommand(
			AssignPairListCommand assignPairListCommand, Object arg)
			throws Exception {
		// TODO P6
        throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPrintCommand(PrintCommand printCommand, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//		String type = (String)printCommand.expression.visit(this,arg);
		printCommand.expression.visit(this, arg);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", 
		        "(" + getTypeString(printCommand.expression.expressionType) + ")V");
		return null;
	}

	@Override
	public Object visitPrintlnCommand(PrintlnCommand printlnCommand, Object arg)
			throws Exception {
	    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        String type = (String)printlnCommand.expression.visit(this,arg);
	    printlnCommand.expression.visit(this,arg);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(" + getTypeString(printlnCommand.expression.expressionType) + ")V");
		return null;
	}

	@Override
	public Object visitDoCommand(DoCommand doCommand, Object arg)
			throws Exception {
        Label lStartBlock = new Label();
        Label lEndBlock = new Label();
        
        mv.visitLabel(lStartBlock);
        doCommand.expression.visit(this, arg);
        mv.visitJumpInsn(IFEQ, lEndBlock);
        //control here means condition in expression evaluated to true
        doCommand.block.visit(this, arg);
        mv.visitJumpInsn(GOTO, lStartBlock);
        mv.visitLabel(lEndBlock);
        //control here means loop ended
        return null;
	}

	@Override
	public Object visitDoEachCommand(DoEachCommand doEachCommand, Object arg)
			throws Exception {
		// TODO P6
        throw new UnsupportedOperationException();
	}

	@Override
	public Object visitIfCommand(IfCommand ifCommand, Object arg)
			throws Exception {
	    Label lStartBlock = new Label();
	    ifCommand.expression.visit(this, arg);
        mv.visitJumpInsn(IFEQ, lStartBlock);
	    ifCommand.block.visit(this, arg);
	    mv.visitLabel(lStartBlock);
		return null;
	}

	@Override
	public Object visitIfElseCommand(IfElseCommand ifElseCommand, Object arg)
			throws Exception {
	    Label lIfBlock = new Label();
	    ifElseCommand.expression.visit(this, arg);
	    //IFEQ pops the top int off the operand stack.
	    //If the int equals zero, execution branches to the address mentioned by label. 
	    //If the int on the stack does not equal zero, execution continues at the next instruction.
	    mv.visitJumpInsn(IFEQ, lIfBlock);
	    ifElseCommand.ifBlock.visit(this, arg);
	    Label lElseBlock = new Label();
	    //if control is here that means if block has been executed 
	    //therefore directly GO past else block.
	    mv.visitJumpInsn(GOTO, lElseBlock);
	    mv.visitLabel(lIfBlock);
	    ifElseCommand.elseBlock.visit(this, arg);
	    mv.visitLabel(lElseBlock);
	    
		return null;
	}

	@Override
	public Object visitSimpleLValue(SimpleLValue simpleLValue, Object arg)
			throws Exception {
	    return simpleLValue.identifier;
	}

	@Override
	public Object visitExprLValue(ExprLValue exprLValue, Object arg)
			throws Exception {
		// TODO P6
        throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPair(Pair pair, Object arg) throws Exception {
		// TODO P6
        throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPairList(PairList pairList, Object arg) throws Exception {
		// TODO P6
	    throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLValueExpression(LValueExpression lValueExpression,
			Object arg) throws Exception {
	    Token ident = (Token)lValueExpression.lValue.visit(this, arg);
	    String varName = ident.getText();
	    String varType = this.getTypeString(lValueExpression.expressionType);
	    mv.visitFieldInsn(GETSTATIC, className, varName, varType);
	    return varType;
	}

	@Override
	public Object visitIntegerLiteralExpression(
			IntegerLiteralExpression integerLiteralExpression, Object arg)
			throws Exception {
        // TO-DO use standard method getTypeString() instead of returning hard coded value
	    //gen code to leave value of literal on top of stack
		mv.visitLdcInsn(integerLiteralExpression.integerLiteral.getIntVal());
		return "I";
	}

	@Override
	public Object visitBooleanLiteralExpression(
			BooleanLiteralExpression booleanLiteralExpression, Object arg)
			throws Exception {
		// TO-DO use standard method getTypeString() instead of returning hard coded value
	    if(booleanLiteralExpression.booleanLiteral.getText().equals("false"))
	    {
	        mv.visitLdcInsn(new Boolean(false));
	    }
	    else
	    {
	        mv.visitLdcInsn(new Boolean(true));
	    }
		return "Z";
	}

	@Override
	public Object visitStringLiteralExpression(
			StringLiteralExpression stringLiteralExpression, Object arg)
			throws Exception {
	    //TO-DO use standard method getTypeString() instead of returning hard coded value
	    mv.visitLdcInsn(stringLiteralExpression.stringLiteral.getText());
		return "Ljava/lang/String;";
	}

	@Override
	public Object visitUnaryOpExpression(UnaryOpExpression unaryOpExpression,
			Object arg) throws Exception {
	    //first visit the expression which leaves its value on top
	    unaryOpExpression.expression.visit(this, arg);
	    if(unaryOpExpression.op == Kind.MINUS)
	    {
	        //INEG netages the value on top of stack
	        mv.visitInsn(INEG);
	    }
	    else if(unaryOpExpression.op == Kind.NOT)
	    {
	        Label l0 = new Label();
	        //jump to label l0 if value on top of stack is 0
	        mv.visitJumpInsn(IFEQ, l0);
	        //control here means value on top of stack was 1
	        //push 0 onto stack
	        mv.visitInsn(ICONST_0);
	        Label l1 = new Label();
	        mv.visitJumpInsn(GOTO, l1);
	        
	        mv.visitLabel(l0);
	        //control here means 0 was on top of stack, so push 1 now
	        mv.visitInsn(ICONST_1);
	        
	        mv.visitLabel(l1);	        
	    }
		return null;
	}

	@Override
	public Object visitBinaryOpExpression(
			BinaryOpExpression binaryOpExpression, Object arg) throws Exception {
        
        SimpleType expr0Type = (SimpleType) binaryOpExpression.expression0.expressionType;
        SimpleType expr1Type = (SimpleType) binaryOpExpression.expression1.expressionType;
        
	    if(expr0Type.type == Kind.INT && expr1Type.type == Kind.INT)
	    {
	        binaryOpExpression.expression0.visit(this, arg);
	        binaryOpExpression.expression1.visit(this, arg);
	        
	        switch(binaryOpExpression.op)
	        {
	        case PLUS:
	            mv.visitInsn(IADD);
	            break;
	        case MINUS:
	            mv.visitInsn(ISUB);
	            break;
	        case TIMES:
	            mv.visitInsn(IMUL);
	            break;
	        case DIVIDE:
	            mv.visitInsn(IDIV);
	            break;
	        case EQUALS:
	            Label eqL0 = new Label();
	            Label eqL1 = new Label();
	            mv.visitJumpInsn(IF_ICMPEQ, eqL0);
	            //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
	            mv.visitInsn(ICONST_0);
	            mv.visitJumpInsn(GOTO, eqL1);
	            
	            mv.visitLabel(eqL0);
	            //control here means expr0 and expr1 were equal
	            // therefore we should push true to stack
	            mv.visitInsn(ICONST_1);

	            mv.visitLabel(eqL1);
	            break;
	        case NOT_EQUALS:
	            Label neL0 = new Label();
                Label neL1 = new Label();
                mv.visitJumpInsn(IF_ICMPEQ, neL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, neL1);
                
                mv.visitLabel(neL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_0);

                mv.visitLabel(neL1);
	            break;
	        case LESS_THAN:
	            Label ltL0 = new Label();
                Label ltL1 = new Label();
                mv.visitJumpInsn(IF_ICMPLT, ltL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, ltL1);
                
                mv.visitLabel(ltL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(ltL1);
	            break;
	        case AT_MOST:
	            Label leL0 = new Label();
                Label leL1 = new Label();
                mv.visitJumpInsn(IF_ICMPLE, leL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, leL1);
                
                mv.visitLabel(leL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(leL1);
	            break;
	        case GREATER_THAN:
	            Label gtL0 = new Label();
                Label gtL1 = new Label();
                mv.visitJumpInsn(IF_ICMPGT, gtL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, gtL1);
                
                mv.visitLabel(gtL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(gtL1);
	            break;
	        case AT_LEAST:
	            Label geL0 = new Label();
                Label geL1 = new Label();
                mv.visitJumpInsn(IF_ICMPGE, geL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, geL1);
                
                mv.visitLabel(geL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(geL1);
	            break;
            default:
	            throw new Exception("Unknown operand with in binaryOpExpr : " + binaryOpExpression.op);
	        }
	    }
	    else if(expr0Type.type == Kind.BOOLEAN && expr1Type.type == Kind.BOOLEAN)
	    {
	        switch(binaryOpExpression.op)
            {
	        case AND:
	            //logic : even if one operand is false, result is false
	            Label andL1 = new Label();
	            Label andL2 = new Label();
	            
	            //first load one expression on stack and evaluate it
	            binaryOpExpression.expression0.visit(this, arg);
	            mv.visitJumpInsn(IFEQ, andL1);
	            //control here means first operand is true;
	            
	            //now visit second expression on stack and evaluate it
	            binaryOpExpression.expression1.visit(this, arg);
	            mv.visitJumpInsn(IFEQ, andL1);
	            //control here means second operand is also true
	            //load true on stack
	            mv.visitInsn(ICONST_1);
	            //move to the end of block
	            mv.visitJumpInsn(GOTO, andL2);
	            
	            mv.visitLabel(andL1);
	            //control here means either of operand is false
	            //load false on stack
	            mv.visitInsn(ICONST_0);
	            
	            mv.visitLabel(andL2);
                break;
            case OR:
                //logic : even if one operand is false, result is false
                Label orL1 = new Label();
                Label orL2 = new Label();
                
                //first load one expression on stack and evaluate it
                binaryOpExpression.expression0.visit(this, arg);
                mv.visitJumpInsn(IFNE, orL1);
                //control here means first operand is true;

                //now visit second expression on stack and evaluate it
                binaryOpExpression.expression1.visit(this, arg);
                mv.visitJumpInsn(IFNE, orL1);
                //control here means second operand is also true
                //load true on stack
                mv.visitInsn(ICONST_0);
                //move to the end of block
                mv.visitJumpInsn(GOTO, orL2);
                
                mv.visitLabel(orL1);
                //control here means either of operand is false
                //load false on stack
                mv.visitInsn(ICONST_1);
                
                mv.visitLabel(orL2);
                break;
            case EQUALS:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);
                
                Label eqL0 = new Label();
                Label eqL1 = new Label();
                mv.visitJumpInsn(IF_ICMPEQ, eqL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, eqL1);
                
                mv.visitLabel(eqL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(eqL1);
                break;
            case NOT_EQUALS:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);

                Label neL0 = new Label();
                Label neL1 = new Label();
                mv.visitJumpInsn(IF_ICMPEQ, neL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, neL1);
                
                mv.visitLabel(neL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_0);

                mv.visitLabel(neL1);
                break;
            case LESS_THAN:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);

                Label ltL0 = new Label();
                Label ltL1 = new Label();
                mv.visitJumpInsn(IF_ICMPLT, ltL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, ltL1);
                
                mv.visitLabel(ltL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(ltL1);
                break;
            case AT_MOST:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);

                Label leL0 = new Label();
                Label leL1 = new Label();
                mv.visitJumpInsn(IF_ICMPLE, leL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, leL1);
                
                mv.visitLabel(leL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(leL1);
                break;
            case GREATER_THAN:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);

                Label gtL0 = new Label();
                Label gtL1 = new Label();
                mv.visitJumpInsn(IF_ICMPGT, gtL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, gtL1);
                
                mv.visitLabel(gtL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(gtL1);
                break;
            case AT_LEAST:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);

                Label geL0 = new Label();
                Label geL1 = new Label();
                mv.visitJumpInsn(IF_ICMPGE, geL0);
                //control here means expr0 and expr1 were NOT equal
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, geL1);
                
                mv.visitLabel(geL0);
                //control here means expr0 and expr1 were equal
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);

                mv.visitLabel(geL1);
                break;
            default:
                throw new Exception("Unknown operand with in binaryOpExpr : " + binaryOpExpression.op);
            }
	    }
	    else if(expr0Type.type == Kind.STRING || expr1Type.type == Kind.STRING)
        {            
            switch(binaryOpExpression.op)
            {
            case PLUS:
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                binaryOpExpression.expression0.visit(this, arg);
                String type = getTypeString(expr0Type);
                if(expr0Type.type == Kind.STRING)
                {
                    type = "Ljava/lang/Object;";
                }
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + type + ")Ljava/lang/String;");
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                
                binaryOpExpression.expression1.visit(this, arg);             
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + getTypeString(expr1Type) + ")Ljava/lang/StringBuilder;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                
                break;
            case EQUALS:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);
                
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                break;
            case NOT_EQUALS:
                binaryOpExpression.expression0.visit(this, arg);
                binaryOpExpression.expression1.visit(this, arg);
                
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                
                Label neL0 = new Label();
                Label neL1 = new Label();
                mv.visitJumpInsn(IFNE, neL0);
                //control here means top of stack was 0
                // therefore we should push true to stack
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, neL1);
                
                mv.visitLabel(neL0);
                //control here means top of stack was 1
                // therefore we should push false to stack
                mv.visitInsn(ICONST_0);

                mv.visitLabel(neL1);
                break;
            case AT_MOST:

                //the order in which we push is very important
                binaryOpExpression.expression1.visit(this, arg);
                binaryOpExpression.expression0.visit(this, arg);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z");

                break;
            default:
                throw new Exception("Unknown operand with in binaryOpExpr : " + binaryOpExpression.op);
            }
        }
		return null;
	}
}
