package com.legend.gen;

import com.legend.interpreter.Env;
import com.legend.ir.Constant;
import com.legend.semantic.*;
import com.legend.semantic.Class;

import java.util.HashMap;
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
    private Env staticFieldContainer = new Env();
    private GlobalConstantPool constantPool = new GlobalConstantPool();
    private final Constant globalConst = new Constant(PrimitiveType.String, "GLOBAL");

    private MethodArea() {
        addConstant(globalConst);
        for (Type type : PrimitiveType.baseTypes()) {
            addType(type);
        }
    }


    public Variable getStaticFieldAddress(String className, String fieldName) {
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

    public Object getStaticFieldValue(String className, String fieldName) {
        Variable variable = getStaticFieldAddress(className, fieldName);
        return staticFieldContainer.getValue(variable);
    }

    public Object getStaticFieldValue(Variable variable) {
        return staticFieldContainer.getValue(variable);
    }

    public void setStaticFieldValue(String className, String fieldName, Object value) {
        Variable variable = getStaticFieldAddress(className, fieldName);
        staticFieldContainer.setValue(variable, value);
    }

    public void setStaticFieldValue(Variable variable, Object value) {
        staticFieldContainer.setValue(variable, value);
    }

    public void setConstantPool(GlobalConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public GlobalConstantPool getConstantPool() {
        return constantPool;
    }

    public Constant addType(Type type) {
//        System.out.println("新增类型: " + type);
        String typeName = type.toString();
        Constant constant = new Constant(PrimitiveType.String, typeName);
        addConstant(constant);
        typeMap.put(type.toString(), type);
        return constant;
    }

    public void addConstant(Constant constant) {
        constantPool.add(constant);
    }

    public Constant getConstByIdx(int idx) {
        return constantPool.getByIdx(idx);
    }

    public Constant getGlobalConst() {
        return globalConst;
    }

    public Class getClassByIdx(int idx) {
        String typeName = String.valueOf(constantPool.getByIdx(idx).getValue());
        return (Class) typeMap.get(typeName);
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
        sb.append(function.returnType().toString());
        return sb.toString();
    }

    public static MethodArea getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static MethodArea instance = new MethodArea();
    }
}
