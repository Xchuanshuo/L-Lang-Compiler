package com.legend.gen;

public class Constant {

    // 基本运算
    public static final byte IADD = 0x01;
    public static final byte ISUB = 0x02;
    public static final byte IMUL = 0x03;
    public static final byte IDIV = 0x04;
    public static final byte IMOD = 0x05;
    public static final byte BIT_AND = 0x06;
    public static final byte BIT_OR = 0x07;
    public static final byte XOR = 0x08;
    public static final byte LSHIFT = 0x09;
    public static final byte RSHIFT = 0x0A;
    public static final byte INC = 0x0B;
    public static final byte INC_1 = 0x0C;
    public static final byte DEC = 0x0D;
    public static final byte FADD = 0x10;
    public static final byte FSUB = 0x11;
    public static final byte FMUL = 0x12;
    public static final byte FDIV = 0x13;
    public static final byte SADD = 0x20;

    // 比较
    public static final byte ICMP_LT = 0x21;
    public static final byte ICMP_LE = 0x22;
    public static final byte ICMP_GT = 0x23;
    public static final byte ICMP_GE = 0x24;
    public static final byte ICMP_EQ = 0x25;
    public static final byte ICMP_NE = 0x26;
    public static final byte FCMP_LT = 0x27;
    public static final byte FCMP_LE = 0x28;
    public static final byte FCMP_GT = 0x29;
    public static final byte FCMP_GE = 0x2A;
    public static final byte FCMP_EQ = 0x2B;
    public static final byte FCMP_NE = 0x2C;
    public static final byte ICMP_AND = 0x2D;
    public static final byte ICMP_OR = 0x2E;

    // 分支与跳转
    public static final byte JUMP = 0x30;
    public static final byte JUMP_Z = 0x31;
    public static final byte JUMP_NZ = 0x32;

    // 内存装载与存储
    public static final byte LOAD = 0x40;
    public static final byte STORE = 0x41;
    public static final byte MOVE = 0x42;

    // 数组
    public static final byte NEW_ARR = 0x50;
    public static final byte ARR_LEN = 0x51;
    public static final byte FA_LOAD = 0x52;
    public static final byte IA_LOAD = 0x53;
    public static final byte AA_LOAD = 0x54;
    public static final byte FA_STORE = 0x55;
    public static final byte IA_STORE = 0x56;
    public static final byte AA_STORE = 0x57;

    // 类与对象
    public static final byte NEW_INSTANCE = 0x60;
    public static final byte NEW_FUNC_OBJ = 0x61;
    public static final byte GET_FIELD = 0x62;
    public static final byte GET_S_FIELD = 0x63;
    public static final byte PUT_FIELD = 0x64;
    public static final byte PUT_S_FIELD = 0x65;
    public static final byte GET_MODULE_VAR = 0x66;
    public static final byte PUT_MODULE_VAR = 0x67;
    public static final byte GET_UPVALUE_VAR = 0x68;
    public static final byte PUT_UPVALUE_VAR = 0x69;

    // 函数调用相关
    public static final byte INVOKE_VIRTUAL = 0x70;
    public static final byte INVOKE_SPECIAL = 0x71;
    public static final byte INVOKE_STATIC = 0x72;
    public static final byte INVOKE_VAR_FUNC = 0x73;
    public static final byte GET_FUNC_LOCALS = 0x74;
    // 返回
    public static final byte RET = 0x76;

    // 类型转换
    public static final byte I2F = (byte) 0x80;
    public static final byte I2B = (byte) 0x81;
    public static final byte I2S = (byte) 0x82;
    public static final byte F2I = (byte) 0x83;
    public static final byte F2S = (byte) 0x84;

    // native
    public static final byte PRINT = (byte) 0xF0;
    public static final byte NOP = (byte) 0xFF;

}
