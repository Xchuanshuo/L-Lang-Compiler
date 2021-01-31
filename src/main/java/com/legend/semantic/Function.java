package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 函数作用域
 */
public class Function extends Scope implements FunctionType {

    // 函数的形参以及类型
    private List<Variable> variables = new LinkedList<>();
    private List<Type> paramTypeList = null;

    // 返回值类型
    protected Type returnType = null;
    // 闭包变量, 即函数所引用的外部变量
    protected Set<Variable> closureVariables = null;

    public Function(String name, Scope enclosingScope, ASTNode astNode) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.astNode = astNode;
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
        if (paramTypeList == null) {
            paramTypeList = new LinkedList<>();
            for (Variable variable : variables) {
                paramTypeList.add(variable.getType());
            }
        }
        return paramTypeList;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public boolean matchParameterTypes(List<Type> paramTypes) {
        if (variables.size() != paramTypes.size()) {
            return false;
        }
        boolean matched = true;
        for (int i = 0;i < paramTypes.size();i++) {
            Type curType = variables.get(i).getType();
            if (!curType.isType(paramTypes.get(i))) {
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
            return DefaultFunctionType.isType(this, (FunctionType) type);
        }
        return false;
    }

    // 该函数是不是类的方法
    public boolean isMethod() {
        return enclosingScope instanceof Class;
    }

    // 该函数是不是类的构造函数
    public boolean isConstructor() {
        if (enclosingScope instanceof Class) {
            return enclosingScope.name.equals(name);
        }
        return false;
    }

    public boolean isInitMethod() {
        if (enclosingScope instanceof Class) {
            return name.equals("_init_");
        }
        return false;
    }

    public boolean isStaticInitMethod() {
        if (enclosingScope instanceof Class) {
            return name.equals("_static_init_");
        }
        return false;
    }

    public void setClosureVariables(Set<Variable> closureVariables) {
        this.closureVariables = closureVariables;
    }

    public Set<Variable> getClosureVariables() {
        return closureVariables;
    }

    public Variable findUpValueVar(String name) {
        if (closureVariables == null) {
            return null;
        }
        for (Variable variable : closureVariables) {
            if (variable.name.equals(name)) {
                return variable;
            }
        }
        return null;
    }

    public int getClosureMaxSize() {
        int size = 0;
        if (closureVariables != null) {
            for (Variable variable : closureVariables) {
                size = Math.max(size, variable.getOffset());
            }
        }
        return size + 1;
    }

    @Override
    public String toString() {
        return enclosingScope + "_" + name;
    }
}
