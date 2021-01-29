package com.legend.semantic;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 函数类型的默认实现
 */
public class DefaultFunctionType implements FunctionType {

    protected String name;
    protected Scope enclosingScope = null;
    protected Type returnType = null;
    protected List<Type> paramTypes = new LinkedList<>();

    private static int nameIndex = 0;

    public DefaultFunctionType() {
        name = "FunctionType" + nameIndex++;
    }

    public void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public Type returnType() {
        return returnType;
    }

    @Override
    public List<Type> paramTypeList() {
        return paramTypes;
    }

    @Override
    public boolean matchParameterTypes(List<Type> paramTypes) {
        if (this.paramTypes.size() != paramTypes.size()) {
            return false;
        }
        boolean matched = true;
        for (int i = 0;i <  paramTypes.size();i++) {
            Type type1 = this.paramTypes.get(i);
            Type type2 = paramTypes.get(i);
            if (!type1.isType(type2)) {
                matched = false;
                break;
            }
        }
        return matched;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof FunctionType) {
            return isType(this, (FunctionType) type);
        }
        return false;
    }

    public static boolean isType(FunctionType type1, FunctionType type2) {
        if (type1 == type2) return true;
        if (!type1.returnType().isType(type2.returnType())) {
            return false;
        }
        List<Type> paramTypes1 = type1.paramTypeList();
        List<Type> paramTypes2 = type2.paramTypeList();
        if (paramTypes1.size() != paramTypes2.size()) {
            return false;
        }
        for (int i = 0;i < paramTypes1.size();i++) {
            if (!paramTypes1.get(i).isType(paramTypes2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type type : paramTypes) {
            sb.append(type).append(",");
        }
        sb.append(")").append(returnType);
        return sb.toString();
    }
}
