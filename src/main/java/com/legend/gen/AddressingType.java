package com.legend.gen;

/**
 * @author Legend
 * @data by on 21-1-19.
 * @description 寻址模式
 */
public enum AddressingType {
    REGISTER(3), // r1,r2,r3
    REGISTER1(2), // r1,r2  r1-> r2
    OFFSET(2), // offset
    OFFSET1(4), // r1,offset r2
    OFFSET2(4), // r1 r2,offset
    OFFSET3(5), // offset1,offset2 r1
    OFFSET4(5), // r1,offset1,offset2
    IMMEDIATE(3), // r1,IMMEDIATE
    NOP(0),
    NATIVE(1);

    private int bytes;

    AddressingType(int bytes) {
        this.bytes = bytes;
    }

    public int getBytes() {
        return bytes;
    }
}
