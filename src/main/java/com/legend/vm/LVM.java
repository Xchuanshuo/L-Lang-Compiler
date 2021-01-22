package com.legend.vm;

import com.legend.common.ByteCodeReader;
import com.legend.exception.GeneratorException;
import com.legend.gen.Instruction;
import com.legend.gen.MethodArea;
import com.legend.gen.operand.Offset;
import com.legend.gen.operand.Register;
import com.legend.semantic.PrimitiveType;
import com.legend.semantic.Type;

import static com.legend.gen.Constant.*;

/**
 * @author Legend
 * @data by on 21-1-21.
 * @description l-lang-vm
 */
public class LVM {

    private ByteCodeReader reader;
    private Slots registers = new Slots(32);
    private static final int DEFAULT_STACK_SIZE = 10000;
    private MethodArea area = MethodArea.getInstance();
    private int entry = 0;
    private Slots stackMemory;
    private final boolean isDebug = false;

    public LVM(byte[] codes, int maxStackSize, int entry) {
        this.reader = new ByteCodeReader(codes);
        this.stackMemory = new Slots(maxStackSize);
        this.entry = entry;
    }

    public LVM(byte[] codes, int entry) {
        this(codes, DEFAULT_STACK_SIZE, entry);
    }

    public void onStart() {
        System.out.println("虚拟机启动---------------------------");
        registers.setInt(Register.PC.getIdx(), entry);
        registers.setInt(Register.BP.getIdx(), stackMemory.getSize() - 1);
        registers.setInt(Register.SP.getIdx(), stackMemory.getSize() - 1);
    }

    public void run() throws GeneratorException {
        onStart();
        // 1.取指 2.译码 3.执行
        while (true) {
            if (reader.isEnd()) break;
            reader.reset(registers.getInt(Register.PC.getIdx()));
            Instruction ins = Instruction.decode(reader);
            exec(ins);
            if (isDebug) {
                System.out.println(ins.toString());
            }
            registers.setInt(Register.PC, reader.pc());
        }
        onStop();
    }

    private void exec(Instruction ins) {
        switch (ins.getOpCode().getCode()) {
            case NOP: break;
            case LOAD:
                load(ins);
                break;
            case STORE:
                store(ins);
                break;
            case IADD:
                iadd(ins);
                break;
            case ISUB:
                isub(ins);
                break;
            case IMUL:
                imul(ins);
                break;
            case IDIV:
                idiv(ins);
                break;
            case IMOD:
                imod(ins);
                break;
            case FADD:
                fadd(ins);
                break;
            case FSUB:
                fsub(ins);
                break;
            case FMUL:
                fmul(ins);
                break;
            case FDIV:
                fdiv(ins);
                break;
            case ICMP_LT:
                icmpLT(ins);
                break;
            case ICMP_LE:
                icmpLE(ins);
                break;
            case ICMP_GT:
                icmpGT(ins);
                break;
            case ICMP_GE:
                icmpGE(ins);
                break;
            case ICMP_EQ:
                icmpEQ(ins);
                break;
            case ICMP_NE:
                icmpNE(ins);
            case FCMP_LT:
                fcmpLT(ins);
                break;
            case FCMP_LE:
                fcmpLE(ins);
                break;
            case FCMP_GT:
                fcmpGT(ins);
                break;
            case FCMP_GE:
                fcmpGE(ins);
                break;
            case FCMP_EQ:
                fcmpEQ(ins);
                break;
            case FCMP_NE:
                fcmpNE(ins);
                break;
            case ICMP_AND:
                cmpAnd(ins);
                break;
            case ICMP_OR:
                cmpOr(ins);
                break;
            case SADD:
                sadd(ins);
                break;
            case JUMP:
                jump(ins);
                break;
            case JUMP_NZ:
                jumpNZ(ins);
                break;
            case JUMP_Z:
                jumpZ(ins);
                break;
            case PRINT: print(ins); break;
        }
    }

    private void sadd(Instruction ins) {
        Object v1, v2;
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        if (registers.isRef(r1)) {
            v1 = registers.getRef(r1);
        } else {
            v1 = registers.getInt(r1);
        }
        if (registers.isRef(r2)) {
            v2 = registers.getRef(r2);
        } else {
            v2 = registers.getInt(r2);
        }
        Register r3 = ins.getRegOperand(2);
        registers.setRef(r3, String.valueOf(v1) + String.valueOf(v2));
    }

