package com.legend.gen;

import com.legend.gen.operand.ImmediateNumber;
import com.legend.gen.operand.Label;
import com.legend.gen.operand.Offset;
import com.legend.gen.operand.Register;
import com.legend.ir.Constant;
import com.legend.ir.TACInstruction;
import com.legend.ir.TACProgram;
import com.legend.ir.TACType;
import com.legend.semantic.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-12-29.
 * @description 代码生成器
 */
public class OpCodeGenerator {

    private OpCodeProgram program = new OpCodeProgram();
    private MethodArea area = MethodArea.getInstance();
    private TACProgram tacProgram;

    public OpCodeGenerator(TACProgram tacProgram) {
        this.tacProgram = tacProgram;
    }

    public void generate() {
        if (tacProgram == null) return;
        List<TACInstruction> tacInstructions = tacProgram.getInstructionList();
        Map<String, Integer> labelToPosMap = new HashMap<>();
        for (TACInstruction tac : tacInstructions) {
            program.addComment(tac.toString());
            switch (tac.getType()) {
                case LABEL:
                    Function function = tacProgram.getFunction(tac);
                    if (function != null) {
                        area.addFunction(function, program.getCurPosition());
                        genPrologue(function);
                    } else {
                        emitNop();
                        labelToPosMap.put((String) tac.getArg1(), program.getCurPosition());
                    }
                    break;
                case ASSIGN:
                    genAssign(tac);
                    break;
                case GOTO:
                    genGoto(tac);
                    break;
                case IF_T:
                case IF_F:
                    genIf(tac);
                    break;
                case NEW_ARRAY:
                    genNewArray(tac);
                    break;
                case ARRAY_LEN:
                    emitArrLen(Register.SP, (Symbol) tac.getArg1(), Register.R2);
                    emitStore(Register.R2, tac.getResult());
                    break;
                case NEW_INSTANCE:
                    genNewInstance(tac);
                    break;
                case GET_FIELD:
                    genGetField(tac);
                    break;
                case GET_STATIC_FIELD:
                    genGetStaticField(tac);
                    break;
                case ARG:
                    emitPush((Symbol) tac.getArg1());
                    break;
                case PARAM:
                    fixParamOffset(tac);
                    break;
                case INVOKE_VIRTUAL:
                case INVOKE_SPECIAL:
                    genInvokeInstanceMethod(tac);
                    break;
                case INVOKE_STATIC:
                    genInvokeStaticMethod(tac);
                    break;
                case PRINT:
                    emitLoad((Symbol) tac.getArg1(), Register.R1);
                    emitPrint(Register.R1);
                    break;
                case RETURN:
                    genReturn(tac);
                    break;
            }
        }
        relocation(labelToPosMap, program.getInstructions());
    }

    private void genPrologue(Function function) { // 函数调用开始
        emitMove(Register.PC, Register.RA);
        emitPush(Register.BP);
        emitMove(Register.SP, Register.BP);
        emitDec(Register.SP, function.getLocalsSize());
    }

    // 函数参数默认偏移为正 内存高地址->低地址 所以取值时会变成负
    // 但参数相对于bp寄存器地址为正，所以这里将本身转换为负 就能正确取到参数
    private void fixParamOffset(TACInstruction tac) {
        Symbol symbol = (Symbol) tac.getArg1();
        symbol.setOffset(-symbol.getOffset());
    }

    private void genEpilogue() { // 函数调用尾声
        emitMove(Register.BP, Register.SP);
        emitPop(Register.BP);
    }

    // 将label实际的位置重定位
    private void relocation(Map<String, Integer> posMap, List<Instruction> instructions) {
        for (Instruction ins : instructions) {
            if (ins.getOpCode() == OpCode.JUMP || ins.getOpCode() == OpCode.JUMP_Z
                    || ins.getOpCode() == OpCode.JUMP_NZ) {
                Label label = ins.getLabel(0);
                int pos = posMap.get(label.getLabel());
                label.setOffset(pos);
            }
        }
    }

