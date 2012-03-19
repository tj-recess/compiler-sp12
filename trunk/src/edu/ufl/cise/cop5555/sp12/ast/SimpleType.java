package edu.ufl.cise.cop5555.sp12.ast;

import edu.ufl.cise.cop5555.sp12.Kind;

public class SimpleType extends Type
{

    public final Kind type;

    public SimpleType(Kind type)
    {
        this.type = type;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception
    {
        return v.visitSimpleType(this, arg);
    }

    @Override
    public boolean equals(Object anotherType)
    {
        if(anotherType instanceof SimpleType)
        {
            return this.type.equals(((SimpleType)anotherType).type);
        }
        return false;
    }

}
