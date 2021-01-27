package com.legend.semantic;

import com.legend.common.MetadataArea;
import com.legend.parser.ast.ASTNode;
import com.legend.semantic.Variable.Super;
import com.legend.semantic.Variable.This;
import com.legend.vm.Object;

import java.util.ArrayList;
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
    // 静态符号表 存储类的静态成员以及静态方法
    private List<Symbol> staticSymbolTable = new ArrayList<>();
    private int fieldCount = 0;
    private int staticFieldCount = 0;
    private List<Variable> fields;
    private List<Variable> staticFields;

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

    public static Class rootClass = new Class("Object", null);

    public static Class arrayClass(String name) {
        Class clazz = new Class(name, null);
        clazz.setParentClass(rootClass);
        return clazz;
    }

    public List<Variable> fields() {
        return fields;
    }

    public List<Variable> getStaticFields() {
        if (staticFields != null) {
            return staticFields;
        }
        calculateStaticFields();
        return staticFields;
    }

    public void calculateStaticFields() {
        if (staticFields != null) return;
        staticFields = new ArrayList<>();
        if (parentClass != null) {
            staticFieldCount = parentClass.staticFieldCount;
        }
        for (Symbol symbol : staticSymbolTable) {
            if (symbol instanceof Variable) {
                symbol.setOffset(staticFieldCount++);
                staticFields.add((Variable) symbol);
            }
        }
    }

    public void calculateFields() {
        if (fields != null) return;
        fields = new ArrayList<>();
        if (parentClass != null) {
            parentClass.calculateFields();
            fieldCount = parentClass.fieldCount;
        }
        for (Symbol symbol : symbols) {
            if (symbol instanceof Variable && !symbol.isStatic()) {
                symbol.setOffset(fieldCount++);
                fields.add((Variable) symbol);
            }
        }
    }

    public This getThis() {
        return thisRef;
    }

    public Super getSuper() {
        return superRef;
    }

    @Override
    public String toString() {
        return enclosingScope + "_" + name;
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

    public void addStatic(Symbol symbol) {
        staticSymbolTable.add(symbol);
    }

    public Variable findStaticVariable(String name) {
        Variable variable = null;
        for (Symbol symbol : getStaticFields()) {
            if (symbol instanceof Variable && symbol.name.equals(name)) {
                variable = (Variable) symbol;
                break;
            }
        }
        if (variable == null && getParentClass() != null) {
            variable = getParentClass().findStaticVariable(name);
        }
        return variable;
    }

    public Function findStaticMethod(String name, List<Type> paramTypes) {
        Function function = null;
        for (Symbol symbol : staticSymbolTable) {
            if (symbol instanceof Function && symbol.isStatic()
                    && symbol.name.equals(name)
                    && ((Function) symbol).matchParameterTypes(paramTypes)) {
                function = (Function) symbol;
                break;
            }
        }
        if (function == null && getParentClass() != null) {
            function = getParentClass().findStaticMethod(name, paramTypes);
        }
        return function;
    }

    public Variable findField(String name) {

        return getVariable(name);
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

    public boolean isArray() {
        return name.endsWith("[");
    }

    public Class getComponentClass() {
        if (isArray()) {
            String componentName = name.substring(0, name.length() - 1);
            return MetadataArea.getInstance().loadArrayClass(componentName);
        }
        return null;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public int getStaticFieldCount() {
        return staticFieldCount;
    }

    public Object newObj() {
        if (fields == null) {
            calculateFields();
        }
        return new Object(this);
    }

    public Object newArrayObj(int count) {
        if (!isArray()) {
            throw new RuntimeException("Not array class " + this.name);
        }
        switch (name) {
            case "Byte[":
                return new Object(this, new byte[count]);
            case "Integer[":
                return new Object(this, new int[count]);
            case "Float[":
                return new Object(this, new float[count]);
            case "Boolean[":
                return new Object(this, new int[count]);
            default:
                return new Object(this, new Object[count]);
        }
    }
}