    private void genInvokeStaticMethod(TACInstruction tac) {
        Constant classConst = (Constant) tac.getArg1();
        Constant methodConst = (Constant) tac.getArg2();
        Function function = area.getFunctionByIdx(methodConst.getOffset());
        emitInvokeStatic(classConst, methodConst);
        emitInc(Register.SP, function.getVariables().size());
        emitStore(Register.RV, tac.getResult());
    }

    private void genInvokeInstanceMethod(TACInstruction tac) {
        Variable objRef = (Variable) tac.getArg1();
        Constant methodConst = (Constant) tac.getArg2();
        emitLoad(objRef, Register.R1);
        Function function = area.getFunctionByIdx(methodConst.getOffset());
        if (tac.getType() == TACType.INVOKE_VIRTUAL) {
            emitInvokeVirtual(Register.R1, methodConst);
        } else if (tac.getType() == TACType.INVOKE_SPECIAL) {
            emitInvokeSpecial(Register.R1, methodConst);
        }
        emitInc(Register.SP, function.getVariables().size());
        emitStore(Register.RV, tac.getResult());
    }

    private void genReturn(TACInstruction tac) {
        if (tac.getArg1() != null) {
            emitLoad((Symbol) tac.getArg1(), Register.RV);
        }
        genEpilogue();
        emitRet();
    }

    private void genGetStaticField(TACInstruction tac) {
        Variable res = tac.getResult();
        // todo
        Constant classConst = (Constant) tac.getArg1();
        Constant fieldConst = (Constant) tac.getArg2();
        emitGetStaticField(classConst, fieldConst, Register.R1);
        emitStore(Register.R1, res);
    }

    private void genGetField(TACInstruction tac) {
        Variable res = tac.getResult();
        Symbol arg1 = (Symbol) tac.getArg1();
        Constant fieldConst = (Constant) tac.getArg2();
        emitLoad(arg1, Register.R1);
        emitGetField(Register.R1, fieldConst, Register.R2);
        emitStore(Register.R2, res);
    }

    private void genNewInstance(TACInstruction tac) {
        Variable res = tac.getResult();
        emitNewInstance((Constant) tac.getArg1(), Register.R1);
        emitStore(Register.R1, res);
    }

    private void genNewArray(TACInstruction tac) {
        Variable res = tac.getResult();
        Constant typeConst = (Constant) tac.getArg1();
        Symbol length = (Symbol) tac.getArg2();
        emitLoad(length, Register.R1);
        emitNewArr(Register.R1, typeConst, Register.R2);
        emitStore(Register.R2, res);
    }

    private void genIf(TACInstruction tac) {
        Symbol condition = (Symbol) tac.getArg1();
        emitLoad(condition, Register.ZERO);
        TACInstruction label = (TACInstruction) tac.getArg2();
        if (tac.getType() == TACType.IF_F) {
            emitJumpZ((String) label.getArg1());
        } else if (tac.getType() == TACType.IF_T) {
            emitJumpNZ((String) label.getArg1());
        }
    }

    private void genGoto(TACInstruction tac) {
        TACInstruction label = (TACInstruction) tac.getArg1();
        emitJump((String) label.getArg1());
    }

    private void genAssign(TACInstruction tac) {
        if (tac.getArg2() == null) {
            Symbol arg1 = (Symbol) tac.getArg1();
            emitLoad(arg1, Register.R1);
            emitStore(Register.R1, arg1);
        } else {
            // 首先处理数组赋值与取值
            if (tac.isArrayAssign()) {
                emitArrayAssign(tac);
            } else if (tac.getArg1() instanceof Variable &&
                    ((Variable) tac.getArg1()).isArrayType()) {
                emitArrayGetVal(tac);
            } else { // 普通二元运算
                genBinaryOp(tac);
            }
        }
    }

