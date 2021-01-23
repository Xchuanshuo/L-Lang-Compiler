package com.legend.gen;


import static com.legend.gen.AddressingType.*;

/**
 * @author Legend
 * @data by on 21-1-18.
 * @description 操作码
 *  指令1字节 偏移量:2字节 寄存器:1字节 立即数:1字节
 */
public class OpCode {

    private byte code;
    private String name;
    private AddressingType addressingType;
    private static OpCode[] codes = new OpCode[256];

    public OpCode(byte code, String name, AddressingType type) {
        this.code = code;
        this.name = name;
        this.addressingType = type;
        if (code < 0) {
            codes[256 + code] = this;
        } else {
            codes[code] = this;
        }
    }

    public static final OpCode IADD = new OpCode(Constant.IADD, "IADD", REGISTER);
    public static final OpCode ISUB = new OpCode(Constant.ISUB, "ISUB", REGISTER);
    public static final OpCode IMUL = new OpCode(Constant.IMUL, "IMUL", REGISTER);
    public static final OpCode IDIV = new OpCode(Constant.IDIV, "IDIV", REGISTER);
    public static final OpCode IMOD = new OpCode(Constant.IMOD, "IMOD", REGISTER);
    public static final OpCode BIT_AND = new OpCode(Constant.BIT_AND, "BIT_AND", REGISTER);
    public static final OpCode BIT_OR = new OpCode(Constant.BIT_OR, "BIT_OR", REGISTER);
    public static final OpCode XOR = new OpCode(Constant.XOR, "XOR", REGISTER);
    public static final OpCode LSHIFT = new OpCode(Constant.LSHIFT, "LSHIFT", REGISTER);
    public static final OpCode RSHIFT = new OpCode(Constant.RSHIFT, "RSHIFT", REGISTER);
    public static final OpCode INC = new OpCode(Constant.INC, "INC", IMMEDIATE);
    public static final OpCode DEC = new OpCode(Constant.DEC, "DEC", IMMEDIATE);
    public static final OpCode FADD = new OpCode(Constant.FADD, "FADD", REGISTER);
    public static final OpCode FSUB = new OpCode(Constant.FSUB, "FSUB", REGISTER);
    public static final OpCode FMUL = new OpCode(Constant.FMUL, "FMUL", REGISTER);
    public static final OpCode FDIV = new OpCode(Constant.FDIV, "FDIV", REGISTER);
    public static final OpCode SADD = new OpCode(Constant.SADD, "SADD", REGISTER);

    public static final OpCode ICMP_LT = new OpCode(Constant.ICMP_LT, "ICMP_LT", REGISTER);
    public static final OpCode ICMP_LE = new OpCode(Constant.ICMP_LE, "ICMP_LE", REGISTER);
    public static final OpCode ICMP_GT = new OpCode(Constant.ICMP_GT, "ICMP_GT", REGISTER);
    public static final OpCode ICMP_GE = new OpCode(Constant.ICMP_GE, "ICMP_GE", REGISTER);
    public static final OpCode ICMP_EQ = new OpCode(Constant.ICMP_EQ, "ICMP_EQ", REGISTER);
    public static final OpCode ICMP_NE = new OpCode(Constant.ICMP_NE, "ICMP_NE", REGISTER);
    public static final OpCode FCMP_LT = new OpCode(Constant.FCMP_LT, "FCMP_LT", REGISTER);
    public static final OpCode FCMP_LE = new OpCode(Constant.FCMP_LE, "FCMP_LE", REGISTER);
    public static final OpCode FCMP_GT = new OpCode(Constant.FCMP_GT, "FCMP_GT", REGISTER);
    public static final OpCode FCMP_GE = new OpCode(Constant.FCMP_GE, "FCMP_GE", REGISTER);
    public static final OpCode FCMP_EQ = new OpCode(Constant.FCMP_EQ, "FCMP_EQ", REGISTER);
    public static final OpCode FCMP_NE = new OpCode(Constant.FCMP_NE, "FCMP_NE", REGISTER);
    public static final OpCode ICMP_AND = new OpCode(Constant.ICMP_AND, "ICMP_AND", REGISTER);
    public static final OpCode ICMP_OR = new OpCode(Constant.ICMP_OR, "ICMP_OR", REGISTER);

