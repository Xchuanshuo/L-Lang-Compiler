package com.legend.semantic;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 数组类型
 */
public class ArrayType implements Type {

    private Type baseType;
    private int length;

    public void setBaseType(Type baseType) {
        this.baseType = baseType;
    }

    @Override
    public String name() {
        return baseType.name();
    }

    public Type baseType() {
        return baseType;
    }

    public boolean isMultiDimension() {
        if (baseType instanceof ArrayType) {
            return true;
        }
        return false;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof ArrayType) {
            return this.baseType.isType(((ArrayType) type).baseType);
        }
        return false;
    }
}
