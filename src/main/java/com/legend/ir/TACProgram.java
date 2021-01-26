package com.legend.ir;

import com.legend.exception.InterpreterException;
import com.legend.gen.GlobalConstantPool;
import com.legend.gen.MethodArea;
import com.legend.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-12-8.
 * @description 三地址程序
 */
public class TACProgram {

    private List<TACInstruction> instructionList = new ArrayList<>();
    private Map<TACInstruction, Function> labelToFunctionMap = new HashMap<>();
    private int labelCounter = 0;

    public TACProgram add(TACInstruction instruction) {
        instructionList.add(instruction);
        return this;
    }

    public List<TACInstruction> getInstructionList() {
        return instructionList;
    }

    public TACInstruction lastInstruction() {
        return instructionList.get(instructionList.size() - 1);
    }

    public TACInstruction firstInstruction() {
        return instructionList.get(instructionList.size() - 1);
    }

    public TACInstruction addLabel() {
        String label = "L" + labelCounter++;
        TACInstruction instruction = new TACInstruction(TACType.LABEL,
                null, label, null, null);
        add(instruction);
        return instruction;
    }

    public TACInstruction newLabel() {
        String label = "L" + labelCounter++;
        return new TACInstruction(TACType.LABEL, null, label, null, null);
    }

    public void addFunction(TACInstruction label, Function function) {
        if (label.getType() != TACType.LABEL) {
            throw new InterpreterException("Expected a Label, but " + label.getType());
        }
        // 函数入口地址默认是当前已经转换的指令条数
        labelToFunctionMap.put(label, function);
    }

    public Function getFunction(TACInstruction label) {
        return labelToFunctionMap.get(label);
    }

    public TACInstruction findLabelByFunction(Function target) {
        for (Map.Entry<TACInstruction, Function> entry : labelToFunctionMap.entrySet()) {
            if (entry.getValue() == target) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void dump() {
        int i = 0;
        for (TACInstruction instruction : instructionList) {
//            System.out.println(instruction);
            System.out.println(String.format("%8s", i++ + ":    ") + instruction);
        }
    }

    public void dumpConstantPool() {
        GlobalConstantPool constantPool = MethodArea.getInstance()
                .getConstantPool();
        System.out.println(constantPool.toString());
    }

    public void fillConstantPool(Scope scope) {
        for (Symbol symbol : scope.getSymbols()) {
            if (symbol instanceof Constant) {
                MethodArea.getInstance().addConstant((Constant) symbol);
            } else if (symbol instanceof Scope) {
                fillConstantPool((Scope) symbol);
            }
        }
    }

    public void fillConstantPool() {
        MethodArea area = MethodArea.getInstance();
        for (TACInstruction instruction : instructionList) {
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

}
