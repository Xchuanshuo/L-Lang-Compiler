package com.legend.gen;

import com.legend.common.ByteCodeReader;
import com.legend.exception.GeneratorException;
import com.legend.gen.operand.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.legend.gen.Constant.PRINT;

/**
 * @author Legend
 * @data by on 20-12-28.
 * @description 指令
 */
public class Instruction {

    private OpCode opCode;
    private List<Operand> operands = new ArrayList<>();

    public Instruction(OpCode opCode) {
        this.opCode = opCode;
    }

    public static Instruction register(OpCode opCode, Register r1,
                                Register r2, Register r3) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, r2, r3));
        return instruction;
    }

    public static Instruction register1(OpCode opCode, Register r1, Register r2) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, r2));
        return instruction;
    }

    public static Instruction offset(OpCode opCode, Offset offset) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.add(offset);
        return instruction;
    }

    public static Instruction offset1(OpCode opCode, Register r1,
                               Offset offset, Register r2) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, offset, r2));
        return instruction;
    }

    public static Instruction offset2(OpCode opCode, Register r1,
                               Register r2, Offset offset) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, r2, offset));
        return instruction;
    }

    public static Instruction offset3(OpCode opCode, Offset o1,
                               Offset o2, Register r1) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(o1, o2, r1));
        return instruction;
    }

    public static Instruction offset4(OpCode opCode, Register r1,
                               Offset o1, Offset o2) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, o1, o2));
        return instruction;
    }

    public static Instruction immediate(OpCode opCode, Register r1,
                                        ImmediateNumber number) {
        Instruction instruction = new Instruction(opCode);
        instruction.operands.addAll(Arrays.asList(r1, number));
        return instruction;
    }


    public static Instruction offset5(Register r1, Offset offset) {
        Instruction instruction = new Instruction(OpCode.PRINT);
        instruction.operands.add(r1);
        instruction.operands.add(offset);
        return instruction;
    }

    public static Instruction decode(ByteCodeReader reader) throws GeneratorException {
        byte c = reader.readByte();
        OpCode opCode = OpCode.getOpcode(c);
        Instruction instruction = new Instruction(opCode);
        switch (opCode.getAddressingType()) {
            case REGISTER:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                break;
            case REGISTER1:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                break;
            case OFFSET:
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                break;
            case OFFSET1:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                break;
            case OFFSET2:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                break;
            case OFFSET3:
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                break;
            case OFFSET4:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                break;
            case IMMEDIATE:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(new ImmediateNumber(reader.readShort()));
                break;
            case OFFSET5:
                instruction.addOperand(Register.getRegByIdx(reader.readByte()));
                instruction.addOperand(Offset.decodeOffset(reader.readShort()));
                break;
        }
        return instruction;
    }

    public byte[] encode() {
        if (opCode == null) return new byte[0];
        try(ByteArrayOutputStream bios = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bios)) {
            dos.writeByte(opCode.getCode());
            switch (opCode.getAddressingType()) {
                case REGISTER:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeByte(operands.get(1).getVal());
                    dos.writeByte(operands.get(2).getVal());
                    break;
                case REGISTER1:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeByte(operands.get(1).getVal());
                    break;
                case OFFSET:
                    dos.writeShort(operands.get(0).getVal());
                    break;
                case OFFSET1:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeShort(operands.get(1).getVal());
                    dos.writeByte(operands.get(2).getVal());
                    break;
                case OFFSET2:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeByte(operands.get(1).getVal());
                    dos.writeShort(operands.get(2).getVal());
                    break;
                case OFFSET3:
                    dos.writeShort(operands.get(0).getVal());
                    dos.writeShort(operands.get(1).getVal());
                    dos.writeByte(operands.get(2).getVal());
                    break;
                case OFFSET4:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeShort(operands.get(1).getVal());
                    dos.writeShort(operands.get(2).getVal());
                    break;
                case IMMEDIATE:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeShort(operands.get(1).getVal());
                    break;
                case OFFSET5:
                    dos.writeByte(operands.get(0).getVal());
                    dos.writeShort(operands.get(1).getVal());
                    break;
            }
            dos.flush();
            return bios.toByteArray();
        } catch (IOException ignore) {
        }
        return new byte[]{opCode.getCode()};
    }

    public void addOperand(Operand operand) {
        operands.add(operand);
    }

    public Register getRegOperand(int idx) {
        return (Register) operands.get(idx);
    }

    public Offset getOffsetOperand(int idx) {
        return (Offset) operands.get(idx);
    }

    public Label getLabel(int idx) {
        return (Label) operands.get(idx);
    }

    public int getImmediateNumber(int idx) {
        return operands.get(idx).getVal();
    }

    public OpCode getOpCode() {
        return opCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(opCode.getName()).append(" ");
        for (Operand operand : operands) {
            if (operand instanceof Register) {
                sb.append("reg");
            } else if (operand instanceof Offset) {
                sb.append("offset");
            } else if (operand instanceof ImmediateNumber) {
                sb.append("number");
            }
            sb.append("(").append(operand).append(")").append(" ");
        }
        return sb.toString();
    }
}
