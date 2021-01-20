package com.legend.gen.operand;

/**
 * @author Legend
 * @data by on 20-9-11.
 * @description 偏移量
 */
public class Offset extends Operand {

    private int offset;

    public Offset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return offset + "";
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getEncodedOffset() {
        if (offset > 0) {
            return offset;
        }
        return 0x400 | -offset;
    }

    public static Offset decodeOffset(int offset) {
//        if ((offset & 0x400) > 0) {
//            offset = offset & 0x3FF;
//            offset = -offset;
//        }
        return new Offset(offset);
    }

    @Override
    public int getVal() {
        return offset;
    }
}
