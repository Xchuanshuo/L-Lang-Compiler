package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 变量
 */
public class Variable extends Symbol {

    // 变量类型
    private Type type;
    // a缺省值
    private Object defaultValue = null;

    public Variable(String name, Scope enclosingScope, ASTNode astNode) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.astNode = astNode;
    }

    public boolean isClassMember() {
        return enclosingScope instanceof Class;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class This extends Variable {

        public This(Scope enclosingScope, ASTNode astNode) {
            super("this", enclosingScope, astNode);
        }

        public Class Class() {
            return (Class) enclosingScope;
        }
    }

    public static class Super extends Variable {

        public Super(Scope enclosingScope, ASTNode astNode) {
            super("this", enclosingScope, astNode);
        }

        public Class Class() {
            return (Class) enclosingScope;
        }
    }
}