    private void emitArrayAssign(TACInstruction tac) {
        Variable base = tac.getResult();
        Symbol idx = (Symbol) tac.getArg1();
        Symbol val = (Symbol) tac.getArg1();
        emitLoad(val, Register.R1);
        emitLoad(base, Register.R2);
        emitLoad(idx, Register.R3);
        Type type = ((ArrayType)base.getType()).baseType();
        if (type == PrimitiveType.Integer) {
            emitIAStore(Register.R1, Register.R2, Register.R3);
        } else if (type == PrimitiveType.Float) {
            emitFAStore(Register.R1, Register.R2, Register.R3);
        } else {
            emitAAStore(Register.R1, Register.R2, Register.R3);
        }
    }

    private void emitArrayGetVal(TACInstruction tac) {
        Variable res = tac.getResult();
        Variable base = (Variable) tac.getArg1();
        Symbol idx = (Symbol) tac.getArg1();
        emitLoad(base, Register.R1);
        emitLoad(idx, Register.R2);
        Type type = ((ArrayType)base.getType()).baseType();
        if (type == PrimitiveType.Integer) {
            emitIALoad(Register.R1, Register.R2, Register.R3);
        } else if (type == PrimitiveType.Float) {
            emitFALoad(Register.R1, Register.R2, Register.R3);
        } else {
            emitAALoad(Register.R1, Register.R2, Register.R3);
        }
        emitStore(Register.R3, res);
    }

    private void genBinaryOp(TACInstruction tac)  {
        Type type = tac.getResult().getType();
        Symbol arg1 = (Symbol) tac.getArg1();
        Symbol arg2 = (Symbol) tac.getArg2();
        Register r1 = Register.R1, r2 = Register.R2, r3 = Register.R3;
        emitLoad(arg1, r1);
        emitLoad(arg2, r2);
        switch (tac.getOp()) {
            case "+":
                emitAdd(type, r1, r2, r3);
                break;
            case "-":
                emitSub(type, r1, r2, r3);
                break;
            case "*":
                emitMul(type, r1, r2, r3);
                break;
            case "/":
                emitDIV(type, r1, r2, r3);
                break;
            case "%":
                emitIMod(r1, r2, r3);
                break;
            case "&":
                emitBitAnd(r1, r2, r3);
                break;
            case "|":
                emitBitOr(r1, r2, r3);
                break;
            case "^":
                emitXOR(r1, r2, r3);
                break;
            case "<<":
                emitLeftShift(r1, r2, r3);
                break;
            case ">>":
                emitRightShift(r1, r2, r3);
                break;
            case "<":
                emitCmpLT(r1, r2, r3);
                break;
            case "<=":
                emitCmpLE(r1, r2, r3);
                break;
            case ">":
                emitCmpGT(r1, r2, r3);
                break;
            case ">=":
                emitCmpGE(r1, r2, r3);
                break;
            case "==":
                emitCmpEQ(r1, r2, r3);
                break;
            case "!=":
                emitCmpNE(r1, r2, r3);
                break;
            case "&&":
                emitCmpAnd(r1, r3, r3);
                break;
            case "||":
                emitCmpOr(r1, r2, r3);
                break;
        }
        emitStore(r3, tac.getResult());
    }

    private void emitDIV(Type type, Register r1, Register r2, Register r3) {
        if (type == PrimitiveType.Integer) {
            emitIDiv(r1, r2, r3);
        } else if (type == PrimitiveType.Float) {
            emitFDiv(r1, r2, r3);
        }
    }

    private void emitMul(Type type, Register r1, Register r2, Register r3) {
        if (type == PrimitiveType.Integer) {
            emitIMul(r1, r2, r3);
        } else if (type == PrimitiveType.Float) {
            emitFMul(r1, r2, r3);
        }
    }

    private void emitSub(Type type, Register r1, Register r2, Register r3) {
        if (type == PrimitiveType.Integer) {
            emitISub(r1, r2, r3);
        } else if (type == PrimitiveType.Float) {
            emitFSub(r1, r2, r3);
        }
    }

