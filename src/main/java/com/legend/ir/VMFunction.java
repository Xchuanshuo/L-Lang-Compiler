package com.legend.ir;

import com.legend.semantic.Function;

/**
 * @author Legend
 * @data by on 20-12-20.
 * @description 记录函数的入口相关信息
 */
public class VMFunction {
    private Function function;
    private int entry;

    public VMFunction(Function function, int entry) {
        this.function = function;
        this.entry = entry;
    }

    public VMFunction(Function function) {
        this.function = function;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
