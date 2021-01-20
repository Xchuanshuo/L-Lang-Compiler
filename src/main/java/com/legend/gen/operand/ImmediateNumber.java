package com.legend.gen.operand;

import com.legend.ir.Constant;

/**
 * @author Legend
 * @data by on 20-9-11.
 * @description 立即数
 */
public class ImmediateNumber extends Operand {

    private int value;

    public ImmediateNumber(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }

    @Override
    public int getVal() {
        return value;
    }
}
