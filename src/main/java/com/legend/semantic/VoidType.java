package com.legend.semantic;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description void类型
 */
public final class VoidType implements Type {

    private VoidType() {}

    private static VoidType voidType = new VoidType();

    @Override
    public String name() {
        return "void";
    }

    @Override
    public boolean isType(Type type) {
        return this == type;
    }

    public static VoidType instance() {
        return voidType;
    }

    @Override
    public String toString() {
        return "void";
    }
}