    private void emitAdd(Type type, Register r1, Register r2, Register r3) {
        if (type == PrimitiveType.Integer) {
            emitIAdd(r1, r2, r3);
        } else if (type == PrimitiveType.Float) {
            emitFAdd(r1, r2, r3);
        } else if (type == PrimitiveType.String) {
            emitSAdd(r1, r2, r3);
        }
    }

    private void emitIAdd(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.IADD, r1, r2, r3));
    }

    private void emitISub(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.ISUB, r1, r2, r3));
    }

    private void emitIMul(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.IMUL, r1, r2, r3));
    }

    private void emitIDiv(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.IDIV, r1, r2, r3));
    }

    private void emitIMod(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.IMOD, r1, r2, r3));
    }

    private void emitBitAnd(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.BIT_AND, r1, r2, r3));
    }

    private void emitBitOr(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.BIT_OR, r1, r2, r3));
    }

    private void emitXOR(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.XOR, r1, r2, r3));
    }

    private void emitLeftShift(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.LSHIFT, r1, r2, r3));
    }

    private void emitRightShift(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.RSHIFT, r1, r2, r3));
    }

    private void emitInc(Register r1, int val) {
        emitInc(r1, new Constant(PrimitiveType.Integer, val));
    }


    private void emitInc(Register r1, Constant constant) {
        program.addIns(Instruction.immediate(OpCode.INC, r1, new ImmediateNumber((Integer) constant.getValue())));
    }

    private void emitDec(Register r1, int val) {
        emitDec(r1, new Constant(PrimitiveType.Integer, val));
    }

    private void emitDec(Register r1, Constant c) {
        program.addIns(Instruction.immediate(OpCode.DEC, r1, new ImmediateNumber((Integer) c.getValue())));
    }

    private void emitFAdd(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.FADD, r1, r2, r3));
    }

    private void emitFSub(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.FSUB, r1, r2, r3));
    }

    private void emitFMul(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.FMUL, r1, r2, r3));
    }

    private void emitFDiv(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.FDIV, r1, r2, r3));
    }

    private void emitSAdd(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.SADD, r1, r2, r3));
    }

    private void emitCmpLT(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_LT, r1, r2, r3));
    }

    private void emitCmpLE(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_LE, r1, r2, r3));
    }

    private void emitCmpGT(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_GT, r1, r2, r3));
    }

    private void emitCmpGE(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_GE, r1, r2, r3));
    }

    private void emitCmpEQ(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_EQ,r1, r2, r3));
    }

    private void emitCmpNE(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_NE, r1, r2, r3));
    }

    private void emitCmpAnd(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_AND, r1, r2, r3));
    }

    private void emitCmpOr(Register r1, Register r2, Register r3) {
        program.addIns(Instruction.register(OpCode.CMP_OR, r1, r2, r3));
    }

    // mem -> register
    private void emitLoad( Symbol src, Register dest) {
        if (src instanceof Variable) {
            program.addIns(Instruction.offset1(
                    OpCode.LOAD, Register.BP, new Offset(-src.getOffset()), dest));
        } else if (src instanceof Constant) {
            program.addIns(Instruction.offset1(
                    OpCode.LOAD, Register.CONSTANT, new Offset(src.getOffset()), dest));
        }
    }

    // register -> mem
    private void emitStore(Register src, Symbol res) {
        program.addIns(Instruction.offset2(OpCode.STORE, src, Register.BP,
                new Offset(-res.getOffset())));
    }

    private void emitJump(String label) {
        program.addIns(Instruction.offset(OpCode.JUMP, new Label(label)));
    }

    private void emitJumpZ(String label) {
        program.addIns(Instruction.offset(OpCode.JUMP_Z, new Label(label)));
    }

    private void emitJumpNZ(String label) {
        program.addIns(Instruction.offset(OpCode.JUMP_NZ, new Label(label)));
    }

    private void emitNewArr(Register lenR, Constant typeConst,
                            Register dest) {
        program.addIns(Instruction.offset1(OpCode.NEW_ARR, lenR,
                new Offset(typeConst.getOffset()), dest));
    }

    private void emitArrLen(Register base, Symbol symbol,
                            Register dest) {
        program.addIns(Instruction.offset1(OpCode.ARR_LEN, base,
                new Offset(symbol.getOffset()), dest));
    }

    private void emitFALoad(Register base, Register idx, Register dest) {
        program.addIns(Instruction.register(OpCode.FA_LOAD, base, idx, dest));
    }

    private void emitIALoad(Register base, Register idx, Register dest) {
        program.addIns(Instruction.register(OpCode.IA_LOAD, base, idx, dest));
    }

    private void emitAALoad(Register base, Register idx, Register dest) {
        program.addIns(Instruction.register(OpCode.AA_LOAD, base, idx, dest));
    }

    private void emitFAStore(Register val, Register base, Register idx) {
        program.addIns(Instruction.register(OpCode.FA_STORE, val, base, idx));
    }

    private void emitIAStore(Register val, Register base, Register idx) {
        program.addIns(Instruction.register(OpCode.IA_STORE, val, base, idx));
    }

    private void emitAAStore(Register val, Register base, Register idx) {
        program.addIns(Instruction.register(OpCode.AA_STORE, val, base, idx));
    }

    private void emitNewInstance(Constant classConst, Register dest) {
        program.addIns(Instruction.offset1(OpCode.NEW_INSTANCE,
                Register.CONSTANT, new Offset(classConst.getOffset()), dest));
    }

    private void emitGetField(Register r1, Constant fieldConst, Register dest) {
        program.addIns(Instruction.offset1(OpCode.GET_FIELD,
                r1, new Offset(fieldConst.getOffset()), dest));
    }

    private void emitGetStaticField(Constant classConst, Constant fieldConst, Register dest) {
        program.addIns(Instruction.offset3(OpCode.GET_S_FIELD,
                new Offset(classConst.getOffset()),
                new Offset(fieldConst.getOffset()), dest));
    }

    private void emitInvokeVirtual(Register obj, Constant methodIdx) {
        program.addIns(Instruction.offset2(OpCode.INVOKE_VIRTUAL,
                obj, Register.SP, new Offset(methodIdx.getOffset())));
    }

    private void emitInvokeSpecial(Register obj, Constant methodIdx) {
        program.addIns(Instruction.offset2(OpCode.INVOKE_SPECIAL,
                obj, Register.SP, new Offset(methodIdx.getOffset())));
    }

    private void emitInvokeStatic(Constant classConst, Constant methodIdx) {
        if (classConst == null) { // todo 全局方法
            return;
        }
        program.addIns(Instruction.offset4(OpCode.INVOKE_STATIC,
                Register.SP, new Offset(classConst.getOffset()),
                new Offset(methodIdx.getOffset())));
    }

    private void emitRet() {
        program.addIns(new Instruction(OpCode.RET));
    }

    private void emitPrint(Register r1) {
        program.addIns(Instruction.print(r1));
    }

    private void emitMove(Register src, Register dest) {
        program.addIns(Instruction.register1(OpCode.MOVE, src, dest));
    }

    private void emitPush(Symbol src) {
        emitLoad(src, Register.R1);
        emitPush(Register.R1);
    }

    private void emitPush(Register r1) {
        program.addIns(Instruction.offset2(OpCode.STORE, r1, Register.SP, new Offset(0)));
        emitDec(Register.SP, 1);
    }

    private void emitPop(Register r1) {
        emitInc(Register.SP, 1);
        program.addIns(Instruction.offset1(OpCode.LOAD, Register.SP, new Offset(0), r1));
    }

    private void emitNop() {
        program.addIns(new Instruction(OpCode.NOP));
    }

    public OpCodeProgram getProgram() {
        return program;
    }
}
