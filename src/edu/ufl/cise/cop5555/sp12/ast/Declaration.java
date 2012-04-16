package edu.ufl.cise.cop5555.sp12.ast;

import edu.ufl.cise.cop5555.sp12.TokenStream.Token;

public class Declaration extends DecOrCommand
{
    public final Type type;
    public final Token ident;
    private int scopeNum;

    public Declaration(Type type, Token ident)
    {
        this.type = type;
        this.ident = ident;
    }
    
    public Declaration(Type type, Token ident, int scopeNum)
    {
        this.type = type;
        this.ident = ident;
        this.scopeNum = scopeNum;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception
    {
        return v.visitDeclaration(this, arg);
    }
    
    public int getScopeNum()
    {
        return this.scopeNum;
    }

    public void setScopeNum(int scopeNum)
    {
        this.scopeNum = scopeNum;
        this.ident.setScope(scopeNum);
    }
}
