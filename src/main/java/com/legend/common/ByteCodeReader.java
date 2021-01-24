package com.legend.common;

/**
 * @author Legend
 * @data by on 21-1-4.
 * @description 字节码阅读器
 */
public class ByteCodeReader {

    private byte[] codes;
    private int position;

    public ByteCodeReader(byte[] codes) {
        this.codes = codes;
    }

    public void reset(int pc) {
        this.position = pc;
    }

    public void reset(byte[] codes, int pc) {
        this.codes = codes;
        this.position = pc;
    }

    public int pc() {
        return this.position;
    }

    public byte readByte() {
        byte code = codes[position];
        position += 1;
        return code;
    }

    public short readShort(){
        byte byte1 = readByte();
        byte byte2 = readByte();
        int ub1 = byte1 & 0xff;
        int ub2 = byte2 & 0xff;
        return (short) ((ub1 << 8) | ub2);
    }

    public int readInt() {
        int byte1 = readByte();
        int byte2 = readByte();
        int byte3 = readByte();
        int byte4 = readByte();
        return (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
    }

    public int[] readInts(int n) {
        int[] ints = new int[n];
        for (int i = 0; i < n; i++) {
            ints[i] = this.readInt();
        }
        return ints;
    }

    public boolean isEnd() {
        return position >= codes.length;
    }

    public void skipPadding() {
        while (this.position %4 != 0) {
            readByte();
        }
    }
}