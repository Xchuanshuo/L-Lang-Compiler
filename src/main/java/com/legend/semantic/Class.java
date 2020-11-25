package com.legend.semantic;

import com.legend.parser.ast.ASTNode;
import com.legend.semantic.Variable.Super;
import com.legend.semantic.Variable.This;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 类类型
 */
public class Class extends Scope implements Type {

    private Class parentClass = null;
    private This thisRef = null;
    private Super superRef = null;

    private DefaultConstructor defaultConstructor;

    public Class(String name, ASTNode astNode) {
        this.name = name;
        this.astNode = astNode;
        thisRef = new This(this, astNode);
        thisRef.setType(this);
    }

    public Class getParentClass() {
        return parentClass;
    }

    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
        superRef = new Super(parentClass, astNode);
        superRef.setType(parentClass);
    }

    private static Class rootClass = new Class("Object", null);

    public This getThis() {
        return thisRef;
    }

    public Super getSuper() {
        return superRef;
    }

    @Override
    public String toString() {
        return "Class " + name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Variable getVariable(String name) {
        Variable rtn = super.getVariable(name);
        if (rtn == null && parentClass != null) {
            // todo 是否检查visibility
            rtn = parentClass.getVariable(name);
        }
        return rtn;
    }

    @Override
    public Class getClass(String name) {
        Class rtn = super.getClass(name);
        if (rtn == null && parentClass != null) {
            // todo 是否要检查visibility
            rtn = parentClass.getClass(name);
        }
        return rtn;
    }

    // 查找构造函数
    public Function findConstructor(List<Type> paramTypes) {
        return super.getFunction(name, paramTypes);
    }

    // 查找函数
    @Override
    public Function getFunction(String name, List<Type> paramTypes) {
        Function rtn = super.getFunction(name, paramTypes);
        if (rtn == null && parentClass != null) {
            rtn = parentClass.getFunction(name, paramTypes);
        }
        return rtn;
    }

    @Override
    public Variable getFunctionVariable(String name, List<Type> paramTypes){
        Variable rtn = super.getFunctionVariable(name, paramTypes);
        if (rtn == null && parentClass != null){
            //TODO 是否要检查visibility?
            rtn = parentClass.getFunctionVariable(name, paramTypes);
        }
        return rtn;
    }

    @Override
    public boolean containsSymbol(Symbol symbol) {
        if (symbol == thisRef || symbol == superRef) {
            return true;
        }
        boolean rtn = super.containsSymbol(symbol);
        if (!rtn && parentClass != null) {
            rtn = parentClass.containsSymbol(symbol);
        }
        return rtn;
    }

    @Override
    public boolean isType(Type type) {
        if (this == type) return true;
        if (type instanceof Class) {
             // return ((Class) type).isAncestor(this);
            return isAncestor((Class) type);
        }
        return false;
    }

    public boolean isAncestor(Class theClass) {
        if (theClass.getParentClass() != null) {
            if (theClass.getParentClass() == this) {
                return true;
            } else {
                return isAncestor(theClass.getParentClass());
            }
        }
        return false;
    }

    public DefaultConstructor defaultConstructor() {
        if (defaultConstructor == null) {
            defaultConstructor = new DefaultConstructor(name, this);
        }
        return defaultConstructor;
    }


}
