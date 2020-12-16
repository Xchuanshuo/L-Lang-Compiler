package com.legend.ir;

import com.legend.semantic.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-12-8.
 * @description 三地址程序
 */
public class TACProgram {

    private List<TACInstruction> instructionList = new ArrayList<>();
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

    public void dump() {
        int i = 0;
        for (TACInstruction instruction : instructionList) {
            System.out.println(instruction);
//            System.out.println(i++ + ":     "+ instruction);
        }
    }

}