    public static final OpCode JUMP = new OpCode(Constant.JUMP, "JUMP", OFFSET);
    public static final OpCode JUMP_Z = new OpCode(Constant.JUMP_Z, "JUMP_Z", OFFSET);
    public static final OpCode JUMP_NZ = new OpCode(Constant.JUMP_NZ, "JUMP_NZ", OFFSET);

    public static final OpCode LOAD = new OpCode(Constant.LOAD, "LOAD", OFFSET1);
    public static final OpCode STORE = new OpCode(Constant.STORE, "STORE", OFFSET2);
    public static final OpCode MOVE = new OpCode(Constant.MOVE, "MOVE", REGISTER1);

    public static final OpCode NEW_ARR = new OpCode(Constant.NEW_ARR, "NEW_ARR", OFFSET1);
    public static final OpCode ARR_LEN = new OpCode(Constant.ARR_LEN, "ARR_LEN", OFFSET1);
    public static final OpCode FA_LOAD = new OpCode(Constant.FA_LOAD, "FA_LOAD", REGISTER);
    public static final OpCode IA_LOAD = new OpCode(Constant.IA_LOAD, "IA_LOAD", REGISTER);
    public static final OpCode AA_LOAD = new OpCode(Constant.AA_LOAD, "AA_LOAD", REGISTER);
    public static final OpCode FA_STORE = new OpCode(Constant.FA_STORE, "FA_STORE", REGISTER);
    public static final OpCode IA_STORE = new OpCode(Constant.IA_STORE, "IA_STORE", REGISTER);
    public static final OpCode AA_STORE = new OpCode(Constant.AA_STORE, "AA_STORE", REGISTER);

    public static final OpCode NEW_INSTANCE = new OpCode(Constant.NEW_INSTANCE, "NEW_INSTANCE", OFFSET1);
    public static final OpCode GET_FIELD = new OpCode(Constant.GET_FIELD, "GET_FIELD", OFFSET1);
    public static final OpCode GET_S_FIELD = new OpCode(Constant.GET_S_FIELD, "GET_S_FIELD", OFFSET3);

    public static final OpCode INVOKE_VIRTUAL = new OpCode(Constant.INVOKE_VIRTUAL, "INVOKE_VIRTUAL", OFFSET2);
    public static final OpCode INVOKE_SPECIAL = new OpCode(Constant.INVOKE_SPECIAL, "INVOKE_SPECIAL", OFFSET2);
    public static final OpCode INVOKE_STATIC = new OpCode(Constant.INVOKE_STATIC, "INVOKE_STATIC", OFFSET4);

    public static final OpCode I2F = new OpCode(Constant.I2F, "i2f", REGISTER1);
    public static final OpCode I2B = new OpCode(Constant.I2B, "i2b", REGISTER1);
    public static final OpCode I2S = new OpCode(Constant.I2S, "i2s", REGISTER1);
    public static final OpCode F2I = new OpCode(Constant.F2I, "f2i", REGISTER1);
    public static final OpCode F2S = new OpCode(Constant.F2S, "f2s", REGISTER1);


    public static final OpCode RET = new OpCode(Constant.RET, "RET", AddressingType.NOP);
    public static final OpCode PRINT = new OpCode(Constant.PRINT, "PRINT", OFFSET5);
    public static final OpCode NOP = new OpCode(Constant.NOP, "NOP", AddressingType.NOP);



    public static OpCode getOpcode(byte code) {
        if (code < 0) {
            return codes[256 + code];
        } else {
            return codes[code];
        }
    }

    public AddressingType getAddressingType() {
        return addressingType;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
