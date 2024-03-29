package edu.ufl.cise.cop5555.sp12.ast;

public class CompoundType extends Type
{

    public final SimpleType keyType;
    public final Type valType;

    public CompoundType(SimpleType keyType, Type valType)
    {
        this.keyType = keyType;
        this.valType = valType;
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception
    {
        return v.visitCompoundType(this, arg);
    }

    @Override
    public boolean equals(Object anotherType)
    {
        if(anotherType instanceof CompoundType)
        {
            CompoundType cType = (CompoundType) anotherType;
            return this.keyType.equals(cType.keyType) && this.valType.equals(cType.valType);
        }
        return false;
    }

}
