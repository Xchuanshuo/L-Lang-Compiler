package com.legend.gen;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Legend
 * @data by on 20-12-29.
 * @description 最后生成的指令集合
 */
public class OpCodeProgram {

    private List<Instruction> instructions = new ArrayList<>();
    private Map<Integer, String> commentMap = new TreeMap<>();
    // 统计当前指令占用字节数
    private int count = 0;

    public void addIns(Instruction instruction) {
        // 指令1字节 +　对应寻址模式指令所需操作数占用的字节数
        count += instruction.getOpCode().getAddressingType().getBytes() + 1;
        instructions.add(instruction);
    }

    public void addComment(String comment) {
        commentMap.put(getInsSize(), "# " + comment);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public int getInsSize() {
        return instructions.size();
    }

    public int getCurPosition() {
        return count;
    }

    public byte[] getByteCodes() {
        try(ByteArrayOutputStream bios = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bios)) {
            for (Instruction instruction : instructions) {
                dos.write(instruction.encode());
            }
            dos.flush();
            return bios.toByteArray();
        } catch (IOException ignored) {}
        return new byte[0];
    }

    public void dump() {
        int i = 0;
        for (Instruction ins : instructions) {
            System.out.println(String.format("%8s", i++ + ":    ") + ins);
        }
    }

    public void dumpWithComments() {
        int i = 0;
        for (Instruction ins : instructions) {
            if (commentMap.containsKey(i)) {
                System.out.println(commentMap.get(i));
            }
            System.out.println(String.format("%8s", i++ + ":    ") + ins);
        }
    }
}
