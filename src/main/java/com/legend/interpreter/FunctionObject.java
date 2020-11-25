package com.legend.interpreter;

import com.legend.semantic.Function;
import com.legend.semantic.Variable;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 存放一个函数运行时的本地变量
 */
public class FunctionObject extends LObject {

    // 类型
    protected Function function = null;

    // 函数对象的接收变量, 函数对象赋值给一个新的变量时 对应的scope也要改变
    protected Variable receiver = null;

    public FunctionObject(Function function) {
        this.function = function;
    }

    protected void setFunction(Function function) {
        this.function = function;
    }
}