    private void iadd(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3.getIdx(), val1 + val2);
    }

    private void isub(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3.getIdx(), val1 - val2);
    }

    private void imul(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3.getIdx(), val1 * val2);
    }

    private void idiv(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3.getIdx(), val1 / val2);
    }

    private void imod(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3.getIdx(), val1 % val2);
    }

    private void fadd(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setFloat(r3, val1 + val2);
    }

    private void fsub(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setFloat(r3, val1 - val2);
    }

    private void fmul(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setFloat(r3, val1 * val2);
    }

    private void fdiv(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setFloat(r3, val1 / val2);
    }

    private void icmpLT(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 < val2);
    }

    private void icmpLE(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 <= val2);
    }

    private void icmpGT(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 > val2);
    }

    private void icmpGE(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 >= val2);
    }

    private void icmpEQ(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 == val2);
    }

    private void icmpNE(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 != val2);
    }

    private void fcmpLT(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 < val2);
    }

    private void fcmpLE(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 <= val2);
    }

    private void fcmpGT(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 > val2);
    }

    private void fcmpGE(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 >= val2);
    }

    private void fcmpEQ(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 == val2);
    }

    private void fcmpNE(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 != val2);
    }

    private void cmpAnd(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, (val1 & val2) == 1);
    }

    private void cmpOr(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, (val1 | val2) == 1);
    }

    private void load(Instruction ins) {
        Object val;
        Register r1 = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        Register r3 = ins.getRegOperand(2);
        if (r1 == Register.CONSTANT) {
            com.legend.ir.Constant cst = area.getConstByIdx(offset.getOffset());
            if (cst.getType() == PrimitiveType.Integer) {
                registers.setInt(r3.getIdx(), cst.getIntVal());
            } else if (cst.getType() == PrimitiveType.Float) {
                registers.setFloat(r3.getIdx(), cst.getFloatVal());
            } else if (cst.getType() == PrimitiveType.String) {
                registers.setRef(r3.getIdx(), cst.getStrVal());
            }
        } else {
            int address = registers.getInt(r1.getIdx()) + offset.getOffset();
            val = stackMemory.getRef(address);
            if (val != null) {
                registers.setRef(r3.getIdx(), val);
            } else {
                registers.setInt(r3.getIdx(), stackMemory.getInt(address));
            }
        }
    }

    private void store(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Offset offset = ins.getOffsetOperand(2);
        int address = registers.getInt(r2) + offset.getOffset();
        if (!registers.isRef(r1)) {
            stackMemory.setInt(address, registers.getInt(r1));
        } else {
            stackMemory.setRef(address, registers.getRef(r1));
        }
    }

    private void print(Instruction ins) {
        Object val = null;
        Register r1 = ins.getRegOperand(0);
        if (!registers.isRef(r1)) {
            Offset offset = ins.getOffsetOperand(1);
            Type type = area.getTypeByIdx(offset.getOffset());
            if (type == PrimitiveType.Integer) {
                val = registers.getInt(r1);
            } else if (type == PrimitiveType.Float) {
                val = registers.getFloat(r1);
            }
        } else {
            val = registers.getRef(r1);
        }
        if (val != null && val.equals("\\n")) {
            System.out.println();
        } else {
            System.out.print(val);
        }
    }

    private void jump(Instruction ins) {
        Offset offset = ins.getOffsetOperand(0);
        reader.reset(offset.getOffset());
    }

    private void jumpZ(Instruction ins) {
        int val = registers.getInt(Register.ZERO);
        if (val == 0) {
            Offset offset = ins.getOffsetOperand(0);
            reader.reset(offset.getOffset());
        }
    }

    private void jumpNZ(Instruction ins) {
        int val = registers.getInt(Register.ZERO);
        if (val != 0) {
            Offset offset = ins.getOffsetOperand(0);
            reader.reset(offset.getOffset());
        }
    }

    public void onStop() {
        System.out.println("虚拟机停止---------------------------");
    }
}
