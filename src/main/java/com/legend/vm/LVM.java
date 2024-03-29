package com.legend.vm;

import com.legend.builtin.BuiltInClass;
import com.legend.builtin.BuiltInFunction;
import com.legend.builtin.Registry;
import com.legend.common.ByteCodeReader;
import com.legend.exception.GeneratorException;
import com.legend.exception.LVMException;
import com.legend.gen.Instruction;
import com.legend.common.MetadataArea;
import com.legend.gen.OpCode;
import com.legend.gen.operand.Offset;
import com.legend.gen.operand.Register;
import com.legend.lexer.Keyword;
import com.legend.semantic.*;
import com.legend.semantic.Class;

import java.util.Set;
import java.util.Stack;

import static com.legend.builtin.Registry.DEFAULT;
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
    private MetadataArea area;
    private Stack<Integer> retAddressStack = new Stack<>();
    private int entry = 0;
    private Slots stackMemory;
    private final boolean isDebug = false;

    public LVM(byte[] codes, int maxStackSize, int entry, MetadataArea area) {
        this.reader = new ByteCodeReader(codes);
        this.stackMemory = new Slots(maxStackSize);
        this.entry = entry;
        this.area = area;
    }

    public LVM(byte[] codes, int entry, MetadataArea area) {
        this(codes, DEFAULT_STACK_SIZE, entry, area);
    }

    public void onStart() {
        System.out.println("-----------------------虚拟机启动-------------------------------");
        BuiltInClass.init();
        Registry.initBuiltIn();
        registers.setInt(Register.PC, entry);
        registers.setInt(Register.BP, stackMemory.getSize() - 1);
        registers.setInt(Register.SP, stackMemory.getSize() - 1);
    }

    public void run() throws GeneratorException {
        onStart();
        // 1.取指 2.译码 3.执行
        while (true) {
            reader.reset(registers.getInt(Register.PC.getIdx()));
            if (reader.isEnd()) break;
            Instruction ins = Instruction.decode(reader);
            int step = ins.getOpCode().getAddressingType().getBytes() + 1;
            registers.setInt(Register.PC, registers.getInt(Register.PC) + step);
            if (isDebug) {
                System.out.println(ins.toString());
            }
            exec(ins);
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
            case BIT_AND:
                bitAnd(ins);
                break;
            case BIT_OR:
                bitOr(ins);
                break;
            case XOR:
                xor(ins);
                break;
            case LSHIFT:
                leftShift(ins);
                break;
            case RSHIFT:
                rightShift(ins);
                break;
            case INC:
                inc(ins);
                break;
            case INC_1:
                inc1(ins);
                break;
            case DEC:
                dec(ins);
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
                break;
            case ACMP_EQ:
                acmpEQ(ins);
                break;
            case ACMP_NE:
                acmpNE(ins);
                break;
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
            case MOVE:
                move(ins);
                break;
            case I2B:
                i2b(ins);
                break;
            case I2F:
                i2f(ins);
                break;
            case I2S:
                i2s(ins);
                break;
            case Z2S:
                z2s(ins);
                break;
            case F2I:
                f2i(ins);
                break;
            case F2S:
                f2s(ins);
                break;
            case NEW_ARR:
                newArr(ins);
                break;
            case ARR_LEN:
                arrLen(ins);
                break;
            case IA_LOAD:
                iaload(ins);
                break;
            case FA_LOAD:
                faload(ins);
                break;
            case AA_LOAD:
                aaload(ins);
                break;
            case IA_STORE:
                iastore(ins);
                break;
            case FA_STORE:
                fastore(ins);
                break;
            case AA_STORE:
                aastore(ins);
                break;
            case NEW_INSTANCE:
                newInstance(ins);
                break;
            case NEW_FUNC_OBJ:
                newFuncObj(ins);
                break;
            case GET_FIELD:
                getField(ins);
                break;
            case GET_S_FIELD:
                getStaticField(ins);
                break;
            case PUT_FIELD:
                putField(ins);
                break;
            case PUT_S_FIELD:
                putStaticField(ins);
                break;
            case GET_MODULE_VAR:
                getModuleVar(ins);
                break;
            case PUT_MODULE_VAR:
                putModuleVar(ins);
                break;
            case GET_UPVALUE_VAR:
                getUpValueVar(ins);
                break;
            case PUT_UPVALUE_VAR:
                putUpValueVar(ins);
                break;
            case INVOKE_VIRTUAL:
                invokeVirtual(ins);
                break;
            case INVOKE_SPECIAL:
                invokeSpecial(ins);
                break;
            case INVOKE_STATIC:
                invokeStatic(ins);
                break;
            case INVOKE_VAR_FUNC:
                invokeVarFunc(ins);
                break;
            case GET_PARAM_COUNT:
                getFuncLocals(ins);
                break;
            case RET:
                ret();
                break;
            case PRINT: print(ins); break;
            default:
                throw new LVMException("No exist any implements for opcode ["
                        + ins.getOpCode().getName() + "]!");
        }
    }

    private void move(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        registers.setInt(r2, registers.getInt(r1));
    }

    private void sadd(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        String v1, v2;
        if (registers.getRef(r1) == null) {
            v1 = "";
        } else {
            v1 = registers.getRef(r1).toString();
        }
        if (registers.getRef(r2) == null) {
            v2 = "";
        } else {
            v2 = registers.getRef(r2).toString();
        }
        Register r3 = ins.getRegOperand(2);
        registers.setRef(r3, StringPool.getStrObj(v1 + v2));
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

    private void bitAnd(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, val1 & val2);
    }

    private void bitOr(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, val1 | val2);
    }

    private void xor(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, val1 ^ val2);
    }

    private void leftShift(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, val1 << val2);
    }

    private void rightShift(Instruction ins) {
        int val1 = registers.getInt(ins.getRegOperand(0));
        int val2 = registers.getInt(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, val1 >> val2);
    }

    private void inc(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        int number = ins.getImmediateNumber(1);
        registers.setInt(r1, registers.getInt(r1) + number);
    }

    private void inc1(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        int number = registers.getInt(ins.getRegOperand(1));
        registers.setInt(r1, registers.getInt(r1) + number);
    }

    private void dec(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        int number = ins.getImmediateNumber(1);
        registers.setInt(r1, registers.getInt(r1) - number);
    }

    private void fadd(Instruction ins) {
        float val1 = registers.getFloat(ins.getRegOperand(0));
        float val2 = registers.getFloat(ins.getRegOperand(1));
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

    private void acmpEQ(Instruction ins) {
        Object ref1 = registers.getRef(ins.getRegOperand(0));
        Object ref2 = registers.getRef(ins.getRegOperand(1));
        boolean res =  ref1 == ref2;
        if (ref1 != null && ref2 != null
                && ref1.getData() instanceof String
                && ref2.getData() instanceof String) {
            res = ref1.getData().equals(ref2.getData());
        }
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, res);
    }

    private void acmpNE(Instruction ins) {
        Object ref1 = registers.getRef(ins.getRegOperand(0));
        Object ref2 = registers.getRef(ins.getRegOperand(1));
        boolean res =  ref1 != ref2;
        if (ref1 != null && ref2 != null
                && ref1.getData() instanceof String
                && ref2.getData() instanceof String) {
            res = !ref1.getData().equals(ref2.getData());
        }
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, res);
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
        boolean val1 = registers.getBoolean(ins.getRegOperand(0));
        boolean val2 = registers.getBoolean(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 && val2);
    }

    private void cmpOr(Instruction ins) {
        boolean val1 = registers.getBoolean(ins.getRegOperand(0));
        boolean val2 = registers.getBoolean(ins.getRegOperand(1));
        Register r3 = ins.getRegOperand(2);
        registers.setBoolean(r3, val1 || val2);
    }

    private void load(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        Register r3 = ins.getRegOperand(2);
        if (r1 == Register.CONSTANT) {
            com.legend.ir.Constant cst = area.getConstByIdx(offset.getOffset());
            if (typeIsEquals(cst.getType(), PrimitiveType.Integer)) {
                registers.setInt(r3, cst.getIntVal());
            } else if (typeIsEquals(cst.getType(), PrimitiveType.Float)) {
                registers.setFloat(r3, cst.getFloatVal());
            } else if (typeIsEquals(cst.getType(), PrimitiveType.String)) {
                registers.setRef(r3, StringPool.getStrObj(cst.getStrVal()));
            } else if (typeIsEquals(cst.getType(), PrimitiveType.Null)) {
                registers.setRef(r3, null);
            }
        } else {
            int address = registers.getInt(r1) + offset.getOffset();
            if (stackMemory.isRef(address)) {
                Object ref = stackMemory.getRef(address);
                if (r3 == Register.RV && ref.type() instanceof Class) {
                    packageClassClosure(ref);
                }
                registers.setRef(r3, ref);
            } else {
                registers.setInt(r3, stackMemory.getInt(address));
            }
        }
    }

    private void packageClassClosure(Object classObjRef) {
        Class clazz = classObjRef.clazz();
        if (!(classObjRef.getData() instanceof Slots)) {
            return;
        }
        Slots slots = classObjRef.fieldSlots();
        int maxSize = 0;
        for (Variable field : clazz.fields()) {
            if (field.getType() instanceof FunctionType) {
                Object funcObj = slots.getRef(field.getOffset());
                if (funcObj != null) {
                    maxSize = Math.max(funcObj.upValueSlots().getSize(), maxSize);
                }
            }
        }
        if (maxSize == 0) return;
        Slots newSlots = new Slots(maxSize + 1);
        for (Variable field : clazz.fields()) {
            if (field.getType() instanceof FunctionType) {
                Object funcObj = slots.getRef(field.getOffset());
                if (funcObj != null) {
                    funcObj.copyAndSet(newSlots);
                }
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
        java.lang.Object val = null;
        Register r1 = ins.getRegOperand(0);
        if (!registers.isRef(r1)) {
            Offset offset = ins.getOffsetOperand(1);
            Type type = area.getTypeByIdx(offset.getOffset());
            if (typeIsEquals(type, PrimitiveType.Integer)
                    || typeIsEquals(type, PrimitiveType.Byte)) {
                val = registers.getInt(r1);
            } else if (typeIsEquals(type, PrimitiveType.Float)) {
                val = registers.getFloat(r1);
            } else if (typeIsEquals(type, PrimitiveType.Boolean)) {
                val = registers.getInt(r1) == 1 ? "true" : "false";
            }
        } else {
            val = registers.getRef(r1);
            if (val != null) {
                val = val.toString();
            }
        }
        if (val != null && val.equals("\\n")) {
            System.out.println();
        } else {
            System.out.print(val);
        }
    }

    private void jump(Instruction ins) {
        Offset offset = ins.getOffsetOperand(0);
        registers.setInt(Register.PC, offset.getOffset());
    }

    private void jumpZ(Instruction ins) {
        int val = registers.getInt(Register.ZERO);
        if (val == 0) {
            Offset offset = ins.getOffsetOperand(0);
            registers.setInt(Register.PC, offset.getOffset());
        }
    }

    private void jumpNZ(Instruction ins) {
        int val = registers.getInt(Register.ZERO);
        if (val != 0) {
            Offset offset = ins.getOffsetOperand(0);
            registers.setInt(Register.PC, offset.getOffset());
        }
    }

    private void i2b(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        int val = registers.getInt(r1);
        registers.setInt(r2, (byte)val);
    }

    private void i2f(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        int val = registers.getInt(r1);
        registers.setFloat(r2, (float) val);
    }

    private void i2s(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        int val = registers.getInt(r1);
        registers.setRef(r2, StringPool.getStrObj(String.valueOf(val)));
    }

    private void z2s(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        int val = registers.getInt(r1);
        String str = val == 1 ? Keyword.TRUE : Keyword.FALSE;
        registers.setRef(r2, StringPool.getStrObj(str));
    }

    private void f2i(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        float val = registers.getFloat(r1);
        registers.setInt(r2, (int) val);
    }

    private void f2s(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        float val = registers.getFloat(r1);
        registers.setRef(r2, StringPool.getStrObj(String.valueOf(val)));
    }

    private void newArr(Instruction ins) {
        Register lenR = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        Register tR = ins.getRegOperand(2);
        Class clazz = area.getClassByIdx(offset.getOffset());
        Object arrObj = clazz.newArrayObj(registers.getInt(lenR));
        registers.setRef(tR, arrObj);
    }

    private void arrLen(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        int address = registers.getInt(r1) + offset.getOffset();
        Object ref = stackMemory.getRef(address);
        Register r3 = ins.getRegOperand(2);
        registers.setInt(r3, ref.arrayLength());
    }

    private void iaload(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        int[] ints = registers.getRef(r1).ints();
        registers.setInt(r3, ints[registers.getInt(r2)]);
    }

    private void faload(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        float[] floats = registers.getRef(r1).floats();
        registers.setFloat(r3, floats[registers.getInt(r2)]);
    }

    private void aaload(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        Object[] objs = registers.getRef(r1).objs();
        registers.setRef(r3, objs[registers.getInt(r2)]);
    }

    private void iastore(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        int val = registers.getInt(r1);
        int[] ints = registers.getRef(r2).ints();
        ints[registers.getInt(r3)] = val;
    }

    private void fastore(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        float val = registers.getFloat(r1);
        float[] floats = registers.getRef(r2).floats();
        floats[registers.getInt(r3)] = val;
    }

    private void aastore(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Register r2 = ins.getRegOperand(1);
        Register r3 = ins.getRegOperand(2);
        Object val = registers.getRef(r1);
        Object[] objs = registers.getRef(r2).objs();
        objs[registers.getInt(r3)] = val;
    }

    private void newInstance(Instruction ins) {
        Offset offset = ins.getOffsetOperand(1);
        Register resultR = ins.getRegOperand(2);
        Class clazz = area.getClassByIdx(offset.getOffset());
        Object ref = clazz.newObj();
        registers.setRef(resultR, ref);
    }

    private void newFuncObj(Instruction ins) {
        Offset offset = ins.getOffsetOperand(1);
        Register resultR = ins.getRegOperand(2);
        Function function = area.getFunctionByIdx(offset.getOffset());
        // 将闭包引用的自由变量打包
        Object ref = new Object(function);
        Set<Variable> upValues = function.getClosureVariables();
        if (upValues != null) {
            Slots upValueSlots = ref.upValueSlots();
            for (Variable value : upValues) {
                int address = registers.getInt(Register.BP) - value.getOffset();
                setValByType(value.getType(), stackMemory, upValueSlots, address, value.getOffset());
            }
        }
        registers.setRef(resultR, ref);
    }


    private void getField(Instruction ins) {
        Register refR = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        Register resultR = ins.getRegOperand(2);
        Object ref = registers.getRef(refR);
        String name = area.getStrConstByIdx(offset.getOffset());
        Variable variable = ref.clazz().findField(name);
        int id = variable.getOffset();
        Slots fieldSlots = ref.fieldSlots();
        Type type = variable.getType();
        setValByType(type, fieldSlots, registers, id, resultR.getIdx());
    }

    private void putField(Instruction ins) {
        Register valR = ins.getRegOperand(0);
        Object ref = registers.getRef(ins.getRegOperand(1));
        String name = area.getStrConstByIdx(ins.getOffsetOperand(2).getOffset());
        Variable field = ref.clazz().findField(name);
        int id = field.getOffset();
        Type type = field.getType();
        if (typeIsEquals(type, PrimitiveType.Integer)
                || typeIsEquals(type, PrimitiveType.Boolean)
                || typeIsEquals(type, PrimitiveType.Byte)) {
            ref.fieldSlots().setInt(id, registers.getInt(valR));
        } else if (typeIsEquals(type, PrimitiveType.Float)) {
            ref.fieldSlots().setFloat(id, registers.getFloat(valR));
        } else {
            ref.fieldSlots().setRef(id, registers.getRef(valR));
        }
        setValByType(type, registers, ref.fieldSlots(), valR.getIdx(), id);
    }

    private void getModuleVar(Instruction ins) {
        Offset offset1 = ins.getOffsetOperand(0);
        Offset offset2 = ins.getOffsetOperand(1);
        Register tR = ins.getRegOperand(2);
        String moduleName = area.getStrConstByIdx(offset1.getOffset());
        String varName = area.getStrConstByIdx(offset2.getOffset());
        Variable variable = area.getModuleVar(moduleName, varName);
        Slots slots = area.moduleVarSlots(moduleName);
        int id = variable.getOffset();
        Type type = variable.getType();
        setValByType(type, slots, registers, id, tR.getIdx());
    }

    private void putModuleVar(Instruction ins) {
        Register valR = ins.getRegOperand(0);
        Offset offset1 = ins.getOffsetOperand(1);
        Offset offset2 = ins.getOffsetOperand(2);
        String moduleName = area.getStrConstByIdx(offset1.getOffset());
        String varName = area.getStrConstByIdx(offset2.getOffset());
        Variable variable = area.getModuleVar(moduleName, varName);
        Slots slots = area.moduleVarSlots(moduleName);
        int id = variable.getOffset();
        Type type = variable.getType();
        setValByType(type, registers, slots, valR.getIdx(), id);
    }

    private void getUpValueVar(Instruction ins) {
        Offset offset1 = ins.getOffsetOperand(0);
        Register tR = ins.getRegOperand(2);
        Object ref = registers.getRef(Register.UP);
        String varName = area.getStrConstByIdx(offset1.getOffset());
        String funcName = area.getFunctionSignature(ref.function());
        Variable variable = area.getUpValueVar(funcName, varName);
        int id = variable.getOffset();
        Type type = variable.getType();
        setValByType(type, ref.upValueSlots(), registers, id, tR.getIdx());
    }

    private void putUpValueVar(Instruction ins) {
        Register valR = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(1);
        Object ref = registers.getRef(Register.UP);
        String varName = area.getStrConstByIdx(offset.getOffset());
        String funcName = area.getFunctionSignature(ref.function());
        Variable variable = area.getUpValueVar(funcName, varName);
        int id = variable.getOffset();
        Type type = variable.getType();
        setValByType(type, registers, ref.upValueSlots(), valR.getIdx(), id);
    }

    private void getStaticField(Instruction ins) {
        Offset offset1 = ins.getOffsetOperand(0);
        Offset offset2 = ins.getOffsetOperand(1);
        Register tR = ins.getRegOperand(2);
        Class clazz = area.getClassByIdx(offset1.getOffset());
        if (!clazz.isInited()) {
            invokeStaticInit(ins.step(), clazz);
            clazz.setInit(true);
            return;
        }
        String className = area.getStrConstByIdx(offset1.getOffset());
        String fieldName = area.getStrConstByIdx(offset2.getOffset());
        Variable field = area.getStaticField(className, fieldName);
        int id = field.getOffset();
        Slots slots = area.staticVarSlots(className);
        Type type = field.getType();
        setValByType(type, slots, registers, id, tR.getIdx());
    }

    private void putStaticField(Instruction ins) {
        Register valR = ins.getRegOperand(0);
        Offset offset1 = ins.getOffsetOperand(1);
        Offset offset2 = ins.getOffsetOperand(2);
        Class clazz = area.getClassByIdx(offset1.getOffset());
        if (!clazz.isInited()) { // trick
            int step = ins.step() + OpCode.LOAD.getAddressingType().getBytes() + 1;
            invokeStaticInit(step, clazz);
            clazz.setInit(true);
            return;
        }
        String className = area.getStrConstByIdx(offset1.getOffset());
        String fieldName = area.getStrConstByIdx(offset2.getOffset());
        Variable field = area.getStaticField(className, fieldName);
        int id = field.getOffset();
        Slots slots = area.staticVarSlots(className);
        Type type = field.getType();
        setValByType(type, registers, slots, valR.getIdx(), id);
    }

    private void invokeStaticInit(int reverse, Class clazz) {
        Function method = clazz.getStaticInitMethod();
        int pos = area.getFunctionPos(method);
        retAddressStack.push(registers.getInt(Register.PC) - reverse);
        registers.setInt(Register.PC, pos);
    }

    private void setValByType(Type type, Slots srcSlots, Slots destSlots,
                              int srcId, int destId) {
        if (typeIsEquals(type, PrimitiveType.Integer)
                || typeIsEquals(type, PrimitiveType.Boolean)
                || typeIsEquals(type, PrimitiveType.Byte)) {
            int val = srcSlots.getInt(srcId);
            if (typeIsEquals(type, PrimitiveType.Byte)) {
                val = val & 0xff;
            }
            destSlots.setInt(destId, val);
        } else if (typeIsEquals(type, PrimitiveType.Float)) {
            destSlots.setFloat(destId, srcSlots.getFloat(srcId));
        }  else {
            destSlots.setRef(destId, srcSlots.getRef(srcId));
        }
    }

    private void invokeVirtual(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Object ref = registers.getRef(r1);
        Offset offset = ins.getOffsetOperand(2);
        int methodPos = area.getFuncPosByIdx(ref.clazz(), offset.getOffset());
        retAddressStack.push(registers.getInt(Register.PC));
        registers.setInt(Register.PC, methodPos);
    }

    private void invokeSpecial(Instruction ins) {
        Register r1 = ins.getRegOperand(0);
        Offset offset = ins.getOffsetOperand(2);
        int methodPos = area.getSpecialFuncPosByIdx(offset.getOffset());
        retAddressStack.push(registers.getInt(Register.PC));
        registers.setInt(Register.PC, methodPos);
    }

    private void invokeStatic(Instruction ins) {
        Offset offset1 = ins.getOffsetOperand(1);
        Offset offset2 = ins.getOffsetOperand(2);
//        String classConst = area.getConstByIdx(offset1.getOffset()).getStrVal();
        Function function = area.getFunctionByIdx(offset2.getOffset());
        if (function.isBuiltIn()) {
            processBuiltInFunc(function);
            return;
        }
        int methodPos = area.getFuncPosByIdx(offset2.getOffset());
        retAddressStack.push(registers.getInt(Register.PC));
        registers.setInt(Register.PC, methodPos);
    }

    private void processBuiltInFunc(Function function) {
        String key = area.getFunctionSignature(function);
        if (!function.isMethod()) {
            String tmp = key.substring(key.indexOf(function.name()));
            key = DEFAULT + "_" + tmp;
        }
        BuiltInFunction func = Registry.findBuiltin(key);
        int pos = registers.getInt(Register.SP) + 1;
        stackMemory.setOffset(pos);
        func.invoke(stackMemory, registers);
        stackMemory.setOffset(0);
    }

    private void invokeVarFunc(Instruction ins) {
        Object ref = registers.getRef(ins.getRegOperand(0));
        Function function  = ref.function();
        int methodPos = area.getFunctionPos(function);
        registers.setRef(Register.UP, ref);
        retAddressStack.push(registers.getInt(Register.PC));
        registers.setInt(Register.PC, methodPos);
    }

    private void getFuncLocals(Instruction ins) {
        Object ref = registers.getRef(ins.getRegOperand(0));
        int localsSize = ref.function().getVariables().size();
        registers.setInt(ins.getRegOperand(1), localsSize);
    }

    private boolean typeIsEquals(Type type1, Type type2) {
        return type1.name().equals(type2.name());
    }

    private void ret() {
        registers.setInt(Register.PC, retAddressStack.pop());
    }

    public void onStop() {
        System.out.println("-----------------------虚拟机停止-------------------------------");
    }
}
