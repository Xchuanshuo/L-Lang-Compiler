package com.legend.ir;

import com.legend.exception.InterpreterException;
import com.legend.exception.ParseException;
import com.legend.semantic.Function;
import com.legend.semantic.Symbol;

import java.awt.*;
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
    private Map<TACInstruction, VMFunction> labelTofunctionMap = new HashMap<>();
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
        labelTofunctionMap.put(label, new VMFunction(function, instructionList.size()));
    }

    public VMFunction getVMFunction(TACInstruction label) {
        return labelTofunctionMap.get(label);
    }

    public TACInstruction findLabelByFunction(Function target) {
        for (Map.Entry<TACInstruction, VMFunction> entry : labelTofunctionMap.entrySet()) {
            if (entry.getValue().getFunction() == target) {
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

}
