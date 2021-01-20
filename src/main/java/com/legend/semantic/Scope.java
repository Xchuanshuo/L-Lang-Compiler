package com.legend.semantic;


import com.legend.lexer.Token;
import com.legend.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 作用域
 */
public class Scope extends Symbol {

    // 当前作用域的符号表
    protected List<Symbol> symbols = new ArrayList<>();
    protected List<Variable> localsVariables = new ArrayList<>();
    private int counter = 0;

    // 添加一个新的符号到当前作用域所持有的符号表
    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }

    // 当前作用域查找普通变量
    public Variable getVariable(String name) {
        return getVariable(this, name);
    }

    public static Variable getVariable(Scope scope, String name) {
        for (Symbol s : scope.symbols) {
            if (s instanceof Variable && s.name.equals(name)) {
                return (Variable) s;
            }
        }
        return null;
    }

    // 当前作用域查找函数变量
    protected Variable getFunctionVariable(String name, List<Type> paramTypes) {
        return getFunctionVariable(this, name, paramTypes);
    }

    public static Variable getFunctionVariable(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol s : scope.symbols) {
            if (s instanceof Variable && ((Variable) s).getType() instanceof FunctionType
                    && s.name.equals(name)) {
                Variable variable = (Variable) s;
                FunctionType functionType = (FunctionType) variable.getType();
                if (functionType.matchParameterTypes(paramTypes)) {
                    return variable;
                }
            }
        }
        return null;
    }

    // 当前作用域查找函数
    protected Function getFunction(String name, List<Type> paramTypes) {
        return getFunction(this, name, paramTypes);
    }

    public static Function getFunction(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol s : scope.symbols) {
            if (s instanceof Function && s.name.equals(name)) {
                Function function = (Function) s;
                if (function.matchParameterTypes(paramTypes)) {
                    return function;
                }
            }
        }
        return null;
    }

    // 当前作用域查找类
    protected Class getClass(String name) {
        return getClass(this, name);
    }

    public static Class getClass(Scope scope, String name) {
        for (Symbol s : scope.symbols) {
            if (s instanceof Class && s.name.equals(name)) {
                return (Class) s;
            }
        }
        return null;
    }

    // 是否包含某个symbol
    public boolean containsSymbol(Symbol symbol) {
        return symbols.contains(symbol);
    }

    public Variable createTempVariable() {
        return createTempVariable(null);
    }

    public Variable createTempVariable(Type type) {
        Token token = new Token(TokenType.IDENTIFIER, "v" + counter);
        Variable variable = new Variable(token.getText(), this, null);
        variable.setType(type);
        variable.setOffset(counter);
        counter++;
        localsVariables.add(variable);
        return variable;
    }

    public List<Variable> getLocalVariables() {
        return localsVariables;
    }

    public int getLocalsSize() {
        return localsVariables.size();
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return enclosingScope + "_" + name;
    }
}
