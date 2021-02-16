package com.legend.common;

import com.legend.exception.LVMException;
import com.legend.gen.GlobalConstantPool;
import com.legend.gen.Instruction;
import com.legend.ir.Constant;
import com.legend.ir.TACInstruction;
import com.legend.ir.TACType;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.vm.Slots;

import java.io.Serializable;
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
public class MetadataArea implements Serializable {

    private Map<String, Type> typeMap = new HashMap<>();
    private Map<String, NameSpace> moduleMap = new HashMap<>();
    private Map<String, Integer> funcNameToPositionMap = new HashMap<>();
    // 记录offset到label的映射 主要目的是为了方便查看信息
    private Map<Integer, String> positionToLabelMap = new HashMap<>();
    private Map<String, Slots> staticFieldSlotsMap = new HashMap<>();
    private Map<String, Slots> moduleVarSlotsMap = new HashMap<>();
    private GlobalConstantPool constantPool = new GlobalConstantPool();
    private final Constant globalConst = new Constant(PrimitiveType.String, "GLOBAL");

    private MetadataArea() {
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

    public Variable getModuleVar(String moduleName, String varName) {
        NameSpace nameSpace = moduleMap.get(moduleName);
        if (nameSpace == null) {
            throw new RuntimeException("No exist a module name of " + moduleName);
        }
        Variable variable = nameSpace.findModuleVar(varName);
        if (variable == null) {
            throw new RuntimeException("No exist a variable "
                    + varName + " in module [" + moduleName + "]");
        }
        return variable;
    }

    public Variable getUpValueVar(String funcName, String varName) {
        Function function = (Function) typeMap.get(funcName);
        if (function == null) {
            throw new RuntimeException("No exist a module name of " + funcName);
        }
        Variable variable = function.findUpValueVar(varName);
        if (variable == null) {
            throw new RuntimeException("No exist a variable "
                    + varName + " in closures [" + funcName + "]");
        }
        return variable;
    }

    public Class getClass(String name) {
        return (Class) typeMap.get(name);
    }

    public Slots staticVarSlots(String className) {
        Class theClass = (Class) typeMap.get(className);
        if (theClass == null) {
            throw new RuntimeException("No exist a class name of " + className);
        }
        if (!staticFieldSlotsMap.containsKey(className)) {
            Slots slots = new Slots(theClass.getStaticFieldCount());
            staticFieldSlotsMap.put(className, slots);
        }
        return staticFieldSlotsMap.get(className);
    }

    public Slots moduleVarSlots(String moduleName) {
        NameSpace module = moduleMap.get(moduleName);
        if (module == null) {
            throw new RuntimeException("No exist a module name of " + moduleName);
        }
        if (!moduleVarSlotsMap.containsKey(moduleName)) {
            Slots slots = new Slots(module.getModuleVarCount());
            moduleVarSlotsMap.put(moduleName, slots);
        }
        return moduleVarSlotsMap.get(moduleName);
    }

    public void setConstantPool(GlobalConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public GlobalConstantPool getConstantPool() {
        return constantPool;
    }

    public Constant addType(Type type) {
        String typeName = type.toString().replace("null_", "");
        if (type instanceof Function) {
            typeName = getFunctionSignature((Function) type);
        }
        Constant constant = new Constant(PrimitiveType.String, typeName);
        addConstant(constant);
        if (typeMap.containsKey(typeName)) {
            return constant;
        }
        typeMap.put(typeName, type);
        return constant;
    }

    public Constant addModule(NameSpace nameSpace) {
        String moduleName = nameSpace.getName();
        Constant constant = new Constant(PrimitiveType.String, moduleName);
        addConstant(constant);
        if (moduleMap.containsKey(moduleName)) {
            return constant;
        }
        moduleMap.put(moduleName, nameSpace);
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

    public int getFuncPosByIdx(Class clazz, int idx) {
        String className = clazz.toString().replace("null_", "");
        Function function = getFunctionByIdx(idx);
        String signature = getFunctionSignature(function);
        String tmp = signature.substring(0, signature.lastIndexOf("("));
        String newFuncName = className + signature.substring(
                tmp.lastIndexOf("_"), signature.length());
        if (funcNameToPositionMap.containsKey(newFuncName)) {
            return funcNameToPositionMap.get(newFuncName);
        } else if (clazz.getParentClass() != null) {
            return getFuncPosByIdx(clazz.getParentClass(), idx);
        }
        throw new LVMException("No exist a function : " + newFuncName);
    }

    public int getFunctionPos(Function function) {
        String newFuncName = getFunctionSignature(function);
        if (funcNameToPositionMap.containsKey(newFuncName)) {
            return funcNameToPositionMap.get(newFuncName);
        }
        throw new LVMException("No exist a function : " + newFuncName);
    }

    public int getSpecialFuncPosByIdx(int idx) {
        // String className = clazz.toString().replace("null_", "");
        Function function = getFunctionByIdx(idx);
        String newFuncName = getFunctionSignature(function);
        if (funcNameToPositionMap.containsKey(newFuncName)) {
            return funcNameToPositionMap.get(newFuncName);
        }
        throw new LVMException("No exist a function : " + newFuncName);
    }

    public int getFuncPosByIdx(int idx) {
        Function function = getFunctionByIdx(idx);
        String signature = getFunctionSignature(function);
        return funcNameToPositionMap.get(signature);
    }

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

    public void dumpConstantPool() {
        System.out.println(constantPool.toString());
    }

    public static MetadataArea getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static MetadataArea instance = new MetadataArea();
    }

    public void fillConstantPool(List<TACInstruction> instructionList) {
        MetadataArea area = MetadataArea.getInstance();
        for (TACInstruction instruction : instructionList) {
            if (instruction.getResult() instanceof Constant) {
                area.addConstant((Constant) instruction.getResult());
            }
            if (instruction.getArg1() instanceof Constant) {
                area.addConstant((Constant) instruction.getArg1());
            }
            if (instruction.getArg2() instanceof Constant) {
                area.addConstant((Constant) instruction.getArg2());
            }
            if (instruction.getArg1() instanceof Type) {
                Constant constant = area.addType((Type) instruction.getArg1());
                instruction.setArg1(constant);
            }
            if (instruction.getArg2() instanceof Type) {
                Constant constant = area.addType((Type) instruction.getArg2());
                instruction.setArg2(constant);
            }
            fillClassField(area, instruction);
            fillModule(area, instruction);
            fillUpValueVar(area, instruction);
        }
    }

    private void fillModule(MetadataArea area, TACInstruction tac) {
        if (tac.getType() == TACType.GET_MODULE_VAR ||
                tac.getType() == TACType.PUT_MODULE_VAR) {
            Constant constant1 = area.addModule((NameSpace) tac.getArg1());
            tac.setArg1(constant1);
            Constant constant2 = new Constant(PrimitiveType.String, tac.getArg2());
            area.addConstant(constant2);
            tac.setArg2(constant2);
        }
    }

    private void fillUpValueVar(MetadataArea area, TACInstruction tac) {
        if (tac.getType() == TACType.GET_UPVALUE_VAR ||
                tac.getType() == TACType.PUT_UPVALUE_VAR) {
            Constant constant = new Constant(PrimitiveType.String, tac.getArg1());
            area.addConstant(constant);
            tac.setArg1(constant);
        }
    }

    private void fillClassField(MetadataArea area, TACInstruction instruction) {
        if (instruction.getType() == TACType.GET_FIELD ||
                instruction.getType() == TACType.GET_STATIC_FIELD) {
            Constant constant = new Constant(PrimitiveType.String, instruction.getArg2());
            area.addConstant(constant);
            instruction.setArg2(constant);
        } else if (instruction.getType() == TACType.PUT_FIELD) {
            Constant constant = new Constant(PrimitiveType.String, instruction.getArg1());
            area.addConstant(constant);
            instruction.setArg1(constant);
        } else if (instruction.getType() == TACType.PUT_STATIC_FIELD) {
            Constant constant = new Constant(PrimitiveType.String, instruction.getArg2());
            area.addConstant(constant);
            instruction.setArg2(constant);
        }
    }
}
