package com.legend.gen.operand;

import com.legend.exception.GeneratorException;

/**
 * @author Legend
 * @data by on 20-9-11.
 * @description 寄存器
 */
public class Register extends Operand {

    private byte address;
    private String name;

    private static Register[] registers = new Register[32];

    public Register(String name, byte address) {
        this.name = name;
        this.address = address;
        registers[address] = this;
    }

    public static final Register ZERO = new Register("ZERO", (byte) 1);
    public static final Register PC = new Register("PC", (byte) 2);
    public static final Register SP = new Register("SP", (byte) 3);
    public static final Register BP = new Register("BP", (byte) 4);
    public static final Register CONSTANT = new Register("CONSTANT", (byte) 5);
    public static final Register RA = new Register("RA", (byte) 7);
    public static final Register RV = new Register("RV", (byte) 8);

    public static final Register R1 = new Register("r1", (byte) 10);
    public static final Register R2 = new Register("r2", (byte) 11);
    public static final Register R3 = new Register("r3", (byte) 12);

    public static final Register LO = new Register("LO", (byte) 20);

    public static Register getRegByIdx(int reg) throws GeneratorException {
        if (reg < 0 || reg >= registers.length) {
            throw new GeneratorException("Not exist Register's address is " + reg);
        }
        if (registers[reg] == null) {
            throw new GeneratorException("Not exist Register's address is " + reg);
        }
        return registers[reg];
    }

    @Override
    public String toString() {
        return name;
    }

    public byte getIdx() {
        return address;
    }

    @Override
    public int getVal() {
        return address;
    }
}
