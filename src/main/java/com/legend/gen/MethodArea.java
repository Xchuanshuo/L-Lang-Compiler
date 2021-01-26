package com.legend.gen;

import com.legend.interpreter.Env;
import com.legend.ir.Constant;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.vm.BuiltInClass;
import com.legend.vm.Slots;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-12-28.
 * @description 方法区
 *  存放数据: 1.类、函数、函数类型等信息
 *          2.静态字段、类方法信息
 *          3.字符串常量池、整数、实数等字面量
 */
public class MethodArea {

    private Map<String, Type> typeMap = new HashMap<>();
    private Map<String, Integer> funcNameToPositionMap = new HashMap<>();
    // 记录offset到label的映射 主要目的是为了方便查看信息
    private Map<Integer, String> positionToLabelMap = new HashMap<>();
    private Map<String, Slots> staticFieldSlotsMap = new HashMap<>();
    private GlobalConstantPool constantPool = new GlobalConstantPool();
    private final Constant globalConst = new Constant(PrimitiveType.String, "GLOBAL");

    private MethodArea() {
        addConstant(globalConst);
        for (Type type : PrimitiveType.baseTypes()) {
            addType(type);
            Type t = Class.arrayClass(type.name() + "[");
            typeMap.put(t.name(), t);
        }
    }

    public Variable getStaticField(String className, String fieldName) {
        Class theClass = (Class) typeMap.get(className);
        if (theClass == null) {
            throw new RuntimeException("No exist a class name of " + className);
        }
        Variable staticField = theClass.findStaticVariable(fieldName);
        if (staticField == null) {
            throw new RuntimeException("No exist a static field of "
                    + fieldName + " in class [" + className + "]");
        }
        return staticField;
    }

    public Slots staticVarSlots(String className) {
        Class theClass = (Class) typeMap.get(className);
        if (theClass == null) {
            throw new RuntimeException("No exist a class name of " + className);
        }
        if (!staticFieldSlotsMap.containsKey(className)) {
            List<Variable> variables = theClass.getStaticFields();
            Slots slots = new Slots(variables.size());
            staticFieldSlotsMap.put(className, slots);
        }
        return staticFieldSlotsMap.get(className);
    }

    public void setConstantPool(GlobalConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public GlobalConstantPool getConstantPool() {
        return constantPool;
    }

    public Constant addType(Type type) {
        String typeName = type.toString().replace("null_", "");
        Constant constant = new Constant(PrimitiveType.String, typeName);
        addConstant(constant);
        if (typeMap.containsKey(typeName)) {
            return constant;
        }
        typeMap.put(type.toString(), type);
        return constant;
    }

    public void addType(String name, Type type) {
        typeMap.put(name, type);
    }

    public Type getTypeByName(String name) {
        return typeMap.get(name);
    }


    public void addConstant(Constant constant) {
        constantPool.add(constant);
    }

    public Constant getConstByIdx(int idx) {
        return constantPool.getByIdx(idx);
    }

    public String getStrConstByIdx(int idx) {
        Constant constant = getConstByIdx(idx);
        return constant.getStrVal();
    }

    public Constant getGlobalConst() {
        return globalConst;
    }

    public Class getClassByIdx(int idx) {
        String typeName = String.valueOf(constantPool.getByIdx(idx).getValue());
        Type type = typeMap.get(typeName);
        if (type instanceof ArrayType) {
            return loadArrayClass(type.name());
        } else if (type instanceof FunctionType) {
            return null;
        } else {
            return (Class) typeMap.get(typeName);
        }
    }

    public Class loadArrayClass(String name) {
        Class clazz = null;
        Type type = typeMap.get(name);
        if (!(type instanceof Class)) {
            clazz = new Class(name, null);
            clazz.setParentClass(Class.rootClass);
            typeMap.put(name, clazz);
        } else {
            clazz = (Class) type;
        }
        return clazz;
    }

    public Function getFunctionByIdx(int idx) {
        String typeName = String.valueOf(constantPool.getByIdx(idx).getValue());
        return (Function) typeMap.get(typeName);
    }

    public Type getTypeByIdx(int idx) {
        String typeName = String.valueOf(constantPool.getByIdx(idx).getValue());
        return typeMap.get(typeName);
    }

    public FunctionType getFunctionTypeByIdx(int idx) {
        String typeName = String.valueOf(constantPool.getByIdx(idx).getValue());
        return (FunctionType) typeMap.get(typeName);
    }

    public Constant addFunction(Function function, int position) {
        String signature = getFunctionSignature(function);
        Constant constant = new Constant(PrimitiveType.String, signature);
        addConstant(constant);
        funcNameToPositionMap.put(signature, position);
        return constant;
    }

    public void addFunction(Constant signature, int position) {
        addConstant(signature);
        funcNameToPositionMap.put(String.valueOf(signature.getValue()), position);
    }

    public int getFuncPosByIdx(int idx) {
        Function function = getFunctionByIdx(idx);
        String signature = getFunctionSignature(function);
        return funcNameToPositionMap.get(signature);
    }

//    public int getFuncPosByIdx(int idx) {
//        String signature = String.valueOf(constantPool.getByIdx(idx).getValue());
//        return funcNameToPositionMap.get(signature);
//    }

    public String getFunctionSignature(Function function) {
        StringBuilder sb = new StringBuilder();
        sb.append(function.toString()).append("(");
        for (Type type : function.paramTypeList()) {
            sb.append(type.toString()).append(",");
        }
        sb.append(")");
        if (!function.isConstructor()) {
            sb.append(function.returnType().toString());
        }
        return sb.toString();
    }

    public static MethodArea getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static MethodArea instance = new MethodArea();
    }
}
